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
package org.kalypso.ogc.gml.map.listeners;

import org.kalypso.ogc.gml.IKalypsoLayerModell;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Point;

/**
 * This interface provides a set of functions for listeners, that should be notified in special events of the mapPanel.
 * 
 * @author Holger Albert
 */
public interface IMapPanelListener
{
  void onExtentChanged( final IMapPanel source, final GM_Envelope oldExtent, final GM_Envelope newExtent );

  void onMapModelChanged( final IMapPanel source, final IKalypsoLayerModell oldModel, final IKalypsoLayerModell newModel );

  /**
   * This function is invoked from the mapPanel, in cases its message has changed.
   * 
   * @param message
   *          The new message, which is set in the mapPanel.
   */
  void onMessageChanged( final IMapPanel source, final String message );

  void onStatusChanged( final IMapPanel source );

  // REMARK: we are using mouseX and mouseY (instead of some point) in order to be independent of the window toolkit
  // Should be changed as soon as we totally switch so SWT
  void onMouseMoveEvent( final IMapPanel source, GM_Point gmPoint, final int mouseX, int mouseY );
}
