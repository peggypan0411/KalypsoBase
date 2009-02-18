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
package org.kalypso.gml.ui.wizard.grid;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.kalypso.commons.resources.SetContentHelper;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.grid.ConvertAscii2Binary;
import org.kalypso.grid.GeoGridException;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.event.FeatureStructureChangeModellEvent;
import org.kalypsodeegree_impl.gml.binding.commons.CoverageCollection;
import org.kalypsodeegree_impl.gml.binding.commons.ICoverage;
import org.kalypsodeegree_impl.gml.binding.commons.ICoverageCollection;
import org.kalypsodeegree_impl.gml.binding.commons.RectifiedGridDomain;

/**
 * extracted from RectifiedGridCoverageImportWizard
 * 
 * @author kuch
 */
public class RectifiedGridCoverageImportFinishWorker implements ICoreRunnableWithProgress
{
  private final Shell m_shell;

  private final File m_inputGridFile;

  private final RectifiedGridDomain m_domain;

  private final ICoverageCollection m_coverages;

  private final IFile m_gmlFile;

  private final IContainer m_outputGridFolder;

  private ICoverage m_newCoverage;
  
  private final String m_sourceCRS;

  public RectifiedGridCoverageImportFinishWorker( final Shell shell, final File selectedFile, final ICoverageCollection coverages, final RectifiedGridDomain domain, final IFile gmlFile, final IContainer gridFolder, final String sourceCRS )
  {
    m_shell = shell;
    m_inputGridFile = selectedFile;
    m_coverages = coverages;
    m_domain = domain;
    m_gmlFile = gmlFile;
    m_outputGridFolder = gridFolder;
    m_sourceCRS = sourceCRS;
  }

  /**
   * @see org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress#execute(org.eclipse.core.runtime.IProgressMonitor)
   */
  public IStatus execute( final IProgressMonitor monitor ) throws InvocationTargetException, CoreException
  {
    final SubMonitor progress = SubMonitor.convert( monitor, "Rasterdaten Import", 100 );

    File convertedGridFile = null;
    try
    {
      monitor.subTask( "- importiere Daten in den Arbeitsbereich" );

      try
      {
        /* Convert .asc files to .bin files if necessary */
        convertedGridFile = convertToBinary( m_inputGridFile, progress.newChild( 70 ) );
      }
      catch( final IOException e )
      {
        e.printStackTrace();
        return StatusUtilities.statusFromThrowable( e, "Fehler beim ASC-BIN Konvertieren: " + e.getLocalizedMessage() );
      }

      final IStatus importStatus = importExternalFiles( progress.newChild( 10 ), convertedGridFile );
      if( !importStatus.isOK() )
        return importStatus;

      /* Fill workspace */
      final IContainer container = m_gmlFile == null ? null : m_gmlFile.getParent();

      final ICoverageCollection coverages = m_coverages == null ? new CoverageCollection( ResourceUtilities.createURL( container ), null ) : m_coverages;

      final IFile outputGridFile = m_outputGridFolder.getFile( new Path( convertedGridFile.getName() ) );
      m_newCoverage = createGmlWorkspace( outputGridFile, coverages, progress.newChild( 20 ) );

      return Status.OK_STATUS;
    }
    catch( final CoreException e )
    {
      throw e;
    }
    catch( final Throwable e )
    {
      e.printStackTrace();

      throw new InvocationTargetException( e );
    }
    finally
    {
      // HACK: if it was converted (now .bin) delete the temporary file
      if( convertedGridFile != null && convertedGridFile.getName().toLowerCase().endsWith( ".bin" ) )
        convertedGridFile.delete();

      monitor.done();
    }
  }

  private ICoverage createGmlWorkspace( final IFile outputGridFile, final ICoverageCollection coverages, final IProgressMonitor monitor ) throws CoreException, MalformedURLException
  {
    final SubMonitor progress = SubMonitor.convert( monitor, 1 + 10 );

    final String mimeType = "image/" + outputGridFile.getFileExtension();

    // TODO: make file path relative to gml path
    final URL outputGridUrl = ResourceUtilities.createURL( outputGridFile );
    final String fileName = outputGridUrl.toExternalForm();
    final ICoverage newCoverage = CoverageCollection.addCoverage( coverages, m_domain, fileName, mimeType );
    newCoverage.setName( m_inputGridFile.getName() );

    ProgressUtilities.worked( progress, 1 );

    final Feature coveragesFeature = coverages.getFeature();
    if( m_coverages == null ) // do not save if coverages already exists
    {
      final SetContentHelper contentHelper = new SetContentHelper()
      {
        @Override
        protected void write( final OutputStreamWriter writer ) throws Throwable
        {
          GmlSerializer.serializeWorkspace( writer, coveragesFeature.getWorkspace() );
        }
      };
      contentHelper.setFileContents( m_gmlFile, false, true, new SubProgressMonitor( monitor, 1 ) );
      ProgressUtilities.worked( progress, 10 );
    }

    /* Fire model event */
    final GMLWorkspace workspace = coveragesFeature.getWorkspace();
    workspace.fireModellEvent( new FeatureStructureChangeModellEvent( workspace, coveragesFeature, newCoverage.getFeature(), FeatureStructureChangeModellEvent.STRUCTURE_CHANGE_ADD ) );

    return newCoverage;
  }

  private IStatus importExternalFiles( final IProgressMonitor monitor, final File gridFile ) throws InvocationTargetException, InterruptedException
  {
    final Shell shell = m_shell;
    final IOverwriteQuery overwriteQuery = new IOverwriteQuery()
    {
      public String queryOverwrite( final String pathString )
      {
        if( MessageDialog.openQuestion( shell, "Datei import", "Die Datei " + pathString + " exisitert bereits.\nSoll Sie überschrieben werden?" ) )
          return IOverwriteQuery.YES;
        return IOverwriteQuery.NO;
      }
    };

    final List<File> fileList = new ArrayList<File>( 1 );
    fileList.add( gridFile );
    final ImportOperation operation = new ImportOperation( m_outputGridFolder.getFullPath(), FileSystemStructureProvider.INSTANCE, overwriteQuery, fileList );
    operation.setOverwriteResources( false );
    operation.setCreateContainerStructure( false );
    operation.setContext( m_shell );
    operation.run( monitor );

    return operation.getStatus();
  }

  /**
   * Special: converts any .asc to an internal binary format. Replaces these occurences in the list of files.
   * 
   * @param files2delete
   *            This list get filled with the newly generated temp files.
   */
  private File convertToBinary( final File gridFile, final IProgressMonitor monitor ) throws IOException
  {
    final SubMonitor progress = SubMonitor.convert( monitor, 1 );

    if( gridFile.getName().toLowerCase().endsWith( ".asc" ) || gridFile.getName().toLowerCase().endsWith( ".asg" ) )
    {
      final File binFile = File.createTempFile( gridFile.getName(), ".bin" );
      binFile.deleteOnExit();

      final ConvertAscii2Binary converter = new ConvertAscii2Binary( gridFile.toURL(), binFile, 2, m_sourceCRS );
      converter.doConvert( progress.newChild( 1 ) );

      return binFile;
    }

    return gridFile;
  }

  public ICoverage getNewCoverage( )
  {
    return m_newCoverage;
  }

}
