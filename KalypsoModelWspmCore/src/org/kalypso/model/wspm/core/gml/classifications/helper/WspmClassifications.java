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
package org.kalypso.model.wspm.core.gml.classifications.helper;

import java.math.BigDecimal;

import org.kalypso.commons.java.lang.Objects;
import org.kalypso.commons.java.lang.Strings;
import org.kalypso.model.wspm.core.IWspmPointProperties;
import org.kalypso.model.wspm.core.gml.WspmProject;
import org.kalypso.model.wspm.core.gml.classifications.IRoughnessClass;
import org.kalypso.model.wspm.core.gml.classifications.IVegetationClass;
import org.kalypso.model.wspm.core.gml.classifications.IWspmClassification;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;

/**
 * @author Dirk Kuch
 */
public final class WspmClassifications
{
  private WspmClassifications( )
  {
  }

  public static IWspmClassification getClassification( final IProfil profile )
  {
    final Object source = profile.getSource();
    if( !(source instanceof Feature) )
      return null;

    final Feature feature = (Feature) source;
    final GMLWorkspace workspace = feature.getWorkspace();
    final Feature root = workspace.getRootFeature();
    if( !(root instanceof WspmProject) )
      return null;

    final WspmProject project = (WspmProject) root;

    return project.getClassificationMember();
  }

  public static Double findRoughnessValue( final IProfil profile, final IRecord point, final IComponent component, final Double plainValue )
  {
    final IComponent componentRoughnessClass = profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_ROUGHNESS_CLASS );
    if( Objects.isNull( componentRoughnessClass ) )
      return plainValue;

    final IWspmClassification classification = WspmClassifications.getClassification( profile );
    if( Objects.isNull( classification ) )
      return plainValue;

    final String clazzName = (String) point.getValue( componentRoughnessClass );
    if( Strings.isEmpty( clazzName ) )
      return plainValue;

    final IRoughnessClass clazz = classification.findRoughnessClass( clazzName );
    if( Objects.isNull( clazz ) )
      return plainValue;

    final BigDecimal value = clazz.getValue( component.getId() );
    if( Objects.isNotNull( value ) )
      return value.doubleValue();

    return plainValue;
  }

  public static Double findVegetationValue( final IProfil profile, final IRecord point, final IComponent component, final Double plainValue )
  {
    final IComponent componentVegetationClass = profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_BEWUCHS_CLASS );
    if( Objects.isNull( componentVegetationClass ) )
      return plainValue;

    final IWspmClassification classification = WspmClassifications.getClassification( profile );
    if( Objects.isNull( classification ) )
      return plainValue;

    final String clazzName = (String) point.getValue( componentVegetationClass );
    if( Strings.isEmpty( clazzName ) )
      return plainValue;

    final IVegetationClass clazz = classification.findVegetationClass( clazzName );
    if( Objects.isNull( clazz ) )
      return plainValue;

    final BigDecimal value = clazz.getValue( component.getId() );
    if( Objects.isNotNull( value ) )
      return value.doubleValue();

    return plainValue;
  }

  public static boolean hasVegetationProperties( final IProfil profile )
  {
    if( Objects.isNull( profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_BEWUCHS_AX ) ) )
      return false;
    else if( Objects.isNull( profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_BEWUCHS_AY ) ) )
      return false;
    else if( Objects.isNull( profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_BEWUCHS_DP ) ) )
      return false;

    return true;
  }

  public static boolean hasVegetationClass( final IProfil profile )
  {
    return Objects.isNotNull( profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_BEWUCHS_CLASS ) );
  }

  public static boolean hasRoughnessProperties( final IProfil profile )
  {
    if( Objects.isNotNull( profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_RAUHEIT_KS ) ) )
      return true;
    else if( Objects.isNotNull( profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_RAUHEIT_KST ) ) )
      return true;

    return false;
  }

  public static boolean hasRoughnessClass( final IProfil profile )
  {
    return Objects.isNotNull( profile.hasPointProperty( IWspmPointProperties.POINT_PROPERTY_ROUGHNESS_CLASS ) );
  }

}
