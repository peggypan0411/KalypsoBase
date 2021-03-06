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
package org.kalypso.project.database.client.core.base.worker;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.ide.undo.DeleteResourcesOperation;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.project.database.client.KalypsoProjectDatabaseClient;
import org.kalypso.project.database.client.i18n.Messages;

/**
 * Deletes an local IProject
 * 
 * @author Dirk Kuch
 */
public class DeleteLocalProjectWorker implements ICoreRunnableWithProgress
{
  private final IProject m_project;

  public DeleteLocalProjectWorker( final IProject project )
  {
    m_project = project;
  }

  @Override
  public IStatus execute( final IProgressMonitor monitor )
  {
    final DeleteResourcesOperation operation = new DeleteResourcesOperation( new IResource[] { m_project }, Messages.getString( "org.kalypso.project.database.client.core.project.workspace.DeleteLocalProjectHandler.0", m_project.getName() ), true ); //$NON-NLS-1$
    try
    {
      return operation.execute( monitor, null );
    }
    catch( final ExecutionException e )
    {
      return new Status( IStatus.ERROR, KalypsoProjectDatabaseClient.PLUGIN_ID, Messages.getString("DeleteLocalProjectWorker.0"), e ); //$NON-NLS-1$
    }
  }
}