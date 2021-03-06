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
package org.kalypso.commons.resources;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.kalypso.commons.java.lang.Objects;

/**
 * FileUtilities
 * 
 * @author schlienger
 */
public class FileUtilities
{
  private FileUtilities( )
  {
    // not intended to be instanciated
  }

  public static String toString( final IFile file ) throws IOException, CoreException
  {
    InputStream is = null;
    try
    {
      is = file.getContents();
      final String content = IOUtils.toString( is, file.getCharset() );
      is.close();
      return content;
    }
    finally
    {
      IOUtils.closeQuietly( is );
    }
  }

  /**
   * Returns the content of the given file as string object. The charset of the file object is used.
   * <p>
   * Alle exceptions are caught, if any is thrown, null is returned.
   * </p>
   * 
   * @return null, if any error occurs.
   */
  public static String toStringQuietly( final IFile file )
  {
    InputStream contents = null;
    try
    {
      contents = file.getContents();
      final String content = IOUtils.toString( contents, file.getCharset() );
      contents.close();
      return content;
    }
    catch( final CoreException e )
    {
      return null;
    }
    catch( final IOException e )
    {
      return null;
    }
    finally
    {
      IOUtils.closeQuietly( contents );
    }
  }

  public static IFile toFile( final IPath fullPath )
  {
    if( fullPath == null )
      return null;

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    return root.getFile( fullPath );
  }

  public static boolean isParent( final IResource base, final IResource current )
  {
    final IContainer parent = current.getParent();
    if( Objects.isNull( parent ) )
      return false;
    else if( base.equals( parent ) )
      return true;

    return isParent( base, parent );
  }
}