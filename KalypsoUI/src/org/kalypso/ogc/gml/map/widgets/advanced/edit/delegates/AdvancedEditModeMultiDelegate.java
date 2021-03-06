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
package org.kalypso.ogc.gml.map.widgets.advanced.edit.delegates;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.jts.JtsVectorUtilities;
import org.kalypso.ogc.gml.map.widgets.advanced.edit.AdvancedEditWidgetSnapper;
import org.kalypso.ogc.gml.map.widgets.advanced.edit.IAdvancedEditWidget;
import org.kalypso.ogc.gml.map.widgets.advanced.edit.IAdvancedEditWidgetDataProvider;
import org.kalypso.ogc.gml.map.widgets.advanced.edit.IAdvancedEditWidgetSnappedPoint;
import org.kalypso.ogc.gml.map.widgets.advanced.utils.GeometryPainter;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.model.geometry.JTSAdapter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Dirk Kuch
 */
public class AdvancedEditModeMultiDelegate extends AbstractAdvancedEditModeMovementDelegate
{
  public AdvancedEditModeMultiDelegate( final IAdvancedEditWidget widget, final IAdvancedEditWidgetDataProvider provider )
  {
    super( widget, provider );
  }

  @Override
  public void paint( final Graphics g )
  {
    final GM_Point gmp = getWidget().getCurrentGmPoint();
    if( gmp == null )
      return;
    try
    {
      final Point jtsPoint = (Point)JTSAdapter.export( gmp );
      final IAdvancedEditWidgetSnappedPoint[] snappedPoints;

      if( getWidget().isLeftMouseButtonPressed() )
      {
        snappedPoints = getWidget().getSnappedPointsAtOrigin();
      }
      else
      {
        final Feature[] features = getProvider().query( gmp, 20 );
        if( ArrayUtils.isEmpty( features ) )
          return;

        /* find snap points */
        final Map<Geometry, Feature> mapGeometries = getProvider().resolveJtsGeometries( features );
        GeometryPainter.highlightPoints( g, getWidget().getIMapPanel(), mapGeometries.keySet().toArray( new Geometry[] {} ), VERTEX );

        snappedPoints = AdvancedEditWidgetSnapper.findSnapPoints( mapGeometries, jtsPoint, getRange() );
      }

      /* highlight snap points */
      if( !ArrayUtils.isEmpty( snappedPoints ) )
      {
        final Set<Point> snapped = new LinkedHashSet<>();
        for( final IAdvancedEditWidgetSnappedPoint p : snappedPoints )
        {
          snapped.add( p.getPoint() );
        }

        GeometryPainter.highlightPoints( g, getWidget().getIMapPanel(), snapped.toArray( new Geometry[] {} ), SNAP );
      }

      // drag & drop symbolization
      if( getWidget().getOriginPoint() != null )
      {
        final Point vector = JtsVectorUtilities.getVector( getWidget().getOriginPoint(), jtsPoint );
        final Map<Feature, IAdvancedEditWidgetSnappedPoint[]> snapped = resolveSnappedPoints();

        /* highlight moved snap points */
        final Set<Point> moved = new HashSet<>();

        final Set<Entry<Feature, IAdvancedEditWidgetSnappedPoint[]>> entrySet = snapped.entrySet();
        for( final Entry<Feature, IAdvancedEditWidgetSnappedPoint[]> entry : entrySet )
        {
          final IAdvancedEditWidgetSnappedPoint[] points = entry.getValue();
          for( final IAdvancedEditWidgetSnappedPoint p : points )
          {
            moved.add( p.getMovedPoint( vector ) );
          }
        }

        GeometryPainter.highlightPoints( g, getWidget().getIMapPanel(), moved.toArray( new Geometry[] {} ), MOVED_SNAP_POINT );

        /* display new geometry */
        displayUpdateGeometry( g, snapped, vector );
      }

    }
    catch( final GM_Exception e )
    {
      KalypsoCorePlugin.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
    }

  }

  private Map<Feature, IAdvancedEditWidgetSnappedPoint[]> resolveSnappedPoints( )
  {
    final Map<Feature, IAdvancedEditWidgetSnappedPoint[]> map = new HashMap<>();

    for( final IAdvancedEditWidgetSnappedPoint point : getWidget().getSnappedPointsAtOrigin() )
    {
      final Feature feature = point.getFeature();

      final IAdvancedEditWidgetSnappedPoint[] points = map.get( feature );
      if( points == null )
      {
        map.put( feature, new IAdvancedEditWidgetSnappedPoint[] { point } );
      }
      else
      {
        map.put( feature, ArrayUtils.add( points, point ) );
      }
    }

    return map;
  }

  /**
   * @see org.kalypso.ogc.gml.widgets.aew.IAdvancedEditWidgetDelegate#getToolTip()
   */
  @Override
  public String getToolTip( )
  {
    return Messages.getString( "org.kalypso.ogc.gml.map.widgets.advanced.edit.delegates.AdvancedEditModeMultiDelegate.0" ); //$NON-NLS-1$
  }

  @Override
  public double getRange( )
  {
    final double width = getWidget().getIMapPanel().getBoundingBox().getWidth();
    final double factor = width / 10;

    return factor;
  }
}
