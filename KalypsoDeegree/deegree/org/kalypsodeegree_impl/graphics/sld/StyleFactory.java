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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import org.kalypso.contribs.java.net.IUrlResolver2;
import org.kalypsodeegree.filterencoding.Expression;
import org.kalypsodeegree.filterencoding.Filter;
import org.kalypsodeegree.graphics.sld.ColorMapEntry;
import org.kalypsodeegree.graphics.sld.CssParameter;
import org.kalypsodeegree.graphics.sld.ExternalGraphic;
import org.kalypsodeegree.graphics.sld.FeatureTypeStyle;
import org.kalypsodeegree.graphics.sld.Fill;
import org.kalypsodeegree.graphics.sld.Font;
import org.kalypsodeegree.graphics.sld.Geometry;
import org.kalypsodeegree.graphics.sld.Graphic;
import org.kalypsodeegree.graphics.sld.GraphicFill;
import org.kalypsodeegree.graphics.sld.GraphicStroke;
import org.kalypsodeegree.graphics.sld.Halo;
import org.kalypsodeegree.graphics.sld.LabelPlacement;
import org.kalypsodeegree.graphics.sld.LegendGraphic;
import org.kalypsodeegree.graphics.sld.LinePlacement;
import org.kalypsodeegree.graphics.sld.LineSymbolizer;
import org.kalypsodeegree.graphics.sld.Mark;
import org.kalypsodeegree.graphics.sld.ParameterValueType;
import org.kalypsodeegree.graphics.sld.PointPlacement;
import org.kalypsodeegree.graphics.sld.PointSymbolizer;
import org.kalypsodeegree.graphics.sld.PolygonColorMapEntry;
import org.kalypsodeegree.graphics.sld.PolygonSymbolizer;
import org.kalypsodeegree.graphics.sld.RasterSymbolizer;
import org.kalypsodeegree.graphics.sld.Rule;
import org.kalypsodeegree.graphics.sld.Stroke;
import org.kalypsodeegree.graphics.sld.Style;
import org.kalypsodeegree.graphics.sld.Symbolizer;
import org.kalypsodeegree.graphics.sld.TextSymbolizer;
import org.kalypsodeegree.graphics.sld.UserStyle;
import org.kalypsodeegree_impl.filterencoding.PropertyName;
import org.kalypsodeegree_impl.graphics.sld.Symbolizer_Impl.UOM;

/**
 * An utility class designed to easy creation of style by convinience methods.
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 */
public final class StyleFactory
{
  private StyleFactory( )
  {
  }

  /**
   * creates a <tt>ParameterValueType</tt> instance with a <tt>String</tt> as value
   *
   * @param value
   *          value of the <tt>ParameterValueType</tt>
   * @return the ParameterValueType created
   */
  public static ParameterValueType createParameterValueType( final String value )
  {
    return new ParameterValueType_Impl( new Object[] { value } );
  }

  /**
   * creates a <tt>ParameterValueType</tt> instance with a <tt>int</tt> as value
   *
   * @param value
   *          value of the <tt>ParameterValueType</tt>
   * @return the ParameterValueType created
   */
  public static ParameterValueType createParameterValueType( final int value )
  {
    return new ParameterValueType_Impl( new Object[] { "" + value } );
  }

  /**
   * creates a <tt>ParameterValueType</tt> instance with a <tt>String</tt> as value
   *
   * @param value
   *          value of the <tt>ParameterValueType</tt>
   * @return the ParameterValueType created
   */
  public static ParameterValueType createParameterValueType( final double value )
  {
    return new ParameterValueType_Impl( new Object[] { "" + value } );
  }

  /**
   * creates a <tt>ParameterValueType</tt> instance with an array of <tt>Expression</tt> s as value
   *
   * @param expressions
   * @return the the ParameterValueType created
   */
  public static ParameterValueType createParameterValueType( final Expression[] expressions )
  {
    return new ParameterValueType_Impl( expressions );
  }

  /**
   * creates a <tt>ParameterValueType</tt> instance with an array of <tt>Expression</tt> or <tt>String</tt>s as value
   *
   * @param expressions
   * @return the the ParameterValueType created
   */
  public static ParameterValueType createParameterValueType( final Object[] expressionsOrString )
  {
    return new ParameterValueType_Impl( expressionsOrString );
  }

  /**
   * creates a CssParameter with a name and a value
   *
   * @param name
   *          name of the css parameter
   * @param value
   *          value of the css parameter
   * @return the CssParameter created
   */
  public static CssParameter createCssParameter( final String name, final String value )
  {
    final ParameterValueType pvt = createParameterValueType( value );
    return new CssParameter_Impl( name, pvt );
  }

  /**
   * creates a CssParameter with a name and a value
   *
   * @param name
   *          name of the css parameter
   * @param value
   *          value of the css parameter
   * @return the CssParameter created
   */
  public static CssParameter createCssParameter( final String name, final int value )
  {
    final ParameterValueType pvt = createParameterValueType( value );
    return new CssParameter_Impl( name, pvt );
  }

  /**
   * creates a CssParameter with a name and a value
   *
   * @param name
   *          name of the css parameter
   * @param value
   *          value of the css parameter
   * @return the CssParameter created
   */
  public static CssParameter createCssParameter( final String name, final double value )
  {
    final ParameterValueType pvt = createParameterValueType( value );
    return new CssParameter_Impl( name, pvt );
  }

  /**
   * creates a <tt>GraphicStroke</tt> from a <tt>Graphic</tt> object
   *
   * @param graphic
   *          <tt>Graphic</tt object
   * @return the GraphicStroke created
   */
  public static GraphicStroke createGraphicStroke( final Graphic graphic )
  {
    return new GraphicStroke_Impl( graphic );
  }

  /**
   * creates a <tt>GraphicFill</tt> from a <tt>Graphic</tt> object
   *
   * @param graphic
   *          <tt>Graphic</tt object
   * @return the GraphicFill created
   */
  public static GraphicFill createGraphicFill( final Graphic graphic )
  {
    return new GraphicFill_Impl( graphic );
  }

  /**
   * create a default Stroke that black, 1 pixel width, complete opaque, with round linejoin and square line cap
   *
   * @return the Stroke created
   */
  public static Stroke createStroke( )
  {
    return createStroke( Color.BLACK, 1, "round", "square" );
  }

  /**
   * create a default stroke with the supplied width
   *
   * @param width
   *          the width of the line
   * @return the stroke created
   */
  public static Stroke createStroke( final double width )
  {
    return createStroke( Color.BLACK, width );
  }

  /**
   * Create a default stroke with the supplied color
   *
   * @param color
   *          the color of the line
   * @return the created stroke
   */
  public static Stroke createStroke( final Color color )
  {
    return createStroke( color, 1 );
  }

  /**
   * create a stroke with the passed width and color
   *
   * @param color
   *          the color of the line
   * @param width
   *          the width of the line
   * @return the created stroke
   */
  public static Stroke createStroke( final Color color, final double width )
  {
    return createStroke( color, width, "round", "square" );
  }

  /**
   * create a stroke with color, width, linejoin type and lineCap type.
   *
   * @param color
   *          the color of the line
   * @param width
   *          the width of the line
   * @param lineJoin
   *          the type of join to be used at points along the line
   * @param lineCap
   *          the type of cap to be used at the end of the line
   * @return the stroke created
   */
  public static Stroke createStroke( final Color color, final double width, final String lineJoin, final String lineCap )
  {
    return createStroke( color, width, 1, null, lineJoin, lineCap );
  }

