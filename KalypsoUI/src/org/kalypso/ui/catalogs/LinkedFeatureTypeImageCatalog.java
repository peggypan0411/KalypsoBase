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
package org.kalypso.ui.catalogs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.catalog.CatalogManager;
import org.kalypso.core.catalog.urn.IURNGenerator;
import org.kalypso.gmlschema.feature.IFeatureType;

/**
 * This is an image catalog for linked features.
 * 
 * @author Holger Albert
 */
public final class LinkedFeatureTypeImageCatalog
{
  private LinkedFeatureTypeImageCatalog( )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * The base type.
   */
  private static String BASETYPE = "swtimage"; //$NON-NLS-1$

  /**
   * The image descriptor cache.
   */
  private static Map<String, ImageDescriptor> IMAGE_DESCRIPTOR_CACHE = new HashMap<>();

  /**
   * This function returns the image descriptor for the given qname and URL.
   * 
   * @return The image descriptor, or null.
   */
  public static ImageDescriptor getImage( final URL context, final QName qname )
  {
    /* Try to get cached image descriptor. */
    final String contextStr = context == null ? "null" : context.toExternalForm(); //$NON-NLS-1$
    final String qnameStr = qname == null ? "null" : qname.toString(); //$NON-NLS-1$
    final String cacheKey = contextStr + '#' + qnameStr;

    /* Does a cached one exist? */
    if( IMAGE_DESCRIPTOR_CACHE.containsKey( cacheKey ) )
      return IMAGE_DESCRIPTOR_CACHE.get( cacheKey );

    /* Get the URL. */
    final URL imgUrl = getURL( BASETYPE, context, qname );

    /* Search for image descriptor in catalog or local url. */
    final ImageDescriptor newId = imgUrl == null ? null : ImageDescriptor.createFromURL( imgUrl );

    /* Store. */
    IMAGE_DESCRIPTOR_CACHE.put( cacheKey, newId );

    return newId;
  }

  /**
   * This function resolves the URL.
   * 
   * @param catalogTypeBasename
   *          The catalog type basename.
   * @param context
   *          The context.
   * @param qname
   *          The qname.
   * @return The URL or null.
   */
  public static URL getURL( final String catalogTypeBasename, final URL context, final QName qname )
  {
    /* Create the URN. */
    final String urn = createUrn( catalogTypeBasename, qname );

    /* Get the URI. */
    final String uri = FeatureTypeCatalog.getLocation( urn );

    /* If we got no uri or an urn, do nothing, we need a real url. */
    if( uri == null || uri.startsWith( "urn" ) ) //$NON-NLS-1$
      return null;

    try
    {
      return new URL( context, uri );
    }
    catch( final MalformedURLException e )
    {
      final IStatus status = StatusUtilities.statusFromThrowable( e );
      KalypsoCorePlugin.getDefault().getLog().log( status );
    }

    return null;
  }

  /**
   * This function creates the URN.
   * 
   * @param catalogTypeBasename
   *          The catalog type basename.
   * @param qname
   *          The qname.
   * @return The URN.
   */
  public static String createUrn( final String catalogTypeBasename, final QName qname )
  {
    // REMARK: catalog is registered for feature type, not for qname
    // Hint for a refaktoring on the CatalogManager
    final CatalogManager catalogManager = KalypsoCorePlugin.getDefault().getCatalogManager();

    /* This generator is suiteable, because it is the same base URN here, as in FeatureImageTypeCatalog. */
    final IURNGenerator generator = catalogManager.getURNGeneratorFor( IFeatureType.class );
    if( generator == null )
      return null;

    final String baseURN = generator.generateURNFor( qname );
    if( baseURN == null )
      return null;

    return baseURN + ":" + catalogTypeBasename + ":linked"; //$NON-NLS-1$ //$NON-NLS-2$
  }
}