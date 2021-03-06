/*
 * --------------- Kalypso-Header --------------------------------------------------------------------
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
 * ---------------------------------------------------------------------------------------------------
 */
package org.kalypso.ogc.gml;

import java.awt.Graphics;
import java.net.URL;
import java.util.logging.Logger;

import javax.media.jai.TiledImage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.i18n.I10nString;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.template.types.StyledLayerType;
import org.kalypso.transformation.transformer.GeoTransformerFactory;
import org.kalypso.transformation.transformer.IGeoTransformer;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree_impl.gml.binding.commons.RectifiedGridDomain;
import org.kalypsodeegree_impl.tools.TransformationUtilities;

/**
 * KalypsoPictureTheme
 * <p>
 * created by
 *
 * @author kuepfer (20.05.2005)
 */
public abstract class KalypsoPictureTheme extends AbstractKalypsoTheme
{
  // TODO: use tracing instead
  private static final Logger LOGGER = Logger.getLogger( KalypsoPictureTheme.class.getName() );

  private final ImageHolder m_image = new ImageHolder();

  private RectifiedGridDomain m_domain;

  private final StyledLayerType m_layerType;

  public KalypsoPictureTheme( final I10nString layerName, final StyledLayerType layerType, final URL context, final IMapModell modell )
  {
    super( layerName, layerType.getLinktype(), modell );

    setContext( context );

    m_layerType = layerType;
  }

  @Override
  public void dispose( )
  {
    if( m_image != null )
      m_image.dispose();

    super.dispose();
  }

  public String getSource( )
  {
    return m_layerType.getHref();
  }

  @Override
  public GM_Envelope getFullExtent( )
  {
    if( m_domain == null )
      return null;

    try
    {
      final GM_Envelope gmEnvelope = m_domain.getGM_Envelope( m_domain.getCoordinateSystem() );
      final String kalypsoCrs = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
      final IGeoTransformer geoTransformer = GeoTransformerFactory.getGeoTransformer( kalypsoCrs );
      return geoTransformer.transform( gmEnvelope );
    }
    catch( final Exception e2 )
    {
      e2.printStackTrace();
      KalypsoPictureTheme.LOGGER.warning( Messages.getString( "org.kalypso.ogc.gml.KalypsoPictureTheme.9" ) ); //$NON-NLS-1$
    }
    return null;
  }

  protected ImageHolder getImage( )
  {
    return m_image;
  }

  protected RectifiedGridDomain getRectifiedGridDomain( )
  {
    return m_domain;
  }

  protected StyledLayerType getStyledLayerType( )
  {
    return m_layerType;
  }

  @Override
  public IStatus paint( final Graphics g, final GeoTransform p, final Boolean selected, final IProgressMonitor monitor )
  {
    if( selected != null && selected )
      return Status.OK_STATUS;

    if( m_domain == null )
      return Status.OK_STATUS;

    final TiledImage image = m_image.getImage();
    if( image == null )
      return Status.OK_STATUS;

    try
    {
      final String pictureCrs = m_domain.getCoordinateSystem();
      final GM_Envelope envelope = m_domain.getGM_Envelope( pictureCrs );
      final String crs = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();

      TransformationUtilities.transformImage( image, envelope, crs, p, g );

      return Status.OK_STATUS;
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      final IStatus status = StatusUtilities.statusFromThrowable( e );
      setStatus( status );
      return status;
    }
  }

  protected void setRectifiedGridDomain( final RectifiedGridDomain domain )
  {
    m_domain = domain;
  }

  protected final URL loadImage( final String filePath )
  {
    final IStatus status = m_image.loadImage( filePath, getContext() );
    setStatus( status );

    return m_image.getImageURL();
  }
}