/*--------------- Kalypso-Header ------------------------------------------

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

 --------------------------------------------------------------------------*/

package org.kalypso.ui.editor.diagrameditor;

import java.util.Arrays;
import java.util.TimeZone;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * @author schlienger
 */
public class DiagramPropertiesDialog extends TitleAreaDialog
{
  private static final String KALYPSO_DEFAULT = "<Kalypso Default>";//$NON-NLS-1$

  protected String m_diagTitle;

  protected boolean m_showLegend;

  protected String m_legendTitle;

  protected TimeZone m_tz;

  public DiagramPropertiesDialog( final Shell parentShell, final String diagTitle, final boolean showLegend, final String legendTitle, final TimeZone timezone )
  {
    super( parentShell );

    m_diagTitle = diagTitle;
    m_showLegend = showLegend;
    m_legendTitle = legendTitle;
    m_tz = timezone;
  }

  /**
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  @Override
  protected void configureShell( final Shell newShell )
  {
    super.configureShell( newShell );

    newShell.setText( Messages.getString( "org.kalypso.ui.editor.diagrameditor.DiagramPropertiesDialog.5" ) ); //$NON-NLS-1$
  }

  /**
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea( final Composite parent )
  {
    setTitle( Messages.getString( "org.kalypso.ui.editor.diagrameditor.DiagramPropertiesDialog.0" ) ); //$NON-NLS-1$

    final Composite cmp = new Composite( parent, SWT.FILL );
    cmp.setLayout( new GridLayout( 2, false ) );
    cmp.setLayoutData( new GridData( GridData.FILL_BOTH ) );

    final Label lblTitle = new Label( cmp, SWT.LEFT );
    lblTitle.setText( Messages.getString( "org.kalypso.ui.editor.diagrameditor.DiagramPropertiesDialog.1" ) ); //$NON-NLS-1$
    final Text txtTitle = new Text( cmp, SWT.BORDER );
    txtTitle.setText( m_diagTitle );
    txtTitle.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    txtTitle.addModifyListener( new ModifyListener()
    {
      @Override
      public void modifyText( final ModifyEvent e )
      {
        m_diagTitle = txtTitle.getText();
      }
    } );

    final Label lblShowLegend = new Label( cmp, SWT.LEFT );
    lblShowLegend.setText( Messages.getString( "org.kalypso.ui.editor.diagrameditor.DiagramPropertiesDialog.2" ) ); //$NON-NLS-1$
    final Button btnShowLegend = new Button( cmp, SWT.CHECK );
    btnShowLegend.setSelection( m_showLegend );
    btnShowLegend.addSelectionListener( new SelectionListener()
    {
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        m_showLegend = btnShowLegend.getSelection();
      }

      @Override
      public void widgetDefaultSelected( final SelectionEvent e )
      {
        // empty
      }
    } );

    final Label lblLegTitle = new Label( cmp, SWT.LEFT );
    lblLegTitle.setText( Messages.getString( "org.kalypso.ui.editor.diagrameditor.DiagramPropertiesDialog.3" ) ); //$NON-NLS-1$
    final Text txtLegTitle = new Text( cmp, SWT.BORDER );
    txtLegTitle.setText( m_legendTitle );
    txtLegTitle.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    txtLegTitle.addModifyListener( new ModifyListener()
    {
      @Override
      public void modifyText( final ModifyEvent e )
      {
        m_legendTitle = txtLegTitle.getText();
      }
    } );

    final Label lblTz = new Label( cmp, SWT.LEFT );
    lblTz.setText( Messages.getString( "org.kalypso.ui.editor.diagrameditor.DiagramPropertiesDialog.4" ) ); //$NON-NLS-1$
    final Combo cmbTz = new Combo( cmp, SWT.DROP_DOWN | SWT.READ_ONLY );
    cmbTz.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

    final String[] tz = TimeZone.getAvailableIDs();
    Arrays.sort( tz );
    final String[] items = new String[tz.length + 1];
    items[0] = KALYPSO_DEFAULT;
    System.arraycopy( tz, 0, items, 1, tz.length );
    cmbTz.setItems( items );

    if( m_tz == null )
      cmbTz.select( 0 );
    else
    {
      final int index = Arrays.binarySearch( tz, m_tz.getID() );
      cmbTz.select( index + 1 );
    }

    cmbTz.addModifyListener( new ModifyListener()
    {
      @Override
      public void modifyText( final ModifyEvent e )
      {
        handleTimeZoneChanged( cmbTz );
      }
    } );

    return cmp;
  }

  protected void handleTimeZoneChanged( final Combo cmbTz )
  {
    final String text = cmbTz.getText();
    if( KALYPSO_DEFAULT.equals( text ) )
      m_tz = null;
    else
      m_tz = TimeZone.getTimeZone( text );
  }

  public String getDiagramTitle( )
  {
    return m_diagTitle;
  }

  public boolean isShowLegend( )
  {
    return m_showLegend;
  }

  public String getLegendTitle( )
  {
    return m_legendTitle;
  }

  public String getTimezoneName( )
  {
    return m_tz == null ? null : m_tz.getID();
  }
}
