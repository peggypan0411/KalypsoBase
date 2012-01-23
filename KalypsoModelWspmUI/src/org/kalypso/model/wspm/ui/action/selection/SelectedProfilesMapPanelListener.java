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
package org.kalypso.model.wspm.ui.action.selection;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.core.gml.ProfileFeatureBinding;
import org.kalypso.ogc.gml.map.MapPanelSelection;
import org.kalypso.ogc.gml.selection.EasyFeatureWrapper;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Dirk Kuch
 */
public class SelectedProfilesMapPanelListener implements ISelectionChangedListener
{

  private final AbstractProfileSelectionWidget m_widget;

  public SelectedProfilesMapPanelListener( final AbstractProfileSelectionWidget widget )
  {
    m_widget = widget;
  }

  @Override
  public void selectionChanged( final SelectionChangedEvent event )
  {
    final ISelection selection = event.getSelection();
    final IProfileFeature[] profiles = doSelection( selection );

    m_widget.onSelectionChange( profiles );
  }

  public IProfileFeature[] doSelection( final ISelection selection )
  {
    if( selection instanceof MapPanelSelection )
    {
      final MapPanelSelection mapPanelSelection = (MapPanelSelection) selection;
      final EasyFeatureWrapper[] features = mapPanelSelection.getAllFeatures();

      return resovleProfiles( features );
    }

    return new IProfileFeature[] {};
  }

  private IProfileFeature[] resovleProfiles( final EasyFeatureWrapper[] features )
  {
    final Set<IProfileFeature> profiles = new LinkedHashSet<>();

    for( final EasyFeatureWrapper eft : features )
    {
      final Feature feature = eft.getFeature();

      if( feature instanceof ProfileFeatureBinding )
      {
        final ProfileFeatureBinding binding = (ProfileFeatureBinding) feature;
        profiles.add( binding );
      }
    }

    return profiles.toArray( new IProfileFeature[] {} );
  }

}
