/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.module.project;

import java.util.Comparator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;

/**
 * @author Dirk Kuch
 */
public interface IProjectHandle extends IAdaptable
{
  Comparator<IProjectHandle> COMPARATOR = new Comparator<IProjectHandle>()
  {
    @Override
    public int compare( final IProjectHandle o1, final IProjectHandle o2 )
    {
      return o1.getName().compareTo( o2.getName() );
    }
  };

  /**
   * @return "label" name of project
   */
  String getName( );

  /**
   * @return id of the corresponding IKalypsoModule
   */
  String getModuleIdentifier( );

  /**
   * @return unique (bean unix name, iproject.name) of project
   */
  String getUniqueName( );

  /**
   * @return description of the project
   */
  String getDescription( );

  IAction[] getProjectActions( );

}
