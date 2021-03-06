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
 * ---------------------------------------------------------------------------
 */
package org.kalypso.ogc.sensor.zml;

import java.net.URL;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.request.IRequest;
import org.kalypso.ogc.sensor.request.RequestFactory;
import org.kalypso.ogc.sensor.request.XMLStringUtilities;
import org.kalypso.zml.request.Request;

/**
 * Provides utility methods for manipulating the URLs designed to be used as Zml-Identifiers between kalypso client and
 * server.
 * <p>
 * This utility class is not intended to be instanciated. Use its static methods.
 * 
 * @author schlienger (18.05.2005)
 */
public final class ZmlURL
{
  private ZmlURL( )
  {
    // not intended to be instanciated
  }

  /**
   * Returns only the identifier part of the zml url. The URL may contain a query part which will be ignored by this
   * convenience method.
   * 
   * @return only identifier part
   */
  public static String getIdentifierPart( final URL url )
  {
    return getIdentifierPart( url.toExternalForm() );
  }

  /**
   * Returns only the identifier part of the zml url. The URL may contain a query part which will be ignored by this
   * convenience method.
   * 
   * @return only identifier part
   */
  public static String getIdentifierPart( final String strUrl )
  {
    final int ix = strUrl.indexOf( '?' );
    if( ix != -1 )
      return strUrl.substring( 0, ix );

    return strUrl;
  }

  /**
   * Insert the filter and/or the request found in the query-part and return the result.
   * 
   * @param href
   *          the zml url to update
   * @param queryPart
   *          May contain a filter spec (embodied within %lt;filter/&gt; tags) and may also contain a request spec
   *          (embodied within %lt;request/&gt; tags). If <code>null</code>, simply <code>href</code> is returned.
   */
  public static String insertQueryPart( final String href, final String queryPart )
  {
    if( StringUtils.isBlank( queryPart ) )
      return href;

    String newHref = href;

    final int fp1 = queryPart.indexOf( ZmlURLConstants.TAG_FILTER1 );
    final int fp2 = queryPart.indexOf( ZmlURLConstants.TAG_FILTER2 );
    if( fp1 != -1 && fp2 != -1 )
    {
      final String filter = queryPart.substring( fp1, fp2 + ZmlURLConstants.TAG_FILTER2.length() );
      newHref = insertFilter( newHref, filter );
    }

    final String request = XMLStringUtilities.getXMLPart( queryPart, ZmlURLConstants.TAG_REQUEST );
    if( request != null )
    {
      newHref = insertRequest( newHref, request );
    }
    return newHref;
  }

  /**
   * Insert the filter spec into the zml url. Return the newly build url string.
   * <p>
   * The filter may or may not contain the %lt;filter/&gt; tags, this is automatically handled by this method.
   * 
   * @param href
   *          the zml url to update
   * @param filter
   *          the xml oriented filter specification
   */
  public static String insertFilter( final String href, String filter )
  {
    if( filter == null || filter.length() == 0 )
      return href;

    // first remove the existing filter spec (does nothing if not present)
    String tmp = href.replaceFirst( ZmlURLConstants.TAG_FILTER1 + ".*" + ZmlURLConstants.TAG_FILTER2, "" ); //$NON-NLS-1$ //$NON-NLS-2$

    filter = filter.replaceAll( "^\\?", "" ); // remove beginning "?" //$NON-NLS-1$ //$NON-NLS-2$
    filter = filter.replaceAll( "^" + ZmlURLConstants.TAG_FILTER1, "" ); // remove beginning "<filter>" //$NON-NLS-1$ //$NON-NLS-2$
    filter = filter.replaceAll( ZmlURLConstants.TAG_FILTER2 + "$", "" ); // remove ending "</filter>" //$NON-NLS-1$ //$NON-NLS-2$

    final String[] strs = tmp.split( "\\?", 2 ); //$NON-NLS-1$
    if( strs[0].startsWith( "<" ) || strs[0].startsWith( "&lt;" ) ) //$NON-NLS-1$ //$NON-NLS-2$
      tmp = "?" + strs[0] + ZmlURLConstants.TAG_FILTER1 + filter + ZmlURLConstants.TAG_FILTER2; //$NON-NLS-1$
    else
      tmp = strs[0] + '?' + ZmlURLConstants.TAG_FILTER1 + filter + ZmlURLConstants.TAG_FILTER2;

    if( strs.length >= 2 )
      tmp += strs[1];

    return tmp;
  }

  /**
   * Insert the request spec into the zml url. Return the newly build url string.
   * 
   * @param href
   *          the zml url to update
   * @param request
   *          the xml request specification
   */
  public static String insertRequest( final String href, final IRequest request ) throws SensorException
  {
    try
    {
      Request requestType = RequestFactory.parseRequest( href );
      if( requestType == null )
        requestType = RequestFactory.OF.createRequest();
      // the original request parameters have higher priority
      if( requestType.getName() == null )
        requestType.setName( request.getName() );
      if( requestType.getAxes() == null )
        requestType.setAxes( StringUtils.join( request.getAxisTypes(), "," ) ); //$NON-NLS-1$
      if( requestType.getStatusAxes() == null )
        requestType.setStatusAxes( StringUtils.join( request.getAxisTypesWithStatus(), "," ) ); //$NON-NLS-1$

      if( request.getDateRange() != null )
      {
        final Calendar from = Calendar.getInstance();
        from.setTime( request.getDateRange().getFrom() );
        requestType.setDateFrom( from );

        final Calendar to = Calendar.getInstance();
        to.setTime( request.getDateRange().getTo() );
        requestType.setDateTo( to );
      }

      final String xmlStr = RequestFactory.buildXmlString( requestType, false );

      return insertRequest( href, xmlStr );
    }
    catch( final Exception e )
    {
      throw new SensorException( e );
    }
  }

  /**
   * Insert the request spec into the zml url. Return the newly build url string.
   * <p>
   * The request must be contained within the %lt;request/&gt; tags.
   * 
   * @param href
   *          the zml url to update
   * @param request
   *          the xml oriented request specification
   */
  public static String insertRequest( final String href, final String request )
  {
    // first remove the existing request (does nothing if not present)
    final String requestPart = XMLStringUtilities.getXMLPart( href, ZmlURLConstants.TAG_REQUEST );
    String tmpUrl;
    if( requestPart != null )
      tmpUrl = href.replace( requestPart, "" ); //$NON-NLS-1$
    else
      tmpUrl = href;

    final String[] strs = tmpUrl.split( "\\?", 2 ); //$NON-NLS-1$
    if( strs[0].startsWith( "<" ) || strs[0].startsWith( "&lt;" ) ) //$NON-NLS-1$ //$NON-NLS-2$
      tmpUrl = "?" + strs[0]; //$NON-NLS-1$
    else
      tmpUrl = strs[0] + '?';

    tmpUrl += request;

    if( strs.length >= 2 )
      tmpUrl += strs[1];

    return tmpUrl;
  }

  /**
   * Return true if the href represents the "empty-id" (which is either "kalypso-ocs://LEER" or "kalypso-ocs://DUMMY")
   * 
   * @return true if the given href contains "LEER"
   */
  public static boolean isEmpty( final String href )
  {
    final String id = getIdentifierPart( href );

    return id.matches( ".*//LEER" ); //$NON-NLS-1$
  }
}
