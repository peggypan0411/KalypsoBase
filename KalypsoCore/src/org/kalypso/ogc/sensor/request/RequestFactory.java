/*
 * --------------- Kalypso-Header --------------------------------------------
 *
 * This file is part of kalypso. Copyright (C) 2004, 2005 by:
 *
 * Technical University Hamburg-Harburg (TUHH) Institute of River and coastal engineering Denickestr. 22 21073 Hamburg,
 * Germany http://www.tuhh.de/wb
 *
 * and
 *
 * Bjoernsen Consulting Engineers (BCE) Maria Trost 3 56070 Koblenz, Germany http://www.bjoernsen.de
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Contact:
 *
 * E-Mail: belger@bjoernsen.de schlienger@bjoernsen.de v.doemming@tuhh.de
 *
 * ------------------------------------------------------------------------------------
 */
package org.kalypso.ogc.sensor.request;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.joda.time.Period;
import org.kalypso.commons.bind.JaxbUtilities;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.impl.SimpleObservation;
import org.kalypso.ogc.sensor.metadata.IMetadataConstants;
import org.kalypso.ogc.sensor.metadata.MetadataHelper;
import org.kalypso.ogc.sensor.metadata.MetadataList;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.ogc.sensor.timeseries.AxisUtils;
import org.kalypso.ogc.sensor.timeseries.TimeseriesUtils;
import org.kalypso.ogc.sensor.timeseries.datasource.DataSourceHelper;
import org.kalypso.ogc.sensor.zml.ZmlURLConstants;
import org.kalypso.zml.request.ObjectFactory;
import org.kalypso.zml.request.Request;
import org.xml.sax.InputSource;

/**
 * Factory-class for parsing Zml-Requests
 * 
 * @author schlienger (25.05.2005)
 */
public final class RequestFactory
{
  public static final ObjectFactory OF = new ObjectFactory();

  public static final JAXBContext JC = JaxbUtilities.createQuiet( ObjectFactory.class );

  private RequestFactory( )
  {
    // empty
  }

  /**
   * Parse an href (usually a Zml-Href) that might contain the request specification
   * 
   * @throws SensorException
   *           if the href does not contain a valid request
   */
  public static Request parseRequest( final String href ) throws SensorException
  {
    if( href == null || href.length() == 0 )
      return null;

    // final int i1 = href.indexOf( ZmlURLConstants.TAG_REQUEST1 );
    // if( i1 == -1 )
    // return null;
    // final int i2 = href.indexOf( ZmlURLConstants.TAG_REQUEST2, i1 );
    // if( i2 == -1 )
    final String strRequestXml = XMLStringUtilities.getXMLPart( href, ZmlURLConstants.TAG_REQUEST );
    if( strRequestXml == null )
      return null;

    // throw new SensorException( "URL-fragment does not contain a valid request definition. URL: " + href );
    // final String strRequestXml = href.substring( i1, i2 + ZmlURLConstants.TAG_REQUEST2.length() );
    StringReader sr = null;
    try
    {
      sr = new StringReader( strRequestXml );
      final Request xmlReq = (Request) JC.createUnmarshaller().unmarshal( new InputSource( sr ) );
      sr.close();
      return xmlReq;
    }
    catch( final JAXBException e )
    {
      e.printStackTrace();
      throw new SensorException( e );
    }
    finally
    {
      IOUtils.closeQuietly( sr );
    }
  }

  /**
   * Create a default observation (best effort) according to the request.
   * <p>
   * If there is an extension for org.kalypso.core.requestObsToMerge, then the observation delivered by the instance of
   * this extension is merged with the observation created here. You can use this for instance to have some default
   * W/Q-Table in the meta data.
   * 
   * @return a new instance of SimpleObservation that will satisfy the request specification
   */
  public static SimpleObservation createDefaultObservation( final Request xmlReq )
  {
    final ObservationRequest request = ObservationRequest.createWith( xmlReq );
    final String[] axesTypes = request.getAxisTypes();
    final String[] statusAxes = request.getAxisTypesWithStatus();
    final List<IAxis> axes = new ArrayList<>();
    for( final String axesType : axesTypes )
    {
      final IAxis axis = TimeseriesUtils.createDefaultAxis( axesType );
      axes.add( axis );
      if( Arrays.binarySearch( statusAxes, axesType ) >= 0 )
        axes.add( KalypsoStatusUtils.createStatusAxisFor( axis, true ) );
    }

    final IAxis valueAxis = AxisUtils.findValueAxis( axes.toArray( new IAxis[] {} ) );
    if( valueAxis == null )
      throw new IllegalArgumentException( "Missing value axis in request" ); //$NON-NLS-1$

    axes.add( DataSourceHelper.createSourceAxis( valueAxis ) );

    // create observation instance
    final SimpleObservation obs = new SimpleObservation( "", request.getName(), new MetadataList(), axes.toArray( new IAxis[axes.size()] ) ); //$NON-NLS-1$ //$NON-NLS-2$
    // update meta data
    final MetadataList mdl = obs.getMetadataList();
    mdl.setProperty( IMetadataConstants.MD_NAME, request.getName() != null ? request.getName() : "<?>" ); //$NON-NLS-1$
    mdl.setProperty( IMetadataConstants.MD_ORIGIN, Messages.getString( "org.kalypso.ogc.sensor.request.RequestFactory.3" ) ); //$NON-NLS-1$

    final Period timestep = request.getTimestep();
    if( timestep != null )
      MetadataHelper.setTimestep( mdl, timestep );

    return obs;
  }

  /**
   * Create a default observation using the href
   */
  public static IObservation createDefaultObservation( final String href ) throws SensorException
  {
    final Request xmlReq = parseRequest( href );
    if( xmlReq == null )
      throw new SensorException( Messages.getString( "org.kalypso.ogc.sensor.request.RequestFactory.4" ) ); //$NON-NLS-1$
    return createDefaultObservation( xmlReq );
  }

  public static String buildXmlString( final Request requestType, final boolean includeXmlHeader ) throws JAXBException, IOException
  {
    StringWriter writer = null;
    try
    {
      writer = new StringWriter();

      final Marshaller marshaller = JaxbUtilities.createMarshaller( JC );
      marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE );
      marshaller.marshal( requestType, writer );
      writer.close();
    }
    finally
    {
      IOUtils.closeQuietly( writer );
    }
    final String xmlStr = writer.toString();
    if( !includeXmlHeader )
      return xmlStr.replaceAll( "<\\?xml.*\\?>", "" ).replaceAll( "\n", "" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    return xmlStr;
  }
}
