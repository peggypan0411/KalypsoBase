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
package org.kalypso.core.util.pool;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;

import org.kalypso.contribs.java.net.UrlResolver;

public final class KeyComparator implements Comparator<IPoolableObjectType>
{
  private static KeyComparator INSTANCE;

  public static KeyComparator getInstance( )
  {
    if( INSTANCE == null )
      INSTANCE = new KeyComparator();

    return INSTANCE;
  }

  private final UrlResolver m_urlResolver = new UrlResolver();

  private KeyComparator( )
  {
    // is a singleton
  }

  @Override
  public int compare( final IPoolableObjectType k1, final IPoolableObjectType k2 )
  {
    final int typeCompare = k1.getType().compareToIgnoreCase( k2.getType() );
    if( typeCompare != 0 )
      return typeCompare;

    final String source1 = resolveUri( k1 );
    final String source2 = resolveUri( k2 );

    return source1.compareTo( source2 );
  }

  private String resolveUri( final IPoolableObjectType key )
  {
    final String location = key.getLocation();
    final URL context = key.getContext();
    try
    {
      if( location.startsWith( "urn:" ) ) //$NON-NLS-1$
        return location;

      final URL sourceURL = m_urlResolver.resolveURL( context, location );
      return sourceURL.toExternalForm();
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();

      return context.toExternalForm() + "#" + location; //$NON-NLS-1$
    }
  }
}