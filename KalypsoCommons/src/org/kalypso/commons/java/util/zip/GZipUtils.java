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
package org.kalypso.commons.java.util.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;


/**
 * @author Gernot Belger
 */
public final class GZipUtils
{
  public static final String EXT_GZ = ".gz"; //$NON-NLS-1$

  private GZipUtils( )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * GZ a file in place. The file contents will be gzipp'ed and written to a file named 'filename.ext.gz'.<br/>
   * The original file will be removed.
   * 
   * @return The result file.
   */
  public static File gzipAndDelete( final File input ) throws IOException
  {
    final File output = new File( input.getParentFile(), input.getName() + EXT_GZ );
    gzip( input, output );
    input.delete();

    return output;
  }

  /**
   * Creates a gzipped file (output) from another file (input).
   */
  public static void gzip( final File input, final File output ) throws IOException
  {
    InputStream is = null;
    OutputStream os = null;

    try
    {
      is = new BufferedInputStream( new FileInputStream( input ) );
      os = new GZIPOutputStream( new FileOutputStream( output ) );

      IOUtils.copy( is, os );
    }
    finally
    {
      IOUtils.closeQuietly( is );
      IOUtils.closeQuietly( os );
    }
  }

  /**
   * Unzips a gz file to the given output file.<br/>
   * The original file is not deleted.
   */
  public static void gunzip( final File input, final File output ) throws IOException
  {
    InputStream is = null;
    OutputStream os = null;

    try
    {
      is = new GZIPInputStream( new FileInputStream( input ) );
      os = new BufferedOutputStream( new FileOutputStream( output ) );

      IOUtils.copy( is, os );
    }
    finally
    {
      IOUtils.closeQuietly( is );
      IOUtils.closeQuietly( os );
    }
  }
}