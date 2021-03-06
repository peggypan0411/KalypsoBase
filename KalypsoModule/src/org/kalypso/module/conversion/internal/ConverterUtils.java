/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.module.conversion.internal;

import java.io.File;

import org.kalypso.module.conversion.IProject2ProjectConverterFactory;
import org.kalypso.module.conversion.IProjectConverter;
import org.kalypso.module.conversion.IProjectConverterFactory;
import org.kalypso.module.conversion.IProjectConverterInPlaceFactory;
import org.osgi.framework.Version;

/**
 * @author Gernot Belger
 */
public final class ConverterUtils
{
  public static final IProjectConverter[] createConverters( final IProjectConverterFactory[] factories, final Version sourceVersion, final File sourceDir, final File targetDir )
  {
    final IProjectConverter[] converter = new IProjectConverter[factories.length];
    for( int i = 0; i < converter.length; i++ )
    {
      final IProjectConverterFactory factory = factories[i];
      converter[i] = createConverter( factory, sourceVersion, sourceDir, targetDir );
    }

    return converter;
  }

  public static IProjectConverter createConverter( final IProjectConverterFactory factory, final Version sourceVersion, final File sourceDir, final File targetDir )
  {
    if( factory instanceof IProject2ProjectConverterFactory )
      return ((IProject2ProjectConverterFactory) factory).createConverter( sourceVersion, sourceDir, targetDir );

    if( factory instanceof IProjectConverterInPlaceFactory )
      return ((IProjectConverterInPlaceFactory) factory).createConverter( sourceVersion, targetDir );

    throw new UnsupportedOperationException( String.format( "Unknown factory type: %s", factory.getClass().getName() ) ); //$NON-NLS-1$
  }
}
