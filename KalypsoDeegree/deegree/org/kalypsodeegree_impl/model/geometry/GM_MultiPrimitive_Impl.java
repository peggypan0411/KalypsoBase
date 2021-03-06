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
import org.kalypsodeegree.model.geometry.GM_Aggregate;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_MultiPrimitive;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_AbstractGeometry;

/**
 * default implementation of the GM_MultiPrimitive interface of package jago.model.
 * <p>
 * ------------------------------------------------------------
 * </p>
 *
 * @version 5.6.2001
 * @author Andreas Poth
 */
class GM_MultiPrimitive_Impl extends GM_Aggregate_Impl implements GM_MultiPrimitive
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 7228377539686274411L;

  /**
   * Creates a new GM_MultiPrimitive_Impl object.
   *
   * @param crs
   */
  public GM_MultiPrimitive_Impl( final String crs )
  {
    super( crs );
  }

  /**
   * Creates a new GM_MultiPrimitive_Impl object.
   *
   * @param crs
   */
  public GM_MultiPrimitive_Impl( final GM_Object[] children, final String crs )
  {
    super( children, crs );
  }

  /**
   * @throws NotImplementedException
   * @see org.kalypsodeegree_impl.model.geometry.GM_Aggregate_Impl#calculateCentroid()
   */
  @Override
  protected GM_Point calculateCentroid( )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * merges this aggregation with another one
   *
   * @exception GM_Exception
   *              will be thrown if the submitted isn't the same type as the recieving one.
   */
  @Override
  public void merge( final GM_Aggregate aggregate ) throws GM_Exception
  {
    if( !(aggregate instanceof GM_MultiPrimitive) )
    {
      throw new GM_Exception( "The submitted aggregation isn't a GM_MultiPrimitive" );
    }

    super.merge( aggregate );
  }

  /**
   * returns the GM_Primitive at the submitted index.
   */
  @Override
  public GM_AbstractGeometry getPrimitiveAt( final int index )
  {
    return (GM_AbstractGeometry) super.getObjectAt( index );
  }

  /**
   * returns all GM_Primitives as array
   */
  @Override
  public GM_AbstractGeometry[] getAllPrimitives( )
  {
    final GM_AbstractGeometry[] gmos = new GM_AbstractGeometry[getSize()];

    return m_aggregate.toArray( gmos );
  }

  @Override
  public int getCoordinateDimension( )
  {
    return -1;
  }

  @Override
  public int getDimension( )
  {
    return 2;
  }

  @SuppressWarnings("unused")
  @Override
  public GM_Object transform( final String targetCRS ) throws GeoTransformerException
  {
    throw new UnsupportedOperationException();
  }
}