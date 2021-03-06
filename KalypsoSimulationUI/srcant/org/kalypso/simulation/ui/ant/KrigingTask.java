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
package org.kalypso.simulation.ui.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.kalypso.commons.bind.JaxbUtilities;
import org.kalypso.contribs.java.net.UrlResolver;
import org.kalypso.contribs.java.xml.XMLUtilities;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypso.simulation.core.ant.CopyObservationMappingHelper;
import org.kalypso.simulation.ui.ant.kriging.KrigingReader;
import org.kalypso.simulation.ui.ant.kriging.SourceObservationProvider;
import org.kalypso.zml.filters.AbstractFilterType;
import org.kalypso.zml.filters.ObjectFactory;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree_impl.gml.schema.schemata.DeegreeUrlCatalog;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;
import org.kalypsodeegree_impl.model.feature.visitors.TransformVisitor;

/**
 * Ein Ant Task, der Zeitreihen-Links in GMLs kopiert. Die generelle Idee ist es, alle Features eines GML durchzugehen,
 * und f�r jedes Feature eine Zeitreihe (definiert �ber einen Link) zu lesen und an eine andere Stelle (definiert durch
 * eine andere Property des Features) zu schreiben. <code>
 * TODO
 * </code>
 * 
 * @author doemming
 */

public class KrigingTask extends Task
{

  /** context used for non qualified hrefs */
  private URL m_context;

  /** epsg-code for spatial reference system, that is used in kriging.txt */
  private String m_epsg;

  /** href to the mapping gml to generate */
  private String m_hrefGeneratesGml;

  /** href to rastermapping (Format Kr�ssig) (mandatory) */
  private String m_hrefKrigingTXT;

  /** href to modellGML (mandatory) */
  private String m_modellGML;

  /**
   * featurepath to the features that have the location property (mus be a polygon) (mandatory)
   */
  private String m_modellGMLFeaturePath;

  /** propertyName of the location property (mus be a polygon) (mandatory) */
  private String m_modellGMLpolygonPropname;

  /** property name, where to generate the taget files (mandatory) */
  private String m_modellGMLTargetObservationlinkPropname;

  /** modell, that contains links to source observations (mandatory) */
  private String m_sourceGML;

  /**
   * featurepath to features that contains links to source observations (mandatory)
   */
  private String m_sourceGMLFeaturePath;

  /**
   * feature property name that contains links to source observations (mandatory)
   */
  private String m_sourceGMLObservationLinkProperty;

  /**
   * feature property name that contains ids that are used for mapping to indentifiers in "kriging.txt" (optional, fid
   * used f unset )
   */
  private String m_sourceGMLIDLinkProperty;

  public final void setHrefGeneratesGml( final String hrefGeneratesGml )
  {
    m_hrefGeneratesGml = hrefGeneratesGml;
  }

  public final void setHrefKrigingTXT( final String hrefKrigingTXT )
  {
    m_hrefKrigingTXT = hrefKrigingTXT;
  }

  public final void setModellGML( final String modellGML )
  {
    m_modellGML = modellGML;
  }

  public final void setModellGMLFeaturePath( final String modellGMLFeaturePath )
  {
    m_modellGMLFeaturePath = modellGMLFeaturePath;
  }

  public final void setModellGMLpolygonPropname( final String modellGMLpolygonPropname )
  {
    m_modellGMLpolygonPropname = modellGMLpolygonPropname;
  }

  public final void setEpsg( final String epsg )
  {
    m_epsg = epsg;
  }

  public final void setContext( final URL context )
  {
    m_context = context;
  }

  public final void setTimeStepMinutes( final double timeStepMinutes )
  {
    if( timeStepMinutes > 0 )
    {
      // to avoid yellow thingies; is it used?
    }
// m_timeStepMinutes = timeStepMinutes;
  }

  public final void setModellGMLTargetObservationlinkPropname( final String modellGMLTargetObservationlinkPropname )
  {
    m_modellGMLTargetObservationlinkPropname = modellGMLTargetObservationlinkPropname;
  }

  public final void setSourceGML( final String sourceGML )
  {
    m_sourceGML = sourceGML;
  }

  public final void setSourceGMLFeaturePath( final String sourceGMLFeaturePath )
  {
    m_sourceGMLFeaturePath = sourceGMLFeaturePath;
  }

  public final void setSourceGMLIDLinkProperty( final String sourceGMLIDLinkProperty )
  {
    m_sourceGMLIDLinkProperty = sourceGMLIDLinkProperty;
  }

  public final void setSourceGMLObservationLinkProperty( final String sourceGMLObservationLinkProperty )
  {
    m_sourceGMLObservationLinkProperty = sourceGMLObservationLinkProperty;
  }

