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
package org.kalypso.model.wspm.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kalypso.model.wspm.core.IWspmPointProperties;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.core.profil.IProfilePointMarker;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.TupleResultUtilities;
import org.kalypso.ogc.sensor.timeseries.TimeseriesUtils;
import org.kalypso.transformation.transformer.GeoTransformerFactory;
import org.kalypso.transformation.transformer.IGeoTransformer;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * @author Gernot Belger
 */
public final class WspmGeometryUtilities
{
  private WspmGeometryUtilities( )
  {

  }

  // TODO: this is a general transformer to the Kalypso default crs; should be moved to a central place
  public static IGeoTransformer GEO_TRANSFORMER;

  static
  {
    try
    {
      final String targetCRS = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
      GEO_TRANSFORMER = GeoTransformerFactory.getGeoTransformer( targetCRS );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  public static GM_Curve createProfileSegment( final IProfile profil )
  {
    return createProfileSegment( profil, profil.getSrsName() );
  }

  /**
   * @deprecated use {@link #createProfileSegment(IProfil)}
   */
  @Deprecated
  public static GM_Curve createProfileSegment( final IProfile profil, final String srsName )
  {
    return createProfileSegment( profil, srsName, null );
  }

  public static GM_Curve createProfileSegment( final IProfile profil, String srsName, final String pointMarkerName )
  {
    try
    {
      final IRecord[] points = profil.getPoints();
      final List<GM_Position> positions = new ArrayList<>( points.length );

      final int compRechtswert = TupleResultUtilities.indexOfComponent( profil, IWspmPointProperties.POINT_PROPERTY_RECHTSWERT );
      final int compHochwert = TupleResultUtilities.indexOfComponent( profil, IWspmPointProperties.POINT_PROPERTY_HOCHWERT );
      final int compBreite = TupleResultUtilities.indexOfComponent( profil, IWspmPointProperties.POINT_PROPERTY_BREITE );
      final int compHoehe = TupleResultUtilities.indexOfComponent( profil, IWspmPointProperties.POINT_PROPERTY_HOEHE );

      int left = 0;
      int right = points.length;

      if( pointMarkerName != null )
      {
        final IProfilePointMarker[] durchstroemte = profil.getPointMarkerFor( pointMarkerName );

        if( durchstroemte.length < 2 )
          return null;

        left = durchstroemte[0].getPoint().getIndex();
        right = durchstroemte[1].getPoint().getIndex();
      }

      // for( final IRecord point : points )
      for( int i = left; i < right; i++ )
      {
        final IRecord point = points[i];
        /* If there are no rw/hw create pseudo geometries from breite and station */
        final Double rw;
        final Double hw;

        if( compRechtswert != -1 && compHochwert != -1 )
        {
          rw = (Double) point.getValue( compRechtswert );
          hw = (Double) point.getValue( compHochwert );

          /* We assume here that we have a GAUSS-KRUEGER crs in a profile. */
          if( StringUtils.isBlank( srsName ) && rw != null )
          {
            srsName = TimeseriesUtils.getCoordinateSystemNameForGkr( Double.toString( rw ) );
          }
        }
        else
        {
          if( compBreite == -1 )
            throw new IllegalStateException( "Cross sections without width or easting/northing attributes detected - geometric processing not possible." ); //$NON-NLS-1$

          rw = (Double) point.getValue( compBreite );
          hw = profil.getStation() * 1000;

          /* We assume here that we have a GAUSS-KRUEGER crs in a profile. */
          if( srsName == null )
          {
            srsName = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
          }
        }

        final Double h = compHoehe == -1 ? null : (Double) point.getValue( compHoehe );

        final GM_Position position = createPosition( rw, hw, h );
        if( position != null )
          positions.add( position );
      }

      if( positions.size() < 2 )
        return null;

      final GM_Position[] poses = positions.toArray( new GM_Position[positions.size()] );
      final GM_Curve curve = GeometryFactory.createGM_Curve( poses, srsName );

      return (GM_Curve) WspmGeometryUtilities.GEO_TRANSFORMER.transform( curve );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }

    return null;
  }

  private static GM_Position createPosition( final Double rw, final Double hw, final Double h )
  {
    if( rw == null || hw == null )
      return null;

    if( rw.isNaN() || hw.isNaN() )
      return null;

    if( h == null )
      return GeometryFactory.createGM_Position( rw, hw );

    return GeometryFactory.createGM_Position( rw, hw, h );
  }

  public static GM_Point createLocation( final IProfile profil, final IRecord point, final String srsName )
  {
    return createLocation( profil, point, srsName, IWspmPointProperties.POINT_PROPERTY_HOEHE );
  }

  public static GM_Point createLocation( final IProfile profil, final IRecord point, String srsName, final String heightComponentID )
  {
    try
    {
      final int compRechtswert = TupleResultUtilities.indexOfComponent( profil, IWspmPointProperties.POINT_PROPERTY_RECHTSWERT );
      final int compHochwert = TupleResultUtilities.indexOfComponent( profil, IWspmPointProperties.POINT_PROPERTY_HOCHWERT );
      final int compBreite = TupleResultUtilities.indexOfComponent( profil, IWspmPointProperties.POINT_PROPERTY_BREITE );
      final int compHoehe = TupleResultUtilities.indexOfComponent( profil, heightComponentID );

      /* If there are no rw/hw create pseudo geometries from breite and station */
      final Double rw;
      final Double hw;

      if( compRechtswert != -1 && compHochwert != -1 )
      {
        rw = (Double) point.getValue( compRechtswert );
        hw = (Double) point.getValue( compHochwert );

        /* We assume here that we have a GAUSS-KRUEGER crs in a profile. */
        if( StringUtils.isBlank( srsName ) && rw != null )
        {
          srsName = TimeseriesUtils.getCoordinateSystemNameForGkr( Double.toString( rw ) );
        }
      }
      else
      {
        if( compBreite == -1 )
          throw new IllegalStateException( "Cross sections without width or easting/northing attributes detected - geometric processing not possible." ); //$NON-NLS-1$

        rw = (Double) point.getValue( compBreite );
        hw = profil.getStation() * 1000;

        /* We assume here that we have a GAUSS-KRUEGER crs in a profile. */
        if( srsName == null )
        {
          srsName = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
        }
      }

      final Double h = compHoehe == -1 ? null : (Double) point.getValue( compHoehe );

      final GM_Position position = createPosition( rw, hw, h );
      if( position == null )
        return null;

      return GeometryFactory.createGM_Point( position, srsName );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Creates a {@link GM_Point} from rw/hw information.
   * <p>
   * We assume that rw/hw are in a GAUSS-KRUEGER coordinate system, the exact one is determined from the first digit of
   * the rw value.
   */
  public static GM_Point pointFromRwHw( final double rw, final double hw, final double h ) throws Exception
  {
    // TODO: should not be used?
    final String crsName = null;

    return pointFromRwHw( rw, hw, h, crsName, GEO_TRANSFORMER );
  }

  public static GM_Point pointFromRwHw( final double rw, final double hw, final double h, String crsName, final IGeoTransformer transformer ) throws Exception
  {
    final GM_Position position = GeometryFactory.createGM_Position( rw, hw, h );

    /* If CRS is not known, we assume here that we have a GAUSS-KRUEGER crs in a profile. */
    if( StringUtils.isBlank( crsName ) )
    {
      crsName = TimeseriesUtils.getCoordinateSystemNameForGkr( Double.toString( rw ) );
    }

    final GM_Point point = GeometryFactory.createGM_Point( position, crsName );
    if( transformer != null )
      return (GM_Point) transformer.transform( point );

    return point;
  }

  public static GM_Point pointFromPoint( final GM_Point inPoint, final String crsName ) throws Exception
  {
    final GM_Position position = inPoint.getPosition();

    return pointFromRwHw( position.getX(), position.getY(), position.getZ(), crsName, GEO_TRANSFORMER );
  }
}