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
package org.kalypso.model.wspm.ui.view.chart.color;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/** Erzeugt eine {@link org.eclipse.jface.resource.ColorRegistry} mit einer Standardfarbeelegung f�r den Profileditor */
public final class DefaultProfilColorRegistryFactory
{
  private DefaultProfilColorRegistryFactory( )
  {
    // wird nicht instantiiert
  }

  /**
   * Erzeugt die Registry, diese muss vom aufrufenden zerst�rt (dispose) werden.
   * 
   * @param display
   */
  public static ColorRegistry createColorRegistry( final Display display )
  {
    final ColorRegistry registry = new ColorRegistry( display, true );

    registry.put( IProfilColorSet.COLOUR, new RGB( 0, 0, 0 ) );
    registry.put( IProfilColorSet.COLOUR_GELAENDE_MARKED, new RGB( 200, 50, 0 ) );
    registry.put( IProfilColorSet.COLOUR_AXIS_FOREGROUND, new RGB( 0, 0, 0 ) );
    registry.put( IProfilColorSet.COLOUR_AXIS_BACKGROUND, new RGB( 255, 255, 255 ) );
    registry.put( IProfilColorSet.COLOUR_STATIONS, new RGB( 128, 128, 128 ) );
    registry.put( IProfilColorSet.COLOUR_WSP, new RGB( 0, 128, 255 ) );

    return registry;
  }
}
