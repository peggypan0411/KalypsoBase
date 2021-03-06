/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ogc.sensor.impl;

import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.sensor.IAxis;

/**
 * Default implementation of the IAxis interface. This class is immutable.
 * 
 * @author schlienger
 */
public final class DefaultAxis extends AbstractAxis
{
  private final String m_label;

  private final String m_unit;

  private final Class< ? > m_dataClass;

  private final String m_type;

  private final boolean m_isKey;

  private final boolean m_persistable;

  /**
   * The hash code can be cached, because this axis is immutable.
   */
  private Integer m_hashCode;

  /**
   * Constructor. Calls the full constructor with the persistable argument set to true.
   */
  public DefaultAxis( final String label, final String type, final String unit, final Class< ? > dataClass, final boolean isKey )
  {
    this( label, type, unit, dataClass, isKey, true );
  }

  /**
   * Constructor
   * 
   * @param label
   *          label of the axis
   * @param type
   *          type of the axis
   * @param unit
   *          unit of the axis
   * @param dataClass
   *          className of the data on this axis
   * @param isKey
   *          true if the axis is a key-axis
   * @param persistable
   *          true if the axis should be persisted once observation is saved
   */
  public DefaultAxis( final String label, final String type, final String unit, final Class< ? > dataClass, final boolean isKey, final boolean persistable )
  {
    if( dataClass == null )
      throw new IllegalArgumentException( Messages.getString( "org.kalypso.ogc.sensor.impl.DefaultAxis.0" ) ); //$NON-NLS-1$

    m_label = label;
    m_type = type;
    m_unit = unit;
    m_dataClass = dataClass;
    m_isKey = isKey;
    m_persistable = persistable;
    m_hashCode = null;
  }

  /**
   * Copy Constuctor.
   */
  public DefaultAxis( final IAxis axis )
  {
    this( axis.getName(), axis.getType(), axis.getUnit(), axis.getDataClass(), axis.isKey(), axis.isPersistable() );
  }

  @Override
  public String getUnit( )
  {
    return m_unit;
  }

  @Override
  public String getName( )
  {
    return m_label;
  }

  @Override
  public Class< ? > getDataClass( )
  {
    return m_dataClass;
  }

  @Override
  public String getType( )
  {
    return m_type;
  }

  @Override
  public boolean isKey( )
  {
    return m_isKey;
  }

  @Override
  public boolean isPersistable( )
  {
    return m_persistable;
  }

  @Override
  public int hashCode( )
  {
    if( m_hashCode == null )
      m_hashCode = new Integer( super.hashCode() );

    return m_hashCode.intValue();
  }
}