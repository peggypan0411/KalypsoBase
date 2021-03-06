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

import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypsodeegree.model.feature.Feature;

/**
 * TODO: Remove this class. However this is still not easy because the parentFeatureProperty is still not known via the
 * api and the workspace of a feature is not a Commandableworkspace
 * 
 * @author belger
 */
public final class EasyFeatureWrapper
{
  private final CommandableWorkspace m_workspace;

  private final Feature m_feature;

  public EasyFeatureWrapper( final CommandableWorkspace workspace, final Feature feature )
  {
    m_workspace = workspace;
    m_feature = feature;
  }

  public Feature getFeature( )
  {
    return m_feature;
  }

  public Feature getParentFeature( )
  {
    return m_feature.getOwner();
  }

  public IRelationType getParentFeatureProperty( )
  {
    return m_feature.getParentRelation();
  }

  public CommandableWorkspace getWorkspace( )
  {
    return m_workspace;
  }

  /**
   * Equals which is only based on {@link Feature#equals(Object)}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( final Object obj )
  {
    if( obj instanceof EasyFeatureWrapper )
    {
      return m_feature.equals( ((EasyFeatureWrapper)obj).m_feature );
    }
    else
    {
      return super.equals( obj );
    }
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode( )
  {
    return m_feature.hashCode();
  }
}