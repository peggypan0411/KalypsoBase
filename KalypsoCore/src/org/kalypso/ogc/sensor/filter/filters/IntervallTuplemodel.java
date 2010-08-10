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
package org.kalypso.ogc.sensor.filter.filters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.kalypso.contribs.java.util.CalendarIterator;
import org.kalypso.contribs.java.util.CalendarUtilities;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IAxisRange;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.impl.AbstractTupleModel;
import org.kalypso.ogc.sensor.impl.SimpleTupleModel;
import org.kalypso.ogc.sensor.metadata.ITimeseriesConstants;
import org.kalypso.ogc.sensor.status.KalypsoStati;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;

/**
 * @author doemming
 */
public class IntervallTuplemodel extends AbstractTupleModel
{
  private static final int TODO_NOTHING = 0;

  private static final int TODO_GOTO_NEXT_TARGET = 1;

  private static final int TODO_GOTO_NEXT_SRC = 2;

  private static final int TODO_FINISHED = 3;

  private final ITupleModel m_srcModel;

  private final int m_mode;

  private final IAxis[] m_valueAxis;

  private final IAxis m_dateAxis;

  private final IAxis[] m_statusAxis;

  private final int m_calendarField;

  private final int m_amount;

  private final Calendar m_from;

  private final Calendar m_to;

  private final ITupleModel m_intervallModel;

  private final double m_defaultValue;

  private final int m_defaultStatus;

  public IntervallTuplemodel( final int mode, final int calendarField, final int amount, final int startCalendarValue, final String startCalendarField, final ITupleModel srcModel, final Date from, final Date to, final double defaultValue, final int defaultStatus ) throws SensorException
  {
    super( srcModel.getAxisList() );
    m_mode = mode;
    m_calendarField = calendarField;
    m_amount = amount;
    m_srcModel = srcModel;
    m_defaultValue = defaultValue;
    m_defaultStatus = defaultStatus;

    // check axis
    final IAxis[] axisList = getAxisList();
    m_dateAxis = ObservationUtilities.findAxisByType( axisList, ITimeseriesConstants.TYPE_DATE );
    m_statusAxis = KalypsoStatusUtils.findStatusAxes( axisList );
    m_valueAxis = findValueAxes( axisList );

    final IAxisRange sourceModelRange = getSourceModelRange( srcModel, m_dateAxis );

    m_from = initFrom( from, sourceModelRange );
    m_to = initTo( to, sourceModelRange );

    m_intervallModel = initModell( axisList, startCalendarField, startCalendarValue );
  }

  private IAxis[] findValueAxes( final IAxis[] axisList )
  {
    final List<IAxis> valueAxis = new ArrayList<IAxis>();
    for( final IAxis axis : axisList )
    {
      boolean isValueAxis = true;
      if( axis == m_dateAxis )
        isValueAxis = false;
      for( final IAxis axi : m_statusAxis )
      {
        if( axis == axi )
          isValueAxis = false;
      }
      if( isValueAxis )
        valueAxis.add( axis );
    }

    return valueAxis.toArray( new IAxis[valueAxis.size()] );
  }

  private Calendar initFrom( final Date from, final IAxisRange sourceModelRange )
  {
    if( from != null )
      return createCalendar( from );

    if( sourceModelRange != null )
      return createCalendar( (Date) sourceModelRange.getLower() );

    return null;
  }

  private Calendar initTo( final Date to, final IAxisRange sourceModelRange )
  {
    if( to != null )
      return createCalendar( to );

    if( sourceModelRange != null )
      return createCalendar( (Date) sourceModelRange.getUpper() );

    return null;
  }

  private static IAxisRange getSourceModelRange( final ITupleModel srcModel, final IAxis dateAxis ) throws SensorException
  {
    return srcModel.getRangeFor( dateAxis );
  }

  private ITupleModel initModell( final IAxis[] axisList, final String startCalendarField, final int startCalendarValue ) throws SensorException
  {
    if( m_from == null || m_to == null )
      return createTuppleModell( axisList, 0 );

    // correct from
    if( startCalendarField != null && startCalendarField.length() > 0 )
      m_from.set( CalendarUtilities.getCalendarField( startCalendarField ), startCalendarValue );

    return doInitModell();
  }

