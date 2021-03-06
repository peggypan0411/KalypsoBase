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
package org.kalypso.ogc.gml.painter;

import java.util.List;

import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.IKalypsoFeatureTypeStyle;
import org.kalypso.ogc.gml.IKalypsoStyle;
import org.kalypso.ogc.gml.IKalypsoUserStyle;
import org.kalypsodeegree.graphics.sld.FeatureTypeStyle;
import org.kalypsodeegree.graphics.sld.Rule;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Gernot Belger
 */
public final class StylePainterFactory
{
  private StylePainterFactory( )
  {

  }

  /**
   * @param userStyleName
   *          Only used for an ugly hack. Should be removed. Save to give <code>null</code>.
   */
  public static IStylePainter create( final String userStyleName, final FeatureTypeStyle style, final List<Feature> features )
  {
    // FIXME: does not belong here!
    // hack, for speed only, is there any clearer
    // see RoughnessFilter and RoughnessRule
    // Evil: what will happen if another style with the same name is used?
    if( Messages.getString("StylePainterFactory_0").equals( userStyleName ) ) //$NON-NLS-1$
      return new RoughnessStylePainter( style, features );

    return new FeatureTypeStylePainter( style, features );
  }

  public static IStylePainter create( final Rule rule, final List<Feature> features )
  {
    return new RulePainter( rule, features );
  }

  public static IStylePainter create( final IKalypsoStyle style, final List<Feature> features )
  {
    if( style instanceof IKalypsoUserStyle )
      return new UserStylePainter( (IKalypsoUserStyle) style, features );

    if( style instanceof IKalypsoFeatureTypeStyle )
      return create( null, (IKalypsoFeatureTypeStyle) style, features );

    throw new UnsupportedOperationException();
  }

  public static IStylePainter create( final IKalypsoFeatureTheme theme, final Boolean paintSelected )
  {
    return new FeatureThemePainter( theme, paintSelected );
  }

}
