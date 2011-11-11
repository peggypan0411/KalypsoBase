/*
 * --------------- Kalypso-Header --------------------------------------------
 *
 * This file is part of kalypso. Copyright (C) 2004, 2005 by:
 *
 * Technical University Hamburg-Harburg (TUHH) Institute of River and coastal engineering Denickestr. 22 21073 Hamburg,
 * Germany http://www.tuhh.de/wb
 *
 * and
 *
 * Bjoernsen Consulting Engineers (BCE) Maria Trost 3 56070 Koblenz, Germany http://www.bjoernsen.de
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Contact:
 *
 * E-Mail: belger@bjoernsen.de schlienger@bjoernsen.de v.doemming@tuhh.de
 *
 * ------------------------------------------------------------------------------------
 */
package org.kalypso.contribs.eclipse.core.resources;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.internal.resources.PlatformURLResourceConnection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.kalypso.contribs.eclipse.core.runtime.PathUtils;

/**
 * ResourceUtilities
 * <p>
 * 
 * @author schlienger (14.06.2005)
 */
public final class ResourceUtilities
{
  private ResourceUtilities( )
  {
    // do not instantiate
  }

  /**
   * Gibt den IFile-Handler zur�ck, falls die URL eine Platform Url denotiert
   * 
   * @see PlatformURLResourceConnection
   */
  public static IFile findFileFromURL( final URL u )
  {
    final IPath path = findPathFromURL( u );
    if( path == null )
      return null;

    return findFileFromPath( path );
  }

  public static File findJavaFileFromURL( final URL url )
  {
    final IPath path = findPathFromURL( url );
    if( path == null )
      return null;

    return makeFileFromPath( path );
  }

  /**
   * Only works with absolute paths. (?)
   */
  public static IFile findFileFromPath( final IPath path )
  {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getFile( path );
  }

  public static IFolder findFolderFromURL( final URL url )
  {
    final IPath path = findPathFromURL( url );
    if( path == null )
      return null;
    return findFolderFromPath( path );
  }

  public static IFolder findFolderFromPath( final IPath path )
  {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getFolder( path );

  }

  /**
   * Resolves an absolute path (i.e. relative to IWorkspaceRoot) and returns its real location.
   * 
   * @return A Java-File representing the resource, or null if file does not exists.
   */
  public static File makeFileFromPath( final IPath resource )
  {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IFile file = root.getFile( resource );
    final IPath location = file.getLocation();
    return location != null ? location.toFile() : null;
  }

  public static File makeFileFromPath2( final IPath resource )
  {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IPath rootLocation = root.getLocation();
    final File rootFile = rootLocation.toFile();
    return new File( rootFile, resource.toString() );
  }

  public static IProject findProjectFromURL( final URL baseURL )
  {
    final IPath path = findPathFromURL( baseURL );
    if( path == null || path.isRoot() || path.segmentCount() < 1 || !path.isAbsolute() )
      return null;

    final String projectName = path.segment( 0 );
    return ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
  }

  @SuppressWarnings("restriction")
  public static IPath findPathFromURL( final URL u )
  {
    final String utostring = u.toString();
    final String urlpath;
    final int ix = utostring.indexOf( '?' );
    if( ix != -1 )
      urlpath = utostring.substring( 0, ix );
    else
      urlpath = utostring;

    if( urlpath != null && urlpath.startsWith( PlatformURLResourceConnection.RESOURCE_URL_STRING ) )
    {
      final String path = urlpath.substring( PlatformURLResourceConnection.RESOURCE_URL_STRING.length() - 1 );
      final Path path2 = new Path( path.replaceAll( "//", "/" ) ); //$NON-NLS-1$ //$NON-NLS-2$
      return path2;
    }
    // Checks if the full path lies in the Workspace, if it does, the java.io.File path is converted
    // to an eclipse path
    // WARNING: this is quite ugly and probalbly doesn't work as it is intended to do
    // especially, if we are working with pathes into the .metadata section we get bugs
    else if( urlpath != null && urlpath.startsWith( "http:/" ) || urlpath.startsWith( "file:/" ) ) //$NON-NLS-1$ //$NON-NLS-2$
    {
      final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      URL url = null;
      try
      {
        url = root.getLocation().toFile().toURI().toURL();
      }
      catch( final MalformedURLException e )
      {
        // just return null
        e.printStackTrace();
        return null;
      }
      if( urlpath.matches( url.toString() + ".+" ) ) //$NON-NLS-1$
      {
        // split the string at the common part (path to workspace) and always take the second
        // part as the relative eclipse workspace path
        final String[] array = urlpath.split( url.toString() );
        if( array[1].startsWith( ".metadata" ) ) //$NON-NLS-1$
          return null;

        return new Path( array[1] );
      }
    }

    return null;
  }

  /** Gets all local files for given resources */
  public static File[] getLocalFiles( final IResource[] resources )
  {
    final File[] files = new File[resources.length];
    for( int i = 0; i < resources.length; i++ )
    {
      final IResource resource = resources[i];
      files[i] = resource.getLocation().toFile();
    }

    return files;
  }

