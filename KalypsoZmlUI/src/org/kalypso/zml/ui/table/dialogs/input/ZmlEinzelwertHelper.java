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
package org.kalypso.zml.ui.table.dialogs.input;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.kalypso.core.KalypsoCorePlugin;

/**
 * @author Dirk Kuch
 */
public final class ZmlEinzelwertHelper
{
  private ZmlEinzelwertHelper( )
  {
  }

  public static boolean compareDayAnchor( final Date d1, final Date d2 )
  {
    if( d1 == null || d2 == null )
    {
      return false;
    }

    final Calendar c1 = Calendar.getInstance( KalypsoCorePlugin.getDefault().getTimeZone() );
    c1.setTime( d1 );

    final Calendar c2 = Calendar.getInstance( KalypsoCorePlugin.getDefault().getTimeZone() );
    c2.setTime( d2 );

    final EqualsBuilder builder = new EqualsBuilder();
    builder.append( c1.get( Calendar.DAY_OF_MONTH ), c2.get( Calendar.DAY_OF_MONTH ) );
    builder.append( c1.get( Calendar.MONTH ), c2.get( Calendar.MONTH ) );
    builder.append( c1.get( Calendar.YEAR ), c2.get( Calendar.YEAR ) );

    return builder.isEquals();
  }
}
