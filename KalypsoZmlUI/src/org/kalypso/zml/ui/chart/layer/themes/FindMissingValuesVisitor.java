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
package org.kalypso.zml.ui.chart.layer.themes;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.kalypso.commons.java.lang.Objects;
import org.kalypso.ogc.sensor.DateRange;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.zml.core.diagram.base.IZmlLayer;
import org.kalypso.zml.core.diagram.data.IZmlLayerDataHandler;

import de.openali.odysseus.chart.framework.model.layer.IChartLayer;
import de.openali.odysseus.chart.framework.model.layer.manager.AbstractChartLayerVisitor;

/**
 * @author Dirk Kuch
 */
public class FindMissingValuesVisitor extends AbstractChartLayerVisitor
{
  Set<DateRange> m_missing = new TreeSet<>();

  @Override
  public void visit( final IChartLayer layer )
  {
    if( !layer.isVisible() )
      return;
    if( !(layer instanceof IZmlLayer) )
    {
      layer.getLayerManager().accept( this );
      return;
    }

    final IZmlLayer zml = (IZmlLayer) layer;
    final IZmlLayerDataHandler handler = zml.getDataHandler();
    if( Objects.isNull( handler ) )
      return;

    final IObservation observation = (IObservation) handler.getAdapter( IObservation.class );
    if( Objects.isNull( observation ) )
      return;

    try
    {
      final FindMissingObservationValuesVisitor visitor = new FindMissingObservationValuesVisitor();
      observation.accept( visitor, null, 1 );

      Collections.addAll( m_missing, visitor.getMissingValues() );
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
    }
  }

  public DateRange[] getMissingValues( )
  {
    return m_missing.toArray( new DateRange[] {} );
  }
}
