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
package org.kalypso.zml.ui.table.commands.toolbar.view;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.core.commands.HandlerUtils;
import org.kalypso.zml.core.table.model.view.ZmlViewResolutionFilter;

/**
 * @author Dirk Kuch
 */
public class ZmlCommand24HourView extends AbstractHourViewCommand
{

  private static final int RESULTION = 24;

  @Override
  public Object execute( final ExecutionEvent event )
  {
    if( HandlerUtils.isSelected( event ) )
      return updateResolution( event, RESULTION, false );

    return Status.OK_STATUS;
  }

  @Override
  protected boolean isActive( final ZmlViewResolutionFilter filter )
  {
    if( filter == null )
      return false;

    if( RESULTION != filter.getResolution() )
      return false;
    if( filter.isStuetzstellenMode() )
      return false;

    return true;
  }

}