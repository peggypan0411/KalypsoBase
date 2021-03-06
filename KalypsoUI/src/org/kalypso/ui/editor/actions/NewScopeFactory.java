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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.gml.selection.FeatureSelectionHelper;
import org.kalypso.ogc.gml.selection.IFeatureSelection;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.ui.editor.gmleditor.part.FeatureAssociationTypeElement;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.IFeatureRelation;

/**
 * @author Gernot Belger
 */
public final class NewScopeFactory
{
  private NewScopeFactory( )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Bit HACKY: used for the GML-Tree to create the correct New-Scope.
   */
  public static INewScope createFromTreeSelection( final CommandableWorkspace workspace, final IStructuredSelection selection, final IFeatureSelectionManager selectionManager )
  {
    final Object elementInScope = selection.getFirstElement();

    if( elementInScope instanceof FeatureAssociationTypeElement )
    {

      return new NewFeaturePropertyScope( (FeatureAssociationTypeElement)elementInScope, workspace, selectionManager );
    }

    if( selection instanceof IFeatureSelection )
    {
      final Feature feature = FeatureSelectionHelper.getFirstFeature( selectionManager );
      if( feature == null )
        return null;

      return new NewFeatureScope( workspace, feature, selectionManager );
    }

    return null;
  }

  public static INewScope createPropertyScope( final IFeatureRelation featureProperty, final CommandableWorkspace workspace, final IFeatureSelectionManager selectionManager )
  {
    return new NewFeaturePropertyScope( featureProperty, workspace, selectionManager );
  }
}
