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
package org.kalypso.ogc.gml.map.widgets.advanced.selection;

import org.kalypso.ogc.gml.map.widgets.advanced.IAdvancedWidgetChangeListener;
import org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidget.EDIT_MODE;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Polygon;

/**
 * @author Dirk Kuch
 */
public interface IAdvancedSelectionWidgetDataProvider
{
  Feature[] query( GM_Polygon surface, EDIT_MODE editMode );

  Feature[] getLastQuery( );

  /**
   * features which are sorted out and not returned from the last query
   */
  Feature[] getMissed( );

  /**
   * post selection changes
   */
  void post( final Feature[] features, EDIT_MODE mode ) throws Exception;

  public void addSelectionChangeListener( IAdvancedWidgetChangeListener listener );

  public void removeSelectionChangeListener( IAdvancedWidgetChangeListener listener );

}