  /**
   * create a stroke with color, width, linejoin type and lineCap type.
   *
   * @param color
   *          the color of the line
   * @param width
   *          the width of the line
   * @param opacity
   *          the opacity or <I>see throughness </I> of the line, 0 - is transparent, 1 is completely drawn
   * @param lineJoin
   *          the type of join to be used at points along the line
   * @param lineCap
   *          the type of cap to be used at the end of the line
   * @return the stroke created
   */
  public static Stroke createStroke( final Color color, final double width, final double opacity, final float[] dashArray, final String lineJoin, final String lineCap )
  {
    final HashMap<String, CssParameter> cssParams = new HashMap<>();

    final CssParameter stroke = createCssParameter( "stroke", getColorAsHex( color ) );
    cssParams.put( "stroke", stroke );
    final CssParameter strokeOp = createCssParameter( "stroke-opacity", opacity );
    cssParams.put( "stroke-opacity", strokeOp );
    final CssParameter strokeWi = createCssParameter( "stroke-width", width );
    cssParams.put( "stroke-width", strokeWi );
    final CssParameter strokeLJ = createCssParameter( "stroke-linejoin", lineJoin );
    cssParams.put( "stroke-linejoin", strokeLJ );
    final CssParameter strokeCap = createCssParameter( "stroke-linecap", lineCap );
    cssParams.put( "stroke-linecap", strokeCap );

    if( dashArray != null )
    {
      String s = "";
      for( int i = 0; i < dashArray.length - 1; i++ )
      {
        s = s + dashArray[i] + ",";
      }
      s = s + dashArray[dashArray.length - 1];
      final CssParameter strokeDash = createCssParameter( "stroke-dasharray", s );
      cssParams.put( "stroke-dasharray", strokeDash );
    }

    return new Stroke_Impl( cssParams, null, null );
  }

  /**
   * create a dashed line of color and width
   *
   * @param color
   *          the color of the line
   * @param width
   *          the width of the line
   * @param dashArray
   *          an array of floats describing the length of line and spaces
   * @return the stroke created
   */
  public static Stroke createStroke( final Color color, final double width, final float[] dashArray )
  {
    final HashMap<String, CssParameter> cssParams = new HashMap<>();

    final CssParameter stroke = createCssParameter( "stroke", getColorAsHex( color ) );
    cssParams.put( "stroke", stroke );
    final CssParameter strokeOp = createCssParameter( "stroke-opacity", "1" );
    cssParams.put( "stroke-opacity", strokeOp );
    final CssParameter strokeWi = createCssParameter( "stroke-width", width );
    cssParams.put( "stroke-width", strokeWi );
    final CssParameter strokeLJ = createCssParameter( "stroke-linejoin", "mitre" );
    cssParams.put( "stroke-linejoin", strokeLJ );
    final CssParameter strokeCap = createCssParameter( "stroke-linejoin", "butt" );
    cssParams.put( "stroke-linecap", strokeCap );

    if( dashArray != null )
    {
      String s = "";
      for( int i = 0; i < dashArray.length - 1; i++ )
      {
        s = s + dashArray[i] + ",";
      }
      s = s + dashArray[dashArray.length - 1];
      final CssParameter strokeDash = createCssParameter( "stroke-dasharray", s );
      cssParams.put( "stroke-dasharray", strokeDash );
    }

    return new Stroke_Impl( cssParams, null, null );
  }

  /**
   * create a stroke with color, width and opacity supplied
   *
   * @param color
   *          the color of the line
   * @param width
   *          the width of the line
   * @param opacity
   *          the opacity or <I>see throughness </I> of the line, 0 - is transparent, 1 is completely drawn
   * @return the stroke created
   */
  public static Stroke createStroke( final Color color, final double width, final double opacity )
  {
    return createStroke( color, width, opacity, null, "mitre", "butt" );
  }

  /**
   * create a default fill 50% gray
   *
   * @return the fill created
   */
  public static Fill createFill( )
  {
    return createFill( Color.GRAY, 1d, null );
  }

  /**
   * create a fill of color
   *
   * @param color
   *          the color of the fill
   * @return the fill created
   */
  public static Fill createFill( final Color color )
  {
    return createFill( color, 1d, null );
  }

  /**
   * create a fill with the supplied color and opacity
   *
   * @param color
   *          the color to fill with
   * @param opacity
   *          the opacity of the fill 0 - transparent, 1 - completly filled
   * @return the fill created
   */
  public static Fill createFill( final Color color, final double opacity )
  {
    return createFill( color, opacity, null );
  }

  /**
   * create a fill with color and opacity supplied and uses the graphic fill supplied for the fill
   *
   * @param color
   *          the foreground color
   * @param opacity
   *          the opacity of the fill
   * @param fill
   *          the graphic object to use to fill the fill
   * @return the fill created
   */
  public static Fill createFill( final Color color, final double opacity, final GraphicFill fill )
  {
    final HashMap<String, CssParameter> cssParams = new HashMap<>();
    final CssParameter fillCo = createCssParameter( "fill", getColorAsHex( color ) );
    cssParams.put( "fill", fillCo );
    final CssParameter fillOp = createCssParameter( "fill-opacity", opacity );
    cssParams.put( "fill-opacity", fillOp );
    return new Fill_Impl( cssParams, fill );
  }

  /**
   * create the named mark
   *
   * @param wellKnownName
   *          the wellknown name of the mark
   * @return the mark created
   */
  public static Mark createMark( final String wellKnownName )
  {
    return new Mark_Impl( wellKnownName, createStroke(), createFill() );
  }

  /**
   * create the named mark with the colors etc supplied
   *
   * @param wellKnownName
   *          the well known name of the mark
   * @param fillColor
   *          the color of the mark
   * @param borderColor
   *          the outline color of the mark
   * @param borderWidth
   *          the width of the outline
   * @return the mark created
   */
  public static Mark createMark( final String wellKnownName, final Color fillColor, final Color borderColor, final double borderWidth )
  {
    final Stroke stroke = createStroke( borderColor, borderWidth );
    final Fill fill = createFill( fillColor );
    return new Mark_Impl( wellKnownName, stroke, fill );
  }

  /**
   * create a mark with default fill (50% gray) and the supplied outline
   *
   * @param wellKnownName
   *          the well known name of the mark
   * @param borderColor
   *          the outline color
   * @param borderWidth
   *          the outline width
   * @return the mark created
   */
  public static Mark createMark( final String wellKnownName, final Color borderColor, final double borderWidth )
  {
    final Stroke stroke = createStroke( borderColor, borderWidth );
    final Fill fill = createFill();
    return new Mark_Impl( wellKnownName, stroke, fill );
  }

  /**
   * create a mark of the supplied color and a default outline (black)
   *
   * @param wellKnownName
   *          the well known name of the mark
   * @param fillColor
   *          the color of the mark
   * @return the created mark
   */
  public static Mark createMark( final String wellKnownName, final Color fillColor )
  {
    final Stroke stroke = createStroke();
    final Fill fill = createFill( fillColor );
    return new Mark_Impl( wellKnownName, stroke, fill );
  }

