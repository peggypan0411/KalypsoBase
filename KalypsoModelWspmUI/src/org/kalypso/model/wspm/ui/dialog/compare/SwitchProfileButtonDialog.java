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
package org.kalypso.model.wspm.ui.dialog.compare;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.model.wspm.core.gml.IProfileSelection;
import org.kalypso.model.wspm.core.gml.SimpleProfileSelection;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.ui.i18n.Messages;
import org.kalypso.model.wspm.ui.view.chart.IProfilChart;

import de.openali.odysseus.chart.framework.util.ChartUtilities;
import de.openali.odysseus.chart.framework.view.IChartComposite;

/**
 * @author Dirk Kuch
 */
public class SwitchProfileButtonDialog extends Composite
{
  private final Font TXT_BOLD;

  protected final IProfilChart m_chartView;

  protected final IProfile[] m_profiles;

  private Label m_label;

  public SwitchProfileButtonDialog( final Composite parent, final IProfilChart chartView, final IProfile[] profiles )
  {
    super( parent, SWT.NULL );

    TXT_BOLD = new Font( parent.getDisplay(), "Tahoma", 8, SWT.BOLD ); //$NON-NLS-1$
    m_chartView = chartView;
    m_profiles = profiles;

    setLayout( GridLayoutFactory.fillDefaults().numColumns( 4 ).create() );

    final FormToolkit toolkit = new FormToolkit( parent.getDisplay() );
    render( toolkit );

    toolkit.adapt( this );
  }

  private void render( final FormToolkit toolkit )
  {
    m_label = toolkit.createLabel( this, getProfileLabel() );
    m_label.setFont( TXT_BOLD );
    final GridData data = new GridData( GridData.FILL, GridData.CENTER, false, false );
    data.minimumWidth = 250;

    toolkit.createLabel( this, "" ).setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) ); //$NON-NLS-1$

    m_label.setLayoutData( data );

    final Button back = toolkit.createButton( this, Messages.getString( "SwitchProfileButtonDialog_1" ), SWT.PUSH ); //$NON-NLS-1$
    back.setLayoutData( getButtonLayoutData() );

    final Button next = toolkit.createButton( this, Messages.getString( "SwitchProfileButtonDialog_2" ), SWT.PUSH ); //$NON-NLS-1$
    next.setLayoutData( getButtonLayoutData() );

    back.addSelectionListener( new SelectionAdapter()
    {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        final int index = getIndex();
        if( index == 0 )
          return;

        setProfile( m_profiles[index - 1] );

      }
    } );

    next.addSelectionListener( new SelectionAdapter()
    {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        final int index = getIndex();
        if( index == m_profiles.length - 1 )
          return;

        setProfile( m_profiles[index + 1] );

      }
    } );
  }

  protected void setProfile( final IProfile profile )
  {
    m_chartView.setProfileSelection( new SimpleProfileSelection( profile ) );

    final IChartComposite chart = m_chartView.getChartComposite();
    if( chart != null )
    {
      ChartUtilities.maximize( chart.getChartModel() );
    }

    m_label.setText( getProfileLabel() );
    this.layout();
  }

  protected int getIndex( )
  {
    final IProfileSelection profileSelection = m_chartView.getProfileSelection();
    final IProfile profile = profileSelection.getProfile();
    return ArrayUtils.indexOf( m_profiles, profile );
  }

  protected String getProfileLabel( )
  {
    final IProfileSelection profileSelection = m_chartView.getProfileSelection();
    final IProfile profil = profileSelection.getProfile();
    final double station = profil.getStation();

    final String msg = String.format( Messages.getString( "SwitchProfileButtonDialog_3" ), station, getIndex() + 1, m_profiles.length ); //$NON-NLS-1$
    return msg;
  }

  private Object getButtonLayoutData( )
  {
    final GridData data = new GridData( GridData.FILL, GridData.FILL, false, false );
    data.widthHint = 150;

    return data;
  }

}
