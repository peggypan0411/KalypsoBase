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

import org.kalypso.transformation.transformer.GeoTransformerException;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_CurveSegment;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_MultiCurve;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;

/**
 * default implementation of the GM_MultiCurve interface from package jago.model.
 * ------------------------------------------------------------
 *
 * @version 12.6.2001
 * @author Andreas Poth
 */
final class GM_MultiCurve_Impl extends GM_MultiPrimitive_Impl implements GM_MultiCurve
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 2730942874409216686L;

  /**
   * Creates a new GM_MultiCurve_Impl object.
   *
   * @param crs
   */
  public GM_MultiCurve_Impl( final String crs )
  {
    super( crs );
  }

  /**
   * Creates a new GM_MultiCurve_Impl object.
   *
   * @param gmc
   */
  public GM_MultiCurve_Impl( final GM_Curve[] gmc )
  {
    this( gmc, null );
  }

  /**
   * Creates a new GM_MultiCurve_Impl object.
   *
   * @param gmc
   * @param crs
   */
  public GM_MultiCurve_Impl( final GM_Curve[] gmc, final String crs )
  {
    super( gmc, crs );
  }

  /**
   * adds a GM_Curve to the aggregation
   */
  @Override
  public void addCurve( final GM_Curve gmc )
  {
    super.add( gmc );
  }

  /**
   * inserts a GM_Curve in the aggregation. all elements with an index equal or larger index will be moved. if index is
   * larger then getSize() - 1 or smaller then 0 or gmc equals null an exception will be thrown.
   *
   * @param gmc
   *          GM_Curve to insert.
   * @param index
   *          position where to insert the new GM_Curve
   */
  @Override
  public void insertCurveAt( final GM_Curve gmc, final int index ) throws GM_Exception
  {
    super.insertObjectAt( gmc, index );
  }

  /**
   * sets the submitted GM_Curve at the submitted index. the element at the position <code>index</code> will be removed.
   * if index is larger then getSize() - 1 or smaller then 0 or gmc equals null an exception will be thrown.
   *
   * @param gmc
   *          GM_Curve to set.
   * @param index
   *          position where to set the new GM_Curve
   */
  @Override
  public void setCurveAt( final GM_Curve gmc, final int index ) throws GM_Exception
  {
    setObjectAt( gmc, index );
  }

  /**
   * removes the submitted GM_Curve from the aggregation
   *
   * @return the removed GM_Curve
   */
  @Override
  public GM_Curve removeCurve( final GM_Curve gmc )
  {
    return (GM_Curve) super.removeObject( gmc );
  }

  /**
   * removes the GM_Curve at the submitted index from the aggregation. if index is larger then getSize() - 1 or smaller
   * then 0 an exception will be thrown.
   *
   * @return the removed GM_Curve
   */
  @Override
  public GM_Curve removeCurveAt( final int index ) throws GM_Exception
  {
    return (GM_Curve) super.removeObjectAt( index );
  }

  /**
   * removes all GM_Curve from the aggregation.
   */
  @Override
  public void removeAll( )
  {
    super.removeAll();
  }

  /**
   * returns the GM_Curve at the submitted index.
   */
  @Override
  public GM_Curve getCurveAt( final int index )
  {
    return (GM_Curve) super.getPrimitiveAt( index );
  }

  /**
   * returns all GM_Curves as array
   */
  @Override
  public GM_Curve[] getAllCurves( )
  {
    return m_aggregate.toArray( new GM_Curve[getSize()] );
  }

  /**
   * returns true if the submitted GM_Curve is within the aggregation
   */
  public boolean isMember( final GM_Curve gmc )
  {
    return super.isMember( gmc );
  }

  /**
   * calculates the centroid of the aggregation
   */
  @Override
  protected GM_Point calculateCentroid( )
  {
    double cnt = 0;

    final int size = getSize();
    // If we are empty we dont have a centroid
    if( size == 0 )
      return GM_Constants.EMPTY_CENTROID;

    final GM_Point gmp = getCurveAt( 0 ).getCentroid();

    final double[] cen = new double[gmp.getAsArray().length];

    final int dimSize = Math.min( cen.length, getCoordinateDimension() );

    for( int i = 0; i < size; i++ )
    {
      cnt += getCurveAt( i ).getNumberOfCurveSegments();

      final double[] pos = getCurveAt( i ).getCentroid().getAsArray();

      // pos.length is always 2, so we have ArrayIndexOutOfBoundsException always when dimSize > 2
      // TODO consider this

      final int dimSize2 = Math.min( pos.length, dimSize );
      for( int j = 0; j < dimSize2; j++ )
      {
        cen[j] += pos[j];
      }
    }

    for( int j = 0; j < dimSize; j++ )
    {
      cen[j] = cen[j] / cnt / size;
    }

    return GeometryFactory.createGM_Point( GeometryFactory.createGM_Position( cen ), getCoordinateSystem() );
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
    GM_CurveSegment sp = null;

    try
    {
      sp = getCurveAt( 0 ).getCurveSegmentAt( 0 );
    }
    catch( final Exception ex )
    {
    }
    // TODO: NPE here, if above exception is thrown
    return sp.getPositionAt( 0 ).getCoordinateDimension();
  }

  @Override
  public GM_Object clone( ) throws CloneNotSupportedException
  {
    final GM_Curve[] curves = getAllCurves();
    final GM_Curve[] clonedCurves = new GM_Curve[curves.length];
    for( int i = 0; i < curves.length; i++ )
      clonedCurves[i] = (GM_Curve) curves[i].clone();

    return new GM_MultiCurve_Impl( clonedCurves, getCoordinateSystem() );
  }

  @Override
  public GM_Object transform( final String targetCRS ) throws GeoTransformerException
  {
    /* If the target is the same coordinate system, do not transform. */
    final String sourceCRS = getCoordinateSystem();
    if( sourceCRS == null || sourceCRS.equalsIgnoreCase( targetCRS ) )
      return this;

    final GM_Curve[] curves = new GM_Curve[getSize()];

    for( int i = 0; i < getSize(); i++ )
      curves[i] = (GM_Curve) getCurveAt( i ).transform( targetCRS );

    return GeometryFactory.createGM_MultiCurve( curves );
  }
}