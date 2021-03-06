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
package org.kalypso.project.database.client.core.base.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.kalypso.core.status.StatusDialog;
import org.kalypso.module.IKalypsoModule;
import org.kalypso.module.IKalypsoModuleProjectOpenAction;
import org.kalypso.module.project.local.ILocalProject;
import org.kalypso.project.database.client.KalypsoProjectDatabaseClient;
import org.kalypso.project.database.client.core.model.projects.ITranscendenceProject;
import org.kalypso.project.database.client.i18n.Messages;
import org.kalypso.project.database.common.nature.IRemoteProjectPreferences;

/**
 * @author Dirk Kuch
 */
public class ProjectOpenAction implements IProjectAction
{
  protected static final Image IMG_PROJECT_LOCAL = new Image( null, ProjectOpenAction.class.getResourceAsStream( "images/project_local.gif" ) ); //$NON-NLS-1$

  protected static final Image IMG_PROJECT_TRANSCENDENCE = new Image( null, ProjectOpenAction.class.getResourceAsStream( "images/project_transcendence.gif" ) ); //$NON-NLS-1$

  protected static final Image iMG_PROJECT_TRANSCENDENCE_OFFLINE = new Image( null, ProjectOpenAction.class.getResourceAsStream( "images/project_transcendence_offline.gif" ) ); //$NON-NLS-1$

  protected static final Image IMG_PROJECT_TRANSCENDENCE_REMOTE_LOCK = new Image( null, ProjectOpenAction.class.getResourceAsStream( "images/project_remote_locked.gif" ) ); //$NON-NLS-1$

  protected static final Image IMG_PROJECT_TRANSCENDENCE_LOCAL_LOCK = new Image( null, ProjectOpenAction.class.getResourceAsStream( "images/project_transcendence_local_lock.gif" ) ); //$NON-NLS-1$

  private OPEN_TYPE m_type = null;

  private final ILocalProject m_handler;

  private final IKalypsoModule m_module;

  // FIXME: ugly, implement different open actions instead!
  public enum OPEN_TYPE
  {
    eLocal,
    eLocalOffline,
    eTranscendenceReadable,
    eTranscendenceReadableServerLocked,
    eTranscendenceWriteable;

    Image getImage( )
    {
      final OPEN_TYPE type = valueOf( name() );
      if( eLocal.equals( type ) )
        return IMG_PROJECT_LOCAL;
      else if( eLocalOffline.equals( type ) )
        return iMG_PROJECT_TRANSCENDENCE_OFFLINE;
      else if( eTranscendenceReadable.equals( type ) )
        return IMG_PROJECT_TRANSCENDENCE;
      else if( eTranscendenceReadableServerLocked.equals( type ) )
        return IMG_PROJECT_TRANSCENDENCE_REMOTE_LOCK;
      else if( eTranscendenceWriteable.equals( type ) )
        return IMG_PROJECT_TRANSCENDENCE_LOCAL_LOCK;

      throw new UnsupportedOperationException();
    }

    public String getStatus( )
    {
      final OPEN_TYPE type = valueOf( name() );
      if( eLocal.equals( type ) )
        return (Messages.getString("ProjectOpenAction.0")); //$NON-NLS-1$
      else if( eLocalOffline.equals( type ) )
        return Messages.getString("ProjectOpenAction.1"); //$NON-NLS-1$
      else if( eTranscendenceReadable.equals( type ) )
        return Messages.getString( "org.kalypso.project.database.client.core.base.actions.ProjectOpenAction.7" ); //$NON-NLS-1$
      else if( eTranscendenceReadableServerLocked.equals( type ) )
        return Messages.getString( "org.kalypso.project.database.client.core.base.actions.ProjectOpenAction.8" ); //$NON-NLS-1$
      else if( eTranscendenceWriteable.equals( type ) )
        return Messages.getString( "org.kalypso.project.database.client.core.base.actions.ProjectOpenAction.9" ); //$NON-NLS-1$

      throw new UnsupportedOperationException();
    }
  }

