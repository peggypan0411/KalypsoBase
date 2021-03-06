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
package org.kalypso.zml.ui.chart.layer.themes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.openali.odysseus.chart.framework.model.figure.impl.FullRectangleFigure;
import de.openali.odysseus.chart.framework.model.layer.ILegendEntry;
import de.openali.odysseus.chart.framework.model.layer.impl.LegendEntry;

/**
 * @author Dirk Kuch
 */
public class ZmlBarLayerLegendEntry
{
  private final ZmlBarLayer m_layer;

  public ZmlBarLayerLegendEntry( final ZmlBarLayer layer )
  {
    m_layer = layer;
  }

  public ILegendEntry[] createLegendEntries( final FullRectangleFigure polygonFigure )
  {
    final List<ILegendEntry> entries = new ArrayList<>();
    if( polygonFigure.getStyle().isVisible() )
    {
      final LegendEntry entry = new LegendEntry( m_layer, m_layer.getTitle() )
      {
        @Override
        public void paintSymbol( final GC gc, final Point size )
        {
          final int height = size.x;
          final int width = size.y;
          polygonFigure.setRectangle( new Rectangle( 0, height / 2, width / 2, height / 2 ) );
          polygonFigure.paint( gc );
          polygonFigure.setRectangle( new Rectangle( width / 2, 0, width/2, height ) );
          polygonFigure.paint( gc );
        }
      };

      entries.add( entry );
    }

    return entries.toArray( new ILegendEntry[] {} );
  }
}