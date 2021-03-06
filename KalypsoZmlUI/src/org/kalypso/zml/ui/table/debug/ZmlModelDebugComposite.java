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
package org.kalypso.zml.ui.table.debug;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.contribs.eclipse.swt.layout.Layouts;
import org.kalypso.zml.core.table.model.IZmlModel;

/**
 * @author Dirk Kuch
 */
public class ZmlModelDebugComposite extends Composite
{

  public ZmlModelDebugComposite( final Composite parent, final FormToolkit toolkit, final IZmlModel dataModel )
  {
    super( parent, SWT.NULL );

    setLayout( Layouts.createGridLayout() );
    print( toolkit, dataModel );

    toolkit.adapt( this );
  }

  private void print( final FormToolkit toolkit, final IZmlModel model )
  {
    toolkit.createLabel( this, "Model Columns" ); //$NON-NLS-1$

    final TreeViewer viewer = new TreeViewer( this );
    viewer.getTree().setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

    viewer.setContentProvider( new DebugZmlModelContentProvider() );
    viewer.setLabelProvider( new DebugZmlModelLabelProvider() );

    viewer.setInput( model.getColumns() );

  }

}
