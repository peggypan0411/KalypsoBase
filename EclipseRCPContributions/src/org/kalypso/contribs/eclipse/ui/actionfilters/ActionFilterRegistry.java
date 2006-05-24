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
package org.kalypso.contribs.eclipse.ui.actionfilters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IActionFilter;

/**
 * A registry of IActionFilterEx instances which are configured for a specific object type. This registry is itself an
 * IActionFilter. It delegates the call to testAttribute to its registered filters. The first one which feels
 * responsible for the given attribute name will be used.
 * 
 * @author schlienger
 */
public class ActionFilterRegistry implements IActionFilter
{
  public final static String ID = "org.kalypso.contribs.eclipse.actionFilters";

  private List<IActionFilterEx> m_list = null;

  private final String m_className;

  public ActionFilterRegistry( final String className )
  {
    m_className = className;
  }

  /**
   * @see org.eclipse.ui.IActionFilter#testAttribute(java.lang.Object, java.lang.String, java.lang.String)
   */
  public boolean testAttribute( Object target, String name, String value )
  {
    final List<IActionFilterEx> list = getList();

    for( final IActionFilterEx actionFilter : list )
    {
      if( Arrays.binarySearch( actionFilter.getNames(), name ) >= 0 )
        return actionFilter.testAttribute( target, name, value );
    }

    return false;
  }

  private List<IActionFilterEx> getList( )
  {
    if( m_list != null )
      return m_list;

    m_list = new ArrayList<IActionFilterEx>();

    final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor( ID );
    for( int i = 0; i < elements.length; i++ )
    {
      // elements[i].get

      // TODO fill list
    }

    return m_list;
  }
}
