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
package org.kalypsodeegree.model.coverage;

import java.net.URL;
import java.util.ArrayList;

import org.kalypsodeegree.tools.ParameterList;

/**
 * 
 * Interface for describing a coverage layer with a multi level resolution splitted into several <tt>Tile</tt>s.
 * 
 * <p>
 * ----------------------------------------------------------------------
 * </p>
 * 
 * @author Andreas Poth
 * @version $Revision$ $Date$
 *          <p>
 */
public interface CVDescriptor
{

  /**
   * returns a description of the coverage layer as defined in the capabilities section of the OGC WCS sppecification
   * 0.7
   */
  CoverageLayer getCoverageLayer();

  /**
   * returns the resource where to access a preview of the coverage
   */
  URL getPreviewResource();

  /**
   * returns the top <tt>Level</tt> of the Layer
   */
  Level getLevel();

  /**
   * returns a list of properties assigned to the <tt>Level</tt>. The properties may be overwritten by the embeded
   * <tt>Level</tt>s.
   */
  ParameterList getProperties();

  /**
   * @return a list of the first-level ETJRanges.
   * @author ETj
   */
  ArrayList getRanges();

}
