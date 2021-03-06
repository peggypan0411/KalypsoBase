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
package org.kalypsodeegree_impl.model.geometry;

import org.kalypso.transformation.transformer.GeoTransformerException;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_MultiGeometry;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;

/**
 * @author "Gernot Belger"
 *
 */
final class GM_MultiGeometry_Impl extends GM_Aggregate_Impl implements GM_MultiGeometry
{
  public GM_MultiGeometry_Impl( final String srs )
  {
    super( srs );
  }

  public GM_MultiGeometry_Impl( final GM_Object[] children, final String crs )
  {
    super( children, crs );
  }

  @Override
  public int getDimension( )
  {
    int maxDimension = 0;

    final int size = getSize();
    for( int i = 0; i < size; i++ )
    {
      final GM_Object geometry = getObjectAt( i );
      maxDimension = Math.max( maxDimension, geometry.getDimension() );
    }

    return maxDimension;
  }

  @Override
  public int getCoordinateDimension( )
  {
    int maxDimension = 0;

    final int size = getSize();
    for( int i = 0; i < size; i++ )
    {
      final GM_Object geometry = getObjectAt( i );

      maxDimension = Math.max( maxDimension, geometry.getCoordinateDimension() );
    }

    return maxDimension;
  }

  @Override
  public GM_Object clone( ) throws CloneNotSupportedException
  {
    final GM_Curve[] clonedGeometries = new GM_Curve[getSize()];
    for( int i = 0; i < getSize(); i++ )
      clonedGeometries[i] = (GM_Curve) getObjectAt( i ).clone();

    return new GM_MultiGeometry_Impl( clonedGeometries, getCoordinateSystem() );
  }

  @Override
  public GM_Object transform( final String targetCRS ) throws GeoTransformerException
  {
    /* If the target is the same coordinate system, do not transform. */
    final String sourceCRS = getCoordinateSystem();
    if( sourceCRS == null || sourceCRS.equalsIgnoreCase( targetCRS ) )
      return this;

    final GM_Object[] geometries = new GM_Object[getSize()];

    for( int i = 0; i < getSize(); i++ )
      geometries[i] = getObjectAt( i ).transform( targetCRS );

    return new GM_MultiGeometry_Impl( geometries, targetCRS );
  }

  @Override
  protected GM_Point calculateCentroid( )
  {
    final int size = getSize();
    if( size == 0 )
      return GM_Constants.EMPTY_CENTROID;

    final double[] cen = new double[getCoordinateDimension()];

    for( int i = 0; i < getSize(); i++ )
    {
      final GM_Object geometry = getObjectAt( i );
      final GM_Point centroid = geometry.getCentroid();

      final double[] pos = centroid.getAsArray();

      for( int j = 0; j < pos.length; j++ )
      {
        cen[j] += pos[j] / size;
      }
    }

    return GeometryFactory.createGM_Point( GeometryFactory.createGM_Position( cen ), getCoordinateSystem() );
  }
}