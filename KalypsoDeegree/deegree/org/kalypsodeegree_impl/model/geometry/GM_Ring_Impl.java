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
package org.kalypsodeegree_impl.model.geometry;

import java.util.Arrays;

import org.kalypso.transformation.transformer.GeoTransformerException;
import org.kalypsodeegree.model.geometry.GM_Aggregate;
import org.kalypsodeegree.model.geometry.GM_Boundary;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_CurveBoundary;
import org.kalypsodeegree.model.geometry.GM_CurveSegment;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_MultiPrimitive;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree.model.geometry.GM_Ring;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.model.geometry.GM_AbstractSurfacePatch;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

/**
 * default implementation of the GM_Ring interface of the
 * <p>
 * -----------------------------------------------------------------------
 * </p>
 *
 * @version 05.04.2002
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 */
final class GM_Ring_Impl extends GM_AbstractCurve_Impl implements GM_Ring
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 9157144642050604928L;

  private GM_Position[] m_points = null;

  private GM_AbstractSurfacePatch m_sp = null;

  /**
   * Constructor, with Array and CS_CoordinateSystem
   */
  public GM_Ring_Impl( final GM_Position[] points, final String crs ) throws GM_Exception
  {
    super( crs );

    setPositions( points );
  }

  /**
   * Constructor, with Array, CS_CoordinateSystem and Orientation
   */
  public GM_Ring_Impl( final GM_Position[] points, final String crs, final char orientation ) throws GM_Exception
  {
    super( crs, orientation );
    setPositions( points );
  }

  /**
   * calculates the envelope
   */
  @Override
  protected GM_Envelope calculateEnvelope( )
  {
    return GeometryUtilities.envelopeFromRing( m_points, getCoordinateSystem() );
  }

  /**
   * GM_Ring must be closed, so isCycle returns TRUE.
   */
  @Override
  public boolean isCycle( )
  {
    return true;
  }

  /**
   * GM_Ring is a PrimitiveBoundary, so isSimple returns TRUE.
   */
  @Override
  public boolean isSimple( )
  {
    return true;
  }

  /**
   * The operation "dimension" shall return the inherent dimension of this GM_Object, which shall be less than or equal
   * to the coordinate dimension. The dimension of a collection of geometric objects shall be the largest dimension of
   * any of its pieces. Points are 0-dimensional, curves are 1-dimensional, surfaces are 2-dimensional, and solids are
   * 3-dimensional.
   */
  @Override
  public int getDimension( )
  {
    return 1;
  }

  /**
   * The operation "coordinateDimension" shall return the dimension of the coordinates that define this GM_Object, which
   * must be the same as the coordinate dimension of the coordinate reference system for this GM_Object.
   */
  @Override
  public int getCoordinateDimension( )
  {
    return getPositions()[0].getCoordinateDimension();
  }

  /**
   * gets the Ring as a Array of positions.
   */
  @Override
  public GM_Position[] getPositions( )
  {
    if( getOrientation() == '-' )
    {
      final GM_Position[] temp = new GM_Position[m_points.length];

      for( int i = 0; i < m_points.length; i++ )
      {
        temp[i] = m_points[m_points.length - 1 - i];
      }

      return temp;
    }

    return m_points;
  }

  /**
   * sets the Ring as a ArrayList of points
   */
  protected void setPositions( final GM_Position[] positions ) throws GM_Exception
  {
    m_points = positions;

    // checks if the ring has more than 3 elements [!(points.length > 3)]
    if( positions.length < 4 )
    {
      throw new GM_Exception( "invalid length of a Ring!" );
    }

    // checks if the startpoint = endpoint of the ring
    if( !positions[0].equals( positions[positions.length - 1] ) )
    {
      throw new GM_Exception( "StartPoint of ring isn't equal to EndPoint!" );
    }

    invalidate();
  }

  /**
   * returns the Ring as a CurveSegment
   */
  @Override
  public GM_CurveSegment getAsCurveSegment( ) throws GM_Exception
  {
    return new GM_LineString_Impl( m_points, getCoordinateSystem() );
  }

  /**
   * returns the CurveBoundary of the Ring. For a CurveBoundary is defines as the first and the last point of a Curve
   * the CurveBoundary of a Ring contains two indentical point (because a Ring is closed)
   */
  @Override
  public GM_CurveBoundary getCurveBoundary( )
  {
    return (GM_CurveBoundary) getBoundary();
  }

  /**
   * checks if this curve segment is completly equal to the submitted geometry
   *
   * @param other
   *          object to compare to
   */
  @Override
  public boolean equals( final Object other )
  {
    if( !super.equals( other ) || !(other instanceof GM_Ring_Impl) )
    {
      return false;
    }

    if( !getEnvelope().equals( ((GM_Object) other).getEnvelope() ) )
    {
      return false;
    }

    final GM_Position[] p2 = ((GM_Ring) other).getPositions();

    if( !Arrays.equals( m_points, p2 ) )
    {
      return false;
    }

    return true;
  }

  /**
   * returns a shallow copy of the geometry
   */
  @Override
  public Object clone( )
  {
    // kuch
    final String system = getCoordinateSystem();
    final GM_Position[] myPositions = GeometryFactory.cloneGM_Position( getPositions() );
    final char orientation = getOrientation();

    try
    {
      return new GM_Ring_Impl( myPositions, system, orientation );
    }
    catch( final GM_Exception e )
    {
      e.printStackTrace();
    }

    throw new IllegalStateException();
  }

  /**
   * The Boolean valued operation "intersects" shall return TRUE if this GM_Object intersects another GM_Object. Within
   * a GM_Complex, the GM_Primitives do not intersect one another. In general, topologically structured data uses shared
   * geometric objects to capture intersection information.
   */
  @Override
  public boolean intersects( final GM_Object gmo )
  {
    boolean inter = false;

    try
    {
      final GM_CurveSegment sp = new GM_LineString_Impl( m_points, getCoordinateSystem() );

      if( gmo instanceof GM_Point )
      {
        inter = LinearIntersects.intersects( ((GM_Point) gmo).getPosition(), sp );
      }
      else if( gmo instanceof GM_Curve )
      {
        final GM_Curve curve = new GM_Curve_Impl( new GM_CurveSegment[] { sp } );
        inter = LinearIntersects.intersects( (GM_Curve) gmo, curve );
      }
      else if( gmo instanceof GM_Polygon )
      {
        final GM_Curve curve = new GM_Curve_Impl( new GM_CurveSegment[] { sp } );
        inter = LinearIntersects.intersects( curve, (GM_Polygon) gmo );
      }
      else if( gmo instanceof GM_MultiPrimitive )
      {
        inter = intersectsAggregate( (GM_MultiPrimitive) gmo );
      }
    }
    catch( final Exception e )
    {
    }

    return inter;
  }

  /**
   * the operations returns true if the submitted multi primitive intersects with the curve segment
   */
  private boolean intersectsAggregate( final GM_Aggregate mprim ) throws Exception
  {
    boolean inter = false;

    final int cnt = mprim.getSize();

    for( int i = 0; i < cnt; i++ )
    {
      if( intersects( mprim.getObjectAt( i ) ) )
      {
        inter = true;
        break;
      }
    }

    return inter;
  }

  /**
   * The Boolean valued operation "contains" shall return TRUE if this GM_Object contains another GM_Object.
   * <p>
   * </p>
   * At the moment the operation just works with point geometries
   */
  @Override
  public boolean contains( final GM_Object gmo )
  {
    try
    {
      if( m_sp == null )
        m_sp = new GM_PolygonPatch_Impl( m_points, null, getCoordinateSystem() );
      return m_sp.contains( gmo );
    }
    catch( final Exception e )
    {
    }

    return false;
  }

  /**
   * The Boolean valued operation "contains" shall return TRUE if this GM_Object contains a single point given by a
   * coordinate.
   * <p>
   * </p>
   * dummy implementation
   */
  @Override
  public boolean contains( final GM_Position position )
  {
    return contains( new GM_Point_Impl( position, null ) );
  }

  /**
   * calculates the centroid of the ring
   */
  @Override
  protected GM_Point calculateCentroid( )
  {
    return GeometryUtilities.centroidFromRing( m_points, getCoordinateSystem() );
  }

  @Override
  protected GM_Boundary calculateBoundary( )
  {
    // TODO: implement,, what is the boundary of a boundary?
    return GM_Constants.EMPTY_BOUNDARY;
  }

  @Override
  public String toString( )
  {
    String ret = null;
    ret = "points = " + m_points + "\n";
    ret += "envelope = " + getEnvelope() + "\n";
    return ret;
  }

  @Override
  public void invalidate( )
  {
    m_sp = null;

    super.invalidate();
  }

  @Override
  public GM_Object transform( final String targetCRS ) throws GeoTransformerException
  {
    try
    {
      /* If the target is the same coordinate system, do not transform. */
      final String sourceCRS = getCoordinateSystem();
      if( sourceCRS == null || sourceCRS.equalsIgnoreCase( targetCRS ) )
        return this;

      final GM_Position[] pos = getPositions();
      final GM_Position[] transPos = new GM_Position[pos.length];
      for( int i = 0; i < pos.length; i++ )
        transPos[i] = pos[i].transform( sourceCRS, targetCRS );

      return GeometryFactory.createGM_Ring( transPos, targetCRS );
    }
    catch( final GM_Exception e )
    {
      throw new GeoTransformerException( e );
    }
  }
}