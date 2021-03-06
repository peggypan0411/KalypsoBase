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

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.ByteUtils;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_CurveSegment;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_MultiCurve;
import org.kalypsodeegree.model.geometry.GM_MultiGeometry;
import org.kalypsodeegree.model.geometry.GM_MultiPoint;
import org.kalypsodeegree.model.geometry.GM_MultiSurface;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.model.geometry.GM_PolygonPatch;
import org.kalypsodeegree.model.geometry.GM_PolyhedralSurface;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree.model.geometry.GM_Rectangle;
import org.kalypsodeegree.model.geometry.GM_Ring;
import org.kalypsodeegree.model.geometry.GM_Triangle;
import org.kalypsodeegree.model.geometry.GM_TriangulatedSurface;

/**
 * <p>
 * ------------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version $Revision$ $Date$
 */
final public class GeometryFactory
{
  /**
   * creates a GM_Envelope object out from two corner coordinates
   */
  public static GM_Envelope createGM_Envelope( final double x1, final double y1, final double x2, final double y2, final String coordinateSystem )
  {
    return new GM_Envelope_Impl( x1, y1, x2, y2, coordinateSystem );
  }

  /**
   * creates a GM_Envelope object out from two corner coordinates
   */
  public static GM_Envelope createGM_Envelope( final GM_Position min, final GM_Position max, final String coordinateSystem )
  {
    return new GM_Envelope_Impl( min, max, coordinateSystem );
  }

  /**
   * creates a GM_Position from two coordinates.
   */
  public static GM_Position createGM_Position( final double x, final double y )
  {
    return new GM_Position2D_Impl( x, y );
  }

  /**
   * creates a GM_Position from three coordinates.
   */
  public static GM_Position createGM_Position( final double x, final double y, final double z )
  {
    if( Double.isNaN( z ) )
      return new GM_Position2D_Impl( x, y );

    return new GM_Position3D_Impl( x, y, z );
  }

  /**
   * creates a GM_Position from an array of double.
   */
  public static GM_Position createGM_Position( final double[] p )
  {
    if( p.length == 2 )
      return new GM_Position2D_Impl( p[0], p[1] );

    if( p.length == 3 )
      return new GM_Position3D_Impl( p[0], p[1], p[2] );

    // FIXME;
// return new GM_Position_Impl( p );
    throw new UnsupportedOperationException();
  }

  /**
   * creates a GM_Point from two coordinates.
   */
  public static GM_Point createGM_Point( final double x, final double y, final String crs )
  {
    return new GM_Point_Impl( x, y, crs );
  }

  /**
   * creates a GM_Point from three coordinates.
   */
  public static GM_Point createGM_Point( final double x, final double y, final double z, final String crs )
  {
    return new GM_Point_Impl( x, y, z, crs );
  }

  /**
   * creates a GM_Point from a position.
   */
  public static GM_Point createGM_Point( final GM_Position position, final String crs )
  {
    return new GM_Point_Impl( position, crs );
  }

  /**
   * creates a GM_Point from a wkb.
   */
  public static GM_Point createGM_Point( final byte[] wkb, final String srs ) throws GM_Exception
  {
    int wkbType = -1;
    double x = 0;
    double y = 0;

    final byte byteorder = wkb[0];

    if( byteorder == 0 )
    {
      wkbType = ByteUtils.readBEInt( wkb, 1 );
    }
    else
    {
      wkbType = ByteUtils.readLEInt( wkb, 1 );
    }

    if( wkbType != 1 )
    {
      throw new GM_Exception( "invalid byte stream" );
    }

    if( byteorder == 0 )
    {
      x = ByteUtils.readBEDouble( wkb, 5 );
      y = ByteUtils.readBEDouble( wkb, 13 );
    }
    else
    {
      x = ByteUtils.readLEDouble( wkb, 5 );
      y = ByteUtils.readLEDouble( wkb, 13 );
    }

    return new GM_Point_Impl( x, y, srs );
  }

  /**
   * creates a GM_CurveSegment from an array of points.
   * 
   * @param points
   *          array of GM_Point
   * @param crs
   *          spatial reference system of the curve
   */
  public static GM_CurveSegment createGM_CurveSegment( final GM_Position[] points, final String crs ) throws GM_Exception
  {
    return new GM_LineString_Impl( points, crs );
  }

