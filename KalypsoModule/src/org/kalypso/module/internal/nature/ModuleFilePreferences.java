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
package org.kalypso.module.internal.nature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.kalypso.module.nature.IModulePreferences;
import org.kalypso.module.nature.ModuleNature;
import org.kalypso.module.nature.ModuleUtils;
import org.osgi.framework.Version;

/**
 * {@link IModulePreferences} implementation that reads the settings from a project in the file system.<br/>
 * Used to read module information from an external project.
 * 
 * @author Gernot Belger
 */
public class ModuleFilePreferences implements IModulePreferences
{
  private final Properties m_node = new Properties();

  public ModuleFilePreferences( final File projectDir )
  {
    final File settingsDir = new File( projectDir, ".settings" ); //$NON-NLS-1$
    final File prefsFile = new File( settingsDir, ModuleNature.ID + ".prefs" ); //$NON-NLS-1$
    FileInputStream inputStream = null;
    try
    {
      if( prefsFile.isFile() )
      {
        inputStream = new FileInputStream( prefsFile );
        m_node.load( inputStream );
        inputStream.close();
      }
    }
    catch( final IOException e )
    {
      e.printStackTrace();
    }
    finally
    {
      IOUtils.closeQuietly( inputStream );
    }
  }

  /**
   * @see org.kalypso.module.nature.IModulePreferences#setModule(java.lang.String)
   */
  @Override
  public void setModule( final String moduleID )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.kalypso.module.nature.IModulePreferences#getModule()
   */
  @Override
  public String getModule( )
  {
    return m_node.getProperty( PREFERENCE_MODULE, null );
  }

  /**
   * @see org.kalypso.module.nature.IModulePreferences#setVersion(org.osgi.framework.Version)
   */
  @Override
  public void setVersion( final Version version )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.kalypso.module.nature.IModulePreferences#getVersion()
   */
  @Override
  public Version getVersion( )
  {
    final String property = m_node.getProperty( PREFERENCE_VERSION, null );
    return ModuleUtils.parseVersion( property );
  }

}
