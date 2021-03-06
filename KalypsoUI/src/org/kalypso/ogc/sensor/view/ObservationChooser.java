/*--------------- Kalypso-Header -------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 --------------------------------------------------------------------------*/

package org.kalypso.ogc.sensor.view;

import org.eclipse.compare.internal.AbstractViewer;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.repository.IRepository;
import org.kalypso.repository.IRepositoryItem;
import org.kalypso.repository.RepositoryContainerSingelton;
import org.kalypso.repository.container.IRepositoryContainer;
import org.kalypso.ui.repository.view.RepositoryLabelProvider;
import org.kalypso.ui.repository.view.RepositoryTreeContentProvider;

/**
 * A view that allows the user to choose an observation within a tree of repositories
 * 
 * @author schlienger (19.05.2005)
 */
public class ObservationChooser extends AbstractViewer
{
  private final IRepositoryContainer m_repContainer;

  private final TreeViewer m_repViewer;

  public ObservationChooser( final Composite parent )
  {
    m_repContainer = RepositoryContainerSingelton.getInstance().getContainer();

    m_repViewer = new TreeViewer( parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI );
    m_repViewer.setContentProvider( new RepositoryTreeContentProvider() );
    m_repViewer.setLabelProvider( new RepositoryLabelProvider() );
    m_repViewer.setInput( m_repContainer );
    m_repViewer.addDoubleClickListener( new IDoubleClickListener()
    {
      @Override
      public void doubleClick( final DoubleClickEvent event )
      {
        onTreeDoubleClick( event );
      }
    } );
  }

  protected void onTreeDoubleClick( final DoubleClickEvent event )
  {
    final IStructuredSelection selection = (IStructuredSelection)event.getSelection();
    final Object firstElement = selection.getFirstElement();
    if( firstElement != null )
    {
      final boolean expandedState = m_repViewer.getExpandedState( firstElement );
      if( expandedState )
      {
        m_repViewer.collapseToLevel( firstElement, 1 );
      }
      else
      {
        m_repViewer.expandToLevel( firstElement, 1 );
      }
    }
  }

  public IObservation isObservationSelected( final ISelection selection )
  {
    final IStructuredSelection sel = (IStructuredSelection)selection;
    if( sel.isEmpty() )
      return null;

    final Object element = sel.getFirstElement();
    if( element instanceof IAdaptable )
    {
      final IObservation obs = (IObservation)((IAdaptable)element).getAdapter( IObservation.class );

      return obs;
    }

    return null;
  }

  /**
   * @return checks if given selection is a <code>IRepository</code>. Returns a repository or null if no repository is
   *         selected.
   */
  public IRepository isRepository( final ISelection selection )
  {
    final IStructuredSelection sel = (IStructuredSelection)selection;
    if( sel.isEmpty() )
      return null;

    final Object element = sel.getFirstElement();
    if( !(element instanceof IRepository) )
      return null;

    return (IRepository)element;
  }

  public TreeViewer getViewer( )
  {
    return m_repViewer;
  }

  /**
   * @see org.eclipse.jface.viewers.Viewer#getControl()
   */
  @Override
  public Control getControl( )
  {
    return m_repViewer.getControl();
  }

  @Override
  public ISelection getSelection( )
  {
    return m_repViewer.getSelection();
  }

  @Override
  public void setSelection( final ISelection selection )
  {
    m_repViewer.setSelection( selection );
  }

  @Override
  public void addSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_repViewer.addSelectionChangedListener( listener );
  }

  @Override
  public void removeSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_repViewer.removeSelectionChangedListener( listener );
  }

  public IRepositoryContainer getRepositoryContainer( )
  {
    return m_repContainer;
  }

  public Shell getShell( )
  {
    return getControl().getShell();
  }

  public IRepositoryItem isRepositoryItem( )
  {
    final ISelection selection = getSelection();
    if( selection instanceof IStructuredSelection )
    {
      final IStructuredSelection structSel = (IStructuredSelection)selection;
      final Object element = structSel.getFirstElement();
      if( element instanceof IRepositoryItem )
        return (IRepositoryItem)element;
    }

    return null;
  }
}
