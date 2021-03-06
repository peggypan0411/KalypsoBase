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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.kalypso.transformation.transformer.GeoTransformerException;
import org.kalypsodeegree.model.geometry.GM_Boundary;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_CurveBoundary;
import org.kalypsodeegree.model.geometry.GM_CurveSegment;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_LineString;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

/**
 * default implementation of the GM_Curve interface from package jago.model.
 * ------------------------------------------------------------
 *
 * @version 14.10.2001
 * @author Andreas Poth
 */
final class GM_Curve_Impl extends GM_AbstractCurve_Impl implements GM_Curve
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 4060425075179654976L;

  protected List<GM_CurveSegment> m_segments;

  /**
   * initialize the curve by submitting a spatial reference system and an array of curve segments. the orientation of
   * the curve is '+'
   *
   * @param segments
   *          array of GM_CurveSegment
   */
  public GM_Curve_Impl( final GM_CurveSegment[] segments ) throws GM_Exception
  {
    this( '+', segments );
  }

  /**
   * initialize the curve by submitting a spatial reference system, an array of curve segments and the orientation of
   * the curve
   *
   * @param segments
   *          array of GM_CurveSegment
   * @param orientation
   *          of the curve
   */
  public GM_Curve_Impl( final char orientation, final GM_CurveSegment[] segments ) throws GM_Exception
  {
    super( segments[0].getCoordinateSystem(), orientation );

    m_segments = new ArrayList<>( segments.length );

    for( int i = 0; i < segments.length; i++ )
    {
      m_segments.add( segments[i] );

      if( i > 0 )
      {
        if( !segments[i - 1].getEndPoint().equals( segments[i].getStartPoint() ) )
        {
          throw new GM_Exception( "end-point of segment[i-1] doesn't match start-point of segment[i]!" );
        }
      }
    }
  }

  /**
   * calculates the envelope of the Curve
   */
  @Override
  protected GM_Envelope calculateEnvelope( )
  {
    GM_Envelope env = null;
    final String crs = getCoordinateSystem();
    for( final GM_CurveSegment segment : m_segments )
    {
      final GM_Position[] positions = segment.getPositions();
      final GM_Envelope posEnv = GeometryUtilities.envelopeFromRing( positions, crs );
      env = env == null ? posEnv : env.getMerged( posEnv );
    }

    return env;
  }

  /**
   * @see org.kalypsodeegree_impl.model.geometry.GM_Object_Impl#calculateBoundary()
   */
  @Override
  protected GM_Boundary calculateBoundary( )
  {
    return new GM_CurveBoundary_Impl( getCoordinateSystem(), getStartPoint().getPosition(), getEndPoint().getPosition() );
  }

  /**
   * calculates the centroid of the Curve <br>
   * if you follow the curve and measure the length from start to end, the centeroid should be half the way.
   */
  @Override
  protected GM_Point calculateCentroid( ) throws GM_Exception
  {
    final GM_Position[] positions = getAsLineString().getPositions();
    if( positions.length < 2 )
      return GM_Constants.EMPTY_CENTROID;

    final double length = getLength();
    if( length == 0 )
      return new GM_Point_Impl( positions[0], getCoordinateSystem() );

    final double halfWay = length / 2d;
    double coveredDistance = 0;
    int i = 1;
    for( ; i < positions.length; i++ )
    {
      final double d = positions[i].getDistance( positions[i - 1] );
      if( coveredDistance + d <= halfWay )
      {
        coveredDistance += d;
      }
      else
      {
        break;
      }
    }
    final GM_Position newPos = GeometryUtilities.createGM_PositionAt( positions[i - 1], positions[i], halfWay - coveredDistance );
    return new GM_Point_Impl( newPos, getCoordinateSystem() );
  }

  /**
   * returns the boundary of the curve
   */
  @Override
  public GM_CurveBoundary getCurveBoundary( )
  {
    return (GM_CurveBoundary) getBoundary();
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
    int srsDimension = 0;
    for( final GM_CurveSegment segment : m_segments )
    {
      final GM_Position[] positions = segment.getPositions();
      for( final GM_Position position : positions )
        srsDimension = Math.max( srsDimension, position.getCoordinateDimension() );
    }

    return srsDimension;
  }

  /**
   * The Boolean valued operation "intersects" shall return TRUE if this GM_Object intersects another GM_Object. Within
   * a GM_Complex, the GM_Primitives do not intersect one another. In general, topologically structured data uses shared
   * geometric objects to capture intersection information.
   * <p>
   * </p>
   * dummy implementation
   */
  @Override
  public boolean intersects( final GM_Object gmo )
  {
    boolean inter = false;

    try
    {
      for( int i = 0; i < m_segments.size(); i++ )
      {
        final GM_CurveSegment cs = getCurveSegmentAt( i );

        if( cs.intersects( gmo ) )
        {
          inter = true;
          break;
        }
      }
    }
    catch( final Exception e )
    {
    }

    return inter;
  }

  /**
   * returns the length of the curve in units of the related spatial reference system
   */
  @Override
  public double getLength( )
  {
    double length = 0;

    for( final GM_CurveSegment segment : m_segments )
      length += segment.getLength();

    return length;
  }

  /**
   * returns the number of segments building the curve
   */
  @Override
  public int getNumberOfCurveSegments( )
  {
    return m_segments.size();
  }

  /**
   * returns the first point of the curve. if the curve doesn't contain a segment or the first segment doesn't contain a
   * point null will be returned
   */
  @Override
  public GM_Point getStartPoint( )
  {
    if( getNumberOfCurveSegments() == 0 )
    {
      return null;
    }

    GM_Point gmp = null;

    try
    {
      gmp = getCurveSegmentAt( 0 ).getStartPoint();
    }
    catch( final GM_Exception e )
    {
    }

    return gmp;
  }

  /**
   * returns the last point of the curve.if the curve doesn't contain a segment or the last segment doesn't contain a
   * point null will be returned
   */
  @Override
  public GM_Point getEndPoint( )
  {
    if( getNumberOfCurveSegments() == 0 )
    {
      return null;
    }

    GM_Point gmp = null;

    try
    {
      gmp = getCurveSegmentAt( getNumberOfCurveSegments() - 1 ).getEndPoint();
    }
    catch( final GM_Exception e )
    {
    }

    return gmp;
  }

  /**
   * returns the curve as GM_LineString. if there isn't a curve segment within the curve null will be returned
   */
  @Override
  public GM_LineString getAsLineString( ) throws GM_Exception
  {
    if( getNumberOfCurveSegments() == 0 )
    {
      return null;
    }

    GM_Position[] tmp = null;

    // normal orientaton
    if( getOrientation() == '+' )
    {
      int cnt = 0;

      for( int i = 0; i < getNumberOfCurveSegments(); i++ )
      {
        cnt += getCurveSegmentAt( i ).getNumberOfPoints();
      }

      tmp = new GM_Position[cnt];

      int k = 0;

      for( int i = 0; i < getNumberOfCurveSegments(); i++ )
      {
        final GM_Position[] gmps = getCurveSegmentAt( i ).getPositions();

        for( final GM_Position element : gmps )
        {
          tmp[k++] = element;
        }
      }
    }
    else
    {
      // inverse orientation
      int cnt = 0;

      for( int i = getNumberOfCurveSegments() - 1; i >= 0; i-- )
      {
        cnt += getCurveSegmentAt( i ).getNumberOfPoints();
      }

      tmp = new GM_Position[cnt];

      int k = 0;

      for( int i = getNumberOfCurveSegments() - 1; i >= 0; i-- )
      {
        final GM_Position[] gmps = getCurveSegmentAt( i ).getPositions();

        for( int j = gmps.length - 1; j >= 0; j-- )
        {
          tmp[k++] = gmps[j];
        }
      }
    }

    return new GM_LineString_Impl( tmp, getCoordinateSystem() );
  }

  /**
   * returns the curve segment at the submitted index
   *
   * @param index
   *          index of the curve segment that should be returned
   * @exception GM_Exception
   *              a exception will be thrown if <tt>index</tt> is smaller than '0' or larger than
   *              <tt>getNumberOfCurveSegments()-1</tt>
   */
  @Override
  public GM_CurveSegment getCurveSegmentAt( final int index ) throws GM_Exception
  {
    if( index < 0 || index > getNumberOfCurveSegments() - 1 )
    {
      throw new GM_Exception( "invalid index/position to get a segment!" );
    }

    return m_segments.get( index );
  }

  /**
   * writes a segment to the curve at submitted position. the old point will be deleted
   *
   * @param segment
   *          curve segment that should be set
   * @param index
   *          index where to set the curve segment
   * @exception GM_Exception
   *              a exception will be thrown if <tt>index</tt> is smaller than '0' or larger than
   *              <tt>getNumberOfCurveSegments()-1</tt> or or the starting point of the submitted curve segment isn't
   *              equal to the ending point of segment at <tt>index-1</tt> and/or the ending point of the submitted
   *              segment isn't equals to the curve segment at <tt>index+1</tt>
   */
  @Override
  public void setCurveSegmentAt( final GM_CurveSegment segment, final int index ) throws GM_Exception
  {
    if( index < 0 || index > getNumberOfCurveSegments() - 1 )
    {
      throw new GM_Exception( "invalid index/position to set a segment!" );
    }

    /*
     * checks if the start/endpoint of the inserted segment is equal to the end/startpoint of the successor/previous
     * segment start segx == start segx-1
     */
    final GM_Point p1 = segment.getEndPoint();
    final GM_Point p2 = segment.getStartPoint();

    if( index == 0 )
    {
      final GM_Point p4 = getCurveSegmentAt( index + 1 ).getStartPoint();

      /*
       * insert segment at beginning of curve
       */
      if( !p1.equals( p4 ) )
      {
        throw new GM_Exception( "end-point of segment[i-1] doesn't match start-point of segment[i]!" );
      }
    }
    else if( index > 0 && index < getNumberOfCurveSegments() - 1 )
    {
      final GM_Point p4 = getCurveSegmentAt( index + 1 ).getStartPoint();
      final GM_Point p5 = getCurveSegmentAt( index - 1 ).getEndPoint();

      /*
       * insert segment anywhere in curve
       */
      if( !p1.equals( p4 ) || !p2.equals( p5 ) )
      {
        throw new GM_Exception( "end-point of segment[i-1 || i]  doesn't match start-point of segment[i || i+1]!" );
      }
    }
    else if( index == getNumberOfCurveSegments() - 1 )
    {
      final GM_Point p5 = getCurveSegmentAt( index - 1 ).getEndPoint();

      /*
       * insert segment at end of curve
       */
      if( !p2.equals( p5 ) )
      {
        throw new GM_Exception( "end-point of segment[i-1 || i]  doesn't match start-point of segment[i || i+1]!" );
      }
    }

    m_segments.set( index, segment );

    invalidate();
  }

  /**
   * inserts a segment in the curve at the submitted position. all points with a position that equals index or is higher
   * will be shifted
   *
   * @param segment
   *          curve segment that should be inserted
   * @param index
   *          index where to insert the curve segment
   * @exception GM_Exception
   *              a exception will be thrown if <tt>index</tt> is smaller than '0' or larger than
   *              <tt>getNumberOfCurveSegments()-1</tt> or or the starting point of the submitted curve segment isn't
   *              equal to the ending point of segment at <tt>index-1</tt> and/or the ending point of the submitted
   *              segment isn't equals to the curve segment at <tt>index+1</tt>
   */
  @Override
  public void insertCurveSegmentAt( final GM_CurveSegment segment, final int index ) throws GM_Exception
  {
    if( index < 0 || index > getNumberOfCurveSegments() - 1 )
    {
      throw new GM_Exception( "invalid index/position to insert a segment!" );
    }

    /*
     * checks if the start/endpoint of the inserted segment is equal to the end/startpoint of the successor/previous
     * segment
     */
    final GM_Point p1 = segment.getEndPoint();
    final GM_Point p2 = segment.getStartPoint();

    if( index == 0 )
    {
      final GM_Point p4 = getCurveSegmentAt( index + 1 ).getStartPoint();

      /*
       * insert segment at beginning of curve
       */
      if( !p1.equals( p4 ) )
      {
        throw new GM_Exception( "end-point of segment[i] doesn't match start-point of segment[i+1]!" );
      }
    }
    else if( index > 0 && index < getNumberOfCurveSegments() - 1 )
    {
      final GM_Point p4 = getCurveSegmentAt( index + 1 ).getStartPoint();
      final GM_Point p5 = getCurveSegmentAt( index - 1 ).getEndPoint();

      /*
       * insert segment anywhere in curve
       */
      if( !p1.equals( p4 ) || !p2.equals( p5 ) )
      {
        throw new GM_Exception( "end-point of segment[i-1 || i]  doesn't match start-point of segment[i || i+1]!" );
      }
    }
    else if( index == getNumberOfCurveSegments() - 1 )
    {
      final GM_Point p5 = getCurveSegmentAt( index - 1 ).getEndPoint();

      /*
       * insert segment at end of curve
       */
      if( !p2.equals( p5 ) )
      {
        throw new GM_Exception( "end-point of segment[i-1]  doesn't match start-point of segment[i]!" );
      }
    }

    m_segments.add( index, segment );

    invalidate();
  }

  /**
   * adds a segment at the end of the curve
   *
   * @param segment
   *          curve segment that should be set
   * @exception GM_Exception
   *              a exception will be thrown if the starting point of the submitted curve segment isn't equal to the
   *              ending point of the last segment.
   */
  @Override
  public void addCurveSegment( final GM_CurveSegment segment ) throws GM_Exception
  {
    // TODO: Was wenn keine Curve da?
    final GM_Point p2 = getEndPoint();

    m_segments.add( segment );

    final GM_Point p1 = segment.getStartPoint();

    if( !p1.equals( p2 ) )
    {
      throw new GM_Exception( "EndPoint of last segment doesn't match StartPoint of adding segment" );
    }

    invalidate();
  }

  /**
   * deletes the segment at the submitted index
   *
   * @param index
   *          index of the curve segement that should be removed from the curve.
   * @exception GM_Exception
   *              will be thrown if <tt>index</tt> is smaller '0' or larger <tt>getNumberOfCurveSegments()-1</tt>
   */
  @Override
  public void deleteCurveSegmentAt( final int index ) throws GM_Exception
  {
    if( index < 0 || index > getNumberOfCurveSegments() - 1 )
    {
      throw new GM_Exception( "invalid index/position to remove a segment!" );
    }

    final GM_Point p1 = getCurveSegmentAt( index - 1 ).getEndPoint();
    // p1 (index.sp) and p2 (index.ep) not used here!
    final GM_Point p4 = getCurveSegmentAt( index + 1 ).getStartPoint();

    if( index > 0 )
    {
      if( !p1.equals( p4 ) )
      {
        throw new GM_Exception( "end-point of segment[index-1] doesn't match start-point of segment[index+1]!" );
      }
    }

    m_segments.remove( index );

    invalidate();
  }

  /**
   * returns true if no segment is within the curve
   */
  @Override
  public boolean isEmpty( )
  {
    return getNumberOfCurveSegments() == 0;
  }

  /**
   * translate each point of the curve with the values of the submitted double array.
   */
  @Override
  public void translate( final double[] d )
  {
    try
    {
      for( int i = 0; i < m_segments.size(); i++ )
      {
        final GM_Position[] pos = getCurveSegmentAt( i ).getPositions();

        for( final GM_Position element : pos )
        {
          element.translate( d );
        }
      }
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
    invalidate();
  }

  /**
   * checks if this curve is completly equal to the submitted geometry
   *
   * @param other
   *          object to compare to
   */
  @Override
  public boolean equals( final Object other )
  {
    if( !super.equals( other ) )
    {
      return false;
    }

    if( !(other instanceof GM_Curve_Impl) )
    {
      return false;
    }

    // Bugfix: envelope maybe not yet valid
    if( !getEnvelope().equals( ((GM_Object) other).getEnvelope() ) )
    {
      return false;
    }

    if( getNumberOfCurveSegments() != ((GM_Curve) other).getNumberOfCurveSegments() )
    {
      return false;
    }

    try
    {
      for( int i = 0; i < m_segments.size(); i++ )
      {
        if( !getCurveSegmentAt( i ).equals( ((GM_Curve) other).getCurveSegmentAt( i ) ) )
        {
          return false;
        }
      }
    }
    catch( final Exception e )
    {
      return false;
    }

    return true;
  }

  /**
   * returns a shallow copy of the geometry
   */
  @Override
  public GM_Curve clone( ) throws CloneNotSupportedException
  {
    final List<GM_CurveSegment> mySegments = new LinkedList<>();
    for( final GM_CurveSegment segment : m_segments )
    {
      mySegments.add( (GM_CurveSegment) segment.clone() );
    }

    try
    {
      return new GM_Curve_Impl( mySegments.toArray( new GM_CurveSegment[] {} ) );
    }
    catch( final GM_Exception e )
    {
      e.printStackTrace();
    }

    throw new IllegalStateException();
  }

  @Override
  public String toString( )
  {
    String ret = null;
    ret = "segments = " + m_segments + "\n";
    ret += "envelope = " + getEnvelope() + "\n";
    return ret;
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

      final GM_CurveSegment[] newcus = new GM_CurveSegment[getNumberOfCurveSegments()];

      for( int i = 0; i < getNumberOfCurveSegments(); i++ )
      {
        final GM_CurveSegment cus = getCurveSegmentAt( i );
        final GM_Position[] pos = cus.getPositions();

        // transformed positions-array
        final GM_Position[] newpos = new GM_Position[pos.length];

        for( int j = 0; j < pos.length; j++ )
          newpos[j] = pos[j].transform( sourceCRS, targetCRS );

        newcus[i] = GeometryFactory.createGM_CurveSegment( newpos, targetCRS );
      }

      return GeometryFactory.createGM_Curve( newcus );
    }
    catch( final GM_Exception e )
    {
      throw new GeoTransformerException( e );
    }
  }
}