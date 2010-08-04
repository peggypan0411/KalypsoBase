/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and
 
 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de
 
 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ogc.sensor.timeseries.forecast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.kalypso.ogc.sensor.DateRange;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.filter.filters.AbstractObservationFilter;
import org.kalypso.ogc.sensor.request.IRequest;
import org.kalypso.ogc.sensor.timeseries.TimeserieUtils;
import org.kalypso.ogc.sensor.timeseries.merged.MergedObservation;
import org.kalypso.ogc.sensor.timeseries.merged.ObservationSource;

/**
 * MergeFilter
 * 
 * @author schlienger
 */
public class ForecastFilter extends AbstractObservationFilter
{
  private IObservation[] m_observations = null;

  /**
   * @see org.kalypso.ogc.sensor.filter.IObservationFilter#initFilter(java.lang.Object,
   *      org.kalypso.ogc.sensor.IObservation, java.net.URL)
   */
  public void initFilter( final IObservation[] observations, final IObservation baseObs, final URL context ) throws SensorException
  {
    super.initFilter( observations, baseObs, context );

    m_observations = observations;
  }

  /**
   * @see org.kalypso.ogc.sensor.IObservation#getValues(org.kalypso.ogc.sensor.request.IRequest)
   * @param args
   *          if <code>args!=null</code>, then the args of the inner observations will be overwritten, usually a
   *          forecastfilter should expect <code>null</code> here.
   */
  @Override
  public ITupleModel getValues( final IRequest args ) throws SensorException
  {
    final List<ObservationSource> sources = new ArrayList<ObservationSource>();
    final DateRange daterange = TimeserieUtils.getDateRange( args );
    for( final IObservation observation : m_observations )
    {
      sources.add( new ObservationSource( null, daterange, null, observation ) );
    }

    final MergedObservation observation = new MergedObservation( getHref(), sources.toArray( new ObservationSource[] {} ) );
    return observation.getValues( args );
  }
}
