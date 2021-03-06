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
package org.kalypso.core;

import org.kalypso.contribs.eclipse.core.runtime.Debug;

/**
 * Holds debug-constants in order to produce trace output.
 * <p>
 * Debug constants correspond to entries in <code>.options</code> file.
 * </p>
 * 
 * @author Gernot Belger
 */
public interface KalypsoCoreDebug
{
  Debug RESOURCE_POOL = new Debug( KalypsoCorePlugin.getDefault(), "/debug/resourcepool" ); //$NON-NLS-1$

  Debug RESOURCE_POOL_KEYS = new Debug( KalypsoCorePlugin.getDefault(), "/debug/resourcepool/keys" ); //$NON-NLS-1$

  Debug MAP_MODELL = new Debug( KalypsoCorePlugin.getDefault(), "/debug/mapModell" ); //$NON-NLS-1$

  Debug SPATIAL_INDEX_PAINT = new Debug( KalypsoCorePlugin.getDefault(), "/debug/spatialIndex/paint" ); //$NON-NLS-1$

  Debug MAP_PANEL = new Debug( KalypsoCorePlugin.getDefault(), "/debug/mapPanel" ); //$NON-NLS-1$

  Debug GISMAPVIEW_VALIDATE = new Debug( KalypsoCorePlugin.getDefault(), "/debug/validatebinding/gismapview" ); //$NON-NLS-1$

  Debug FEATUREVIEW_VALIDATE = new Debug( KalypsoCorePlugin.getDefault(), "/debug/validatebinding/gismapview" ); //$NON-NLS-1$

  Debug OBSTABLEREPORT_VALIDATE = new Debug( KalypsoCorePlugin.getDefault(), "/debug/validatebinding/obstablereport" ); //$NON-NLS-1$

  Debug PERF_SERIALIZE_GML = new Debug( KalypsoCorePlugin.getDefault(), "/perf/serialization/gml" ); //$NON-NLS-1$
}
