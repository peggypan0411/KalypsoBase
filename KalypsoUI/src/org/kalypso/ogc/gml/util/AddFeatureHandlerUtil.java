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
package org.kalypso.ogc.gml.util;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.kalypso.contribs.eclipse.jface.dialog.DialogSettingsUtils;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.IGMLSchema;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.ogc.gml.filterdialog.model.FeatureTypeLabelProvider;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.IFeatureRelation;

/**
 * Common code for adding features.
 * 
 * @author Gernot Belger
 */
public final class AddFeatureHandlerUtil
{
  private AddFeatureHandlerUtil( )
  {
    throw new UnsupportedOperationException();
  }

  public static boolean checkPrecondition( final Shell shell, final IFeatureRelation targetProperty )
  {
    if( !checkMaxCount( targetProperty ) )
    {
      MessageDialog.openInformation( shell, Messages.getString( "org.kalypso.ogc.gml.featureview.control.TableFeatureContol.2" ), Messages.getString( "org.kalypso.ogc.gml.featureview.control.TableFeatureContol.3" ) ); //$NON-NLS-1$ //$NON-NLS-2$
      return false;
    }

    return true;
  }

  /**
   * This function checks, if more features can be added.
   * 
   * @return <code>true</code>, if so.
   */
  private static boolean checkMaxCount( final IFeatureRelation targetProperty )
  {
    /* Get the needed properties. */
    final Feature parentFeature = targetProperty.getOwner();
    final IRelationType parentRelation = targetProperty.getPropertyType();

    final int maxOccurs = parentRelation.getMaxOccurs();
    if( maxOccurs == IPropertyType.UNBOUND_OCCURENCY )
      return true;

    /* List shoul never exceed max occurs */
    if( parentRelation.isList() )
    {
      final List< ? > list = (List< ? >)parentFeature.getProperty( parentRelation );
      final int size = list.size();

      return size <= maxOccurs;
    }

    /* if not a list: Never add another feature if the reference is already set */
    if( parentFeature.getProperty( parentRelation ) != null )
      return false;

    return true;
  }

  public static IFeatureType chooseFeatureType( final Shell shell, final String dialogTitle, final IRelationType relationType, final GMLWorkspace workspace )
  {
    final IGMLSchema gmlSchema = workspace.getGMLSchema();

    final IFeatureType targetFeatureType = relationType.getTargetFeatureType();
    final IFeatureType[] substituts = GMLSchemaUtilities.getSubstituts( targetFeatureType, gmlSchema, false, true );

    if( substituts.length == 0 )
    {
      // May only happen if the type is abstract
      final String message = String.format( Messages.getString( "AddFeatureHandlerUtil.0" ), getNewLabel( relationType ) ); //$NON-NLS-1$
      MessageDialog.openWarning( shell, dialogTitle, message );
      return null;
    }

    if( substituts.length == 1 )
      return substituts[0];

    /* Let user choose */
    final String message = Messages.getString( "AddFeatureHandlerUtil.1" ); //$NON-NLS-1$

    final ILabelProvider labelProvider = new FeatureTypeLabelProvider( IAnnotation.ANNO_NAME );
// final TreeSingleSelectionDialog dialog = new TreeSingleSelectionDialog( shell, substituts, new
// ArrayTreeContentProvider(), labelProvider, message );

    final ElementListSelectionDialog dialog = new ElementListSelectionDialog( shell, labelProvider );

    dialog.setMessage( message );
    dialog.setEmptySelectionMessage( "" ); //$NON-NLS-1$
    dialog.setTitle( dialogTitle );

    dialog.setElements( substituts );
    dialog.setDialogBoundsSettings( DialogSettingsUtils.getDialogSettings( KalypsoGisPlugin.getDefault(), AddFeatureHandlerUtil.class.getName() ), Dialog.DIALOG_PERSISTLOCATION
        | Dialog.DIALOG_PERSISTLOCATION );
    dialog.setAllowDuplicates( false );
    dialog.setInitialSelections( new Object[] { substituts[0] } );
    dialog.setMultipleSelection( false );

    if( dialog.open() == Window.CANCEL )
      return null;

    return (IFeatureType)dialog.getResult()[0];
  }

  public static String getNewLabel( final IRelationType relationType )
  {
    return relationType.getTargetFeatureType().getAnnotation().getValue( IAnnotation.ANNO_NAME );
  }
}