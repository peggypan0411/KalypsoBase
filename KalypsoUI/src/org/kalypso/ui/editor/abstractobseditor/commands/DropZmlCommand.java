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
package org.kalypso.ui.editor.abstractobseditor.commands;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.kalypso.commons.command.ICommand;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.ogc.sensor.template.ObsView;
import org.kalypso.ogc.sensor.template.ObsViewItem;
import org.kalypso.ui.editor.abstractobseditor.AbstractObservationEditor;
import org.kalypso.ui.internal.i18n.Messages;

public class DropZmlCommand implements ICommand
{
  private final AbstractObservationEditor m_editor;

  private final String[] m_files;

  private final ObsViewItem[] m_orgItems;

  private final ObsView m_view;

  public DropZmlCommand( final AbstractObservationEditor editor, final ObsView view, final String[] files )
  {
    m_editor = editor;
    m_orgItems = view.getItems();
    m_view = view;
    m_files = files;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#isUndoable()
   */
  @Override
  public boolean isUndoable( )
  {
    return true;
  }

  /**
   * @see org.kalypso.commons.command.ICommand#process()
   */
  @Override
  public void process( ) throws Exception
  {
    final IWorkspaceRoot wksp = ResourcesPlugin.getWorkspace().getRoot();

    for( final String m_file : m_files )
    {
      IFile file = wksp.getFileForLocation( new Path( m_file ) );
      file = (IFile)wksp.findMember( file.getFullPath() );
      final URL url = ResourceUtilities.createURL( file );

      m_editor.loadObservation( url, url.toExternalForm() );
    }
  }

  /**
   * @see org.kalypso.commons.command.ICommand#redo()
   */
  @Override
  public void redo( ) throws Exception
  {
    process();
  }

  /**
   * @see org.kalypso.commons.command.ICommand#undo()
   */
  @Override
  public void undo( ) throws Exception
  {
    m_view.removeAllItems();
    for( final ObsViewItem m_orgItem : m_orgItems )
      m_view.addItem( m_orgItem );
  }

  /**
   * @see org.kalypso.commons.command.ICommand#getDescription()
   */
  @Override
  public String getDescription( )
  {
    return Messages.getString( "org.kalypso.ui.editor.abstractobseditor.commands.DropZmlCommand.0" ); //$NON-NLS-1$
  }
}