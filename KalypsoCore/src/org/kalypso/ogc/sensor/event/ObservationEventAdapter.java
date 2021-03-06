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
package org.kalypso.ogc.sensor.event;

import java.util.LinkedHashSet;
import java.util.Set;

import org.kalypso.ogc.sensor.IObservation;

/**
 * ObservationEventAdapter
 *
 * @author schlienger
 */
public class ObservationEventAdapter implements IObservationEventProvider
{
  private final Set<IObservationListener> m_listeners = new LinkedHashSet<>();

  private final IObservation m_obs;

  public ObservationEventAdapter( final IObservation obs )
  {
    m_obs = obs;
  }

  @Override
  public void addListener( final IObservationListener listener )
  {
    m_listeners.add( listener );
  }

  @Override
  public void removeListener( final IObservationListener listener )
  {
    m_listeners.remove( listener );
  }

  /**
   * Fires obs changed event
   */
  @Override
  public void fireChangedEvent( final Object source, final ObservationChangeType type )
  {
    final IObservationListener[] listeners = m_listeners.toArray( new IObservationListener[] {} );
    for( final IObservationListener listener : listeners )
    {
      listener.observationChanged( m_obs, source, type );
    }
  }
}
