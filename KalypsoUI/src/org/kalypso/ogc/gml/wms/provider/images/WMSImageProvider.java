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
package org.kalypso.ogc.gml.wms.provider.images;

import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.kalypso.ogc.gml.wms.deegree.RemoteWMService;
import org.kalypso.ogc.gml.wms.loader.ICapabilitiesLoader;
import org.kalypso.ogc.gml.wms.loader.WMSCapabilitiesLoader;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * This provider loads an image from a WMS. It caches the capabilities, so that this request is only done once.
 * 
 * @author Holger Albert
 */
public class WMSImageProvider extends AbstractDeegreeImageProvider
{
  /**
   * This constant stores the type name.
   */
  public final static String TYPE_NAME = "wms"; //$NON-NLS-1$

  public WMSImageProvider( )
  {
  }

  @Override
  protected RemoteWMService createRemoteService( final WMSCapabilities capabilities )
  {
    return new RemoteWMService( capabilities );
  }

  @Override
  public String getLabel( )
  {
    return Messages.getString( "org.kalypso.ogc.gml.wms.loader.images.WMSImageProvider.6" ) + getService(); //$NON-NLS-1$
  }

  @Override
  public ICapabilitiesLoader createCapabilitiesLoader( )
  {
    // FIXME: event 10 secs is sometimes not enough for slow/big wms servers; configure it from outside?
    return new WMSCapabilitiesLoader( 10000 );
  }
}