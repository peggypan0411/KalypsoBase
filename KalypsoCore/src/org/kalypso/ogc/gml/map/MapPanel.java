/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ogc.gml.map;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IDisposable;
import org.kalypso.commons.command.ICommandTarget;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.core.runtime.jobs.MutexRule;
import org.kalypso.contribs.eclipse.jobs.BufferPaintJob;
import org.kalypso.contribs.eclipse.jobs.BufferPaintJob.IPaintable;
import org.kalypso.contribs.eclipse.jobs.ImageCache;
import org.kalypso.contribs.eclipse.jobs.JobObserverJob;
import org.kalypso.contribs.eclipse.jobs.TextPaintable;
import org.kalypso.core.KalypsoCoreDebug;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.gml.IKalypsoCascadingTheme;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.IKalypsoLayerModell;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.map.layer.BufferedRescaleMapLayer;
import org.kalypso.ogc.gml.map.layer.CacscadingMapLayer;
import org.kalypso.ogc.gml.map.layer.DirectMapLayer;
import org.kalypso.ogc.gml.map.layer.SelectionMapLayer;
import org.kalypso.ogc.gml.map.listeners.IMapPanelListener;
import org.kalypso.ogc.gml.map.listeners.IMapPanelMTPaintListener;
import org.kalypso.ogc.gml.map.listeners.IMapPanelPaintListener;
import org.kalypso.ogc.gml.mapmodel.IKalypsoThemeVisitor;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.mapmodel.IMapModellListener;
import org.kalypso.ogc.gml.mapmodel.MapModellAdapter;
import org.kalypso.ogc.gml.mapmodel.MapModellHelper;
import org.kalypso.ogc.gml.selection.EasyFeatureWrapper;
import org.kalypso.ogc.gml.selection.IFeatureSelection;
import org.kalypso.ogc.gml.selection.IFeatureSelectionListener;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.ogc.gml.widgets.IWidgetManager;
import org.kalypso.ogc.gml.widgets.WidgetManager;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.graphics.transformation.WorldToScreenTransform;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * AWT canvas that displays a {@link org.kalypso.ogc.gml.mapmodel.MapModell}.
 * 
 * @author Andreas von D�mming
 * @author Gernot Belger
 */
public class MapPanel extends Canvas implements ComponentListener, IMapPanel
{
  // Rule used to force some of the layers to be rendered one after another.
  private final ISchedulingRule m_layerMutex = new MutexRule();

  /**
   * Maximum delay by which repaints to the map are produced.
   * 
   * @see java.awt.Component#repaint(long)
   */
  private static final long LAYER_REPAINT_MILLIS = 500;

  private static interface IListenerRunnable
  {
    void visit( final IMapPanelListener l );
  }

  private static final long serialVersionUID = 1L;

  private final boolean m_isMultitouchEnabled;

