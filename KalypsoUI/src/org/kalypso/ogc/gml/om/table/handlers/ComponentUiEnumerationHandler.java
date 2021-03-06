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
package org.kalypso.ogc.gml.om.table.handlers;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.observation.result.IRecord;
import org.kalypso.ogc.gml.om.table.celleditor.ComboBoxViewerCellEditor;

/**
 * Handles enumerated values, i.e. values which have enumeration-restrictions.
 * 
 * @author Dirk Kuch
 * @author Gernot Belger
 */
public class ComponentUiEnumerationHandler extends AbstractComponentUiHandler
{
  protected final Map<Object, IAnnotation> m_items;

  public ComponentUiEnumerationHandler( final int component, final boolean editable, final boolean resizeable, final boolean moveable, final String columnLabel, final String columnTooltip, final int columnStyle, final int columnWidth, final int columnWidthPercent, final String displayFormat, final String nullFormat, final Map<Object, IAnnotation> items )
  {
    super( component, editable, resizeable, moveable, columnLabel, columnTooltip, columnStyle, columnWidth, columnWidthPercent, displayFormat, nullFormat, null );

    m_items = items;
  }

  @Override
  public CellEditor createCellEditor( final Table table )
  {
    final Set<Object> set = m_items.keySet();

    final LabelProvider labelProvider = new LabelProvider()
    {
      /**
       * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
       */
      @Override
      public String getText( final Object element )
      {
        final IAnnotation annotation = m_items.get( element );

        if( annotation != null )
          return annotation.getLabel().trim();

        return super.getText( element );
      }
    };

    return new ComboBoxViewerCellEditor( new ArrayContentProvider(), labelProvider, set, table, SWT.READ_ONLY | SWT.DROP_DOWN );
  }

  @Override
  public Object doGetValue( final IRecord record )
  {
    return record.getValue( getComponent() );
  }

  @Override
  public void doSetValue( final IRecord record, final Object value )
  {
    if( value == null )
      record.setValue( getComponent(), false );
    else
      setValue( record, value );
  }

  @Override
  public String getStringRepresentation( final IRecord record )
  {
    final int componentIndex = getComponent();
    final Object value = record.getValue( componentIndex );

    final IAnnotation annotation = m_items.get( value );
    if( annotation == null )
      return value.toString();

    return annotation.getLabel().trim();
  }

  @Override
  public Object parseValue( final String text )
  {
    for( final Map.Entry<Object, IAnnotation> item : m_items.entrySet() )
    {
      final IAnnotation value = item.getValue();
      if( value != null && value.getLabel().equals( text ) )
        return item.getKey();
    }

    return null;
  }

  @Override
  public void setValue( final IRecord record, final Object value )
  {
    final int index = getComponent();
    final Object oldValue = record.getValue( index );

    if( !ObjectUtils.equals( value, oldValue ) )
      record.setValue( getComponent(), value );
  }
}