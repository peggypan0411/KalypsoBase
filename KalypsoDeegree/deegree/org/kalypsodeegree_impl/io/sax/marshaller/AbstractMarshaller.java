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
package org.kalypsodeegree_impl.io.sax.marshaller;

import org.kalypso.commons.xml.NS;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Abstract class for all GM_Object marshaller classes
 * 
 * @author Felipe Maximino
 */
public abstract class AbstractMarshaller<T extends Object> implements IMashallerConstants
{
  private final XMLReader m_reader;

  private final String m_tag;

  private final String m_qName;

  private T m_marshalledObject;

  public AbstractMarshaller( final XMLReader reader, final String tag )
  {
    this( reader, tag, null );
  }

  public AbstractMarshaller( final XMLReader reader, final String tag, final T object )
  {
    m_reader = reader;
    m_tag = tag;
    m_qName = MarshallerUtils.getQName( m_tag );
    m_marshalledObject = object;
  }

  protected T getMarshalledObject( )
  {
    return m_marshalledObject;
  }

  protected void setMarshalledObject( final T marshalledObject )
  {
    m_marshalledObject = marshalledObject;
  }

  protected XMLReader getXMLReader( )
  {
    return m_reader;
  }

  protected String getTag( )
  {
    return m_tag;
  }

  /**
   * String of form: 'namespace:localPart'
   */
  protected String getQName( )
  {
    return m_qName;
  }

  public void marshall( ) throws SAXException
  {
    startMarshalling();
    doMarshall();
    endMarshalling();
  }

  protected void startMarshalling( ) throws SAXException
  {
    final ContentHandler contentHandler = m_reader.getContentHandler();
    contentHandler.startElement( NS.GML3, m_tag, m_qName, EMPTY_ATTRIBUTES );
  }

  protected abstract void doMarshall( ) throws SAXException;

  protected void endMarshalling( ) throws SAXException
  {
    final ContentHandler contentHandler = m_reader.getContentHandler();
    contentHandler.endElement( NS.GML3, m_tag, m_qName );
  }
}
