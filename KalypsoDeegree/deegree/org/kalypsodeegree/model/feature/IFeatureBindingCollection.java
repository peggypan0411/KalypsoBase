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
package org.kalypsodeegree.model.feature;

import java.util.List;

import javax.xml.namespace.QName;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Position;

/**
 * Interface to be implemented by classes that wrapped a feature collection to provided a view as a {@link List} of
 * {@link FWCls} <br>
 * TODO: move everything into FeatureList or let feature list implement this interface<br/>
 * TODOwhy 'binding'? rename to IFeatureCollection
 *
 * @author Gernot Belger
 * @author Dirk Kuch
 */
public interface IFeatureBindingCollection<FWCls extends Feature> extends List<FWCls>
{
  /**
   * Creates and Adds a new element of the specified type into the feature collection at the end of the feature
   * collection
   *
   * @param newChildType
   *          the type of the element to add
   * @throws IllegalArgumentException
   *           if some aspect of the specified newChildType prevents it from being added to this list. E.g.
   *           <ul>
   *           <li/>newChildType is null
   *           <li/>the underlying feature collection does not accepts elements of the specified type
   *           <li/>the type is not adaptable to the class {@link FWCls}
   *           </ul>
   */
  FWCls addNew( QName newChildType );

  <T extends FWCls> T addNew( QName newChildType, Class<T> classToAdapt );

  FWCls addNew( QName newChildType, String newFeatureId );

  <T extends FWCls> T addNew( QName newChildType, String newFeatureId, Class<T> classToAdapt );

  /**
   * Creates and Adds a new element of the specified type into the feature collection at the specified position
   *
   * @param index
   *          index at which the specified element is to be inserted.
   * @param newChildType
   *          the type of the element to add
   * @throws UnsupportedOperationException
   *           if the <tt>add</tt> method is not supported by this list.
   * @throws IllegalArgumentException
   *           if some aspect of the specified newChildType prevents it from being added to this list. E.g.
   *           <ul>
   *           <li/>newChildType is null
   *           <li/>the underlaying feature collection does not accepts elements of the specified type
   *           <li/>the type is not adaptable to the class {@link FWCls}
   *           </ul>
   * @throws IndexOutOfBoundsException
   *           if the index is out of range (index &lt; 0 || index &gt; size()).
   */
  <T extends FWCls> T addNew( int index, QName newChildType, Class<T> classToAdapt );

  /**
   * Add this feature as reference to this list
   *
   * @param toAdd
   *          a wrapper wrapping the feature to be added as list
   * @return true if the feature has been added
   * @throws IllegalArgumentException
   *           if the argument toAdd is null
   */
  <T extends FWCls> boolean addRef( T toAdd ) throws IllegalArgumentException;

  void accept( IFeatureBindingCollectionVisitor<FWCls> visitor );

  /**
   * Answer all feature wrappers overlapping the given geometry
   * 
   * @param selectionSurface
   *          The geometry to find elements of this list for
   * @param geometryProperty
   *          qname of geometry property
   * @param containedOnly
   *          control the selection of feature according to whether a feature (limited to a geometry specified by
   *          checkedGeometryPropertyName ) are contained in the geometry or not:
   *          <ul>
   *          <li/>true to select only features that are contains in the given geometry
   *          <li/>false to allow selection of all overlapping feature
   *          </ul>
   * @param checkedGeometryPropertyName
   *          the q-name of the feature property to check
   * @return a list of feature overlapping the given surface
   * @throws {@link IllegalArgumentException} if selectionSurface is null
   */
  List<FWCls> query( GM_Object selectionSurface, QName geometryProperty, boolean containedOnly );

  /**
   * Answer all feature wrappers overlaping the given envelope
   *
   * @param envelope
   *          the envelope specifying the selection area
   * @return a list of feature overlaping the given surface
   * @thorws {@link IllegalArgumentException} if envelope is null
   */
  List<FWCls> query( final GM_Envelope envelope );

  /**
   * Answer all feature wrappers containing the given position
   *
   * @param selectionSurface
   *          the selection surface
   * @return a list of feature overlaping the given surface
   * @throws {@link IllegalArgumentException} if position is null
   */
  List<FWCls> query( final GM_Position position );

  /**
   * Returns the combined bounding box off all contained objects.
   */
  GM_Envelope getBoundingBox( );

  /**
   * Clones the given object as member into this list
   */
  void cloneInto( FWCls toClone ) throws Exception;

  /**
   * Get the parent feature of this list.
   */
  Feature getParentFeature( );

  /**
   * Gets the underlying feature list.
   */
  FeatureList getFeatureList( );

  /**
   * @see FeatureList#addLink(Feature)
   */
  IXLinkedFeature addLink( FWCls toAdd ) throws IllegalArgumentException;

  /**
   * @see FeatureList#addLink(String)
   */
  IXLinkedFeature addLink( String href ) throws IllegalArgumentException;

  /**
   * @see FeatureList#addLink(String, QName)
   */
  IXLinkedFeature addLink( String href, QName featureTypeName ) throws IllegalArgumentException;

  /**
   * @see FeatureList#addLink(String, IFeatureType)
   */
  IXLinkedFeature addLink( String href, IFeatureType featureType ) throws IllegalArgumentException;

  /**
   * @see FeatureList#insertLink(int, Feature)
   */
  IXLinkedFeature insertLink( int index, FWCls toLink ) throws IllegalArgumentException;

  /**
   * @see FeatureList#insertLink(int, String)
   */
  IXLinkedFeature insertLink( int index, String href ) throws IllegalArgumentException;

  /**
   * @see FeatureList#insertLink(int, String, QName)
   */
  IXLinkedFeature insertLink( int index, String href, QName featureTypeName ) throws IllegalArgumentException;

  /**
   * @see FeatureList#insertLink(int, String, IFeatureType)
   */
  IXLinkedFeature insertLink( int index, String href, IFeatureType featureType ) throws IllegalArgumentException;
}