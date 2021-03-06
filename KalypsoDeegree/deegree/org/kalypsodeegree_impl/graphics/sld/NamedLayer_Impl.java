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
package org.kalypsodeegree_impl.graphics.sld;

import org.kalypsodeegree.graphics.sld.LayerFeatureConstraints;
import org.kalypsodeegree.graphics.sld.NamedLayer;
import org.kalypsodeegree.graphics.sld.Style;
import org.kalypsodeegree.xml.Marshallable;

/**
 * A NamedLayer uses the "name" attribute to identify a layer known to the WMS and can contain zero or more styles,
 * either NamedStyles or UserStyles. In the absence of any styles the default style for the layer is used.
 * <p>
 * ----------------------------------------------------------------------
 * </p>
 *
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @version $Revision$ $Date$
 */
public class NamedLayer_Impl extends Layer_Impl implements NamedLayer, Marshallable
{
  /**
   * constructor initializing the class with the <NamedLayer>
   */
  NamedLayer_Impl( final String name, final LayerFeatureConstraints layerFeatureConstraints, final Style[] styles )
  {
    super( name, layerFeatureConstraints, styles );
  }

  /**
   * exports the content of the Font as XML formated String
   *
   * @return xml representation of the Font
   */
  @Override
  public String exportAsXML( )
  {
    final StringBuffer sb = new StringBuffer( 1000 );
    sb.append( "<NamedLayer>" );

    final String name = getName();
    sb.append( "<Name>" ).append( name ).append( "</Name>" );

    final LayerFeatureConstraints layerFeatureConstraints = getLayerFeatureConstraints();
    if( layerFeatureConstraints != null )
    {
      sb.append( ((Marshallable) layerFeatureConstraints).exportAsXML() );
    }

    final Style[] styles = getStyles();
    for( final Style style : styles )
    {
      sb.append( ((Marshallable) style).exportAsXML() );
    }
    sb.append( "</NamedLayer>" );

    return sb.toString();
  }
}