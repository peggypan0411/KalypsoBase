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
package org.kalypso.commons.java.net.test;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.kalypso.contribs.java.net.UrlUtilities;

/**
 * UrlUtilitiesTest
 * 
 * @author schlienger
 */
public class UrlUtilitiesTest extends TestCase
{

  public void testResolveURL( ) throws MalformedURLException
  {
    final URL base = new URL( "file://c/temp/hello" ); //$NON-NLS-1$

    final UrlUtilities urlUtilities = new UrlUtilities();
    final URL u1 = urlUtilities.resolveURL( base, "" ); //$NON-NLS-1$
    System.out.println( u1 );
    assertEquals( base, u1 );

    final URL u2 = urlUtilities.resolveURL( base, "world/toYou.txt" ); //$NON-NLS-1$
    System.out.println( u2 );
  }

}