  private ITupleModel doInitModell( ) throws SensorException
  {
    // create empty model
    final IAxis[] axisList = getAxisList();
    final CalendarIterator iterator = new CalendarIterator( m_from, m_to, m_calendarField, m_amount );
    final int stepCount = iterator.size();

    Assert.isTrue( stepCount > 0, String.format( "Empty intervall tuple model. Check from (%s)/to(%s).", m_from, m_to ) );

    final int rows = stepCount - 1;
    final ITupleModel intervallModel = createTuppleModell( axisList, rows );

    // default values
    final int[] defaultStatus = new int[m_statusAxis.length];
    for( int i = 0; i < defaultStatus.length; i++ )
      defaultStatus[i] = m_defaultStatus;

    final double[] defaultValues = new double[m_valueAxis.length];
    for( int i = 0; i < defaultValues.length; i++ )
      defaultValues[i] = m_defaultValue;

    // new Values
    final double[] newValues = new double[m_valueAxis.length];
    for( int i = 0; i < newValues.length; i++ )
      newValues[i] = 0d;

    final int[] newStatus = new int[m_statusAxis.length];
    for( int i = 0; i < newStatus.length; i++ )
      newStatus[i] = KalypsoStati.BIT_OK;

    // initialize target
    Calendar lastTargetCalendar = iterator.next(); // TODO hasnext ?
    int targetRow = 0;
    Intervall targetIntervall = null;

    int srcRow = 0;
    Intervall srcIntervall = null;

    // initialize values
    final Calendar firstSrcCal;
    // check if source timeseries is empty
    final int srcMaxRows = m_srcModel.getCount();
    if( srcMaxRows != 0 ) // not empty
      firstSrcCal = createCalendar( (Date) m_srcModel.getElement( 0, m_dateAxis ) );
    else
      // if empty, we pretend that it begins at requested range
      firstSrcCal = m_from;

    // initialize source
    Calendar lastSrcCalendar = lastTargetCalendar;

    // BUGFIX: handle case when source start before from
    // Before this fix, this lead to a endless loop
    if( firstSrcCal.before( lastSrcCalendar ) )
      lastSrcCalendar = firstSrcCal;

    // fill initial row
    // final Intervall initialIntervall = new Intervall( m_from, m_from, defaultStatus, defaultValues );
    // updateModelfromintervall( m_intervallModel, targetRow, initialIntervall );
    // targetRow++;
    // doemming: removed last 3 rows to avoid generating beginning "0" value.
    int todo = TODO_NOTHING;
    while( todo != TODO_FINISHED )
    {
      // set next source intervall
      if( srcIntervall == null || todo == TODO_GOTO_NEXT_SRC )
      {
        // calculate the end of a sourceintervall with given distance
        final Calendar srcCalIntervallEnd = (Calendar) lastSrcCalendar.clone();
        srcCalIntervallEnd.add( m_calendarField, m_amount );

        // if we are after the source timeseries
        if( srcRow >= srcMaxRows )
        {
          // generate defaults
          // create dummy intervall
          srcIntervall = new Intervall( lastSrcCalendar, srcCalIntervallEnd, defaultStatus, defaultValues );

          lastSrcCalendar = srcIntervall.getEnd();
          // TODO m_to, defaults
          todo = TODO_NOTHING;
          continue;
        }
        // read current values from source timeserie
        final Calendar srcCal = createCalendar( (Date) m_srcModel.getElement( srcRow, m_dateAxis ) );
        final Object[] srcStatusValues = ObservationUtilities.getElements( m_srcModel, srcRow, m_statusAxis );
        final Integer[] srcStati = new Integer[srcStatusValues.length];
        for( int i = 0; i < srcStatusValues.length; i++ )
          srcStati[i] = new Integer( ((Number) srcStatusValues[i]).intValue() );

        final Object[] srcValuesObjects = ObservationUtilities.getElements( m_srcModel, srcRow, m_valueAxis );
        final Double[] srcValues = new Double[srcValuesObjects.length];
        for( int i = 0; i < srcValuesObjects.length; i++ )
          srcValues[i] = (Double) srcValuesObjects[i];
        srcIntervall = null;

        if( !lastSrcCalendar.after( srcCal ) )
        {
          // we need next source intervall

          if( srcCalIntervallEnd.before( firstSrcCal ) )
          {
            // we are before the source timeseries
            srcIntervall = new Intervall( lastSrcCalendar, srcCalIntervallEnd, defaultStatus, defaultValues );
            lastSrcCalendar = srcCalIntervallEnd;
          }
          else
          // we are inside source timeseries
          {
            switch( m_mode )
            {
              case IntervallFilter.MODE_INTENSITY:
                srcIntervall = new Intervall( lastSrcCalendar, srcCal, srcStati, srcValues );
                break;
              default:
                // (IntervallFilter.MODE_SUM) as length of first interval is undefined, we ignore first value
                // TODO solve: for whitch intervall is the first value valid ?
                // there is no definition :-(
                // Bugfix: we use it nevertheless, as it works ok if intervalls are equal;
                // also, always no warning produces problems elsewhere
// if( srcRow > 0 )
                srcIntervall = new Intervall( lastSrcCalendar, srcCal, srcStati, srcValues );
                break;
            }
            lastSrcCalendar = srcCal;
            srcRow++;
          }
        }
        todo = TODO_NOTHING;
      }
      // next target intervall
      if( targetIntervall == null || todo == TODO_GOTO_NEXT_TARGET )
      {
        if( targetIntervall != null )
        {
          updateModelfromIntervall( intervallModel, targetRow, targetIntervall );
          targetRow++;
        }
        if( !iterator.hasNext() )
        {
          todo = TODO_FINISHED;
          continue;
        }
        final Calendar cal = iterator.next();
        if( lastTargetCalendar.before( cal ) )
          targetIntervall = new Intervall( lastTargetCalendar, cal, newStatus, newValues );
        else
          targetIntervall = null;
        lastTargetCalendar = cal;
        todo = TODO_NOTHING;
      }
      // check validity of intervalls
      if( srcIntervall == null )
      {
        todo = TODO_GOTO_NEXT_SRC;
        continue;
      }
      if( targetIntervall == null )
      {
        todo = TODO_GOTO_NEXT_TARGET;
        continue;
      }
      // compute intersection intervall
      final int matrix = srcIntervall.calcIntersectionMatrix( targetIntervall );
      Intervall intersection = null;
      if( matrix != Intervall.STATUS_INTERSECTION_NONE_BEFORE && matrix != Intervall.STATUS_INTERSECTION_NONE_AFTER )
        intersection = srcIntervall.getIntersection( targetIntervall, m_mode );

      switch( matrix )
      {
        case Intervall.STATUS_INTERSECTION_NONE_BEFORE:
          todo = TODO_GOTO_NEXT_TARGET;
          break;
        case Intervall.STATUS_INTERSECTION_NONE_AFTER:
          todo = TODO_GOTO_NEXT_SRC;
          break;
        case Intervall.STATUS_INTERSECTION_START:
        case Intervall.STATUS_INTERSECTION_INSIDE:
          targetIntervall.merge( intersection, m_mode );
          todo = TODO_GOTO_NEXT_TARGET;
          break;
        case Intervall.STATUS_INTERSECTION_END:
        case Intervall.STATUS_INTERSECTION_ARROUND:
          targetIntervall.merge( intersection, m_mode );
          todo = TODO_GOTO_NEXT_SRC;
          break;
        default:
          break;
      }
    }

    return intervallModel;
  }

