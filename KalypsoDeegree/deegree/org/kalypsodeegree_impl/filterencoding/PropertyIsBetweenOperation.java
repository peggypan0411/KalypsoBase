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

import java.util.Date;

import org.kalypsodeegree.filterencoding.Expression;
import org.kalypsodeegree.filterencoding.FilterConstructionException;
import org.kalypsodeegree.filterencoding.FilterEvaluationException;
import org.kalypsodeegree.filterencoding.Operation;
import org.kalypsodeegree.filterencoding.visitor.FilterVisitor;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.xml.ElementList;
import org.kalypsodeegree.xml.XMLTools;
import org.w3c.dom.Element;

/**
 * Encapsulates the information of a <PropertyIsBetween>-element (as defined in Filter DTD).
 * 
 * @author Markus Schneider
 * @version 07.08.2002
 */
public class PropertyIsBetweenOperation extends ComparisonOperation
{

  private PropertyName m_propertyName;

  private Expression m_lowerBoundary;

  private Expression m_upperBoundary;

  public PropertyIsBetweenOperation( final PropertyName propertyName, final Expression lowerBoundary, final Expression upperBoundary )
  {
    super( OperationDefines.PROPERTYISBETWEEN );
    m_propertyName = propertyName;
    m_lowerBoundary = lowerBoundary;
    m_upperBoundary = upperBoundary;
  }

  /**
   * Given a DOM-fragment, a corresponding Operation-object is built. This method recursively calls other buildFromDOM
   * () - methods to validate the structure of the DOM-fragment.
   * 
   * @throws FilterConstructionException
   *           if the structure of the DOM-fragment is invalid
   */
  public static Operation buildFromDOM( final Element element ) throws FilterConstructionException
  {

    // check if root element's name equals 'PropertyIsBetween'
    if( !element.getLocalName().equals( "PropertyIsBetween" ) )
      throw new FilterConstructionException( "Name of element does not equal 'PropertyIsBetween'!" );

    final ElementList children = XMLTools.getChildElements( element );
    if( children.getLength() != 3 )
      throw new FilterConstructionException( "'PropertyIsBetween' requires exactly 3 elements!" );

    final PropertyName propertyName = (PropertyName) PropertyName.buildFromDOM( children.item( 0 ) );
    final Expression lowerBoundary = buildLowerBoundaryFromDOM( children.item( 1 ) );
    final Expression upperBoundary = buildUpperBoundaryFromDOM( children.item( 2 ) );

    return new PropertyIsBetweenOperation( propertyName, lowerBoundary, upperBoundary );
  }

  /**
   * Given a DOM-fragment, a corresponding Expression-object (for the LowerBoundary-element) is built. This method
   * recursively calls other buildFromDOM () - methods to validate the structure of the DOM-fragment.
   * 
   * @throws FilterConstructionException
   *           if the structure of the DOM-fragment is invalid
   */
  private static Expression buildLowerBoundaryFromDOM( final Element element ) throws FilterConstructionException
  {

    // check if root element's name equals 'LowerBoundary'
    if( !element.getLocalName().equals( "LowerBoundary" ) )
      throw new FilterConstructionException( "Name of element does not equal 'LowerBoundary'!" );

    final ElementList children = XMLTools.getChildElements( element );

    if( children.getLength() != 1 )
    {
      if( element.getChildNodes().getLength() == 1 )
        return new BoundaryExpression( XMLTools.getStringValue( element ) );
      throw new FilterConstructionException( "'LowerBoundary' requires exactly 1 element!" );
    }
    return Expression_Impl.buildFromDOM( children.item( 0 ) );
  }

