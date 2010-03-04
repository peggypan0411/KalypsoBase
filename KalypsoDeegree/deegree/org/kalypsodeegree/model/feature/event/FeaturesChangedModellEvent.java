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
package org.kalypsodeegree.model.feature.event;

import java.util.Arrays;

import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;

/**
 * @author doemming
 */
public class FeaturesChangedModellEvent extends ModellEvent implements IGMLWorkspaceModellEvent
{
  private final Feature[] m_features;

  private final GMLWorkspace m_workspace;

  public FeaturesChangedModellEvent( final GMLWorkspace workspace, final Feature[] features )
  {
    super( workspace, FEATURE_CHANGE );
    m_workspace = workspace;
    m_features = features;
    
    // reset cached values for changed features
    for ( final Feature lFeature: m_features ) {
      lFeature.setCachedGeometry( null );
    }
  }

  public Feature[] getFeatures( )
  {
    return m_features;
  }

  public GMLWorkspace getGMLWorkspace( )
  {
    return m_workspace;
  }

  /**
   * @see org.kalypsodeegree.model.feature.event.ModellEvent#toString()
   */
  @Override
  public String toString( )
  {
    StringBuffer buf = new StringBuffer( 128 );
    buf.append( "FeatureChangedModellEvent[" );
    if( m_features != null )
    {
      buf.append( Arrays.asList( m_features ) );
    }
    else
    {
      buf.append( "null" );
    }
    buf.append( ']' );
    return buf.toString();
  }
}
