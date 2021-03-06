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
package org.kalypso.ogc.gml.map.handlers;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.map.handlers.utils.PDFExporter;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * This handler starts the PDF export.
 * 
 * @author Holger Albert
 */
public class ExportPdfHandler extends AbstractHandler
{
  /**
   * The constructor.
   */
  public ExportPdfHandler( )
  {
  }

  /**
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    try
    {
      /* Get the evaluation context. */
      final IEvaluationContext context = (IEvaluationContext)event.getApplicationContext();

      /* Get the shell. */
      final Shell shell = (Shell)context.getVariable( ISources.ACTIVE_SHELL_NAME );

      /* Get the active part. */
      final IWorkbenchPart activePart = HandlerUtil.getActivePart( event );
      if( activePart == null )
        return null;

      /* Get the map panel. */
      final IMapPanel mapPanel = MapHandlerUtils.getMapPanelChecked( context );

      /* Ask for a file name. */
      final String fileName = String.format( "%s.pdf", FilenameUtils.removeExtension( activePart.getTitle() ) ); //$NON-NLS-1$
      final File targetFile = MapHandlerUtils.showSaveFileDialog( shell, Messages.getString( "ExportPdfHandler_1" ), fileName, PDFExporter.class.getCanonicalName(), new String[] { "*.pdf", "*.*" }, new String[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      Messages.getString( "ExportPdfHandler_4" ), Messages.getString( "ExportPdfHandler_5" ) } ); //$NON-NLS-1$ //$NON-NLS-2$
      if( targetFile == null )
        return null;

      /* Export the PDF. */
      final PDFExporter exporter = new PDFExporter( mapPanel );
      final IStatus status = exporter.doExport( targetFile, new NullProgressMonitor() );

      /* If the export was ok, open the PDF. */
      /* If the export has failed, show an error to the user. */
      if( status.isOK() )
      {
        /* Open the PDF file. */
        final boolean launch = Program.launch( targetFile.getAbsolutePath() );
        if( !launch )
        {
          MessageDialog.openError( shell, Messages.getString( "ExportPdfHandler_6" ), Messages.getString( "ExportPdfHandler_7" ) ); //$NON-NLS-1$ //$NON-NLS-2$
          return null;
        }
      }
      else
        ErrorDialog.openError( shell, Messages.getString( "ExportPdfHandler_8" ), Messages.getString( "ExportPdfHandler_9" ), status ); //$NON-NLS-1$ //$NON-NLS-2$

      return null;
    }
    catch( final Exception ex )
    {
      throw new ExecutionException( ex.getMessage(), ex );
    }
  }
}