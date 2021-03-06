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
package org.kalypso.ogc.gml.outline.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.kalypso.ogc.gml.map.handlers.MapHandlerUtils;
import org.kalypso.ogc.gml.outline.nodes.IThemeNode;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * Opens the property dialog for a {@link org.kalypso.ogc.gml.IKalypsoTheme}.
 * 
 * @author Gernot Belger
 */
public class RemoveNodeHandler extends AbstractHandler
{
  /**
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute( final ExecutionEvent event )
  {
    final IEvaluationContext context = (IEvaluationContext)event.getApplicationContext();
    final Shell shell = (Shell)context.getVariable( ISources.ACTIVE_SHELL_NAME );
    final ISelection selection = (ISelection)context.getVariable( ISources.ACTIVE_CURRENT_SELECTION_NAME );

    final IThemeNode[] selectedNodes = MapHandlerUtils.getSelectedNodes( selection );

    final WorkbenchLabelProvider labelProvider = new WorkbenchLabelProvider();
    try
    {
      final ListSelectionDialog dialog = new ListSelectionDialog( shell, selectedNodes, new ArrayContentProvider(), labelProvider, Messages.getString( "RemoveNodeHandler_0" ) ); //$NON-NLS-1$
      dialog.setInitialSelections( selectedNodes );
      if( !(dialog.open() == Window.OK) )
        return null;

      // TODO: remove styles

      return null;
    }
    finally
    {
      labelProvider.dispose();
    }
  }

}
