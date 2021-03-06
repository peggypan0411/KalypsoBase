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
package org.kalypso.ui.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.kalypso.ui.editor.mapeditor.WidgetActionPart;
import org.kalypsodeegree.model.feature.event.ModellEvent;
import org.kalypsodeegree.model.feature.event.ModellEventListener;
import org.kalypsodeegree.model.feature.event.ModellEventProvider;

/**
 * @author belger
 */
public abstract class AbstractGisEditorActionDelegate implements IEditorActionDelegate, IViewActionDelegate, IActionDelegate2, ModellEventListener
{
  private WidgetActionPart m_part;

  private IAction m_action;

  private ISelection m_selection;

  /**
   * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
   */
  @Override
  public void setActiveEditor( final IAction action, final IEditorPart targetEditor )
  {
    setActivePart( action, targetEditor );
  }

  /**
   * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
   */
  @Override
  public void init( final IViewPart view )
  {
    setActivePart( null, view );
  }

  protected void setActivePart( final IAction action, final IWorkbenchPart part )
  {
    // remember active action
    // needed, because we want to rehfresh the action after modell events
    m_action = action;

    // disconnect eventlistener from old model
    if( m_part != null )
    {
      final ModellEventProvider mep = m_part.getModellEventProvider();
      if( mep != null )
        mep.removeModellListener( this );
    }

    m_part = new WidgetActionPart( part );

    // connect eventlistener from new model
    if( m_part != null )
    {
      final ModellEventProvider mep = m_part.getModellEventProvider();
      if( mep != null )
        mep.addModellListener( this );
    }

    // update action state
    if( action != null )
    {
      refreshAction( action, m_selection );
    }
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void selectionChanged( final IAction action, final ISelection selection )
  {
    m_selection = selection;
    if( action != null )
    {
      refreshAction( action, selection );
    }
  }

  protected WidgetActionPart getPart( )
  {
    return m_part;
  }

  protected ISelection getSelection( )
  {
    return m_selection;
  }

  protected IAction getAction( )
  {
    return m_action;
  }

  /**
   * @see org.kalypsodeegree.model.feature.event.ModellEventListener#onModellChange(org.kalypsodeegree.model.feature.event.ModellEvent)
   */
  @Override
  public void onModellChange( final ModellEvent modellEvent )
  {
    if( m_action != null )
    {
      refreshAction( m_action, m_selection );
    }
  }

  /**
   * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
   */
  @Override
  public void init( final IAction action )
  {
    m_action = action;
    refreshAction( m_action, m_selection );
  }

  /**
   * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
   */
  @Override
  public void runWithEvent( final IAction action, final Event event )
  {
    run( action );
  }

  /**
   * @see org.eclipse.ui.IActionDelegate2#dispose()
   */
  @Override
  public void dispose( )
  {
    m_part = null;
    m_action = null;
    m_selection = null;
  }

  /**
   * implement here: <br>
   * 1. validate action constraints <br>
   * 2. update action status
   * 
   * @param action
   */
  protected abstract void refreshAction( final IAction action, final ISelection selection );

}