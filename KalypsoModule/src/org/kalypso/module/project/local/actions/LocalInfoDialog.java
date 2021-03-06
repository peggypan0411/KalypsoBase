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
package org.kalypso.module.project.local.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.module.internal.i18n.Messages;
import org.kalypso.module.project.local.ILocalProjectHandle;

/**
 * @author Dirk Kuch
 */
public class LocalInfoDialog extends TitleAreaDialog
{
  private final ILocalProjectHandle m_item;

  private String m_comment;

  public LocalInfoDialog( final ILocalProjectHandle item, final Shell parentShell )
  {
    super( parentShell );
    m_item = item;

    setBlockOnOpen( true );
    setDialogHelpAvailable( false );
    setHelpAvailable( false );
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createContents( final Composite parent )
  {
    final Control contents = super.createContents( parent );

    setTitle( Messages.getString( "org.kalypso.core.projecthandle.local.LocalInfoDialog.0" ) ); //$NON-NLS-1$
    setMessage( Messages.getString( "org.kalypso.core.projecthandle.local.LocalInfoDialog.Description" ) ); //$NON-NLS-1$

    getButton( OK ).setEnabled( false );

    return contents;
  }

  /**
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea( final Composite parent )
  {
    final Composite composite = (Composite) super.createDialogArea( parent );
    composite.setLayout( new GridLayout() );
    final GridData data = new GridData( GridData.FILL, GridData.FILL, true, true );
    data.heightHint = 350;
    data.widthHint = 400;
    composite.setLayoutData( data );

    final Composite body = new Composite( composite, SWT.NULL );
    body.setLayout( new GridLayout() );
    body.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

    renderProjectInfo( body );

    return composite;
  }

  private void renderProjectInfo( final Composite parent )
  {
    /* name */
    new Label( parent, SWT.NULL ).setText( Messages.getString( "org.kalypso.core.projecthandle.local.LocalInfoDialog.1" ) ); //$NON-NLS-1$

    final Text name = new Text( parent, SWT.BORDER | SWT.READ_ONLY );
    name.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );
    name.setText( m_item.getName() );

    /* description */
    final Label labelDescription = new Label( parent, SWT.TOP );
    labelDescription.setText( Messages.getString( "org.kalypso.core.projecthandle.local.LocalInfoDialog.2" ) ); //$NON-NLS-1$
    labelDescription.setLayoutData( new GridData( GridData.FILL, GridData.FILL, false, false ) );

    final Text description = new Text( parent, SWT.BORDER | SWT.MULTI | SWT.WRAP );
    description.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

    description.setFocus();

    try
    {
      final IProjectDescription projectDescription = m_item.getProject().getDescription();
      m_comment = projectDescription.getComment();
    }
    catch( final CoreException e )
    {
      KalypsoCorePlugin.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
    }

    description.setText( m_comment );

    description.addModifyListener( new ModifyListener()
    {
      @Override
      public void modifyText( final ModifyEvent e )
      {
        handleCommentModified( description.getText() );
      }
    } );
  }

  protected void handleCommentModified( final String text )
  {
    m_comment = text;
    getButton( OK ).setEnabled( true );
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed( )
  {
    try
    {
      final IProject project = m_item.getProject();
      final IProjectDescription projectDescription = project.getDescription();
      projectDescription.setComment( m_comment );

      project.setDescription( projectDescription, new NullProgressMonitor() );
    }
    catch( final CoreException e1 )
    {
      KalypsoCorePlugin.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e1 ) );
    }

    super.okPressed();
  }
}
