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
package org.kalypso.model.wspm.core.profil.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.jts.JTSUtilities;
import org.kalypso.model.wspm.core.i18n.Messages;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilPointMarker;
import org.kalypso.model.wspm.core.profil.visitors.ProfileVisitors;
import org.kalypso.model.wspm.core.util.WspmGeometryUtilities;
import org.kalypso.model.wspm.core.util.WspmProfileHelper;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.model.geometry.JTSAdapter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * @deprecated use IProfile / IProfileRecord implementation
 * @author Dirk Kuch
 */
@Deprecated
public class ProfileWrapper
{
  private final IProfil m_profile;

  public ProfileWrapper( final IProfil profile )
  {
    m_profile = profile;
  }

  public double getStation( )
  {
    return m_profile.getStation();
  }

  public boolean hasPoint( final double width )
  {
    final IProfileRecord point = ProfileVisitors.findPoint( m_profile, width );
    if( Objects.isNotNull( point ) )
      return true;

    return false;
  }

  public IProfileRecord addPoint( final Double width )
  {
    final IProfileRecord point = ProfileVisitors.findPoint( m_profile, width );
    if( Objects.isNotNull( point ) )
      return point;

    final IProfileRecord add = m_profile.createProfilPoint();
    add.setBreite( width );
    add.setHoehe( getHoehe( width ) );

    return WspmProfileHelper.addRecordByWidth( m_profile, add );
  }

  public double getHoehe( final Double width )
  {
    final IProfileRecord before = getProfile().findPreviousPoint( width );
    final IProfileRecord after = getProfile().findNextPoint( width );

    if( before == null || after == null )
      return 0.0;

    final double deltaH = after.getHoehe() - before.getHoehe();
    final double distanceDeltaH = Math.abs( before.getBreite() - after.getBreite() );

    final double distance = Math.abs( before.getBreite() - width );
    final double hoehe = deltaH / distanceDeltaH * distance;

    return before.getHoehe() + hoehe;
  }

  public IProfilPointMarker[] getProfilePointMarkerWrapper( final String marker )
  {

    final List<IProfilPointMarker> wrappers = new ArrayList<IProfilPointMarker>();

    final IProfilPointMarker[] markers = m_profile.getPointMarkerFor( marker );
    for( final IProfilPointMarker m : markers )
    {
      wrappers.add( m );
    }

    return wrappers.toArray( new IProfilPointMarker[] {} );
  }

  @Override
  public String toString( )
  {
    return String.format( Messages.getString( "ProfileWrapper_0" ), m_profile.getStation() ); //$NON-NLS-1$
  }

  public LineString getGeometry( ) throws GM_Exception
  {
    final String crs = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();

    final GM_Curve profileCurve = WspmGeometryUtilities.createProfileSegment( m_profile, crs );
    final LineString profileLineString = (LineString) JTSAdapter.export( profileCurve );

    return profileLineString;
  }

  public IProfil getProfile( )
  {
    return m_profile;
  }

  @Override
  public boolean equals( final Object obj )
  {
    if( obj instanceof ProfileWrapper )
    {
      final ProfileWrapper other = (ProfileWrapper) obj;

      final String station = String.format( "%.3f", getStation() ); //$NON-NLS-1$
      final String otherStation = String.format( "%.3f", other.getStation() ); //$NON-NLS-1$

      final EqualsBuilder builder = new EqualsBuilder();
      builder.append( station, otherStation );

      return builder.isEquals();
    }

    return super.equals( obj );
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode( )
  {
    final String station = String.format( "%.3f", getStation() ); //$NON-NLS-1$

    final HashCodeBuilder builder = new HashCodeBuilder();
    builder.append( station );

    return builder.toHashCode();
  }

  public GM_Point getPosition( final double breite ) throws Exception
  {
    return WspmProfileHelper.getGeoPosition( breite, m_profile );
  }

  public Coordinate getJtsPosition( final double breite ) throws Exception
  {
    final GM_Point point = getPosition( breite );
    final Point p = (Point) JTSAdapter.export( point );

    return p.getCoordinate();
  }

  public void remove( final IProfileRecord... remove )
  {
    for( final IProfileRecord record : remove )
    {
      m_profile.removePoint( record );
    }
  }

  public double findLowestHeight( )
  {
    final IProfileRecord point = ProfileVisitors.findLowestPoint( m_profile );
    return point.getHoehe();

  }

  public double getWidth( final Point point ) throws GM_Exception
  {
    // TODO: dangerous: widht/rw/hw are not alwayws related!
    // TODO: maybe delegate to WspProfileHelper#getWidthPosition
    final double jtsDistance = JTSUtilities.pointDistanceOnLine( getGeometry(), point );
    final double width = getProfile().getFirstPoint().getBreite() + jtsDistance;

    return width;
  }

  public IProfilPointMarker[] getPointMarkers( final IProfileRecord point )
  {
    final IProfilPointMarker[] markers = m_profile.getPointMarkerFor( point );
    if( ArrayUtils.isEmpty( markers ) )
      return new IProfilPointMarker[] {};

    final List<IProfilPointMarker> myMarkers = new ArrayList<IProfilPointMarker>();

    for( final IProfilPointMarker marker : markers )
    {
      myMarkers.add( marker );
    }

    return myMarkers.toArray( new IProfilPointMarker[] {} );
  }

}
