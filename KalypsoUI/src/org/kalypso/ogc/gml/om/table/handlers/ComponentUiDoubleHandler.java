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

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.kalypso.contribs.java.lang.NumberUtils;
import org.kalypso.observation.result.IRecord;
import org.kalypso.ogc.gml.table.celleditors.DefaultCellValidators;

/**
 * Handles double values.
 * 
 * @author Dirk Kuch
 * @author Gernot Belger
 */
public class ComponentUiDoubleHandler extends AbstractComponentUiHandler
{
  public ComponentUiDoubleHandler( final int component, final boolean editable, final boolean resizeable, final boolean moveable, final String columnLabel, final String columnTooltip, final int columnStyle, final int columnWidth, final int columnWidthPercent, final String displayFormat, final String nullFormat, final String parseFormat )
  {
    super( component, editable, resizeable, moveable, columnLabel, columnTooltip, columnStyle, columnWidth, columnWidthPercent, displayFormat, nullFormat, parseFormat );
  }

  @Override
  public CellEditor createCellEditor( final Table table )
  {
    final TextCellEditor textCellEditor = new TextCellEditor( table, SWT.NONE );
    textCellEditor.setValidator( DefaultCellValidators.DOUBLE_VALIDATOR );
    return textCellEditor;
  }

  @Override
  public Object doGetValue( final IRecord record )
  {
    final Object value = record.getValue( getComponent() );
    if( value == null )
      return ""; //$NON-NLS-1$

    // IMPORTANT: do not use 'getStringRepresentation'; we need to see internal digits here (else just clicking the cell might change the value)
    return String.format( "%f", value ); //$NON-NLS-1$
  }

  @Override
  public void doSetValue( final IRecord record, final Object value )
  {
    if( value == null || value.toString().trim().length() == 0 )
      setValue( record, null );
    else
      setValue( record, parseValue( value.toString() ) );
  }

  @Override
  public Object parseValue( final String text )
  {
    return NumberUtils.parseDouble( text );
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