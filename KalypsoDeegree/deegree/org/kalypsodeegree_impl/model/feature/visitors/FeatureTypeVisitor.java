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

import javax.xml.namespace.QName;

import org.kalypso.contribs.javax.xml.namespace.QNameUnique;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;

/**
 * Decorator over any FeatureVisitor, but only visits features of a given type.<br/>
 * Comparisaon is by name of the given type<br/>
 * 
 * @author Gernot Belger
 */
public class FeatureTypeVisitor implements FeatureVisitor
{
  private final QNameUnique m_typename;

  private final QNameUnique m_localTypename;

  /** Falls true, werden auch features acceptiert, welche den angegebenen Typ substituieren */
  private final boolean m_acceptIfSubstituting;

  private FeatureVisitor m_visitor;

  public FeatureTypeVisitor( final FeatureVisitor visitor, final IFeatureType ft, final boolean acceptIfSubstituting )
  {
    this( visitor, ft.getQName(), ft.getLocalQName(), acceptIfSubstituting );
  }

  public FeatureTypeVisitor( final FeatureVisitor visitor, final QNameUnique typename, final QNameUnique localQName, final boolean acceptIfSubstituting )
  {
    m_visitor = visitor;
    m_typename = typename;
    m_localTypename = localQName;
    m_acceptIfSubstituting = acceptIfSubstituting;
  }

  public QName getTypename( )
  {
    return m_typename;
  }

  public void setVisitor( final FeatureVisitor visitor )
  {
    m_visitor = visitor;
  }

  @Override
  public boolean visit( final Feature f )
  {
    if( matchesType( f ) )
      m_visitor.visit( f );

    return true;
  }

  public boolean matchesType( final Feature f )
  {
    if( f == null )
      return false;

    final IFeatureType featureType = f.getFeatureType();
    if( m_localTypename == featureType.getLocalQName() )
      return true;

    if( m_acceptIfSubstituting )
      return GMLSchemaUtilities.substitutes( featureType, m_typename, m_localTypename );

    return false;
  }
}
