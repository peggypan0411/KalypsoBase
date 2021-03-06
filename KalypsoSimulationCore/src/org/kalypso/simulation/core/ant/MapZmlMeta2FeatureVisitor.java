/*--------------- Kalypso-Header ------------------------------------------

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

 --------------------------------------------------------------------------*/

package org.kalypso.simulation.core.ant;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kalypso.contribs.java.net.UrlResolverSingleton;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.metadata.MetadataList;
import org.kalypso.ogc.sensor.zml.ZmlFactory;
import org.kalypso.simulation.core.i18n.Messages;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPathException;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPathUtilities;

/**
 * @author Gernot Belger
 */
public class MapZmlMeta2FeatureVisitor extends AbstractMonitoredFeatureVisitor implements FeatureVisitor
{
  public static class Metadata
  {
    private String m_name;

    public void setName( final String name )
    {
      m_name = name;
    }

    public String getName( )
    {
      return m_name;
    }
  }

  public static class Mapping
  {
    private String m_targetProperty;

    private String m_format;

    private final List<Metadata> m_metadataEntries = new ArrayList<>( 2 );

    /** This FeatureProperty will be created */
    public void setTargetProperty( final String targetProperty )
    {
      m_targetProperty = targetProperty;
    }

    public String getTargetProperty( )
    {
      return m_targetProperty;
    }

    /** Metadata entries from which the property will be created */
    public void addConfiguredMetadata( final Metadata metadata )
    {
      m_metadataEntries.add( metadata );
    }

    public Metadata[] getMetadataNames( )
    {
      return m_metadataEntries.toArray( new Metadata[m_metadataEntries.size()] );
    }

    /** Optional format string, how to parse the values */
    public void setFormat( final String format )
    {
      m_format = format;
    }

    public String getFormat( )
    {
      return m_format;
    }
  }

  private final Logger m_logger = Logger.getLogger( getClass().getName() );

  private final URL m_context;

  private final GMLXPath m_zmlLink;

  private final Mapping[] m_mappings;

  public MapZmlMeta2FeatureVisitor( final URL context, final GMLXPath zmlLink, final Mapping[] mappings )
  {
    m_context = context;
    m_zmlLink = zmlLink;
    m_mappings = mappings;
  }

  /**
   * @see org.kalypsodeegree.model.feature.FeatureVisitor#visit(org.kalypsodeegree.model.feature.Feature)
   */
  @Override
  public final boolean visit( final Feature f )
  {
    final TimeseriesLinkType link = fetchTimeseriesLink( f );
    if( link == null )
      return true;

    final String href = link.getHref();
    setCurrentSubTask( href );

    try
    {
      final URL url = UrlResolverSingleton.getDefault().resolveURL( m_context, href );
      final IObservation observation = ZmlFactory.parseXML( url );
      for( final Mapping mapping : m_mappings )
      {
        applyMapping( mapping, observation, f );
      }
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();

      m_logger.log( Level.SEVERE, Messages.getString( "org.kalypso.ogc.util.MapZmlMeta2FeatureVisitor.2" ) + href, e ); //$NON-NLS-1$
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
      m_logger.log( Level.SEVERE, Messages.getString( "org.kalypso.ogc.util.MapZmlMeta2FeatureVisitor.3" ) + href, e ); //$NON-NLS-1$
    }

    return true;
  }

  private TimeseriesLinkType fetchTimeseriesLink( final Feature f )
  {
    try
    {
      final Object property = GMLXPathUtilities.query( m_zmlLink, f );
      if( property == null )
        return null;

      if( property instanceof TimeseriesLinkType )
        return (TimeseriesLinkType) property;

      m_logger.warning( Messages.getString( "org.kalypso.ogc.util.MapZmlMeta2FeatureVisitor.1" ) + TimeseriesLinkType.class.getName() ); //$NON-NLS-1$
    }
    catch( final GMLXPathException e )
    {
      e.printStackTrace();
    }

    return null;
  }

  private void applyMapping( final Mapping mapping, final IObservation observation, final Feature f )
  {
    final IPropertyType ftp = f.getFeatureType().getProperty( mapping.getTargetProperty() );
    if( ftp == null )
    {
      m_logger.warning( Messages.getString( "org.kalypso.ogc.util.MapZmlMeta2FeatureVisitor.4" ) + m_zmlLink ); //$NON-NLS-1$
      return;
    }

    // welche property wird erzeigt
    final MetadataList metadataList = observation.getMetadataList();
    final Metadata[] names = mapping.getMetadataNames();
    final String[] values = new String[names.length];
    for( int i = 0; i < values.length; i++ )
    {
      values[i] = metadataList.getProperty( names[i].getName() );
      if( values[i] == null )
      {
        m_logger.log( Level.WARNING, Messages.getString( "org.kalypso.ogc.util.MapZmlMeta2FeatureVisitor.5" ) + f.getId() + Messages.getString( "org.kalypso.ogc.util.MapZmlMeta2FeatureVisitor.6" ) + names[i] ); //$NON-NLS-1$ //$NON-NLS-2$
        return;
      }
    }

    final Object object = FeatureHelper.createFeaturePropertyFromStrings( (IValuePropertyType) ftp, mapping.getFormat(), values, false );
    f.setProperty( ftp, object );
  }
}
