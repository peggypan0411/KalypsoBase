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
package org.kalypso.module.project.local;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kalypso.module.project.IProjectHandle;

/**
 * @author Dirk Kuch
 */
public class ProjectItemModel // extends AbstractProjectHandleProvider implements IProjectHandleProvder
{
  private final Set<IProjectHandlesChangedListener> m_listener = new LinkedHashSet<>();

  private final IProjectHandleProvider[] m_itemProviders;

  private final IProjectHandlesChangedListener m_itemChangeListener = new IProjectHandlesChangedListener()
  {
    @Override
    public void itemsChanged( )
    {
      handleItemsChanged();
    }
  };

  public ProjectItemModel( )
  {
    // FIXME:
    m_itemProviders = null; // new IProjectHandleProvder[] { new LocalWorkspaceItemProvider() };

// m_itemProviders = ModuleWorkspaceExtensions.getItemProviders();
    for( final IProjectHandleProvider provider : m_itemProviders )
      provider.addProviderChangedListener( m_itemChangeListener );
  }

  public void dispose( )
  {
    for( final IProjectHandleProvider provider : m_itemProviders )
      provider.removeProviderChangedListener( m_itemChangeListener );
  }

// synchronized private void buildProjectList( )
// {
// final ILocalProject[] local = m_local.getProjects();
// IRemoteProject[] remote = new IRemoteProject[] {};
//
// if( m_remote != null )
// {
// final Set<IRemoteProject> handler = new HashSet<IRemoteProject>();
//
// final KalypsoProjectBean[] beans = m_remote.getBeans();
// for( final KalypsoProjectBean bean : beans )
// {
// handler.add( new RemoteProjectHandler( bean ) );
// }
//
// remote = handler.toArray( new IRemoteProject[] {} );
// }
//
// for( final ILocalProject handler : local )
// {
// if( ArrayUtils.contains( remote, handler ) )
// {
// final int index = ArrayUtils.indexOf( remote, handler );
//
// final IRemoteProject r = remote[index];
// remote = (IRemoteProject[]) ArrayUtils.remove( remote, index );
//
// m_projects.add( new TranscendenceProjectHandler( handler, r ) );
// }
// else
// {
// m_projects.add( handler );
// }
// }
//
// for( final IRemoteProject r : remote )
// {
// m_projects.add( r );
// }
// }

  public synchronized IProjectHandle[] getProjects( )
  {
    final Collection<IProjectHandle> items = new ArrayList<>();
    for( final IProjectHandleProvider provider : m_itemProviders )
    {
      final IProjectHandle[] projects = provider.getProjects();
      items.addAll( Arrays.asList( projects ) );
    }
    return items.toArray( new IProjectHandle[items.size()] );

// if( m_projects.isEmpty() )
// buildProjectList();
//
// return m_projects.toArray( new IProjectItem[] {} );
  }

  protected void handleItemsChanged( )
  {
    for( final IProjectHandlesChangedListener listener : m_listener )
      listener.itemsChanged();
  }

// /**
// * @see org.kalypso.project.database.client.core.model.local.ILocalWorkspaceListener#localWorkspaceChanged()
// */
// @Override
// public void localWorkspaceChanged( )
// {
// buildProjectList();
//
// for( final IProjectDatabaseListener listener : m_listener )
// {
// listener.projectModelChanged();
// }
// }

// /**
// * @see org.kalypso.project.database.client.core.model.remote.IRemoteWorkspaceListener#remoteWorkspaceChanged()
// */
// @Override
// public void remoteWorkspaceChanged( )
// {
// buildProjectList();
//
// for( final IProjectDatabaseListener listener : m_listener )
// {
// listener.projectModelChanged();
// }
// }

// @Override
// public void addRemoteListener( final IRemoteProjectsListener listener )
// {
// if( m_remote != null )
// {
// m_remote.addListener( listener );
// }
// }

// @Override
// public void removeRemoteListener( final IRemoteProjectsListener listener )
// {
// if( m_remote != null )
// {
// m_remote.removeListener( listener );
// }
// }

// @Override
// public IProjectItem[] getProjects( final IProjectDatabaseFilter filter )
// {
// final Set<IProjectItem> myProjects = new HashSet<IProjectItem>();
//
// final IProjectItem[] projects = getProjects();
// for( final IProjectItem handler : projects )
// {
// if( filter.select( handler ) )
// {
// myProjects.add( handler );
// }
// }
//
// return myProjects.toArray( new IProjectItem[] {} );
// }

// @Override
// public void setRemoteProjectsDirty( )
// {
// if( m_remote != null )
// {
// m_remote.setDirty();
// }
// }

// @Override
// public IStatus getRemoteConnectionState( )
// {
// if( m_remote != null )
// return m_remote.getRemoteConnectionState();
//
//    return StatusUtilities.createWarningStatus( Messages.getString( "org.kalypso.project.database.client.core.model.ProjectDatabaseModel.0" ) ); //$NON-NLS-1$
// }

// /**
// * @see org.kalypso.project.database.client.core.model.remote.IRemoteProjectsListener#remoteConnectionChanged()
// */
// @Override
// public void remoteConnectionChanged( final IStatus connectionState )
// {
// buildProjectList();
//
// for( final IProjectDatabaseListener listener : m_listener )
// {
// listener.projectModelChanged();
// }
// }

// /**
// * @see
// org.kalypso.project.database.client.core.model.interfaces.IProjectDatabaseModel#findProject(org.eclipse.core.resources.IProject)
// */
// @Override
// public IProjectItem findProject( final IProject project )
// {
// final IProjectItem[] projects = getProjects();
// for( final IProjectItem handler : projects )
// {
// if( handler.equals( project ) )
// return handler;
// }
//
// return null;
// }

// /**
// * @see org.kalypso.project.database.client.core.model.interfaces.IProjectDatabaseModel#getLocalWorkspaceModel()
// */
// @Override
// public ILocalWorkspaceModel getLocalWorkspaceModel( )
// {
// return m_local;
// }

// /**
// * @see org.kalypso.project.database.client.core.model.interfaces.IProjectDatabaseModel#getRemoteWorkspaceModel()
// */
// @Override
// public IRemoteWorkspaceModel getRemoteWorkspaceModel( )
// {
// return m_remote;
// }

// /**
// * @see org.kalypso.project.database.client.core.model.interfaces.IProjectDatabaseModel#getProject(java.lang.String)
// */
// @Override
// public IProjectItem getProject( String unique )
// {
//    if( unique.startsWith( "/" ) ) //$NON-NLS-1$
// {
// unique = unique.substring( 1 );
// }
//
// final IProjectItem[] projects = getProjects();
// for( final IProjectItem project : projects )
// {
// if( project.getUniqueName().equals( unique ) )
// return project;
// }
//
// return null;
// }
}
