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
package org.kalypsodeegree_impl.model.feature;

import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.IFeaturePropertyHandler;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

/**
 * This implementation just leaves all values as they are. Used in order to avoid null-checks.
 * 
 * @author Gernot Belger
 */
public class DefaultFeaturePropertyHandler implements IFeaturePropertyHandler
{
  /**
   * @see org.kalypsodeegree.model.feature.IFeaturePropertyHandler#setValue(org.kalypsodeegree.model.feature.Feature,
   *      org.kalypso.gmlschema.property.IPropertyType, java.lang.Object)
   */
  @Override
  public Object setValue( final Feature feature, final IPropertyType pt, final Object valueToSet )
  {
    return valueToSet;
  }

  /**
   * @see org.kalypsodeegree.model.feature.IFeaturePropertyHandler#getValue(org.kalypsodeegree.model.feature.Feature,
   *      org.kalypso.gmlschema.property.IPropertyType, java.lang.Object)
   */
  @Override
  public Object getValue( final Feature feature, final IPropertyType pt, final Object currentValue )
  {
    return currentValue;
  }

  /**
   * @see org.kalypsodeegree.model.feature.IFeaturePropertyHandler#invalidateEnvelope(org.kalypso.gmlschema.property.IPropertyType)
   */
  @Override
  public boolean invalidateEnvelope( final IPropertyType pt )
  {
    return GeometryUtilities.isGeometry( pt );
  }

  /**
   * @see org.kalypsodeegree.model.feature.IFeaturePropertyHandler#isFunctionProperty(org.kalypso.gmlschema.property.IPropertyType)
   */
  @Override
  public boolean isFunctionProperty( final IPropertyType pt )
  {
    return false;
  }

}
