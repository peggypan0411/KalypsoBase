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
package org.kalypso.commons.tokenreplace;

/**
 * @author doemming
 */
public class DefaultTokenReplacer implements ITokenReplacer
{
  private final String m_token;

  private final String m_replaceString;

  public DefaultTokenReplacer( final String token, final String replaceString )
  {
    m_token = token;
    m_replaceString = replaceString;
  }

  /**
   * @see org.kalypso.commons.tokenreplace.ITokenReplacer#replaceToken(java.lang.Object, java.lang.String)
   */
  @Override
  public String replaceToken( final Object value, final String argument )
  {
    return m_replaceString;
  }

  /**
   * @see org.kalypso.commons.tokenreplace.ITokenReplacer#getToken()
   */
  @Override
  public String getToken( )
  {
    return m_token;
  }

}
