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
package de.openali.odysseus.chart.framework.util.img;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

import de.openali.odysseus.chart.framework.model.IChartModel;
import de.openali.odysseus.chart.framework.model.layer.IChartLayer;

/**
 * @author kimwerner
 */
public class ChartPlotPainter
{
  final Point m_size;

  private final IChartModel m_chartModel;

  public ChartPlotPainter( final IChartModel chartModel, final Point size )
  {
    m_chartModel = chartModel;
    m_size = size;
  }

  private IChartLayer[] getLayersForPaint( )
  {
    final PaintableLayersVisitor collector = new PaintableLayersVisitor();

    m_chartModel.accept( collector );

    return collector.getLayers();
  }

  public final Point getSize( )
  {
    return m_size;
  }

  public void paint( final GC gc, final ChartImageInfo chartImageInfo, final IProgressMonitor monitor )
  {
    final IChartLayer[] layers = getLayersForPaint();

    final Transform oldTransform = new Transform( gc.getDevice() );
    final Transform transform = new Transform( gc.getDevice() );

    gc.getTransform( oldTransform );
    gc.getTransform( transform );

    try
    {
      monitor.beginTask( "Painting layers", layers.length );
    //  gc.setAlpha( 0 );
    //  gc.fillRectangle( gc.getClipping());
      for( final IChartLayer layer : layers )
      {
        if( layer.isVisible() )
          layer.paint( gc, chartImageInfo, new SubProgressMonitor( monitor, 1 ) );

        if( monitor.isCanceled() )
          throw new OperationCanceledException();
      }
    }
    finally
    {
      gc.setTransform( oldTransform );

      oldTransform.dispose();
      transform.dispose();
    }
  }
}