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
package org.kalypso.ui.editor.sldEditor;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.kalypso.contribs.eclipse.swt.awt.ImageConverter;
import org.kalypso.contribs.eclipse.swt.layout.Layouts;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.filterencoding.FilterEvaluationException;
import org.kalypsodeegree.graphics.sld.Fill;
import org.kalypsodeegree_impl.graphics.sld.awt.FillPainter;

/**
 * @author Thomas Jung
 */
public class FillEditorComposite extends Composite
{
  private final Set<IFillModifyListener> m_listeners = new HashSet<>();

  private final Fill m_fill;

  private Color m_color;

  private Label m_colorLabel;

  private Image m_preview;

  private Composite m_previewComp;

  private final Boolean m_previewVisible;

  public FillEditorComposite( final Composite parent, final int style, final Fill fill, final Boolean previewVisible )
  {
    super( parent, style );
    m_fill = fill;
    m_previewVisible = previewVisible;

    try
    {
      createControl();
      if( m_previewVisible == true )
        updatePreview();

    }
    catch( final FilterEvaluationException e )
    {
      e.printStackTrace();
    }
  }

  private void createControl( ) throws FilterEvaluationException
  {
    setLayout( Layouts.createGridLayout( 2 ) );

    createColorControl();

    createOpacityControl();

    // uncommenten until we support different fill types
// createTypeControl();

    if( m_previewVisible == true )
      createPreviewControl();

  }

  private void createColorControl( ) throws FilterEvaluationException
  {
    /* Color */
    final Label colorTextLabel = new Label( this, SWT.NONE );
    colorTextLabel.setText( Messages.getString( "org.kalypso.ui.editor.sldEditor.FillEditorComposite.0" ) ); //$NON-NLS-1$

    m_colorLabel = new Label( this, SWT.BORDER );
    m_colorLabel.setText( "     " ); //$NON-NLS-1$
    final GridData gridData = new GridData( SWT.END, SWT.CENTER, true, false );
    gridData.widthHint = 16;
    gridData.heightHint = 16;

    m_colorLabel.setLayoutData( gridData );

    final java.awt.Color fillColor = m_fill.getFill( null );
    m_color = new Color( m_colorLabel.getDisplay(), fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue() );

    m_colorLabel.setBackground( m_color );

    /* mouse listeners */
    m_colorLabel.addMouseListener( new MouseAdapter()
    {
      @SuppressWarnings( "synthetic-access" )//$NON-NLS-1$
      @Override
      public void mouseDown( final MouseEvent e )
      {
        final ColorDialog colorDialog = new ColorDialog( FillEditorComposite.this.getShell() );
        final RGB chosenColor = colorDialog.open();
        if( chosenColor != null )
        {
          m_color.dispose();
          m_color = new Color( m_colorLabel.getDisplay(), chosenColor.red, chosenColor.green, chosenColor.blue );
          m_fill.setFill( new java.awt.Color( chosenColor.red, chosenColor.green, chosenColor.blue ) );
        }
        m_colorLabel.setBackground( m_color );
        contentChanged();
      }
    } );

    m_colorLabel.addMouseTrackListener( new MouseTrackAdapter()
    {
      /**
       * @see org.eclipse.swt.events.MouseTrackAdapter#mouseEnter(org.eclipse.swt.events.MouseEvent)
       */
      @Override
      public void mouseEnter( final MouseEvent e )
      {
        setCursor( new Cursor( null, SWT.CURSOR_HAND ) );
      }

      /**
       * @see org.eclipse.swt.events.MouseTrackAdapter#mouseExit(org.eclipse.swt.events.MouseEvent)
       */
      @Override
      public void mouseExit( final MouseEvent e )
      {
        setCursor( new Cursor( null, SWT.CURSOR_ARROW ) );
      }
    } );
  }

// private void createTypeControl( )
// {
// /* fill type combo */
// // combo text
// final Label comboTextLabel = new Label( this, SWT.NONE );
//    comboTextLabel.setText( Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.3") ); //$NON-NLS-1$
//
// final ComboViewer fillTypeCombo = new ComboViewer( this, SWT.READ_ONLY );
// final GridData comboGridData = new GridData( SWT.FILL, SWT.CENTER, true, false );
//
// fillTypeCombo.getControl().setLayoutData( comboGridData );
// fillTypeCombo.setContentProvider( new ArrayContentProvider() );
//
// final String[] types = new String[4];
//    types[0] = Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.4"); //$NON-NLS-1$
//    types[1] = Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.5"); //$NON-NLS-1$
//    types[2] = Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.6"); //$NON-NLS-1$
//    types[3] = Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.7"); //$NON-NLS-1$
// fillTypeCombo.setInput( types );
// fillTypeCombo.setSelection( new StructuredSelection( fillTypeCombo.getElementAt( 0 ) ) );
// fillTypeCombo.getControl().setEnabled( false );
//
// fillTypeCombo.setLabelProvider( new LabelProvider()
// {
// /**
// * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
// */
// @Override
// public String getText( final Object element )
// {
// return super.getText( element );
// }
// } );
//
// // selection listener
// fillTypeCombo.addSelectionChangedListener( new ISelectionChangedListener()
// {
// @Override
//      @SuppressWarnings("synthetic-access") //$NON-NLS-1$
// public void selectionChanged( final SelectionChangedEvent event )
// {
// final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
// final Object element = selection.getFirstElement();
//
// final String string = (String) element;
//
// // TODO: get the GraphicFill from a GraphicFill editor.
// // right now, there is just possible a plain fill .
//
// final GraphicFill graphicFill = null;
//
//        if( string == Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.9") ) //$NON-NLS-1$
// {
// m_fill.setGraphicFill( null );
// }
//        else if( string == Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.10") ) //$NON-NLS-1$
// {
// }
//        else if( string == Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.11") ) //$NON-NLS-1$
// {
// }
//        else if( string == Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.12") ) //$NON-NLS-1$
// {
// }
// m_fill.setGraphicFill( graphicFill );
//
// contentChanged();
// }
// } );
//
// final Label addGraphicLabel = new Label( this, SWT.NONE );
// addGraphicLabel.setLayoutData( new GridData( SWT.BEGINNING, SWT.CENTER, true, false ) );
//    addGraphicLabel.setText( Messages.getString("org.kalypso.ui.editor.sldEditor.FillEditorComposite.13") ); //$NON-NLS-1$
//
// final Button addGraphicButton = new Button( this, SWT.NONE );
// final GridData addGraphicData = new GridData( SWT.END, SWT.CENTER, true, false );
// addGraphicData.widthHint = 20;
//
// addGraphicButton.setLayoutData( addGraphicData );
//
// // final Image editImage = Kalypso1d2dProjectPlugin.getImageProvider().getImageDescriptor(
// // DESCRIPTORS.RESULT_VIEWER_EDIT ).createImage();
// // addGraphicButton.setImage( editImage );
// }

