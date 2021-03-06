/***********************************************
 * created on 		12.06.2006
 * last modified:
 *
 * author:			sstein
 *
 * description:
 *
 *
 ***********************************************/
package org.openjump.core.graph.delauneySimplexInsert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree.model.geometry.GM_Triangle;
import org.kalypsodeegree_impl.model.geometry.JTSAdapter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

/**
 * @author sstein Use the class to access the delauney triangulation by L. Paul Chew Methods of the class are modified
 *         versions from DelaunayPanel.java in DelaunayAp.java.<br>
 * <br>
 *         Changes via Kalypso:<br>
 *         * Gernot Belger: code formatting + java 1.5 templates<br>
 *         * Gernot Belger: boundary for thiessen polygons can be provided from outside
 */
public class DTriangulationForJTS
{
  private final DelaunayTriangulation dt; // The Delaunay triangulation

  private final Simplex initialTriangle; // The large initial triangle

  private double dx = 0;

  private double dy = 0;

  public boolean debug = false; // True iff printing info for debugging

  private final Geometry m_thiessenBoundary;

  public DTriangulationForJTS( final List<Point> pointList )
  {
    this( pointList, null );
  }

  public DTriangulationForJTS( final List<Point> pointList, final Geometry thiessenBoundary )
  {
    double argmaxx = 0;
    double argmaxy = 0;
    double argminx = 0;
    double argminy = 0;

    int count = 0;
    // -- calc coordinates of initial symplex
    if( thiessenBoundary == null )
    {
      for( final Point point : pointList )
      {
        final Point pt = point;
        if( count == 0 )
        {
          argmaxx = pt.getX();
          argminx = pt.getX();
          argmaxy = pt.getY();
          argminy = pt.getY();
        }
        else
        {
          if( pt.getX() < argminx )
          {
            argminx = pt.getX();
          }
          if( pt.getX() > argmaxx )
          {
            argmaxx = pt.getX();
          }
          if( pt.getY() < argminy )
          {
            argminy = pt.getY();
          }
          if( pt.getY() > argmaxy )
          {
            argmaxy = pt.getY();
          }
        }
        count++;
      }

      final double widthx = argmaxx - argminx;
      final double widthy = argmaxy - argminy;

      final Pnt lowerLeftPnt1 = new Pnt( argminx - 1.5 * dx, argminy - dy, 0 );

      // Create a rectangular boundary
      final GeometryFactory gf = new GeometryFactory();
      final Coordinate[] coords = new Coordinate[5];
      coords[0] = new Coordinate( argminx + 1 * widthx, argminy + 0.5 * widthy ); // lowerleft
      coords[1] = new Coordinate( argminx + 3 * widthx, argminy + 0.5 * widthy ); // lowerright
      coords[2] = new Coordinate( argminx + 3 * widthx, argminy + 2.5 * widthy ); // topright
      coords[3] = new Coordinate( argminx + 1 * widthx, argminy + 2.5 * widthy ); // topleft
      // -- to close linestring
      coords[4] = new Coordinate( lowerLeftPnt1.coord( 0 ) + 1 * widthx, lowerLeftPnt1.coord( 1 ) + 0.5 * widthy ); // lowerleft
      final LinearRing lr = gf.createLinearRing( coords );
      m_thiessenBoundary = gf.createPolygon( lr, null );
    }
    else
    {
      m_thiessenBoundary = thiessenBoundary;

      // different initial triangle in case of external boundary, so the geometries will be big enoeugh
      final Envelope envelopeInternal = m_thiessenBoundary.getEnvelopeInternal();
      argminx = envelopeInternal.getMinX();
      argminy = envelopeInternal.getMinY();
      argmaxx = envelopeInternal.getMaxX();
      argmaxy = envelopeInternal.getMaxY();
    }

    dx = argmaxx - argminx;
    dy = argmaxy - argminy;
    // -- the initial simplex must contain all points
    // -- take the bounding box, move the diagonals (sidewards)
    // the meeting point will be the mirrored bbox-center on the top edge
    initialTriangle = new Simplex( new Pnt[] { new Pnt( argminx - 1.5 * dx, argminy - dy ), // lower left
        new Pnt( argmaxx + 1.5 * dx, argminy - dy ), // lower right
        new Pnt( argminx + dx / 2.0, argmaxy + 1.5 * dy ) } ); // center, top

    dt = new DelaunayTriangulation( initialTriangle );
    addPoints( pointList );
  }

