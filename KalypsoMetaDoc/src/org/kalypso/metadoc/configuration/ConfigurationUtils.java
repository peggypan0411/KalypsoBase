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

package org.kalypso.metadoc.configuration;

import java.util.Arrays;

import org.apache.commons.collections.ExtendedProperties;

/**
 * We are using commons configuration 1.1 and its bugy: <br>
 * MapConfiguration.addProperty() does not adhere to its contract, i.e. when a property for that key is already present,
 * then make a list out of the properties. The MapConfiguration doesn't do this, it simply overwrites the value!
 * <p>
 * So I decided to use a BaseConfiguration instead of a MapConfiguration. But, I also needed a Map having a
 * Configuration, so I wrote the utility method createMap().
 *
 * @author schlienger
 */
public final class ConfigurationUtils
{
  private ConfigurationUtils( )
  {
    // not intended to be instantiated
  }

  /**
   * Add the value to the given property. If the property is already defined, it adds the value only if it is not
   * already existing in the list of values for that property.
   */
  public static void addPropertyDistinct( final ExtendedProperties conf, final String property, final String value )
  {
    if( conf == null || value == null || property == null )
      return;

    final String[] array = conf.getStringArray( property );
    if( array == null || !Arrays.asList( array ).contains( value ) )
      conf.addProperty( property, value );
  }
}
