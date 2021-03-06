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
package org.kalypso.ui.views.properties;

import org.eclipse.core.runtime.IAdapterFactory;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ui.editor.mapeditor.AbstractMapPart;

/**
 * Generel adapter factory for {@link AbstractMapPart}.
 * <p>
 * Some adapterTypes (like {@link MapPanel}) are exposed via this factory (instead of directly using {@link AbstractMapPart#getAdapter(Class)}, because the expression frameworks 'adapt'-element does
 * only recognise types handled via adapter factories.
 * 
 * @author Gernot Belger
 */
public class MapPartAdapterFactory implements IAdapterFactory
{
  @Override
  public Object getAdapter( final Object adaptableObject, final Class adapterType )
  {
    if( !(adaptableObject instanceof AbstractMapPart) )
      return null;

    final AbstractMapPart mapPart = (AbstractMapPart)adaptableObject;

    if( adapterType == IMapPanel.class )
      return mapPart.getMapPanel();

    return null;
  }

  @Override
  public Class< ? >[] getAdapterList( )
  {
    return new Class[] { IMapPanel.class };
  }
}