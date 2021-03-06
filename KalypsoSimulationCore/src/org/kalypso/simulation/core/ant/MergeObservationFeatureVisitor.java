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
package org.kalypso.simulation.core.ant;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.contribs.java.net.IUrlResolver;
import org.kalypso.contribs.java.net.UrlResolverSingleton;
import org.kalypso.contribs.java.util.logging.ILogger;
import org.kalypso.contribs.java.util.logging.LoggerUtilities;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.status.KalypsoStati;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.ogc.sensor.timeseries.AxisUtils;
import org.kalypso.ogc.sensor.timeseries.wq.IWQConverter;
import org.kalypso.ogc.sensor.timeseries.wq.WQFactory;
import org.kalypso.ogc.sensor.zml.ZmlFactory;
import org.kalypso.ogc.sensor.zml.ZmlURL;
import org.kalypso.simulation.core.i18n.Messages;
import org.kalypso.zml.obslink.TimeseriesLinkType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;

/**
 * @author Gernot Belger
 */
public class MergeObservationFeatureVisitor implements FeatureVisitor
{
  private final URL m_targetContext;

  private final URL m_sourceContext;

  private final String m_observationProperty;

  private final ILogger m_logger;

  /**
   * @param sourceContext
   *          context to resolve observation links of source observations
   * @param targetContext
   *          context to resolve observation links of target observations
   * @param observationProperty
   *          name of the property of the observation link
   */
  public MergeObservationFeatureVisitor( final URL sourceContext, final URL targetContext, final String observationProperty, final ILogger logger )
  {
    m_sourceContext = sourceContext;
    m_targetContext = targetContext;
    m_observationProperty = observationProperty;
    m_logger = logger;
  }

  /**
   * @see org.kalypsodeegree.model.feature.FeatureVisitor#visit(org.kalypsodeegree.model.feature.Feature)
   */
  @Override
  public final boolean visit( final Feature f )
  {
    final String featureId = f.getId();
    final IUrlResolver resolver = UrlResolverSingleton.getDefault();

    try
    {
      final TimeseriesLinkType obsLink = (TimeseriesLinkType) f.getProperty( m_observationProperty );
      if( obsLink == null )
      {
        m_logger.log( Level.WARNING, LoggerUtilities.CODE_SHOW_DETAILS, Messages.getString( "org.kalypso.ogc.util.MergeObservationFeatureVisitor.0", featureId, m_observationProperty ) ); //$NON-NLS-1$
        return true;
      }

      final String href = obsLink.getHref();
      if( href == null || href.length() == 0 )
      {
        m_logger.log( Level.WARNING, LoggerUtilities.CODE_SHOW_MSGBOX, Messages.getString( "org.kalypso.ogc.util.MergeObservationFeatureVisitor.2", featureId ) ); //$NON-NLS-1$
        return true;
      }

      // load source obs
      final URL sourceURL = resolver.resolveURL( m_sourceContext, href );
      final IObservation sourceObs = ZmlFactory.parseXML( sourceURL );
      if( sourceObs == null )
      {
        m_logger.log( Level.WARNING, LoggerUtilities.CODE_SHOW_MSGBOX, Messages.getString( "org.kalypso.ogc.util.MergeObservationFeatureVisitor.3", featureId ) ); //$NON-NLS-1$
        return true;
      }

      // load target obs
      final URL targetURL = resolver.resolveURL( m_targetContext, href );
      final IObservation targetObs = ZmlFactory.parseXML( targetURL );
      if( targetObs == null )
      {
        m_logger.log( Level.WARNING, LoggerUtilities.CODE_SHOW_MSGBOX, Messages.getString( "org.kalypso.ogc.util.MergeObservationFeatureVisitor.4", featureId ) ); //$NON-NLS-1$
        return true;
      }

      // merge obses
      mergeObservation( sourceObs, targetObs );

      // Write target observation. A bit ugly, in order to find the file where to write it
      // remove query part if present, href is also used as file name here!
      final String targetHref = ZmlURL.getIdentifierPart( obsLink.getHref() );
      final IFile targetfile = ResourceUtilities.findFileFromURL( resolver.resolveURL( m_targetContext, targetHref ) );
      final IPath location = targetfile.getLocation();
      final File file = location.toFile();
      ZmlFactory.writeToFile( targetObs, file );
    }
    catch( final SensorException e )
    {
      e.printStackTrace();

      // tricky: wrap the exception with timeserie-link as text to have a better error message
      m_logger.log( Level.WARNING, LoggerUtilities.CODE_SHOW_DETAILS, Messages.getString( "org.kalypso.ogc.util.MergeObservationFeatureVisitor.5", featureId ) ); //$NON-NLS-1$
    }
    catch( final Exception e )
    {
      e.printStackTrace();

      m_logger.log( Level.WARNING, LoggerUtilities.CODE_SHOW_DETAILS, Messages.getString( "org.kalypso.ogc.util.MergeObservationFeatureVisitor.8", featureId, e.getLocalizedMessage() ) ); //$NON-NLS-1$
    }

    return true;
  }

