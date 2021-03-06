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
package org.kalypso.ui.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * @author Gernot
 */
public class NewGMLFileWizard extends Wizard implements INewWizard
{
  public static final String WIZARD_ID = "org.kalypso.ui.wizard.NewGMLFileWizard"; //$NON-NLS-1$

  private GMLSchemaSelectionPage m_schemaSelectionPage;

  private FeatureTypeSelectionPage m_featureTypeSelectionPage;

  private IStructuredSelection m_selection;

  private WizardNewFileCreationPage m_fileCreationPage;

  private IWorkbench m_workbench;

  public NewGMLFileWizard( )
  {
    setNeedsProgressMonitor( true );
    setHelpAvailable( false );

    setWindowTitle( Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.0" ) ); //$NON-NLS-1$
  }

  /**
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish( )
  {
    final IPath containerName = m_fileCreationPage.getContainerFullPath();
    final String fileName = m_fileCreationPage.getFileName();
    final IFeatureType featureType = m_featureTypeSelectionPage.getSelectedFeatureType();
    final IWorkbenchPage page = m_workbench.getActiveWorkbenchWindow().getActivePage();

    final ICoreRunnableWithProgress op = new ICoreRunnableWithProgress()
    {
      @Override
      public IStatus execute( final IProgressMonitor monitor ) throws CoreException
      {
        monitor.beginTask( Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.1" ) + fileName, 3 ); //$NON-NLS-1$

        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final IResource resource = root.findMember( containerName );
        if( !resource.exists() || !(resource instanceof IContainer) )
          return new Status( IStatus.ERROR, KalypsoGisPlugin.getId(), Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.2", containerName ) ); //$NON-NLS-1$ 

        final IContainer container = (IContainer)resource;
        final IFile file = container.getFile( new Path( fileName ) );

        GmlSerializer.createGmlFile( featureType, file, new SubProgressMonitor( monitor, 2 ), null );

        monitor.subTask( Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.4" ) ); //$NON-NLS-1$
        getShell().getDisplay().asyncExec( new Runnable()
        {
          @Override
          public void run( )
          {
            try
            {
              IDE.openEditor( page, file, true );
            }
            catch( final PartInitException e )
            {
              ErrorDialog.openError( getShell(), Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.5" ), Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.6" ), e.getStatus() ); //$NON-NLS-1$ //$NON-NLS-2$
            }
          }
        } );
        monitor.worked( 1 );

        return Status.OK_STATUS;
      }
    };

    final IStatus status = RunnableContextHelper.execute( getContainer(), false, false, op );
    ErrorDialog.openError( getShell(), Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.7" ), Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.8" ), status ); //$NON-NLS-1$ //$NON-NLS-2$
    return status.isOK();
  }

  /**
   * @see org.eclipse.jface.wizard.Wizard#addPages()
   */
  @Override
  public void addPages( )
  {
    m_schemaSelectionPage = new GMLSchemaSelectionPage();
    m_featureTypeSelectionPage = new FeatureTypeSelectionPage();
    m_fileCreationPage = new WizardNewFileCreationPage( "newFile", m_selection ); //$NON-NLS-1$
    m_fileCreationPage.setTitle( Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.10" ) ); //$NON-NLS-1$
    m_fileCreationPage.setDescription( Messages.getString( "org.kalypso.ui.wizard.NewGMLFileWizard.11" ) ); //$NON-NLS-1$
    m_fileCreationPage.setFileName( "neu.gml" ); //$NON-NLS-1$

    addPage( m_schemaSelectionPage );
    addPage( m_featureTypeSelectionPage );
    addPage( m_fileCreationPage );
  }

  /**
   * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPageControls( final Composite pageContainer )
  {
    super.createPageControls( pageContainer );

    final GMLSchemaSelectionPage schemaSelectionPage = m_schemaSelectionPage;
    final FeatureTypeSelectionPage featureTypeSelectionPage = m_featureTypeSelectionPage;
    schemaSelectionPage.getSelectionProvider().addSelectionChangedListener( new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged( final SelectionChangedEvent event )
      {
        final String namespace = schemaSelectionPage.getNamespace();
        featureTypeSelectionPage.setNamespace( namespace );
      }
    } );
  }

  /**
   * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
   */
  @Override
  public void init( final IWorkbench workbench, final IStructuredSelection selection )
  {
    m_workbench = workbench;
    m_selection = selection;
  }

}
