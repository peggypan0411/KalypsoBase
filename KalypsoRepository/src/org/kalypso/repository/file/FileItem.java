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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.repository.IRepository;
import org.kalypso.repository.IRepositoryItem;
import org.kalypso.repository.IRepositoryItemVisitor;
import org.kalypso.repository.RepositoryException;
import org.kalypso.repository.utils.RepositoryVisitors;

/**
 * An item of a <code>FileRepository</code> that represents a <code>File</code>.
 *
 * @author schlienger
 */
public class FileItem implements IRepositoryItem
{
  private final FileRepository m_rep;

  private final File m_file;

  public FileItem( final FileRepository rep, final File file )
  {
    m_rep = rep;
    m_file = file;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#getName()
   */
  @Override
  public String getName( )
  {
    return m_file.getName();
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#getParent()
   */
  @Override
  public IRepositoryItem getParent( )
  {
    return m_rep.createItem( m_file.getParentFile() );
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#getChildren()
   */
  @Override
  public IRepositoryItem[] getChildren( )
  {
    final File[] files = m_file.listFiles( m_rep.getFilter() );
    if( files == null )
      return new IRepositoryItem[] {};

    final IRepositoryItem[] items = new IRepositoryItem[files.length];
    for( int i = 0; i < items.length; i++ )
      items[i] = m_rep.createItem( files[i] );

    return items;
  }

  public File getFile( )
  {
    return m_file;
  }

  public FileRepository getRep( )
  {
    return m_rep;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#hasChildren()
   */
  @Override
  public boolean hasChildren( )
  {
    return m_file.isDirectory();
  }

  @Override
  public String toString( )
  {
    return getName();
  }

  @Override
  public Object getAdapter( final Class anotherClass )
  {
    if( anotherClass == File.class )
      return m_file;

    return null;
  }

  @Override
  public IRepository getRepository( )
  {
    return m_rep;
  }

  /**
   * Returns the identifier of the FileRepository and the relative path of the file in the repository
   *
   * @see org.kalypso.repository.IRepositoryItem#getIdentifier()
   */
  @Override
  public String getIdentifier( )
  {
    return m_rep.getIdentifier() + ":/" + FileUtilities.getRelativePathTo( m_rep.m_root, m_file ).replace( '\\', '/' ); //$NON-NLS-1$
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( final Object obj )
  {
    return EqualsBuilder.reflectionEquals( this, obj );
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode( )
  {
    return HashCodeBuilder.reflectionHashCode( this );
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#hasAdapter(java.lang.Class)
   */
  @Override
  public boolean hasAdapter( final Class< ? > adapter )
  {
    final Object object = getAdapter( adapter );
    if( object == null )
      return false;

    return true;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#isMultipleSourceItem()
   */
  @Override
  public boolean isMultipleSourceItem( )
  {
    return false;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#accept(org.kalypso.repository.IRepositoryItemVisitor)
   */
  @Override
  public void accept( final IRepositoryItemVisitor visitor ) throws RepositoryException
  {
    RepositoryVisitors.accept( this, visitor );
  }
}