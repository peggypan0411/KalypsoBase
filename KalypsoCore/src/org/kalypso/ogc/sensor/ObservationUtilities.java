/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and Coastal Engineering
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
package org.kalypso.ogc.sensor;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.kalypso.commons.factory.FactoryException;
import org.kalypso.commons.java.lang.MathUtils;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.commons.parser.IParser;
import org.kalypso.commons.parser.ParserException;
import org.kalypso.ogc.sensor.impl.SimpleObservation;
import org.kalypso.ogc.sensor.impl.SimpleTupleModel;
import org.kalypso.ogc.sensor.metadata.MetadataHelper;
import org.kalypso.ogc.sensor.metadata.MetadataList;
import org.kalypso.ogc.sensor.request.IRequest;
import org.kalypso.ogc.sensor.request.ObservationRequest;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.ogc.sensor.timeseries.AxisUtils;
import org.kalypso.ogc.sensor.timeseries.TimeseriesUtils;
import org.kalypso.ogc.sensor.timeseries.datasource.DataSourceHelper;
import org.kalypso.ogc.sensor.zml.ZmlFactory;

/**
 * Utilities around IObservation.
 *
 * @author schlienger
 */
public final class ObservationUtilities
{
  private static final String MSG_ERROR_NOAXISTYPE = "Keine Achse gefunden vom Typ:"; //$NON-NLS-1$

  private static final Comparator<IAxis> AXIS_SORT_COMPARATOR = new AxisSortComparator();

  private ObservationUtilities( )
  {
    // not intended to be instanciated
  }

  /**
   * Finds the axis of the given observation that has the given name.
   *
   * @param axes
   *          the list of axes to scan
   * @param axisName
   *          the name of the axis which is searched
   * @return first axis found
   * @throws NoSuchElementException
   *           when no axis matches the name
   */
  public static IAxis findAxisByName( final IAxis[] axes, final String axisName ) throws NoSuchElementException
  {
    for( final IAxis axe : axes )
    {
      if( axe.getName().equalsIgnoreCase( axisName ) )
        return axe;
    }

    throw new NoSuchElementException( MSG_ERROR_NOAXISTYPE + axisName );
  }

  /**
   * Find an axis with the given name, if it could not be found, tries to find it by type. If it still could not be
   * found, then a NoSuchElementException is thrown
   *
   * @see ObservationUtilities#findAxisByName(IAxis[], String)
   * @see ObservationUtilities#findAxisByType(IAxis[], String)
   * @throws NoSuchElementException
   *           when neither name nor type matched
   */
  public static IAxis findAxisByNameThenByType( final IAxis[] axes, final String axisNameOrType ) throws NoSuchElementException
  {
    try
    {
      return findAxisByName( axes, axisNameOrType );
    }
    catch( final NoSuchElementException ignored )
    {
      return findAxisByType( axes, axisNameOrType );
    }
  }

  /**
   * returns null when no axis found instead of throwing an exception
   *
   * @return axis or null if not found
   * @see ObservationUtilities#findAxisByName(IAxis[], String)
   */
  public static IAxis findAxisByNameNoEx( final IAxis[] axes, final String axisName )
  {
    try
    {
      return findAxisByName( axes, axisName );
    }
    catch( final NoSuchElementException e )
    {
      return null;
    }
  }

  /**
   * Finds the axis of the given observation that has the given type.
   *
   * @param axes
   *          the list of axes to scan
   * @param axisType
   *          the type of the axis which is searched
   * @return the first axis found
   * @throws NoSuchElementException
   *           when no axis matches the name
   */
  public static IAxis findAxisByType( final IAxis[] axes, final String axisType ) throws NoSuchElementException
  {
    final IAxis axis = findAxisByTypeNoEx( axes, axisType );
    if( axis == null )
      throw new NoSuchElementException( MSG_ERROR_NOAXISTYPE + axisType );

    return axis;
  }

  /**
   * Finds the axis of the given observation that has the given type.
   *
   * @param axes
   *          the list of axes to scan
   * @param axisType
   *          the type of the axis which is searched
   * @return the first axis found, null if no axis of this type was found
   */
  public static IAxis findAxisByTypeNoEx( final IAxis[] axes, final String axisType ) throws NoSuchElementException
  {
    for( final IAxis axe : axes )
    {
      if( axe.getType().equalsIgnoreCase( axisType ) )
        return axe;
    }

    return null;
  }

  /**
   * Return true if one of the axis is of the given type
   */
  public static boolean hasAxisOfType( final IAxis[] axes, final String axisType )
  {
    for( final IAxis axe : axes )
    {
      if( axe.getType().equalsIgnoreCase( axisType ) )
        return true;
    }

    return false;
  }

