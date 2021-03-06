/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestraße 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 * 
 *  and
 *  
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Contact:
 * 
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *   
 *  ---------------------------------------------------------------------------*/
package org.kalypso.commons.databinding.property.value;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.value.ValueProperty;
import org.kalypso.commons.databinding.observable.value.ITypedObservableValue;

/**
 * @author Gernot Belger
 * @author Holger Albert
 */
public abstract class TypedValueProperty<SOURCE, VALUE> extends ValueProperty
{
  private final Class<VALUE> m_valueType;

  private final Class<SOURCE> m_sourceType;

  public TypedValueProperty( final Class<SOURCE> sourceType, final Class<VALUE> valueType )
  {
    m_sourceType = sourceType;
    m_valueType = valueType;
  }

  /**
   * @see org.eclipse.core.databinding.property.value.IValueProperty#getValueType()
   */
  @Override
  public Object getValueType( )
  {
    return m_valueType;
  }

  /**
   * @see org.eclipse.core.databinding.property.value.IValueProperty#observe(org.eclipse.core.databinding.observable.Realm,
   *      java.lang.Object)
   */
  @Override
  public ITypedObservableValue<SOURCE, VALUE> observe( final Realm realm, final Object source )
  {
    return doObserve( realm, m_sourceType.cast( source ) );
  }

  /**
   * @see org.eclipse.core.databinding.property.value.IValueProperty#observeDetail(org.eclipse.core.databinding.observable.list.IObservableList)
   */
  @Override
  public IObservableList observeDetail( final IObservableList master )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.eclipse.core.databinding.property.value.IValueProperty#observeDetail(org.eclipse.core.databinding.observable.set.IObservableSet)
   */
  @Override
  public IObservableMap observeDetail( final IObservableSet master )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.eclipse.core.databinding.property.value.IValueProperty#observeDetail(org.eclipse.core.databinding.observable.map.IObservableMap)
   */
  @Override
  public IObservableMap observeDetail( final IObservableMap master )
  {
    throw new UnsupportedOperationException();
  }

  protected abstract ITypedObservableValue<SOURCE, VALUE> doObserve( Realm realm, SOURCE source );
}