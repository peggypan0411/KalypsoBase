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

import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.kalypso.commons.i18n.I10nString;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.template.gismapview.CascadingLayer;
import org.kalypso.template.types.StyledLayerType;

/**
 * @author Stefan Kurzbach extended by Dirk Kuch
 */
// TODO: implementing IMapModell here is an ugly hack to show the layers in the outline view
// do not do such a thing. Instead let each theme return a content-provider, so the structure is delegated to the
// themes.
public class CascadingLayerKalypsoTheme extends AbstractCascadingLayerTheme
{
  public CascadingLayerKalypsoTheme( final I10nString layerName, final CascadingLayer layerType, final URL context, final IFeatureSelectionManager selectionManager, final IMapModell mapModel ) throws CoreException
  {
    super( layerName, layerType.getLinktype(), mapModel );

    GisTemplateLayerHelper.updateProperties( layerType, this );

    final GisTemplateMapModell innerMapModell = new GisTemplateMapModell( context, mapModel.getCoordinatesSystem(), selectionManager )
    {
      @Override
      public Object getThemeParent( final IKalypsoTheme theme )
      {
        return CascadingLayerKalypsoTheme.this;
      }
    };
    innerMapModell.setName( layerName );
    setInnerMapModel( innerMapModell );

    final List<JAXBElement< ? extends StyledLayerType>> layers = layerType.getLayer();
    // TODO: maybe get active layer from top-most Gismapview
    getInnerMapModel().createFromTemplate( layers, null );
  }

  /**
   * @see org.kalypso.ogc.gml.AbstractKalypsoTheme#dispose()
   */
  @Override
  public void dispose( )
  {
    final GisTemplateMapModell innerMapModel = getInnerMapModel();
    if( innerMapModel != null )
      innerMapModel.dispose();

    super.dispose();
  }

  public void fillLayerList( final List<JAXBElement< ? extends StyledLayerType>> layers, final String id, final String srsName, final IProgressMonitor monitor ) throws CoreException
  {
    final IMapModell innerMapModel = getInnerMapModel();
    final IKalypsoTheme[] themes = innerMapModel.getAllThemes();
    monitor.beginTask( "", themes.length ); //$NON-NLS-1$

    int count = 0;
    for( final IKalypsoTheme theme : themes )
    {
      final String layerId = id + "_" + count++; //$NON-NLS-1$
      final JAXBElement< ? extends StyledLayerType> layerElement = GisTemplateLayerHelper.configureLayer( theme, layerId, getFullExtent(), srsName, new SubProgressMonitor( monitor, 1 ) );
      layers.add( layerElement );
    }
  }

}