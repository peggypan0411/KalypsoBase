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
package org.kalypso.ogc.gml.map.handlers.parts;

import org.eclipse.core.commands.ExecutionException;
import org.kalypso.ogc.gml.command.ChangeExtentCommand;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.map.handlers.MapHandlerUtils;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;

/**
 * This part pans to a feature.
 * 
 * @author Holger Albert
 */
public class PanToFeaturePart
{
  /**
   * A view part containing a map panel.
   */
  private final IMapPanel m_mapPanel;

  /**
   * The constructor.
   * 
   * @param part
   *          A view part containing a map panel.
   */
  public PanToFeaturePart( final IMapPanel mapPanel )
  {
    m_mapPanel = mapPanel;
  }

  /**
   * This function changes the extent of the map panel, so that it centers the centroid if the first geometry of the
   * given feature.
   * 
   * @param Feature
   *          The feature to center to.
   */
  public void panTo( final Feature feature ) throws ExecutionException
  {
    /* Get all geometrys. */
    final GM_Object[] geometries = feature.getGeometryPropertyValues();

    /* If the feature has no geometrys, there is nothing to do. */
    if( geometries.length == 0 )
      return;

    /* If there are some geometries, pan to the first one. */
    final GM_Object geometry = geometries[0];

    /* The center point. */
    final GM_Point centroid = geometry.getCentroid();

    /* The extent of the map panel. */
    final GM_Envelope boundingBox = m_mapPanel.getBoundingBox();
    if( boundingBox == null )
      return;

    /* Get the new paned bounding box to the centroid of the geometry. */
    final GM_Envelope paned = boundingBox.getPaned( centroid );

    /* Finally set the bounding box. */
    MapHandlerUtils.postMapCommandChecked( m_mapPanel, new ChangeExtentCommand( m_mapPanel, paned ), null );
  }
}