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
package org.kalypso.jts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.Assert;
import org.kalypso.commons.java.lang.MathUtils;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.commons.math.LinearEquation;
import org.kalypso.commons.math.LinearEquation.SameXValuesException;
import org.kalypso.commons.math.geom.PolyLine;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree_impl.model.geometry.JTSAdapter;

import com.infomatiq.jsi.Rectangle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.GeometryEditor;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;

/**
 * Utility class for some geometry operations.
 * 
 * @author Holger Albert
 */
public final class JTSUtilities
{
  /**
   * The allowed tolerance.
   */
  // FIXME: bad, rather use precisions model of JTS
  public static final double TOLERANCE = 10E-04;

  private JTSUtilities( )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * This function delivers the first point from a line in another geometry.
   * 
   * @param line
   *          The points of this line will be checked. The first, which lies in the given geometry is returned.
   * @param geometry_2nd
   *          The points of the line will be checked with this geometry.
   * @return The first point of the line, which lies in the second geometry.
   */
  public static Point linePointInGeometry( final LineString line, final Geometry geometry_2nd )
  {
    final int numPoints = line.getNumPoints();

    for( int i = 0; i < numPoints; i++ )
    {
      final Point pointN = line.getPointN( i );

      if( geometry_2nd.contains( pointN ) )
      {
        final GeometryFactory factory = new GeometryFactory( pointN.getPrecisionModel(), pointN.getSRID() );
        return factory.createPoint( new Coordinate( pointN.getCoordinate() ) );
      }
    }

    return null;
  }

  /**
   * This function calculates the distance from the start point to a point, lying on the line.
   * 
   * @param line
   *          The line.
   * @param point
   *          One point lying on the line.
   * @return The distance of the point on the line.
   * @deprecated This method is too slow for long chains. Use {@link com.vividsolutions.jts.linearref.LengthIndexedLine} instead. And does not work for last line segment!!!!
   */
  @Deprecated
  public static double pointDistanceOnLine( final LineString line, final Point point )
  {
    /* Check for intersection. */
    if( point.distance( line ) >= TOLERANCE )
      throw new IllegalStateException( "The point does not lie on the line..." );

    if( line.getEndPoint().equals( point ) )
      return line.getLength();

    /* The needed factory. */
    final GeometryFactory factory = new GeometryFactory( line.getPrecisionModel(), line.getSRID() );

    /* Get all coordinates. */
    final Coordinate[] coordinates = line.getCoordinates();

    /* Only loop until the one before the last one. */
    for( int i = 0; i < coordinates.length - 2; i++ )
    {
      /* Get the coordinates to the current one + 1. */
      final Coordinate[] coords = ArrayUtils.subarray( coordinates, 0, i + 2 );

      /* Create a new line with the coordinates. */
      final LineString ls = factory.createLineString( coords );
      if( point.distance( ls ) >= TOLERANCE )
        continue;

      /* Point was intersecting the last segment, now take all coordinates but the last one ... */
      final LinkedList<Coordinate> lineCoords = new LinkedList<>();
      for( int j = 0; j < coords.length - 1; j++ )
        lineCoords.add( coords[j] );

      /* ... and add the point as last one. */
      lineCoords.add( point.getCoordinate() );

      /* Create the new geometry. */
      final LineString tempLine = factory.createLineString( lineCoords.toArray( new Coordinate[] {} ) );

      return tempLine.getLength();
    }

    throw new IllegalStateException();
  }

  /**
   * This function calculates a point at a specific length of a line.
   * 
   * @param lineJTS
   *          The line string on which the point has to be.
   * @param distance
   *          The distance at which the point should be placed on the line.
   * @return The newly created point on the line or null, if something was wrong.
   */
  public static Point pointOnLine( final LineString lineJTS, final double distance )
  {
    final Coordinate crd = posOnLine( lineJTS, distance );
    final GeometryFactory factory = new GeometryFactory( lineJTS.getPrecisionModel(), lineJTS.getSRID() );
    return factory.createPoint( crd );
  }

  /**
   * This function calculates a specific position on a line.
   * 
   * @param lineJTS
   *          The line string on which the point has to be.
   * @param distanceOnLine
   *          The distance at which the point should be placed on the line.
   * @return The newly created position on the line or null, if something was wrong.
   * @deprecated Use LenghtIndexedLine instead.
   */
  @Deprecated
  public static Coordinate posOnLine( final LineString line, final double distanceOnLine )
  {
    final double length = line.getLength();
    if( distanceOnLine < 0 || distanceOnLine > length )
      return null;

    final int numPoints = line.getNumPoints();
    if( numPoints == 0 )
      return null;

    /* Only loop until the point before the last point. */
    /* The remaining distance will be the remaining distance on the found line segment */
    double remainingDistance = distanceOnLine;
    LineSegment segment = null;
    for( int i = 0; i < numPoints - 1; i++ )
    {
      final Point startPoint = line.getPointN( i );
      final Point endPoint = line.getPointN( i + 1 );

      segment = new LineSegment( new Coordinate( startPoint.getCoordinate() ), new Coordinate( endPoint.getCoordinate() ) );
      final double lineLength = segment.getLength();
      if( remainingDistance - lineLength < 0 )
        break;

      remainingDistance -= lineLength;
    }

    if( segment == null )
      return null;

    /* Now calculate the rest of the line. */
    final double max = segment.getLength();
    final Coordinate startPoint = segment.p0;
    final Coordinate endPoint = segment.p1;

    /* If the two X koords are equal, take one of them for the new point. */
    final double x = interpolateX( startPoint.x, endPoint.x, 0, max, remainingDistance );
    final double y = interpolateX( startPoint.y, endPoint.y, 0, max, remainingDistance );
    final double z = interpolateX( startPoint.z, endPoint.z, 0, max, remainingDistance );
    return new Coordinate( x, y, z );
  }

  private static double interpolateX( final double x1, final double x2, final int y1, final double y2, final double y )
  {
    if( Double.isNaN( x1 ) || Double.isNaN( x2 ) )
      return Double.NaN;

    if( Double.compare( x1, x2 ) == 0 )
      return x1;
    else
    {
      try
      {
        final LinearEquation computeX = new LinearEquation( x1, y1, x2, y2 );
        return computeX.computeX( y );
      }
      catch( final SameXValuesException e )
      {
        // should never happen, has we tests this explicitly
        e.printStackTrace();
        return x1;
      }
    }
  }

