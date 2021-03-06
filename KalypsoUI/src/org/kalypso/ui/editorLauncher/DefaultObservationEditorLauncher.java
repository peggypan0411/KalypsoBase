/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ui.editorLauncher;

import java.net.MalformedURLException;

import org.apache.commons.httpclient.URIException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.ogc.sensor.template.PseudoTemplateEditorInput;
import org.kalypso.ogc.sensor.template.TemplateStorage;

/**
 * DefaultObservationTemplateLauncher
 * 
 * @author schlienger
 */
public class DefaultObservationEditorLauncher implements IDefaultTemplateLauncher
{
  private final String m_pseudoFilename;

  private final String m_fileExtension;

  public DefaultObservationEditorLauncher( final String pseudoFilename, final String fileExtension )
  {
    m_pseudoFilename = pseudoFilename;
    m_fileExtension = fileExtension;
  }

  /**
   * @see org.kalypso.ui.editorLauncher.IDefaultTemplateLauncher#getFilename()
   */
  @Override
  public String getFilename( )
  {
    return m_pseudoFilename;
  }

  /**
   * @see org.kalypso.ui.editorLauncher.IDefaultTemplateLauncher#getEditor()
   */
  @Override
  public IEditorDescriptor getEditor( )
  {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IEditorRegistry editorRegistry = workbench.getEditorRegistry();
    return editorRegistry.getDefaultEditor( getFilename() );
  }

  @Override
  public IEditorInput createInput( final IFile file )
  {
    try
    {
      final IPath projectRelativePath = file.getProjectRelativePath();

      final PseudoTemplateEditorInput input = new PseudoTemplateEditorInput( new TemplateStorage( file, ResourceUtilities.createURL( file ), "project:/" + projectRelativePath ), m_fileExtension ); //$NON-NLS-1$

      return input;
    }
    catch( final MalformedURLException | URIException e )
    {
      e.printStackTrace();
      return null;
    }
  }
}