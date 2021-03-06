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
package org.kalypso.contribs.java.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author belger
 */
public class UrlUtilities implements IUrlResolver
{
  private final Properties m_replaceTokenMap = new Properties();

  /**
   * <p>
   * Resolves a (potential) relative URL to a base URL.
   * </p>
   *
   * @param baseURL
   *          URL, to which the relative url will be resolved
   * @param relativeURL
   *          a string designating an absolute or relative URL
   * @return if relativeURL is relative, return new URL( baseURL, relativeURL ), else return new URL( relativeURL )
   * @throws MalformedURLException
   */
  @Override
  public URL resolveURL( final URL baseURL, final String relativeURL ) throws MalformedURLException
  {
    // REMARK: warum nicht einfach so?
    return new URL( baseURL, relativeURL );
  }

  /**
   * @see org.kalypso.contribs.java.net.IUrlResolver#getReplaceEntries()
   */
  @Override
  public final Iterator<Entry<Object, Object>> getReplaceEntries( )
  {
    return m_replaceTokenMap.entrySet().iterator();
  }

  /**
   * @see org.kalypso.contribs.java.net.IUrlResolver#addReplaceToken(java.lang.String, java.lang.String)
   */
  @Override
  public void addReplaceToken( final String key, final String value )
  {
    m_replaceTokenMap.setProperty( key, value );
  }

  /**
   * @throws IOException
   * @see org.kalypso.contribs.java.net.IUrlResolver#createWriter(java.net.URL)
   */
  @Override
  public OutputStreamWriter createWriter( final URL url ) throws IOException
  {
    final URLConnection connection = url.openConnection();
    try
    {
      connection.setDoOutput( true );

      final OutputStream outputStream = connection.getOutputStream();
      return new OutputStreamWriter( outputStream );
    }
    catch( final UnknownServiceException e )
    {
      // in diesem Fall unterst�tzt die URL kein Output

      // jetzt versuchen, selbst einen Stream zu �ffnen
      final String protocol = url.getProtocol();
      if( "file".equals( protocol ) ) //$NON-NLS-1$
      {
        final File file = new File( url.getFile() );
        return new OutputStreamWriter( new FileOutputStream( file ) );
      }

      // wenn alles nichts hilfe, doch die esception werden
      throw e;
    }
  }

  /**
   * Erzeugt den Reader anhand der URL und {@link URLConnection#getContentEncoding()}.
   *
   * @see org.kalypso.contribs.java.net.IUrlResolver#createReader(java.net.URL)
   */
  @Override
  public InputStreamReader createReader( final URL url ) throws IOException
  {
    final URLConnection connection = url.openConnection();
    final String contentEncoding = connection.getContentEncoding();
    final InputStream inputStream = connection.getInputStream();
    // TODO: unsauber! wenn kein encoding da, am besten keinen Reader verwenden,
    // da sonst z.B: probleme beim xml-parsen
    if( contentEncoding == null )
      return new InputStreamReader( inputStream );

    return new InputStreamReader( inputStream, contentEncoding );
  }

  /**
   * �ffnet die URL-Connection und gibt null zur�ck bei Fehler
   */
  public static URLConnection connectQuietly( final URL url )
  {
    URLConnection connection;

    try
    {
      connection = url.openConnection();
      connection.connect();
    }
    catch( final IOException e )
    {
      connection = null;
    }
    return connection;
  }

  /**
   * Parses the query part of an {@link URL} into a hash map (param name mapping to param value).<br>
   * If the query part contains a parameter twice, the second parameter wins.
   *
   * @return Always returns a new {@link Map} object. The empty map, if the given {@link URL} has no query part.
   * @throws IllegalArgumentException
   *           If the query part of the URL is not strictly ?param1=value&param2=value2&...
   */
  public static Map<String, String> parseQuery( final URL url )
  {
    final String query = url.getQuery();

    return QueryUtilities.parse( query );
  }

  /**
   * Adds query-parameters to an {@link URL}, replacing existing parameters.
   */
  public static URL addQuery( final URL url, final Map<String, String> params ) throws MalformedURLException
  {
    final Map<String, String> existingParams = parseQuery( url );
    existingParams.putAll( params );
    return replaceQuery( url, existingParams );
  }

  /**
   * Replaces the query part of a given {@link URL} by the contents of a paramater map.
   */
  public static URL replaceQuery( final URL url, final Map<String, String> params ) throws MalformedURLException
  {
    final StringBuffer queryBuffer = new StringBuffer();
    for( final Entry<String, String> param : params.entrySet() )
    {
      queryBuffer.append( param.getKey() );
      queryBuffer.append( '=' );
      queryBuffer.append( param.getValue() );
      queryBuffer.append( '&' );
    }

    /* Delete last '&' */
    if( !params.isEmpty() )
      queryBuffer.deleteCharAt( queryBuffer.length() - 1 );

    final String urlString = url.toExternalForm();
    final int queryIndex = urlString.indexOf( '?' );
    final String newUrlString;
    if( queryIndex == -1 )
      newUrlString = urlString + "?" + queryBuffer.toString();
    else
      newUrlString = urlString.substring( 0, queryIndex + 1 ) + queryBuffer.toString();

    return new URL( newUrlString );
  }

  /**
   * Reads the contents of a {@link URL} into a {@link String}. The stream is always closed.
   *
   * @param url
   *          the url to read, must not be <code>null</code>
   * @param encoding
   *          the encoding to use, <code>null</code> means platform default
   * @return the file contents, never <code>null</code>
   * @throws IOException
   *           in case of an I/O error
   * @throws java.io.UnsupportedEncodingException
   *           if the encoding is not supported by the VM
   */
  public static String readUrlToString( final URL url, final String encoding ) throws IOException
  {
    InputStream in = null;
    try
    {
      in = url.openStream();

      InputStreamReader isr;
      if( encoding == null )
        isr = new InputStreamReader( in );
      else
        isr = new InputStreamReader( in, encoding );

      final StringBuilder stringBuilder = new StringBuilder();
      final char[] buffer = new char[4096];
      while( isr.ready() )
      {
        final int readBytes = isr.read( buffer );
        if( readBytes == -1 )
          break;

        stringBuilder.append( new String( buffer, 0, readBytes ) );
      }
      in.close();
      return stringBuilder.toString();
    }
    finally
    {
      try
      {
        if( in != null )
          in.close();
      }
      catch( final Exception ignored )
      {
      }
    }
  }

  /**
   * Create a new {@link URL} object via {@link URL#URL(String)}.<br/>
   * Catches thrown exceptions and returns <code>null</code> instead.
   */
  public static URL createQuiet( final String spec )
  {
    try
    {
      return new URL( spec );
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  public static URL removeQuery( final URL location ) throws MalformedURLException
  {
    final String utostring = location.toString();
    final int ix = utostring.indexOf( '?' );
    if( ix != -1 )
      return new URL( utostring.substring( 0, ix ) );

    return location;
  }
}