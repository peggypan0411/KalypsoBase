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
package org.kalypso.debug.editor.gistree;

import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.IGMLSchema;
import org.kalypso.ogc.gml.schemaeditor.GmlSchemaEditorInput;
import org.kalypso.ui.editor.gmleditor.part.GmlEditor;
import org.kalypsodeegree.model.feature.GMLWorkspace;

/**
 * @author thuel2
 */
public class ShowSchemaEditorActionDelegate implements IEditorActionDelegate
{
  private GmlEditor m_targetEditor;

  /**
   * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
   *      org.eclipse.ui.IEditorPart)
   */
  @Override
  public void setActiveEditor( final IAction action, final IEditorPart targetEditor )
  {
    m_targetEditor = (GmlEditor) targetEditor;

    refreshAction( action );
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  @Override
  public void run( final IAction action )
  {
    final GMLWorkspace workspace = m_targetEditor.getTreeView().getWorkspace();
    if( workspace == null )
      return;

    final IGMLSchema schema = workspace.getGMLSchema();
    if( schema == null )
      return;

    final String version = schema.getGMLVersion();
    final String schemaNamespace = schema.getTargetNamespace();
    final String schemaLocationString = workspace.getSchemaLocationString();

    final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    final IWorkbenchPage activePage = window.getActivePage();

    try
    {
      final Map<String, URL> namespaces = GMLSchemaUtilities.parseSchemaLocation( schemaLocationString, workspace.getContext() );

      final IEditorInput input = new GmlSchemaEditorInput( schemaNamespace, version, namespaces.get( schemaNamespace ) );
      activePage.openEditor( input, "org.kalypso.ogc.gml.schemaeditor.GMLSchemaEditor" );
    }
    catch( final Exception e )
    {
      final IStatus status = StatusUtilities.statusFromThrowable( e );

      ErrorDialog.openError( window.getShell(), "Schema anzeigen", "Fehler beim Öffnen des Schema Editors", status );
    }
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
   *      org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void selectionChanged( final IAction action, final ISelection selection )
  {
    refreshAction( action );
  }

  private void refreshAction( final IAction action )
  {
    action.setEnabled( m_targetEditor != null && m_targetEditor.getTreeView() != null && m_targetEditor.getTreeView().getWorkspace() != null );
  }

}
