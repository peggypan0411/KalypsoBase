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
package org.kalypso.grid;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.ArrayUtils;
import org.geotiff.image.jai.GeoTIFFDirectory;
import org.kalypso.commons.java.io.FileUtilities;
import org.libtiff.jai.codec.XTIFFField;

import com.sun.media.jai.codec.SeekableStream;

/**
 * Helper class for handling of grid files.
 *
 * @author Dirk Kuch
 */
public class GridFileVerifier
{
  private static final String[] validFileExtensions = new String[] { "tif", "tiff", "jpg", "jpeg", "asc", "dat", "png", "asg" };

  public enum RASTER_TYPE
  {
    eAsciiGrid,
    eImage,
    eImageWorldFile,
    eBinWorldFile,
    eImageGeo
  }

  public enum IMAGE_TYPE
  {
    eNoImage,
    eGIF,
    eJPG,
    eTIFF,
    ePNG
  }

  public static boolean verify( final URL urlImage )
  {
    /* file exists? */
    if( urlImage == null )
      return false;

    final String file = urlImage.toString().toLowerCase();
    final int index = file.lastIndexOf( "." );

    /* valid file extension */
    if( !ArrayUtils.contains( GridFileVerifier.validFileExtensions, file.substring( index + 1 ) ) )
      return false;

    final RASTER_TYPE raster_type = determineType( urlImage );
    if( raster_type == null )
      return false;

    return true;
  }

  public static IGridMetaReader getRasterMetaReader( final URL urlGrid, final String cs )
  {
    final RASTER_TYPE raster_type = determineType( urlGrid );
    switch( raster_type )
    {
      case eAsciiGrid:
        return new GridMetaReaderAscii( urlGrid, cs );

      case eImage:
        return null;

      case eImageGeo:
        return new GridMetaReaderGeoTiff( urlGrid, determineImageType( urlGrid ) );

      case eImageWorldFile:
      {
        final URL worldFile = getWorldFile( urlGrid );
        return new GridMetaReaderWorldFile( urlGrid, worldFile );
      }

      case eBinWorldFile:
      {
        final URL worldFile = getWorldFile( urlGrid );
        return new GridMetaReaderBinWorldFile( urlGrid, worldFile );
      }

      default:
        throw new IllegalStateException( "Unknwon raster type, domain cannot be determined" );
    }
  }

  private static RASTER_TYPE determineType( final URL urlImage )
  {
    /* determin image type */
    final IMAGE_TYPE image_type = determineImageType( urlImage );

    /* if it is an image, it can maybe have an world file with coordinates */
    if( !IMAGE_TYPE.eNoImage.equals( image_type ) )
    {
      final URL worldFile = getWorldFile( urlImage );
      if( worldFile != null )
        return RASTER_TYPE.eImageWorldFile;

      /* is it an geodata image, like geotif? ATM only geotif is supported */
      if( IMAGE_TYPE.eTIFF.equals( image_type ) && isGeoTiff( urlImage ) )
        return RASTER_TYPE.eImageGeo;
      else
        /* only a simple image */
        return RASTER_TYPE.eImage;
    }

    if( isAsciGrid( urlImage ) )
      return RASTER_TYPE.eAsciiGrid;

    if( isBinGrid( urlImage ) )
      return RASTER_TYPE.eBinWorldFile;

    return null;
  }

  private static boolean isBinGrid( final URL urlImage )
  {
    return urlImage.getFile().toLowerCase().endsWith( ".bin" ); //$NON-NLS-1$
  }

  private static boolean isAsciGrid( final URL urlImage )
  {
    if( urlImage == null )
      throw new IllegalStateException();

    // TODO: mke a real inspection of file
    final String[] ascExtensions = new String[] { "asc", "dat", "asg" }; //$NON-NLS-1$

    final String file = urlImage.toString().toLowerCase();
    final int index = file.lastIndexOf( "." );

    if( ArrayUtils.contains( ascExtensions, file.substring( index + 1 ) ) )
      return true;

    return false;
  }

  private static boolean isGeoTiff( final URL urlImage )
  {
    if( urlImage == null )
      throw new IllegalStateException();
    SeekableStream stream;
    try
    {
      stream = SeekableStream.wrapInputStream( urlImage.openStream(), true );
      final GeoTIFFDirectory directory = new GeoTIFFDirectory( stream, 0 );

      final XTIFFField[] geoKeys = directory.getGeoKeys();
      if( geoKeys.length <= 0 )
        return false;

      return true;
    }
    catch( final IOException e )
    {
      e.printStackTrace();
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }

    return false;
  }

  public static URL getWorldFile( final URL urlImage )
  {
    if( urlImage == null )
      throw new IllegalStateException();

    // FIXME: merge this code with world file reader!
    final String[] worldFileExtensions = new String[] { "tfw", "gfw", "jpw", "jgw", "pgw", "wld" };

    for( final String extension : worldFileExtensions )
    {
      final String wfName = FileUtilities.nameWithoutExtension( urlImage.toString() ) + "." + extension;

      try
      {
        final URL url = new URL( wfName );

        /* url is valid? -> try to open stream */
        final InputStream stream = url.openStream();
        stream.close();

        return url;
      }
      catch( final MalformedURLException e )
      {
        e.printStackTrace();
      }
      catch( final IOException e )
      {
        // file does not exists? -> exception is thrown, try next worldfile type
        // e.printStackTrace();
      }
    }

    return null;
  }

  private static IMAGE_TYPE determineImageType( final URL urlImage )
  {
    if( urlImage == null )
      throw new IllegalStateException();

    final String file = urlImage.toString().toLowerCase();
    final int index = file.lastIndexOf( "." );

    final String fileExtension = file.substring( index + 1 );

    final String[] jpgTypes = new String[] { "jpg", "jpeg" };
    final String[] tifTypes = new String[] { "tif", "tiff" };

    if( ArrayUtils.contains( jpgTypes, fileExtension ) )
      return IMAGE_TYPE.eJPG;

    if( ArrayUtils.contains( tifTypes, fileExtension ) )
      return IMAGE_TYPE.eTIFF;

    if( "gif".equals( fileExtension ) )
      return IMAGE_TYPE.eGIF;

    if( "png".equals( fileExtension ) )
      return IMAGE_TYPE.ePNG;

    return IMAGE_TYPE.eNoImage;
  }
}
