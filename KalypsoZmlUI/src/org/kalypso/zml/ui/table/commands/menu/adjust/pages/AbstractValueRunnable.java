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
package org.kalypso.zml.ui.table.commands.menu.adjust.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.TupleModelDataSet;
import org.kalypso.ogc.sensor.status.KalypsoStati;
import org.kalypso.ogc.sensor.transaction.TupleModelTransaction;
import org.kalypso.ogc.sensor.transaction.UpdateTupleModelDataSetCommand;
import org.kalypso.repository.IDataSourceItem;
import org.kalypso.zml.core.table.model.IZmlModelColumn;
import org.kalypso.zml.core.table.model.references.IZmlModelValueCell;
import org.kalypso.zml.ui.i18n.Messages;

/**
 * @author Dirk Kuch
 */
public abstract class AbstractValueRunnable implements ICoreRunnableWithProgress
{

  private final IZmlModelValueCell[] m_cells;

  private final IZmlModelColumn m_column;

  public AbstractValueRunnable( final IZmlModelColumn column, final IZmlModelValueCell[] cells )
  {
    m_column = column;
    m_cells = cells;
  }

  @Override
  public final IStatus execute( final IProgressMonitor monitor )
  {
    final List<IStatus> statis = new ArrayList<>();
    try
    {
      final IObservation observation = m_column.getObservation();
      final TupleModelTransaction transaction = new TupleModelTransaction( observation.getValues( null ), observation.getMetadataList() );

      for( final IZmlModelValueCell cell : m_cells )
      {

        try
        {
          final Number value = getValue( (Number) cell.getValue() );

          final TupleModelDataSet dataset = new TupleModelDataSet( m_column.getValueAxis(), value, KalypsoStati.BIT_USER_MODIFIED, IDataSourceItem.SOURCE_MANUAL_CHANGED );
          transaction.add( new UpdateTupleModelDataSetCommand( cell.getModelIndex(), dataset, true ) );

        }
        catch( final Exception e )
        {
          statis.add( StatusUtilities.createExceptionalErrorStatus( Messages.AbstractValueRunnable_0, e ) );
        }
      }

      m_column.getObservation().getValues( null ).execute( transaction );
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
    }

    return StatusUtilities.createStatus( statis, Messages.AbstractValueRunnable_1 );
  }

  protected abstract Number getValue( final Number value );

}
