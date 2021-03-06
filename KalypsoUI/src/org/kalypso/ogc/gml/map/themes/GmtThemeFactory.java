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
package org.kalypso.ogc.gml.map.themes;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.kalypso.commons.i18n.I10nString;
import org.kalypso.ogc.gml.CascadingKalypsoTheme;
import org.kalypso.ogc.gml.CascadingLayerKalypsoTheme;
import org.kalypso.ogc.gml.GisTemplateHelper;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.template.gismapview.CascadingLayer;
import org.kalypso.template.types.StyledLayerType;
import org.kalypsodeegree.model.geometry.GM_Envelope;

/**
 * Theme factory for {@link org.kalypso.ogc.gml.CascadingKalypsoTheme}s, based on .gmt files and {@link CascadingLayerKalypsoTheme}s.
 * 
 * @author Gernot Belger
 */
public class GmtThemeFactory extends AbstractThemeFactory
{
  @Override
  public IKalypsoTheme createTheme( final I10nString layerName, final StyledLayerType layerType, final URL context, final IMapModell mapModell, final IFeatureSelectionManager selectionManager ) throws CoreException
  {
    if( layerType instanceof CascadingLayer )
      return new CascadingLayerKalypsoTheme( layerName, (CascadingLayer)layerType, context, selectionManager, mapModell );

    return new CascadingKalypsoTheme( layerName, layerType, context, selectionManager, mapModell );
  }

  @Override
  public StyledLayerType createLayerType( final IKalypsoTheme theme )
  {
    if( theme instanceof CascadingLayerKalypsoTheme )
      return GisTemplateHelper.OF_GISMAPVIEW.createCascadingLayer();

    return super.createLayerType( theme );
  }

  @Override
  public void configureLayer( final IKalypsoTheme theme, final String id, final GM_Envelope bbox, final String srsName, final StyledLayerType layer, final IProgressMonitor monitor ) throws CoreException
  {
    if( theme instanceof CascadingLayerKalypsoTheme )
      configureCascadingLayerKalypsoTheme( (CascadingLayer)layer, (CascadingLayerKalypsoTheme)theme, id, srsName );

    if( theme instanceof CascadingKalypsoTheme )
      configureCascadingKalypsoTheme( layer, (CascadingKalypsoTheme)theme, bbox, srsName );
  }

  private static void configureCascadingLayerKalypsoTheme( final CascadingLayer layer, final CascadingLayerKalypsoTheme theme, final String id, final String srsName ) throws CoreException
  {
    /* Init depends */
    layer.getDepends();

    /* Configure the layer list. */
    theme.fillLayerList( layer.getLayer(), id, srsName, new NullProgressMonitor() );
  }

  private static void configureCascadingKalypsoTheme( final StyledLayerType layer, final CascadingKalypsoTheme theme, final GM_Envelope bbox, final String srsName ) throws CoreException
  {
    layer.setHref( theme.getMapViewRefUrl() );

    theme.createGismapTemplate( bbox, srsName, new NullProgressMonitor() );
  }
}