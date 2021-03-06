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
package org.kalypso.ogc.gml.map.widgets.advanced.edit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.kalypso.ogc.gml.map.widgets.advanced.utils.IPointHighLighter;

/**
 * @author Dirk Kuch
 */
public interface IAdvancedEditWidgetDelegate
{
  IPointHighLighter MOVED_SNAP_POINT = new IPointHighLighter()
  {
    private final Color m_snapColor = new Color( 0x31, 0x47, 0xa0, 128 );

    private final int m_size = 5;

    @Override
    public void draw( final Graphics g, final java.awt.Point point )
    {
      final Color original = g.getColor();
      g.setColor( m_snapColor );
      g.fillOval( point.x - m_size / 2, point.y - m_size / 2, m_size, m_size );
      g.setColor( original );
    }
  };

  IPointHighLighter SNAP = new IPointHighLighter()
  {
    private final Color m_snapColor = new Color( 0x40, 0xde, 0x28 );

    private final int m_size = 10;

    @Override
    public void draw( final Graphics g, final java.awt.Point point )
    {
      final Color original = g.getColor();
      g.setColor( m_snapColor );
      g.fillOval( point.x - m_size / 2, point.y - m_size / 2, m_size, m_size );
      g.setColor( original );
    }
  };

  IPointHighLighter VERTEX = new IPointHighLighter()
  {
    private final Color m_vertexColor = new Color( 0x3e, 0x79, 0xd9 );

    @Override
    public void draw( final Graphics g, final java.awt.Point point )
    {
      final Color original = g.getColor();
      g.setColor( m_vertexColor );
      g.drawRect( point.x - 6 / 2, point.y - 6 / 2, 6, 6 );
      g.setColor( original );
    }
  };

  String getToolTip( );

  void leftReleased( Point p );

  void paint( final Graphics g );

  double getRange( );
}
