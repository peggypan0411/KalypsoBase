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
package org.kalypso.project.database.common.nature;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.kalypso.commons.java.lang.Strings;

class RemoteProjectPreferencesHandler implements IRemoteProjectPreferences
{
  private final WorkspaceJob m_flushJob = new WorkspaceJob( "" ) //$NON-NLS-1$
  {
    @Override
    public IStatus runInWorkspace( final IProgressMonitor monitor )
    {
      try
      {
        m_node.flush();
      }
      catch( final Exception e )
      {
      }
      return Status.OK_STATUS;
    }
  };

  private static final String PROJECT_LOCK_TICKET = "project.lock"; //$NON-NLS-1$

  private static final String PROJECT_IS_MODIFIED = "project.is.modified"; //$NON-NLS-1$

  private static final String PROJECT_CHANGES_COMMITED = "project.changes.commited"; //$NON-NLS-1$

  private static final String PROJECT_IS_ON_SERVER = "project.is.on.server"; //$NON-NLS-1$

  private static final String PROJECT_DOWNLOADED_VERSION = "project.downloaded.version"; //$NON-NLS-1$

  private static final String REMOTE_PROJECT_TYPE = "project.remote.type"; //$NON-NLS-1$

  protected final IEclipsePreferences m_node;

  protected RemoteProjectPreferencesHandler( final IEclipsePreferences node )
  {
    m_node = node;
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#isLocked()
   */
  @Override
  public boolean isLocked( )
  {
    return Strings.isNotEmpty( getEditTicket() );
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#setEditTicket(java.lang.String)
   */
  @Override
  public void setEditTicket( final String ticket )
  {
    // FIXME this should never happen
    if( m_node == null )
      return;

    m_node.put( PROJECT_LOCK_TICKET, ticket );
    flush();
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#getEditTicket()
   */
  @Override
  public String getEditTicket( )
  {
    try
    {
      return m_node.get( PROJECT_LOCK_TICKET, null );
    }
    catch( final Throwable t )
    {
      return null;
    }
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#isOnServer()
   */
  @Override
  public boolean isOnServer( )
  {
    return m_node.getBoolean( PROJECT_IS_ON_SERVER, false );
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#isOnServer()
   */
  @Override
  public Integer getVersion( )
  {
    try
    {
      return Integer.valueOf( m_node.get( PROJECT_DOWNLOADED_VERSION, "-1" ) ); //$NON-NLS-1$  
    }
    catch( final Throwable t )
    {
      return -1;
    }

  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#setIsOnServer(boolean)
   */
  @Override
  public void setIsOnServer( final boolean onServer )
  {
    m_node.putBoolean( PROJECT_IS_ON_SERVER, Boolean.valueOf( onServer ) );
    flush();
  }

  private void flush( )
  {
    m_flushJob.schedule();
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#setVersion(java.lang.Integer)
   */
  @Override
  public void setVersion( final Integer version )
  {
    m_node.put( PROJECT_DOWNLOADED_VERSION, version.toString() );
    flush();
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#getProjectType()
   */
  @Override
  public String getProjectType( )
  {
    return m_node.get( REMOTE_PROJECT_TYPE, "blub" ); //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#setProjectType(java.lang.String)
   */
  @Override
  public void setProjectType( final String projectType )
  {
    m_node.put( REMOTE_PROJECT_TYPE, projectType );
    flush();
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#getChangesCommited()
   */
  @Override
  public boolean getChangesCommited( )
  {
    final String value = m_node.get( PROJECT_CHANGES_COMMITED, Boolean.FALSE.toString() );

    return Boolean.valueOf( value );
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#setChangesCommited(boolean)
   */
  @Override
  public void setChangesCommited( final boolean commited )
  {
    m_node.put( PROJECT_CHANGES_COMMITED, Boolean.valueOf( commited ).toString() );
    if( Boolean.TRUE.equals( commited ) )
    {
      setModified( false );
    }

    flush();
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#isModified()
   */
  @Override
  public boolean isModified( )
  {
    final String value = m_node.get( PROJECT_IS_MODIFIED, Boolean.FALSE.toString() );
    return Boolean.valueOf( value );
  }

  /**
   * @see org.kalypso.project.database.common.nature.IRemoteProjectPreferences#setModified(boolean)
   */
  @Override
  public void setModified( final boolean value )
  {
    m_node.put( PROJECT_IS_MODIFIED, Boolean.valueOf( value ).toString() );
    flush();
  }
}
