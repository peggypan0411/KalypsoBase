/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestraße 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 * 
 *  and
 *  
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Contact:
 * 
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *   
 *  ---------------------------------------------------------------------------*/
package org.kalypso.sld.featuretypestyle;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.kalypso.contribs.java.net.IUrlResolver2;
import org.kalypso.contribs.java.net.UrlResolverSingleton;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.catalog.CatalogManager;
import org.kalypso.core.catalog.CatalogSLD;
import org.kalypsodeegree.graphics.sld.ExternalGraphic;
import org.kalypsodeegree.graphics.sld.FeatureTypeStyle;
import org.kalypsodeegree.graphics.sld.Fill;
import org.kalypsodeegree.graphics.sld.Graphic;
import org.kalypsodeegree.graphics.sld.GraphicFill;
import org.kalypsodeegree.graphics.sld.GraphicStroke;
import org.kalypsodeegree.graphics.sld.Halo;
import org.kalypsodeegree.graphics.sld.LineSymbolizer;
import org.kalypsodeegree.graphics.sld.NamedLayer;
import org.kalypsodeegree.graphics.sld.PointSymbolizer;
import org.kalypsodeegree.graphics.sld.PolygonSymbolizer;
import org.kalypsodeegree.graphics.sld.Rule;
import org.kalypsodeegree.graphics.sld.Stroke;
import org.kalypsodeegree.graphics.sld.Style;
import org.kalypsodeegree.graphics.sld.StyledLayerDescriptor;
import org.kalypsodeegree.graphics.sld.Symbolizer;
import org.kalypsodeegree.graphics.sld.TextSymbolizer;
import org.kalypsodeegree.graphics.sld.UserStyle;
import org.kalypsodeegree_impl.graphics.sld.SLDFactory;

/**
 * @author doemming
 */
public class SLDCatalogTest extends TestCase
{
  private CatalogSLD m_catalogSLD;

  private CatalogManager m_manager;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp( ) throws Exception
  {
    super.setUp();

    m_manager =  KalypsoCorePlugin.getDefault().getCatalogManager();
    m_catalogSLD = KalypsoCorePlugin.getDefault().getSLDCatalog();

//    final File repository = new File( "C:/TMP/sld_repository" );
//    m_manager = CatalogManager.getDefault( repository );
//    m_manager.register( new URNGeneratorFeatureTypeStyle() );
//    m_manager.register( new URNGeneratorIFeatureType() );
//    m_catalogSLD = new CatalogSLD( m_manager, repository );
  }

  /**
   * generates a catalog for xPlanung featureTypes
   */
  public void testGenerateCatalogs( ) throws Exception
  {
    try
    {
      // catalog for xplanung featuretypes
      final URL styleURL = getClass().getResource( "resources/styles.xml" );
      generateFeatureTypeStyleCatalogFromSLD( styleURL );
    }
    catch( final Exception e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw e;
    }

  }

  public void generateFeatureTypeStyleCatalogFromSLD( final URL styleURL ) throws Exception
  {
    // create resolver relative to styleURL
    final IUrlResolver2 resolver = new IUrlResolver2()
    {

      @Override
      public URL resolveURL( final String href ) throws MalformedURLException
      {
        return UrlResolverSingleton.resolveUrl( styleURL, href );
      }
    };
    // load SLD
    final StyledLayerDescriptor sld = SLDFactory.createSLD( styleURL );
    final NamedLayer[] namedLayers = sld.getNamedLayers();
    final Style[] styles = namedLayers[0].getStyles();
    for( final Style style : styles )
    {
      if( style instanceof FeatureTypeStyle )
      {
        final FeatureTypeStyle ftStyle = (FeatureTypeStyle) style;
        System.out.println( "ftStyle " + ftStyle.getTitle() );
      }
      else if( style instanceof UserStyle )
      {
        final UserStyle userStyle = (UserStyle) style;
        System.out.println( "userStyle " + userStyle.getTitle() );
        final FeatureTypeStyle[] featureTypeStyles = userStyle.getFeatureTypeStyles();
        for( final FeatureTypeStyle ftStyle : featureTypeStyles )
        {
          final String name = ftStyle.getName();
          final QName featureTypeQName = new QName( "http://www.xplanung.de/bplangml", name );
          ftStyle.setTitle( name );
          ftStyle.setName( "default" );
          ftStyle.setFeatureTypeName( featureTypeQName );
          final URI store = m_catalogSLD.getStore();
          final IUrlResolver2 resolver2 = new IUrlResolver2()
          {
            @Override
            public URL resolveURL( final String href ) throws MalformedURLException
            {
              return UrlResolverSingleton.resolveUrl( store.toURL(), href );
            }
          };
          updateiconURNS( resolver2, ftStyle );
          m_catalogSLD.addRelative( ftStyle, store );
        }
      }
    }
    m_manager.saveAll();

    System.out.println( "loaded SLD and catalogs OK" );

    // load userStyle:
    final String urn = "urn:ogc:gml:featuretype:http___www.xplanung.de_bplangml:DenkmalschutzBereich:sld:default";
    final FeatureTypeStyle value = m_catalogSLD.getValue( resolver, urn, urn );
    assertNotNull( value );
    System.out.println( "loaded FeatureTypeStyle from catalog OK" );
    final QName qName = new QName( "http://www.xplanung.de/bplangml", "DenkmalschutzBereich" );
    final List<String> entryURNs = m_catalogSLD.getEntryURNs( qName );
    for( final String uri : entryURNs )
    {
      System.out.println( "URI" + uri );
    }
    final FeatureTypeStyle default1 = m_catalogSLD.getDefault( resolver, qName );
    assertNotNull( default1 );
  }