  /**
   * create a mark with the supplied fill and stroke
   *
   * @param wellKnownName
   *          the well known name of the mark
   * @param fill
   *          the fill to use
   * @param stroke
   *          the stroke to use
   * @return the mark created
   */
  public static Mark createMark( final String wellKnownName, final Fill fill, final Stroke stroke )
  {
    return new Mark_Impl( wellKnownName, stroke, fill );
  }

  /**
   * wrapper for stylefactory method
   *
   * @param url
   *          the url of the image
   * @param format
   *          mime type of the image
   * @return the external graphic
   */
  public static ExternalGraphic createExternalGraphic( final IUrlResolver2 resolver, final String href, final String format )
  {
    return new ExternalGraphic_Impl( resolver, format, href );
  }

  /**
   * creates a graphic object
   *
   * @param externalGraphic
   *          an external graphic to use if displayable
   * @param mark
   *          a mark to use
   * @param opacity
   *          - the opacity of the graphic
   * @param size
   *          - the size of the graphic
   * @param rotation
   *          - the rotation from the top of the page of the graphic
   * @return the graphic created
   */
  public static Graphic createGraphic( final ExternalGraphic externalGraphic, final Mark mark, final double opacity, final double size, final double rotation )
  {
    Object[] mae = null;
    if( externalGraphic != null && mark != null )
    {
      mae = new Object[] { externalGraphic, mark };
    }
    else if( externalGraphic != null )
    {
      mae = new Object[] { externalGraphic };
    }
    else if( mark != null )
    {
      mae = new Object[] { mark };
    }
    final ParameterValueType op = createParameterValueType( opacity );
    final ParameterValueType sz = createParameterValueType( size );
    final ParameterValueType ro = createParameterValueType( rotation );
    return new Graphic_Impl( mae, op, sz, ro );
  }

  /**
   * wrapper round Stylefactory Method
   *
   * @return the default pointplacement
   */
  public static PointPlacement createPointPlacement( )
  {
    return new PointPlacement_Impl();
  }

  /**
   * wrapper round Stylefactory Method
   *
   * @param anchorX
   *          - the X coordinate
   * @param anchorY
   *          - the Y coordinate
   * @param rotation
   *          - the rotaion of the label
   * @return the pointplacement created
   */
  public static PointPlacement createPointPlacement( final double anchorX, final double anchorY, final double rotation )
  {
    final ParameterValueType pvt1 = createParameterValueType( anchorX );
    final ParameterValueType pvt2 = createParameterValueType( anchorY );
    final ParameterValueType[] anchorPoint = new ParameterValueType[] { pvt1, pvt2 };
    final ParameterValueType rot = createParameterValueType( rotation );
    return new PointPlacement_Impl( anchorPoint, null, rot, false );
  }

  /**
   * wrapper round Stylefactory Method
   *
   * @param anchorX
   *          - the X coordinate
   * @param anchorY
   *          - the Y coordinate
   * @param displacementX
   *          - the X distance from the anchor
   * @param displacementY
   *          - the Y distance from the anchor
   * @param rotation
   *          - the rotaion of the label
   * @return the pointplacement created
   */
  public static PointPlacement createPointPlacement( final double anchorX, final double anchorY, final double displacementX, final double displacementY, final double rotation )
  {
    final ParameterValueType pvt1 = createParameterValueType( anchorX );
    final ParameterValueType pvt2 = createParameterValueType( anchorY );
    final ParameterValueType[] anchorPoint = new ParameterValueType[] { pvt1, pvt2 };

    final ParameterValueType pvt3 = createParameterValueType( displacementX );
    final ParameterValueType pvt4 = createParameterValueType( displacementY );
    final ParameterValueType[] displacement = new ParameterValueType[] { pvt3, pvt4 };

    final ParameterValueType rot = createParameterValueType( rotation );
    return new PointPlacement_Impl( anchorPoint, displacement, rot, false );
  }

  /**
   * @param anchorX
   *          - the X coordinate
   * @param anchorY
   *          - the Y coordinate
   * @param displacementX
   *          - the X distance from the anchor
   * @param displacementY
   *          - the Y distance from the anchor
   * @param rotation
   *          - the rotaion of the label
   * @param auto
   *          - auto positioning of the label
   * @return the pointplacement created
   */
  public static PointPlacement createPointPlacement( final double anchorX, final double anchorY, final double displacementX, final double displacementY, final double rotation, final boolean auto )
  {
    final ParameterValueType pvt1 = createParameterValueType( anchorX );
    final ParameterValueType pvt2 = createParameterValueType( anchorY );
    final ParameterValueType[] anchorPoint = new ParameterValueType[] { pvt1, pvt2 };

    final ParameterValueType pvt3 = createParameterValueType( displacementX );
    final ParameterValueType pvt4 = createParameterValueType( displacementY );
    final ParameterValueType[] displacement = new ParameterValueType[] { pvt3, pvt4 };

    final ParameterValueType rot = createParameterValueType( rotation );
    return new PointPlacement_Impl( anchorPoint, displacement, rot, auto );
  }

  /**
   * creates a <tt>LinePlacement</tt> with a user defined distance between the labels and the lines. A positive value
   * indicates a position above the line, a negative value indicates a position below. The line width is asumed to be 2
   * pixel and the gap between the labels is set to factor 10 of the label width.
   *
   * @param offset
   *          - the distance between the line and the label
   * @return the LinePlacement created
   */
  public static LinePlacement createLinePlacement( final double offset )
  {

    final ParameterValueType perpendicularOffset = createParameterValueType( offset );
    final ParameterValueType lineWidth = createParameterValueType( 2 );
    final ParameterValueType gap = createParameterValueType( 10 );

    return new LinePlacement_Impl( perpendicularOffset, lineWidth, gap );
  }

  /**
   * creates a <tt>LinePlacement</tt> with a relative position of the label according to the line the lines. The line
   * width is asumed to be 2 pixel and the gap between the labels is set to factor 10 of the label width.
   *
   * @param position
   *          of the label relative to the line
   * @return the LinePlacement created
   */
  public static LinePlacement createLinePlacement( final LinePlacement.PlacementType position )
  {
    final ParameterValueType perpendicularOffset = createParameterValueType( position.name() );
    final ParameterValueType lineWidth = createParameterValueType( 2 );
    final ParameterValueType gap = createParameterValueType( 10 );

    return new LinePlacement_Impl( perpendicularOffset, lineWidth, gap );
  }

  /**
   * creates a <tt>LinePlacement</tt> with a user defined distance between the labels and the lines. A positive value
   * indicates a position above the line, a negative value indicates a position below.
   *
   * @param offset
   *          - the distance between the line and the label
   * @param lineWidth
   *          - assumed lineWidth
   * @param gap
   *          - gap between the labels measured in label width
   * @return the LinePlacement created
   */
  public static LinePlacement createLinePlacement( final double offset, final double lineWidth, final int gap )
  {
    final ParameterValueType perpendicularOffset = createParameterValueType( offset );
    final ParameterValueType lineWidthPvt = createParameterValueType( lineWidth );
    final ParameterValueType gapPvt = createParameterValueType( gap );

    return new LinePlacement_Impl( perpendicularOffset, lineWidthPvt, gapPvt );
  }

