/** This file is part of kalypso/deegree.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * history:
 *
 * Files in this package are originally taken from deegree and modified here
 * to fit in kalypso. As goals of kalypso differ from that one in deegree
 * interface-compatibility to deegree is wanted but not retained always.
 *
 * If you intend to use this software in other ways than in kalypso
 * (e.g. OGC-web services), you should consider the latest version of deegree,
 * see http://www.deegree.org .
 *
 * all modifications are licensed as deegree,
 * original copyright:
 *
 * Copyright (C) 2001 by:
 * EXSE, Department of Geography, University of Bonn
 * http://www.giub.uni-bonn.de/exse/
 * lat/lon GmbH
 * http://www.lat-lon.de
 */
package org.kalypsodeegree_impl.model.feature.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;

/**
 * This visitor collects all Feature with the property specified in the QName and the matching value in the map. It is
 * also possible to look for properties which are null. To achiev this just add null to the HashMap<K,V> for the value.
 *
 * @author kuepfer
 */
public class CollectFeaturesWithProperty implements FeatureVisitor
{

  private final HashMap<QName, Object> m_qNames;

  private final List<Feature> m_resulte;

  public CollectFeaturesWithProperty( final HashMap<QName, Object> qNames, final List<Feature> result )
  {
    m_qNames = qNames;
    if( result == null )
      m_resulte = new ArrayList<>();
    else
      m_resulte = result;
  }

  /**
   * @see org.kalypsodeegree.model.feature.FeatureVisitor#visit(org.kalypsodeegree.model.feature.Feature)
   */
  @Override
  public boolean visit( final Feature f )
  {
    final IFeatureType featureType = f.getFeatureType();
    for( final Object element : m_qNames.keySet() )
    {
      final QName qName = (QName) element;
      final IPropertyType property = featureType.getProperty( qName );
      if( property != null )
      {
        final Object value = f.getProperty( qName );
        final Object testValue = m_qNames.get( qName );
        if( testValue != null && testValue.equals( value ) )
          m_resulte.add( f );
      }
    }
    return true;
  }

  public Feature[] getFeatures( )
  {
    return m_resulte.toArray( new Feature[m_resulte.size()] );
  }
}
