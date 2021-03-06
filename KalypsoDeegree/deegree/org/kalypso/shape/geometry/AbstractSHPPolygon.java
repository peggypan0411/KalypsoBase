/** This file is part of kalypso/deegree.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * history:
 *
 * Files in this package are originally taken from deegree and modified here
 * to fit in kalypso. As goals of kalypso differ from that one in deegree
 * interface-compatibility to deegree is wanted but not retained always.
 *
 * If you intend to use this software in other ways than in kalypso
 * (e.g. OGC-web services), you should consider the latest version of deegree,
 * see http://www.deegree.org .
 *
 * all modifications are licensed as deegree,
 * original copyright:
 *
 * Copyright (C) 2001 by:
 * EXSE, Department of Geography, University of Bonn
 * http://www.giub.uni-bonn.de/exse/
 * lat/lon GmbH
 * http://www.lat-lon.de
 */
package org.kalypso.shape.geometry;

import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Gernot Belger
 */
abstract class AbstractSHPPolygon implements ISHPPolygon
{
  private final AbstractSHPPolyLine m_rings;

  public AbstractSHPPolygon( final byte[] recBuf )
  {
    this( new SHPPolyLine( recBuf ) );
  }

  public AbstractSHPPolygon( final ISHPMultiPoint points, final int[] parts )
  {
    this( new SHPPolyLine( points, parts ) );
  }

  public AbstractSHPPolygon( final AbstractSHPPolyLine rings )
  {
    m_rings = rings;
  }

  @Override
  public void write( final DataOutput output ) throws IOException
  {
    m_rings.write( output );
  }

  @Override
  public int length( )
  {
    return m_rings.length();
  }

  @Override
  public String toString( )
  {
    return m_rings.toString();
  }

  @Override
  public SHPEnvelope getEnvelope( )
  {
    return m_rings.getEnvelope();
  }

  @Override
  public int getNumParts( )
  {
    return m_rings.getNumParts();
  }

  @Override
  public int getNumPoints( )
  {
    return m_rings.getNumPoints();
  }

  @Override
  public ISHPPoint[] getPoints( )
  {
    return m_rings.getPoints();
  }

  @Override
  public int[] getParts( )
  {
    return m_rings.getParts();
  }
}