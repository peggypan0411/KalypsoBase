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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import ogc2.www.opengis.net.sld.ColorMap;
import ogc2.www.opengis.net.sld.ObjectFactory;

import org.apache.commons.io.IOUtils;
import org.deegree.ogcbase.CommonNamespaces;
import org.kalypso.commons.bind.JaxbUtilities;
import org.kalypso.contribs.java.net.IUrlResolver2;
import org.kalypsodeegree.filterencoding.Expression;
import org.kalypsodeegree.filterencoding.Filter;
import org.kalypsodeegree.filterencoding.Operation;
import org.kalypsodeegree.graphics.sld.ColorMapEntry;
import org.kalypsodeegree.graphics.sld.CssParameter;
import org.kalypsodeegree.graphics.sld.Extent;
import org.kalypsodeegree.graphics.sld.ExternalGraphic;
import org.kalypsodeegree.graphics.sld.FeatureTypeConstraint;
import org.kalypsodeegree.graphics.sld.FeatureTypeStyle;
import org.kalypsodeegree.graphics.sld.Fill;
import org.kalypsodeegree.graphics.sld.Font;
import org.kalypsodeegree.graphics.sld.Geometry;
import org.kalypsodeegree.graphics.sld.Graphic;
import org.kalypsodeegree.graphics.sld.GraphicFill;
import org.kalypsodeegree.graphics.sld.GraphicStroke;
import org.kalypsodeegree.graphics.sld.Halo;
import org.kalypsodeegree.graphics.sld.LabelPlacement;
import org.kalypsodeegree.graphics.sld.Layer;
import org.kalypsodeegree.graphics.sld.LayerFeatureConstraints;
import org.kalypsodeegree.graphics.sld.LegendGraphic;
import org.kalypsodeegree.graphics.sld.LineColorMapEntry;
import org.kalypsodeegree.graphics.sld.LinePlacement;
import org.kalypsodeegree.graphics.sld.LineSymbolizer;
import org.kalypsodeegree.graphics.sld.Mark;
import org.kalypsodeegree.graphics.sld.NamedLayer;
import org.kalypsodeegree.graphics.sld.NamedStyle;
import org.kalypsodeegree.graphics.sld.ParameterValueType;
import org.kalypsodeegree.graphics.sld.PointPlacement;
import org.kalypsodeegree.graphics.sld.PointSymbolizer;
import org.kalypsodeegree.graphics.sld.PolygonColorMapEntry;
import org.kalypsodeegree.graphics.sld.PolygonSymbolizer;
import org.kalypsodeegree.graphics.sld.RasterSymbolizer;
import org.kalypsodeegree.graphics.sld.RemoteOWS;
import org.kalypsodeegree.graphics.sld.Rule;
import org.kalypsodeegree.graphics.sld.Stroke;
import org.kalypsodeegree.graphics.sld.Style;
import org.kalypsodeegree.graphics.sld.StyledLayerDescriptor;
import org.kalypsodeegree.graphics.sld.SurfaceLineSymbolizer;
import org.kalypsodeegree.graphics.sld.SurfacePolygonSymbolizer;
import org.kalypsodeegree.graphics.sld.Symbolizer;
import org.kalypsodeegree.graphics.sld.TextSymbolizer;
import org.kalypsodeegree.graphics.sld.UserLayer;
import org.kalypsodeegree.graphics.sld.UserStyle;
import org.kalypsodeegree.xml.ElementList;
import org.kalypsodeegree.xml.XMLParsingException;
import org.kalypsodeegree.xml.XMLTools;
import org.kalypsodeegree_impl.filterencoding.AbstractFilter;
import org.kalypsodeegree_impl.filterencoding.ComplexFilter;
import org.kalypsodeegree_impl.filterencoding.Expression_Impl;
import org.kalypsodeegree_impl.filterencoding.FalseFilter;
import org.kalypsodeegree_impl.filterencoding.LogicalOperation;
import org.kalypsodeegree_impl.filterencoding.OperationDefines;
import org.kalypsodeegree_impl.filterencoding.PropertyName;
import org.kalypsodeegree_impl.graphics.sld.Symbolizer_Impl.UOM;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Factory class for all mapped SLD-elements.
 * <p>
 * TODO: Default values for omitted elements (such as fill color) should better not be used in the construction of the
 * corresponding objects (Fill), but marked as left out (to make it possible to differentiate between explicitly given
 * values and default values).
 * <p>
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision$ $Date$
 */
public class SLDFactory
{
  public static final String SLDNS_EXT = "http://www.opengis.net/sldExt";

  private static String ogcNS = "http://www.opengis.net/ogc";

  private static String xlnNS = "http://www.w3.org/1999/xlink";

  /**
   * Creates a <tt>StyledLayerDescriptor</tt> -instance from the given XML-representation.
   * <p>
   *
   * @param s
   *          contains the XML document
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the XML document is encountered
   * @return the constructed <tt>StyledLayerDescriptor</tt> -instance
   */
  public static StyledLayerDescriptor createSLD( final IUrlResolver2 urlResolver, final String s ) throws XMLParsingException
  {
    final StringReader sr = new StringReader( s );

    return SLDFactory.createSLD( urlResolver, sr );
  }

  /**
   * Creates a <tt>StyledLayerDescriptor</tt> -instance from the given Reader.
   * <p>
   *
   * @param reader
   *          provides the XML document
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the XML document is encountered
   * @return the constructed <tt>StyledLayerDescriptor</tt> -instance
   * @deprecated Use the inputStream version instead: {@link #createSLD(IUrlResolver2, InputStream)}, because with
   *             reader's the xml-encoding is not handled properly.
   */
  @Deprecated
  public static StyledLayerDescriptor createSLD( final IUrlResolver2 urlResolver, final Reader reader ) throws XMLParsingException
  {
    StyledLayerDescriptor sld = null;

    try
    {
      final Document doc = XMLTools.parse( reader );
      sld = SLDFactory.createStyledLayerDescriptor( urlResolver, doc.getDocumentElement() );
    }
    catch( final IOException e )
    {
      throw new XMLParsingException( "IOException encountered while parsing SLD-Document: " + e.getMessage() );
    }
    catch( final SAXException e )
    {
      throw new XMLParsingException( "SAXException encountered while parsing SLD-Document: " + e.getMessage() );
    }

    return sld;
  }

  /**
   * Creates a <tt>StyledLayerDescriptor</tt> -instance from the given Reader.
   * <p>
   *
   * @param reader
   *          provides the XML document
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the XML document is encountered
   * @return the constructed <tt>StyledLayerDescriptor</tt> -instance
   */
  public static StyledLayerDescriptor createSLD( final IUrlResolver2 urlResolver, final InputStream is ) throws XMLParsingException
  {
    try
    {
      final Document doc = XMLTools.parse( is );
      return SLDFactory.createStyledLayerDescriptor( urlResolver, doc.getDocumentElement() );
    }
    catch( final IOException e )
    {
      throw new XMLParsingException( "IOException encountered while parsing SLD-Document: " + e.getMessage() );
    }
    catch( final SAXException e )
    {
      throw new XMLParsingException( "SAXException encountered while parsing SLD-Document: " + e.getMessage() );
    }
  }

  public static StyledLayerDescriptor createSLD( final File file ) throws IOException, XMLParsingException
  {
    final URL context = file.toURI().toURL();
    final IUrlResolver2 urlResolver = new IUrlResolver2()
    {
      public URL resolveURL( final String relativeOrAbsolute ) throws MalformedURLException
      {
        return new URL( context, relativeOrAbsolute );
      }
    };

    InputStream is = null;
    try
    {
      is = new BufferedInputStream( new FileInputStream( file ) );
      final StyledLayerDescriptor sld = createSLD( urlResolver, is );
      is.close();
      return sld;
    }
    finally
    {
      IOUtils.closeQuietly( is );
    }
  }

  public static StyledLayerDescriptor createSLD( final URL url ) throws IOException, XMLParsingException
  {
    final IUrlResolver2 urlResolver = new IUrlResolver2()
    {
      public URL resolveURL( final String relativeOrAbsolute ) throws MalformedURLException
      {
        return new URL( url, relativeOrAbsolute );
      }
    };

    InputStream is = null;
    try
    {
      is = new BufferedInputStream( url.openStream() );
      final StyledLayerDescriptor sld = createSLD( urlResolver, is );
      is.close();
      return sld;
    }
    finally
    {
      IOUtils.closeQuietly( is );
    }
  }

