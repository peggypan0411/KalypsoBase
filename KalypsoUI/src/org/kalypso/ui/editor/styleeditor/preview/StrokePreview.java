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

import java.awt.Graphics2D;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.kalypso.ui.editor.styleeditor.binding.IStyleInput;
import org.kalypsodeegree.filterencoding.FilterEvaluationException;
import org.kalypsodeegree.graphics.sld.Stroke;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree_impl.graphics.sld.awt.StrokePainter;

/**
 * @author Gernot Belger
 */
public class StrokePreview extends Preview<Stroke>
{
  public StrokePreview( final Composite parent, final Point size, final IStyleInput<Stroke> input )
  {
    super( parent, size, input );
  }

  /**
   * @see org.kalypso.ui.editor.styleeditor.preview.Preview#doCreateGeometry()
   */
  @Override
  protected GM_Object doCreateGeometry( )
  {
    return null;
  }

  @Override
  protected void doPaintData( final Graphics2D g2d, final int width, final int height, final GM_Object geom )
  {
    try
    {
      final StrokePainter painter = new StrokePainter( getInputData(), null, null, null );
      final int[][] pos = new int[3][4];
      pos[0][0] = 4;
      pos[1][0] = 4;
      pos[0][1] = (int)(width / 3.0);
      pos[1][1] = height - 4;
      pos[0][2] = (int)(width / 1.5);
      pos[1][2] = 4;
      pos[0][3] = width - 4;
      pos[1][3] = height - 4;
      pos[2][0] = 4;

      painter.paintPoses( g2d, pos );
    }
    catch( final FilterEvaluationException e )
    {
      e.printStackTrace();
    }
  }
}