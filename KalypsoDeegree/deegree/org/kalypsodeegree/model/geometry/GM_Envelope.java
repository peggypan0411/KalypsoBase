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
 * a boundingbox as child of a GM_Polygon isn't part of the iso19107 spec but it simplifies the geometry handling within
 * jago
 */
public interface GM_Envelope extends Cloneable
{
  QName ENVELOPE_ELEMENT = new QName( NS.GML3, "Envelope" ); //$NON-NLS-1$

  QName PROPERTY_LOWER_CORNER = new QName( NS.GML3, "lowerCorner" ); //$NON-NLS-1$

  QName PROPERTY_UPPER_CORNER = new QName( NS.GML3, "upperCorner" ); //$NON-NLS-1$

  QName PROPERTY_COORD = new QName( NS.GML3, "coord" ); //$NON-NLS-1$

  QName PROPERTY_POS = new QName( NS.GML3, "pos" ); //$NON-NLS-1$

  QName PROPERTY_COORDINATES = new QName( NS.GML3, "coordinates" ); //$NON-NLS-1$

  /**
   * returns the width of bounding box
   */
  double getWidth( );

  /**
   * returns the height of bounding box
   */
  double getHeight( );

  public double getMinX( );

  public double getMinY( );

  public double getMaxX( );

  public double getMaxY( );

  /**
   * returns the minimum coordinates of bounding box
   */
  GM_Position getMin( );

  /**
   * returns the maximum coordinates of bounding box
   */
  GM_Position getMax( );

  /**
   * returns true if the bounding box contains the submitted position
   */
  boolean contains( double x, double y );

  /**
   * returns true if the bounding box contains the submitted position
   */
  boolean contains( GM_Position position );

  /**
   * returns true if this envelope intersects the submitted envelope
   */
  boolean intersects( GM_Envelope bb );

  /**
   * returns true if all positions of the submitted bounding box are within this bounding box
   */
  boolean contains( GM_Envelope bb );

  /**
   * returns a new GM_Envelope object representing the intersection of this GM_Envelope with the specified GM_Envelope.
   */
  GM_Envelope createIntersection( GM_Envelope bb );

  /**
   * merges two GM_Envelops and returns the minimum envelope containing both.
   *
   * @return merged envelope
   */
  GM_Envelope getMerged( GM_Envelope envelope );

  /**
   * Creates a new envelope 'buffered' by the given distance.
   */
  GM_Envelope getBuffer( double b );

  GM_Envelope getPaned( GM_Point centroid );

  GM_Envelope getMerged( GM_Position position );

  public Object clone( );

  /**
   * This function returns the coordinate system, the coordinates of the contained positions are in.
   *
   * @return The coordinate system.
   */
  public String getCoordinateSystem( );

  /**
   * Checks if this point is completly equal to the submitted geometry
   *
   * @param exact
   *          If <code>false</code>, the positions are compardd by {@link GM_Position#equals(Object, false)}
   * @see GM_Position#equals(Object, boolean)
   */
  public boolean equals( final Object other, final boolean exact );

}