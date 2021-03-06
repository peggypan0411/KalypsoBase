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
package org.kalypso.gml.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.kalypso.gml.GMLException;
import org.kalypso.gml.GMLorExceptionContentHandler;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

@Ignore
public class GmlParsingTester extends TestCase
{
  private final SAXParserFactory m_saxFactory = SAXParserFactory.newInstance();

  @Override
  protected void setUp( ) throws Exception
  {
    super.setUp();

    m_saxFactory.setNamespaceAware( true );
  }

  protected GMLWorkspace readGml( final String location ) throws IOException, ParserConfigurationException, SAXException, GMLException
  {
    final URL resource = getClass().getResource( location );
    InputStream is = null;
    try
    {
      is = new BufferedInputStream( resource.openStream() );

      final GMLWorkspace workspace = parseGml( is, resource );

      is.close();

      return workspace;
    }
    finally
    {
      IOUtils.closeQuietly( is );
    }
  }

  protected GMLWorkspace parseGml( final InputStream is, final URL context ) throws ParserConfigurationException, SAXException, IOException, GMLException
  {
    final SAXParser saxParser = m_saxFactory.newSAXParser();
    // make namespace-prefxes visible to content handler
    // used to allow necessary schemas from gml document
// saxParser.setProperty( "http://xml.org/sax/features/namespace-prefixes", Boolean.TRUE );
    final XMLReader reader = saxParser.getXMLReader();

    // TODO: also set an error handler here
    // TODO: use progress-monitors to show progress and let the user cancel parsing

    final GMLorExceptionContentHandler exceptionHandler = new GMLorExceptionContentHandler( reader, null, context, null );
    reader.setContentHandler( exceptionHandler );

    final InputSource inputSource = new InputSource( is );
    reader.parse( inputSource );

    return exceptionHandler.getWorkspace();
  }
}
