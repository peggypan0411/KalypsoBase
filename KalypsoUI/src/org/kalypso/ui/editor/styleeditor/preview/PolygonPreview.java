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
package org.kalypso.ui.editor.styleeditor.preview;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.kalypso.ui.editor.styleeditor.binding.IStyleInput;
import org.kalypsodeegree.graphics.sld.PolygonSymbolizer;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * @author Gernot Belger
 */
public class PolygonPreview extends SymbolizerPreview<PolygonSymbolizer>
{
  public PolygonPreview( final Composite parent, final Point size, final IStyleInput<PolygonSymbolizer> input )
  {
    super( parent, size, input );
  }

  /**
   * @see org.kalypso.ui.editor.styleeditor.preview.SymbolizerPreview#doCreateGeometry()
   */
  @Override
  protected GM_Object doCreateGeometry( ) throws GM_Exception
  {
    final GM_Position pos1 = GeometryFactory.createGM_Position( 0.1, 0.1 );
    final GM_Position pos2 = GeometryFactory.createGM_Position( 0.9, 0.1 );
    final GM_Position pos3 = GeometryFactory.createGM_Position( 0.9, 0.9 );
    final GM_Position pos4 = GeometryFactory.createGM_Position( 0.1, 0.9 );

    final GM_Position[] positions = new GM_Position[] { pos1, pos2, pos3, pos4, pos1 };

    return GeometryFactory.createGM_Surface( positions, null, null );
  }
}