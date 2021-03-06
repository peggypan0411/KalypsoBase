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

package org.kalypso.grid;

import com.vividsolutions.jts.geom.Coordinate;

public class SubstractionGrid extends AbstractDelegatingGeoGrid
{
  private final IGeoGrid m_inputGrid1;

  private final IGeoGrid m_inputGrid2;

  private boolean m_usePositiveValuesOnly;

  public SubstractionGrid( final IGeoGrid inputGrid1, final IGeoGrid inputGrid2 ) throws Exception
  {
    super( inputGrid1 );
    m_inputGrid1 = inputGrid1;
    m_inputGrid2 = inputGrid2;
    m_usePositiveValuesOnly = false;
  }

  public void usePositiveValuesOnly( final boolean value )
  {
    m_usePositiveValuesOnly = value;
  }

  @Override
  public final double getValue( final int x, final int y ) throws GeoGridException
  {
    try
    {
      final Coordinate coordinate = GeoGridUtilities.toCoordinate( m_inputGrid1, x, y, null );
      if( m_inputGrid1.getEnvelope().contains( coordinate ) && m_inputGrid2.getEnvelope().contains( coordinate ) )
      {
        double value1 = m_inputGrid1.getValue( coordinate );
        double value2 = m_inputGrid2.getValue( coordinate );

        if( Double.isNaN( value1 ) || Double.isNaN( value2 ) )
          return Double.NaN;

        if( m_usePositiveValuesOnly )
        {
          if( value1 < 0.0 )
            value1 = 0.0;
          if( value2 < 0.0 )
            value2 = 0.0;
        }
        return value1 - value2;
      }
      return Double.NaN;
    }
    catch( final Exception ex )
    {
      throw new GeoGridException( "Could not generate the value.", ex ); //$NON-NLS-1$
    }
  }

}
