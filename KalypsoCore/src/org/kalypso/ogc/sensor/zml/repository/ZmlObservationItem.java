/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ogc.sensor.zml.repository;

import java.io.File;

import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.zml.ZmlFactory;
import org.kalypso.repository.file.FileItem;
import org.kalypso.repository.file.FileRepository;

/**
 * An IObservation aware FileItem.
 *
 * @author schlienger
 */
public class ZmlObservationItem extends FileItem
{
  private IObservation m_zml = null;

  public ZmlObservationItem( final FileRepository rep, final File file )
  {
    super( rep, file );
  }

  /**
   * @see org.kalypso.repository.file.FileItem#equals(java.lang.Object)
   */
  @Override
  public boolean equals( final Object obj )
  {
    if( obj instanceof ZmlObservationItem )
    {
      final ZmlObservationItem item = (ZmlObservationItem) obj;

      return getFile().equals( item.getFile() );
    }

    return super.equals( obj );
  }

  @Override
  public Object getAdapter( final Class anotherClass )
  {
    try
    {
      if( anotherClass == IObservation.class )
        return parseZmlFile();
    }
    catch( final SensorException e )
    {
      // TODO handling
      throw new RuntimeException( e );
    }

    return null;
  }

  /**
   * Helper, lazy loading.
   *
   * @return observation object read from the file
   * @throws SensorException
   */
  private IObservation parseZmlFile( ) throws SensorException
  {
    try
    {
      // check against the filter
      if( m_zml == null && getRep().getFilter().accept( getFile() ) )
      {
        final File f = getFile();
        m_zml = ZmlFactory.parseXML( f.toURI().toURL() );
      }

      return m_zml;
    }
    catch( final Exception e ) // generic Exception caught for simplicity
    {
      throw new SensorException( e );
    }
  }
}