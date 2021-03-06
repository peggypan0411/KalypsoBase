/** This file is part of kalypso/deegree.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * history:
 * 
 * Files in this package are originally taken from deegree and modified here
 * to fit in kalypso. As goals of kalypso differ from that one in deegree
 * interface-compatibility to deegree is wanted but not retained always. 
 * 
 * If you intend to use this software in other ways than in kalypso 
 * (e.g. OGC-web services), you should consider the latest version of deegree,
 * see http://www.deegree.org .
 *
 * all modifications are licensed as deegree, 
 * original copyright:
 *
 * Copyright (C) 2001 by:
 * EXSE, Department of Geography, University of Bonn
 * http://www.giub.uni-bonn.de/exse/
 * lat/lon GmbH
 * http://www.lat-lon.de
 */
package org.kalypsodeegree_impl.model.feature.gmlxpath;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kalypso.commons.xml.NSUtilities;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree_impl.model.feature.gmlxpath.xelement.IXElement;
import org.kalypsodeegree_impl.model.feature.gmlxpath.xelement.XElementFormPath;

/**
 * Each GMLXPathSegment represents the parts of the gmlxPath between two '/' <br>
 * 
 * @author doemming
 */
public final class GMLXPathSegment
{
  public static GMLXPathSegment[] addSegments( final GMLXPathSegment[] segments, final GMLXPathSegment segment )
  {
    final int parentLength = segments.length;
    final GMLXPathSegment[] newSegments = new GMLXPathSegment[parentLength + 1];
    System.arraycopy( segments, 0, newSegments, 0, parentLength );
    newSegments[parentLength] = segment;
    return newSegments;
  }

  public static GMLXPathSegment[] segmentsFromFeature( final Feature feature ) throws GMLXPathException
  {
    final String id = feature.getId();
    if( id == null || id.length() < 1 )
      throw new GMLXPathException( "canot build gmlxpath for feature with invalid id" );
    else
      return new GMLXPathSegment[] { new GMLXPathSegment( feature ) };
  }

  public static GMLXPathSegment[] segmentsFromPath( final String path, final NamespaceContext namespaceContext )
  {
    final String trimmedPath = path.trim();

    if( trimmedPath.length() == 0 )
      return new GMLXPathSegment[] {};

    // FIXME: we need to be able to escape '/' somehow in order to read fully quantified qnames (http://xxx.org)
    final String[] segments = trimmedPath.split( "/" );
    final GMLXPathSegment[] xSegments = new GMLXPathSegment[segments.length];
    for( int i = 0; i < segments.length; i++ )
      xSegments[i] = new GMLXPathSegment( segments[i], namespaceContext );

    return xSegments;
  }

  private static final XElementFactory m_fac = new XElementFactory();

  private final transient IXElement m_addressXElement;

  private final transient IXElement m_conditionXElement;

  private final String m_segment;

  public GMLXPathSegment( final Feature feature )
  {
    this( "id( '" + feature.getId() + "' )", null );
  }

  public GMLXPathSegment( final String segmentString, final NamespaceContext namespaceContext )
  {
    m_segment = segmentString;

    final int index = segmentString.indexOf( "[" );
    final String address;
    final String condition;
    if( index < 0 )
    {
      address = segmentString;
      condition = null;
    }
    else
    {
      address = segmentString.substring( 0, index );
      condition = segmentString.substring( index );
    }

    m_addressXElement = m_fac.create( address, namespaceContext );

    if( condition != null )
      m_conditionXElement = m_fac.create( condition, namespaceContext );
    else
      m_conditionXElement = null;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString( )
  {
    return m_segment;
  }

  public IXElement getAddressXElement( )
  {
    return m_addressXElement;
  }

  public IXElement getConditionXElement( )
  {
    return m_conditionXElement;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( final Object obj )
  {
    return EqualsBuilder.reflectionEquals( this, obj );
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode( )
  {
    return HashCodeBuilder.reflectionHashCode( this );
  }

  public static GMLXPathSegment forQName( final QName qname )
  {
    final String namespaceURI = qname.getNamespaceURI();
    final String localPart = qname.getLocalPart();

    if( XMLConstants.NULL_NS_URI.equals( namespaceURI ) )
      return new GMLXPathSegment( localPart, null );

    final String defaultPrefix = "ns" + System.currentTimeMillis();
    // Hopefully this works allways...
    final String prefix = NSUtilities.getNSProvider().getPreferredPrefix( namespaceURI, defaultPrefix );

    // Create a constant context
    final NamespaceContext namespaceContext = new NamespaceContext()
    {
      @Override
      public String getNamespaceURI( final String pref )
      {
        return namespaceURI;
      }

      @Override
      public String getPrefix( final String namespace )
      {
        return prefix;
      }

      @Override
      public Iterator<String> getPrefixes( final String namespace )
      {
        throw new UnsupportedOperationException();
      }
    };

    final String segment = String.format( "%s:%s", prefix, localPart );
    return new GMLXPathSegment( segment, namespaceContext );
  }

  public QName getQName( )
  {
    if( m_addressXElement instanceof XElementFormPath )
      return ((XElementFormPath) m_addressXElement).getQName();

    return null;
  }
}