  public static LineSegment findSegmentInLine( final LineString line, final double distanceOnLine )
  {
    final double length = line.getLength();
    if( distanceOnLine < 0 || distanceOnLine > length )
      return null;

    final int numPoints = line.getNumPoints();
    if( numPoints == 0 )
      return null;

    double currentDistance = 0;
    for( int i = 0; i < numPoints - 1; i++ )
    {
      final Point startPoint = line.getPointN( i );
      final Point endPoint = line.getPointN( i + 1 );

      final double distance = endPoint.distance( startPoint );
      currentDistance += distance;

      if( distanceOnLine < currentDistance )
        return new LineSegment( startPoint.getCoordinate(), endPoint.getCoordinate() );
    }

    return null;
  }

  /**
   * This function calculates a point at a specific length of a line.
   * 
   * @param lineJTS
   *          The line string on which the point has to be.
   * @param percent
   *          The distance in percent at which the point should be placed on the line as Int(!)
   * @return The newly created point on the line or null, if something was wrong. Returns the start point or the end
   *         point if percentage is 0 or 100.
   */
  public static Point pointOnLinePercent( final LineString lineJTS, final double percent )
  {
    if( percent < 0 || percent > 100 )
      return null;

    if( percent == 0 )
      return lineJTS.getPointN( 0 );

    if( percent == 100 )
      return lineJTS.getPointN( lineJTS.getNumPoints() - 1 );

    final double length = lineJTS.getLength();
    final double distance = length / 100.0 * percent;

    return pointOnLine( lineJTS, distance );
  }

  /**
   * This function creates a line segment (JTS) of a line from a given start point to an end point, including all points
   * on the given line.<br>
   * The returned line always starts at <code>start</code> and ends at <code>end</code>, regardless of the orientation
   * of the input line.<br/>
   * The start and end point are projected to the given linear geometry, so do not necessarily need to lie on the given
   * line.<br/>
   * This implementation is based on {@link LengthIndexedLine} of JTS and has the same restraints.
   * 
   * @param line
   *          The original line.
   * @param start
   *          The start point of the new line
   * @param end
   *          The end point of the new line
   * @return A linear geometry (i.e. either a {@link LineString} or a {@link MultiLineString} on the original line
   *         starting at with the start point and ending with the end point. If the input is a {@link LineString}, the
   *         result can be safelay cast to {@link LineString} as well.
   */
  public static Geometry extractLineString( final Geometry linearGeomety, final Point startPoint, final Point endPoint )
  {
    final LengthIndexedLine index = new LengthIndexedLine( linearGeomety );

    final double startIndex = index.project( startPoint.getCoordinate() );
    final double endIndex = index.project( endPoint.getCoordinate() );

    /* make sure orientiation is from start to end */
    final double start = Math.min( startIndex, endIndex );
    final double end = Math.max( startIndex, endIndex );

    return index.extractLine( start, end );
  }

  /**
   * This function creates a line segment (JTS) of a line from a given start point to an end point, including all points
   * on the given line.<br>
   * TODO: The used distance is calculated only by the x- and y-coordinates!! For an 3-dimensaional distance
   * calculation, the start and end point should have z-coordinates.
   * 
   * @param line
   *          The original line.
   * @param start
   *          The start point of the new line (it has to be one point that lies on the original line).
   * @param end
   *          The end point of the new line (it has to be one point that lies on the original line).
   * @return A LineString on the original line starting at with the start point and ending with the end point.
   * @deprecated Use {@link #extractLineString(Geometry, Point, Point)} instead
   */
  @Deprecated
  public static LineString createLineString( final Geometry line, final Point start, final Point end )
  {
    /* Check if both points are lying on the line (2d!). */
    if( line.distance( start ) >= TOLERANCE || line.distance( end ) >= TOLERANCE )
      return null;

    if( line instanceof LineString )
    {
      /* Check the orientation of the line. */
      if( !getLineOrientation( (LineString)line, start, end ) )
        return createLineStringFromLine( (LineString)line, end, start );

      return createLineStringFromLine( (LineString)line, start, end );
    }
    else if( line instanceof MultiLineString )
      return createLineStringFromMultiLine( (MultiLineString)line, start, end );

    return null;
  }

  /**
   * Evaluates the two given points and returns true, if the direction is equal of that from line (its points).
   * 
   * @param line
   *          The original LineString.
   * @param start
   *          The start point of the new line (it has to be one point that lies on the original LineString).
   * @param end
   *          The end point of the new line (it has to be one point that lies on the original LineString).
   * @return True, if the first found point of the line is nearer to the start point, than to the end point.
   */
  public static boolean getLineOrientation( final LineString line, final Point start, final Point end )
  {
    /* Check if both points are lying on the line. */
    if( line.distance( start ) >= TOLERANCE || line.distance( end ) >= TOLERANCE )
      throw new IllegalArgumentException( "One of the two points does not lie on the given line ..." );

    boolean first = false;

    for( int i = 0; i < line.getNumPoints() - 1; i++ )
    {
      final Point pointN = line.getPointN( i );
      final Point pointN1 = line.getPointN( i + 1 );

      /* Build a line with the two points to check the flag. */
      final LineSegment testLine = new LineSegment( new Coordinate( pointN.getCoordinate() ), new Coordinate( pointN1.getCoordinate() ) );

      if( testLine.distance( start.getCoordinate() ) < TOLERANCE )
        first = true;

      if( testLine.distance( end.getCoordinate() ) < TOLERANCE )
      {
        /* The direction is inverse. */
        if( !first )
          return false;

        break;
      }
    }

    return true;
  }

