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
package org.kalypso.shape.deegree;

import java.nio.charset.Charset;
import java.util.Iterator;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.shape.IShapeData;
import org.kalypso.shape.ShapeDataException;
import org.kalypso.shape.ShapeType;
import org.kalypso.shape.dbf.IDBFValue;
import org.kalypso.shape.geometry.ISHPGeometry;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Triangle;
import org.kalypsodeegree.model.geometry.GM_TriangulatedSurface;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;

/**
 * data provider for exporting GM_TriangulatedSurface patches from 1d2d result. It converts the existing
 * TriangulatedSurface feature into several patch features. <br>
 * This is because of the very slow handling of MultiShapesZ in ArcView.<br>
 * If you want to export the TriangulatedSurface feature as it is in the gml, use
 * {@link TriangulatedSurfaceMultiPartShapeDataProvider} instead.
 * 
 * @author Thomas Jung
 */
public class TriangulatedSurfaceSinglePartShapeDataProvider implements IShapeData
{
  private final GM_Object2Shape m_gmObject2Shape;

  private final Feature[] m_features;

  private final GMLXPath m_geometry;

  private IDBFValue[] m_fields = null;

  private int m_total = -1;

  private final Charset m_charset;

  public TriangulatedSurfaceSinglePartShapeDataProvider( final Feature[] features, final GMLXPath geometry, final Charset charset, final GM_Object2Shape gmObject2Shape )
  {
    m_features = features;
    m_geometry = geometry;
    m_charset = charset;
    m_gmObject2Shape = gmObject2Shape;
  }

  @Override
  public Charset getCharset( )
  {
    return m_charset;
  }

  @Override
  public String getCoordinateSystem( )
  {
    return m_gmObject2Shape.getCoordinateSystem();
  }

  @Override
  public IDBFValue[] getFields( )
  {
    if( m_fields == null )
    {
      final IFeatureType featureType = GenericShapeDataFactory.findLeastCommonType( m_features );
      final IDBFValue[] fields = GenericShapeDataFactory.findFields( featureType );
      m_fields = new IDBFValue[fields.length];
      for( int i = 0; i < fields.length; i++ )
        m_fields[i] = new TinValue( fields[i] );
    }

    return m_fields;
  }

  @Override
  public ShapeType getShapeType( )
  {
    return m_gmObject2Shape.getShapeType();
  }

  @Override
  public int size( ) throws ShapeDataException
  {
    if( m_total == -1 )
    {
      m_total = 0;
      for( final Feature feature : m_features )
      {
        final GM_TriangulatedSurface tin = TinIterator.getTin( m_geometry, feature );
        if( tin != null )
          m_total += tin.size();
      }
    }

    return m_total;
  }

  @Override
  public Iterator< ? > iterator( )
  {
    return new TinIterator( m_geometry, m_features );
  }

  @Override
  public ISHPGeometry getGeometry( final Object element ) throws ShapeDataException
  {
    final TinPointer pointer = (TinPointer) element;
    final GM_TriangulatedSurface tin = pointer.getTin();
    final int triangleIndex = pointer.getTriangleIndex();
    final GM_Triangle triangle = tin.get( triangleIndex );
    return m_gmObject2Shape.convert( triangle );
  }
}
