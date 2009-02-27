/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.swtchart.chart.legend;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.kalypso.contribs.eclipse.swt.graphics.GCWrapper;
import org.kalypso.swtchart.chart.layer.IChartLayer;
import org.kalypso.swtchart.logging.Logger;

/**
 * @author schlienger
 * @author burtscher
 * 
 * widget displaying a legend describing a charts contents; also a container for ILegendItems 
 * 
 */
public class Legend extends Canvas implements PaintListener
{
  private String m_title;

  private ArrayList<ILegendItem> m_items;

  private int m_iconWidth = 20;

  private int m_iconHeight = 20;

  private int m_inset = 5;

  private Image m_bufferImg;

  public Legend( Composite parent, int style )
  {
    super( parent, style );
    addPaintListener( this );
    m_items = new ArrayList<ILegendItem>();
    setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );
  }

  /**
   * sets the legends title
   * TODO: the title is not rendered into the legend image 
   * - this should be done as there's no need for a title otherwise
   */
  public void setTitle( final String title )
  {
    m_title = title;
  }

  /**
   * creates an ILegendItem for the given ChartLayer and adds it to the list of ILegendItems 
   */
  public void addLayer( IChartLayer l )
  {
    DefaultLegendItem li = new DefaultLegendItem( l, m_iconWidth, m_iconHeight, m_inset );
    addLegendItem( li );
  }

  /**
   * creates ILegendItems for the given ChartLayers and adds them to the list of ILegendItems 
   */
  public void addLayers( IChartLayer[] layers )
  {
    for( IChartLayer l : layers )
    {
      addLayer( l );
    }
  }

  /**
   * creates ILegendItems for the given ChartLayers and adds them to the list of ILegendItems 
   */
  public void addLayers( List<IChartLayer> layers )
  {
    for( IChartLayer l : layers )
    {
      if (l.getVisibility())
        addLayer( l );
    }
  }

  /**
   * adds an ILegendItem to the list of ILengendItems
   */
  public void addLegendItem( ILegendItem l )
  {
    m_items.add( l );
  }

  /**
   * paints the legend into the PaintEvents GC; uses DoubleBuffering
   */
  public void paintControl( PaintEvent e )
  {
    GCWrapper gcw = new GCWrapper( e.gc );
    final Rectangle screenArea = getClientArea();
    m_bufferImg = paintLegend( e.display, gcw, screenArea, m_bufferImg );
    gcw.dispose();

  }

  /**
   * paints the legend into a buffer image and uses the given Rectangle to define the size; if the given image is null, a new image will be created
   * and returned
   * 
   * TODO: rename to paintBufferedLegend or similar to separate from drawImage
   */
  public Image paintLegend( Device dev, GCWrapper gcw, Rectangle screen, Image bufferImage )
  {
    final Image usedBufferImage;
    if( bufferImage == null )
    {
      usedBufferImage = new Image( dev, screen.width, screen.height );

      drawImage( usedBufferImage, dev );
    }
    else
      usedBufferImage = bufferImage;

    gcw.drawImage( usedBufferImage, 0, 0 );
    return usedBufferImage;
  }

  /**
   * paints the legend into an Image
   */
  public void drawImage( Image img, Device dev )
  {
    GCWrapper buffGc = new GCWrapper( new GC( img ) );
    buffGc.setBackground( dev.getSystemColor( SWT.COLOR_WHITE ) );
    buffGc.setForeground( dev.getSystemColor( SWT.COLOR_BLACK ) );
    try
    {
      // Abstand zum oberen Rand der Komponente
      int top = 0;

      for( ILegendItem li : m_items )
      {
        Point size = li.computeSize( 0, 0 );
        Image liImg = new Image( dev, size.x, size.y );
        li.paintImage( liImg );
        buffGc.drawImage( liImg, 0, top );
        liImg.dispose();
        top += size.y;
      }

    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
    finally
    {
      buffGc.dispose();
    }
  }

  @Override
  public Point computeSize( int whint, int hhint )
  {
    int width = 0;
    int height = 0;
    for( ILegendItem li : m_items )
    {
      Point lisize = li.computeSize( 0, 0 );
      Logger.trace(li.getName()+" hat Gr��e "+lisize);
      if( lisize.x > width )
        width = lisize.x;
      height += lisize.y;
      Logger.trace("Current Legend Size width: "+width+ " height: "+height);
    }
    int tolerance = 5;
    Point size=new Point( width, height + tolerance );
    Logger.trace("Legend Size: "+size);
    return size;
  }

}
