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
package org.kalypso.ogc.sensor.provider;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.kalypso.contribs.java.net.UrlResolverSingleton;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.util.pool.IPoolListener;
import org.kalypso.core.util.pool.IPoolableObjectType;
import org.kalypso.core.util.pool.ResourcePool;
import org.kalypso.ogc.sensor.IObservation;

/**
 * A Theme for an IObservation
 * 
 * @author schlienger
 */
public class PooledObsProvider extends AbstractObsProvider implements IPoolListener
{
  private boolean m_isDisposed = false;

  private final ResourcePool m_pool = KalypsoCorePlugin.getDefault().getPool();

  private final IPoolableObjectType m_key;

  public PooledObsProvider( final IPoolableObjectType key )
  {
    this( key, null );
  }

  /**
   * Copy constructor, only used for copying.
   */
  private PooledObsProvider( final IPoolableObjectType key, final IObservation observation )
  {
    m_key = key;
    setObservation( observation );

    m_pool.addPoolListener( this, key );
  }

  @Override
  public void dispose( )
  {
    m_isDisposed = true;
    m_pool.removePoolListener( this );

    super.dispose();
  }

  /**
   * Remove the observation and inform listeners that theme changed
   * 
   * @see org.kalypso.util.pool.IPoolListener#objectInvalid(org.kalypso.util.pool.IPoolableObjectType, java.lang.Object)
   */
  @Override
  public void objectInvalid( final IPoolableObjectType key, final Object oldValue )
  {
    if( !m_isDisposed )
      setObservation( null );
  }

  /**
   * Set the loaded observation and inform listeners that this theme has changed
   * 
   * @see org.kalypso.util.pool.IPoolListener#objectLoaded(org.kalypso.util.pool.IPoolableObjectType, java.lang.Object,
   *      org.eclipse.core.runtime.IStatus)
   */
  @Override
  public final void objectLoaded( final IPoolableObjectType key, final Object newValue, final IStatus status )
  {
    if( !m_isDisposed )
    {
      setObservation( (IObservation) newValue );
    }
  }

  /**
   * @see org.kalypso.ogc.sensor.template.IObsProvider#copy()
   */
  @Override
  public IObsProvider copy( )
  {
    final IObservation observation = getObservation();
    return new PooledObsProvider( m_key, observation );
  }

  /**
   * @see org.kalypso.util.pool.IPoolListener#isDisposed()
   */
  @Override
  public boolean isDisposed( )
  {
    return m_isDisposed;
  }

  /**
   * @see org.kalypso.util.pool.IPoolListener#dirtyChanged(org.kalypso.util.pool.IPoolableObjectType, boolean)
   */
  @Override
  public void dirtyChanged( final IPoolableObjectType key, final boolean isDirty )
  {
    // nothing to do
  }

  /**
   * @see org.kalypso.ogc.sensor.provider.IObsProvider#isValid()
   */
  @Override
  public boolean isValid( )
  {
    if( getObservation() != null )
      return true;

    final URL context = m_key.getContext();
    final String location = m_key.getLocation();

    try
    {
      UrlResolverSingleton.resolveUrl( context, location );
      return true;
    }
    catch( final Throwable t )
    {
      return false;
    }
  }

  public IPoolableObjectType getPoolKey( )
  {
    return m_key;
  }

}