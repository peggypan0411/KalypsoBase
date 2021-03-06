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
package org.kalypso.core.catalog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBException;

/**
 * Interface for a catalog
 * <p>
 * <b>definitions:</b><br>
 * systemID: anything to identify something, whatever applications use uri,urn,id,phrase<br>
 * publicID: anything to identify something, whatever applications use uri,urn,id,phrase<br>
 * uri: something that points to the resource<br>
 * <p>
 * <b>remarks:</b><br>
 * 1. a catalog may have uri entries that are relative (against the catalog-location), see also resolve-method<br>
 * 2. the only difference between systemID and publicID is the order while resolving<br>
 * 
 * @author doemming
 */
public interface ICatalog
{
  /**
   * returns a uri (absolute) or the systemID if no match was found int the catalog<br>
   * callers usually resolve the result against their local context
   * 
   * @param resolveContext
   *          If true, the found uri is resolved against the local context
   */
  String resolve( final String systemID, final String publicID, final boolean resolveContext );

  /**
   * Same as {@link #resolve(String, String, true)}.
   */
  String resolve( final String systemID, final String publicID );

  /**
   * resolves all URNs that fit to the pattern<br>
   * the pattern must start with "urn:" and may and with "*"<br>
   * example: pattern="urn:ogc:sld:www.kalypso.tu-harburg.de_na:catchment:*"<br>
   * callers may use this methode for a dialog that let the user choose among the matching URNs<br>
   * the choosen URN then must be resolved by ICatalog.resolve(urn,run)
   * 
   * @param urnPattern
   *          pattern to match
   * @return URNs for pattern
   */
  List<String> getEntryURNS( String urnPattern ) throws MalformedURLException, JAXBException;

  /**
   * adds an entry to the catalog
   * 
   * @param uri
   *          absolute pointer to the resource
   * @param publicID
   *          the systemID
   * @param systemID
   *          the publicID
   */
  void addEntry( String uri, String systemID, String publicID );

  /**
   * adds an entry to the catalog, but changes the uri to a relative reference to the catalog itself<br>
   * 
   * @param uri
   *          absolute pointer to the resource
   * @param publicID
   *          the systemID
   * @param systemID
   *          the publicID
   */
  void addEntryRelative( String uri, String systemID, String publicID );

  void addNextCatalog( URL catalogURL );

}