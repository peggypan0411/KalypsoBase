/*--------------- Kalypso-Header --------------------------------------------------------------------

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

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ui.repository.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.kalypso.contribs.eclipse.ui.MementoUtils;
import org.kalypso.ogc.sensor.view.ObservationChooser;
import org.kalypso.ogc.sensor.view.propertySource.ObservationPropertySourceProvider;
import org.kalypso.repository.IRepository;
import org.kalypso.repository.IRepositoryItem;
import org.kalypso.repository.conf.RepositoryFactoryConfig;

/**
 * Wird als ZeitreihenBrowser benutzt.
 * 
 * @author schlienger
 */
public class RepositoryExplorerPart extends ViewPart implements ISelectionProvider, ISelectionChangedListener
{
  private PropertySheetPage m_propsPage = null;

  private IMemento m_memento = null;

  // persistence flags for memento
  private static final String TAG_REPOSITORY = "repository"; //$NON-NLS-1$

  private static final String TAG_REPOSITORY_PROPS = "repositoryProperties"; //$NON-NLS-1$

  private static final String TAG_EXPANDED = "expanded"; //$NON-NLS-1$

  private static final String TAG_ELEMENT = "element"; //$NON-NLS-1$

  private static final String TAG_IDENFITIER = "identifier"; //$NON-NLS-1$

  private static final String TAG_REPOSITORIES = "repositories"; //$NON-NLS-1$

  public static final String ID = "org.kalypso.ui.repository.view.RepositoryExplorerPart"; //$NON-NLS-1$

  private ObservationChooser m_chooser;

  @Override
  public Object getAdapter( final Class adapter )
  {
    if( adapter.equals( IPropertySheetPage.class ) )
    {
      // lazy loading
      if( m_propsPage == null || m_propsPage.getControl().isDisposed() )
      {
        // dispose it when not null (not sure if this is ok)
        if( m_propsPage != null )
          m_propsPage.dispose();

        // PropertySheetPage erzeugen. Sie wird in das standard PropertySheet
        // von Eclipse dargestellt
        m_propsPage = new PropertySheetPage();

        // eigenes entry mit source provider
        final PropertySheetEntry entry = new PropertySheetEntry();
        entry.setPropertySourceProvider( new ObservationPropertySourceProvider() );

        m_propsPage.setRootEntry( entry );

        m_propsPage.selectionChanged( this, getSelection() );
      }

      return m_propsPage;
    }

    if( adapter.equals( ObservationChooser.class ) )
      return m_chooser;

    return super.getAdapter( adapter );
  }

  @Override
  public void dispose( )
  {
    removeSelectionChangedListener( this );

    super.dispose();
  }

  @Override
  public void createPartControl( final Composite parent )
  {
    m_chooser = new ObservationChooser( parent );

    addSelectionChangedListener( this );

    getSite().setSelectionProvider( m_chooser );

    final MenuManager menuMgr = new MenuManager();
    final Menu menu = menuMgr.createContextMenu( m_chooser.getViewer().getTree() );
    m_chooser.getViewer().getTree().setMenu( menu );

    getSite().registerContextMenu( /* "org.kalypso.ogc.sensor.view.observationBrowser", */menuMgr, m_chooser );

    if( m_memento != null )
      restoreState( m_memento );
    m_memento = null;
  }

  @Override
  public void init( final IViewSite site, final IMemento memento ) throws PartInitException
  {
    super.init( site, memento );

    m_memento = memento;
  }

  @Override
  public void setFocus( )
  {
    // noch nix
  }

  @Override
  public void selectionChanged( final SelectionChangedEvent event )
  {
    if( m_propsPage != null && !m_propsPage.getControl().isDisposed() )
      m_propsPage.selectionChanged( this, event.getSelection() );
  }

  @Override
  public void saveState( final IMemento memento )
  {
    if( m_chooser == null )
      return;

    final TreeViewer viewer = m_chooser.getViewer();
    if( viewer == null )
    {
      if( m_memento != null ) // Keep the old state;
        memento.putMemento( m_memento );

      return;
    }

    // save list of repositories
    final IMemento repsMem = memento.createChild( RepositoryExplorerPart.TAG_REPOSITORIES );
    final IRepository[] repositories = m_chooser.getRepositoryContainer().getRepositories();
    for( final IRepository repository : repositories )
    {
      final IMemento child = repsMem.createChild( RepositoryExplorerPart.TAG_REPOSITORY );
      child.putTextData( new RepositoryFactoryConfig( repository ).saveState() );

      // save properties for that repository
      final IMemento propsMem = child.createChild( RepositoryExplorerPart.TAG_REPOSITORY_PROPS );
      try
      {
        MementoUtils.saveProperties( propsMem, repository.getProperties() );
      }
      catch( final IOException e )
      {
        e.printStackTrace();
      }
    }

    // save visible expanded elements
    final Object expandedElements[] = viewer.getVisibleExpandedElements();
    if( expandedElements.length > 0 )
    {
      final IMemento expandedMem = memento.createChild( RepositoryExplorerPart.TAG_EXPANDED );
      for( final Object element : expandedElements )
        if( element instanceof IRepositoryItem )
        {
          final IMemento elementMem = expandedMem.createChild( RepositoryExplorerPart.TAG_ELEMENT );
          final String id = ((IRepositoryItem)element).getIdentifier();
          elementMem.putString( RepositoryExplorerPart.TAG_IDENFITIER, id );
        }
    }
  }

  /**
   * Restores the state of the receiver to the state described in the specified memento.
   * 
   * @param memento
   *          the memento
   */
  private void restoreState( final IMemento memento )
  {
    if( m_chooser == null )
      return;

    final TreeViewer viewer = m_chooser.getViewer();

    final IMemento repsMem = memento.getChild( RepositoryExplorerPart.TAG_REPOSITORIES );
    if( repsMem != null )
    {
      final IMemento[] repMem = repsMem.getChildren( RepositoryExplorerPart.TAG_REPOSITORY );
      for( final IMemento element : repMem )
      {
        if( element == null )
          continue;

        try
        {
          final RepositoryFactoryConfig item = RepositoryFactoryConfig.restore( element.getTextData() );
          if( item != null )
          {
            // TODO: dirty! always use extension mechanism to instantiate repositories
            final IRepository rep = item.getFactory().createRepository();

            final IMemento propsMem = element.getChild( RepositoryExplorerPart.TAG_REPOSITORY_PROPS );
            if( propsMem != null )
              MementoUtils.loadProperties( propsMem, rep.getProperties() );

            m_chooser.getRepositoryContainer().addRepository( rep );
          }
        }
        catch( final Exception e ) // generic exception caught for simplicity
        {
          // ignored
          e.printStackTrace();
        }
      }
    }

    final IMemento childMem = memento.getChild( RepositoryExplorerPart.TAG_EXPANDED );
    if( childMem != null )
    {
      final List<IRepositoryItem> elements = new ArrayList<>();
      viewer.setExpandedElements( elements.toArray() );
    }
  }

  @Override
  public ISelection getSelection( )
  {
    return m_chooser.getSelection();
  }

  @Override
  public void setSelection( final ISelection selection )
  {
    m_chooser.setSelection( selection );
  }

  @Override
  public void addSelectionChangedListener( final ISelectionChangedListener listener )
  {
    if( m_chooser != null )
      m_chooser.addSelectionChangedListener( listener );
  }

  @Override
  public void removeSelectionChangedListener( final ISelectionChangedListener listener )
  {
    if( m_chooser != null )
      m_chooser.removeSelectionChangedListener( listener );
  }
}