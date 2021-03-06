/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ogc.gml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.bind.JaxbUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.jaxb.TemplateUtilities;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.template.featureview.Featuretemplate;
import org.kalypso.template.gismapview.Gismapview;
import org.kalypso.template.gismapview.Gismapview.Layers;
import org.kalypso.template.gismapview.ObjectFactory;
import org.kalypso.template.gistableview.Gistableview;
import org.kalypso.template.gistableview.Gistableview.Layer;
import org.kalypso.template.gistreeview.Gistreeview;
import org.kalypso.template.types.ExtentType;
import org.kalypso.template.types.StyledLayerType;
import org.kalypso.transformation.CRSHelper;
import org.kalypso.transformation.transformer.GeoTransformerFactory;
import org.kalypso.transformation.transformer.IGeoTransformer;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.filterencoding.Filter;
import org.kalypsodeegree.filterencoding.FilterConstructionException;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree_impl.filterencoding.AbstractFilter;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;
import org.kalypsodeegree_impl.model.feature.FeaturePath;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Hilfsklasse, um aus den Binding-Klassen 'echte' Objekte zu erzeugen und umgekehrt
 * 
 * @author Belger
 */
public final class GisTemplateHelper
{
  public static final ObjectFactory OF_GISMAPVIEW = new ObjectFactory();

  public static final org.kalypso.template.types.ObjectFactory OF_TEMPLATE_TYPES = TemplateUtilities.OF_TEMPLATE_TYPES;

  public static final JAXBContext JC_FEATUREVIEW = JaxbUtilities.createQuiet( org.kalypso.template.featureview.ObjectFactory.class );

  public static final org.kalypso.template.featureview.ObjectFactory OF_FEATUREVIEW = new org.kalypso.template.featureview.ObjectFactory();

  private GisTemplateHelper( )
  {
    // never instantiate this class
  }

  /**
   * @param strictType
   *          If true, use the real FeatureType of the first feature of the given collection to strictly type the layer.
   */
  public static final Gismapview createGisMapView( final Map<Feature, IRelationType> layersToCreate, final boolean strictType )
  {
    final Gismapview gismapview = TemplateUtilities.OF_GISMAPVIEW.createGismapview();
    final Layers layers = TemplateUtilities.OF_GISMAPVIEW.createGismapviewLayers();
    gismapview.setLayers( layers );

    final List<JAXBElement< ? extends StyledLayerType>> layer = layers.getLayer();

    int count = 0;
    for( final Map.Entry<Feature, IRelationType> entry : layersToCreate.entrySet() )
    {
      final Feature feature = entry.getKey();
      final URL context = feature.getWorkspace().getContext();

      final IRelationType rt = entry.getValue();

      final String parentName = FeatureHelper.getAnnotationValue( feature, IAnnotation.ANNO_NAME );
      final String parentLabel = FeatureHelper.getAnnotationValue( feature, IAnnotation.ANNO_LABEL );
      final String layerName = parentName + " - " + parentLabel; //$NON-NLS-1$

      final FeaturePath featurePathToParent = new FeaturePath( feature );

      final Object property = feature.getProperty( rt );
      String typeName = ""; //$NON-NLS-1$
      if( strictType && (property instanceof List< ? >) )
      {
        final List< ? > list = (List< ? >)property;
        if( !list.isEmpty() )
        {
          final Feature firstChild = FeatureHelper.getFeature( feature.getWorkspace(), list.get( 0 ) );
          typeName = "[" + firstChild.getFeatureType().getQName().getLocalPart() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        }
      }

      final String memberName = rt.getQName().getLocalPart() + typeName;

      final FeaturePath featurePath = new FeaturePath( featurePathToParent, memberName );

      final StyledLayerType layerType = OF_TEMPLATE_TYPES.createStyledLayerType();
      layerType.setHref( context.toExternalForm() );
      layerType.setFeaturePath( featurePath.toString() );
      layerType.setLinktype( "gml" ); //$NON-NLS-1$
      layerType.setName( layerName );
      layerType.setVisible( true );
      layerType.setId( "ID_" + count++ ); //$NON-NLS-1$

      final JAXBElement<StyledLayerType> layerElement = TemplateUtilities.OF_GISMAPVIEW.createLayer( layerType );

      layer.add( layerElement );
    }

    if( layer.size() > 0 )
      layers.setActive( layer.get( 0 ).getValue() );

    return gismapview;
  }

  public static final Featuretemplate loadGisFeatureTemplate( final IStorage file ) throws CoreException, IOException, JAXBException
  {
    InputStream is = null;

    try
    {
      is = new BufferedInputStream( file.getContents() );

      final Featuretemplate template = GisTemplateHelper.loadGisFeatureTemplate( new InputSource( is ) );

      is.close();

      return template;
    }
    finally
    {
      IOUtils.closeQuietly( is );
    }
  }

