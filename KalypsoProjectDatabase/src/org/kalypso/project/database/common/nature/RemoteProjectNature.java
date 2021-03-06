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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;

/**
 * ProjectNature of remote Kalypso Projects
 *
 * @author Dirk Kuch
 */
public class RemoteProjectNature implements IProjectNature
{
  private static Map<IProject, ProjectScope> SCOPES = new HashMap<>();

  private static Map<IProject, IPreferenceChangeListener> LISTENER = new HashMap<>();

  public static final String NATURE_ID = "org.kalypso.project.database.project.nature"; //$NON-NLS-1$

  private static final String PREFERENCES = "org.kalypso.project.database"; //$NON-NLS-1$

  private IProject m_project = null;

  @Override
  public void configure( )
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void deconfigure( )
  {
    // TODO Auto-generated method stub
  }

  @Override
  public IProject getProject( )
  {
    return m_project;
  }

  @Override
  public void setProject( final IProject project )
  {
    m_project = project;
  }

  public IRemoteProjectPreferences getRemotePreferences( final IProject project, final IPreferenceChangeListener listener )
  {
    ProjectScope scope = SCOPES.get( project );
    if( scope == null )
    {
      scope = new ProjectScope( project );
      SCOPES.put( project, scope );

      final IEclipsePreferences node = scope.getNode( PREFERENCES );
      if( listener != null )
      {
        node.addPreferenceChangeListener( listener );
      }

    }

    final IEclipsePreferences node = scope.getNode( PREFERENCES );
    final IPreferenceChangeListener oldListener = LISTENER.get( project );
    if( oldListener != null )
    {
      node.removePreferenceChangeListener( oldListener );
    }

    if( listener != null )
    {
      node.addPreferenceChangeListener( listener );
      LISTENER.put( project, listener );
    }

    return new RemoteProjectPreferencesHandler( node );
  }

}
