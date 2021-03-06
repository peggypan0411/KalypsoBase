/** This file is part of kalypso/deegree.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * history:
 *
 * Files in this package are originally taken from deegree and modified here
 * to fit in kalypso. As goals of kalypso differ from that one in deegree
 * interface-compatibility to deegree is wanted but not retained always.
 *
 * If you intend to use this software in other ways than in kalypso
 * (e.g. OGC-web services), you should consider the latest version of deegree,
 * see http://www.deegree.org .
 *
 * all modifications are licensed as deegree,
 * original copyright:
 *
 * Copyright (C) 2001 by:
 * EXSE, Department of Geography, University of Bonn
 * http://www.giub.uni-bonn.de/exse/
 * lat/lon GmbH
 * http://www.lat-lon.de
 */
package org.kalypsodeegree_impl.gml.binding.commons;

import javax.xml.namespace.QName;

import org.kalypso.commons.xml.NS;
import org.kalypso.transformation.transformer.GeoTransformerFactory;
import org.kalypso.transformation.transformer.IGeoTransformer;
import org.kalypsodeegree.model.coverage.GridRange;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * Class which holds the GridDomainData of a RectifiedGridCoverage
 * 
 * @author N. Peiler
 */
public class RectifiedGridDomain
{
  public static final class OffsetVector
  {
    private final double m_geoX;

    private final double m_geoY;

    public OffsetVector( final double geoX, final double geoY )
    {
      m_geoX = geoX;
      m_geoY = geoY;
    }

    public GM_Position move( final GM_Position pos, final int number )
    {
      if( pos.getCoordinateDimension() > 2 )
        return GeometryFactory.createGM_Position( pos.getX() + number * m_geoX, pos.getY() + number * m_geoY, pos.getZ() );
      else
        return GeometryFactory.createGM_Position( pos.getX() + number * m_geoX, pos.getY() + number * m_geoY );
    }

    public double getGeoX( )
    {
      return m_geoX;
    }

    public double getGeoY( )
    {
      return m_geoY;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString( )
    {
      return String.format( "GeoX: %f\tGeoY: %f", m_geoX, m_geoY );
    }
  }

  /**
   * lowerleft corner of the RectifiedGridCoverage
   */
  private final GM_Point m_origin;

  /** Offset vecotr for the raster x-direction. */
  private final OffsetVector m_offsetX;

  /** Offset vecotr for the raster y-direction. */
  private final OffsetVector m_offsetY;

  private final GridRange m_gridRange;

  private final GM_Polygon m_rasterBoundaryAsSurface;

  public static final QName QNAME = new QName( NS.GML3, "RectifiedGrid" );

  /**
   * constructs a RectifiedGridDomain with the given origin, offset and gridRange
   * 
   * @param origin
   * @param offset
   * @param gridRange
   */
  public RectifiedGridDomain( final GM_Point origin, final OffsetVector offsetX, final OffsetVector offsetY, final GridRange gridRange )
  {
    m_origin = origin;
    m_offsetX = offsetX;
    m_offsetY = offsetY;
    m_gridRange = gridRange;
    m_rasterBoundaryAsSurface = getGM_Surface( origin.getCoordinateSystem() );
  }

  public GM_Polygon getGM_Surface( final String crs )
  {
    try
    {
      return RectifiedGridDomain.calculateSurface( m_origin, m_offsetX, m_offsetY, 0, 0, getNumColumns(), getNumRows(), crs );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      return null;
    }
  }

  private static GM_Polygon calculateSurface( final GM_Point origin, final OffsetVector offsetX, final OffsetVector offsetY, final int minX, final int minY, final int maxX, final int maxY, final String cs ) throws Exception
  {
    final GM_Position originPos = origin.getPosition();

    final GM_Position pos0 = offsetY.move( offsetX.move( originPos, minX ), minY );
    final GM_Position pos1 = offsetX.move( pos0, maxX - minX - 1 );
    final GM_Position pos2 = offsetY.move( pos1, maxY - minY - 1 );
    final GM_Position pos3 = offsetY.move( pos0, maxY - minY - 1 );
    final GM_Position[] ring = new GM_Position[] { pos0, pos1, pos2, pos3, pos0 };
    final String originCrs = origin.getCoordinateSystem();
    final GM_Polygon surface = GeometryFactory.createGM_Surface( ring, null, originCrs );

    if( originCrs == null || cs == null || originCrs.equals( cs ) )
      return surface;

    final IGeoTransformer geoTrans = GeoTransformerFactory.getGeoTransformer( cs );
    return (GM_Polygon)geoTrans.transform( surface );
  }

  public String getCoordinateSystem( )
  {
    return m_origin.getCoordinateSystem();
  }

