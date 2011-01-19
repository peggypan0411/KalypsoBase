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
package org.kalypso.zml.core.table.model.data;

import java.util.LinkedHashSet;
import java.util.Set;

import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.provider.IObsProvider;
import org.kalypso.ogc.sensor.provider.IObsProviderListener;

/**
 * @author Dirk Kuch
 */
public class ZmlObsProviderColumnDataHandler implements IZmlModelColumnDataHandler
{

  private final IObsProviderListener m_observationProviderListener = new IObsProviderListener()
  {
    @Override
    public void observationReplaced( )
    {
      onObservationLoaded();
    }

    /**
     * @see org.kalypso.ogc.sensor.template.IObsProviderListener#observationChangedX(java.lang.Object)
     */
    @Override
    public void observationChanged( final Object source )
    {
      onObservationChanged();
    }
  };

  private final Set<IZmlModelColumnDataListener> m_listeners = new LinkedHashSet<IZmlModelColumnDataListener>();

  private final IObsProvider m_provider;

  public ZmlObsProviderColumnDataHandler( final IObsProvider provider )
  {
    m_provider = provider;
    m_provider.addListener( m_observationProviderListener );
  }

  /**
   * @see org.kalypso.zml.core.table.model.data.IZmlModelColumnDataHandler#dispose()
   */
  @Override
  public void dispose( )
  {
    m_provider.removeListener( m_observationProviderListener );
    m_provider.dispose();
  }

  protected void onObservationChanged( )
  {
    final IZmlModelColumnDataListener[] listeners = m_listeners.toArray( new IZmlModelColumnDataListener[] {} );
    for( final IZmlModelColumnDataListener listener : listeners )
    {
      listener.eventObservationChanged();
    }
  }

  protected void onObservationLoaded( )
  {
    final IZmlModelColumnDataListener[] listeners = m_listeners.toArray( new IZmlModelColumnDataListener[] {} );
    for( final IZmlModelColumnDataListener listener : listeners )
    {
      listener.eventObservationLoaded();
    }
  }

  /**
   * @see org.kalypso.zml.core.table.model.data.IZmlModelColumnDataHandler#getModel()
   */
  @Override
  public ITupleModel getModel( ) throws SensorException
  {
    final IObservation observation = getObservation();
    return observation.getValues( null );
  }

  /**
   * @see org.kalypso.zml.core.table.model.data.IZmlModelColumnDataHandler#getObservation()
   */
  @Override
  public IObservation getObservation( )
  {
    return m_provider.getObservation();
  }

  /**
   * @see org.kalypso.zml.core.table.model.data.IZmlModelColumnDataHandler#addListener(org.kalypso.zml.core.table.model.data.IZmlModelColumnDataListener)
   */
  @Override
  public void addListener( final IZmlModelColumnDataListener listener )
  {
    m_listeners.add( listener );
  }

}
