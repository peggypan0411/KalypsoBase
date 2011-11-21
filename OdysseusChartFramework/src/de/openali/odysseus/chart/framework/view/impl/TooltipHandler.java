package de.openali.odysseus.chart.framework.view.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import de.openali.odysseus.chart.framework.model.IChartModel;
import de.openali.odysseus.chart.framework.model.layer.EditInfo;
import de.openali.odysseus.chart.framework.model.layer.ILayerManager;
import de.openali.odysseus.chart.framework.model.layer.ITooltipChartLayer;
import de.openali.odysseus.chart.framework.model.layer.manager.visitors.TooltipChartLayerVisitor;
import de.openali.odysseus.chart.framework.view.IChartComposite;

/**
 * handler to manage tooltips; registers on creation, deregisters on disposation
 * 
 * @author kimwerner
 */
public class TooltipHandler extends MouseAdapter implements MouseListener, MouseMoveListener
{

  private final IChartComposite m_chart;

  public TooltipHandler( final IChartComposite chart )
  {
    m_chart = chart;
    ((Composite) m_chart).addMouseListener( this );
    ((Composite) m_chart).addMouseMoveListener( this );

  }

  public void dispose( )
  {
    if( !((Composite) m_chart).isDisposed() )
    {
      ((Composite) m_chart).removeMouseListener( this );
      ((Composite) m_chart).removeMouseMoveListener( this );
    }
  }

  private final Point screen2plotPoint( final Point screen, final Rectangle plotRect )
  {
    if( plotRect == null )
      return screen;

    return new Point( screen.x - plotRect.x, screen.y - plotRect.y );
  }

  /**
   * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
   */
  @Override
  public void mouseMove( final MouseEvent e )
  {
    if( (e.stateMask & SWT.BUTTON_MASK) == 0 )// no mousebutton pressed
    {
      final IChartModel model = m_chart.getChartModel();
      if( model == null )
        return;
      final Point point = screen2plotPoint( new Point( e.x, e.y ), m_chart.getPlotRect() );

      final ILayerManager manager = model.getLayerManager();
      final TooltipChartLayerVisitor visitor = new TooltipChartLayerVisitor();
      manager.accept( visitor );

      /** Array umdrehen, damit die oberen Layer zuerst befragt werden */
      final ITooltipChartLayer[] layers = visitor.getLayers();
      ArrayUtils.reverse( layers );
      for( final ITooltipChartLayer layer : layers )
      {
        if( layer.isVisible() )
        {
          final EditInfo info = layer.getHover( point );
          if( info != null )
          {
            m_chart.setTooltipInfo( info );
            return;
          }
        }
      }
    }
    m_chart.setTooltipInfo( null );
  }
}
