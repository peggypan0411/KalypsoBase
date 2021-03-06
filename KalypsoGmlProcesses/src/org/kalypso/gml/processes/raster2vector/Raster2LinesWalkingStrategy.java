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
package org.kalypso.gml.processes.raster2vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.kalypso.gml.processes.i18n.Messages;
import org.kalypso.grid.GeoGridException;
import org.kalypso.grid.GeoGridUtilities;
import org.kalypso.grid.IGeoGrid;
import org.kalypso.grid.IGeoGridWalker;
import org.kalypso.grid.IGeoWalkingStrategy;
import org.kalypso.grid.areas.IGeoGridArea;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author jung
 */
public class Raster2LinesWalkingStrategy implements IGeoWalkingStrategy
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
      monitor.beginTask( Messages.getString( "org.kalypso.gml.processes.raster2vector.Raster2LinesWalkingStrategy.0" ), sizeY ); //$NON-NLS-1$

    final int yStart = 0;
    final int yEnd = sizeY;
    final int xStart = 0;
    final int xEnd = sizeX;

    pwo.start( grid );

    final Coordinate tmpCrd = new Coordinate();

    for( int y = yStart; y < yEnd + 2; y++ )
    {
      for( int x = xStart; x < xEnd + 1; x++ )
      {
        final Coordinate coordinate = GeoGridUtilities.calcCoordinate( grid, x, y, tmpCrd );
        pwo.operate( x, y, coordinate );
      }

      if( monitor != null )
        monitor.worked( 1 );

      if( monitor != null && monitor.isCanceled() )
        throw new OperationCanceledException( Messages.getString( "org.kalypso.gml.processes.raster2vector.Raster2LinesWalkingStrategy.1" ) ); //$NON-NLS-1$
    }

    return pwo.finish();
  }
}
