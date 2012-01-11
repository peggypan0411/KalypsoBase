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
import java.util.Date;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.timeseries.AxisUtils;
import org.kalypso.ogc.sensor.visitor.IObservationValueContainer;
import org.kalypso.ogc.sensor.visitor.IObservationVisitor;
import org.kalypso.zml.ui.KalypsoZmlUI;

/**
 * @author Dirk Kuch
 */
public class ZmlBarLayerVisitor implements IObservationVisitor
{
  Point m_lastScreen;

  IAxis m_dateAxis;

  private IAxis m_valueAxis;

  private final ZmlBarLayerRangeHandler m_range;

  private final ZmlBarLayer m_layer;

  List<Rectangle> m_rectangles = new ArrayList<Rectangle>();

  public ZmlBarLayerVisitor( final ZmlBarLayer layer, final ZmlBarLayerRangeHandler range )
  {
    m_layer = layer;
    m_range = range;
  }

  @Override
  public void visit( final IObservationValueContainer container )
  {
    try
    {
      final Object domainValue = container.get( getDateAxis( container ) );
      final Object targetValue = getTargetValue( container );
      if( Objects.isNull( domainValue, targetValue ) )
        return;

      final Point base = m_layer.getCoordinateMapper().numericToScreen( 0.0, 0.0 );

      final Number logicalDomain = m_range.getDateDataOperator().logicalToNumeric( (Date) domainValue );
      final Number logicalTarget = m_range.getNumberDataOperator().logicalToNumeric( (Number) targetValue );
      final Point screen = m_layer.getCoordinateMapper().numericToScreen( logicalDomain, logicalTarget );

      // don't draw empty lines only rectangles
      if( screen.y != base.y )
      {

        final int x = getX( m_lastScreen, screen );

        final List<Point> points = new ArrayList<Point>();
        points.add( new Point( x, base.y ) );
        points.add( new Point( x, screen.y ) );
        points.add( screen );
        points.add( new Point( screen.x, base.y ) );
        final Point[] p = points.toArray( new Point[] {} );

        m_rectangles.add( new Rectangle( x, screen.y, screen.x - x, base.y - screen.y ) );
      }

      m_lastScreen = screen;
    }
    catch( final Throwable t )
    {
      KalypsoZmlUI.getDefault().getLog().log( StatusUtilities.statusFromThrowable( t ) );
    }
  }

  public Rectangle[] getRectangles( )
  {
    return m_rectangles.toArray( new Rectangle[] {} );
  }

  private Object getTargetValue( final IObservationValueContainer container ) throws SensorException
  {
    Object value = container.get( getValueAxis( container ) );

    /** @hack for polder control */
    if( value instanceof Boolean )
    {
      if( Boolean.valueOf( (Boolean) value ) )
        value = 1;
      else
        value = 0;
    }

    return value;
  }

  private int getX( final Point p1, final Point p2 )
  {
    if( p1 == null )
      return p2.x;

    return p1.x;
  }

  private IAxis getValueAxis( final IObservationValueContainer container )
  {
    if( Objects.isNull( m_valueAxis ) )
      m_valueAxis = AxisUtils.findValueAxis( container.getAxes() );

    return m_valueAxis;
  }

  private IAxis getDateAxis( final IObservationValueContainer container )
  {
    if( Objects.isNull( m_dateAxis ) )
      m_dateAxis = AxisUtils.findDateAxis( container.getAxes() );

    return m_dateAxis;
  }
}
