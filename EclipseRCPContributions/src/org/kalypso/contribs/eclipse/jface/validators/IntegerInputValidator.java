/*
 *  This file is part of Kalypso
 *
 *  Copyright (c) 2008 by
 *
 *  Bj�rnsen Beratende Ingenieure GmbH, Koblenz, Germany (Bjoernsen Consulting Engineers), http://www.bjoernsen.de
 *  Technische Universit�t Hamburg-Harburg, Institut f�r Wasserbau, Hamburg, Germany
 *  (Technical University Hamburg-Harburg, Institute of River and Coastal Engineering), http://www.tu-harburg.de/wb/
 *
 *  Kalypso is free software: you can redistribute it and/or modify it under the terms  
 *  of the GNU Lesser General Public License (LGPL) as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  Kalypso is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with Kalypso.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kalypso.contribs.eclipse.jface.validators;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * This validater checks for integers.
 * 
 * @author Holger Albert
 */
public class IntegerInputValidator implements IInputValidator
{
  /**
   * The constructor.
   */
  public IntegerInputValidator( )
  {
  }

  /**
   * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
   */
  @Override
  public String isValid( final String newText )
  {
    try
    {
      Integer.parseInt( newText );
    }
    catch( final NumberFormatException e )
    {
      return "Please insert a valid number without fraction digits.";
    }

    return null;
  }
}