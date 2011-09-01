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
package org.kalypso.ogc.core.utils.internal.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.kalypso.contribs.java.lang.NumberUtils;
import org.kalypso.ogc.core.service.IOGCService;

/**
 * This comparator can compare {@link org.kalypso.ogc.core.service.IOGCService}'s. It will compare them by their
 * version.
 * 
 * @author Toni DiNardo
 */
public class OGCServiceComparator implements Comparator<IOGCService>
{
  /**
   * The constructor.
   */
  public OGCServiceComparator( )
  {
  }

  /**
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare( IOGCService o1, IOGCService o2 )
  {
    /* Get the versions. */
    String version1 = o1.getVersion();
    String version2 = o2.getVersion();
    if( version1 == null && version2 != null )
      return -1;

    if( version1 == null && version2 == null )
      return 0;

    if( version1 != null && version2 == null )
      return 1;

    /* Get the trimmed versions. */
    String versionTrimmed1 = version1.trim();
    String versionTrimmed2 = version2.trim();
    if( versionTrimmed1.length() == 0 && versionTrimmed2.length() > 0 )
      return -1;

    if( versionTrimmed1.length() == 0 && versionTrimmed2.length() == 0 )
      return 0;

    if( versionTrimmed1.length() > 0 && versionTrimmed2.length() == 0 )
      return 1;

    /* Get the splitted versions. */
    String[] versionSplit1 = StringUtils.split( version1, "." );
    String[] versionSplit2 = StringUtils.split( version2, "." );
    for( int i = 0; i < versionSplit1.length; i++ )
    {
      /* Get the version fragment of version 1. */
      String versionFragment1 = versionSplit1[i];

      /* In this case (version1 = 1.0.0.1 and version2 = 1.0.0) the version 1 is greater. */
      if( i >= versionSplit2.length )
        return -1;

      /* Get the version fragment of version 2. */
      String versionFragment2 = versionSplit2[i];

      /* Get the integer. */
      int versionFragmentInt1 = NumberUtils.parseQuietInt( versionFragment1, 0 );
      int versionFragmentInt2 = NumberUtils.parseQuietInt( versionFragment2, 0 );

      /* Check the next version fragment. */
      if( versionFragmentInt1 == versionFragmentInt2 )
        continue;

      if( versionFragmentInt1 > versionFragmentInt2 )
        return -1;

      return 1;
    }

    /* In this case (version1 = 1.0.0 and version2 = 1.0.0.1) the version 2 is greater. */
    if( version1.length() < version2.length() )
      return 1;

    /* In this case (version1 = 1.0.0 and version2 = 1.0.0), the versions are equal. */
    /* Also the length of both arrays are equal. */
    return 0;
  }
}