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
package org.kalypso.ogc.gml.table.celleditors;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.kalypso.contribs.java.lang.NumberUtils;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * TODO: hook into a more general validation framework
 * 
 * @author Gernot Belger
 */
public class DefaultCellValidators
{
  public static final ICellEditorValidator DOUBLE_VALIDATOR = new DoubleCellValidator();

  public static final ICellEditorValidator INTEGER_VALIDATOR = new IntegerCellValidator();

  public static final ICellEditorValidator BOOLEAN_VALIDATOR = new BooleanCellValidator();

  public static final class BooleanCellValidator implements ICellEditorValidator
  {
    /**
     * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
     */
    @Override
    public String isValid( final Object value )
    {
      try
      {
        if( value != null )
          Boolean.valueOf( value.toString() );

        return null;
      }
      catch( final NumberFormatException nfe )
      {
        return Messages.getString( "org.kalypso.ogc.gml.table.celleditors.DefaultCellValidators.0" ); //$NON-NLS-1$
      }
    }
  }

  public static final class DoubleCellValidator implements ICellEditorValidator
  {
    /**
     * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
     */
    @Override
    public String isValid( final Object value )
    {
      try
      {
        if( value == null )
          return null;

        final String string = value.toString();
        if( string.isEmpty() )
          return null;

        NumberUtils.parseDouble( string );

        return null;
      }
      catch( final NumberFormatException nfe )
      {
        return Messages.getString( "org.kalypso.ogc.gml.table.celleditors.DefaultCellValidators.1" ); //$NON-NLS-1$
      }
    }
  }

  public static final class IntegerCellValidator implements ICellEditorValidator
  {
    /**
     * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
     */
    @Override
    public String isValid( final Object value )
    {
      try
      {
        if( value == null )
          return null;

        final String string = value.toString();
        if( string.isEmpty() )
          return null;

        Integer.parseInt( value.toString() );

        return null;
      }
      catch( final NumberFormatException nfe )
      {
        return Messages.getString( "org.kalypso.ogc.gml.table.celleditors.DefaultCellValidators.2" ); //$NON-NLS-1$
      }
    }
  }
}
