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
package org.kalypso.model.wspm.ui.profil.wizard.pointsInsert.impl;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.kalypso.model.wspm.core.KalypsoModelWspmCoreExtensions;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.core.profil.serializer.IProfileSource;
import org.kalypso.model.wspm.core.profil.serializer.ProfileSerializerUtilitites;
import org.kalypso.model.wspm.ui.profil.wizard.pointsInsert.AbstractPointsSource;
import org.kalypso.observation.result.IRecord;

/**
 * @author Belger
 */
public class FilePointsSource extends AbstractPointsSource
{
  protected Text m_fileName;

  /**
   * @see org.kalypso.model.wspm.ui.profil.wizard.pointsInsert.IPointsSource#getPoints()
   */
  @Override
  public List<IRecord> getPoints( )
  {
    final File f = new File( m_fileName.getText() );

    try
    {
      final IProfileSource prfS = KalypsoModelWspmCoreExtensions.createProfilSource( "prf" ); //$NON-NLS-1$
      // TODO: here the profile type is directly given (always read as pasche)
      // change this later to let the user choose how to read

      final IProfile[] profiles = ProfileSerializerUtilitites.readProfile( prfS, f, "org.kalypso.model.wspm.tuhh.profiletype" ); //$NON-NLS-1$
      return profiles == null || profiles.length < 0 ? null : profiles[0].getResult();
    }
    catch( final Exception e )
    {
      // TODO: ErrorHandling
      e.printStackTrace();
    }
    return null;

  }

  @Override
  public Control doCreateControl( final Composite parent )
  {
    final Composite panel = new Composite( parent, SWT.NONE );
    panel.setLayout( new GridLayout( 3, false ) );

    final Label label = new Label( panel, SWT.NONE );
    label.setLayoutData( new GridData() );
    label.setText( org.kalypso.model.wspm.ui.i18n.Messages.getString( "org.kalypso.model.wspm.ui.profil.wizard.pointsInsert.impl.FilePointsSource.0" ) ); //$NON-NLS-1$
    m_fileName = new Text( panel, SWT.BORDER );
    m_fileName.setLayoutData( new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL ) );

    final Button button = new Button( panel, SWT.NONE );
    button.setText( "..." ); //$NON-NLS-1$
    button.setLayoutData( new GridData() );

    button.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        final FileDialog dlg = new FileDialog( parent.getShell() );
        final String fileName = dlg.open();
        if( fileName != null )
        {
          m_fileName.setText( fileName );
        }
      }
    } );

    return panel;
  }

  @Override
  protected void loadState( final IDialogSettings settings )
  {
    final String fileName = settings.get( "DLG_SETTINGS_FILENAME" ); //$NON-NLS-1$
    if( fileName != null )
    {
      m_fileName.setText( fileName );
    }

  }

  @Override
  public void saveState( final IDialogSettings settings )
  {
    settings.put( "DLG_SETTINGS_FILENAME", m_fileName.getText() ); //$NON-NLS-1$

  }
}
