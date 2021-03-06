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
package org.kalypso.ui.editor.obstableeditor.actions;

import java.util.TimeZone;

import org.kalypso.commons.command.ICommand;
import org.kalypso.ogc.sensor.tableview.TableView;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * @author schlienger
 */
public class ChangeTablePropsCommand implements ICommand
{
  private final TableView m_table;

  private final String m_timezoneName;

  private final String m_orgTimezoneName;

  public ChangeTablePropsCommand( final TableView table, final String timezoneName )
  {
    final TimeZone timezone = table.getTimezone();

    m_orgTimezoneName = timezone == null ? null : timezone.getID();

    m_table = table;

    m_timezoneName = timezoneName;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#isUndoable()
   */
  @Override
  public boolean isUndoable( )
  {
    return true;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#process()
   */
  @Override
  public void process( ) throws Exception
  {
    final TimeZone timeZone = m_timezoneName == null ? null : TimeZone.getTimeZone( m_timezoneName );
    m_table.setTimezone( timeZone );
  }

  /**
   * @see org.kalypso.commons.command.ICommand#redo()
   */
  @Override
  public void redo( ) throws Exception
  {
    process();
  }

  /**
   * @see org.kalypso.commons.command.ICommand#undo()
   */
  @Override
  public void undo( ) throws Exception
  {
    if( m_orgTimezoneName == null )
      m_table.setTimezone( null );
    else
      m_table.setTimezone( TimeZone.getTimeZone( m_orgTimezoneName ) );
  }

  /**
   * @see org.kalypso.commons.command.ICommand#getDescription()
   */
  @Override
  public String getDescription( )
  {
    return Messages.getString( "org.kalypso.ui.editor.obstableeditor.actions.ChangeTablePropsCommand.0" ); //$NON-NLS-1$
  }
}
