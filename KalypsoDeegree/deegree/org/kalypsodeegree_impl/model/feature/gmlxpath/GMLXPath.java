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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kalypsodeegree.model.feature.Feature;

/**
 * GMLXPath represents a xpath that can be processed via GMLXPathUtilities
 * 
 * @author doemming
 */
public class GMLXPath
{
  /** Separates two segments in the feature-path */
  public final static char SEGMENT_SEPARATOR = '/';

  /** Something between two '/' */
  private final GMLXPathSegment[] m_segments;

  private GMLXPath( final GMLXPathSegment[] segments )
  {
    m_segments = segments;
  }

  /**
   * creates a GMLXPath from a string
   * 
   * @deprecated Use {@link #GMLXPath(String, NamespaceContext)} instead.
   */
  @Deprecated
  public GMLXPath( final String path )
  {
    this( path, null );
  }

  /**
   * creates a GMLXPath from a string
   * 
   * @param namespaceContext
   *          The context against which qname's within the path are resolved. Not to implementors: the xpath does not
   *          store a reference to this context and uses it only within the constructor.
   */
  public GMLXPath( final String path, final NamespaceContext namespaceContext )
  {
    this( GMLXPathSegment.segmentsFromPath( path, namespaceContext ) );
  }

  /**
   * creates a GMLXPath that points to feature
   */
  public GMLXPath( final Feature feature ) throws GMLXPathException
  {
    this( GMLXPathSegment.segmentsFromFeature( feature ) );
  }

  public GMLXPath( final GMLXPath parent, final QName segment )
  {
    this( GMLXPathSegment.addSegments( parent.m_segments, GMLXPathSegment.forQName( segment ) ) );
  }

  public GMLXPath( final QName qname )
  {
    this( GMLXPathSegment.forQName( qname ) );
  }

  public GMLXPath( final GMLXPathSegment segment )
  {
    this( new GMLXPathSegment[] { segment } );
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString( )
  {
    final StringBuilder buffer = new StringBuilder();

    for( int i = 0; i < m_segments.length; i++ )
    {
      buffer.append( m_segments[i].toString() );
      if( i != m_segments.length - 1 )
        buffer.append( SEGMENT_SEPARATOR );
    }
    return buffer.toString();
  }

  public int getSegmentSize( )
  {
    return m_segments.length;
  }

  public GMLXPathSegment getSegment( final int index )
  {
    return m_segments[index];
  }

  public GMLXPath getParentPath( )
  {
    final GMLXPathSegment[] segments = new GMLXPathSegment[m_segments.length - 1];
    System.arraycopy( m_segments, 0, segments, 0, m_segments.length - 1 );
    return new GMLXPath( segments );
  }

  @Override
  public boolean equals( final Object obj )
  {
    return EqualsBuilder.reflectionEquals( this, obj );
  }

  @Override
  public int hashCode( )
  {
    return HashCodeBuilder.reflectionHashCode( this );
  }

  public GMLXPath append( final GMLXPath path )
  {
    GMLXPathSegment[] result = m_segments;

    final int segmentCount = path.getSegmentSize();
    for( int i = 0; i < segmentCount; i++ )
    {
      final GMLXPathSegment segment = path.getSegment( i );
      result = GMLXPathSegment.addSegments( result, segment );
    }

    return new GMLXPath( result );
  }
}