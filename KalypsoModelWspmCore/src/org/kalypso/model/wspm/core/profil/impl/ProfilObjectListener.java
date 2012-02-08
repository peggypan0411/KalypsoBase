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
package org.kalypso.model.wspm.core.profil.impl;

import org.kalypso.model.wspm.core.profil.changes.ProfilChangeHint;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.ITupleResultChangedListener;

/**
 * @author Gernot Belger
 */
public class ProfilObjectListener implements ITupleResultChangedListener
{
  ProfilChangeHint m_hint = new ProfilChangeHint( ProfilChangeHint.OBJECT_DATA_CHANGED );

  private final AbstractProfil m_profil;

  public ProfilObjectListener( final AbstractProfil profil )
  {
    m_profil = profil;
  }

  @Override
  public void valuesChanged( final ValueChange[] changes )
  {
    m_profil.fireProfilChanged( m_hint );
  }

  @Override
  public void recordsChanged( final IRecord[] records, final TYPE type )
  {
    m_profil.fireProfilChanged( m_hint );
  }

  @Override
  public void componentsChanged( final IComponent[] components, final TYPE type )
  {
    m_profil.fireProfilChanged( m_hint );
  }
}