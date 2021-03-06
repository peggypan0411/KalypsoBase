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
package de.openali.odysseus.chart.framework.model.data.impl;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Comparator;

import de.openali.odysseus.chart.framework.model.data.IDataOperator;
import de.openali.odysseus.chart.framework.model.data.IDataRange;

/**
 * @author burtscher1
 * @deprecated
 */
@Deprecated
public class DummyDataOperator<T> implements IDataOperator<T>
{

  /**
   * @see de.openali.odysseus.chart.framework.model.data.IDataOperator#getComparator()
   */
  @Override
  public Comparator<T> getComparator( )
  {
    return new Comparator<T>()
    {
      @Override
      public int compare( final T o1, final T o2 )
      {
        return 0;
      }

    };
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.data.IDataOperator#getFormat(de.openali.odysseus.chart.framework.model.data.IDataRange)
   */
  @Override
  public Format getFormat( final IDataRange<Number> range )
  {
    return new Format()
    {

      @Override
      public StringBuffer format( final Object obj, final StringBuffer toAppendTo, final FieldPosition pos )
      {
        return null;
      }

      @Override
      public Object parseObject( final String source, final ParsePosition pos )
      {
        return null;
      }
    };
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.data.IDataOperator#logicalToNumeric(java.lang.Object)
   */
  @Override
  public Double logicalToNumeric( final T logVal )
  {
    return Double.NaN;
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.data.IDataOperator#numericToLogical(java.lang.Number)
   */
  @Override
  public T numericToLogical( final Number numVal )
  {
    return null;
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.data.IStringDataConverter#logicalToString(java.lang.Object)
   */
  @Override
  public String logicalToString( final T value )
  {
    return ""; //$NON-NLS-1$
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.data.IStringParser#getFormatHint()
   */
  @Override
  public String getFormatHint( )
  {
    return "no hint - this is a dummy implementation"; //$NON-NLS-1$
  }

  /**
   * @see de.openali.odysseus.chart.framework.model.data.IStringParser#stringToLogical(java.lang.String)
   */
  @Override
  public T stringToLogical( final String value )
  {
    return null;
  }

}
