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
package org.kalypso.model.wspm.ui.profil.wizard.propertyEdit;

import org.eclipse.jface.viewers.IFilter;
import org.kalypso.model.wspm.core.IWspmPointProperties;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.observation.result.IComponent;

/**
 * @author Dirk Kuch
 */
public class PropertyFilter implements IFilter
{

  private final IProfile m_profile;

  public PropertyFilter( final IProfile profile )
  {
    m_profile = profile;
  }

  /**
   * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
   */
  @Override
  public boolean select( final Object test )
  {
    if( !(test instanceof IComponent) )
      return false;

    final IComponent property = (IComponent) test;
    final String identifier = property.getId();
    if( m_profile.isPointMarker( identifier ) )
      return false;
    else if( IWspmPointProperties.POINT_PROPERTY_BEWUCHS_CLASS.equals( identifier ) )
      return false;
    else if( IWspmPointProperties.POINT_PROPERTY_ROUGHNESS_CLASS.equals( identifier ) )
      return false;
    else if( IWspmPointProperties.POINT_PROPERTY_CODE.equals( identifier ) )
      return false;

    return true;
  }

}
