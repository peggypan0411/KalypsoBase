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
package org.kalypso.commons.java.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.KalypsoCommonsPlugin;
import org.kalypso.commons.internal.i18n.Messages;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.java.io.FileVisitor;

/**
 * Utility class for io and files
 * 
 * @author schlienger
 */
public class FileUtilities
{
  public static final String JAVA_IO_TMPDIR = "java.io.tmpdir"; //$NON-NLS-1$

  /** regex defining which are the invalid characters for a file name */
  public final static String INVALID_CHARACTERS = "[\\\\/:\\*\\?\"<>|]"; //$NON-NLS-1$

  /**
   * THE system tmp dir "java.io.tmpdir"
   */
  public static final File TMP_DIR = new File( System.getProperty( JAVA_IO_TMPDIR ) );

  /**
   * Rekursives l�schen von Dateien und Verzeichnissen
   * 
   * @param file
   *          Falls das Argument eine Datei ist, wird diese gel�scht. Ist es ein Verzeichnis, wird dieses mitsamt aller
   *          darin liegenden Verzeichnisse und Dateien gel�scht.
   */
  public static void deleteRecursive( final File file )
  {
    if( file == null )
      return;

    if( file.isDirectory() )
    {
      final File[] files = file.listFiles();
      if( files != null )
      {
        for( final File element : files )
          deleteRecursive( element );
      }
    }

    file.delete();
  }

  public static File mkTempFile( )
  {
    final long millis = Calendar.getInstance().getTimeInMillis();

    return new File( TMP_DIR, Long.valueOf( millis ).toString() );
  }

  /**
   * This function creates a file handle in the temporary directory. It ensures, that the file does not exist already.
   * 
   * @param prefix
   *          The prefix will be used in front of the generated name. If empty or null it will be <xode>tmp</code>.
   * @param extension
   *          The extension of the temporary file. If empty or null it will be <xode>dat</code>.
   * @return A file handle in the temporary directory, whose file does not exist.
   */
  public static File getNewTempFile( String prefix, String extension )
  {
    /* A prefix is needed. */
    if( prefix == null || prefix.length() == 0 )
      prefix = "tmp"; //$NON-NLS-1$

    /* An extension is needed. */
    if( extension == null || extension.length() == 0 )
      extension = "dat"; //$NON-NLS-1$

    /* Create a new file handle, as long as the file exists. */
    File tmpFile = new File( FileUtilities.TMP_DIR, String.format( "%s_%s.%s", prefix, String.valueOf( System.currentTimeMillis() ), extension ) ); //$NON-NLS-1$
    while( tmpFile.exists() )
      tmpFile = new File( FileUtilities.TMP_DIR, String.format( "%s_%s.%s", prefix, String.valueOf( System.currentTimeMillis() ), extension ) ); //$NON-NLS-1$

    return tmpFile;
  }

  /**
   * Creates a temp directory in java.io.tmpdir.
   * 
   * @param prefix
   * @return temporary directory
   * @see FileUtilities#createNewTempDir(String, File )
   */
  public static File createNewTempDir( final String prefix )
  {
    return createNewTempDir( prefix, TMP_DIR );
  }

  /**
   * Creates a temp directory inside the given one. It uses <code>System.currentTimeMillis</code> for naming the new
   * temp dir. This method can hang a little while in the case the directory it tries to create already exist.
   * 
   * @param prefix
   * @param parentDir
   * @return temporary directory
   */
  public synchronized static File createNewTempDir( final String prefix, final File parentDir )
  {
    while( true )
    {
      final File newDir = new File( parentDir, prefix + System.currentTimeMillis() );
      if( newDir.mkdirs() )
        return newDir;
    }
  }

  /**
   * Creates a temp file inside the given folder. It uses <code>System.currentTimeMillis</code> for naming the new temp
   * file. This method can hang a little while in the case the file it tries to create already exist.
   * 
   * @param prefix
   * @param parentDir
   * @return unique file
   */
  public synchronized static File createNewUniqueFile( final String prefix, final File parentDir )
  {
    File newFile = new File( parentDir, prefix + System.currentTimeMillis() );
    while( newFile.exists() )
      newFile = new File( parentDir, prefix + System.currentTimeMillis() );

    return newFile;
  }

