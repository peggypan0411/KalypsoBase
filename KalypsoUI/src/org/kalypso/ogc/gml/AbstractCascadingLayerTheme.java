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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.kalypso.commons.i18n.I10nString;
import org.kalypso.ogc.gml.mapmodel.IKalypsoThemeVisitor;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.mapmodel.IMapModellListener;
import org.kalypso.ogc.gml.mapmodel.MapModellAdapter;
import org.kalypso.template.types.StyledLayerType;
import org.kalypso.ui.ImageProvider;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.GM_Envelope;

/**
 * @author Stefan Kurzbach extended by Dirk Kuch
 */
abstract class AbstractCascadingLayerTheme extends AbstractKalypsoTheme implements IKalypsoCascadingTheme, IKalypsoSaveableTheme
{
  private GisTemplateMapModell m_innerMapModel;

  private final IMapModellListener m_modelListener = new MapModellAdapter()
  {
    @Override
    public void themeActivated( final IMapModell source, final IKalypsoTheme previouslyActive, final IKalypsoTheme nowActive )
    {
      handleThemeActivated( nowActive );
    }

    @Override
    public void themeAdded( final IMapModell source, final IKalypsoTheme theme )
    {
      // HACK FIXME: commented out so a map with wms themes may still work
      // else map loading with wms themes that are not accessible cannot be used at the moment...

      if( isVisible() )
      {
        // FIXME: we should fire a 'themeAdded' event for our listeners instead; explicitely
        // fireing a repaint may cause trouble for headless mode
        doFireRepaintRequested( null/* theme.getFullExtent() */);
      }

      handleThemeStatusChanged( AbstractCascadingLayerTheme.this );
    }

    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#themeContextChanged(org.kalypso.ogc.gml.mapmodel.IMapModell, org.kalypso.ogc.gml.IKalypsoTheme)
     */
    @Override
    public void themeContextChanged( final IMapModell source, final IKalypsoTheme theme )
    {
      handleThemeContextChanged();
    }

    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#themeOrderChanged(org.kalypso.ogc.gml.mapmodel.IMapModell)
     */
    @Override
    public void themeOrderChanged( final IMapModell source )
    {
      // FIXME: we should fire a 'themeOrder' event for our listeners instead; explicitely
      // fireing a repaint may cause trouble for headless mode
      doFireRepaintRequested( getFullExtent() );
      handleThemeStatusChanged( AbstractCascadingLayerTheme.this );
    }

    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#themeRemoved(org.kalypso.ogc.gml.mapmodel.IMapModell, org.kalypso.ogc.gml.IKalypsoTheme, boolean)
     */
    @Override
    public void themeRemoved( final IMapModell source, final IKalypsoTheme theme, final boolean lastVisibility )
    {
      if( lastVisibility )
      {
        // FIXME: we should fire a 'themeRemoved' event for our listeners instead; explicitely
        // fireing a repaint may cause trouble for headless mode
        doFireRepaintRequested( theme.getFullExtent() );
      }

      handleThemeStatusChanged( AbstractCascadingLayerTheme.this );
    }

    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#themeStatusChanged(org.kalypso.ogc.gml.mapmodel.IMapModell, org.kalypso.ogc.gml.IKalypsoTheme)
     */
    @Override
    public void themeStatusChanged( final IMapModell source, final IKalypsoTheme theme )
    {
      handleThemeStatusChanged( theme );
    }

    /**
     * @see org.kalypso.ogc.gml.mapmodel.MapModellAdapter#themeVisibilityChanged(org.kalypso.ogc.gml.mapmodel.IMapModell, org.kalypso.ogc.gml.IKalypsoTheme, boolean)
     */
    @Override
    public void themeVisibilityChanged( final IMapModell source, final IKalypsoTheme theme, final boolean visibility )
    {
      fireVisibilityChanged( theme, theme.isVisible() );
// doFireRepaintRequested( theme.getFullExtent() );
    }
  };

