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
package org.kalypso.grid;

import java.util.Map;

import javax.vecmath.Tuple2i;

import org.shiftone.cache.Cache;
import org.shiftone.cache.adaptor.CacheMap;
import org.shiftone.cache.decorator.miss.MissHandler;
import org.shiftone.cache.decorator.miss.MissHandlingCache;
import org.shiftone.cache.policy.lru.LruCacheFactory;

/**
 * @author belger
 */
public class CachingGeoGrid extends AbstractDelegatingGeoGrid
{
  private Map<Tuple2i, Double> m_cacheMap = null;

  public CachingGeoGrid( final IGeoGrid delegate )
  {
    super( delegate, true );
  }

  @SuppressWarnings("unchecked")
  private Map<Tuple2i, Double> getCache( ) throws GeoGridException
  {
    synchronized( this )
    {
      if( m_cacheMap == null )
      {
        final IGeoGrid delegate = getDelegate();
        final int cacheSize = Math.max( delegate.getSizeX(), delegate.getSizeY() ) * 10;
        // REMARK: we give a long period, as we cannod void the registration of the reaper-timer
        final Cache cache = new LruCacheFactory().newInstance( "GridCache" + System.currentTimeMillis(), 1000 * 60 * 5, cacheSize );

        final MissHandler missHandler = new MissHandler()
        {
          @Override
          public Object fetchObject( final Object key ) throws Exception
          {
            final Tuple2i tuple = (Tuple2i) key;
            return getDelegate().getValue( tuple.x, tuple.y );
          }
        };

        final MissHandlingCache missHandlingCache = new MissHandlingCache( cache, missHandler );
        m_cacheMap = new CacheMap( missHandlingCache );
      }
    }

    return m_cacheMap;
  }

  @Override
  public void dispose( )
  {
    super.dispose();

    try
    {
      getCache().clear();
    }
    catch( final GeoGridException e )
    {
      e.printStackTrace();
    }
  }

  @Override
  public double getValue( final int x, final int y ) throws GeoGridException
  {
    return getCache().get( new Tuple2i( x, y )
    {
    } );
  }

}
