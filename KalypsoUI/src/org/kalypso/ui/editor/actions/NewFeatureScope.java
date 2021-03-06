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
package org.kalypso.ui.editor.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.ui.catalogs.FeatureTypePropertiesCatalog;
import org.kalypso.ui.catalogs.IFeatureTypePropertiesConstants;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Gernot Belger
 */
public class NewFeatureScope implements INewScope
{
  private final Collection<NewFeaturePropertyScope> m_scopes = new ArrayList<>();

  public NewFeatureScope( final CommandableWorkspace workspace, final Feature feature, final IFeatureSelectionManager selectionManager )
  {
    buildSubScopes( workspace, feature, selectionManager );
  }

  private void buildSubScopes( final CommandableWorkspace workspace, final Feature feature, final IFeatureSelectionManager selectionManager )
  {
    if( showOwnScopeMenu( feature ) )
    {
      // Create sister elements
      final Feature parentFeature = feature.getOwner();
      final IRelationType targetRelation = feature.getParentRelation();
      addScope( parentFeature, targetRelation, workspace, selectionManager );
    }

    // FIXME: it would be nice in many cases to also have the new-items for sub-features in the menu
    // But we need to be able to tweak which ones are visible. Also we need to
    // hide sub-features that make no sense.
    // TODO: implement correctly
    if( showSubScopes( feature ) )
    {
      final IPropertyType[] properties = feature.getFeatureType().getProperties();
      for( final IPropertyType pt : properties )
      {
        if( pt instanceof IRelationType )
        {
          final IRelationType rt = (IRelationType)pt;
          addScope( feature, rt, workspace, selectionManager );
        }
      }
    }
  }

  private void addScope( final Feature parentFeature, final IRelationType targetRelation, final CommandableWorkspace workspace, final IFeatureSelectionManager selectionManager )
  {
    final NewFeaturePropertyScope scope = new NewFeaturePropertyScope( parentFeature, targetRelation, workspace, selectionManager );
    m_scopes.add( scope );
  }

  private boolean showSubScopes( final Feature feature )
  {
    return FeatureTypePropertiesCatalog.isPropertyOn( feature, IFeatureTypePropertiesConstants.GMLTREE_NEW_MENU_SHOW_SUB_FEATURES );
  }

  private static boolean showOwnScopeMenu( final Feature feature )
  {
    return FeatureTypePropertiesCatalog.isPropertyOn( feature, IFeatureTypePropertiesConstants.GMLTREE_NEW_MENU_ON_FEATURE );
  }

  @Override
  public IMenuManager createMenu( )
  {
    final IMenuManager newMenuManager = new MenuManager( Messages.getString( "org.kalypso.ui.editor.actions.FeatureActionUtilities.7" ) ); //$NON-NLS-1$
    addMenuItems( newMenuManager );
    return newMenuManager;
  }

  @Override
  public void addMenuItems( final IMenuManager menuManager )
  {
    for( final NewFeaturePropertyScope scope : m_scopes )
      scope.addMenuItemWithoutAddition( menuManager );
  }

  @Override
  public IAction[] createActions( )
  {
    throw new UnsupportedOperationException();
  }
}