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
package org.kalypso.simulation.core.internal.local;

import org.eclipse.core.runtime.CoreException;
import org.kalypso.simulation.core.ISimulation;
import org.kalypso.simulation.core.KalypsoSimulationCoreExtensions;
import org.kalypso.simulation.core.SimulationException;
import org.kalypso.simulation.core.i18n.Messages;
import org.kalypso.simulation.core.internal.queued.ISimulationFactory;

/**
 * L�dt alle als EclipsePlugins geladenen Calculation-Plugins anhand des Extension-Points.
 * 
 * @author belger
 */
public class LocalSimulationFactory implements ISimulationFactory
{
  /**
   * @see org.kalypso.services.calculation.service.impl.ICalcJobFactory#getSupportedTypes()
   */
  @Override
  public String[] getSupportedTypes( )
  {
    return KalypsoSimulationCoreExtensions.getRegisteredTypeIDs();
  }

  /**
   * @see org.kalypso.services.calculation.service.impl.ICalcJobFactory#createJob(java.lang.String)
   */
  @Override
  public ISimulation createJob( final String typeID ) throws SimulationException
  {
    try
    {
      return KalypsoSimulationCoreExtensions.createSimulation( typeID );
    }
    catch( final CoreException e )
    {
      throw new SimulationException( Messages.getString("org.kalypso.simulation.core.internal.local.LocalSimulationFactory.0") + typeID, e ); //$NON-NLS-1$
    }
  }
}
