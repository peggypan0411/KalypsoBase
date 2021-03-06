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
import org.kalypsodeegree.graphics.sld.Fill;
import org.kalypsodeegree.graphics.sld.Halo;
import org.kalypsodeegree.graphics.sld.ParameterValueType;
import org.kalypsodeegree.graphics.sld.Stroke;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.xml.Marshallable;

/**
 * Incarnation of a sld:Halo-element. A Halo is a type of Fill that is applied to the backgrounds of font glyphs. The
 * use of halos greatly improves the readability of text labels.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision$ $Date$
 */
public class Halo_Impl implements Halo, Marshallable
{
  private Fill m_fill = null;

  private ParameterValueType m_radius = null;

  private Stroke m_stroke = null;

  /**
   * Create a new <tt>Halo</tt> -instance.
   * <p>
   * 
   * @param radius
   *          radius to be used for the halo
   * @param fill
   *          defines the fill style, use null for default style
   * @param stroke
   *          defines the stroke style, use null for default style
   */
  Halo_Impl( final ParameterValueType radius, final Fill fill, final Stroke stroke )
  {
    setRadius( radius );
    setFill( fill );
    setStroke( stroke );
  }

  /**
   * A Fill allows area geometries to be filled. There are two types of fills: solid-color and repeated GraphicFill. In
   * general, if a Fill element is omitted in its containing element, no fill will be rendered. The default is a solid
   * 50%-gray (color "#808080") opaque fill.
   * <p>
   * 
   * @return the underlying <tt>Fill</tt> -object or null
   */
  @Override
  public Fill getFill( )
  {
    return m_fill;
  }

  /**
   * Sets the underlying <tt>Fill</tt> -instance.
   * <p>
   * 
   * @param fill
   *          defines the fill color and pattern
   */
  @Override
  public void setFill( final Fill fill )
  {
    m_fill = fill;
  }

  /**
   * The Radius element gives the absolute size of a halo radius in pixels encoded as a floating-point number. The
   * radius is taken from the outside edge of a font glyph to extend the area of coverage of the glyph (and the inside
   * edge of holes in the glyphs). The halo of a text label is considered to be a single shape. The default radius is
   * one pixel. Negative values are not allowed.
   * <p>
   * 
   * @return the radius definition as <tt>ParameterValueType</tt>, or null if it has not been specified
   */
  @Override
  public ParameterValueType getRadius( )
  {
    return m_radius;
  }

  /**
   * Sets the value for the radius of the halo.
   * <p>
   * 
   * @param radius
   *          radius to be used for the halo, use null for a rectangle styled halo
   */
  @Override
  public void setRadius( final ParameterValueType radius )
  {
    m_radius = radius;
  }

  /**
   * The Radius element gives the absolute size of a halo radius in pixels encoded as a floating-point number. The
   * radius is taken from the outside edge of a font glyph to extend the area of coverage of the glyph (and the inside
   * edge of holes in the glyphs). The halo of a text label is considered to be a single shape. The default radius is
   * one pixel. Negative values are not allowed.
   * <p>
   * 
   * @param feature
   *          specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the radius value, or -1 if it has not been specified
   * @throws FilterEvaluationException
   *           if the evaluation fails
   */
  @Override
  public double getRadius( final Feature feature ) throws FilterEvaluationException
  {
    if( m_radius == null )
      return 1.0;

    String stringValue = null;
    try
    {
      stringValue = m_radius.evaluate( feature );
      return Double.parseDouble( stringValue );
    }
    catch( final NumberFormatException e )
    {
      throw new FilterEvaluationException( "Given value ('" + stringValue + "') for radius of Halo does not denote a number." );
    }
  }

  /**
   * @see org.kalypsodeegree_impl.graphics.sld.Halo_Impl#getRadius(Feature) <p>
   * @param radius
   *          radius to be set for the halo
   */
  @Override
  public void setRadius( final double radius )
  {
    if( radius > 0 )
    {
      final ParameterValueType pvt = StyleFactory.createParameterValueType( "" + radius );
      m_radius = pvt;
    }
    else
      m_radius = StyleFactory.createParameterValueType( "" + 1.0 );
  }

  /**
   * Returns the underlying <tt>Stroke</tt> -instance.
   * <p>
   * 
   * @return the underlying <tt>Stroke</tt> -object or null
   */
  @Override
  public Stroke getStroke( )
  {
    return m_stroke;
  }

  /**
   * Sets the underlying <tt>Stroke</tt> -instance.
   * <p>
   * 
   * @param stroke
   *          defines the stroke color and pattern
   */
  @Override
  public void setStroke( final Stroke stroke )
  {
    m_stroke = stroke;
  }

  /**
   * exports the content of the Halo as XML formated String
   * 
   * @return xml representation of the Halo
   */
  @Override
  public String exportAsXML( )
  {
    final StringBuffer sb = new StringBuffer( 1000 );
    sb.append( "<Halo>" );
    if( m_radius != null )
    {
      sb.append( "<Radius>" );
      sb.append( ((Marshallable) m_radius).exportAsXML() );
      sb.append( "</Radius>" );
    }
    if( m_fill != null )
    {
      sb.append( ((Marshallable) m_fill).exportAsXML() );
    }
    if( m_stroke != null )
    {
      sb.append( ((Marshallable) m_stroke).exportAsXML() );
    }
    sb.append( "</Halo>" );

    return sb.toString();
  }
}