  public GM_Point getOrigin( final String cs ) throws Exception
  {
    if( cs == null || m_origin.getCoordinateSystem().equals( cs ) )
      return m_origin;

    final IGeoTransformer geoTrans = GeoTransformerFactory.getGeoTransformer( cs );
    return (GM_Point)geoTrans.transform( m_origin );
  }

  /**
   * @return Returns the gridRange.
   */
  public GridRange getGridRange( )
  {
    return m_gridRange;
  }

  /**
   * @return Returns the numColumns.
   */
  public int getNumColumns( )
  {
    final double[] low = m_gridRange.getLow();
    final double[] high = m_gridRange.getHigh();
    final double numColumns = high[0] - low[0];
    return new Double( numColumns ).intValue();
  }

  /**
   * @return Returns the numRows.
   */
  public int getNumRows( )
  {
    final double[] low = m_gridRange.getLow();
    final double[] high = m_gridRange.getHigh();
    final double numRows = high[1] - low[1];
    return new Double( numRows ).intValue();
  }

  /**
   * @return Returns the offset of a gridCell for the x-Axis
   * @throws Exception
   */
  public double getOffsetX( final String cs ) throws Exception
  {
    if( cs == null || m_origin.getCoordinateSystem().equals( cs ) )
      return m_offsetX.getGeoX();

    // ???
    final GM_Envelope destEnv = getGM_Envelope( cs );
    return destEnv.getWidth() / getNumColumns();
  }

  /**
   * @return Returns the offset of a gridCell for the y-Axis
   */
  public double getOffsetY( final String cs ) throws Exception
  {
    if( cs == null || m_origin.getCoordinateSystem().equals( cs ) )
      return m_offsetY.getGeoY();

    // ???
    final GM_Envelope destEnv = getGM_Envelope( cs );
    return destEnv.getHeight() / getNumRows();
  }

  /**
   * @return Returns the GM_Envelope of the RectifiedGridDomain
   * @throws Exception
   */
  public GM_Envelope getGM_Envelope( final String cs ) throws Exception
  {
    final String originCrs = m_origin.getCoordinateSystem();
    if( originCrs == null || cs == null || originCrs.equals( cs ) )
      return m_rasterBoundaryAsSurface.getEnvelope();

    final IGeoTransformer geoTrans = GeoTransformerFactory.getGeoTransformer( cs );
    return geoTrans.transform( m_rasterBoundaryAsSurface ).getEnvelope();
  }

  public GM_Polygon getGM_Surface( final int lowX, final int lowY, final int highX, final int highY, final String cs ) throws Exception
  {
    return RectifiedGridDomain.calculateSurface( m_origin, m_offsetX, m_offsetY, lowX, lowY, highX, highY, cs );
  }

  /**
   * get low and high (GridRange) of the RectifiedGridCoverage for the given envelope
   */
  public int[] getGridExtent( final GM_Envelope env, final String cs ) throws Exception
  {
    /* Check precionditions: only cartesian offset vectors supported */
    // TODO: support arbitrary offset vectors
    if( m_offsetX.getGeoY() != 0.0 )
      System.out.println( "OffsetX-Vector is not cartesian!" );
    if( m_offsetY.getGeoX() != 0.0 )
      System.out.println( "OffsetY-Vector is not cartesian!" );

    int lowX = (int)getGridRange().getLow()[0];
    int lowY = (int)getGridRange().getLow()[1];

    final GM_Envelope envelope = getGM_Envelope( cs );
    final GM_Position origin = envelope.getMin();

    if( env.getMin().getX() - origin.getX() > 0 )
      lowX = (int)((env.getMin().getX() - origin.getX()) / getOffsetX( cs ));

    if( env.getMin().getY() - origin.getY() > 0 )
      lowY = (int)((env.getMin().getY() - origin.getY()) / getOffsetY( cs ));

    int highX = (int)((env.getMax().getX() - origin.getX()) / getOffsetX( cs ));
    if( highX > (int)getGridRange().getHigh()[0] )
      highX = (int)getGridRange().getHigh()[0];

    int highY = Math.abs( (int)((env.getMax().getY() - origin.getY()) / getOffsetY( cs )) );
    if( highY > (int)getGridRange().getHigh()[1] )
      highY = (int)getGridRange().getHigh()[1];

    final int[] gridExtent = new int[] { lowX, lowY, highX, highY };

    return gridExtent;
  }

  public OffsetVector getOffsetX( )
  {
    return m_offsetX;
  }

  public OffsetVector getOffsetY( )
  {
    return m_offsetY;
  }

  /**
   * Calculates the position of a raster cell.
   */
  public GM_Position getPositionAt( final int x, final int y )
  {
    return m_offsetY.move( m_offsetX.move( m_origin.getPosition(), x ), y );
  }
}