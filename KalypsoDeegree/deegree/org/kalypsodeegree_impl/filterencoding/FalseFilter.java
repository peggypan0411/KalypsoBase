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
package org.kalypsodeegree_impl.filterencoding;

import org.kalypsodeegree.model.feature.Feature;

/**
 * Abstract superclass representing <Filter>elements (as defined in the Filter DTD). A <Filter>element either consists
 * of (one or more) FeatureId-elements or one operation-element. This is reflected in the two implementations
 * FeatureFilter and ComplexFilter.
 * <p>
 * 
 * @author Markus Schneider
 * @version $Revision$
 */
public class FalseFilter extends AbstractFilter
{
  /**
   * Calculates the <tt>Filter</tt>'s logical value (false).
   * <p>
   * 
   * @param feature
   *          (in this special case irrelevant)
   * @return false (always)
   */
  @Override
  public boolean evaluate( final Feature feature )
  {
    return false;
  }

  /** Produces an indented XML representation of this object. */
  @Override
  public StringBuffer toXML( )
  {
    final StringBuffer sb = new StringBuffer();
    sb.append( "<ogc:Filter xmlns:ogc='http://www.opengis.net/ogc'>" );
    sb.append( "<False/>" );
    sb.append( "</ogc:Filter>\n" );
    return sb;
  }
}