  public static StyledLayerDescriptor createStyledLayerDescriptor( final String name, final String title, final String abstract_, final Layer[] layers )
  {
    return new StyledLayerDescriptor_Impl( name, title, abstract_, layers );
  }

  public static StyledLayerDescriptor createStyledLayerDescriptor( final Layer[] layers )
  {
    return new StyledLayerDescriptor_Impl( layers );
  }

  /**
   * Creates a <tt>TextSymbolizer</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'TextSymbolizer'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'TextSymbolizer'- <tt>Element</tt>
   * @param min
   *          scale-constraint to be used
   * @param max
   *          scale-constraint to be used
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>TextSymbolizer</tt> -instance
   */
  private static TextSymbolizer createTextSymbolizer( final IUrlResolver2 urlResolver, final Element element, final double min, final double max, final UOM uom ) throws XMLParsingException
  {

    // optional: <Geometry>
    Geometry geometry = null;
    final Element geometryElement = XMLTools.getChildByName( "Geometry", CommonNamespaces.SLDNS.toString(), element );

    if( geometryElement != null )
    {
      geometry = SLDFactory.createGeometry( geometryElement );
    }

    // optional: <Label>
    ParameterValueType label = null;
    final Element labelElement = XMLTools.getChildByName( "Label", CommonNamespaces.SLDNS.toString(), element );

    if( labelElement != null )
    {
      label = SLDFactory.createParameterValueType( labelElement );
    }

    // optional: <Font>
    Font font = null;
    final Element fontElement = XMLTools.getChildByName( "Font", CommonNamespaces.SLDNS.toString(), element );

    if( fontElement != null )
    {
      font = SLDFactory.createFont( fontElement );
    }

    // optional: <LabelPlacement>
    LabelPlacement labelPlacement = null;
    final Element lpElement = XMLTools.getChildByName( "LabelPlacement", CommonNamespaces.SLDNS.toString(), element );

    if( lpElement != null )
    {
      labelPlacement = SLDFactory.createLabelPlacement( lpElement );
    }

    // optional: <Halo>
    Halo halo = null;
    final Element haloElement = XMLTools.getChildByName( "Halo", CommonNamespaces.SLDNS.toString(), element );

    if( haloElement != null )
    {
      halo = SLDFactory.createHalo( urlResolver, haloElement );
    }

    // optional: <Fill>
    final Fill fill = null;

    return new TextSymbolizer_Impl( geometry, label, font, labelPlacement, halo, fill, min, max, uom );
  }

  /**
   * Creates a <tt>Halo</tt> -instance according to the contents of the DOM-subtree starting at the given 'Halo'-
   * <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'Halo'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Halo</tt> -instance
   */
  private static Halo createHalo( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // optional: <Radius>
    ParameterValueType radius = null;
    final Element radiusElement = XMLTools.getChildByName( "Radius", CommonNamespaces.SLDNS.toString(), element );

    if( radiusElement != null )
    {
      radius = SLDFactory.createParameterValueType( radiusElement );
    }

    // optional: <Fill>
    Fill fill = null;
    final Element fillElement = XMLTools.getChildByName( "Fill", CommonNamespaces.SLDNS.toString(), element );

    if( fillElement != null )
    {
      fill = SLDFactory.createFill( urlResolver, fillElement );
    }

    // optional: <Stroke>
    Stroke stroke = null;
    final Element strokeElement = XMLTools.getChildByName( "Stroke", CommonNamespaces.SLDNS.toString(), element );

    if( strokeElement != null )
    {
      stroke = SLDFactory.createStroke( urlResolver, strokeElement );
    }

    return new Halo_Impl( radius, fill, stroke );
  }

  /**
   * Creates a <tt>LabelPlacement</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'LabelPlacement'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'LabelPlacement'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>LabelPlacement</tt> -instance
   */
  private static LabelPlacement createLabelPlacement( final Element element ) throws XMLParsingException
  {
    LabelPlacement labelPlacement = null;

    // required: <PointPlacement> / <LinePlacement>
    final NodeList nodelist = element.getChildNodes();
    PointPlacement pPlacement = null;
    LinePlacement lPlacement = null;

    for( int i = 0; i < nodelist.getLength(); i++ )
      if( nodelist.item( i ) instanceof Element )
      {
        final Element child = (Element) nodelist.item( i );
        final String namespace = child.getNamespaceURI();

        if( !CommonNamespaces.SLDNS.toString().equals( namespace ) )
        {
          continue;
        }

        final String childName = child.getLocalName();

        if( childName.equals( "PointPlacement" ) )
        {
          pPlacement = SLDFactory.createPointPlacement( child );
        }
        else if( childName.equals( "LinePlacement" ) )
        {
          lPlacement = SLDFactory.createLinePlacement( child );
        }
      }

    if( (pPlacement != null) && (lPlacement == null) )
    {
      labelPlacement = new LabelPlacement_Impl( pPlacement );
    }
    else if( (pPlacement == null) && (lPlacement != null) )
    {
      labelPlacement = new LabelPlacement_Impl( lPlacement );
    }
    else
      throw new XMLParsingException( "Element 'LabelPlacement' must contain exactly one " + "'PointPlacement'- or one 'LinePlacement'-element!" );

    return labelPlacement;
  }

  /**
   * Creates a <tt>PointPlacement</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'PointPlacement'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'PointPlacement'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>PointPlacement</tt> -instance
   */
  private static PointPlacement createPointPlacement( final Element element ) throws XMLParsingException
  {

    // optional: auto-Attribute (this is deegree-specific)
    boolean auto = false;
    final String autoStr = XMLTools.getAttrValue( element, "auto" );

    if( (autoStr != null) && autoStr.equals( "true" ) )
    {
      auto = true;
    }

    // optional: <AnchorPoint>
    ParameterValueType[] anchorPoint = null;
    final Element apElement = XMLTools.getChildByName( "AnchorPoint", CommonNamespaces.SLDNS.toString(), element );

    if( apElement != null )
    {
      anchorPoint = new ParameterValueType[2];

      final Element apXElement = XMLTools.getChildByName( "AnchorPointX", CommonNamespaces.SLDNS.toString(), apElement );
      final Element apYElement = XMLTools.getChildByName( "AnchorPointY", CommonNamespaces.SLDNS.toString(), apElement );

      if( (apXElement == null) || (apYElement == null) )
        throw new XMLParsingException( "Element 'AnchorPoint' must contain exactly one " + "'AnchorPointX'- and one 'AnchorPointY'-element!" );

      anchorPoint[0] = SLDFactory.createParameterValueType( apXElement );
      anchorPoint[1] = SLDFactory.createParameterValueType( apYElement );
    }

    // optional: <Displacement>
    ParameterValueType[] displacement = null;
    final Element dElement = XMLTools.getChildByName( "Displacement", CommonNamespaces.SLDNS.toString(), element );

    if( dElement != null )
    {
      displacement = new ParameterValueType[2];

      final Element dXElement = XMLTools.getChildByName( "DisplacementX", CommonNamespaces.SLDNS.toString(), dElement );
      final Element dYElement = XMLTools.getChildByName( "DisplacementY", CommonNamespaces.SLDNS.toString(), dElement );

      if( (dXElement == null) || (dYElement == null) )
        throw new XMLParsingException( "Element 'Displacement' must contain exactly one " + "'DisplacementX'- and one 'DisplacementY'-element!" );

      displacement[0] = SLDFactory.createParameterValueType( dXElement );
      displacement[1] = SLDFactory.createParameterValueType( dYElement );
    }

    // optional: <Rotation>
    ParameterValueType rotation = null;
    final Element rElement = XMLTools.getChildByName( "Rotation", CommonNamespaces.SLDNS.toString(), element );

    if( rElement != null )
    {
      rotation = SLDFactory.createParameterValueType( rElement );
    }

    return new PointPlacement_Impl( anchorPoint, displacement, rotation, auto );
  }

