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
package org.kalypso.ui.editor.styleeditor.symbolizer;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.ui.editor.styleeditor.binding.IStyleInput;
import org.kalypso.ui.editor.styleeditor.binding.StyleInput;
import org.kalypso.ui.editor.styleeditor.colorMapEntryTable.ColorMapEntryTable;
import org.kalypso.ui.editor.styleeditor.panels.ModeSelectionComboPanel;
import org.kalypso.ui.editor.styleeditor.panels.PanelEvent;
import org.kalypso.ui.editor.styleeditor.panels.PanelListener;
import org.kalypso.ui.editor.styleeditor.preview.SymbolizerPreview;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.graphics.sld.RasterSymbolizer;

/**
 * @author F.Lindemann
 */
public class RasterSymbolizerLayout extends AbstractSymbolizerComposite<RasterSymbolizer>
{
  public RasterSymbolizerLayout( final FormToolkit toolkit, final Composite parent, final IStyleInput<RasterSymbolizer> input )
  {
    super( toolkit, parent, input );
  }

  @Override
  protected Control createContent( final FormToolkit toolkit, final Composite parent )
  {
    final Composite panel = toolkit.createComposite( parent );
    GridLayoutFactory.fillDefaults().applyTo( panel );

    final RasterSymbolizer rasterSymbolizer = getSymbolizer();

    // ***** ComboBox Mode Panel
    final ModeSelectionComboPanel modeComboPanel = new ModeSelectionComboPanel( panel, Messages.getString( "org.kalypso.ui.editor.styleeditor.symbolizerLayouts.RasterSymbolizerLayout.0" ), 0 ); //$NON-NLS-1$
    modeComboPanel.addPanelListener( new PanelListener()
    {
      @Override
      public void valueChanged( final PanelEvent event )
      {
        fireStyleChanged();
      }
    } );

    // ***** Table
    final Composite tableComposite = new Composite( panel, SWT.NULL );
    final StyleInput<RasterSymbolizer> input = new StyleInput<>( rasterSymbolizer, getInput() );
    new ColorMapEntryTable( tableComposite, input );

    return panel;
  }

  @Override
  protected SymbolizerPreview<RasterSymbolizer> createPreview( final Composite parent, final Point size, final IStyleInput<RasterSymbolizer> input )
  {
    return null;
  }

  @Override
  protected void doUpdateControl( )
  {
    // FIXME
  }
}