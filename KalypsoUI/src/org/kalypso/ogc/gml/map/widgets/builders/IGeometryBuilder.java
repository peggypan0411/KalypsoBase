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

import java.awt.Graphics;
import java.awt.Point;

import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;

/**
 * All builders which should handle geometrys should implement this interface.
 * 
 * @author Holger Albert
 */
public interface IGeometryBuilder
{
  /**
   * Adds a new point to the geometry.
   * 
   * @param p
   *          The point to be added.
   * @return null if the cnt is not reached, otherwise the geometry.
   */
  public GM_Object addPoint( final GM_Point p ) throws Exception;

  /**
   * This method must be invoked if the geometry should be created (e.g. at double click).
   * <p>
   * If this method is invoked multiple times, always the same geometry should be returned. TODO: why? not really needed and prohibts use as measure tool
   */
  public GM_Object finish( ) throws Exception;

  /**
   * Paints the geometry, while drawing.
   */
  public void paint( final Graphics g, final GeoTransform projection, final Point p );

  /**
   * right click? -> remove last setted geometry point
   */
  public void removeLastPoint( );

  public void reset( );
}
