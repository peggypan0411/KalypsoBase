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
package org.kalypso.gml.ui.internal.shape;

import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.kalypso.contribs.eclipse.jface.dialog.DialogSettingsUtils;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.core.status.StatusDialog;
import org.kalypso.gml.ui.KalypsoGmlUIPlugin;
import org.kalypso.gml.ui.i18n.Messages;
import org.kalypso.utils.shape.ShapeUtilities;

/**
 * TODO: make useable as 'add theme' wizard in wspm and workflow projects
 * TODO: - page that just entes a name; shape is then saved in shape import folder
 * TODO: add as new theme to map
 *
 * @author Gernot Belger
 */
public class ShapeFileNewWizard extends Wizard implements INewWizard
{
  public static final String WIZARD_ID = "org.kalypso.gml.ui.shape.ShapeFileNewWizard"; //$NON-NLS-1$

  private final ShapeFileNewData m_shapeFileNewData = new ShapeFileNewData();

  public ShapeFileNewWizard( )
  {
    setNeedsProgressMonitor( true );
    setHelpAvailable( false );

    setDialogSettings( DialogSettingsUtils.getDialogSettings( KalypsoGmlUIPlugin.getDefault(), WIZARD_ID ) );

    setWindowTitle( Messages.getString( "ShapeFileNewWizard_0" ) ); //$NON-NLS-1$
  }

  @Override
  public void init( final IWorkbench workbench, final IStructuredSelection selection )
  {
    m_shapeFileNewData.init( getDialogSettings() );

    final IContainer container = findSelection( selection );
    if( container != null )
    {
      final IFile shapeFile = container.getFile( new Path( Messages.getString( "ShapeFileNewWizard_1" ) ) ); //$NON-NLS-1$
      m_shapeFileNewData.setShapeFile( shapeFile );
    }
  }

  @Override
  public void addPages( )
  {
    addPage( new ShapeFileNewFilePage( "filepath", m_shapeFileNewData ) ); //$NON-NLS-1$
    addPage( new ShapeFileNewSignaturePage( "signature", m_shapeFileNewData ) ); //$NON-NLS-1$
  }

  private IContainer findSelection( final IStructuredSelection selection )
  {
    for( final Iterator< ? > iterator = selection.iterator(); iterator.hasNext(); )
    {
      final Object element = iterator.next();
      if( element instanceof IContainer )
        return (IContainer)element;

      if( element instanceof IResource )
        return ((IResource)element).getParent();
    }

    return null;
  }

  @Override
  public boolean performFinish( )
  {
    m_shapeFileNewData.storeSettings( getDialogSettings() );

    final IStatus result = doFinish();
    if( result.matches( IStatus.CANCEL ) )
      return false;

    StatusDialog.open( getShell(), result, getWindowTitle() );

    return !result.matches( IStatus.ERROR );
  }

  public IStatus doFinish( )
  {
    try
    {
      final IFile baseFile = m_shapeFileNewData.getShpFile();

      final Shell shell = getShell();
      final String title = getWindowTitle();

      if( !askForAndDeleteExistingFiles( shell, title, baseFile ) )
        return Status.CANCEL_STATUS;

      final ShapeFileNewOperation operation = new ShapeFileNewOperation( m_shapeFileNewData );
      return RunnableContextHelper.execute( getContainer(), true, false, operation );
    }
    catch( final CoreException e )
    {
      return e.getStatus();
    }
  }

  static boolean askForAndDeleteExistingFiles( final Shell shell, final String title, final IFile baseFile ) throws CoreException
  {
    final IFile[] shapeFileExists = ShapeUtilities.getExistingShapeFiles( baseFile );
    if( shapeFileExists.length > 0 )
    {
      final MessageDialog dialog = new MessageDialog( shell, title, null, Messages.getString( "ShapeFileNewWizard_3" ), MessageDialog.WARNING, new String[] { //$NON-NLS-1$
      IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0 );
      if( dialog.open() != IDialogConstants.OK_ID )
        return false;
    }

    deleteExitingShapeFiles( shapeFileExists );

    return true;
  }

  static void deleteExitingShapeFiles( final IFile[] shapeFileExists ) throws CoreException
  {
    // Delete via resource-api in order to keep history
    for( final IFile existingFile : shapeFileExists )
      existingFile.delete( false, true, null );
  }
}