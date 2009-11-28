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
package org.kalypso.ogc.gml;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.kalypso.commons.command.ICommand;
import org.kalypso.commons.i18n.I10nString;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.contribs.java.awt.HighlightGraphics;
import org.kalypso.core.KalypsoCoreDebug;
import org.kalypso.core.KalypsoCoreExtensions;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.i18n.Messages;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypsodeegree.graphics.displayelements.DisplayElement;
import org.kalypsodeegree.graphics.sld.UserStyle;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.event.FeatureStructureChangeModellEvent;
import org.kalypsodeegree.model.feature.event.FeaturesChangedModellEvent;
import org.kalypsodeegree.model.feature.event.IGMLWorkspaceModellEvent;
import org.kalypsodeegree.model.feature.event.ModellEvent;
import org.kalypsodeegree.model.feature.event.ModellEventListener;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree_impl.model.feature.FeatureFactory;
import org.kalypsodeegree_impl.model.sort.SplitSort;

/**
 * @author Andreas von D�mming
 */
public class KalypsoFeatureTheme extends AbstractKalypsoTheme implements IKalypsoFeatureTheme, ModellEventListener, IKalypsoUserStyleListener
{
  /* Preserve order of styles. */
  private final Map<IKalypsoUserStyle, UserStylePainter> m_styleMap = new LinkedHashMap<IKalypsoUserStyle, UserStylePainter>();

  private CommandableWorkspace m_workspace;

  private final IFeatureType m_featureType;

  private final FeatureList m_featureList;

  private final IFeatureSelectionManager m_selectionManager;

  private final String m_featurePath;

  /**
   * (Crude) hack: remember that we only have a (syntetic) list of only one feature.<br>
   * Fixes the problem, that single features do not correctly get updated.
   */
  private boolean m_isSingleFeature = false;

  /**
   * Holds the descriptor for the default icon of this theme. Is used in legends, such as the outline.
   */
  private Image m_featureThemeIcon;

  public KalypsoFeatureTheme( final CommandableWorkspace workspace, final String featurePath, final I10nString name, final IFeatureSelectionManager selectionManager, final IMapModell mapModel )
  {
    super( name, "FeatureTheme", mapModel ); //$NON-NLS-1$

    m_workspace = workspace;
    m_featurePath = featurePath;
    m_selectionManager = selectionManager;

    final Object featureFromPath = m_workspace.getFeatureFromPath( m_featurePath );

    if( featureFromPath instanceof FeatureList )
    {
      m_featureList = (FeatureList) featureFromPath;
      m_featureType = m_workspace.getFeatureTypeFromPath( m_featurePath );
    }
    else if( featureFromPath instanceof Feature )
    {
      final Feature singleFeature = (Feature) featureFromPath;
      final Feature parent = singleFeature.getOwner();
      m_featureList = new SplitSort( parent, singleFeature.getParentRelation() );
      m_featureList.add( singleFeature );
      m_featureType = singleFeature.getFeatureType();
      m_isSingleFeature = true;
    }
    else
    {
      // Should'nt we throw an exception here?
      m_featureList = null;
      m_featureType = null;
      setStatus( StatusUtilities.createStatus( IStatus.WARNING, Messages.getString("org.kalypso.ogc.gml.KalypsoFeatureTheme.0") + featurePath, null ) ); //$NON-NLS-1$
    }

    m_workspace.addModellListener( this );
  }

  @Override
  public void dispose( )
  {
    final Set<IKalypsoUserStyle> set = m_styleMap.keySet();
    final IKalypsoUserStyle[] styles = set.toArray( new IKalypsoUserStyle[set.size()] );
    for( final IKalypsoUserStyle element : styles )
      removeStyle( element );

    if( m_workspace != null )
    {
      m_workspace.removeModellListener( this );
      m_workspace = null;
    }

    if( m_featureThemeIcon != null )
      m_featureThemeIcon.dispose();

    super.dispose();
  }

  private void setDirty( )
  {
    fireRepaintRequested( getFullExtent() );
  }