  public AbstractCascadingLayerTheme( final I10nString name, final String linktype, final IMapModell mapModel )
  {
    super( name, linktype, mapModel );
  }

  protected void fireVisibilityChanged( final IKalypsoTheme theme, final boolean visible )
  {
    acceptListenersRunnable( new IListenerRunnable()
    {
      @Override
      public void visit( final IKalypsoThemeListener l )
      {
        if( l == null )
          return;

        l.visibilityChanged( theme, visible );
      }
    } );
  }

  protected void doFireRepaintRequested( final GM_Envelope bbox )
  {
    fireRepaintRequested( bbox );
  }

  /* delegate methods to IMapModell */

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#accept(org.kalypso.ogc.gml.mapmodel.IKalypsoThemeVisitor, int)
   */
  @Override
  public void accept( final IKalypsoThemeVisitor visitor, final int depth_infinite )
  {
    m_innerMapModel.accept( visitor, depth_infinite );
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#accept(org.kalypso.ogc.gml.mapmodel.IKalypsoThemeVisitor, int, org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public void accept( final IKalypsoThemeVisitor visitor, final int depth_infinite, final IKalypsoTheme theme )
  {
    m_innerMapModel.accept( visitor, depth_infinite, theme );
  }

  /**
   * @param theme
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#activateTheme(org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public void activateTheme( final IKalypsoTheme theme )
  {
    m_innerMapModel.activateTheme( theme );
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#addMapModelListener(org.kalypso.ogc.gml.mapmodel.IMapModellListener)
   */
  @Override
  public void addMapModelListener( final IMapModellListener l )
  {
    m_innerMapModel.addMapModelListener( l );
  }

  /**
   * @param theme
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#addTheme(org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public void addTheme( final IKalypsoTheme theme )
  {
    m_innerMapModel.addTheme( theme );
  }

  @Override
  public IKalypsoTheme addLayer( final StyledLayerType layer ) throws CoreException
  {
    return m_innerMapModel.addLayer( layer );
  }

  @Override
  public IKalypsoTheme insertLayer( final StyledLayerType layer, final int position ) throws Exception
  {
    return m_innerMapModel.insertLayer( layer, position );
  }

  @Override
  public URL getContext( )
  {
    return m_innerMapModel.getContext();
  }

  @Override
  public IKalypsoTheme getActiveTheme( )
  {
    return m_innerMapModel.getActiveTheme();
  }

  @Override
  public IKalypsoTheme[] getAllThemes( )
  {
    return m_innerMapModel.getAllThemes();
  }

  @Override
  public String getCoordinatesSystem( )
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public GM_Envelope getFullExtent( )
  {
    if( getInnerMapModel() != null )
      return getInnerMapModel().getFullExtentBoundingBox();
    else
      return null;
  }

  /**
   * @return
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#getFullExtentBoundingBox()
   */
  @Override
  public GM_Envelope getFullExtentBoundingBox( )
  {
    return m_innerMapModel.getFullExtentBoundingBox();
  }

  @Override
  public ImageDescriptor getDefaultIcon( )
  {
    return KalypsoGisPlugin.getImageProvider().getImageDescriptor( ImageProvider.DESCRIPTORS.IMAGE_THEME_CASCADING );
  }

  @Override
  public GisTemplateMapModell getInnerMapModel( )
  {
    return m_innerMapModel;
  }

  /**
   * Overwritten in order to have correct parent for tree strucutures.
   * 
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#getThemeParent(org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public Object getThemeParent( final IKalypsoTheme theme )
  {
    // do not delegate to inner model, this would be wrong.
    return this;
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#getTheme(int)
   */
  @Override
  public IKalypsoTheme getTheme( final int pos )
  {
    return m_innerMapModel.getTheme( pos );
  }

  /**
   * @return
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#getThemeSize()
   */
  @Override
  public int getThemeSize( )
  {
    return m_innerMapModel.getThemeSize();
  }

  protected void handleThemeActivated( final IKalypsoTheme theme )
  {
    fireStatusChanged( theme );
  }

  protected void handleThemeContextChanged( )
  {
    fireContextChanged();
  }

  protected void handleThemeStatusChanged( final IKalypsoTheme theme )
  {
    fireStatusChanged( theme );
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#insertTheme(org.kalypso.ogc.gml.IKalypsoTheme, int)
   */
  @Override
  public void insertTheme( final IKalypsoTheme theme, final int position )
  {
    m_innerMapModel.insertTheme( theme, position );
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#internalActivate(org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public void internalActivate( final IKalypsoTheme theme )
  {
    m_innerMapModel.internalActivate( theme );
  }

  /**
   * @param theme
   * @return
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#isThemeActivated(org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public boolean isThemeActivated( final IKalypsoTheme theme )
  {
    return m_innerMapModel.isThemeActivated( theme );
  }

  /**
   * @param theme
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#moveDown(org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public void moveDown( final IKalypsoTheme theme )
  {
    m_innerMapModel.moveDown( theme );
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#moveUp(org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public void moveUp( final IKalypsoTheme theme )
  {
    m_innerMapModel.moveUp( theme );
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#paint(java.awt.Graphics, org.kalypsodeegree.graphics.transformation.GeoTransform, org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public IStatus paint( final Graphics g, final GeoTransform p, final IProgressMonitor monitor )
  {
    return paint( g, p, null, monitor );
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoTheme#paint(java.awt.Graphics, org.kalypsodeegree.graphics.transformation.GeoTransform, java.lang.Boolean, org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public IStatus paint( final Graphics g, final GeoTransform p, final Boolean selected, final IProgressMonitor monitor )
  {
    if( m_innerMapModel == null )
      return Status.OK_STATUS;

    return m_innerMapModel.paint( g, p, monitor );
  }

  /**
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#removeMapModelListener(org.kalypso.ogc.gml.mapmodel.IMapModellListener)
   */
  @Override
  public void removeMapModelListener( final IMapModellListener l )
  {
    m_innerMapModel.removeMapModelListener( l );
  }

  /**
   * @param theme
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#removeTheme(org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public void removeTheme( final IKalypsoTheme theme )
  {
    m_innerMapModel.removeTheme( theme );
  }

  @Override
  public void saveFeatures( final IProgressMonitor monitor ) throws CoreException
  {
    if( getInnerMapModel() == null )
      return;

    for( final IKalypsoTheme theme : getInnerMapModel().getAllThemes() )
    {
      if( theme instanceof IKalypsoSaveableTheme )
        ((IKalypsoSaveableTheme)theme).saveFeatures( monitor );
    }
  }

  @Override
  public boolean isDirty( )
  {
    if( getInnerMapModel() == null )
      return false;

    for( final IKalypsoTheme theme : getInnerMapModel().getAllThemes() )
    {
      if( theme instanceof IKalypsoSaveableTheme )
      {
        if( ((IKalypsoSaveableTheme)theme).isDirty() )
          return true;
      }
    }

    return false;
  }

  protected void setInnerMapModel( final GisTemplateMapModell model )
  {
    if( m_innerMapModel != null )
      m_innerMapModel.removeMapModelListener( m_modelListener );

    m_innerMapModel = model;

    m_innerMapModel.addMapModelListener( m_modelListener );
  }

  /**
   * @param theme1
   * @param theme2
   * @see org.kalypso.ogc.gml.mapmodel.IMapModell#swapThemes(org.kalypso.ogc.gml.IKalypsoTheme, org.kalypso.ogc.gml.IKalypsoTheme)
   */
  @Override
  public void swapThemes( final IKalypsoTheme theme1, final IKalypsoTheme theme2 )
  {
    m_innerMapModel.swapThemes( theme1, theme2 );
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoTheme#getLabel()
   */
  @Override
  public String getLabel( )
  {
    return m_innerMapModel.getLabel();
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoTheme#setName(org.kalypso.contribs.java.lang.I10nString)
   */
  @Override
  public void setName( final I10nString name )
  {
    m_innerMapModel.setName( name );

    super.setName( name );
  }
}