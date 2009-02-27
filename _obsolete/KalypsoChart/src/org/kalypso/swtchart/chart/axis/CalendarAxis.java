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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.graphics.Point;
import org.kalypso.swtchart.chart.axis.IAxisConstants.DIRECTION;
import org.kalypso.swtchart.chart.axis.IAxisConstants.POSITION;
import org.kalypso.swtchart.chart.axis.IAxisConstants.PROPERTY;
import org.kalypso.swtchart.chart.axis.component.IAxisComponent;
import org.kalypso.swtchart.logging.Logger;

/**
 * @author schlienger
 * @author burtscher Concrete IAxis implementation - to be used for calendar data from XML documents
 */
public class CalendarAxis extends AbstractAxis<Calendar>
{
  public CalendarAxis( final String id, final String label, final PROPERTY prop, final POSITION pos, final DIRECTION dir )
  {
    super( id, label, prop, pos, dir, Calendar.class );
  }

  public double logicalToNormalized( final Calendar value )
  {
    final Calendar gcTo = getTo();
    final long toMillis = gcTo.getTimeInMillis();

    final Calendar gcFrom = getFrom();
    final long fromMillis = gcFrom.getTimeInMillis();

    final long valueMillis = value.getTimeInMillis();

    // r should not be 0 here (see AbstractAxis)
    final long r = toMillis - fromMillis;

    // Die rechte Seite muss unbedingt nach double gecastet werden, da sonst auf die Werte 0 oder 1 gerundet wird.
    final double norm = ((double) (valueMillis - fromMillis)) / r;
    // Logger.trace("Normalization: "+DateFormat.getInstance().format(value)+" => "+norm+"; from:
    return norm;
  }

  public Calendar normalizedToLogical( final double value )
  {
    final double r = getTo().getTimeInMillis() - getFrom().getTimeInMillis();

    final long logical = (long) (value * r + getFrom().getTimeInMillis());

    final Calendar c = Calendar.getInstance();

    c.setTime( new Date( logical ) );

    return c;
  }

  /**
   * @see org.kalypso.swtchart.axis.IAxis#logicalToScreen(T)
   */
  public int logicalToScreen( final Calendar value )
  {
    if( m_registry == null )
    {
      return 0;
    }

    final IAxisComponent comp = m_registry.getComponent( this );
    if( comp == null )
    {
      return 0;
    }

    final double norm = logicalToNormalized( value );
    final int screen = comp.normalizedToScreen( norm );
    return screen;
  }

  /**
   * @see org.kalypso.swtchart.axis.IAxis#screenToLogical(int)
   */
  public Calendar screenToLogical( final Integer value )
  {
    if( m_registry == null )
    {
      return null;
    }

    final IAxisComponent comp = m_registry.getComponent( this );
    if( comp == null )
    {
      return null;
    }

    return normalizedToLogical( comp.screenToNormalized( value ) );
  }

  /**
   * @see org.kalypso.swtchart.axis.IAxis#logicalToScreenInterval(T, T, double)
   */
  public Point logicalToScreenInterval( final Calendar value, final Calendar fixedPoint, final double intervalSize )
  {
    // intervalSize wird als Millisecond-Angabe interpretiert
    final long intervalMillis = (long) intervalSize;
    final long fixedMillis = fixedPoint.getTimeInMillis();
    final long valueMillis = value.getTimeInMillis();
    /*
     * der Increment-Wert für die Schleife richtet sich nach der Richtung, in der der Wert vom fixedPoint gesehen liegt
     */
    long start = fixedMillis;
    long end = fixedMillis;
    Logger.trace( "Axis (" + getIdentifier() + "): starting IntervalCalculation" );
    if( start >= valueMillis )
    {
      while( start > valueMillis )
      {
        start -= intervalMillis;
      }
      end = start + intervalMillis;
    }
    else
    {
      while( end < valueMillis )
      {
        end += intervalMillis;
      }
      start = end - intervalMillis;
    }
    Logger.trace( "Axis (" + getIdentifier() + "): IntervalCalculation finished" );

    // Start-Zeitpunkt des Wert-Intervalls
    final Calendar startCal = Calendar.getInstance();
    startCal.setTimeInMillis( start );

    // End-Zeitpunkt des Wert-Intervalls
    final Calendar endCal = Calendar.getInstance();
    endCal.setTimeInMillis( end );

    final IAxisComponent comp = m_registry.getComponent( this );
    if( comp == null )
    {
      return null;
    }

    final int startPoint = comp.normalizedToScreen( logicalToNormalized( startCal ) );
    final int endPoint = comp.normalizedToScreen( logicalToNormalized( endCal ) );
    Logger.trace( "Axis (" + getIdentifier() + "): Using Interval: " + startPoint + ":" + endPoint );
    return new Point( startPoint, endPoint );
  }

  /**
   * @see org.kalypso.swtchart.axis.IAxis#getMin()
   */
  @Override
  public Calendar getFrom( )
  {
    // das muss geklont werden, da der Wert sonst von aussen veränderbar ist
    final Calendar from = super.getFrom();
    return from == null ? null : (Calendar) from.clone();
  }

  /**
   * @see org.kalypso.swtchart.axis.IAxis#getMin()
   */
  @Override
  public Calendar getTo( )
  {
    // das muss geklont werden, da der Wert sonst von aussen veränderbar ist
    final Calendar to = super.getTo();
    return to == null ? null : (Calendar) to.clone();
  }

}
