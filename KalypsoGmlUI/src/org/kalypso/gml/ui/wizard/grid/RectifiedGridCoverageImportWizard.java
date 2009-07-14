/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.gml.ui.wizard.grid;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.kalypso.contribs.eclipse.core.runtime.PluginUtilities;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.gml.ui.KalypsoGmlUIPlugin;
import org.kalypso.grid.IGridMetaReader;
import org.kalypsodeegree_impl.gml.binding.commons.RectifiedGridDomain;
import org.kalypsodeegree_impl.gml.binding.commons.RectifiedGridDomain.OffsetVector;

/**
 * @author Gernot Belger
 */
public class RectifiedGridCoverageImportWizard extends Wizard implements IImportWizard
{
  private IStructuredSelection m_selection;

  private WizardNewFileCreationPage m_fileCreationPage;

  private PageSelectGeodataFile m_pageSelect;

  /**
   * @param gridFolder
   *          The new grid gets imported into this folder. If <code>null</code>, the user will be asked for the folder.
   */
  public RectifiedGridCoverageImportWizard( )
  {
    final IDialogSettings settings = PluginUtilities.getDialogSettings( KalypsoGmlUIPlugin.getDefault(), "ImportRectifiedGridCoverageWizardSettings" );

    setDialogSettings( settings );
    setNeedsProgressMonitor( true );

    setWindowTitle( "Import" );
  }

  /**
   * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
   *      org.eclipse.jface.viewers.IStructuredSelection)
   */
  public void init( final IWorkbench workbench, final IStructuredSelection selection )
  {
    m_selection = selection;
  }

  /**
   * @see org.eclipse.jface.wizard.Wizard#addPages()
   */
  @Override
  public void addPages( )
  {
    /* Only ask user for gml-file if no coverage collection is set */
    m_fileCreationPage = new WizardNewFileCreationPage( "newFile", m_selection );
    m_fileCreationPage.setTitle( "Neue GML-Datei" );
    m_fileCreationPage.setDescription( "W�hlen Sie Speicherort und Namen der neuen Datei aus. Die GML-Datei indiziert das importierte Raster. F�r eine Kachelung k�nnen weitere Raster in die gleiche Datei importiert werden." );

    m_pageSelect = new PageSelectGeodataFile( m_fileCreationPage );
    m_pageSelect.setTitle( "Rasterdatei" );
    m_pageSelect.setDescription( "W�hlen Sie die Rasterdatei aus. Die Rasterdatei wird in den Arbeitsbereich neben die GML Datei (n�chste Seite) kopiert." );

    addPage( m_pageSelect );
    addPage( m_fileCreationPage );

    super.addPages();
  }

  /**
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish( )
  {
    // read/copy input
    final File selectedFile = m_pageSelect.getSelectedFile();
    final String crs = m_pageSelect.getProjection();
    final OffsetVector offsetX = m_pageSelect.getOffsetX();
    final OffsetVector offsetY = m_pageSelect.getOffsetY();
    final Double[] ulc = m_pageSelect.getUpperLeftCorner();

    final IPath containerName = m_fileCreationPage.getContainerFullPath();
    final String gmlFileName = m_fileCreationPage.getFileName();
    try
    {
      final IGridMetaReader reader = m_pageSelect.getReader();
      final RectifiedGridDomain coverage = reader.getCoverage( offsetX, offsetY, ulc, crs );

      final IFile gmlFile = ResourcesPlugin.getWorkspace().getRoot().getFolder( containerName ).getFile( gmlFileName );

      // TODO: maybe also ask user for the location of the grid file
      final IContainer gridFolder = gmlFile.getParent();

      final RectifiedGridCoverageImportFinishWorker op = new RectifiedGridCoverageImportFinishWorker( selectedFile, null, coverage, gmlFile, gridFolder, null );

      final IStatus status = RunnableContextHelper.execute( getContainer(), true, true, op );

      ErrorDialog.openError( getShell(), getWindowTitle(), "Fehler beim Import einer Datei", status );
      return status.isOK();
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }

    return false;
  }
}