  /**
   * Creates an URL given a resource. Uses the eclipse scheme defined in
   * PlatformURLResourceConnection.RESOURCE_URL_STRING.
   * 
   * @see PlatformURLResourceConnection#RESOURCE_URL_STRING
   * @param resource
   * @return platform URL
   * @throws MalformedURLException
   */
  public static URL createURL( final IResource resource ) throws MalformedURLException
  {
    String strUrl = createURLSpec( resource.getFullPath() );

    if( resource instanceof IContainer )
      strUrl += '/';

    return new URL( strUrl );
  }

  /**
   * Same as {@link #createURL(IResource)}, but returns <code>null</code> in case of an exception.
   */
  public static URL createQuietURL( final IResource resource )
  {
    try
    {
      return createURL( resource );
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Creates the string representation of an URL given an IPath.
   * 
   * @param path
   * @return platform URL
   */
  @SuppressWarnings("restriction")
  public static String createURLSpec( final IPath path )
  {
    return PlatformURLResourceConnection.RESOURCE_URL_STRING + path.toString();
  }

  /**
   * Tries to get the parent project of this container.
   * 
   * @return the parent project of the start container or null if the container is the WorkspaceRoot or itself if start
   *         is a Project.
   */
  public static IProject findParentProject( final IContainer start ) throws CoreException
  {
    if( start instanceof IWorkspaceRoot )
      return null;
    else if( start.getType() == IResource.PROJECT )
      return (IProject) start;
    final FindParentProjectVisitor visitor = new FindParentProjectVisitor();
    start.accept( visitor );
    return visitor.getParentProject();
  }

  /**
   * This function tries to construct an Eclipse-File from a path with a relative path to the workspace.<br/>
   * <br/>
   * First it tries to find the project and then iterates over all segments, getting the IFolder for it. At the last
   * segment, you get an IFile.
   * 
   * @param path
   *          The path of the file. It must be relative to the workspace.
   * @return The Eclipse-File representing the path.
   */
  public static IFile getFileFromPath( final IPath path )
  {
    /* Need all segments of this path. */
    final String[] segments = path.segments();
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject( segments[0] );

    if( project == null )
      return null;

    /* Get all folders, until the element before the last element. */
    IFolder tmpFolder = null;
    for( int i = 1; i < segments.length - 1; i++ )
      if( i == 1 )
        tmpFolder = project.getFolder( segments[1] );
      else
        tmpFolder = tmpFolder.getFolder( segments[i] );

    /* At least, get the IFile. */
    final IFile file = tmpFolder.getFile( segments[segments.length - 1] );

    return file;
  }

  /**
   * Returns all children of the given container.
   * 
   * @param depth
   *          See {@link org.eclipse.core.resources.IResource}
   */
  public static IFile[] getChildren( final IContainer container, final int depth ) throws CoreException
  {
    final CollectFilesVisitor visitor = new CollectFilesVisitor();
    container.accept( visitor, depth, IResource.NONE );
    return visitor.getFiles();
  }

  /**
   * Returns all children of the given container.
   * <p>
   * If any exception is thrown, it is suppressed and an empty array of files is returned.
   * 
   * @param depth
   *          See {@link org.eclipse.core.resources.IResource}
   */
  public static IFile[] getChildrenQuiet( final IContainer container, final int depth )
  {
    try
    {
      final CollectFilesVisitor visitor = new CollectFilesVisitor();
      container.accept( visitor, depth, IResource.NONE );
      return visitor.getFiles();
    }
    catch( final CoreException e )
    {
      return new IFile[0];
    }
  }

  /**
   * Returns all children of the given container with a given extension. If extension is null all files will be
   * returned.
   * <p>
   * If any exception is thrown, it is suppressed and an empty array of files is returned.
   * 
   * @param depth
   *          See {@link org.eclipse.core.resources.IResource}
   * @param extension
   *          the extension
   */
  public static IFile[] getChildrenWithExtensionQuiet( final IContainer container, final int depth, final String extension )
  {
    try
    {
      final CollectFilesWithExtensionVisitor visitor = new CollectFilesWithExtensionVisitor();
      visitor.setExtension( extension );
      container.accept( visitor, depth, IResource.NONE );
      return visitor.getFiles();
    }
    catch( final CoreException e )
    {
      return new IFile[0];
    }
  }

  /**
   * check if the child file can be expressed as a relative path regarding to the given parent folder.
   * 
   * @return The relative path (possibly using '..' notation, or <code>terrainModelFile#toFullPath</code> an absolute
   *         path if this is not possible.
   */
  public static IPath makeRelativ( final IContainer parentFolder, final IFile childFile )
  {
    final IContainer childFolder = childFile.getParent();

    final IPath parentPath = parentFolder.getFullPath();
    final IPath childFolderPath = childFolder.getFullPath();

    final IPath relativPath = PathUtils.makeRelativ( parentPath, childFolderPath );

    return relativPath.append( childFile.getName() );
  }

  /**
   * Same as {@link #makeRelativ(IFile, IFile)}, using a {@link IFile} as parent.
   */
  public static IPath makeRelativ( final IFile parentFile, final IFile childFile )
  {
    return makeRelativ( parentFile.getParent(), childFile );
  }

  public static void mkdirs( final IContainer container ) throws CoreException
  {
    if( !(container instanceof IFolder) )
      return;

    final IFolder folder = (IFolder) container;

    final File dir = folder.getFullPath().toFile();
    dir.mkdirs();

    folder.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
  }
}
