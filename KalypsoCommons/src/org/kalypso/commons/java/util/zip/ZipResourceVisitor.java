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
package org.kalypso.commons.java.util.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.KalypsoCommonsPlugin;
import org.kalypso.commons.internal.i18n.Messages;

/**
 * A {@link org.eclipse.core.resources.IResourceVisitor}which puts all visited resources in a single zip file.
 *
 * @author belger
 */
public class ZipResourceVisitor implements IResourceVisitor
{
  public static enum PATH_TYPE
  {
    PROJECT_RELATIVE,
    PLATFORM_RELATIVE
  }

  private final ZipOutputStream m_zos;

  private final Set<String> m_entries = new HashSet<>();

  private PATH_TYPE m_pathType = PATH_TYPE.PROJECT_RELATIVE;

  public ZipResourceVisitor( final File zipfile ) throws FileNotFoundException
  {
    m_zos = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream( zipfile ) ) );
  }

  public ZipResourceVisitor( final File zipfile, final PATH_TYPE pathType ) throws FileNotFoundException
  {
    this( zipfile );
    m_pathType = pathType;
  }

  /**
   * A type of path to use for internal zip-file structure. By default, project relative paths will be used. Platform
   * relative paths are intended for accessing data from several projects at once.
   */
  public void setPathType( final PATH_TYPE pathType )
  {
    m_pathType = pathType;
  }

  /**
   * Must be called, to close Zip-File
   *
   * @throws IOException
   */
  public void close( ) throws IOException
  {
    m_zos.close();
  }

  /**
   * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
   */
  @Override
  public boolean visit( final IResource resource ) throws CoreException
  {
    if( resource.getType() == IResource.FILE )
    {
      final IFile file = (IFile) resource;
      final String relativePathTo;
      switch( m_pathType )
      {
        case PLATFORM_RELATIVE:
          relativePathTo = file.getFullPath().makeRelative().toString();
          break;
        case PROJECT_RELATIVE:
        default:
          relativePathTo = file.getProjectRelativePath().toString();
          break;
      }
      try
      {
        if( !m_entries.contains( relativePathTo ) )
        {
          m_entries.add( relativePathTo );
          ZipUtilities.writeZipEntry( m_zos, file.getLocation().toFile(), relativePathTo );
        }
      }
      catch( final IOException e )
      {
        final IStatus status = new Status( IStatus.ERROR, KalypsoCommonsPlugin.getID(), 0, Messages.getString( "org.kalypso.commons.java.util.zip.ZipResourceVisitor0" ) + relativePathTo, e ); //$NON-NLS-1$
        throw new CoreException( status );
      }
    }

    return true;
  }
}