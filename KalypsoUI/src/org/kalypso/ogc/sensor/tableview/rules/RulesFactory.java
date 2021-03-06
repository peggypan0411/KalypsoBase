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
package org.kalypso.ogc.sensor.tableview.rules;

import org.kalypso.commons.java.util.StringUtilities;
import org.kalypso.ogc.sensor.status.KalypsoStati;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.template.obstableview.TypeRenderingRule;

/**
 * RulesFactory
 * 
 * @author schlienger
 */
public class RulesFactory
{
  private static ITableViewRules DEFAULT_RULES = null;

  private RulesFactory( )
  {
    // not to be instanciated
  }

  /**
   * Factory method for creating a RenderingRule object with a binding object.
   * 
   * @return RenderingRule
   */
  public static RenderingRule createRenderingRule( final TypeRenderingRule rr )
  {
    final int mask = rr.getMask();
    final String fg = rr.getForegroundcolor();
    final String bg = rr.getBackgroundcolor();
    final String font = rr.getFont();
    final String tt = rr.getTooltip();
    final String icon = rr.getIcon();

    return new RenderingRule( mask, fg == null ? null : StringUtilities.stringToColor( fg ), bg == null ? null : StringUtilities.stringToColor( bg ), font == null ? null
        : StringUtilities.stringToFont( font ), tt, KalypsoStatusUtils.getIconFor( icon ) );
  }

  /**
   * @return default rules for Kalypso (only a copy is returned!)
   */
  public static synchronized ITableViewRules getDefaultRules( )
  {
    // lazy loading
    if( DEFAULT_RULES == null )
    {
      DEFAULT_RULES = new Rules();

      final int[] bits = { KalypsoStati.BIT_CHECK, KalypsoStati.BIT_REQUIRED, KalypsoStati.BIT_USER_MODIFIED, KalypsoStati.BIT_DERIVATED, KalypsoStati.BIT_DERIVATION_ERROR };

      for( final int bit : bits )
      {
        DEFAULT_RULES.addRule( new RenderingRule( bit, KalypsoStatusUtils.getForegroundFor( bit ), KalypsoStatusUtils.getBackgroundFor( bit ), null, KalypsoStatusUtils.getTooltipFor( bit ), KalypsoStatusUtils.getIconFor( bit ) ) );
      }
    }

    return DEFAULT_RULES.cloneRules();
  }
}