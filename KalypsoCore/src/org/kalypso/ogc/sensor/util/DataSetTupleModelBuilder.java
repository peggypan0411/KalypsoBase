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
package org.kalypso.ogc.sensor.util;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.TupleModelDataSet;
import org.kalypso.ogc.sensor.impl.SimpleTupleModel;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.ogc.sensor.metadata.MetadataList;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.ogc.sensor.timeseries.AxisUtils;
import org.kalypso.ogc.sensor.timeseries.TimeseriesUtils;
import org.kalypso.ogc.sensor.timeseries.datasource.DataSourceHandler;
import org.kalypso.ogc.sensor.timeseries.datasource.DataSourceHelper;
import org.kalypso.repository.IDataSourceItem;

/**
 * @author Dirk Kuch
 */
public class DataSetTupleModelBuilder implements ICoreRunnableWithProgress
{
  private final Map<Date, TupleModelDataSet[]> m_values;

  private final MetadataList m_metadata;

  private SimpleTupleModel m_model;

  public DataSetTupleModelBuilder( final MetadataList metadata, final Map<Date, TupleModelDataSet[]> values )
  {
    m_metadata = metadata;
    m_values = values;
  }

  public ITupleModel getModel( )
  {
    return m_model;
  }

  @Override
  public IStatus execute( final IProgressMonitor monitor )
  {
    final IAxis[] axes = findAxes();

    m_model = new SimpleTupleModel( axes );

    final int dateAxis = ArrayUtils.indexOf( axes, AxisUtils.findDateAxis( axes ) );

    final DataSourceHandler sources = new DataSourceHandler( m_metadata );

    final Set<Entry<Date, TupleModelDataSet[]>> entries = m_values.entrySet();
    for( final Entry<Date, TupleModelDataSet[]> entry : entries )
    {
      final Object[] data = new Object[ArrayUtils.getLength( axes )];

      data[dateAxis] = entry.getKey();

      final TupleModelDataSet[] datasets = entry.getValue();
      for( final TupleModelDataSet dataset : datasets )
      {
        final IAxis axis = dataset.getValueAxis();

        final int valueAxis = ArrayUtils.indexOf( axes, axis );
        final int statusAxis = ArrayUtils.indexOf( axes, AxisUtils.findStatusAxis( axes, axis ) );
        final int dataSourceAxis = ArrayUtils.indexOf( axes, AxisUtils.findDataSourceAxis( axes, axis ) );

        data[valueAxis] = dataset.getValue();
        data[statusAxis] = dataset.getStatus();

        final String source = dataset.getSource();
        if( StringUtils.isNotEmpty( source ) )
        {
          final int sourceIndex = sources.addDataSource( source, source );
          data[dataSourceAxis] = sourceIndex;
        }
        else
        {
          final int sourceIndex = sources.addDataSource( IDataSourceItem.SOURCE_UNKNOWN, IDataSourceItem.SOURCE_UNKNOWN );
          data[dataSourceAxis] = sourceIndex;
        }
      }

      m_model.addTuple( data );
    }

    return Status.OK_STATUS;
  }

  private IAxis[] findAxes( )
  {
    final Set<IAxis> axes = new LinkedHashSet<>();
    axes.add( TimeseriesUtils.createDefaultAxis( ITimeseriesConstants.TYPE_DATE, true ) );

    final Collection<TupleModelDataSet[]> values = m_values.values();
    for( final TupleModelDataSet[] datasets : values )
    {
      for( final TupleModelDataSet dataset : datasets )
      {
        final IAxis valueAxis = dataset.getValueAxis();
        if( !axes.contains( valueAxis ) )
        {
          axes.add( valueAxis );
          axes.add( KalypsoStatusUtils.createStatusAxisFor( valueAxis, true ) );
          axes.add( DataSourceHelper.createSourceAxis( valueAxis ) );
        }
      }
    }

    return axes.toArray( new IAxis[] {} );
  }
}
