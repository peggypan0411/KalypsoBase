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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Ein URL Katalog, welcher die Namespaces anhand von anderen URL-Katalogen aufl�st, deren Klassennamen er aus einer
 * Property-Liste liest. Die Kataloge werden per Reflektion �ber den Standard-Konstruktor instantiiert.
 * 
 * @author thuel2
 */
public class ClassUrlCatalog implements IUrlCatalog
{
  private IUrlCatalog m_catalog;

  /**
   * Closes the stream
   * 
   * @throws IOException
   */
  private static Properties loadProperties( final InputStream stream ) throws IOException
  {
    final Properties props = new Properties();
    try
    {
      props.load( stream );
    }
    finally
    {
      stream.close();
    }

    return props;
  }

  /**
   * Die Classennamen werden aus den 'values' der Property-Datei gelesen. Die 'keys' werden ignoriert.
   * 
   * @throws IOException
   */
  public ClassUrlCatalog( final File propertyFile ) throws IOException
  {
    this( loadProperties( new FileInputStream( propertyFile ) ) );
  }

  /**
   * Die Classennamen werden aus den 'values' der URL gelesen. Die 'keys' werden ignoriert.
   * 
   * @throws IOException
   */
  public ClassUrlCatalog( final URL propertyURL ) throws IOException
  {
    this( loadProperties( propertyURL.openStream() ) );
  }

  /**
   * Die Classennamen werden aus den 'values' der Property-Datei gelesen. Die 'keys' werden ignoriert. Jede Klasse
   * selbst muss ein IUrlCatalog sein.
   */
  public ClassUrlCatalog( final Properties props )
  {
    try
    {
      final String[] classNames = props.values().toArray( new String[0] );
      final IUrlCatalog[] catalogs = new IUrlCatalog[classNames.length];
      for( int i = 0; i < classNames.length; i++ )
      {
        final Class<IUrlCatalog> clazz = (Class<IUrlCatalog>) Class.forName( classNames[i] );
        final Constructor<IUrlCatalog> constructor = clazz.getConstructor( new Class[] {} );
        catalogs[i] = constructor.newInstance( new Object[] {} );
      }

      m_catalog = new MultiUrlCatalog( catalogs );
    }
    catch( final Exception e )
    {
      // kein Error-Handling, dies ist ein Konfigurationsfehler des
      // Kalypso-Servers
      e.printStackTrace();

      m_catalog = new MultiUrlCatalog( new IUrlCatalog[0] );
    }
  }

  /**
   * @see org.kalypso.contribs.java.net.IUrlCatalog#getURL(java.lang.String)
   */
  @Override
  public URL getURL( final String key )
  {
    if( m_catalog != null )
      return m_catalog.getURL( key );

    return null;
  }

  /**
   * @see org.kalypso.contribs.java.net.IUrlCatalog#getPreferredNamespacePrefix(java.lang.String)
   */
  @Override
  public String getPreferedNamespacePrefix( final String namespace )
  {
    if( m_catalog != null )
      return m_catalog.getPreferedNamespacePrefix( namespace );

    return null;
  }

  /**
   * @see org.kalypso.contribs.java.net.IUrlCatalog#getCatalog()
   */
  @Override
  public Map<String, URL> getCatalog( )
  {
    return m_catalog.getCatalog();
  }

}