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
package org.kalypso.services.observation;

import org.kalypso.services.observation.client.repository.ObservationServiceRepository;

/**
 * Provides utility methods for manipulating the URLs designed to be used as Zml-Identifiers between kalypso client and
 * server.
 * <p>
 * This utility class is not intended to be instanciated. Use its static methods.
 * 
 * @author schlienger (18.05.2005)
 */
public final class ObservationServiceUtils
{
  public static final String ID_OBSERVATION_REPOSITORY = ObservationServiceRepository.ID;

  public static final String REPOSITORY_FACTORY = "org.kalypso.services.observation.client.repository.ObservationServiceRepositoryFactory"; //$NON-NLS-1$

  private ObservationServiceUtils( )
  {
  }

  /**
   * Return true if the given id represents a server-side url. A server-side url begins with the server-specific
   * scheme-name.
   * 
   * @param href
   *          string representation of the zml url
   * @return true if server side
   * @see ZmlURLConstants#SCHEME_OCS
   */
  public static boolean isServerSide( final String href )
  {
    return href.startsWith( ObservationServiceRepository.ID );
  }

  /**
   * Add the kalypso-ocs-scheme part to the id (if not already present)
   */
  public static String addServerSideId( final String href )
  {
    String ssid = href;
    if( !isServerSide( ssid ) )
      ssid = ObservationServiceRepository.ID + ":" + ssid; //$NON-NLS-1$

    return ssid;
  }

  /**
   * Remove the kalypso-ocs-scheme part
   */
  public static String removeServerSideId( final String href )
  {
    final String id = href.replaceFirst( ObservationServiceRepository.ID + ":", "" ); //$NON-NLS-1$ //$NON-NLS-2$

    return id;
  }

}
