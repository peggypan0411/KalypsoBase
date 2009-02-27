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
package org.kalypso.swtchart.chart.axis;

import java.util.Comparator;

import org.eclipse.swt.graphics.Point;
import org.kalypso.swtchart.chart.axis.IAxisConstants.DIRECTION;
import org.kalypso.swtchart.chart.axis.IAxisConstants.POSITION;
import org.kalypso.swtchart.chart.axis.IAxisConstants.PROPERTY;
import org.kalypso.swtchart.chart.axis.component.IAxisComponent;

/**
 * @author schlienger
 * @author burtscher Concrete IAxis implementation - to be used for numeric data
 */
public class NumberAxis extends AbstractAxis<Number>
{
  public NumberAxis( final String id, final String label, final PROPERTY prop, final POSITION pos, final DIRECTION dir, final Comparator<Number> comp )
  {
    super( id, label, prop, pos, dir, comp, Number.class );
  }

  public Double logicalToNormalized( final Number value )
  {
    /* NullPointer Exception, empty Oberservation */
    final Number nTo = getTo();
    final Number nFrom = getFrom();

    if( (nTo == null) || (nFrom == null) )
    {
      return null;
    }

    // r should not be 0 here (see AbstractAxis)
    final double r = nTo.doubleValue() - nFrom.doubleValue();

    final double norm = (value.doubleValue() - getFrom().doubleValue()) / r;

    return norm;
  }

  public Number normalizedToLogical( final Double value )
  {
    final double r = getTo().doubleValue() - getFrom().doubleValue();

    final double logical = value * r + getFrom().doubleValue();

    return logical;
  }

  /**
   * @see org.kalypso.swtchart.axis.IAxis#logicalToScreen(T)
   */
  public int logicalToScreen( final Number value )
  {
    if( (m_registry == null) || (value == null) )
    {
      return 0;
    }

    final IAxisComponent comp = m_registry.getComponent( this );
    if( comp == null )
    {
      return 0;
    }

    return comp.normalizedToScreen( logicalToNormalized( value ) );
  }

  /**
   * @see org.kalypso.swtchart.axis.IAxis#screenToLogical(int)
   */
  public Number screenToLogical( final Integer value )
  {
    if( m_registry == null )
    {
      return Double.NaN;
    }

    final IAxisComponent comp = m_registry.getComponent( this );
    if( comp == null )
    {
      return Double.NaN;
    }

    return normalizedToLogical( comp.screenToNormalized( value ) );
  }

  /**
   * @see org.kalypso.swtchart.axis.IAxis#logicalToScreenInterval(T, T, double) TODO Not implemented yet
   */
  public Point logicalToScreenInterval( final Number value, final Number fixedPoint, final double intervalSize )
  {

    return null;
  }
}
