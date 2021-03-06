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
package de.openali.odysseus.chart.framework.util.img.legend.renderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.openali.odysseus.chart.framework.model.IChartModel;
import de.openali.odysseus.chart.framework.model.layer.IChartLayer;
import de.openali.odysseus.chart.framework.model.layer.ILayerManager;
import de.openali.odysseus.chart.framework.model.layer.ILegendEntry;
import de.openali.odysseus.chart.framework.model.style.ITextStyle;
import de.openali.odysseus.chart.framework.util.ChartUtilities;
import de.openali.odysseus.chart.framework.util.img.legend.IChartLegendCanvas;
import de.openali.odysseus.chart.framework.util.img.legend.config.IChartLegendConfig;
import de.openali.odysseus.chart.framework.util.img.legend.utils.LegendChartLayersVisitor;

/**
 * @author Dirk Kuch
 */
public class CompactChartLegendRenderer implements IChartLegendRenderer
{
  private int m_numRows;

  public static final String ID = "de.openali.odysseus.chart.legend.render.compact"; //$NON-NLS-1$

  @Override
  public String getIdentifier( )
  {
    return ID; //$NON-NLS-1$
  }

  private Point calculateSize( final Point... points )
  {
    int x = 0;
    int y = 0;

    for( final Point point : points )
    {
      x += point.x;
      y = Math.max( point.y, y );
    }

    return new Point( x, y );
  }

  @Override
  public Image createImage( final IChartLegendCanvas canvas, final IChartLegendConfig config )
  {
    final IChartLayer[] layers = getLayers( canvas.getModel() );
    final Rectangle rect = getSize( layers, config );
    final int rowHeight = m_numRows < 2 ? rect.height : rect.height / m_numRows;
    if( rect.width <= 0 || rect.height <= 0 )
      return null;

    final Device dev = ChartUtilities.getDisplay();
    final Image img = new Image( dev, rect.width, rect.height );
    final GC gc = new GC( img );

    final ITextStyle style = config.getTextStyle();
    style.apply( gc );

    try
    {
      int x = 0;
      int y = 0;

      for( final IChartLayer layer : layers )
      {
        // final ILegendEntry entry = getLegendEntry( layer );
        if( layer.getLegendEntries() == null )
          continue;
        for( final ILegendEntry entry : layer.getLegendEntries() )
        {
          if( entry == null )
            continue;

          final ImageData imageData = createLegendItem( config, entry, rowHeight );
          if( x + imageData.width > config.getMaximumWidth().width )
          {
            x = 0;
            y += imageData.height;
          }

          final Image image = new Image( dev, imageData );
          gc.drawImage( image, x, y );

          x += imageData.width;

          image.dispose();
        }
      }
      return img;
    }
    finally
    {
      gc.dispose();
    }

  }

  protected IChartLayer[] getLayers( final IChartModel model )
  {
    final ILayerManager layerManager = model.getLayerManager();
    final LegendChartLayersVisitor visitor = new LegendChartLayersVisitor();
    layerManager.accept( visitor );

    return visitor.getLayers();
  }

  private ImageData createLegendItem( final IChartLegendConfig config, final ILegendEntry entry, final int rowHeight )
  {
    final Point size = getItemSize( config, entry );

    final Device dev = ChartUtilities.getDisplay();
    final Image img = new Image( dev, size.x, size.y );
    final GC gc = new GC( img );

    final Point iconSize = entry.computeSize( config.getIconSize() );
    final ImageData iconImageData = entry.getSymbol( iconSize );
    final Image iconImage = new Image( dev, iconImageData );
    try
    {
      gc.drawImage( iconImage, 0, (rowHeight - iconSize.y) / 2 );

      final ITextStyle style = config.getTextStyle();
      style.apply( gc );

      final String description = entry.getDescription();

      final Point textSize;
      if( description == null )
        textSize = new Point( 1, 1 );
      else
        textSize = gc.textExtent( description );

      final Point anchor = getTextAnchor( config, iconSize.x, rowHeight, textSize );

      gc.drawText( description == null ? "" : description, anchor.x, anchor.y, SWT.TRANSPARENT | SWT.WRAP | SWT.DRAW_DELIMITER ); //$NON-NLS-1$

      return img.getImageData();
    }
    finally
    {
      iconImage.dispose();
      img.dispose();
      gc.dispose();
    }
  }

  private Point getItemSize( final IChartLegendConfig config, final ILegendEntry entry )
  {
    final Device dev = ChartUtilities.getDisplay();
    final Image image = new Image( dev, 1, 1 );
    final GC gc = new GC( image );

    final ITextStyle style = config.getTextStyle();
    style.apply( gc );

    try
    {
      final Point iconSize = entry.computeSize( config.getIconSize() );
      final Point spacer = config.getSpacer();
      final Point titleSize = gc.textExtent( entry.getDescription() == null ? "" : entry.getDescription(), SWT.DRAW_DELIMITER | SWT.DRAW_TAB ); //$NON-NLS-1$

      // TODO subtract spacer2 from last line element?
      final Point itemSpacer = config.getItemSpacer();

      final Point size = calculateSize( iconSize, spacer, titleSize, itemSpacer );

      return size;
    }
    finally
    {
      image.dispose();
      gc.dispose();
    }
  }

  /**
   * @see de.openali.odysseus.chart.framework.util.img.ILegendStrategy#getSize(de.openali.odysseus.chart.framework.util.img.LegendImageCreator)
   */
  @Override
  public Rectangle calculateSize( final IChartLegendCanvas canvas, final IChartLegendConfig config )
  {
    return getSize( getLayers( canvas.getModel() ), config );
  }

  private Rectangle getSize( final IChartLayer[] layers, final IChartLegendConfig config )
  {
    int heigth = 0;
    int row = 0;

    int maxRowWidth = 0;
    int maxRowHeight = 0;

    m_numRows = 0;

    for( final IChartLayer layer : layers )
    {
      // final ILegendEntry entry = getLegendEntry( layer );
      if( layer.getLegendEntries() == null )
        continue;
      for( final ILegendEntry entry : layer.getLegendEntries() )
      {
        if( entry == null )
          continue;

        final Point size = getItemSize( config, entry );

        if( row + size.x > config.getMaximumWidth().width )
        {
          maxRowWidth = Math.max( maxRowWidth, size.x );
          maxRowHeight = size.y;

          heigth += size.y;
          row = size.x;

          m_numRows += 1;
        }
        else
        {
          row += size.x;

          if( size.y > maxRowHeight )
            heigth += size.y - maxRowHeight;

          maxRowWidth = Math.max( maxRowWidth, row );
          maxRowHeight = Math.max( maxRowHeight, size.y );

          if( m_numRows == 0 )
            m_numRows = 1;
        }
      }
    }

    return new Rectangle( config.getMaximumWidth().x, config.getMaximumWidth().y, maxRowWidth, heigth );
  }

  private Point getTextAnchor( final IChartLegendConfig config, final int iconWidth, final int rowHeight, final Point textSize )
  {
    final Point spacer = config.getSpacer();

    final int x = iconWidth + spacer.x;
    final int y = (rowHeight - textSize.y) / 2;

    return new Point( x, y );
  }

  /**
   * @see de.openali.odysseus.chart.framework.util.img.legend.renderer.IChartLegendRenderer#rowSize()
   */
  @Override
  public int rowSize( )
  {
    return m_numRows;
  }

}
