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

package org.kalypso.services.observation.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.java.net.IUrlResolver;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITuppleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.request.ObservationRequest;
import org.kalypso.ogc.sensor.zml.ZmlFactory;
import org.kalypso.ogc.sensor.zml.ZmlURL;
import org.kalypso.services.observation.KalypsoServiceObsActivator;
import org.kalypso.services.observation.client.repository.ServiceRepositoryObservation;
import org.kalypso.services.observation.i18n.Messages;
import org.kalypso.services.observation.sei.IObservationService;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;

/**
 * This feature visitor copies one timeserie over another. It is used to commit the prognose timeseries to the IMS.
 * 
 * @author schlienger
 */
public class CommitPrognoseFeatureVisitor implements FeatureVisitor
{
  private final static Logger LOG = Logger.getLogger( CommitPrognoseFeatureVisitor.class.getName() );

  private final Collection<IStatus> m_stati = new ArrayList<IStatus>();

  private final String m_sourceTS;

  private final String m_targetTS;

  private final URL m_context;

  private final IUrlResolver m_resolver;

  private final IObservationService m_srv;

  private final String m_sourceFilter;

  public CommitPrognoseFeatureVisitor( final IObservationService srv, final IUrlResolver resolver, final URL context, final String sourceTS, final String targetTS, final String sourceFilter )
  {
    m_srv = srv;
    m_resolver = resolver;
    m_context = context;
    m_sourceFilter = sourceFilter;
    m_sourceTS = sourceTS;
    m_targetTS = targetTS;
  }

  /**
   * @see org.kalypsodeegree.model.feature.FeatureVisitor#visit(org.kalypsodeegree.model.feature.Feature)
   */
  public boolean visit( final Feature f )
  {
    m_stati.add( work( f ) );

    return true;
  }

  private IStatus work( final Feature f )
  {
    final String id = f.getId();

    final TimeseriesLinkType sourceLink = (TimeseriesLinkType) f.getProperty( m_sourceTS );
    final TimeseriesLinkType targetLink = (TimeseriesLinkType) f.getProperty( m_targetTS );
    if( sourceLink == null )
      return new Status( IStatus.WARNING, KalypsoServiceObsActivator.getID(), 0, Messages.getString( "org.kalypso.services.observation.client.CommitPrognoseFeatureVisitor.0", m_sourceTS, id ), null ); //$NON-NLS-1$

    if( targetLink == null )
      return new Status( IStatus.WARNING, KalypsoServiceObsActivator.getID(), 0, Messages.getString( "org.kalypso.services.observation.client.CommitPrognoseFeatureVisitor.0", m_targetTS, id ), null ); //$NON-NLS-1$

    final String sourceHref = sourceLink.getHref();
    final String targetHref = targetLink.getHref();
    try
    {
      return doIt( sourceHref, targetHref );
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();
      return StatusUtilities.statusFromThrowable( e, Messages.getString( "org.kalypso.services.observation.client.CommitPrognoseFeatureVisitor.1" ) + id ); //$NON-NLS-1$
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
      return StatusUtilities.statusFromThrowable( e, Messages.getString( "org.kalypso.services.observation.client.CommitPrognoseFeatureVisitor.2" ) + id ); //$NON-NLS-1$
    }
  }

  private IStatus doIt( final String sourceHref, final String targetHref ) throws MalformedURLException, SensorException
  {
    final String filteredSourceHref;
    if( m_sourceFilter != null && m_sourceFilter.length() > 0 && sourceHref.indexOf( '?' ) == -1 )
      filteredSourceHref = sourceHref + "?" + m_sourceFilter; //$NON-NLS-1$
    else
      filteredSourceHref = sourceHref;

    final URL urlRS = m_resolver.resolveURL( m_context, filteredSourceHref );
    final IObservation source = ZmlFactory.parseXML( urlRS, filteredSourceHref );
// final String destRef = targetHref;

    final String destRef = ZmlURL.insertRequest( targetHref, new ObservationRequest( new Date(), new Date() ) );
    final URL urlPG = m_resolver.resolveURL( m_context, destRef );
    final IObservation dest = ZmlFactory.parseXML( urlPG, destRef );

    ITuppleModel values = null;
    try
    {
      // copy values from source into dest, expecting full compatibility
      values = ObservationUtilities.optimisticValuesCopy( source, dest, null, true );
    }
    catch( final IllegalArgumentException e )
    {
      // // not all axes could be associated between source and dest
      // LOG.warning( "Observations are not compatible: " + e + "\n" + "Check if all axes of " + dest
      // + " are defined in " + source );

      final Status status = new Status( IStatus.WARNING, KalypsoServiceObsActivator.getID(), 0, Messages.getString( "org.kalypso.services.observation.client.CommitPrognoseFeatureVisitor.3", source ), e ); //$NON-NLS-1$

      return status;
    }

    // // todo: maybe inform when nothing happened during copy
    // if( values == null )
    // LOG.info( "Nothing to copy for " + source.getName() );
    // else
    // {
    // save observation if it is a server side one
    if( ZmlURL.isServerSide( targetHref ) )
    {
      ServiceRepositoryObservation.setValuesFor( values, targetHref, m_srv );
      LOG.info( "Observation saved on server: " + targetHref ); //$NON-NLS-1$
    }
    else
      LOG.warning( "! Observation not server side: " + targetHref ); //$NON-NLS-1$
    // }

    return Status.OK_STATUS;
  }

  public IStatus[] getStati( )
  {
    return m_stati.toArray( new IStatus[m_stati.size()] );
  }
}
