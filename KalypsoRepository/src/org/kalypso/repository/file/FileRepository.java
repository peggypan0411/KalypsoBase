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
package org.kalypso.repository.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.contribs.java.io.filter.AcceptAllFileFilter;
import org.kalypso.repository.AbstractRepository;
import org.kalypso.repository.IRepositoryItem;

/**
 * Ein File Repository.
 *
 * @author schlienger
 */
public class FileRepository extends AbstractRepository
{
  protected final File m_root;

  protected final FileFilter m_filter;

  /**
   * Creates a FileRepository.
   *
   * @param factory
   * @param conf
   * @param location
   *          path of the root
   * @param identifier
   *          user defined identifier for this repository
   * @param readOnly
   *          if true the repository is read only
   * @param filter
   *          [optional] if null an <code>AcceptAllFileFilter</code> is used.
   */
  public FileRepository( final String factory, final String conf, final String location, final String identifier, final boolean readOnly, final boolean cached, final FileFilter filter )
  {
    super( identifier, identifier, factory, conf, readOnly, cached, identifier );

    if( filter == null )
      m_filter = new AcceptAllFileFilter();
    else
      m_filter = filter;

    m_root = new File( location );
    if( !m_root.exists() )
      throw new IllegalArgumentException( "Location existiert nicht! (Location: " + location + ")" ); //$NON-NLS-1$ //$NON-NLS-2$

  }

  /**
   * @see org.kalypso.repository.IRepository#getDescription()
   */
  @Override
  public String getDescription( )
  {
    return m_root.toString();
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#getChildren()
   */
  @Override
  public IRepositoryItem[] getChildren( )
  {
    return createItem( m_root ).getChildren();
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#hasChildren()
   */
  @Override
  public boolean hasChildren( )
  {
    return m_root.isDirectory();
  }

  public FileFilter getFilter( )
  {
    return m_filter;
  }

  /**
   * Factory method that can be overriden by subclasses to create adequate items.
   *
   * @param file
   * @return IRepositoryItem instance
   */
  public FileItem createItem( final File file )
  {
    return new FileItem( this, file );
  }

  /**
   * @see org.kalypso.repository.IRepository#reload()
   */
  @Override
  public void reload( )
  {
    fireRepositoryStructureChanged();
  }

  /**
   * @see org.kalypso.repository.IRepository#findItem(java.lang.String)
   */
  @Override
  public IRepositoryItem findItem( final String id )
  {
    // both lowercase to be sure comparison is done homogeneously
    final String baseId = getIdentifier().toLowerCase();
    final String itemId = id.toLowerCase();

    final String scheme = baseId + ":/"; //$NON-NLS-1$

    if( !itemId.startsWith( scheme ) )
      return null;

    // absolute path of the root (replace backslashes on windows with forward
    // slashes)
    final String strRoot = m_root.getAbsolutePath().replace( '\\', '/' );

    // replaceFirst can not handle "$" in itemId, so replaced by next line
    // final String path = itemId.replaceFirst( scheme, strRoot );
    final String path = strRoot + itemId.replaceFirst( scheme, "" ); //$NON-NLS-1$
    final File f = new File( path );

    if( !f.exists() )
      return null;

    if( !m_filter.accept( f ) )
      return null;

    if( !FileUtilities.isChildOf( m_root, f ) )
      return null;

    return createItem( f );
  }

  @Override
  public Object getAdapter( final Class anotherClass )
  {
    if( File.class.equals( anotherClass ) )
      return m_root;

    return super.getAdapter( anotherClass );
  }

  public void makeItem( final File dir ) throws IOException
  {
    if( !dir.exists() )
      FileUtils.forceMkdir( dir );

    fireRepositoryStructureChanged();
  }

  public void deleteItem( final FileItem item ) throws IOException
  {
    final File file = item.getFile();

    if( file.isDirectory() )
      FileUtils.deleteDirectory( file );
    else
      FileUtils.forceDelete( file );

    fireRepositoryStructureChanged();
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#hasAdapter(java.lang.Class)
   */
  @Override
  public boolean hasAdapter( final Class< ? > adapter )
  {
    if( getAdapter( adapter ) == null )
      return false;

    return true;
  }

}