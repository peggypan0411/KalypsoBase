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

import org.kalypso.transformation.transformer.GeoTransformerException;
import org.kalypsodeegree.model.geometry.GM_AbstractSurfacePatch;
import org.kalypsodeegree.model.geometry.GM_Aggregate;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.model.geometry.GM_PolygonPatch;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree.model.geometry.GM_Ring;

/**
 * default implementierung of the GM_Polygon interface from package jago.model.
 * ------------------------------------------------------------
 * 
 * @version 11.6.2001
 * @author Andreas Poth
 */
final class GM_PolygonPatch_Impl extends GM_AbstractSurfacePatch_Impl implements GM_PolygonPatch
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = -1293845886457211088L;

  /**
   * Creates a new GM_Polygon_Impl object.
   * 
   * @param interpolation
   * @param exteriorRing
   * @param interiorRings
   * @param crs
   * @throws GM_Exception
   */
  public GM_PolygonPatch_Impl( final GM_Position[] exteriorRing, final GM_Position[][] interiorRings, final String crs ) throws GM_Exception
  {
    super( exteriorRing, interiorRings, crs );
  }

  /**
   * checks if this curve is completly equal to the submitted geometry
   * 
   * @param other
   *          object to compare to
   */
  @Override
  public boolean equals( final Object other )
  {
    if( !super.equals( other ) || !(other instanceof GM_PolygonPatch_Impl) )
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString( )
  {
    String ret = "GM_SurfacePatch: ";
    ret = "interpolation = " + INTERPOLATION_NONE + "\n";
    ret += "exteriorRing = \n";

    final GM_Position[] exteriorRing = getExteriorRing();
    for( final GM_Position element : exteriorRing )
    {
      ret += element + "\n";
    }

    ret += "interiorRings = " + getInteriorRings() + "\n";
    ret += "envelope = " + getEnvelope() + "\n";
    return ret;
  }

  /**
   * returns a deep copy of the geometry
   */
  @Override
  public Object clone( )
  {
    final GM_Position[] clonedExteriorRing = GeometryFactory.cloneGM_Position( getExteriorRing() );

    final GM_Position[][] interiorRings = getInteriorRings();
    final GM_Position[][] clonedInterior = interiorRings == null ? null : new GM_Position[interiorRings.length][];

    if( clonedInterior != null )
    {
      for( int i = 0; i < interiorRings.length; i++ )
        clonedInterior[i] = GeometryFactory.cloneGM_Position( interiorRings[i] );
    }

    try
    {
      return new GM_PolygonPatch_Impl( clonedExteriorRing, clonedInterior, getCoordinateSystem() );
    }
    catch( final GM_Exception e )
    {
      e.printStackTrace();
    }

    throw new IllegalStateException();
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
        inter = LinearIntersects.intersects( (GM_Curve)gmo, new GM_Polygon_Impl( this ) );
      }
      else if( gmo instanceof GM_Polygon )
      {
        inter = LinearIntersects.intersects( (GM_Polygon)gmo, new GM_Polygon_Impl( this ) );
      }
      else if( gmo instanceof GM_Aggregate )
      {
        inter = intersectsMultiObject( (GM_Aggregate)gmo );
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
  private boolean intersectsMultiObject( final GM_Aggregate mprim ) throws Exception
  {
    final int cnt = mprim.getSize();
    for( int i = 0; i < cnt; i++ )
    {
      if( intersects( mprim.getObjectAt( i ) ) )
        return true;
    }

    return false;
  }

  /**
   * The Boolean valued operation "contains" shall return TRUE if this GM_Object contains another GM_Object.
   */
  @Override
  public boolean contains( final GM_Object gmo )
  {
    boolean contain = false;

    try
    {
      if( gmo instanceof GM_Point )
      {
        contain = LinearContains.contains( this, ((GM_Point)gmo).getPosition() );
      }
      else if( gmo instanceof GM_Curve )
      {
        // contain = contain_.contains ( new GM_Surface_Impl ( this ),
        // (GM_Curve)gmo );
        contain = LinearContains.contains( this, ((GM_Curve)gmo).getAsLineString() );
      }
      else if( gmo instanceof GM_Polygon )
      {
        contain = LinearContains.contains( new GM_Polygon_Impl( this ), (GM_Polygon)gmo );
      }
      else if( gmo instanceof GM_Aggregate )
      {
        contain = containsMultiObject( (GM_Aggregate)gmo );
      }
    }
    catch( final Exception e )
    {
    }

    return contain;
  }

  private boolean containsMultiObject( final GM_Aggregate gmo )
  {
    try
    {
      for( int i = 0; i < gmo.getSize(); i++ )
      {
        if( !contains( gmo.getObjectAt( i ) ) )
        {
          return false;
        }
      }
    }
    catch( final Exception e )
    {
    }

    return true;
  }

  @Override
  public GM_AbstractSurfacePatch transform( final String targetCRS ) throws GeoTransformerException
  {
    try
    {
      /* If the target is the same coordinate system, do not transform. */
      final String sourceCRS = getCoordinateSystem();
      if( sourceCRS == null || sourceCRS.equalsIgnoreCase( targetCRS ) )
        return this;

      /* exterior ring */
      final GM_Ring exRing = GeometryFactory.createGM_Ring( getExteriorRing(), getCoordinateSystem() );
      final GM_Ring transExRing = (GM_Ring)exRing.transform( targetCRS );

      /* interior rings */
      final GM_Position[][] interiors = getInteriorRings();
      if( interiors == null )
        return new GM_PolygonPatch_Impl( transExRing.getPositions(), null, targetCRS );

      final GM_Position[][] transInRings = new GM_Position[interiors.length][];

      for( int j = 0; j < interiors.length; j++ )
      {
        final GM_Ring inRing = GeometryFactory.createGM_Ring( interiors[j], getCoordinateSystem() );
        transInRings[j] = ((GM_Ring)inRing.transform( targetCRS )).getPositions();
      }

      return new GM_PolygonPatch_Impl( transExRing.getPositions(), transInRings, targetCRS );
    }
    catch( final GM_Exception e )
    {
      throw new GeoTransformerException( e );
    }
  }
}