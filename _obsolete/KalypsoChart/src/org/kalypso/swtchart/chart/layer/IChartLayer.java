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
package org.kalypso.swtchart.chart.layer;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.kalypso.contribs.eclipse.swt.graphics.GCWrapper;
import org.kalypso.swtchart.chart.axis.IAxis;
import org.kalypso.swtchart.chart.legend.ILegendItem;
import org.kalypso.swtchart.chart.styles.ILayerStyle;

/**
 * @author schlienger
 * @author burtscher an IChartLayer represents a (visual) layer of the chart; it can be assigned to up to 2 axes to
 *         translate logical data into screen values
 */
public interface IChartLayer
{
  // TODO: add get/set ID: this is needed to get the back-reference where this laer came from (for examlpe from the LayerType)
  
  /**
   * sets the layers name (which will be shown in the legend)
   */
  public void setName( final String name );

  /**
   * @return the layers name
   */
  public String getName( );

  /**
   * sets a description for the layer
   */
  public void setDescription( String description );

  /**
   * @return the layers description
   */
  public String getDescription( );

  /**
   * @return the IAxis object used with domain data
   */
  public IAxis getDomainAxis( );

  /**
   * @return DataRange object describing minimal and maximal domain values
   */
  public IDataRange getDomainRange( );

  /**
   * @return the IAxis object used with value data
   */
  public IAxis getValueAxis( );

  /**
   * @return DataRange object describing minimal and maximal target values (to remain inside the elsewise used
   *         vocabulary: the "target values" should be refered to as "value values" which sounds a bit strange...)
   */
  public IDataRange getValueRange( );

  /**
   * TODO: remove this from IChartLayer interface ILegendItem should be constructed upon layers, client should always
   * use drawIcon and getName only the legend should know about legend-items
   * 
   * @return ILegendItem which describes the Layers content and style
   * @deprecated
   */
  @Deprecated
  public ILegendItem getLegendItem( );

  /**
   * draws a picture of a given size into the given Image; the icon will be used for the legend to describe the layers
   * visual representation
   * TODO: remove widht and height parameters!
   * Use img.getBounds instead
   */
  public void drawIcon( final Image img, final int width, final int height );

  /**
   * @return true if the layer is set active, false otherwise
   */
  public boolean isActive( );

  /**
   * @return the layer style
   */
  public ILayerStyle getStyle( );

  /**
   * sets the layer's style
   */
  public void setStyle( final ILayerStyle style );

  /**
   * draws the layer using the given GCWrapper and Device
   */
  public void paint( final GCWrapper gc, final Device dev );

  public void setVisibility( final boolean isVisible );

  public boolean getVisibility( );
}
