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
package org.kalypsodeegree_impl.model.geometry;

import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_CurveSegment;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_LineString;
import org.kalypsodeegree.model.geometry.GM_MultiPrimitive;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.model.geometry.GM_Position;

/**
 * default implementation of the GM_LineString interface of package jago.model.
 * ------------------------------------------------------------
 * 
 * @version 10.6.2001
 * @author Andreas Poth
 */
final class GM_LineString_Impl extends GM_CurveSegment_Impl implements GM_LineString
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 8093549521711824076L;

  /**
   * Creates a new GM_LineString_Impl object.
   * 
   * @param gmps
   * @param cs
   * @throws GM_Exception
   */
  public GM_LineString_Impl( final GM_Position[] gmps, final String cs ) throws GM_Exception
  {
    super( gmps, cs );
  }

  /**
   * returns a shallow copy of the geometry
   */
  @Override
  public Object clone( )
  {
    // kuch
    final String system = getCoordinateSystem();
    final GM_Position[] clonedPositions = GeometryFactory.cloneGM_Position( getPositions() );

    try
    {
      return new GM_LineString_Impl( clonedPositions, system );
    }
    catch( final GM_Exception e )
    {
      e.printStackTrace();
    }

    throw new IllegalStateException();
  }

  /**
   * returns a reference to itself
   */
  @Override
  public GM_LineString getAsLineString( )
  {
    return this;
  }

  /**
   * The Boolean valued operation "intersects" shall return TRUE if this GM_Object intersects another GM_Object. Within
   * a GM_Complex, the GM_Primitives do not intersect one another. In general, topologically structured data uses shared
   * geometric objects to capture intersection information.
   */
  @Override
  public boolean intersects( final GM_Object gmo )
  {
    boolean inter = false;

    try
    {
      if( gmo instanceof GM_Point )
      {
        inter = LinearIntersects.intersects( ((GM_Point)gmo).getPosition(), this );
      }
      else if( gmo instanceof GM_Curve )
      {
        final GM_CurveSegment[] cs = new GM_CurveSegment[] { this };
        inter = LinearIntersects.intersects( (GM_Curve)gmo, new GM_Curve_Impl( cs ) );
      }
      else if( gmo instanceof GM_Polygon )
      {
        final GM_CurveSegment[] cs = new GM_CurveSegment[] { this };
        inter = LinearIntersects.intersects( new GM_Curve_Impl( cs ), (GM_Polygon)gmo );
      }
      else if( gmo instanceof GM_MultiPrimitive )
      {
        inter = intersectsMultiPrimitive( (GM_MultiPrimitive)gmo );
      }
    }
    catch( final Exception e )
    {
    }

    return inter;
  }

  /**
   * the operations returns true if the submitted multi primitive intersects with the curve segment
   */
  private boolean intersectsMultiPrimitive( final GM_MultiPrimitive mprim ) throws Exception
  {
    boolean inter = false;

    final int cnt = mprim.getSize();

    for( int i = 0; i < cnt; i++ )
    {
      if( intersects( mprim.getPrimitiveAt( i ) ) )
      {
        inter = true;
        break;
      }
    }

    return inter;
  }

  /**
   * The Boolean valued operation "contains" shall return TRUE if this GM_Object contains another GM_Object.
   */
  @Override
  public boolean contains( final GM_Object gmo )
  {
    return false;
  }

}