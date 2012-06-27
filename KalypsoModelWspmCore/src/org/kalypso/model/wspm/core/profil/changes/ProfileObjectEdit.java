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
import org.kalypso.model.wspm.core.profil.IProfileObject;
import org.kalypso.observation.IObservation;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.TupleResult;

public class ProfileObjectEdit implements IProfilChange
{
  private final IProfileObject m_object;

  private final IComponent m_property;

  private final Object m_newValue;

  private final int m_rowIndex;

  public ProfileObjectEdit( final IProfileObject profileObject, final IComponent key, final Object newValue )
  {
    this( profileObject, key, 0, newValue );
  }

  public ProfileObjectEdit( final IProfileObject profileObject, final IComponent key, final int rowIndex, final Object newValue )
  {
    m_object = profileObject;
    m_property = key;
    m_newValue = newValue;
    m_rowIndex = rowIndex;
  }

  @Override
  public IProfilChange doChange( )
  {
    // FIXME rowIndex may change until rollback or undo
    final IObservation<TupleResult> observation = m_object.getObservation();
    final TupleResult result = observation.getResult();
    final int intCmp = result.indexOfComponent( m_property );
    if( m_rowIndex < result.size() )
    {
      final IRecord record = result.get( m_rowIndex );

      final Object oldValue = record.getValue( intCmp );
      record.setValue( intCmp, m_newValue );

      return new ProfileObjectEdit( m_object, m_property, m_rowIndex, oldValue );
    }

    return null;

  }
}
