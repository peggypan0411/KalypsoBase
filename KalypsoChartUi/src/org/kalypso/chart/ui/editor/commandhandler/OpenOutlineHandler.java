package org.kalypso.chart.ui.editor.commandhandler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.kalypso.chart.ui.IChartPart;
import org.kalypso.chart.ui.outline.ChartOutlinePopupDialog;

public class OpenOutlineHandler extends AbstractHandler
{
  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();

    final IChartPart chartPart = ChartHandlerUtilities.findChartComposite( context );
    if( chartPart == null )
      return null;

    IWorkbenchPart part = (IWorkbenchPart) context.getVariable( ISources.ACTIVE_PART_NAME );
    if( part == null )
      throw new ExecutionException( "No active part." );

    IWorkbenchPartSite site = part.getSite();
    if( site == null )
      throw new ExecutionException( "No active site." );

    IWorkbenchPage page = site.getPage();
    if( page == null )
      throw new ExecutionException( "No active page." );

    PopupDialog d = new ChartOutlinePopupDialog( (Shell) context.getVariable( ISources.ACTIVE_SHELL_NAME ), chartPart );

    d.open();
    Shell shell = d.getShell();
    Point size = shell.getSize();
    Point mousePos = Display.getCurrent().getCursorLocation();
    shell.setBounds( new Rectangle( mousePos.x - size.x, mousePos.y, size.x, size.y ) );

    return null;
  }
}
