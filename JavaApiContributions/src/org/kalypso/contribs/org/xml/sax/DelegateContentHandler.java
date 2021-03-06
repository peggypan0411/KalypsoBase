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
package org.kalypso.contribs.org.xml.sax;

import org.eclipse.core.runtime.Assert;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * A content handler implementation, that delegates all call to another {@link ContentHandler}.<br/>
 * The delegate must not be <code>null</code>, once this content handler is being used.
 * 
 * @author Gernot Belger
 */
public class DelegateContentHandler implements ContentHandler
{
  private ContentHandler m_delegate = null;

  private Locator m_locator;

  public DelegateContentHandler( )
  {
    this( null );
  }

  public DelegateContentHandler( final ContentHandler delegate )
  {
    m_delegate = delegate;
  }

  public ContentHandler getDelegate( )
  {
    return m_delegate;
  }

  public void setDelegate( final ContentHandler delegate )
  {
    m_delegate = delegate;

    if( m_locator != null )
      m_delegate.setDocumentLocator( m_locator );
  }

  /**
   * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
   */
  @Override
  public void setDocumentLocator( final Locator locator )
  {
    m_locator = locator;

    if( m_delegate != null )
      m_delegate.setDocumentLocator( locator );
  }

  public Locator getDocumentLocator( )
  {
    return m_locator;
  }

  /**
   * @see org.xml.sax.ContentHandler#startDocument()
   */
  @Override
  public void startDocument( ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.startDocument();
  }

  /**
   * @see org.xml.sax.ContentHandler#endDocument()
   */
  @Override
  public void endDocument( ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.endDocument();
  }

  /**
   * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
   */
  @Override
  public void startPrefixMapping( final String prefix, final String uri ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.startPrefixMapping( prefix, uri );
  }

  /**
   * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
   */
  @Override
  public void endPrefixMapping( final String prefix ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.endPrefixMapping( prefix );
  }

  /**
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
   *      org.xml.sax.Attributes)
   */
  @Override
  public void startElement( final String uri, final String localName, final String qName, final Attributes atts ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.startElement( uri, localName, qName, atts );
  }

  /**
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement( final String uri, final String localName, final String qName ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.endElement( uri, localName, qName );
  }

  /**
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  @Override
  public void characters( final char[] ch, final int start, final int length ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.characters( ch, start, length );
  }

  /**
   * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
   */
  @Override
  public void ignorableWhitespace( final char[] ch, final int start, final int length ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.ignorableWhitespace( ch, start, length );
  }

  /**
   * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
   */
  @Override
  public void processingInstruction( final String target, final String data ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.processingInstruction( target, data );
  }

  /**
   * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
   */
  @Override
  public void skippedEntity( final String name ) throws SAXException
  {
    Assert.isNotNull( m_delegate );

    m_delegate.skippedEntity( name );
  }

}
