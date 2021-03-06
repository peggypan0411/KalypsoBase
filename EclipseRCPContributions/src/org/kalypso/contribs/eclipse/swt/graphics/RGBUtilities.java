/*--------------- Kalypso-Header ------------------------------------------

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

--------------------------------------------------------------------------*/

package org.kalypso.contribs.eclipse.swt.graphics;

import org.eclipse.swt.graphics.RGB;

/**
 * Some utility functions for dealing with {@link org.eclipse.swt.graphics.RGB}.
 * 
 * @author schlienger
 */
public final class RGBUtilities
{
  private RGBUtilities( )
  {
  }

  public static RGB parse( final String string, final String split )
  {
    final String[] strings = string.split( split );
    final int r = Integer.parseInt( strings[0] );
    final int g = Integer.parseInt( strings[1] );
    final int b = Integer.parseInt( strings[2] );

    return new RGB( r, g, b );
  }

  public static RGB decodeHtmlColor( final String html )
  {
    String decode = null;

    if( html.startsWith( "#" ) )
      decode = html.substring( 1 );
    else
      decode = html;

    RGB rgb;
    int red, green, blue;
    switch( decode.length() )
    {
      case 6:
        red = Integer.parseInt( decode.substring( 0, 2 ), 16 );
        green = Integer.parseInt( decode.substring( 2, 4 ), 16 );
        blue = Integer.parseInt( decode.substring( 4, 6 ), 16 );
        rgb = new RGB( red, green, blue );
        break;
      case 3:
        red = Integer.parseInt( decode.substring( 0, 1 ), 16 );
        green = Integer.parseInt( decode.substring( 1, 2 ), 16 );
        blue = Integer.parseInt( decode.substring( 2, 3 ), 16 );
        rgb = new RGB( red, green, blue );
        break;
      case 1:
        red = green = blue = Integer.parseInt( decode.substring( 0, 1 ), 16 );
        rgb = new RGB( red, green, blue );
        break;
      default:
        throw new IllegalArgumentException( "Invalid color: " + decode );
    }

    return rgb;
  }
}
