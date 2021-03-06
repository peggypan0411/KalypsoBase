/*--------------- Kalypso-Header --------------------------------------------------------------------

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
 
 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.services.observation.sei;

import java.util.Map;

/**
 * An Observation Bean. Specialisation of <code>ItemBean</code>. Can deliver the MetadataList for the IObservation it
 * represents.
 * 
 * @author schlienger
 */
public class ObservationBean extends ItemBean
{
  private Map<Object, Object> m_metadataList;

  public ObservationBean( )
  {
    this( "", "", false, null ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public ObservationBean( final String id )
  {
    this( id, "", false, null ); //$NON-NLS-1$
  }

  public ObservationBean( final String id, final String name, final Boolean modifyable, final Map<Object, Object> metadata )
  {
    super( id, name, modifyable );

    m_metadataList = metadata;
  }

  public Map<Object, Object> getMetadataList( )
  {
    return m_metadataList;
  }

  public void setMetadataList( final Map<Object, Object> md )
  {
    m_metadataList = md;
  }
}