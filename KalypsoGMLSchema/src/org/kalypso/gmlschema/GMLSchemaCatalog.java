package org.kalypso.gmlschema;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.commons.xml.NS;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.java.net.IUrlCatalog;
import org.kalypso.gmlschema.i18n.Messages;

/**
 * <p>
 * GML Schema Catalog, benutzt den {@link org.kalypsodeegree_impl.gml.schema.GMLSchemaCache}.
 * </p>
 * 
 * @author schlienger
 */
@SuppressWarnings( "restriction" )
public final class GMLSchemaCatalog
{
  private final IUrlCatalog m_urlCatalog;

  private final Map<String, URL> m_additionalSchemaFiles = new HashMap<>();

  private final GMLSchemaCache m_cache;

  /**
   * @throws NullPointerException
   *           If catalog or cacheDirectory is null.
   */
  public GMLSchemaCatalog( final IUrlCatalog catalog )
  {
    if( catalog == null )
      throw new NullPointerException();

    m_urlCatalog = catalog;

    m_cache = new GMLSchemaCache();
  }

  public IUrlCatalog getDefaultCatalog( )
  {
    return m_urlCatalog;
  }

  /**
   * L�dt ein Schema aus dieser URL (nicht aus dem Cache!) und f�gt es dann dem cache hinzu (mit namespace als key).
   * 
   * @return null, wenn schema nicht geladen werden konnte
   */
  public IGMLSchema getSchema( final String gmlVersion, final URL schemaLocation )
  {
    Debug.CATALOG.printf( "Loading schema into cache for gmlVersion %s and schemaLocation %s%n", gmlVersion, schemaLocation ); //$NON-NLS-1$

    try
    {
      final GMLSchema schema = GMLSchemaFactory.createGMLSchema( gmlVersion, schemaLocation );
      final Date validity = new Date( schemaLocation.openConnection().getLastModified() );

      m_cache.addSchema( schema.getTargetNamespace(), schema, validity, System.currentTimeMillis(), gmlVersion );

      // IMPORTANT: We remember these additional namespaces separately so we can reload them later, if the cache forgets the schema
      m_additionalSchemaFiles.put( schema.getTargetNamespace(), schemaLocation );

      Debug.CATALOG.printf( "Schema successfully loaded and put into the cache. Validity = %s%n", validity ); //$NON-NLS-1$

      return schema;
    }
    catch( final Exception e )
    {
      final String message = Messages.getString( "org.kalypso.gmlschema.GMLSchemaCatalog.0", schemaLocation ); //$NON-NLS-1$
      StatusUtilities.statusFromThrowable( e, message );

      Debug.CATALOG.printf( message );
      Debug.CATALOG.printf( "%n" ); //$NON-NLS-1$

      return null;
    }
  }

  /**
   * L�dt ein (eventuell gecachetes Schema �ber den Katalog. Als CacheId wird dieser Name benutzt.
   */
  public GMLSchema getSchema( final String namespace, final String gmlVersion ) throws GMLSchemaException
  {
    return getSchema( namespace, gmlVersion, null );
  }

  /**
   * L�dt ein (eventuell gecachetes) Schema. Ist das Schema nicht bereits bekannt (=gecached), wird �ber den Katalog
   * oder �ber die gegebene URL geladen.
   * <p>
   * Ist im Katalog eine schema-location gegeben, wird diese bevorzugt.
   * </p>
   */
  public GMLSchema getSchema( final String namespace, final String gmlVersion, final URL schemaLocation ) throws GMLSchemaException
  {
    Debug.CATALOG.printf( "Trying to retrieve schema from cache for:%n\tnamespace: %s%n\tgmlVersion: %s%n\tschemaLocation: %s%n", namespace, gmlVersion, schemaLocation ); //$NON-NLS-1$

    final Pair<URL, String> catalogUrlAndVersion = findLocationAndVersion( namespace, gmlVersion );

    final URL catalogUrl = catalogUrlAndVersion.getKey();
    final String version = catalogUrlAndVersion.getValue();

    Debug.CATALOG.printf( "Determined version and catalogUrl: %s - %s%n", version, catalogUrl ); //$NON-NLS-1$

    // maybe its a local schema?
    final URL localOrCatalogURL = lookupLocalLocation( namespace, catalogUrl );

    // Fallback to schemaLocation if
    final URL schemaUrl = localOrCatalogURL == null ? schemaLocation : localOrCatalogURL;
    if( schemaUrl == null )
      Debug.CATALOG.printf( "No location for namespace: %s - trying to load from cache.%n", namespace ); //$NON-NLS-1$

    try
    {
      // else, try to get it from the external cache
      final URIResolver uriResolver = URIResolverPlugin.createResolver();
      final String externalForm = schemaUrl == null ? null : schemaUrl.toExternalForm();
      final String resolvedUri = externalForm == null ? null : uriResolver.resolve( externalForm, null, externalForm );
      final URL resolvedUrl = resolvedUri == null ? null : new URL( resolvedUri );

      // auch versuchen aus dem Cache zu laden, wenn die url null ist;
      // vielleicht ist der namespace ja noch im file-cache
      return m_cache.getSchema( namespace, version, resolvedUrl );
    }
    catch( final Throwable e )
    {
      if( Objects.isNotNull( namespace ) )
        System.out.println( String.format( "Failed to resolve namespace: %s", namespace ) ); //$NON-NLS-1$

      throw new GMLSchemaException( e );
    }
  }

  private URL lookupLocalLocation( final String namespace, final URL catalogUrl )
  {
    if( catalogUrl != null )
      return catalogUrl;

    return m_additionalSchemaFiles.get( namespace );
  }

  private Pair<URL, String> findLocationAndVersion( final String namespace, final String gmlVersion )
  {
    // HACK: if we are looking for the gml namespace
    // tweak it and add the version number

    if( namespace.equals( NS.GML2 ) )
    {
      final String version;
      if( gmlVersion == null )
        // if we get here and don't know the version number, we are probably loading
        // a gml whichs schema is the gml schema directly.
        // This is only possible for gml3 documents.
        version = "3.1.1"; //$NON-NLS-1$
      else
        version = gmlVersion;

      final URL catalogUrl = m_urlCatalog.getURL( namespace + "#" + version.charAt( 0 ) ); //$NON-NLS-1$
      return Pair.of( catalogUrl, version );
    }

    final URL catalogUrl = m_urlCatalog.getURL( namespace );

    // HACK: crude hack to enforce GML3 for WFS
    final String version;
    if( NS.WFS.equals( namespace ) )
      version = "3.1.1"; //$NON-NLS-1$
    else
      version = gmlVersion;

    return Pair.of( catalogUrl, version );
  }

  /**
   * Clears the cache. Schematas are reloaded after this operation.
   */
  public void clearCache( )
  {
    m_cache.clearCache();
    Debug.CATALOG.printf( "Cleared schema cache." ); //$NON-NLS-1$
  }
}
