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
package org.kalypso.afgui.scenarios;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.kalypso.afgui.KalypsoAFGUIFrameworkPlugin;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;

import de.renew.workflow.connector.cases.IScenarioDataListener;
import de.renew.workflow.connector.cases.IScenarioDataProvider;

/**
 * Responsibility: handle ScenarioDataChange Extension Point
 *
 * @author Dirk Kuch
 */
public class ScenarioDataChangeListenerExtension
{
  private final static String EXTENSION_POINT = "org.kalypso.afgui.scenarioDataChangeListener"; //$NON-NLS-1$

  private static ScenarioDataChangeListenerExtension m_instance;

  // singleton
  private ScenarioDataChangeListenerExtension( )
  {
    init();
  }

  private void init( )
  {
    final IScenarioDataProvider provider = KalypsoAFGUIFrameworkPlugin.getDataProvider();

    final IExtensionRegistry registry = Platform.getExtensionRegistry();
    final IExtensionPoint extensionPoint = registry.getExtensionPoint( EXTENSION_POINT );

    final IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
    for( final IConfigurationElement element : elements )
    {
      try
      {
        final IScenarioDataListener listener = (IScenarioDataListener) element.createExecutableExtension( "listener" ); //$NON-NLS-1$
        provider.addScenarioDataListener( listener );
      }
      catch( final Exception e )
      {
        KalypsoAFGUIFrameworkPlugin.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
      }
    }
  }

  public static ScenarioDataChangeListenerExtension getInstance( )
  {
    if( m_instance == null )
      m_instance = new ScenarioDataChangeListenerExtension();

    return m_instance;
  }
}
