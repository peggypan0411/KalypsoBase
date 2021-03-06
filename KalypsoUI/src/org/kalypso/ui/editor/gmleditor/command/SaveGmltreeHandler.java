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
package org.kalypso.ui.editor.gmleditor.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;
import org.kalypso.contribs.eclipse.core.commands.HandlerUtils;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.core.status.StatusDialog;
import org.kalypso.ui.editor.gmleditor.part.GmlTreeView;
import org.kalypso.ui.internal.i18n.Messages;

public class SaveGmltreeHandler extends AbstractHandler
{
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final Shell shell = HandlerUtil.getActiveShellChecked( event );
    final IWorkbenchSite site = HandlerUtil.getActiveSiteChecked( event );

    final GmlTreeView treeViewer = GmltreeHandlerUtils.getTreeViewerChecked( event );
    if( treeViewer == null )
      return null;

    final String commandName = HandlerUtils.getCommandName( event );

    if( !MessageDialog.openConfirm( shell, commandName, Messages.getString( "org.kalypso.ui.editor.gmleditor.actions.SaveGmlDelegate.1" ) ) ) //$NON-NLS-1$
      return null;

    final IProgressService progressService = (IProgressService)site.getService( IProgressService.class );
    final WorkspaceModifyOperation op = new WorkspaceModifyOperation()
    {
      @SuppressWarnings( "synthetic-access" )
      @Override
      protected void execute( final IProgressMonitor monitor ) throws CoreException
      {
        treeViewer.saveData( monitor );
        // FIXME: we need better event handling ni the gml tree; normally, we should not need this
        SaveGmltreeHandler.this.fireHandlerChanged( new HandlerEvent( SaveGmltreeHandler.this, true, false ) );
      }
    };

    final String errorMessage = Messages.getString( "org.kalypso.ui.editor.gmleditor.actions.SaveGmlDelegate.3" ); //$NON-NLS-1$;
    final IStatus result = ProgressUtilities.busyCursorWhile( progressService, op, errorMessage );
    if( !result.isOK() )
      StatusDialog.open( shell, result, commandName );

    return null;
  }
}