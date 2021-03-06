/*--------------- Kalypso-Header ----------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 University of Technology Hamburg-Harburg (TUHH)
 Institute of River and Coastal Engineering
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
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact:

 E-Mail:
 g.belger@bjoernsen.de
 m.schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ------------------------------------------------------------------------*/
package org.kalypso.contribs.eclipse.jface.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Default implementation of {@link org.eclipse.jface.viewers.IPostSelectionProvider}.<br>
 * Implements support for the listeners and the getter / setter. Subclasses may re-implement the getter/setter.<br>
 *
 * @author Gernot Belger
 */
public class SelectionProviderAdapter implements IPostSelectionProvider
{
  private final List<ISelectionChangedListener> m_listeners = new ArrayList<>();

  private final List<ISelectionChangedListener> m_postListeners = new ArrayList<>();

  private ISelection m_selection = StructuredSelection.EMPTY;

  @Override
  public final void addSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_listeners.add( listener );
  }

  @Override
  public final void removeSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_listeners.remove( listener );
  }

  @Override
  public void setSelection( final ISelection selection )
  {
    if( selection == null )
      m_selection = StructuredSelection.EMPTY;
    else
      m_selection = selection;

    fireSelectionChanged();
  }

  @Override
  public ISelection getSelection( )
  {
    return m_selection;
  }

  @Override
  public void addPostSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_postListeners.add( listener );
  }

  @Override
  public void removePostSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_postListeners.remove( listener );
  }

  public final void fireSelectionChanged( )
  {
    final ISelectionChangedListener[] listenersArray = m_listeners.toArray( new ISelectionChangedListener[m_listeners.size()] );

    fireSelectionChanged( new SelectionChangedEvent( this, getSelection() ), listenersArray );
  }

  public final void firePostSelectionChanged( )
  {
    final ISelectionChangedListener[] listenersArray = m_postListeners.toArray( new ISelectionChangedListener[m_postListeners.size()] );

    fireSelectionChanged( new SelectionChangedEvent( this, getSelection() ), listenersArray );
  }

  private void fireSelectionChanged( final SelectionChangedEvent event, final ISelectionChangedListener[] listeners )
  {
    for( final ISelectionChangedListener listener : listeners )
    {
      final SafeRunnable safeRunnable = new SafeRunnable()
      {
        @Override
        public void run( )
        {
          listener.selectionChanged( event );
        }
      };

      SafeRunnable.run( safeRunnable );
    }
  }
}