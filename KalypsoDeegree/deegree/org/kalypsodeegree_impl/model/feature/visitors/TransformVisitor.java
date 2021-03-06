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
package org.kalypsodeegree_impl.model.feature.visitors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.transformation.transformer.GeoTransformerFactory;
import org.kalypso.transformation.transformer.IGeoTransformer;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;
import org.kalypsodeegree.model.feature.IXLinkedFeature;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree_impl.model.feature.Feature_Impl;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

/**
 * Transforms all visited features to another coordinate system
 *
 * @author belger
 */
public class TransformVisitor implements FeatureVisitor
{
  private IGeoTransformer m_transformer;

  /** feature -> exception */
  private final Map<Feature, Throwable> m_exceptions = new HashMap<>();

  public TransformVisitor( final String targetCRS )
  {
    try
    {
      m_transformer = GeoTransformerFactory.getGeoTransformer( targetCRS );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Returns thrown exceptions while visiting
   */
  public Map<Feature, Throwable> getExceptions( )
  {
    return m_exceptions;
  }

  /**
   * @see org.kalypsodeegree.model.feature.FeatureVisitor#visit(org.kalypsodeegree.model.feature.Feature)
   */
  @Override
  public boolean visit( final Feature f )
  {
    try
    {
      // TRICKY: directly fetch an xlinked featuee, else we might get an error as the feature types of the link
      // and the linked feature may be different.
      // We should check, if we want to transform through xlinks anyways...
      if( f instanceof IXLinkedFeature )
        return false;

      doVisit( f );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      m_exceptions.put( f, e );
    }

    return true;
  }

  private void doVisit( final Feature f ) throws Exception
  {
    final IFeatureType featureType = f.getFeatureType();

    boolean wasTransformed = false;

    final IPropertyType[] ftps = featureType.getProperties();
    for( final IPropertyType ftp : ftps )
      wasTransformed |= transformProperty( f, ftp );

    if( wasTransformed )
      f.setEnvelopesUpdated();
  }

  private boolean transformProperty( final Feature f, final IPropertyType ftp ) throws Exception
  {
    if( ftp.isVirtual() )
      return false;

    if( f instanceof Feature_Impl && ((Feature_Impl) f).isFunctionProperty( ftp ) )
      return false;

    if( !GeometryUtilities.isGeometry( ftp ) )
      return false;

    if( ftp.isList() )
      return transformList( f, ftp );

    return transformNonList( f, ftp );
  }

  private boolean transformNonList( final Feature f, final IPropertyType ftp ) throws Exception
  {
    final GM_Object object = (GM_Object) f.getProperty( ftp );
    final GM_Object transformedGeom = transformProperty( object );
    if( object != transformedGeom )
      f.setProperty( ftp, transformedGeom );

    return object != transformedGeom;
  }

  private boolean transformList( final Feature f, final IPropertyType ftp ) throws Exception
  {
    boolean wasTransformed = false;
    final List<GM_Object> geomList = (List<GM_Object>) f.getProperty( ftp );
    final int size = geomList.size();
    for( int i = 0; i < size; i++ )
    {
      final GM_Object geom = geomList.get( i );
      final GM_Object transformedGeom = transformProperty( geom );

      wasTransformed = wasTransformed | geom != transformedGeom;

      geomList.set( i, transformedGeom );
    }

    return wasTransformed;
  }

// private void invalidateFeatureList( final Feature f )
// {
// // HACK: we invalidate the complete geo-index, in order to make sure the complete bbox of the list is
// // correctly set.
// final Feature parent = f.getOwner();
// if( parent == null )
// return;
//
// final Object parentList = parent.getProperty( f.getParentRelation() );
// if( parentList instanceof FeatureList )
// ((FeatureList) parentList).invalidate();
// }

  private GM_Object transformProperty( final GM_Object object ) throws Exception
  {
    if( object == null )
      return null;

    return m_transformer.transform( object );
  }

}