  /**
   * creates a <tt>LinePlacement</tt> with a user defined distance between the labels and the lines. A positive value
   * indicates a position above the line, a negative value indicates a position below.
   *
   * @param position
   *          - relative position of the label to the line
   * @param lineWidth
   *          - assumed lineWidth
   * @param gap
   *          - gap between the labels measured in label width
   * @return the LinePlacement created
   */
  public static LinePlacement createLinePlacement( final String position, final double lineWidth, final int gap )
  {
    final ParameterValueType perpendicularOffset = createParameterValueType( position );
    final ParameterValueType lineWidthPvt = createParameterValueType( lineWidth );
    final ParameterValueType gapPvt = createParameterValueType( gap );

    return new LinePlacement_Impl( perpendicularOffset, lineWidthPvt, gapPvt );
  }

  /**
   * creates a label placement that is orientated on a line
   *
   * @param linePlacement
   *          description of the line where the lable will be orientated on
   * @return created LabelPlacement
   */
  public static LabelPlacement createLabelPlacement( final LinePlacement linePlacement )
  {
    return new LabelPlacement_Impl( linePlacement );
  }

  /**
   * creates a label placement that is orientated on a point
   *
   * @param pointPlacement
   *          description of the point where the lable will be orientated on
   * @return created LabelPlacement
   */
  public static LabelPlacement createLabelPlacement( final PointPlacement pointPlacement )
  {
    return new LabelPlacement_Impl( pointPlacement );
  }

  /**
   * create a geotools font object from a java font
   *
   * @param font
   *          - the font to be converted
   * @return - the deegree sld font
   */
  public static Font createFont( final java.awt.Font font )
  {
    return createFont( font.getFamily(), font.isItalic(), font.isBold(), font.getSize() );
  }

  /**
   * create font of supplied family and size
   *
   * @param fontFamily
   *          - the font family
   * @param fontSize
   *          - the size of the font in points
   * @return the font object created
   */
  public static Font createFont( final String fontFamily, final double fontSize )
  {
    return createFont( fontFamily, false, false, fontSize );
  }

  /**
   * create font of supplied family, size and weight/style
   *
   * @param fontFamily
   *          - the font family
   * @param italic
   *          - should the font be italic?
   * @param bold
   *          - should the font be bold?
   * @param fontSize
   *          - the size of the font in points
   * @return the new font object
   */
  public static Font createFont( final String fontFamily, final boolean italic, final boolean bold, final double fontSize )
  {
    final Map<String, CssParameter> cssParams = new HashMap<>();

    cssParams.put( "font-family", createCssParameter( "font-family", fontFamily ) );
    cssParams.put( "font-size", createCssParameter( "font-size", "" + fontSize ) );
    if( bold )
    {
      cssParams.put( "font-weight", createCssParameter( "font-weight", "bold" ) );
    }
    else
    {
      cssParams.put( "font-weight", createCssParameter( "font-weight", "normal" ) );
    }
    if( italic )
    {
      cssParams.put( "font-style", createCssParameter( "font-style", "italic" ) );
    }
    else
    {
      cssParams.put( "font-style", createCssParameter( "font-style", "normal" ) );
    }

    return new Font_Impl( cssParams );
  }

  /**
   * wrapper round StyleFactory method to create default halo
   *
   * @return the new halo
   */
  public static Halo createHalo( )
  {
    return createHalo( createFill(), createStroke(), null );
  }

  /**
   * wrapper round StyleFactory method to create halo
   *
   * @param color
   *          - the color of the halo
   * @param radius
   *          - the radius of the halo use a value <= 0 for rectangle
   * @return the new halo
   */
  public static Halo createHalo( final Color color, final Double radius )
  {
    return createHalo( createFill( color ), createStroke(), radius );
  }

  /**
   * wrapper round StyleFactory method to create halo
   *
   * @param fillColor
   *          - the fill color of the halo
   * @param opacity
   *          - the opacity of the halo fill 0 - transparent 1 - solid
   * @param strokeColor
   *          - the stroke color of the halo
   * @param radius
   *          - the radius of the halo use a value <= 0 for rectangle
   * @return the new halo
   */
  public static Halo createHalo( final Color fillColor, final double opacity, final Color strokeColor, final Double radius )
  {
    final Fill fill = createFill( fillColor, opacity );
    final Stroke stroke = createStroke( strokeColor );
    return createHalo( fill, stroke, radius );
  }

  /**
   * wrapper round StyleFactory method to create halo
   *
   * @param fill
   *          - the fill of the halo
   * @param stroke
   *          - the stroke of the halo
   * @param radius
   *          - the radius of the halo use a value <= 0 for rectangle
   * @return the new halo
   */
  public static Halo createHalo( final Fill fill, final Stroke stroke, final Double radius )
  {
    ParameterValueType pvt = null;
    if( radius != null )
      pvt = createParameterValueType( radius );

    return new Halo_Impl( pvt, fill, stroke );
  }

  /**
   * create a default line symboliser
   *
   * @return the new line symbolizer
   */
  public static LineSymbolizer createLineSymbolizer( )
  {
    return createLineSymbolizer( createStroke( 1 ), null );
  }

  /**
   * create a new line symbolizer
   *
   * @param width
   *          the width of the line
   * @return the new line symbolizer
   */
  public static LineSymbolizer createLineSymbolizer( final double width )
  {
    return createLineSymbolizer( createStroke( width ), null );
  }

  /**
   * create a LineSymbolizer
   *
   * @param color
   *          - the color of the line
   * @return the new line symbolizer
   */
  public static LineSymbolizer createLineSymbolizer( final Color color )
  {
    return createLineSymbolizer( createStroke( color ), null );
  }

  /**
   * create a LineSymbolizer
   *
   * @param color
   *          - the color of the line
   * @param width
   *          - the width of the line
   * @return the new line symbolizer
   */
  public static LineSymbolizer createLineSymbolizer( final Color color, final double width )
  {
    return createLineSymbolizer( createStroke( color, width ), null );
  }

  /**
   * create a LineSymbolizer
   *
   * @param color
   *          - the color of the line
   * @param width
   *          - the width of the line
   * @param geometryPropertyName
   *          - the name of the geometry to be drawn
   * @return the new line symbolizer
   */
  public static LineSymbolizer createLineSymbolizer( final Color color, final double width, final PropertyName geometryPropertyName )
  {
    return createLineSymbolizer( createStroke( color, width ), geometryPropertyName );
  }

  /**
   * create a LineSymbolizer
   *
   * @param stroke
   *          - the stroke to be used to draw the line
   * @return the new line symbolizer
   */
  public static LineSymbolizer createLineSymbolizer( final Stroke stroke )
  {
    return createLineSymbolizer( stroke, null );
  }

  /**
   * create a LineSymbolizer
   *
   * @param stroke
   *          - the stroke to be used to draw the line
   * @param geometryPropertyName
   *          - the name of the geometry to be drawn
   * @return the new line symbolizer
   */
  public static LineSymbolizer createLineSymbolizer( final Stroke stroke, final PropertyName geometryPropertyName )
  {
    final Geometry geom = createGeometry( geometryPropertyName );
    return new LineSymbolizer_Impl( stroke, geom, UOM.pixel );
  }