  /**
   * Creates a <tt>LinePlacement</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'LinePlacement'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'LinePlacement'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>LinePlacement</tt> -instance
   */
  private static LinePlacement createLinePlacement( final Element element ) throws XMLParsingException
  {

    // optional: <PerpendicularOffset>
    ParameterValueType pOffset = null;
    final Element pOffsetElement = XMLTools.getChildByName( "PerpendicularOffset", CommonNamespaces.SLDNS.toString(), element );

    if( pOffsetElement != null )
    {
      pOffset = SLDFactory.createParameterValueType( pOffsetElement );
    }

    // optional: <Gap> (this is deegree-specific)
    ParameterValueType gap = null;
    final Element gapElement = XMLTools.getChildByName( "Gap", CommonNamespaces.SLDNS.toString(), element );

    if( gapElement != null )
    {
      gap = SLDFactory.createParameterValueType( gapElement );
    }

    // optional: <LineWidth> (this is deegree-specific)
    ParameterValueType lineWidth = null;
    final Element lineWidthElement = XMLTools.getChildByName( "LineWidth", CommonNamespaces.SLDNS.toString(), element );

    if( lineWidthElement != null )
    {
      lineWidth = SLDFactory.createParameterValueType( lineWidthElement );
    }

    return new LinePlacement_Impl( pOffset, lineWidth, gap );
  }

  /**
   * Creates a <tt>Font</tt> -instance according to the contents of the DOM-subtree starting at the given 'Font'-
   * <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'Font'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Font</tt> -instance
   */
  private static Font createFont( final Element element ) throws XMLParsingException
  {

    // optional: <CssParameter>s
    final ElementList nl = XMLTools.getChildElementsByName( "CssParameter", CommonNamespaces.SLDNS.toString(), element );
    final HashMap<String, CssParameter> cssParams = new HashMap<String, CssParameter>( nl.getLength() );

    for( int i = 0; i < nl.getLength(); i++ )
    {
      final CssParameter cssParam = SLDFactory.createCssParameter( nl.item( i ) );
      cssParams.put( cssParam.getName(), cssParam );
    }

    return new Font_Impl( cssParams );
  }

  /**
   * Creates a <tt>ParameterValueType</tt> -instance according to the contents of the DOM-subtree starting at the given
   * <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the <tt>Element</tt> (must be of the type sld:ParameterValueType)
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>ParameterValueType</tt> -instance
   */
  private static ParameterValueType createParameterValueType( final Element element ) throws XMLParsingException
  {
    if( element == null )
      return null;

    // mix of text nodes and <wfs:Expression>-elements
    final List<Object> componentList = new ArrayList<Object>();
    final NodeList nl = element.getChildNodes();

    for( int i = 0; i < nl.getLength(); i++ )
    {
      final Node node = nl.item( i );

      switch( node.getNodeType() )
      {
        case Node.TEXT_NODE:
        {
          componentList.add( node.getNodeValue() );
          break;
        }
        case Node.ELEMENT_NODE:
        {
          final Expression expression = Expression_Impl.buildFromDOM( (Element) node );
          componentList.add( expression );
          break;
        }
        default:
          throw new XMLParsingException( "Elements of type 'ParameterValueType' may only " + "consist of CDATA and 'ogc:Expression'-elements!" );
      }
    }

    final Object[] components = componentList.toArray( new Object[componentList.size()] );
    return new ParameterValueType_Impl( components );
  }

  /**
   * Creates a <tt>StyledLayerDescriptor</tt> -instance according to the contents of the DOM-subtree starting at the
   * given 'StyledLayerDescriptor'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'StyledLayerDescriptor'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>StyledLayerDescriptor</tt> -instance
   */
  public static StyledLayerDescriptor createStyledLayerDescriptor( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // optional: <Name>
    final String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS.toString(), element, null );

    // optional: <Title>
    final String title = XMLTools.getStringValue( "Title", CommonNamespaces.SLDNS.toString(), element, null );
    // optional: <Abstract>
    final String abstract_ = XMLTools.getStringValue( "Abstract", CommonNamespaces.SLDNS.toString(), element, null );
    // required: version-Attribute
    // final String version = XMLTools.getRequiredAttrValue( "version", element );
    // TODO: check for correct version here...; must be "1.0.0", but i have seen many wrong .sld's

    // optional: <NamedLayer>(s) / <UserLayer>(s)
    final NodeList nodelist = element.getChildNodes();
    final List<Layer> layerList = new ArrayList<Layer>( 100 );

    for( int i = 0; i < nodelist.getLength(); i++ )
      if( nodelist.item( i ) instanceof Element )
      {
        final Element child = (Element) nodelist.item( i );
        final String namespace = child.getNamespaceURI();

        if( !CommonNamespaces.SLDNS.toString().equals( namespace ) )
        {
          continue;
        }

        final String childName = child.getLocalName();

        if( childName.equals( "NamedLayer" ) )
        {
          layerList.add( SLDFactory.createNamedLayer( urlResolver, child ) );
        }
        else if( childName.equals( "UserLayer" ) )
        {
          layerList.add( SLDFactory.createUserLayer( urlResolver, child ) );
        }
      }

    final Layer[] layers = layerList.toArray( new Layer[layerList.size()] );


    // TODO: add i18n stuff here
    // Reading the bundle here would be nice, but what to do with it...
    // Either we resolve the localized strings while parsing the sld (but then the resolved strings get written back to
    // the sld)
    // or we have to give the reference to the bundle to all sub-element (Rules, Symbolizers, ...)
// ResourceBundle sldBundle = null;
// try
// {
// final URL resourceBundleURL = urlResolver.resolveURL( "" );
// sldBundle = ResourceBundleUtils.loadResourceBundle( resourceBundleURL );
// }
// catch( final MalformedURLException e )
// {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }

    return new StyledLayerDescriptor_Impl( name, title, abstract_, layers );
  }

  /**
   * Creates a <tt>NamedStyle</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'NamedStyle'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'NamedStyle'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>NamedStyle</tt> -instance
   */
  private static NamedStyle createNamedStyle( final Element element ) throws XMLParsingException
  {
    // required: <Name>
    final String name = XMLTools.getRequiredStringValue( "Name", CommonNamespaces.SLDNS.toString(), element );

    return new NamedStyle_Impl( name );
  }

  /**
   *
   */
  public static NamedStyle createNamedStyle( final String name )
  {
    return new NamedStyle_Impl( name );
  }

  /**
   * Creates a <tt>RemoteOWS</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'RemoteOWS'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'RemoteOWS'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>RemoteOWS</tt> -instance
   */
  private static RemoteOWS createRemoteOWS( final Element element ) throws XMLParsingException
  {
    // required: <Service>
    final String service = XMLTools.getRequiredStringValue( "Service", CommonNamespaces.SLDNS.toString(), element );

    if( !(service.equals( "WFS" ) || service.equals( "WCS" )) )
      throw new XMLParsingException( "Value ('" + service + "') of element 'service' is invalid. " + "Allowed values are: 'WFS' and 'WCS'." );

    // required: <OnlineResource>
    final Element onlineResourceElement = XMLTools.getRequiredChildByName( "OnlineResource", CommonNamespaces.SLDNS.toString(), element );
    final String href = XMLTools.getRequiredAttrValue( "xlink:href", onlineResourceElement );
    URL url = null;

    try
    {
      url = new URL( href );
    }
    catch( final MalformedURLException e )
    {
      throw new XMLParsingException( "Value ('" + href + "') of attribute 'href' of " + "element 'OnlineResoure' does not denote a valid URL: " + e.getMessage() );
    }

    return new RemoteOWS_Impl( service, url );
  }

  /**
   * Creates a <tt>NamedLayer</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'UserLayer'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'NamedLayer'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>NamedLayer</tt> -instance
   */
  private static NamedLayer createNamedLayer( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // required: <Name>
    final String name = XMLTools.getRequiredStringValue( "Name", CommonNamespaces.SLDNS.toString(), element );

    // optional: <LayerFeatureConstraints>
    LayerFeatureConstraints lfc = null;
    final Element lfcElement = XMLTools.getChildByName( "LayerFeatureConstraints", CommonNamespaces.SLDNS.toString(), element );

    if( lfcElement != null )
    {
      lfc = SLDFactory.createLayerFeatureConstraints( lfcElement );
    }

    // optional: <NamedStyle>(s) / <UserStyle>(s)
    final NodeList nodelist = element.getChildNodes();
    final List<Style> styleList = new ArrayList<Style>();

    for( int i = 0; i < nodelist.getLength(); i++ )
      if( nodelist.item( i ) instanceof Element )
      {
        final Element child = (Element) nodelist.item( i );
        final String namespace = child.getNamespaceURI();

        if( !CommonNamespaces.SLDNS.toString().equals( namespace ) )
        {
          continue;
        }

        final String childName = child.getLocalName();

        if( childName.equals( "NamedStyle" ) )
        {
          styleList.add( SLDFactory.createNamedStyle( child ) );
        }
        else if( childName.equals( "UserStyle" ) )
        {
          styleList.add( SLDFactory.createUserStyle( urlResolver, child ) );
        }
      }

    final Style[] styles = styleList.toArray( new Style[styleList.size()] );

    return new NamedLayer_Impl( name, lfc, styles );
  }

  /**
   *
   */
  public static NamedLayer createNamedLayer( final String name, final LayerFeatureConstraints layerFeatureConstraints, final Style[] styles )
  {
    return new NamedLayer_Impl( name, layerFeatureConstraints, styles );
  }

  /**
   * Creates a <tt>UserLayer</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'UserLayer'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'UserLayer'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>UserLayer</tt> -instance
   */
  private static UserLayer createUserLayer( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // optional: <Name>
    final String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS.toString(), element, null );

    // optional: <RemoteOWS>
    RemoteOWS remoteOWS = null;
    final Element remoteOWSElement = XMLTools.getChildByName( "RemoteOWS", CommonNamespaces.SLDNS.toString(), element );

    if( remoteOWSElement != null )
    {
      remoteOWS = SLDFactory.createRemoteOWS( remoteOWSElement );
    }

    // required: <LayerFeatureConstraints>
    LayerFeatureConstraints lfc = null;
    final Element lfcElement = XMLTools.getRequiredChildByName( "LayerFeatureConstraints", CommonNamespaces.SLDNS.toString(), element );
    lfc = SLDFactory.createLayerFeatureConstraints( lfcElement );

    // optional: <UserStyle>(s)
    final ElementList nodelist = XMLTools.getChildElementsByName( "UserStyle", CommonNamespaces.SLDNS.toString(), element );
    final UserStyle[] styles = new UserStyle[nodelist.getLength()];

    for( int i = 0; i < nodelist.getLength(); i++ )
    {
      styles[i] = SLDFactory.createUserStyle( urlResolver, nodelist.item( i ) );
    }

    return new UserLayer_Impl( name, lfc, styles, remoteOWS );
  }

  /**
   * Creates a <tt>FeatureTypeConstraint</tt> -instance according to the contents of the DOM-subtree starting at the
   * given 'FeatureTypeConstraint'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'FeatureTypeConstraint'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>FeatureTypeConstraint</tt> -instance
   */
  private static FeatureTypeConstraint createFeatureTypeConstraint( final Element element ) throws XMLParsingException
  {
    // optional: <Name>
    final String name = XMLTools.getStringValue( "FeatureTypeName", CommonNamespaces.SLDNS.toString(), element, null );

    // optional: <Filter>
    Filter filter = null;
    final Element filterElement = XMLTools.getChildByName( "Filter", SLDFactory.ogcNS, element );

    if( filterElement != null )
    {
      filter = AbstractFilter.buildFromDOM( filterElement );
    }

    // optional: <Extent>(s)
    final ElementList nodelist = XMLTools.getChildElementsByName( "Extent", CommonNamespaces.SLDNS.toString(), element );
    final Extent[] extents = new Extent[nodelist.getLength()];

    for( int i = 0; i < nodelist.getLength(); i++ )
    {
      extents[i] = SLDFactory.createExtent( nodelist.item( i ) );
    }

    return new FeatureTypeConstraint_Impl( name, filter, extents );
  }

  /**
   * Creates an <tt>Extent</tt> -instance according to the contents of the DOM-subtree starting at the given 'Extent'-
   * <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'Extent'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Extent</tt> -instance
   */
  private static Extent createExtent( final Element element ) throws XMLParsingException
  {
    // required: <Name>
    final String name = XMLTools.getRequiredStringValue( "Name", CommonNamespaces.SLDNS.toString(), element );
    // required: <Value>
    final String value = XMLTools.getRequiredStringValue( "Value", CommonNamespaces.SLDNS.toString(), element );

    return new Extent_Impl( name, value );
  }

  /**
   * Creates a <tt>LayerFeatureConstraints</tt> -instance according to the contents of the DOM-subtree starting at the
   * given 'LayerFeatureConstraints'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'LayerFeatureConstraints'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>LayerFeatureConstraints</tt> -instance
   */
  public static LayerFeatureConstraints createLayerFeatureConstraints( final Element element ) throws XMLParsingException
  {
    // required: <FeatureTypeConstraint>(s)
    final ElementList nodelist = XMLTools.getChildElementsByName( "FeatureTypeConstraint", CommonNamespaces.SLDNS.toString(), element );
    final FeatureTypeConstraint[] ftcs = new FeatureTypeConstraint[nodelist.getLength()];

    for( int i = 0; i < nodelist.getLength(); i++ )
    {
      ftcs[i] = SLDFactory.createFeatureTypeConstraint( nodelist.item( i ) );
    }

    return new LayerFeatureConstraints_Impl( ftcs );
  }

  /**
   * Creates a <tt>UserStyle</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'UserStyle'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'UserStyle'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>UserStyle</tt> -instance
   */
  private static UserStyle createUserStyle( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // optional: <Name>
    final String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS.toString(), element, null );
    // optional: <Title>
    final String title = XMLTools.getStringValue( "Title", CommonNamespaces.SLDNS.toString(), element, null );
    // optional: <Abstract>
    final String abstract_ = XMLTools.getStringValue( "Abstract", CommonNamespaces.SLDNS.toString(), element, null );

    // optional: <IsDefault>
    final String defaultString = XMLTools.getStringValue( "IsDefault", CommonNamespaces.SLDNS.toString(), element, null );
    boolean isDefault = false;

    if( defaultString != null )
      if( defaultString.equals( "1" ) )
      {
        isDefault = true;
      }

    // required: <FeatureTypeStyle> (s)
    final ElementList nl = XMLTools.getChildElementsByName( "FeatureTypeStyle", CommonNamespaces.SLDNS.toString(), element );
    final FeatureTypeStyle[] styles = new FeatureTypeStyle[nl.getLength()];

    if( styles.length == 0 )
      throw new XMLParsingException( "Required child-element 'FeatureTypeStyle' of element " + "'UserStyle' is missing!" );

    for( int i = 0; i < nl.getLength(); i++ )
    {
      styles[i] = SLDFactory.createFeatureTypeStyle( urlResolver, nl.item( i ) );
    }

    return StyleFactory.createUserStyle( name, title, abstract_, isDefault, styles );
  }

  /**
   * Creates a <tt>FeatureTypeStyle</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'FeatureTypeStyle'- <tt>Element</tt>.
   * <p>
   * TODO: The ElseFilter currently does not work correctly with FeatureFilters.
   * <p>
   *
   * @param element
   *          the 'FeatureTypeStyle'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>FeatureTypeStyle</tt> -instance
   */
  public static FeatureTypeStyle createFeatureTypeStyle( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // optional: <Name>
    final String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS.toString(), element, null );
    // optional: <Title>
    final String title = XMLTools.getStringValue( "Title", CommonNamespaces.SLDNS.toString(), element, null );
    // optional: <Abstract>
    final String abstract_ = XMLTools.getStringValue( "Abstract", CommonNamespaces.SLDNS.toString(), element, null );
    // optional: <FeatureTypeName>
    final QName featureTypeQName = XMLTools.getQNameValue( "FeatureTypeName", CommonNamespaces.SLDNS.toString(), element );
    // final String featureTypeName = XMLTools.getStringValue( "FeatureTypeName", CommonNamespaces.SLDNS.toString(),
    // element, null
    // );
    // if( featureTypeQName == null && featureTypeName != null )
    // featureTypeQName = new QName( featureTypeName );

    // optional: several <Rule> / <SemanticTypeIdentifier>
    final NodeList nodelist = element.getChildNodes();
    final List<Rule> ruleList = new ArrayList<Rule>();
    final List<String> typeIdentifierList = new ArrayList<String>();

    // collect Filters of all Rules
    final List<Filter> filters = new ArrayList<Filter>();
    // collect all Rules that have an ElseFilter
    final List<Rule> elseRules = new ArrayList<Rule>();

    for( int i = 0; i < nodelist.getLength(); i++ )
    {
      final Node item = nodelist.item( i );
      if( item instanceof Element )
      {
        final Element child = (Element) item;
        final String namespace = child.getNamespaceURI();

        if( !CommonNamespaces.SLDNS.toString().equals( namespace ) )
        {
          continue;
        }

        final String childName = child.getLocalName();

        if( childName.equals( "Rule" ) )
        {
          final Rule rule = SLDFactory.createRule( urlResolver, child );
          if( rule.hasElseFilter() )
          {
            elseRules.add( rule );
          }
          else if( (rule.getFilter() == null) || (rule.getFilter() instanceof ComplexFilter) )
          {
            filters.add( rule.getFilter() );
          }
          ruleList.add( rule );
        }
        else if( childName.equals( "SemanticTypeIdentifier" ) )
        {
          typeIdentifierList.add( XMLTools.getStringValue( child ) );
        }
      }
    }

    // compute and set the ElseFilter for all ElseFilter-Rules
    Filter elseFilter = null;
    // a Rule exists with no Filter at all -> elseFilter = false
    if( filters.contains( null ) )
    {
      elseFilter = new FalseFilter();
    }
    else if( filters.size() == 1 )
    {
      elseFilter = new ComplexFilter( OperationDefines.NOT );
      final List<Operation> arguments = ((LogicalOperation) ((ComplexFilter) elseFilter).getOperation()).getArguments();
      final ComplexFilter complexFilter = (ComplexFilter) filters.get( 0 );
      arguments.add( complexFilter.getOperation() );
    }
    else if( filters.size() > 1 )
    {
      // several Rules with Filters exist -> elseFilter = NOT (Filter1 OR Filter2 OR...)
      final ComplexFilter innerFilter = new ComplexFilter( OperationDefines.OR );
      elseFilter = new ComplexFilter( innerFilter, null, OperationDefines.NOT );
      final List<Operation> arguments = ((LogicalOperation) innerFilter.getOperation()).getArguments();

      for( final Filter complexFilter : filters )
      {
        arguments.add( ((ComplexFilter) complexFilter).getOperation() );
      }
    }

    for( final Rule elseRule : elseRules )
    {
      elseRule.setFilter( elseFilter );
    }

    final Rule[] rules = ruleList.toArray( new Rule[ruleList.size()] );
    final String[] typeIdentifiers = typeIdentifierList.toArray( new String[typeIdentifierList.size()] );
    return StyleFactory.createFeatureTypeStyle( name, title, abstract_, featureTypeQName, typeIdentifiers, rules );
  }

  /**
   * Creates a <tt>Rule</tt> -instance according to the contents of the DOM-subtree starting at the given 'Rule'-
   * <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'Rule'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Rule</tt> -instance
   */
  private static Rule createRule( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // optional: <Name>
    final String name = XMLTools.getStringValue( "Name", CommonNamespaces.SLDNS.toString(), element, null );
    // optional: <Title>
    final String title = XMLTools.getStringValue( "Title", CommonNamespaces.SLDNS.toString(), element, null );
    // optional: <Abstract>
    final String abstract_ = XMLTools.getStringValue( "Abstract", CommonNamespaces.SLDNS.toString(), element, null );

    // optional: <LegendGraphic>
    LegendGraphic legendGraphic = null;
    final Element legendGraphicElement = XMLTools.getChildByName( "LegendGraphic", CommonNamespaces.SLDNS.toString(), element );

    if( legendGraphicElement != null )
    {
      legendGraphic = SLDFactory.createLegendGraphic( urlResolver, legendGraphicElement );
    }

    // optional: <Filter>
    boolean isAnElseFilter = false;
    Filter filter = null;
    final Element filterElement = XMLTools.getChildByName( "Filter", SLDFactory.ogcNS, element );
    if( filterElement != null )
    {
      filter = AbstractFilter.buildFromDOM( filterElement );
    }

    // optional: <ElseFilter>
    final Element elseFilterElement = XMLTools.getChildByName( "ElseFilter", CommonNamespaces.SLDNS.toString(), element );
    if( elseFilterElement != null )
    {
      isAnElseFilter = true;
    }

    if( (filterElement != null) && (elseFilterElement != null) )
      throw new XMLParsingException( "Element 'Rule' may contain a 'Filter'- or " + "an 'ElseFilter'-element, but not both!" );

    // optional: <MinScaleDenominator>
    final double min = XMLTools.getDoubleValue( "MinScaleDenominator", CommonNamespaces.SLDNS.toString(), element, 0.0 );
    // optional: <MaxScaleDenominator>
    final double max = XMLTools.getDoubleValue( "MaxScaleDenominator", CommonNamespaces.SLDNS.toString(), element, 9E99 );

    // optional: different Symbolizer-elements
    final NodeList symbolizerNL = element.getChildNodes();
    final List<Symbolizer> symbolizerList = new ArrayList<Symbolizer>();

    for( int i = 0; i < symbolizerNL.getLength(); i++ )
    {
      final Node item = symbolizerNL.item( i );
      if( item instanceof Element )
      {
        final Symbolizer symbolizer = createSymbolizer( urlResolver, (Element) item, min, max );
        if( symbolizer != null )
          symbolizerList.add( symbolizer );
      }
    }

    final Symbolizer[] symbolizers = symbolizerList.toArray( new Symbolizer[symbolizerList.size()] );
    return StyleFactory.createRule( symbolizers, name, title, abstract_, legendGraphic, filter, isAnElseFilter, min, max );
  }

  public static Symbolizer createSymbolizer( final IUrlResolver2 urlResolver, final Element symbolizerElement, final double min, final double max ) throws XMLParsingException
  {
    final String namespace = symbolizerElement.getNamespaceURI();

    if( !(CommonNamespaces.SLDNS.toString().equals( namespace ) || SLDNS_EXT.equals( namespace )) )
      return null;

    /*
     * In reference to Symbology Encoding specification (1.1.0), we read an extra 'uom' attribute from the
     * Symbology-Elements. In SLD 1.0.0 this is not yet supported, always 'pixel' is assumed.
     */
    final UOM uom;
    if( symbolizerElement.hasAttribute( "uom" ) )
      uom = UOM.valueOf( symbolizerElement.getAttribute( "uom" ) );
    else
      uom = UOM.pixel;

    final String symbolizerName = symbolizerElement.getLocalName();

    if( symbolizerName.equals( "LineSymbolizer" ) )
      return SLDFactory.createLineSymbolizer( urlResolver, symbolizerElement, min, max, uom );

    if( symbolizerName.equals( "PointSymbolizer" ) )
      return SLDFactory.createPointSymbolizer( urlResolver, symbolizerElement, min, max, uom );

    if( symbolizerName.equals( "PolygonSymbolizer" ) )
      return SLDFactory.createPolygonSymbolizer( urlResolver, symbolizerElement, min, max, uom );

    if( symbolizerName.equals( "TextSymbolizer" ) )
      return SLDFactory.createTextSymbolizer( urlResolver, symbolizerElement, min, max, uom );

    if( symbolizerName.equals( "RasterSymbolizer" ) )
      return SLDFactory.createRasterSymbolizer( urlResolver, symbolizerElement, min, max, uom );

    if( symbolizerName.equals( "SurfaceLineSymbolizer" ) )
      return SLDFactory.createSurfaceLineSymbolizer( urlResolver, symbolizerElement, min, max, uom );

    if( symbolizerName.equals( "SurfacePolygonSymbolizer" ) )
      return SLDFactory.createSurfacePolygonSymbolizer( urlResolver, symbolizerElement, min, max, uom );

    return null;
  }

  /**
   * Creates a <tt>PointSymbolizer</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'PointSymbolizer'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'PointSymbolizer'- <tt>Element</tt>
   * @param min
   *          scale-constraint to be used
   * @param max
   *          scale-constraint to be used
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>PointSymbolizer</tt> -instance
   */
  private static PointSymbolizer createPointSymbolizer( final IUrlResolver2 urlResolver, final Element element, final double min, final double max, final UOM uom ) throws XMLParsingException
  {
    // optional: <Geometry>
    Geometry geometry = null;
    final Element geometryElement = XMLTools.getChildByName( "Geometry", CommonNamespaces.SLDNS.toString(), element );

    if( geometryElement != null )
    {
      geometry = SLDFactory.createGeometry( geometryElement );
    }

    // optional: <Graphic>
    Graphic graphic = null;
    final Element graphicElement = XMLTools.getChildByName( "Graphic", CommonNamespaces.SLDNS.toString(), element );

    if( graphicElement != null )
    {
      graphic = SLDFactory.createGraphic( urlResolver, graphicElement );
    }

    return new PointSymbolizer_Impl( graphic, geometry, min, max, uom );
  }

  /**
   * Creates a <tt>LineSymbolizer</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'LineSymbolizer'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'LineSymbolizer'- <tt>Element</tt>
   * @param min
   *          scale-constraint to be used
   * @param max
   *          scale-constraint to be used
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>LineSymbolizer</tt> -instance
   */
  private static LineSymbolizer createLineSymbolizer( final IUrlResolver2 urlResolver, final Element element, final double min, final double max, final UOM uom ) throws XMLParsingException
  {
    // optional: <Geometry>
    Geometry geometry = null;
    final Element geometryElement = XMLTools.getChildByName( "Geometry", CommonNamespaces.SLDNS.toString(), element );

    if( geometryElement != null )
    {
      geometry = SLDFactory.createGeometry( geometryElement );
    }

    // optional: <Stroke>
    Stroke stroke = null;
    final Element strokeElement = XMLTools.getChildByName( "Stroke", CommonNamespaces.SLDNS.toString(), element );

    if( strokeElement != null )
    {
      stroke = SLDFactory.createStroke( urlResolver, strokeElement );
    }

    return new LineSymbolizer_Impl( stroke, geometry, min, max, uom );
  }

  /**
   * Creates a <tt>PolygonSymbolizer</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'PolygonSymbolizer'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'PolygonSymbolizer'- <tt>Element</tt>
   * @param min
   *          scale-constraint to be used
   * @param max
   *          scale-constraint to be used
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>PolygonSymbolizer</tt> -instance
   */
  private static PolygonSymbolizer createPolygonSymbolizer( final IUrlResolver2 urlResolver, final Element element, final double min, final double max, final UOM uom ) throws XMLParsingException
  {
    // optional: <Geometry>
    Geometry geometry = null;
    final Element geometryElement = XMLTools.getChildByName( "Geometry", CommonNamespaces.SLDNS.toString(), element );

    if( geometryElement != null )
    {
      geometry = SLDFactory.createGeometry( geometryElement );
    }

    // optional: <Fill>
    Fill fill = null;
    final Element fillElement = XMLTools.getChildByName( "Fill", CommonNamespaces.SLDNS.toString(), element );

    if( fillElement != null )
    {
      fill = SLDFactory.createFill( urlResolver, fillElement );
    }

    // optional: <Stroke>
    Stroke stroke = null;
    final Element strokeElement = XMLTools.getChildByName( "Stroke", CommonNamespaces.SLDNS.toString(), element );

    if( strokeElement != null )
    {
      stroke = SLDFactory.createStroke( urlResolver, strokeElement );
    }

    return new PolygonSymbolizer_Impl( fill, stroke, geometry, min, max, uom );
  }

  /**
   * Creates a <tt>SurfaceLineSymbolizer</tt> -instance according to the contents of the DOM-subtree starting at the
   * given 'SurfaceLineSymbolizer'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'SurfaceLineSymbolizer'- <tt>Element</tt>
   * @param min
   *          scale-constraint to be used
   * @param max
   *          scale-constraint to be used
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>SurfaceLineSymbolizer</tt> -instance
   */
  private static SurfaceLineSymbolizer createSurfaceLineSymbolizer( final IUrlResolver2 urlResolver, final Element element, final double min, final double max, final UOM uom ) throws XMLParsingException
  {
    // optional: <Geometry>
    Geometry geometry = null;
    final Element geometryElement = XMLTools.getChildByName( "Geometry", CommonNamespaces.SLDNS.toString(), element );
    if( geometryElement != null )
    {
      geometry = SLDFactory.createGeometry( geometryElement );
    }

    // <LineColorMap>
    final Element colorMapElement = XMLTools.getChildByName( "LineColorMap", SLDNS_EXT, element );

    if( colorMapElement == null )
      throw new XMLParsingException( "Missing required 'LineColorMap' element" );

    final LineColorMap colorMap = createLineColorMap( urlResolver, colorMapElement );

    return new SurfaceLineSymbolizer_Impl( colorMap, geometry, min, max, uom );
  }

  /**
   * Creates a <tt>SurfacePolygonSymbolizer</tt> -instance according to the contents of the DOM-subtree starting at the
   * given 'SurfaceLineSymbolizer'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'SurfacePolygonSymbolizer'- <tt>Element</tt>
   * @param min
   *          scale-constraint to be used
   * @param max
   *          scale-constraint to be used
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>SurfacePolygonSymbolizer</tt> -instance
   */
  private static SurfacePolygonSymbolizer createSurfacePolygonSymbolizer( final IUrlResolver2 urlResolver, final Element element, final double min, final double max, final UOM uom ) throws XMLParsingException
  {
    // optional: <Geometry>
    Geometry geometry = null;
    final Element geometryElement = XMLTools.getChildByName( "Geometry", CommonNamespaces.SLDNS.toString(), element );
    if( geometryElement != null )
    {
      geometry = SLDFactory.createGeometry( geometryElement );
    }

    // <PolygonColorMap>
    final Element colorMapElement = XMLTools.getChildByName( "PolygonColorMap", SLDNS_EXT, element );

    if( colorMapElement == null )
      throw new XMLParsingException( "Missing required 'PolygonColorMap' element" );

    final PolygonColorMap colorMap = createPolygonColorMap( urlResolver, colorMapElement );

    return new SurfacePolygonSymbolizer_Impl( colorMap, geometry, min, max, uom );
  }

  private static PolygonColorMap createPolygonColorMap( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // <PolygonColorMapEntry>
    final ElementList colorMapEntryElementList = XMLTools.getChildElementsByName( "PolygonColorMapEntry", SLDNS_EXT, element );

    if( colorMapEntryElementList == null )
      throw new XMLParsingException( "Missing required 'PolygonColorMapEntry' element" );

    final PolygonColorMap colorMap = new PolygonColorMap_Impl();

    for( int i = 0; i < colorMapEntryElementList.getLength(); i++ )
    {
      final Element item = colorMapEntryElementList.item( i );
      final PolygonColorMapEntry colorMapEntry = createPolygonColorMapEntry( urlResolver, item );

      colorMap.addColorMapClass( colorMapEntry );
    }

    return colorMap;
  }

  private static LineColorMap createLineColorMap( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // <LineColorMapEntry>
    final ElementList colorMapEntryElementList = XMLTools.getChildElementsByName( "LineColorMapEntry", SLDNS_EXT, element );

    if( colorMapEntryElementList == null )
      throw new XMLParsingException( "Missing required 'LineColorMapEntry' element" );

    final LineColorMap colorMap = new LineColorMap_Impl();

    for( int i = 0; i < colorMapEntryElementList.getLength(); i++ )
    {
      final Element item = colorMapEntryElementList.item( i );
      final LineColorMapEntry colorMapEntry = createLineColorMapEntry( urlResolver, item );

      colorMap.addColorMapClass( colorMapEntry );
    }

    return colorMap;
  }

  private static PolygonColorMapEntry createPolygonColorMapEntry( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // <PolygonColorMapEntry>
// final Element colorMapEntryElement = XMLTools.getChildByName( "PolygonColorMapEntry", SLDNS_EXT, element );
//
// if( colorMapEntryElement == null )
// throw new XMLParsingException( "Missing required 'PolygonColorMapEntry' element" );

    // <Fill>
    Fill fill = null;
    final Element fillElement = XMLTools.getChildByName( "Fill", CommonNamespaces.SLDNS.toString(), element );

    if( fillElement != null )
    {
      fill = SLDFactory.createFill( urlResolver, fillElement );
    }

    // <Stroke>
    Stroke stroke = null;
    final Element strokeElement = XMLTools.getChildByName( "Stroke", CommonNamespaces.SLDNS.toString(), element );

    if( strokeElement != null )
    {
      stroke = SLDFactory.createStroke( urlResolver, strokeElement );
    }

    // <label>
    final Element labelElt = XMLTools.getChildByName( "label", SLDNS_EXT, element );
    final ParameterValueType label = createParameterValueType( labelElt );

    // <from>
    final Element fromElt = XMLTools.getChildByName( "from", SLDNS_EXT, element );
    final ParameterValueType from = createParameterValueType( fromElt );

    // <to>
    final Element toElt = XMLTools.getChildByName( "to", SLDNS_EXT, element );
    final ParameterValueType to = createParameterValueType( toElt );

    final PolygonColorMapEntry colorMapEntry = new PolygonColorMapEntry_Impl( fill, stroke, label, from, to );

    return colorMapEntry;
  }

  private static LineColorMapEntry createLineColorMapEntry( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // <Stroke>
    Stroke stroke = null;
    final Element strokeElement = XMLTools.getChildByName( "Stroke", CommonNamespaces.SLDNS.toString(), element );

    if( strokeElement != null )
    {
      stroke = SLDFactory.createStroke( urlResolver, strokeElement );
    }

    // <label>
    final Element labelElt = XMLTools.getChildByName( "label", SLDNS_EXT, element );
    final ParameterValueType label = createParameterValueType( labelElt );

    // <quantity>
    final Element quantityElt = XMLTools.getChildByName( "quantity", SLDNS_EXT, element );
    final ParameterValueType quantity = createParameterValueType( quantityElt );

    final LineColorMapEntry colorMapEntry = new LineColorMapEntry_Impl( stroke, label, quantity );

    return colorMapEntry;
  }

  /**
   * Creates a <tt>Geometry</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'Geometry'- <tt>Element</tt>.
   * <p>
   * FIXME: Add support for 'Function'-Elements.
   * <p>
   *
   * @param element
   *          the 'Geometry'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Geometry</tt> -instance
   */
  private static Geometry createGeometry( final Element element ) throws XMLParsingException
  {
    final Element propertyNameElement = XMLTools.getRequiredChildByName( "PropertyName", SLDFactory.ogcNS, element );
    final PropertyName propertyName = (PropertyName) PropertyName.buildFromDOM( propertyNameElement );
    return new Geometry_Impl( propertyName );
  }

  /**
   * Creates a <tt>Fill</tt> -instance according to the contents of the DOM-subtree starting at the given 'Fill'-
   * <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'Fill'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Fill</tt> -instance
   */
  private static Fill createFill( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // optional: <GraphicFill>
    GraphicFill graphicFill = null;
    final Element graphicFillElement = XMLTools.getChildByName( "GraphicFill", CommonNamespaces.SLDNS.toString(), element );

    if( graphicFillElement != null )
    {
      graphicFill = SLDFactory.createGraphicFill( urlResolver, graphicFillElement );
    }

    // optional: <CssParameter>s
    final ElementList nl = XMLTools.getChildElementsByName( "CssParameter", CommonNamespaces.SLDNS.toString(), element );
    final HashMap<String, CssParameter> cssParams = new HashMap<String, CssParameter>( nl.getLength() );

    for( int i = 0; i < nl.getLength(); i++ )
    {
      final CssParameter cssParam = SLDFactory.createCssParameter( nl.item( i ) );
      cssParams.put( cssParam.getName(), cssParam );
    }

    return new Fill_Impl( cssParams, graphicFill );
  }

  /**
   * Creates a <tt>LegendGraphic</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'LegendGraphic'-element.
   * <p>
   *
   * @param element
   *          the 'LegendGraphic'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Graphic</tt> -instance
   */
  private static LegendGraphic createLegendGraphic( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // required: <Graphic>
    final Element graphicElement = XMLTools.getRequiredChildByName( "Graphic", CommonNamespaces.SLDNS.toString(), element );
    final Graphic graphic = SLDFactory.createGraphic( urlResolver, graphicElement );

    return new LegendGraphic_Impl( graphic );
  }

  /**
   * Creates an <tt>ExternalGraphic</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'ExternalGraphic'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'ExternalGraphic'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>ExternalGraphic</tt> -instance
   */
  private static ExternalGraphic createExternalGraphic( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // required: <OnlineResource>
    final Element onlineResourceElement = XMLTools.getRequiredChildByName( "OnlineResource", CommonNamespaces.SLDNS.toString(), element );

    // required: href-Attribute (in <OnlineResource>)
    final String href = XMLTools.getRequiredAttrValue( "href", SLDFactory.xlnNS, onlineResourceElement );
    // URL url = null;
    //
    // try
    // {
    // url = urlResolver.resolveURL( href );
    // }
    // catch( MalformedURLException e )
    // {
    // throw new XMLParsingException( "Value ('" + href + "') of attribute 'href' of " + "element 'OnlineResoure' does
    // not denote a valid URL: " + e.getMessage() );
    // }

    // required: <Format> (in <OnlineResource>)
    final String format = XMLTools.getRequiredStringValue( "Format", CommonNamespaces.SLDNS.toString(), element );

    return new ExternalGraphic_Impl( urlResolver, format, href );
  }

  /**
   * Creates a <tt>Mark</tt> -instance according to the contents of the DOM-subtree starting at the given 'Mark'-
   * <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'Mark'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Mark</tt> -instance
   */
  private static Mark createMark( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    Stroke stroke = null;
    Fill fill = null;

    // optional: <WellKnownName>
    final String wkn = XMLTools.getStringValue( "WellKnownName", CommonNamespaces.SLDNS.toString(), element, null );

    // optional: <Stroke>
    final Element strokeElement = XMLTools.getChildByName( "Stroke", CommonNamespaces.SLDNS.toString(), element );

    if( strokeElement != null )
    {
      stroke = SLDFactory.createStroke( urlResolver, strokeElement );
    }

    // optional: <Fill>
    final Element fillElement = XMLTools.getChildByName( "Fill", CommonNamespaces.SLDNS.toString(), element );

    if( fillElement != null )
    {
      fill = SLDFactory.createFill( urlResolver, fillElement );
    }

    return new Mark_Impl( wkn, stroke, fill );
  }

  /**
   * Creates a <tt>Stroke</tt> -instance according to the contents of the DOM-subtree starting at the given 'Stroke'-
   * <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'Stroke'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Stroke</tt> -instance
   */
  private static Stroke createStroke( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    GraphicFill gf = null;
    GraphicStroke gs = null;

    // optional: <GraphicFill>
    final Element gfElement = XMLTools.getChildByName( "GraphicFill", CommonNamespaces.SLDNS.toString(), element );

    if( gfElement != null )
    {
      gf = SLDFactory.createGraphicFill( urlResolver, gfElement );
    }

    // optional: <GraphicStroke>
    final Element gsElement = XMLTools.getChildByName( "GraphicStroke", CommonNamespaces.SLDNS.toString(), element );

    if( gsElement != null )
    {
      gs = SLDFactory.createGraphicStroke( urlResolver, gsElement );
    }

    // optional: <CssParameter>s
    final ElementList nl = XMLTools.getChildElementsByName( "CssParameter", CommonNamespaces.SLDNS.toString(), element );
    final HashMap<String, CssParameter> cssParams = new HashMap<String, CssParameter>( nl.getLength() );

    for( int i = 0; i < nl.getLength(); i++ )
    {
      final CssParameter cssParam = SLDFactory.createCssParameter( nl.item( i ) );
      cssParams.put( cssParam.getName(), cssParam );
    }

    return new Stroke_Impl( cssParams, gs, gf );
  }

  /**
   * Creates a <tt>GraphicFill</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'GraphicFill'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'GraphicFill'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>GraphicFill</tt> -instance
   */
  private static GraphicFill createGraphicFill( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // required: <Graphic>
    final Element graphicElement = XMLTools.getRequiredChildByName( "Graphic", CommonNamespaces.SLDNS.toString(), element );
    final Graphic graphic = SLDFactory.createGraphic( urlResolver, graphicElement );

    return new GraphicFill_Impl( graphic );
  }

  /**
   * Creates a <tt>GraphicStroke</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'GraphicStroke'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'GraphicStroke'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>GraphicStroke</tt> -instance
   */
  private static GraphicStroke createGraphicStroke( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {
    // required: <Graphic>
    final Element graphicElement = XMLTools.getRequiredChildByName( "Graphic", CommonNamespaces.SLDNS.toString(), element );
    final Graphic graphic = SLDFactory.createGraphic( urlResolver, graphicElement );

    return new GraphicStroke_Impl( graphic );
  }

  /**
   * Creates a <tt>Graphic</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'Graphic'-element.
   * <p>
   *
   * @param element
   *          the 'Graphic'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>Graphic</tt> -instance
   */
  private static Graphic createGraphic( final IUrlResolver2 urlResolver, final Element element ) throws XMLParsingException
  {

    // optional: <Opacity>
    ParameterValueType opacity = null;
    // optional: <Size>
    ParameterValueType size = null;
    // optional: <Rotation>
    ParameterValueType rotation = null;

    // optional: <ExternalGraphic>s / <Mark>s
    final NodeList nodelist = element.getChildNodes();
    final List<Object> marksAndExtGraphicsList = new ArrayList<Object>();

    for( int i = 0; i < nodelist.getLength(); i++ )
    {
      if( nodelist.item( i ) instanceof Element )
      {
        final Element child = (Element) nodelist.item( i );
        final String namespace = child.getNamespaceURI();

        if( !CommonNamespaces.SLDNS.toString().equals( namespace ) )
          continue;

        final String childName = child.getLocalName();
        if( childName.equals( "ExternalGraphic" ) )
          marksAndExtGraphicsList.add( SLDFactory.createExternalGraphic( urlResolver, child ) );
        else if( childName.equals( "Mark" ) )
          marksAndExtGraphicsList.add( SLDFactory.createMark( urlResolver, child ) );
        else if( childName.equals( "Opacity" ) )
          opacity = SLDFactory.createParameterValueType( child );
        else if( childName.equals( "Size" ) )
          size = SLDFactory.createParameterValueType( child );
        else if( childName.equals( "Rotation" ) )
          rotation = SLDFactory.createParameterValueType( child );
      }
    }

    final Object[] marksAndExtGraphics = marksAndExtGraphicsList.toArray( new Object[marksAndExtGraphicsList.size()] );

    return new Graphic_Impl( marksAndExtGraphics, opacity, size, rotation );
  }

  /**
   * Creates a <tt>CssParameter</tt> -instance according to the contents of the DOM-subtree starting at the given
   * 'CssParameter'- <tt>Element</tt>.
   * <p>
   *
   * @param element
   *          the 'CssParamter'- <tt>Element</tt>
   * @throws XMLParsingException
   *           if a syntactic or semantic error in the DOM-subtree is encountered
   * @return the constructed <tt>CssParameter</tt> -instance
   */
  private static CssParameter createCssParameter( final Element element ) throws XMLParsingException
  {
    // required: name-Attribute
    final String name = XMLTools.getRequiredAttrValue( "name", element );
    final ParameterValueType pvt = SLDFactory.createParameterValueType( element );

    return (new CssParameter_Impl( name, pvt ));
  }

  private static RasterSymbolizer createRasterSymbolizer( final IUrlResolver2 urlResolver, final Element element, final double min, final double max, final UOM uom )
  {
    try
    {
      final Element colorMapElement = XMLTools.getChildByName( "ColorMap", CommonNamespaces.SLDNS.toString(), element );
      final SortedMap<Double, ColorMapEntry> colorMap = createColorMap( colorMapElement );

      final Element imageOutlineElement = XMLTools.getChildByName( "ImageOutline", CommonNamespaces.SLDNS.toString(), element );
      final Symbolizer imageOutline = createImageOutline( urlResolver, imageOutlineElement, min, max, uom );

      final Element shadedReliefElement = XMLTools.getChildByName( "ShadedRelief", CommonNamespaces.SLDNS.toString(), element );
      final ShadedRelief shadedRelief = createShadedRelief( shadedReliefElement );

      final Element opacityElement = XMLTools.getChildByName( "Opacity", CommonNamespaces.SLDNS.toString(), element );
      final ParameterValueType opacity = createParameterValueType( opacityElement );

      return new RasterSymbolizer_Impl( opacity, colorMap, imageOutline, shadedRelief );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      return null;
    }
  }

  private static SortedMap<Double, ColorMapEntry> createColorMap( final Element colorMapElement ) throws JAXBException
  {
    final SortedMap<Double, ColorMapEntry> colorMap = new TreeMap<Double, ColorMapEntry>();
    if( colorMapElement == null )
      return null;

    final JAXBContext jc = JaxbUtilities.createQuiet( ObjectFactory.class, ogc2.www.opengis.net.gml.ObjectFactory.class, ogc2.www.opengis.net.ogc.ObjectFactory.class );
    final Unmarshaller unmarshaller = jc.createUnmarshaller();
    final ogc2.www.opengis.net.sld.ColorMap colorMapObject = (ColorMap) unmarshaller.unmarshal( colorMapElement );

    final List<ogc2.www.opengis.net.sld.ColorMapEntry> colorMapEntries = colorMapObject.getColorMapEntry();
    for( int i = 0; i < colorMapEntries.size(); i++ )
    {
      final ogc2.www.opengis.net.sld.ColorMapEntry colorMapEntry = colorMapEntries.get( i );
      final Color color = Color.decode( colorMapEntry.getColor() );
      final Double opacity = colorMapEntry.getOpacity();
      final Double quantity = colorMapEntry.getQuantity();
      final String label = colorMapEntry.getLabel();

      final double realOpacity = opacity == null ? 1.0 : opacity;

      // REMARK: what to do without quantity? i makes no sense, but what else...
      final double realQuantity = quantity == null ? i : quantity;

      final String realLabel = label == null ? "" : label.trim();

      final ColorMapEntry colorMapEntryObject = new ColorMapEntry_Impl( color, realOpacity, realQuantity, realLabel );

      colorMap.put( realQuantity, colorMapEntryObject );
    }

    return colorMap;
  }

  private static ShadedRelief createShadedRelief( final Element element )
  {
    if( element == null )
      return null;

    final String brightnessOnlyString = XMLTools.getStringValue( "BrightnessOnly", CommonNamespaces.SLDNS.toString(), element, null );
    final Boolean brightnessOnly = brightnessOnlyString == null ? null : DatatypeConverter.parseBoolean( brightnessOnlyString );

    final String reliefFactorString = XMLTools.getStringValue( "ReliefFactor", CommonNamespaces.SLDNS.toString(), element, null );
    final Double reliefFactor = reliefFactorString == null ? null : DatatypeConverter.parseDouble( reliefFactorString );

    return new ShadedRelief( brightnessOnly, reliefFactor );
  }

  private static Symbolizer createImageOutline( final IUrlResolver2 urlResolver, final Element element, final double min, final double max, final UOM uom ) throws XMLParsingException
  {
    if( element == null )
      return null;

    final Element lineSymbolizerElement = XMLTools.getChildByName( "LineSymbolizer", CommonNamespaces.SLDNS.toString(), element );
    if( lineSymbolizerElement != null )
      return createLineSymbolizer( urlResolver, lineSymbolizerElement, min, max, uom );

    final Element polygonSymbolizerElement = XMLTools.getChildByName( "PolygonSymbolizer", CommonNamespaces.SLDNS.toString(), element );
    if( polygonSymbolizerElement != null )
      return createPolygonSymbolizer( urlResolver, polygonSymbolizerElement, min, max, uom );

    return null;
  }


}