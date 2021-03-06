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
package org.kalypsodeegree_impl.io.sax.test;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.kalypso.gmlschema.types.UnmarshallResultEater;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.io.sax.parser.PointContentHandler;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author Felipe Maximino
 */
public class PointContentHandlerTest extends AssertGeometry
{
  private static final GM_Point POINT2D = GeometryFactory.createGM_Point( 0.0, 1.0, "EPSG:31467" );

  private static final GM_Point POINT3D = GeometryFactory.createGM_Point( 0.0, 1.0, 2.0, "EPSG:31467" );

  private static final String ERROR_COORDINATES = "One point must have at least 2 coordinates and at most 3 coordinates!";

  private static final String ERROR_TUPLES = "One point must have exactly one tuple of coordinates.";

  /**
   * tests a 3 dimensional GM_Point.
   */
  @Test
  public void testPoint1( ) throws Exception
  {
    final GM_Point point = parsePoint( "/etc/test/resources/point1.gml" );
    assertPoint( POINT3D, point );
  }

  /**
   * tests a 3 dimensional GM_Point with actually only 2 coordinates. should throw an exception
   */
  @Test
  public void testPoint2( ) throws Exception
  {
    try
    {
      parsePoint( "/etc/test/resources/point2.gml" );
    }
    catch( final SAXParseException e )
    {
      assertEquals( ERROR_COORDINATES, e.getMessage() );
    }
  }

  /**
   * tests a 2 dimensional GM_point.
   */
  @Test
  public void testPoint3( ) throws Exception
  {
    final GM_Point point = parsePoint( "/etc/test/resources/point3.gml" );
    assertPoint( POINT2D, point );
  }

  /**
   * tests a 3 dimensional GM_Point with different coordinates, tuples and decimal separators
   */
  @Test
  public void testPoint4( ) throws Exception
  {
    final GM_Point point = parsePoint( "/etc/test/resources/point4.gml" );
    assertPoint( POINT3D, point );
  }

  /**
   * tests a 3 dimensional GM_Point with actually 2 tuples of coordinates. should throw an exception.
   */
  @Test
  public void testPoint5( ) throws Exception
  {
    try
    {
      parsePoint( "/etc/test/resources/point5.gml" );
      fail( "This should throw an exception" );
    }
    catch( final SAXParseException e )
    {
      assertEquals( ERROR_TUPLES, e.getMessage() );
    }

  }

  /**
   * test a 3 dimensional GM_Point with gml:coord element
   */
  @Test
  public void testPoint6( ) throws Exception
  {
    final GM_Point point = parsePoint( "/etc/test/resources/point6.gml" );
    assertPoint( POINT3D, point );
  }

  /**
   * test a 2 dimensional GM_Point with gml:coord element
   */
  @Test
  public void testPoint7( ) throws Exception
  {
    final GM_Point point = parsePoint( "/etc/test/resources/point7.gml" );
    assertPoint( POINT2D, point );
  }

  /**
   * test a 2 dimensional GM_Point with gml:coord element, but only X set. should throw an exception.
   */
  @Test
  public void testPoint8( ) throws Exception
  {
    try
    {
      parsePoint( "/etc/test/resources/point8.gml" );
      fail( "This should throw an exception" );
    }
    catch( final SAXParseException e )
    {
      assertEquals( ERROR_COORDINATES, e.getMessage() );
    }
  }

  /**
   * test a 3 dimensional GM_Point with gml:coord element, but the coordinates are set in the wrong order (X, Z, Y).
   * should throw an exception.
   */
  @Test
  public void testPoint9( ) throws Exception
  {
    try
    {
      parsePoint( "/etc/test/resources/point9.gml" );
      fail( "This should throw an exception" );
    }
    catch( final SAXParseException e )
    {
      assertTrue( e.getMessage().startsWith( "Unexpected start element:" ) && e.getMessage().contains( "}Z" ) );
    }
  }

  /**
   * test a 2 dimensional GM_Point with gml:pos element
   */
  @Test
  public void testPoint10( ) throws Exception
  {
    final GM_Point point = parsePoint( "/etc/test/resources/point10.gml" );
    assertPoint( POINT3D, point );
  }

  private GM_Point parsePoint( final String name ) throws Exception
  {
    final InputSource is = new InputSource( getClass().getResourceAsStream( name ) );

    final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
    saxFactory.setNamespaceAware( true );

    final SAXParser parser = saxFactory.newSAXParser();
    final XMLReader reader = parser.getXMLReader();

    final GM_Point[] result = new GM_Point[1];
    final UnmarshallResultEater resultEater = new UnmarshallResultEater()
    {
      @Override
      public void unmarshallSuccesful( final Object value )
      {
        assertTrue( value instanceof GM_Point );
        result[0] = (GM_Point) value;
      }
    };

    final ContentHandler contentHandler = new PointContentHandler( reader, resultEater, null );
    reader.setContentHandler( contentHandler );
    reader.parse( is );

    return result[0];
  }
}
