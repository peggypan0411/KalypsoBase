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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.Spacer;
import org.jfree.ui.TextAnchor;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.MetadataList;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.diagview.IAxisMapping;
import org.kalypso.ogc.sensor.diagview.IDiagramAxis;
import org.kalypso.ogc.sensor.diagview.IDiagramCurve;
import org.kalypso.ogc.sensor.diagview.IDiagramTemplate;
import org.kalypso.ogc.sensor.timeseries.TimeserieConstants;
import org.kalypso.ogc.sensor.timeseries.TimeserieUtils;
import org.kalypso.util.factory.ConfigurableCachableObjectFactory;
import org.kalypso.util.factory.FactoryException;
import org.kalypso.util.runtime.args.DateRangeArgument;

/**
 * A plot for IObservation.
 * 
 * @author schlienger
 */
public class ObservationPlot extends XYPlot
{
  private static final ConfigurableCachableObjectFactory OF;

  /** default line renderer */
  private final XYItemRenderer LINE_RENDERER = new StandardXYItemRenderer(
      StandardXYItemRenderer.LINES );

  /** default bar renderer */
  private final XYItemRenderer BAR_RENDERER = new XYBarRenderer();

  static
  {
    final Properties props = new Properties();
    try
    {
      props.load( ChartFactory.class
          .getResourceAsStream( "resource/type2valueAxis.properties" ) );
    }
    catch( IOException e )
    {
      e.printStackTrace();
    }

    OF = new ConfigurableCachableObjectFactory( props, false,
        ChartFactory.class.getClassLoader() );
  }

  /** maps the diagram axis (from the template) to the chart axis */
  private transient final Map m_diag2chartAxis;

  /** maps the chart axis to its position in the plot */
  private transient final Map m_chartAxes2Pos;

  /** maps the diagram axes (from the template) to a dataset */
  private transient final Map m_diagAxis2ds = new HashMap();

  /** maps the diagram curve to the data serie */
  private transient final Map m_curve2serie = new HashMap();

  /** maps the series to their datasets */
  private transient final Map m_serie2dataset = new HashMap();

  private int m_domPos = 0;

  private int m_ranPos = 0;

  private transient Map m_yConsts = new HashMap();
  
  private transient Map m_markers = new HashMap();
  
  /**
   * Constructor.
   * 
   * @param template
   * @throws SensorException
   */
  public ObservationPlot( final IDiagramTemplate template )
      throws SensorException
  {
    super();

    // space between axes and data area
    setAxisOffset( new Spacer( Spacer.ABSOLUTE, 5, 5, 5, 5 ) );

    // standard renderer
    setRenderer( LINE_RENDERER );

    final Collection diagAxes = template.getDiagramAxes();

    m_diag2chartAxis = new HashMap( diagAxes.size() );
    m_chartAxes2Pos = new HashMap( diagAxes.size() );

    for( final Iterator it = diagAxes.iterator(); it.hasNext(); )
    {
      final IDiagramAxis diagAxis = (IDiagramAxis) it.next();

      addDiagramAxis( diagAxis );
    }

    final Collection curves = template.getCurves();
    for( Iterator it = curves.iterator(); it.hasNext(); )
    {
      final Object diagramCurve = it.next();
      addCurve( (IDiagramCurve) diagramCurve );
    }

    setNoDataMessage( "Keine Daten vorhanden" );
  }

  public void dispose()
  {
    clearCurves();
  }
  
