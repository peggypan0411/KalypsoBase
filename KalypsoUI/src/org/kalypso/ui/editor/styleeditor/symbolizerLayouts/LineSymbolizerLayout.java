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
package org.kalypso.ui.editor.styleeditor.symbolizerLayouts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.kalypso.ogc.gml.IKalypsoStyle;
import org.kalypso.ui.editor.styleeditor.MessageBundle;
import org.kalypso.ui.editor.styleeditor.panels.ColorChooserPanel;
import org.kalypso.ui.editor.styleeditor.panels.ComboPanel;
import org.kalypso.ui.editor.styleeditor.panels.PanelEvent;
import org.kalypso.ui.editor.styleeditor.panels.PanelListener;
import org.kalypso.ui.editor.styleeditor.panels.SliderPanel;
import org.kalypso.ui.editor.styleeditor.panels.StrokeDasharrayPanel;
import org.kalypso.ui.editor.styleeditor.panels.StrokeDashoffsetPanel;
import org.kalypso.ui.editor.styleeditor.panels.StrokeLinecapComboPanel;
import org.kalypso.ui.editor.styleeditor.panels.StrokeLinejoinComboPanel;
import org.kalypsodeegree.filterencoding.FilterEvaluationException;
import org.kalypsodeegree.graphics.sld.LineSymbolizer;
import org.kalypsodeegree.graphics.sld.Stroke;
import org.kalypsodeegree.graphics.sld.Symbolizer;

/**
 * @author F.Lindemann
 */

public class LineSymbolizerLayout extends AbstractSymbolizerLayout
{
  public LineSymbolizerLayout( final Composite composite, final Symbolizer symbolizer, final IKalypsoStyle userStyle )
  {
    super( composite, symbolizer, userStyle );
  }

  @Override
  public void draw( ) throws FilterEvaluationException
  {
    final LineSymbolizer lineSymbolizer = (LineSymbolizer) m_symbolizer;
    final Stroke stroke = lineSymbolizer.getStroke();

    final GridLayout compositeLayout = new GridLayout();
    compositeLayout.marginHeight = 2;

    final Group strokeGroup = new Group( m_composite, SWT.NULL );
    final GridData strokeGroupData = new GridData();
    strokeGroupData.widthHint = 210;
    strokeGroupData.heightHint = 244;
    strokeGroup.setLayoutData( strokeGroupData );
    strokeGroup.setLayout( compositeLayout );
    strokeGroup.layout();

    // Stroke ColorChooser
    ColorChooserPanel strokeColorChooserPanel = null;
    strokeColorChooserPanel = new ColorChooserPanel( strokeGroup, MessageBundle.STYLE_EDITOR_COLOR, stroke.getStroke( null ) );
    strokeColorChooserPanel.addColorChooserListener( new PanelListener()
    {
      public void valueChanged( final PanelEvent event )
      {
        final Color color = ((ColorChooserPanel) event.getSource()).getColor();
        stroke.setStroke( new java.awt.Color( color.getRed(), color.getGreen(), color.getBlue() ) );
        m_style.fireStyleChanged();
      }
    } );

    // Stroke Width Slider
    final SliderPanel strokeWidthPanel = new SliderPanel( strokeGroup, MessageBundle.STYLE_EDITOR_WIDTH, 0, 10, 1, SliderPanel.INTEGER, stroke.getWidth( null ) );
    strokeWidthPanel.addPanelListener( new PanelListener()
    {
      public void valueChanged( final PanelEvent event )
      {
        final double width = ((SliderPanel) event.getSource()).getSelection();
        stroke.setWidth( width );
        m_style.fireStyleChanged();
      }
    } );

    // Stroke Opacity Slider
    final SliderPanel strokeOpacityPanel = new SliderPanel( strokeGroup, MessageBundle.STYLE_EDITOR_OPACITY, 0, 1, 1, SliderPanel.DECIMAL, stroke.getOpacity( null ) );
    strokeOpacityPanel.addPanelListener( new PanelListener()
    {
      public void valueChanged( final PanelEvent event )
      {
        final double opacity = ((SliderPanel) event.getSource()).getSelection();
        stroke.setOpacity( opacity );
        m_style.fireStyleChanged();
      }
    } );

    // Stroke Linejoin ComboPanel
    final ComboPanel strokeLinejoinPanel = new StrokeLinejoinComboPanel( strokeGroup, MessageBundle.STYLE_EDITOR_LINEJOIN, stroke.getLineJoin( null ) );
    stroke.setLineJoin( strokeLinejoinPanel.getSelection() );
    strokeLinejoinPanel.addPanelListener( new PanelListener()
    {
      public void valueChanged( final PanelEvent event )
      {
        final int lineJoin = ((StrokeLinejoinComboPanel) event.getSource()).getSelection();
        stroke.setLineJoin( lineJoin );
        m_style.fireStyleChanged();
      }
    } );

    // Stroke Linecap ComboPanel
    final ComboPanel strokeLinecapPanel = new StrokeLinecapComboPanel( strokeGroup, MessageBundle.STYLE_EDITOR_LINECAP, stroke.getLineCap( null ) );
    stroke.setLineCap( strokeLinecapPanel.getSelection() );
    strokeLinecapPanel.addPanelListener( new PanelListener()
    {
      public void valueChanged( final PanelEvent event )
      {
        final int lineCap = ((StrokeLinecapComboPanel) event.getSource()).getSelection();
        stroke.setLineCap( lineCap );
        m_style.fireStyleChanged();
      }
    } );

    // Stroke DashOffset
    final StrokeDashoffsetPanel strokeDashoffsetPanel = new StrokeDashoffsetPanel( strokeGroup, MessageBundle.STYLE_EDITOR_DASHOFFSET, stroke.getDashOffset( null ) );
    strokeDashoffsetPanel.addPanelListener( new PanelListener()
    {
      public void valueChanged( final PanelEvent event )
      {
        final float dashOffset = ((StrokeDashoffsetPanel) event.getSource()).getValue();
        stroke.setDashOffset( dashOffset );
        m_style.fireStyleChanged();
      }
    } );

    // Stroke DashArray
    final StrokeDasharrayPanel strokeDasharrayPanel = new StrokeDasharrayPanel( strokeGroup, MessageBundle.STYLE_EDITOR_DASHARRAY, stroke.getDashArray( null ) );
    strokeDasharrayPanel.addPanelListener( new PanelListener()
    {
      public void valueChanged( final PanelEvent event )
      {
        final float dashArray[] = ((StrokeDasharrayPanel) event.getSource()).getValue();
        stroke.setDashArray( dashArray );
        m_style.fireStyleChanged();
      }
    } );
  }
}