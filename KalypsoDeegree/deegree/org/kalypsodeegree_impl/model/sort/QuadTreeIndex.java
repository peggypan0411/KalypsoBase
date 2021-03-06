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
package org.kalypsodeegree_impl.model.sort;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree_impl.model.geometry.JTSAdapter;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.ItemVisitor;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.quadtree.Quadtree;

/**
 * // TODO: refaktor QuadTreeIndex in order to not use the envelope-provider. This is necessary, because everything is
 * synchronized now.... (don't call foreign code within synchronized code)
 *
 * @author Gernot Belger
 */
public class QuadTreeIndex implements SpatialIndexExt
{
  private SpatialIndex m_index;

  private final List<Object> m_items = new ArrayList<>();

  private final IEnvelopeProvider m_envelopeProvider;

  private Envelope m_boundingBox = null;

  public QuadTreeIndex( final IEnvelopeProvider envelopeProvider )
  {
    m_envelopeProvider = envelopeProvider;
    m_index = null;
  }

  public void clear( )
  {
    m_items.clear();
    m_index = null;
    m_boundingBox = null;
  }

  private Envelope getEnvelope( final Object object )
  {
    final GM_Envelope envelope = m_envelopeProvider.getEnvelope( object );
    return JTSAdapter.export( envelope );
  }

  @Override
  public Envelope getBoundingBox( )
  {
    if( m_boundingBox == null )
      m_boundingBox = recalcEnvelope();

    return m_boundingBox;
  }

  private Envelope recalcEnvelope( )
  {
    if( m_items.isEmpty() )
      return new Envelope();

    Envelope bbox = null;
    for( final Object item : m_items )
    {
      final Envelope envelope = getEnvelope( item );
      if( envelope.isNull() )
        continue;

      if( bbox == null )
        bbox = envelope;
      else
        bbox.expandToInclude( envelope );
    }

    if( bbox == null )
      return new Envelope();

    return bbox;
  }

  public void invalidate( )
  {
    m_boundingBox = null;
    m_index = null;
  }

  public void invalidate( final Object item )
  {
    revalidate();

    final Envelope itemEnv = getEnvelope( item );
    // TODO: this is not good, because the env this object was added may differ from the new envelope
    // however, this may lead to the case, that the object will not be removed
    m_index.remove( itemEnv, item );
    if( !itemEnv.isNull() )
      m_index.insert( itemEnv, item );

    /* Bounding box must be calculated anew */
    m_boundingBox = null;
  }

  private void revalidate( )
  {
    if( m_index != null )
      return;

    m_index = createIndex();

    for( final Object item : m_items )
    {
      final Envelope itemEnv = getEnvelope( item );
      if( itemEnv.isNull() )
      {
// System.out.println( "Null envelope for: " + item );
      }
      else
        m_index.insert( itemEnv, item );
    }
  }

  private SpatialIndex createIndex( )
  {
    return new Quadtree();
// return new STRtree();
  }

  @Override
  public void paint( final Graphics g, final GeoTransform geoTransform )
  {
    // sorry, don't know how to paint a quadtree
  }

  @Override
  public int size( )
  {
    return m_items.size();
  }

  @Override
  public void insert( final Envelope itemEnv, final Object item )
  {
    m_items.add( item );

    if( m_boundingBox != null && !itemEnv.isNull() )
      m_boundingBox.expandToInclude( itemEnv );

    if( m_index == null )
      revalidate();
    else if( !itemEnv.isNull() )
      m_index.insert( itemEnv, item );

    // TODO:check depth of quadtree, if too big, maybe we have to revalidate?
  }

  @Override
  public List< ? > query( final Envelope searchEnv )
  {
    revalidate();

    return m_index.query( searchEnv );
  }

  @Override
  public void query( final Envelope searchEnv, final ItemVisitor visitor )
  {
    revalidate();

    m_index.query( searchEnv, visitor );
  }

  @Override
  public boolean remove( final Envelope itemEnv, final Object item )
  {
    m_index.remove( itemEnv, item );
    m_boundingBox = null;

    return m_items.remove( item );
  }

  /**
   * NOT IMPLEMENTED
   *
   * @see org.kalypsodeegree_impl.model.sort.SpatialIndexExt#contains(com.vividsolutions.jts.geom.Envelope,
   *      java.lang.Object)
   */
  @Override
  public boolean contains( final Envelope itemEnv, final Object item )
  {
    throw new UnsupportedOperationException();
  }
}