  /**
   * creates a GM_Curve from an array of GM_Positions.
   * 
   * @param positions
   *          positions
   * @param crs
   *          geometries coordinate reference system
   */
  public static GM_Curve createGM_Curve( final GM_Position[] positions, final String crs ) throws GM_Exception
  {
    final GM_CurveSegment[] cs = new GM_CurveSegment[1];
    cs[0] = GeometryFactory.createGM_CurveSegment( positions, crs );
    return new GM_Curve_Impl( cs );
  }

  /**
   * creates a GM_Curve from one curve segment.
   * 
   * @param segment
   *          GM_CurveSegments
   */
  public static GM_Curve createGM_Curve( final GM_CurveSegment segment ) throws GM_Exception
  {
    return new GM_Curve_Impl( new GM_CurveSegment[] { segment } );
  }

  /**
   * creates a GM_Curve from an array of curve segments.
   * 
   * @param segments
   *          array of GM_CurveSegments
   */
  public static GM_Curve createGM_Curve( final GM_CurveSegment[] segments ) throws GM_Exception
  {
    return new GM_Curve_Impl( segments );
  }

  /**
   * creates a GM_Curve from an array of ordinates
   */
  public static GM_Curve createGM_Curve( final double[] ord, final int dim, final String crs ) throws GM_Exception
  {
    final GM_Position[] pos = new GM_Position[ord.length / dim];
    int i = 0;
    int k = 0;
    while( i < ord.length )
    {
      final double[] o = new double[dim];
      for( int j = 0; j < dim; j++ )
      {
        o[j] = ord[i++];
      }
      pos[k++] = GeometryFactory.createGM_Position( o );
    }
    return GeometryFactory.createGM_Curve( pos, crs );
  }

  /**
   * creates a GM_SurfacePatch from array(s) of GM_Position
   * 
   * @param exteriorRing
   *          exterior ring of the patch
   * @param interiorRings
   *          interior rings of the patch
   * @param si
   *          GM_SurfaceInterpolation
   * @param crs
   *          spatial reference system of the surface patch
   */
  public static GM_PolygonPatch createGM_PolygonPatch( final GM_Position[] exteriorRing, final GM_Position[][] interiorRings, final String crs ) throws GM_Exception
  {
    return new GM_PolygonPatch_Impl( exteriorRing, interiorRings, crs );
  }

  public static GM_PolygonPatch createGM_PolygonPatch( final double[] exterior, final double[][] interior, final int dim, final String crs ) throws GM_Exception
  {
    final GM_Position[] ext = positionsFromDoubles( exterior, dim );
    final GM_Position[][] in;
    if( interior == null || interior.length == 0 )
      in = null;
    else
    {
      in = new GM_Position[interior.length][];
      for( int j = 0; j < in.length; j++ )
        in[j] = positionsFromDoubles( interior[j], dim );
    }

    return createGM_PolygonPatch( ext, in, crs );
  }

  public static GM_PolygonPatch createGM_PolygonPatch( final GM_Ring exterior, final GM_Ring[] interior, final String crs ) throws GM_Exception
  {
    final GM_Position[] ext = exterior.getPositions();
    final GM_Position[][] in;
    if( interior == null || interior.length == 0 )
      in = null;
    else
    {
      in = new GM_Position[interior.length][];
      for( int j = 0; j < in.length; j++ )
        in[j] = interior[j].getPositions();
    }

    return createGM_PolygonPatch( ext, in, crs );
  }

