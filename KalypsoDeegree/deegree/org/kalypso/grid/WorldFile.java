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

/**
 * Represents the contents of a World-File (tfw, gfw, ...).
 * 
 * @author Gernot Belger
 */
public class WorldFile
{
  private final double m_rasterXGeoX;

  private final double m_rasterXGeoY;

  private final double m_rasterYGeoX;

  private final double m_rasterYGeoY;

  private final double m_ulcx;

  private final double m_ulcy;

  public WorldFile( final double rasterXGeoX, final double rasterXGeoY, final double rasterYGeoX, final double rasterYGeoY, final double ulcx, final double ulcy )
  {
    m_rasterXGeoX = rasterXGeoX;
    m_rasterXGeoY = rasterXGeoY;
    m_rasterYGeoX = rasterYGeoX;
    m_rasterYGeoY = rasterYGeoY;
    m_ulcx = ulcx;
    m_ulcy = ulcy;
  }

  public double getRasterXGeoX( )
  {
    return m_rasterXGeoX;
  }

  public double getRasterXGeoY( )
  {
    return m_rasterXGeoY;
  }

  public double getRasterYGeoX( )
  {
    return m_rasterYGeoX;
  }

  public double getRasterYGeoY( )
  {
    return m_rasterYGeoY;
  }

  public double getUlcx( )
  {
    return m_ulcx;
  }

  public double getUlcy( )
  {
    return m_ulcy;
  }

}
