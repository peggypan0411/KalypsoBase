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
package org.kalypso.ui.editor.gistableeditor.actions;

import org.deegree.model.feature.Annotation;
import org.eclipse.jface.action.Action;
import org.kalypso.ogc.gml.table.LayerTableViewer;
import org.kalypso.ogc.gml.table.command.SetColumnVisibleCommand;
import org.kalypso.util.command.ICommandTarget;

public final class ColumnAction extends Action
{
  private final ICommandTarget m_commandTarget;

  private final LayerTableViewer m_viewer;

  private final String m_propertyName;

  public ColumnAction( final ICommandTarget commandTarget, final LayerTableViewer viewer,
      final String propertyName, final Annotation annotation )
  {
    super( propertyName );
    if( annotation != null )
    {
      setText( annotation.getLabel() + " (" + propertyName + ")" );
//      setDescription( annotation.getDescription() );
//      setToolTipText( annotation.getTooltip() );
    }
    m_commandTarget = commandTarget;
    m_viewer = viewer;
    m_propertyName = propertyName;
    setChecked( viewer.hasColumn( propertyName ) );
  }

  /**
   * @see org.eclipse.jface.action.IAction#run()
   */
  public void run()
  {
    final SetColumnVisibleCommand setColumnVisibleCommand = new SetColumnVisibleCommand( m_viewer,
        m_propertyName, isChecked() );

    m_commandTarget.postCommand( setColumnVisibleCommand, null );
  }
}