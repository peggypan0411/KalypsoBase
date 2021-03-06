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

package org.kalypsodeegree.model.geometry;

import javax.xml.namespace.QName;

import org.kalypso.commons.xml.NS;

/**
 * This Interface defines the Aggregation of GM_Surfaces. The implementing class should capsulate a java.util.Vector or
 * a comarative data structure.
 * <p>
 * -----------------------------------------------------
 * </p>
 *
 * @author Andreas Poth
 * @version $Revision$ $Date$
 *          <p>
 */
public interface GM_MultiSurface extends GM_MultiPrimitive
{
  public static final QName MULTI_SURFACE_ELEMENT = new QName( NS.GML3, "MultiSurface" );

  /**
   * adds an GM_Surface to the aggregation
   */
  void addSurface( GM_Polygon gms );

  /**
   * inserts a GM_Surface in the aggregation. all elements with an index equal or larger index will be moved. if index
   * is larger then getSize() - 1 or smaller then 0 or gms equals null an exception will be thrown.
   *
   * @param gms
   *          GM_Surface to insert.
   * @param index
   *          position where to insert the new GM_Surface
   */
  void insertSurfaceAt( GM_Polygon gms, int index ) throws GM_Exception;

  /**
   * sets the submitted GM_Surface at the submitted index. the element at the position <code>index</code> will be
   * removed. if index is larger then getSize() - 1 or smaller then 0 or gms equals null an exception will be thrown.
   *
   * @param gms
   *          GM_Surface to set.
   * @param index
   *          position where to set the new GM_Surface
   */
  void setSurfaceAt( GM_Polygon gms, int index ) throws GM_Exception;

  /**
   * removes the submitted GM_Surface from the aggregation
   *
   * @return the removed GM_Surface
   */
  GM_Polygon removeSurface( GM_Polygon gms );

  /**
   * removes the GM_Surface at the submitted index from the aggregation. if index is larger then getSize() - 1 or
   * smaller then 0 an exception will be thrown.
   *
   * @return the removed GM_Surface
   */
  GM_Polygon removeSurfaceAt( int index ) throws GM_Exception;

  /**
   * returns the GM_Surface at the submitted index.
   */
  GM_Polygon getSurfaceAt( int index );

  /**
   * returns all GM_Surfaces as array
   */
  GM_Polygon[] getAllSurfaces( );

  double getArea( );
}