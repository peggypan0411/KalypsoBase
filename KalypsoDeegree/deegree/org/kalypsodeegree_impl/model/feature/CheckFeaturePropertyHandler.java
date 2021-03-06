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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.IFeaturePropertyHandler;

/**
 * A handler which just checks if the given value fits to the property type. Does nothing else.
 * 
 * @author Gernot Belger
 */
public class CheckFeaturePropertyHandler implements IFeaturePropertyHandler
{
  /**
   * @see org.kalypsodeegree.model.feature.IFeaturePropertyHandler#setValue(org.kalypsodeegree.model.feature.Feature,
   *      org.kalypso.gmlschema.property.IPropertyType, java.lang.Object)
   */
  @Override
  public Object setValue( final Feature feature, final IPropertyType pt, final Object valueToSet )
  {
    if( pt == null )
      throw new IllegalArgumentException( "pt may not null" );

    // Check if value fits to property
    // Example1: QName is xs:double -> value must be Double
    // Exmaple2: property is list -> value must be a list
    if( pt.isList() && !(valueToSet instanceof List) )
      throw new IllegalArgumentException( "Value must be a list for qname: " + pt.getQName() );

    if( !pt.isList() )
    {
      if( pt instanceof IValuePropertyType )
      {
        // TODO:This doesn�t work - what to do?
        // REMARK: WHAT? does not work?? This is a test if the value fits to
        // the type of the property. Test here is necessary because if
        // we do not test here we will get later ClassCastExceptions
        // and there we do not know why.
        // Next: please contact me instead of just commenting it out. Gernot
        final Class< ? > valueClass = ((IValuePropertyType) pt).getTypeHandler().getValueClass();
        if( valueToSet != null && !valueClass.isAssignableFrom( valueToSet.getClass() ) )
        {
          KalypsoDeegreePlugin.getDefault().getLog().log( new Status( IStatus.WARNING, "org.kalypso.deegree", "Wrong type of value (" + valueToSet.getClass() + ") for qname: " + pt.getQName() ) );
          // throw new IllegalArgumentException( "Wrong type of value (" + valueToSet.getClass() + ") for qname: " +
          // pt.getQName() );
        }

        // TODO: the type check should occur on the real value to set, that is after all other handlers
      }
      else if( pt instanceof IRelationType )
      {
        if( valueToSet != null && !(valueToSet instanceof Feature) && !(valueToSet instanceof String) )
          throw new IllegalArgumentException( "Wrong type of value (" + valueToSet.getClass() + ") for qname: " + pt.getQName() );
      }
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
    return currentValue;
  }

  /**
   * @see org.kalypsodeegree.model.feature.IFeaturePropertyHandler#invalidateEnvelope(org.kalypso.gmlschema.property.IPropertyType)
   */
  @Override
  public boolean invalidateEnvelope( final IPropertyType pt )
  {
    return false;
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