  /**
   * Creates a unique file name inside the given folder. It uses <code>System.currentTimeMillis</code> for creating the
   * new file name. This method can hang a little while in the case the file it tries to create already exist.
   * 
   * @param prefix
   * @param extension
   * @param parentDir
   * @return unique file
   */
  public synchronized static String createNewUniqueFileName( final String prefix, final String extension, final File parentDir )
  {
    String newFileName = new String( prefix + "_" + System.currentTimeMillis() + extension ); //$NON-NLS-1$
    File newFile = new File( parentDir, newFileName );
    while( newFile.exists() )
    {
      newFileName = new String( prefix + "_" + System.currentTimeMillis() + extension ); //$NON-NLS-1$
      newFile = new File( parentDir, newFileName );
    }
    return newFileName;
  }

  /**
   * Creates a unique file name inside the given folder. It adds a counter between prefix and extension for creating the
   * new file name.<br>
   * First try is parent/prefix.extension
   * 
   * @param prefix
   * @param extension
   * @param parentDir
   * @return unique file
   */
  public synchronized static File createNewUniqueFile( final String prefix, final String extension, final File parentDir )
  {
    File newFile = new File( parentDir, prefix + extension );

    int count = 0;
    while( newFile.exists() )
    {
      final String newFileName = new String( prefix + "_" + count++ + extension ); //$NON-NLS-1$
      newFile = new File( parentDir, newFileName );
    }
    return newFile;
  }

  /**
   * Macht aus einer absoluten Dateiangabe eine relative
   * 
   * @param basedir
   * @param absoluteFile
   * @return Ein File-Object, welches einen relativen Pfad enth?lt; null, wenn <code>basedir</code> kein Parent-Dir von <code>absoluteFile</code> ist
   */
  public static File getRelativeFileTo( final File basedir, final File absoluteFile )
  {
    final String rel = getRelativePathTo( basedir, absoluteFile );

    final File file = new File( "." + rel ); //$NON-NLS-1$
    return file;
  }

  /**
   * Returns the relative path, without any reserved characters such as '.'. This is meant to be used without string
   * concatenation function to reproduce an absolute path again. Directly creating a File object on the path returned by
   * this method won't produce a good result. Use the <code>getRelativeFileTo()</code> method instead.
   * 
   * @param basedir
   *          if null, the absolute path of absoluteFile is returned.
   * @param absoluteFile
   * @return the relative path from absoluteFile to basedir
   */
  public static String getRelativePathTo( final File basedir, final File absoluteFile )
  {
    if( basedir == null )
      return absoluteFile.getAbsolutePath();
    final String baseAbs = basedir.getAbsolutePath();
    final String absAbs = absoluteFile.getAbsolutePath();
    return getRelativePathTo( baseAbs, absAbs );
  }

  public static String getRelativePathTo( final String base, final String absolute )
  {
    final String separator = "/"; //$NON-NLS-1$
    String basePath = base.replaceAll( "\\\\", separator ); //$NON-NLS-1$
    final String absolutePath = absolute.replaceAll( "\\\\", separator ); //$NON-NLS-1$
    if( !absolutePath.startsWith( basePath ) )
    {
      if( basePath.lastIndexOf( separator ) > -1 )
        basePath = basePath.substring( 0, basePath.lastIndexOf( separator ) + 1 );

      final String difference = StringUtils.difference( basePath, absolutePath );
      if( difference == null || "".equals( difference ) ) //$NON-NLS-1$
        return null;

      final int index = absolutePath.indexOf( difference );
      if( index < 5 )
        return null;

      final String back = basePath.substring( index );
      // TODO change regExp to "everything except fileseparator"
      final String x = back.replaceAll( "([a-zA-Z0-9_-]|\\.)+", ".." ); //$NON-NLS-1$ //$NON-NLS-2$
      if( x.length() > 0 )
        // return x + "/" + difference; //$NON-NLS-1$
        return x + difference;

      return difference;
    }
    if( absolutePath.equalsIgnoreCase( basePath ) )
      return ""; //$NON-NLS-1$
    if( basePath.endsWith( separator ) )
      return absolutePath.substring( basePath.length() );
    else
      return absolutePath.substring( basePath.length() + 1 );
  }

