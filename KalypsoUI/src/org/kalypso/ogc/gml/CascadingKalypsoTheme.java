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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.kalypso.commons.i18n.I10nString;
import org.kalypso.commons.i18n.ITranslator;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.java.net.UrlResolverSingleton;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.template.gismapview.Gismapview;
import org.kalypso.template.types.StyledLayerType;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.xml.sax.InputSource;

/**
 * @author Stefan Kurzbach
 */
public class CascadingKalypsoTheme extends AbstractCascadingLayerTheme
{
  private boolean m_resolved = false;

  @Override
  public boolean isLoaded( )
  {
    return m_resolved;
  }

  private final IResourceChangeListener m_resourceChangeListener = new IResourceChangeListener()
  {
    @Override
    public void resourceChanged( final IResourceChangeEvent event )
    {
      handleResourceChanged( event );
    }
  };

  private static URL resolveUrl( final URL context, final String viewRefUrl ) throws CoreException
  {
    try
    {
      return UrlResolverSingleton.resolveUrl( context, viewRefUrl );
    }
    catch( final MalformedURLException e )
    {
      throw new CoreException( StatusUtilities.statusFromThrowable( e, Messages.getString( "org.kalypso.ogc.gml.CascadingKalypsoTheme.0" ) + context + Messages.getString( "org.kalypso.ogc.gml.CascadingKalypsoTheme.1" ) + viewRefUrl ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private final String m_mapViewRefUrl;

  private final IFile m_file;

  public CascadingKalypsoTheme( final I10nString layerName, final StyledLayerType layerType, final URL context, final IFeatureSelectionManager selectionManager, final IMapModell mapModel ) throws CoreException
  {
    super( layerName, "gmt", mapModel ); //$NON-NLS-1$

    m_mapViewRefUrl = layerType.getHref();

    GisTemplateLayerHelper.updateProperties( layerType, this );

    final URL url = CascadingKalypsoTheme.resolveUrl( context, m_mapViewRefUrl );
    setInnerMapModel( new GisTemplateMapModell( url, mapModel.getCoordinatesSystem(), selectionManager )
    {
      @Override
      public Object getThemeParent( final IKalypsoTheme theme )
      {
        return CascadingKalypsoTheme.this;
      }
    } );

    m_file = ResourceUtilities.findFileFromURL( url );
    m_resolved = true;
    if( m_file != null )
    {
      m_file.getWorkspace().addResourceChangeListener( m_resourceChangeListener, IResourceChangeEvent.POST_CHANGE );
      startLoadJob();
    }
    else
      throw new CoreException( new Status( IStatus.ERROR, KalypsoGisPlugin.getId(), Messages.getString( "org.kalypso.ogc.gml.CascadingKalypsoTheme.3" ) + url.toExternalForm() + Messages.getString( "org.kalypso.ogc.gml.CascadingKalypsoTheme.4" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public synchronized void createGismapTemplate( final GM_Envelope bbox, final String srsName, final IProgressMonitor monitor ) throws CoreException
  {
    if( m_file != null )
    {
      try
      {
        /* Remove resource listener while saving: prohibits unnecessary reloading when file is saved */
        m_file.getWorkspace().removeResourceChangeListener( m_resourceChangeListener );

        getInnerMapModel().saveGismapTemplate( bbox, srsName, monitor, m_file );
      }
      finally
      {
        m_file.getWorkspace().addResourceChangeListener( m_resourceChangeListener, IResourceChangeEvent.POST_CHANGE );
      }
    }
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

    if( m_resourceChangeListener != null )
      m_file.getWorkspace().removeResourceChangeListener( m_resourceChangeListener );

    super.dispose();
  }

  /**
   * Returns the reference to the cascaded .gmt file.
   */
  public String getMapViewRefUrl( )
  {
    return m_mapViewRefUrl;
  }

  protected synchronized IStatus loadJob( final IFile file )
  {
    InputStream contents = null;
    try
    {
      contents = file.getContents();
      final InputSource inputSource = new InputSource( contents );
      final Gismapview innerGisView = GisTemplateHelper.loadGisMapView( inputSource );
      if( innerGisView.getName() != null )
      {
        final ITranslator translator = getMapModell().getName().getTranslator();
        final I10nString innerName = new I10nString( innerGisView.getName(), translator );
        CascadingKalypsoTheme.this.setName( innerName );
      }

      getInnerMapModel().createFromTemplate( innerGisView );
      fireStatusChanged( this );
    }
    catch( final Throwable e )
    {
      final String msessage = Messages.getString( "org.kalypso.ogc.gml.CascadingKalypsoTheme.6", file.getName() ); //$NON-NLS-1$
      final IStatus status = StatusUtilities.statusFromThrowable( e, msessage );
      setStatus( status );
      return status;
    }
    finally
    {
      IOUtils.closeQuietly( contents );
      m_resolved = true;
    }
    return Status.OK_STATUS;
  }

  protected void startLoadJob( )
  {
    m_resolved = false;
    final IFile file = m_file;
    final Job job = new Job( Messages.getString( "org.kalypso.ogc.gml.CascadingKalypsoTheme.5" ) + m_file.getName() ) //$NON-NLS-1$
    {
      @Override
      public IStatus run( final IProgressMonitor monitor )
      {
        return loadJob( file );
      }
    };
    job.setRule( m_file );
    job.schedule();
  }

  protected void handleResourceChanged( final IResourceChangeEvent event )
  {
    if( m_file == null )
      return;

    final IResourceDelta rootDelta = event.getDelta();
    final IResourceDelta fileDelta = rootDelta.findMember( m_file.getFullPath() );
    if( fileDelta == null )
      return;
    final int kind = fileDelta.getKind();
    switch( kind )
    {
      case IResourceDelta.CHANGED:
      case IResourceDelta.ADDED:
        startLoadJob();
        break;

      case IResourceDelta.REMOVED:
        // TODO: release map and file
        setStatus( new Status( IStatus.ERROR, KalypsoGisPlugin.getId(), Messages.getString( "org.kalypso.ogc.gml.CascadingKalypsoTheme.2" ) + m_file.getFullPath() ) ); //$NON-NLS-1$
        break;
    }
  }

}