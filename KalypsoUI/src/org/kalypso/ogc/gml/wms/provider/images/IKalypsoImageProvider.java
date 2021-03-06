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
package org.kalypso.ogc.gml.wms.provider.images;

import java.awt.Image;

import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.kalypso.ogc.gml.wms.loader.ICapabilitiesLoader;
import org.kalypsodeegree.model.geometry.GM_Envelope;

/**
 * This interface provides functions for image provider.
 * 
 * @author Holger Albert
 */
public interface IKalypsoImageProvider
{
  /**
   * This constant stores the key for the layers.
   */
  String KEY_LAYERS = "LAYERS"; //$NON-NLS-1$

  /**
   * This constant stores the key for the URL.
   */
  String KEY_URL = "URL"; //$NON-NLS-1$

  /**
   * This constant stores the key for the styles.
   */
  String KEY_STYLES = "STYLES"; //$NON-NLS-1$

  /**
   * This constant stores the key for the image provider.
   */
  String KEY_PROVIDER = "PROVIDER"; //$NON-NLS-1$

  /**
   * Initializes this image provider. This function must be called.
   * 
   * @param themeName
   *          The name of the theme.
   * @param layers
   *          The layers.
   * @param styles
   *          The styles.
   * @param service
   *          The service.
   * @param localSRS
   *          The client coordinate system.
   * @param sldBody
   *          This is the content of a SLD, if we want the server to render the image with a specific style.
   */
  void init( String providerID, String themeName, String[] layers, String[] styles, String service, String localSRS, String sldBody );

  /**
   * Check if this loader is already initialized and does it if not.<br/>
   * In the case of a WMS, the capabilities will be loaded in this method.
   */
  IStatus checkInitialize( IProgressMonitor monitor );

  /**
   * This function will create the image and return it.
   * 
   * @param width
   *          The requested width.
   * @param height
   *          The requested height.
   * @param bbox
   *          The requested bounding box.
   * @return The created image.
   */
  Image getImage( int width, int height, GM_Envelope bbox ) throws CoreException;

  /**
   * This function returns a label.
   * 
   * @return The label.
   */
  String getLabel( );

  /**
   * This function returns the full extent, if available.
   * 
   * @return The full extent.
   */
  GM_Envelope getFullExtent( );

  /**
   * This function should return the loader, which is used for loading the capabilities.
   * 
   * @return The capabilities loader.
   */
  ICapabilitiesLoader createCapabilitiesLoader( );

  ImageDescriptor getLegendGraphic( String layer, String style );

  WMSCapabilities getCapabilities( );

  boolean isLayerVisible( String name );

  void setLayerVisible( String[] name, boolean visible );

  String getStyle( Layer layer );

  String getSource( );
}