  /**
   * This function creates a LineString (JTS) of a LineString from a given start point to an end point, including all
   * points on the given LineString. However it does not check the orientation of the start and end point. This must be
   * done before calling this method. Use {@link JTSUtilities#getLineOrientation(LineString, Point, Point)} for this
   * operation. Both points should have the same orientation than the line, otherwise the new line has only two points,
   * namly the start and end point.
   * 
   * @param line
   *          The original LineString.
   * @param start
   *          The start point of the new line (it has to be one point that lies on the original LineString).
   * @param end
   *          The end point of the new line (it has to be one point that lies on the original LineString).
   * @return A LineString on the original LineString starting at with the start point and ending with the end point.
   */
  private static LineString createLineStringFromLine( final LineString line, final Point start, final Point end )
  {
    final List<Point> points = new LinkedList<>();

    boolean add = false;

    points.add( start );

    for( int i = 0; i < line.getNumPoints() - 1; i++ )
    {
      final Point pointN = line.getPointN( i );
      final Point pointN1 = line.getPointN( i + 1 );

      if( add )
        points.add( pointN );

      /* Build a line with the two points to check the flag. */
      final LineSegment testLine = new LineSegment( new Coordinate( pointN.getCoordinate() ), new Coordinate( pointN1.getCoordinate() ) );

      if( testLine.distance( start.getCoordinate() ) < TOLERANCE )
        add = true;

      if( testLine.distance( end.getCoordinate() ) < TOLERANCE )
      {
        add = false;
        break;
      }
    }

    points.add( end );

    /* Create the coordinates for the new line string. */
    final Coordinate[] coordinates = new Coordinate[points.size()];

    for( int i = 0; i < points.size(); i++ )
      coordinates[i] = new Coordinate( points.get( i ).getCoordinate() );

    final GeometryFactory factory = line.getFactory();
    return factory.createLineString( coordinates );
  }

  /**
   * This function creates a LineString (JTS) of a MultiLineString from a given start point to an end point, including
   * all points on the given MultiLineString. Gaps between the different LineStrings will be connected in the result, if
   * it should contain more than one LineString of the original MultiLineString. However it does not check the
   * orientation of the start and end point.<br>
   * <br>
   * KNOWN ISSUE:<br>
   * It is possible that this function produce errors, if the start or the end point lies in a gap between two
   * LineStrings. The two points of the gap will not be checked for that point. The result than, will possibly be
   * nonsense. <br>
   * <br>
   * ATTENTION:<br>
   * This class is strange, because creating a LineString part of a MultiLineString should be normally done by
   * dissolving the MultiLineString in one LineString-Object and getting the LineString part of it.<br>
   * There can not be quaranteed, that this function works error free!
   * 
   * @param line
   *          The original MultiLineString.
   * @param start
   *          The start point of the new line (it has to be one point on the original MultiLineString).
   * @param end
   *          The end point of the new line (it has to be one point on the original MultiLineString).
   * @return A LineString on the original MultiLineString starting at with the start point and ending with the end
   *         point.
   */
  private static LineString createLineStringFromMultiLine( final MultiLineString line, final Point start, final Point end )
  {
    final List<Point> points = new LinkedList<>();

    boolean add = false;
    boolean endPointFound = false;

    points.add( start );
    for( int i = 0; i < line.getNumGeometries(); i++ )
    {
      final LineString lineN = (LineString)line.getGeometryN( i );

      for( int j = 0; j < lineN.getNumPoints() - 1; j++ )
      {
        final Point pointN = lineN.getPointN( j );
        final Point pointN1 = lineN.getPointN( j + 1 );

        if( add )
          points.add( pointN );

        /* Build a line with the two points to check the flag. */
        final LineSegment testLine = new LineSegment( new Coordinate( pointN.getCoordinate() ), new Coordinate( pointN1.getCoordinate() ) );

        if( testLine.distance( start.getCoordinate() ) < TOLERANCE )
          add = true;

        if( testLine.distance( end.getCoordinate() ) < TOLERANCE )
        {
          add = false;
          endPointFound = true;
          break;
        }
      }

      if( endPointFound )
        break;
    }

    points.add( end );

    /* Create the coordinates for the new line string. */
    final Coordinate[] coordinates = new Coordinate[points.size()];

    for( int i = 0; i < points.size(); i++ )
      coordinates[i] = new Coordinate( points.get( i ).getCoordinate() );

    final GeometryFactory factory = new GeometryFactory( line.getPrecisionModel(), line.getSRID() );

    return factory.createLineString( coordinates );
  }

  /**
   * This function creates a line segment with the two given points, calculates the length of the line segment and
   * returns the length.
   * 
   * @param pointOne
   *          This point will be used as start point of the line segment.
   * @param pointTwo
   *          This point will be used as end point of the line segment.
   * @return The length of the line between the two points given.
   */
  public static double getLengthBetweenPoints( final Point pointOne, final Point pointTwo )
  {
    return getLengthBetweenPoints( pointOne.getCoordinate(), pointTwo.getCoordinate() );
  }

  /**
   * This function creates a line segment with the two given coordinates, calculates the length of the line segment and
   * returns the length.
   * 
   * @param coordinateOne
   *          This coordinate will be used as start point of the line segment.
   * @param coordinateTwo
   *          This coordinate will be used as end point of the line segment.
   * @return The length of the line between the two coordinates given.
   */
  public static double getLengthBetweenPoints( final Coordinate coordinateOne, final Coordinate coordinateTwo )
  {
    final LineSegment segment = new LineSegment( coordinateOne, coordinateTwo );
    return segment.getLength();
  }

  /** Creates a jts-polygon from a deegree-envelope */
  public static Polygon convertGMEnvelopeToPolygon( final GM_Envelope envelope, final GeometryFactory gf )
  {
    final Coordinate minCoord = JTSAdapter.export( envelope.getMin() );
    final Coordinate maxCoord = JTSAdapter.export( envelope.getMax() );
    final Coordinate tmp1Coord = new Coordinate( minCoord.x, maxCoord.y );
    final Coordinate tmp2Coord = new Coordinate( maxCoord.x, minCoord.y );

    final Coordinate[] coordinates = new Coordinate[] { minCoord, tmp1Coord, maxCoord, tmp2Coord, minCoord };
    final LinearRing linearRing = gf.createLinearRing( coordinates );
    return gf.createPolygon( linearRing, null );
  }

  /** Converts an envelope to a polygon that covers the envelope. */
  public static Polygon convertEnvelopeToPolygon( final Envelope envelope, final GeometryFactory gf )
  {
    final Coordinate minCoord = new Coordinate( envelope.getMinX(), envelope.getMinY() );
    final Coordinate maxCoord = new Coordinate( envelope.getMaxX(), envelope.getMaxY() );
    final Coordinate tmp1Coord = new Coordinate( minCoord.x, maxCoord.y );
    final Coordinate tmp2Coord = new Coordinate( maxCoord.x, minCoord.y );

    final Coordinate[] coordinates = new Coordinate[] { minCoord, tmp1Coord, maxCoord, tmp2Coord, minCoord };
    final LinearRing linearRing = gf.createLinearRing( coordinates );
    return gf.createPolygon( linearRing, null );
  }

