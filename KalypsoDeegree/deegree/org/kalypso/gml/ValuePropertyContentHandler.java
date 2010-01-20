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
package org.kalypso.gml;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.gmlschema.GMLSchemaLoaderWithLocalCache;
import org.kalypso.gmlschema.IGMLSchema;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.types.IMarshallingTypeHandler;
import org.kalypso.gmlschema.types.TypeRegistryException;
import org.kalypso.gmlschema.types.UnmarshallResultEater;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree_impl.io.sax.parser.DelegatingContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * A {@link org.xml.sax.ContentHandler} implementation which parses GML value-properties.
 * 
 * This content handler delegates the parsing to a proper IMarshallingTypeHandler.
 * <p>
 * NOTE: at this moment, the value-properties that are parsed by a IMarshallingTypeHandler2
 *  are resolved in the {@link org.kalypso.gml.PropertyContentHandler}.
 * 
 * @author Andreas von Doemming
 * @author Felipe Maximino - Refaktoring
 */
public class ValuePropertyContentHandler extends DelegatingContentHandler implements UnmarshallResultEater
{
  private final URL m_context;
  
  private final IFeatureHandler m_featureHandler;
  
  private final IMarshallingTypeHandler m_typeHandler;
  
  private final GMLSchemaLoaderWithLocalCache m_schemaLoader;
  
  private final IPropertyType m_scopeProperty;
  
  public ValuePropertyContentHandler( final XMLReader xmlReader, final IFeatureHandler featureHandler, final IMarshallingTypeHandler typeHandler, final GMLSchemaLoaderWithLocalCache schemaLoader, final IPropertyType scopeProperty, final URL context )
  {
    super( xmlReader, featureHandler );
    
    m_featureHandler = featureHandler;
    m_schemaLoader = schemaLoader;
    m_typeHandler = typeHandler;
    m_context = context;
    m_scopeProperty = scopeProperty;
  }

  /**
   * @see org.kalypsodeegree_impl.io.sax.parser.DelegatingContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement( String uri, String localName, String qName ) throws SAXException
  {
    endDelegation();
    m_parentContentHandler.endElement( uri, localName, qName );
  }

  /**
   * @see org.kalypsodeegree_impl.io.sax.parser.DelegatingContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement( String uri, String localName, String qName, Attributes atts ) throws SAXException
  {
    try{
      final IGMLSchema schema = m_schemaLoader.findSchema( uri );
      final String gmlVersion = schema.getGMLVersion();

      // TODO: we SHOULD provide here the full information to the handler: qname, att, ...
      m_typeHandler.unmarshal( m_xmlReader, m_context, this, gmlVersion );
    }
    catch( final TypeRegistryException e )
    {
      e.printStackTrace();  
      m_xmlReader.getErrorHandler().warning( new SAXParseException( "Failed to unmarshall property value: ", getLocator(), e ) );
    }
    catch( final InvocationTargetException ite )
    {
      final IStatus status = StatusUtilities.createStatus( IStatus.WARNING, "Failed to load schema from catalog: " + uri, null );
      KalypsoDeegreePlugin.getDefault().getLog().log( status );
    }
  }

  /**
   * @see org.kalypso.gmlschema.types.UnmarshallResultEater#unmarshallSuccesful(java.lang.Object)
   */
  @Override
  public void unmarshallSuccesful( Object value ) throws SAXParseException
  { 
    try
    {
      (( UnmarshallResultEater ) m_featureHandler).unmarshallSuccesful( value );
      
      //HACK: we should be able to verify if the scopeProperty tag was correctly ended during binding.
      endElement( m_scopeProperty.getQName().getNamespaceURI(), m_scopeProperty.getQName().getLocalPart(), null );
    }
    catch ( SAXException e ) {
      throw new SAXParseException( String.format( "Unexpected end element: %s", m_scopeProperty.getQName() ), m_locator );
    }   
  }
}