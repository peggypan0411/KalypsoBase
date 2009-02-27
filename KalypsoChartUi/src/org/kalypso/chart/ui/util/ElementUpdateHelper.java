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
package org.kalypso.chart.ui.util;

import java.util.Map;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.UIElement;
import org.kalypso.chart.framework.view.IChartDragHandler;
import org.kalypso.chart.ui.IChartPart;

/**
 * @author burtscher1
 * 
 */
public class ElementUpdateHelper
{

  /**
   * function is called by all radio commands from updateElement (IElementUpdater) in order to synchronize toggle state
   * with chart handler state
   */
  @SuppressWarnings("unused")
  public static void updateElement( final UIElement element, final Map< ? , ? > parameters, final Class< ? > handlerClass )
  {
    // chart finden,
    final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if( window == null )
      return;

    // FIXME: this is not always the right chart! Must be found via the current context instead

    final IWorkbenchPart activePart = window.getPartService().getActivePart();
    final IChartPart chartPart = (IChartPart) activePart.getAdapter( IChartPart.class );
    if( chartPart != null )
    {
      final IChartDragHandler activeHandler = chartPart.getPlotDragHandler().getActiveHandler();
      if( activeHandler != null && activeHandler.getClass().equals( handlerClass ) )
      {
        element.setChecked( true );
      }
      else
      {
        element.setChecked( false );
      }
    }

  }

}