  public CommandableWorkspace getWorkspace( )
  {
    return m_workspace;
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoFeatureTheme#getFeatureType()
   */
  public IFeatureType getFeatureType( )
  {
    return m_featureType;
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoFeatureTheme#getFeaturePath()
   */
  public String getFeaturePath( )
  {
    return m_featurePath;
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoTheme#paint(java.awt.Graphics,
   *      org.kalypsodeegree.graphics.transformation.GeoTransform, java.lang.Boolean,
   *      org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public IStatus paint( final Graphics g, final GeoTransform p, final Boolean selected, final IProgressMonitor monitor )
  {
    final Graphics graphics = wrapGrahicForSelection( g, selected );

    final IPaintDelegate paintDelegate = new IPaintDelegate()
    {
      public void paint( final DisplayElement displayElement, final IProgressMonitor paintMonitor ) throws CoreException
      {
        displayElement.paint( graphics, p, paintMonitor );

        // DEBUG output to show feature envelope: TODO: put into tracing option
        // final GM_Envelope envelope = displayElement.getFeature().getEnvelope();
        // if( envelope != null )
        // {
        // GM_Position destPointMin = projection.getDestPoint( envelope.getMin() );
        // GM_Position destPointMax = projection.getDestPoint( envelope.getMax() );
        //
        // GM_Envelope_Impl env = new GM_Envelope_Impl( destPointMin, destPointMax, null );
        //
        // graphics.drawRect( (int) env.getMin().getX(), (int) env.getMin().getY(), (int) env.getWidth(), (int)
        // env.getHeight()
        // );
        // }
      }
    };

    try
    {
      final double scale = p.getScale();
      final GM_Envelope bbox = p.getSourceRect();
      paint( scale, bbox, selected, monitor, paintDelegate );

      if( m_featureList != null && KalypsoCoreDebug.SPATIAL_INDEX_PAINT.isEnabled() )
        m_featureList.paint( g, p );
    }
    catch( final CoreException e )
    {
      return e.getStatus();
    }

    return Status.OK_STATUS;
  }

  /**
   * Determines, if a {@link HighlightGraphics} will be used to draw the selection or not.
   */
  // TODO (future): replace highlightGraphics concept by highlight-style
  private Graphics wrapGrahicForSelection( final Graphics g, final Boolean selected )
  {
    /* If we draw normally, never use highlight graphics */
    if( selected == null || selected == false )
      return g;

    boolean hasSelectionStyle = false;
    for( final IKalypsoUserStyle style : m_styleMap.keySet() )
    {
      if( style.isUsedForSelection() )
      {
        hasSelectionStyle = true;
      }
    }

    if( hasSelectionStyle )
      return g;

    /* Use normal style with highlight graphics to paint */
    return new HighlightGraphics( (Graphics2D) g );
  }

  public void paint( final double scale, final GM_Envelope bbox, final Boolean selected, final IProgressMonitor monitor, final IPaintDelegate delegate ) throws CoreException
  {
    final UserStylePainter[] styleArray = getStylesForPaint( selected );

    final SubMonitor progress = SubMonitor.convert( monitor, Messages.getString( "org.kalypso.ogc.gml.KalypsoFeatureTheme.1" ), styleArray.length ); //$NON-NLS-1$

    if( m_featureList == null )
      return;

    final GMLWorkspace workspace = getWorkspace();

    for( final UserStylePainter painter : styleArray )
    {
      final SubMonitor childProgress = progress.newChild( 1 );
      painter.paintSelected( workspace, scale, bbox, m_featureList, selected, childProgress, delegate );
      ProgressUtilities.done( childProgress );
    }
  }

  private UserStylePainter[] getStylesForPaint( final Boolean selected )
  {
    final List<UserStylePainter> normalStyles = new ArrayList<UserStylePainter>( m_styleMap.size() );
    final List<UserStylePainter> selectionStyles = new ArrayList<UserStylePainter>( m_styleMap.size() );
    for( final Entry<IKalypsoUserStyle, UserStylePainter> entry : m_styleMap.entrySet() )
    {
      if( entry.getKey().isUsedForSelection() )
      {
        selectionStyles.add( entry.getValue() );
      }
      else
      {
        normalStyles.add( entry.getValue() );
      }
    }

    /* If no selection style is present, we will paint with old HighlightGraphics stuff, so return normal styles. */
    if( selected == null || selected == false || selectionStyles.size() == 0 )
      return normalStyles.toArray( new UserStylePainter[normalStyles.size()] );

    return selectionStyles.toArray( new UserStylePainter[selectionStyles.size()] );
  }

  public void addStyle( final IKalypsoUserStyle style )
  {
    final UserStylePainter styleDisplayMap = new UserStylePainter( style, m_selectionManager );
    m_styleMap.put( style, styleDisplayMap );
    style.addStyleListener( this );

    fireStatusChanged( this );
  }

  public void removeStyle( final IKalypsoUserStyle style )
  {
    style.removeStyleListener( this );
    m_styleMap.remove( style );
  }

  public UserStyle[] getStyles( )
  {
    final Set<IKalypsoUserStyle> set = m_styleMap.keySet();
    return set.toArray( new UserStyle[set.size()] );
  }

  /**
   * @see org.kalypsodeegree.model.feature.event.ModellEventListener#onModellChange(org.kalypsodeegree.model.feature.event.ModellEvent)
   */
  public void onModellChange( final ModellEvent modellEvent )
  {
    if( m_featureList == null )
      return;

    if( modellEvent instanceof IGMLWorkspaceModellEvent )
    {
      // my workspace ?
      final GMLWorkspace changedWorkspace = ((IGMLWorkspaceModellEvent) modellEvent).getGMLWorkspace();
      if( ((m_workspace != null) && (changedWorkspace != m_workspace) && (changedWorkspace != m_workspace.getWorkspace())) )
        return; // not my workspace

      if( modellEvent instanceof FeaturesChangedModellEvent )
      {
        final FeaturesChangedModellEvent featuresChangedModellEvent = ((FeaturesChangedModellEvent) modellEvent);
        final Feature[] features = featuresChangedModellEvent.getFeatures();

        // HACK: for sinlge-feature lists (see flag), we must invalidate the list ourselfs.
        if( m_isSingleFeature )
        {
          // TODO: we do not know which one of the changed features is the right one... (se FIXME below)
          // So we just invalidate all features i nthis list
          for( final Feature feature : features )
            m_featureList.invalidate( feature );
        }

        if( features.length > 100 )
        {
          // OPTIMIZATION: as List#contains is quite slow, we generally repaint if the number of changed features
          // is too large.
          setDirty();
        }
        else
        {
          GM_Envelope invalidBox = null;
          for( final Feature feature : features )
          {
            if( m_featureList.contains( feature ) || m_featureList.contains( feature.getId() ) )
            {
              final GM_Envelope envelope = feature.getEnvelope();
              if( invalidBox == null )
                invalidBox = envelope;
              else
                invalidBox = invalidBox.getMerged( envelope );
            }
          }
          if( invalidBox != null )
          {
            // TODO: buffer: does not work well for points, or fat-lines
            fireRepaintRequested( invalidBox );
          }
        }
      }
      else if( modellEvent instanceof FeatureStructureChangeModellEvent )
      {
        final FeatureStructureChangeModellEvent fscme = (FeatureStructureChangeModellEvent) modellEvent;
        final Feature[] parents = fscme.getParentFeatures();
        for( final Feature parent : parents )
        {
          if( m_featureList.getParentFeature() == parent )
          {
            switch( fscme.getChangeType() )
            {
              case FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_ADD:
                // fall through
              case FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_DELETE:
                // fall through
              case FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_MOVE:
                setDirty();
                break;
              default:
                setDirty();
            }
          }
        }
      }
    }
    else
    {
      // unknown event, set dirty
      // TODO : if the event-hierarchy is implemented correctly the else-part can be removed
      setDirty();
    }
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoTheme#getBoundingBox()
   */
  public GM_Envelope getFullExtent( )
  {
    return m_featureList == null ? null : m_featureList.getBoundingBox();
  }

  public FeatureList getFeatureList( )
  {
    return m_featureList;
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoFeatureTheme#getFeatureListVisible(org.kalypsodeegree.model.geometry.GM_Envelope)
   */
  public FeatureList getFeatureListVisible( final GM_Envelope searchEnvelope )
  {
    if( m_featureList == null )
      return null;

    /* Use complete bounding box if search envelope is not set. */
    final GM_Envelope env = searchEnvelope == null ? getFullExtent() : searchEnvelope;

    final GMLWorkspace workspace = getWorkspace();

    // Put features in set in order to avoid duplicates
    final Set<Feature> features = new HashSet<Feature>();
    final IPaintDelegate paintDelegate = new IPaintDelegate()
    {
      public void paint( final DisplayElement displayElement, final IProgressMonitor paintMonitor )
      {
        final Feature feature = displayElement.getFeature();
        final GM_Envelope envelope = feature.getEnvelope();
        if( envelope != null && env.intersects( envelope ) )
        {
          features.add( feature );
        }
      }
    };

    final IProgressMonitor monitor = new NullProgressMonitor();

    for( final Entry<IKalypsoUserStyle, UserStylePainter> entry : m_styleMap.entrySet() )
    {
      final IKalypsoUserStyle style = entry.getKey();
      if( style.isUsedForSelection() )
      {
        continue;
      }

      final UserStylePainter stylePainter = entry.getValue();

      final Boolean selected = null; // selection is not considered here
      final Double scale = null; // TODO: scale is not considered here, but it should
      try
      {
        stylePainter.paintFeatureTypeStyles( workspace, scale, env, m_featureList, selected, monitor, paintDelegate );
      }
      catch( final CoreException e )
      {
        KalypsoCorePlugin.getDefault().getLog().log( e.getStatus() );
      }
    }

    final FeatureList resultList = FeatureFactory.createFeatureList( m_featureList.getParentFeature(), m_featureList.getParentFeatureTypeProperty() );
    resultList.addAll( features );
    return resultList;
  }

  /**
   * @see org.kalypso.commons.command.ICommandTarget#postCommand(org.kalypso.commons.command.ICommand,
   *      java.lang.Runnable)
   */
  public void postCommand( final ICommand command, final Runnable runnable )
  {
    try
    {
      m_workspace.postCommand( command );
    }
    catch( final Exception e )
    {
      // TODO: error handling
      e.printStackTrace();
    }
    if( runnable != null )
    {
      runnable.run();
    }
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoFeatureTheme#getSchedulingRule()
   */
  public ISchedulingRule getSchedulingRule( )
  {
    return null;
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoFeatureTheme#getSelectionManager()
   */
  public IFeatureSelectionManager getSelectionManager( )
  {
    return m_selectionManager;
  }

  /**
   * @see org.kalypso.ogc.gml.AbstractKalypsoTheme#getDefaultIcon()
   */
  @Override
  protected ImageDescriptor getDefaultIcon( )
  {
    if( m_featureThemeIcon == null )
    {
      m_featureThemeIcon = new Image( Display.getCurrent(), getClass().getResourceAsStream( "resources/featureTheme.gif" ) ); //$NON-NLS-1$
    }

    return ImageDescriptor.createFromImage( m_featureThemeIcon );
  }

  /**
   * @see org.kalypso.ogc.gml.AbstractKalypsoTheme#getChildren(java.lang.Object)
   */
  @Override
  public Object[] getChildren( final Object o )
  {
    if( o != this )
      throw new IllegalStateException();

    final UserStyle[] styles = getStyles();
    if( styles == null )
      return super.getChildren( o );

    final List<UserStyleTreeObject> treeObjects = new ArrayList<UserStyleTreeObject>( styles.length );
    for( final UserStyle style : styles )
    {
      final IKalypsoUserStyle kus = (IKalypsoUserStyle) style;
      // We do not show selection-styles
      // TODO: optionally...
      if( !kus.isUsedForSelection() )
        treeObjects.add( new UserStyleTreeObject( this, kus ) );
    }

    return treeObjects.toArray( new UserStyleTreeObject[treeObjects.size()] );
  }

  /**
   * @see org.kalypso.ogc.gml.IKalypsoUserStyleListener#styleChanged(org.kalypso.ogc.gml.IKalypsoUserStyle)
   */
  public void styleChanged( final IKalypsoUserStyle source )
  {
    setDirty();
    fireStatusChanged( this );
  }

  /**
   * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
   */
  @SuppressWarnings("unchecked")
  @Override
  public Object getAdapter( final Class adapter )
  {
    if( adapter == IKalypsoThemeInfo.class )
      return createThemeInfo();

    return super.getAdapter( adapter );
  }

  private IKalypsoThemeInfo createThemeInfo( )
  {
    final IFeatureType featureType = getFeatureType();
    if( featureType == null )
      return null; // no data available; maybe show some status-message?

    /* If an explicit info is configured for this map, use it */
    // REMARK: is necessary to copy this from AbstractFeatureTheme, as this adapter must be called first
    final String themeInfoId = getProperty( IKalypsoTheme.PROPERTY_THEME_INFO_ID, null );
    if( themeInfoId != null )
      return KalypsoCoreExtensions.createThemeInfo( themeInfoId, this );

    // HACK: use featureThemeInfo from KalypsoUI as a default. This is needed, because this feature info the
    // featureType-properties mechanisms from KalypsoUI in order find a registered featureThemeInfo for the current
    // qname
    final IKalypsoThemeInfo defaultFeatureThemeInfo = KalypsoCoreExtensions.createThemeInfo( "org.kalypso.ui.featureThemeInfo.default", this ); //$NON-NLS-1$
    if( defaultFeatureThemeInfo != null )
      return defaultFeatureThemeInfo;

    return new FeatureThemeInfo( this, new Properties() );
  }
}