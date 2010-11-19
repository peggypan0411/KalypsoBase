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
package org.kalypso.zml.ui.table;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.kalypso.contribs.eclipse.swt.layout.LayoutHelper;
import org.kalypso.zml.ui.table.provider.IZmlTableComposite;
import org.kalypso.zml.ui.table.provider.ZmlColumnRegistry;
import org.kalypso.zml.ui.table.provider.ZmlLabelProvider;
import org.kalypso.zml.ui.table.provider.ZmlTableColumn;
import org.kalypso.zml.ui.table.provider.ZmlTableContentProvider;
import org.kalypso.zml.ui.table.schema.AbstractColumnType;
import org.kalypso.zml.ui.table.schema.DataColumnType;
import org.kalypso.zml.ui.table.schema.ZmlTableType;
import org.kalypso.zml.ui.table.utils.ZmlTableHelper;

/**
 * @author Dirk Kuch
 */
public class ZmlTableComposite extends Composite implements IZmlTableComposite
{
  private TableViewer m_tableViewer;

  private ZmlColumnRegistry m_registry;

  private final Map<Integer, AbstractColumnType> m_columnIndex = new HashMap<Integer, AbstractColumnType>();

  public ZmlTableComposite( final Composite parent, final ZmlTableType tableType )
  {
    super( parent, SWT.NULL );

    setLayout( LayoutHelper.createGridLayout() );

    if( tableType != null )
      setup( tableType );
  }

  private void setup( final ZmlTableType tableType )
  {
    m_registry = new ZmlColumnRegistry( this, tableType );
    m_tableViewer = new TableViewer( this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION );
    m_tableViewer.setContentProvider( new ZmlTableContentProvider( tableType ) );

    final List<AbstractColumnType> columns = tableType.getColumns().getColumn();
    for( final AbstractColumnType column : columns )
    {
      buildColumnViewer( column );
    }

    m_tableViewer.setInput( m_registry );

    /** layout stuff */
    final Table table = m_tableViewer.getTable();
    table.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
    table.setHeaderVisible( true );

  }

  private TableViewerColumn buildColumnViewer( final AbstractColumnType type )
  {
    final int index = m_tableViewer.getTable().getColumnCount();
    m_columnIndex.put( index, type );

    final TableViewerColumn column = new TableViewerColumn( m_tableViewer, ZmlTableHelper.toSWT( type.getAlignment() ) );
    column.setLabelProvider( new ZmlLabelProvider( type ) );
    column.getColumn().setText( type.getLabel() );

    final BigInteger width = type.getWidth();
    if( width != null )
      column.getColumn().setWidth( width.intValue() );

    if( width == null && type.isAutopack() )
      column.getColumn().pack();

    /** edit support */
    if( type instanceof DataColumnType && type.isEditable() )
    {
      column.setEditingSupport( new ZmlEditingSupport( (DataColumnType) type, column ) );
    }

    return column;
  }

  public void clean( )
  {
    m_registry.clean();
  }

  public void addColumn( final IZmlTableColumn column )
  {
    m_registry.loadColumn( column );
  }

  @Override
  public void refresh( )
  {
    m_tableViewer.refresh();

    /** update header labels */
    final TableColumn[] tableColumns = m_tableViewer.getTable().getColumns();
    Assert.isTrue( tableColumns.length == m_columnIndex.size() );

    for( int i = 0; i < tableColumns.length; i++ )
    {
      final AbstractColumnType columnType = m_columnIndex.get( i );
      final TableColumn tableColumn = tableColumns[i];

      /** only update headers of data column types */
      if( columnType instanceof DataColumnType )
      {
        final ZmlTableColumn column = m_registry.getColumn( columnType.getId() );
        tableColumn.setText( column.getLabel() );
      }

      if( columnType.isAutopack() && columnType.getWidth() == null )
        tableColumn.pack();
    }
  }
}
