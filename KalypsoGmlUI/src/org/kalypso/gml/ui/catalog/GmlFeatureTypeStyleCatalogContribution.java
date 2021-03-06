package org.kalypso.gml.ui.catalog;

import java.net.URL;

import org.kalypso.core.catalog.CatalogManager;
import org.kalypso.core.catalog.ICatalogContribution;

public class GmlFeatureTypeStyleCatalogContribution implements ICatalogContribution
{
  @Override
  public void contributeTo( final CatalogManager catalogManager )
  {
    final URL catalogURL = getClass().getResource( "resources/catalog.xml" ); //$NON-NLS-1$
    catalogManager.addNextCatalog( catalogURL );
  }
}
