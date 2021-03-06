/*--------------- Kalypso-Header ------------------------------------------

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

 --------------------------------------------------------------------------*/

package org.kalypso.ogc.sensor.timeseries.wq;

import java.io.StringReader;

import org.kalypso.commons.java.lang.Strings;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.ogc.sensor.metadata.MetadataList;
import org.kalypso.ogc.sensor.timeseries.wq.wechmann.WechmannFactory;
import org.kalypso.ogc.sensor.timeseries.wq.wqtable.WQTableFactory;
import org.xml.sax.InputSource;

/**
 * @author schlienger
 */
public final class WQFactory
{
  private WQFactory( )
  {
    // empty
  }

  /**
   * Loads the appropriate WQ-converter depending on the metadata of the given observation
   */
  public static IWQConverter createWQConverter( final IObservation obs ) throws SensorException
  {
    return createWQConverter( obs.getMetadataList() );
  }

  public static IWQConverter createWQConverter( final MetadataList metadata ) throws SensorException
  {
    try
    {
      if( metadata.containsKey( ITimeseriesConstants.MD_WQ_WECHMANN ) )
      {
        final String wechmann = metadata.getProperty( ITimeseriesConstants.MD_WQ_WECHMANN );

        return WechmannFactory.parse( new InputSource( new StringReader( wechmann ) ) );
      }
      else if( metadata.containsKey( ITimeseriesConstants.MD_WQ_TABLE ) )
      {
        final String wqtable = metadata.getProperty( ITimeseriesConstants.MD_WQ_TABLE );
        if( Strings.isEmpty( wqtable ) )
          return null;

        return WQTableFactory.parse( new InputSource( new StringReader( wqtable ) ) );
      }

      return null;
    }
    catch( final WQException e )
    {
      throw new SensorException( e );
    }
  }
}
