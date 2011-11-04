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
package org.kalypso.ogc.gml.outline.nodes;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.kalypso.contribs.eclipse.swt.SWTUtilities;

/**
 * This class wraps an object for the legend graphic.
 * 
 * @author Holger Albert
 */
public class LegendElement
{
  /**
   * This is the size of the icon. This size is used for summarizing the width of the legend item.
   */
  public static int ICON_SIZE = 16;

  /**
   * This is the gap after the icon. This size is used for summarizing the width of the legend item.
   */
  public static int GAP = 4;

  /**
   * The font, to use for this legend element.
   */
  private final Font m_font;

  /**
   * The level of this legend element.
   */
  private final int m_level;

  private final IThemeNode m_node;

  /**
   * The constructor.
   * 
   * @param font
   *          The font, to use for this legend element.
   * @param level
   *          The level of this legend element.
   * @param object
   *          The object of this legend element.
   */
  public LegendElement( final org.eclipse.swt.graphics.Font font, final int level, final IThemeNode node )
  {
    m_font = font;
    m_level = level;
    m_node = node;
  }

  /**
   * This function returns the dimension of this legend element.
   * 
   * @return The dimension of this element.
   */
  public Rectangle getSize( )
  {
    final Point textSize = SWTUtilities.calcTextSize( getText(), m_font );
    final int textWidth = textSize.x;
    final int textHeight = textSize.y;

    /* Width. */
    final int width = textWidth + ICON_SIZE + GAP + m_level * (ICON_SIZE + GAP);

    /* Height. */
    int height = ICON_SIZE;
    if( textHeight > ICON_SIZE )
      height = textHeight;

    return new Rectangle( 0, 0, width, height );
  }

  /**
   * This function returns the text for this legend item.
   * 
   * @return The text of this legend item.
   */
  public String getText( )
  {
    return m_node.getLabel();
  }

  /**
   * This function returns the image for this legend item.
   * 
   * @return The image of this legend item.
   */
  public ImageDescriptor getImage( )
  {
    return m_node.getImageDescriptor();
  }

  /**
   * This function returns the font, to use for this legend element.
   * 
   * @return The font, to use for this legend element.
   */
  public Font getFont( )
  {
    return m_font;
  }

  /**
   * This function returns the level of this legend element.
   * 
   * @return The level of this legend element.
   */
  public int getLevel( )
  {
    return m_level;
  }
}