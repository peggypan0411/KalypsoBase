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
package org.kalypso.gml.ui.internal.coverage.imports;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.contribs.java.io.FilePattern;
import org.kalypso.gml.ui.KalypsoGmlUIPlugin;
import org.kalypso.gml.ui.i18n.Messages;
import org.kalypso.grid.GridFileVerifier;
import org.kalypso.grid.GridMetaReaderBinWorldFile;
import org.kalypso.grid.IGridMetaReader;

/**
 * Imports a Kalypso grid bin file as coverage.
 *
 * @author Gernot Belger
 */
public class BINCoverageImporter extends AbstractGridCoverageImporter
{
  @Override
  public FilePattern getFilePattern( )
  {
    final String pattern = "*.bin";
    final String filterName = "Kalypso Grid-Files";

    return new FilePattern( pattern, filterName );
  }

  @Override
  protected IGridMetaReader createRasterMetaReader( final URL data, final String sourceSRS )
  {
    final URL worldFile = GridFileVerifier.getWorldFile( data );
    return new GridMetaReaderBinWorldFile( data, worldFile );
  }

  @Override
  protected String doImportData( final File sourceFile, final File targetDir, final String sourceSRS, final IProgressMonitor monitor ) throws CoreException
  {
    File targetFile = null;

    try
    {
      final String gridFileName = sourceFile.getName();

      try
      {
        targetFile = new File( targetDir, gridFileName );
        if( targetFile.exists() )
        {
          targetFile = null; // elese if will be deleted
          final String message = Messages.getString( "org.kalypso.gml.ui.wizard.grid.ImportGridUtilities.0", targetFile ); //$NON-NLS-1$
          throw new CoreException( new Status( IStatus.ERROR, KalypsoGmlUIPlugin.id(), message, null ) );
        }

        FileUtils.copyFile( sourceFile, targetFile );
        return targetFile.getName();
      }
      catch( final IOException e )
      {
        e.printStackTrace();
        final String message = Messages.getString( "org.kalypso.gml.ui.wizard.grid.ImportGridUtilities.3" ); //$NON-NLS-1$
        final IStatus status = new Status( IStatus.ERROR, KalypsoGmlUIPlugin.id(), message, e );
        throw new CoreException( status );
      }
    }
    catch( final CoreException e )
    {
      if( targetFile != null )
        targetFile.delete();

      throw e;
    }
  }
}