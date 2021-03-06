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
package org.kalypso.contribs.ogc31;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.kalypso.commons.xml.NS;
import org.kalypso.contribs.java.net.AbstractUrlCatalog;

/**
 * this catalog resolves all schemas that are original provided by ogc or very close to these
 *
 * @author doemming
 */
public class UrlCatalogOGC extends AbstractUrlCatalog
{
  @Override
  protected void fillCatalog( final Class< ? > myClass, final Map<String, URL> catalog, final Map<String, String> prefixes )
  {
    try
    {
      // Version 3.1.1. from http://schemas.opengis.net/gml/3.1.1/base/gml.xsd
      catalog.put( NS.GML3 + "#3", new URL( "platform:/plugin/org.kalypso.contribs.ogc31/etc/schemas/gml/3.1.1/base/gml.xsd" ) ); //$NON-NLS-1$ //$NON-NLS-2$
      prefixes.put( NS.GML3, "gml" ); //$NON-NLS-1$

      // SWE & OM things
      catalog.put( NS.SWE, new URL( "platform:/plugin/org.kalypso.contribs.ogc31/etc/schemas/sweCommon/1.0.30/swe.xsd" ) ); //$NON-NLS-1$
      prefixes.put( NS.SWE, "swe" ); //$NON-NLS-1$

      catalog.put( NS.ST, new URL( "platform:/plugin/org.kalypso.contribs.ogc31/etc/schemas/sweCommon/1.0.30/simpleTypeDerivation.xsd" ) ); //$NON-NLS-1$
      prefixes.put( NS.ST, "st" ); //$NON-NLS-1$

      catalog.put( NS.OM, new URL( "platform:/plugin/org.kalypso.contribs.ogc31/etc/schemas/om/1.0.30/observation.xsd" ) ); //$NON-NLS-1$
      prefixes.put( NS.OM, "om" ); //$NON-NLS-1$
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();
    }
  }
}