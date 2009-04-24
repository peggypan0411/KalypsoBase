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
package org.kalypso.ogc.gml.map.widgets.advanced.selection.delegates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.Assert;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.ogc.gml.map.utilities.MapUtilities;
import org.kalypso.ogc.gml.map.widgets.advanced.selection.AbstractAdvancedSelectionWidgetDelegate;
import org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidget;
import org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidgetDataProvider;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree.model.geometry.GM_Ring;
import org.kalypsodeegree.model.geometry.GM_Surface;
import org.kalypsodeegree.model.geometry.GM_SurfacePatch;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * @author Dirk Kuch
 */
public class RectanglePolygonDelegate extends AbstractAdvancedSelectionWidgetDelegate
{
  public RectanglePolygonDelegate( final IAdvancedSelectionWidget widget, final IAdvancedSelectionWidgetDataProvider provider )
  {
    super( widget, provider );
  }

  /**
   * @see org.kalypso.planer.client.ui.gui.widgets.measures.aw.IAdvancedSelectionWidgetDelegate#leftReleased(java.awt.Point)
   */
  @Override
  public void leftReleased( final Point p )
  {
    try
    {
      final GM_Point base = MapUtilities.transform( getWidget().getIMapPanel(), getMousePressed() );
      final GM_Point current = getWidget().getCurrentGmPoint();
      Assert.isTrue( base.getCoordinateSystem() == current.getCoordinateSystem() );

      final GM_Envelope envelop = GeometryFactory.createGM_Envelope( base.getPosition(), current.getPosition(), base.getCoordinateSystem() );

      final Feature[] features = getDataProvider().query( envelop );
      getDataProvider().post( features, getWidget().getEditMode() );
    }
    catch( final Exception e )
    {
      KalypsoCorePlugin.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
    }
    
    super.leftReleased( p );
  }

  /**
   * @see org.kalypso.ogc.gml.widgets.selection.AbstractAdvancedSelectionWidgetDelegate#paint(java.awt.Graphics)
   */
  @Override
  public void paint( final Graphics g )
  {
    if( isMouseButtonPressed() == false )
      return;

    /* draw rectangle */
    final Point base = getMousePressed();
    final Point current = getWidget().getCurrentPoint();

    g.drawRect( base.x, base.y, current.x - base.x, current.y - base.y );

    try
    {
      final GM_Point gmBase = MapUtilities.transform( getWidget().getIMapPanel(), base );
      final GM_Point gmCurrent = getWidget().getCurrentGmPoint();
      Assert.isTrue( gmBase.getCoordinateSystem() == gmCurrent.getCoordinateSystem() );

      final GM_Envelope envelop = GeometryFactory.createGM_Envelope( gmBase.getPosition(), gmCurrent.getPosition(), gmBase.getCoordinateSystem() );

      final Feature[] features = getDataProvider().query( envelop );
      for( final Feature feature : features )
      {
        highlightUnderlying( feature, g );
      }
    }
    catch( final Exception e )
    {
      KalypsoCorePlugin.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
    }
  }

  /**
   * @see org.kalypso.planer.client.ui.gui.widgets.measures.aw.AbstractAdvancedSelectionWidgetDelegate#highlightUnderlying(org.kalypsodeegree.model.feature.Feature,
   *      java.awt.Graphics)
   */
  @Override
  protected void highlightUnderlying( final Feature feature, final Graphics g )
  {
    final GM_Surface<GM_SurfacePatch> surface = (GM_Surface<GM_SurfacePatch>) getDataProvider().resolveGeometry( feature );

    final Color originalColor = g.getColor();
    g.setColor( new Color( 0, 255, 0, 128 ) );

    final GM_Ring ring = surface.getSurfaceBoundary().getExteriorRing();
    final GM_Position[] positions = ring.getPositions();

    int[] x_positions = new int[] {};
    int[] y_positions = new int[] {};

    for( final GM_Position position : positions )
    {
      final Point awt = MapUtilities.retransform( getWidget().getIMapPanel(), position );
      x_positions = ArrayUtils.add( x_positions, Double.valueOf( awt.getX() ).intValue() );
      y_positions = ArrayUtils.add( y_positions, Double.valueOf( awt.getY() ).intValue() );
    }

    Assert.isTrue( x_positions.length == y_positions.length );
    g.fillPolygon( x_positions, y_positions, x_positions.length );

    g.setColor( originalColor );

  }

}
