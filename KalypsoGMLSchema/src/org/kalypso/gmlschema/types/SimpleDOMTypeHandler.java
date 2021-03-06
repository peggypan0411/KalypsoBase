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
package org.kalypso.gmlschema.types;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.ParseException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.kalypso.contribs.java.xml.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * A simple DOM serializer. May be subclassed.
 *
 * @author schlienger
 */
public abstract class SimpleDOMTypeHandler implements IMarshallingTypeHandler
{
  private final QName m_qname;

  private final boolean m_isGeometry;

  public SimpleDOMTypeHandler( final QName qName, final boolean isGeometry )
  {
    m_qname = qName;
    m_isGeometry = isGeometry;
  }

  @Override
  public void marshal( final Object value, final XMLReader reader, final URL context, final String gmlVersion ) throws SAXException
  {
    try
    {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document document = builder.newDocument();
      final Node node = internalMarshall( value, document, context );

      // value is encoded in xml in document object
      final StringWriter writer = new StringWriter();
      XMLHelper.writeDOM( node, "UTF-8", writer ); //$NON-NLS-1$
      IOUtils.closeQuietly( writer );

      // value is encoded in string
      final String xmlString = writer.toString();
      final InputSource input = new InputSource( new StringReader( xmlString ) );
      final SAXParserFactory saxFac = SAXParserFactory.newInstance();
      saxFac.setNamespaceAware( true );

      final SAXParser saxParser = saxFac.newSAXParser();
      final XMLReader xmlReader = saxParser.getXMLReader();
      xmlReader.setContentHandler( reader.getContentHandler() );
      xmlReader.parse( input );
    }
    catch( final SAXException saxe )
    {
      throw saxe;
    }
    catch( final Exception e )
    {
      throw new SAXException( e );
    }
  }

  protected abstract Node internalMarshall( final Object value, final Document document, final URL context ) throws TypeRegistryException;

  @Override
  public void unmarshal( final XMLReader reader, final URL context, final UnmarshallResultEater marshalResultEater, final String gmlVersion ) throws TypeRegistryException
  {
    try
    {
      final UnmarshallResultEater eater = new UnmarshallResultEater()
      {
        @Override
        public void unmarshallSuccesful( final Object value ) throws SAXParseException
        {
          final Node node = (Node) value;
          if( node == null )
            marshalResultEater.unmarshallSuccesful( null );
          else
          {
            try
            {
              final Object object = internalUnmarshall( node );
              marshalResultEater.unmarshallSuccesful( object );
            }
            catch( final TypeRegistryException e )
            {
              throw new SAXParseException( e.getLocalizedMessage(), null, e );
            }
          }
        }
      };

      // FIXME: use transformer instead of self-made stuff

      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware( true );

      final DocumentBuilder builder = factory.newDocumentBuilder();
      final Document document = builder.newDocument();
      final DOMConstructor domBuilderContentHandler = new DOMConstructor( document, eater );

      // simulate property-tag for dombuilder
      reader.setContentHandler( domBuilderContentHandler );
    }
    catch( final Exception e )
    {
      throw new TypeRegistryException( e );
    }
  }

  /**
   * Subclasses may extend
   */
  @SuppressWarnings("unused")
  protected Object internalUnmarshall( final Node node ) throws TypeRegistryException
  {
    return node;
  }

  @Override
  public Object cloneObject( final Object objectToClone, final String gmlVersion ) throws CloneNotSupportedException
  {
    throw new CloneNotSupportedException();
  }

  @Override
  @SuppressWarnings("unused")
  public Object parseType( final String text ) throws ParseException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public abstract Class< ? > getValueClass( );

  @Override
  public QName getTypeName( )
  {
    return m_qname;
  }

  @Override
  public boolean isGeometry( )
  {
    return m_isGeometry;
  }
}