  @Override
  public void execute( ) throws BuildException
  {
    try
    {
      final Logger logger = Logger.getAnonymousLogger();
      logger.info( "load mapping schema NS=" + DeegreeUrlCatalog.NS_UPDATE_OBSERVATION_MAPPING );

      final GMLWorkspace resultWorkspace = CopyObservationMappingHelper.createMappingWorkspace( m_context );
      logger.info( "check coordinatessystem" );

      // crs stuff
      final String targetCRSName = m_epsg;
      if( targetCRSName == null )
        throw new Exception( "coordinatesystem not set" );

      logger.info( "use coordinatessystem " + m_epsg + " OK" );

      final UrlResolver urlResolver = new UrlResolver();

      if( m_hrefKrigingTXT == null )
        throw new Exception( "kriging raster not set" );

      final URL krigingMapURL = urlResolver.resolveURL( m_context, m_hrefKrigingTXT );
      if( krigingMapURL == null )
        throw new Exception( "kriging raster not found" );

      logger.info( "use kriging ratser (mapping) " + krigingMapURL.toExternalForm() );

      logger.info( "load modell..." );
      // load catchment model
      if( m_modellGML == null )
        throw new Exception( "modell not set" );

      final URL modellURL = urlResolver.resolveURL( m_context, m_modellGML );
      if( modellURL == null )
        throw new Exception( "modell not sound" );

      final GMLWorkspace modellWorkspace = GmlSerializer.createGMLWorkspace( modellURL, null );
      if( modellWorkspace == null )
        throw new Exception( "could not load modell" );

      modellWorkspace.accept( new TransformVisitor( targetCRSName ), modellWorkspace.getRootFeature(), FeatureVisitor.DEPTH_INFINITE );

      if( m_modellGMLFeaturePath == null )
        throw new Exception( "modell feature path not set" );
      final Object modellFeatureFromPath = modellWorkspace.getFeatureFromPath( m_modellGMLFeaturePath );
      final Feature[] modelFeatures = FeatureHelper.getFeaturess( modellFeatureFromPath );

      logger.info( "load src modell..." );
      // load src model
      if( m_sourceGML == null )
        throw new Exception( "source modell not set" );
      final URL srcModellURL = urlResolver.resolveURL( m_context, m_sourceGML );
      final GMLWorkspace srcModellWorkspace = GmlSerializer.createGMLWorkspace( srcModellURL, null );
      if( srcModellWorkspace == null )
        throw new Exception( "could not load source modell" );
      srcModellWorkspace.accept( new TransformVisitor( targetCRSName ), srcModellWorkspace.getRootFeature(), FeatureVisitor.DEPTH_INFINITE );
      final Object srcFeatureFromPath = srcModellWorkspace.getFeatureFromPath( m_sourceGMLFeaturePath );
      final Feature[] srcFeatures = FeatureHelper.getFeaturess( srcFeatureFromPath );
      final SourceObservationProvider provider = new SourceObservationProvider( srcFeatures, m_sourceGMLIDLinkProperty, m_sourceGMLObservationLinkProperty );

      logger.info( "calculate mapping ..." );
      final Reader inputStreamReader = new InputStreamReader( krigingMapURL.openStream() );

      final KrigingReader kReader = new KrigingReader( Logger.getLogger( Logger.GLOBAL_LOGGER_NAME ), inputStreamReader, provider, targetCRSName );

      final JAXBContext jc = JaxbUtilities.createQuiet( ObjectFactory.class );
      final Marshaller marshaller = JaxbUtilities.createMarshaller( jc );
      marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      for( final Feature feature : modelFeatures )
      {
        final AbstractFilterType inFilter = kReader.createFilter( feature, m_modellGMLpolygonPropname );

        final Writer writer = new StringWriter();
        marshaller.marshal( inFilter, writer );
        final String string = XMLUtilities.removeXMLHeader( writer.toString() );
        final String filterInline = XMLUtilities.prepareInLine( string );

        final TimeseriesLinkType copyLink = (TimeseriesLinkType) feature.getProperty( m_modellGMLTargetObservationlinkPropname );
        CopyObservationMappingHelper.addMapping( resultWorkspace, filterInline, copyLink.getHref() );
      }
      final File result = new File( m_hrefGeneratesGml );
      final FileOutputStream resultWriter = new FileOutputStream( result );
      GmlSerializer.serializeWorkspace( resultWriter, resultWorkspace, "UTF-8" );
      resultWriter.close();
    }
    catch( final Exception e )
    {
      e.printStackTrace();

      throw new BuildException( e.getLocalizedMessage(), e );
    }
  }
}
