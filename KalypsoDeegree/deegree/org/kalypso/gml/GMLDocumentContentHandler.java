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

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import org.kalypso.gmlschema.GMLSchemaLoaderWithLocalCache;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.IGMLSchema;
import org.kalypso.gmlschema.types.AbstractGmlContentHandler;
import org.kalypso.gmlschema.types.IGmlContentHandler;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree_impl.model.feature.FeatureFactory;
import org.kalypsodeegree_impl.model.feature.IFeatureProviderFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * This {@link ContentHandler} implementation parsed a full gml-document.
 * <p>
 * This handler only parses the first line from a gml document and creates the appropriate gml schema from that.
 * <p>
 * All the rest of parsing is delegated to the {@link GMLContentHandler} content handler.
 * </p>
 * 
 * @author Gernot Belger
 * @author Felipe Maximino - Refactoring
 */
public class GMLDocumentContentHandler extends AbstractGmlContentHandler implements IWorkspaceProvider, IFeatureHandler
{
  private final URL m_schemaLocationHint;

  private final URL m_context;

  private final IFeatureProviderFactory m_providerFactory;

  private String m_schemaLocationString;

  /** Schema of root feature */
  private IGMLSchema m_rootSchema;

  private Feature m_rootFeature;

  public GMLDocumentContentHandler( final XMLReader reader, final IGmlContentHandler parentContentHandler, final URL schemaLocationHint, final URL context, final IFeatureProviderFactory providerFactory )
  {
    super( reader, parentContentHandler );

    m_schemaLocationHint = schemaLocationHint;
    m_context = context;
    m_providerFactory = providerFactory;

    m_schemaLocationString = null;
    m_rootSchema = null;
  }

  @Override
  public void startElement( final String uri, final String localName, final String qName, final Attributes atts ) throws SAXException
  {
    final GMLSchemaLoaderWithLocalCache schemaLoader = new GMLSchemaLoaderWithLocalCache();
    loadDocumentSchema( schemaLoader, uri, atts );

    final FeatureContentHandler delegate = new FeatureContentHandler( getXMLReader(), this, schemaLoader, m_context );
    delegate.activate();
    delegate.startElement( uri, localName, qName, atts );
  }

  @Override
  public void endElement( final String uri, final String localName, final String qName ) throws SAXParseException
  {
    try
    {
      m_rootFeature = ((IRootFeatureProvider) getTopLevel()).getRootFeature();

      activateParent();
    }
    catch( final GMLException e )
    {
      e.printStackTrace();
      throwSAXParseException( e, "Failed to retreive root feature" );
    }
  }

  private void loadDocumentSchema( final GMLSchemaLoaderWithLocalCache schemaLoader, final String uri, final Attributes atts ) throws SAXException
  {
    // first element may have schema-location
    m_schemaLocationString = GMLSchemaUtilities.getSchemaLocation( atts );

    m_rootSchema = schemaLoader.loadMainSchema( uri, atts, m_schemaLocationString, m_schemaLocationHint, m_context );

    final Map<String, URL> namespaces = GMLSchemaUtilities.parseSchemaLocation( m_schemaLocationString, m_context );
    /* If a localtionHint is given, this precedes any schemaLocation in the GML-File */
    if( m_schemaLocationHint != null )
      namespaces.put( uri, m_schemaLocationHint );

    schemaLoader.setSchemaLocation( namespaces );
  }

  @Override
  public void handle( final Feature feature )
  {
    m_rootFeature = feature;
  }

  @Override
  public GMLWorkspace getWorkspace( )
  {
    final NamespaceContext nameSpaceContext = getNamespaceContext();
    return FeatureFactory.createGMLWorkspace( m_rootSchema, m_rootFeature, m_context, m_schemaLocationString, m_providerFactory, nameSpaceContext );
  }
}