  private void createOpacityControl( ) throws FilterEvaluationException
  {
    /* color opacity */
    // spinner text
    final Label opacityTextLabel = new Label( this, SWT.NONE );
    opacityTextLabel.setText( Messages.getString( "org.kalypso.ui.editor.sldEditor.FillEditorComposite.14" ) ); //$NON-NLS-1$

    final Spinner opacitySpinner = new Spinner( this, SWT.NONE );
    opacitySpinner.setLayoutData( new GridData( SWT.END, SWT.CENTER, true, false ) );
    opacitySpinner.setBackground( getBackground() );
    double opacity = m_fill.getOpacity( null );
    if( Double.isNaN( opacity ) || opacity > 1.0 || opacity < 0.0 )
      opacity = 1.0;
    final BigDecimal selectionValue = new BigDecimal( opacity * 100 ).setScale( 0, BigDecimal.ROUND_HALF_UP );
    opacitySpinner.setValues( selectionValue.intValue(), 1, 100, 0, 1, 10 );

    opacitySpinner.addSelectionListener( new SelectionAdapter()
    {

      @SuppressWarnings( "synthetic-access" )//$NON-NLS-1$
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        final double opac = new BigDecimal( opacitySpinner.getSelection() ).setScale( 2, BigDecimal.ROUND_HALF_UP ).divide( new BigDecimal( 100 ), BigDecimal.ROUND_HALF_UP ).doubleValue();
        m_fill.setOpacity( opac );
        contentChanged();
      }
    } );
  }

  private void createPreviewControl( )
  {
    final Group previewGroup = new Group( this, SWT.NONE );
    previewGroup.setLayout( new GridLayout() );
    final GridData previewGridData = new GridData( SWT.FILL, SWT.CENTER, true, false );
    previewGridData.horizontalSpan = 2;
    previewGridData.heightHint = 30;
    previewGroup.setLayoutData( previewGridData );
    previewGroup.setText( Messages.getString( "org.kalypso.ui.editor.sldEditor.FillEditorComposite.16" ) ); //$NON-NLS-1$

    /* preview */
    m_previewComp = new Composite( previewGroup, SWT.NONE );
    final GridData previewCompData = new GridData( SWT.FILL, SWT.CENTER, true, false );
    previewCompData.heightHint = 22;
    m_previewComp.setLayoutData( previewCompData );

    addDisposeListener( new DisposeListener()
    {
      @Override
      public void widgetDisposed( final DisposeEvent e )
      {
        disposeControl();
      }

    } );

    m_previewComp.addControlListener( new ControlAdapter()
    {
      @SuppressWarnings( "synthetic-access" )//$NON-NLS-1$
      @Override
      public void controlResized( final ControlEvent e )
      {
        if( m_previewVisible == true )
          updatePreview();
      }
    } );
  }

  @SuppressWarnings( "static-access" )//$NON-NLS-1$
  private void updatePreview( )
  {
    final Point point = m_previewComp.getSize();
    final BigDecimal width = new BigDecimal( point.x ).setScale( 0 );
    final BigDecimal height = new BigDecimal( point.y ).setScale( 0 );

    if( width.intValue() == 0 || height.intValue() == 0 )
      return;

    final BufferedImage bufferedImage = new BufferedImage( width.intValue(), height.intValue(), BufferedImage.TYPE_INT_RGB );

    final Graphics2D g2D = bufferedImage.createGraphics();
    g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

    g2D.setPaintMode();

    g2D.setColor( java.awt.Color.WHITE );
    g2D.fillRect( 0, 0, width.intValue(), height.intValue() );

    g2D.setColor( java.awt.Color.BLACK );
    final Font font = new Font( "SansSerif", Font.BOLD, height.intValue() ); //$NON-NLS-1$
    g2D.setFont( font );

    /* demo text */
    final String title = Messages.getString( "org.kalypso.ui.editor.sldEditor.FillEditorComposite.20" ); //$NON-NLS-1$
    g2D.drawString( title, width.divide( new BigDecimal( 2 ), 0, BigDecimal.ROUND_HALF_UP ).intValue() - 30, height.divide( new BigDecimal( 1.2 ), 0, BigDecimal.ROUND_HALF_UP ).intValue() );

    FillPainter painter;

    /* fill preview painting */
    try
    {
      painter = new FillPainter( m_fill, null, null, null );
      painter.prepareGraphics( g2D );
      g2D.fillRect( 0, 0, width.intValue(), height.intValue() );
    }
    catch( final FilterEvaluationException e )
    {
      e.printStackTrace();
    }

    final ImageConverter converter = new ImageConverter();
    final ImageData convertToSWT = converter.convertToSWT( bufferedImage );

    m_preview = new Image( getDisplay(), convertToSWT );
    m_previewComp.setBackgroundImage( m_preview );
  }

  protected void disposeControl( )
  {
    m_color.dispose();
    m_preview.dispose();

    m_listeners.clear();
  }

  /**
   * Add the listener to the list of listeners. If an identical listeners has already been registered, this has no
   * effect.
   */
  public void addModifyListener( final IFillModifyListener l )
  {
    m_listeners.add( l );
  }

  public void removeModifyListener( final IFillModifyListener l )
  {
    m_listeners.remove( l );
  }

  protected void fireModified( )
  {
    final IFillModifyListener[] ls = m_listeners.toArray( new IFillModifyListener[m_listeners.size()] );
    for( final IFillModifyListener fillModifyListener : ls )
      fillModifyListener.onFillChanged( this, m_fill );
  }

  protected void contentChanged( )
  {
    if( m_previewVisible == true )
      updatePreview();
    fireModified();
  }
}
