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
package de.openali.odysseus.chart.framework.util.img;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;

import de.openali.odysseus.chart.framework.model.IChartModel;
import de.openali.odysseus.chart.framework.model.layer.IChartLayer;
import de.openali.odysseus.chart.framework.model.style.ITextStyle;
import de.openali.odysseus.chart.framework.util.StyleUtils;

/**
 * @author Dirk Kuch
 */
public class LegendImageCreator
{
  private final IChartModel m_model;

  private ITextStyle m_style = StyleUtils.getDefaultTextStyle();

  private final int m_maxImageWidth;

  private Point m_iconSize = new Point( 15, 15 );

  private final Point m_itemSpacer = new Point( 15, 15 );

  private final ILegendStrategy m_strategy;

  public LegendImageCreator( final IChartModel model, final int maximalImageWidth )
  {
    this( model, maximalImageWidth, new DefaultLegendStrategy() );
  }

  public LegendImageCreator( final IChartModel model, final int maximalImageWidth, final ILegendStrategy strategy )
  {
    m_model = model;
    m_maxImageWidth = maximalImageWidth;
    m_strategy = strategy;
  }

  public void setTextStyle( final ITextStyle style )
  {
    m_style = style;
  }

  public void setIconSize( final Point size )
  {
    m_iconSize = size;
  }

  public void setItemSpacer( final Point size )
  {
    m_iconSize = size;
  }

  public Point getSize( )
  {
    return m_strategy.getSize( this );
  }

  public Image createImage( )
  {
    final IChartLayer[] layers = getLayers();
    final Point size = getSize();

    final Device dev = PlatformUI.getWorkbench().getDisplay();
    final Image image = new Image( dev, size.x, size.y );
    final GC gc = new GC( image );

    return null;
  }

  Point getSpacer( )
  {
    return new Point( 2, 0 );
  }

  Point getTextExtend( final GC gc, final Font font, final String title )
  {
    gc.setFont( font );

    return gc.textExtent( m_model.getTitle(), SWT.DRAW_DELIMITER | SWT.DRAW_TAB );
  }

  IChartLayer[] getLayers( )
  {
    final Set<IChartLayer> visible = new LinkedHashSet<IChartLayer>();

    final IChartLayer[] layers = m_model.getLayerManager().getLayers();
    for( final IChartLayer layer : layers )
    {
      if( layer.isLegend() )
        visible.add( layer );
    }

    return visible.toArray( new IChartLayer[] {} );
  }

  Font getFont( final Device dev )
  {
    final FontData fontData = m_style.toFontData();
    if( fontData == null )
      return new Font( dev, dev.getFontList( null, true )[0] );

    return new Font( dev, fontData );
  }

  public int getMaximumWidth( )
  {
    return m_maxImageWidth;
  }

  public Point getIconSize( )
  {
    return m_iconSize;
  }

  public Point getItemSpacer( )
  {
    return m_itemSpacer;
  }

}