  /**
   * Adds a diagram axis and configures it for the use in this plot.
   * 
   * @param diagAxis
   * @throws SensorException
   */
  private final void addDiagramAxis( IDiagramAxis diagAxis )
      throws SensorException
  {
    final ValueAxis vAxis;

    try
    {
      vAxis = (ValueAxis) OF.getObjectInstance( diagAxis.getDataType(),
          ValueAxis.class, new Object[] { diagAxis.toFullString() } );
    }
    catch( FactoryException e )
    {
      throw new SensorException( e );
    }

    vAxis.setInverted( diagAxis.isInverted() );
    
    if( diagAxis.getLowerMargin() != null )
      vAxis.setLowerMargin( diagAxis.getLowerMargin().doubleValue() );
    
    if( diagAxis.getUpperMaring() != null )
      vAxis.setUpperMargin( diagAxis.getUpperMaring().doubleValue() );

    AxisLocation loc = getLocation( diagAxis );

    if( diagAxis.getDirection().equals( IDiagramAxis.DIRECTION_HORIZONTAL ) )
    {
      setDomainAxis( m_domPos, vAxis );
      setDomainAxisLocation( m_domPos, loc );

      m_chartAxes2Pos.put( vAxis, new Integer( m_domPos ) );

      m_domPos++;
    }
    else
    {
      setRangeAxis( m_ranPos, vAxis );
      setRangeAxisLocation( m_ranPos, loc );

      m_chartAxes2Pos.put( vAxis, new Integer( m_ranPos ) );

      m_ranPos++;
    }

    m_diag2chartAxis.put( diagAxis, vAxis );
  }

  /**
   * Removes all curves from plot.
   */
  public synchronized void clearCurves( )
  {
    for( int i = 0; i < getDatasetCount(); i++ )
      setDataset( i, null );

    m_serie2dataset.clear();
    m_curve2serie.clear();

    m_diagAxis2ds.clear();

    m_chartAxes2Pos.clear();
    m_diag2chartAxis.clear();
    m_domPos = 0;
    m_ranPos = 0;

    clearDomainMarkers();
    clearAnnotations();
    
    m_yConsts.clear();
    m_markers.clear();
    
    clearDomainAxes();
    clearRangeAxes();
  }

  /**
   * Adds a curve to the plot
   * 
   * @param curve
   * @throws SensorException
   */
  public synchronized void addCurve( final IDiagramCurve curve )
      throws SensorException
  {
    final IAxisMapping[] mings = curve.getMappings();
    IAxis xAxis = null;
    IDiagramAxis xDiagAxis = null;
    IAxis yAxis = null;
    IDiagramAxis yDiagAxis = null;

    for( int i = 0; i < mings.length; i++ )
    {
      final IDiagramAxis diagAxis = mings[i].getDiagramAxis();

      // check if this axis is already present in this plot
      if( !m_diag2chartAxis.containsKey( diagAxis ) )
        addDiagramAxis( diagAxis );

      if( diagAxis.getDirection().equals( IDiagramAxis.DIRECTION_HORIZONTAL ) )
      {
        xAxis = mings[i].getObservationAxis();
        xDiagAxis = diagAxis;
      }
      else
      {
        yAxis = mings[i].getObservationAxis();
        yDiagAxis = diagAxis;
      }
    }

    if( xAxis == null || yAxis == null || xDiagAxis == null
        || yDiagAxis == null )
      throw new IllegalArgumentException( "Kann Kurve " + curve
          + " im Diagramm nicht hinizuf�gen. Die Achsen sind nicht g�ltig." );

    final XYCurveSerie xyc = new XYCurveSerie( curve, xAxis, yAxis, xDiagAxis,
        yDiagAxis );

    m_curve2serie.put( curve, xyc );

//    final String key = xDiagAxis.getIdentifier() + "#-#"
//        + yDiagAxis.getIdentifier();
    final IDiagramAxis key = yDiagAxis;

    CurveDataset cds = (CurveDataset) m_diagAxis2ds.get( key );

    if( cds == null )
    {
      cds = new CurveDataset();

      m_diagAxis2ds.put( key, cds );

      int pos = m_diagAxis2ds.values().size();

      setDataset( pos, cds );
      
      final XYItemRenderer renderer = getRenderer( yAxis.getType() );
      // TODO: verify if pos is valid for use as index of series...
      renderer.setSeriesPaint( pos == 0 ? pos : pos-1, curve.getColor() );
      setRenderer( pos, renderer );

      mapDatasetToDomainAxis( pos, ((Integer) m_chartAxes2Pos
          .get( m_diag2chartAxis.get( xDiagAxis ) )).intValue() );
      mapDatasetToRangeAxis( pos, ((Integer) m_chartAxes2Pos
          .get( m_diag2chartAxis.get( yDiagAxis ) )).intValue() );
    }

    cds.addCurveSerie( xyc );

    m_serie2dataset.put( xyc, cds );

    final IObservation obs = curve.getTheme()
        .getObservation();
    
    // add a marker if the obs is a forecast
    final DateRangeArgument fr = TimeserieUtils.isForecast( obs );
    if( fr != null )
    {
      final Long begin = new Long( fr.getFrom().getTime() );
      if( !m_markers.containsKey( begin) )
      {
        final long end = fr.getTo().getTime();
        final Marker marker = createMarker( begin.doubleValue(), end,
          TimeserieConstants.MD_VORHERSAGE, TimeserieUtils.getColorForMD( TimeserieConstants.MD_VORHERSAGE ) );
        
        addDomainMarker( marker, Layer.BACKGROUND );
        
        m_markers.put( begin, marker );
      }
    }

    // add a constant Y line if obs has alarmstufen
    if( yAxis.getType().equals( TimeserieConstants.TYPE_WATERLEVEL ) )
    {
	    final String[] alarms = TimeserieUtils.findOutMDAlarmLevel( obs );
	    final MetadataList mdl = obs.getMetadataList();
	    for( int i = 0; i < alarms.length; i++ )
	    {
	      final Double value = new Double( mdl.getProperty( alarms[i] ) );
	      if( !m_yConsts.containsKey( value ) )
	      {
	        final Color color = TimeserieUtils.getColorForAlarmLevel( alarms[i] );
	        m_yConsts.put( value, new ValueAndColor( alarms[i] + " (" + value.doubleValue() + ")", value.doubleValue(), color ) );
	        
	        final double x;
	        if( xyc.getItemCount() > 1 )
	          x = xyc.getXValue(1).doubleValue();
	        else
	          x = getDomainAxis().getLowerBound();
	        final XYTextAnnotation ann = new XYTextAnnotation( alarms[i], x, value.doubleValue() );
	        ann.setPaint( color );
	        addAnnotation( ann );
	      }
	    }
    }
  }

