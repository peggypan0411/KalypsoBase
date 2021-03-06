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
package org.kalypso.ogc.gml.featureview.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.kalypso.contribs.eclipse.swt.events.DoubleModifyListener;
import org.kalypso.contribs.eclipse.swt.layout.Layouts;
import org.kalypso.contribs.java.lang.NumberUtils;
import org.kalypso.transformation.ui.CRSSelectionPanel;
import org.kalypso.transformation.ui.listener.CRSSelectionListener;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * This class builds the dialog for the data input for the GM_Point.
 * 
 * @author Holger Albert
 */
public class PointDialog extends Dialog
{
  private double m_values[];

  private String m_cs;

  private Label m_label[];

  private Text m_text[];

  public PointDialog( final Shell parent, final double[] values, final String cs )
  {
    super( parent );

    m_values = values;
    m_cs = cs;
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea( final Composite parent )
  {
    /* Create a own composite for placing controls. */
    final Composite panel = (Composite)super.createDialogArea( parent );

    /* Configuring the composite. */
    final Shell shell = panel.getShell();
    shell.setText( Messages.getString( "org.kalypso.ogc.gml.featureview.dialog.PointDialog.data" ) ); //$NON-NLS-1$

    /* The label for the input data. */
    final Label label = new Label( panel, SWT.NONE );
    label.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    label.setText( Messages.getString( "org.kalypso.ogc.gml.featureview.dialog.PointDialog.fillin" ) ); //$NON-NLS-1$
    label.setAlignment( SWT.LEFT );

    /* A new group for the labels and texts. */
    final Group group = new Group( panel, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

    /* Configuring the group. */
    group.setLayout( new GridLayout( 2, true ) );

    final Display display = shell.getDisplay();
    final Color badColor = display.getSystemColor( SWT.COLOR_RED );
    final Color goodColor = display.getSystemColor( SWT.COLOR_BLACK );

    final DoubleModifyListener doubleModifyListener = new DoubleModifyListener( goodColor, badColor );

    m_label = new Label[m_values.length];
    m_text = new Text[m_values.length];

    for( int i = 0; i < m_values.length; i++ )
    {
      /* The label for the input data. */
      m_label[i] = new Label( group, SWT.NONE );
      m_label[i].setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

      m_label[i].setText( getAxisLable( i ) ); //$NON-NLS-1$
      m_label[i].setToolTipText( "" ); //$NON-NLS-1$

      m_label[i].setAlignment( SWT.LEFT );

      /* The text for the input data. */
      m_text[i] = new Text( group, SWT.BORDER );
      m_text[i].setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
      m_text[i].setText( Double.toString( m_values[i] ) );

      m_text[i].addModifyListener( new PointDialogListener( this, i ) );
      m_text[i].addModifyListener( doubleModifyListener );
    }

    /* The label for the input data. */
    final Label label1 = new Label( panel, SWT.NONE );
    label1.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    label1.setText( Messages.getString( "org.kalypso.ogc.gml.featureview.dialog.PointDialog.fillincs" ) ); //$NON-NLS-1$
    label1.setAlignment( SWT.LEFT );

    /* A new composite for the coordinate system panel. */
    final Composite combo_composite = new Composite( panel, SWT.NONE );
    combo_composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

    /* Configuring the group. */
    combo_composite.setLayout( Layouts.createGridLayout() );

    /* The coordinate system panel. */
    final CRSSelectionPanel crsPanel = new CRSSelectionPanel( combo_composite, SWT.NONE );
    crsPanel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

    if( getCS() != null )
      crsPanel.setSelectedCRS( getCS() );

    crsPanel.addSelectionChangedListener( new CRSSelectionListener()
    {
      @Override
      protected void selectionChanged( final String selectedCRS )
      {
        setCS( selectedCRS );
      }
    } );

    return panel;
  }

  private String getAxisLable( final int index )
  {
    switch( index )
    {
      case 0:
        return String.format( "X-%s", Messages.getString( "org.kalypso.ogc.gml.featureview.dialog.PointDialog.axis" ) ); //$NON-NLS-1$ //$NON-NLS-2$
      case 1:
        return String.format( "Y-%s", Messages.getString( "org.kalypso.ogc.gml.featureview.dialog.PointDialog.axis" ) ); //$NON-NLS-1$ //$NON-NLS-2$
      case 2:
        return String.format( "Z-%s", Messages.getString( "org.kalypso.ogc.gml.featureview.dialog.PointDialog.axis" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    throw new UnsupportedOperationException();
  }

  /**
   * Diese Funktion setzt den Status des OK-Buttons, nach dem Status der Text-Felder.
   */
  public void checkModified( )
  {
    getButton( IDialogConstants.OK_ID ).setEnabled( true );

    for( int i = 0; i < m_text.length; i++ )
    {
      if( !NumberUtils.isDouble( m_text[i].getText() ) )
        getButton( IDialogConstants.OK_ID ).setEnabled( false );
    }
  }

  /**
   * This function returns the values.
   * 
   * @return The values.
   */
  public double[] getValues( )
  {
    return m_values;
  }

  /**
   * This function sets the values.
   * 
   * @param values
   *          The values.
   */
  public void setValues( final double[] values )
  {
    m_values = values;
  }

  /**
   * This function returns the cs.
   * 
   * @return The cs.
   */
  public String getCS( )
  {
    return m_cs;
  }

  /**
   * This function sets the cs.
   * 
   * @param cs
   *          The cs.
   */
  public void setCS( final String cs )
  {
    m_cs = cs;
  }
}