  /**
   * create a default polygon symbolizer
   *
   * @return the new polygon symbolizer
   */
  public static PolygonSymbolizer createPolygonSymbolizer( )
  {
    return createPolygonSymbolizer( createStroke(), createFill() );
  }

  /**
   * create a polygon symbolizer
   *
   * @param fillColor
   *          - the color to fill the polygon
   * @return the new polygon symbolizer
   */
  public static PolygonSymbolizer createPolygonSymbolizer( final Color fillColor )
  {
    return createPolygonSymbolizer( createStroke(), createFill( fillColor ) );
  }

  /**
   * create a polygon symbolizer
   *
   * @param fillColor
   *          - the color to fill the polygon
   * @param borderColor
   *          - the outline color of the polygon
   * @param borderWidth
   *          - the width of the outline
   * @return the new polygon symbolizer
   */
  public static PolygonSymbolizer createPolygonSymbolizer( final Color fillColor, final Color borderColor, final double borderWidth )
  {
    return createPolygonSymbolizer( createStroke( borderColor, borderWidth ), createFill( fillColor ) );
  }

  /**
   * create a polygon symbolizer
   *
   * @param borderColor
   *          - the outline color of the polygon
   * @param borderWidth
   *          - the width of the outline
   * @return the new polygon symbolizer
   */
  public static PolygonSymbolizer createPolygonSymbolizer( final Color borderColor, final double borderWidth )
  {
    final Stroke stroke = createStroke( borderColor, borderWidth );
    return createPolygonSymbolizer( stroke, createFill() );
  }

  /**
   * create a polygon symbolizer
   *
   * @param stroke
   *          - the stroke to use to outline the polygon
   * @param fill
   *          - the fill to use to color the polygon
   * @return the new polygon symbolizer
   */
  public static PolygonSymbolizer createPolygonSymbolizer( final Stroke stroke, final Fill fill )
  {
    return createPolygonSymbolizer( stroke, fill, null );
  }

  /**
   * create a polygon symbolizer
   *
   * @param stroke
   *          - the stroke to use to outline the polygon
   * @param fill
   *          - the fill to use to color the polygon
   * @param geometryPropertyName
   *          - the name of the geometry to be drawn
   * @return the new polygon symbolizer
   */
  public static PolygonSymbolizer createPolygonSymbolizer( final Stroke stroke, final Fill fill, final PropertyName geometryPropertyName )
  {
    final Geometry geom = createGeometry( geometryPropertyName );
    return new PolygonSymbolizer_Impl( fill, stroke, geom, UOM.pixel );
  }

  /**
   * create a default raster symbolizer with one colorMap entry for noData values
   *
   * @return the new raster symbolizer
   */
  public static RasterSymbolizer createRasterSymbolizer( )
  {
    final TreeMap<Double, ColorMapEntry> colorMap = new TreeMap<>();
    final ColorMapEntry noData = new ColorMapEntry_Impl( Color.WHITE, 0, -9999, "Keine Daten" );
    colorMap.put( new Double( -9999 ), noData );
    return new RasterSymbolizer_Impl( null, colorMap, null, null );
  }

  /**
   * create a default point symbolizer
   *
   * @return the new point symbolizer
   */
  public static PointSymbolizer createPointSymbolizer( final PropertyName propertyName )
  {
    final Color grey = new Color( 200, 200, 200, 255 );
    final Color black = new Color( 0, 0, 0, 255 );
    final String wellKnownName = "square";

    final Mark mark = createMark( wellKnownName, grey, black, 1 );
    final Graphic graphic = createGraphic( null, mark, 1, 5, 0 );

    return createPointSymbolizer( graphic, propertyName );
  }

  /**
   * create a default point symbolizer
   *
   * @return the new point symbolizer
   */
  public static PointSymbolizer createPointSymbolizer( )
  {
    final Graphic graphic = createGraphic( null, null, 1, 5, 0 );
    return createPointSymbolizer( graphic );
  }

  /**
   * create a point symbolizer
   *
   * @param graphic
   *          - the graphic object to draw at the point
   * @return the new point symbolizer
   */
  public static PointSymbolizer createPointSymbolizer( final Graphic graphic )
  {
    return createPointSymbolizer( graphic, null );
  }

  /**
   * create a point symbolizer
   *
   * @param graphic
   *          - the graphic object to draw at the point
   * @param geometryPropertyName
   *          - the name of the geometry to be drawn
   * @return the new point symbolizer
   */
  public static PointSymbolizer createPointSymbolizer( final Graphic graphic, final PropertyName geometryPropertyName )
  {
    final Geometry geom = createGeometry( geometryPropertyName );
    return new PointSymbolizer_Impl( graphic, geom, UOM.pixel );
  }

  /**
   * create a textsymbolizer
   *
   * @param color
   *          the color of the text
   * @param font
   *          the font to use
   * @param attributeName
   *          the attribute to use for the label
   * @return the new textsymbolizer
   */
  public static TextSymbolizer createTextSymbolizer( final Color color, final Font font, final String attributeName, final LabelPlacement labelPlacement )
  {
    final ParameterValueType label = createParameterValueType( attributeName );
    final Fill fill = createFill( color );
    final Halo halo = createHalo();
    return createTextSymbolizer( null, label, font, labelPlacement, halo, fill );
  }

  /**
   * create a textsymbolizer
   *
   * @param geometryPropertyName
   *          geometry assigned to the TextSymbolizer
   * @param attribute
   *          attribute to draw/print
   * @param labelPlacement
   *          defines the placement of the text
   * @return the new textsymbolizer
   */
  public static TextSymbolizer createTextSymbolizer( final PropertyName geometryPropertyName, final String attribute, final LabelPlacement labelPlacement )
  {
    final Font font = createFont( java.awt.Font.decode( "Sans Serif" ) );
    return createTextSymbolizer( geometryPropertyName, attribute, font, labelPlacement, createHalo(), createFill() );
  }

  /**
   * create a textsymbolizer
   *
   * @param geometryPropertyName
   *          geometry assigned to the TextSymbolizer
   * @param attribute
   *          attribute to draw/print
   * @param font
   *          font to use for the text
   * @param labelPlacement
   *          defines the placement of the text
   * @param halo
   *          halo/backgroud of the text
   * @param fill
   *          color, opacity of the text
   * @param min
   *          min scale denominator
   * @param max
   *          max scale denominator
   * @return the new textsymbolizer
   */
  public static TextSymbolizer createTextSymbolizer( final PropertyName geometryPropertyName, final String attribute, final Font font, final LabelPlacement labelPlacement, final Halo halo, final Fill fill )
  {
    final Geometry geom = createGeometry( geometryPropertyName );
    final ParameterValueType label = createParameterValueType( attribute );
    return createTextSymbolizer( geom, label, font, labelPlacement, halo, fill );
  }

  /**
   * create a textsymbolizer
   *
   * @param geometry
   *          geometry assigned to the TextSymbolizer
   * @param label
   *          attribute to draw/print
   * @param font
   *          font to use for the text
   * @param labelPlacement
   *          defines the placement of the text
   * @param halo
   *          halo/backgroud of the text
   * @param fill
   *          color, opacity of the text
   * @param min
   *          min scale denominator
   * @param max
   *          max scale denominator
   * @return the new textsymbolizer
   */
  public static TextSymbolizer createTextSymbolizer( final Geometry geometry, final ParameterValueType label, final Font font, final LabelPlacement labelPlacement, final Halo halo, final Fill fill )
  {
    return new TextSymbolizer_Impl( geometry, label, font, labelPlacement, halo, fill, UOM.pixel );
  }