  public final static Featuretemplate loadGisFeatureTemplate( final URL url, final IProgressMonitor monitor ) throws CoreException
  {
    monitor.beginTask( Messages.getString( "org.kalypso.ogc.gml.GisTemplateHelper.0" ), 1000 ); //$NON-NLS-1$

    InputStream inputStream = null;
    try
    {
      inputStream = new BufferedInputStream( url.openStream() );
      final InputSource is = new InputSource( inputStream );
      final Featuretemplate template = loadGisFeatureTemplate( is );

      inputStream.close();

      return template;
    }
    catch( final JAXBException e )
    {
      throw new CoreException( new Status( IStatus.ERROR, KalypsoGisPlugin.PLUGIN_ID, Messages.getString( "org.kalypso.ogc.gml.GisTemplateHelper.1" ), e ) ); //$NON-NLS-1$
    }
    catch( final IOException e )
    {
      throw new CoreException( new Status( IStatus.ERROR, KalypsoGisPlugin.PLUGIN_ID, Messages.getString( "org.kalypso.ogc.gml.GisTemplateHelper.1" ), e ) ); //$NON-NLS-1$
    }
    finally
    {
      IOUtils.closeQuietly( inputStream );
      monitor.done();
    }
  }

  public static final Featuretemplate loadGisFeatureTemplate( final InputStream inputStream, final IProgressMonitor monitor ) throws CoreException
  {
    monitor.beginTask( Messages.getString( "org.kalypso.ogc.gml.GisTemplateHelper.3" ), 1000 ); //$NON-NLS-1$
    try
    {
      final InputSource is = new InputSource( inputStream );
      return loadGisFeatureTemplate( is );
    }
    catch( final JAXBException e )
    {
      throw new CoreException( StatusUtilities.statusFromThrowable( e, Messages.getString( "org.kalypso.ogc.gml.GisTemplateHelper.4" ) ) ); //$NON-NLS-1$
    }
    finally
    {
      monitor.done();
    }
  }

  public static final Featuretemplate loadGisFeatureTemplate( final InputSource is ) throws JAXBException
  {
    final Unmarshaller unmarshaller = JC_FEATUREVIEW.createUnmarshaller();

    return (Featuretemplate)unmarshaller.unmarshal( is );
  }

  public static final Gismapview loadGisMapView( final IStorage storage ) throws JAXBException, CoreException, SAXException, ParserConfigurationException, IOException
  {
    InputStream inputStream = null;
    try
    {
      inputStream = storage.getContents();
      final InputSource is = new InputSource( inputStream );
      if( storage instanceof IEncodedStorage )
        is.setEncoding( ((IEncodedStorage)storage).getCharset() );
      final Gismapview gisMapView = GisTemplateHelper.loadGisMapView( is );
      inputStream.close();
      return gisMapView;
    }
    finally
    {
      IOUtils.closeQuietly( inputStream );
    }
  }

  public static final Gismapview loadGisMapView( final File file ) throws IOException, JAXBException, SAXException, ParserConfigurationException
  {
    BufferedInputStream inputStream = null;
    try
    {
      inputStream = new BufferedInputStream( new FileInputStream( file ) );
      final Gismapview gisMapView = GisTemplateHelper.loadGisMapView( new InputSource( inputStream ) );
      inputStream.close();
      return gisMapView;
    }
    finally
    {
      IOUtils.closeQuietly( inputStream );
    }
  }

  /**
   * @param location
   *          An absolute (i.e. resolved resource location).
   */
  public static final Gismapview loadGisMapView( final URL location ) throws JAXBException, SAXException, ParserConfigurationException, IOException
  {
    final InputSource inputSource = new InputSource( location.toString() );
    return loadGisMapView( inputSource );
  }

  // TODO: for all calling methods: close streams!
  public static final Gismapview loadGisMapView( final InputSource is ) throws JAXBException, SAXException, ParserConfigurationException, IOException
  {
    final Unmarshaller unmarshaller = TemplateUtilities.createGismapviewUnmarshaller();

    // XInclude awareness
    final SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware( true );
    spf.setXIncludeAware( true );
    final XMLReader xr = spf.newSAXParser().getXMLReader();
    xr.setContentHandler( unmarshaller.getUnmarshallerHandler() );
    xr.parse( is );
    return (Gismapview)unmarshaller.getUnmarshallerHandler().getResult();
  }

  public static final Gistableview loadGisTableview( final IStorage storage ) throws CoreException, JAXBException
  {
    final InputSource is = new InputSource( storage.getContents() );
    if( storage instanceof IEncodedStorage )
      is.setEncoding( ((IEncodedStorage)storage).getCharset() );
    return GisTemplateHelper.loadGisTableview( is );
  }

  /**
   * @param templateLocation
   *          An absolute (i.e. resolved) resource location
   */
  public static final Gistableview loadGisTableview( final URL templateLocation ) throws JAXBException
  {
    final InputSource source = new InputSource( templateLocation.toString() );
    return loadGisTableview( source );
  }

