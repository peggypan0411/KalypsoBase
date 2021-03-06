/*
 * --------------- Kalypso-Header --------------------------------------------------------------------
 *
 * This file is part of kalypso. Copyright (C) 2004, 2005 by:
 *
 * Technical University Hamburg-Harburg (TUHH) Institute of River and coastal engineering Denickestr. 22 21073 Hamburg,
 * Germany http://www.tuhh.de/wb
 *
 * and
 *
 * Bjoernsen Consulting Engineers (BCE) Maria Trost 3 56070 Koblenz, Germany http://www.bjoernsen.de
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Contact:
 *
 * E-Mail: belger@bjoernsen.de schlienger@bjoernsen.de v.doemming@tuhh.de
 *
 * ---------------------------------------------------------------------------------------------------
 */
package org.kalypso.commons.diff;

import java.util.Stack;
import java.util.logging.Level;

import org.kalypso.contribs.java.util.logging.ILogger;

/**
 * @author doemming
 */
public class DiffLogger implements IDiffLogger
{
  private final ILogger m_logger;

  private final Stack<StringBuffer> m_buffers = new Stack<>();

  public DiffLogger( final ILogger logger )
  {
    m_logger = logger;
  }

  /**
   * @see org.kalypso.commons.diff.IDiffLogger#log(int, java.lang.String)
   */
  @Override
  public void log( final int status, final String message )
  {
    final String diff;
    switch( status )
    {
      case IDiffComparator.DIFF_CONTENT:
        diff = "<-> "; //$NON-NLS-1$
        break;
      case IDiffComparator.DIFF_REMOVED:
        diff = "--- "; //$NON-NLS-1$
        break;
      case IDiffComparator.DIFF_ADDED:
        diff = "+++ "; //$NON-NLS-1$
        break;
      case IDiffComparator.DIFF_UNCOMPAREABLE:
        diff = "??? "; //$NON-NLS-1$
        break;
      // case IDiffComparator.DIFF_INFO:
      case IDiffComparator.DIFF_OK:
        return;
        // case IDiffComparator.DIFF_OK:
        // diff = "=== ";
        // break;
      case IDiffComparator.DIFF_INFO:
        diff = "# "; //$NON-NLS-1$
        break;
      default:
        diff = "unknown "; //$NON-NLS-1$
        break;
    }
    final int offset = m_buffers.size();
    final StringBuffer tab = new StringBuffer();
    for( int i = 0; i < offset; i++ )
      tab.append( " " ); //$NON-NLS-1$
    innerLog( Level.INFO, tab.toString() + diff + message );
  }

  public void innerLog( final Level level, final String message )
  {
    if( m_buffers.isEmpty() )
      m_logger.log( level, -1, message );
    else
    {
      final StringBuffer buffer = m_buffers.peek();
      if( buffer.length() > 0 )
        buffer.append( "\n" ); //$NON-NLS-1$
      buffer.append( message );
    }
  }

  /**
   * @see org.kalypso.commons.diff.IDiffLogger#block()
   */
  @Override
  public void block( )
  {
    final StringBuffer stringBuffer = new StringBuffer();
    m_buffers.push( stringBuffer );
  }

  /**
   * @see org.kalypso.commons.diff.IDiffLogger#unblock(boolean)
   */
  @Override
  public void unblock( final boolean keepLastLog )
  {
    final StringBuffer buffer = m_buffers.pop();
    if( keepLastLog )
      innerLog( Level.INFO, buffer.toString() );
  }
}