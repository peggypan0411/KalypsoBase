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
/*
 * Created on 26.07.2004
 *
 */
package org.kalypso.ui.editor.styleeditor.symbolizerLayouts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.kalypso.ogc.gml.IKalypsoUserStyle;
import org.kalypso.ui.editor.styleeditor.MessageBundle;
import org.kalypso.ui.editor.styleeditor.panels.ColorPalettePanel;
import org.kalypso.ui.editor.styleeditor.panels.ComboPanel;
import org.kalypso.ui.editor.styleeditor.panels.PanelEvent;
import org.kalypso.ui.editor.styleeditor.panels.PanelListener;
import org.kalypso.ui.editor.styleeditor.panels.SliderPanel;
import org.kalypso.ui.editor.styleeditor.panels.WellKnownNameComboPanel;
import org.kalypso.ui.editor.styleeditor.rulePattern.RuleCollection;
import org.kalypsodeegree.filterencoding.FilterEvaluationException;
import org.kalypsodeegree.graphics.sld.Graphic;
import org.kalypsodeegree.graphics.sld.Mark;
import org.kalypsodeegree.graphics.sld.PointSymbolizer;
import org.kalypsodeegree.graphics.sld.Symbolizer;
import org.kalypsodeegree_impl.graphics.sld.StyleFactory;

/**
 * @author F.Lindemann
 */

public class FilterPatternPointSymbolizerLayout extends AbstractSymbolizerLayout
{

  private int selectionIndex = 0;

  private RuleCollection ruleCollection = null;

  private int symbolizerIndex = -1;

  ColorPalettePanel colorPalettePanel = null;

  public FilterPatternPointSymbolizerLayout( final Composite m_composite, final Symbolizer m_symbolizer, final IKalypsoUserStyle m_userStyle, final RuleCollection m_ruleCollection, final int m_symbolizerIndex )
  {
    super( m_composite, m_symbolizer, m_userStyle );
    this.ruleCollection = m_ruleCollection;
    this.symbolizerIndex = m_symbolizerIndex;
  }

