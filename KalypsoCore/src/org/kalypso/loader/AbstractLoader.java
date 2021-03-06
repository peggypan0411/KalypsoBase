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
package org.kalypso.loader;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.core.util.pool.IPoolableObjectType;

/**
 * @author belger
 */
public abstract class AbstractLoader implements ILoader
{
  private IStatus m_status = Status.OK_STATUS;

  /**
   * @see org.kalypso.loader.ILoader#getStatus()
   */
  @Override
  public IStatus getStatus( )
  {
    return m_status;
  }

  protected void setStatus( final IStatus status )
  {
    m_status = status;
  }

  @Override
  public final IResource[] getResources( final IPoolableObjectType key )
  {
    try
    {
      return getResourcesInternal( key );
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();
    }
    catch( final IllegalArgumentException e )
    {
      // Explicitely catch this one too, as finding resources may result in IllegalArgumentException
      e.printStackTrace();
    }

    return new IResource[0];
  }

  protected abstract IResource[] getResourcesInternal( final IPoolableObjectType key ) throws MalformedURLException;
}