  /**
   * create a simple styling rule
   *
   * @param symbolizer
   *          - the symbolizer to use
   * @return the new rule
   */
  public static Rule createRule( final Symbolizer symbolizer )
  {
    return createRule( symbolizer, 0, Double.MAX_VALUE );
  }

  /**
   * reate a simple styling rule
   *
   * @param symbolizers
   *          - an array of symbolizers to use
   * @return the new rule
   */
  public static Rule createRule( final Symbolizer[] symbolizers )
  {
    return createRule( symbolizers, 0, Double.MAX_VALUE );
  }

  /**
   * create a simple styling rule, see the SLD Spec for more details of scaleDenominators
   *
   * @param symbolizer
   *          - the symbolizer to use
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new rule
   */
  public static Rule createRule( final Symbolizer symbolizer, final double minScaleDenominator, final double maxScaleDenominator )
  {
    return createRule( new Symbolizer[] { symbolizer }, minScaleDenominator, maxScaleDenominator );
  }

  /**
   * create a simple styling rule, see the SLD Spec for more details of scaleDenominators
   *
   * @param symbolizers
   *          - an array of symbolizers to use
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new rule
   */
  public static Rule createRule( final Symbolizer[] symbolizers, final double minScaleDenominator, final double maxScaleDenominator )
  {
    return createRule( symbolizers, "default", "default", "default", minScaleDenominator, maxScaleDenominator );
  }

  /**
   * create a simple styling rule, see the SLD Spec for more details of scaleDenominators
   *
   * @param symbolizers
   *          - an array of symbolizers to use
   * @param name
   *          - name of the rule
   * @param title
   *          - title of the rule
   * @param description
   *          - text describing throws rule
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new rule
   */
  public static Rule createRule( final Symbolizer[] symbolizers, final String name, final String title, final String description, final double minScaleDenominator, final double maxScaleDenominator )
  {
    return createRule( symbolizers, name, title, description, null, null, false, minScaleDenominator, maxScaleDenominator );
  }

  /**
   * create a complex styling rule, see the SLD Spec for more details of scaleDenominators
   *
   * @param symbolizers
   *          - an array of symbolizers to use
   * @param name
   *          - name of the rule
   * @param title
   *          - title of the rule
   * @param description
   *          - text describing throws rule
   * @param filter
   *          - filter to use with the rule
   * @param elseFilter
   *          - true if the passed is an ElseFilter (see SLD spec)
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new rule
   */
  public static Rule createRule( final Symbolizer[] symbolizers, final String name, final String title, final String description, final LegendGraphic legendGraphic, final Filter filter, final boolean elseFilter, final double minScaleDenominator, final double maxScaleDenominator )
  {
    return new Rule_Impl( symbolizers, name, title, description, legendGraphic, filter, elseFilter, minScaleDenominator, maxScaleDenominator );
  }

  /**
   * create a Feature type styler
   *
   * @param symbolizer
   *          - the symbolizer to use
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( )
  {
    return createFeatureTypeStyle( new Rule[] {} );

  }

  /**
   * create a Feature type styler
   *
   * @param symbolizer
   *          - the symbolizer to use
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final Symbolizer symbolizer )
  {
    return createFeatureTypeStyle( null, symbolizer, 0, Double.MAX_VALUE );
  }

  /**
   * create a Feature type styler see the SLD Spec for more details of scaleDenominators
   *
   * @param symbolizer
   *          - the symbolizer to use
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final Symbolizer symbolizer, final double minScaleDenominator, final double maxScaleDenominator )
  {
    return createFeatureTypeStyle( null, symbolizer, minScaleDenominator, maxScaleDenominator );
  }

  /**
   * create a Feature type styler see the SLD Spec for more details of scaleDenominators
   *
   * @param symbolizers
   *          - an array of symbolizers to use
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final Symbolizer[] symbolizers, final double minScaleDenominator, final double maxScaleDenominator )
  {
    return createFeatureTypeStyle( null, symbolizers, minScaleDenominator, maxScaleDenominator );
  }

  /**
   * create a Feature type styler
   *
   * @param featureTypeStyleName
   *          - name for the feature type styler
   * @param symbolizer
   *          - the symbolizer to use
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final String featureTypeStyleName, final Symbolizer symbolizer )
  {
    return createFeatureTypeStyle( featureTypeStyleName, symbolizer, 0, Double.MAX_VALUE );
  }

  /**
   * create a Feature type styler
   *
   * @param featureTypeStyleName
   *          - name for the feature type styler
   * @param symbolizers
   *          - an array of symbolizers to use
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final String featureTypeStyleName, final Symbolizer[] symbolizers )
  {
    return createFeatureTypeStyle( featureTypeStyleName, symbolizers, 0, Double.MAX_VALUE );
  }

  /**
   * create a Feature type styler see the SLD Spec for more details of scaleDenominators
   *
   * @param featureTypeStyleName
   *          - name for the feature type styler
   * @param symbolizer
   *          - the symbolizer to use
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final String featureTypeStyleName, final Symbolizer symbolizer, final double minScaleDenominator, final double maxScaleDenominator )
  {
    return createFeatureTypeStyle( featureTypeStyleName, new Symbolizer[] { symbolizer }, minScaleDenominator, maxScaleDenominator );
  }

  /**
   * create a Feature type styler see the SLD Spec for more details of scaleDenominators
   *
   * @param featureTypeStyleName
   *          - name for the feature type styler
   * @param symbolizers
   *          - an array of symbolizers to use
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final String featureTypeStyleName, final Symbolizer[] symbolizers, final double minScaleDenominator, final double maxScaleDenominator )
  {
    final Rule rule = createRule( symbolizers, minScaleDenominator, maxScaleDenominator );

    return createFeatureTypeStyle( featureTypeStyleName, rule );
  }

  /**
   * create a Feature type styler
   *
   * @param rule
   *          - rule contained in the featureTypeStyle
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final Rule rule )
  {
    return createFeatureTypeStyle( new Rule[] { rule } );
  }

  /**
   * create a Feature type styler
   *
   * @param rules
   *          - rules contained in the featureTypeStyle
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final Rule[] rules )
  {
    return createFeatureTypeStyle( null, rules );
  }

  /**
   * create a Feature type styler
   *
   * @param featureTypeStyleName
   *          - name for the feature type styler
   * @param rule
   *          - rule contained in the featureTypeStyle
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final String featureTypeStyleName, final Rule rule )
  {
    return createFeatureTypeStyle( featureTypeStyleName, new Rule[] { rule } );
  }

  /**
   * create a Feature type styler
   *
   * @param featureTypeStyleName
   *          - name for the feature type styler
   * @param rules
   *          - rules contained in the featureTypeStyle
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final String featureTypeStyleName, final Rule[] rules )
  {
    return createFeatureTypeStyle( featureTypeStyleName, null, null, null, rules );
  }

  /**
   * create a Feature type styler
   *
   * @param featureTypeStyleName
   *          - name for the feature type styler
   * @param featureTypeName
   *          - name of the feature type the Feature type style shall be assigned to
   * @param title
   *          - title of the FeatureTypeStyle
   * @param description
   *          - text describing the FeatureTypeStyle
   * @param rules
   *          - rules contained in the featureTypeStyle
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final String featureTypeStyleName, final String title, final String description, final QName featureTypeQName, final Rule[] rules )
  {
    return createFeatureTypeStyle( featureTypeStyleName, title, description, featureTypeQName, null, rules );
  }

  /**
   * create a Feature type styler
   *
   * @param featureTypeStyleName
   *          - name for the feature type styler
   * @param featureTypeName
   *          - name of the feature type the Feature type style shall be assigned to
   * @param title
   *          - title of the FeatureTypeStyle
   * @param description
   *          - text describing the FeatureTypeStyle
   * @param rules
   *          - rules contained in the featureTypeStyle
   * @return the new feature type styler
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final String featureTypeStyleName, final String title, final String description, final QName featureTypeQName, final String[] semanticTypeIdentifier, final Rule[] rules )
  {
    return new FeatureTypeStyle_Impl( featureTypeStyleName, title, description, featureTypeQName, semanticTypeIdentifier, rules );
  }

  /**
   * create a new style
   *
   * @param symbolizer
   *          - the symbolizer to use
   * @return the new style
   */
  public static Style createStyle( final Symbolizer symbolizer )
  {
    return createStyle( null, symbolizer, 0, Double.MAX_VALUE );
  }