  /**
   * Merges the two observations as described in the class comment.
   *
   * @throws SensorException
   */
  private void mergeObservation( final IObservation sourceObs, final IObservation targetObs ) throws SensorException
  {
    final IAxis sourceKeyAxis = getKeyAxis( sourceObs );
    final IAxis targetKeyAxis = getKeyAxis( targetObs );

    final IAxis[] sourceAxes = sourceObs.getAxes();
    final IAxis[] targetAxes = targetObs.getAxes();

    final ITupleModel sourceTuples = sourceObs.getValues( null );
    final ITupleModel targetTuples = targetObs.getValues( null );

    final IWQConverter targetWQ = WQFactory.createWQConverter( targetObs );
    final IWQConverter sourceWQ = WQFactory.createWQConverter( sourceObs );

    final Map<IAxis[], IAxis[]> axisMap = mapAxes( sourceAxes, targetAxes );

    final Map<Object, Integer> sourceKeyHash = ObservationUtilities.hashValues( sourceTuples, sourceKeyAxis );

    for( int i = 0; i < targetTuples.size(); i++ )
    {
      final Object targetKey = targetTuples.get( i, targetKeyAxis );

      // does the source contains the key?
      final Integer sourceKeyIndex = sourceKeyHash.get( targetKey );
      if( sourceKeyIndex == null )
        continue;
      final int sourceIndex = sourceKeyIndex.intValue();

      for( final Entry<IAxis[], IAxis[]> entry : axisMap.entrySet() )
      {
        final IAxis[] targets = entry.getKey();
        final IAxis targetAxis = targets[0];
        final IAxis targetStatusAxis = targets[1];
        final IAxis[] sources = entry.getValue();
        final IAxis sourceAxis = sources[0];
        final IAxis sourceStatusAxis = sources[1];

        // is the target a warned value?
        final Number targetStatus = getPersistentStatusValue( targetTuples, targetWQ, targetStatusAxis, i );
        if( !KalypsoStatusUtils.checkMask( targetStatus.intValue(), KalypsoStati.BIT_CHECK ) )
          continue;

        // is the source value user edited?
        final Number sourceStatus = getPersistentStatusValue( sourceTuples, sourceWQ, sourceStatusAxis, sourceIndex );
        if( !KalypsoStatusUtils.checkMask( sourceStatus.intValue(), KalypsoStati.BIT_USER_MODIFIED ) )
          continue;

        // copy value
        final Object sourceValue = sourceTuples.get( sourceIndex, sourceAxis );

        targetTuples.set( i, targetAxis, sourceValue );
        targetTuples.set( i, targetStatusAxis, sourceStatus );
      }

    }

  }

  /**
   * Returns the value of the given status axis. If the axis is not persistent, returns the value of the corresponding
   * persistent axis.
   */
  private Number getPersistentStatusValue( final ITupleModel values, final IWQConverter converter, final IAxis statusAxis, final int row ) throws SensorException
  {
    final IAxis persistentAxis = findPersistentAxis( values.getAxes(), converter, statusAxis );

    return (Number) values.get( row, persistentAxis );
  }

  private IAxis findPersistentAxis( final IAxis[] axes, final IWQConverter converter, final IAxis statusAxis )
  {
    try
    {
      return AxisUtils.findPersistentAxis( axes, converter, statusAxis );
    }
    catch( final IllegalArgumentException e )
    {
      // no persistent axis found: return to old behaviour
      e.printStackTrace();
      return statusAxis;
    }
  }

  /**
   * Filters the 'good' target axises and maps them to their corresponding source axes.
   *
   * @return Map <IAxis[], IAxis[]>; each array is of size 2; the axis and its status axis
   */
  private Map<IAxis[], IAxis[]> mapAxes( final IAxis[] sourceAxes, final IAxis[] targetAxes )
  {
    final Map<IAxis[], IAxis[]> result = new HashMap<>();

    for( final IAxis targetAxis : targetAxes )
    {
      // check, if it is a persistent value axis which has a status and a corresponding source axis;
      // everything else is ignored
      if( targetAxis.isKey() )
        continue;

      if( !targetAxis.isPersistable() )
        continue;

      if( KalypsoStatusUtils.isStatusAxis( targetAxis ) )
        continue;

      final IAxis targetStatusAxis = KalypsoStatusUtils.findStatusAxisFor( targetAxes, targetAxis );
      if( targetStatusAxis == null )
        continue;

      final IAxis sourceAxis = findSourceAxis( sourceAxes, targetAxis );
      final IAxis sourceStatusAxis = KalypsoStatusUtils.findStatusAxisFor( sourceAxes, sourceAxis );
      if( sourceStatusAxis == null )
        throw new IllegalArgumentException( Messages.getString( "org.kalypso.ogc.util.MergeObservationFeatureVisitor.10", sourceAxis.getName() ) ); //$NON-NLS-1$

      final IAxis[] targets = new IAxis[] { targetAxis, targetStatusAxis };
      final IAxis[] sources = new IAxis[] { sourceAxis, sourceStatusAxis };

      result.put( targets, sources );
    }

    return result;
  }

  /**
   * Find a sourceAxis for a targetAxis. <br>
   * As names are not always identical (depends, if the server is availabale or not), we simply use the type. <br>
   * So this will not work if we have observations with more than one axis of the same type...
   */
  private IAxis findSourceAxis( final IAxis[] sourceAxes, final IAxis targetAxis )
  {
    // find corresponding source axis
    final String targetType = targetAxis.getType();
    final String targetName = targetAxis.getName();
    final IAxis sourceAxis = ObservationUtilities.findAxisByTypeNoEx( sourceAxes, targetType );
    if( sourceAxis == null )
      throw new IllegalArgumentException( Messages.getString( "org.kalypso.ogc.util.MergeObservationFeatureVisitor.11", targetName ) ); //$NON-NLS-1$

    return sourceAxis;
  }

  private IAxis getKeyAxis( final IObservation obs )
  {
    final IAxis[] axes = ObservationUtilities.findAxesByKey( obs.getAxes() );
    if( axes.length == 0 || axes.length > 1 )
      throw new IllegalArgumentException( Messages.getString( "org.kalypso.ogc.util.MergeObservationFeatureVisitor.12" ) ); //$NON-NLS-1$

    return axes[0];
  }
}
