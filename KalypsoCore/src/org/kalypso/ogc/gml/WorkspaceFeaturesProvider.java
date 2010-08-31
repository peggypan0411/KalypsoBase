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
import org.kalypso.ogc.gml.painter.ResolvedFeatureList;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.event.ModellEvent;
import org.kalypsodeegree.model.feature.event.ModellEventListener;
import org.kalypsodeegree_impl.model.feature.FeatureFactory;

/**
 * @author Gernot Belger
 */
public class WorkspaceFeaturesProvider extends AbstractFeaturesProvider
{
  private final ModellEventListener m_workspaceListener = new ModellEventListener()
  {
    @Override
    public void onModellChange( final ModellEvent modellEvent )
    {
      handleModellChanged( modellEvent );
    }
  };

  private final String m_featurePath;

  private FeatureList m_featureList;

  private IFeatureType m_featureType;

  private List<Feature> m_features;

  private final CommandableWorkspace m_workspace;

  public WorkspaceFeaturesProvider( final CommandableWorkspace workspace, final String featurePath )
  {
    m_workspace = workspace;

    m_featurePath = featurePath;

    final Object featureFromPath = workspace.getFeatureFromPath( m_featurePath );

    if( featureFromPath instanceof FeatureList )
    {
      m_featureList = (FeatureList) featureFromPath;
      m_featureType = workspace.getFeatureTypeFromPath( m_featurePath );
      m_features = new ResolvedFeatureList( workspace, m_featureList );
    }
    else if( featureFromPath instanceof Feature )
    {
      final Feature singleFeature = (Feature) featureFromPath;
      final Feature parent = singleFeature.getOwner();
      m_featureList = FeatureFactory.createFeatureList( parent, singleFeature.getParentRelation() );
      m_featureList.add( singleFeature );
      m_featureType = singleFeature.getFeatureType();
      m_features = Collections.singletonList( singleFeature );
    }
    else
    {
      m_featureList = null;
      m_featureType = null;
      m_features = null;
// FIXME: Should'nt we throw an exception here?
//        setStatus( new Status( IStatus.WARNING, KalypsoCorePlugin.getID(), Messages.getString( "org.kalypso.ogc.gml.PoolFeaturesProvider.5", m_featurePath ) ) ); //$NON-NLS-1$
    }

    workspace.addModellListener( m_workspaceListener );
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
