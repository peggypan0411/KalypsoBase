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
package org.kalypso.layerprovider;

import java.net.URL;

import org.kalypso.observation.IObservation;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.swtchart.chart.Chart;
import org.kalypso.swtchart.chart.axis.registry.IAxisRegistry;
import org.kalypso.swtchart.chart.layer.IChartLayer;
import org.kalypso.swtchart.chart.layer.ILayerProvider;
import org.ksp.chart.configuration.LayerType;

/**
 * LayerProvider for TupleResult Data - provides a TupleResultLineChartLayer
 * 
 * @author schlienger
 * @author burtscher
 */
public class DefaultTupleResultLayerProvider implements ILayerProvider
{
  private IObservation<TupleResult> m_obs;

  private IComponent m_domainComponent;

  private IComponent m_valueComponent;

  private IAxisRegistry m_registry;

  private String m_domAxisID;

  private String m_valAxisID;

  public DefaultTupleResultLayerProvider( )
  {
  }

  /**
   * alter Konstruktor - wird nicht mehr benutzt
   * 
   * @param registry
   * @param obs
   * @param domainComponent
   * @param valueComponent
   * @param domAxisID
   * @param valAxisID
   */
  public DefaultTupleResultLayerProvider( final IAxisRegistry registry, final IObservation<TupleResult> obs, final IComponent domainComponent, final IComponent valueComponent, final String domAxisID, final String valAxisID )
  {
    m_registry = registry;
    m_obs = obs;

    m_domainComponent = domainComponent;
    m_valueComponent = valueComponent;

    m_domAxisID = domAxisID;
    m_valAxisID = valAxisID;
  }

  public void init( Chart chart, LayerType lpt )
  {
  }

  /**
   * @see de.kalypso.swtchart.layer.ILayerProvider#getLayers()
   */
  public IChartLayer getLayer( URL context )
  {
    IChartLayer icl = null;
    if( m_obs != null )
    {
      final TupleResult result = m_obs.getResult();
      // icl = new TupleResultLineChartLayer( result, m_domainComponent, m_valueComponent, m_registry.getAxis(
      // m_domAxisID ), m_registry.getAxis( m_valAxisID ) );
      icl = new TupleResultLineChartLayer( result, m_domainComponent.getId(), m_valueComponent.getId(), m_registry.getAxis( m_domAxisID ), m_registry.getAxis( m_valAxisID ) );
    }
    return icl;
  }
}
