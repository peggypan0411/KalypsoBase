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
package org.kalypso.simulation.core;

import java.io.Serializable;

import javax.xml.namespace.QName;

/**
 * Bean um vom Rechendienst abzufragen, welches Input/Output Verhalten er hat.
 * 
 * @author belger
 */
public class SimulationDescription implements Serializable
{
  private String m_id;

  private String m_description;

  private QName m_type;

  public SimulationDescription( )
  {
    // nur f�r wspcompile
  }

  /**
   * @param id
   *          ID dieses Datenobjekts
   * @param description
   *          Beschreibung der Daten
   */
  public SimulationDescription( final String id, final String description, final QName type )
  {
    m_id = id;
    m_description = description;
    m_type = type;
  }

  public final String getId( )
  {
    return m_id;
  }

  public final void setId( final String id )
  {
    m_id = id;
  }

  public final String getDescription( )
  {
    return m_description;
  }

  public final void setDescription( final String description )
  {
    m_description = description;
  }

  public QName getType( )
  {
    return m_type;
  }

  public void setType( final QName type )
  {
    m_type = type;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString( )
  {
    return super.toString() + "\n ID: " + m_id + "\n Description: " + m_description; //$NON-NLS-1$ //$NON-NLS-2$
  }
}