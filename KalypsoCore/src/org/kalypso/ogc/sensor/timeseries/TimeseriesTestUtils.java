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
package org.kalypso.ogc.sensor.timeseries;

import java.util.Calendar;

import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.impl.SimpleObservation;
import org.kalypso.ogc.sensor.impl.SimpleTupleModel;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;

/**
 * @author Dirk Kuch
 */
public final class TimeseriesTestUtils
{
  private TimeseriesTestUtils( )
  {
  }

  /**
   * Create a test time series with a date axis and one default axis for each of the given axisTypes. A tupple-model is
   * randomly generated.
   * 
   * @param axisTypes
   *          as seen in TimeserieConstants.TYPE_*
   * @param amountRows
   *          amount of rows of the TuppleModel that is randomly created
   * @throws SensorException
   */
  public static IObservation createTestTimeserie( final String[] axisTypes, final int amountRows, final boolean allowNegativeValues ) throws SensorException
  {
    final IAxis[] axes = new IAxis[axisTypes.length + 1];
    axes[0] = TimeseriesUtils.createDefaultAxis( ITimeseriesConstants.TYPE_DATE, true );
    for( int i = 0; i < axisTypes.length; i++ )
    {
      axes[i + 1] = TimeseriesUtils.createDefaultAxis( axisTypes[i] );
    }

    final SimpleObservation obs = new SimpleObservation( axes );
    final SimpleTupleModel model = new SimpleTupleModel( axes );

    final Calendar cal = Calendar.getInstance();
    for( int i = 0; i < amountRows; i++ )
    {
      final Object[] tupple = new Object[axes.length];
      tupple[0] = cal.getTime();

      for( int j = 1; j < tupple.length; j++ )
      {
        if( allowNegativeValues )
          tupple[j] = new Double( Math.random() * 100 * (Math.random() > .5 ? 1 : -1) );
        else
          tupple[j] = new Double( Math.random() * 100 );
      }

      model.addTuple( tupple );

      cal.add( Calendar.DAY_OF_YEAR, 1 );
    }

    obs.setValues( model );

    return obs;
  }
}
