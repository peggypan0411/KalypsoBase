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
package org.kalypso.contribs.java.util.test;

import junit.framework.TestCase;

import org.kalypso.contribs.java.util.Arrays;

/**
 * tests for Arrays class
 * 
 * @author schlienger
 */
public class ArraysTest extends TestCase
{
  public void testRemoveDupicates( )
  {
    final double[] d = { 1.002, 1.002, 2.3, 4.5, 6.7, 5.000333, 5.000334, 5.000336, 8 };

    assertEquals( d.length, 9 );

    final double[] d1 = Arrays.removeDupicates( d, .0000000001 );
    System.out.println( "Remove dup1:" + Arrays.dump( d1 ) );
    assertEquals( d1.length, 8 );

    final double[] d2 = Arrays.removeDupicates( d, .0001 );
    System.out.println( "Remove dup2:" + Arrays.dump( d2 ) );
    assertEquals( d2.length, 6 );

    final double[] d3 = Arrays.removeDupicates( d, .0000005 );
    System.out.println( "Remove dup3:" + Arrays.dump( d3 ) );
    assertEquals( d3.length, 8 );
  }

  public void testTruncSmaller( )
  {
    final double[] d = { 1.002, 1.002, 2.3, 4.5, 6.7, 5.000333, 5.000334, 5.000336, 8 };

    double value = 4;
    double[] d1 = Arrays.truncSmaller( d, value );
    System.out.println( "Trunc smaller " + value + ": " + Arrays.dump( d1 ) );
    assertEquals( d1.length, 4 );
    assertEquals( d1[d1.length - 1], value, 0.0001 );

    value = 9;
    d1 = Arrays.truncSmaller( d, value );
    System.out.println( "Trunc smaller " + value + ": " + Arrays.dump( d1 ) );
    assertEquals( d1.length, 10 );
    assertEquals( d1[d1.length - 1], value, 0.0001 );

    value = 1;
    d1 = Arrays.truncSmaller( d, value );
    System.out.println( "Trunc smaller " + value + ": " + Arrays.dump( d1 ) );
    assertEquals( d1.length, 1 );
    assertEquals( d1[d1.length - 1], value, 0.0001 );
  }

  public void testTruncBigger( )
  {
    final double[] d = { 1.002, 1.002, 2.3, 4.5, 6.7, 5.000333, 5.000334, 5.000336, 8 };

    double value = 4;
    double[] d1 = Arrays.truncBigger( d, value );
    System.out.println( "Trunc bigger " + value + ": " + Arrays.dump( d1 ) );
    assertEquals( d1.length, 7 );
    assertEquals( d1[0], value, 0.0001 );

    value = 9;
    d1 = Arrays.truncBigger( d, value );
    System.out.println( "Trunc bigger " + value + ": " + Arrays.dump( d1 ) );
    assertEquals( d1.length, 1 );
    assertEquals( d1[0], value, 0.0001 );

    value = 1;
    d1 = Arrays.truncBigger( d, value );
    System.out.println( "Trunc bigger " + value + ": " + Arrays.dump( d1 ) );
    assertEquals( d1.length, 10 );
    assertEquals( d1[0], value, 0.0001 );
  }

  public void testImplode( )
  {
    final String test = "1.2.3.4.5.6.7.8.9";

    final String[] splits = test.split( "\\." );

    assertEquals( splits.length, 9 );

    final String s1 = Arrays.implode( splits, ".", 0, 8 );

    assertEquals( s1, test );

    final String s2 = Arrays.implode( splits, ".", 2, 7 );

    assertEquals( s2, "3.4.5.6.7.8" );

    final String s3 = Arrays.implode( splits, "", 0, 8 );

    assertEquals( s3, "123456789" );
  }
}
