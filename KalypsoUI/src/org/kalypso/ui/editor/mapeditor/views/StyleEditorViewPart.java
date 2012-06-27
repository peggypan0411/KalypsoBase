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
package org.kalypso.ui.editor.mapeditor.views;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.kalypso.contribs.eclipse.core.runtime.AdapterUtils;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.IKalypsoStyle;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.IKalypsoUserStyle;
import org.kalypso.ogc.gml.outline.nodes.FeatureTypeStyleNode;
import org.kalypso.ogc.gml.outline.nodes.IThemeNode;
import org.kalypso.ogc.gml.outline.nodes.ThemeNodeUtils;
import org.kalypso.ogc.gml.outline.nodes.UserStyleNode;
import org.kalypso.ui.editor.styleeditor.SLDComposite;
import org.kalypso.ui.editor.styleeditor.StyleEditorConfig;
import org.kalypso.ui.editor.styleeditor.style.FeatureTypeStyleInput;
import org.kalypsodeegree.graphics.sld.FeatureTypeStyle;
import org.kalypsodeegree.graphics.sld.Rule;
import org.kalypsodeegree.graphics.sld.UserStyle;

public class StyleEditorViewPart extends ViewPart implements ISelectionChangedListener
{
  public static String ID = "org.kalypso.ui.editor.mapeditor.views.styleeditor"; //$NON-NLS-1$

  private ISelectionProvider m_gmop = null;

  private SLDComposite m_sldComposite = null;

  public void setSelectionChangedProvider( final ISelectionProvider selectionProvider )
  {
    if( m_gmop == null && selectionProvider == null )
    {
      /* REMARK: special case, happens, if map is closed */
      m_sldComposite.setInput( null );
      return;
    }

    if( m_gmop == selectionProvider )
      return;

    if( m_gmop != null )
    {
      m_gmop.removeSelectionChangedListener( this );
      selectionChanged( new SelectionChangedEvent( m_gmop, StructuredSelection.EMPTY ) );
    }

    m_gmop = selectionProvider;

    if( m_gmop != null )
    {
      m_gmop.addSelectionChangedListener( this );

      selectionChanged( new SelectionChangedEvent( m_gmop, m_gmop.getSelection() ) );
    }
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#dispose()
   */
  @Override
  public void dispose( )
  {
    super.dispose();

    if( m_gmop != null )
      m_gmop.removeSelectionChangedListener( this );

    if( m_sldComposite != null )
      m_sldComposite.dispose();
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl( final Composite parent )
  {
    m_sldComposite = new SLDComposite( parent );
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  @Override
  public void setFocus( )
  {
    if( m_sldComposite != null )
      m_sldComposite.setFocus();
  }

  @Override
  public void selectionChanged( final SelectionChangedEvent event )
  {
    final Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();

    final FeatureTypeStyleInput input = createInput( o );

    m_sldComposite.setInput( input );
  }

  private FeatureTypeStyleInput createInput( final Object o )
  {
    if( !(o instanceof IThemeNode) )
      return null;

    final IThemeNode node = (IThemeNode) o;
    final IKalypsoTheme theme = ThemeNodeUtils.findTheme( node );
    if( !(theme instanceof IKalypsoFeatureTheme) )
      return null;

    final IKalypsoFeatureTheme featureTheme = (IKalypsoFeatureTheme) theme;
    return createInputForTheme( featureTheme, node );
  }

  private FeatureTypeStyleInput createInputForTheme( final IKalypsoFeatureTheme featureTheme, final IThemeNode node )
  {
    final IFeatureType featureType = featureTheme == null ? null : featureTheme.getFeatureType();
    final IKalypsoTheme theme = AdapterUtils.getAdapter( node, IKalypsoTheme.class );

    if( node instanceof UserStyleNode )
    {
      final IKalypsoUserStyle kalypsoStyle = ((UserStyleNode) node).getStyle();
      return createInput( kalypsoStyle, featureType, -1 );
    }

    if( node.getElement() instanceof FeatureTypeStyle )
    {
      final FeatureTypeStyle fts = (FeatureTypeStyle) node.getElement();
      if( fts instanceof IKalypsoStyle )
        return createInput( (IKalypsoStyle) fts, featureType, -1 );
      else
      {
        final IThemeNode parentNode = node.getParent();
        return createInputForTheme( featureTheme, parentNode );
      }
    }

    if( node.getElement() instanceof Rule )
    {
      final Rule indexRule = (Rule) node.getElement();
      final FeatureTypeStyleNode ftsNode = (FeatureTypeStyleNode) node.getParent();
      final FeatureTypeStyle fts = ftsNode.getStyle();
      final Rule[] rules = fts.getRules();
      int index = -1;
      if( indexRule != null )
      {
        for( int i = 0; i < rules.length; i++ )
        {
          if( rules[i] == indexRule )
          {
            index = i;
            break;
          }
        }
      }

      final IKalypsoStyle style = findStyle( ftsNode );
      if( style == null )
        return null;
      else
        return createInput( style, featureType, index );
    }

    if( theme instanceof IKalypsoFeatureTheme )
    {
      final IKalypsoFeatureTheme nodeTheme = (IKalypsoFeatureTheme) theme;
      // Reset style-editor, but the styles are not unique, so do not set anything
      final IFeatureType otherType = nodeTheme == null ? null : nodeTheme.getFeatureType();
      final IKalypsoStyle[] styles = nodeTheme.getStyles();
      if( ArrayUtils.isEmpty( styles ) )
        return null;

      return createInput( styles[0], otherType, -1 );
    }

    return null;
  }

  private IKalypsoStyle findStyle( final IThemeNode node )
  {
    if( node == null )
      return null;

    final Object element = node.getElement();
    if( element instanceof IKalypsoStyle )
      return (IKalypsoStyle) element;

    return findStyle( node.getParent() );
  }

  private static FeatureTypeStyleInput createInput( final IKalypsoStyle style, final IFeatureType featureType, final int styleToSelect )
  {
    if( style == null )
      return null;

    final StyleEditorConfig configuration = createConfiguration( style );

    final FeatureTypeStyle fts = findFeatureTypeStyle( style, styleToSelect );
    return new FeatureTypeStyleInput( fts, style, styleToSelect, featureType, configuration );
  }

  private static FeatureTypeStyle findFeatureTypeStyle( final IKalypsoStyle style, final int styleToSelect )
  {
    if( style instanceof FeatureTypeStyle )
      return (FeatureTypeStyle) style;

    if( style instanceof UserStyle )
    {
      final FeatureTypeStyle[] styles = ((UserStyle) style).getFeatureTypeStyles();
      if( styles.length == 0 )
        return null;

      if( styleToSelect == -1 )
        return styles[0];
      else
        return styles[styleToSelect];
    }

    return null;
  }

  public static StyleEditorConfig createConfiguration( final IKalypsoStyle style )
  {
    final StyleEditorConfig config = new StyleEditorConfig();

    if( style.isCatalogStyle() )
    {
      config.setFeatureTypeStyleCompositeShowProperties( false );

      // TODO: we need this information from outside; for most catalog styles, editing the geometry or adding/removing
      // rules/symbolizers does not make sense
      // TODO: hide 'name' field of rule if strucutre change is not allowed
      config.setSymbolizerEditGeometry( false );
      config.setRuleTabViewerAllowChange( false );
      config.setRuleEditName( false );
      config.setSymbolizerTabViewerAllowChange( false );
    }

    return config;
  }

}