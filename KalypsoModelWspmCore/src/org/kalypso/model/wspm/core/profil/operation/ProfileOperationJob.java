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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.kalypso.contribs.eclipse.core.runtime.jobs.MutexRule;
import org.kalypso.model.wspm.core.i18n.Messages;

/**
 * Job, to execute Profil-Operation
 *
 * @author belger
 */
public class ProfileOperationJob extends Job
{
  private static final MutexRule MUTEX = new MutexRule();

  private final ProfileOperationRunnable m_runnable;

  public ProfileOperationJob( final IUndoableOperation... operations )
  {
    super( getLabel( operations ) );

    m_runnable = new ProfileOperationRunnable( operations );

    setUser( true );
    setSystem( false );
    setPriority( Job.SHORT );

    setRule( MUTEX );
  }

  private static String getLabel( final IUndoableOperation[] operations )
  {
    if( ArrayUtils.isEmpty( operations ) )
      return StringUtils.EMPTY;

    else if( ArrayUtils.getLength( operations ) == 1 )
      return operations[0].getLabel();

    return Messages.getString("ProfilOperationJob_0"); //$NON-NLS-1$
  }

  @Override
  protected IStatus run( final IProgressMonitor monitor )
  {
    return m_runnable.execute( monitor );
  }

}
