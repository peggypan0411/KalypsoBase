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
package org.kalypsodeegree_impl.model.feature.index;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;

/**
 * This visitor hashes all features by one property. The property should be indexable (must have unique values),
 * otherwise some values could be missing, because they uses the same key then.<br>
 * Should the property be null, the visitor will not visit any features and the map will be empty than.
 * 
 * @author Holger Albert
 */
public class IndexByPropertyVisitor implements FeatureVisitor
{
  /**
   * The qname of the property, which is to index.
   */
  private QName m_property;

  /**
   * Stores the value of a property as key and the feature, containing the property with this value as value.
   */
  private Map<Object, Feature> m_index;

  /**
   * The constructor.
   * 
   * @param property
   *          Should the property be null, the visitor will not visit any features and the map will be empty than.
   * @param asTreeMap
   *          Indicates, if a TreeMap(sortable) should be used instead of a HashMap.
   * @param comparator
   *          If not null and a TreeMap should be returned, this comparator will be given to the TreeMap.
   */
  public IndexByPropertyVisitor( QName property, boolean asTreeMap, Comparator<Object> comparator )
  {
    m_property = property;

    if( asTreeMap )
    {
      if( comparator == null )
        m_index = new TreeMap<Object, Feature>();
      else
        m_index = new TreeMap<Object, Feature>( comparator );
    }
    else
      m_index = new HashMap<Object, Feature>();
  }

  /**
   * @see org.kalypsodeegree.model.feature.FeatureVisitor#visit(org.kalypsodeegree.model.feature.Feature)
   */
  public boolean visit( Feature f )
  {
    if( m_property == null )
      return false;

    Object property = f.getProperty( m_property );
    m_index.put( property, f );

    return true;
  }

  /**
   * This function returns the index map for this visitor.
   * 
   * @return The indexed map.
   */
  public Map<Object, Feature> getIndex( )
  {
    return m_index;
  }
}