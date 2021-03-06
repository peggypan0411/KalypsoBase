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
package org.kalypso.model.wspm.core.gml;

import javax.xml.namespace.QName;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.observation.IObservation;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.observation.result.TupleResultUtilities;
import org.kalypso.ogc.gml.om.IObservationFeature;
import org.kalypso.ogc.gml.om.ObservationFeatureFactory;
import org.kalypsodeegree_impl.model.feature.Feature_Impl;

/**
 * @author Gernot Belger
 */
public class WspmFixation extends Feature_Impl implements IObservationFeature
{
  public static final QName QNAME_FEATURE_WSPM_FIXATION = new QName( IWspmConstants.NS_WSPMRUNOFF, "WaterlevelFixation" ); //$NON-NLS-1$

  /* The station the waterlevel is assigned to. Required */
  public static final String COMPONENT_STATION = IWspmConstants.LENGTH_SECTION_PROPERTY_STATION;

  /* The waterlevel height. Required */
  public static final String COMPONENT_WSP = IWspmConstants.LENGTH_SECTION_PROPERTY_WATERLEVEL;

  /* Comment for this waterlevel. Optional */
  public static final String COMPONENT_COMMENT = IWspmConstants.LENGTH_SECTION_PROPERTY_TEXT;

  /* Location component of the waterlevel. Optional */
  public static final String COMPONENT_EASTING = IWspmConstants.LENGTH_SECTION_PROPERTY_EASTING;

  /* Location component of the waterlevel. Optional */
  public static final String COMPONENT_NORTHING = IWspmConstants.LENGTH_SECTION_PROPERTY_NORTHING;

  /* Runoff for this waterlevel. Optional. */
  public static final String COMPONENT_RUNOFF = IWspmConstants.LENGTH_SECTION_PROPERTY_RUNOFF;

  public WspmFixation( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  @Override
  public IObservation<TupleResult> toObservation( )
  {
    final IObservation<TupleResult> obs = ObservationFeatureFactory.toObservationInternal( this, null );
    final TupleResult result = obs.getResult();

    /* ensure that all required components are present */
    final String[] components = new String[] { COMPONENT_STATION, COMPONENT_WSP, COMPONENT_COMMENT };

    final int oldCount = result.getComponents().length;

    for( final String component : components )
      TupleResultUtilities.getOrCreateComponent( result, component );

    /* save if observation was changed */
    if( result.getComponents().length != oldCount )
      saveObservation( obs );

    return obs;
  }

  @Override
  public void saveObservation( final IObservation<TupleResult> observation )
  {
    ObservationFeatureFactory.toFeature( observation, this );
  }

  @Override
  public WspmWaterBody getOwner( )
  {
    return (WspmWaterBody)super.getOwner();
  }
}