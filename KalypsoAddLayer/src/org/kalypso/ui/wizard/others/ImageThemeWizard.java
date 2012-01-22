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
package org.kalypso.ui.wizard.others;

import java.util.Map;

import org.kalypso.commons.command.ICommand;
import org.kalypso.ogc.gml.IKalypsoLayerModell;
import org.kalypso.ui.ImageProvider;
import org.kalypso.ui.action.AddThemeCommand;

/**
 * The wizard for a image theme.
 *
 * @author Holger Albert
 */
public class ImageThemeWizard extends AbstractOtherThemeWizard
{
  /**
   * The wizard page for entering properties for a image theme.
   */
  private ImageThemeWizardPage m_imageThemeWizardPage;

  /**
   * The constructor.
   */
  public ImageThemeWizard( )
  {
    super( new ThemeNameWizardPage( "themeNamePage", "Bild", ImageProvider.IMAGE_KALYPSO_ICON_BIG, "Bild" ) );

    m_imageThemeWizardPage = null;
  }

  /**
   * @see org.kalypso.ui.wizard.others.AbstractOtherThemeWizard#addPages()
   */
  @Override
  public void addPages( )
  {
    /* Add the pages of the parent. */
    super.addPages();

    /* Add the image theme wizard page. */
    m_imageThemeWizardPage = new ImageThemeWizardPage( "ImageThemeWizardPage" );
    addPage( m_imageThemeWizardPage );
  }

  /**
   * @see org.kalypso.ui.wizard.others.AbstractOtherThemeWizard#createCommand(org.kalypso.ogc.gml.IKalypsoLayerModell,
   *      java.lang.String)
   */
  @Override
  protected ICommand createCommand( final IKalypsoLayerModell mapModell, final String themeName )
  {
    /* Create the add theme command. */
    final AddThemeCommand command = new AddThemeCommand( mapModell, themeName, "image", "", "" );

    /* Add the selected properties. */
    final Map<String, String> properties = m_imageThemeWizardPage.getProperties();
    if( properties != null && properties.size() > 0 )
      command.addProperties( properties );

    return command;
  }
}