  static
  {
    System.setProperty( "sun.awt.noerasebackground", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  private final IFeatureSelectionManager m_selectionManager;

  private final Collection<ISelectionChangedListener> m_selectionListeners = new HashSet<>( 5 );

  private final IFeatureSelectionListener m_globalSelectionListener = new IFeatureSelectionListener()
  {
    @Override
    public void selectionChanged( final Object source, final IFeatureSelection selection )
    {
      globalSelectionChanged();
    }
  };

  private IKalypsoLayerModell m_model = null;

  private final WidgetManager m_widgetManager;

  protected GM_Envelope m_boundingBox = null;

  private GM_Envelope m_wishBBox;

  private final Collection<IMapPanelListener> m_mapPanelListeners = Collections.synchronizedSet( new LinkedHashSet<IMapPanelListener>() );

  private final Collection<IMapPanelPaintListener> m_paintListeners = Collections.synchronizedSet( new LinkedHashSet<IMapPanelPaintListener>() );

  private final Collection<IMapPanelMTPaintListener> m_postPaintListeners = new HashSet<>();

  private final Map<IKalypsoTheme, IMapLayer> m_layers = Collections.synchronizedMap( new HashMap<IKalypsoTheme, IMapLayer>() );

  private final ExtentHistory m_extentHistory = new ExtentHistory( 200 );

  private String m_message = ""; //$NON-NLS-1$

  // TODO: fetch from map-modell (aka from .gmt file)
  private final Color m_backgroundColor = new Color( 255, 255, 255 );

  private final IMapModellListener m_modellListener = new MapModellAdapter()
  {
    @Override
    public void themeAdded( final IMapModell source, final IKalypsoTheme theme )
    {
      if( theme.isVisible() )
        setBoundingBox( getWishBox(), false, true );

      updateStatus();
    }

    @Override
    public void themeOrderChanged( final IMapModell source )
    {
      invalidateMap();
    }

    @Override
    public void themeRemoved( final IMapModell source, final IKalypsoTheme theme, final boolean lastVisibility )
    {
      handleThemeRemoved( theme, lastVisibility );
    }

    @Override
    public void themeVisibilityChanged( final IMapModell source, final IKalypsoTheme theme, final boolean visibility )
    {
      invalidateMap();
    }

    @Override
    public void themeStatusChanged( final IMapModell source, final IKalypsoTheme theme )
    {
    }
  };

  private BufferPaintJob m_bufferPaintJob = null;

  /** One mutex-rule per panel, so painting jobs for one panel run one after another. */
  private final ISchedulingRule m_painterMutex = new MutexRule();

  /**
   * An image cache is used to provide the buffered images for the panel and also the buffered layers.<br>
   * This reduces the impact of recreating lots of buffered images when mutiple layers get repainted.
   */
  private final ImageCache m_imageCache = new ImageCache( 1, m_backgroundColor, true );

  /**
   * An image cache is used to provide the buffered images for the panel and also the buffered layers.<br>
   * This reduces the impact of recreating lots of buffered images when mutiple layers get repainted.
   */
  private final ImageCache m_layerImageCache = new ImageCache( 5, new Color( 255, 255, 255, 0 ), false );

  private IStatus m_status = Status.OK_STATUS;

  private Dimension m_size;

  private boolean m_useFullSelection = false;

  private BufferedImage m_imageBuffer = null;

  private IDisposable m_mtObject;

  public BufferedImage getBufferedImage( )
  {
    if( m_imageBuffer == null )
      paint( null );
    return m_imageBuffer;
  }

  public MapPanel( final ICommandTarget viewCommandTarget, final IFeatureSelectionManager manager )
  {
    if( "true".equals( System.getProperty( "org.kalypso.ogc.gml.mappanel.multitouch" ) ) ) //$NON-NLS-1$ //$NON-NLS-2$
      m_isMultitouchEnabled = true;
    else
      m_isMultitouchEnabled = false;

    m_selectionManager = manager;
    m_selectionManager.addSelectionListener( m_globalSelectionListener );

    m_widgetManager = new WidgetManager( viewCommandTarget, this );
    addMouseListener( m_widgetManager );
    addMouseMotionListener( m_widgetManager );
    addMouseWheelListener( m_widgetManager );
    addKeyListener( m_widgetManager );
    addComponentListener( this );
  }

  protected final GM_Envelope getWishBox( )
  {
    return m_wishBBox;
  }

  // REMARK: most probably we should always return the complete selection; filtering by themes does not make so much
// sense
  // However, we use this flag for the moment to keep this backwards compatible and avoid side effekt.
  // TODO: try this out when we are far from deploying
  @Override
  public void setUseFullSelection( final boolean useFullSelection )
  {
    m_useFullSelection = useFullSelection;
  }

  /**
   * Runns the given runnable on every listener in a safe way.
   */
  private void acceptListenersRunnable( final IListenerRunnable r )
  {
    final IMapPanelListener[] listeners = m_mapPanelListeners.toArray( new IMapPanelListener[m_mapPanelListeners.size()] );
    for( final IMapPanelListener l : listeners )
    {
      final ISafeRunnable code = new SafeRunnable()
      {
        @Override
        public void run( ) throws Exception
        {
          r.visit( l );
        }
      };

      SafeRunner.run( code );
    }
  }

  /**
   * Add a listener in the mapPanel that will be notified in specific changes. <br/>
   * At the moment there is only the message changed event.
   */
  @Override
  public void addMapPanelListener( final IMapPanelListener l )
  {
    m_mapPanelListeners.add( l );
  }

  public void addPostPaintListener( final IMapPanelMTPaintListener pl )
  {
    m_postPaintListeners.add( pl );
  }

  @Override
  public void addPaintListener( final IMapPanelPaintListener pl )
  {
    m_paintListeners.add( pl );
  }

  @Override
  public void addSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_selectionListeners.add( listener );
  }

  @Override
  public void componentHidden( final ComponentEvent e )
  {
    //
  }

  @Override
  public void componentMoved( final ComponentEvent e )
  {
    //
  }

  @Override
  public void componentResized( final ComponentEvent e )
  {
    /*
     * Bugfix: this prohibits a repaint, if the window is minimized: we do not repaint, and keep the old size. When the
     * component is shown again, the new size is the old size and we also do not need to paint.
     */

    // TRICKY: we are minimized, if the location is lesser than zero. Is there another way to find this out?
    if( !m_isMultitouchEnabled )
    {
      final Point locationOnScreen = getLocationOnScreen();
      if( locationOnScreen.x < 0 && locationOnScreen.y < 0 )
        return;
    }

    /* Only resize, if size really changed. */
    final Dimension size = getSize();
    if( ObjectUtils.equals( m_size, size ) )
      return;

    if( size != null && size.width == 0 && size.height == 0 )
      return;
    if( ObjectUtils.equals( m_size, size ) )
      return;

    m_size = size;

    setBoundingBox( m_wishBBox, false );
  }

  @Override
  public void componentShown( final ComponentEvent e )
  {
    setBoundingBox( m_wishBBox, false );
  }

  @Override
  public void dispose( )
  {
    removeMouseListener( m_widgetManager );
    removeMouseMotionListener( m_widgetManager );
    removeMouseWheelListener( m_widgetManager );
    removeKeyListener( m_widgetManager );
    removeComponentListener( this );

    m_selectionManager.removeSelectionListener( m_globalSelectionListener );

    m_widgetManager.dispose();

    if( m_mtObject != null )
      m_mtObject.dispose();

    setMapModell( null );

    // REMARK: this should not be necessary, but fixes the memory leak problem when opening/closing a .gmt file.
    // TODO: where is this map panel still referenced from?
    // Anwer: From the map-commands!
    m_selectionListeners.clear();
    m_mapPanelListeners.clear();
    m_paintListeners.clear();

    synchronized( this )
    {
      if( m_bufferPaintJob != null )
      {
        m_bufferPaintJob.dispose();
        m_bufferPaintJob = null;
      }

      m_imageCache.clear();
    }
  }

  protected void fireExtentChanged( final GM_Envelope oldExtent, final GM_Envelope newExtent )
  {
    acceptListenersRunnable( new IListenerRunnable()
    {
      @Override
      public void visit( final IMapPanelListener l )
      {
        l.onExtentChanged( MapPanel.this, oldExtent, newExtent );
      }
    } );
  }

  protected void fireMapModelChanged( final IKalypsoLayerModell oldModel, final IKalypsoLayerModell newModel )
  {
    acceptListenersRunnable( new IListenerRunnable()
    {
      @Override
      public void visit( final IMapPanelListener l )
      {
        l.onMapModelChanged( MapPanel.this, oldModel, newModel );
      }
    } );
  }

  /**
   * Must be invoked, if the message of the mapPanel has changed.
   */
  private void fireMessageChanged( final String message )
  {
    acceptListenersRunnable( new IListenerRunnable()
    {
      @Override
      public void visit( final IMapPanelListener l )
      {
        l.onMessageChanged( MapPanel.this, message );
      }
    } );
  }

  /**
   * Must be invoked, if the status of the mapPanel has changed.
   */
  private void fireStatusChanged( )
  {
    acceptListenersRunnable( new IListenerRunnable()
    {
      @Override
      public void visit( final IMapPanelListener l )
      {
        l.onStatusChanged( MapPanel.this );
      }
    } );
  }

  protected final void fireSelectionChanged( )
  {
    final ISelectionChangedListener[] listenersArray = m_selectionListeners.toArray( new ISelectionChangedListener[m_selectionListeners.size()] );

    final IStructuredSelection selection = (IStructuredSelection)getSelection();
    final SelectionChangedEvent e = new SelectionChangedEvent( this, selection );
    for( final ISelectionChangedListener l : listenersArray )
    {
      final Display display = PlatformUI.getWorkbench().getDisplay();
      display.syncExec( new Runnable()
      {
        @Override
        public void run( )
        {
          final SafeRunnable safeRunnable = new SafeRunnable()
          {
            /**
             * Overwritten because opening the message dialog here results in a NPE
             * 
             * @see org.eclipse.jface.util.SafeRunnable#handleException(java.lang.Throwable)
             */
            @Override
            public void handleException( final Throwable t )
            {
              t.printStackTrace();
            }

            @Override
            public void run( )
            {
              l.selectionChanged( e );
            }
          };

          SafeRunnable.run( safeRunnable );
        }
      } );
    }
  }

  @Override
  public GM_Envelope getBoundingBox( )
  {
    return m_boundingBox;
  }

  /**
   * @see org.kalypso.ogc.gml.map.IMapPanel#getScreenBounds()
   */
  @Override
  public Rectangle getScreenBounds( )
  {
    return getBounds();
  }

  /**
   * calculates the current map scale (denominator) as defined in the OGC SLD 1.0.0 specification
   * 
   * @return scale of the map
   */
  @Override
  public double getCurrentScale( )
  {
    final GeoTransform projection = getProjection();
    if( projection == null )
      return Double.NaN;

    // TODO: hot-spot: always recalculates the scale, should be cached
    return projection.getScale();
  }

  @Override
  public IKalypsoLayerModell getMapModell( )
  {
    return m_model;
  }

  @Override
  public String getMessage( )
  {
    return m_message;
  }

  @Override
  public ISelection getSelection( )
  {
    // See setUseFullSelection
    if( m_useFullSelection )
      return m_selectionManager;

    final IMapModell mapModell = getMapModell();
    if( mapModell == null )
      return StructuredSelection.EMPTY;

    // REMARK: we need an own implementation, as the feature selection
    // did return 'Feature' objects (whereas the FeatureSelection return sEasyFeatureWrappers)
    return new MapPanelSelection( m_selectionManager );

// final IKalypsoTheme activeTheme = mapModell.getActiveTheme();
// if( activeTheme instanceof IKalypsoFeatureTheme )
// return (ISelection) activeTheme.getAdapter( IFeatureSelection.class );
//
// return StructuredSelection.EMPTY;
  }

  @Override
  public IFeatureSelectionManager getSelectionManager( )
  {
    return m_selectionManager;
  }

  @Override
  public IWidgetManager getWidgetManager( )
  {
    return m_widgetManager;
  }

  protected void globalSelectionChanged( )
  {
    invalidateMap();

    fireSelectionChanged();
  }

  /**
   * Invalidates the whole map, all data is redrawn freshly.<br>
   * Should not be invoked from outside; normally every theme invalidates itself, if its data is changed; if not check,
   * if all events are correctly sent.<br>
   * Important: does not invalidate the theme's buffers, so this will in most cases not do what you want.. please always
   * invalidate the theme by correctly firing gml-events
   */
  @Override
  public void invalidateMap( )
  {
    synchronized( this )
    {
      /* Cancel old job if still running. */
      if( m_bufferPaintJob != null )
      {
        m_bufferPaintJob.dispose();
        m_bufferPaintJob = null;
      }

      final IMapModell mapModell = getMapModell();
      if( mapModell == null )
        return;

      final IPaintable paintable = createPaintable( mapModell );

      final BufferPaintJob bufferPaintJob = new BufferPaintJob( paintable, m_imageCache );
      bufferPaintJob.setRule( m_painterMutex );
      bufferPaintJob.setPriority( Job.SHORT );
      bufferPaintJob.setSystem( true );

      /* This jobs observes the paint-job and repaints the map all 5seconds and once after painting is done */
      final JobObserverJob repaintJob = new JobObserverJob( "Repaint map observer", bufferPaintJob, 1000 ) //$NON-NLS-1$
      {
        @Override
        protected void jobRunning( )
        {
          MapPanel.this.repaintMap();
        }
      };
      repaintJob.setSystem( true );
      repaintJob.schedule();

      m_bufferPaintJob = bufferPaintJob;
      // delay the Schedule, so if another invalidate comes within that time-span, no repaint happens at all
      bufferPaintJob.schedule( 100 );
    }

    repaintMap();
  }

  private IPaintable createPaintable( final IMapModell mapModell )
  {
    final GeoTransform projection = getProjection();

    if( projection == null )
    {
      final int width = getWidth();
      final int height = getHeight();
      final Point size = new Point( width, height );
      return new TextPaintable( size, "No Extent Set", m_backgroundColor ); //$NON-NLS-1$
    }

    final IMapLayer[] layers = getLayersForRendering();
    return new MapPanelPainter( this, layers, mapModell.getLabel(), projection );
  }

  /**
   * Paints contents of the map in the following order:
   * <ul>
   * <li>the buffered image containing the layers</li>
   * <li>the status, if not OK</li>
   * <li>all 'paint-listeners'</li>
   * <li>the current widget</li>
   * </ul>
   * 
   * @see java.awt.Component#paint(java.awt.Graphics)
   */
  @Override
  public void paint( final Graphics g )
  {
    final int width = getWidth();
    final int height = getHeight();
    if( height == 0 || width == 0 )
      return;

    // only recreate buffered image if size has changed
    if( m_imageBuffer == null || m_imageBuffer.getWidth() != width || m_imageBuffer.getHeight() != height )
      m_imageBuffer = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
    else
      // this seems to be enough for clearing the image here
      m_imageBuffer.flush();

    Graphics2D bufferGraphics = null;
    try
    {
      bufferGraphics = m_imageBuffer.createGraphics();
      bufferGraphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      bufferGraphics.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

      final BufferPaintJob bufferPaintJob = m_bufferPaintJob; // get copy (for more thread safety)
      if( bufferPaintJob != null )
      {
        paintBufferedMap( bufferGraphics, bufferPaintJob );
      }

      // TODO: at the moment, we paint the status just on top of the map, if we change this component to SWT, we should
      // show the statusComposite in a title bar, if the status is non-OK (with details button for a stack trace)
      paintStatus( bufferGraphics );

      final IMapPanelPaintListener[] pls = m_paintListeners.toArray( new IMapPanelPaintListener[] {} );
      for( final IMapPanelPaintListener pl : pls )
        pl.paint( bufferGraphics );

      paintWidget( bufferGraphics );
    }
    finally
    {
      if( bufferGraphics != null )
        bufferGraphics.dispose();
    }

    if( m_isMultitouchEnabled )
    {
      final IMapPanelMTPaintListener[] postls = m_postPaintListeners.toArray( new IMapPanelMTPaintListener[] {} );
      for( final IMapPanelMTPaintListener pl : postls )
        pl.paint( m_imageBuffer );
    }
    else
    {
      g.drawImage( m_imageBuffer, 0, 0, null );
    }
  }

  private void paintBufferedMap( final Graphics2D bufferGraphics, final BufferPaintJob bufferPaintJob )
  {
    final BufferedImage image = bufferPaintJob.getImage();
    if( image == null )
    {
      if( bufferPaintJob.getState() == Job.SLEEPING )
      {
        bufferPaintJob.wakeUp( 0 );
        return;
      }
    }

    final GM_Envelope imageBounds = imageBoundFromPaintable( bufferPaintJob );
    if( ObjectUtils.equals( imageBounds, m_boundingBox ) )
      bufferGraphics.drawImage( image, 0, 0, null );
    else
    {
      // If current buffer only shows part of the map, paint it into the right screen-rect
      final GeoTransform currentProjection = getProjection();
      if( currentProjection != null && imageBounds != null )
        MapPanelUtilities.paintIntoExtent( bufferGraphics, currentProjection, image, imageBounds, m_backgroundColor );
    }
  }

  private GM_Envelope imageBoundFromPaintable( final BufferPaintJob bufferPaintJob )
  {
    final IPaintable paintable = bufferPaintJob.getPaintable();
    if( paintable instanceof MapPanelPainter )
    {
      final MapPanelPainter mapPaintable = (MapPanelPainter)paintable;
      final GeoTransform world2screen = mapPaintable.getWorld2screen();
      return world2screen.getSourceRect();
    }

    return null;
  }

  /**
   * @see java.awt.Component#isDoubleBuffered()
   */
  @Override
  public boolean isDoubleBuffered( )
  {
    return true;
  }

  /**
   * If a message is present, paint it and return true
   */
  private void paintStatus( final Graphics2D g )
  {
    if( m_status.isOK() )
      return;

    final String message = m_status.getMessage();

    final int stringWidth = g.getFontMetrics().stringWidth( message );

    final int width = getWidth();
    final int height = getHeight();

    g.setColor( m_backgroundColor );
    g.fillRect( 0, 0, width, height );
    g.setColor( Color.black );

    g.drawString( message, (width - stringWidth) / 2, height / 2 );
  }

  /**
   * Removes this listener from the mapPanel.
   */
  @Override
  public void removeMapPanelListener( final IMapPanelListener l )
  {
    m_mapPanelListeners.remove( l );
  }

  @Override
  public void removePaintListener( final IMapPanelPaintListener pl )
  {
    m_paintListeners.remove( pl );
  }

  @Override
  public void removeSelectionChangedListener( final ISelectionChangedListener listener )
  {
    m_selectionListeners.remove( listener );
  }

  /**
   * This function sets the bounding box to this map panel and all its themes.
   * 
   * @param wishBBox
   *          The new extent, will be adapted so it fits into the current size of the panel.
   */
  @Override
  public void setBoundingBox( final GM_Envelope wishBBox )
  {
    setBoundingBox( wishBBox, true );
  }

  @Override
  public void setBoundingBox( final GM_Envelope wishBBox, final boolean useHistory )
  {
    setBoundingBox( wishBBox, useHistory, true );
  }

  @Override
  public void setBoundingBox( final GM_Envelope wishBBox, final boolean useHistory, final boolean invalidateMap )
  {
    final GM_Envelope oldExtent;

    synchronized( this )
    {
      oldExtent = m_boundingBox;

      /* The wished bounding box. */
      m_wishBBox = wishBBox;

      /* We do remember the wish-box here, this behaves more nicely if the size of the view changed meanwhile. */
      if( useHistory && m_wishBBox != null )
        m_extentHistory.push( m_wishBBox );

      m_boundingBox = determineBoundingBox();
    }

    if( invalidateMap )
    {
      /* Tell everyone, that the extent has changed. */
      fireExtentChanged( oldExtent, m_boundingBox );

      invalidateMap();
    }
    else
      repaintMap();
  }

  /**
   * Determines the real bounding box from the current wish box.<br>
   * If the wish box is currently null, we always maximize the map. This is specially nice for previously empty maps,
   * and a new theme is added.
   */
  private GM_Envelope determineBoundingBox( )
  {
    /* Adjust the new extent (using the wish bounding box). */
    final double ratio = MapPanelUtilities.getRatio( this );

    // FIXME: if m_wishBox == null at this point, full extent will be called at this point, which is very slow.
    // Probably we should put this in a separate job

    final GM_Envelope boundingBox = MapModellHelper.adjustBoundingBox( m_model, m_wishBBox, ratio );

    if( boundingBox != null )
    {
      KalypsoCoreDebug.MAP_PANEL.printf( "MinX: %d%n", boundingBox.getMin().getX() ); //$NON-NLS-1$
      KalypsoCoreDebug.MAP_PANEL.printf( "MinY: %d%n", boundingBox.getMin().getY() ); //$NON-NLS-1$
      KalypsoCoreDebug.MAP_PANEL.printf( "MaxX: %d%n", boundingBox.getMax().getX() ); //$NON-NLS-1$
      KalypsoCoreDebug.MAP_PANEL.printf( "MaxY: %d%n", boundingBox.getMax().getY() ); //$NON-NLS-1$
    }

    return boundingBox;
  }

  @Override
  public void setMapModell( final IKalypsoLayerModell modell )
  {
    final IKalypsoLayerModell oldModel;
    IMapLayer[] oldLayers = new IMapLayer[0];
    synchronized( this )
    {
      oldModel = m_model;
      if( oldModel != null )
      {
        oldModel.removeMapModelListener( m_modellListener );

        oldLayers = m_layers.values().toArray( new IMapLayer[m_layers.values().size()] );

        m_layers.clear();
      }
      m_model = modell;
    }

    // BUGFIX: dispose layers outside of sync block in order to avoid dead lock
    for( final IMapLayer layer : oldLayers )
      layer.dispose();

    if( modell != null )
      modell.addMapModelListener( m_modellListener );

    invalidateMap();

    updateStatus();

    fireMapModelChanged( oldModel, modell );
  }

  protected void updateStatus( )
  {
    if( m_model == null )
      setStatus( new Status( IStatus.INFO, KalypsoCorePlugin.getID(), Messages.getString( "org.kalypso.ogc.gml.map.MapPanel.20" ), null ) ); //$NON-NLS-1$
    else
    {
      // We should instead get a status from the model itself
      if( m_model.getThemeSize() == 0 )
        setStatus( new Status( IStatus.INFO, KalypsoCorePlugin.getID(), Messages.getString( "org.kalypso.ogc.gml.map.MapPanel.21" ), null ) ); //$NON-NLS-1$
      else
        setStatus( Status.OK_STATUS );
    }
  }

  /**
   * Sets the message of this mapPanel. Some widgets update it, so that the MapView could update the status-bar text.
   */
  @Override
  public void setMessage( final String message )
  {
    m_message = message;

    fireMessageChanged( message );
  }

  @Override
  public void setSelection( final ISelection selection )
  {
    if( selection instanceof IFeatureSelection )
    {
      final IFeatureSelection featureSelection = (IFeatureSelection)selection;
      final EasyFeatureWrapper[] allFeatures = featureSelection.getAllFeatures();
      getSelectionManager().setSelection( allFeatures );
    }
  }

  @Override
  public void update( final Graphics g )
  {
    // do not clear background, it flickers even if we double buffer
    paint( g );
  }

  @Override
  public void fireMouseMouveEvent( final int mousex, final int mousey )
  {
    final IMapModell mapModell = getMapModell();
    if( mapModell == null )
      return;

    final GeoTransform transform = getProjection();
    if( transform == null )
      return;

    final double gx = transform.getSourceX( mousex );
    final double gy = transform.getSourceY( mousey );

    final String cs = mapModell.getCoordinatesSystem();
    final GM_Point gmPoint = GeometryFactory.createGM_Point( gx, gy, cs );

    final IMapPanelListener[] listeners = m_mapPanelListeners.toArray( new IMapPanelListener[] {} );
    for( final IMapPanelListener mpl : listeners )
      mpl.onMouseMoveEvent( this, gmPoint, mousex, mousey );
  }

  @Override
  public GeoTransform getProjection( )
  {
    final GM_Envelope boundingBox = m_boundingBox;
    if( boundingBox == null )
      return null;

    final GeoTransform projection = new WorldToScreenTransform();
    projection.setSourceRect( boundingBox );
    final int width = getWidth();
    final int height = getHeight();
    projection.setDestRect( 0, 0, width, height, null );

    return projection;
  }

  @Override
  public ExtentHistory getExtentHistory( )
  {
    return m_extentHistory;
  }

  private void setStatus( final IStatus status )
  {
    if( StatusUtilities.equals( m_status, status ) )
      return;

    m_status = status;

    fireStatusChanged();
  }

  @Override
  public IStatus getStatus( )
  {
    return m_status;
  }

  @Override
  public void repaintMap( )
  {
    repaint( 50 );
  }

  @Override
  public BufferedImage getMapImage( )
  {
    final BufferPaintJob bufferPaintJob = m_bufferPaintJob; // get copy for thread safety
    if( bufferPaintJob == null )
      return null;

    return bufferPaintJob.getImage();
  }

  /**
   * Create the list of layers in the order it should be rendered.
   */
  private IMapLayer[] getLayersForRendering( )
  {
    final List<IMapLayer> result = new ArrayList<>( 20 );
    final List<IKalypsoFeatureTheme> visibleFestureThemes = new ArrayList<>( 10 );

    final IKalypsoThemeVisitor createLayerVisitor = new IKalypsoThemeVisitor()
    {
      @Override
      public boolean visit( final IKalypsoTheme theme )
      {
        if( theme.isVisible() )
        {
          final IMapLayer layer = getLayer( theme );
          result.add( layer );

          if( theme instanceof IKalypsoFeatureTheme )
            visibleFestureThemes.add( (IKalypsoFeatureTheme)theme );

          return true;
        }

        // No sense in descending into invisible cascading-themes
        return false;
      }
    };

    m_model.accept( createLayerVisitor, IKalypsoThemeVisitor.DEPTH_INFINITE );

    // Reverse list, last should be rendered first.
    Collections.reverse( result );

    final IKalypsoFeatureTheme[] selectionThemes = visibleFestureThemes.toArray( new IKalypsoFeatureTheme[visibleFestureThemes.size()] );
    /* Paint selection in same order as all themes */
    ArrayUtils.reverse( selectionThemes );
    // TODO: care for disposal of SelectionMapLayer
    result.add( new SelectionMapLayer( this, selectionThemes ) );
    result.add( new WidgetLayer( this, m_widgetManager ) );

    return result.toArray( new IMapLayer[result.size()] );
  }

  protected IMapLayer getLayer( final IKalypsoTheme theme )
  {
    synchronized( this )
    {
      final IMapLayer existingLayer = m_layers.get( theme );
      if( existingLayer != null )
        return existingLayer;

      // TODO: move into factory method; there should be an extension-point...
      final IMapLayer newLayer;
      if( theme instanceof IKalypsoCascadingTheme )
        newLayer = new CacscadingMapLayer( this, theme );
      else if( theme instanceof IKalypsoFeatureTheme )
      {
        // REMARK: un-comment to change to different rendering strategy. I like
        // 'BufferedRescale' best...
        // newLayer = new DirectMapLayer( this, theme );
        // newLayer = new BufferedMapLayer( this, theme );

        // Render asynchronous: no
        // Repaint during rendering: yes
        newLayer = new BufferedRescaleMapLayer( this, theme, new MutexRule(), true, m_layerImageCache, LAYER_REPAINT_MILLIS );
      }
      else if( theme.getClass().getName().endsWith( "KalypsoWMSTheme" ) ) //$NON-NLS-1$
      {
        // Render asynchronously: yes (own mutex)
        // Repaint during rendering: no
        newLayer = new BufferedRescaleMapLayer( this, theme, new MutexRule(), false, m_layerImageCache );
      }
      else if( theme.getClass().getName().endsWith( "KalypsoScaleTheme" ) ) //$NON-NLS-1$
        newLayer = new DirectMapLayer( this, theme );
      else if( theme.getClass().getName().endsWith( "KalypsoLegendTheme" ) ) //$NON-NLS-1$
        newLayer = new DirectMapLayer( this, theme );
      else
      {
        // Render asynchronous: no
        // Repaint during rendering: no
        newLayer = new BufferedRescaleMapLayer( this, theme, m_layerMutex, false, m_layerImageCache );
      }

      m_layers.put( theme, newLayer );
      return newLayer;
    }
  }

  protected void handleThemeRemoved( final IKalypsoTheme theme, final boolean lastVisibility )
  {
    final IMapLayer layer = m_layers.remove( theme );
    if( layer != null )
      layer.dispose();

    if( lastVisibility )
      setBoundingBox( m_wishBBox, false, true );

    updateStatus();
  }

  @Override
  public boolean isMultitouchEnabled( )
  {
    return m_isMultitouchEnabled;
  }

  public void setMTObject( final IDisposable mtApp )
  {
    m_mtObject = mtApp;
  }

  @Override
  public Object getMTObject( )
  {
    return m_mtObject;
  }

  void checkBoundingBox( )
  {
    if( m_boundingBox == null )
      setBoundingBox( m_wishBBox, false, false );
  }

  /**
   * Lets the active widget paint itself.
   */
  private void paintWidget( final Graphics g )
  {
    g.setColor( Color.RED );
    m_widgetManager.paintWidget( g );
  }
}