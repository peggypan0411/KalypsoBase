package de.renew.workflow.connector.context;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandEvent;
import org.eclipse.core.commands.ICommandListener;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.progress.UIJob;

import de.renew.workflow.connector.WorkflowConnectorPlugin;

/**
 * TODO: this class exists twice.
 * 
 * @author Stefan Kurzbach
 */
public class GenericCommandActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate, IEditorActionDelegate, IObjectActionDelegate, IExecutableExtension, ICommandListener,
    IActionDelegate2
{
  private static final class UpdateActionbarsJob extends UIJob
  {
    private final IActionBars m_bars;

    private final String m_id;

    private final boolean m_state;

    public UpdateActionbarsJob( final String name, final IActionBars bars, final String id, final boolean state )
    {
      super( name );
      m_bars = bars;
      m_id = id;
      m_state = state;
    }

    @Override
    public IStatus runInUIThread( final IProgressMonitor monitor )
    {
      final IContributionManager toolBarManager = m_bars.getToolBarManager();
      final IContributionManager menuManager = m_bars.getMenuManager();

      final IContributionItem toolbarContribution = toolBarManager.find( m_id );
      if( toolbarContribution != null )
      {
        toolbarContribution.setVisible( m_state );
        toolBarManager.update( true );
      }
      final IContributionItem menuContribution = menuManager.find( m_id );
      if( menuContribution != null )
      {
        menuContribution.setVisible( m_state );
        menuManager.update( true );
      }

      m_bars.updateActionBars();

      return Status.OK_STATUS;
    }
  }

  private static final Object PARAM_COMMAND_ID = "commandId";

  ParameterizedCommand m_parameterizedCommand = null;

  private IHandlerService m_handlerService = null;

  private IAction m_action;

  private IActionBars m_actionBars;

  private String m_commandId;

  private Map<String, String> m_parameterMap;

  /**
   * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
   */
  @Override
  public void dispose( )
  {
    if( m_parameterizedCommand != null )
      m_parameterizedCommand.getCommand().removeCommandListener( this );

    m_handlerService = null;
    m_parameterizedCommand = null;
    m_action = null;
    m_actionBars = null;
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
   *      org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void selectionChanged( final IAction action, final ISelection selection )
  {
    // we don't care, handlers get their selection from the
    // ExecutionEvent application context
    m_action = action;
    updateActionState();
  }

  /**
   * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
   *      java.lang.String, java.lang.Object)
   */
  @Override
  @SuppressWarnings({ "unchecked", "unused" })
  public void setInitializationData( final IConfigurationElement config, final String propertyName, final Object data ) throws CoreException
  {
    if( data instanceof String )
    {
      m_commandId = (String) data;
    }
    if( data instanceof Map )
    {
      m_parameterMap = (Map<String, String>) data;
      m_commandId = m_parameterMap.get( PARAM_COMMAND_ID );
    }
  }

  /**
   * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
   */
  @Override
  public void init( final IWorkbenchWindow window )
  {
    if( m_handlerService != null )
    {
      // already initialized
      return;
    }

    final IWorkbench workbench = window.getWorkbench();
    m_handlerService = (IHandlerService) workbench.getService( IHandlerService.class );
    final ICommandService commandService = (ICommandService) workbench.getService( ICommandService.class );
    m_parameterizedCommand = createCommand( commandService );

    if( m_parameterizedCommand != null )
      m_parameterizedCommand.getCommand().addCommandListener( this );

    updateActionState();
  }

  /**
   * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
   */
  @Override
  public void init( final IViewPart view )
  {
    m_actionBars = view.getViewSite().getActionBars();
    init( view.getSite().getWorkbenchWindow() );
  }

  /**
   * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
   *      org.eclipse.ui.IEditorPart)
   */
  @Override
  public void setActiveEditor( final IAction action, final IEditorPart targetEditor )
  {
    if( targetEditor != null )
    {
      m_actionBars = targetEditor.getEditorSite().getActionBars();
      init( targetEditor.getSite().getWorkbenchWindow() );
    }

    m_action = action;
    updateActionState();
  }

  /**
   * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
   *      org.eclipse.ui.IWorkbenchPart)
   */
  @Override
  public void setActivePart( final IAction action, final IWorkbenchPart targetPart )
  {
    if( targetPart != null )
      init( targetPart.getSite().getWorkbenchWindow() );

    m_action = action;
    updateActionState();
  }

  /**
   * @see org.eclipse.core.commands.ICommandListener#commandChanged(org.eclipse.core.commands.CommandEvent)
   */
  @Override
  public void commandChanged( final CommandEvent commandEvent )
  {
    updateActionState();
  }

  /**
   * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
   */
  @Override
  public void init( final IAction action )
  {
    m_action = action;
    updateActionState();
  }

  /**
   * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
   */
  @Override
  public void runWithEvent( final IAction action, final Event event )
  {
    /* Do not run for unchecked radio-buttons */
    if( (action.getStyle() & IAction.AS_RADIO_BUTTON) != 0 && !action.isChecked() )
      return;

    if( m_handlerService == null )
    {
      return;
    }

    if( m_parameterizedCommand != null )
    {
      try
      {
        m_handlerService.executeCommand( m_parameterizedCommand, null );
      }
      catch( final Throwable e )
      {
        final Status status = new Status( IStatus.ERROR, WorkflowConnectorPlugin.PLUGIN_ID, SWT.OK, "Fehler", e );
        WorkflowConnectorPlugin.getDefault().getLog().log( status );
        ErrorDialog.openError( event.display.getActiveShell(), action.getText(), "Kommando konnte nicht ausgeführt werden.", status );
      }
    }
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  @Override
  public void run( final IAction action )
  {
    throw new UnsupportedOperationException();
  }

  protected ParameterizedCommand createCommand( final ICommandService commandService )
  {
    final Command command = commandService.getCommand( m_commandId );
    try
    {
      if( !command.isDefined() )
        command.define( m_commandId, m_commandId, commandService.getCategory( "de.renew.workflow.tasks.category" ) );

      final ArrayList<Parameterization> parameters = new ArrayList<Parameterization>();
      for( final String parmName : m_parameterMap.keySet() )
      {
        if( PARAM_COMMAND_ID.equals( parmName ) )
          continue;

        final IParameter parm = command.getParameter( parmName );
        if( parm == null )
        {
          // asking for a bogus parameter? No problem
          continue;
        }

        parameters.add( new Parameterization( parm, m_parameterMap.get( parmName ) ) );
      }
      return new ParameterizedCommand( command, parameters.toArray( new Parameterization[parameters.size()] ) );
    }
    catch( final NotDefinedException e )
    {
      e.printStackTrace();
    }
    return null;
  }

  private void updateActionState( )
  {
    if( m_action == null || m_actionBars == null )
      return;

    final Command command = m_parameterizedCommand == null ? null : m_parameterizedCommand.getCommand();

    final boolean enabledState = command != null ? command.isEnabled() : false;

    m_action.setEnabled( enabledState );

    final String actionId = m_action.getId();
    final IActionBars actionBars = m_actionBars;

    final UIJob job = new UpdateActionbarsJob( "Update Action-Bars", actionBars, actionId, enabledState );
    job.setPriority( Job.INTERACTIVE );
    job.schedule( 200 );
  }
}
