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
package org.kalypso.zml.ui.core.element;

import org.kalypso.commons.java.lang.Objects;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ObservationTokenHelper;
import org.kalypso.ogc.sensor.template.ObsView;
import org.kalypso.zml.core.base.TSLinkWithName;
import org.kalypso.zml.ui.KalypsoZmlUI;
import org.kalypso.zml.ui.core.provider.style.ZmlStyleSet;
import org.kalypso.zml.ui.core.registry.KodRegistry;

import de.openali.odysseus.chart.factory.config.StyleFactory;
import de.openali.odysseus.chart.framework.model.style.IStyleSet;
import de.openali.odysseus.chart.framework.model.style.impl.StyleSet;
import de.openali.odysseus.chartconfig.x020.LayerType;

/**
 * @author Dirk Kuch
 */
public class ZmlLinkDiagramElement extends AbstractTsLinkDiagramElement
{
  public ZmlLinkDiagramElement( final TSLinkWithName link )
  {
    super( link );
  }

  public ObsView.ItemData getItemData( )
  {
    return new ObsView.ItemData( getLink().isEditable(), getLink().getColor(), getLink().getStroke(), getLink().isShowLegend() );
  }

  @Override
  public IStyleSet getStyleSet( final String type )
  {
    final ZmlStyleSet zmlStyleSet = new ZmlStyleSet( getItemData() );
    final StyleSet styleSet = zmlStyleSet.getStyleSet();
    if( styleSet.isEmpty() )
    {
      try
      {
        final KodRegistry registy = KodRegistry.getInstance();
        final LayerType layer = registy.getLayer( type );
        if( Objects.isNull( layer ) )
          return null;

        return StyleFactory.createStyleSet( layer.getStyles() );
      }
      catch( final Throwable t )
      {
        KalypsoZmlUI.getDefault().getLog().log( StatusUtilities.statusFromThrowable( t ) );
      }
    }

    return styleSet;
  }

  @Override
  public String getTitle( final IAxis axis )
  {
    final String tokenizedName = getLink().getName();
    final IObservation observation = getSource().getObsProvider().getObservation();
    return ObservationTokenHelper.replaceTokens( tokenizedName, observation, axis );
  }
}