  public void addPoints( final List<Point> pointList )
  {
    for( final Point jtsPt : pointList )
    {
      try
      {
        final Coordinate coord = jtsPt.getCoordinate();

        final Pnt pt = new Pnt( coord.x, coord.y );
        dt.delaunayPlace( pt );
      }
      catch( final Exception e )
      {
        if( debug )
          System.out.println( "no geometry" );
      }
    }
  }

  public void addPoint( final double x, final double y )
  {
    final Pnt point = new Pnt( x, y );
    if( debug )
      System.out.println( "Click " + point );

    dt.delaunayPlace( point );
  }

  public void addPoint( final double x, final double y, final double z )
  {
    final Pnt point = new Pnt( x, y, z );
    if( debug )
      System.out.println( "Click " + point );

    dt.delaunayPlace( point );
  }

  /**
   * Draw all the Delaunay edges.
   *
   * @return Arraylist with LineString geometries.
   */
  public List<LineString> drawAllDelaunay( )
  {
    // Loop through all the edges of the DT (each is done twice)
    final GeometryFactory gf = new GeometryFactory();
    final List<LineString> lines = new ArrayList<LineString>();
    for( final Iterator it = dt.iterator(); it.hasNext(); )
    {
      final Simplex triangle = (Simplex) it.next();
      for( final Iterator otherIt = triangle.facets().iterator(); otherIt.hasNext(); )
      {
        final Set facet = (Set) otherIt.next();
        final Pnt[] endpoint = (Pnt[]) facet.toArray( new Pnt[2] );
        final Coordinate[] coords = new Coordinate[2];

        coords[0] = new Coordinate( endpoint[0].coord( 0 ), endpoint[0].coord( 1 ) );
        coords[1] = new Coordinate( endpoint[1].coord( 0 ), endpoint[1].coord( 1 ) );

        final LineString ls = gf.createLineString( coords );
        lines.add( ls );
      }
    }
    return lines;
  }

  @SuppressWarnings("unchecked")
  public List<GM_Triangle> getAllTriangles( final String crs )
  {
    final List<GM_Triangle> triangles = new ArrayList<GM_Triangle>();

    // Loop through all the edges of the DT (each is done twice)
    for( final Iterator it = dt.iterator(); it.hasNext(); )
    {
      final Simplex triangle = (Simplex) it.next();

      // loop over all vertices of the current triangle
      final Set<Coordinate> coordSet = new HashSet<Coordinate>();

      for( final Iterator otherIt = triangle.facets().iterator(); otherIt.hasNext(); )
      {
        // get all vertex points and add them to a set
        final Set triangleVertex = (Set) otherIt.next();
        final Pnt[] vertexPoints = (Pnt[]) triangleVertex.toArray( new Pnt[2] );

        coordSet.add( new Coordinate( vertexPoints[0].coord( 0 ), vertexPoints[0].coord( 1 ) ) );
        coordSet.add( new Coordinate( vertexPoints[1].coord( 0 ), vertexPoints[1].coord( 1 ) ) );
      }

      // now we have a list with all vertex coords
      final List<GM_Position> posList = new ArrayList<GM_Position>();
      for( final Coordinate coord : coordSet )
        posList.add( JTSAdapter.wrap( coord ) );

      final GM_Position[] poses = posList.toArray( new GM_Position[posList.size()] );
      final GM_Triangle gmTriangle = org.kalypsodeegree_impl.model.geometry.GeometryFactory.createGM_Triangle( poses, crs );
      triangles.add( gmTriangle );
    }

    return triangles;
  }

  public List<GM_Triangle> getInnerTriangles( final String crs )
  {
    final List<GM_Triangle> triangles = new ArrayList<GM_Triangle>();

    final List<GM_Triangle> allTriangles = getAllTriangles( crs );

    final Coordinate[] initialTriangleCoords = getInitialTriangleCoords();

    // check for outer coords
    for( final GM_Triangle triangle : allTriangles )
    {
      final GM_Position[] positions = triangle.getExteriorRing();

      boolean inner = true;
      for( final GM_Position pos : positions )
      {
        final Coordinate ptCoord = JTSAdapter.export( pos );
        if( isTriangleCoord( initialTriangleCoords, ptCoord ) )
        {
          inner = false;
          break;
        }
      }
      if( inner == true )
        triangles.add( triangle );
    }
    return triangles;
  }

