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
package org.kalypso.ogc.gml.serialize;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.java.net.IUrlResolver;
import org.kalypso.contribs.java.net.UrlResolver;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree_impl.model.feature.IFeatureProviderFactory;

/**
 * @author Gernot Belger
 */
public class GmlSerializerXlinkFeatureProvider extends AbstractXLinkFeatureProvider
{
  private GMLWorkspace m_workspace;

  private final IFeatureProviderFactory m_factory;

  private final IUrlResolver m_urlResolver;

  public GmlSerializerXlinkFeatureProvider( final GMLWorkspace context, final String uri, final IFeatureProviderFactory factory )
  {
    this( context, uri, factory, new UrlResolver() );
  }

  public GmlSerializerXlinkFeatureProvider( final GMLWorkspace context, final String uri, final IFeatureProviderFactory factory, final IUrlResolver urlResolver )
  {
    super( context, uri );

    m_factory = factory;
    m_urlResolver = urlResolver;
  }

  /**
   * @see org.kalypsodeegree.model.feature.IFeatureProvider#getWorkspace()
   */
  @Override
  public GMLWorkspace getWorkspace( )
  {
    final String uri = getUri();
    if( m_workspace == null && uri != null )
    {
      try
      {
        // TODO: maybe add listener to workspace in order to be informed of deletion of my feature?
        final GMLWorkspace contextWorkspace = getContext();
        final URL context = contextWorkspace == null ? null : contextWorkspace.getContext();
        final URL url = m_urlResolver.resolveURL( context, uri );

        m_workspace = GmlSerializer.createGMLWorkspace( url, m_factory );
      }
      catch( final Exception e )
      {
        final IStatus status = StatusUtilities.statusFromThrowable( e );
        KalypsoCorePlugin.getDefault().getLog().log( status );
      }
    }

    return m_workspace;
  }

  /**
   * @see org.kalypsodeegree.model.feature.IFeatureProvider#dispose()
   */
  @Override
  public void dispose( )
  {
    m_workspace = null;
  }
}