  public static final Gistableview loadGisTableview( final InputSource is ) throws JAXBException
  {
    final Unmarshaller unmarshaller = TemplateUtilities.JC_GISTABLEVIEW.createUnmarshaller();
    return (Gistableview)unmarshaller.unmarshal( is );
  }

  public static final Gistreeview loadGisTreeView( final IStorage file ) throws JAXBException, CoreException
  {
    final InputSource is = new InputSource( file.getContents() );
    if( file instanceof IEncodedStorage )
      is.setEncoding( ((IEncodedStorage)file).getCharset() );
    return GisTemplateHelper.loadGisTreeView( is );
  }

  public static final Gistreeview loadGisTreeView( final InputSource is ) throws JAXBException
  {
    final Unmarshaller unmarshaller = TemplateUtilities.JC_GISTREEVIEW.createUnmarshaller();
    return (Gistreeview)unmarshaller.unmarshal( is );
  }

  public static void saveGisMapView( final Gismapview modellTemplate, final OutputStream outStream, final String encoding ) throws JAXBException
  {
    final Marshaller marshaller = TemplateUtilities.createGismapviewMarshaller( encoding );
    marshaller.marshal( modellTemplate, outStream );
  }

  public static void saveGisMapView( final Gismapview modellTemplate, final Writer writer, final String encoding ) throws JAXBException
  {
    final Marshaller marshaller = TemplateUtilities.createGismapviewMarshaller( encoding );
    marshaller.marshal( modellTemplate, writer );
  }

  public static void saveGisMapView( final Gismapview gisMapView, final File outFile, final String encoding ) throws IOException, JAXBException
  {
    BufferedOutputStream os = null;
    try
    {
      os = new BufferedOutputStream( new FileOutputStream( outFile ) );
      GisTemplateHelper.saveGisMapView( gisMapView, os, encoding );
      os.close();
    }
    finally
    {
      IOUtils.closeQuietly( os );
    }
  }

  public static void saveGisMapView( final Gismapview mapview, final IFile mapFile, final String encoding ) throws JAXBException, IOException, CoreException
  {

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    GisTemplateHelper.saveGisMapView( mapview, bos, encoding );
    bos.close();

    final ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
    if( mapFile.exists() )
      mapFile.setContents( bis, false, true, new NullProgressMonitor() );
    else
      mapFile.create( bis, true, new NullProgressMonitor() );

    bis.close();
  }

  public static GM_Envelope getBoundingBox( final Gismapview gisview )
  {
    final ExtentType extent = gisview.getExtent();
    if( extent == null )
      return null;

    final GM_Envelope env = GeometryFactory.createGM_Envelope( extent.getLeft(), extent.getBottom(), extent.getRight(), extent.getTop(), extent.getSrs() );
    final String orgSRSName = extent.getSrs();
    if( orgSRSName != null )
    {
      try
      {
        final String targetSRS = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
        if( !orgSRSName.equals( targetSRS ) )
        {
          // if srs attribute exists and it is not the target srs we have to convert it
          final IGeoTransformer transformer = GeoTransformerFactory.getGeoTransformer( targetSRS );
          return transformer.transform( env );
        }
      }
      catch( final Exception e )
      {
        // we just print the error, but asume that we can return an envelope that is not converted
        e.printStackTrace();
      }
    }

    return env;
  }

  /**
   * This method creates a new Map with a bounding box
   * 
   * @return gismapview new empty map with a layer list
   */
  public static Gismapview emptyGisView( )
  {
    final Gismapview gismapview = GisTemplateHelper.OF_GISMAPVIEW.createGismapview();
    final Layers layersType = GisTemplateHelper.OF_GISMAPVIEW.createGismapviewLayers();
    layersType.setActive( null );
    gismapview.setLayers( layersType );
    return gismapview;
  }

  public static GM_Polygon getBoxAsSurface( final Gismapview mapview, final String targetCS ) throws Exception
  {
    final ExtentType extent = mapview.getExtent();
    final String crsName = extent.getSrs();

    if( CRSHelper.isKnownCRS( crsName ) )
    {
      final GM_Envelope envelope = GeometryFactory.createGM_Envelope( extent.getLeft(), extent.getBottom(), extent.getRight(), extent.getTop(), crsName );
      final GM_Polygon bboxAsSurface = GeometryFactory.createGM_Surface( envelope, crsName );
      if( targetCS != null )
      {
        final IGeoTransformer transformer = GeoTransformerFactory.getGeoTransformer( targetCS );
        return (GM_Polygon)transformer.transform( bboxAsSurface );
      }

      return bboxAsSurface;
    }

    return null;
  }

  public static Filter getFilter( final Layer layer ) throws FilterConstructionException
  {
    final Object filterObject = layer.getFilter();
    return AbstractFilter.buildFromAnyType( filterObject );
  }
}