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

import org.kalypso.transformation.transformer.GeoTransformerException;

/**
 * A sequence of decimals numbers which when written on a width are a sequence of coordinate positions. The width is
 * derived from the CRS or coordinate dimension of the container.
 * <p>
 * -----------------------------------------------------------------------
 * </p>
 *
 * @version 5.6.2001
 * @author Andreas Poth
 *         <p>
 */
public interface GM_Position
{
  double MUTE = 0.000001;

  /**
   * returns the x-value of the point
   */
  double getX( );

  /**
   * returns the y-value of the point
   */
  double getY( );

  /**
   * returns the z-value of the point
   */
  double getZ( );

  /**
   * Returns the dimension (usually 2 or 3) of this position.
   */
  short getCoordinateDimension( );

  /**
   * Discouraged!<br>
   * returns the x- and y-value of the point as a two dimensional array the first field contains the x- the second field
   * the y-value.
   */
  double[] getAsArray( );

  /**
   * translates the coordinates of the position. the first coordinate of the position will be translated by the first
   * field of <tt>d</tt> the second coordinate by the second field of <tt>d</tt> and so on... <br>
   */
  void translate( double[] d );

  double getDistance( GM_Position position );

  /**
   * This function transforms the position.
   *
   * @param sourceCRS
   *          The source coordinate system.
   * @param targetCRS
   *          The target coordinate system.
   * @return The transformed position.
   */
  GM_Position transform( String sourceCRS, String targetCRS ) throws GeoTransformerException;

  Object clone( );
}