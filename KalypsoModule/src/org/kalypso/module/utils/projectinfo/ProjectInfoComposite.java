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
package org.kalypso.module.utils.projectinfo;

import java.io.File;
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.kalypso.contribs.eclipse.ui.forms.MessageProvider;
import org.kalypso.module.nature.IModulePreferences;
import org.kalypso.module.nature.ModuleNature;

/**
 * @author Gernot Belger
 */
public class ProjectInfoComposite extends Composite
{
  private static final String STR_TOOLTIP_COMMENT = "Projektbeschreibung (Benutzerdefiniert)";

  private final Text m_typeText;

  private final Text m_versionText;

  private IProjectDescription m_project;

  private final Text m_commentText;

  private IMessageProvider m_message;

  private IModulePreferences m_preferences;

  public ProjectInfoComposite( final Composite parent )
  {
    super( parent, SWT.NONE );

    setLayout( new GridLayout( 2, false ) );

    final Label commentLabel = new Label( this, SWT.NONE );
    commentLabel.setToolTipText( STR_TOOLTIP_COMMENT );
    commentLabel.setText( "Beschreibung" );
    m_commentText = new Text( this, SWT.READ_ONLY | SWT.BORDER );
    m_commentText.setToolTipText( STR_TOOLTIP_COMMENT );
    m_commentText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    new Label( this, SWT.NONE ).setText( "Modelltyp" );
    m_typeText = new Text( this, SWT.READ_ONLY | SWT.BORDER );
    m_typeText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    new Label( this, SWT.NONE ).setText( "Version" );
    m_versionText = new Text( this, SWT.READ_ONLY | SWT.BORDER );
    m_versionText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    updateControl();
  }

  public void setProject( final File file )
  {
    readAndValidateProject( file );

    updateControl();
  }

  private void readAndValidateProject( final File file )
  {
    m_message = readProjectDescription( file );
    if( m_message == null )
      m_message = readPreferences( file );
  }

  private IMessageProvider readProjectDescription( final File file )
  {
    m_project = null;

    if( file == null )
      return new MessageProvider( "No project directory selected", IMessageProvider.ERROR );

    if( !file.isDirectory() )
      return new MessageProvider( "Selected project is not a directory", IMessageProvider.ERROR );

    try
    {
      final IPath projectPath = new Path( file.getPath() );
      final IPath projectFilePath = projectPath.append( IProjectDescription.DESCRIPTION_FILE_NAME );
      m_project = ResourcesPlugin.getWorkspace().loadProjectDescription( projectFilePath );
      return null;
    }
    catch( final CoreException e )
    {
// final String message = String.format( "Chosen directory does not contain a project ('%s' file not present)",
// IProjectDescription.DESCRIPTION_FILE_NAME );

      e.printStackTrace();
      return new MessageProvider( "Failed to read project description", IMessageProvider.ERROR );
    }
  }

  private IMessageProvider readPreferences( final File projectDir )
  {
    m_preferences = null;

    final URI uri = projectDir.toURI();
    final IContainer[] containers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI( uri );
    for( final IContainer container : containers )
    {
      if( container instanceof IProject )
      {
        final ModuleNature nature = ModuleNature.toThisNature( (IProject) container );
        if( nature != null )
          m_preferences = nature.getPreferences();
        else
        {
          // User has chosen a project in workspace which is not a module project
          m_preferences = null;
        }

        return new MessageProvider( "The project is already part of the Kalypso Workspace", IMessageProvider.INFORMATION );
      }
    }

    /* Resource is a local file: read preferences from there */
    m_preferences = ModuleNature.getPreferences( projectDir );
    return null;
  }

  public IProjectDescription getProject( )
  {
    return m_project;
  }

  public IMessageProvider validate( )
  {
    return m_message;
  }

  private void updateControl( )
  {
    if( m_project == null )
    {
      m_typeText.setText( "-" );
      m_versionText.setText( "-" );
      m_commentText.setText( "-" );
      return;
    }

    final String comment = m_project.getComment();
    if( StringUtils.isBlank( comment ) )
      m_commentText.setText( "<Nicht gesetzt>" );
    else
      m_commentText.setText( comment );

    final String moduleID = m_preferences == null ? null : m_preferences.getModule();
    final String version = m_preferences == null ? null : m_preferences.getVersion();

    if( version == null )
      m_versionText.setText( "<Unbekannte Version>" );
    else
      m_versionText.setText( version );

// final IKalypsoModule module = moduleID == null ? null : ModuleExtensions.getKalypsoModule( moduleID );
// if( module == null )
// m_typeText.setText( "<Unbekannter Modelltyp>" );
// else
// m_typeText.setText( module.getHeader() );
  }
}
