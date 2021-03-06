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
package org.kalypso.ogc.gml.map.layer;

import java.awt.Graphics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.map.IMapLayer;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypsodeegree.graphics.transformation.GeoTransform;

/**
 * @author Gernot Belger
 */
public class SelectionMapLayer implements IMapLayer
{
  private final IKalypsoFeatureTheme[] m_selectionThemes;

  private final IMapPanel m_panel;

  public SelectionMapLayer( final IMapPanel panel, final IKalypsoFeatureTheme[] selectionThemes )
  {
    m_panel = panel;
    m_selectionThemes = selectionThemes;
  }

  /**
   * @see org.kalypso.ogc.gml.map.IMapLayer#dispose()
   */
  @Override
  public void dispose( )
  {
    // Nothing to do
  }

  @Override
  public void paint( final Graphics g, final GeoTransform world2screen, final IProgressMonitor monitor )
  {
    monitor.beginTask( getLabel(), m_selectionThemes.length );
    for( final IKalypsoFeatureTheme theme : m_selectionThemes )
    {
      theme.paint( g, world2screen, Boolean.TRUE, new SubProgressMonitor( monitor, 1 ) );
      ProgressUtilities.worked( monitor, 0 );
    }
  }

  @Override
  public String getLabel( )
  {
    return Messages.getString( "org.kalypso.ogc.gml.map.layer.SelectionMapLayer.0" ); //$NON-NLS-1$
  }

  @Override
  public IMapPanel getMapPanel( )
  {
    return m_panel;
  }
}