  /**
   * Returns true if childCandidate is stored under the path of parent, either directly or in a sub directory.
   * 
   * @param parent
   * @param childCandidate
   * @return true if childCandidate is a child of the given parent.
   */
  public static boolean isChildOf( final File parent, final File childCandidate )
  {
    File f = childCandidate;

    while( f != null )
    {
      if( f.equals( parent ) )
        return true;

      f = f.getParentFile();
    }

    return false;
  }

  /**
   * @param name
   *          name of path of the file
   * @return characters after last "." of given file name
   */
  public static String getSuffix( final String name )
  {
    final String[] strings = name.split( "\\." ); //$NON-NLS-1$
    if( strings.length != 0 )
      return strings[strings.length - 1];
    return null;
  }

  /**
   * @param file
   * @return characters after last "." of given file name
   */
  public static String getSuffix( final File file )
  {
    return getSuffix( file.getAbsolutePath() );
  }

  /**
   * Returns only the name part of the given file name removing the extension part.
   * <p>
   * Example:
   * 
   * <pre>
   *     test.foo -- test
   *     robert.tt -- robert
   * </pre>
   * 
   * @param fileName
   * @return fileName without the last '.???' extension part (NOTE: the extension part is not limited to 3 chars)
   */
  public static String nameWithoutExtension( final String fileName )
  {
    final int lastIndexOf = fileName.lastIndexOf( '.' );
    if( lastIndexOf == -1 )
      return fileName;

    return fileName.substring( 0, lastIndexOf );
  }

  /**
   * L�sst den FileVisitor die angegebene Datei bzw. Verzeichnis und alle darin enthaltenen Dateien besuchen.
   * 
   * @param recurse
   *          Falls true, werden auch Unterverzeichnisse besucht
   * @throws IOException
   */
  public static void accept( final File root, final FileVisitor visitor, final boolean recurse ) throws IOException
  {
    if( !root.exists() )
      return;

    // zuerst die Datei selbst
    final boolean stop = !visitor.visit( root );
    if( stop || !root.isDirectory() )
      return;

    final File[] files = root.listFiles();
    if( files == null )
      return;

    for( final File file : files )
    {
      if( file.isFile() || file.isDirectory() && recurse )
        accept( file, visitor, recurse );
    }
  }

  public static void copyShapeFileToDirectory( final String shapeBase, final File target )
  {
    File _shp;
    File _dbf;
    File _shx;
    File _sbn;
    File _sbx;
    if( target.isDirectory() )
    {
      try
      {
        _shp = new File( shapeBase + ".shp" ); //$NON-NLS-1$
        if( _shp.exists() )
          FileUtils.copyFileToDirectory( _shp, target );
        else
          return;
        _dbf = new File( shapeBase + ".dbf" ); //$NON-NLS-1$
        if( _dbf.exists() )
          FileUtils.copyFileToDirectory( _dbf, target );
        else
          return;
        _shx = new File( shapeBase + ".shx" ); //$NON-NLS-1$
        if( _shx.exists() )
          FileUtils.copyFileToDirectory( _shx, target );
        else
          return;
        _sbn = new File( shapeBase + ".sbn" ); //$NON-NLS-1$
        if( _sbn.exists() )
          FileUtils.copyFileToDirectory( _sbn, target );
        _sbx = new File( shapeBase + ".sbx" ); //$NON-NLS-1$
        if( _sbn.exists() )
          FileUtils.copyFileToDirectory( _sbx, target );

      }
      catch( final MalformedURLException e )
      {
        e.printStackTrace();
      }
      catch( final IOException e )
      {
        e.printStackTrace();
      }
    }
  }

