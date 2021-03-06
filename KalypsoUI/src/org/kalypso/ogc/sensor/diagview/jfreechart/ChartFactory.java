/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and
 
 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de
 
 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ogc.sensor.diagview.jfreechart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.Plot;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.diagview.DiagView;

/**
 * Utility methods for working with IObservation and the JFreeChart
 * 
 * @author schlienger
 */
public final class ChartFactory
{
  /**
   * Create a Plot that fits the given DiagView
   */
  static Plot createObservationPlot( final DiagView template ) throws SensorException
  {
    // nonsense
    synchronized( template )
    {
      final ObservationPlot plot = new ObservationPlot( template );

      return plot;
    }
  }

  /**
   * Create a ChartPanel over the given ObservationChart. The created panel has no popup menu.
   * <p>
   * Takes care of registering the ChartPanel within the ObservationChart so that the chart can refer to it once print is called for instance.
   */
  public static ChartPanel createChartPanel( final ObservationChart obsChart )
  {
    final ChartPanel chartPanel = new ChartPanel( obsChart, false, false, false, false, false );
    chartPanel.setMouseZoomable( true, false );

    obsChart.setPanel( chartPanel );

    return chartPanel;
  }
}