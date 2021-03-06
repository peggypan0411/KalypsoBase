package org.kalypso.chart.ui.editor.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.handlers.IHandlerService;
import org.kalypso.chart.ui.editor.ChartEditor;

public class EditActionDelegate extends ActionDelegate implements IEditorActionDelegate
{

  private IEditorPart m_targetEditor;

  @Override
  public void setActiveEditor( final IAction action, final IEditorPart targetEditor )
  {
    m_targetEditor = targetEditor;
    action.setEnabled( m_targetEditor instanceof ChartEditor );

  }

  /**
   * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
   */
  @Override
  public void runWithEvent( final IAction action, final Event event )
  {
    try
    {
      final IHandlerService handlerService = (IHandlerService) m_targetEditor.getEditorSite().getService( IHandlerService.class );
      handlerService.executeCommand( "org.kalypso.chart.ui.commands.edit", event ); //$NON-NLS-1$
    }
    catch( final ExecutionException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch( final NotDefinedException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch( final NotEnabledException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch( final NotHandledException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
