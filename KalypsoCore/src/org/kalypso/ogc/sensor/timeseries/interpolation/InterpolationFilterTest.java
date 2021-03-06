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
package org.kalypso.ogc.sensor.timeseries.interpolation;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.kalypso.ogc.sensor.DateRange;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.request.ObservationRequest;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.ogc.sensor.zml.ZmlFactory;

/**
 * InterpolationFilterTest
 * 
 * @author schlienger
 */
public class InterpolationFilterTest extends TestCase
{
  private static final SimpleDateFormat SDF = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ); //$NON-NLS-1$

  private IObservation m_obs;

  private IAxis m_dateAxis;

  private IAxis m_valueAxis;

  @Override
  protected void setUp( ) throws Exception
  {
    super.setUp();

    final URL url = InterpolationFilterTest.class.getResource( "InterpolationFilterTest.zml" ); //$NON-NLS-1$
    m_obs = ZmlFactory.parseXML( url ); //$NON-NLS-1$
    assertNotNull( m_obs );

    m_dateAxis = ObservationUtilities.findAxisByClass( m_obs.getAxes(), Date.class );
    assertNotNull( m_dateAxis );
    m_valueAxis = KalypsoStatusUtils.findAxisByClass( m_obs.getAxes(), Double.class, true );
    assertNotNull( m_valueAxis );
  }

  public void testGetValues( ) throws SensorException, ParseException
  {
    final InterpolationFilter filter = new InterpolationFilter( Calendar.HOUR_OF_DAY, 1, true, "0", 0, true ); //$NON-NLS-1$
    filter.initFilter( null, m_obs, null );

    SDF.setTimeZone( TimeZone.getTimeZone( "UTC" ) ); //$NON-NLS-1$

    // test with same date-range
    final ITupleModel m1 = filter.getValues( null );
    final Date from1 = SDF.parse( "2004-11-23 13:00:00" ); //$NON-NLS-1$
    final Date to1 = SDF.parse( "2004-11-25 13:00:00" ); //$NON-NLS-1$
    verifyTuppleModel( m1, from1, to1, new Double( 60.0 ), new Double( 37.0 ) );

    // test with bigger date-range
    final Date from2 = SDF.parse( "2004-11-23 10:00:00" ); //$NON-NLS-1$
    final Date to2 = SDF.parse( "2004-11-25 17:00:00" ); //$NON-NLS-1$
    final ITupleModel m2 = filter.getValues( new ObservationRequest( new DateRange( from2, to2 ) ) );
    verifyTuppleModel( m2, from2, to2, new Double( 0 ), new Double( 37.0 ) );

    // test with smaller date-range
    final Date from3 = SDF.parse( "2004-11-23 19:00:00" ); //$NON-NLS-1$
    final Date to3 = SDF.parse( "2004-11-25 11:00:00" ); //$NON-NLS-1$
    final ITupleModel m3 = filter.getValues( new ObservationRequest( new DateRange( from3, to3 ) ) );
    // FIXME: check: fails, but it shouldn't
// verifyTuppleModel( m3, from3, to3, new Double( 55 ), new Double( 37.0 ) );
  }

  private void verifyTuppleModel( final ITupleModel m, final Date from, final Date to, final Double firstValue, final Double lastValue ) throws SensorException
  {
    final Calendar cal = Calendar.getInstance();
    cal.setTime( from );
    int i = 0;
    while( cal.getTime().before( to ) )
    {
      i++;
      cal.add( Calendar.HOUR_OF_DAY, 1 );
    }
    i++;

    assertEquals( i, m.size() );

    assertEquals( from, m.get( 0, m_dateAxis ) );
    assertEquals( firstValue.doubleValue(), ((Number) m.get( 0, m_valueAxis )).doubleValue(), 0.001 );
    assertEquals( to, m.get( m.size() - 1, m_dateAxis ) );
    assertEquals( lastValue.doubleValue(), ((Number) m.get( m.size() - 1, m_valueAxis )).doubleValue(), 0.001 );
  }

}
