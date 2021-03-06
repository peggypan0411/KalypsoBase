package org.kalypso.ogc.gml.map.widgets;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.kalypso.contribs.eclipse.core.commands.HandlerUtils;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.map.handlers.MapHandlerUtils;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.mapmodel.MapModellHelper;
import org.kalypso.ogc.gml.widgets.IWidget;
import org.kalypso.ui.KalypsoGisPlugin;
import org.kalypso.ui.editor.mapeditor.AbstractMapPart;
import org.kalypso.ui.internal.i18n.Messages;
import org.osgi.framework.Bundle;

/**
 * @author Stefan Kurzbach
 */
public class SelectWidgetHandler extends AbstractHandler implements IElementUpdater, IExecutableExtension
{
  public static final String COMMAND_ID = "org.kalypso.ogc.gml.map.widgets.SelectWidgetCommand"; //$NON-NLS-1$

  public static final String PARAM_CONTEXT = COMMAND_ID + ".context"; //$NON-NLS-1$

  public static final String PARAM_WIDGET_CLASS = COMMAND_ID + ".widget"; //$NON-NLS-1$

  public static final String PARAM_PLUGIN_ID = COMMAND_ID + ".plugin"; //$NON-NLS-1$

  public static final String PARAM_WIDGET_ICON = COMMAND_ID + ".icon"; //$NON-NLS-1$

  private static final Object PARAM_WIDGET_TOOLTIP = COMMAND_ID + ".tooltip"; //$NON-NLS-1$

  private String m_widgetClassFromExtension;

  private String m_pluginIdFromExtension;

  private String m_widgetIconFromExtension;

  private String m_widgetTooltipFromExtension;

  /**
   * The map of all parameter. May be null.
   */
  private Map< ? , ? > m_parameter;

  @Override
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IEvaluationContext applicationContext = (IEvaluationContext)event.getApplicationContext();
    applicationContext.getVariable( ISources.ACTIVE_EDITOR_ID_NAME );

    final String widgetFromEvent = event.getParameter( PARAM_WIDGET_CLASS );
    final String widgetParameter;
    if( widgetFromEvent != null )
      widgetParameter = widgetFromEvent;
    else
      widgetParameter = m_widgetClassFromExtension;

    final String pluginFromEvent = event.getParameter( PARAM_PLUGIN_ID );
    final String pluginParameter;
    if( pluginFromEvent != null )
      pluginParameter = pluginFromEvent;
    else
      pluginParameter = m_pluginIdFromExtension;

    final IWidget widget = getWidgetFromBundle( pluginParameter, widgetParameter );
    if( widget == null )
    {
      final String msg = Messages.getString( "org.kalypso.ogc.gml.map.widgets.SelectWidgetHandler.6", pluginParameter, widgetParameter ); //$NON-NLS-1$
      final IStatus status = new Status( IStatus.WARNING, KalypsoGisPlugin.getId(), msg );
      KalypsoGisPlugin.getDefault().getLog().log( status );
      return status;
    }

    /* Set the map of all parameter, in case there are additional parameter. */
    widget.setParameter( (Map<String, String>)m_parameter );

    final IMapPanel mapPanel = MapHandlerUtils.getMapPanelChecked( applicationContext );
    if( mapPanel == null )
      return new Status( IStatus.WARNING, KalypsoGisPlugin.PLUGIN_ID, Messages.getString( "org.kalypso.ogc.gml.map.widgets.SelectWidgetHandler.7" ), new IllegalStateException() ); //$NON-NLS-1$

    if( HandlerUtils.isDeselected( event ) )
    {
      mapPanel.getWidgetManager().removeWidget( widget );

      return null;
    }

    final Shell shell = HandlerUtil.getActiveShellChecked( event );

    /* Always make sure that the map was fully loaded */
    // REMARK: we first test directly, without ui-operation, in order to enhance performance if the map already is open.
    final IMapModell model = mapPanel.getMapModell();
    if( !MapModellHelper.isMapLoaded( model ) )
    {
      if( !MapModellHelper.waitForAndErrorDialog( shell, mapPanel, "", "" ) ) //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }

    // DIRTY: for some applications, we need to wait for the map to be really loaded,
    // so there is this scheduling rule...
    // It can only be obtained from the real workebench-part
    // We also need the part to get its page to open the Widget-Option-Part;
    // but we do not have a page in all contexts....
    final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow( event );
    final AbstractMapPart mapPart = findMapPart( window );
    final IWorkbenchPage page = mapPart == null ? null : mapPart.getSite().getPage();

    final UIJob job = new ActivateWidgetJob( Messages.getString( "org.kalypso.ogc.gml.map.widgets.SelectWidgetHandler.10" ), widget, mapPanel, page ); //$NON-NLS-1$

    if( mapPart != null )
      job.setRule( mapPart.getSchedulingRule().getSelectWidgetSchedulingRule() );

    job.schedule();

    return null;
  }

