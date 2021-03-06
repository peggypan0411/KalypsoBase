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

package org.kalypso.ogc.gml.outline.nodes;

import org.kalypso.ogc.gml.IKalypsoUserStyle;

public class UserStyleNode extends KalypsoStyleNode<IKalypsoUserStyle>
{
  UserStyleNode( final IThemeNode parent, final IKalypsoUserStyle style )
  {
    super( parent, style );
  }

  public IKalypsoUserStyle getStyle( )
  {
    return getElement();
  }

  @Override
  public String toString( )
  {
    final IKalypsoUserStyle style = getElement();
    if( style == null )
      return "<no styles set>"; //$NON-NLS-1$

    if( style.getName() != null )
      return style.getName();

    return style.toString();
  }

  @Override
  protected Object[] getElementChildren( )
  {
    final IKalypsoUserStyle style = getElement();
    return style.getFeatureTypeStyles();
  }

  @Override
  public String getLabel( )
  {
    final IKalypsoUserStyle userStyle = getStyle();
    final String label = userStyle.getLabel();

    return resolveI18nString( label );
  }

  @Override
  public String getDescription( )
  {
    final String tooltip = getStyle().getAbstract();
    return resolveI18nString( tooltip );
  }

  @Override
  public String resolveI18nString( final String label )
  {
    return getElement().resolveI18nString( label );
  }
}