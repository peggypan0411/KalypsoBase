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

import java.util.List;

import org.kalypso.commons.java.lang.Objects;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree_impl.model.feature.gmlxpath.xelement.IXElement;

/**
 * @author doemming
 */
public final class GMLXPathUtilities
{
  private GMLXPathUtilities( )
  {
    throw new UnsupportedOperationException( "Helper class, do not instantiate" );
  }

  /**
   * query xPath for GMLWorkspace
   */
  public static Object query( final GMLXPath xPath, final GMLWorkspace workspace ) throws GMLXPathException
  {
    return getResultForSegment( xPath, workspace, 0, true );
  }

  /**
   * query xPath for Feature
   */
  @SuppressWarnings("unchecked")
  public static <T> T query( final GMLXPath xPath, final Feature feature ) throws GMLXPathException
  {
    return (T) getResultForSegment( xPath, feature, 0, false );
  }

  /**
   * query xPath for Feature
   */
  public static Object query( final GMLXPath xPath, final IFeatureType featureType ) throws GMLXPathException
  {
    return getResultForSegment( xPath, featureType, 0, true );
  }

  /**
   * Same as {@link #query(GMLXPath, Feature)}, but ignores any exception (stack trace is printet though).
   * 
   * @return <code>null</code>, if the path returns <code>null</code> or an exception occurred.
   */
  public static Object queryQuiet( final GMLXPath xPath, final Feature feature )
  {
    try
    {
      return query( xPath, feature );
    }
    catch( final GMLXPathException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  private static Object getResultForSegment( final GMLXPath xPath, final Object context, final int segmentIndex, final boolean isFeatureTypeLevel ) throws GMLXPathException
  {
    if( Objects.isNull( xPath ) )
      return context;

    if( segmentIndex >= xPath.getSegmentSize() )
      return context;
    final GMLXPathSegment segment = xPath.getSegment( segmentIndex );

    final Object newContext = getValueFromSegment( segment, context, isFeatureTypeLevel );

    if( segmentIndex + 1 >= xPath.getSegmentSize() )
      return newContext;

    // operate next level in xpath
    // recursion starts...
    if( newContext instanceof Feature )
      return getResultForSegment( xPath, newContext, segmentIndex + 1, false );

    if( newContext instanceof List< ? > )
    {
      return getResultForSegment( xPath, newContext, segmentIndex + 1, false );

      // TODO: check if still needed;
      // FIXME: this is dubious.... depend on the type of xpath, what do do now. Normally, we should just recurse
// final List< ? > contextList = (List< ? >) newContext;
// final List<Object> resultList = new ArrayList<Object>();
// for( final Object object : contextList )
// {
// if( object instanceof Feature )
// {
// final Object result = getResultForSegment( xPath, object, segmentIndex + 1, !isFeatureTypeLevel );
// if( result != null )
// resultList.add( result );
// }
// }
//
// if( resultList.size() == 1 )
// return resultList.get( 0 );
// return resultList;
    }

    if( newContext instanceof IFeatureType )
      return getResultForSegment( xPath, newContext, segmentIndex + 1, !isFeatureTypeLevel );

    if( newContext instanceof IRelationType )
    {
      final IRelationType rt = (IRelationType) newContext;
      final IFeatureType targetFeatureType = rt.getTargetFeatureType();
      return getResultForSegment( xPath, targetFeatureType, segmentIndex + 1, !isFeatureTypeLevel );
    }

    // everything else is an error, as only feaures and featurelists can have subelements
    // TODO: why? Can't we use xpath to retrieve value's of properties?
    // Proposition: if we have reached the end of the xpath, return all values. If not
    // we have an error, so throw an exception.

    return null;
  }

  /**
   * @return result of xpath expression for one xpath segment
   */
  public static Object getValueFromSegment( final GMLXPathSegment xSegment, final Object context, final boolean isFeatureTypeLevel ) throws GMLXPathException
  {
    final IXElement addressXElement = xSegment.getAddressXElement();

    final Object newContext = addressXElement.evaluate( context, isFeatureTypeLevel );

    final IXElement conditionXElement = xSegment.getConditionXElement();
    if( conditionXElement == null )
      return newContext;

    if( newContext instanceof Feature )
    {
      final Boolean b = (Boolean) conditionXElement.evaluate( newContext, isFeatureTypeLevel );
      if( b )
        return newContext;
      return null;
    }

    throw new GMLXPathException();
  }
}