  /**
   * Given a DOM-fragment, a corresponding Expression-object (for the UpperBoundary-element) is built. This method
   * recursively calls other buildFromDOM () - methods to validate the structure of the DOM-fragment.
   * 
   * @throws FilterConstructionException
   *           if the structure of the DOM-fragment is invalid
   */
  private static Expression buildUpperBoundaryFromDOM( final Element element ) throws FilterConstructionException
  {

    // check if root element's name equals 'UpperBoundary'
    if( !element.getLocalName().equals( "UpperBoundary" ) )
      throw new FilterConstructionException( "Name of element does not equal 'UpperBoundary'!" );

    final ElementList children = XMLTools.getChildElements( element );
    if( children.getLength() != 1 )
    {
      if( element.getChildNodes().getLength() == 1 )
        return new BoundaryExpression( XMLTools.getStringValue( element ) );
      throw new FilterConstructionException( "'UpperBoundary' requires exactly 1 element!" );
    }
    return Expression_Impl.buildFromDOM( children.item( 0 ) );
  }

  /**
   * returns the name of the property that shall be compared to the boundaries
   */
  public PropertyName getPropertyName( )
  {
    return m_propertyName;
  }

  public void setPropertyName( final PropertyName propName )
  {
    m_propertyName = propName;
  }

  /**
   * returns the lower boundary of the operation as an <tt>Expression</tt>
   */
  public Expression getLowerBoundary( )
  {
    return m_lowerBoundary;
  }

  public void setLowerBoundary( final Expression lowerBounds )
  {
    m_lowerBoundary = lowerBounds;
  }

  /**
   * returns the upper boundary of the operation as an <tt>Expression</tt>
   */
  public Expression getUpperBoundary( )
  {
    return m_upperBoundary;
  }

  public void setUpperBoundary( final Expression upperBounds )
  {
    m_upperBoundary = upperBounds;
  }

  /** Produces an indented XML representation of this object. */
  @Override
  public StringBuffer toXML( )
  {
    final StringBuffer sb = new StringBuffer( 500 );
    sb.append( "<ogc:" ).append( getOperatorName() ).append( ">" );
    sb.append( m_propertyName.toXML() );
    sb.append( "<ogc:LowerBoundary>" );
    sb.append( m_lowerBoundary.toXML() );
    sb.append( "</ogc:LowerBoundary>" );
    sb.append( "<ogc:UpperBoundary>" );
    sb.append( m_upperBoundary.toXML() );
    sb.append( "</ogc:UpperBoundary>" );
    sb.append( "</ogc:" ).append( getOperatorName() ).append( ">" );
    return sb;
  }

  /**
   * Calculates the <tt>PropertyIsBetween</tt> -Operation's logical value based on the certain property values of the
   * given <tt>Feature</tt>. TODO: Improve datatype handling.
   * 
   * @param feature
   *          that determines the property values
   * @return true, if the <tt>Operation</tt> evaluates to true, else false
   * @throws FilterEvaluationException
   *           if the evaluation fails
   */
  @Override
  public boolean evaluate( final Feature feature ) throws FilterEvaluationException
  {
    final Number lowerValue = getAsNumber( m_lowerBoundary, feature );
    final Number upperValue = getAsNumber( m_upperBoundary, feature );
    final Number thisValue = getAsNumber( m_propertyName, feature );

    final double d1 = lowerValue.doubleValue();
    final double d2 = upperValue.doubleValue();
    final double d3 = thisValue.doubleValue();
    return d1 <= d3 && d3 <= d2;
  }

  private Number getAsNumber( final Expression expression, final Feature feature ) throws FilterEvaluationException
  {
    final Object evaluate = expression.evaluate( feature );
    if( evaluate instanceof Number )
      return (Number) evaluate;

    if( evaluate instanceof Date )
      return new Long( ((Date) evaluate).getTime() );

    final String msg = String.format( "PropertyIsBetweenOperation can only be applied to numerical expressions: %s", evaluate );
    throw new FilterEvaluationException( msg );
  }

  /**
   * @see org.kalypsodeegree.filterencoding.Operation#accept(org.kalypsodeegree.filterencoding.visitor.FilterVisitor,
   *      org.kalypsodeegree.filterencoding.Operation, int)
   */
  @Override
  public void accept( final FilterVisitor fv, final Operation operation, final int depth )
  {
    fv.visit( this );
  }
}