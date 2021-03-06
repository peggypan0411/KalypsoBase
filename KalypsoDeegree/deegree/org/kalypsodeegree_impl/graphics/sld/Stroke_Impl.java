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

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.kalypsodeegree.filterencoding.Expression;
import org.kalypsodeegree.filterencoding.FilterEvaluationException;
import org.kalypsodeegree.graphics.sld.CssParameter;
import org.kalypsodeegree.graphics.sld.GraphicFill;
import org.kalypsodeegree.graphics.sld.GraphicStroke;
import org.kalypsodeegree.graphics.sld.Stroke;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.xml.Marshallable;

/**
 * A Stroke allows a string of line segments (or any linear geometry) to be rendered. There are three basic types of
 * strokes: solid Color, GraphicFill (stipple), and repeated GraphicStroke. A repeated graphic is plotted linearly and
 * has its graphic symbol bended around the curves of the line string. The default is a solid black line (Color
 * "#000000").
 * <p>
 * The supported CSS-Parameter names are:
 * <ul>
 * <li>stroke (color)
 * <li>stroke-opacity
 * <li>stroke-width
 * <li>stroke-linejoin
 * <li>stroke-linecap
 * <li>stroke-dasharray
 * <li>stroke-dashoffset
 * <li>stroke-arrow-type (line | segment)
 * <li>stroke-arrow-widget (open (default) | fill )
 * <li>stroke-arrow-alignment (start | middle | end)
 * <li>stroke-arrow-size
 * <p>
 * <h1>stroke-arrow-type</h1> An Arrow will be placed on a line or its line segments
 * <h1>stroke-arrow-alignment</h1> where on a line or its line segments the arrow will be placed: start, middle, end
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision$ $Date$
 */
public class Stroke_Impl extends Drawing_Impl implements org.kalypsodeegree.graphics.sld.Stroke, Marshallable
{
  private GraphicStroke m_graphicStroke = null;

  private Color color = null;

  private double smplOpacity = -1;

  private double smplWidth = -1;

  private int smplLineJoin = -1;

  private int smplLineCap = -1;

  private float[] smplDashArray = null;

  private float smplDashOffset = -1;

  /**
   * Constructs a new <tt>Stroke_Impl<tt>.
   */
  protected Stroke_Impl( )
  {
    super( new HashMap<String, CssParameter>(), null );
  }

