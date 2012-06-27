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
package org.kalypso.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.kalypsodeegree.KalypsoDeegreePlugin;

/**
 * The class for initializing the preferences of kalypso deegree with default values.
 *
 * @author Holger Albert
 */
public class KalypsoDeegreePreferenceInitializer extends AbstractPreferenceInitializer
{
  @Override
  public void initializeDefaultPreferences( )
  {
    /* Get the default scope. */
    final IScopeContext defaultScope = DefaultScope.INSTANCE;

    /* Get the node for the plugin. */
    final IEclipsePreferences defaultNode = defaultScope.getNode( KalypsoDeegreePlugin.getID() );

    /* Put the defaults. */
    defaultNode.put( IKalypsoDeegreePreferences.AVAILABLE_CRS_SETTING, IKalypsoDeegreePreferences.AVAILABLE_CRS_VALUE );
    defaultNode.put( IKalypsoDeegreePreferences.DEFAULT_CRS_SETTING, IKalypsoDeegreePreferences.DEFAULT_CRS_VALUE );
  }
}