  /**
   * create a new style with name 'default'
   *
   * @param symbolizer
   *          - the symbolizer to use
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new style
   */
  public static Style createStyle( final Symbolizer symbolizer, final double minScaleDenominator, final double maxScaleDenominator )
  {
    return createStyle( "default", symbolizer, minScaleDenominator, maxScaleDenominator );
  }

  /**
   * create a new style
   *
   * @param name
   *          - the name of the style
   * @param symbolizer
   *          - the symbolizer to use
   * @return the new style
   */
  public static Style createStyle( final String name, final Symbolizer symbolizer )
  {
    return createStyle( name, symbolizer, 0, Double.MAX_VALUE );
  }

  /**
   * create a new style
   *
   * @param name
   *          - the name of the style
   * @param symbolizer
   *          - the symbolizer to use
   * @param minScaleDenominator
   *          - the minimim scale to draw the feature at
   * @param maxScaleDenominator
   *          - the maximum scale to draw the feature at
   * @return the new style
   */
  public static Style createStyle( final String name, final Symbolizer symbolizer, final double minScaleDenominator, final double maxScaleDenominator )
  {
    // create the feature type style
    final FeatureTypeStyle fts = createFeatureTypeStyle( name, symbolizer, minScaleDenominator, maxScaleDenominator );

    return createStyle( name, null, null, fts );
  }

  /**
   * create a style
   *
   * @param name
   *          - the name of the style
   * @param featureTypeName
   *          - name of the feature type the Feature type style shall be assigned to
   * @param title
   *          - title of the FeatureTypeStyle
   * @param description
   *          - text describing the FeatureTypeStyle
   * @param rules
   *          - rules contained in the featureTypeStyle
   * @return the new style
   */
  public static Style createStyle( final String name, final String title, final String description, final QName featureTypeQName, final Rule[] rules )
  {
    final FeatureTypeStyle fts = createFeatureTypeStyle( name, title, description, featureTypeQName, rules );
    return createStyle( name, null, null, fts );
  }

  /**
   * create a new style
   *
   * @param name
   *          - the name of the style
   * @param title
   *          - title of the style
   * @param description
   *          - text describing the style
   * @param featureTypeStyle
   *          - featureTypeStyle
   * @return the new style
   */
  public static Style createStyle( final String name, final String title, final String description, final FeatureTypeStyle featureTypeStyle )
  {
    return createStyle( name, title, description, new FeatureTypeStyle[] { featureTypeStyle } );
  }

  /**
   * create a new style
   *
   * @param name
   *          - the name of the style
   * @param title
   *          - title of the style
   * @param description
   *          - text describing the style
   * @param featureTypeStyles
   *          - featureTypeStyle
   * @return the new style
   */
  public static Style createStyle( final String name, final String title, final String description, final FeatureTypeStyle[] featureTypeStyles )
  {
    return createUserStyle( name, title, description, false, featureTypeStyles );
  }

  /**
   * create a new style
   *
   * @param name
   *          - the name of the style
   * @param title
   *          - title of the style
   * @param description
   *          - text describing the style
   * @param featureTypeStyles
   *          - featureTypeStyle
   * @return the new style
   */
  public static UserStyle createUserStyle( final String name, final String title, final String description, final boolean isDefault, final FeatureTypeStyle[] featureTypeStyles )
  {
    return new UserStyle_Impl( name, title, description, isDefault, featureTypeStyles );
  }

  /**
   * creates a style with name 'defaultPoint' for rendering point geometries
   *
   * @param wellKnownName
   *          the well known name of the mark
   * @param fillColor
   *          the color of the mark
   * @param borderColor
   *          the outline color of the mark
   * @param borderWidth
   *          the width of the outline
   * @param opacity
   *          - the opacity of the graphic
   * @param size
   *          - the size of the graphic
   * @param rotation
   *          - the rotation from the top of the page of the graphic
   * @param min
   *          - the minimim scale to draw the feature at
   * @param max
   *          - the maximum scale to draw the feature at
   * @return the style created
   */
  public static Style createPointStyle( final String wellKnownName, final Color fillColor, final Color borderColor, final double borderWidth, final double opacity, final double size, final double rotation )
  {
    final Mark mark = createMark( wellKnownName, fillColor, borderColor, borderWidth );
    final Graphic graphic = createGraphic( null, mark, opacity, size, rotation );
    final Symbolizer symbolizer = createPointSymbolizer( graphic, null );
    return createStyle( "defaultPoint", symbolizer );
  }

  /**
   * creates a style with name 'defaultLine' for rendering line geometries
   *
   * @param color
   *          the line color
   * @param width
   *          the width of the line
   * @param opacity
   *          - the opacity of the line
   * @param min
   *          - the minimim scale to draw the feature at
   * @param max
   *          - the maximum scale to draw the feature at
   * @return the style created
   */
  public static Style createLineStyle( final Color color, final double width, final double opacity )
  {
    final Stroke stroke = createStroke( color, width, opacity );
    final Symbolizer symbolizer = createLineSymbolizer( stroke, null );
    return createStyle( "defaultLine", symbolizer );
  }

  /**
   * creates a style with name 'defaultPolygon' for rendering polygon geometries
   *
   * @param fillColor
   *          - the fill color of the polygon
   * @param fillOpacity
   *          - the fill opacity of the polygon
   * @param strokeColor
   *          - the line color
   * @param strokeWidth
   *          - the width of the line
   * @param strokeOpacity
   *          - the opacity of the line
   * @param min
   *          - the minimim scale to draw the feature at
   * @param max
   *          - the maximum scale to draw the feature at
   * @return the style created
   */
  public static Style createPolygonStyle( final Color fillColor, final double fillOpacity, final Color strokeColor, final double strokeWidth, final double strokeOpacity )
  {
    final Stroke stroke = createStroke( strokeColor, strokeWidth, strokeOpacity );
    final Fill fill = createFill( fillColor, fillOpacity );
    final Symbolizer symbolizer = createPolygonSymbolizer( stroke, fill, null );
    return createStyle( "defaultPolygon", symbolizer );
  }

