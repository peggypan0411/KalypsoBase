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

import org.kalypso.shape.ShapeType;

/**
 * Class representig an empty geometry <BR>
 * <B>Last changes <B>: <BR>
 * update 04.10.2007, Jung, implemented writeNullShape method
 * <!---------------------------------------------------------------------------->
 * 
 * @version 25.1.2000
 * @author Andreas Poth <br>
 *         Thomas Jung
 */
public class SHPNullShape implements ISHPGeometry
{
  /**
   * @see org.kalypsodeegree_impl.io.shpapi.AbstractShape#writeContent(java.io.DataOutput)
   */
  @Override
  public void write( final DataOutput output )
  {
    // empty
  }

  /**
   * @see org.kalypsodeegree_impl.io.shpapi.ISHPGeometry#getType()
   */
  @Override
  public ShapeType getType( )
  {
    return ShapeType.NULL;
  }

  @Override
  public int length( )
  {
    return 0;
  }

  /**
   * @see org.kalypsodeegree_impl.io.shpapi.SHPGeometry#getEnvelope()
   */
  @Override
  public SHPEnvelope getEnvelope( )
  {
    return null;
  }

}