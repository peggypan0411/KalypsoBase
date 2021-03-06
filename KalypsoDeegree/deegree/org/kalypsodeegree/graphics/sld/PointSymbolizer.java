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

/**
 * Used to render a "graphic" at a point. If a line-string or polygon geometry is used with this symbol, then the
 * semantic is to use the centroid of the geometry, or any similar representative point. The meaning of the contained
 * elements are discussed with the element definitions below. If the Geometry element is omitted, then the "default"
 * geometry for the feature type is used. (Many feature types will have only one geometry attribute.) If the Graphic
 * element is omitted, then nothing will be plotted.
 * <p>
 * ----------------------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version $Revision$ $Date$
 */
public interface PointSymbolizer extends Symbolizer
{

  /**
   * A Graphic is a "graphic symbol" with an inherent shape, color, and size. Graphics can either be referenced from an
   * external URL in a common format (such as GIF or SVG) or may be derived from a Mark. Multiple external URLs may be
   * referenced with the semantic that they all provide the same graphic in different formats. The "hot spot" to use for
   * rendering at a point or the start and finish handle points to use for rendering a graphic along a line must either
   * be inherent in the external format or are system- dependent. The default size of an image format (such as GIF) is
   * the inherent size of the image. The default size of a format without an inherent size is 16 pixels in height and
   * the corresponding aspect in width. If a size is specified, the height of the graphic will be scaled to that size
   * and the corresponding aspect will be used for the width. The default if neither an ExternalURL nor a Mark is
   * specified is to use the default Mark with a size of 6 pixels. The size is in pixels and the rotation is in degrees
   * clockwise, with 0 (default) meaning no rotation. In the case that a Graphic is derived from a font-glyph Mark, the
   * Size specified here will be used for the final rendering. Allowed CssParameters are "opacity", "size", and
   * "rotation".
   * 
   * @return the graphic of the point
   */
  Graphic getGraphic( );

  /**
   * Sets the Graphic for the Point.
   * 
   * @param graphic
   *          the graphic of the point
   */
  void setGraphic( Graphic graphic );
}