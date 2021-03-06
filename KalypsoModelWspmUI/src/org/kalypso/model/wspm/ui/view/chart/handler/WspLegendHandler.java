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
package org.kalypso.model.wspm.ui.view.chart.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.kalypso.chart.ui.IChartPart;
import org.kalypso.model.wspm.ui.i18n.Messages;
import org.kalypso.model.wspm.ui.view.chart.layer.wsp.WspLayer;
import org.kalypso.model.wspm.ui.view.chart.layer.wsp.utils.WspLegendPopupDialog;

import de.openali.odysseus.chart.framework.model.IChartModel;
import de.openali.odysseus.chart.framework.model.layer.IChartLayer;
import de.openali.odysseus.chart.framework.model.layer.ILayerManager;
import de.openali.odysseus.chart.framework.view.IChartComposite;

/**
 * This handler opens the wsp legend.
 * 
 * @author Holger Albert
 */
public class WspLegendHandler extends AbstractHandler
{
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    /* Get the context. */
    final IEvaluationContext context = (IEvaluationContext)event.getApplicationContext();

    /* Get the part. */
    final IWorkbenchPart part = (IWorkbenchPart)context.getVariable( ISources.ACTIVE_PART_NAME );
    if( part == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.model.wspm.ui.view.chart.layer.WspLegendHandler.0" ) ); //$NON-NLS-1$

    /* Get the site. */
    final IWorkbenchPartSite site = part.getSite();
    if( site == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.model.wspm.ui.view.chart.layer.WspLegendHandler.1" ) ); //$NON-NLS-1$

    /* Get the page. */
    final IWorkbenchPage page = site.getPage();
    if( page == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.model.wspm.ui.view.chart.layer.WspLegendHandler.2" ) ); //$NON-NLS-1$

    /* This handler only works with the chart view. */
    if( !(part instanceof IChartPart) )
      throw new ExecutionException( Messages.getString( "org.kalypso.model.wspm.ui.view.chart.layer.WspLegendHandler.3" ) ); //$NON-NLS-1$

    /* Cast. */
    final IChartPart chartView = (IChartPart)part;

    final IChartComposite chartComposite = chartView.getChartComposite();
    if( chartComposite == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.model.wspm.ui.view.chart.layer.WspLegendHandler.4" ) ); //$NON-NLS-1$

    /* Get the layer manager. */
    final IChartModel chartModel = chartComposite.getChartModel();
    if( chartModel == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.model.wspm.ui.view.chart.layer.WspLegendHandler.4" ) ); //$NON-NLS-1$

    final ILayerManager layerManager = chartModel.getLayerManager();

    /* The wsp layer. */
    WspLayer wspLayer = null;

    /* Get all layers. */
    final IChartLayer[] layers = layerManager.getLayers();
    for( final IChartLayer layer : layers )
    {
      if( layer instanceof WspLayer )
      {
        /* Cast. */
        wspLayer = (WspLayer)layer;

        break;
      }
    }

    /* Create the dialog. */
    final WspLegendPopupDialog dialog = new WspLegendPopupDialog( (Shell)context.getVariable( ISources.ACTIVE_SHELL_NAME ), wspLayer );

    /* Open the dialog. */
    dialog.open();

    /* Adjust the position of the dialog. */
    final Shell shell = dialog.getShell();
    final Point shellSize = shell.getSize();
    final Point mousePos = shell.getDisplay().getCursorLocation();
    shell.setBounds( new Rectangle( mousePos.x - shellSize.x, mousePos.y, shellSize.x, shellSize.y ) );

    return null;
  }
}