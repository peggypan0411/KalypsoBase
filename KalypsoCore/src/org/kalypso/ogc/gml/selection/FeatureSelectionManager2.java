/*--------------- Kalypso-Header ------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 --------------------------------------------------------------------------*/

package org.kalypso.ogc.gml.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Gernot Belger
 */
public class FeatureSelectionManager2 implements IFeatureSelectionManager
{
  /** feature -> easyWrapper */
  private final Map<Feature, EasyFeatureWrapper> m_map = new HashMap<>();

  private final Set<IFeatureSelectionListener> m_listener = new LinkedHashSet<>( 5 );

  @Override
  public void setSelection( final EasyFeatureWrapper[] selectedFeatures )
  {
    final EasyFeatureWrapper[] allFeatures = getAllFeatures();
    if( Arrays.equals( selectedFeatures, allFeatures ) )
      return;

    final Set<EasyFeatureWrapper> newState = new HashSet<>();
    Collections.addAll( newState, selectedFeatures );
    if( newState.equals( m_map.keySet() ) )
      return;

    m_map.clear();

    for( final EasyFeatureWrapper wrapper : selectedFeatures )
    {
      m_map.put( wrapper.getFeature(), wrapper );
    }

    fireSelectionChanged();
  }

  @Override
  public void changeSelection( final Feature[] featuresToRemove, final EasyFeatureWrapper[] featuresToAdd )
  {
    final Set<Feature> oldState = new HashSet<>( m_map.keySet() );

    for( final Feature f : featuresToRemove )
      m_map.remove( f );

    for( final EasyFeatureWrapper wrapper : featuresToAdd )
      m_map.put( wrapper.getFeature(), wrapper );

    if( !m_map.keySet().equals( oldState ) )
      fireSelectionChanged();
  }

  private void fireSelectionChanged( )
  {
    final IFeatureSelectionListener[] ls = m_listener.toArray( new IFeatureSelectionListener[m_listener.size()] );
    for( final IFeatureSelectionListener listener : ls )
      listener.selectionChanged( this, this );
  }

  @Override
  public void clear( )
  {
    if( !m_map.isEmpty() )
    {
      m_map.clear();
      fireSelectionChanged();
    }
  }

  @Override
  public void addSelectionListener( final IFeatureSelectionListener l )
  {
    m_listener.add( l );
  }

  @Override
  public void removeSelectionListener( final IFeatureSelectionListener l )
  {
    m_listener.remove( l );
  }

  @Override
  public CommandableWorkspace getWorkspace( final Feature feature )
  {
    final EasyFeatureWrapper wrapper = getWrapper( feature );
    if( wrapper == null )
      return null;

    return wrapper.getWorkspace();
  }

  private EasyFeatureWrapper getWrapper( final Feature feature )
  {
    return m_map.get( feature );
  }

  public Feature getParentFeature( final Feature feature )
  {
    final EasyFeatureWrapper wrapper = getWrapper( feature );
    if( wrapper == null )
      return null;

    return wrapper.getParentFeature();
  }

  public IRelationType getParentFeatureProperty( final Feature feature )
  {
    final EasyFeatureWrapper wrapper = getWrapper( feature );
    if( wrapper == null )
      return null;

    return wrapper.getParentFeatureProperty();
  }

  @Override
  public Object getFirstElement( )
  {
    final Set<Feature> keySet = m_map.keySet();
    if( !keySet.isEmpty() )
      return keySet.iterator().next();
    return null;

  }

  @Override
  public Iterator<Feature> iterator( )
  {
    return m_map.keySet().iterator();
  }

  @Override
  public int size( )
  {
    return m_map.size();
  }

  @Override
  public Object[] toArray( )
  {
    return m_map.keySet().toArray();
  }

  @Override
  public List<Feature> toList( )
  {
    return new ArrayList<>( m_map.keySet() );
  }

  @Override
  public boolean isEmpty( )
  {
    return m_map.isEmpty();
  }

  @Override
  public EasyFeatureWrapper[] getAllFeatures( )
  {
    return m_map.values().toArray( new EasyFeatureWrapper[m_map.size()] );
  }

  @Override
  public IFeatureSelectionManager getSelectionManager( )
  {
    return this;
  }

  @Override
  public Feature getFocusedFeature( )
  {
    // The feature manager doesn't support the focused feature
    return null;
  }

  @Override
  public IPropertyType getFocusedProperty( )
  {
    // The feature manager doesn't support the focused feature
    return null;
  }

  @Override
  public String toString( )
  {
    return m_map.toString();
  }

  @Override
  public boolean isSelected( final Feature feature )
  {
    return m_map.containsKey( feature );
  }
}
