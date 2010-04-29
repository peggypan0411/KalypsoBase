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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypsodeegree.model.coverage.GridRange;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.gml.binding.commons.RectifiedGridDomain;
import org.kalypsodeegree_impl.gml.binding.commons.RectifiedGridDomain.OffsetVector;
import org.kalypsodeegree_impl.model.cv.GridRange_Impl;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * {@link IGridMetaReader} implementation for World-Files.
 *
 * @author Dirk Kuch
 */
public class GridMetaReaderBinWorldFile implements IGridMetaReader
{
  private final URL m_worldFile;

  private WorldFile m_world;

  private final URL m_binFile;

  private final IStatus m_valid;

  public GridMetaReaderBinWorldFile( final URL binFile, final URL worldFile )
  {
    m_binFile = binFile;
    m_worldFile = worldFile;
    m_valid = setup();
  }

  private IStatus setup( )
  {
    if( m_worldFile == null )
    {
      final String msg = String.format( "No world file found for %s", m_binFile );
      return StatusUtilities.createStatus( IStatus.ERROR, msg, null );
    }

    InputStream is = null;
    try
    {
      is = m_worldFile.openStream();
      m_world = new WorldFileReader().readWorldFile( is );
      return Status.OK_STATUS;
    }
    catch( final IOException e )
    {
      e.printStackTrace();
      final String msg = String.format( "Failed to access world file %s: %s", m_worldFile, e.toString() );
      return StatusUtilities.createStatus( IStatus.ERROR, msg, e );
    }
   finally
    {
      IOUtils.closeQuietly( is );
    }

  }

  /**
   * @see org.kalypso.gml.ui.wizard.imports.IRasterMetaReader#getLowerLeftCornerY()
   */
  public double getOriginCornerY( )
  {
    return m_world.getUlcy();
  }

  /**
   * @see org.kalypso.gml.ui.wizard.imports.IRasterMetaReader#getPhiX()
   */
  public double getVectorXy( )
  {
    return m_world.getRasterXGeoY();
  }

  /**
   * @see org.kalypso.gml.ui.wizard.imports.IRasterMetaReader#getPhiY()
   */
  public double getVectorYx( )
  {
    return m_world.getRasterYGeoX();
  }

  /**
   * @see org.kalypso.gml.ui.wizard.imports.IRasterMetaReader#getPixelDx()
   */
  public double getVectorXx( )
  {
    return m_world.getRasterXGeoX();
  }

  /**
   * @see org.kalypso.gml.ui.wizard.imports.IRasterMetaReader#getPixelDy()
   */
  public double getVectorYy( )
  {
    return m_world.getRasterYGeoY();
  }

  /**
   * @see org.kalypso.gml.ui.wizard.imports.IRasterMetaReader#getUpperLeftCornerX()
   */
  public double getOriginCornerX( )
  {
    return m_world.getUlcx();
  }

  /**
   * @see org.kalypso.gml.ui.wizard.imports.IRasterMetaReader#getCoverage(org.kalypsodeegree_impl.model.cv.RectifiedGridDomain.OffsetVector,
   *      org.kalypsodeegree_impl.model.cv.RectifiedGridDomain.OffsetVector, java.lang.Double[],
   *      org.opengis.cs.CS_CoordinateSystem)
   */
  public RectifiedGridDomain getCoverage( final OffsetVector offsetX, final OffsetVector offsetY, final Double[] upperLeftCorner, final String crs ) throws Exception
  {
    if( (offsetX == null) || (offsetY == null) || (upperLeftCorner == null) || (upperLeftCorner.length != 2) || (crs == null) )
      throw new IllegalStateException();

    BinaryGeoGrid grid = null;
    try
    {
      grid = BinaryGeoGrid.openGrid( m_binFile, new Coordinate(), new Coordinate(), new Coordinate(), "", false );
      final int width = grid.getSizeX();
      final int height = grid.getSizeY();
      grid.dispose();

      final double[] lows = new double[] { 0, 0 };
      final double[] highs = new double[] { width, height };

      final GridRange gridRange = new GridRange_Impl( lows, highs );
      final GM_Point origin = GeometryFactory.createGM_Point( upperLeftCorner[0], upperLeftCorner[1], crs );

      return new RectifiedGridDomain( origin, offsetX, offsetY, gridRange );
    }
    finally
    {
      if( grid != null )
        grid.dispose();
    }
  }

  /**
   * @see org.kalypso.grid.IGridMetaReader#isValid()
   */
  @Override
  public IStatus isValid( )
  {
    return m_valid;
  }
}