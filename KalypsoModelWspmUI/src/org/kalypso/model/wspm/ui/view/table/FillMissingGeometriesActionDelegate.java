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
package org.kalypso.model.wspm.ui.view.table;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.kalypso.contribs.eclipse.jface.wizard.WizardDialog2;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.ui.profil.dialogs.reducepoints.IPointsProvider;
import org.kalypso.model.wspm.ui.profil.dialogs.reducepoints.SelectionPointsProvider;
import org.kalypso.model.wspm.ui.profil.dialogs.reducepoints.SimplePointsProvider;
import org.kalypso.model.wspm.ui.profil.wizard.interpolate.geomtry.FillMissingProfileGeometriesWizard;

/**
 * @author Dirk Kuch
 */
public class FillMissingGeometriesActionDelegate implements IViewActionDelegate
{
  private IViewPart m_view;

  private ISelection m_selection;

  @Override
  public void init( final IViewPart view )
  {
    m_view = view;
  }

  @Override
  public void run( final IAction action )
  {
    final Shell viewShell = m_view.getViewSite().getShell();

    final ISelectionProvider selectionProvider = m_view.getViewSite().getSelectionProvider();
    if( selectionProvider != null )
    {
      m_selection = selectionProvider.getSelection();
    }

    // get profile points from selection and view
    // TODO: this does not work any more
    // final IProfilEventManager pem = (IProfilEventManager) m_view.getAdapter( IProfilEventManager.class );
    final IProfile profile = m_view instanceof TableView ? ((TableView) m_view).getProfil() : null;

    if( profile == null )
    {
      // should never happen
      MessageDialog.openError( viewShell, "Fill missing geomtries", "No profile is selected." ); //$NON-NLS-1$ //$NON-NLS-2$
      return;
    }

    final IPointsProvider allPointsPointsProvider = new SimplePointsProvider( "From all points of the profile", profile.getPoints() ); //$NON-NLS-1$
    final IPointsProvider selectionPointProvider = new SelectionPointsProvider( profile, m_selection );

    final FillMissingProfileGeometriesWizard wizard = new FillMissingProfileGeometriesWizard( new IPointsProvider[] { allPointsPointsProvider, selectionPointProvider } );
    final WizardDialog2 dialog = new WizardDialog2( viewShell, wizard );
    dialog.open();
  }

  @Override
  public void selectionChanged( final IAction action, final ISelection selection )
  {
    m_selection = selection;
  }

}
