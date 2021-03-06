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
package org.kalypso.module.conversion;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.kalypso.contribs.eclipse.jface.wizard.FileChooserDelegateDirectory;
import org.kalypso.contribs.eclipse.jface.wizard.FileChooserGroup;
import org.kalypso.contribs.eclipse.jface.wizard.FileChooserGroup.FileChangedListener;
import org.kalypso.module.conversion.internal.ConverterUtils;
import org.kalypso.module.conversion.internal.ProjectConversionOperation;
import org.kalypso.module.conversion.internal.ProjectConverterExtensions;
import org.kalypso.module.internal.i18n.Messages;
import org.kalypso.module.utils.projectinfo.ProjectInfoComposite;
import org.osgi.framework.Version;

/**
 * Lets the user choose which project to import and convert (only external projects supported now). <br/>
 * TODO: move this page to a more common place.
 * 
 * @author Gernot Belger
 */
public class ProjectConversionPage extends WizardPage
{
  private FileChooserGroup m_projectChooserGroup;

  private ProjectInfoComposite m_infoGroup;

  private final String m_moduleID;

  public ProjectConversionPage( final String pageName, final String moduleID )
  {
    super( pageName );

    m_moduleID = moduleID;

    setTitle( Messages.getString( "ProjectConversionPage.0" ) ); //$NON-NLS-1$
    setDescription( Messages.getString( "ProjectConversionPage.1" ) ); //$NON-NLS-1$

    setPageComplete( false );
  }

  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl( final Composite parent )
  {
    initializeDialogUnits( parent );

    final ScrolledForm form = new ScrolledForm( parent, SWT.V_SCROLL | SWT.H_SCROLL );
    form.setExpandHorizontal( true );
    form.setExpandVertical( true );

    final ColumnLayout layout = new ColumnLayout();
    layout.maxNumColumns = 1;
    final Composite body = form.getBody();
    body.setLayout( layout );
    setControl( form );

    createProjectFileGroup( body );
    createProjectInfoGroup( body );

    final File file = m_projectChooserGroup.getFile();
    if( file != null )
      handleFileChanged( file );
  }

  private Composite createProjectFileGroup( final Composite panel )
  {
    final Group group = new Group( panel, SWT.NONE );
    group.setLayout( new GridLayout( 3, false ) );
    group.setText( Messages.getString( "ProjectConversionPage.2" ) ); //$NON-NLS-1$

    final FileChooserDelegateDirectory dirDelegate = new FileChooserDelegateDirectory();
    m_projectChooserGroup = new FileChooserGroup( dirDelegate );
    m_projectChooserGroup.setDialogSettings( getDialogSettings() );
    m_projectChooserGroup.setLabel( null );

    m_projectChooserGroup.createControlsInGrid( group );
    m_projectChooserGroup.addFileChangedListener( new FileChangedListener()
    {
      @Override
      public void fileChanged( final File file )
      {
        handleFileChanged( file );
      }
    } );

    return group;
  }

  private Control createProjectInfoGroup( final Composite parent )
  {
    final Group group = new Group( parent, SWT.NONE );
    group.setLayout( new FillLayout() );
    group.setText( Messages.getString( "ProjectConversionPage.3" ) ); //$NON-NLS-1$

    m_infoGroup = new ProjectInfoComposite( group );

    return group;
  }

  protected void handleFileChanged( final File file )
  {
    updateProjectInfo( file );

    final IMessageProvider meessage = validatePage();
    setMessage( meessage );
  }

  private void updateProjectInfo( final File file )
  {
    m_infoGroup.setProject( file );
  }

  private IMessageProvider validatePage( )
  {
    final IMessageProvider groupMessage = m_projectChooserGroup.validate();
    if( groupMessage != null )
      return groupMessage;

    return m_infoGroup.validate();
  }

  private void setMessage( final IMessageProvider message )
  {
    setPageComplete( message == null || message.getMessageType() != IMessageProvider.ERROR );

    if( message == null )
      setMessage( (String) null );
    else
      setMessage( message.getMessage(), message.getMessageType() );
  }

  public File getProjectDir( )
  {
    return m_projectChooserGroup.getFile();
  }

  public IProjectConversionOperation getConversionOperation( final File sourceDir, final File targetDir, final IProject targetProject ) throws CoreException
  {
    final Version sourceVersion = m_infoGroup.getVersion();
    final IProjectConverterFactory factory = ProjectConverterExtensions.getProjectConverter( m_moduleID );
    final IProjectConverter converter = ConverterUtils.createConverter( factory, sourceVersion, sourceDir, targetDir );

    return new ProjectConversionOperation( targetProject, converter );
  }
}
