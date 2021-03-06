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
package org.kalypso.ogc.sensor.status;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.ArrayUtils;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.impl.DefaultAxis;

/**
 * Utility class for the handling of status information within Kalypso
 * 
 * @author schlienger
 */
public final class KalypsoStatusUtils
{
  private static final String STATUS_AXIS_LABELPREFIX = "_kalypso_status_"; //$NON-NLS-1$

  public static final String STATUS_AXIS_TYPE = "kalypso-status"; //$NON-NLS-1$

  public static final String SRC_AXIS_TYPE = "DATA_SRC"; //$NON-NLS-1$

  public static final Class< ? > STATUS_AXIS_DATACLASS = Integer.class;

  public static final String STATUS_AXIS_UNIT = ""; //$NON-NLS-1$

  private static final ImageIcon ICON_QUESTION = new ImageIcon( KalypsoStatusUtils.class.getResource( "resource/question.gif" ), "question" ); //$NON-NLS-1$ //$NON-NLS-2$

  private static final ImageIcon ICON_WARNING = new ImageIcon( KalypsoStatusUtils.class.getResource( "resource/warning.gif" ), "warning" ); //$NON-NLS-1$ //$NON-NLS-2$

  // private final static Icon ICON_ERROR = new ImageIcon(
  // KalypsoStatusUtils.class.getResource( "resource/error.gif" ) );

  private static final ImageIcon ICON_CONFLICT = new ImageIcon( KalypsoStatusUtils.class.getResource( "resource/conflict.gif" ) ); //$NON-NLS-1$

  private static final ImageIcon ICON_WRITE = new ImageIcon( KalypsoStatusUtils.class.getResource( "resource/write.gif" ), "write" ); //$NON-NLS-1$ //$NON-NLS-2$

  private static final Color COLOR_LIGHTYELLOW = new Color( 248, 243, 192 );

  private KalypsoStatusUtils( )
  {
    // not to be instanciated
  }

  /**
   * Returns the status axis name for the given value axis.
   * 
   * @param axis
   *          the observation axis for which to return the status axis name
   * @return the name of the corresponding status axis
   */
  public static String getStatusAxisNameFor( final IAxis axis )
  {
    if( isStatusAxis( axis ) )
      return axis.getName();

    return STATUS_AXIS_LABELPREFIX + axis.getName();
  }

  /**
   * Returns the axis label without the status marker
   * 
   * @return just axis label
   */
  public static String getAxisNameFor( final IAxis statusAxis )
  {
    return statusAxis.getName().replaceAll( STATUS_AXIS_LABELPREFIX, "" ); //$NON-NLS-1$
  }

