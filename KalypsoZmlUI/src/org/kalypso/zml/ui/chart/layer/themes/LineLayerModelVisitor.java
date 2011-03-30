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
package org.kalypso.zml.ui.chart.layer.themes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.swt.graphics.Point;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.timeseries.AxisUtils;
import org.kalypso.ogc.sensor.visitor.IObservationValueContainer;
import org.kalypso.ogc.sensor.visitor.IObservationVisitor;
import org.kalypso.zml.core.diagram.data.IZmlLayerDataHandler;
import org.kalypso.zml.ui.KalypsoZmlUI;

import de.openali.odysseus.chart.framework.model.layer.IChartLayerFilter;

/**
 * @author Dirk Kuch
 */
public class LineLayerModelVisitor implements IObservationVisitor
{
  private final Collection<Point> m_path = new ArrayList<Point>();

  private final ZmlLineLayer m_layer;

  private IAxis m_dateAxis;

  private final IChartLayerFilter[] m_filters;

  public LineLayerModelVisitor( final ZmlLineLayer layer, final IChartLayerFilter[] filters )
  {
    m_layer = layer;
    m_filters = filters;
  }

  private IAxis getValueAxis( )
  {
    final IZmlLayerDataHandler handler = m_layer.getDataHandler();

    return handler.getValueAxis();
  }

  private IAxis getDateAxis( )
  {
    if( m_dateAxis == null )
    {
      final IZmlLayerDataHandler handler = m_layer.getDataHandler();
      final IObservation observation = handler.getObservation();
      if( Objects.isNull( observation ) )
        return null;

      final IAxis[] axes = observation.getAxes();

      m_dateAxis = AxisUtils.findDateAxis( axes );
    }

    return m_dateAxis;
  }

  /**
   * @see org.kalypso.ogc.sensor.visitor.ITupleModelVisitor#visit(org.kalypso.ogc.sensor.visitor.ITupleModelVisitorValue)
   */
  @Override
  public void visit( final IObservationValueContainer container )
  {
    try
    {
      final IAxis dateAxis = getDateAxis();
      final IAxis valueAxis = getValueAxis();
      if( Objects.isNull( dateAxis, valueAxis ) )
        return;

      if( !container.hasAxis( getDateAxis().getType(), valueAxis.getType() ) )
        return;

      final Object dateObject = container.get( dateAxis );
      final Object valueObject = container.get( valueAxis );
      if( Objects.isNull( dateObject, valueObject ) )
        return;

      if( isFiltered( container ) )
        return;

      final Date adjusted = (Date) dateObject;

      final Point screen = m_layer.getCoordinateMapper().numericToScreen( m_layer.getRangeHandler().getDateDataOperator().logicalToNumeric( adjusted ), m_layer.getRangeHandler().getNumberDataOperator().logicalToNumeric( (Double) valueObject ) );
      m_path.add( screen );
    }
    catch( final SensorException e )
    {
      KalypsoZmlUI.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
    }
  }

  private boolean isFiltered( final IObservationValueContainer container )
  {
    if( ArrayUtils.isEmpty( m_filters ) )
      return false;

    for( final IChartLayerFilter filter : m_filters )
    {
      if( filter.isFiltered( container ) )
        return true;
    }

    return false;
  }

  public Point[] getPoints( )
  {
    return m_path.toArray( new Point[m_path.size()] );
  }
}
