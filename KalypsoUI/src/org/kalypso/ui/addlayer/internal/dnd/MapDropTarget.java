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
package org.kalypso.ui.addlayer.internal.dnd;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.kalypso.ui.addlayer.dnd.IMapDropTarget;

/**
 * @author Gernot Belger
 */
public class MapDropTarget implements IMapDropTarget
{
  private static final String PROPERTY_EXTENSION = "extension"; //$NON-NLS-1$

  private static final String PROPERTY_WIZARD_ID = "wizardId"; //$NON-NLS-1$

  private final IConfigurationElement m_element;

  public MapDropTarget( final IConfigurationElement element )
  {
    m_element = element;
  }

  @Override
  public boolean supportsExtension( final String extension )
  {
    final Set<String> supportedExtension = getExtensions();
    return supportedExtension.contains( extension );
  }

  private Set<String> getExtensions( )
  {
    final String supportedExtension = m_element.getAttribute( PROPERTY_EXTENSION );
    final String[] extensions = StringUtils.split( supportedExtension, ',' );

    return new HashSet<>( Arrays.asList( extensions ) );
  }

  @Override
  public String getWizardId( )
  {
    return m_element.getAttribute( PROPERTY_WIZARD_ID );
  }
}