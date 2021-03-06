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
package org.kalypso.ogc.sensor.timeseries.wq.test;

import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.zml.ZmlFactory;
import org.xml.sax.InputSource;

/**
 * WQObservationFilterTest
 * 
 * @author schlienger
 */
public class WQObservationFilterTest extends TestCase
{
  /**
   * first test
   * 
   * @throws SensorException
   */
  public void testGetValues( ) throws SensorException
  {
    InputStream ins = null;
    try
    {
      ins = WQObservationFilterTest.class.getResourceAsStream( "wq-test.zml" ); //$NON-NLS-1$

      final IObservation obs = ZmlFactory.parseXML( new InputSource( ins ), null ); //$NON-NLS-1$

      final ITupleModel wqValues = obs.getValues( null );

      assertNotNull( wqValues );

      System.out.println( ObservationUtilities.dump( wqValues, "  " ) ); //$NON-NLS-1$
    }
    finally
    {
      IOUtils.closeQuietly( ins );
    }
  }

  /**
   * tests schirgiswalde
   * 
   * @throws SensorException
   */
  public void testSchirgiswalde( ) throws SensorException
  {
    InputStream ins = null;
    try
    {
      ins = WQObservationFilterTest.class.getResourceAsStream( "wq-test2.zml" ); //$NON-NLS-1$

      final IObservation obs = ZmlFactory.parseXML( new InputSource( ins ), null ); //$NON-NLS-1$

      final ITupleModel values = obs.getValues( null );

      assertNotNull( values );

      System.out.println( ObservationUtilities.dump( values, "  " ) ); //$NON-NLS-1$
    }
    finally
    {
      IOUtils.closeQuietly( ins );
    }
  }

  public void testObsWithoutWQParam( ) throws SensorException
  {
    InputStream ins = null;
    try
    {
      ins = WQObservationFilterTest.class.getResourceAsStream( "wq-test3.zml" ); //$NON-NLS-1$

      final IObservation obs = ZmlFactory.parseXML( new InputSource( ins ), null ); //$NON-NLS-1$

      final ITupleModel values = obs.getValues( null );

      assertNotNull( values );

      System.out.println( ObservationUtilities.dump( values, "  " ) ); //$NON-NLS-1$
    }
    finally
    {
      IOUtils.closeQuietly( ins );
    }
  }

  public void testWQTable( ) throws SensorException
  {
    InputStream ins = null;
    try
    {
      ins = WQObservationFilterTest.class.getResourceAsStream( "wq-test4.zml" ); //$NON-NLS-1$

      final IObservation obs = ZmlFactory.parseXML( new InputSource( ins ), null ); //$NON-NLS-1$

      final ITupleModel values = obs.getValues( null );

      assertNotNull( values );

      System.out.println( ObservationUtilities.dump( values, "  " ) ); //$NON-NLS-1$
    }
    finally
    {
      IOUtils.closeQuietly( ins );
    }
  }
}