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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.kalypso.module.internal.i18n.Messages;
import org.kalypso.module.project.local.ILocalProjectHandle;
import org.kalypso.module.project.local.wizard.export.WizardProjectExport;

/**
 * @author Dirk Kuch
 */
public class ProjectExportAction extends Action
{
  private static final ImageDescriptor IMG_EXPORT = ImageDescriptor.createFromURL( ProjectExportAction.class.getResource( "images/action_export.gif" ) ); //$NON-NLS-1$

  protected final ILocalProjectHandle m_item;

  public ProjectExportAction( final ILocalProjectHandle item )
  {
    m_item = item;

    setImageDescriptor( IMG_EXPORT );
    setToolTipText( Messages.getString( "org.kalypso.core.projecthandle.local.ProjectExportAction.1" ) ); //$NON-NLS-1$
  }

  @Override
  public void runWithEvent( final Event event )
  {
    final Shell shell = event.widget.getDisplay().getActiveShell();

    final WizardProjectExport wizard = new WizardProjectExport( m_item.getProject() );
    wizard.init( PlatformUI.getWorkbench(), new StructuredSelection( m_item.getProject() ) );

    final WizardDialog dialog = new WizardDialog( shell, wizard );
    dialog.open();
  }
}