  /**
   * creates a GM_Curve from a wkb.
   * 
   * @param wkb
   *          byte stream that contains the wkb information
   * @param crs
   *          spatial reference system of the curve
   */
  public static GM_Curve createGM_Curve( final byte[] wkb, final String crs ) throws GM_Exception
  {
    int wkbType = -1;
    int numPoints = -1;
    GM_Position[] points = null;
    double x = 0;
    double y = 0;

    final byte byteorder = wkb[0];

    if( byteorder == 0 )
    {
      wkbType = ByteUtils.readBEInt( wkb, 1 );
    }
    else
    {
      wkbType = ByteUtils.readLEInt( wkb, 1 );
    }

    // check if it's realy a linestrin/curve
    if( wkbType != 2 )
    {
      throw new GM_Exception( "invalid byte stream for GM_Curve" );
    }

    // read number of points
    if( byteorder == 0 )
    {
      numPoints = ByteUtils.readBEInt( wkb, 5 );
    }
    else
    {
      numPoints = ByteUtils.readLEInt( wkb, 5 );
    }

    int offset = 9;

    points = new GM_Position[numPoints];

    // read the i-th point depending on the byteorde
    if( byteorder == 0 )
    {
      for( int i = 0; i < numPoints; i++ )
      {
        x = ByteUtils.readBEDouble( wkb, offset );
        offset += 8;
        y = ByteUtils.readBEDouble( wkb, offset );
        offset += 8;
        points[i] = new GM_Position2D_Impl( x, y );
      }
    }
    else
    {
      for( int i = 0; i < numPoints; i++ )
      {
        x = ByteUtils.readLEDouble( wkb, offset );
        offset += 8;
        y = ByteUtils.readLEDouble( wkb, offset );
        offset += 8;
        points[i] = new GM_Position2D_Impl( x, y );
      }
    }

    final GM_CurveSegment[] segment = new GM_CurveSegment[1];

    segment[0] = GeometryFactory.createGM_CurveSegment( points, crs );

    return GeometryFactory.createGM_Curve( segment );
  }

  /**
   * creates a GM_Surface composed of one GM_SurfacePatch from array(s) of GM_Position
   * 
   * @param exteriorRing
   *          exterior ring of the patch
   * @param interiorRings
   *          interior rings of the patch
   * @param si
   *          GM_SurfaceInterpolation
   * @param crs
   *          spatial reference system of the surface patch
   */
  public static GM_Polygon createGM_Surface( final GM_Position[] exteriorRing, final GM_Position[][] interiorRings, final String crs ) throws GM_Exception
  {
    final GM_PolygonPatch sp = new GM_PolygonPatch_Impl( exteriorRing, interiorRings, crs );
    return new GM_Polygon_Impl( sp );
  }

  /**
   * creates a GM_Surface from an array of GM_SurfacePatch.
   * 
   * @param patch
   *          patches that build the surface
   */
  public static GM_Polygon createGM_Surface( final GM_PolygonPatch patch ) throws GM_Exception
  {
    return new GM_Polygon_Impl( patch );
  }

