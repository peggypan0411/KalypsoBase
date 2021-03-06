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

import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.model.wspm.core.gml.classifications.IWspmClassification;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.IFeatureBindingCollection;

/**
 * @author Dirk Kuch
 */
public interface IWspmProject extends Feature
{
  QName QN_MEMBER_WATER_BODY = new QName( IWspmConstants.NS_WSPM, "waterBodyMember" ); //$NON-NLS-1$

  QName QN_TYPE = new QName( IWspmConstants.NS_WSPMPROJ, "WspmProject" ); //$NON-NLS-1$

  QName QN_CLASSIFICATION_MEMBER = new QName( IWspmConstants.NS_WSPM_CLASSIFICATIONS, "classificationMember" ); //$NON-NLS-1$

  IFeatureBindingCollection<WspmWaterBody> getWaterBodies( );

  WspmWaterBody findWater( String waterName );

  WspmWaterBody findWaterByRefNr( String refNr );

  /**
   * Create a new {@link WspmWaterBody} with a given name and adds it to this project. <br/>
   * If a waterBody with the same name is already present, this will be returned instead.
   */
  WspmWaterBody createOrGetWaterBody( String name, boolean isDirectionUpstreams ) throws GMLSchemaException;

  WspmWaterBody createOrGetWaterBodyByRefNr( String refNr, boolean isDirectionUpstreams ) throws GMLSchemaException;

  /**
   * Creates a new {@link WspmWaterBody} with a given name. This method always creates a new element.
   */
  WspmWaterBody createWaterBody( String name, boolean isDirectionUpstreams ) throws GMLSchemaException;

  WspmWaterBody createWaterBodyByRefNr( String refNr, boolean isDirectionUpstreams ) throws GMLSchemaException;

  IWspmClassification getClassificationMember( );

  IWspmClassification createClassificationMember( );
}
