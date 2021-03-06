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
package org.kalypso.model.wspm.ui.view;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.contribs.eclipse.jface.dialog.DialogSettingsUtils;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.core.profil.IProfileListener;
import org.kalypso.model.wspm.core.result.IStationResult;
import org.kalypso.model.wspm.ui.KalypsoModelWspmUIPlugin;

/**
 * @author belger
 * @author kimwerner
 */
public abstract class AbstractProfilView implements IProfileListener, IProfilView
{
  private final IProfile m_profile;

  private Control m_control;

  private final IStationResult[] m_results;

  public AbstractProfilView( final IProfile profile )
  {
    this( profile, null );
  }

  public AbstractProfilView( final IProfile profile, final IStationResult[] results )
  {
    m_profile = profile;
    m_results = results == null ? new IStationResult[0] : results;

    if( m_profile != null )
    {
      m_profile.addProfilListener( this );
    }
  }

  /**
   * PRovide dialog settings that can be used by this view to store it's state.
   */
  protected IDialogSettings getDialogSettings( )
  {
    return DialogSettingsUtils.getDialogSettings( KalypsoModelWspmUIPlugin.getDefault(), getClass().getName() );
  }

  /**
   * @see org.kalypso.model.wspm.ui.view.IProfilView#createControl(org.eclipse.swt.widgets.Composite,
   *      org.eclipse.ui.forms.widgets.FormToolkit)
   */
  @Override
  public final Control createControl( final Composite parent, final FormToolkit toolkit )
  {
    final Control control = doCreateControl( parent, toolkit );
    m_control = control;

    m_control.addDisposeListener( new DisposeListener()
    {
      @Override
      public void widgetDisposed( final DisposeEvent e )
      {
        AbstractProfilView.this.dispose();
        control.removeDisposeListener( this );
      }
    } );

    return m_control;
  }

  protected abstract Control doCreateControl( Composite parent, FormToolkit toolkit );

  /**
   * @see org.kalypso.model.wspm.ui.profil.view.IProfilView#dispose()
   */
  @Override
  public void dispose( )
  {
    if( m_profile != null )
    {
      m_profile.removeProfilListener( this );
    }
  }

  @Override
  public final Control getControl( )
  {
    return m_control;
  }

  /**
   * @see org.kalypso.model.wspm.ui.profil.view.IProfilView#getProfil()
   */
  public final IProfile getProfile( )
  {
    return m_profile;
  }

  public IStationResult[] getResults( )
  {
    return m_results;
  }

  /**
   * @see org.kalypso.model.wspm.core.profil.IProfilListener#onProblemMarkerChanged(org.kalypso.model.wspm.core.profil.IProfil)
   */
  @Override
  public void onProblemMarkerChanged( final IProfile source )
  {
    // instances must overwrite this method
  }
}