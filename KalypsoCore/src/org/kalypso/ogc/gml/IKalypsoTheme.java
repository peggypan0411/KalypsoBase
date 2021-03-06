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
package org.kalypso.ogc.gml;

import java.awt.Graphics;
import java.net.URL;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.kalypso.commons.i18n.I10nString;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypsodeegree.graphics.transformation.GeoTransform;
import org.kalypsodeegree.model.geometry.GM_Envelope;

/**
 * Implements {@link org.eclipse.ui.model.IWorkbenchAdapter} in order to provider nice labels/images, ..... Does NOT
 * implement {@link org.eclipse.ui.model.IWorkbenchAdapter2}. Fonts, and so on are decided outside of the theme scope.
 *
 * @author Katharina <a href="mailto:k.lupp@web.de>Katharina Lupp </a>
 */
public interface IKalypsoTheme extends IAdaptable
{
  String PROPERTY_CUSTOM_THEME_ID = "customID"; //$NON-NLS-1$

  /**
   * Name of the property which determines if the user is allowed to deleted this theme.
   */
  String PROPERTY_DELETEABLE = "deleteable"; //$NON-NLS-1$

  /**
   * Name of the property which determines the id of the IKalypsoThemeInfo for this theme.
   */
  String PROPERTY_THEME_INFO_ID = "themeInfoId"; //$NON-NLS-1$

  /**
   * If this property is set to false, the theme will not be used to calculate the maximal extent of a map.<br/>
   * This property is <code>true</code> by default.
   */
  String PROPERTY_USE_IN_FULL_EXTENT = "useForFullExtent"; //$NON-NLS-1$

  /**
   * * Adds a listener to the list of listeners. Has no effect if the same listeners is already registered.
   */
  void addKalypsoThemeListener( final IKalypsoThemeListener listener );

  /**
   * Removes a listener from the list of listeners. Has no effect if the listeners is not registered.
   */
  void removeKalypsoThemeListener( final IKalypsoThemeListener listener );

  void dispose( );

  URL getContext( );

  ImageDescriptor getDefaultIcon( );

  /**
   * Paints the theme to the given graphics context<br/>
   *
   * @throws CoreException
   *           REAMRK: Implementors should additionally set the status object internally, if an exception occurs in
   *           order to show the problem in the outline.
   */
  IStatus paint( final Graphics g, final GeoTransform world2screen, final Boolean selected, final IProgressMonitor monitor );

  /**
   * returns the name of the layer
   */
  I10nString getName( );

  /**
   * Returns the label of this label, in the current locale.<br/>
   * No status texts are added here<br/>
   */
  String getLabel( );

  String getType( );

  void setName( final I10nString name );

  String getLegendIcon( );

  boolean shouldShowLegendChildren( );

  void setLegendIcon( String legendIcon, URL context );

  /**
   * Returns the full extent bounding box for the theme.
   */
  GM_Envelope getFullExtent( );

  IMapModell getMapModell( );

  /**
   * Returns the context id that this theme represents.
   */
  String getTypeContext( );

  /**
   * This function should return true, if the theme has tried to load the image, data, etc. once. Regardless if it was
   * successful or not. I.e. in case of a WMS it would return true, if the theme connected to the WMS and the connection
   * was finished. It does not matter if it could successfully retrieve the image or not.
   *
   * @return <code>true</code>, if the first loading try has finished
   */
  boolean isLoaded( );

  IStatus getStatus( );

  boolean isVisible( );

  void setVisible( final boolean visible );

  /**
   * Returns the names of all known properties.
   */
  String[] getPropertyNames( );

  /**
   * Retrieves the indicated property from this theme.<br/>
   * The name of the property should be one of the <code>PROPERTY_</code> constants of this interface.<br/>
   *
   * @param defaultValue
   *          If the property is not set, use this default value.
   * @throws IllegalArgumentException
   *           If the given property name is unknown.
   */
  String getProperty( final String name, final String defaultValue );

  /**
   * Sets the given property of this theme.<br>
   * The name of the property should be one of the <code>PROPERTY_</code> constants of this interface.
   */
  void setProperty( final String name, final String value );

  /**
   * This function returns the ID of this theme.
   *
   * @return The ID of this theme.
   */
  String getId( );

  /**
   * This function sets the id of this theme.
   *
   * @param id
   *          The id of this theme.
   */
  void setId( String id );
}