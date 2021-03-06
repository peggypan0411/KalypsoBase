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
package de.openali.odysseus.chart.framework.model.style.impl;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import de.openali.odysseus.chart.framework.OdysseusChartFramework;

public class ImageMarker extends AbstractMarker
{
  private final ImageDescriptor m_id;

  public ImageMarker( final ImageDescriptor id )
  {
    m_id = id;
  }

  @Override
  public void paint( final GC gc, final int x, final int y, final int width, final int height, final boolean drawForeground, final boolean drawBackground )
  {
    final Image img = OdysseusChartFramework.getDefault().getImageRegistry().getResource( gc.getDevice(), m_id );
    if( drawBackground )
      gc.fillRectangle( x, y, width, height );

    gc.drawImage( img, 0, 0, img.getBounds().width, img.getBounds().height, x, y, width, height );

    if( drawForeground )
      gc.drawRectangle( x, y, width, height );
  }

  @Override
  public ImageMarker copy( )
  {
    return new ImageMarker( m_id );
  }
}