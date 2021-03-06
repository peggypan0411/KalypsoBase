/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestraße 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 * 
 *  and
 *  
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Contact:
 * 
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *   
 *  ---------------------------------------------------------------------------*/
package org.kalypso.ui.editor.styleeditor.graphic;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.commons.eclipse.jface.viewers.AbstractManagedTabViewer;
import org.kalypso.commons.eclipse.jface.viewers.TabItemMoveBackwardsAction;
import org.kalypso.commons.eclipse.jface.viewers.TabItemMoveForwardAction;
import org.kalypso.commons.eclipse.jface.viewers.TabViewer;
import org.kalypso.ui.ImageProvider;
import org.kalypso.ui.editor.styleeditor.MessageBundle;
import org.kalypso.ui.editor.styleeditor.binding.IStyleInput;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.graphics.sld.Graphic;

/**
 * @author Gernot Belger
 */
public class GraphicElementsTabViewer extends AbstractManagedTabViewer<Graphic>
{
  public GraphicElementsTabViewer( final FormToolkit toolkit, final Composite parent, final IStyleInput<Graphic> input )
  {
    super( toolkit, parent, new GraphicElementsTabList( input ), canChange() );
  }

  private static boolean canChange( )
  {
    // return input.getConfig().isRuleTabViewerAllowChange();
    return true;
  }

  @Override
  protected void createTabActions( final ToolBarManager manager )
  {
    final TabViewer tabViewer = getViewer();

    final TabItemMoveBackwardsAction moveBackwardsAction = new TabItemMoveBackwardsAction( tabViewer );
    moveBackwardsAction.setImageDescriptor( ImageProvider.IMAGE_STYLEEDITOR_BACKWARD );
    moveBackwardsAction.setToolTipText( MessageBundle.STYLE_EDITOR_FORWARD );

    final TabItemMoveForwardAction moveForwardAction = new TabItemMoveForwardAction( tabViewer );
    moveForwardAction.setImageDescriptor( ImageProvider.IMAGE_STYLEEDITOR_FORWARD );
    moveForwardAction.setToolTipText( MessageBundle.STYLE_EDITOR_FORWARD );

    manager.add( new AddMarkItemAction( tabViewer ) );
    manager.add( new AddExternalGraphicItemAction( tabViewer ) );
    manager.add( moveBackwardsAction );
    manager.add( moveForwardAction );
  }

  /**
   * @see org.kalypso.ui.editor.styleeditor.forms.AbstractTabViewerPart#getRemoveMessage(java.lang.String)
   */
  @Override
  protected String getRemoveMessage( final String tabName )
  {
    return String.format( Messages.getString( "GraphicElementsTabViewer_0" ), tabName ); //$NON-NLS-1$
  }
}