  @Override
  public void updateElement( final UIElement element, final Map parameters )
  {
    // TODO: icon and tooltip should only be set once, but updateElement is called often
    // Is this icon/tooltip stuff still in use?
    if( m_widgetIconFromExtension != null )
    {
      final ImageDescriptor iconFromBundle = getIconFromBundle( m_pluginIdFromExtension, m_widgetIconFromExtension );
      element.setIcon( iconFromBundle );
    }

    if( m_widgetTooltipFromExtension != null )
      element.setTooltip( m_widgetTooltipFromExtension );

    final IHandlerService handlerService = (IHandlerService)element.getServiceLocator().getService( IHandlerService.class );
    final IEvaluationContext context = handlerService.getCurrentState();
    final IMapPanel mapPanel = MapHandlerUtils.getMapPanel( context );
    if( mapPanel != null )
    {
      final IWidget[] widgets = mapPanel.getWidgetManager().getWidgets();
      if( ArrayUtils.isEmpty( widgets ) )
        element.setChecked( false );
      else
      {
        element.setChecked( false );

        for( final IWidget widget : widgets )
        {
          // SelectWidgetHandler ist mehrfach vorhanden, daher muss explizit auf die Klasse des Widgets gepr�ft werden.
          final String actualWidgetClass = widget.getClass().getName();
          if( actualWidgetClass.equals( m_widgetClassFromExtension ) )
            element.setChecked( true );
        }
      }
    }
  }

  @Override
  public void setInitializationData( final IConfigurationElement config, final String propertyName, final Object data )
  {
    if( data != null && data instanceof Map< ? , ? > )
    {
      final Map< ? , ? > parameterMap = (Map< ? , ? >)data;
      m_pluginIdFromExtension = (String)parameterMap.get( PARAM_PLUGIN_ID );
      m_widgetClassFromExtension = (String)parameterMap.get( PARAM_WIDGET_CLASS );
      m_widgetIconFromExtension = (String)parameterMap.get( PARAM_WIDGET_ICON );
      m_widgetTooltipFromExtension = (String)parameterMap.get( PARAM_WIDGET_TOOLTIP );

      /* Store the map of all parameter, in case there are additional parameter. */
      m_parameter = parameterMap;
    }

    if( m_pluginIdFromExtension == null )
      m_pluginIdFromExtension = config.getContributor().getName();
  }

  /**
   * Search for a workbench part in the active page of the given window that is an {@link AbstractMapPart}.<br>
   * Search order is: active part, view parts, editor parts.
   */
  private AbstractMapPart findMapPart( final IWorkbenchWindow window )
  {
    if( window == null )
      return null;

    final IWorkbenchPage page = window.getActivePage();
    if( page == null )
      return null;

    final IWorkbenchPart activePart = page.getActivePart();
    if( activePart instanceof AbstractMapPart )
      return (AbstractMapPart)activePart;

    final IViewReference[] viewReferences = page.getViewReferences();
    for( final IViewReference viewReference : viewReferences )
    {
      final IWorkbenchPart part = viewReference.getView( false );
      if( part instanceof AbstractMapPart )
        return (AbstractMapPart)part;
    }

    final IEditorReference[] editorReferences = page.getEditorReferences();
    for( final IEditorReference editorReference : editorReferences )
    {
      final IWorkbenchPart part = editorReference.getEditor( false );
      if( part instanceof AbstractMapPart )
        return (AbstractMapPart)part;
    }

    return null;
  }

  private ImageDescriptor getIconFromBundle( final String pluginId, final String imageFilePath )
  {
    return AbstractUIPlugin.imageDescriptorFromPlugin( pluginId, imageFilePath );
  }

  private IWidget getWidgetFromBundle( final String pluginId, final String widgetName )
  {
    try
    {
      final Bundle bundle = Platform.getBundle( pluginId );
      final Class<IWidget> widgetClass = (Class<IWidget>)bundle.loadClass( widgetName );
      return widgetClass.newInstance();
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
    return null;
  }
}