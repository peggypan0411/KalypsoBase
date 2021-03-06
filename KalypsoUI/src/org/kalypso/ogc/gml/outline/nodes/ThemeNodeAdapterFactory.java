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
package org.kalypso.ogc.gml.outline.nodes;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.kalypsodeegree.xml.Marshallable;

/**
 * @author Gernot Belger
 */
public class ThemeNodeAdapterFactory implements IAdapterFactory
{
  private static final ThemeNodeWorkbenchAdapter THEME_NODE_WORKBENCH_ADAPTER = new ThemeNodeWorkbenchAdapter();

  @Override
  public Object getAdapter( final Object adaptableObject, final Class adapterType )
  {
    if( !(adaptableObject instanceof IThemeNode) )
      return null;

    final IThemeNode node = (IThemeNode)adaptableObject;

    if( adapterType == IWorkbenchAdapter2.class || adapterType == IWorkbenchAdapter.class )
      return THEME_NODE_WORKBENCH_ADAPTER;

    if( adapterType == Marshallable.class )
      return node.getAdapter( Marshallable.class );

    if( adapterType.isInstance( node ) )
      return node;

    final Object element = node.getElement();
    if( adapterType.isInstance( element ) )
      return element;

    return null;
  }

  @Override
  public Class< ? >[] getAdapterList( )
  {
    return new Class[] { IWorkbenchAdapter2.class, IWorkbenchAdapter2.class };
  }
}
