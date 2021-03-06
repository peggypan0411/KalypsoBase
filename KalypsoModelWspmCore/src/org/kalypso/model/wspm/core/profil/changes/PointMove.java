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
package org.kalypso.model.wspm.core.profil.changes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.kalypso.model.wspm.core.i18n.Messages;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.core.profil.IProfileChange;
import org.kalypso.observation.result.IRecord;

/**
 * @author kimwerner
 */
public class PointMove implements IProfileChange
{
  private final IProfile m_profil;

  private final List<IRecord> m_points = new ArrayList<>();

  private final int m_direction;

  public PointMove( final IProfile profil, final IRecord point, final int direction )
  {
    m_profil = profil;
    m_points.add( point );
    m_direction = direction;
  }

  public PointMove( final IProfile profil, final List<IRecord> points, final int direction )
  {
    m_profil = profil;
    m_points.addAll( points );
    m_direction = direction;
  }

  @Override
  public IProfileChange doChange( )
  {

    if( m_direction == 0 )
      return new PointMove( m_profil, m_points, 0 );
    final IRecord[] points = m_profil.getPoints();

    final int index = ArrayUtils.indexOf( points, m_points.get( 0 ) );

    final int newPosition = index + m_direction;
    if( newPosition < 0 || newPosition >= points.length )
      return new PointMove( m_profil, m_points, 0 );

    m_profil.getResult().removeAll( m_points );
    m_profil.getResult().addAll( newPosition, m_points );
    return new PointMove( m_profil, m_points, -m_direction );
  }

  @Override
  public String toString( )
  {
    return Messages.getString( "org.kalypso.model.wspm.core.profil.changes.PointMove.0" ); //$NON-NLS-1$
  }
}