  /**
   * @param ring
   *          array of ordered coordinates, last must equal first one
   * @return signed area, area >= 0 means points are counter clockwise defined (mathematic positive)
   */
  public static double calcSignedAreaOfRing( final Coordinate[] ring )
  {
    if( ring.length < 4 ) // 3 points and 4. is repetition of first point
      throw new UnsupportedOperationException( "Can not calculate area of < 3 points ..." );

    final Coordinate a = ring[0]; // base
    double area = 0;
    for( int i = 1; i < ring.length - 2; i++ )
    {
      final Coordinate b = ring[i];
      final Coordinate c = ring[i + 1];

      area += (b.y - a.y) * (a.x - c.x) // bounding rectangle

          - ((a.x - b.x) * (b.y - a.y)//
              + (b.x - c.x) * (b.y - c.y)//
          + (a.x - c.x) * (c.y - a.y)//
          ) / 2d;
    }

    return area;
  }

  /**
   * This function will check all line segments and return the one, in which the given point lies. If no segment is
   * found it will return null.
   * 
   * @param curve
   *          The curve to check.
   * @param point
   *          The point, which marks the segment (e.g. an intersection point of another geometry).
   * @return The line segment or null.
   */
  public static LineSegment findLineSegment( final LineString curve, final Point point )
  {
    for( int i = 0; i < curve.getNumPoints() - 1; i++ )
    {
      final Point pointN = curve.getPointN( i );
      final Point pointN1 = curve.getPointN( i + 1 );

      /* Build a line with the two points to check the intersection. */
      final LineSegment segment = new LineSegment( pointN.getCoordinate(), pointN1.getCoordinate() );

      /* If found, return it. */
      if( segment.distance( point.getCoordinate() ) < TOLERANCE )
        return segment;
    }

    return null;
  }

  /**
   * This function adds points to the line.<br>
   * It will not change the topology of the line in that way, that it changes direction.<br>
   * <br>
   * REMARK:<br>
   * It can be very slow, in dependance of the amount of points to be added.
   * 
   * @param line
   *          The line, to which the points are added to.
   * @param originalPoints
   *          The points, which should be added. The points has to lie on the line.
   * @return The new line as copy of the old line, including the given points. The result may be null.
   */
  public static LineString addPointsToLine( final LineString line, final List<Point> originalPoints )
  {
    return addPointsToLine( line, originalPoints.toArray( new Point[] {} ) );
  }

  public static LineString addPointsToLine( final LineString line, final Point... points )
  {
    final Coordinate[] crds = new Coordinate[points.length];
    for( int i = 0; i < crds.length; i++ )
      crds[i] = new Coordinate( points[i].getCoordinate() );

    return addPointsToLine( line, crds );
  }

  public static LineString addPointsToLine( final LineString line, final Coordinate... locations )
  {
    /* The geometry factory. */
    final GeometryFactory factory = new GeometryFactory( line.getPrecisionModel(), line.getSRID() );

    /* Memory for the new coordinates. */
    final CoordinateList newCoordinates = new CoordinateList();

    /* Get all coordinates. */
    final Coordinate[] lineCoordinates = line.getCoordinates();

    /* Always add the first coordinate. */
    newCoordinates.add( lineCoordinates[0], false );

    final Collection<Coordinate> toIgnore = new HashSet<>();

    /* Only loop until the one before the last one. */
    for( int i = 0; i < lineCoordinates.length - 1; i++ )
    {
      /* Get the coordinates. */
      final Coordinate startCoord = lineCoordinates[i];
      final Coordinate endCoord = lineCoordinates[i + 1];

      /* Create a new line with the coordinates. */
      final LineSegment ls = new LineSegment( startCoord, endCoord );

      /* If no one is intersecting, the current end coordinate has to be added. */
      final List<Coordinate> toAdd = new ArrayList<>();

      for( final Coordinate location : locations )
      {
        if( toIgnore.contains( location ) )
          continue;

        if( ls.distance( location ) < TOLERANCE )
        {
          /* The point intersects, and has to be added. */
          toAdd.add( location );

          /* The points should be removed from the old points list for performance reasons. */
          toIgnore.add( location );
          continue;
        }
      }

      /* Add all points. */
      final List<CoordinatePair> coordinatePairs = getCoordinatePairs( startCoord, toAdd );
      for( final CoordinatePair coordinatePair : coordinatePairs )
        newCoordinates.add( coordinatePair.getSecondCoordinate(), false );

      /* Add the end coordinate. */
      newCoordinates.add( endCoord, false );
    }

    return factory.createLineString( newCoordinates.toCoordinateArray() );
  }

  /**
   * Inverts a given geometry.
   * 
   * @param geometry
   *          The geometry, which should be inverted.
   */
  public static Geometry invert( final Geometry geometry )
  {
    final GeometryFactory factory = new GeometryFactory( geometry.getPrecisionModel(), geometry.getSRID() );

    if( geometry instanceof LineString )
    {
      final LineString lineString = (LineString)geometry;
      final Coordinate[] coordinates = lineString.getCoordinates();

      final Set<Coordinate> myCoordinates = new LinkedHashSet<>();
      for( int i = coordinates.length - 1; i >= 0; i-- )
        myCoordinates.add( coordinates[i] );

      return factory.createLineString( myCoordinates.toArray( new Coordinate[] {} ) );
    }

    throw new UnsupportedOperationException();
  }

  /**
   * This function adds a z-coordinate to each point of a line string. It interpolates the z-coordinate, using the
   * length of the line segment between the start point (parameter start) and the current point. The last point will get
   * the maximum as the z-coordinate (parameter end).
   * 
   * @param lineString
   *          To each point on this line string the z-coordinate will be added.
   * @param start
   *          A start value (a start time, for example).
   * @param end
   *          A end value (an end time, for example).
   * @return A new line string with z-coordinate.
   */
  public static LineString addInterpolatedZCoordinates( final LineString lineString, final double start, final double end ) throws SameXValuesException
  {
    /* Interpolate the times for each points, if time is given. */

    /* List with the coordinates of the new line string. */
    final List<Coordinate> coordinates = new ArrayList<>();

    /* The x-axis represents the distance on the line string. */
    final double x1 = 0.0;
    final double x2 = lineString.getLength();

    /* The y-axis represents the values given by the parameters (a time, for example). */
    final double y1 = start;
    final double y2 = end;

    /* Create the linear equation. */
    final LinearEquation equation = new LinearEquation( x1, y1, x2, y2 );

    for( int i = 0; i < lineString.getNumPoints(); i++ )
    {
      /* Get the points. */
      final Point point = lineString.getPointN( i );

      /* The distance from start to this point. */
      /* If this point is the start, it should be 0. */
      /* If this point is the end, it should be lineString.getLength(). */
      final double distance = pointDistanceOnLine( lineString, point );

      /* Compute the x to this point (needed time to this point, for example). */
      final double x = equation.computeY( distance );

      /* Create the coordinate. */
      final Coordinate coordinate = new Coordinate( point.getX(), point.getY(), x );
      coordinates.add( coordinate );
    }

    /* Create the new line string. */
    final GeometryFactory factory = new GeometryFactory( lineString.getPrecisionModel(), lineString.getSRID() );

    return factory.createLineString( coordinates.toArray( new Coordinate[] {} ) );
  }

