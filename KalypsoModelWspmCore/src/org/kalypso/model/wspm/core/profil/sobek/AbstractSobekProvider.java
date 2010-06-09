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
package org.kalypso.model.wspm.core.profil.sobek;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.kalypso.model.wspm.core.KalypsoModelWspmCorePlugin;
import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.core.profil.sobek.profiles.SobekProfile;

/**
 * This class provides base functionality for creating sobek profiles.
 * 
 * @author Holger Albert
 */
public abstract class AbstractSobekProvider
{
  /**
   * The constructor.
   */
  protected AbstractSobekProvider( )
  {
  }

  /**
   * This function returns the profiles, which will be iterated, to help create the sobek profiles.
   * 
   * @return The profiles, which will be iterated, to help create the sobek profiles.
   */
  protected abstract List<IProfileFeature> getProfiles( );

  /**
   * This function returns the sobek profiles. <br>
   * <br>
   * <strong>HINT:</string> This may take long, dependend on the implementation.
   * 
   * @param monitor
   *          A progress monitor.
   * @return The sobek profiles.
   */
  public List<SobekProfile> getSobekProfiles( IProgressMonitor monitor ) throws CoreException
  {
    /* Monitor. */
    if( monitor == null )
      monitor = new NullProgressMonitor();

    try
    {
      /* Get the profiles. */
      List<IProfileFeature> profiles = getProfiles();

      /* Monitor. */
      monitor.beginTask( "Converting profiles...", profiles.size() * 100 );
      monitor.subTask( "Converting profiles..." );

      /* Memory for the results. */
      List<SobekProfile> results = new ArrayList<SobekProfile>();

      for( int i = 0; i < profiles.size(); i++ )
      {
        /* Monitor. */
        if( monitor.isCanceled() )
          throw new CoreException( new Status( IStatus.CANCEL, KalypsoModelWspmCorePlugin.getID(), "The operation was canceled by the user..." ) );

        /* Get the profile. */
        IProfileFeature profileFeature = profiles.get( i );

        /* Convert the profile into a sobek profile. */
        SobekProfile sobekProfile = convertProfile( profileFeature );

        /* Add to the results. */
        results.add( sobekProfile );

        /* Monitor. */
        monitor.worked( 100 );
      }

      return results;
    }
    finally
    {
      /* Monitor. */
      monitor.done();
    }
  }

  /**
   * This function converts the profile to a sobek profile.
   * 
   * @param profile
   *          The profile.
   * @return The sobek profile.
   */
  protected abstract SobekProfile convertProfile( IProfileFeature profile );
}