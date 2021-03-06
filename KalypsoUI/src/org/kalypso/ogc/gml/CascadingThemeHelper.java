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
package org.kalypso.ogc.gml;

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.ObjectUtils;
import org.kalypso.ogc.gml.mapmodel.IMapModell;

/**
 * @author Dirk Kuch
 */
public class CascadingThemeHelper
{
  public static final String PROPERTY_THEME_ID = "themeId"; //$NON-NLS-1$

  /**
   * Finds Cascading theme with the given name from the map model. Searches only for instances of CascadingKalypsoTheme
   * 
   * @param mapModell
   * @param themeName
   * @return CascadingKalypsoTheme, or null if no theme with that name is found
   */
  public static final IKalypsoCascadingTheme getNamedCascadingTheme( final IMapModell mapModell, final String themeName )
  {
    final IKalypsoTheme[] allThemes = mapModell.getAllThemes();
    for( final IKalypsoTheme kalypsoTheme : allThemes )
    {
      if( kalypsoTheme instanceof IKalypsoCascadingTheme && kalypsoTheme.getName().getKey().equals( themeName ) )
        return (IKalypsoCascadingTheme)kalypsoTheme;
    }

    return null;
  }

  /**
   * Finds Cascading theme with the given name or given theme property from the map model. Searches only for instances
   * of CascadingKalypsoTheme. This code is for old projects, in which the maps didn't have the property set.
   * 
   * @param mapModell
   * @param themeName
   * @param themeProperty
   * @return CascadingKalypsoTheme, or null if no theme with that name is found
   */
  public static final IKalypsoCascadingTheme getNamedCascadingTheme( final IMapModell mapModell, final String themeName, final String themeProperty )
  {
    final IKalypsoTheme[] allThemes = mapModell.getAllThemes();
    for( final IKalypsoTheme kalypsoTheme : allThemes )
    {
      final String themeProp = kalypsoTheme.getProperty( PROPERTY_THEME_ID, StringUtils.EMPTY );

      if( kalypsoTheme instanceof IKalypsoCascadingTheme && kalypsoTheme.getName().getKey().equals( themeName ) )
        return (IKalypsoCascadingTheme)kalypsoTheme;

      else if( kalypsoTheme instanceof IKalypsoCascadingTheme && themeProp.equals( themeProperty ) )
        return (IKalypsoCascadingTheme)kalypsoTheme;
    }

    return null;
  }

  /**
   * Finds Cascading theme where the property 'themeId' is set to the given value. Searches only for instances of
   * CascadingKalypsoTheme
   * 
   * @param mapModell
   * @param themeProperty
   * @return CascadingKalypsoTheme, or null if no theme with that name is found
   */
  public static final IKalypsoCascadingTheme getCascadingThemeByProperty( final IMapModell mapModell, final String themeID )
  {
    final IKalypsoTheme[] allThemes = mapModell.getAllThemes();
    for( final IKalypsoTheme kalypsoTheme : allThemes )
    {
      final String themeProp = kalypsoTheme.getProperty( PROPERTY_THEME_ID, StringUtils.EMPTY );

      if( kalypsoTheme instanceof IKalypsoCascadingTheme && themeProp.equals( themeID ) )
        return (IKalypsoCascadingTheme)kalypsoTheme;
    }

    return null;
  }

  /**
   * Search for a theme by its feature-path.
   * 
   * @return The child-theme which is a {@link IKalypsoFeatureTheme} and whose feature-path corresponds to the given
   *         one.
   */
  public static IKalypsoFeatureTheme findThemeWithFeaturePath( final IKalypsoCascadingTheme cascadingTheme, final String featurePath )
  {
    final IKalypsoTheme[] allThemes = cascadingTheme.getAllThemes();
    for( final IKalypsoTheme kalypsoTheme : allThemes )
    {
      if( kalypsoTheme instanceof IKalypsoFeatureTheme )
      {
        final IKalypsoFeatureTheme featureTheme = (IKalypsoFeatureTheme)kalypsoTheme;
        if( ObjectUtils.equal( featureTheme.getFeaturePath(), featurePath ) )
          return featureTheme;
      }
    }

    return null;
  }
}