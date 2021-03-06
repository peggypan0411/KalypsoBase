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

import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.ogc.gml.command.DeleteFeatureCommand;
import org.kalypso.ogc.gml.selection.EasyFeatureWrapper;
import org.kalypso.ui.editor.gmleditor.part.GMLLabelProvider;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureVisitor;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;
import org.kalypsodeegree_impl.model.feature.visitors.FindLinkedFeatureVisitor;

/**
 * Utilities for handling feature lists in the table feature control.
 * 
 * @author Holger Albert
 */
public final class TableFeatureControlUtils
{
  private TableFeatureControlUtils( )
  {
    throw new UnsupportedOperationException();
  }

  /**
   * This function will build the command for deleting features from the list.
   * 
   * @param allFeatures
   *          The features, that should be deleted.
   * @param shell
   *          The shell.
   */
  public static DeleteFeatureCommand deleteFeaturesFromSelection( final EasyFeatureWrapper[] allFeatures, final Shell shell )
  {
    if( allFeatures.length > 0 )
    {
      final String[] gmlIds = new String[allFeatures.length];
      for( int i = 0; i < gmlIds.length; i++ )
        gmlIds[i] = allFeatures[i].getFeature().getId();

      /* Work with the first workspace, normally all features in this context should live in the same workspace. */
      /* Furthermore, it is not relevant, in which workspace the command is processed. */
      final GMLWorkspace workspace = allFeatures[0].getWorkspace();

      /* Find features with links to the removed features and display a warning message. */
      final FindLinkedFeatureVisitor visitor = new FindLinkedFeatureVisitor( gmlIds );
      workspace.accept( visitor, workspace.getRootFeature(), FeatureVisitor.DEPTH_INFINITE );
      final Map<Feature, Set<IRelationType>> linkedFeatures = visitor.getLinkedFeatures();

      if( !askForDeletion( shell, allFeatures, linkedFeatures ) )
        return null;

      return new DeleteFeatureCommand( allFeatures );
    }

    return null;
  }

  private static boolean askForDeletion( final Shell shell, final EasyFeatureWrapper[] allFeatures, final Map<Feature, Set<IRelationType>> linkedFeatures )
  {
    if( linkedFeatures.size() == 0 )
      return askForDeletionNoReferences( shell, allFeatures );

    return askForDeletionWithReferences( shell, allFeatures, linkedFeatures );
  }

  private static boolean askForDeletionNoReferences( final Shell shell, final EasyFeatureWrapper[] allFeatures )
  {
    final String message = getAskNoReferencesMessage( allFeatures );
    return MessageDialog.openConfirm( shell, Messages.getString( "org.kalypso.ui.editor.actions.TableFeatureControlUtils.2" ), message ); //$NON-NLS-1$
  }

  private static boolean askForDeletionWithReferences( final Shell shell, final EasyFeatureWrapper[] allFeatures, final Map<Feature, Set<IRelationType>> linkedFeatures )
  {
    final String msg = getAskWithReferencesMessage( allFeatures );

    final MessageDialog dialog = new MessageDialog( shell, Messages.getString( "org.kalypso.ui.editor.actions.TableFeatureControlUtils.2" ), null, msg, MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0 ) //$NON-NLS-1$
    {
      @Override
      protected Control createCustomArea( final Composite dialogParent )
      {
        final TableViewer viewer = new TableViewer( dialogParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.HIDE_SELECTION );
        viewer.setContentProvider( new ArrayContentProvider() );
        viewer.setLabelProvider( new GMLLabelProvider()
        {
          @Override
          public String getText( final Object element )
          {
            if( element instanceof Feature )
              return getFeatureDeleteLabel( (Feature)element );

            return super.getText( element );
          }
        } );
        viewer.setInput( linkedFeatures.keySet() );

        viewer.getTable();

        final Control control = viewer.getControl();
        final GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        gridData.minimumHeight = 200;
        gridData.widthHint = 200;
        gridData.heightHint = 200;
        control.setLayoutData( gridData );

        return control;
      }
    };

    final int open = dialog.open();
    // REMARK: this is not the same as != Window.CANCEL
    return open == Window.OK;
  }

  private static String getAskWithReferencesMessage( final EasyFeatureWrapper[] allFeatures )
  {
    if( allFeatures.length == 1 )
      return Messages.getString( "org.kalypso.ui.editor.actions.TableFeatureControlUtils.0" ); //$NON-NLS-1$
    else
      return Messages.getString( "org.kalypso.ui.editor.actions.TableFeatureControlUtils.1" ); //$NON-NLS-1$
  }

  private static String getAskNoReferencesMessage( final EasyFeatureWrapper[] allFeatures )
  {
    if( allFeatures.length == 1 )
    {
      final String label = getFeatureDeleteLabel( allFeatures[0].getFeature() );
      return String.format( Messages.getString( "TableFeatureControlUtils.1" ), label ); //$NON-NLS-1$
    }
    else
      return String.format( Messages.getString( "TableFeatureControlUtils.2" ), allFeatures.length ); //$NON-NLS-1$
  }

  protected static String getFeatureDeleteLabel( final Feature feature )
  {
    final String name = FeatureHelper.getAnnotationValue( feature, IAnnotation.ANNO_NAME );
    final String label = FeatureHelper.getAnnotationValue( feature, IAnnotation.ANNO_LABEL );
    return String.format( "%s: <%s>", name, label ); //$NON-NLS-1$ //$NON-NLS-2$
  }
}