  /**
   * This function calculates the center coordinate between two coordinates.
   * 
   * @param coordinate_one
   *          The first coordinate.
   * @param coordinate_two
   *          The second coordinate.
   * @return The center coordinate.
   */
  public static Coordinate getCenterCoordinate( final Coordinate coordinate_one, final Coordinate coordinate_two )
  {
    final double x = (coordinate_one.x + coordinate_two.x) / 2;
    final double y = (coordinate_one.y + coordinate_two.y) / 2;

    return new Coordinate( x, y );
  }

  /**
   * This function collects polygons from polygons (which will return itself in the list) or multi polygons.
   * 
   * @param geometry
   *          The geometry to collect from. If it is no polygon, an empty list will be returned.
   * @return The list of contained polygons or an empty list.
   */
  public static List<Polygon> collectPolygons( final Geometry geometry )
  {
    /* Memory for the results. */
    final List<Polygon> polygons = new ArrayList<>();

    if( !(geometry instanceof Polygon) && !(geometry instanceof MultiPolygon) )
      return polygons;

    if( geometry instanceof Polygon )
    {
      polygons.add( (Polygon)geometry );
      return polygons;
    }

    if( geometry instanceof MultiPolygon )
    {
      final MultiPolygon multi = (MultiPolygon)geometry;
      final int num = multi.getNumGeometries();

      for( int i = 0; i < num; i++ )
      {
        final Geometry geometryN = multi.getGeometryN( i );
        polygons.add( (Polygon)geometryN );
      }
    }

    return polygons;
  }

  /**
   * This function inspects each coordinate of the given array and removes the z-coordinate from it (sets Double.NaN).
   * 
   * @param coordinates
   *          The array of coordinates.
   * @return A new array of new coordinates without the z-coordinate.
   */
  public static Coordinate[] removeZCoordinates( final Coordinate[] coordinates )
  {
    /* Memory for the results. */
    final List<Coordinate> results = new ArrayList<>();

    for( final Coordinate coordinate : coordinates )
      results.add( new Coordinate( coordinate.x, coordinate.y ) );

    return results.toArray( new Coordinate[] {} );
  }

  /**
   * Buffers a geometry.<br>
   * The size of the buffer is determined as a ratio of the size of its envelope.<br>
   * More exact, it is <code>ratio * Math.max(envelope.getWidht(), envelope.getHeight())</code>.
   */
  public static Geometry bufferWithRatio( final Geometry geometry, final double ratio )
  {
    final Envelope envelope = geometry.getEnvelopeInternal();
    final double width = envelope.getWidth();
    final double height = envelope.getHeight();
    final double max = Math.max( width, height );

    return geometry.buffer( max * ratio );
  }

  /**
   * Calculates the fractions some polygons are covering one base geometry (should be a geometry with an area).
   * 
   * @see #fractionAreaOf(Geometry, Polygon)
   */
  public static double[] fractionAreasOf( final Geometry baseGeometry, final Polygon[] coverPolygons )
  {
    final double[] factors = new double[coverPolygons.length];
    for( int i = 0; i < coverPolygons.length; i++ )
      factors[i] = JTSUtilities.fractionAreaOf( baseGeometry, coverPolygons[i] );

    return factors;
  }

  /**
   * Calculates the part (as fraction) of one polygon covering another.
   * 
   * @param baseGeometry
   *          The geometry (should be a geometry with an area), that is covered (by the calculated fraction) by the <code>coverPolygon</code>. May NOT be <code>null</code>.
   * @param coverPolygon
   *          The polygon covering (or not) the basePolygon. My be <code>null</code> (in that case, <code>0.0</code> is
   *          returned).
   */
  public static double fractionAreaOf( final Geometry baseGeometry, final Polygon coverPolygon )
  {
    Assert.isNotNull( baseGeometry );

    if( coverPolygon == null )
      return 0.0;

    /* The intersect function is much faster, than the intersection function. */
    /* So you should use it to check for intersection, even if you need the polygon of the overlapping area. */
    if( !baseGeometry.intersects( coverPolygon ) )
      return 0.0;

    final Geometry intersection = baseGeometry.intersection( coverPolygon );
    if( intersection == null )
      return 0.0;

    final double totalArea = baseGeometry.getArea();
    final double intersectionArea = intersection.getArea();

    return intersectionArea / totalArea;
  }

  public static double getAreaFraction( final Geometry baseGeometry, final Polygon coverPolygon )
  {
    Assert.isNotNull( baseGeometry );

    if( coverPolygon == null )
      return 0.0;

    /* The intersect function is much faster, than the intersection function. */
    /* So you should use it to check for intersection, even if you need the polygon of the overlapping area. */
    if( !baseGeometry.intersects( coverPolygon ) )
      return 0.0;

    final Geometry intersection = baseGeometry.intersection( coverPolygon );
    if( intersection == null )
      return 0.0;

    final double coverArea = coverPolygon.getArea();
    final double intersectionArea = intersection.getArea();

    return intersectionArea / coverArea;
  }

