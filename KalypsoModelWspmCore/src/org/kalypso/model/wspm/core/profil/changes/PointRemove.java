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

import org.kalypso.model.wspm.core.i18n.Messages;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilChange;
import org.kalypso.model.wspm.core.profil.IllegalProfileOperationException;
import org.kalypso.observation.result.IRecord;

/**
 * @author kimwerner
 */

public class PointRemove implements IProfilChange
{
  private final IProfil m_profil;

  private final IRecord[] m_points;

  private final int[] m_pointPositions;

  public PointRemove( final IProfil profil, final IRecord point )
  {
    this( profil, new IRecord[] { point } );
  }

  public PointRemove( final IProfil profil, final IRecord[] points )
  {
    m_profil = profil;
    m_points = points;
    m_pointPositions = new int[m_points.length];
  }

  /**
   * @see org.kalypso.model.wspm.core.profil.IProfilChange#doChange()
   */
  @Override
  public IProfilChange doChange( final ProfilChangeHint hint ) throws IllegalProfileOperationException
  {
    if( hint != null )
      hint.setPointsChanged();

    for( int i = 0; i < m_points.length; i++ )
      m_pointPositions[i] = m_profil.indexOfPoint( m_points[i] );

    if( m_profil.removePoints( m_points ) )
      return new PointsAdd( m_profil, m_pointPositions, m_points );
    else
    {
      if( m_points.length > 0 )
        m_profil.setActivePoint( m_points[0] );
      throw new IllegalProfileOperationException( Messages.getString( "org.kalypso.model.wspm.core.profil.changes.PointRemove.1" ), this ); //$NON-NLS-1$
    }
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString( )
  {
    return "Remove points";
  }
}
