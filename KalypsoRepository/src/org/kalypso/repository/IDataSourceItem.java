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
package org.kalypso.repository;

/**
 * @author Dirk Kuch
 */
public interface IDataSourceItem
{
  String FILTER_SOURCE = "filter://";//$NON-NLS-1$

  String MD_DATA_SOURCE_ITEM = "Datenabruf_Quelle";//$NON-NLS-1$

  String MD_DATA_SOURCE_ITEM_REPOSITORY = "Datenabruf_Repository_Quelle";//$NON-NLS-1$

  String SOURCE_PREFIX = "source://";//$NON-NLS-1$

  String SOURCE_UNKNOWN = SOURCE_PREFIX + "unknown";//$NON-NLS-1$

  String SOURCE_MISSING = SOURCE_PREFIX + "missing";//$NON-NLS-1$

  String SOURCE_MANUAL_CHANGED = SOURCE_PREFIX + "manually.changed";//$NON-NLS-1$

  String SOURCE_INTERPOLATED_WECHMANN_VALUE = IDataSourceItem.FILTER_SOURCE + "fill.empty.wechmann.value"; //$NON-NLS-1$

}
