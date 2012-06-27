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
package org.kalypso.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.kalypso.deegree.i18n.Messages;
import org.kalypso.transformation.ui.AvailableCRSPanel;
import org.kalypso.transformation.ui.CRSSelectionPanel;
import org.kalypso.transformation.ui.listener.IAvailableCRSPanelListener;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The preference page for deegree things, like the coordinate system.
 *
 * @author Holger Albert
 */
public class KalypsoDeegreePreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
  /**
   * This variable stores the GUI element for the coordinate system.
   */
  protected CRSSelectionPanel m_crsPanel;

  /**
   * This variable stores the coordinate system.
   */
  protected String m_coordinateSystem;

  /**
   * This variable stores the GUI element for the available coordinate systems.
   */
  protected AvailableCRSPanel m_availableCRSPanel;

  /**
   * This variable stores the available coordinate systems.
   */
  protected String m_availableCoordinateSystems;

  /**
   * The constructor.
   */
  public KalypsoDeegreePreferencePage( )
  {
    /* Initialize everything. */
    init();
  }

  public KalypsoDeegreePreferencePage( final String title )
  {
    super( title );

    /* Initialize everything. */
    init();
  }

  public KalypsoDeegreePreferencePage( final String title, final ImageDescriptor image )
  {
    super( title, image );

    /* Initialize everything. */
    init();
  }

  /**
   * This function initializes everything.
   */
  private void init( )
  {
    m_crsPanel = null;
    m_coordinateSystem = null;

    m_availableCRSPanel = null;
    m_availableCoordinateSystems = null;

    /* Set the description. */
    setDescription( Messages.getString( "org.kalypso.preferences.KalypsoDeegreePreferencePage.0" ) ); //$NON-NLS-1$
  }

  /**
   * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createContents( final Composite parent )
  {
    /* Create the main container. */
    final Composite main = new Composite( parent, SWT.NONE );
    main.setLayout( new GridLayout( 1, false ) );

    /* Create the panel for the coordinate system. */
    m_crsPanel = new CRSSelectionPanel( main, SWT.NONE, Messages.getString( "org.kalypso.preferences.KalypsoDeegreePreferencePage.1" ) ); //$NON-NLS-1$
    m_crsPanel.setToolTipText( Messages.getString( "org.kalypso.preferences.KalypsoDeegreePreferencePage.2" ) ); //$NON-NLS-1$
    m_crsPanel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );

    /* Add a listener. */
    m_crsPanel.addSelectionChangedListener( new ISelectionChangedListener()
    {
      /**
       * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
       */
      @Override
      public void selectionChanged( final SelectionChangedEvent event )
      {
        /* Store the coordinate system. */
        m_coordinateSystem = m_crsPanel.getSelectedCRS();

        /* Validate the page. */
        validatePage();
      }
    } );

    /* Set the old value. */
    m_crsPanel.setSelectedCRS( Platform.getPreferencesService().getString( KalypsoDeegreePlugin.getID(), IKalypsoDeegreePreferences.DEFAULT_CRS_SETTING, IKalypsoDeegreePreferences.DEFAULT_CRS_VALUE, null ) );

    /* Create the panel for the available coordinate systems. */
    m_availableCRSPanel = new AvailableCRSPanel( main, SWT.NONE );
    m_availableCRSPanel.setToolTipText( Messages.getString( "org.kalypso.preferences.KalypsoDeegreePreferencePage.3" ) ); //$NON-NLS-1$
    m_availableCRSPanel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );

    /* Add a listener. */
    m_availableCRSPanel.addAvailableCRSPanelListener( new IAvailableCRSPanelListener()
    {
      /**
       * @see org.kalypso.transformation.ui.IAvailableCRSPanelListener#coordinateSystemsInitialized(java.lang.String[])
       */
      @Override
      public void coordinateSystemsInitialized( final String[] codes )
      {
        /* Store the available coordinate systems. */
        storeCoordinateSystems();
      }

      /**
       * @see org.kalypso.transformation.ui.IAvailableCRSPanelListener#coordinateSystemAdded(java.lang.String)
       */
      @Override
      public void coordinateSystemAdded( final String code )
      {
        /* Store the available coordinate systems. */
        storeCoordinateSystems();
      }

      /**
       * @see org.kalypso.transformation.ui.IAvailableCRSPanelListener#coordinateSystemRemoved(java.lang.String)
       */
      @Override
      public void coordinateSystemRemoved( final String code )
      {
        /* Store the available coordinate systems. */
        storeCoordinateSystems();
      }

      /**
       * This function stores the available coordinate systems.
       */
      private void storeCoordinateSystems( )
      {
        /* Store the available coordinate systems. */
        m_availableCoordinateSystems = m_availableCRSPanel.getAvailableCoordinateSystems();

        String[] codes = null;
        if( m_availableCoordinateSystems != null && m_availableCoordinateSystems.length() > 0 )
          codes = m_availableCoordinateSystems.split( ";" ); //$NON-NLS-1$

        /* Update the combo box of the crs selection panel. */
        m_crsPanel.updateCoordinateSystemsCombo( codes );

        /* Validate the page. */
        validatePage();
      }
    } );

    /* Set the old value. */
    m_availableCRSPanel.setAvailableCoordinateSystems( Platform.getPreferencesService().getString( KalypsoDeegreePlugin.getID(), IKalypsoDeegreePreferences.AVAILABLE_CRS_SETTING, IKalypsoDeegreePreferences.AVAILABLE_CRS_VALUE, null ) );

    /* Validate the page. */
    validatePage();

    return main;
  }

  /**
   * This function validates the page.
   */
  protected void validatePage( )
  {
    /* Reset the error message. */
    setErrorMessage( null );

    if( m_availableCoordinateSystems == null || m_availableCoordinateSystems.length() == 0 )
    {
      setErrorMessage( Messages.getString( "org.kalypso.preferences.KalypsoDeegreePreferencePage.5" ) ); //$NON-NLS-1$
      return;
    }

    if( m_coordinateSystem == null || m_coordinateSystem.length() == 0 )
    {
      setErrorMessage( Messages.getString( "org.kalypso.preferences.KalypsoDeegreePreferencePage.6" ) ); //$NON-NLS-1$
      return;
    }
  }

  @Override
  public boolean performOk( )
  {
    /* Get the preferences. */
    final IScopeContext instanceScope = InstanceScope.INSTANCE;
    final IEclipsePreferences instanceNode = instanceScope.getNode( KalypsoDeegreePlugin.getID() );

    /* Set the new values. */
    instanceNode.put( IKalypsoDeegreePreferences.DEFAULT_CRS_SETTING, m_coordinateSystem );
    instanceNode.put( IKalypsoDeegreePreferences.AVAILABLE_CRS_SETTING, m_availableCoordinateSystems );

    try
    {
      /* Save the preferences. */
      instanceNode.flush();
    }
    catch( final BackingStoreException ex )
    {
      ex.printStackTrace();
    }

    return super.performOk();
  }

  @Override
  protected void performDefaults( )
  {
    /* Get the default preferences. */
    final IScopeContext defaultScope = DefaultScope.INSTANCE;
    final IEclipsePreferences defaultNode = defaultScope.getNode( KalypsoDeegreePlugin.getID() );

    /* Must be set before, because the coordinate system in the combo will be checked against this ones. */
    if( m_availableCRSPanel != null && !m_availableCRSPanel.isDisposed() )
      m_availableCRSPanel.setAvailableCoordinateSystems( defaultNode.get( IKalypsoDeegreePreferences.AVAILABLE_CRS_SETTING, IKalypsoDeegreePreferences.AVAILABLE_CRS_VALUE ) );

    if( m_crsPanel != null && !m_crsPanel.isDisposed() )
      m_crsPanel.setSelectedCRS( defaultNode.get( IKalypsoDeegreePreferences.DEFAULT_CRS_SETTING, IKalypsoDeegreePreferences.DEFAULT_CRS_VALUE ) );

    super.performDefaults();
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  @Override
  public void init( final IWorkbench workbench )
  {
  }
}