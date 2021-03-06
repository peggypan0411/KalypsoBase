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
package org.kalypso.core.util.pool;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.UIJob;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.i18n.Messages;
import org.kalypso.loader.LoaderException;

final class SaveAndDisposeInfoJob extends UIJob
{
  private final KeyInfo m_info;

  public SaveAndDisposeInfoJob( final String name, final KeyInfo info )
  {
    super( name );
    m_info = info;
  }

  @Override
  public IStatus runInUIThread( final IProgressMonitor monitor )
  {
    try
    {
      final String location = m_info.getKey().getLocation();
      final String message = Messages.getString( "org.kalypso.util.pool.SaveAndDisposeInfoJob.0" ) + location + Messages.getString( "org.kalypso.util.pool.SaveAndDisposeInfoJob.1" ); //$NON-NLS-1$ //$NON-NLS-2$
      final Display display = getDisplay();
      final Shell shell = display == null ? null : display.getActiveShell();
      final boolean doSave = MessageDialog.openQuestion( shell, Messages.getString( "org.kalypso.util.pool.SaveAndDisposeInfoJob.2" ), message ); //$NON-NLS-1$
      if( doSave )
        m_info.saveObject( monitor );
    }
    catch( final LoaderException e )
    {
      return StatusUtilities.statusFromThrowable( e );
    }
    finally
    {
      m_info.dispose();
    }

    return Status.OK_STATUS;
  }
}