  /**
   * creates a GM_Surface from a wkb.
   * 
   * @param wkb
   *          byte stream that contains the wkb information
   * @param crs
   *          spatial reference system of the curve
   * @param si
   *          GM_SurfaceInterpolation
   */
  public static GM_Polygon createGM_Surface( final byte[] wkb, final String crs ) throws GM_Exception
  {
    int wkbtype = -1;
    int numRings = 0;
    int numPoints = 0;
    int offset = 0;
    double x = 0;
    double y = 0;

    GM_Position[] externalBoundary = null;
    GM_Position[][] internalBoundaries = null;

    final byte byteorder = wkb[offset++];

    if( byteorder == 0 )
    {
      wkbtype = ByteUtils.readBEInt( wkb, offset );
    }
    else
    {
      wkbtype = ByteUtils.readLEInt( wkb, offset );
    }

    offset += 4;

    if( wkbtype == 6 )
    {
      return null;
    }

    // is the geometry respresented by wkb a polygon?
    if( wkbtype != 3 )
    {
      throw new GM_Exception( "invalid byte stream for GM_Surface " + wkbtype );
    }

    // read number of rings of the polygon
    if( byteorder == 0 )
    {
      numRings = ByteUtils.readBEInt( wkb, offset );
    }
    else
    {
      numRings = ByteUtils.readLEInt( wkb, offset );
    }

    offset += 4;

    // read number of points of the external ring
    if( byteorder == 0 )
    {
      numPoints = ByteUtils.readBEInt( wkb, offset );
    }
    else
    {
      numPoints = ByteUtils.readLEInt( wkb, offset );
    }

    offset += 4;

    // allocate memory for the external boundary
    externalBoundary = new GM_Position[numPoints];

    if( byteorder == 0 )
    {
      // read points of the external boundary from the byte[]
      for( int i = 0; i < numPoints; i++ )
      {
        x = ByteUtils.readBEDouble( wkb, offset );
        offset += 8;
        y = ByteUtils.readBEDouble( wkb, offset );
        offset += 8;
        externalBoundary[i] = new GM_Position2D_Impl( x, y );
      }
    }
    else
    {
      // read points of the external boundary from the byte[]
      for( int i = 0; i < numPoints; i++ )
      {
        x = ByteUtils.readLEDouble( wkb, offset );
        offset += 8;
        y = ByteUtils.readLEDouble( wkb, offset );
        offset += 8;
        externalBoundary[i] = new GM_Position2D_Impl( x, y );
      }
    }

    // only if numRings is larger then one there internal rings
    if( numRings > 1 )
    {
      internalBoundaries = new GM_Position[numRings - 1][];
    }

    if( byteorder == 0 )
    {
      for( int j = 1; j < numRings; j++ )
      {
        // read number of points of the j-th internal ring
        numPoints = ByteUtils.readBEInt( wkb, offset );
        offset += 4;

        // allocate memory for the j-th internal boundary
        internalBoundaries[j - 1] = new GM_Position[numPoints];

        // read points of the external boundary from the byte[]
        for( int i = 0; i < numPoints; i++ )
        {
          x = ByteUtils.readBEDouble( wkb, offset );
          offset += 8;
          y = ByteUtils.readBEDouble( wkb, offset );
          offset += 8;
          internalBoundaries[j - 1][i] = new GM_Position2D_Impl( x, y );
        }
      }
    }
    else
    {
      for( int j = 1; j < numRings; j++ )
      {
        // read number of points of the j-th internal ring
        numPoints = ByteUtils.readLEInt( wkb, offset );
        offset += 4;

        // allocate memory for the j-th internal boundary
        internalBoundaries[j - 1] = new GM_Position[numPoints];

        // read points of the external boundary from the byte[]
        for( int i = 0; i < numPoints; i++ )
        {
          x = ByteUtils.readLEDouble( wkb, offset );
          offset += 8;
          y = ByteUtils.readLEDouble( wkb, offset );
          offset += 8;
          internalBoundaries[j - 1][i] = new GM_Position2D_Impl( x, y );
        }
      }
    }

    final GM_PolygonPatch patch = GeometryFactory.createGM_PolygonPatch( externalBoundary, internalBoundaries, crs );

    return GeometryFactory.createGM_Surface( patch );
  }

  /**
   * Creates a <tt>GM_Surface</tt> from a <tt>GM_Envelope</tt>.
   * <p>
   * 
   * @param bbox
   *          envelope to be converted
   * @param crs
   *          spatial reference system of the surface
   * @return corresponding surface
   * @throws GM_Exception
   */
  public static GM_Polygon createGM_Surface( final GM_Envelope bbox, final String crs ) throws GM_Exception
  {
    final GM_Position min = bbox.getMin();
    final GM_Position max = bbox.getMax();

    final GM_Position[] exteriorRing = new GM_Position[] { min, new GM_Position2D_Impl( max.getX(), min.getY() ), max, new GM_Position2D_Impl( min.getX(), max.getY() ), min };

    return GeometryFactory.createGM_Surface( exteriorRing, null, crs );
  }

  /**
   * Creates a <tt>GM_Surface</tt> from the ordinates of the exterior ring and the the interior rings
   * <p>
   * 
   * @param crs
   *          spatial reference system of the surface
   * @return corresponding surface
   * @throws GM_Exception
   */
  public static GM_Polygon createGM_Surface( final double[] exterior, final double[][] interior, final int dim, final String crs ) throws GM_Exception
  {
    // get exterior ring
    final GM_Position[] ext = positionsFromDoubles( exterior, dim );

    // get interior rings if available
    final GM_Position[][] in;
    if( interior == null || interior.length == 0 )
      in = null;
    else
    {
      in = new GM_Position[interior.length][];
      for( int j = 0; j < in.length; j++ )
        in[j] = positionsFromDoubles( interior[j], dim );
    }

    return GeometryFactory.createGM_Surface( ext, in, crs );
  }

  private static GM_Position[] positionsFromDoubles( final double[] exterior, final int dim )
  {
    final GM_Position[] poses = new GM_Position[exterior.length / dim];
    for( int i = 0; i < poses.length; i++ )
    {
      final double[] o = new double[dim];
      for( int j = 0; j < dim; j++ )
        o[j] = exterior[i * dim + j];

      poses[i] = GeometryFactory.createGM_Position( o );
    }

    return poses;
  }