  /**
   * Constructs a new <tt>Stroke_Impl<tt>.
   * <p>
   *
   * @param cssParams
   *          keys are <tt>Strings<tt> (see above), values are <tt>CssParameters</tt>
   */
  public Stroke_Impl( final Map<String, CssParameter> cssParams, final GraphicStroke graphicStroke, final GraphicFill graphicFill )
  {
    super( cssParams, graphicFill );

    m_graphicStroke = graphicStroke;

    try
    {
      extractSimpleColor();
      extractSimpleOpacity();
      extractSimpleWidth();
      extractSimpleLineJoin();
      extractSimpleLineCap();
      extractSimpleDasharray();
      extractSimpleDashOffset();
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * extracts the color of the stroke if it is simple (nor Expression) to avoid new calculation for each call of
   * getStroke(Feature feature)
   */
  private void extractSimpleColor( ) throws FilterEvaluationException
  {
    final CssParameter cssParam = getParameter( CSS_STROKE );
    if( cssParam != null )
    {
      final Object[] o = cssParam.getValue().getComponents();
      for( final Object element : o )
      {
        if( element instanceof Expression )
        {
          color = null;
          break;
        }
        try
        {
          color = Color.decode( ((String) element).trim() );
        }
        catch( final NumberFormatException e )
        {
          throw new FilterEvaluationException( "Given value ('" + element + "') for CSS-Parameter 'stroke' " + "does not denote a valid color!" );
        }
      }
    }
  }

  /**
   * FIXME: does not belong here<br/>
   * returns true if the passed CssParameter contain a simple value
   */
  private boolean isSimple( final CssParameter cssParam )
  {
    final Object[] o = cssParam.getValue().getComponents();
    for( final Object element : o )
    {
      if( element instanceof Expression )
        return false;
    }

    return true;
  }

  /**
   * extracts the opacity of the stroke if it is simple (no Expression) to avoid new calculation for each call of
   * getStroke(Feature feature)
   */
  private void extractSimpleOpacity( ) throws FilterEvaluationException
  {
    final CssParameter cssParam = getParameter( CSS_OPACITY );
    if( cssParam != null )
      if( isSimple( cssParam ) )
      {
        final Object[] o = cssParam.getValue().getComponents();
        try
        {
          smplOpacity = Double.parseDouble( (String) o[0] );
        }
        catch( final NumberFormatException e )
        {
          throw new FilterEvaluationException( "Given value for parameter 'stroke-opacity' ('" + o[0] + "') has invalid format!" );
        }

        if( smplOpacity < 0.0 || smplOpacity > 1.0 )
          throw new FilterEvaluationException( "Value for parameter 'stroke-opacity' (given: '" + o[0] + "') must be between 0.0 and 1.0!" );
      }
  }

  /**
   * extracts the width of the stroke if it is simple (no Expression) to avoid new calculation for each call of
   * getStroke(Feature feature)
   */
  private void extractSimpleWidth( ) throws FilterEvaluationException
  {
    final CssParameter cssParam = getParameter( CSS_WIDTH );
    if( cssParam != null )
      if( isSimple( cssParam ) )
      {
        final Object[] o = cssParam.getValue().getComponents();
        try
        {
          smplWidth = Double.parseDouble( (String) o[0] );
        }
        catch( final NumberFormatException e )
        {
          throw new FilterEvaluationException( "Given value for parameter 'stroke-width' ('" + o[0] + "') has invalid format!" );
        }
        if( smplWidth <= 0.0 )
          throw new FilterEvaluationException( "Value for parameter 'stroke-width' (given: '" + smplWidth + "') must be > 0.0!" );
      }
  }

  /**
   * extracts the line join of the stroke if it is simple (no Expression) to avoid new calculation for each call of
   * getStroke(Feature feature)
   */
  private void extractSimpleLineJoin( ) throws FilterEvaluationException
  {
    final CssParameter cssParam = getParameter( CSS_LINEJOIN );
    if( cssParam != null )
      if( isSimple( cssParam ) )
      {
        final Object[] o = cssParam.getValue().getComponents();
        final String value = ((String) o[0]).trim();
        if( value.equals( "mitre" ) )
          smplLineJoin = Stroke.LJ_MITRE;
        else if( value.equals( "round" ) )
          smplLineJoin = Stroke.LJ_ROUND;
        else if( value.equals( "bevel" ) )
          smplLineJoin = Stroke.LJ_BEVEL;
        else
          throw new FilterEvaluationException( "Given value for parameter 'stroke-linejoin' ('" + value + "') is unsupported. Supported values are: " + "'mitre', 'round' or 'bevel'!" );
      }
  }

  /**
   * extracts the line cap of the stroke if it is simple (no Expression) to avoid new calculation for each call of
   * getStroke(Feature feature)
   */
  private void extractSimpleLineCap( ) throws FilterEvaluationException
  {
    final CssParameter cssParam = getParameter( CSS_LINECAP );
    if( cssParam != null )
      if( isSimple( cssParam ) )
      {
        final Object[] o = cssParam.getValue().getComponents();
        final String value = ((String) o[0]).trim();
        if( value.equals( "butt" ) )
          smplLineCap = Stroke.LC_BUTT;
        else if( value.equals( "round" ) )
          smplLineCap = Stroke.LC_ROUND;
        else if( value.equals( "square" ) )
          smplLineCap = Stroke.LC_SQUARE;
        else
          throw new FilterEvaluationException( "Given value for parameter 'stroke-linecap' ('" + value + "') is unsupported. Supported values are: " + "'butt', 'round' or 'square'!" );
      }
  }

  /**
   * extracts the dasharray of the stroke if it is simple (no Expression) to avoid new calculation for each call of
   * getStroke(Feature feature)
   */
  private void extractSimpleDasharray( ) throws FilterEvaluationException
  {
    final CssParameter cssParam = getParameter( CSS_DASHARRAY );
    if( cssParam == null )
      return;

    if( !isSimple( cssParam ) )
      return;

    final Object[] o = cssParam.getValue().getComponents();
    if( o.length == 0 )
      return;

    final String value = ((String) o[0]).trim();
    final StringTokenizer st = new StringTokenizer( value, ",; " );
    final int count = st.countTokens();
    float[] dashArray;
    if( count % 2 == 0 )
      dashArray = new float[count];
    else
      dashArray = new float[count * 2];

    int k = 0;
    while( st.hasMoreTokens() )
    {
      final String s = st.nextToken();
      try
      {
        dashArray[k++] = Float.parseFloat( s );
      }
      catch( final NumberFormatException e )
      {
        throw new FilterEvaluationException( "List of values for parameter 'stroke-dashoffset' " + "contains an invalid token: '" + s + "'!" );
      }
    }

    // odd number of values -> the pattern must be repeated twice
    if( count % 2 == 1 )
    {
      int j = 0;
      while( k < count * 2 )
        dashArray[k++] = dashArray[j++];
    }

    smplDashArray = dashArray;
  }

  /**
   * extracts the dash offset of the stroke if it is simple (no Expression) to avoid new calculation for each call of
   * getStroke(Feature feature)
   */
  private void extractSimpleDashOffset( ) throws FilterEvaluationException
  {
    final CssParameter cssParam = getParameter( CSS_DASHOFFSET );
    if( cssParam != null )
      if( isSimple( cssParam ) )
      {
        final Object[] o = cssParam.getValue().getComponents();
        final String value = ((String) o[0]).trim();
        try
        {
          smplDashOffset = Float.parseFloat( value );
        }
        catch( final NumberFormatException e )
        {
          throw new FilterEvaluationException( "Given value for parameter 'stroke-dashoffset' ('" + value + "') has invalid format!" );
        }
      }
  }

  /**
   * The GraphicStroke element both indicates that a repeated-linear-graphic stroke type will be used.
   * <p>
   *
   * @returns the underlying <tt>GraphicStroke</tt> instance (may be null)
   */
  @Override
  public GraphicStroke getGraphicStroke( )
  {
    return m_graphicStroke;
  }

  /**
   * The GraphicStroke element both indicates that a repeated-linear-graphic stroke type will be used.
   *
   * @param graphicStroke
   *          the graphicStroke element
   *          <p>
   */
  @Override
  public void setGraphicStroke( final GraphicStroke graphicStroke )
  {
    m_graphicStroke = graphicStroke;
  }

  /**
   * The stroke CssParameter element gives the solid color that will be used for a stroke. The color value is
   * RGB-encoded using two hexadecimal digits per primary-color component, in the order Red, Green, Blue, prefixed with
   * a hash (#) sign. The hexadecimal digits between A and F may be in either uppercase or lowercase. For example, full
   * red is encoded as #ff0000 (with no quotation marks). The default color is defined to be black (#000000) in the
   * context of the LineSymbolizer, if the stroke CssParameter element is absent.
   * <p>
   *
   * @param feature
   *          specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) value of the parameter
   * @throws FilterEvaluationException
   *           if the evaluation fails
   */
  @Override
  public Color getStroke( final Feature feature ) throws FilterEvaluationException
  {
    Color awtColor = COLOR_DEFAULT;

    if( color == null )
    {
      // evaluate color depending on the passed feature's properties
      final CssParameter cssParam = getParameter( CSS_STROKE );

      if( cssParam != null )
      {
        final String s = cssParam.getValue( feature );

        try
        {
          awtColor = s == null || s.length() == 0 ? Color.BLACK : Color.decode( s );
        }
        catch( final NumberFormatException e )
        {
          throw new FilterEvaluationException( "Given value ('" + s + "') for CSS-Parameter 'stroke' " + "does not denote a valid color!" );
        }
      }
    }
    else
      awtColor = color;

    return awtColor;
  }

  /**
   * @see org.kalypsodeegree_impl.graphics.sld.Stroke_Impl#getStroke(Feature) <p>
   * @param stroke
   *          the stroke to be set
   */
  @Override
  public void setStroke( final Color stroke )
  {
    color = stroke;
    final CssParameter strokeColor = StyleFactory.createCssParameter( CSS_STROKE, StyleFactory.getColorAsHex( stroke ) );
    getCssParameters().put( CSS_STROKE, strokeColor );
  }

  /**
   * The stroke-opacity CssParameter element specifies the level of translucency to use when rendering the stroke. The
   * value is encoded as a floating-point value (float) between 0.0 and 1.0 with 0.0 representing completely transparent
   * and 1.0 representing completely opaque, with a linear scale of translucency for intermediate values. For example,
   * 0.65 would represent 65% opacity. The default value is 1.0 (opaque).
   * <p>
   *
   * @param feature
   *          specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) value of the parameter
   * @throws FilterEvaluationException
   *           if the evaluation fails
   */
  @Override
  public double getOpacity( final Feature feature ) throws FilterEvaluationException
  {
    if( smplOpacity < 0 )
    {
      final CssParameter cssParam = getParameter( CSS_OPACITY );

      if( cssParam != null )
      {
        // evaluate opacity depending on the passed feature's properties
        final String value = cssParam.getValue( feature );

        try
        {
          final double opacity = Double.parseDouble( value );
          if( opacity < 0.0 || opacity > 1.0 )
            throw new FilterEvaluationException( "Value for parameter 'stroke-opacity' (given: '" + value + "') must be between 0.0 and 1.0!" );
          return opacity;
        }
        catch( final NumberFormatException e )
        {
          throw new FilterEvaluationException( "Given value for parameter 'stroke-opacity' ('" + value + "') has invalid format!" );
        }
      }

      return OPACITY_DEFAULT;
    }
    else
      return smplOpacity;
  }

  /**
   * @see org.kalypsodeegree_impl.graphics.sld.Stroke_Impl#getOpacity(Feature) <p>
   * @param opacity
   *          the opacity to be set for the stroke
   */
  @Override
  public void setOpacity( double opacity )
  {
    if( opacity > 1 )
      opacity = 1;
    else if( opacity < 0 )
      opacity = 0;
    smplOpacity = opacity;
    final CssParameter strokeOp = StyleFactory.createCssParameter( CSS_OPACITY, "" + opacity );
    addCssParameter( CSS_OPACITY, strokeOp );
  }

  /**
   * The stroke-width CssParameter element gives the absolute width (thickness) of a stroke in pixels encoded as a
   * float. (Arguably, more units could be provided for encoding sizes, such as millimeters or typesetter's points.) The
   * default is 1.0. Fractional numbers are allowed (with a system-dependent interpretation) but negative numbers are
   * not.
   * <p>
   *
   * @param feature
   *          specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) value of the parameter
   * @throws FilterEvaluationException
   *           if the evaluation fails
   */
  @Override
  public double getWidth( final Feature feature ) throws FilterEvaluationException
  {
    double width = WIDTH_DEFAULT;

    if( smplWidth < 0 )
    {
      // evaluate smplWidth depending on the passed feature's properties
      final CssParameter cssParam = getParameter( CSS_WIDTH );

      if( cssParam != null )
      {
        final String value = cssParam.getValue( feature );

        try
        {
          width = Double.parseDouble( value );
        }
        catch( final NumberFormatException e )
        {
          throw new FilterEvaluationException( "Given value for parameter 'stroke-width' ('" + value + "') has invalid format!" );
        }

        if( width <= 0.0 )
          throw new FilterEvaluationException( "Value for parameter 'stroke-width' (given: '" + value + "') must be greater than 0!" );
      }
    }
    else
      width = smplWidth;

    return width;
  }

  /**
   * @see org.kalypsodeegree_impl.graphics.sld.Stroke_Impl#getWidth(Feature) <p>
   * @param width
   *          the width to be set for the stroke
   */
  @Override
  public void setWidth( double width )
  {
    if( width <= 0 )
      width = 1;
    smplWidth = width;
    final CssParameter strokeWi = StyleFactory.createCssParameter( CSS_WIDTH, "" + width );
    addCssParameter( CSS_WIDTH, strokeWi );
  }

  /**
   * The stroke-linejoin CssParameter element encode enumerated values telling how line strings should be joined
   * (between line segments). The values are represented as content strings. The allowed values for line join are mitre,
   * round, and bevel.
   * <p>
   *
   * @param feature
   *          specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) value of the parameter
   * @throws FilterEvaluationException
   *           if the evaluation fails
   */
  @Override
  public int getLineJoin( final Feature feature ) throws FilterEvaluationException
  {
    int lineJoin = LJ_DEFAULT;

    if( smplLineJoin < 0 )
    {
      final CssParameter cssParam = getParameter( CSS_LINEJOIN );

      if( cssParam != null )
      {
        final String value = cssParam.getValue( feature );

        if( value.equals( "mitre" ) )
          lineJoin = Stroke.LJ_MITRE;
        else if( value.equals( "round" ) )
          lineJoin = Stroke.LJ_ROUND;
        else if( value.equals( "bevel" ) )
          lineJoin = Stroke.LJ_BEVEL;
        else
          throw new FilterEvaluationException( "Given value for parameter 'stroke-linejoin' ('" + value + "') is unsupported. Supported values are: " + "'mitre', 'round' or 'bevel'!" );
      }
    }
    else
      lineJoin = smplLineJoin;

    return lineJoin;
  }

  /**
   * @see org.kalypsodeegree_impl.graphics.sld.Stroke_Impl#getLineJoin(Feature) <p>
   * @param lineJoin
   *          the lineJoin to be set for the stroke
   */
  @Override
  public void setLineJoin( int lineJoin )
  {
    String join = null;
    if( lineJoin == Stroke.LJ_MITRE )
      join = "mitre";
    else if( lineJoin == Stroke.LJ_ROUND )
      join = "round";
    else if( lineJoin == Stroke.LJ_BEVEL )
      join = "bevel";
    else
    {
      // default
      lineJoin = Stroke.LJ_BEVEL;
      join = "bevel";
    }
    smplLineJoin = lineJoin;
    final CssParameter strokeLJ = StyleFactory.createCssParameter( CSS_LINEJOIN, join );
    addCssParameter( CSS_LINEJOIN, strokeLJ );
  }

  /**
   * Thestroke-linecap CssParameter element encode enumerated values telling how line strings should be capped (at the
   * two ends of the line string). The values are represented as content strings. The allowed values for line cap are
   * butt, round, and square. The default values are system-dependent.
   * <p>
   *
   * @param feature
   *          specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) value of the parameter
   * @throws FilterEvaluationException
   *           if the evaluation fails
   */
  @Override
  public int getLineCap( final Feature feature ) throws FilterEvaluationException
  {
    int lineCap = LC_DEFAULT;

    if( smplLineCap < 0 )
    {

      final CssParameter cssParam = getParameter( CSS_LINECAP );

      if( cssParam != null )
      {
        final String value = cssParam.getValue( feature );

        if( value.equals( "butt" ) )
          lineCap = Stroke.LC_BUTT;
        else if( value.equals( "round" ) )
          lineCap = Stroke.LC_ROUND;
        else if( value.equals( "square" ) )
          lineCap = Stroke.LC_SQUARE;
        else
          throw new FilterEvaluationException( "Given value for parameter 'stroke-linecap' ('" + value + "') is unsupported. Supported values are: " + "'butt', 'round' or 'square'!" );
      }
    }
    else
      lineCap = smplLineCap;

    return lineCap;
  }

  /**
   * @see org.kalypsodeegree_impl.graphics.sld.Stroke_Impl#getLineCap(Feature) <p>
   * @param lineCap
   *          lineCap to be set for the stroke
   */
  @Override
  public void setLineCap( int lineCap )
  {
    String cap = null;
    if( lineCap == Stroke.LC_BUTT )
      cap = "butt";
    else if( lineCap == Stroke.LC_ROUND )
      cap = "round";
    else if( lineCap == Stroke.LC_SQUARE )
      cap = "square";
    else
    {
      // default;
      cap = "round";
      lineCap = Stroke.LC_SQUARE;
    }
    smplLineCap = lineCap;
    final CssParameter strokeCap = StyleFactory.createCssParameter( CSS_LINECAP, cap );
    addCssParameter( CSS_LINECAP, strokeCap );
  }

  /**
   * Evaluates the 'stroke-dasharray' parameter as defined in OGC 02-070. The stroke-dasharray CssParameter element
   * encodes a dash pattern as a series of space separated floats. The first number gives the length in pixels of dash
   * to draw, the second gives the amount of space to leave, and this pattern repeats. If an odd number of values is
   * given, then the pattern is expanded by repeating it twice to give an even number of values. Decimal values have a
   * system-dependent interpretation (usually depending on whether antialiasing is being used). The default is to draw
   * an unbroken line.
   * <p>
   *
   * @param feature
   *          the encoded pattern
   * @throws FilterEvaluationException
   *           if the eevaluation fails or the encoded pattern is erroneous
   * @return the decoded pattern as an array of float-values (null if the parameter was not specified)
   */
  @Override
  public float[] getDashArray( final Feature feature ) throws FilterEvaluationException
  {
    final CssParameter cssParam = getParameter( CSS_DASHARRAY );

    float[] dashArray = null;
    if( smplDashArray == null )
    {
      if( cssParam == null )
        return null;

      final String value = cssParam.getValue( feature );

      final StringTokenizer st = new StringTokenizer( value, ",; " );
      final int count = st.countTokens();

      if( count % 2 == 0 )
        dashArray = new float[count];
      else
        dashArray = new float[count * 2];

      int i = 0;
      while( st.hasMoreTokens() )
      {
        final String s = st.nextToken();
        try
        {
          dashArray[i++] = Float.parseFloat( s );
        }
        catch( final NumberFormatException e )
        {
          throw new FilterEvaluationException( "List of values for parameter 'stroke-dashoffset' " + "contains an invalid token: '" + s + "'!" );
        }
      }

      // odd number of values -> the pattern must be repeated twice
      if( count % 2 == 1 )
      {
        int j = 0;
        while( i < count * 2 - 1 )
          dashArray[i++] = dashArray[j++];
      }
    }
    else
      dashArray = smplDashArray;

    return dashArray;
  }

  /**
   * @see org.kalypsodeegree_impl.graphics.sld.Stroke_Impl#getDashArray(Feature) <p>
   * @param dashArray
   *          the dashArray to be set for the Stroke
   */
  @Override
  public void setDashArray( final float[] dashArray )
  {
    if( dashArray != null )
    {
      final String s = StringUtils.join( ArrayUtils.toObject( dashArray ), ',' );
      smplDashArray = dashArray;
      final CssParameter strokeDash = StyleFactory.createCssParameter( CSS_DASHARRAY, s );
      addCssParameter( CSS_DASHARRAY, strokeDash );
    }
  }

  /**
   * The stroke-dashoffset CssParameter element specifies the distance as a float into the stroke-dasharray pattern at
   * which to start drawing.
   * <p>
   *
   * @param feature
   *          specifies the <tt>Feature</tt> to be used for evaluation of the underlying 'sld:ParameterValueType'
   * @return the (evaluated) value of the parameter
   * @throws FilterEvaluationException
   *           if the evaluation fails
   */
  @Override
  public float getDashOffset( final Feature feature ) throws FilterEvaluationException
  {
    float dashOffset = 0;

    if( smplDashOffset < 0 )
    {
      final CssParameter cssParam = getParameter( CSS_DASHOFFSET );
      if( cssParam != null )
      {
        final String value = cssParam.getValue( feature );

        try
        {
          dashOffset = Float.parseFloat( value );
        }
        catch( final NumberFormatException e )
        {
          throw new FilterEvaluationException( "Given value for parameter 'stroke-dashoffset' ('" + value + "') has invalid format!" );
        }
      }
    }
    else
      dashOffset = smplDashOffset;

    return dashOffset;
  }

  /**
   * The stroke-dashoffset CssParameter element specifies the distance as a float into the stroke-dasharray pattern at
   * which to start drawing.
   * <p>
   *
   * @param dashOffset
   *          the dashOffset to be set for the Stroke
   */
  @Override
  public void setDashOffset( float dashOffset )
  {
    if( dashOffset < 0 )
      dashOffset = 0;
    smplDashOffset = dashOffset;
    final CssParameter strokeDashOff = StyleFactory.createCssParameter( CSS_DASHOFFSET, "" + dashOffset );
    addCssParameter( CSS_DASHOFFSET, strokeDashOff );
  }

  /**
   * exports the content of the Stroke as XML formated String
   *
   * @return xml representation of the Stroke
   */
  @Override
  public String exportAsXML( )
  {
    final StringBuffer sb = new StringBuffer( 1000 );
    sb.append( "<Stroke>" );

    final GraphicFill graphicFill = getGraphicFill();
    if( graphicFill != null )
      sb.append( ((Marshallable) graphicFill).exportAsXML() );
    else if( m_graphicStroke != null )
      sb.append( ((Marshallable) m_graphicStroke).exportAsXML() );

    final Map<String, CssParameter> cssParams = getCssParameters();
    final Iterator<CssParameter> iterator = cssParams.values().iterator();
    while( iterator.hasNext() )
      sb.append( iterator.next().exportAsXML() );

    sb.append( "</Stroke>" );

    return sb.toString();
  }
}