  /**
   * Returns the axes that are compatible with the desired Dataclass
   *
   * @return all axes which are compatible with desired Classtype
   */
  public static IAxis[] findAxesByClass( final IAxis[] axes, final Class< ? > desired )
  {
    if( ArrayUtils.isEmpty( axes ) )
      return new IAxis[] {};

    final ArrayList<IAxis> list = new ArrayList<>( axes == null ? 0 : axes.length );

    for( final IAxis axe : axes )
    {
      if( desired.isAssignableFrom( axe.getDataClass() ) )
        list.add( axe );
    }

    if( list.size() == 0 )
      throw new NoSuchElementException( MSG_ERROR_NOAXISTYPE + desired );

    return list.toArray( new IAxis[list.size()] );
  }

  /**
   * Returns the axes that are compatible with the desired Dataclasses
   *
   * @return all axes which are compatible with desired Classtype
   */
  public static IAxis[] findAxesByClasses( final IAxis[] axes, final Class< ? >[] desired )
  {
    if( ArrayUtils.isEmpty( axes ) )
      return new IAxis[] {};

    final List<IAxis> list = new ArrayList<>( axes == null ? 0 : axes.length );

    for( final IAxis axe : axes )
    {
      for( final Class< ? > element : desired )
      {
        if( element.isAssignableFrom( axe.getDataClass() ) )
          list.add( axe );
      }
    }

    if( list.size() == 0 )
      throw new NoSuchElementException( MSG_ERROR_NOAXISTYPE + desired );

    return list.toArray( new IAxis[list.size()] );
  }

  /**
   * Returns the first axis that is compatible with the desired Dataclass
   */
  public static IAxis findAxisByClass( final IAxis[] axes, final Class< ? > desired )
  {
    for( final IAxis axe : axes )
    {
      if( desired.isAssignableFrom( axe.getDataClass() ) )
        return axe;
    }

    throw new NoSuchElementException( MSG_ERROR_NOAXISTYPE + desired );
  }

  /**
   * Returns the first axis that is compatible with the desired Dataclass, does not throw an exception if no such axis
   * found. Returns null if no axis found.
   */
  public static IAxis findAxisByClassNoEx( final IAxis[] axes, final Class< ? > desired )
  {
    for( final IAxis axe : axes )
    {
      if( desired.isAssignableFrom( axe.getDataClass() ) )
        return axe;
    }

    return null;
  }

  /**
   * @return the axes which are key-axes. Returns an empty array if no axis found.
   */
  public static IAxis[] findAxesByKey( final IAxis[] axes )
  {
    final ArrayList<IAxis> list = new ArrayList<>( axes.length );

    for( final IAxis axe : axes )
    {
      if( axe.isKey() )
        list.add( axe );
    }

    return list.toArray( new IAxis[list.size()] );
  }

  /**
   * @param sep
   *          string separator between elements
   * @return simple string representation of the given model
   */
  public static String dump( final ITupleModel model, final String sep ) throws SensorException
  {
    final StringWriter writer = new StringWriter();

    try
    {
      dump( model, sep, writer );
    }
    finally
    {
      IOUtils.closeQuietly( writer );
    }

    return writer.toString();
  }

  /**
   * Dumps the contents of the model into a writer. Caller must close the writer.
   */
  public static void dump( final ITupleModel model, final String sep, final Writer writer ) throws SensorException
  {
    // do not use the same array because of the sort
    final IAxis[] axes = new ArrayList<>( Arrays.asList( model.getAxes() ) ).toArray( new IAxis[0] );

    // sort axes in order to have a better output
    sortAxes( axes );

    // retrieve apropriate parsers for each axis
    final IParser[] parsers = new IParser[axes.length];
    try
    {
      for( int j = 0; j < axes.length; j++ )
        parsers[j] = ZmlFactory.createParser( axes[j] );
    }
    catch( final FactoryException e )
    {
      e.printStackTrace();
      throw new SensorException( e );
    }

    try
    {
      // header
      for( int j = 0; j < axes.length; j++ )
      {
        writer.write( axes[j].toString() );

        if( j < axes.length - 1 )
          writer.write( sep );
      }

      writer.write( '\n' );

      // values
      for( int i = 0; i < model.size(); i++ )
      {
        // for each axis
        for( int j = 0; j < axes.length; j++ )
        {
          final IAxis axis = axes[j];

          try
          {
            writer.write( parsers[j].toString( model.get( i, axis ) ) );
          }
          catch( final ParserException e )
          {
            e.printStackTrace();

            writer.write( "Fehler" ); //$NON-NLS-1$
          }

          if( j < axes.length - 1 )
            writer.write( sep );
        }

        writer.write( '\n' );
      }
    }
    catch( final Exception e )
    {
      e.printStackTrace();
      throw new SensorException( e );
    }
  }

