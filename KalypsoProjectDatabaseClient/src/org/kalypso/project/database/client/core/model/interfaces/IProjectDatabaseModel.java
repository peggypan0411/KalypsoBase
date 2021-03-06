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
package org.kalypso.project.database.client.core.model.interfaces;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.kalypso.module.project.IProjectHandle;
import org.kalypso.project.database.client.core.model.remote.IRemoteProjectsListener;
import org.kalypso.project.database.common.interfaces.IProjectDatabaseListener;

/**
 * @author Dirk Kuch
 */
public interface IProjectDatabaseModel
{
  public void addListener( final IProjectDatabaseListener listener );

  public void addRemoteListener( final IRemoteProjectsListener listener );

  public void removeRemoteListener( final IRemoteProjectsListener listener );

  public void removeListener( final IProjectDatabaseListener listener );

  public IStatus getConnectionState( );

  public void setRemoteProjectsDirty( );

  public IProjectHandle[] getProjects( );

// public IProjectHandler[] getProjects( final IProjectDatabaseFilter filter );

  public ILocalWorkspaceModel getLocalWorkspaceModel( );

  public IRemoteWorkspaceModel getRemoteWorkspaceModel( );

  /**
   * @return {@link IProjectDatabaseModel} project representation of a "local" IProject
   */
  public IProjectHandle findProject( IProject project );

  public IProjectHandle getProject( String name );

}
