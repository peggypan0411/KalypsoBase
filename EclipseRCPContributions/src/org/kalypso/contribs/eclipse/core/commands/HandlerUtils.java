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
package org.kalypso.contribs.eclipse.core.commands;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Utility class for {@link org.eclipse.core.commands.IHandler} related stuff.
 * 
 * @author Gernot Belger
 */
public final class HandlerUtils
{
  private HandlerUtils( )
  {
    throw new UnsupportedOperationException( "Helper class, do not instantiate" ); //$NON-NLS-1$
  }

  public static String getCommandName( final ExecutionEvent event )
  {
    try
    {
      final Command command = event.getCommand();
      return command.getName();
    }
    catch( final NotDefinedException e )
    {
      e.printStackTrace();
      return "<Command name not set>"; //$NON-NLS-1$
    }
  }

  public static boolean isSelected( final ExecutionEvent executionEvent )
  {
    return !isDeselected( executionEvent );
  }

  /**
   * Checks if this command was executed as de-selection of a radio button/menu.<br>
   * If this is the case, we just ignore it.<br>
   * In doubt, we always execute.
   */
  public static boolean isDeselected( final ExecutionEvent executionEvent )
  {
    final Object trigger = executionEvent.getTrigger();
    if( !(trigger instanceof Event) )
      return false;

    final Event event = (Event) trigger;
    final Widget widget = event.widget;
    if( widget instanceof ToolItem )
    {
      final ToolItem item = (ToolItem) widget;
      return !item.getSelection();
    }
    if( widget instanceof MenuItem )
    {
      final MenuItem item = (MenuItem) widget;
      return !item.getSelection();
    }

    return false;
  }

}
