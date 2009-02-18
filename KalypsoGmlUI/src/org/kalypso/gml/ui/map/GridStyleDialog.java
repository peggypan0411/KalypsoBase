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
package org.kalypso.gml.ui.map;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.ui.editor.sldEditor.RasterColorMapEditorComposite;
import org.kalypsodeegree.graphics.sld.ColorMapEntry;

/**
 * Dialog for creating a RasterColorMap (defined by a {@link ColorMapEntry} array) via the definition of a start
 * {@link ColorMapEntry} and an end {@link ColorMapEntry}. This could be done via a
 * {@link RasterColorMapEditorComposite}. In this composite the user can specify the colors, values and opacities for
 * each of these two ColorMapEntries. The classes inbetween get interpolated.
 * 
 * @author Thomas jung
 */
public class GridStyleDialog extends TitleAreaDialog
{
  private ColorMapEntry[] m_colorMap;

  private RasterColorMapEditorComposite m_rasterComponent;

  private final BigDecimal m_globalMin;

  private final BigDecimal m_globalMax;

  public GridStyleDialog( final Shell shell, final ColorMapEntry[] colorMap, final BigDecimal min, final BigDecimal max )
  {
    super( shell );
    m_colorMap = colorMap;

    m_globalMin = min;
    m_globalMax = max;

    setTitle( "Farbverlauf erzeugen" );
  }

  /**
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea( Composite parent )
  {
    setMessage( "W�hlen Sie Anfangs- und Endfarbe f�r den Farbverlauf." );

    parent.getShell().setText( "Farbverlauf" );

    final Composite panel = (Composite) super.createDialogArea( parent );

    m_rasterComponent = new RasterColorMapEditorComposite( panel, SWT.NONE, m_colorMap, m_globalMin, m_globalMax )
    {
      @Override
      protected void colorMapChanged( )
      {
      }
    };
    return panel;
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed( )
  {
    final List<ColorMapEntry> colorMapEntries = m_rasterComponent.getColorMap();
    m_colorMap = colorMapEntries.toArray( new ColorMapEntry[colorMapEntries.size()] );

    super.okPressed();
  }

  public ColorMapEntry[] getColorMap( )
  {
    return m_colorMap;
  }
}
