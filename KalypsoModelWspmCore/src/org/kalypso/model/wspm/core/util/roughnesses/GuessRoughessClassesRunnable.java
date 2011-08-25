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
package org.kalypso.model.wspm.core.util.roughnesses;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.model.wspm.core.IWspmPointProperties;
import org.kalypso.model.wspm.core.KalypsoModelWspmCorePlugin;
import org.kalypso.model.wspm.core.gml.classifications.IRoughnessClass;
import org.kalypso.model.wspm.core.gml.classifications.IWspmClassification;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;

/**
 * Guess roughness class from existing ks / kst value
 * 
 * @author Dirk Kuch
 */
public class GuessRoughessClassesRunnable implements ICoreRunnableWithProgress
{

  private final IProfil m_profile;

  private final String m_property;

  private final boolean m_overwriteValues;

  private final Double m_delta;

  public GuessRoughessClassesRunnable( final IProfil profile, final String property, final boolean overwriteValues, final Double delta )
  {
    m_profile = profile;
    m_property = property;
    m_overwriteValues = overwriteValues;
    m_delta = delta;
  }

  /**
   * @see org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress#execute(org.eclipse.core.runtime.IProgressMonitor)
   */
  @SuppressWarnings("deprecation")
  @Override
  public IStatus execute( final IProgressMonitor monitor )
  {
    final IComponent property = m_profile.hasPointProperty( m_property );
    if( Objects.isNull( property ) )
      return new Status( IStatus.CANCEL, KalypsoModelWspmCorePlugin.getID(), String.format( "Can't update profile %.3f km. Missing point property: %s", m_profile.getStation(), m_property ) );

    final IComponent propertyClazz = m_profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_ROUGHNESS_CLASS );
    if( Objects.isNull( propertyClazz ) )
      return new Status( IStatus.CANCEL, KalypsoModelWspmCorePlugin.getID(), String.format( "Can't update profile %.3f km. Missing point property: %s", m_profile.getStation(), IWspmPointProperties.POINT_PROPERTY_ROUGHNESS_CLASS ) );

    final IWspmClassification clazzes = RoughnessClassHelper.getClassification( m_profile );
    if( Objects.isNull( clazzes ) )
      return new Status( IStatus.CANCEL, KalypsoModelWspmCorePlugin.getID(), String.format( "Missing profile feature for profile %.3f km.", m_profile.getStation() ) );

    final List<IStatus> statis = new ArrayList<IStatus>();

    final IRecord[] points = m_profile.getPoints();
    for( final IRecord point : points )
    {
      final Double value = (Double) point.getValue( property );
      if( Objects.isNull( value ) )
      {
        final Double width = (Double) point.getValue( m_profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_BREITE ) );
        final IStatus status = new Status( IStatus.WARNING, KalypsoModelWspmCorePlugin.getID(), String.format( "Missing ks value - point: %.3f", width ) );
        statis.add( status );

        continue;
      }

      final IRoughnessClass clazz = findMatchingClass( clazzes, value );
      if( Objects.isNull( clazz ) )
      {
        final Double width = (Double) point.getValue( m_profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_BREITE ) );
        final IStatus status = new Status( IStatus.WARNING, KalypsoModelWspmCorePlugin.getID(), String.format( "Didn't found matching roughness class for %s value %.3f on point: %.3f", m_property, value, width ) );
        statis.add( status );

        continue;
      }

      if( isWritable( point, propertyClazz ) )
        point.setValue( propertyClazz, clazz.getName() );
    }

    return StatusUtilities.createStatus( statis, String.format( "Updated roughness classes from roughness values on profile %.3f", m_profile.getStation() ) );
  }

  private boolean isWritable( final IRecord point, final IComponent propertyClazz )
  {
    if( m_overwriteValues )
      return true;

    return Objects.isNull( point.getValue( propertyClazz ) );
  }

  private IRoughnessClass findMatchingClass( final IWspmClassification clazzes, final Double value )
  {
    IRoughnessClass ptr = null;
    double ptrDiff = Double.MAX_VALUE;

    final IRoughnessClass[] roughnesses = clazzes.getRoughnessClasses();
    for( final IRoughnessClass roughness : roughnesses )
    {
      final Double v = roughness.getValue( m_property );

      /* roughness is in range? */
      final double delta = Math.abs( v - value );
      if( delta == 0.0 )
        return roughness;

      if( delta > m_delta )
        continue;

      if( delta < ptrDiff )
      {
        ptrDiff = delta;
        ptr = roughness;
      }
    }

    return ptr;
  }
}