  /**
   * creates a GM_MultiPoint from an array of GM_Point.
   * 
   * @param points
   *          array of GM_Points
   */
  public static GM_MultiPoint createGM_MultiPoint( final GM_Point[] points )
  {
    return new GM_MultiPoint_Impl( points );
  }

  public static GM_MultiPoint createGM_MultiPoint( final GM_Point[] points, final String crs )
  {
    return new GM_MultiPoint_Impl( points, crs );
  }

  /**
   * creates a GM_MultiPoint from a wkb.
   * 
   * @param wkb
   *          byte stream that contains the wkb information
   * @param crs
   *          spatial reference system of the curve
   */
  public static GM_MultiPoint createGM_MultiPoint( final byte[] wkb, final String crs ) throws GM_Exception
  {
    GM_Point[] points = null;
    int wkbType = -1;
    int numPoints = -1;
    double x = 0;
    double y = 0;

    byte byteorder = wkb[0];

    // read wkbType
    if( byteorder == 0 )
    {
      wkbType = ByteUtils.readBEInt( wkb, 1 );
    }
    else
    {
      wkbType = ByteUtils.readLEInt( wkb, 1 );
    }

    // if the geometry isn't a multipoint throw exception
    if( wkbType != 4 )
    {
      throw new GM_Exception( "Invalid byte stream for GM_MultiPoint" );
    }

    // read number of points
    if( byteorder == 0 )
    {
      numPoints = ByteUtils.readBEInt( wkb, 5 );
    }
    else
    {
      numPoints = ByteUtils.readLEInt( wkb, 5 );
    }

    points = new GM_Point[numPoints];

    int offset = 9;

    final Object[] o = new Object[3];
    o[2] = crs;

    // read all points
    for( int i = 0; i < numPoints; i++ )
    {
      // byteorder of the i-th point
      byteorder = wkb[offset];

      // wkbType of the i-th geometry
      if( byteorder == 0 )
      {
        wkbType = ByteUtils.readBEInt( wkb, offset + 1 );
      }
      else
      {
        wkbType = ByteUtils.readLEInt( wkb, offset + 1 );
      }

      // if the geometry isn't a point throw exception
      if( wkbType != 1 )
      {
        throw new GM_Exception( "Invalid byte stream for GM_Point as " + "part of a multi point" );
      }

      // read the i-th point depending on the byteorde
      if( byteorder == 0 )
      {
        x = ByteUtils.readBEDouble( wkb, offset + 5 );
        y = ByteUtils.readBEDouble( wkb, offset + 13 );
      }
      else
      {
        x = ByteUtils.readLEDouble( wkb, offset + 5 );
        y = ByteUtils.readLEDouble( wkb, offset + 13 );
      }

      offset += 21;

      points[i] = new GM_Point_Impl( x, y, crs );
    }

    return GeometryFactory.createGM_MultiPoint( points );
  }

  /**
   * creates a GM_MultiCurve from an array of GM_Curves.
   * 
   * @param curves
   */
  public static GM_MultiCurve createGM_MultiCurve( final GM_Curve[] curves )
  {
    return new GM_MultiCurve_Impl( curves );
  }

  /**
   * creates a GM_MultiCurve from an array of GM_Curves.
   * 
   * @param curves
   */
  public static GM_MultiCurve createGM_MultiCurve( final GM_Curve[] curves, final String crs )
  {
    return new GM_MultiCurve_Impl( curves, crs );
  }

