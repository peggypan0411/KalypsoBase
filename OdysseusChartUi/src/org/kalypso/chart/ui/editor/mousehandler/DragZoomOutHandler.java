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
package org.kalypso.chart.ui.editor.mousehandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.openali.odysseus.chart.framework.model.IChartModel;
import de.openali.odysseus.chart.framework.model.impl.visitors.ZoomOutVisitor;
import de.openali.odysseus.chart.framework.model.layer.EditInfo;
import de.openali.odysseus.chart.framework.view.IChartComposite;

/**
 * @author Gernot Belger
 * @author burtscher1
 */
public class DragZoomOutHandler extends AbstractChartDragHandler
{

  public DragZoomOutHandler( final IChartComposite chartComposite )
  {
    super( chartComposite, 5, SWT.BUTTON_MASK, SWT.CURSOR_CROSS );
  }

  /**
   * @see org.kalypso.chart.ui.editor.mousehandler.AbstractChartDragHandler#doMouseUpAction(org.eclipse.swt.graphics.Point,
   *      de.openali.odysseus.chart.framework.model.layer.EditInfo)
   */
  @Override
  public void doMouseUpAction( final Point end, final EditInfo editInfo )
  {

    try
    {
      final ZoomOutVisitor visitor = new ZoomOutVisitor( getChart().screen2plotPoint( editInfo.getPosition() ), getChart().screen2plotPoint( end ) );

      final IChartModel model = getChart().getChartModel();
      model.getMapperRegistry().accept( visitor );
    }
    finally
    {
      getChart().setDragArea( null );
    }
  }

  /**
   * @see org.kalypso.chart.ui.editor.mousehandler.AbstractChartDragHandler#doMouseMoveAction(org.eclipse.swt.graphics.Point,
   *      de.openali.odysseus.chart.framework.model.layer.EditInfo)
   */
  @Override
  public void doMouseMoveAction( final Point end, final EditInfo editInfo )
  {
    final Point start = editInfo.getPosition();
    final Point pos = getChart().plotPoint2screen( end );
    getChart().setDragArea( new Rectangle( start.x, start.y, pos.x - start.x, pos.y - start.y ) );
  }

}
