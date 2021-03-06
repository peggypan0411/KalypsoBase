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
package org.kalypso.model.wspm.core.profil.operation;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.swt.widgets.GetShellFromDisplay;
import org.kalypso.contribs.eclipse.ui.plugin.AbstractUIPluginExt;
import org.kalypso.model.wspm.core.i18n.Messages;

/**
 * @author Gernot
 */
public class ProfileOperationRunnable implements ICoreRunnableWithProgress, IAdaptable
{
  private final IUndoableOperation[] m_operations;

  public ProfileOperationRunnable( final IUndoableOperation... operation )
  {
    m_operations = operation;
  }

  @Override
  public IStatus execute( final IProgressMonitor monitor )
  {
    final Set<IStatus> stati = new LinkedHashSet<>();
    for( final IUndoableOperation operation : m_operations )
    {
      try
      {
        final IOperationHistory operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();

        // give "this" as adaptable, it can deliver a shell (used for message dialogs for instance)
        stati.add( operationHistory.execute( operation, monitor, this ) );
      }
      catch( final ExecutionException e )
      {
        stati.add( new Status( IStatus.ERROR, AbstractUIPluginExt.ID, 0, Messages.getString( "org.kalypso.model.wspm.ui.profil.operation.ProfilOperationRunnable.0", operation.getLabel() ), e ) ); //$NON-NLS-1$
      }

    }

    if( stati.size() == 1 )
      return stati.iterator().next();

    return StatusUtilities.createStatus( stati, Messages.getString("ProfilOperationRunnable.0") ); //$NON-NLS-1$
  }

  @Override
  public Object getAdapter( final Class adapter )
  {
    if( adapter == Shell.class )
    {
      final Display display = PlatformUI.getWorkbench().getDisplay();
      final GetShellFromDisplay getShellFromDisplay = new GetShellFromDisplay( display );

      display.asyncExec( getShellFromDisplay );

      return getShellFromDisplay.getShell();
    }

    return null;
  }
}
