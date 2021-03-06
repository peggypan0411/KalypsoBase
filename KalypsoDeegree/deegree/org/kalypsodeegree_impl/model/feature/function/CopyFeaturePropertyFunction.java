/** This file is part of kalypso/deegree.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * history:
 * 
 * Files in this package are originally taken from deegree and modified here
 * to fit in kalypso. As goals of kalypso differ from that one in deegree
 * interface-compatibility to deegree is wanted but not retained always. 
 * 
 * If you intend to use this software in other ways than in kalypso 
 * (e.g. OGC-web services), you should consider the latest version of deegree,
 * see http://www.deegree.org .
 *
 * all modifications are licensed as deegree, 
 * original copyright:
 *
 * Copyright (C) 2001 by:
 * EXSE, Department of Geography, University of Bonn
 * http://www.giub.uni-bonn.de/exse/
 * lat/lon GmbH
 * http://www.lat-lon.de
 */
package org.kalypsodeegree_impl.model.feature.function;

import java.util.Map;

import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree_impl.model.feature.FeaturePropertyFunction;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPathException;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPathUtilities;

/**
 * This function property simply copies the value from another property.
 * <p>
 * Parameters:
 * <ul>
 * <li>xpath (String): The xpath relative to the current feature pointing to the copied property.</li>
 * <li>copySet (Boolean): If true, setting the property causes the copied property to be set.</li>
 * </ul>
 * </p>
 * 
 * @author Gernot Belger
 */
public class CopyFeaturePropertyFunction extends FeaturePropertyFunction
{
  private GMLXPath m_xpath;

  private boolean m_copySet;

  /**
   * @see org.kalypsodeegree_impl.model.feature.FeaturePropertyFunction#init(java.util.Properties)
   */
  @Override
  public void init( final Map<String, String> properties )
  {
    m_xpath = new GMLXPath( properties.get( "xpath" ) );
    m_copySet = Boolean.parseBoolean( properties.get( "copySet" ) );
  }

  /**
   * @see org.kalypsodeegree.model.feature.IFeaturePropertyHandler#setValue(org.kalypsodeegree.model.feature.Feature,
   *      org.kalypso.gmlschema.property.IPropertyType, java.lang.Object)
   */
  @Override
  public Object setValue( final Feature feature, final IPropertyType pt, final Object valueToSet )
  {
    if( m_copySet )
    {
      // TODO: is it possible?
    }

    return valueToSet;
  }

  /**
   * @see org.kalypsodeegree.model.feature.IFeaturePropertyHandler#getValue(org.kalypsodeegree.model.feature.Feature,
   *      org.kalypso.gmlschema.property.IPropertyType, java.lang.Object)
   */
  @Override
  public Object getValue( final Feature feature, final IPropertyType pt, final Object currentValue )
  {
    try
    {
      // remark: works only on changes or active repaint of map
      // set to null for redraw
      // of requested feature with changed geometry
      // feature.setCachedGeometry( null );
      return GMLXPathUtilities.query( m_xpath, feature );
    }
    catch( final GMLXPathException e )
    {
      KalypsoDeegreePlugin.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );

      /* If an error occurs, return the original value. */
      return currentValue;
    }

  }

}
