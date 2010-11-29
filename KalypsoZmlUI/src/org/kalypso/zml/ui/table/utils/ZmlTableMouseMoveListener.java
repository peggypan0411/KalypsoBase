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
package org.kalypso.zml.ui.table.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.kalypso.zml.ui.table.ZmlTableComposite;
import org.kalypso.zml.ui.table.binding.BaseColumn;
import org.kalypso.zml.ui.table.model.IZmlModelRow;
import org.kalypso.zml.ui.table.model.ZmlModelRow;
import org.kalypso.zml.ui.table.model.references.IZmlValueReference;

/**
 * @author Dirk Kuch
 */
public class ZmlTableMouseMoveListener implements MouseMoveListener
{
  Point m_position;

  private final ZmlTableComposite m_table;

  public ZmlTableMouseMoveListener( final ZmlTableComposite table )
  {
    m_table = table;
  }

  /**
   * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
   */
  @Override
  public void mouseMove( final MouseEvent e )
  {
    m_position = new Point( e.x, e.y );
  }

  private ViewerCell getActiveCell( )
  {
    final ViewerCell cell = m_table.getTableViewer().getCell( m_position );

    return cell;
  }

  public BaseColumn findActiveColumn( )
  {
    final ViewerCell cell = getActiveCell();
    if( cell == null )
      return null;

    final BaseColumn column = m_table.getColumn( cell.getColumnIndex() );

    return column;
  }

  public IZmlModelRow findActiveRow( )
  {
    final ViewerCell cell = getActiveCell();
    if( cell == null )
      return null;

    final Object element = cell.getElement();
    if( element instanceof ZmlModelRow )
      return (ZmlModelRow) element;

    return null;
  }

  public IZmlValueReference findActiveCell( )
  {
    final BaseColumn column = findActiveColumn();
    final IZmlModelRow row = findActiveRow();
    if( column == null || row == null )
      return null;

    final IZmlValueReference reference = row.get( column.getType() );

    return reference;
  }

  public IZmlModelRow[] findSelectedRows( )
  {
    final TableViewer viewer = m_table.getTableViewer();
    final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

    final List<IZmlModelRow> rows = new ArrayList<IZmlModelRow>();

    final Object[] elements = selection.toArray();
    for( final Object element : elements )
    {
      if( element instanceof IZmlModelRow )
        rows.add( (IZmlModelRow) element );
    }

    return rows.toArray( new IZmlModelRow[] {} );
  }
}
