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
package org.kalypso.contribs.eclipse.jface.viewers.table;

import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Layout info how the width of the column should be calculated.
 * 
 * @author Gernot Belger
 */
public class ColumnWidthInfo
{
  public static final int NOT_SET = -1;

  public static final int PACK = -2;

  private final Item m_column;

  private int m_minimumWidth = NOT_SET;

  private int m_calculatedMinimumWidth;

  /* The width that will be finally set to the column */
  private int m_columnWidth;

  private boolean m_autoResize;

  public ColumnWidthInfo( final Item column )
  {
    m_column = column;
  }

  public void setMinimumWidth( final int minimumWidth )
  {
    m_minimumWidth = minimumWidth;
  }

  public int getMinimumWidth( )
  {
    return m_minimumWidth;
  }

  void calculateMinimumWidth( )
  {
    m_calculatedMinimumWidth = doCalculateMinimumWidth();
  }

  private int doCalculateMinimumWidth( )
  {
    if( m_minimumWidth == NOT_SET )
      return getWidth( m_column );

    if( m_minimumWidth >= 0 )
      return m_minimumWidth;

    return calculatePack( m_column );
  }

  private static int getWidth( final Item column )
  {
    if( column instanceof TableColumn )
      return ((TableColumn)column).getWidth();

    if( column instanceof TreeColumn )
      return ((TreeColumn)column).getWidth();

    throw new IllegalArgumentException();
  }

  private int calculatePack( final Item column )
  {
    if( column instanceof TableColumn )
    {
      final TableColumn tableColumn = (TableColumn)column;

      // TODO: very slow for large tables... would be nice to guess the width from the first 100 rows or so.
      // final Table parent = tableColumn.getParent();
      // if( parent.getItemCount() > 1000 )
      // {
      // int width = 0;
      // for( int i = 0; i < 100; i++ )
      // {
      // final TableItem item = parent.getItem( 100 );
      // final Rectangle textBounds = item.getTextBounds( i );
      // width = Math.max( width, textBounds.width );
      // }
      //
      // return width;
      // }

      tableColumn.pack();
      return tableColumn.getWidth();
    }

    if( column instanceof TreeColumn )
    {
      ((TreeColumn)column).pack();
      return ((TreeColumn)column).getWidth();
    }

    throw new IllegalArgumentException();
  }

  void updateItemWidth( )
  {
    if( m_column instanceof TableColumn )
      ((TableColumn)m_column).setWidth( m_columnWidth );
    else if( m_column instanceof TreeColumn )
      ((TreeColumn)m_column).setWidth( m_columnWidth );
    else
      throw new IllegalArgumentException();
  }

  void setColumnWidth( final int columnWidth )
  {
    m_columnWidth = columnWidth;
  }

  Item getColumn( )
  {
    return m_column;
  }

  int getCalculatedMinimumWidth( )
  {
    return m_calculatedMinimumWidth;
  }

  public int getColumnWidth( )
  {
    return m_columnWidth;
  }

  public boolean isAutoResize( )
  {
    return m_autoResize;
  }

  public void setAutoResize( final boolean autoResize )
  {
    m_autoResize = autoResize;
  }
}