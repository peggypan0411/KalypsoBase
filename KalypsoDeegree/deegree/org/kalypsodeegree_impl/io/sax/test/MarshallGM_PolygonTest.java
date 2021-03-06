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

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.io.sax.marshaller.PolygonMarshaller;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author Felipe Maximino
 */
public class MarshallGM_PolygonTest extends TestCase
{
  private final GM_Polygon m_surfaceOneHole;

  private final GM_Polygon m_surfaceTwoHoles;

  private final GM_Polygon m_surfaceNoHole;

  public MarshallGM_PolygonTest( ) throws GM_Exception
  {
    final GM_Position pos0 = GeometryFactory.createGM_Position( 0.0, 0.0, 0.2 );

    final GM_Position pos1 = GeometryFactory.createGM_Position( 0.0, 1.0, 0.2 );

    final GM_Position pos2 = GeometryFactory.createGM_Position( 1.0, 1.0, 1.2 );

    final GM_Position pos3 = GeometryFactory.createGM_Position( 1.0, 0.0, 2.2 );

    final GM_Position pos4 = GeometryFactory.createGM_Position( 0.3, 0.6, 4.0 );

    final GM_Position pos5 = GeometryFactory.createGM_Position( 0.6, 0.3, 5.0 );

    final GM_Position pos6 = GeometryFactory.createGM_Position( 0.6, 0.7, 6.0 );

    final GM_Position pos7 = GeometryFactory.createGM_Position( 0.7, 0.6, 7.0 );

    final GM_Position[] exteriorRing = new GM_Position[] { pos0, pos1, pos2, pos3, pos0 };
    final GM_Position[] interiorRing1 = new GM_Position[] { pos0, pos4, pos5, pos0 };
    final GM_Position[] interiorRing2 = new GM_Position[] { pos2, pos7, pos6, pos2 };

    m_surfaceNoHole = GeometryFactory.createGM_Surface( exteriorRing, new GM_Position[][] {}, "EPSG:31467" );
    m_surfaceOneHole = GeometryFactory.createGM_Surface( exteriorRing, new GM_Position[][] { interiorRing1 }, "EPSG:31467" );
    m_surfaceTwoHoles = GeometryFactory.createGM_Surface( exteriorRing, new GM_Position[][] { interiorRing1, interiorRing2 }, "EPSG:31467" );
  }

  @Test
  public void testPolygoneNoHole( ) throws IOException, SAXException
  {
    testPolygon( "polygon_marshall_noHole.gml", m_surfaceNoHole );
  }

  @Test
  public void testPolygoneOneHole( ) throws IOException, SAXException
  {
    testPolygon( "polygon_marshall_oneHole.gml", m_surfaceOneHole );
  }

  @Test
  public void testPolygoneTwoHoles( ) throws IOException, SAXException
  {
    testPolygon( "polygon_marshall_twoHoles.gml", m_surfaceTwoHoles );
  }

  private void testPolygon( final String expectedResource, final GM_Polygon polygonToSave ) throws IOException, SAXException
  {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();

    final XMLReader reader = SaxParserTestUtils.createXMLReader( os );

    final PolygonMarshaller marshaller = new PolygonMarshaller( reader );
    SaxParserTestUtils.marshallDocument( reader, marshaller, polygonToSave );
    os.close();

    final URL url = getClass().getResource( "/etc/test/resources/" + expectedResource );

    final String actualContent = os.toString( SaxParserTestUtils.ENCODING );
    SaxParserTestUtils.assertContentEquals( url, actualContent );
  }
}
