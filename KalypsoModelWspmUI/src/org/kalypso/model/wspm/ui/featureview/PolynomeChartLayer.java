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
package org.kalypso.model.wspm.ui.featureview;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.kalypsodeegree_impl.gml.binding.math.IPolynomial1D;
import org.kalypsodeegree_impl.gml.binding.math.PolynomialUtilities;

import de.openali.odysseus.chart.ext.base.layer.AbstractLineLayer;
import de.openali.odysseus.chart.framework.model.data.IDataRange;
import de.openali.odysseus.chart.framework.model.figure.impl.PointFigure;
import de.openali.odysseus.chart.framework.model.figure.impl.PolylineFigure;
import de.openali.odysseus.chart.framework.model.layer.ILayerProvider;
import de.openali.odysseus.chart.framework.model.mapper.IAxis;
import de.openali.odysseus.chart.framework.model.style.ILineStyle;
import de.openali.odysseus.chart.framework.model.style.IPointStyle;
import de.openali.odysseus.chart.framework.model.style.IStyleSet;
import de.openali.odysseus.chart.framework.util.img.ChartImageInfo;

/**
 * A chart layer which displays Polynomial1D's. <br/>>
 * All polynomial given in the constructor are displayed according to their validity. <br/>
 *
 * @author Gernot Belger
 */
public class PolynomeChartLayer extends AbstractLineLayer
{
  private final int m_pixelsPerTick;

  private final boolean m_showPoints;

  private final PolynomDataContainer m_data;

  /**
   * @param pixelsPerTick
   *          : Determines the resolution how the polynomes are rendered. 1 means: for every pixel in x-diretion, a
   *          polynome value is calculated and rendered.
   */
  public PolynomeChartLayer( final ILayerProvider provider, final PolynomDataContainer dataContainer, final int pixelsPerTick, final IStyleSet styleSet, final boolean showPoints )
  {
    super( provider, styleSet );
    m_pixelsPerTick = pixelsPerTick;
    m_showPoints = showPoints;
    m_data = dataContainer;
  }

  @Override
  public void paint( final GC gc, final ChartImageInfo chartImageInfo, final IProgressMonitor monitor )
  {
    final IDataRange<Double> domainRange = m_data.getDomainRange();
    final double min = domainRange.getMin();
    final double max = domainRange.getMax();

    final PolylineFigure plf = new PolylineFigure();
    final ILineStyle pls = (ILineStyle) getStyleSet().getStyle( "line" ); //$NON-NLS-1$
    plf.setStyle( pls );
    final PointFigure pf = new PointFigure();
    final IPointStyle ps = (IPointStyle) getStyleSet().getStyle( "point" ); //$NON-NLS-1$
    pf.setStyle( ps );

    final ArrayList<Point> path = new ArrayList<>();

    final IAxis domainAxis = getDomainAxis();
    final IAxis targetAxis = getTargetAxis();

    final double logical0 = domainAxis.screenToNumeric( 0 ).doubleValue();
    final double logical1 = domainAxis.screenToNumeric( 1 ).doubleValue();
    final double logicalPixelWidth = Math.abs( logical0 - logical1 );

    final double tick = logicalPixelWidth * m_pixelsPerTick;

    for( double pos = min; pos < max; pos += tick )
    {
      final IPolynomial1D poly = PolynomialUtilities.getPoly( m_data.getPolyArray(), pos );
      if( poly == null )
      {
        continue;
      }

      final double value = poly.computeResult( pos );

      final int x = domainAxis.numericToScreen( pos );
      final int y = targetAxis.numericToScreen( value );
      path.add( new Point( x, y ) );
    }

    plf.setPoints( path.toArray( new Point[] {} ) );
    plf.paint( gc );

    if( m_showPoints )
    {
      pf.setPoints( path.toArray( new Point[] {} ) );
      pf.paint( gc );
    }

  }

  @Override
  public IDataRange<Double> getDomainRange( )
  {
    return m_data.getDomainRange();
  }

  @Override
  public IDataRange<Double> getTargetRange( final IDataRange<Double> domainIntervall )
  {
    return m_data.getTargetRange();
  }
}