  /**
   * Replaces all invalid characters from the given fileName so that it is valid against the OS-rules for naming files.
   * 
   * @return a valid filename that can be used to create a new file, special (invalid) characters are removed and
   *         replaced by the given replacement-string
   */
  public static String validateName( final String fileName, final String replacement )
  {
    return fileName.replaceAll( INVALID_CHARACTERS, replacement );
  }

  /**
   * Gets the name part of a path-like-string.
   * <p>
   * That is, everything after the last '/' or '\'.
   * </p>
   * <p>
   * E.g. <code>C:/mydirectory/file.txt</code> gets <code>file.txt</code>
   * </p>
   * .
   */
  public static String nameFromPath( final String path )
  {
    final int lastIndexOfSlash = path.lastIndexOf( '/' );

    /**
     * Bug fixed by Dejan, 18.01.2007 It was final int lastIndexOfBackslash = path.lastIndexOf( '\\' ); i.e. the same
     * value as lastIndexOfSlash, so it was not working for backslashes TODO: consider using java.io.File.separatorChar
     * instead of slash & backslash
     */
    final int lastIndexOfBackslash = path.lastIndexOf( '\\' );

    final int lastIndexOf = Math.max( lastIndexOfSlash, lastIndexOfBackslash );

    if( lastIndexOf == -1 )
      return path;

    if( lastIndexOf + 1 == path.length() - 1 )
      return ""; //$NON-NLS-1$

    return path.substring( lastIndexOf + 1 );
  }

  /**
   * Sets a certain suffix to the given file name. If the file name already has a suffix (that is a non-empty string
   * after the last '.') it will be replaced.
   * 
   * @param suffix
   *          The suffix without the point '.'
   */
  public static String setSuffix( final String fileName, final String suffix )
  {
    final int index = fileName.lastIndexOf( '.' );
    if( index == -1 )
      return fileName + '.' + suffix;

    return fileName.substring( 0, index ) + suffix;
  }

  /**
   * Copies the content of a url into a string.
   * 
   * @param encoding
   *          The encoding to read the content, if <code>null</code> the platforms default encoding will be used.
   */
  public static String toString( final URL input, final String encoding ) throws IOException
  {
    InputStream is = null;
    try
    {
      is = input.openStream();

      if( encoding == null )
        return IOUtils.toString( is );

      return IOUtils.toString( is, encoding );
    }
    finally
    {
      IOUtils.closeQuietly( is );
    }
  }

  /**
   * Replaces all invalid characters from the given fileName so that it is valid against the OS-rules for naming files.
   * and looks if file already exists in baseFolder
   * 
   * @return a valid filename that can be used to create a new file, special (invalid) characters are removed and
   *         replaced by the given replacement-string
   */
  public static String validateName( final IFolder baseFolder, final String name, final String replacement )
  {
    final String myBaseName = validateName( name, replacement );
    String myName = myBaseName;

    int count = 0;
    while( baseFolder.getFile( myName ).exists() )
    {
      myName = String.format( "%s%d", myBaseName, count ); //$NON-NLS-1$
      count++;
    }

    return myName;
  }

