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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;
import org.kalypso.contribs.java.lang.CatchRunnable;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.diagview.DiagView;
import org.kalypso.ogc.sensor.diagview.DiagViewCurve;
import org.kalypso.ogc.sensor.template.IObsViewEventListener;
import org.kalypso.ogc.sensor.template.ObsViewEvent;
import org.kalypso.ogc.sensor.template.SwingEclipseUtilities;
import org.kalypso.template.obsdiagview.Alignment;
import org.kalypso.template.obsdiagview.FontWeight;
import org.kalypso.template.obsdiagview.Obsdiagview.TitleFormat;

/**
 * @author schlienger
 */
public class ObservationChart extends JFreeChart implements IObsViewEventListener
{
  protected static final Logger LOGGER = Logger.getLogger( ObservationChart.class.getName() );

  private final StandardLegend m_legend = new StandardLegend();

  private final DiagView m_view;

  private final boolean m_waitForSwing;

  private ChartPanel m_chartPanel = null;

  public ObservationChart( final DiagView template ) throws SensorException
  {
    this( template, false );
  }

  /**
   * Creates an ObservationChart
   * 
   * @param waitForSwing
   *          when true, the events are handled synchonuously in onObsviewChanged(), this is usefull when you are
   *          creating the diagram for non-gui purposes such as in the export-document-wizard: there you need to wait
   *          for swing to be finished with updating/painting the diagram before doing the export, else you get strange
   *          results
   */
  public ObservationChart( final DiagView template, final boolean waitForSwing ) throws SensorException
  {
    super( null, null, ChartFactory.createObservationPlot( template ), false );

    setTitle( getTitle( template ) );

    m_view = template;
    m_waitForSwing = waitForSwing;

    setLegendProperties( template.getLegendName(), template.isShowLegend() );

    // removed in this.dispose()
    m_view.addObsViewEventListener( this );

    // good for the eyes
    setBackgroundPaint( new GradientPaint( 0, 0, Color.white, 0, 1000, new Color( 168, 168, 255 ) ) );
  }

  private TextTitle getTitle( final DiagView template )
  {
    final TitleFormat format = template.getTitleFormat();

    final int size = format.getFontSize();
    final FontWeight weight = format.getFontWeight();

    Font font = null;
    if( FontWeight.BOLD.equals( weight ) )
      font = new Font( format.getFontFamily(), Font.BOLD, size );
    else if( FontWeight.NORMAL.equals( weight ) )
      font = new Font( format.getFontFamily(), Font.PLAIN, size );
    else if( FontWeight.ITALIC.equals( weight ) )
      font = new Font( format.getFontFamily(), Font.ITALIC, size );

    final Alignment alignment = format.getAlignment();
    if( Alignment.CENTER.equals( alignment ) )
      return new TextTitle( template.getTitle(), font, HorizontalAlignment.CENTER );
    else if( Alignment.LEFT.equals( alignment ) )
      return new TextTitle( template.getTitle(), font, HorizontalAlignment.LEFT );
    else if( Alignment.RIGHT.equals( alignment ) )
      return new TextTitle( template.getTitle(), font, HorizontalAlignment.RIGHT );

    return new TextTitle( template.getTitle(), font, HorizontalAlignment.CENTER );
  }

  protected void setLegendProperties( final String legendName, final boolean showLegend )
  {
    m_legend.setTitle( legendName );
// m_legend.setDisplaySeriesLines( true );
    m_legend.setDisplaySeriesShapes( false );
    m_legend.setOutlineStroke( new BasicStroke( 0.0f ) );
    m_legend.setOutlinePaint( Color.white );

    if( showLegend )
      setLegend( m_legend );
    else
      setLegend( null );
  }

  /**
   * Disposes the chart.
   */
  public void dispose( )
  {
    m_view.removeObsViewListener( this );

    m_chartPanel = null;

    clearChart();
  }

  /**
   * Clears the curves in the chart
   */
  public void clearChart( )
  {
    getObservationPlot().clearCurves();
  }

  /**
   * @return plot casted as obs plot
   */
  public ObservationPlot getObservationPlot( )
  {
    return (ObservationPlot)getPlot();
  }

  public DiagView getTemplate( )
  {
    return m_view;
  }

  /**
   * Set the ChartPanel, this is useful if print should be called in the near future
   */
  protected void setPanel( final ChartPanel chartPanel )
  {
    m_chartPanel = chartPanel;
  }

  /**
   * @see org.kalypso.ogc.sensor.template.IObsViewEventListener#onObsViewChanged(org.kalypso.ogc.sensor.template.ObsViewEvent)
   */
  @Override
  public void onObsViewChanged( final ObsViewEvent evt )
  {
    final CatchRunnable runnable = new CatchRunnable()
    {
      @Override
      protected void runIntern( ) throws Throwable
      {
        final ObservationPlot obsPlot = getObservationPlot();

        DiagView view = null;
        if( evt.getObject() instanceof DiagView )
          view = (DiagView)evt.getObject();
        else if( evt.getObject() instanceof DiagViewCurve )
          view = (DiagView)((DiagViewCurve)evt.getObject()).getView();

        final int et = evt.getType();

        switch( et )
        {
          case ObsViewEvent.TYPE_ITEM_ADD:
          {
            obsPlot.addCurve( (DiagViewCurve)evt.getObject() );
            break;
          }

          case ObsViewEvent.TYPE_ITEM_REMOVE:
          {
            obsPlot.removeCurve( (DiagViewCurve)evt.getObject() );
            break;
          }

          case ObsViewEvent.TYPE_ITEM_REMOVE_ALL:
          {
            clearChart();
            break;
          }

          case ObsViewEvent.TYPE_ITEM_DATA_CHANGED:
          case ObsViewEvent.TYPE_ITEM_STATE_CHANGED:
          {
            final DiagViewCurve curve = (DiagViewCurve)evt.getObject();
            obsPlot.removeCurve( curve );
            if( curve.isShown() )
              obsPlot.addCurve( curve );
            break;
          }

          case ObsViewEvent.TYPE_VIEW_CHANGED:
          {
            setTitle( view.getTitle() );
            setLegendProperties( view.getLegendName(), view.isShowLegend() );

            final TimeZone timezone = view.getTimezone();
            final TimeZone plotTimezone = timezone == null ? KalypsoCorePlugin.getDefault().getTimeZone() : timezone;
            obsPlot.setTimezone( plotTimezone );
            break;
          }

          case ObsViewEvent.TYPE_FEATURES_CHANGED:
          {
            obsPlot.refreshMetaInformation();
            break;
          }
        }
      }
    };

    SwingEclipseUtilities.invokeAndHandleError( runnable, m_waitForSwing );
  }

  /**
   * @see org.kalypso.ogc.sensor.template.IObsViewEventListener#onPrintObsView(org.kalypso.ogc.sensor.template.ObsViewEvent)
   */
  @Override
  public final void onPrintObsView( final ObsViewEvent evt )
  {
    // use the ChartPanel to print
    final ChartPanel chartPanel = m_chartPanel;
    if( chartPanel != null )
    {
      final CatchRunnable runnable = new CatchRunnable()
      {
        @Override
        protected void runIntern( ) throws Throwable
        {
          chartPanel.createChartPrintJob();
        }
      };

      SwingEclipseUtilities.invokeAndHandleError( runnable, false );
    }
  }
}