  public ProjectOpenAction( final IKalypsoModule module, final ILocalProject handler )
  {
    m_module = module;
    m_handler = handler;

    // try
// {
    if( handler instanceof ITranscendenceProject )
    {
      final ITranscendenceProject transcendence = (ITranscendenceProject)handler;
      final IRemoteProjectPreferences remotePreferences = transcendence.getRemotePreferences();
      if( remotePreferences == null )
        m_type = OPEN_TYPE.eLocal;
      else
      {
        final boolean localLock = remotePreferences.isLocked();
        final Boolean serverLock = transcendence.getBean().hasEditLock();
        if( localLock )
          m_type = OPEN_TYPE.eTranscendenceWriteable;
        else if( serverLock )
          m_type = OPEN_TYPE.eTranscendenceReadableServerLocked;
        else
          m_type = OPEN_TYPE.eTranscendenceReadable;
      }
    }
    else
    {
      throw new UnsupportedOperationException();
//
// final IRemoteProjectPreferences remotePreferences = handler.getRemotePreferences();
// if( remotePreferences == null )
// m_type = OPEN_TYPE.eLocal;
// else
// {
// final boolean onServer = remotePreferences.isOnServer();
// if( onServer )
// m_type = OPEN_TYPE.eLocalOffline;
// else
// m_type = OPEN_TYPE.eLocal;
// }
    }

// }
// catch( final CoreException e )
// {
// KalypsoProjectDatabaseClient.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
// }

  }

  @Override
  public void render( final Composite body, final FormToolkit toolkit )
  {
    final ImageHyperlink link = toolkit.createImageHyperlink( body, SWT.NULL );
    link.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );
    link.setImage( m_type.getImage() );
    link.setText( m_handler.getName() );

    link.setToolTipText( String.format( "%s (%s", m_handler.getName(), m_type.getStatus() ) ); //$NON-NLS-1$

    final Shell shell = body.getShell();

    link.addHyperlinkListener( new HyperlinkAdapter()
    {
      @Override
      public void linkActivated( final HyperlinkEvent e )
      {
        runAction( shell );
      }
    } );
  }

  protected void runAction( final Shell shell )
  {
    final String actionLabel = m_handler.getName();

    final IProject project = m_handler.getProject();

    final IStatus status = doOpenProject( project );

    if( status.isOK() || status.matches( IStatus.CANCEL ) )
      return;

    new StatusDialog( shell, status, actionLabel ).open();
  }

  private IStatus doOpenProject( final IProject project )
  {
    /* Some common checks, common to all the open actions */

    /* Validate parameters */
    if( !project.exists() )
    {
      final String message = String.format( Messages.getString("ProjectOpenAction.2"), project.getName() ); //$NON-NLS-1$
      return new Status( IStatus.ERROR, KalypsoProjectDatabaseClient.PLUGIN_ID, message );
    }

    if( !project.isOpen() )
    {
      // TODO: instead: we should ask the user if we should open the project.
      final String message = String.format( Messages.getString("ProjectOpenAction.3"), project.getName() ); //$NON-NLS-1$
      return new Status( IStatus.ERROR, KalypsoProjectDatabaseClient.PLUGIN_ID, message );
    }

    final IKalypsoModuleProjectOpenAction action = m_module.getProjectOpenAction();

    try
    {
      return action.open( project );
    }
    catch( final CoreException e )
    {
      e.printStackTrace();
      return e.getStatus();
    }
    catch( final Exception e )
    {
      final String msg = String.format( Messages.getString("ProjectOpenAction.4"), e.getLocalizedMessage() ); //$NON-NLS-1$
      return new Status( IStatus.ERROR, KalypsoProjectDatabaseClient.PLUGIN_ID, msg, e );
    }
  }
}