  /**
   * creates a GM_MultiCurve from a wkb.
   * 
   * @param wkb
   *          byte stream that contains the wkb information
   * @param crs
   *          spatial reference system of the curve
   */
  public static GM_MultiCurve createGM_MultiCurve( final byte[] wkb, final String crs ) throws GM_Exception
  {
    int wkbType = -1;
    int numPoints = -1;
    int numParts = -1;
    double x = 0;
    double y = 0;
    GM_Position[][] points = null;

    int offset = 0;

    byte byteorder = wkb[offset++];

    if( byteorder == 0 )
    {
      wkbType = ByteUtils.readBEInt( wkb, offset );
    }
    else
    {
      wkbType = ByteUtils.readLEInt( wkb, offset );
    }

    offset += 4;

    // check if it's realy a linestring
    if( wkbType != 5 )
    {
      throw new GM_Exception( "Invalid byte stream for GM_MultiCurve" );
    }

    // read number of linestrings
    if( byteorder == 0 )
    {
      numParts = ByteUtils.readBEInt( wkb, offset );
    }
    else
    {
      numParts = ByteUtils.readLEInt( wkb, offset );
    }

    offset += 4;

    points = new GM_Position[numParts][];

    // for every linestring
    for( int j = 0; j < numParts; j++ )
    {
      byteorder = wkb[offset++];

      if( byteorder == 0 )
      {
        wkbType = ByteUtils.readBEInt( wkb, offset );
      }
      else
      {
        wkbType = ByteUtils.readLEInt( wkb, offset );
      }

      offset += 4;

      // check if it's realy a linestring
      if( wkbType != 2 )
      {
        throw new GM_Exception( "Invalid byte stream for GM_Curve as " + " part of a GM_MultiCurve." );
      }

      // read number of points
      if( byteorder == 0 )
      {
        numPoints = ByteUtils.readBEInt( wkb, offset );
      }
      else
      {
        numPoints = ByteUtils.readLEInt( wkb, offset );
      }

      offset += 4;

      points[j] = new GM_Position[numPoints];

      // read the i-th point depending on the byteorde
      if( byteorder == 0 )
      {
        for( int i = 0; i < numPoints; i++ )
        {
          x = ByteUtils.readBEDouble( wkb, offset );
          offset += 8;
          y = ByteUtils.readBEDouble( wkb, offset );
          offset += 8;
          points[j][i] = new GM_Position2D_Impl( x, y );
        }
      }
      else
      {
        for( int i = 0; i < numPoints; i++ )
        {
          x = ByteUtils.readLEDouble( wkb, offset );
          offset += 8;
          y = ByteUtils.readLEDouble( wkb, offset );
          offset += 8;
          points[j][i] = new GM_Position2D_Impl( x, y );
        }
      }
    }

    final GM_CurveSegment[] segment = new GM_CurveSegment[1];
    final GM_Curve[] curves = new GM_Curve[numParts];

    for( int i = 0; i < numParts; i++ )
    {
      segment[0] = GeometryFactory.createGM_CurveSegment( points[i], crs );
      curves[i] = GeometryFactory.createGM_Curve( segment );
    }

    return GeometryFactory.createGM_MultiCurve( curves );
  }

  /**
   * creates a GM_MultiSurface
   */
  public static GM_MultiSurface createGM_MultiSurface( final GM_Polygon[] surfaces, final String crs )
  {
    return new GM_MultiSurface_Impl( surfaces, crs );
  }

