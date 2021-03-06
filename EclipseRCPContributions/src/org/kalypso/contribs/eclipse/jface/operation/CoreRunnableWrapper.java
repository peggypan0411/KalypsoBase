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
package org.kalypso.contribs.eclipse.jface.operation;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;

/**
 * This class wraps a {@link ICoreRunnableWithProgress} in a {@link IRunnableWithProgress}.
 *
 * @author Gernot Belger
 */
public final class CoreRunnableWrapper implements IRunnableWithProgress
{
  private final ICoreRunnableWithProgress m_runnable;

  private IStatus m_status = Status.OK_STATUS;

  public CoreRunnableWrapper( final ICoreRunnableWithProgress runnable )
  {
    m_runnable = runnable;
  }

  @Override
  public void run( final IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
  {
    try
    {
      m_status = m_runnable.execute( monitor );
    }
    catch( final OperationCanceledException e )
    {
      m_status = ProgressUtilities.STATUS_OPERATION_CANCELLED;
    }
    catch( final CoreException e )
    {
      throw new InvocationTargetException( e );
    }
  }

  public IStatus getStatus( )
  {
    return m_status;
  }
}