  /**
   * Dumps the tupple at given index using sep as separator.
   *
   * @return string representation of the given line (tupple)
   */
  public static String dump( final ITupleModel model, final String sep, final int index, final boolean excludeStatusAxes ) throws SensorException
  {
    IAxis[] axes = model.getAxes();

    if( excludeStatusAxes )
      axes = KalypsoStatusUtils.withoutStatusAxes( axes );

    // sort axes in order to have a better output
    sortAxes( axes );

    // retrieve apropriate parsers for each axis
    final IParser[] parsers = new IParser[axes.length];
    try
    {
      for( int j = 0; j < axes.length; j++ )
        parsers[j] = ZmlFactory.createParser( axes[j] );
    }
    catch( final FactoryException e )
    {
      e.printStackTrace();
      throw new SensorException( e );
    }

    final StringBuffer sb = new StringBuffer();

    for( int i = 0; i < axes.length; i++ )
    {
      try
      {
        sb.append( parsers[i].toString( model.get( index, axes[i] ) ) );
      }
      catch( final ParserException e )
      {
        e.printStackTrace();

        sb.append( "Fehler" ); //$NON-NLS-1$
      }

      if( i < axes.length - 1 )
        sb.append( sep );
    }

    return sb.toString();
  }

  /**
   * Returns the given row. Creates a new array containing the references to the values in the tuppleModel for that row
   * and these columns
   *
   * @param row
   *          row index for which objects will be taken
   * @param axisList
   *          columns for which objects will be taken
   * @throws SensorException
   */
  public static Object[] getElements( final ITupleModel tuppleModel, final int row, final IAxis[] axisList ) throws SensorException
  {
    final Object[] result = new Object[axisList.length];
    for( int i = 0; i < axisList.length; i++ )
      result[i] = tuppleModel.get( row, axisList[i] );
    return result;
  }

  /**
   * Hashes the values of one axis into a map.
   *
   * @return Map <Object, Integer>: value to its index
   */
  public static Map<Object, Integer> hashValues( final ITupleModel tuples, final IAxis axis ) throws SensorException
  {
    final Map<Object, Integer> result = new HashMap<>();

    for( int i = 0; i < tuples.size(); i++ )
    {
      final Object value = tuples.get( i, axis );
      result.put( value, new Integer( i ) );
    }

    return result;
  }

  /**
   * Sort an array of axes according to the Kalypso convention: axes are sorted based on their type information.
   * Example:
   * <p>
   * date, Q, T, V, W, etc.
   */
  public static void sortAxes( final IAxis[] axes )
  {
    Arrays.sort( axes, AXIS_SORT_COMPARATOR );
  }

  /**
   * AxisSortComparator: sorts the axes according to their types
   *
   * @author schlienger (02.06.2005)
   */
  public static class AxisSortComparator implements Comparator<IAxis>
  {
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare( final IAxis a1, final IAxis a2 )
    {
      return a1.getType().compareTo( a2.getType() );
    }
  }

  /**
   * @param tuppleModel
   * @param dateAxis
   *          must be key axis (sorted)
   * @param date
   * @param minIndex
   * @param maxIndex
   * @return index next to date
   * @throws SensorException
   */
  public static int findNextIndexForDate( final ITupleModel tuppleModel, final IAxis dateAxis, final Date date, int minIndex, int maxIndex ) throws SensorException
  {
    if( minIndex > maxIndex )
    {
      final int tmp = minIndex;
      minIndex = maxIndex;
      maxIndex = tmp;
    }
    final int smallesintervall = 10;
    final long targetDate = date.getTime();
    if( maxIndex - minIndex < smallesintervall )
    {
      int result = -1;
      long bestDistance = -1;
      for( int i = minIndex; i < maxIndex; i++ )
      {
        final Date rowDate = (Date) tuppleModel.get( i, dateAxis );
        final long distance = Math.abs( rowDate.getTime() - targetDate );
        if( i == minIndex || distance < bestDistance )
        {
          bestDistance = distance;
          result = i;
        }
      }
      return result;
    }
    // do recursion
    final int midIndex = (minIndex + maxIndex) / 2;
    final Date date1 = (Date) tuppleModel.get( midIndex - 1, dateAxis );
    final Date date2 = (Date) tuppleModel.get( midIndex, dateAxis );
    if( Math.abs( date1.getTime() - targetDate ) < Math.abs( date2.getTime() - targetDate ) )
      return findNextIndexForDate( tuppleModel, dateAxis, date, minIndex, midIndex );
    return findNextIndexForDate( tuppleModel, dateAxis, date, midIndex, maxIndex );
  }