  /**
   * Creates a status axis for the given 'normal' axis.
   * 
   * @return new status axis
   * @throws IllegalArgumentException
   *           if given axis is already a status axis
   */
  public static IAxis createStatusAxisFor( final IAxis axis, final boolean persistable )
  {
    if( isStatusAxis( axis ) )
      throw new IllegalArgumentException( "Axis " + axis + Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoStatusUtils.12" ) ); //$NON-NLS-1$ //$NON-NLS-2$

    final String statusAxisName = getStatusAxisNameFor( axis );
    return new DefaultAxis( statusAxisName, STATUS_AXIS_TYPE, STATUS_AXIS_UNIT, STATUS_AXIS_DATACLASS, false, persistable );
  }

  /**
   * Returns true if the axis is a status-axis (speaking Kalypso intern)
   * 
   * @return true if status-axis
   */
  // FIXME: also returns treu for Data-Source axes!
  public static boolean isStatusAxis( final IAxis axis )
  {
    if( axis.getType().equals( STATUS_AXIS_TYPE ) )
      return true;
    else if( axis.getType().equals( SRC_AXIS_TYPE ) )
      return true;

    return false;
  }

  /**
   * Returns true if the given statusCandidate is the status axis for the given axis
   */
  public static boolean isStatusAxisFor( final IAxis axis, final IAxis statusCandidate )
  {
    final String statusAxisLabel = getStatusAxisNameFor( axis );

    return statusCandidate.getName().equals( statusAxisLabel );
  }

  /**
   * Return true if both axes are equal in the sense of IAxis.equals() plus:
   * <ul>
   * <li>both axes are status-axes
   * <li>both axes have the same name
   * </ul>
   */
  public static boolean equals( final IAxis axis1, final IAxis axis2 )
  {
    if( !axis1.equals( axis2 ) )
      return false;

    return isStatusAxis( axis1 ) && isStatusAxis( axis2 ) && axis1.getName().equals( axis2.getName() );
  }

  /**
   * Finds the first status axis among the given list.
   * 
   * @return status axis
   * @throws NoSuchElementException
   *           if no status axis in the list
   */
  public static IAxis findStatusAxis( final IAxis[] axes ) throws NoSuchElementException
  {
    for( final IAxis axe : axes )
    {
      if( isStatusAxis( axe ) )
        return axe;
    }

    throw new NoSuchElementException( Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoStatusUtils.13" ) ); //$NON-NLS-1$
  }

  /**
   * Returns the status axis for the given axis if found in the axes-list.
   * 
   * @throws NoSuchElementException
   *           if no corresponding status axis found
   */
  public static IAxis findStatusAxisFor( final IAxis[] axes, final IAxis axis ) throws NoSuchElementException
  {
    for( final IAxis axe : axes )
    {
      if( isStatusAxisFor( axis, axe ) )
        return axe;
    }

    return null;
  }

  /**
   * Returns the status axis for the given axis if found in the axes-list.
   */
  public static IAxis findStatusAxisForNoEx( final IAxis[] axes, final IAxis axis ) throws NoSuchElementException
  {
    for( final IAxis axe : axes )
    {
      if( isStatusAxisFor( axis, axe ) )
        return axe;
    }

    return null;
  }

  /**
   * Finds the list of status axis among the given axes
   * 
   * @return status axes
   */
  public static IAxis[] findStatusAxes( final IAxis[] axes )
  {
    final ArrayList<IAxis> list = new ArrayList<>();

    for( final IAxis axe : axes )
    {
      if( isStatusAxis( axe ) )
        list.add( axe );
    }

    return list.toArray( new IAxis[list.size()] );
  }

  /**
   * Returns the list of non-status axes
   * 
   * @return non-status axes
   */
  public static IAxis[] withoutStatusAxes( final IAxis[] axes )
  {
    final ArrayList<IAxis> list = new ArrayList<>();

    for( int i = 0; i < axes.length; i++ )
    {
      if( !isStatusAxis( axes[i] ) )
        list.add( axes[i] );
    }

    return list.toArray( new IAxis[list.size()] );
  }

  /**
   * Returns the axes that are compatible with the desired Dataclass. You can specify if you want to exclude the status
   * axes from the result list or not.
   * <p>
   * Please note that currently the status axis is of a Number type.
   * 
   * @param axes
   * @param desired
   * @param excludeStatusAxes
   *          if true, status axes will not be included in the returned array
   * @return axes which are compatible with specified Class of data
   * @throws NoSuchElementException
   */
  public static IAxis[] findAxesByClass( final IAxis[] axes, final Class< ? > desired, final boolean excludeStatusAxes ) throws NoSuchElementException
  {
    if( ArrayUtils.isEmpty( axes ) )
      return new IAxis[] {};

    final ArrayList<IAxis> list = new ArrayList<>( axes == null ? 0 : axes.length );

    for( int i = 0; i < axes.length; i++ )
    {
      if( desired.isAssignableFrom( axes[i].getDataClass() ) )
      {
        if( !excludeStatusAxes || excludeStatusAxes && !KalypsoStatusUtils.isStatusAxis( axes[i] ) )
          list.add( axes[i] );
      }
    }

    if( list.size() == 0 )
      throw new NoSuchElementException( Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoStatusUtils.15" ) + desired ); //$NON-NLS-1$

    return list.toArray( new IAxis[list.size()] );
  }

  /**
   * Same as {@link KalypsoStatusUtils#findAxesByClass(IAxis[], Class, boolean)}for several classes.
   * 
   * @see KalypsoStatusUtils#findAxesByClass(IAxis[], Class, boolean)
   */
  public static IAxis[] findAxesByClasses( final IAxis[] axes, final Class< ? >[] desired, final boolean excludeStatusAxes ) throws NoSuchElementException
  {
    final List<IAxis> list = new ArrayList<>( axes == null ? 0 : axes.length );

    for( final Class< ? > element : desired )
    {
      for( int i = 0; i < axes.length; i++ )
      {
        if( element.isAssignableFrom( axes[i].getDataClass() ) )
        {
          if( !excludeStatusAxes || excludeStatusAxes && !KalypsoStatusUtils.isStatusAxis( axes[i] ) )
            list.add( axes[i] );
        }
      }
    }

    return list.toArray( new IAxis[list.size()] );
  }

  /**
   * Returns the first axis that is compatible with the desired Dataclass. You can specify if you want to exclude the
   * status axes from the result list or not.
   * <p>
   * Please note that currently the status axis is of a Number type.
   * 
   * @param axes
   * @param desired
   * @param excludeStatusAxes
   *          if true, status axes will not be included in the returned array
   * @return first axis found
   * @throws NoSuchElementException
   */
  public static IAxis findAxisByClass( final IAxis[] axes, final Class< ? > desired, final boolean excludeStatusAxes ) throws NoSuchElementException
  {
    for( int i = 0; i < axes.length; i++ )
    {
      if( desired.isAssignableFrom( axes[i].getDataClass() ) )
      {
        if( !excludeStatusAxes || excludeStatusAxes && !KalypsoStatusUtils.isStatusAxis( axes[i] ) )
          return axes[i];
      }
    }

    throw new NoSuchElementException( Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoStatusUtils.16" ) + desired ); //$NON-NLS-1$
  }

  /**
   * Checks if bit is in the mask.
   * 
   * @return true if bit is set in the given mask
   */
  public static boolean checkMask( final int mask, final int bit )
  {
    if( bit == 0 || mask == 0 )
      return mask == bit;

    return (mask & bit) == bit;
  }

  /**
   * @return the icon that best fits the mask, or null if no fit
   */
  public static ImageIcon getIconFor( final int mask )
  {
    if( checkMask( mask, KalypsoStati.BIT_CHECK ) )
      return ICON_WARNING;

    if( checkMask( mask, KalypsoStati.BIT_REQUIRED ) )
      return ICON_QUESTION;

    if( checkMask( mask, KalypsoStati.BIT_USER_MODIFIED ) )
      return ICON_WRITE;

    if( checkMask( mask, KalypsoStati.BIT_DERIVATION_ERROR ) )
      return ICON_CONFLICT;

    return null;
  }

  /**
   * Returns an icon that corresponds to the given description. Description can be any of the following:
   * <ol>
   * <li>"question": the Question Icon
   * <li>"warning": the Warning Icon
   * <li>"write": the Write Icon
   * <li>"conflict": the Conflict Icon
   * <li>any URL: an URL that will be used for finding the image (see ImageIcon.ImageIcon( URL ) )
   * <li>null: returns null
   * </ol>
   * 
   * @see KalypsoStatusUtils#ICON_QUESTION
   * @see KalypsoStatusUtils#ICON_WARNING
   * @see KalypsoStatusUtils#ICON_WRITE
   * @see KalypsoStatusUtils#ICON_CONFLICT
   * @see ImageIcon#ImageIcon(java.net.URL)
   */
  public static ImageIcon getIconFor( final String iconDescription )
  {
    if( iconDescription == null )
      return null;

    if( "question".equalsIgnoreCase( iconDescription ) ) //$NON-NLS-1$
      return ICON_QUESTION;
    if( "warning".equalsIgnoreCase( iconDescription ) ) //$NON-NLS-1$
      return ICON_WARNING;
    if( "write".equalsIgnoreCase( iconDescription ) ) //$NON-NLS-1$
      return ICON_WRITE;
    if( "conflict".equalsIgnoreCase( iconDescription ) ) //$NON-NLS-1$
      return ICON_CONFLICT;

    try
    {
      return new ImageIcon( new URL( iconDescription ) );
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * @return the tooltip that best fits the mask, or null if no fit
   */
  public static String getTooltipFor( final int mask )
  {
    if( checkMask( mask, KalypsoStati.BIT_OK ) )
      return "OK"; //$NON-NLS-1$

    if( checkMask( mask, KalypsoStati.BIT_CHECK ) )
      return Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoStatusUtils.21" ); //$NON-NLS-1$

    if( checkMask( mask, KalypsoStati.BIT_REQUIRED ) )
      return Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoStatusUtils.22" ); //$NON-NLS-1$

    if( checkMask( mask, KalypsoStati.BIT_USER_MODIFIED ) )
      return Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoStatusUtils.23" ); //$NON-NLS-1$

    if( checkMask( mask, KalypsoStati.BIT_DERIVATION_ERROR ) )
      return Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoStatusUtils.24" ); //$NON-NLS-1$

    if( checkMask( mask, KalypsoStati.BIT_DERIVATED ) )
      return Messages.getString( "org.kalypso.ogc.sensor.status.KalypsoStatusUtils.25" ); //$NON-NLS-1$

    return ""; //$NON-NLS-1$
  }

  /**
   * @return mask dependent color to be used as foreground
   */
  public static Color getForegroundFor( final int mask )
  {
    if( (mask & 0) == 0 )
      return null;

    // currently returns null, but can be customized in the near
    // future
    return null;
  }

  /**
   * @return mask dependent color to be used as background
   */
  public static Color getBackgroundFor( final int mask )
  {
    if( checkMask( mask, KalypsoStati.BIT_DERIVATION_ERROR ) )
      return COLOR_LIGHTYELLOW;

    if( checkMask( mask, KalypsoStati.BIT_DERIVATED ) )
      return COLOR_LIGHTYELLOW;

    // Customization possible in the near future...
    return null;
  }

  /**
   * Combines the stati according to the policy defined when interpolating the corresponding value-axes.
   */
  public static int performInterpolation( final int status1, final int status2 )
  {
    return (status1 | status2) & KalypsoStati.MASK_INTERPOLATION;
  }

  /**
   * Combines the stati according to the policy defined when performing arithmetic operations on the corresponding
   * value-axes.
   */
  public static int performArithmetic( final int[] stati )
  {
    int status = 0;
    for( final int element : stati )
    {
      status |= element;
    }

    return status & KalypsoStati.MASK_ARITHMETIC;
  }

  /**
   * Combines the stati according to the policy defined when performing arithmetic operations on the corresponding
   * value-axes.
   */
  public static int performArithmetic( final int status1, final int status2 )
  {
    return (status1 | status2) & KalypsoStati.MASK_ARITHMETIC;
  }

  /**
   * Get the combined status for the given axis.<br>
   * If the axis has no corresponding status axis, BIT_OK is returned.<br>
   * Else, the or'ed value of all stati is returned.
   * 
   * @throws SensorException
   */
  public static int getStatus( final ITupleModel values, final IAxis axis ) throws SensorException
  {
    int mergedStatus = KalypsoStati.BIT_OK;

    final IAxis statusAxis = KalypsoStatusUtils.findStatusAxisForNoEx( values.getAxes(), axis );
    if( statusAxis == null )
      return mergedStatus;

    for( int i = 0; i < values.size(); i++ )
    {
      final Object statusObject = values.get( i, statusAxis );
      final int status = ((Number)statusObject).intValue();
      mergedStatus |= status;
    }

    return mergedStatus;
  }

  /**
   * Counts the occurrence of a given status bit.
   * 
   * @throws SensorException
   */
  public static int countStatus( final ITupleModel values, final IAxis axis, final int mask ) throws SensorException
  {
    final IAxis statusAxis = KalypsoStatusUtils.findStatusAxisFor( values.getAxes(), axis );
    int count = 0;
    for( int i = 0; i < values.size(); i++ )
    {
      final int status = ((Number)values.get( i, statusAxis )).intValue();
      if( checkMask( status, mask ) )
        count++;
    }

    return count;
  }

  public static IAxis findAxisForStatusAxis( final IAxis[] axes, final IAxis statusAxis )
  {
    final String valueAxisName = getAxisNameFor( statusAxis );
    return ObservationUtilities.findAxisByNameNoEx( axes, valueAxisName );
  }
}