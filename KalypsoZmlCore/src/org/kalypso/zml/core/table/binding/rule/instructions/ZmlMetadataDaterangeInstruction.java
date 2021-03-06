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
package org.kalypso.zml.core.table.binding.rule.instructions;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.contribs.java.util.DateUtilities;
import org.kalypso.ogc.sensor.metadata.MetadataList;
import org.kalypso.zml.core.table.model.IZmlModel;
import org.kalypso.zml.core.table.model.IZmlModelColumn;
import org.kalypso.zml.core.table.model.references.IZmlModelCell;
import org.kalypso.zml.core.table.model.references.IZmlModelValueCell;
import org.kalypso.zml.core.table.model.references.ZmlModelIndexCell;
import org.kalypso.zml.core.table.model.references.ZmlModelValueCell;
import org.kalypso.zml.core.table.schema.AbstractRuleInstructionType;
import org.kalypso.zml.core.table.schema.MetadataDateRangeInstructionType;

/**
 * @author Dirk kuch
 */
public class ZmlMetadataDaterangeInstruction extends AbstractZmlRuleInstructionType
{

  public ZmlMetadataDaterangeInstruction( final AbstractRuleInstructionType type )
  {
    super( type );
  }

  @Override
  public MetadataDateRangeInstructionType getType( )
  {
    return (MetadataDateRangeInstructionType) super.getType();
  }

  @Override
  public boolean matches( final IZmlModelCell reference )
  {
    final MetadataList metadata = resolveMetadata( reference );
    if( Objects.isNull( metadata ) )
      return false;

    final MetadataDateRangeInstructionType type = getType();

    final Date from = getDate( metadata, type.getPropertyFrom() );
    final Date to = getDate( metadata, type.getPropertyTo() );
    if( Objects.isNull( from, to ) )
      return false;

    final Date referenceDate = reference.getIndexValue();
    if( Objects.isNull( referenceDate ) )
      return false;

    final long diffFrom = from.getTime() - referenceDate.getTime();
    if( diffFrom > 0 )
      return false;
    else if( !type.isPropertyFromInclusive() && diffFrom == 0 )
      return false;

    final long diffTo = to.getTime() - referenceDate.getTime();

    if( diffTo < 0 )
      return false;
    else if( !type.isPropertyToInclusive() && diffTo == 0 )
      return false;

    return true;
  }

  private MetadataList resolveMetadata( final IZmlModelCell reference )
  {
    if( reference instanceof ZmlModelValueCell )
    {
      final IZmlModelColumn column = ((IZmlModelValueCell) reference).getColumn();

      return column.getMetadata();
    }
    else if( reference instanceof ZmlModelIndexCell )
    {
      final IZmlModel model = reference.getModel();
      final IZmlModelColumn[] columns = model.getColumns();
      for( final IZmlModelColumn column : columns )
      {
        if( column.isMetadataSource() )
          return column.getMetadata();
      }

      // fallback
      if( ArrayUtils.isNotEmpty( columns ) )
        return columns[0].getMetadata();
    }

    throw new UnsupportedOperationException();
  }

  private Date getDate( final MetadataList metadata, final String property )
  {
    if( metadata == null || property == null )
      return null;

    final String value = metadata.getProperty( property );
    if( value == null )
      return null;

    return DateUtilities.parseDateTime( value );
  }

}