  // /**
  // * creates a style with name 'defaultPoint' for rendering point geometries.
  // * The style contains 1..n rules depending on the value range and the number
  // * of steps within it. So it is possible to create a style that creates
  // * different rendering depending on the value of one feature attribute.
  // * <p>
  // * there will be a linear interpolation between colors, size and width of
  // the
  // * first and the last rule considering the number of passed steps (rules)
  // *
  // * @param wellKnownNames -
  // * list of well known names of the mark. the first field will be
  // * assigned to the starting rule the last to the ending rule.
  // * @param startFillColor -
  // * the color of the mark of the first rule
  // * @param endFillColor -
  // * the color of the mark of the last rule
  // * @param startBorderColor -
  // * the outline color of the mark of the first rule
  // * @param endBorderColor -
  // * the outline color of the mark of the last rule
  // * @param startBorderWidth -
  // * the width of the outline of the first rule
  // * @param endBorderWidth -
  // * the width of the outline of the last rule
  // * @param opacity -
  // * the opacity of the graphic
  // * @param startSize -
  // * the size of the graphic of the first rule
  // * @param endSize -
  // * the size of the graphic of the last rule
  // * @param rotation -
  // * the rotation from the top of the page of the graphic
  // * @param min -
  // * the minimim scale to draw the feature at
  // * @param max -
  // * the maximum scale to draw the feature at
  // * @param featurePropertyName -
  // * name of the feature property that determines the selection of the
  // * rule for drawing
  // * @param numberOfSteps -
  // * number of steps used for the interpolation between first and last
  // * value. It is identical with the number of rules that will be
  // * created.
  // *
  // * @return the style created
  // */
  // public static Style createPointStyle( String[] wellKnownNames, Color
  // startFillColor,
  // Color endFillColor, Color startBorderColor, Color endBorderColor, double
  // startBorderWidth,
  // double endBorderWidth, double opacity, double startSize, double endSize,
  // double rotation,
  // double min, double max, String featurePropertyName, int numberOfSteps )
  // {
  // return null;
  // }
  //
  // /**
  // * creates a style with name 'defaultLine' for rendering line geometries.
  // The
  // * style contains 1..n rules depending on the value range and the number of
  // * steps within it. So it is possible to create a style that creates
  // different
  // * rendering depending on the value of one feature attribute.
  // * <p>
  // * there will be a linear interpolation between colors, size and width of
  // the
  // * first and the last rule considering the number of passed steps (rules)
  // *
  // * @param startColor -
  // * the color of the first rule
  // * @param endColor -
  // * the color of the last rule
  // * @param startWidth -
  // * the width of the line of the first rule
  // * @param endWidth -
  // * the width of the line of the last rule
  // * @param opacity -
  // * the opacity of the graphic
  // * @param min -
  // * the minimim scale to draw the feature at
  // * @param max -
  // * the maximum scale to draw the feature at
  // * @param featurePropertyName -
  // * name of the feature property that determines the selection of the
  // * rule for drawing
  // * @param numberOfSteps -
  // * number of steps used for the interpolation between first and last
  // * value. It is identical with the number of rules that will be
  // * created.
  // *
  // * @return the style created
  // */
  // public static Style createLineStyle( Color startColor, Color endColor,
  // double startWidth,
  // double endWidth, double opacity, double min, double max, String
  // featurePropertyName,
  // int numberOfSteps )
  // {
  // return null;
  // }
  //
  // /**
  // * creates a style with name 'defaultPoint' for rendering point geometries.
  // * The style contains 1..n rules depending on the value range and the number
  // * of steps within it. So it is possible to create a style that creates
  // * different rendering depending on the value of one feature attribute.
  // * <p>
  // * there will be a linear interpolation between colors, size and width of
  // the
  // * first and the last rule considering the number of passed steps (rules)
  // *
  // * @param startFillColor -
  // * the fill color of the first rule
  // * @param endFillColor -
  // * the fill color of the last rule
  // * @param fillOpacity -
  // * the opacity of the fill
  // * @param startStrokeColor -
  // * the line color of the first rule
  // * @param endStrokeColor -
  // * the line color of the last rule
  // * @param startStrokeWidth -
  // * the width of the outline of the first rule
  // * @param endStrokeWidth -
  // * the width of the outline of the last rule
  // * @param strokeOpacity -
  // * the opacity of the outline
  // * @param min -
  // * the minimim scale to draw the feature at
  // * @param max -
  // * the maximum scale to draw the feature at
  // * @param featurePropertyName -
  // * name of the feature property that determines the selection of the
  // * rule for drawing
  // * @param numberOfSteps -
  // * number of steps used for the interpolation between first and last
  // * value. It is identical with the number of rules that will be
  // * created.
  // *
  // * @return the style created
  // */
  // public static Style createPolygonStyle( Color startFillColor, Color
  // endFillColor,
  // double fillOpacity, Color startStrokeColor, Color endStrokeColor, double
  // startStrokeWidth,
  // double endStrokeWidth, double strokeOpacity, double min, double max,
  // String featurePropertyName, int numberOfSteps )
  // {
  // return null;
  // }
  //
  // /**
  // * create a new default style
  // *
  // * @return the new style
  // */
  // public static Style createStyle()
  // {
  // return null;
  // }

  /**
   * transforms the color of the request from java.awt.Color to the hexadecimal representation as in an OGC conform
   * WMS-GetMap request (e.g. white == "#ffffff").
   *
   * @return the color as hexadecimal representation
   */
  public static String getColorAsHex( final Color color )
  {
    String r = Integer.toHexString( color.getRed() );
    if( r.length() < 2 )
      r = "0" + r;
    String g = Integer.toHexString( color.getGreen() );
    if( g.length() < 2 )
      g = "0" + g;
    String b = Integer.toHexString( color.getBlue() );
    if( b.length() < 2 )
      b = "0" + b;
    return "#" + r + g + b;
  }

  /**
   * creates a PolygonColorMapEntry
   *
   * @param fillColor
   *          fill color
   * @param strokeColor
   *          stroke color
   * @param fromValue
   *          from value of the entry
   * @param toValue
   *          to value of the entry
   * @return {@link PolygonColorMapEntry_Impl}
   */
  public static PolygonColorMapEntry createPolygonColorMapEntry( final Color fillColor, final Color strokeColor, final BigDecimal fromValue, final BigDecimal toValue )
  {
    // fill
    final Fill defaultFill = StyleFactory.createFill( fillColor );

    // stroke
    final Stroke defaultStroke = StyleFactory.createStroke( strokeColor );

    // parameters
    final String label = String.format( "%s - %s", fromValue.toString(), toValue.toString() );

    final ParameterValueType defaultLabel = createParameterValueType( label );
    final ParameterValueType defaultFrom = createParameterValueType( fromValue.doubleValue() );
    final ParameterValueType defaultTo = createParameterValueType( toValue.doubleValue() );

    return new PolygonColorMapEntry_Impl( defaultFill, defaultStroke, defaultLabel, defaultFrom, defaultTo );
  }

  public static Geometry createGeometry( final PropertyName geometryPropertyName )
  {
    if( geometryPropertyName == null )
      return null;

    return new Geometry_Impl( geometryPropertyName );
  }
}