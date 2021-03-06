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
import org.eclipse.swt.graphics.Image;
import org.kalypso.observation.result.IRecord;

/**
 * Default implementation of {@link IComponentUiHandler}, implements most of the default behaviour.
 * 
 * @author Dirk Kuch
 * @author Gernot Belger
 */
public abstract class AbstractComponentUiHandler implements IComponentUiHandler
{
  private final int m_component;

  private final boolean m_editable;

  private final int m_columnStyle;

  private final int m_columnWidth;

  private final String m_displayFormat;

  private final String m_nullFormat;

  private final String m_parseFormat;

  private final String m_columnLabel;

  private final boolean m_resizeable;

  private final int m_columnWidthPercent;

  private final boolean m_moveable;

  private final String m_columnTooltip;

  public AbstractComponentUiHandler( final int component, final boolean editable, final boolean resizeable, final boolean moveable, final String columnLabel, final String columnTooltip, final int columnStyle, final int columnWidth, final int columnWidthPercent, final String displayFormat, final String nullFormat, final String parseFormat )
  {
    m_component = component;
    m_editable = editable;
    m_resizeable = resizeable;
    m_moveable = moveable;
    m_columnLabel = columnLabel;
    m_columnTooltip = columnTooltip;
    m_columnStyle = columnStyle;
    m_columnWidth = columnWidth;
    m_columnWidthPercent = columnWidthPercent;
    m_displayFormat = displayFormat;
    m_nullFormat = nullFormat;
    m_parseFormat = parseFormat;
  }

  @Override
  public String getIdentity( )
  {
    return ObjectUtils.identityToString( this );
  }

  protected int getComponent( )
  {
    return m_component;
  }

  @Override
  public String getStringRepresentation( final IRecord record )
  {
    try
    {
      final Object value = record.getValue( m_component );
      if( value == null )
        return m_nullFormat == null ? "" : String.format( m_nullFormat ); //$NON-NLS-1$
      return String.format( m_displayFormat, value );
    }
    catch( final IndexOutOfBoundsException e )
    {
      return String.format( m_nullFormat );
    }
  }

  @Override
  public Image getImage( final IRecord record )
  {
    return null;
  }

  @Override
  public boolean isEditable( )
  {
    return m_editable;
  }

  @Override
  public boolean isResizeable( )
  {
    return m_resizeable;
  }

  @Override
  public boolean isMoveable( )
  {
    return m_moveable;
  }

  @Override
  public int getColumnStyle( )
  {
    return m_columnStyle;
  }

  @Override
  public int getColumnWidth( )
  {
    return m_columnWidth;
  }

  @Override
  public int getColumnWidthPercent( )
  {
    return m_columnWidthPercent;
  }

  public String getDisplayFormat( )
  {
    return m_displayFormat;
  }

  public String getNullFormat( )
  {
    return m_nullFormat;
  }

  public String getParseFormat( )
  {
    return m_parseFormat;
  }

  @Override
  public String getColumnLabel( )
  {
    return m_columnLabel;
  }

  @Override
  public String getColumnTooltip( )
  {
    return m_columnTooltip;
  }
}