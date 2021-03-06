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
package org.kalypso.contribs.java.lang;

import java.util.LinkedList;
import java.util.List;

/**
 * MultiException can contain a list of exception. It is used as a container for exceptions that might occur during a
 * loop processing, but that should not break the loop. Better, the exceptions that occur are caught and added to this
 * container. Once the loop processing is finished, the method isEmpty() can be called to check if it is necessary to
 * throw the MultiException or not.
 *
 * @author schlienger
 */
public class MultiException extends Exception
{
  private List<Exception> m_exceptions = null;

  public MultiException( )
  {
    super();
  }

  @Override
  protected void finalize( ) throws Throwable
  {
    if( !isEmpty() )
      m_exceptions.clear();

    super.finalize();
  }

  public void addException( final Exception e )
  {
    if( m_exceptions == null )
      m_exceptions = new LinkedList<>();

    m_exceptions.add( e );
  }

  @Override
  public String getMessage( )
  {
    if( isEmpty() )
      return "";

    final StringBuffer bf = new StringBuffer();
    for( final Exception exception : m_exceptions )
      bf.append( exception.getMessage() ).append( '\n' );

    return bf.toString();
  }

  /**
   * @see java.lang.Throwable#getLocalizedMessage()
   */
  @Override
  public String getLocalizedMessage( )
  {
    if( isEmpty() )
      return "";

    final StringBuffer bf = new StringBuffer();
    for( final Exception exception : m_exceptions )
      bf.append( exception.getLocalizedMessage() ).append( '\n' );

    return bf.toString();
  }

  /**
   * @return true if this MultiException container does not contain any exceptions
   */
  public boolean isEmpty( )
  {
    return m_exceptions == null || m_exceptions.size() == 0;
  }

  @Override
  public synchronized Throwable getCause( )
  {
    if( m_exceptions.size() == 0 )
      return super.getCause();
    return m_exceptions.get( 0 );
  }
}