  /**
   * @param tuppleModel
   * @param dateAxis
   *          must be key axis (sorted)
   * @param date
   * @param minIndex
   * @param maxIndex
   * @return index before date
   * @throws SensorException
   */
  public static int findIndexBeforeDate( final ITupleModel tuppleModel, final IAxis dateAxis, final Date date, final int minIndex, final int maxIndex ) throws SensorException
  {
    final int index = findNextIndexForDate( tuppleModel, dateAxis, date, minIndex, maxIndex );
    if( index < 0 )
      return index;

    final Date rowDate = (Date) tuppleModel.get( index, dateAxis );
    if( rowDate.before( rowDate ) )
      return index;
    return index - 1;
  }

  public static double getInterpolatedValueAt( final ITupleModel tuppelModel, final IAxis dateAxis, final IAxis valueAxis, final Date date ) throws SensorException
  {
    int index = ObservationUtilities.findIndexBeforeDate( tuppelModel, dateAxis, date, 0, tuppelModel.size() );
    // check range
    if( index < 0 )
      index = 0;
    if( index + 1 >= tuppelModel.size() )
      index = tuppelModel.size() - 2;
    final Date d1 = (Date) tuppelModel.get( index, dateAxis );
    final Date d2 = (Date) tuppelModel.get( index + 1, dateAxis );
    final double v1 = ((Double) tuppelModel.get( index, valueAxis )).doubleValue();
    final double v2 = ((Double) tuppelModel.get( index + 1, valueAxis )).doubleValue();
    return MathUtils.interpolate( d1.getTime(), d2.getTime(), v1, v2, date.getTime() );
  }

  /**
   * Request value from an observation , but buffers (i.e enlarges the request it by a given amount.
   *
   * @param dateRange
   *          If <code>null</code>, request the values from the baseObservation with a <code>null</code> request.
   * @throws SensorException
   */
  public static ITupleModel requestBuffered( final IObservation baseObservation, final DateRange dateRange, final int bufferField, final int bufferAmount ) throws SensorException
  {

    if( dateRange == null )
      return baseObservation.getValues( null );
    else if( Objects.isNull( dateRange.getFrom(), dateRange.getTo() ) )
      return baseObservation.getValues( null );

    final Date from = dateRange.getFrom();
    final Date to = dateRange.getTo();

    final Calendar bufferedFrom = Calendar.getInstance();
    bufferedFrom.setTime( from );
    bufferedFrom.add( bufferField, -bufferAmount );

    final Calendar bufferedTo = Calendar.getInstance();
    bufferedTo.setTime( to );
    bufferedTo.add( bufferField, bufferAmount );

    final IRequest bufferedRequest = new ObservationRequest( bufferedFrom.getTime(), bufferedTo.getTime() );
    return baseObservation.getValues( bufferedRequest );
  }

  public static IObservation forceParameterType( final IObservation observation, final String forcedParmaterType ) throws SensorException
  {
    /* Force the parameter type for evaporation and temperature */
    final ITupleModel tupleModel = observation.getValues( null );
    final Object[][] rawData = ObservationUtilities.getRawData( tupleModel );

    /* Exchange old value type with forced parameter type */
    final IAxis[] axes = tupleModel.getAxes();
    final IAxis[] forcedAxes = new IAxis[axes.length];
    for( int i = 0; i < forcedAxes.length; i++ )
      forcedAxes[i] = getForcedParameterAxes( axes[i], forcedParmaterType );

    final SimpleTupleModel newModel = new SimpleTupleModel( forcedAxes, rawData );

    /* Create and return new obs */
    final String name = observation.getName();
    final String href = observation.getHref();
    final MetadataList metadata = MetadataHelper.clone( observation.getMetadataList() );
    return new SimpleObservation( href, name, metadata, newModel );
  }

  private static Object[][] getRawData( final ITupleModel tupleModel ) throws SensorException
  {
    final int size = tupleModel.size();
    final IAxis[] axes = tupleModel.getAxes();

    final Object[][] rawData = new Object[size][];

    for( int i = 0; i < rawData.length; i++ )
    {
      rawData[i] = new Object[axes.length];

      for( int a = 0; a < axes.length; a++ )
        rawData[i][a] = tupleModel.get( i, axes[a] );
    }

    return rawData;
  }

  private static IAxis getForcedParameterAxes( final IAxis axis, final String forcedParmaterType )
  {
    if( AxisUtils.isValueAxis( axis ) )
      return TimeseriesUtils.createDefaultAxis( forcedParmaterType );

    if( AxisUtils.isStatusAxis( axis ) )
    {
      final IAxis tempAxis = TimeseriesUtils.createDefaultAxis( forcedParmaterType );
      return KalypsoStatusUtils.createStatusAxisFor( tempAxis, true );
    }

    if( AxisUtils.isDataSrcAxis( axis ) )
    {
      final IAxis tempAxis = TimeseriesUtils.createDefaultAxis( forcedParmaterType );
      return DataSourceHelper.createSourceAxis( tempAxis );
    }

    /* Default: do nothing */
    return axis;
  }
}