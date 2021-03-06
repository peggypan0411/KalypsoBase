package de.openali.odysseus.chart.framework.model.style.impl;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

import de.openali.odysseus.chart.framework.OdysseusChartFramework;
import de.openali.odysseus.chart.framework.model.mapper.IAxisConstants.ALIGNMENT;
import de.openali.odysseus.chart.framework.model.style.IStyleConstants.FONTSTYLE;
import de.openali.odysseus.chart.framework.model.style.IStyleConstants.FONTWEIGHT;
import de.openali.odysseus.chart.framework.model.style.ITextStyle;

public class TextStyle extends AbstractStyle implements ITextStyle
{
  private FONTSTYLE m_style;

  private RGB m_textRGB;

  private String m_family;

  private int m_size;

  private RGB m_fillRGB;

  private FONTWEIGHT m_weight;

  private ALIGNMENT m_alignment;

  public TextStyle( final int height, final String family, final RGB rgbText, final RGB rgbFill, final FONTSTYLE style, final FONTWEIGHT weight, final ALIGNMENT alignment, final int alpha, final boolean isVisible )
  {
    setFamily( family );
    setHeight( height );
    setTextColor( rgbText );
    setFillColor( rgbFill );
    setFontStyle( style );
    setWeight( weight );
    setAlpha( alpha );
    setVisible( isVisible );
    setAlignment( alignment );
  }

  /*
   * (non-Javadoc)
   * @see
   * de.openali.odysseus.chart.framework.impl.model.style.ITextStyle#setWeight(de.openali.odysseus.chart.framework.model
   * .style.IStyleConstants.FONTWEIGHT)
   */
  @Override
  public void setWeight( final FONTWEIGHT weight )
  {
    m_weight = weight;
  }

  /*
   * (non-Javadoc)
   * @see de.openali.odysseus.chart.framework.impl.model.style.ITextStyle#setFillColor(org.eclipse.swt.graphics.RGB)
   */
  @Override
  public void setFillColor( final RGB rgbFill )
  {
    m_fillRGB = rgbFill;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.openali.odysseus.chart.framework.impl.model.style.ITextStyle#setStyle(de.openali.odysseus.chart.framework.model
   * .style.IStyleConstants.FONTSTYLE)
   */
  @Override
  public void setFontStyle( final FONTSTYLE style )
  {
    m_style = style;
  }

  /*
   * (non-Javadoc)
   * @see de.openali.odysseus.chart.framework.impl.model.style.ITextStyle#setTextColor(org.eclipse.swt.graphics.RGB)
   */
  @Override
  public void setTextColor( final RGB rgbText )
  {
    m_textRGB = rgbText;
  }

  /*
   * (non-Javadoc)
   * @see de.openali.odysseus.chart.framework.impl.model.style.ITextStyle#setFamily(java.lang.String)
   */
  @Override
  public void setFamily( final String family )
  {
    m_family = family;
  }

  /*
   * (non-Javadoc)
   * @see de.openali.odysseus.chart.framework.impl.model.style.ITextStyle#setHeight(int)
   */
  @Override
  public void setHeight( final int size )
  {
    m_size = size;
  }

  @Override
  public void apply( final GC gc )
  {
    final FontData fd = toFontData();
    final Font font = OdysseusChartFramework.getDefault().getFontRegistry().getResource( gc.getDevice(), fd );

    gc.setFont( font );
    gc.setBackground( OdysseusChartFramework.getDefault().getColorRegistry().getResource( gc.getDevice(), m_fillRGB ) );
    gc.setForeground( OdysseusChartFramework.getDefault().getColorRegistry().getResource( gc.getDevice(), m_textRGB ) );
    gc.setAlpha( getAlpha() );
  }

  @Override
  public FontData toFontData( )
  {
    return new FontData( m_family, m_size, m_style.toSWT() | m_weight.toSWT() );
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.style.ITextStyle#copy()
   */
  @Override
  public ITextStyle clone( )
  {
    return new TextStyle( getHeight(), getFamily(), getTextColor(), getFillColor(), getFontStyle(), getWeight(), getAlignment(), getAlpha(), isVisible() );
  }

  @Override
  public FONTWEIGHT getWeight( )
  {
    return m_weight;
  }

  @Override
  public FONTSTYLE getFontStyle( )
  {
    return m_style;
  }

  @Override
  public RGB getFillColor( )
  {
    return m_fillRGB;
  }

  @Override
  public RGB getTextColor( )
  {
    return m_textRGB;
  }

  @Override
  public String getFamily( )
  {
    return m_family;
  }

  @Override
  public int getHeight( )
  {
    return m_size;
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.style.ITextStyle#getPosition()
   */
  @Override
  public ALIGNMENT getAlignment( )
  {
    return m_alignment;
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.style.ITextStyle#setPosition(de.openali.odysseus.chart.framework.model.mapper.IAxisConstants.ALIGNMENT)
   */
  @Override
  public void setAlignment( final ALIGNMENT position )
  {
    m_alignment = position;
  }
}