  public static Polygon cleanPolygonInteriorRings( Polygon poly )
  {
    final GeometryFactory factory = new GeometryFactory();

    final List<LinearRing> myInnerRings = new ArrayList<>();
    final int rings = poly.getNumInteriorRing();

    for( int i = 0; i < rings; i++ )
    {
      final LineString lineString = poly.getInteriorRingN( i );
      final Coordinate[] coordinates = lineString.getCoordinates();
      final LinearRing ring = factory.createLinearRing( coordinates );

      final Polygon innerPolygon = factory.createPolygon( ring, null );
      final double area = innerPolygon.getArea();
      if( area > 0.1 )
        myInnerRings.add( ring );
    }

    if( myInnerRings.size() != rings )
    {
      final LineString outer = poly.getExteriorRing();
      final Coordinate[] outerCoordinates = outer.getCoordinates();
      final LinearRing outerRing = factory.createLinearRing( outerCoordinates );

      poly = factory.createPolygon( outerRing, myInnerRings.toArray( new LinearRing[] {} ) );
    }

    return poly;
  }

  public static Geometry[] findGeometriesInRange( final Geometry[] geometries, final Geometry base, final double radius )
  {
    final Set<Geometry> myGeometries = new HashSet<>();

    for( final Geometry geometry : geometries )
    {
      final double distance = base.distance( geometry );
      if( distance <= radius )
        myGeometries.add( geometry );
    }

    return myGeometries.toArray( new Geometry[] {} );
  }

  /**
   * @return coordinates in range sorted by distance
   */
  public static Coordinate[] findCoordinatesInRange( final Coordinate[] coordinates, final Coordinate base, final double radius )
  {
    final Comparator<Coordinate> comparator = new Comparator<Coordinate>()
    {
      @Override
      public int compare( final Coordinate c1, final Coordinate c2 )
      {

        final Double d1 = Double.valueOf( c1.distance( base ) );
        final Double d2 = Double.valueOf( c2.distance( base ) );

        return d1.compareTo( d2 );
      }
    };

    final Set<Coordinate> myCoordinates = new TreeSet<>( comparator );

    for( final Coordinate c : coordinates )
    {
      final double distance = base.distance( c );
      if( distance <= radius )
        myCoordinates.add( c );
    }

    return myCoordinates.toArray( new Coordinate[] {} );
  }

  /**
   * This function returns the minimal x-value of a sequence of coordinates.
   * 
   * @param seq
   *          The coordinate sequence.
   * @return The minimal x-value of a sequence of coordinates. {@link Double#POSITIVE_INFINITY} If the sequence is
   *         empty.
   */
  public static double getMinX( final CoordinateSequence seq )
  {
    double min = Double.POSITIVE_INFINITY;
    for( int i = 0; i < seq.size(); i++ )
      min = Math.min( min, seq.getX( i ) );

    return min;
  }

  /**
   * This function returns the maximal x-value of a sequence of coordinates.
   * 
   * @param seq
   *          The coordinate sequence.
   * @return The maximal x-value of a sequence of coordinates. {@link Double#NEGATIVE_INFINITY} If the sequence is
   *         empty.
   */
  public static double getMaxX( final CoordinateSequence seq )
  {
    double max = Double.NEGATIVE_INFINITY;
    for( int i = 0; i < seq.size(); i++ )
      max = Math.max( max, seq.getX( i ) );

    return max;
  }

  /**
   * This function returns all x-values of the given sequence as an array.
   * 
   * @param seq
   *          The coordinate sequence.
   * @return All x-values of the given sequence as an array.
   */
  public static double[] getXValues( final CoordinateSequence seq )
  {
    final double[] x = new double[seq.size()];
    for( int i = 0; i < seq.size(); i++ )
      x[i] = seq.getX( i );

    return x;
  }

  /**
   * This function returns all y-values of the given sequence as an array.
   * 
   * @param seq
   *          The coordinate sequence.
   * @return All y-values of the given sequence as an array.
   */
  public static double[] getYValues( final CoordinateSequence seq )
  {
    final double[] y = new double[seq.size()];
    for( int i = 0; i < seq.size(); i++ )
      y[i] = seq.getY( i );

    return y;
  }

  /**
   * This function validates geometries.
   * 
   * @param msg
   *          Basic error message.
   * @param g
   *          Geometries to check.
   * @return If an error exists an error message will be returned.
   */
  public static String validateGeometries( String msg, final Geometry... g )
  {
    boolean error = false;
    for( final Geometry geometry : g )
    {
      final IsValidOp isValidOp = new IsValidOp( geometry );
      final TopologyValidationError validationError = isValidOp.getValidationError();
      if( validationError != null )
      {
        msg += String.format( " Error: %s", validationError.getMessage() );
        error = true;
      }
    }

    if( error )
      return msg;

    return null;
  }

  /**
   * This function adds z coordinates to the given geometry, using the inverse distance weighting on a list of points
   * with z coordinates.
   * 
   * @param geometry
   *          The geometry for which the z coordinates should be added.
   * @param points
   *          The points with z coordinates, which will be used for the inverse distance weighting.
   * @param numberOfPoints
   *          The number of the nearest points of the list, that will be used in the distance weighting. If this
   *          parameter is <= 0, all points will be used.
   * @return A new geometry with x, y and z coordinates.
   */
  public static Geometry addZCoordinates( final Geometry geometry, final List<Coordinate> points, final int numberOfPoints )
  {
    /* Check the prerequisites. */
    if( geometry == null )
      throw new IllegalArgumentException( "No geometry given ..." );

    /* Check the prerequisites. */
    if( points == null || points.size() == 0 )
      throw new IllegalArgumentException( "No points with z coordinates given ..." );

    /* Create the geometry factory. */
    final GeometryFactory factory = new GeometryFactory();

    /* Clone the geometry. */
    final Geometry newGeometry = factory.createGeometry( geometry );

    /* Modify the coordinates of the new geometry. */
    final Coordinate[] coordinates = newGeometry.getCoordinates();
    for( final Coordinate coordinate : coordinates )
    {
      /* Now add a z coordinate to the coordinate. */
      addZCoordinate( coordinate, points, numberOfPoints );
    }

    /* Tell the new geometry, that it has changed. */
    newGeometry.geometryChanged();

    return newGeometry;
  }

