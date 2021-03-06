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
package de.openali.odysseus.service.ods.util;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/**
 * @author Alexander Burtscher
 */
public class ODSUtils
{
  private static final String SYMBOL_FILE_SUFFIX = ".png";

  private static final int SWT_IMAGE_TYPE = SWT.IMAGE_PNG;

  public static ImageData loadSymbol( final File symbolDir, final String sceneId, final String chartId, final String layerId, final String symbolId ) throws FileNotFoundException
  {
    final ImageLoader il = new ImageLoader();
    final File symbolFile = getSymbolFile( symbolDir, sceneId, chartId, layerId, symbolId );
    System.out.println( "Loading symbol file " + symbolFile.getAbsolutePath() );
    if( !symbolFile.exists() )
      throw new FileNotFoundException();

    final ImageData[] id = il.load( symbolFile.getAbsolutePath() );
    return id[0];
  }

  private static File getSymbolFile( final File symbolDir, final String sceneId, final String chartId, final String layerId, final String symbolId )
  {
    final String filename = sceneId + "_" + chartId + "_" + layerId + "_" + symbolId + SYMBOL_FILE_SUFFIX;
    final File symbolFile = new File( symbolDir, filename );
    return symbolFile;
  }

  public static void writeSymbol( final File symbolDir, final ImageData id, final String sceneId, final String chartId, final String layerId, final String symbolId )
  {
    final ImageLoader il = new ImageLoader();
    il.data = new ImageData[] { id };
    final File symbolFile = getSymbolFile( symbolDir, sceneId, chartId, layerId, symbolId );
    System.out.println( "Writing symbol file " + symbolFile.getAbsolutePath() );
    il.save( symbolFile.getAbsolutePath(), SWT_IMAGE_TYPE );
  }
}