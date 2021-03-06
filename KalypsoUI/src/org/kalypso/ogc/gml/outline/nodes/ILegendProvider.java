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
package org.kalypso.ogc.gml.outline.nodes;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * This interface provides functions for providers that should return legends.
 * 
 * @author Holger Albert
 */
public interface ILegendProvider
{
  /**
   * This function returns an image, containing the legend, if one is available. Otherwise it will return null.
   * 
   * @param whiteList
   *          The ids of the nodes (themes), which are to be shown. May be empty or null. In these cases, all nodes
   *          (themes) will be shown.
   * @param onlyVisible
   *          True, if only visible theme nodes should be asked.
   * @param font
   *          This font will be used for the self created text of the legend.
   * @return An legend graphic or null.
   */
  ImageDescriptor getLegendImage( );

  /**
   * Return <code>true</code>, if the legend image already contains the label. Usually this is the case for WMS legend
   * images.
   */
  boolean isLabelInImage( );
}