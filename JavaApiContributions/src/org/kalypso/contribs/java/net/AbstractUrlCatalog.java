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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstrakter UrlKatalog. Ableitende Klassen m�ssen nur den konkreten Katalog f�llen.
 *
 * @author belger
 */
public abstract class AbstractUrlCatalog implements IUrlCatalog
{
  private final Map<String, URL> m_catalog = new HashMap<>();

  private final Map<String, String> m_prefixes = new HashMap<>();

  public AbstractUrlCatalog( )
  {
    fillCatalog( getClass(), m_catalog, m_prefixes );
  }

  protected abstract void fillCatalog( final Class< ? > myClass, final Map<String, URL> catalog, final Map<String, String> prefixes );

  /**
   * @see org.kalypso.contribs.java.net.IUrlCatalog#getCatalog()
   */
  @Override
  public final Map<String, URL> getCatalog( )
  {
    return m_catalog;
  }

  /**
   * @see org.kalypso.contribs.java.net.IUrlCatalog#getURL(java.lang.String)
   */
  @Override
  public final URL getURL( final String namespace )
  {
    return m_catalog.get( namespace );
  }

  /**
   * Default implementation returns null.
   *
   * @see org.kalypso.contribs.java.net.IUrlCatalog#getPreferredNamespacePrefix(java.lang.String)
   */
  @Override
  public String getPreferedNamespacePrefix( final String namespace )
  {
    return m_prefixes.get( namespace );
  }
}
