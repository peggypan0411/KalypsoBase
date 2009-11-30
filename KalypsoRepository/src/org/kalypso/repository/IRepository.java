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
package org.kalypso.repository;

import java.io.Writer;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Eingangspunkt zu einem Repository. Es liefert z.B. die ersten Items. Damit kann eine Struktur aufgebaut werden.
 * 
 * @author schlienger
 */
public interface IRepository extends IRepositoryItem
{
  /**
   * @return the classname of the factory that was used to create this repository
   */
  public String getFactory( );

  /**
   * @return the configuration string used to create the repository
   */
  public String getConfiguration( );

  /**
   * @return name of the repository
   */
  public String getName( );

  /**
   * @return display name of the repository
   */
  public String getLabel( );

  /**
   * @return some description
   */
  public String getDescription( );

  /**
   * Returns true when this repository is in readonly mode. What this really means, depends on the client
   * implementation. Some repositories might only be viewed or browsed, while some others might be modified.
   * 
   * @return readonly flag
   */
  public boolean isReadOnly( );

  /**
   * Finds the item that has the given id.
   * 
   * @param id
   * @return item, or null if not found
   * @throws RepositoryException
   */
  public IRepositoryItem findItem( final String id ) throws RepositoryException;

  /**
   * Sets a property for this repository. Properties can be used internally, by the repository itself, or by its items.
   * 
   * @param name
   * @param value
   */
  public void setProperty( final String name, final String value );

  /**
   * Returns the value of the given property or null if not set yet.
   * 
   * @param name
   * @return property string
   */
  public String getProperty( final String name );

  /**
   * Returns the value of the given property, or defaultValue if no value is set yet.
   * 
   * @param name
   * @param defaultValue
   * @return property string
   */
  public String getProperty( final String name, final String defaultValue );

  /**
   * @return convenience method that returns the properties of this repository in the form of a Properties object.
   */
  public Properties getProperties( );

  /**
   * Convenience method for settings a whole set of properties.
   * 
   * @param props
   */
  public void setProperties( final Properties props );

  /**
   * Forces the reload of the whole Repository structure.
   * 
   * @throws RepositoryException
   */
  public void reload( ) throws RepositoryException;

  /**
   * Dumps the contents (structure and summary of items)
   */
  public void dumpStructure( final Writer writer, final IProgressMonitor monitor ) throws InterruptedException, RepositoryException;

  /**
   * Clears potential resources
   */
  public void dispose( );

  public void addRepositoryListener( final IRepositoryListener l );

  public void removeRepositoryListener( final IRepositoryListener l );

  public void fireRepositoryStructureChanged( );

  /**
   * Sets identifier string of the repository. Needed by proxy repository implementation to overwrite origin repository
   * string.
   */
  public void setIdentifier( String identifier );

}