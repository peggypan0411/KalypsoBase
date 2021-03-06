/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.module.conversion;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.kalypso.contribs.eclipse.core.runtime.IStatusCollector;
import org.kalypso.contribs.eclipse.core.runtime.StatusCollector;
import org.kalypso.module.internal.Module;

/**
 * @author Gernot Belger
 */
public abstract class AbstractLoggingOperation implements ILoggingOperation
{
  private final IStatusCollector m_log = new StatusCollector( Module.PLUGIN_ID );

  private final String m_label;

  public AbstractLoggingOperation( final String label )
  {
    m_label = label;
  }

  public final String getLabel( )
  {
    return m_label;
  }

  protected final IStatusCollector getLog( )
  {
    return m_log;
  }

  @Override
  public final IStatus execute( final IProgressMonitor monitor ) throws CoreException, InvocationTargetException, InterruptedException
  {
    try
    {
      doExecute( monitor );
    }
    catch( final CoreException e )
    {
      throw e;
    }
    catch( final OperationCanceledException e )
    {
      throw new InterruptedException();
    }
    catch( final InterruptedException e )
    {
      throw e;
    }
    catch( final InvocationTargetException e )
    {
      throw e;
    }
    catch( final Throwable e )
    {
      throw new InvocationTargetException( e );
    }

    return m_log.asMultiStatus( m_label );
  }

  protected abstract void doExecute( final IProgressMonitor monitor ) throws Exception;
}
