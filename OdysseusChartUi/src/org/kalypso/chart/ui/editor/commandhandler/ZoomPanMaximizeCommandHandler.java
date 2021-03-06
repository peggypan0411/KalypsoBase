package org.kalypso.chart.ui.editor.commandhandler;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.kalypso.chart.ui.IChartPart;
import org.kalypso.chart.ui.editor.ElementUpdateHelper;
import org.kalypso.chart.ui.editor.mousehandler.ZoomPanMaximizeHandler;
import org.kalypso.chart.ui.editor.mousehandler.ZoomPanMaximizeHandler.DIRECTION;

import de.openali.odysseus.chart.framework.view.IChartComposite;
import de.openali.odysseus.chart.framework.view.IChartHandlerManager;

public class ZoomPanMaximizeCommandHandler extends AbstractHandler implements IElementUpdater
{
  ZoomPanMaximizeHandler m_handler;

  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();
    final IChartComposite chart = ChartHandlerUtilities.getChartChecked( context );

    final IChartHandlerManager plotHandler = chart.getPlotHandler();
    m_handler = new ZoomPanMaximizeHandler( chart, getDirection( event ) );

    plotHandler.activatePlotHandler( m_handler );

    final IChartPart part = ChartHandlerUtilities.findChartComposite( context );
    if( part != null )
      ChartHandlerUtilities.updateElements( part );

    return Status.OK_STATUS;
  }

  protected ZoomPanMaximizeHandler getHandler( )
  {
    return m_handler;
  }

  private ZoomPanMaximizeHandler.DIRECTION getDirection( final ExecutionEvent event )
  {
    final String parameter = event.getParameter( "direction" ); //$NON-NLS-1$

    return DIRECTION.getDirection( parameter );
  }

  @Override
  public void updateElement( final UIElement element, final Map parameters )
  {
    ElementUpdateHelper.updateElement( element, ZoomPanMaximizeHandler.class );
  }
}
