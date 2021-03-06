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
package org.kalypsodeegree.tools;

import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

import org.kalypsodeegree.filterencoding.Filter;
import org.kalypsodeegree.filterencoding.Operation;
import org.kalypsodeegree_impl.filterencoding.ComplexFilter;
import org.kalypsodeegree_impl.filterencoding.FeatureFilter;
import org.kalypsodeegree_impl.filterencoding.FeatureId;
import org.kalypsodeegree_impl.filterencoding.LogicalOperation;
import org.kalypsodeegree_impl.filterencoding.OperationDefines;

/**
 * @author kuepfer
 */
public class FilterUtilites
{

  private FilterUtilites( )
  {
    // not intended to be instanciated
  }

  /**
   * Merges filter1 with filter2 to a new filter. ComplexFilters are merged with and AND operation. FeatureFilters are
   * merged by returning the two featureId list in one list. ElseFilters can not be merged.
   *
   * @param filter1
   *          filter to be merged with filter2
   * @param filter2
   *          filter to be merged with filter1
   * @return if filter1 != null and filter2 == null, filter2 is returned and vis versa. If both filters to be merged are
   *         null, then null is returned.
   * @throws exception
   *           throws a OperationNotSupportedException if the filters could not be merched.
   */
  public static Filter mergeFilters( final Filter filter1, final Filter filter2 ) throws OperationNotSupportedException
  {
    if( filter1 != null && filter2 == null )
      return filter1;
    else if( filter1 == null && filter2 != null )
      return filter2;
    else if( filter1 == null && filter2 == null )
      return null;
    if( filter1 instanceof ComplexFilter && filter2 instanceof ComplexFilter )
      return mergeComplexFilters( (ComplexFilter) filter1, (ComplexFilter) filter2 );
    else if( filter1 instanceof FeatureFilter && filter2 instanceof FeatureFilter )
      return mergeFeatureFilters( (FeatureFilter) filter1, (FeatureFilter) filter2 );
    else
      throw new OperationNotSupportedException( "filters are not of the same type and can not be merged!" );
  }

  private static Filter mergeFeatureFilters( final FeatureFilter filter1, final FeatureFilter filter2 )
  {
    final ArrayList<FeatureId> featureIds1 = filter1.getFeatureIds();
    final ArrayList<FeatureId> featureIds2 = filter2.getFeatureIds();
    final FeatureFilter mergedFeatureIds = new FeatureFilter();
    for( final FeatureId id : featureIds1 )
      mergedFeatureIds.addFeatureId( id );
    for( final FeatureId id : featureIds2 )
      mergedFeatureIds.addFeatureId( id );
    return mergedFeatureIds;
  }

  private static Filter mergeComplexFilters( final ComplexFilter filter1, final ComplexFilter filter2 )
  {
    final Operation op1 = filter1.getOperation();
    final Operation op2 = filter2.getOperation();
    if( op1 != null && op2 == null )
      return filter1;
    else if( op1 == null && op2 != null )
      return filter2;
    final ArrayList<Operation> andOps = new ArrayList<>();
    andOps.add( op1 );
    andOps.add( op2 );
    return new ComplexFilter( new LogicalOperation( OperationDefines.AND, andOps ) );

  }

  /**
   * Remove the one filter from an existing filter. If the filter to be removed is not contained in the original filter,
   * the original filter is returned.
   *
   * @param originalFilter
   *          the actual filter.
   * @param filterToRemove
   *          filter to be removed from the original filter.
   * @throws exception
   *           throws an FilterConstructionException if the operation was not successful.
   */
  public Filter removeFilter( final Filter originalFilter, final Filter filterToRemove ) throws OperationNotSupportedException
  {
    // TODO remove operation: visit all operations and check for equality. If they are equal remove filter element.
    originalFilter.getClass();
    filterToRemove.getClass();
    throw new OperationNotSupportedException( "The removal of a filter element is not implemented." );
  }

}
