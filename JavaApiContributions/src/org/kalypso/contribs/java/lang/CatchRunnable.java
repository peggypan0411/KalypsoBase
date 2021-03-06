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

/**
 * <p>
 * Helper-Klasse f�r Runnable's mit geworfenen Exceptions.
 * </p>
 * <p>
 * Implementierende Klassen m�ssen die Methode {@link #runIntern()}�berschreiben.
 * </p>
 * <p>
 * Eventuell geworfene Exception k�nne n dann durch getThrown geholt werden
 * </p>
 * 
 * @author belger
 */
public abstract class CatchRunnable implements Runnable
{
  private Throwable m_throwable = null;

  /**
   * @see java.lang.Runnable#run()
   */
  @Override
  public final void run( )
  {
    try
    {
      runIntern();
    }
    catch( final Throwable t )
    {
      t.printStackTrace();
      m_throwable = t;
    }
  }

  protected abstract void runIntern( ) throws Throwable;

  public Throwable getThrown( )
  {
    return m_throwable;
  }
}
