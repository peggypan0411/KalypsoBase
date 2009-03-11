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
package org.kalypso.workflow.ui.browser.urlaction;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.workflow.WorkflowContext;
import org.kalypso.workflow.ui.browser.AbstractURLAction;
import org.kalypso.workflow.ui.browser.ICommandURL;

/**
 * @author kuepfer
 */
public class URLActionCopyFile extends AbstractURLAction
{
  private static final String PARAM_SOURCE_URL = "source";

  private static final String PARAM_TARGET_URL = "target";

  private static final String PARAM_CALC_DIR = "calcdir";

  private static final String PARAM_NEWFILE_NAME = "newfilename";

  private static final String PARAM_OVERWRITE = "overwrite";

  /**
   * @see org.kalypso.workflow.ui.browser.IURLAction#run(org.kalypso.workflow.ui.browser.ICommandURL)
   */
  public boolean run( ICommandURL commandURL )
  {
    final String sourceURLAsString = commandURL.getParameter( PARAM_SOURCE_URL );
    final String targetURLAsString = commandURL.getParameter( PARAM_TARGET_URL );
    final String newFileName = commandURL.getParameter( PARAM_NEWFILE_NAME );
    final boolean overwrite = Boolean.parseBoolean( commandURL.getParameter( PARAM_OVERWRITE ) );

    final WorkflowContext context = getWorkFlowContext();
    IPath targetPath = null;
    IFile sourceFile = null;
    try
    {
      final URL sourceURL = context.resolveURL( sourceURLAsString );
      sourceFile = ResourceUtilities.findFileFromURL( sourceURL );
      if( !targetURLAsString.equals( PARAM_CALC_DIR ) )
      {
        final URL targetURL = context.resolveURL( targetURLAsString );
        targetPath = ResourceUtilities.findFileFromURL( targetURL ).getFullPath();
        if( newFileName != null )
        {
          final IPath basePath = targetPath.removeLastSegments( 1 );
          targetPath = basePath.addTrailingSeparator().append( newFileName );
        }
      }
      else
      {
        if( newFileName == null )
          targetPath = context.getContextCalcDir().getFullPath().addTrailingSeparator().append( sourceFile.getName() );
        else
          targetPath = context.getContextCalcDir().getFullPath().addTrailingSeparator().append( newFileName );

      }
      getWorkspaceRoot().refreshLocal( IResource.DEPTH_INFINITE, null );
    }
    catch( MalformedURLException e )
    {
      e.printStackTrace();
      return false;
    }
    catch( CoreException e )
    {
      e.printStackTrace();
    }
    // check if target allready exists
    final IResource member = getWorkspaceRoot().findMember( targetPath );
    // check if source exists
    final boolean exists = sourceFile.exists();
    if( exists )
    {
      final boolean confirmOverwrite;
      if( overwrite )
        confirmOverwrite = true;
      else if( overwrite && member != null )
        confirmOverwrite = MessageDialog.openConfirm( getShell(), "FLOWS Planer Portal", "Sollen die existierenden Ma�nahmen f�r die neue Berechnung �berschieben werden?" );
      else
        confirmOverwrite = true;
      if( confirmOverwrite )
        try
        {
          if( member != null )
            member.delete( true, null );
          sourceFile.copy( targetPath, true, null );
        }
        catch( CoreException e )
        {
          e.printStackTrace();
          return false;
        }
    }
    else
      return false;
    return true;
  }

}
