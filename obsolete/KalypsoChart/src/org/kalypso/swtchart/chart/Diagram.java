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
package org.kalypso.swtchart.chart;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.kalypso.swtchart.chart.layer.IChartLayer;
import org.kalypso.swtchart.chart.legend.Legend;
import org.kalypso.swtchart.configuration.ChartLoader;
import org.kalypso.swtchart.configuration.ConfigurationLoader;
import org.kalypso.swtchart.exception.ConfigChartNotFoundException;
import org.kalypso.swtchart.preferences.ChartPreferences;
import org.kalypso.swtchart.preferences.IChartPreferences;

/**
 * @author alibu
 */
public class Diagram extends Composite implements Listener
{
  private Chart m_chart;

  private Legend m_legend;

  public Diagram( Composite parent, int style ) throws JAXBException, ConfigChartNotFoundException
  {
    super( parent, style );
    addListener( SWT.Resize, this );
    this.setLayout( new FormLayout() );

    m_chart = new Chart( this, SWT.BORDER );

    IEclipsePreferences prefs = new ChartPreferences().getPreferences();

    String configPath = prefs.get( IChartPreferences.CONFIG_PATH, "" );
    String configName = prefs.get( IChartPreferences.CONFIG_NAME, "" );

    String configFullPath = configPath + configName;

    ConfigurationLoader cl = new ConfigurationLoader( configFullPath );
    ChartLoader.createChart( m_chart, cl.getConfiguration(), "WasserstandEtc", null );
    // ChartLoader.createChart(m_chart, cl.getConfiguration() , "Test");
    m_chart.setAutoscale( false );

    m_legend = new Legend( this, SWT.BORDER );
    List<IChartLayer> layers = m_chart.getLayers();
    for( IChartLayer layer : layers )
    {
      m_legend.addLayer( layer );
    }
    Point legendSize = m_legend.computeSize( 0, 0 );

    FormData fd_chart = new FormData();
    fd_chart.left = new FormAttachment( 0, 10 );
    fd_chart.right = new FormAttachment( m_legend, -5, SWT.LEFT );
    fd_chart.top = new FormAttachment( 0, 10 );
    fd_chart.bottom = new FormAttachment( 100, -10 );
    m_chart.setLayoutData( fd_chart );

    FormData fd_legend = new FormData();
    fd_legend.left = new FormAttachment( 100, -legendSize.x - 5 );
    fd_legend.right = new FormAttachment( 100, -10 );
    fd_legend.top = new FormAttachment( 100, -10 - legendSize.y );
    fd_legend.bottom = new FormAttachment( 100, -10 );
    m_legend.setLayoutData( fd_legend );

    layout();

  }

  public Chart getChart( )
  {
    return m_chart;
  }

  /**
   * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
   */
  public void handleEvent( Event event )
  {
    // TODO Auto-generated method stub
    layout();
  }

}
