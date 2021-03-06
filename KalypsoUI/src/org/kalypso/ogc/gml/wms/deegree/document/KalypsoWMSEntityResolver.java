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
package org.kalypso.ogc.gml.wms.deegree.document;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.kalypso.ui.KalypsoUIDebug;
import org.kalypso.ui.internal.i18n.Messages;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * A class for resolving some entities.
 * 
 * @author Holger Albert
 */
public class KalypsoWMSEntityResolver implements EntityResolver
{
  /**
   * Map containing all local available resources.
   */
  private final HashMap<String, String> m_entities;

  /**
   * The constructor.
   */
  public KalypsoWMSEntityResolver( )
  {
    m_entities = new HashMap<>();
    m_entities.put( "http://schemas.opengis.net/wms/1.1.0/capabilities_1_1_0.dtd", "resources/capabilities_1_1_0.dtd" ); //$NON-NLS-1$ //$NON-NLS-2$
    m_entities.put( "http://schemas.opengis.net/wms/1.1.1/WMS_MS_Capabilities.dtd", "resources/WMS_MS_Capabilities.dtd" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
   */
  @Override
  public InputSource resolveEntity( final String publicId, final String systemId ) throws IOException
  {
    final String path = m_entities.get( systemId );
    if( path != null )
    {
      KalypsoUIDebug.DEBUG_WMS.printf( Messages.getString( "org.kalypso.ogc.gml.wms.deegree.document.KalypsoWMSEntityResolver.4" ) + systemId ); //$NON-NLS-1$

      final URL resource = getClass().getResource( path );
      if( resource != null )
        return new InputSource( resource.openStream() );
    }

    return null;
  }
}