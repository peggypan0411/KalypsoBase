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
package org.kalypso.grid;

import java.math.BigDecimal;

import org.kalypsodeegree.model.elevation.ElevationException;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Polygon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * A {@link IGeoGrid} implementation which delegates all method calls to a delegate grid.
 * <p>
 * The delegate can be changed during runtime, if it is not present a suitable exception is thrown.
 * </p>
 * 
 * @author Gernot Belger
 */
public abstract class AbstractDelegatingGeoGrid implements IGeoGrid
{
  private IGeoGrid m_delegate;

  /** See {@link #AbstractDelegatingGeoGrid(IGeoGrid, boolean)} */
  private final boolean m_disposeDelegate;

  public AbstractDelegatingGeoGrid( final IGeoGrid delegate )
  {
    this( delegate, false );
  }

  /**
   * @param disposeDelegate
   *          The delegate only gets disposed with this wrapper, iff the this parameter is set to <code>true</code>.
   */
  public AbstractDelegatingGeoGrid( final IGeoGrid delegate, final boolean disposeDelegate )
  {
    m_delegate = delegate;
    m_disposeDelegate = disposeDelegate;
  }

  public void setDelegate( final IGeoGrid delegate )
  {
    m_delegate = delegate;
  }

  public IGeoGrid getDelegate( )
  {
    return m_delegate;
  }

  @Override
  public void dispose( )
  {
    if( m_disposeDelegate )
      m_delegate.dispose();
  }

  @Override
  public Envelope getEnvelope( ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getEnvelope();
  }

  @Override
  public Coordinate getOffsetX( ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getOffsetX();
  }

  @Override
  public Coordinate getOffsetY( ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getOffsetY();
  }

  @Override
  public String getSourceCRS( ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getSourceCRS();
  }

  @Override
  public Coordinate getOrigin( ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getOrigin();
  }

  @Override
  public int getSizeX( ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getSizeX();
  }

  @Override
  public int getSizeY( ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getSizeY();
  }

  @Override
  public double getValue( final int x, final int y ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getValue( x, y );
  }

  @Override
  public double getValue( final Coordinate crd ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getValue( crd );
  }

  @Override
  public double getValueChecked( final int x, final int y ) throws GeoGridException
  {
    if( x < 0 || x >= getSizeX() || y < 0 || y >= getSizeY() )
      return Double.NaN;

    return getValue( x, y );
  }

  @Override
  public GM_Polygon getSurface( final String targetCRS ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getSurface( targetCRS );
  }

  @Override
  public GM_Polygon getCell( final int x, final int y, final String targetCRS ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getCell( x, y, targetCRS );
  }

  @Override
  public IGeoWalkingStrategy getWalkingStrategy( ) throws GeoGridException
  {
    if( m_delegate == null )
      throw new GeoGridException( "No grid-delegate available", null );

    return m_delegate.getWalkingStrategy();
  }

  @Override
  public BigDecimal getMin( ) throws GeoGridException
  {
    return m_delegate.getMin();
  }

  @Override
  public BigDecimal getMax( ) throws GeoGridException
  {
    return m_delegate.getMax();
  }

  @Override
  public void close( ) throws Exception
  {
    m_delegate.close();
  }

  @Override
  public GM_Envelope getBoundingBox( ) throws ElevationException
  {
    return m_delegate.getBoundingBox();
  }

  @Override
  public double getMinElevation( ) throws ElevationException
  {
    return m_delegate.getMinElevation();
  }

  @Override
  public double getMaxElevation( ) throws ElevationException
  {
    return m_delegate.getMaxElevation();
  }

  @Override
  public double getElevation( final GM_Point location ) throws ElevationException
  {
    return m_delegate.getElevation( location );
  }
}