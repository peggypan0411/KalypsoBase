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
 * Created on 15.07.2004
 *  
 */
package org.kalypso.ui.editor.styleeditor.panels;

import javax.swing.event.EventListenerList;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.kalypso.contribs.eclipse.swt.layout.Layouts;
import org.kalypso.ui.editor.styleeditor.dialogs.filterpatterndialog.FilterPatternDialog;
import org.kalypso.ui.editor.styleeditor.style.RuleCollection;
import org.kalypsodeegree_impl.filterencoding.ComplexFilter;

/**
 * @author F.Lindemann
 */
public class ColorPalette
{

  private Composite composite = null;

  private final EventListenerList listenerList = new EventListenerList();

  private int colorSize = 0;

  private int borderWidth = 0;

  private final int MAX_WIDTH = 5;

  private Color[] colors = null;

  private ColorBox[] colorBoxes = null;

  private RuleCollection ruleCollection = null;

  public ColorPalette( final Composite parent, final Color[] m_colors, final int m_colorSize, final int m_borderWidth, final RuleCollection m_ruleCollection )
  {
    colors = m_colors;
    setRuleCollection( m_ruleCollection );
    composite = new Composite( parent, SWT.NULL );
    final GridLayout compositeLayout = Layouts.createGridLayout( MAX_WIDTH, true );
    final GridData compositeData = new GridData();
    composite.setLayoutData( compositeData );
    composite.setLayout( compositeLayout );
    compositeLayout.horizontalSpacing = 0;
    compositeLayout.verticalSpacing = 0;
    composite.layout();
    colorSize = m_colorSize;
    borderWidth = m_borderWidth;
    init();
  }

  public void addColorPaletterListener( final PanelListener pl )
  {
    listenerList.add( PanelListener.class, pl );
  }

  int i = 0;

  private void init( )
  {
    colorBoxes = new ColorBox[colors.length];
    for( i = 0; i < colors.length; i++ )
    {
      final ColorBox box = new ColorBox( composite, colors[i], colorSize, borderWidth );
      colorBoxes[i] = box;

      final FilterPatternDialog filterPatternDialog = new FilterPatternDialog( composite.getShell(), ((ComplexFilter)getRuleCollection().get( i ).getFilter()).getOperation() );
      box.addPanelListener( new PanelListener()
      {
        @Override
        public void valueChanged( final PanelEvent event )
        {
          filterPatternDialog.setColor( ((ColorBox)event.getSource()).getColor() );
          filterPatternDialog.open();
          if( filterPatternDialog.getReturnCode() == Window.OK )
          {
            ((ColorBox)event.getSource()).setColor( filterPatternDialog.getColor() );
          }
          for( int j = 0; j < getColorBoxes().length; j++ )
          {
            getColors()[j] = getColorBoxes()[j].getColor();
          }
          fire();
        }
      } );
    }
  }

  private void update( )
  {
    for( int j = 0; j < colorBoxes.length; j++ )
    {
      colorBoxes[j].setColor( colors[j] );
    }
  }

  public void setColors( final Color[] m_colors )
  {
    colors = m_colors;
    update();
  }

  public Color[] getColors( )
  {
    return colors;
  }

  protected void fire( )
  {
    final Object[] listeners = listenerList.getListenerList();
    for( int counter = listeners.length - 2; counter >= 0; counter -= 2 )
    {
      if( listeners[counter] == PanelListener.class )
      {
        final PanelEvent event = new PanelEvent( this );
        ((PanelListener)listeners[counter + 1]).valueChanged( event );
      }
    }
  }

  public ColorBox[] getColorBoxes( )
  {
    return colorBoxes;
  }

  public void setColorBoxes( final ColorBox[] m_colorBoxes )
  {
    colorBoxes = m_colorBoxes;
  }

  public RuleCollection getRuleCollection( )
  {
    return ruleCollection;
  }

  public void setRuleCollection( final RuleCollection m_ruleCollection )
  {
    ruleCollection = m_ruleCollection;
  }
}