  /**
   * creates a GM_MultiSurface from a wkb
   */
  public static GM_MultiSurface createGM_MultiSurface( final byte[] wkb, final String crs ) throws GM_Exception
  {
    int wkbtype = -1;
    int numPoly = 0;
    int numRings = 0;
    int numPoints = 0;
    int offset = 0;
    double x = 0;
    double y = 0;
    GM_Position[] externalBoundary = null;
    GM_Position[][] internalBoundaries = null;

    byte byteorder = wkb[offset++];

    if( byteorder == 0 )
    {
      wkbtype = ByteUtils.readBEInt( wkb, offset );
    }
    else
    {
      wkbtype = ByteUtils.readLEInt( wkb, offset );
    }

    offset += 4;

    // is the wkbmetry a multipolygon?
    if( wkbtype != 6 )
    {
      throw new GM_Exception( "Invalid byte stream for GM_MultiSurface" );
    }

    // read number of polygons on the byte[]
    if( byteorder == 0 )
    {
      numPoly = ByteUtils.readBEInt( wkb, offset );
    }
    else
    {
      numPoly = ByteUtils.readLEInt( wkb, offset );
    }

    offset += 4;

    final List<GM_Polygon> list = new ArrayList<>( numPoly );

    for( int ip = 0; ip < numPoly; ip++ )
    {
      byteorder = wkb[offset];
      offset++;

      if( byteorder == 0 )
      {
        wkbtype = ByteUtils.readBEInt( wkb, offset );
      }
      else
      {
        wkbtype = ByteUtils.readLEInt( wkb, offset );
      }

      offset += 4;

      // is the geometry respresented by wkb a polygon?
      if( wkbtype != 3 )
      {
        throw new GM_Exception( "invalid byte stream for GM_Surface " + wkbtype );
      }

      // read number of rings of the polygon
      if( byteorder == 0 )
      {
        numRings = ByteUtils.readBEInt( wkb, offset );
      }
      else
      {
        numRings = ByteUtils.readLEInt( wkb, offset );
      }

      offset += 4;

      // read number of points of the external ring
      if( byteorder == 0 )
      {
        numPoints = ByteUtils.readBEInt( wkb, offset );
      }
      else
      {
        numPoints = ByteUtils.readLEInt( wkb, offset );
      }

      offset += 4;

      // allocate memory for the external boundary
      externalBoundary = new GM_Position[numPoints];

      if( byteorder == 0 )
      {
        // read points of the external boundary from the byte[]
        for( int i = 0; i < numPoints; i++ )
        {
          x = ByteUtils.readBEDouble( wkb, offset );
          offset += 8;
          y = ByteUtils.readBEDouble( wkb, offset );
          offset += 8;
          externalBoundary[i] = new GM_Position2D_Impl( x, y );
        }
      }
      else
      {
        // read points of the external boundary from the byte[]
        for( int i = 0; i < numPoints; i++ )
        {
          x = ByteUtils.readLEDouble( wkb, offset );
          offset += 8;
          y = ByteUtils.readLEDouble( wkb, offset );
          offset += 8;
          externalBoundary[i] = new GM_Position2D_Impl( x, y );
        }
      }

      // only if numRings is larger then one there internal rings
      if( numRings > 1 )
      {
        internalBoundaries = new GM_Position[numRings - 1][];
      }

      if( byteorder == 0 )
      {
        for( int j = 1; j < numRings; j++ )
        {
          // read number of points of the j-th internal ring
          numPoints = ByteUtils.readBEInt( wkb, offset );
          offset += 4;

          // allocate memory for the j-th internal boundary
          internalBoundaries[j - 1] = new GM_Position[numPoints];

          // read points of the external boundary from the byte[]
          for( int i = 0; i < numPoints; i++ )
          {
            x = ByteUtils.readBEDouble( wkb, offset );
            offset += 8;
            y = ByteUtils.readBEDouble( wkb, offset );
            offset += 8;
            internalBoundaries[j - 1][i] = new GM_Position2D_Impl( x, y );
          }
        }
      }
      else
      {
        for( int j = 1; j < numRings; j++ )
        {
          // read number of points of the j-th internal ring
          numPoints = ByteUtils.readLEInt( wkb, offset );
          offset += 4;

          // allocate memory for the j-th internal boundary
          internalBoundaries[j - 1] = new GM_Position[numPoints];

          // read points of the external boundary from the byte[]
          for( int i = 0; i < numPoints; i++ )
          {
            x = ByteUtils.readLEDouble( wkb, offset );
            offset += 8;
            y = ByteUtils.readLEDouble( wkb, offset );
            offset += 8;
            internalBoundaries[j - 1][i] = new GM_Position2D_Impl( x, y );
          }
        }
      }

      final GM_PolygonPatch patch = GeometryFactory.createGM_PolygonPatch( externalBoundary, internalBoundaries, crs );

      list.add( GeometryFactory.createGM_Surface( patch ) );
    }

    return new GM_MultiSurface_Impl( list.toArray( new GM_Polygon[list.size()] ), crs );
  }

  public static GM_Point createGM_Point( final Point p, final GeoTransform transform, final String coordinatesSystem )
  {
    final double g1x = transform.getSourceX( p.getX() );
    final double g1y = transform.getSourceY( p.getY() );
    return GeometryFactory.createGM_Point( g1x, g1y, coordinatesSystem );
  }

  public static GM_Position[] cloneGM_Position( final GM_Position[] positions )
  {
    final List<GM_Position> myList = new LinkedList<>();

    for( final GM_Position position : positions )
      myList.add( (GM_Position)position.clone() );

    return myList.toArray( new GM_Position[myList.size()] );
  }

