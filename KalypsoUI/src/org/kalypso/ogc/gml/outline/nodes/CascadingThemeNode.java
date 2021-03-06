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

import org.eclipse.core.runtime.Assert;
import org.kalypso.ogc.gml.IKalypsoCascadingTheme;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.mapmodel.IMapModellListener;
import org.kalypso.ogc.gml.mapmodel.MapModellAdapter;

/**
 * @author Gernot Belger
 */
class CascadingThemeNode extends KalypsoThemeNode<IKalypsoCascadingTheme>
{
  private final IMapModellListener m_mapModellListener = new MapModellAdapter()
  {
    @Override
    public void themeAdded( final IMapModell source, final org.kalypso.ogc.gml.IKalypsoTheme theme )
    {
      refreshViewer( CascadingThemeNode.this );
    }

    @Override
    public void themeRemoved( final IMapModell source, final org.kalypso.ogc.gml.IKalypsoTheme theme, final boolean lastVisibility )
    {
      refreshViewer( CascadingThemeNode.this );
    }

    @Override
    public void themeOrderChanged( final IMapModell source )
    {
      refreshViewer( CascadingThemeNode.this );
    }
  };

  CascadingThemeNode( final IThemeNode parent, final IKalypsoCascadingTheme theme )
  {
    super( parent, theme );

    Assert.isNotNull( theme );

    theme.addMapModelListener( m_mapModellListener );
  }

  @Override
  public void dispose( )
  {
    getElement().removeMapModelListener( m_mapModellListener );

    super.dispose();
  }

  @Override
  protected Object[] getElementChildren( )
  {
    final IKalypsoCascadingTheme theme = getElement();
    return theme.getAllThemes();
  }

  @Override
  public boolean isCompactable( )
  {
    return false;
  }

  @Override
  protected boolean doReverseChildren( )
  {
    return false;
  }
}