  private SimpleTupleModel createTuppleModell( final IAxis[] axisList, final int rows )
  {
    return new SimpleTupleModel( axisList, new Object[rows][axisList.length] );
  }

  // accept values for result
  private void updateModelfromIntervall( final ITupleModel model, final int targetRow, final Intervall targetIntervall ) throws SensorException
  {
    final Calendar cal = targetIntervall.getEnd();
    final int[] status = targetIntervall.getStatus();
    final double[] value = targetIntervall.getValue();
    model.setElement( targetRow, cal.getTime(), m_dateAxis );
    for( int i = 0; i < m_statusAxis.length; i++ )
    {
      model.setElement( targetRow, new Integer( status[i] ), m_statusAxis[i] );
    }

    for( int i = 0; i < m_valueAxis.length; i++ )
    {
      model.setElement( targetRow, new Double( value[i] ), m_valueAxis[i] );
    }
  }

  private static Calendar createCalendar( final Date date )
  {
    final Calendar result = Calendar.getInstance();
    result.setTime( date );
    return result;
  }

  @Override
  public int getCount( ) throws SensorException
  {
    return m_intervallModel.getCount();
  }

  @Override
  public int hashCode( )
  {
    return m_intervallModel.hashCode();
  }

  @Override
  public String toString( )
  {
    return m_intervallModel.toString();
  }

  @Override
  public Object getElement( final int index, final IAxis axis ) throws SensorException
  {
    return m_intervallModel.getElement( index, axis );
  }

  @Override
  public void setElement( final int index, final Object element, final IAxis axis )
  {
    throw new UnsupportedOperationException( getClass().getName() + Messages.getString( "org.kalypso.ogc.sensor.filter.filters.IntervallTupplemodel.0" ) ); //$NON-NLS-1$
    // TODO support it
  }

  @Override
  public int indexOf( final Object element, final IAxis axis ) throws SensorException
  {
    // TODO: better than this test: should test if axis.isKey() is true
    if( element instanceof Date )
      return m_srcModel.indexOf( element, axis );
    throw new UnsupportedOperationException( getClass().getName() + Messages.getString( "org.kalypso.ogc.sensor.filter.filters.IntervallTupplemodel.1" ) //$NON-NLS-1$
        + axis.getName() + Messages.getString( "org.kalypso.ogc.sensor.filter.filters.IntervallTupplemodel.2" ) ); //$NON-NLS-1$
  }
}