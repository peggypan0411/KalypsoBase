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
package org.kalypso.project.database.client.core.base.worker;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.java.lang.Strings;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.module.project.local.ILocalProject;
import org.kalypso.project.database.client.KalypsoProjectDatabaseClient;
import org.kalypso.project.database.client.core.model.projects.ITranscendenceProject;
import org.kalypso.project.database.client.i18n.Messages;
import org.kalypso.project.database.sei.IProjectDatabase;

/**
 * Acquires a project lock (lock ticket) in the model base and update {@link org.kalypso.project.database.common.nature.RemoteProjectNature} lock settings
 * 
 * @author Dirk Kuch
 */
public class AcquireProjectLockWorker implements ICoreRunnableWithProgress
{
  private final ILocalProject m_handler;

  public AcquireProjectLockWorker( final ILocalProject handler )
  {
    m_handler = handler;
  }

  @Override
  public IStatus execute( final IProgressMonitor monitor )
  {
    final IProjectDatabase service = KalypsoProjectDatabaseClient.getService();
    final String ticket = service.acquireProjectEditLock( m_handler.getUniqueName() );
    if( Strings.isEmpty( ticket ) ) //$NON-NLS-1$
      return new Status( IStatus.ERROR, KalypsoProjectDatabaseClient.PLUGIN_ID, Messages.getString( "org.kalypso.project.database.client.core.project.lock.acquire.AcquireProjectLockWorker.1", m_handler.getName() ) ); //$NON-NLS-1$

    if( !(m_handler instanceof ITranscendenceProject) )
      return new Status( IStatus.ERROR, KalypsoProjectDatabaseClient.PLUGIN_ID, Messages.getString( "org.kalypso.project.database.client.core.project.lock.acquire.AcquireProjectLockWorker.2", m_handler.getName() ) ); //$NON-NLS-1$

    final ITranscendenceProject trancendence = (ITranscendenceProject)m_handler;
    trancendence.getRemotePreferences().setEditTicket( ticket );

    return Status.OK_STATUS;
  }
}
