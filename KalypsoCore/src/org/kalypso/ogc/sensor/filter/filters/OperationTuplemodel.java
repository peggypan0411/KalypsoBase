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
package org.kalypso.ogc.sensor.filter.filters;

import java.util.Date;

import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.impl.AbstractTupleModel;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;

/**
 * @author doemming
 */
public class OperationTuplemodel extends AbstractTupleModel
{
  private final double m_operand;

  private final int m_operation;

  private final ITupleModel m_baseModel;

  public OperationTuplemodel( final double operand, final int operation, final ITupleModel baseModel )
  {
    super( baseModel.getAxes() );

    m_operand = operand;
    m_operation = operation;
    m_baseModel = baseModel;
  }

  @Override
  public int size( ) throws SensorException
  {
    return m_baseModel.size();
  }

  @Override
  public int hashCode( )
  {
    return m_baseModel.hashCode();
  }

  @Override
  public String toString( )
  {
    return m_baseModel.toString();
  }

  @Override
  public Object get( final int index, final IAxis axis ) throws SensorException
  {
    final IAxis a = ObservationUtilities.findAxisByName( m_baseModel.getAxes(), axis.getName() );
    if( index >= m_baseModel.size() )
      return null;

    final Object object = m_baseModel.get( index, a );
    if( object == null || object instanceof Date || KalypsoStatusUtils.isStatusAxis( axis ) )
      return object;

    if( object instanceof Number ) // let it be a Number here so we can handle integers and such
    {
      final double value = ((Number) object).doubleValue();
      switch( m_operation )
      {
        case OperationFilter.OPERATION_PLUS:
          return new Double( value + m_operand );
        case OperationFilter.OPERATION_MINUS:
          return new Double( value - m_operand );
        case OperationFilter.OPERATION_MAL:
          return new Double( value * m_operand );
        case OperationFilter.OPERATION_DURCH:
          return new Double( value / m_operand );
      }
    }

    throw new UnsupportedOperationException( getClass().getName() + Messages.getString( "org.kalypso.ogc.sensor.filter.filters.OperationTupplemodel.0" ) //$NON-NLS-1$
        + object.getClass().getName() + Messages.getString( "org.kalypso.ogc.sensor.filter.filters.OperationTupplemodel.1" ) ); //$NON-NLS-1$
  }

  @Override
  public void set( final int index, final IAxis axis, final Object element )
  {
    throw new UnsupportedOperationException( getClass().getName() + Messages.getString( "org.kalypso.ogc.sensor.filter.filters.OperationTupplemodel.2" ) ); //$NON-NLS-1$
    // TODO support it
  }

  @Override
  public int indexOf( final Object element, final IAxis axis ) throws SensorException
  {
    if( element instanceof Date )
      return m_baseModel.indexOf( element, axis );

    throw new UnsupportedOperationException( getClass().getName() + Messages.getString( "org.kalypso.ogc.sensor.filter.filters.OperationTupplemodel.3" ) //$NON-NLS-1$
        + axis.getName() + Messages.getString( "org.kalypso.ogc.sensor.filter.filters.OperationTupplemodel.4" ) ); //$NON-NLS-1$
    // TODO support it
  }
}