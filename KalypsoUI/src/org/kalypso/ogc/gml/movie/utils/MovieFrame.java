/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.ogc.gml.movie.utils;

import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;

import javax.media.jai.JAI;

import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.mapmodel.MapModellHelper;
import org.kalypsodeegree.model.geometry.GM_Envelope;

/**
 * A movie frame contains a theme, a file of an image and a label.
 * 
 * @author Holger Albert
 */
public class MovieFrame implements IMovieFrame
{
  /**
   * The map model.-
   */
  private IMapModell m_mapModel;

  /**
   * The label.
   */
  private String m_label;

  /**
   * The bounding box.
   */
  private GM_Envelope m_boundingBox;

  /**
   * The temp directory for that movie.
   */
  private File m_tmpDirectory;

  /**
   * The constructor.
   */
  public MovieFrame( IMapModell mapModel, String label, GM_Envelope boundingBox, File tmpDirectory )
  {
    m_mapModel = mapModel;
    m_label = label;
    m_boundingBox = boundingBox;
    m_tmpDirectory = tmpDirectory;
  }

  /**
   * @see org.kalypso.ogc.gml.movie.utils.IMovieFrame#getImage(int, int)
   */
  @Override
  public RenderedImage getImage( int width, int height )
  {
    /* Get the directory of the images for this size. */
    /* It will be created, if it does not already exist. */
    File imageDirectory = getImageDirectory( width, height );

    /* The image file. */
    File imageFile = new File( imageDirectory, m_label + ".PNG" );
    if( imageFile.exists() )
      return JAI.create( "fileload", imageFile.getAbsolutePath() );

    /* Create the image. */
    BufferedImage image = MapModellHelper.createWellFormedImageFromModel( m_mapModel, width, height, new Insets( 1, 1, 1, 1 ), 0, m_boundingBox );

    /* Save the image. */
    JAI.create( "filestore", image, imageFile.getAbsolutePath(), "PNG" );

    return image;
  }

  /**
   * @see org.kalypso.ogc.gml.movie.utils.IMovieFrame#getLabel()
   */
  @Override
  public String getLabel( )
  {
    return m_label;
  }

  /**
   * This function returns the directory, which contains the images of this size. If it does not exist, it will be
   * created.
   * 
   * @return The directory, which contains the images of this size.
   */
  private File getImageDirectory( int width, int height )
  {
    if( !m_tmpDirectory.exists() )
      m_tmpDirectory.mkdirs();

    String sizeName = String.format( "%d_x_%d", width, height );
    File sizeDirectory = new File( m_tmpDirectory, sizeName );
    if( !sizeDirectory.exists() )
      sizeDirectory.mkdirs();

    return sizeDirectory;
  }
}