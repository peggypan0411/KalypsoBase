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

package org.kalypsodeegree_impl.model.feature;

import java.util.Comparator;
import java.util.List;

import org.kalypsodeegree.model.feature.Feature;

/**
 * A simple comparator for features which actually compares the value of the given property. A specific comparator can
 * be provided for comparing the values of the property.<br/>
 * TODO: merge with {@link FeatureComparator}
 * 
 * @author schlienger
 * @deprecated Probabyl you should use {@link FeatureComparator}
 */
@Deprecated
public class FeatureComparator2 implements Comparator<Feature>
{
  private final String m_propertyName;

  private final Comparator m_propertyValueComparator;

  /**
   * Constructor without specific property comparator, values must be comparable.
   * 
   * @param propertyName
   *          name of the property to be compared
   */
  public FeatureComparator2( final String propertyName )
  {
    this( propertyName, null );
  }

  /**
   * Constructor with specific property comparator.
   * 
   * @param propertyName
   *          name of the property to be compared
   * @param propertyValueComparator
   *          comparator to use for comparing the values of the given property. Optional, can be null.
   */
  public FeatureComparator2( final String propertyName, final Comparator< ? > propertyValueComparator )
  {
    m_propertyName = propertyName;
    m_propertyValueComparator = propertyValueComparator;
  }

  @Override
  public int compare( final Feature f1, final Feature f2 )
  {
    if( f1 == f2 )
      return 0;

    final Object value1 = f1.getProperty( m_propertyName );
    final Object value2 = f2.getProperty( m_propertyName );

    return compareValues( value1, value2 );
  }

  private int compareValues( final Object value1, final Object value2 )
  {
    if( m_propertyValueComparator != null )
      return m_propertyValueComparator.compare( value1, value2 );

    if( value1 instanceof Comparable< ? > )
      return ((Comparable) value1).compareTo( value2 );

    /**
     * Sepcial case: list-properties: just compare first value
     */
    if( value1 instanceof List< ? > && value2 instanceof List< ? > )
    {
      final List< ? > list1 = (List< ? >) value1;
      final List< ? > list2 = (List< ? >) value2;
      if( list1.isEmpty() || list2.isEmpty() )
        return 0;

      final Object listElement1 = list1.get( 0 );
      final Object listElement2 = list2.get( 0 );
      return compareValues( listElement1, listElement2 );
    }

    return 0;
  }
}
