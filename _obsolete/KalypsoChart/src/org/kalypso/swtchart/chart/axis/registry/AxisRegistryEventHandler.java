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
package org.kalypso.swtchart.chart.axis.registry;

import java.util.ArrayList;
import java.util.List;

import org.kalypso.swtchart.chart.axis.IAxis;

/**
 * @author schlienger
 * @author burtscher
 */
public class AxisRegistryEventHandler implements IAxisRegistryEventProvider
{
  private final List<IAxisRegistryEventListener> m_listeners = new ArrayList<IAxisRegistryEventListener>();

  /**
   * @see org.kalypso.swtchart.axis.IAxisRegistryEventProvider#addAxisRegistryEventListener(org.kalypso.swtchart.axis.IAxisRegistryEventListener)
   */
  public void addAxisRegistryEventListener( final IAxisRegistryEventListener l )
  {
    m_listeners.add( l );
  }

  /**
   * @see org.kalypso.swtchart.axis.IAxisRegistryEventProvider#removeAxisRegistryEventListener(org.kalypso.swtchart.axis.IAxisRegistryEventListener)
   */
  public void removeAxisRegistryEventListener( final IAxisRegistryEventListener l )
  {
    m_listeners.remove( l );
  }

  protected void fireAxisAdded( final IAxis axis )
  {
    for( IAxisRegistryEventListener l : m_listeners )
      l.onAxisAdded( axis );
  }

  protected void fireAxisRemoved( final IAxis axis )
  {
    for( final IAxisRegistryEventListener l : m_listeners )
      l.onAxisRemoved( axis );
  }
}
