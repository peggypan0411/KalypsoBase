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
package org.kalypso.ogc.gml.om.table.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.progress.UIJob;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * @author Gernot Belger
 */
public class InsertRowHandler extends AbstractHandler
{
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final TableViewer viewer = ToolbarCommandUtils.findTableViewer( event );
    final TupleResult tupleResult = ToolbarCommandUtils.findTupleResult( event );
    if( viewer == null || tupleResult == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.ogc.gml.om.table.command.AddRowHandler.0" ) ); //$NON-NLS-1$

    final IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
    if( selection == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.ogc.gml.om.table.command.InsertRowHandler.3" ) ); //$NON-NLS-1$

    final int index;
    if( selection.size() == 0 )
    {
      //throw new ExecutionException( Messages.getString("org.kalypso.ogc.gml.om.table.command.InsertRowHandler.1") ); //$NON-NLS-1$
      index = -1;
    }
    else
    {
      final Object obj = selection.getFirstElement();
      index = tupleResult.indexOf( obj );
    }

    final IRecord row = tupleResult.createRecord();
    tupleResult.doInterpolation( row, index, 0.5 );

    tupleResult.add( index + 1, row );

    // select the new row; in ui job, as table is also updated in an ui event
    new UIJob( "" ) //$NON-NLS-1$
    {
      @Override
      public IStatus runInUIThread( final IProgressMonitor monitor )
      {
        viewer.setSelection( new StructuredSelection( row ) );
        return Status.OK_STATUS;
      }
    }.schedule();

    return null;
  }

}
