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
package org.kalypso.ui.wizard.others;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.kalypso.ogc.gml.IKalypsoLayerModell;
import org.kalypso.util.themes.legend.controls.LegendComposite;

/**
 * A wizard page for entering properties of for a legend theme.
 * 
 * @author Holger Albert
 */
public class LegendThemeWizardPage extends WizardPage
{
  /**
   * The map modell.
   */
  private IKalypsoLayerModell m_mapModell;

  /**
   * The selected properties.
   */
  private Map<String, String> m_properties;

  /**
   * The constructor.
   * 
   * @param pageName
   *          The name of the page.
   * @param mapModell
   *          The map modell.
   */
  public LegendThemeWizardPage( String pageName, IKalypsoLayerModell mapModell )
  {
    super( pageName );

    m_mapModell = mapModell;
    m_properties = new HashMap<String, String>();

    setTitle( "Legendeneigenschaften" );
    setDescription( "W�hlen Sie die Eigenschaften der Legende." );
  }

  /**
   * The constructor.
   * 
   * @param pageName
   *          The name of the page.
   * @param title
   *          The title for this wizard page, or null if none.
   * @param titleImage
   *          The image descriptor for the title of this wizard page, or null if none.
   * @param mapModell
   *          The map modell.
   */
  public LegendThemeWizardPage( String pageName, String title, ImageDescriptor titleImage, IKalypsoLayerModell mapModell )
  {
    super( pageName, title, titleImage );

    m_mapModell = mapModell;
    m_properties = new HashMap<String, String>();

    setDescription( "W�hlen Sie die Eigenschaften der Legende." );
  }

  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl( Composite parent )
  {
    /* Create the main composite. */
    Composite main = new Composite( parent, SWT.NONE );
    main.setLayout( new GridLayout( 1, false ) );

    /* Create the legend composite. */
    LegendComposite legendComposite = new LegendComposite( main, SWT.NONE, m_mapModell, null );
    legendComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

    // TODO

    /* Set the control to the page. */
    setControl( main );

    /* In the first time, the page cannot be completed. */
    setPageComplete( false );

    // TODO
    checkPageComplete();
  }

  /**
   * This function checks, if the page can be completed.
   */
  protected void checkPageComplete( )
  {
    /* The wizard page can be completed. */
    setMessage( null );
    setErrorMessage( null );
    setPageComplete( true );

    // TODO
  }

  public Map<String, String> getProperties( )
  {
    return m_properties;
  }
}