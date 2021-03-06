/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.contribs.org.xml.sax;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * A content handler which appends everything to a given {@link Appendable} (such as a {@link StringBuffer}.
 * 
 * @author Andreas von D�mmning
 */
public class AppendingContentHandler implements ContentHandler
{
  private final Appendable m_appendable;

  public AppendingContentHandler( final Appendable appendable )
  {
    m_appendable = appendable;
  }

  /**
   * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
   */
  @Override
  public void setDocumentLocator( final Locator locator )
  {
    // nothing
  }

  /**
   * @see org.xml.sax.ContentHandler#startDocument()
   */
  @Override
  public void startDocument( )
  {
    // nothing
  }

  /**
   * @see org.xml.sax.ContentHandler#endDocument()
   */
  @Override
  public void endDocument( )
  {
    // nothing
  }

  /**
   * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
   */
  @Override
  public void startPrefixMapping( final String prefix, final String uri )
  {
    // nothing
  }

  /**
   * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
   */
  @Override
  public void endPrefixMapping( final String prefix )
  {
    // nothing
  }

  /**
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
   *      org.xml.sax.Attributes)
   */
  @Override
  public void startElement( final String uri, final String localName, final String qName, final Attributes atts ) throws SAXException
  {
    try
    {
      m_appendable.append( '<' );
      m_appendable.append( qName );

      for( int i = 0; i < atts.getLength(); i++ )
      {
        m_appendable.append( ' ' );
        m_appendable.append( atts.getQName( i ) );
        m_appendable.append( '=' );
        m_appendable.append( atts.getValue( i ) );
      }
      m_appendable.append( ">\n" );
    }
    catch( final IOException e )
    {
      throw new SAXException( e );
    }
  }

  /**
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement( final String uri, final String localName, final String qName ) throws SAXException
  {
    try
    {
      m_appendable.append( "</" );
      m_appendable.append( qName );
      m_appendable.append( ">\n" );
    }
    catch( final IOException e )
    {
      throw new SAXException( e );
    }
  }

  /**
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  @Override
  public void characters( final char[] ch, final int start, final int length ) throws SAXException
  {
    try
    {
      m_appendable.append( String.copyValueOf( ch, start, length ) );
    }
    catch( final IOException e )
    {
      throw new SAXException( e );
    }
  }

  /**
   * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
   */
  @Override
  public void ignorableWhitespace( final char[] ch, final int start, final int length ) throws SAXException
  {
    try
    {
      m_appendable.append( String.copyValueOf( ch, start, length ) );
    }
    catch( final IOException e )
    {
      throw new SAXException( e );
    }
  }

  /**
   * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
   */
  @Override
  public void processingInstruction( final String target, final String data ) throws SAXException
  {
    try
    {
      m_appendable.append( "processingInstruction: " );
      m_appendable.append( target );
      m_appendable.append( " / " );
      m_appendable.append( data );
      m_appendable.append( '\n' );
    }
    catch( final IOException e )
    {
      throw new SAXException( e );
    }
  }

  /**
   * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
   */
  @Override
  public void skippedEntity( final String name ) throws SAXException
  {
    try
    {
      m_appendable.append( "skippedEntity: " );
      m_appendable.append( name );
      m_appendable.append( '\n' );
    }
    catch( final IOException e )
    {
      throw new SAXException( e );
    }

  }

  public Appendable getAppendable( )
  {
    return m_appendable;
  }

}
