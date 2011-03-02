/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.zml.ui.chart.layer.themes;

import java.util.Date;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationTokenHelper;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.timeseries.AxisUtils;
import org.kalypso.zml.core.diagram.data.IZmlLayerDataHandler;
import org.kalypso.zml.core.diagram.layer.IZmlLayer;
import org.kalypso.zml.ui.KalypsoZmlUI;

import de.openali.odysseus.chart.ext.base.layer.AbstractBarLayer;
import de.openali.odysseus.chart.ext.base.layer.ChartLayerUtils;
import de.openali.odysseus.chart.framework.model.data.IDataRange;
import de.openali.odysseus.chart.framework.model.figure.impl.PolygonFigure;
import de.openali.odysseus.chart.framework.model.layer.ILayerProvider;
import de.openali.odysseus.chart.framework.model.layer.ILegendEntry;
import de.openali.odysseus.chart.framework.model.style.IAreaStyle;
import de.openali.odysseus.chart.framework.model.style.IStyleSet;
import de.openali.odysseus.chart.framework.model.style.impl.StyleSetVisitor;

/**
 * @author Dirk Kuch
 * @author kimwerner
 */
public class ZmlBarLayer extends AbstractBarLayer implements IZmlLayer
{

  private IZmlLayerDataHandler m_handler;

  private String m_labelDescriptor;

  private final ZmlBarLayerLegendEntry m_legend = new ZmlBarLayerLegendEntry( this );

  private final ZmlBarLayerRangeHandler m_range = new ZmlBarLayerRangeHandler( this );

  private final IStyleSet m_styleSet;

  protected ZmlBarLayer( final ILayerProvider layerProvider, final IStyleSet styleSet )
  {
    super( layerProvider, null );
    m_styleSet = styleSet;
  }

  /**
   * @see de.openali.odysseus.chart.ext.base.layer.AbstractBarLayer#dispose()
   */
  @Override
  public void dispose( )
  {
    m_handler.dispose();

    super.dispose();
  }

  @Override
  public synchronized ILegendEntry[] getLegendEntries( )
  {
    return createLegendEntries();
  }

  @Override
  public ILegendEntry[] createLegendEntries( )
  {
    return m_legend.createLegendEntries( getPolygonFigure() );
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.layer.IChartLayer#getDomainRange()
   */
  @Override
  public IDataRange<Number> getDomainRange( )
  {
    return m_range.getDomainRange();
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.layer.IChartLayer#getTargetRange()
   */
  @Override
  public IDataRange<Number> getTargetRange( final IDataRange<Number> domainIntervall )
  {
    return m_range.getTargetRange();
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.layer.IChartLayer#paint(org.eclipse.swt.graphics.GC)
   */
  @Override
  public void paint( final GC gc )
  {
    try
    {
      final ITupleModel model = m_handler.getModel();
      if( model == null )
        return;

      final org.kalypso.ogc.sensor.IAxis dateAxis = AxisUtils.findDateAxis( model.getAxes() );
      final PolygonFigure pf = getPolygonFigure();
      final Point base = getCoordinateMapper().numericToScreen( 0.0, 0.0 );

      Point lastScreen = null;
      for( int i = 0; i < model.size(); i++ )
      {
        try
        {
          final Object domainValue = model.get( i, dateAxis );
          Object targetValue = model.get( i, m_handler.getValueAxis() );
          if( domainValue == null || targetValue == null )
            continue;

          /** @hack for polder control */
          if( targetValue instanceof Boolean )
          {
            if( Boolean.valueOf( (Boolean) targetValue ) )
              targetValue = 1;
            else
              targetValue = 0;
          }

          final Number logicalDomain = m_range.getDateDataOperator().logicalToNumeric( ChartLayerUtils.addTimezoneOffset( (Date) domainValue ) );
          final Number logicalTarget = m_range.getNumberDataOperator().logicalToNumeric( (Number) targetValue );
          final Point screen = getCoordinateMapper().numericToScreen( logicalDomain, logicalTarget );

          // don't draw empty lines only rectangles
          if( screen.y != base.y )
          {
            final int lastScreenX = lastScreen == null ? screen.x : lastScreen.x;
            pf.setPoints( new Point[] { new Point( lastScreenX, base.y ), new Point( lastScreenX, screen.y ), screen, new Point( screen.x, base.y ) } );
            pf.paint( gc );
          }
          lastScreen = screen;
        }
        catch( final Throwable t )
        {
          KalypsoZmlUI.getDefault().getLog().log( StatusUtilities.statusFromThrowable( t ) );
        }
      }
    }
    catch( final SensorException e )
    {
      KalypsoZmlUI.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
    }
  }

  /**
   * @see org.kalypso.zml.ui.chart.layer.themes.IZmlLayer#getDataHandler()
   */
  @Override
  public IZmlLayerDataHandler getDataHandler( )
  {
    return m_handler;
  }

  /**
   * @see org.kalypso.zml.core.diagram.layer.IZmlLayer#setDataHandler(org.kalypso.zml.core.diagram.data.IZmlLayerDataHandler)
   */
  @Override
  public void setDataHandler( final IZmlLayerDataHandler handler )
  {
    if( m_handler != null )
      m_handler = handler;

    m_handler = handler;
  }

  /**
   * @see org.kalypso.zml.core.diagram.layer.IZmlLayer#setLabelDescriptor(java.lang.String)
   */
  @Override
  public void setLabelDescriptor( final String labelDescriptor )
  {
    m_labelDescriptor = labelDescriptor;
  }

  @Override
  public String getTitle( )
  {
    if( m_labelDescriptor == null )
      return super.getTitle();

    final IObservation observation = getDataHandler().getObservation();
    if( observation == null )
      return m_labelDescriptor;

    return ObservationTokenHelper.replaceTokens( m_labelDescriptor, observation, getDataHandler().getValueAxis() );
  }

  /**
   * @see de.openali.odysseus.chart.ext.base.layer.AbstractBarLayer#getAreaStyle()
   */
  @Override
  protected IAreaStyle getAreaStyle( )
  {
    final IStyleSet styleSet = getStyleSet();
    final int index = ZmlLayerHelper.getLayerIndex( getId() );

    final StyleSetVisitor visitor = new StyleSetVisitor();
    final IAreaStyle style = visitor.visit( styleSet, IAreaStyle.class, index );

    return style;
  }

  protected IStyleSet getStyleSet( )
  {
    return m_styleSet;
  }
}
