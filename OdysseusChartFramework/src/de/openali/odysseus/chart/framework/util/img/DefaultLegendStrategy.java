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

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;

import de.openali.odysseus.chart.framework.model.layer.IChartLayer;
import de.openali.odysseus.chart.framework.model.layer.ILegendEntry;
import de.openali.odysseus.chart.framework.model.style.ITextStyle;

/**
 * @author Dirk Kuch
 */
public class DefaultLegendStrategy implements ILegendPaintStrategy
{

  private Point m_size;

  /**
   * @see de.openali.odysseus.chart.framework.util.img.ILegendStrategy#getSize(de.openali.odysseus.chart.framework.util.img.LegendImageCreator)
   */
  @Override
  public Point getSize( final ChartLegendPainter creator )
  {
    if( m_size != null )
      return m_size;

    final IChartLayer[] layers = creator.getLayers();

    int heigth = 0;
    int row = 0;

    int maxRowSize = 0;

    for( final IChartLayer layer : layers )
    {
      final ILegendEntry entry = getLegendEntry( layer );
      if( entry == null )
        continue;

      final Point size = getItemSize( creator, entry );

      if( row + size.x > creator.getMaximumWidth() )
      {
        maxRowSize = Math.max( maxRowSize, row );
        row = 0;
        heigth += size.y;
      }
      else
      {
        row += size.x;
        maxRowSize = Math.max( maxRowSize, row );
        if( heigth == 0 )
          heigth = size.y;
      }
    }

    m_size = new Point( maxRowSize, heigth );

    return m_size;
  }

  private Point getItemSize( final ChartLegendPainter creator, final ILegendEntry entry )
  {
    final Device dev = PlatformUI.getWorkbench().getDisplay();
    final Image image = new Image( dev, 1, 1 );
    final GC gc = new GC( image );

    final ITextStyle style = creator.getTextStyle();
    style.apply( gc );

    try
    {
      final Point iconSize = entry.computeSize( creator.getIconSize() );
      final Point spacer = creator.getSpacer();
      final Point titleSize = gc.textExtent( entry.getDescription() == null ? "" : entry.getDescription(), SWT.DRAW_DELIMITER | SWT.DRAW_TAB );

      // TODO subtract spacer2 from last line element?
      final Point itemSpacer = creator.getItemSpacer();

      final Point size = calculateSize( iconSize, spacer, titleSize, itemSpacer );

      return size;
    }
    finally
    {
      gc.dispose();
    }
  }

  private ILegendEntry getLegendEntry( final IChartLayer layer )
  {
    final ILegendEntry[] legendEntries = layer.getLegendEntries();

    // TODO - instead of returning null (layer.getLegendEntries()) always return an empty array!
    if( ArrayUtils.isEmpty( legendEntries ) )
      return null;

    for( final ILegendEntry entry : legendEntries )
    {
      return entry;
    }

    return null;
  }

  /**
   * @see de.openali.odysseus.chart.framework.util.img.ILegendStrategy#createImage(de.openali.odysseus.chart.framework.util.img.LegendImageCreator)
   */
  @Override
  public Image createImage( final ChartLegendPainter creator )
  {
    final IChartLayer[] layers = creator.getLayers();
    final Point size = getSize( creator );
    if( size.x <= 0 || size.y <= 0 )
      return null;

    final Device dev = PlatformUI.getWorkbench().getDisplay();
    final Image canvas = new Image( dev, size.x, size.y );
    final GC gc = new GC( canvas );

    final ITextStyle style = creator.getTextStyle();
    style.apply( gc );

    try
    {
      int x = 0;
      int y = 0;

      for( final IChartLayer layer : layers )
      {
        final ILegendEntry entry = getLegendEntry( layer );
        if( entry == null )
          continue;

        final ImageData imageData = createLegendItem( creator, entry );
        final Image image = new Image( dev, imageData );
        gc.drawImage( image, x, y );

        if( x + imageData.width > creator.getMaximumWidth() )
        {
          x = 0;
          y += imageData.height;
        }
        else
        {
          x += imageData.width;
        }

        image.dispose();
      }

      return canvas;
    }
    finally
    {
      gc.dispose();
    }

  }

  private ImageData createLegendItem( final ChartLegendPainter creator, final ILegendEntry entry )
  {
    final Point size = getItemSize( creator, entry );

    final Device dev = PlatformUI.getWorkbench().getDisplay();
    final Image canvas = new Image( dev, size.x, size.y );
    final GC gc = new GC( canvas );

    try
    {
      final Point iconSize = entry.computeSize( creator.getIconSize() );
      final ImageData iconImageData = entry.getSymbol( iconSize );
      final Image iconImage = new Image( dev, iconImageData );
      gc.drawImage( iconImage, 0, 0 );

      final ITextStyle style = creator.getTextStyle();
      style.apply( gc );

      final Point anchor = getTextAnchor( creator, iconSize );
      final String description = entry.getDescription();
      gc.drawText( description == null ? "" : description, anchor.x, anchor.y );

      return canvas.getImageData();
    }
    finally
    {
      canvas.dispose();
      gc.dispose();
    }
  }

  private Point getTextAnchor( final ChartLegendPainter creator, final Point iconSize )
  {
    final ITextStyle style = creator.getTextStyle();
    final Point spacer = creator.getSpacer();

    final int x = iconSize.x + spacer.x;
    int y = 0;

    // text is smaller than icon height?
    final int space = iconSize.y - style.getHeight();
    if( space > 0 )
      y = Double.valueOf( space / 2.0 ).intValue();

    return new Point( x, y );
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

}
