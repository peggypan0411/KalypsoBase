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
package org.kalypso.ogc.sensor.proxy;

import java.io.StringReader;

import org.kalypso.binding.ratingtable.RatingTableList;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.ogc.sensor.metadata.MetadataList;
import org.kalypso.ogc.sensor.timeseries.wq.WQTimeserieProxy;
import org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTableFactory;
import org.xml.sax.InputSource;

/**
 * AutoProxyFactory: this class can create proxy of an IObservation based on its metadata.
 * 
 * @author schlienger
 */
public final class AutoProxyFactory
{
  private AutoProxyFactory( )
  {
    // do not instanciate
  }

  /**
   * Return a proxy IObservation that may be a proxy of the original observation if sufficient information is found to
   * create such a proxy.
   * <p>
   * For instance, some observations have WQ-Information in their metadata that allows one to create a WQ-Observation
   * from it.
   * 
   * @param obs
   * @return either a proxy observation or the original observation
   */
  public static IObservation proxyObservation( final IObservation obs )
  {
    // direct assignment, just to be able to use 'proxy' as name everywhere
    IObservation proxy = obs;

    proxy = proxyForWQTable( proxy );
    proxy = proxyForWQWechmann( proxy );

    return proxy;
  }

  private static IObservation proxyForWQTable( final IObservation obs )
  {
    final MetadataList mdl = obs.getMetadataList();

    final String wq = mdl.getProperty( ITimeseriesConstants.MD_WQ_TABLE, "" ); //$NON-NLS-1$

    if( wq.length() > 0 )
    {
      final StringReader sr = new StringReader( wq );
      try
      {
        final RatingTableList tableList;
        tableList = WQTableFactory.parseSimple( new InputSource( sr ) );
        sr.close();

        final String type1 = tableList.getFromType();
        final String type2 = tableList.getToType();

        boolean foundType1 = false;
        boolean foundType2 = false;

        final IAxis[] axes = obs.getAxes();
        for( final IAxis axe : axes )
        {
          if( axe.getType().equals( type1 ) )
            foundType1 = true;
          else if( axe.getType().equals( type2 ) )
            foundType2 = true;
        }

        // directly return original observation if none or all of the types found
        if( !foundType1 && !foundType2 || foundType1 && foundType2 )
          return obs;

        final String realAxisType = foundType1 ? type1 : type2;
        final String proxyAxisType = foundType1 ? type2 : type1;

        final WQTimeserieProxy wqf = new WQTimeserieProxy( realAxisType, proxyAxisType, obs );
        return wqf;
      }
      catch( final Exception e )
      {
        final StackTraceElement trace = e.getStackTrace()[0];
        System.out.println( trace.getClassName() + ":" + trace.getMethodName() + "#" + trace.getLineNumber() ); //$NON-NLS-1$ //$NON-NLS-2$
        // e.printStackTrace();
        return obs;
      }
      finally
      {
        sr.close();
      }
    }

    return obs;
  }

  private static IObservation proxyForWQWechmann( final IObservation obs )
  {
    final MetadataList mdl = obs.getMetadataList();

    final String wq = mdl.getProperty( ITimeseriesConstants.MD_WQ_WECHMANN, "" ); //$NON-NLS-1$

    if( wq.length() > 0 )
    {
      boolean foundW = false;
      boolean foundQ = false;
      final IAxis[] axes = obs.getAxes();
      for( final IAxis axe : axes )
      {
        if( axe.getType().equals( ITimeseriesConstants.TYPE_RUNOFF ) )
          foundQ = true;
        else if( axe.getType().equals( ITimeseriesConstants.TYPE_WATERLEVEL ) )
          foundW = true;
      }

      // directly return original observation if no W nor Q or if both
      // already present
      if( !foundW && !foundQ || foundW && foundQ )
        return obs;

      final String source;
      final String dest;

      if( foundW )
      {
        source = ITimeseriesConstants.TYPE_WATERLEVEL;
        dest = ITimeseriesConstants.TYPE_RUNOFF;
      }
      else
      {
        source = ITimeseriesConstants.TYPE_RUNOFF;
        dest = ITimeseriesConstants.TYPE_WATERLEVEL;
      }

      // now that we have wq-params and that we know the type of the
      // axis, let's say the filter can be created
      final WQTimeserieProxy wqf = new WQTimeserieProxy( source, dest, obs );
      return wqf;
    }

    return obs;
  }
}