  /**
   * Removes the curve from the plot
   * 
   * @param curve
   */
  public synchronized void removeCurve( final IDiagramCurve curve )
  {
    final XYCurveSerie serie = (XYCurveSerie) m_curve2serie.get( curve );
    if( serie != null )
    {
      final CurveDataset ds = (CurveDataset) m_serie2dataset.get( serie );

      if( ds != null )
      {
        ds.removeCurveSerie( serie );
        
        // if dataset is empty, also remove it and the range axis to which it belongs
        if( ds.getSeriesCount() == 0 )
        {
          // and remove the dataset
          for( int i = 0; i < getDatasetCount(); i++ )
          {
            if( getDataset( i ) == ds )
            {
              setDataset( i, null );
              
              break;
            }
          }
          
          // step though axes and remove the one that is associated to
          // the dataset we want to remove
          final Iterator it = m_diagAxis2ds.keySet().iterator();
          while( it.hasNext() )
          {
            final IDiagramAxis dAxis = (IDiagramAxis) it.next();
            if( m_diagAxis2ds.get( dAxis ) == ds )
            {
              final ValueAxis cAxis = (ValueAxis) m_diag2chartAxis.get( dAxis );
              final Integer pos = (Integer) m_chartAxes2Pos.get( cAxis );
              
              // trick: if it is the only axis, then do not remove it
              // else NullPointerException in drawQuadrants (JFreeChart)
              if( getRangeAxis() != getRangeAxis( pos.intValue() ) )
              {
                setRangeAxis( pos.intValue(), null );
              	m_chartAxes2Pos.remove( cAxis );
              	m_diag2chartAxis.remove( dAxis );
              }

              it.remove();
              
              // break, that's it
              break;
            }
          }
        }
      }

      m_curve2serie.remove( curve );
    }
  }

