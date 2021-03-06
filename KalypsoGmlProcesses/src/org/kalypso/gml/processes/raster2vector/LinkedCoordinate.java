package org.kalypso.gml.processes.raster2vector;

import java.util.ArrayList;
import java.util.Collection;

import org.kalypso.gml.processes.i18n.Messages;

import com.vividsolutions.jts.algorithm.PointInRing;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

/**
 * Element einer verketteten Liste von Cooridnaten
 *
 * @author belger
 */
public class LinkedCoordinate
{
  public final Coordinate crd;

  public Coordinate[] m_innerCrds = null;

  public final boolean border;

  private LinkedCoordinate m_head = null;

  private LinkedCoordinate m_tail = null;

  public LinkedCoordinate( final Coordinate c, final boolean bBorder )
  {
    crd = c;
    border = bBorder;
    m_innerCrds = null;
  }

  public void setInnerCrds( final Coordinate[] innerCrds )
  {
    m_innerCrds = innerCrds;
  }

  public void link( final LinkedCoordinate list ) throws LinkedCoordinateException
  {
    // die andere Liste an ein freies ende h�ngen
    // eine der listen muss vieleicht umgedreht werden
    if( m_head == null && list.m_tail == null )
    {
      m_head = list;
      list.m_tail = this;
      return;
    }

    if( m_tail == null && list.m_head == null )
    {
      m_tail = list;
      list.m_head = this;
      return;
    }

    if( m_tail == null && list.m_tail == null )
    {
      reverseHead( list );
      m_tail = list;
      list.m_head = this;
      return;
    }

    if( m_head == null && list.m_head == null )
    {
      reverseTail( list );
      m_head = list;
      list.m_tail = this;
      return;
    }

    throw new LinkedCoordinateException( Messages.getString( "org.kalypso.gml.processes.raster2vector.LinkedCoordinate.0" ) ); //$NON-NLS-1$
  }

  public boolean isCircle( )
  {
    LinkedCoordinate current = this;
    while( current.m_head != null )
    {
      if( current.m_head == this )
        return true;

      current = current.m_head;
    }

    return false;
  }

  private static void reverseHead( final LinkedCoordinate list ) throws LinkedCoordinateException
  {
    if( list.m_tail != null )
      throw new LinkedCoordinateException( Messages.getString( "org.kalypso.gml.processes.raster2vector.LinkedCoordinate.1" ) ); //$NON-NLS-1$

    LinkedCoordinate current = list;
    while( current != null )
    {
      final LinkedCoordinate head = current.m_head;

      current.m_head = current.m_tail;
      current.m_tail = head;

      current = head;

      // damits auch bei Kreisen geht
      if( current == list )
        return;
    }
  }

  private static void reverseTail( final LinkedCoordinate list ) throws LinkedCoordinateException
  {
    if( list.m_head != null )
      throw new LinkedCoordinateException( Messages.getString( "org.kalypso.gml.processes.raster2vector.LinkedCoordinate.2" ) ); //$NON-NLS-1$

    LinkedCoordinate current = list;
    while( current != null )
    {
      final LinkedCoordinate tail = current.m_tail;

      current.m_tail = current.m_head;
      current.m_head = tail;

      current = tail;

      // damits auch bei Kreisen geht
      if( current == list )
        return;
    }
  }

  public Coordinate[] getAsRing( ) throws LinkedCoordinateException
  {
    if( !isCircle() )
      throw new LinkedCoordinateException( Messages.getString( "org.kalypso.gml.processes.raster2vector.LinkedCoordinate.3" ) ); //$NON-NLS-1$

    final Collection<Coordinate> currentString = new ArrayList<>();

    // Startplatz �ndern
    LinkedCoordinate currentCrd = this;
    while( currentCrd != null )
    {
      currentString.add( currentCrd.crd );

      currentCrd = currentCrd.m_head;

      if( currentCrd == this )
      {
        currentString.add( currentCrd.crd );
        break;
      }
    }

    // return gf.createLinearRing( s_orientFilter.filter( crds ) );
    return currentString.toArray( new Coordinate[currentString.size()] );
  }

  /**
   * Returns the first non-<code>null</code> inner-coordinate of this linked list.
   */
  public Coordinate findInnerCrd( final PointInRing pir )
  {
    LinkedCoordinate current = this;
    while( current != null )
    {
      if( current.m_innerCrds != null )
      {
        for( final Coordinate currentInner : current.m_innerCrds )
        {
          if( currentInner != null && pir.isInside( currentInner ) )
            return currentInner;
        }
      }

      current = current.m_head;

      if( current == this )
        break;
    }

    return null;
  }

  public Collection<LineString> getLineStrings( final GeometryFactory gf )
  {
    final Collection<LineString> strings = new ArrayList<>();

    Collection<Coordinate> currentString = new ArrayList<>();

    // Startplatz �ndern
    LinkedCoordinate start = this;
    while( start.border && start.m_head != this )
      start = start.m_head;

    LinkedCoordinate currentCrd = start;
    while( currentCrd != null )
    {
      if( currentCrd.border )
      {
        if( currentString.size() > 1 )
          strings.add( gf.createLineString( currentString.toArray( new Coordinate[currentString.size()] ) ) );

        currentString = new ArrayList<>();
      }
      else
        currentString.add( currentCrd.crd );

      currentCrd = currentCrd.m_head;

      if( currentCrd == start )
      {
        currentString.add( currentCrd.crd );
        break;
      }
    }

    if( currentString.size() > 1 )
      strings.add( gf.createLineString( currentString.toArray( new Coordinate[currentString.size()] ) ) );

    return strings;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString( )
  {
    return Messages.getString( "org.kalypso.gml.processes.raster2vector.LinkedCoordinate.4" ) + crd + Messages.getString( "org.kalypso.gml.processes.raster2vector.LinkedCoordinate.5" ) + border; //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Gibt true zur�ck, wenn alle Koodinaten am Rand liegen
   */
  public boolean onlyBorder( )
  {
    LinkedCoordinate current = this;
    while( current != null )
    {
      if( !current.border )
        return false;

      current = current.m_head;

      if( current == this )
        break;
    }

    return true;
  }

}