  public static GM_Triangle createGM_Triangle( final GM_Position[] pos, final String crs )
  {
    if( pos.length != 3 )
      return null;

    return createGM_Triangle( pos[0], pos[1], pos[2], crs );
  }

  public static GM_Triangle createGM_Triangle( final GM_Position pos1, final GM_Position pos2, final GM_Position pos3, final String crs )
  {
    final GM_Triangle triangle = new GM_Triangle_Impl( pos1, pos2, pos3, crs );

    if( triangle.getOrientation() == 1 )
      return new GM_Triangle_Impl( pos1, pos3, pos2, crs );

    return triangle;
  }

  public static GM_Rectangle createGM_Rectangle( final GM_Position[] pos, final String crs )
  {
    if( pos.length != 4 )
      return null;

    return new GM_Rectangle_Impl( pos[0], pos[1], pos[2], pos[3], crs );
  }

  public static GM_Rectangle createGM_Rectangle( GM_Position pos1, GM_Position pos2, GM_Position pos3, GM_Position pos4, String crs )
  {
    return new GM_Rectangle_Impl( pos1, pos2, pos3, pos4, crs );
  }

  public static GM_TriangulatedSurface createGM_TriangulatedSurface( final String crs ) throws GM_Exception
  {
    return new GM_TriangulatedSurface_Impl( crs );
  }

  public static GM_PolyhedralSurface<GM_PolygonPatch> createGM_PolyhedralSurface( final String crs ) throws GM_Exception
  {
    return new GM_PolyhedralSurface_Impl<>( crs );
  }

  /**
   * creates a GM_Curve from an double array of GM_Positions.
   * 
   * @param positions
   *          positions
   * @param crs
   *          geometries coordinate reference system
   */
  public static GM_Curve[] createGM_Curve( final GM_Position[][] rings, final String crs ) throws GM_Exception
  {
    final List<GM_Curve> curveList = new LinkedList<>();

    for( final GM_Position[] positions : rings )
    {
      final GM_CurveSegment[] cs = new GM_CurveSegment[1];
      cs[0] = GeometryFactory.createGM_CurveSegment( positions, crs );

      curveList.add( new GM_Curve_Impl( cs ) );
    }
    return curveList.toArray( new GM_Curve[curveList.size()] );
  }

  public static GM_Ring[] createGM_Rings( final GM_Position[][] rings, final String crs ) throws GM_Exception
  {
    final List<GM_Ring> ringList = new LinkedList<>();

    for( final GM_Position[] positions : rings )
      ringList.add( createGM_Ring( positions, crs ) );

    return ringList.toArray( new GM_Ring[ringList.size()] );
  }

  public static GM_Ring createGM_Ring( final GM_Position[] positions, final String crs ) throws GM_Exception
  {
    return new GM_Ring_Impl( positions, crs );
  }

  public static GM_TriangulatedSurface createGM_TriangulatedSurface( final GM_Triangle[] triangles, final String crs ) throws GM_Exception
  {
    final GM_TriangulatedSurface triangulatedSurface = createGM_TriangulatedSurface( crs );

    for( final GM_Triangle triangle : triangles )
      triangulatedSurface.add( triangle );

    return triangulatedSurface;
  }

  public static GM_TriangulatedSurface createGM_TriangulatedSurface( final List<GM_Triangle> triangles, final String crs ) throws GM_Exception
  {
    return new GM_TriangulatedSurface_Impl( triangles, crs );
  }

  public static GM_PolyhedralSurface<GM_PolygonPatch> createGM_PolyhedralSurface( final GM_PolygonPatch[] polygons, final String crs ) throws GM_Exception
  {
    final GM_PolyhedralSurface<GM_PolygonPatch> triangulatedSurface = createGM_PolyhedralSurface( crs );

    for( final GM_PolygonPatch triangle : polygons )
      triangulatedSurface.add( triangle );

    return triangulatedSurface;
  }

  public static GM_MultiGeometry createGM_MultiGeometry( final String srs )
  {
    return new GM_MultiGeometry_Impl( srs );
  }

  public static GM_MultiGeometry createGM_MultiGeometry( final GM_Object[] elements, final String srs )
  {
    return new GM_MultiGeometry_Impl( elements, srs );
  }
}