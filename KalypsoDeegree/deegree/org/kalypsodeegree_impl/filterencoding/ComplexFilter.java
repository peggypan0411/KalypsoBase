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
package org.kalypsodeegree_impl.filterencoding;

import java.util.ArrayList;

import org.kalypsodeegree.filterencoding.FilterEvaluationException;
import org.kalypsodeegree.filterencoding.Operation;
import org.kalypsodeegree.model.feature.Feature;

/**
 * Encapsulates the information of a <Filter>element that contains an Operation (only) (as defined in the Filter DTD).
 * Operation is one of the following types:
 * <ul>
 * <li>spatial_ops</li>
 * <li>comparison_ops</li>
 * <li>logical_ops</li>
 * </ul>
 *
 * @author Markus Schneider
 * @version 06.08.2002
 */
public class ComplexFilter extends AbstractFilter
{
  /** Operation the ComplexFilter is based on */
  private Operation m_operation;

  /** Constructs a new ComplexFilter based on the given operation. */
  public ComplexFilter( final Operation operation )
  {
    m_operation = operation;
  }

  /**
   * Constructs a new <tt>ComplexFilter<tt> that consists of an
   * empty <tt>LogicalOperation</tt> of the given type.
   * <p>
   *
   * @param operatorId
   *          OperationDefines.AND, OperationDefines.OR or OperationDefines.NOT
   */
  public ComplexFilter( final int operatorId )
  {
    m_operation = new LogicalOperation( operatorId, new ArrayList<Operation>() );
  }

  /**
   * Constructs a new <tt>ComplexFilter<tt> that consists of a
   * <tt>LogicalOperation</tt> with the given <tt>Filter</tt>.
   * <p>
   *
   * @param filter1
   *          first Filter to be used
   * @param filter2
   *          second Filter to be used null, if operatorId == OperationDefines.NOT
   * @param operatorId
   *          OperationDefines.AND, OperationDefines.OR or OperationDefines.NOT
   */
  public ComplexFilter( final ComplexFilter filter1, final ComplexFilter filter2, final int operatorId )
  {
    // extract the Operations from the Filters
    final ArrayList<Operation> arguments = new ArrayList<>();
    arguments.add( filter1.getOperation() );
    if( filter2 != null )
      arguments.add( filter2.getOperation() );

    m_operation = new LogicalOperation( operatorId, arguments );
  }

  /** Returns the contained Operation. */
  public Operation getOperation( )
  {
    return m_operation;
  }

  /** Sets the Operation. */
  public void setOperation( final Operation operation )
  {
    m_operation = operation;
  }

  /**
   * Calculates the <tt>Filter</tt>'s logical value based on the certain property values of the given feature.
   *
   * @param feature
   *          that determines the values of <tt>PropertyNames</tt> in the expression
   * @return true, if the <tt>Filter</tt> evaluates to true, else false
   * @throws FilterEvaluationException
   *           if the evaluation fails
   */
  @Override
  public boolean evaluate( final Feature feature ) throws FilterEvaluationException
  {
    return m_operation.evaluate( feature );
  }

  /** Produces an indented XML representation of this object. */
  @Override
  public StringBuffer toXML( )
  {
    final StringBuffer sb = new StringBuffer( 1000 );
    sb.append( "<ogc:Filter xmlns:ogc='http://www.opengis.net/ogc'>" );
    sb.append( m_operation.toXML() );
    sb.append( "</ogc:Filter>\n" );
    return sb;
  }
}