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
package org.kalypso.model.wspm.ui.view.chart;

import org.eclipse.swt.widgets.Shell;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.ogc.gml.om.table.handlers.IComponentUiHandlerProvider;

import de.openali.odysseus.chart.framework.model.mapper.IAxis;
import de.openali.odysseus.chart.framework.model.mapper.registry.IAxisRegistry;

/**
 * A layer provider provides layers for the view, depending on the specific profile type.
 * 
 * @author kimwerner
 */
public interface IProfilLayerProvider
{
  /**
   * if the layer depends on other layers or properties, create all required things here. </p> if there is nothing to do
   * see getLayer( final String layerId, final ProfilChartView view ) </p> return all affected layer
   */
  void addLayerToProfile( Shell shell, final IProfile profil, final String layerId );

  /**
   * return the layers not shown yet, but addable.
   * 
   * @FIXME: return some wrapper for a potential layer instead.
   */
  LayerDescriptor[] getAddableLayers( final ProfilChartModel chartModel );

  IProfilChartLayer[] createLayers( final IProfile profile, Object result );

  IComponentUiHandlerProvider getComponentUiHandlerProvider( final IProfile profile );

  IAxis[] registerAxis( IAxisRegistry mapperRegistry );
}