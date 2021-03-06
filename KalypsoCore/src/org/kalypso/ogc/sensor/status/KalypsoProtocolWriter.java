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
package org.kalypso.ogc.sensor.status;

import java.util.logging.Level;

import org.kalypso.contribs.java.util.logging.ILogger;
import org.kalypso.contribs.java.util.logging.LoggerUtilities;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.metadata.IMetadataConstants;
import org.kalypso.ogc.sensor.metadata.MetadataList;

/**
 * KalypsoProcolHelper analyses observations' values and produces a kind of protocol.
 * 
 * @author schlienger
 */
public final class KalypsoProtocolWriter
{
  private KalypsoProtocolWriter( )
  {
    // not to be instanciated
  }

  public static void analyseValues( final IObservation observation, final ITupleModel model, final ILogger logger ) throws SensorException
  {
    analyseValues( new IObservation[] { observation }, new ITupleModel[] { model }, logger );
  }

  /**
   * Analyses the given tupple models and reports possible errors (according to status of tupples).
   */
  public static void analyseValues( final IObservation[] observations, final ITupleModel[] models, final ILogger logger ) throws SensorException
  {
    if( observations.length != models.length )
      throw new IllegalArgumentException( Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoProtocolWriter.1" ) ); //$NON-NLS-1$

    final StringBuffer bf = new StringBuffer();

    for( int i = 0; i < models.length; i++ )
    {
      final ITupleModel tuppleModel = models[i];
      final IAxis[] statusAxes = KalypsoStatusUtils.findStatusAxes( tuppleModel.getAxes() );
      final int[] mergedStati = new int[statusAxes.length];
      for( int iAxes = 0; iAxes < mergedStati.length; iAxes++ )
        mergedStati[iAxes] = KalypsoStati.BIT_OK;

      if( statusAxes.length != 0 )
      {
        final IObservation observation = observations[i];
        for( int ix = 0; ix < tuppleModel.size(); ix++ )
        {
          // clear reporting buffer
          bf.setLength( 0 );

          for( int iAxes = 0; iAxes < statusAxes.length; iAxes++ )
          {
            final IAxis axis = statusAxes[iAxes];
            final Number nb = (Number) tuppleModel.get( ix, axis );
            final int statusValue = nb == null ? 0 : nb.intValue();

            mergedStati[iAxes] = mergedStati[iAxes] | statusValue;
          }
        }

        final MetadataList metadataList = observation.getMetadataList();

        for( int iAxes = 0; iAxes < mergedStati.length; iAxes++ )
        {
          final IAxis axis = statusAxes[iAxes];

          final String obsName = observation.getName();
          final String type = KalypsoStatusUtils.getAxisNameFor( axis );

          final StringBuffer sb = new StringBuffer( Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoProtocolWriter.0" ) ); //$NON-NLS-1$
          sb.append( obsName );

          final String desc = metadataList.getProperty( IMetadataConstants.MD_DESCRIPTION, "" ); //$NON-NLS-1$
          if( desc.length() > 0 )
          {
            // desc += " aus " + observations[i].getMetadataList().getProperty( ObservationConstants.MD_ORIGIN,
            // "<unbekannt>" );
            sb.append( " (" ); //$NON-NLS-1$
            sb.append( desc );
            sb.append( ")" );//$NON-NLS-1$
          }

          sb.append( ", " );//$NON-NLS-1$
          sb.append( type );
          sb.append( ", " );//$NON-NLS-1$

          final String message = sb.toString();

          if( axis.isPersistable() && KalypsoStatusUtils.checkMask( mergedStati[iAxes], KalypsoStati.BIT_CHECK ) )
            logger.log( Level.FINE, LoggerUtilities.CODE_SHOW_DETAILS, message + Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoProtocolWriter.3" ) ); //$NON-NLS-1$
          else if( !axis.isPersistable() && KalypsoStatusUtils.checkMask( mergedStati[iAxes], KalypsoStati.BIT_DERIVATION_ERROR ) )
            logger.log( Level.FINE, LoggerUtilities.CODE_SHOW_DETAILS, message + Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoProtocolWriter.2" ) ); //$NON-NLS-1$
        }

      }
    }
  }
}