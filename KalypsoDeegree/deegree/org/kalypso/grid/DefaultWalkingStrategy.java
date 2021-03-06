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
package org.kalypso.grid;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.kalypso.grid.areas.IGeoGridArea;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author Gernot Belger
 */
public class DefaultWalkingStrategy implements IGeoWalkingStrategy
{
  /**
   * Simple, straightforward implementation of the interface method.
   * <p>
   * Override in order to optimize according to the underlying (real) grid.
   * </p>
   * 
   * @see org.kalypso.grid.IGeoWalkingStrategy#walk(org.kalypso.grid.IGeoGrid, org.kalypso.grid.IGeoGridWalker,
   *      org.kalypso.grid.IGeoGridArea, org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public Object walk( final IGeoGrid grid, final IGeoGridWalker pwo, final IGeoGridArea walkingArea, final IProgressMonitor monitor ) throws GeoGridException, OperationCanceledException
  {
    final int sizeX = grid.getSizeX();
    final int sizeY = grid.getSizeY();
    if( monitor != null )
      monitor.beginTask( "Raster wird durchlaufen", sizeY );

    pwo.start( grid );

    final Coordinate tmpCrd = new Coordinate();

    for( int y = 0; y < sizeY; y++ )
    {
      if( monitor != null && y % 100 == 0 )
        monitor.subTask( String.format( "%d/%d", y, sizeY ) );

      for( int x = 0; x < sizeX; x++ )
      {
        final Coordinate coordinate = GeoGridUtilities.toCoordinate( grid, x, y, tmpCrd );
        if( walkingArea == null || walkingArea.contains( x, y, coordinate ) )
        {
          // PERFORMANCE: only access z value if we will visit that cell
          final double value = grid.getValueChecked( x, y );
          coordinate.z = value;
          pwo.operate( x, y, coordinate );
        }
      }

      if( monitor != null )
        monitor.worked( 1 );

      if( monitor != null && monitor.isCanceled() )
        throw new OperationCanceledException( "Abbruch durch Benutzer" );
    }

    return pwo.finish();
  }
}