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
package org.kalypso.zml.core.table.model.references;

import org.apache.commons.lang3.StringUtils;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.adapter.AbstractObservationImporter;
import org.kalypso.ogc.sensor.status.KalypsoStati;
import org.kalypso.repository.IDataSourceItem;

/**
 * @author Dirk Kuch
 */
public final class ZmlValues
{
  private ZmlValues( )
  {
  }

  public static boolean isStuetzstelle( final IZmlModelValueCell reference ) throws SensorException
  {
    return isStuetzstelle( reference.getStatus(), reference.getDataSource() );
  }

  public static boolean isStuetzstelle( final Number status, final String source )
  {
    if( Objects.allNull( status, source ) ) // TODO correct
      return false;
    else if( Objects.isNotNull( status ) && (status.intValue() & KalypsoStati.BIT_USER_MODIFIED) != 0 )
      return true;
    else if( Objects.isNull( source ) )
      return true;
    else if( source.contains( AbstractObservationImporter.MISSING_VALUE_POSTFIX ) )
      return false;
    else if( StringUtils.equalsIgnoreCase( IDataSourceItem.SOURCE_UNKNOWN, source ) )
      return false;

    return !source.startsWith( IDataSourceItem.FILTER_SOURCE ); //$NON-NLS-1$
  }

  public static boolean isNullstelle( final Number value, final Number status, final String source )
  {
    if( isStuetzstelle( status, source ) )
      return false;

    if( Objects.isNull( value ) )
      return false;

    return value.doubleValue() == 0.0;
  }
}