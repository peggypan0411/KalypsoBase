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
package org.kalypso.zml.ui.core.provider.style;

import org.eclipse.swt.graphics.RGB;
import org.kalypso.template.obsdiagview.TypeCurve;
import org.kalypso.template.obsdiagview.TypeCurve.Stroke;

import de.openali.odysseus.chart.framework.model.figure.impl.IDefaultStyles;
import de.openali.odysseus.chart.framework.model.style.impl.LineStyle;
import de.openali.odysseus.chart.framework.model.style.impl.PointStyle;

/**
 * @author Dirk Kuch
 */
public class CurveStyleSet extends AbstractStyleSetProvider
{
  public CurveStyleSet( final TypeCurve[] curves )
  {
    buildLineStyles( curves );
    buildPointStyles( curves );

    // TODO
// buildTextStyles( curves );
// buildAreaStyles( curves );
  }

  private void buildLineStyles( final TypeCurve[] curves )
  {
    for( final TypeCurve curve : curves )
    {
      final String id = curve.getId();

      final String color = curve.getColor();
      final RGB rgb = getRgb( color );

      final Stroke stroke = curve.getStroke();
      final int width = getWidth( stroke );

      final LineStyle lineStyle = new LineStyle( width, rgb, IDefaultStyles.DEFAULT_ALPHA, IDefaultStyles.DEFAULT_DASHOFFSET, IDefaultStyles.DEFAULT_DASHARRAY, IDefaultStyles.DEFAULT_LINEJOIN, IDefaultStyles.DEFAULT_LINECAP, IDefaultStyles.DEFAULT_MITERLIMIT, IDefaultStyles.DEFAULT_VISIBILITY );
      getStyleSet().addStyle( LINE_PREFIX + id, lineStyle );
    }
  }

  private int getWidth( final Stroke stroke )
  {
    if( stroke == null )
      return 1;

    return Float.valueOf( stroke.getWidth() ).intValue();
  }

  private RGB getRgb( final String color )
  {
    final String[] colors = color.split( "\\;" ); //$NON-NLS-1$
    return new RGB( Integer.valueOf( colors[0] ), Integer.valueOf( colors[1] ), Integer.valueOf( colors[2] ) );
  }

  private void buildPointStyles( final TypeCurve[] curves )
  {
    for( final TypeCurve curve : curves )
    {
      final String id = curve.getId();

      final String color = curve.getColor();
      final RGB rgb = getRgb( color );

      final Stroke stroke = curve.getStroke();
      final int width = getWidth( stroke );

      final PointStyle pointStyle = new PointStyle( getLineStyle( id ), width, width, IDefaultStyles.DEFAULT_ALPHA, rgb, IDefaultStyles.DEFAULT_FILL_VISIBILITY, IDefaultStyles.DEFAULT_MARKER, IDefaultStyles.DEFAULT_VISIBILITY );

      getStyleSet().addStyle( POINT_PREFIX + id, pointStyle );
    }
  }
}
