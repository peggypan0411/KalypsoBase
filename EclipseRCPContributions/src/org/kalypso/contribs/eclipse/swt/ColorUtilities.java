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
package org.kalypso.contribs.eclipse.swt;

import org.eclipse.swt.graphics.RGB;

/**
 * Helper class for dealing with colors.
 * 
 * @author Holger Albert
 */
public final class ColorUtilities
{
  /**
   * The constructor.
   */
  private ColorUtilities( )
  {
  }

  /**
   * This function returns an RGB color of the gicen html string.
   * 
   * @param html
   *          The html string in the format #RGB.
   * @return The RGB color, or null, if string is not correct parsable.
   */
  public static RGB toRGBFromHTML( final String html )
  {
    if( html == null || html.length() != 7 )
      return null;

    if( !html.startsWith( "#" ) )
      return null;

    final RGB rgb = new RGB( 0, 0, 0 );

    final String red = html.substring( 1, 3 );
    rgb.red = Integer.decode( "0x" + red );

    final String green = html.substring( 3, 5 );
    rgb.green = Integer.decode( "0x" + green );

    final String blue = html.substring( 5, 7 );
    rgb.blue = Integer.decode( "0x" + blue );

    return rgb;
  }

  /**
   * Converts a rgb color to a W3C conform form.
   * 
   * @param rgb
   *          The color in RGB format.
   * @return The color in W3C conform form.
   */
  public static String toHTMLFromRGB( final RGB rgb )
  {
    String red = Integer.toHexString( rgb.red );
    if( red.length() < 2 )
      red = "0" + red;

    String green = Integer.toHexString( rgb.green );
    if( green.length() < 2 )
      green = "0" + green;

    String blue = Integer.toHexString( rgb.blue );
    if( blue.length() < 2 )
      blue = "0" + blue;

    return new String( "#" + red + green + blue );
  }

  public static java.awt.Color toAwtColor( final RGB rgb )
  {
    return new java.awt.Color( rgb.red, rgb.green, rgb.blue );
  }

  public static RGB toRGB( final java.awt.Color color )
  {
    return new RGB( color.getRed(), color.getGreen(), color.getBlue() );
  }
}