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
package org.kalypso.shape.deegree;

import org.kalypso.shape.ShapeDataException;
import org.kalypso.shape.dbf.IDBFField;
import org.kalypso.shape.dbf.IDBFValue;
import org.kalypsodeegree.model.feature.Feature;

public class TinValue implements IDBFValue
{
  private final IDBFValue m_delegate;

  public TinValue( final IDBFValue delegate )
  {
    m_delegate = delegate;
  }

  /**
   * @see org.kalypso.shape.dbf.IDBFValue#getField()
   */
  @Override
  public IDBFField getField( ) throws ShapeDataException
  {
    return m_delegate.getField();
  }

  /**
   * @see org.kalypso.shape.dbf.IDBFValue#getValue(java.lang.Object)
   */
  @Override
  public Object getValue( final Object element ) throws ShapeDataException
  {
    final TinPointer pointer = (TinPointer) element;
    final Feature feature = pointer.getFeature();
    return m_delegate.getValue( feature );
  }

  /**
   * @see org.kalypso.shape.dbf.IDBFValue#getLabel()
   */
  @Override
  public String getLabel( )
  {
    return m_delegate.getLabel();
  }

}
