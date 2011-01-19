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
package org.kalypso.chart.ui.editor;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.UIElement;
import org.kalypso.chart.ui.IChartPart;

import de.openali.odysseus.chart.framework.view.IChartDragHandler;
import de.openali.odysseus.chart.framework.view.IPlotHandler;

/**
 * @author burtscher1
 */
public class ElementUpdateHelper
{
  /**
   * function is called by all radio commands from updateElement (IElementUpdater) in order to synchronize toggle state
   * with chart handler state
   */
  public static void updateElement( final UIElement element, final Map< ? , ? > parameters, final Class< ? > handlerClass )
  {
    // TODO: we get NPEs here

    // chart finden,
    // FIXME: get chart from context
    final IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart();
    if( activePart == null )
      return;

    final IChartPart chartPart = (IChartPart) activePart.getAdapter( IChartPart.class );
    if( chartPart == null )
      return;

    final IPlotHandler plotDragHandler = chartPart.getPlotDragHandler();
    if( plotDragHandler != null )
    {
      final IChartDragHandler[] handlers = plotDragHandler.getActiveHandlers();
      element.setChecked( isChecked( handlers, handlerClass ) );
    }
    else
    {
      element.setChecked( false );
    }

  }

  private static boolean isChecked( final IChartDragHandler[] handlers, final Class< ? > clazz )
  {
    if( ArrayUtils.isEmpty( handlers ) )
      return false;

    for( final IChartDragHandler handler : handlers )
    {
      if( handler.getClass().equals( clazz ) )
        return true;
    }

    return false;
  }

}