  private void updateURNS( final IUrlResolver2 resolver, final Graphic graphic )
  {
    if( graphic == null )
      return;
    final Object[] marksAndExtGraphics = graphic.getMarksAndExtGraphics();
    for( final Object object : marksAndExtGraphics )
    {
      if( object instanceof ExternalGraphic )
      {
        final ExternalGraphic externalGraphic = (ExternalGraphic) object;
        try
        {
          final URL realLocation = externalGraphic.getOnlineResourceURL();
          final String href = externalGraphic.getOnlineResource();
          final URL virtualLocation = resolver.resolveURL( href );

          final String uri = realLocation.toURI().toString();
          final String systemID = virtualLocation.toURI().toString();

          m_manager.getBaseCatalog().addEntry( uri, systemID, null );
          // externalGraphic.setOnlineResource( uri );
        }
        catch( final MalformedURLException e )
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch( final URISyntaxException e )
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  // TODO much better make Visitor that visits all sld-elements (doemming)
  private void updateiconURNS( final IUrlResolver2 resolver, final FeatureTypeStyle ftStyle )
  {
    final Rule[] rules = ftStyle.getRules();
    for( final Rule rule : rules )
    {
      final Symbolizer[] symbolizers = rule.getSymbolizers();
      for( final Symbolizer symbolizer : symbolizers )
      {
        if( symbolizer instanceof PolygonSymbolizer )
        {
          updateURNS( resolver, ((PolygonSymbolizer) symbolizer).getFill() );
          updateURNS( resolver, ((PolygonSymbolizer) symbolizer).getStroke() );
        }
        if( symbolizer instanceof LineSymbolizer )
        {
          updateURNS( resolver, ((LineSymbolizer) symbolizer).getStroke() );
        }
        if( symbolizer instanceof PointSymbolizer )
        {
          final PointSymbolizer pSymbolizer = (PointSymbolizer) symbolizer;
          final Graphic graphic = pSymbolizer.getGraphic();
          updateURNS( resolver, graphic );
        }
        if( symbolizer instanceof TextSymbolizer )
        {
          updateURNS( resolver, ((TextSymbolizer) symbolizer).getFill() );
          updateURNS( resolver, ((TextSymbolizer) symbolizer).getHalo() );
        }
      }
    }
  }

  private void updateURNS( final IUrlResolver2 resolver, final Halo halo )
  {
    if( halo == null )
      return;
    updateURNS( resolver, halo.getFill() );
    updateURNS( resolver, halo.getStroke() );
  }

  private void updateURNS( final IUrlResolver2 resolver, final Fill fill )
  {
    if( fill == null )
      return;
    updateURN( resolver, fill.getGraphicFill() );
  }

  private void updateURNS( final IUrlResolver2 resolver, final Stroke stroke )
  {
    if( stroke == null )
      return;
    updateURN( resolver, stroke.getGraphicFill() );
    final GraphicStroke graphicStroke = stroke.getGraphicStroke();
    if( graphicStroke == null )
      return;
    updateURNS( resolver, graphicStroke.getGraphic() );
  }

  private void updateURN( final IUrlResolver2 resolver, final GraphicFill graphicFill )
  {
    if( graphicFill == null )
      return;
    updateURNS( resolver, graphicFill.getGraphic() );
  }
}