  /**
   * This function adds a z coordinate to the given point, using the inverse distance weighting on a list of points with
   * z coordinates.
   * 
   * @param coordinate
   *          The coordinate for which the z coordinates should be added.
   * @param points
   *          The points with z coordinates, which will be used for the inverse distance weighting.
   * @param numberOfPoints
   *          The number of the nearest points of the list, that will be used in the distance weighting. If this
   *          parameter is <= 0, all points will be used.
   */
  private static void addZCoordinate( final Coordinate coordinate, final List<Coordinate> points, final int numberOfPoints )
  {
    /* Check the prerequisites. */
    if( coordinate == null )
      throw new IllegalArgumentException( "No geometry given ..." );

    /* Check the prerequisites. */
    if( points == null || points.size() == 0 )
      throw new IllegalArgumentException( "No points with z coordinates given ..." );

    /* Get the coordinate pairs. */
    final List<CoordinatePair> coordinatePairs = getCoordinatePairs( coordinate, points );

    /* The sum of all distances. */
    double sumDistances = 0.0;

    /* Calculate the distances. */
    final List<Double> distances = new ArrayList<>();
    for( int i = 0; i < coordinatePairs.size(); i++ )
    {
      if( numberOfPoints > 0 && i >= numberOfPoints )
        break;

      /* Get the coordinate pair. */
      final CoordinatePair coordinatePair = coordinatePairs.get( i );

      final double d = coordinatePair.getDistance();
      if( 0.0 == d )
      {
        final Coordinate second = coordinatePair.getSecondCoordinate();
        coordinate.z = second.z;

        return;
      }

      /* Get the distance of the coordinate to the coordinate of the point. */
      final double distance = 1 / d;

      /* First add it to the sum of distances. */
      sumDistances = sumDistances + distance;

      /* Then add it to the list of distances. */
      distances.add( new Double( distance ) );
    }

    /* Calculate the factors. */
    final List<Double> factors = new ArrayList<>();
    for( int i = 0; i < distances.size(); i++ )
    {
      /* Get the distance. */
      final Double distance = distances.get( i );

      /* Calculate the factor. */
      final double factor = distance.doubleValue() / sumDistances;

      /* Add it to the list of factors. */
      factors.add( new Double( factor ) );
    }

    /* Now calculate the new z coordinate. */
    double newZ = 0.0;
    for( int i = 0; i < coordinatePairs.size(); i++ )
    {
      if( numberOfPoints > 0 && i >= numberOfPoints )
        break;

      /* Get the coordinate pair. */
      final CoordinatePair coordinatePair = coordinatePairs.get( i );

      /* Get the factor. */
      final Double factor = factors.get( i );

      /* Calculate the new z coordinate. */
      newZ = newZ + coordinatePair.getSecondCoordinate().z * factor.doubleValue();
    }

    /* Set the new z coordinate. */
    coordinate.z = newZ;
  }

  /**
   * This function returns a list of coordinates pairs. The first coordinate of a pair will be always the parameter
   * coordinate and the second coordinate of a pair will be a coordinate of one point of the list. The list will be
   * sorted by the distance, each pair has.
   * 
   * @param coordinate
   *          The coordinate.
   * @param points
   *          The list of points.
   * @return A list of coordinate pairs. The first coordinate of a pair will be always the parameter coordinate, and the
   *         second coordinate of a pair will be a coordinate of one point of the list. The list will be sorted by the
   *         distance, each pair has.
   */
  public static List<CoordinatePair> getCoordinatePairs( final Coordinate coordinate, final List<Coordinate> points )
  {
    /* Memory for the results. */
    final List<CoordinatePair> results = new ArrayList<>();

    for( int i = 0; i < points.size(); i++ )
    {
      /* Get the point. */
      final Coordinate point = points.get( i );

      /* Create the coordinate pair. */
      final CoordinatePair coordinatePair = new CoordinatePair( coordinate, point );

      /* Add to the results. */
      results.add( coordinatePair );
    }

    /* Sort the results. */
    Collections.sort( results, null );

    return results;
  }

  /**
   * This function returns the nearest point on the line within a distance of the provided point.
   * 
   * @param line
   *          The points on this line will be evaluated.
   * @param point
   *          This is the reference point.
   * @param distance
   *          The nearest point of line segment, the point is in must lie within the given distance. If you provide a
   *          invalid distance such as {@link Double#NaN}, {@link Double#NEGATIVE_INFINITY}, {@link Double#POSITIVE_INFINITY} or a negative number, a default distance ({@link #TOLERANCE} =
   *          {@value #TOLERANCE}) will be used.
   * @return The point, if one could be found or null.
   */
  public static Point findPointInLine( final LineString line, final Point point, double distance )
  {
    /* Check for intersection. */
    if( point.distance( line ) >= TOLERANCE )
      throw new IllegalStateException( "The point does not lie on the line ..." );

    /* Check the distance. */
    if( Double.isNaN( distance ) || Double.isInfinite( distance ) )
      distance = TOLERANCE;

    /* Find the line segment, this point is in. */
    final LineSegment segment = findLineSegment( line, point );
    if( segment == null )
      return null;

    /* Check the distance to the reference point. */
    final Coordinate referenceCoordinate = point.getCoordinate();
    final double distance0 = segment.getCoordinate( 0 ).distance( referenceCoordinate );
    final double distance1 = segment.getCoordinate( 1 ).distance( referenceCoordinate );
    Coordinate closestCoordinate = null;
    if( distance0 <= distance1 )
      closestCoordinate = segment.getCoordinate( 0 );
    else
      closestCoordinate = segment.getCoordinate( 1 );

    if( closestCoordinate.distance( referenceCoordinate ) <= distance )
    {
      final GeometryFactory factory = new GeometryFactory( line.getPrecisionModel(), line.getSRID() );
      return factory.createPoint( closestCoordinate );
    }

    return null;
  }

  /**
   * This function finds the points via the NEAREST rule. Method was copied from InformDSS class AbstractGeoMeasure
   * 
   * @return The list of affected points. Always with size = 2.
   */
  public static Point findNearestProjectionPoints( final Polygon polygone, final Point point )
  {
    final Coordinate base = point.getCoordinate();

    double distance = Double.MAX_VALUE;
    Coordinate ptr = null;

    /* Get the exterior ring. */
    final LineString ring = polygone.getExteriorRing();
    final Coordinate[] coordinates = ring.getCoordinates();

    for( final Coordinate coordinate : coordinates )
    {
      final double d = coordinate.distance( base );
      if( d < distance )
      {
        ptr = coordinate;
        distance = d;
      }
    }

    if( Objects.isNull( ptr ) )
      return null;

    final GeometryFactory factory = new GeometryFactory( point.getPrecisionModel(), point.getSRID() );
    return factory.createPoint( ptr );
  }

  public static Coordinate[] replace( final Coordinate[] coordinates, final Coordinate old, final Coordinate set )
  {
    final List<Coordinate> replaced = new ArrayList<>();
    for( final Coordinate coordinate : coordinates )
    {
      if( coordinate.equals( old ) )
        replaced.add( set );
      else
        replaced.add( coordinate );
    }

    return replaced.toArray( new Coordinate[] {} );
  }

