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

import java.io.Serializable;
import java.util.Arrays;

import org.kalypsodeegree.model.geometry.GM_CurveSegment;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_LineString;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;

/**
 * default implementation of the GM_CurveSegment interface from package jago.model. the class is abstract because it
 * should be specialized by derived classes <code>GM_LineString</code> for example
 * <p>
 * ---------------------------------------------------------------------------
 * </p>
 * 
 * @version 10.6.2001
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 */
class GM_CurveSegment_Impl implements GM_CurveSegment, Serializable
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = -8102075931849374162L;

  protected String m_crs = null;

  protected GM_Position[] points = new GM_Position[0];

  /**
   * Creates a new GM_CurveSegment_Impl object.
   * 
   * @param gmps
   * @param crs
   * @throws GM_Exception
   */
  protected GM_CurveSegment_Impl( final GM_Position[] gmps, final String crs ) throws GM_Exception
  {
    if( gmps == null )
    {
      throw new GM_Exception( "can't create an empty curve segment" );
    }

    points = gmps;

    // get spatial reference system of the curve segment from the first point
    m_crs = crs;
  }

  /**
   * returns the first point of the curve. if the curve segment doesn't contain a point <code>null</code> will be
   * returned
   */
  @Override
  public GM_Point getStartPoint( )
  {
    return new GM_Point_Impl( points[0], m_crs );
  }

  /**
   * returns the last point of the curve. if the curve segment doesn't contain a point <code>null</code> will be
   * returned
   */
  @Override
  public GM_Point getEndPoint( )
  {
    return new GM_Point_Impl( points[getNumberOfPoints() - 1], m_crs );
  }

  /**
   * returns the number of points building the curve or curve segment
   */
  @Override
  public int getNumberOfPoints( )
  {
    return points.length;
  }

  /**
   * returns all positions of the segement as array of GM_Position. If the segment is empty null will be returned
   */
  @Override
  public GM_Position[] getPositions( )
  {
    return points;
  }

  /**
   * returns the curve segment position at the submitted index
   */
  @Override
  public GM_Position getPositionAt( final int index )
  {
    return points[index];
  }

  /**
   * reverses the direction of the curvesegment
   */
  @Override
  public void reverse( )
  {
    final GM_Position[] reverse_ = new GM_Position[points.length];

    for( int i = 0; i < points.length; i++ )
    {
      reverse_[points.length - 1 - i] = points[i];
    }

    points = reverse_;
  }

  /**
   * returns the coordinate system of the curve segment
   */
  @Override
  public String getCoordinateSystem( )
  {
    return m_crs;
  }

  /**
   * checks if this curve segment is completly equal to the submitted geometry
   * 
   * @param other
   *          object to compare to
   */
  @Override
  public boolean equals( final Object other )
  {
    if( other == null || !(other instanceof GM_CurveSegment_Impl) )
    {
      return false;
    }

    if( m_crs == null && ((GM_CurveSegment_Impl) other).getCoordinateSystem() != null )
    {
      return false;
    }

    if( m_crs != null )
    {
      if( !m_crs.equals( ((GM_CurveSegment_Impl) other).getCoordinateSystem() ) )
      {
        return false;
      }
    }
    else
    {
      if( ((GM_CurveSegment_Impl) other).getCoordinateSystem() != null )
      {
        return false;
      }
    }

    final GM_Position[] p1 = getPositions();
    final GM_Position[] p2 = ((GM_CurveSegment) other).getPositions();

    if( !Arrays.equals( p1, p2 ) )
    {
      return false;
    }

    return true;
  }

  /**
   * The Boolean valued operation "contains" shall return TRUE if this GM_Object contains another GM_Object.
   * <p>
   * </p>
   */
  @Override
  public boolean contains( final GM_Object gmo )
  {
    throw new NoSuchMethodError( "the contains operation for curve segments " + "isn't supported at the moment." );
  }

  @Override
  public String toString( )
  {
    String ret = null;
    ret = "points = ";
    ret += "crs = " + m_crs + "\n";
    return ret;
  }

  /**
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone( )
  {
    // kuch
    final String system = getCoordinateSystem();
    final GM_Position[] clonedPositions = GeometryFactory.cloneGM_Position( getPositions() );

    try
    {
      return new GM_CurveSegment_Impl( clonedPositions, system );
    }
    catch( final GM_Exception e )
    {
      e.printStackTrace();
    }

    throw new IllegalStateException();
  }

  /**
   * @see org.kalypsodeegree.model.geometry.GM_CurveSegment#intersects(org.kalypsodeegree.model.geometry.GM_Object)
   */
  @Override
  public boolean intersects( final GM_Object gmo )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.kalypsodeegree.model.geometry.GM_GenericCurve#getAsLineString()
   */
  @Override
  public GM_LineString getAsLineString( )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.kalypsodeegree.model.geometry.GM_GenericCurve#getLength()
   */
  @Override
  public double getLength( )
  {
    double result = 0;
    for( int i = 1; i < points.length; i++ )
      result += points[i].getDistance( points[i - 1] );
    return result;
  }
}