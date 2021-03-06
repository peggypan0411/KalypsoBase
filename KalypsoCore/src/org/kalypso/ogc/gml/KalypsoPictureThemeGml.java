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
package org.kalypso.ogc.gml;

import java.awt.Graphics;
import java.net.URL;

import javax.media.jai.TiledImage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.i18n.I10nString;
import org.kalypso.contribs.java.net.UrlResolverSingleton;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypso.template.types.StyledLayerType;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.coverage.RangeSetFile;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;
import org.kalypsodeegree_impl.gml.binding.commons.ICoverage;
import org.kalypsodeegree_impl.gml.binding.commons.ICoverageCollection;
import org.kalypsodeegree_impl.gml.binding.commons.RectifiedGridCoverage;

/**
 * @author Dirk Kuch
 */
public class KalypsoPictureThemeGml extends KalypsoPictureTheme
{
  private final ICoverageCollection m_coverages;

  public KalypsoPictureThemeGml( final I10nString layerName, final StyledLayerType layerType, final URL context, final IMapModell modell ) throws CoreException
  {
    super( layerName, layerType, context, modell );

    try
    {
      final URL gmlURL = UrlResolverSingleton.resolveUrl( context, layerType.getHref() );
      setContext( gmlURL );

      // TODO: botch... find a better way of loading gml workspace!
      // maybe it could be treated as normal gml with a special display element?
      final GMLWorkspace workspace = GmlSerializer.createGMLWorkspace( gmlURL, null );
      final Feature fRoot = workspace.getRootFeature();

      m_coverages = (ICoverageCollection) fRoot.getAdapter( ICoverageCollection.class );
      final IFeatureBindingCollection<ICoverage> coverages = m_coverages.getCoverages();
      if( coverages.size() != 1 )
        throw new UnsupportedOperationException( Messages.getString( "org.kalypso.ogc.gml.KalypsoPictureThemeGml.0" ) ); //$NON-NLS-1$

      for( final ICoverage coverage : coverages )
      {
        final RectifiedGridCoverage coverage2 = (RectifiedGridCoverage) coverage;

        /* recGridDomain */
        setRectifiedGridDomain( coverage2.getGridDomain() );

        // HACK: we assume, that we only have exactly ONE coverage per picture-theme
        break;
      }
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final IStatus status = new Status( IStatus.ERROR, KalypsoCorePlugin.getID(), Messages.getString( "org.kalypso.ogc.gml.KalypsoPictureThemeGml.1" ), e ); //$NON-NLS-1$
      throw new CoreException( status );
    }
  }

  @Override
  public IStatus paint( final Graphics g, final GeoTransform p, final Boolean selected, final IProgressMonitor monitor )
  {
    /** image creation removed from constructor, so not visible themes will not be loaded! */
    final ImageHolder imageHolder = getImage();
    final TiledImage image = imageHolder.getImage();
    if( image == null )
    {
      final IFeatureBindingCollection<ICoverage> coverages = m_coverages.getCoverages();
      for( final ICoverage coverage : coverages )
      {
        /* imgFile */
        final Object rangeSet = coverage.getRangeSet();
        if( coverage instanceof RectifiedGridCoverage && rangeSet instanceof RangeSetFile )
        {
          final RangeSetFile type = (RangeSetFile) rangeSet;
          final String filePath = type.getFileName();
          final URL imageURL = loadImage( filePath );
          if( imageURL == null )
            return getStatus();
        }

        // HACK: we assume, that we only have exactly ONE coverage per picture-theme
        break;
      }
    }

    return super.paint( g, p, selected, monitor );
  }
}