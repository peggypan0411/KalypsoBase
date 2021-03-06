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
package org.kalypso.ogc.gml.map.widgets.builders;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.kalypso.transformation.transformer.GeoTransformerFactory;
import org.kalypso.transformation.transformer.IGeoTransformer;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * This class is a geometry builder for a polygon.
 * 
 * @author Holger Albert
 */
public class PolygonGeometryBuilder implements IGeometryBuilder
{
  /**
   * Stores the count of points which this geometry must have. If it is 0, there is no rule.
   */
  private int m_cnt_points;

  private final List<GM_Point> m_points = new ArrayList<>();

  private final String m_crs;

  private GM_Object m_result;

  private final ToolTipRenderer m_renderer;

  final java.awt.Cursor CROSSHAIR_CURSOR = java.awt.Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR );

  final java.awt.Cursor DEFAULT_CURSOR = java.awt.Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );

  private final IGeometryBuilderExtensionProvider m_extender;

  private final boolean m_updateCursor;

  /**
   * The constructor.
   * 
   * @param cnt_points
   *          If > 2 the the geometry will be finished, if the count of points is reached. If <= 2 no rule regarding the
   *          count of the points will apply, except, that a polygon needs at least 3 points for beeing created.
   * @param targetCrs
   *          The target coordinate system.
   */
  public PolygonGeometryBuilder( final int cnt_points, final String targetCrs, final IGeometryBuilderExtensionProvider extender )
  {
    this( cnt_points, targetCrs, extender, true );
  }

  /**
   * The constructor.
   * 
   * @param cnt_points
   *          If > 2 the the geometry will be finished, if the count of points is reached. If <= 2 no rule regarding the
   *          count of the points will apply, except, that a polygon needs at least 3 points for beeing created.
   * @param targetCrs
   *          The target coordinate system.
   */
  public PolygonGeometryBuilder( final int cnt_points, final String targetCrs )
  {
    this( cnt_points, targetCrs, null );
  }

  public PolygonGeometryBuilder( final int cnt_points, final String targetCrs, final IGeometryBuilderExtensionProvider extender, final boolean updateCursor )
  {
    m_extender = extender;
    m_updateCursor = updateCursor;
    m_cnt_points = 0;

    if( cnt_points > 2 )
      m_cnt_points = cnt_points;

    m_crs = targetCrs;

    m_renderer = new ToolTipRenderer( m_extender );

    if( m_extender != null && m_updateCursor )
      m_extender.setCursor( CROSSHAIR_CURSOR );
  }

  /**
   * @see org.kalypso.ogc.gml.map.widgets.builders.IGeometryBuilder#addPoint(org.kalypsodeegree.model.geometry.GM_Point)
   */
  @Override
  public GM_Object addPoint( final GM_Point p ) throws Exception
  {
    m_points.add( p );

    if( m_points.size() > 2 && m_points.size() == m_cnt_points )
      return finish();

    return null;
  }

  /**
   * This function creates the geometry (GM_Surface).
   */
  private GM_Object createGeometry( final GM_Position[] poses ) throws GM_Exception
  {
    final GM_Position[] pos = new GM_Position[poses.length + 1];

    for( int i = 0; i < poses.length; i++ )
      pos[i] = (GM_Position)poses[i].clone();

    pos[poses.length] = (GM_Position)poses[0].clone();

    return GeometryFactory.createGM_Surface( pos, new GM_Position[0][0], m_crs );
  }

  private void drawHandles( final Graphics g, final int[] x, final int[] y )
  {
    for( int i = 0; i < y.length; i++ )
      drawHandle( g, x[i], y[i] );
  }

  // TODO: move to central place. Should used by all widgets / all geometry builders
  public static void drawHandle( final Graphics g, final int x, final int y )
  {
    final int sizeOuter = 6;
    g.drawRect( x - sizeOuter / 2, y - sizeOuter / 2, sizeOuter, sizeOuter );
  }

  /**
   * @see org.kalypso.ogc.gml.map.widgets.builders.IGeometryBuilder#finish()
   */
  @Override
  public GM_Object finish( ) throws Exception
  {
    if( m_extender != null && m_updateCursor )
      m_extender.setCursor( DEFAULT_CURSOR );

    if( m_result != null )
      return m_result;

    if( m_points.size() > 2 && (m_cnt_points == m_points.size() || m_cnt_points <= 2) )
    {
      final IGeoTransformer transformer = GeoTransformerFactory.getGeoTransformer( m_crs );

      final GM_Position[] poses = new GM_Position[m_points.size()];
      for( int i = 0; i < poses.length; i++ )
      {
        final GM_Point transformedPoint = (GM_Point)transformer.transform( m_points.get( i ) );
        poses[i] = transformedPoint.getPosition();
      }

      return createGeometry( poses );
    }

    return null;
  }

  private int[][] getPointArrays( final GeoTransform projection, final Point currentPoint )
  {
    final List<Integer> xArray = new ArrayList<>();
    final List<Integer> yArray = new ArrayList<>();

    for( int i = 0; i < m_points.size(); i++ )
    {
      final GM_Point point = m_points.get( i );

      final int x = (int)projection.getDestX( point.getX() );
      final int y = (int)projection.getDestY( point.getY() );

      xArray.add( new Integer( x ) );
      yArray.add( new Integer( y ) );
    }

    if( currentPoint != null )
    {
      xArray.add( currentPoint.x );
      yArray.add( currentPoint.y );
    }

    final int[] xs = ArrayUtils.toPrimitive( xArray.toArray( new Integer[xArray.size()] ) );
    final int[] ys = ArrayUtils.toPrimitive( yArray.toArray( new Integer[yArray.size()] ) );

    return new int[][] { xs, ys };
  }

  @Override
  public void paint( final Graphics g, final GeoTransform projection, final Point currentPoint )
  {
    // IMPORTANT: we remember GM_Points (not Point's) and retransform them for painting
    // because the projection depends on the current map-extent, so this builder
    // is stable in regard to zoom in/out
    if( !m_points.isEmpty() )
    {
      final int[][] points = getPointArrays( projection, currentPoint );

      final int[] arrayX = points[0];
      final int[] arrayY = points[1];

      /* Paint a polygon. */
      g.drawPolygon( arrayX, arrayY, arrayX.length );
      drawHandles( g, arrayX, arrayY );
    }

    m_renderer.paint( g );
  }

  @Override
  public void removeLastPoint( )
  {
    if( m_points.size() > 0 )
      m_points.remove( m_points.size() - 1 );
  }

  @Override
  public void reset( )
  {
    m_points.clear();
    m_result = null;
    if( m_extender != null && m_updateCursor )
      m_extender.setCursor( CROSSHAIR_CURSOR );
  }

  public int size( )
  {
    return m_points.size();
  }

  /**
   * This function returns the minimum number of points, as given in the constructor.
   * 
   * @return The number of points, at which the geometry should be finished. <= 2 if no rule will be used.
   */
  public int getCntPoints( )
  {
    return m_cnt_points;
  }

  public int getPointCount( )
  {
    return m_points.size();
  }
}