  public static double distanceZ( final Coordinate c1, final Coordinate c2 )
  {
    if( Double.isNaN( c1.z ) || Double.isNaN( c2.z ) )
      throw new IllegalStateException();

    final double dx = c1.x - c2.x;
    final double dy = c1.y - c2.y;
    final double dz = c1.z - c2.z;

    return Math.sqrt( dx * dx + dy * dy + dz * dz );
  }

  public static Polygon interpolateMissingZ( final Polygon input )
  {
    final GeometryFactory geomFactory = input.getFactory();
    final LineString exteriorRing = input.getExteriorRing();
    final LinearRing exteriorZ = geomFactory.createLinearRing( interpolateMissingZ( exteriorRing ).getCoordinates() );
    final LinearRing[] interiorZ = new LinearRing[input.getNumInteriorRing()];
    for( int i = 0; i < input.getNumInteriorRing(); i++ )
    {
      final LineString interiorRing = input.getInteriorRingN( i );
      interiorZ[i] = geomFactory.createLinearRing( interpolateMissingZ( interiorRing ).getCoordinates() );
    }
    return geomFactory.createPolygon( exteriorZ, interiorZ );
  }

  /**
   * Interpolate missing z values for all vertices from vertices that already have a z value.<br/>
   * For each vertex, the z value is interpolated from the two nearest adjacent vertices with z values. If only one
   * neighbour has a z value, it is directly copied.
   */
  public static LineString interpolateMissingZ( final LineString input )
  {
    final LengthIndexedLine lengthIndex = new LengthIndexedLine( input );

    final Coordinate[] coordinates = input.getCoordinates();

    /* a create mapping distance - z for later interpolation */
    final Collection<Double> distancesWithZsList = new ArrayList<>( coordinates.length );

    final Collection<Double> zsList = new ArrayList<>( coordinates.length );

    for( final Coordinate vertex : coordinates )
    {
      if( !Double.isNaN( vertex.z ) )
      {
        final double distance = lengthIndex.indexOf( vertex );
        distancesWithZsList.add( distance );
        zsList.add( vertex.z );
      }
    }

    // nothing to do if there are no z values anywhere
    if( zsList.isEmpty() )
      return input;

    final Double[] distances = distancesWithZsList.toArray( new Double[distancesWithZsList.size()] );
    final Double[] zs = zsList.toArray( new Double[zsList.size()] );

    /* create a polyline for interpolation of missing z's */
    final PolyLine interpolator = new PolyLine( distances, zs, TOLERANCE );

    /* last step: interpolate missing z's via interpolator */
    final Coordinate[] coordinatesWithZ = new Coordinate[coordinates.length];

    for( int i = 0; i < coordinatesWithZ.length; i++ )
    {
      final Coordinate vertex = coordinates[i];

      if( Double.isNaN( vertex.z ) )
      {
        final double distance = lengthIndex.indexOf( vertex );
        final double interpolatedZ = interpolator.getYFor( distance, false );
        coordinatesWithZ[i] = new Coordinate( vertex.x, vertex.y, interpolatedZ );
      }
      else
        coordinatesWithZ[i] = new Coordinate( vertex );
    }

    /* create and return new line string */
    final LineString output = input.getFactory().createLineString( coordinatesWithZ );
    output.setSRID( input.getSRID() );
    return output;
  }

  /**
   * Removes points closer to
   */
  public static Polygon removeCoincidentPoints( final Polygon polygon, final double nearDistance, final double distanceTolerance )
  {
    final LinearRing exteriorRing = (LinearRing)polygon.getExteriorRing();
    final LinearRing newExteriorRing = removeCoincidentPoints( exteriorRing, nearDistance, distanceTolerance );
    final int ringCount = polygon.getNumInteriorRing();
    final LinearRing[] newInteriorRings = new LinearRing[ringCount];
    for( int i = 0; i < ringCount; i++ )
      newInteriorRings[i] = removeCoincidentPoints( (LinearRing)polygon.getInteriorRingN( i ), nearDistance, distanceTolerance );
    return polygon.getFactory().createPolygon( newExteriorRing, newInteriorRings );
  }

  /**
   * Removes interior points of this line that are within the given distance from another point
   */
  @SuppressWarnings( "unchecked" )
  public static <T extends LineString> T removeCoincidentPoints( final T linestring, final double nearDistance, final double distanceTolerance )
  {
    final GeometryEditor editor = new GeometryEditor();
    final Geometry result = editor.edit( linestring, new GeometryEditor.CoordinateOperation()
    {

      @Override
      public Coordinate[] edit( final Coordinate[] coordinates, final Geometry geometry )
      {
        final CoordinateList coordList = new CoordinateList();
        coordList.add( coordinates[0] );
        for( int i = 1; i < coordinates.length - 1; i++ )
        {
          final Coordinate p0 = coordList.getCoordinate( coordList.size() - 1 );
          final Coordinate p1 = coordinates[i];
          final Coordinate p2 = coordinates[i + 1];
          if( p1.distance( p0 ) <= nearDistance || p1.distance( p2 ) <= nearDistance )
          {
            final boolean nearlyColinear = new LineSegment( p0, p2 ).distance( p1 ) < distanceTolerance;
            if( nearlyColinear )
              // gobble
              continue;
          }
          coordList.add( p1 );
        }
        coordList.add( coordinates[coordinates.length - 1] );
        return coordList.toCoordinateArray();
      }
    } );
    return (T)result;
  }

  public static com.infomatiq.jsi.Rectangle toRectangle( final Envelope envelope )
  {
    final float x1 = MathUtils.floorFloat( envelope.getMinX() );
    final float y1 = MathUtils.floorFloat( envelope.getMinY() );

    final float x2 = MathUtils.ceilFloat( envelope.getMaxX() );
    final float y2 = MathUtils.ceilFloat( envelope.getMaxY() );

    return new Rectangle( x1, y1, x2, y2 );
  }

  public static Rectangle toRectangle( final double x, final double y )
  {
    final float x1 = MathUtils.floorFloat( x );
    final float y1 = MathUtils.floorFloat( y );
    final float x2 = MathUtils.ceilFloat( x );
    final float y2 = MathUtils.ceilFloat( y );

    return new Rectangle( x1, y1, x2, y2 );
  }
}