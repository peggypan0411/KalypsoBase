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
package org.kalypsodeegree_impl.graphics.sld;

import org.kalypsodeegree.filterencoding.FilterEvaluationException;
import org.kalypsodeegree.graphics.sld.CssParameter;
import org.kalypsodeegree.graphics.sld.ParameterValueType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.xml.Marshallable;
import org.kalypsodeegree_impl.tools.Debug;

/**
 * The simple SVG/CSS2 styling parameters are given with the CssParameter element, which is defined as follows:
 * 
 * <pre>
 *  <xs:element name="CssParameter" type="sld:ParameterValueType"/>
 *     <xs:complexType name="ParameterValueType" mixed="true">
 *        <xs:choice minOccurs="0" maxOccurs="unbounded">
 *             <xs:element ref="wfs:expression"/>
 *         </xs:choice>
 *  </xs:complexType>
 * </pre>
 * 
 * The parameter values are allowed to be complex expressions for maximum flexibility. The mixed="true" definition means
 * that regular text may be mixed in with various sub-expressions, implying a text-substitution model for parameter
 * values. Numeric and character-string data types are not distinguished, which may cause some complications.
 * <p>
 * </p>
 * Here are some usage examples:
 * 
 * <pre>
 * 1. <CssParameter name="stroke-width">3</CssParameter>
 * 2. <CssParameter name="stroke-width">
 *         <wfs:Literal>3</wfs:Literal>
 *    </CssParameter>
 * 3. <CssParameter name="stroke-width">
 *         <wfs:Add>
 *             <wfs:PropertyName>/A</wfs:PropertyName>
 *             <wfs:Literal>2</wfs:Literal>
 *         </wfs:Add>
 *    </CssParameter>
 * 4. <Label>This is city "<wfs:PropertyName>/NAME</wfs:PropertyName>"
 * of state <wfs:PropertyName>/STATE</wfs:PropertyName></Label>
 * </pre>
 * 
 * The allowed SVG/CSS styling parameters for a stroke are: stroke (color), stroke-opacity, stroke-width,
 * stroke-linejoin, stroke-linecap, stroke-dasharray, and stroke-dashoffset. The chosen parameter is given by the name
 * attribute of the CssParameter element.
 * <p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision$ $Date$
 */
class CssParameter_Impl implements CssParameter, Marshallable
{
  private ParameterValueType m_pvt = null;

  private String m_name = null;

  /**
   * constructor initializing the class with the <CssParameter>
   */
  CssParameter_Impl( final String name, final ParameterValueType pvt )
  {
    m_name = name;
    m_pvt = pvt;
  }

  /**
   * Returns the name attribute's value of the CssParameter.
   * <p>
   * 
   * @return the value of the name attribute of the CssParameter
   */
  @Override
  public String getName( )
  {
    return m_name;
  }

  /**
   * Sets the name attribute's value of the CssParameter.
   * <p>
   * 
   * @param name
   *          the value of the name attribute of the CssParameter
   */
  @Override
  public void setName( final String name )
  {
    m_name = name;
  }

  /**
   * Returns the value of the CssParameter as an <tt>ParameterValueType</tt>.
   * <p>
   * 
   * @return the mixed content of the element
   */
  @Override
  public ParameterValueType getValue( )
  {
    return m_pvt;
  }

  /**
   * Sets the value of the CssParameter as an <tt>ParameterValueType</tt>.
   * <p>
   * 
   * @param value
   *          the mixed content of the element
   */
  @Override
  public void setValue( final ParameterValueType value )
  {
    m_pvt = value;
  }

  /**
   * Returns the (evaluated) value of the CssParameter as a simple <tt>String</tt>.
   * <p>
   * 
   * @param feature
   *          specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) <tt>String</tt> value of the parameter
   * @throws FilterEvaluationException
   *           if the evaluations fails
   */
  @Override
  public String getValue( final Feature feature ) throws FilterEvaluationException
  {
    return m_pvt.evaluate( feature );
  }

  /**
   * Sets the value of the CssParameter as a simple <tt>String</tt>.
   * <p>
   * 
   * @param value
   *          CssParameter-Value to be set
   */
  @Override
  public void setValue( final String value )
  {
    ParameterValueType pvt = null;
    pvt = StyleFactory.createParameterValueType( "" + value );
    m_pvt = pvt;
  }

  /**
   * exports the content of the CssParameter as XML formated String
   * 
   * @return xml representation of the CssParameter
   */
  @Override
  public String exportAsXML( )
  {
    Debug.debugMethodBegin();

    final StringBuffer sb = new StringBuffer( "<CssParameter name=" );
    sb.append( "'" + m_name + "'>" );
    sb.append( ((Marshallable) m_pvt).exportAsXML() );
    sb.append( "</CssParameter>" );

    Debug.debugMethodEnd();
    return sb.toString();
  }
}