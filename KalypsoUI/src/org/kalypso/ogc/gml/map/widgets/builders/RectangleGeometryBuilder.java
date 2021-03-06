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
 * This class is a geometry builder for a rectangle.
 * 
 * @author Thomas Jung
 */
public class RectangleGeometryBuilder implements IGeometryBuilder
{
  private GM_Point m_startPoint = null;

  private GM_Point m_endPoint = null;

  private final String m_crs;

  private GM_Object m_result;

  private final ToolTipRenderer m_renderer;

  final java.awt.Cursor CROSSHAIR_CURSOR = java.awt.Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR );

  final java.awt.Cursor DEFAULT_CURSOR = java.awt.Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );

  private final IGeometryBuilderExtensionProvider m_extender;

  /**
   * The constructor.
   * 
   * @param targetCrs
   *          The target coordinate system.
   */
  public RectangleGeometryBuilder( final String targetCrs, final IGeometryBuilderExtensionProvider extender )
  {
    m_extender = extender;

    m_crs = targetCrs;

    m_renderer = new ToolTipRenderer( m_extender );

    if( m_extender != null )
      m_extender.setCursor( CROSSHAIR_CURSOR );
  }

  public RectangleGeometryBuilder( final String targetCrs )
  {
    this( targetCrs, null );
  }

  /**
   * @see org.kalypso.informdss.manager.util.widgets.IGeometryBuilder#addPoint(org.kalypsodeegree.model.geometry.GM_Position)
   */
  @Override
  public GM_Object addPoint( final GM_Point p ) throws Exception
  {
    if( m_startPoint == null )
    {
      m_startPoint = p;
      m_endPoint = null;
      return null;
    }

    m_endPoint = p;
    return finish();
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
    final int sizeOuter = 6;
    if( y.length > 2 )
    {
      g.drawRect( x[0] - sizeOuter / 2, y[0] - sizeOuter / 2, sizeOuter, sizeOuter );
      g.drawRect( x[2] - sizeOuter / 2, y[2] - sizeOuter / 2, sizeOuter, sizeOuter );
    }
  }

  /**
   * @see org.kalypso.informdss.manager.util.widgets.IGeometryBuilder#finish()
   */
  @Override
  public GM_Object finish( ) throws Exception
  {
    if( m_extender != null )
      m_extender.setCursor( DEFAULT_CURSOR );

// if( m_result != null )
// return m_result;

    if( m_startPoint != null && m_endPoint != null )
    {
      final IGeoTransformer transformer = GeoTransformerFactory.getGeoTransformer( m_crs );

      /* convert the two points into four */
      final GM_Position[] poses = new GM_Position[4];

      final GM_Point transformedPoint1 = (GM_Point)transformer.transform( m_startPoint );
      final GM_Point transformedPoint2 = (GM_Point)transformer.transform( m_endPoint );

      final GM_Position pos1 = transformedPoint1.getPosition();
      final GM_Position pos2 = transformedPoint2.getPosition();

      poses[0] = GeometryFactory.createGM_Position( pos1.getX(), pos1.getY() );
      poses[1] = GeometryFactory.createGM_Position( pos1.getX(), pos2.getY() );
      poses[2] = GeometryFactory.createGM_Position( pos2.getX(), pos2.getY() );
      poses[3] = GeometryFactory.createGM_Position( pos2.getX(), pos1.getY() );

      m_result = createGeometry( poses );

      return m_result;
    }

    return null;
  }

  private int[][] getPointArrays( final GeoTransform projection, final Point currentPoint )
  {
    final List<Integer> xArray = new ArrayList<>();
    final List<Integer> yArray = new ArrayList<>();

    final int xStart = (int)projection.getDestX( m_startPoint.getX() );
    final int yStart = (int)projection.getDestY( m_startPoint.getY() );

    if( currentPoint != null )
    {
      xArray.add( new Integer( xStart ) );
      xArray.add( new Integer( xStart ) );
      xArray.add( currentPoint.x );
      xArray.add( currentPoint.x );

      yArray.add( new Integer( yStart ) );
      yArray.add( currentPoint.y );
      yArray.add( currentPoint.y );
      yArray.add( new Integer( yStart ) );
    }

    final int[] xs = ArrayUtils.toPrimitive( xArray.toArray( new Integer[xArray.size()] ) );
    final int[] ys = ArrayUtils.toPrimitive( yArray.toArray( new Integer[yArray.size()] ) );

    return new int[][] { xs, ys };
  }

  /**
   * @see org.kalypso.informdss.manager.util.widgets.IGeometryBuilder#paint(java.awt.Graphics, org.kalypsodeegree.graphics.transformation.GeoTransform)
   */
  @Override
  public void paint( final Graphics g, final GeoTransform projection, final Point currentPoint )
  {
    if( m_startPoint != null )
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

  /**
   * @see org.kalypso.nofdpidss.ui.view.wizard.measure.construction.create.builders.geometry.IMyGeometryBuilder#removeLastPoint()
   */
  @Override
  public void removeLastPoint( )
  {
    if( m_startPoint == null )
      return;

    if( m_endPoint != null )
    {
      m_endPoint = null;
      return;
    }

    m_startPoint = null;

  }

  /**
   * @see org.kalypso.ogc.gml.map.widgets.builders.IGeometryBuilder#removePoints()
   */
  @Override
  public void reset( )
  {
    m_startPoint = null;
    m_endPoint = null;
    m_result = null;

    if( m_extender != null )
      m_extender.setCursor( CROSSHAIR_CURSOR );
  }
}