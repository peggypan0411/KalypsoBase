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
package org.kalypso.zml.ui.table.commands.menu;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.kalypso.ogc.sensor.TupleModelDataSet;
import org.kalypso.ogc.sensor.status.KalypsoStati;
import org.kalypso.ogc.sensor.transaction.TupleModelTransaction;
import org.kalypso.ogc.sensor.transaction.UpdateTupleModelDataSetCommand;
import org.kalypso.repository.IDataSourceItem;
import org.kalypso.zml.core.table.model.IZmlModelColumn;
import org.kalypso.zml.core.table.model.ZmlValueLabelProvider;
import org.kalypso.zml.core.table.model.editing.IZmlEditingStrategy;
import org.kalypso.zml.core.table.model.interpolation.ZmlInterpolationWorker;
import org.kalypso.zml.core.table.model.references.IZmlModelValueCell;
import org.kalypso.zml.core.table.model.view.ZmlModelViewport;
import org.kalypso.zml.core.table.model.visitor.IZmlModelColumnVisitor;
import org.kalypso.zml.ui.table.IZmlTable;
import org.kalypso.zml.ui.table.commands.ZmlHandlerUtil;
import org.kalypso.zml.ui.table.nat.layers.IZmlTableSelection;

/**
 * @author Dirk Kuch
 */
public class ZmlCommandSetAllValues extends AbstractHandler
{
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IZmlTable table = ZmlHandlerUtil.getTable( event );
    final IZmlTableSelection selection = table.getSelection();
    final ZmlModelViewport model = table.getModelViewport();

    final IZmlModelValueCell current = selection.getFocusCell();

    try
    {
      final IZmlModelColumn column = current.getColumn();
      final IZmlEditingStrategy strategy = model.getEditingStrategy( column );

      if( strategy.isAggregated() )
      {
        final ZmlValueLabelProvider provider = new ZmlValueLabelProvider( column );
        final String targetValue = provider.getText( table.getModelViewport(), current );

        final IZmlModelValueCell[] visibleCells = model.getCells( column );
        for( final IZmlModelValueCell cell : visibleCells )
        {
          strategy.setValue( cell, targetValue );
        }
      }
      else
      {
        final Number targetValue = current.getValue();

        final TupleModelTransaction transaction = new TupleModelTransaction( column.getTupleModel(), column.getMetadata() );

        column.accept( new IZmlModelColumnVisitor()
        {
          @Override
          public void visit( final IZmlModelValueCell ref )
          {
            final TupleModelDataSet dataset = new TupleModelDataSet( column.getValueAxis(), targetValue, KalypsoStati.BIT_USER_MODIFIED, IDataSourceItem.SOURCE_MANUAL_CHANGED );
            transaction.add( new UpdateTupleModelDataSetCommand( ref.getModelIndex(), dataset, true ) );
          }
        } );

        column.getTupleModel().execute( transaction );
      }

      /**
       * re-interpolate complete observation because of table view filter (like 12h view, stueztstellen ansicht, etc)
       */
      final ZmlInterpolationWorker interpolationWorker = new ZmlInterpolationWorker( column );
      interpolationWorker.execute( new NullProgressMonitor() );

      // TODO status handling of interpolation worker

      return Status.OK_STATUS;
    }
    catch( final Exception e )
    {
      throw new ExecutionException( "Aktualisieren der Werte fehlgeschlagen.", e );
    }
  }
}
