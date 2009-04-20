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
package org.kalypso.ogc.gml.outline;

import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.ui.internal.util.Util;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.kalypso.ogc.gml.ICheckStateProvider;

/**
 * The this label provider modifies some labels for handling themes, that have only one style.
 *
 * @author Gernot Belger
 */
public class GisMapOutlineLabelProvider extends WorkbenchLabelProvider
{
  public void elementsChanged( final Object... elements )
  {
    super.fireLabelProviderChanged( new LabelProviderChangedEvent( this, elements ) );
  }

  /**
   * @see org.eclipse.ui.model.WorkbenchLabelProvider#decorateText(java.lang.String, java.lang.Object)
   */
  @Override
  protected String decorateText( final String input, final Object element )
  {
    return input;
  }

  public boolean isChecked( final Object element )
  {
    final ICheckStateProvider provider = getCheckStateProvider( element );
    if( provider == null )
      return false;

    return provider.isChecked();
  }

  public boolean isGrayed( final Object element )
  {
    final ICheckStateProvider provider = getCheckStateProvider( element );
    if( provider == null )
      return true;

    return provider.isGrayed();
  }

  @SuppressWarnings("restriction")
  private ICheckStateProvider getCheckStateProvider( final Object element )
  {
    return (ICheckStateProvider) Util.getAdapter( element, ICheckStateProvider.class );
  }

}