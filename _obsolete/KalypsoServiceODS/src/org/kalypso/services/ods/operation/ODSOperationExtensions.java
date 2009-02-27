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
package org.kalypso.services.ods.operation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.kalypso.swtchart.logging.Logger;

/**
 * @author burtscher
 */
public class ODSOperationExtensions
{
  private static Map<String, IConfigurationElement> THE_MAP = null;
  

  private ODSOperationExtensions( )
  {
    // will not be instantiated
  }

  public static IODSOperation createOperation( final String id ) throws CoreException
  {
    final Map<String, IConfigurationElement> elts = getOperations();
    Logger.trace("searching for operation: "+id);
    
    final IConfigurationElement element = elts.get( id );
    if( element == null )
      return null;

    return (IODSOperation) element.createExecutableExtension( "class" );
  }

  private synchronized static Map<String, IConfigurationElement> getOperations( )
  {
    if( THE_MAP != null )
      return THE_MAP;

    THE_MAP = new HashMap<String, IConfigurationElement>();
    final IExtensionRegistry er = Platform.getExtensionRegistry();
    if( er != null )
    {
      final IConfigurationElement[] configurationElementsFor = er.getConfigurationElementsFor( "org.kalypso.services.ods.ODSOperation" );
      for( final IConfigurationElement element : configurationElementsFor )
      {
        final String id = element.getAttribute( "id" );
        THE_MAP.put( id, element );
        Logger.trace("adding operation: "+id);
      }
    }
    else
      Logger.trace( "Error: cant find ExtensionRegistry" );
    return THE_MAP;
  }

}
