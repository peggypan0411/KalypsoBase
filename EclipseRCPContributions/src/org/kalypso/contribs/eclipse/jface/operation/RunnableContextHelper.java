/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.contribs.eclipse.jface.operation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.internal.EclipseRCPContributionsPlugin;

/**
 * Helper-Class for IRunnableContext
 * 
 * @author belger
 */
public final class RunnableContextHelper
{
  private final IRunnableContext m_context;

  private RunnableContextHelper( final IRunnableContext context )
  {
    m_context = context;
  }

  /**
   * Runs the given runnable in the given context, but catches all (event runtime-) exception and turns them into a
   * {@Link IStatus}object.
   */
  public IStatus execute( final boolean fork, final boolean cancelable, final IRunnableWithProgress runnable )
  {
    try
    {
      m_context.run( fork, cancelable, runnable );
    }
    catch( final OperationCanceledException e )
    {
      return new Status( IStatus.CANCEL, EclipseRCPContributionsPlugin.ID, "Abbruch durch Benutzer" );
    }
    catch( final InterruptedException ie )
    {
      return new Status( IStatus.CANCEL, EclipseRCPContributionsPlugin.ID, "Abbruch durch Benutzer" );
    }
    catch( final Throwable t )
    {
      final IStatus status = StatusUtilities.statusFromThrowable( t );
      EclipseRCPContributionsPlugin.getDefault().getLog().log( status );
      return status;
    }

    if( runnable instanceof CoreRunnableWrapper )
      return ((CoreRunnableWrapper) runnable).getStatus();

    return Status.OK_STATUS;
  }

  /**
   * Runs the given runnable in the given context, but catches all (event runtime-) exception and turns them into a
   * {@Link IStatus}object.
   */
  public IStatus execute( final boolean fork, final boolean cancelable, final ICoreRunnableWithProgress runnable )
  {
    final IRunnableWithProgress innerRunnable = new CoreRunnableWrapper( runnable );

    return execute( fork, cancelable, innerRunnable );
  }

  /**
   * Runs the given runnable in the given context, but catches all (event runtime-) exception and turns them into a
   * {@Link IStatus}object.
   */
  public static IStatus execute( final IRunnableContext context, final boolean fork, final boolean cancelable, final IRunnableWithProgress runnable )
  {
    final RunnableContextHelper helper = new RunnableContextHelper( context );
    return helper.execute( fork, cancelable, runnable );
  }

  /**
   * Runs the given runnable in the given context, but catches all (event runtime-) exception and turns them into a
   * {@Link IStatus} object.
   */
  public static IStatus execute( final IRunnableContext context, final boolean fork, final boolean cancelable, final ICoreRunnableWithProgress runnable )
  {
    final RunnableContextHelper helper = new RunnableContextHelper( context );
    return helper.execute( fork, cancelable, runnable );
  }

  /**
   * Runs a runnable in a progress monitor dialog.
   * 
   * @deprecated Use
   *             {@link org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities#busyCursorWhile(ICoreRunnableWithProgress)}
   *             instead.
   */
  @Deprecated
  public final static IStatus executeInProgressDialog( final Shell shell, final ICoreRunnableWithProgress runnable, final IErrorHandler errorHandler )
  {
    // run the execute method in a Progress-Dialog
    final ProgressMonitorDialog dialog = new ProgressMonitorDialog( shell );
    final IStatus status = execute( dialog, false, true, runnable );
    errorHandler.handleError( shell, status );
    return status;
  }
}
