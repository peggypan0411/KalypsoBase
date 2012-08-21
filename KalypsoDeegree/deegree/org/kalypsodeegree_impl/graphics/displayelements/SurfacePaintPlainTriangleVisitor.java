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
package org.kalypsodeegree_impl.graphics.displayelements;

import java.awt.Graphics;
import java.awt.Graphics2D;

import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree.model.geometry.ISurfacePatchVisitor;
import org.kalypsodeegree_impl.graphics.sld.awt.FillPainter;
import org.kalypsodeegree_impl.graphics.sld.awt.SldAwtUtilities;
import org.kalypsodeegree_impl.graphics.sld.awt.StrokePainter;

/**
 * @author Patrice Kongo
 */
public class SurfacePaintPlainTriangleVisitor<T extends GM_Polygon> implements ISurfacePatchVisitor<T>
{
  private final Graphics m_gc;

  private final GeoTransform m_projection;

  private final IElevationColorModel m_colorModel;

  public SurfacePaintPlainTriangleVisitor( final Graphics gc, final GeoTransform projection, final IElevationColorModel colorModel )
  {
    m_gc = gc;
    m_projection = projection;
    m_colorModel = colorModel;
  }

  @Override
  public boolean visit( final T patch )
  {
    paintTriangle( patch );
    return true;
  }

  private void paintTriangle( final GM_Polygon polygon )
  {
    m_colorModel.setProjection( m_projection );

    final int numOfClasses = m_colorModel.getNumOfClasses();

    final GM_Position[] positions = polygon.getExteriorRing();

    /* loop over all classes */
    for( int currentClass = 0; currentClass < numOfClasses; currentClass++ )
    {
      final double startValue = m_colorModel.getFrom( currentClass );
      final double endValue = m_colorModel.getTo( currentClass );

      final GM_Position[] figure = TrianglePainter.calculateFigure( positions, startValue, endValue );
      if( figure != null )
      {
        // TODO: check if current surface is too small (i.e. < stroke.width) and if this is the case
        // only draw a point with that color or something similar

        // TODO: different strategy? -> paint pixel by pixel -> detect triangle value and paint with right class

        paintSurface( figure, currentClass );
      }
    }
  }

  private void paintSurface( final GM_Position[] posArray, final int currentClass )
  {
    final StrokePainter strokePainter = m_colorModel.getLinePainter( currentClass );
    final FillPainter fillPainter = m_colorModel.getFillPolygonPainter( currentClass );
    final GeoTransform world2Screen = fillPainter.getWorld2Screen();

    try
    {
      SldAwtUtilities.paintRing( (Graphics2D) m_gc, posArray, world2Screen, fillPainter, strokePainter );
    }
    catch( final Exception e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}