  private static boolean isTriangleCoord( final Coordinate[] triangleCoords, final Coordinate ptCoord )
  {
    for( final Coordinate coord : triangleCoords )
    {
      if( ptCoord.distance( coord ) < 0.01 )
        return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public Coordinate[] getInitialTriangleCoords( )
  {
    final Set<Coordinate> coordSet = new HashSet<Coordinate>();
    for( final Iterator otherIt = initialTriangle.facets().iterator(); otherIt.hasNext(); )
    {
      // get all vertex points and add them to a set
      final Set triangleVertex = (Set) otherIt.next();
      final Pnt[] vertexPoints = (Pnt[]) triangleVertex.toArray( new Pnt[2] );
      coordSet.add( new Coordinate( vertexPoints[0].coord( 0 ), vertexPoints[0].coord( 1 ) ) );
      coordSet.add( new Coordinate( vertexPoints[1].coord( 0 ), vertexPoints[1].coord( 1 ) ) );
    }

    return coordSet.toArray( new Coordinate[coordSet.size()] );
  }

  /**
   * Draw all the Voronoi edges.
   *
   * @return Arraylist with LineString geometries.
   */
  public List<LineString> drawAllVoronoi( )
  {
    final GeometryFactory gf = new GeometryFactory();
    final List<LineString> lines = new ArrayList<LineString>();
    // Loop through all the edges of the DT (each is done twice)
    for( final Iterator it = dt.iterator(); it.hasNext(); )
    {
      final Simplex triangle = (Simplex) it.next();
      for( final Iterator otherIt = dt.neighbors( triangle ).iterator(); otherIt.hasNext(); )
      {
        final Simplex other = (Simplex) otherIt.next();
        final Pnt p = Pnt.circumcenter( (Pnt[]) triangle.toArray( new Pnt[0] ) );
        final Pnt q = Pnt.circumcenter( (Pnt[]) other.toArray( new Pnt[0] ) );
        final Coordinate[] coords = new Coordinate[2];
        coords[0] = new Coordinate( p.coord( 0 ), p.coord( 1 ) );
        coords[1] = new Coordinate( q.coord( 0 ), q.coord( 1 ) );
        final LineString ls = gf.createLineString( coords );
        lines.add( ls );
      }
    }
    return lines;
  }

  /**
   * Draw all the sites (i.e., the input points) of the DT.
   *
   * @return Arraylist with point geometries.
   */
  public List<Point> drawAllSites( )
  {
    // Loop through all sites of the DT
    // Each is done several times, about 6 times each on average; this
    // can be proved via Euler's formula.
    final GeometryFactory gf = new GeometryFactory();
    final List<Point> points = new ArrayList<Point>();
    for( final Iterator it = dt.iterator(); it.hasNext(); )
    {
      for( final Iterator otherIt = ((Simplex) it.next()).iterator(); otherIt.hasNext(); )
      {
        final Pnt pt = (Pnt) otherIt.next();
        final Coordinate coord = new Coordinate( pt.coord( 0 ), pt.coord( 1 ) );
        final Point jtsPt = gf.createPoint( coord );
        points.add( jtsPt );
      }
    }
    return points;
  }

  /**
   * Draw all the empty circles (one for each triangle) of the DT.
   *
   * @return Arraylist with polygon geometries.
   */
  public List<Geometry> drawAllCircles( )
  {
    // Loop through all triangles of the DT
    final GeometryFactory gf = new GeometryFactory();
    final List<Geometry> circles = new ArrayList<Geometry>();
    loop: for( final Iterator it = dt.iterator(); it.hasNext(); )
    {
      final Simplex triangle = (Simplex) it.next();
      for( final Iterator otherIt = initialTriangle.iterator(); otherIt.hasNext(); )
      {
        final Pnt p = (Pnt) otherIt.next();
        if( triangle.contains( p ) )
          continue loop;
      }
      final Pnt c = Pnt.circumcenter( (Pnt[]) triangle.toArray( new Pnt[0] ) );
      final double radius = c.subtract( (Pnt) triangle.iterator().next() ).magnitude();
      final Coordinate coord = new Coordinate( c.coord( 0 ), c.coord( 1 ) );
      final Point jtsPt = gf.createPoint( coord );
      circles.add( jtsPt.buffer( radius ) );
    }
    return circles;
  }

  public DelaunayTriangulation getDelaunayTriangulation( )
  {
    return dt;
  }

  /**
   * @return the corner points of the initial simplex which is divided into smaller simplexes by the iterative insertion
   *         of the point dataset
   */
  public List<Point> getInitialSimmplexAsJTSPoints( )
  {
    final GeometryFactory gf = new GeometryFactory();
    final List<Point> points = new ArrayList<Point>();

    for( final Iterator otherIt = initialTriangle.iterator(); otherIt.hasNext(); )
    {
      final Pnt pt = (Pnt) otherIt.next();
      final Coordinate coord = new Coordinate( pt.coord( 0 ), pt.coord( 1 ) );
      final Point jtsPt = gf.createPoint( coord );
      points.add( jtsPt );
    }
    return points;
  }

  /**
   * the size of the box has been empirically defined to get "undistorted" outer thiessen polygons
   *
   * @return a bounding box necessary to create the final thiessen polygons
   */
  public Geometry getThiessenBoundingBox( )
  {
    return m_thiessenBoundary;
  }

  /**
   * Method returns thiessen polygons within a empirically enlarged bounding box around the point set. Therefore the
   * voronoi edges are polygonized and the intersecting voronoi polygons with the bounding box are calculated. These
   * intersecting thiessen polygons (in the bounding box) are returned.
   * <p>
   * Note: "thiesen" and "voronoi" is exchangeable.
   *
   * @return
   */
  public List<Polygon> getThiessenPolys( )
  {
    // -- do union of all edges and use the polygonizer to create polygons from it
    if( debug )
      System.out.println( "get voronoi egdes" );
    final List<LineString> edges = drawAllVoronoi();
    if( debug )
      System.out.println( "merge voronoi egdes to multiLineString" );
    Geometry mls = edges.get( 0 );
    for( int i = 1; i < edges.size(); i++ )
    {
      final LineString line = edges.get( i );
      mls = mls.union( line );
    }
    if( debug )
      System.out.println( "polygonize" );
    final Polygonizer poly = new Polygonizer();
    poly.add( mls );
    final Collection<Polygon> polys = poly.getPolygons();
    // -- get final polygons in bounding box (=intersection polygons with the bbox)
    final Geometry bbox = getThiessenBoundingBox();
    if( debug )
      System.out.println( "get intersections and final polys.." );
    final List<Polygon> finalPolys = new ArrayList<Polygon>();
    for( final Iterator<Polygon> iter = polys.iterator(); iter.hasNext(); )
    {
      final Geometry candPoly = iter.next();
      final Geometry intersection = bbox.intersection( candPoly );
      if( intersection != null )
      {
        if( intersection.getArea() > 0 )
          finalPolys.add( (Polygon) intersection );
      }
    }
    return finalPolys;
  }

  /**
   * resolves triangles and updates / sets the z-values of those triangles
   *
   * @param positions
   *          positions containing x,y,z coordinates
   */
  public GM_Triangle[] getAllTrianglesWithZValues( final GM_Position[] positions, final String crs ) throws GM_Exception
  {
    final List<GM_Triangle> resultTriangles = new ArrayList<GM_Triangle>();

    final List<GM_Triangle> baseTriangles = getAllTriangles( crs );

    for( final GM_Triangle baseTriangle : baseTriangles )
    {
      final GM_Position[] ring = toPositionZ( positions, baseTriangle.getExteriorRing() );
      if( ArrayUtils.isEmpty( ring ) )
        continue;

      final GM_Triangle triangle = org.kalypsodeegree_impl.model.geometry.GeometryFactory.createGM_Triangle( ring, crs );
      resultTriangles.add( triangle );
    }

    return resultTriangles.toArray( new GM_Triangle[] {} );
  }

  public static GM_Position[] toPositionZ( final GM_Position[] positions, final GM_Position[] ring )
  {
    if( ArrayUtils.isEmpty( ring ) || ring.length != 4 )
      return new GM_Position[] {};

    final List<GM_Position> replaced = new ArrayList<GM_Position>();
    for( int i = 0; i < 3; i++ )
    {
      final GM_Position ptr = ring[i];
      final GM_Position position = find( positions, ptr );
      if( position == null )
      {
        // FIXME
        replaced.add( org.kalypsodeegree_impl.model.geometry.GeometryFactory.createGM_Position( ptr.getX(), ptr.getY(), 1.0 ) );
// replaced.add( ptr );
      }

      else
        replaced.add( position );
    }

    return replaced.toArray( new GM_Position[] {} );
  }

  public static GM_Position find( final GM_Position[] positions, final GM_Position base )
  {
    for( final GM_Position position : positions )
    {
      if( position.getDistance( base ) < 0.5 )
        return position;
    }

    return null;
  }

}
