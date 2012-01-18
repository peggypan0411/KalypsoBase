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
package org.kalypso.model.wspm.core.profil.changes;

import org.kalypso.model.wspm.core.profil.IProfilChange;
import org.kalypso.model.wspm.core.profil.IProfilPointMarker;
import org.kalypso.model.wspm.core.profil.wrappers.IProfileRecord;

public class PointMarkerSetPoint implements IProfilChange
{
  private final IProfilPointMarker m_pointMarker;

  private final IProfileRecord m_newPosition;

  private IProfileRecord m_oldPosition;

  public PointMarkerSetPoint( final IProfilPointMarker pointMarker, final IProfileRecord newPosition )
  {
    m_pointMarker = pointMarker;
    m_newPosition = newPosition;
    m_oldPosition = pointMarker.getPoint();
  }

  @Override
  public void configureHint( final ProfilChangeHint hint )
  {
    hint.setMarkerMoved();

  }

  @Override
  public IProfilChange doChange( )
  {

    m_oldPosition = m_pointMarker.setPoint( m_newPosition );

    return new PointMarkerSetPoint( m_pointMarker, m_oldPosition );
  }

  @Override
  public String toString( )
  {
    return m_pointMarker.toString();
  }
}
