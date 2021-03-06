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
package org.kalypsodeegree.graphics.transformation;

import java.awt.Rectangle;

import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Position;

/**
 * <code>GeoTransformInterface</code> declares the methods which have to be implemented by each class that executes a
 * geographical coordinate transformation.
 *
 * @author Andreas Poth poth@lat-lon.de
 * @version 28.12.2000
 */
public interface GeoTransform
{
  public double getSourceX( double xdest );

  public double getDestX( double xsource );

  public double getSourceY( double ydest );

  public double getDestY( double ysource );

  public void setSourceRect( GM_Envelope rect );

  public void setSourceRect( double xMin, double yMin, double xMax, double yMax, String sourceCoordinateSystem );

  public GM_Envelope getSourceRect( );

  public void setDestRect( GM_Envelope rect );

  public void setDestRect( double xMin, double yMin, double xMax, double yMax, String destCoordinateSystem );

  public GM_Position getSourcePoint( GM_Position point );

  public GM_Position getDestPoint( GM_Position point );

  public double getDestWidth( );

  public double getDestHeight( );

  public double getScale( );

  Rectangle getDestRect( );
}