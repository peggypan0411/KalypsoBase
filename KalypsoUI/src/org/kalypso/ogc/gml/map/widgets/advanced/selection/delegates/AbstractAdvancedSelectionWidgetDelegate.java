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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidget;
import org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidget.EDIT_MODE;
import org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidgetDataProvider;
import org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidgetDelegate;
import org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidgetGeometryProvider;
import org.kalypsodeegree.graphics.displayelements.DisplayElement;
import org.kalypsodeegree.graphics.sld.Symbolizer;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.xml.XMLTools;
import org.kalypsodeegree_impl.graphics.displayelements.DisplayElementFactory;
import org.kalypsodeegree_impl.graphics.sld.SLDFactory;
import org.kalypsodeegree_impl.model.geometry.JTSAdapter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Dirk Kuch
 */
public abstract class AbstractAdvancedSelectionWidgetDelegate implements IAdvancedSelectionWidgetDelegate
{
  static Document SLD_ADD;

  static Document SLD_REMOVE;

  private final IAdvancedSelectionWidget m_widget;

  private final IAdvancedSelectionWidgetDataProvider m_dataProvider;

  private Point m_pressed = null;

  private final IAdvancedSelectionWidgetGeometryProvider m_geometryProvider;

  public AbstractAdvancedSelectionWidgetDelegate( final IAdvancedSelectionWidget widget, final IAdvancedSelectionWidgetDataProvider dataProvider, final IAdvancedSelectionWidgetGeometryProvider geometryProvider )
  {
    m_widget = widget;
    m_geometryProvider = geometryProvider;
    m_dataProvider = dataProvider;
  }

  protected IAdvancedSelectionWidget getWidget( )
  {
    return m_widget;
  }

  protected IAdvancedSelectionWidgetDataProvider getDataProvider( )
  {
    return m_dataProvider;
  }

  protected IAdvancedSelectionWidgetGeometryProvider getGeometryProvider( )
  {
    return m_geometryProvider;
  }

  /**
   * @see org.kalypso.planer.client.ui.gui.widgets.measures.aw.IAdvancedSelectionWidgetDelegate#paint(java.awt.Graphics)
   */
  @Override
  public void paint( final Graphics g )
  {
    try
    {
      // underlying features
      final GM_Point point = m_widget.getCurrentGmPoint();
      final EDIT_MODE mode = getEditMode();

      final Feature[] features = m_dataProvider.query( getSurface( point ), mode );
      if( features.length > 0 )
        highlightUnderlyingGeometries( features, g, mode );

    }
    catch( final GM_Exception e )
    {
      KalypsoCorePlugin.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
    }
  }

  protected GM_Polygon getSurface( final GM_Point point ) throws GM_Exception
  {
    if( point == null )
      return null;

    final Geometry geometry = JTSAdapter.export( point );
    final Geometry buffer = geometry.buffer( 0.1 );

    return (GM_Polygon)JTSAdapter.wrap( buffer );
  }

  protected void highlightUnderlyingGeometries( final Feature[] features, final Graphics g, final EDIT_MODE mode )
  {
    if( ArrayUtils.isEmpty( features ) )
      return;

    try
    {
      final Document document = getDocument( mode );
      final Symbolizer symbolizer = SLDFactory.createSymbolizer( null, document.getDocumentElement() );
      final GeoTransform projection = getWidget().getIMapPanel().getProjection();

      for( final Feature feature : features )
      {
        final DisplayElement lde = DisplayElementFactory.buildDisplayElement( feature, symbolizer, null );
        lde.paint( g, projection, new NullProgressMonitor() );
      }
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  private Document getDocument( final EDIT_MODE mode ) throws IOException, SAXException
  {
    if( EDIT_MODE.eAdd.equals( mode ) )
    {
      if( SLD_ADD == null )
        SLD_ADD = XMLTools.parse( AbstractAdvancedSelectionWidgetDelegate.class.getResource( "slds/add.sld" ) ); //$NON-NLS-1$

      return SLD_ADD;

    }
    else if( EDIT_MODE.eRemove.equals( mode ) )
    {
      if( SLD_REMOVE == null )
        SLD_REMOVE = XMLTools.parse( AbstractAdvancedSelectionWidgetDelegate.class.getResource( "slds/remove.sld" ) ); //$NON-NLS-1$

      return SLD_REMOVE;
    }

    return null;
  }

  /**
   * @see org.kalypso.ogc.gml.widgets.selection.IAdvancedSelectionWidgetDelegate#leftPressed(java.awt.Point)
   */
  @Override
  public void leftPressed( final Point p )
  {
    m_pressed = p;
  }

  /**
   * @see org.kalypso.ogc.gml.widgets.selection.IAdvancedSelectionWidgetDelegate#leftReleased(java.awt.Point)
   */
  @Override
  public void leftReleased( final Point p )
  {
    m_pressed = null;
  }

  /**
   * @see org.kalypso.ogc.gml.widgets.selection.IAdvancedSelectionWidgetDelegate#isMouseButtonPressed()
   */
  @Override
  public boolean isMouseButtonPressed( )
  {
    if( m_pressed == null )
      return false;

    return true;
  }

  /**
   * @see org.kalypso.ogc.gml.widgets.selection.IAdvancedSelectionWidgetDelegate#getMousePressed()
   */
  @Override
  public Point getMousePressed( )
  {
    return m_pressed;
  }

  /**
   * @see org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidgetDelegate#keyReleased(java.awt.event.KeyEvent)
   */
  @Override
  public void keyReleased( final KeyEvent e )
  {
    // nothing to do
  }

  /**
   * @see org.kalypso.ogc.gml.map.widgets.advanced.selection.IAdvancedSelectionWidgetDelegate#doubleClickedLeft(java.awt.Point)
   */
  @Override
  public void doubleClickedLeft( final Point p )
  {
    // nothing to do
  }
}