  /**
   * overriden to return a default axis when no real axes defined yet
   * 
   * @see org.jfree.chart.plot.XYPlot#getDomainAxis()
   */
  public ValueAxis getDomainAxis( )
  {
    synchronized( m_diag2chartAxis )
    {
      if( m_diag2chartAxis.size() == 0 )
        return new NumberAxis();

      return super.getDomainAxis();
    }
  }

  /**
   * overriden to return a default axis when no real axes defined yet
   * 
   * @see org.jfree.chart.plot.XYPlot#getRangeAxis()
   */
  public ValueAxis getRangeAxis( )
  {
    synchronized( m_diag2chartAxis )
    {
      if( m_diag2chartAxis.size() == 0 )
        return new NumberAxis();

      return super.getRangeAxis();
    }
  }

  /**
   * @see org.jfree.chart.plot.XYPlot#drawAnnotations(java.awt.Graphics2D, java.awt.geom.Rectangle2D, org.jfree.chart.plot.PlotRenderingInfo)
   */
  public void drawAnnotations( Graphics2D g2d, Rectangle2D rec, PlotRenderingInfo arg2 )
  {
    super.drawAnnotations( g2d, rec, arg2 );
    
    for( final Iterator it = m_yConsts.keySet().iterator(); it.hasNext(); )
    {
      final ValueAndColor vac = (ValueAndColor) m_yConsts.get( it.next() );
      drawHorizontalLine( g2d, rec, vac.value, g2d.getStroke(), vac.color );
    }
  }

  /**
   * Helper that creates a marker
   * 
   * @param start
   * @param end
   * @param label
   * @param color
   * @return marker
   */
  private final static Marker createMarker( double start, double end,
      String label, Color color )
  {
    final IntervalMarker marker = new IntervalMarker( start, end );
    marker.setPaint( color );
    marker.setLabel( label );
    marker.setLabelAnchor( RectangleAnchor.BOTTOM );
    marker.setLabelTextAnchor( TextAnchor.CENTER );

    return marker;
  }

  /**
   * Returns the adequate renderer for the given axis type.
   * 
   * @param axisType
   * @return renderer
   */
  private final XYItemRenderer getRenderer( final String axisType )
  {
    if( axisType.equals( TimeserieConstants.TYPE_RAINFALL ) )
      return BAR_RENDERER;

    return LINE_RENDERER;
  }

  /**
   * @param diagAxis
   * @return location according to axis
   */
  private final static AxisLocation getLocation( final IDiagramAxis diagAxis )
  {
    if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_BOTTOM ) )
    {
      //if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_LEFT ) )
      return AxisLocation.BOTTOM_OR_LEFT;
      //else if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_RIGHT ) )
      //  return AxisLocation.BOTTOM_OR_RIGHT;
    }
    else if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_TOP ) )
    {
      //if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_LEFT ) )
      return AxisLocation.TOP_OR_LEFT;
      //else if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_RIGHT ) )
      //  return AxisLocation.TOP_OR_RIGHT;
    }
    else if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_LEFT ) )
    {
      //if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_BOTTOM ) )
      //  return AxisLocation.BOTTOM_OR_LEFT;
      //else if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_TOP ) )
      return AxisLocation.TOP_OR_LEFT;
    }
    else if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_RIGHT ) )
    {
      //if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_BOTTOM ) )
      //  return AxisLocation.BOTTOM_OR_RIGHT;
      //else if( diagAxis.getPosition().equals( IDiagramAxis.POSITION_TOP ) )
      return AxisLocation.TOP_OR_RIGHT;
    }

    // default
    return AxisLocation.BOTTOM_OR_LEFT;
  }

  /**
   * mini helper class for storing a value and a color
   * 
   * @author schlienger
   */
  private final static class ValueAndColor
  {
    final double value;

    final Color color;

    final String label;

    public ValueAndColor( final String lbl, final double val, final Color col )
    {
      this.label = lbl;
      this.value = val;
      this.color = col;
    }
  }
}