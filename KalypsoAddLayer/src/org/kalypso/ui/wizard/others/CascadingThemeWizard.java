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

import org.kalypso.commons.command.ICommand;
import org.kalypso.ogc.gml.IKalypsoLayerModell;
import org.kalypso.ui.ImageProvider;
import org.kalypso.ui.action.AddCascadingThemeCommand;
import org.kalypso.ui.i18n.Messages;

public class CascadingThemeWizard extends AbstractOtherThemeWizard
{
  public CascadingThemeWizard( )
  {
    super( new ThemeNameWizardPage( "themeNamePage", Messages.getString( "org.kalypso.ui.wizard.others.CascadingThemeWizard.1" ), ImageProvider.IMAGE_KALYPSO_ICON_BIG, "" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  protected ICommand createCommand( final IKalypsoLayerModell mapModell, final String themeName )
  {
    return new AddCascadingThemeCommand( mapModell, themeName );
  }
}