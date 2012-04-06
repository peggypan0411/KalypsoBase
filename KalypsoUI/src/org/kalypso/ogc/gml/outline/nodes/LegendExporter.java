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
package org.kalypso.ogc.gml.outline.nodes;

import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.i18n.Messages;
import org.kalypso.ui.KalypsoGisPlugin;

/**
 * Helper for creating legend images for themes.
 *
 * @author Gernot Belger
 */
public class LegendExporter
{
  /**
   * This function exports the legend of the given themes to a file. It seems that it has to be run in an UI-Thread.
   *
   * @param themes
   *          The themes to export.
   * @param file
   *          The file, where it should save to.
   * @param format
   *          The image format (for example: SWT.IMAGE_PNG).
   * @param device
   *          The device.
   * @param insets
   *          Defines the size of an empty border around the image. Can be <code>null</code>.
   * @param sizeWidth
   *          The width of the image, the legend is drawn onto.<br>
   *          If one of sizeWidth or sizeHeight is <=0 the width and height of the image is determined automatically.
   * @param sizeHeight
   *          The height of the image, the legend is drawn onto.<br>
   *          If one of sizeWidth or sizeHeight is <=0 the width and height of the image is determined automatically.
   * @param onlyVisible
   *          True, if only visible theme nodes should be asked.
   * @param monitor
   *          A progress monitor.
   * @return A status, containing information about the process.
   */
  public IStatus exportLegends( final IThemeNode[] nodes, final File file, final int format, final Device device, Insets insets, final int sizeWidth, final int sizeHeight, final boolean onlyVisible, final IProgressMonitor monitor )
  {
    /* Monitor. */
    final SubMonitor progress = SubMonitor.convert( monitor, Messages.getString( "org.kalypso.ogc.gml.map.utilities.MapUtilities.0" ), 150 ); //$NON-NLS-1$

    /* The legend image. */
    Image image = null;

    try
    {
      /* Set default insets, if none are given. */
      if( insets == null )
        insets = new Insets( 5, 5, 5, 5 );

      /* Create the legend image. */
      image = exportLegends( null, nodes, device, insets, null, sizeWidth, sizeHeight, onlyVisible, -1, progress.newChild( 50 ) );

      /* Monitor. */
      ProgressUtilities.worked( progress, 50 );

      /* Save the image. */
      final ImageLoader imageLoader = new ImageLoader();
      imageLoader.data = new ImageData[] { image.getImageData() };
      imageLoader.save( file.toString(), format );

      /* Monitor. */
      ProgressUtilities.worked( monitor, 50 );

      return Status.OK_STATUS;
    }
    catch( final Exception ex )
    {
      return new Status( IStatus.ERROR, KalypsoGisPlugin.getId(), ex.getLocalizedMessage(), ex );
    }
    finally
    {
      if( image != null )
        image.dispose();

      /* Monitor. */
      progress.done();
    }
  }

  /**
   * This function exports the legend of the given themes as swt image.<br>
   *
   * @param whiteList
   *          The ids of the nodes (themes), which are to be shown. May be empty or null. In these cases, all nodes
   *          (themes) will be shown.
   * @param nodes
   *          The theme nodes to export.
   * @param device
   *          The device.
   * @param insets
   *          Defines the size of an empty border around the image. Must not be <code>null</code>.
   * @param backgroundRGB
   *          Defines the background color of the image. If <code>null</code>, an transparent image will be returned.
   * @param sizeWidth
   *          The width of the image, the legend is drawn onto.<br>
   *          If one of sizeWidth or sizeHeight is <=0 the width and height of the image is determined automatically.
   * @param sizeHeight
   *          The height of the image, the legend is drawn onto.<br>
   *          If one of sizeWidth or sizeHeight is <=0 the width and height of the image is determined automatically.
   * @param onlyVisible
   *          True, if only visible theme nodes should be asked.
   * @param fontSize
   *          The font size. If <=0, the font size will be 10.
   * @param monitor
   *          A progress monitor.
   * @return The newly created image, must be disposed by the caller.
   */
  public Image exportLegends( final String[] whiteList, final IThemeNode[] nodes, final Device device, Insets insets, final RGB backgroundRGB, final int sizeWidth, final int sizeHeight, final boolean onlyVisible, int fontSize, final IProgressMonitor monitor )
  {
    /* Set default insets, if none are given. */
    if( insets == null )
      insets = new Insets( 5, 5, 5, 5 );

    /* Set the default size, if none is given. */
    if( fontSize <= 0 )
      fontSize = 10;

    /* Monitor. */
    final SubMonitor progress = SubMonitor.convert( monitor, Messages.getString( "org.kalypso.ogc.gml.map.utilities.MapUtilities.0" ), nodes.length * 100 + 100 ); //$NON-NLS-1$

    /* This font will be used to generate the legend. */
    final Font font = new Font( device, JFaceResources.DIALOG_FONT, fontSize, SWT.NORMAL );

    /* Memory for the legends. */
    final List<Image> legends = new ArrayList<Image>();

    /* Collect the legends. */
    for( final IThemeNode themeNode : nodes )
    {
      /* Monitor. */
      progress.subTask( Messages.getString( "org.kalypso.ogc.gml.map.utilities.MapUtilities.2" ) + themeNode.getLabel() + Messages.getString( "org.kalypso.ogc.gml.map.utilities.MapUtilities.3" ) ); //$NON-NLS-1$ //$NON-NLS-2$

      try
      {
        if( onlyVisible && !themeNode.isChecked( themeNode ) )
          continue;

        /* Get the legend. */
        final Image legend = themeNode.getLegendGraphic( whiteList, onlyVisible, font );
        if( legend != null )
          legends.add( legend );
      }
      catch( final CoreException e )
      {
        e.printStackTrace();

        // FIXME:
        // we cannot throw an exception here, else the whole legend fails if only one theme has an problem.
        // Solutions:
        // - do not allow theme to throw CoreException on 'getLegendGraphics' instead produce error icon+message
        // - or, produce an error image here
      }

      /* Monitor. */
      ProgressUtilities.worked( progress, 100 );
    }

    /* Calculate the size. */
    int width = 0;
    int height = 0;
    if( sizeWidth > 0 && sizeHeight > 0 )
    {
      width = sizeWidth;
      height = sizeHeight;
    }
    else
    {
      for( final Image legend : legends )
      {
        final Rectangle bounds = legend.getBounds();
        if( bounds.width > width )
          width = bounds.width;

        height = height + bounds.height;
      }

      width += insets.left + insets.right;
      height += insets.top + insets.bottom;
    }

    /* Monitor. */
    ProgressUtilities.worked( progress, 50 );

    /* Now create the new image. */
    final Image image = new Image( device, width, height );

    /* Need a GC. */
    final GC gc = new GC( image );

    /* Set the background color. */
    if( backgroundRGB != null )
    {
      final Color bgColor = new Color( device, backgroundRGB );
      gc.setBackground( bgColor );
      gc.fillRectangle( image.getBounds() );
      bgColor.dispose();
    }

    /* Draw on it. */
    int heightSoFar = insets.top;
    for( final Image legend : legends )
    {
      gc.drawImage( legend, insets.left, heightSoFar );
      heightSoFar = heightSoFar + legend.getBounds().height;
      legend.dispose();
    }

    /* Dispose the GC. */
    gc.dispose();

    /* Monitor. */
    ProgressUtilities.worked( progress, 50 );

    return image;
  }
}