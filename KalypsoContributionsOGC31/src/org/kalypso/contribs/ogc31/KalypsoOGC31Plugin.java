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
package org.kalypso.contribs.ogc31;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

/**
 * @author Gernot Belger
 */
public class KalypsoOGC31Plugin extends Plugin
{
  // The shared instance.
  private static KalypsoOGC31Plugin m_plugin;

  public KalypsoOGC31Plugin( )
  {
    super();

    m_plugin = this;
  }

  /**
   * Returns the shared instance.
   */
  public static KalypsoOGC31Plugin getDefault( )
  {
    return m_plugin;
  }

  /**
   * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start( final BundleContext context ) throws Exception
  {
    super.start( context );

    // PERFORMANCE: as soon as this plug-in is started, we initialise the GML3.1 context
    // as this takes quite some time. This enables clients to initialise the context on
    // startup by starting this plug-in immediately.
    // Runs in a separate job, as else plug-in initialisation may fail due to timeout
    final Job job = new Job( "Initialising OGC31-Binding-Classes" )
    {
      @Override
      protected IStatus run( final IProgressMonitor monitor )
      {
        KalypsoOGC31JAXBcontext.getContext();
        return Status.OK_STATUS;
      }
    };
    job.setSystem( true );
    job.setPriority( Job.LONG );
    job.schedule();
  }
}