  @Override
  public void draw( ) throws FilterEvaluationException
  {
    final GridLayout compositeLayout = new GridLayout();
    compositeLayout.marginHeight = 2;
    // ***** group
    final Group group = new Group( m_composite, SWT.NULL );
    final GridData groupData = new GridData();
    groupData.widthHint = 210;
    groupData.heightHint = 215;
    group.setLayoutData( groupData );
    group.setLayout( compositeLayout );
    group.layout();

    final PointSymbolizer pointSymbolizer = (PointSymbolizer) m_symbolizer;
    final Graphic graphic = pointSymbolizer.getGraphic();

    final Object objects[] = graphic.getMarksAndExtGraphics();
    final Mark mark = (Mark) objects[0];

    final ComboPanel wellKnownNameComboBox = new WellKnownNameComboPanel( group, MessageBundle.STYLE_EDITOR_TYPE, mark.getWellKnownName() );
    for( int i = 0; i < getRuleCollection().size(); i++ )
    {
      final Symbolizer symb = getRuleCollection().get( i ).getSymbolizers()[getSymbolizerIndex()];
      if( symb instanceof PointSymbolizer )
      {
        final Object[] obj = ((PointSymbolizer) symb).getGraphic().getMarksAndExtGraphics();
        if( obj.length > 0 && obj[0] instanceof Mark )
        {
          ((Mark) obj[0]).setWellKnownName( mark.getWellKnownName() );
        }
      }
    }
    wellKnownNameComboBox.addPanelListener( new PanelListener()
    {
      public void valueChanged( final PanelEvent event )
      {
        final int index = ((ComboPanel) event.getSource()).getSelection();
        final String wkn = WellKnownNameComboPanel.getWellKnownNameByIndex( index );
        for( int i = 0; i < getRuleCollection().size(); i++ )
        {
          final Symbolizer symb = getRuleCollection().get( i ).getSymbolizers()[getSymbolizerIndex()];
          if( symb instanceof PointSymbolizer )
          {
            final Object[] obj = ((PointSymbolizer) symb).getGraphic().getMarksAndExtGraphics();
            if( obj.length > 0 && obj[0] instanceof Mark )
            {
              ((Mark) obj[0]).setWellKnownName( wkn );
            }
          }
        }
        m_userStyle.fireStyleChanged();
      }
    } );

    final SliderPanel graphicSizePanel = new SliderPanel( group, MessageBundle.STYLE_EDITOR_SIZE, 1, 15, 1, SliderPanel.INTEGER, graphic.getSize( null ) );
    for( int i = 0; i < getRuleCollection().size(); i++ )
    {
      final Symbolizer symb = getRuleCollection().get( i ).getSymbolizers()[getSymbolizerIndex()];
      if( symb instanceof PointSymbolizer )
      {
        ((PointSymbolizer) symb).getGraphic().setSize( graphic.getSize( null ) );
      }
    }
    graphicSizePanel.addPanelListener( new PanelListener()
    {
      public void valueChanged( final PanelEvent event )
      {
        final double size = ((SliderPanel) event.getSource()).getSelection();
        for( int i = 0; i < getRuleCollection().size(); i++ )
        {
          final Symbolizer symb = getRuleCollection().get( i ).getSymbolizers()[getSymbolizerIndex()];
          if( symb instanceof PointSymbolizer )
          {
            ((PointSymbolizer) symb).getGraphic().setSize( size );
          }
        }
        m_userStyle.fireStyleChanged();
      }
    } );

    // get all colors for each rule of the pattern for this specific symbolizer
    final Color[] colors = new Color[getRuleCollection().size()];
    for( int i = 0; i < getRuleCollection().size(); i++ )
    {
      final Symbolizer symb = getRuleCollection().get( i ).getSymbolizers()[getSymbolizerIndex()];
      if( symb instanceof PointSymbolizer )
      {
        final Object[] obj = ((PointSymbolizer) symb).getGraphic().getMarksAndExtGraphics();
        if( obj.length > 0 && obj[0] instanceof Mark )
        {
          final java.awt.Color color = ((Mark) obj[0]).getFill().getFill( null );
          colors[i] = new Color( null, color.getRed(), color.getGreen(), color.getBlue() );
        }
      }
    }

    if( colorPalettePanel == null )
    {
      colorPalettePanel = new ColorPalettePanel( group, colors, getRuleCollection() );
      colorPalettePanel.setType( ColorPalettePanel.CUSTOM_TRANSITION );
      // init colors of PointSymbolizer
      for( int i = 0; i < getRuleCollection().size(); i++ )
      {
        final Symbolizer symb = getRuleCollection().get( i ).getSymbolizers()[getSymbolizerIndex()];
        if( symb instanceof PointSymbolizer )
        {
          final Object[] obj = ((PointSymbolizer) symb).getGraphic().getMarksAndExtGraphics();
          if( obj.length > 0 && obj[0] instanceof Mark )
          {
            ((Mark) obj[0]).getFill().setFill( new java.awt.Color( colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue() ) );
          }
        }
      }

      colorPalettePanel.addColorPalettePanelListener( new PanelListener()
      {
        public void valueChanged( final PanelEvent event )
        {
          final Color[] colorArray = colorPalettePanel.getColorPalette();

          for( int i = 0; i < getRuleCollection().size(); i++ )
          {
            final Symbolizer symb = getRuleCollection().get( i ).getSymbolizers()[getSymbolizerIndex()];
            if( symb instanceof PointSymbolizer )
            {
              ((Mark) ((PointSymbolizer) symb).getGraphic().getMarksAndExtGraphics()[0]).setFill( StyleFactory.createFill( new java.awt.Color( colorArray[i].getRed(), colorArray[i].getGreen(), colorArray[i].getBlue() ) ) );
            }
          }
          m_userStyle.fireStyleChanged();
        }
      } );
    }
    else
      colorPalettePanel.draw( m_composite );
  }

  public int getSelectionIndex( )
  {
    return selectionIndex;
  }

  public void setSelectionIndex( final int m_selectionIndex )
  {
    this.selectionIndex = m_selectionIndex;
  }

  public int getSymbolizerIndex( )
  {
    return symbolizerIndex;
  }

  public void setSymbolizerIndex( final int m_symbolizerIndex )
  {
    this.symbolizerIndex = m_symbolizerIndex;
  }

  public RuleCollection getRuleCollection( )
  {
    return ruleCollection;
  }

  public void setRuleCollection( final RuleCollection m_ruleCollection )
  {
    this.ruleCollection = m_ruleCollection;
  }
}