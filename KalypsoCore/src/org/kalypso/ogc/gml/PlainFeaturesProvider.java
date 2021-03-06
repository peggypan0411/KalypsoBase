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
package org.kalypso.ogc.gml;

import java.util.Collections;
import java.util.List;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.event.ModellEvent;
import org.kalypsodeegree.model.feature.event.ModellEventListener;
import org.kalypsodeegree_impl.model.feature.FeatureFactory;
import org.kalypsodeegree_impl.model.feature.FeaturePath;

/**
 * @author Gernot Belger
 */
public class PlainFeaturesProvider extends AbstractFeaturesProvider
{
  private final ModellEventListener m_workspaceListener = new ModellEventListener()
  {
    @Override
    public void onModellChange( final ModellEvent modellEvent )
    {
      handleModellChanged( modellEvent );
    }
  };

  private final FeatureList m_featureList;

  private final IFeatureType m_featureType;

  private final List<Feature> m_features;

  private final CommandableWorkspace m_workspace;

  private final String m_featurePath;

  public PlainFeaturesProvider( final CommandableWorkspace workspace, final Feature feature )
  {
    m_workspace = workspace;
    m_featurePath = new FeaturePath( feature ).toString();

    final Feature parent = feature.getOwner();
    m_featureList = FeatureFactory.createFeatureList( parent, feature.getParentRelation() );
    m_featureList.add( feature );
    m_featureType = feature.getFeatureType();
    m_features = Collections.singletonList( feature );

    workspace.addModellListener( m_workspaceListener );
  }

  public PlainFeaturesProvider( final CommandableWorkspace workspace )
  {
    m_workspace = workspace;
    m_featurePath = null;
    m_featureList = null;
    m_featureType = null;
    m_features = Collections.emptyList();
  }

  @Override
  public String getFeaturePath( )
  {
    return m_featurePath;
  }

  /**
   * @see org.kalypso.ogc.gml.IFeaturesProvider#getFeatureType()
   */
  @Override
  public IFeatureType getFeatureType( )
  {
    return m_featureType;
  }

  /**
   * @see org.kalypso.ogc.gml.IFeaturesProvider#getFeatures()
   */
  @Override
  public FeatureList getFeatureList( )
  {
    return m_featureList;
  }

  /**
   * @see org.kalypso.ogc.gml.IFeaturesProvider#getFeatures()
   */
  @Override
  public List<Feature> getFeatures( )
  {
    return m_features;
  }

  /**
   * @see org.kalypso.ogc.gml.IFeaturesProvider#getWorkspace()
   */
  @Override
  public CommandableWorkspace getWorkspace( )
  {
    return m_workspace;
  }

  protected void handleModellChanged( final ModellEvent event )
  {
    fireFeaturesChanged( event );
  }
}
