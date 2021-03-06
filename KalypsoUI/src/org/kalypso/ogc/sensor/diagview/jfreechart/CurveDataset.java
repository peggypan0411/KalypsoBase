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
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.kalypso.ogc.sensor.SensorException;

/**
 * A CurveDataset contains XYCurveSerie objects for the purpose of being displayed within a JFreeChart.
 * <p>
 * Inserted synchronized at some places because if pages are switched too fast in kalypso wizard then I presume that many swing ui threads are trying to update the chart, thus leading to possible
 * array out of bound exceptions because of concurrent accesses.
 * 
 * @author schlienger
 */
class CurveDataset extends AbstractIntervalXYDataset
{
  private final static class RendererInfo
  {
    private final Stroke m_stroke;

    private final Color m_color;

    private final XYCurveSerie m_xyc;

    public RendererInfo( final XYCurveSerie xyc, final Color color, final Stroke stroke )
    {
      m_xyc = xyc;
      m_color = color;
      m_stroke = stroke;
    }

    public Color getColor( )
    {
      return m_color;
    }

    public Stroke getStroke( )
    {
      return m_stroke;
    }

    public XYCurveSerie getSerie( )
    {
      return m_xyc;
    }
  }

  /**
   * List of RendererInfo.
   * <p>
   * REMARK: we cannot use a map ore something similiar, because the hashCode of the XYCurveSeries is not well-implemented, causing different curves to be equal.
   * </p>
   */
  private final List<RendererInfo> m_curves = Collections.synchronizedList( new ArrayList<RendererInfo>() );

  public CurveDataset( )
  {
    // empty
  }

  public void addCurveSerie( final XYCurveSerie xyc, final Color color, final Stroke stroke, final XYItemRenderer renderer )
  {
    m_curves.add( new RendererInfo( xyc, color, stroke ) );

    reconfigureRenderer( renderer );

    fireDatasetChanged();
  }

  public void removeCurveSerie( final XYCurveSerie xyc )
  {
    for( final Iterator<RendererInfo> iter = m_curves.iterator(); iter.hasNext(); )
    {
      final RendererInfo element = iter.next();
      if( element.getSerie() == xyc )
      {
        iter.remove();
        fireDatasetChanged();
        return;
      }
    }
  }

  /**
   * @see org.jfree.data.general.SeriesDataset#getSeriesCount()
   */
  @Override
  public int getSeriesCount( )
  {
    return m_curves.size();
  }

  /**
   * @see org.jfree.data.general.SeriesDataset#getSeriesName(int)
   */
  @Override
  public String getSeriesName( final int series )
  {
    final RendererInfo[] curveArray = getCurveArray();
    return curveArray[series].getSerie().getName();
  }

  /**
   * @see org.jfree.data.xy.XYDataset#getItemCount(int)
   */
  @Override
  public int getItemCount( final int series )
  {
    final RendererInfo[] curveArray = getCurveArray();
    try
    {
      return curveArray[series].getSerie().getItemCount();
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * @see org.jfree.data.xy.XYDataset#getXValue(int, int)
   */
  @Override
  public double getXValue( final int series, final int item )
  {
    final Number x = getX( series, item );

    return x == null ? Double.NaN : x.doubleValue();
  }

  /**
   * @see org.jfree.data.xy.XYDataset#getX(int, int)
   */
  @Override
  public Number getX( final int series, final int item )
  {
    final RendererInfo[] curveArray = getCurveArray();
    try
    {
      final Number value = curveArray[series].getSerie().getXValue( item );
      return value;
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * @see org.jfree.data.xy.XYDataset#getYValue(int, int)
   */
  @Override
  public double getYValue( final int series, final int item )
  {
    final Number y = getY( series, item );

    return y == null ? Double.NaN : y.doubleValue();
  }

  /**
   * @see org.jfree.data.xy.XYDataset#getY(int, int)
   */
  @Override
  public Number getY( final int series, final int item )
  {
    final RendererInfo[] curveArray = getCurveArray();
    try
    {
      final Number value = curveArray[series].getSerie().getYValue( item );
      return value;
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * @see org.jfree.data.xy.IntervalXYDataset#getStartXValue(int, int)
   */
  @Override
  public double getStartXValue( final int series, final int item )
  {
    if( item > 0 )
      return getXValue( series, item - 1 );

    return getXValue( series, item );
  }

  /**
   * @see org.jfree.data.xy.IntervalXYDataset#getEndXValue(int, int)
   */
  @Override
  public double getEndXValue( final int series, final int item )
  {
    return getXValue( series, item );
  }

  /**
   * @see org.jfree.data.xy.IntervalXYDataset#getStartYValue(int, int)
   */
  @Override
  public double getStartYValue( final int series, final int item )
  {
    if( item > 0 )
      return getYValue( series, item - 1 );

    return getYValue( series, item );
  }

  /**
   * @see org.jfree.data.xy.IntervalXYDataset#getEndYValue(int, int)
   */
  @Override
  public double getEndYValue( final int series, final int item )
  {
    return getYValue( series, item );
  }

  /**
   * @see org.jfree.data.xy.IntervalXYDataset#getStartX(int, int)
   */
  @Override
  public Number getStartX( final int series, final int item )
  {
    if( item > 0 )
      return getX( series, item - 1 );

    return getX( series, item );
  }

  /**
   * @see org.jfree.data.xy.IntervalXYDataset#getEndX(int, int)
   */
  @Override
  public Number getEndX( final int series, final int item )
  {
    return getX( series, item );
  }

  /**
   * @see org.jfree.data.xy.IntervalXYDataset#getStartY(int, int)
   */
  @Override
  public Number getStartY( final int series, final int item )
  {
    if( item > 0 )
      return getY( series, item - 1 );

    return getY( series, item );
  }

  /**
   * @see org.jfree.data.xy.IntervalXYDataset#getEndY(int, int)
   */
  @Override
  public Number getEndY( final int series, final int item )
  {
    return getY( series, item );
  }

  private void reconfigureRenderer( final XYItemRenderer renderer )
  {
    if( renderer == null )
      return;

    final RendererInfo[] curveArray = getCurveArray();

    for( int i = 0; i < curveArray.length; i++ )
    {
      final RendererInfo info = curveArray[i];
      renderer.setSeriesPaint( i, info.getColor() );
      renderer.setSeriesStroke( i, info.getStroke() );
    }
  }

  /**
   * Return the status of the item in the given serie.
   * 
   * @return status-number or null if not existing
   */
  public Number getStatusFor( final int series, final int item )
  {
    final RendererInfo[] curveArray = getCurveArray();
    try
    {
      final Number value = curveArray[series].getSerie().getStatus( item );

      return value;
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  private RendererInfo[] getCurveArray( )
  {
    return m_curves.toArray( new RendererInfo[m_curves.size()] );
  }

  public boolean hideLegend( final int series )
  {
    final RendererInfo rendererInfo = m_curves.get( series );
    final XYCurveSerie serie = rendererInfo.getSerie();
    return !serie.getShowLegend();
  }

}