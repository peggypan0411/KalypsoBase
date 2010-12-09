package org.kalypso.zml.core.table.rules.impl.forecast;

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

import java.util.Date;

import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.ogc.sensor.DateRange;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.ogc.sensor.metadata.MetadataHelper;
import org.kalypso.ogc.sensor.metadata.MetadataList;
import org.kalypso.zml.core.KalypsoZmlCore;
import org.kalypso.zml.core.table.binding.DataColumn;
import org.kalypso.zml.core.table.binding.ZmlRule;
import org.kalypso.zml.core.table.model.IZmlModel;
import org.kalypso.zml.core.table.model.IZmlModelColumn;
import org.kalypso.zml.core.table.model.references.IZmlValueReference;
import org.kalypso.zml.core.table.rules.impl.AbstractZmlTableRule;

/**
 * @author Dirk Kuch
 */
public class ZmlRuleUnsafeForecastValue extends AbstractZmlTableRule
{
  String MD_SICHERE_VORHERSAGE_ENDE = "sichere Vorhersage Ende"; //$NON-NLS-1$

  public static final String ID = "org.kalypso.zml.ui.core.rule.unsafe.forecast.value";

  /**
   * @see org.kalypso.zml.ui.core.rules.IZmlTableRule#getIdentifier()
   */
  @Override
  public String getIdentifier( )
  {
    return ID;
  }

  /**
   * @see org.kalypso.zml.ui.core.rules.IZmlTableRule#apply(org.kalypso.zml.ui.table.provider.ZmlValueReference)
   */
  @Override
  protected boolean doApply( final ZmlRule rule, final IZmlValueReference reference )
  {
    try
    {
      final MetadataList metadata = findMetadata( reference );

      // index value reference!
      final Object index = reference.getValue();
      if( index instanceof Date )
      {
        final Date date = (Date) index;
        final DateRange daterange = MetadataHelper.getDateRange( metadata, MD_SICHERE_VORHERSAGE_ENDE, ITimeseriesConstants.MD_VORHERSAGE_ENDE );

        return daterange.containsInclusive( date );
      }

      return false;
    }
    catch( final SensorException e )
    {
      KalypsoZmlCore.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
    }

    return false;
  }

  private MetadataList findMetadata( final IZmlValueReference reference )
  {
    final IZmlModel model = reference.getModel();
    final IZmlModelColumn[] columns = model.getColumns();
    for( final IZmlModelColumn column : columns )
    {
      final DataColumn dataColumn = column.getDataColumn();
      if( dataColumn.isMetadataSource() )
        return column.getMetadata();
    }

    return null;
  }
}
