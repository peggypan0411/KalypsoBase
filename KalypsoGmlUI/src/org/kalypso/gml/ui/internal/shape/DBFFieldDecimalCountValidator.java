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
package org.kalypso.gml.ui.internal.shape;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.kalypso.gml.ui.i18n.Messages;
import org.kalypso.shape.dbf.DBaseException;

/**
 * @author Gernot Belger
 */
public class DBFFieldDecimalCountValidator implements IValidator
{
  private final DBFFieldBean m_field;

  public DBFFieldDecimalCountValidator( final DBFFieldBean field )
  {
    m_field = field.copy();
  }

  @Override
  public IStatus validate( final Object value )
  {
    if( !(value instanceof Number) )
      return ValidationStatus.error( Messages.getString( "DBFFieldDecimalCountValidator_0" ) ); //$NON-NLS-1$

    try
    {
      m_field.setDecimalCount( ((Number) value).shortValue() );
      return ValidationStatus.ok();
    }
    catch( final DBaseException e )
    {
      return ValidationStatus.error( e.getLocalizedMessage(), e );
    }
  }
}
