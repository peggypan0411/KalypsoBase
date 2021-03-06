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
package org.kalypsodeegree.graphics.sld;

import java.util.Map;

/**
 * This is the top level interface of <tt>Fill</tt> and <tt>Stroke</tt> defining the methods <tt>getGraphicFill()</tt>
 * and <tt>getCssParameters()</tt> that are common to both.
 * <p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version $Revision$ $Date$
 */
public interface Drawing
{
  /**
   * The GraphicFill element both indicates that a stipple-fill repeated graphic will be used and specifies the fill
   * graphic.
   * 
   * @return the GraphicFill-Element
   */
  GraphicFill getGraphicFill( );

  void setGraphicFill( final GraphicFill graphicFill );

  /**
   * A simple SVG/CSS2 styling parameters are given with the CssParameter element.
   * <p>
   * </p>
   * This method is for technical use. The user should access the specialized methods of the derived classes.
   * 
   * @return the CssParameters
   * @deprecated Use {#getParameter} instead
   */
  @Deprecated
  Map<String, CssParameter> getCssParameters( );

  CssParameter getParameter( final String key );

  /**
   * A simple SVG/CSS2 styling parameters are given with the CssParameter element.
   * <p>
   * </p>
   * This method adds a CssParameter to a given set of CssParameters.
   * <p>
   * 
   * @param key
   *          the key of the object to insert
   * @param value
   *          the value of the object to insert
   */
  void addCssParameter( String key, CssParameter value );

  /**
   * A simple SVG/CSS2 styling parameters are given with the CssParameter element.
   * <p>
   * </p>
   * This method removes a CssParameter from a given set of CssParameters.
   * 
   * @param key
   *          the key of the object to remove
   */
  void removeCssParameter( String key );
}