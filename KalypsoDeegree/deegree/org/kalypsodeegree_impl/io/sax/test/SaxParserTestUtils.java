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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.xml.serializer.ToXMLStream;
import org.kalypso.commons.java.net.UrlUtilities;
import org.kalypsodeegree_impl.io.sax.marshaller.AbstractMarshaller;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Gernot Belger
 */
public final class SaxParserTestUtils
{
  public static final String ENCODING = "UTF-8";

  private SaxParserTestUtils( )
  {
    throw new UnsupportedOperationException( "Helper class, do not instantiate" );
  }

  /**
   * Create an XMLReader that writes all xml into the given {@link OutputStream}.
   */
  public static XMLReader createXMLReader( final OutputStream os ) throws SAXException
  {
    final ToXMLStream xmlStream = new ToXMLStream();
    xmlStream.setOutputStream( os );
    // Configure content handler. IMPORTANT: call after setOutputStream!
    xmlStream.setLineSepUse( true );
    xmlStream.setIndent( true );
    xmlStream.setIndentAmount( 1 );
    xmlStream.setEncoding( ENCODING );

    final XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler( xmlStream );

    return reader;
  }

  public static <T> void marshallDocument( final XMLReader reader, final AbstractMarshaller<T> marshaller, final T objectToMarshall ) throws SAXException
  {
    final ContentHandler contentHandler = reader.getContentHandler();
    contentHandler.startDocument();
    marshaller.marshall( objectToMarshall );
    contentHandler.endDocument();
  }

  private static void assertContentEquals( final String expected, final String actual )
  {
    final Pattern whitespacePattern = Pattern.compile( "\\s", Pattern.MULTILINE );

    final String actualCleaned = whitespacePattern.matcher( actual ).replaceAll( "" );
    final String expectedCleaned = whitespacePattern.matcher( expected ).replaceAll( "" );

    Assert.assertEquals( expectedCleaned, actualCleaned );
  }

  public static void assertContentEquals( final URL expectedContent, final File actualContent ) throws IOException
  {
    final String actual = FileUtils.readFileToString( actualContent, ENCODING );
    final String expected = UrlUtilities.toString( expectedContent, ENCODING );

    assertContentEquals( expected, actual );
  }

  public static void assertContentEquals( final URL expectedContent, final String actualContent ) throws IOException
  {
    final String expected = UrlUtilities.toString( expectedContent, ENCODING );
    assertContentEquals( expected, actualContent );
  }
}