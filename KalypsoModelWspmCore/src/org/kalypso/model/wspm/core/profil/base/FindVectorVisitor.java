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
package org.kalypso.model.wspm.core.profil.base;

import org.kalypso.commons.exception.CancelVisitorException;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.jts.JTSConverter;
import org.kalypso.jts.JtsVectorUtilities;
import org.kalypso.model.wspm.core.profil.wrappers.IProfileRecord;
import org.kalypso.model.wspm.core.profil.wrappers.IProfileRecordVisitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Dirk Kuch
 */
public class FindVectorVisitor implements IProfileRecordVisitor
{
  private Coordinate m_c0 = null;

  private Coordinate m_c1 = null;

  private Double m_p0 = null;

  @Override
  public void visit( final IProfileRecord point, final int searchDirection ) throws CancelVisitorException
  {
    final Coordinate coordinate = point.getCoordinate();
    if( Objects.isNull( coordinate ) )
      return;

    if( Objects.isNull( m_c0 ) )
    {
      m_c0 = coordinate;
      m_p0 = point.getBreite();
    }
    else
    {
      m_c1 = coordinate;

      throw new CancelVisitorException();
    }
  }

  public double getP0( )
  {
    return m_p0;
  }

  public boolean isValid( )
  {
    return Objects.allNotNull( m_p0, m_c0, m_c1 );
  }

  public Coordinate getVector( )
  {
    return JtsVectorUtilities.getVector( m_c0, m_c1 );
  }

  public Point getP0Coordinate( )
  {
    return JTSConverter.toPoint( m_c0 );
  }

  @Override
  public boolean isWriter( )
  {
    return false;
  }
}