  /**
   * This function deletes files/directories from the list, which are older than 'days' days. <br />
   * Be aware, that it <strong>deletes all directories recursively</strong>.<br />
   * So if one directory contains files, you want to keep, do not add this directory, but only the child
   * files/directories you realy want to delete.
   * 
   * @param files
   *          The files/directories to check for deletion. All files/directories older than 'days' days will be deleted.
   * @param days
   *          The days, to keep the files and directories.
   * @return A multi status, containing the result for each file, which was tried to delete.
   */
  public static MultiStatus deleteFiles( final List<File> files, final int days )
  {
    /* The date 'days' before now. */
    final Calendar before = Calendar.getInstance();
    before.add( Calendar.DAY_OF_MONTH, -days );

    /* Check the date of the files, each file older than 'days' will be deleted. */
    final List<File> filesToDelete = new ArrayList<>();

    for( int i = 0; i < files.size(); i++ )
    {
      /* Get the file. */
      final File file = files.get( i );

      /* Using the last modified time should not hurt. */
      final long lastModified = file.lastModified();
      if( lastModified < before.getTimeInMillis() )
        filesToDelete.add( file );
    }

    /* List for success or error messages. */
    final MultiStatus stati = new MultiStatus( KalypsoCommonsPlugin.getID(), IStatus.OK, Messages.getString( "org.kalypso.commons.java.io.FileUtilities.1", String.valueOf( days ) ), null ); //$NON-NLS-1$

    /* Delete these files. */
    for( int i = 0; i < filesToDelete.size(); i++ )
    {
      /* Get the file/directory to delete. */
      final File fileToDelete = filesToDelete.get( i );

      if( !fileToDelete.exists() )
        continue;

      try
      {
        /* Delete file or the directory and its contents. */
        FileUtilities.deleteRecursive( fileToDelete );

        /* Add the success message. */
        stati.add( new Status( IStatus.OK, KalypsoCommonsPlugin.getID(), fileToDelete.getName() + ": OK" ) ); //$NON-NLS-1$
      }
      catch( final Exception ex )
      {
        /* If one could no be deleted, it does not matter, the next run will get it. We will get them all :). */
        final IStatus status = StatusUtilities.statusFromThrowable( ex, fileToDelete.getName() + ": NOT DELETED" ); //$NON-NLS-1$
        stati.add( status );
      }
    }

    return stati;
  }

  public static String resolveValidFileName( String fileName )
  {
    fileName = fileName.replaceAll( "\\\\", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    fileName = fileName.replaceAll( "/", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    fileName = fileName.replaceAll( ":", "_" ); //$NON-NLS-1$ //$NON-NLS-2$
    fileName = fileName.replaceAll( "\\.", "_" ); //$NON-NLS-1$ //$NON-NLS-2$

    return fileName.trim();
  }

  public static File[] getFiles( final File fDir, final String regex )
  {
    Assert.isTrue( fDir.isDirectory() );
    final String myRegEx = regex.toLowerCase();

    final File[] files = fDir.listFiles( new FileFilter()
    {
      @Override
      public boolean accept( final File pathname )
      {
        final String name = pathname.getName().toLowerCase();
        if( name.matches( myRegEx ) )
          return true;

        return false;
      }
    } );

    return files;
  }

  public static void deleteQuietly( final File file )
  {
    try
    {
      if( !file.exists() )
        return;

      FileUtils.forceDelete( file );
    }
    catch( final IOException e )
    {
      KalypsoCommonsPlugin.getDefault().getLog().log( new Status( IStatus.ERROR, KalypsoCommonsPlugin.getID(), e.getLocalizedMessage(), e ) );
    }
  }

  /**
   * @return complete file name including suffix
   */
  public static String resolveFileName( final String file )
  {
    final int index = findLastSeperator( file );
    if( index == -1 )
      return file;

    return file.substring( index + 1 );
  }

  public static int findLastSeperator( final String file )
  {
    if( file.contains( "/" ) ) //$NON-NLS-1$
      return file.lastIndexOf( "/" ); //$NON-NLS-1$
    else if( file.contains( "\\" ) ) //$NON-NLS-1$
      return file.lastIndexOf( "\\" ); //$NON-NLS-1$

    return -1;
  }

  public static void cleanDirectory( final File dir, final IFileFilter filter )
  {
    final File[] files = dir.listFiles();
    for( final File file : files )
    {
      if( filter.select( file ) )
        FileUtilities.deleteQuietly( file );
    }
  }

  public static File findExecutableOnPath( final String executableName )
  {
    final String systemPath = System.getenv( "PATH" ); //$NON-NLS-1$  
    final String[] pathDirs = systemPath.split( File.pathSeparator );
    for( final String pathDir : pathDirs )
    {
      final File file = new File( pathDir, executableName );
      if( file.isFile() )
        return file;
    }
    // not found on path
    return null;
  }

}