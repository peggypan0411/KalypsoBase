package org.kalypso.chart.framework.trash;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.kalypso.chart.framework.logging.Logger;
import org.kalypso.chart.framework.model.layer.IChartLayer;
import org.kalypso.contribs.eclipse.swt.graphics.GCWrapper;

/**
 * @author burtscher
 */
public class DefaultLegendItem implements ILegendItem_old
{
  private final IChartLayer m_layer;

  private int m_inset = 5;

  private final int m_iconHeight;

  private final int m_iconWidth;

  private final FontData m_fd = new FontData( "Arial", 10, SWT.NONE );

  public DefaultLegendItem( IChartLayer l, int iconWidth, int iconHeight, int inset )
  {
    m_inset = inset;
    m_iconHeight = iconHeight;
    m_iconWidth = iconWidth;
    m_layer = l;
  }

  /**
   * calculates the extent of text using certain fontdata TODO: should be extracted into a general helper Method
   */
  private Point getTextExtent( GCWrapper gc, Device dev, String label, FontData fd )
  {
    final Font f = new Font( dev, fd );
    gc.setFont( f );
    final Point point = gc.textExtent( label );
    gc.setFont( dev.getSystemFont() );
    f.dispose();
    return point;
  }

  /**
   * @see org.kalypso.chart.framework.model.legend.ILegendItem_old#getName()
   */
  public String getTitle( )
  {
    final String lname = m_layer.getTitle();
    if( lname != null )
      return lname;
    return "no Name";
  }

  /**
   * @see org.kalypso.chart.framework.model.legend.ILegendItem_old#getDescription()
   */
  public String getDescription( )
  {
    final String desc = m_layer.getDescription();
    if( desc != null )
      return desc;
    return "no Description";
  }

  /**
   * @see org.kalypso.chart.framework.model.legend.ILegendItem_old#drawIcon(org.eclipse.swt.graphics.Image)
   */
  public void drawIcon( Image img )
  {
    m_layer.drawIcon( img );
  }

  /**
   * @see org.kalypso.chart.framework.model.legend.ILegendItem_old#calcSize(org.kalypso.contribs.eclipse.swt.graphics.GCWrapper)
   */
  public Point computeSize( int whint, int hhint )
  {
    final GC gc = new GC( Display.getDefault() );
    final GCWrapper gcw = new GCWrapper( gc );

    final Point nameWidth = getTextExtent( gcw, Display.getCurrent(), getTitle(), m_fd );

    final int width = m_inset + m_iconWidth + m_inset + nameWidth.x + m_inset;
    final int height = m_inset + m_iconHeight + m_inset;
    // Logger.trace("LegendTextWidth (computeSize): "+nameWidth+" LegendText: "+getName());

    gcw.dispose();
    gc.dispose();

    return new Point( width, height );
  }

  /**
   * @see org.kalypso.chart.framework.model.legend.ILegendItem_old#getSize()
   */
  public Point getSize( )
  {
    return computeSize( 0, 0 );
  }

  /**
   * @see org.kalypso.chart.framework.model.legend.ILegendItem_old#paintImage(org.eclipse.swt.graphics.Image)
   */
  public void paintImage( Image img )
  {
    final GC gc = new GC( img );
    final Point p0 = new Point( m_inset, m_inset );
    final GCWrapper gcw = new GCWrapper( gc );

    final Point nameBounds = getTextExtent( gcw, Display.getCurrent(), getTitle(), m_fd );

    gc.setBackground( Display.getDefault().getSystemColor( (int) (Math.random() * 20.0) ) );

    // Icon holen
    final Image iconImage = new Image( Display.getDefault(), m_iconWidth, m_iconHeight );
    m_layer.drawIcon( iconImage );
    // Icon kopieren
    gc.drawImage( iconImage, 0, 0, m_iconWidth, m_iconHeight, p0.x, p0.y, m_iconWidth, m_iconHeight );
    // TestImage zerstören
    iconImage.dispose();

    gc.setForeground( Display.getDefault().getSystemColor( SWT.COLOR_GRAY ) );
    gc.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );
    gc.drawRectangle( p0.x, p0.y, m_iconWidth, m_iconHeight );

    // Jetzt den Layernamen
    gc.setForeground( Display.getDefault().getSystemColor( SWT.COLOR_BLACK ) );

    // an Mitte von icon anpassen
    // int width=getName().length()*fm.getAverageCharWidth();
    final int width = nameBounds.x;
    Logger.trace( "LegendTextWidth (paint) : " + width + " LegendText: " + getTitle() );

    final int fontheight = nameBounds.y;
    final int fontTop = (m_iconHeight - fontheight) / 2;

    final int textX = p0.x + m_iconWidth + m_inset;
    final int textY = p0.y + fontTop;
    // gc.drawString( getName(), textX , textY );

    drawText( gcw, gc.getDevice(), getTitle(), textX, textY, m_fd );

    gcw.dispose();
    gc.dispose();
  }

  /**
   * draws a given text at the position using special fontData
   */
  private void drawText( GCWrapper gc, Device dev, String text, int x, int y, FontData fd )
  {
    final Font f = new Font( dev, fd );
    gc.m_gc.setTextAntialias( SWT.ON );
    gc.setFont( f );
    gc.drawText( text, x, y );
    gc.setFont( dev.getSystemFont() );
    f.dispose();
  }
}
