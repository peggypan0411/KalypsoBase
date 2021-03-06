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
package org.kalypsodeegree_impl.model.feature;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.relation.IRelationType;

/**
 * Feature with dirty flag <br>
 * <br>
 * TODO on workspace save -> reset dirty flag
 *
 * @deprecated Use {@link AbstractCachedFeature2} instead. This one leads to bugs.
 * @author Dirk Kuch
 */
@Deprecated
public class AbstractCachedFeature extends Feature_Impl
{
  private final Set<String> m_dirty = new HashSet<>();

  protected AbstractCachedFeature( final Object parent, final IRelationType parentRelation, final IFeatureType ft, final String id, final Object[] propValues )
  {
    super( parent, parentRelation, ft, id, propValues );
  }

  /**
   * @see org.kalypsodeegree_impl.model.feature.Feature_Impl#setProperty(org.kalypso.gmlschema.property.IPropertyType,
   *      java.lang.Object)
   */
  @Override
  public void setProperty( final IPropertyType pt, final Object value )
  {
    m_dirty.add( pt.getQName().toString() );

    super.setProperty( pt, value );
  }

  /**
   * @see org.kalypsodeegree_impl.model.feature.Feature_Impl#setProperty(javax.xml.namespace.QName, java.lang.Object)
   */
  @Override
  public void setProperty( final QName propQName, final Object value )
  {
    m_dirty.add( propQName.toString() );

    super.setProperty( propQName, value );
  }

  protected boolean isDirty( )
  {
    return !m_dirty.isEmpty();
  }

  protected boolean isDirty( final QName qname )
  {
    return m_dirty.contains( qname.toString() );
  }

  protected boolean isDirty( final QName... qnames )
  {
    for( final QName qn : qnames )
    {
      if( isDirty( qn ) )
      {
        return true;
      }
    }

    return false;
  }

  protected void setValid( final QName qname )
  {
    m_dirty.remove( qname.toString() );
  }

  protected void setValid( final QName... qnames )
  {
    for( final QName qn : qnames )
    {
      m_dirty.remove( qn.toString() );
    }
  }
}
