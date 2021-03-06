/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.featureview.views;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.kalypso.commons.command.DefaultCommandManager;
import org.kalypso.commons.command.ICommand;
import org.kalypso.contribs.eclipse.core.runtime.jobs.MutexRule;
import org.kalypso.contribs.eclipse.jface.viewers.SelectionProviderAdapter;
import org.kalypso.contribs.eclipse.swt.custom.ScrolledCompositeCreator;
import org.kalypso.contribs.eclipse.ui.partlistener.PartAdapter2;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.featureview.KalypsoFeatureViewPlugin;
import org.kalypso.featureview.i18n.Messages;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.ogc.gml.featureview.IFeatureChangeListener;
import org.kalypso.ogc.gml.featureview.control.FeatureComposite;
import org.kalypso.ogc.gml.featureview.maker.CachedFeatureviewFactory;
import org.kalypso.ogc.gml.featureview.maker.FeatureviewHelper;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.gml.selection.EasyFeatureWrapper;
import org.kalypso.ogc.gml.selection.FeatureSelectionManager2;
import org.kalypso.ogc.gml.selection.IFeatureSelection;
import org.kalypso.template.featureview.FeatureviewType;
import org.kalypso.util.command.JobExclusiveCommandTarget;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.event.ModellEvent;
import org.kalypsodeegree.model.feature.event.ModellEventListener;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;

/**
 * The Feature View shows a single feature as a form.
 * <p>
 * The view analyses the currently selection, and shows the first element which is a {@link org.kalypsodeegree.model.feature.Feature}.
 * <p>
 * <p>
 * Features:
 * </p>
 * <ul>
 * <li>Shows the current selected feature in either a view or an editor. The latter can only be recieved, if the editor adapts {@link org.eclipse.jface.viewers.ISelectionProvider} or {@linked
 * org.eclipse.jface.viewers.IPostSelectionProvider}.</li>
 * <li>In preference, the view listens to post-selections, in order to change the shown feature not too often.</li>
 * <li>If the returned selection is a {@link org.kalypso.ogc.gml.selection.IFeatureSelection}, changes (i.e. made by the user) in the feature-control will be immediately postet into the given
 * {@link org.kalypso.ogc.gml.mapmodel.CommandableWorkspace}.</li>
 * <li>If the current selection changes to something not viewable, the last shown feature continues to be shown. If the source of the shown feature is closed, the view releases the feature.</li>
 * <li>While the view is updated (a new feature was selected),</li>
 * <li>If the feature changes (either by editing it inside the view or from outside), the view will redisplay the new content.</li>
 * <li></li>
 * </ul>
 * TODO 's:
 * <ul>
 * <li>Save data: ceate a global action handler to save gml-data. Every workbench part which changes a GmlWorkspace, including this view, should register for this handler.</li>
 * <li>Are there any global (feature-)actions which apply to this view? If yes, it may be a good idea to implement ISelectionProvider</li>
 * <li></li>
 * </ul>
 * 
 * @see org.eclipse.jface.viewers.IPostSelectionProvider
 */
public class FeatureView extends ViewPart implements ModellEventListener
{
  public static final String ID = "org.kalypso.featureview.views.FeatureView"; //$NON-NLS-1$

  static final String _KEIN_FEATURE_SELEKTIERT_ = Messages.getString( "org.kalypso.featureview.views.FeatureView.1" ); //$NON-NLS-1$

  /**
   * Settings constant for section name (value <code>FeatureView</code>).
   */
  private static final String STORE_SECTION = "FeatureView"; //$NON-NLS-1$

  /**
   * Settings constant for show tables (value <code>FeatureView.STORE_SHOW_TABLES</code>).
   */
  private static final String STORE_SHOW_TABLES = "FeatureView.STORE_SHOW_TABLES"; //$NON-NLS-1$

  /**
   * Settings constant for show green hook at validation ok (value <code>FeatureView.STORE_SHOW_VALIDATION_OK</code>).
   */
  private static final String STORE_SHOW_VALIDATION_OK = "FeatureView.STORE_SHOW_VALIDATION_OK"; //$NON-NLS-1$

  private final IFeatureChangeListener m_fcl = new IFeatureChangeListener()
  {
    @Override
    public void featureChanged( final ICommand changeCommand )
    {
      m_target.setCommandManager( m_commandManager );
      m_target.postCommand( changeCommand, null );
    }

    @Override
    public void openFeatureRequested( final Feature feature, final IPropertyType ftp )
    {
      // just show this feature in the view, don't change the selection this doesn't work
      // don't change the command manager, changing the feature only work inside the same workspace
      activateFeature( feature, false );
    }
  };

  private final FeatureviewHelper m_fvFactory = new FeatureviewHelper();

  private final CachedFeatureviewFactory m_cfvFactory = new CachedFeatureviewFactory( m_fvFactory );

  protected final FeatureComposite m_featureComposite = new FeatureComposite( null, KalypsoCorePlugin.getDefault().getSelectionManager(), m_cfvFactory );

  protected final JobExclusiveCommandTarget m_target = new JobExclusiveCommandTarget( new DefaultCommandManager(), null );

  protected CommandableWorkspace m_commandManager = null;

  private Group m_mainGroup;

  /** Recreates the Feature-Composite inside a scrollable container. */
  private final ScrolledCompositeCreator m_creator = new ScrolledCompositeCreator( null )
  {
    @Override
    protected Control createContents( final Composite scrollParent, final int style )
    {
      return m_featureComposite.createControl( scrollParent, style );
    }
  };

  private final ISchedulingRule m_mutextRule = new MutexRule();

  private final ISelectionListener m_selectionListener = new INullSelectionListener()
  {
    @Override
    public void selectionChanged( final IWorkbenchPart part, final ISelection selection )
    {
      // controls of my feature composite may create events, don't react
      if( part != null && part != FeatureView.this && selection instanceof IFeatureSelection )
        FeatureView.this.handleSelectionChanged( part, selection );
    }
  };

  private final IPartListener2 m_partListener = new PartAdapter2()
  {
    @Override
    public void partClosed( final IWorkbenchPartReference partRef )
    {
      handlePartClosed( partRef.getPart( false ) );
    }
  };

  private final UIJob m_updateControlJob = new UIJob( "Update feature control" ) //$NON-NLS-1$
  {
    @Override
    public IStatus runInUIThread( final IProgressMonitor monitor )
    {
      doUpdateFeatureControl();
      return Status.OK_STATUS;
    }
  };

  private IDialogSettings m_settings;

  private Action m_showTablesAction = null;

  private Action m_showValidationOkAction = null;

  private FormToolkit m_toolkit;

  /**
   * The part from which the last selection was retrieved.
   */
  private IWorkbenchPart m_selectionSourcePart;

  final ISelectionProvider m_selectionProvider = new SelectionProviderAdapter();

  public FeatureView( )
  {
    final IDialogSettings viewsSettings = KalypsoFeatureViewPlugin.getDefault().getDialogSettings();

    m_updateControlJob.setUser( false );
    m_updateControlJob.setSystem( true );

    m_settings = viewsSettings.getSection( STORE_SECTION );
    if( m_settings == null )
    {
      m_settings = viewsSettings.addNewSection( STORE_SECTION );
      // set default values
      m_settings.put( STORE_SHOW_TABLES, true );
      m_settings.put( STORE_SHOW_VALIDATION_OK, false );
    }

    m_fvFactory.setShowTables( m_settings.getBoolean( STORE_SHOW_TABLES ) );
    m_featureComposite.setShowOk( m_settings.getBoolean( STORE_SHOW_VALIDATION_OK ) );
    m_cfvFactory.reset();
  }

  @Override
  public void init( final IViewSite site ) throws PartInitException
  {
    super.init( site );

    final IWorkbenchPage page = site.getPage();
    page.getWorkbenchWindow().getSelectionService().addPostSelectionListener( m_selectionListener );
    page.addPartListener( m_partListener );

    site.setSelectionProvider( m_selectionProvider );
  }

  @Override
  public void dispose( )
  {
    activateFeature( null, false ); // unhook listeners
    m_featureComposite.dispose();

    final IWorkbenchPartSite site = getSite();

    site.setSelectionProvider( null );

    final IWorkbenchPage page = site.getPage();
    page.removePartListener( m_partListener );
    page.getWorkbenchWindow().getSelectionService().removePostSelectionListener( m_selectionListener );
  }

  public void setShowTables( final boolean showTables )
  {
    final Feature currentFeature = getCurrentFeature();

    m_fvFactory.setShowTables( showTables );
    m_cfvFactory.reset();

    m_settings.put( STORE_SHOW_TABLES, showTables );

    activateFeature( currentFeature, true );
  }

  public boolean isShowTables( )
  {
    return m_fvFactory.isShowTables();
  }

  public void setShowOk( final boolean showOk )
  {
    final Feature currentFeature = getCurrentFeature();

    m_featureComposite.setShowOk( showOk );
    m_cfvFactory.reset();

    m_settings.put( STORE_SHOW_VALIDATION_OK, showOk );

    activateFeature( currentFeature, true );
  }

  public boolean isShowOk( )
  {
    return m_featureComposite.isShowOk();
  }

  protected void handleSelectionChanged( final IWorkbenchPart part, final ISelection selection )
  {
    m_selectionSourcePart = part;

    if( selection instanceof IFeatureSelection )
    {
      final IFeatureSelection featureSel = (IFeatureSelection)selection;

      final Feature feature = featureFromSelection( featureSel );
      m_commandManager = featureSel.getWorkspace( feature );
      activateFeature( feature, false );
    }
    else
    {
      m_commandManager = null;
      activateFeature( null, false );
    }
  }

  protected void handlePartClosed( final IWorkbenchPart part )
  {
    if( part == m_selectionSourcePart )
      FeatureView.this.handleSelectionChanged( null, StructuredSelection.EMPTY );
  }

  private Feature featureFromSelection( final IFeatureSelection featureSel )
  {
    final Feature focusedFeature = featureSel.getFocusedFeature();
    if( focusedFeature != null )
      return focusedFeature;

    for( final Iterator< ? > sIt = featureSel.iterator(); sIt.hasNext(); )
    {
      final Object object = sIt.next();
      if( object instanceof Feature )
        return (Feature)object;
    }
    return null;
  }

  @Override
  public void createPartControl( final Composite parent )
  {
    // Comment this in, if you want to use the FormToolkit for FeatureView.
    m_featureComposite.setFormToolkit( m_toolkit );

    m_mainGroup = new Group( parent, SWT.NONE );
    m_mainGroup.setLayout( new GridLayout() );
    m_mainGroup.setText( _KEIN_FEATURE_SELEKTIERT_ );

    /* If a toolkit is set, use it for the main group. */
    if( m_toolkit != null )
      m_toolkit.adapt( m_mainGroup, true, true );

    m_featureComposite.addChangeListener( m_fcl );

    activateFeature( null, false );

    // add showTables-Action to menu-bar
    // we do this here, because adding it via the org.eclipse.ui.viewActions extension-point
    // does not allow to set the checked state dynamically
    m_showTablesAction = new Action( Messages.getString( "org.kalypso.featureview.views.FeatureView.2" ), IAction.AS_CHECK_BOX ) //$NON-NLS-1$
    {
      /**
       * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
       */
      @Override
      public void runWithEvent( final Event event )
      {
        setShowTables( isChecked() );
      }
    };
    m_showTablesAction.setChecked( isShowTables() );

    m_showValidationOkAction = new Action( Messages.getString( "org.kalypso.featureview.views.FeatureView.3" ), IAction.AS_CHECK_BOX ) //$NON-NLS-1$
    {
      /**
       * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
       */
      @Override
      public void runWithEvent( final Event event )
      {
        setShowOk( isChecked() );
      }
    };
    m_showValidationOkAction.setChecked( isShowOk() );

    final IViewSite viewSite = getViewSite();
    viewSite.getActionBars().getMenuManager().add( m_showTablesAction );
    viewSite.getActionBars().getMenuManager().add( m_showValidationOkAction );

    final IWorkbenchWindow workbenchWindow = viewSite.getWorkbenchWindow();
    final IWorkbenchPage activePage = workbenchWindow.getActivePage();
    final ISelection selection = workbenchWindow.getSelectionService().getSelection();
    final IWorkbenchPart activePart = activePage == null ? null : activePage.getActivePart();
    handleSelectionChanged( activePart, selection );
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus( )
  {
    m_mainGroup.setFocus();
  }

  /**
   * @param force
   *          if true, always reset this view, else, only if feature has really changed.
   */
  protected void activateFeature( final Feature feature, final boolean force )
  {
    final Group mainGroup = m_mainGroup;
    final ScrolledCompositeCreator creator = m_creator;

    final Job job = new UIJob( getSite().getShell().getDisplay(), Messages.getString( "org.kalypso.featureview.views.FeatureView.4" ) ) //$NON-NLS-1$
    {
      @Override
      public IStatus runInUIThread( final IProgressMonitor monitor )
      {
        final Feature oldFeature = m_featureComposite.getFeature();
        final GMLWorkspace oldWorkspace = oldFeature == null ? null : oldFeature.getWorkspace();
        final GMLWorkspace workspace = feature == null ? null : feature.getWorkspace();

        if( !force && oldWorkspace == workspace && oldFeature == feature )
          return Status.OK_STATUS;

        if( oldWorkspace != null )
        {
          oldWorkspace.removeModellListener( FeatureView.this );
          // TODO: WHY?
          // getSite().setSelectionProvider( null );
        }
        try
        {
          if( m_featureComposite != null )
            m_featureComposite.disposeControl();
          final Control scroller = creator.getScrolledComposite();
          if( scroller != null )
            scroller.dispose();
        }
        catch( final Exception e )
        {
          // TODO: set real log output
          System.out.println( "e: " + e ); //$NON-NLS-1$
        }
        if( m_featureComposite == null )
          return Status.OK_STATUS;

        m_featureComposite.setFeature( feature );

        final String groupLabel;
        if( workspace != null && feature != null && mainGroup != null && !mainGroup.isDisposed() )
        {
          workspace.addModellListener( FeatureView.this );

          creator.createControl( mainGroup, SWT.V_SCROLL, SWT.NONE );

          final FormToolkit toolkit = getToolkit();

          final Control contentControl = creator.getContentControl();

          final ScrolledComposite scrolledComposite = creator.getScrolledComposite();
          scrolledComposite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

          /* If a toolkit is set, use it. */
          if( toolkit != null )
          {
            toolkit.adapt( contentControl, true, true );
            toolkit.adapt( scrolledComposite, true, true );
          }

          groupLabel = FeatureHelper.getAnnotationValue( feature, IAnnotation.ANNO_LABEL );
        }
        else
          groupLabel = _KEIN_FEATURE_SELEKTIERT_;

        if( mainGroup != null && !mainGroup.isDisposed() )
        {
          mainGroup.setText( groupLabel );
          mainGroup.layout();

          // TRICKY: fixes the bug, that after calling 'Edit feature' from the tree,
          // the tree has still the focus but this part is active (so you could change
          // the seleciton in the tree, but we could not react to it).
          // The reason for this was this job, which causes the gui to be created after the call
          // to 'setFocus'.
          if( FeatureView.this == getViewSite().getPage().getActivePart() )
            mainGroup.setFocus();
        }

        if( feature == null || m_commandManager == null )
          m_selectionProvider.setSelection( StructuredSelection.EMPTY );
        else
        {
          final FeatureSelectionManager2 featureSelection = new FeatureSelectionManager2();
          featureSelection.setSelection( new EasyFeatureWrapper[] { new EasyFeatureWrapper( m_commandManager, feature ) } );
          m_selectionProvider.setSelection( featureSelection );
        }

        return Status.OK_STATUS;
      }
    };

    job.setRule( m_mutextRule );
    job.setUser( true );

    // This is the way to do it, but still blocks the user interface.
    // If this is still too slow, split the job into ui and non-ui
    // parts and run the non-ui part in a non-blocking thread.
    // NOTE: running via IProgressService#runInUi gives a
    // ugly (and slow) modal pop-ups everytime the user changes the selection
    job.schedule();

    // this is the official way to do it
    // but gives no response (busy-cursor) to the user
    // final IWorkbenchSiteProgressService siteService = (IWorkbenchSiteProgressService)getSite().getAdapter(
    // IWorkbenchSiteProgressService.class );
    // siteService.schedule( job, 0 /* now */, true /* use half-busy cursor in part */);
  }

  @Override
  public void onModellChange( final ModellEvent modellEvent )
  {
    // TODO: why doesn't the feature composite itself is a modelllistener and reacts to the changes?
    if( modellEvent.isType( ModellEvent.FEATURE_CHANGE ) )
      startUpdateFeatureControl();
  }

  private void startUpdateFeatureControl( )
  {
    final Group mainGroup = m_mainGroup;
    final Control control = m_featureComposite.getControl();

    if( mainGroup == null || mainGroup.isDisposed() || control == null || control.isDisposed() )
      return;

    // REMARK: in order to protect against too many changes, delay the update at this point
    m_updateControlJob.cancel();
    m_updateControlJob.schedule( 50 );
  }

  protected void doUpdateFeatureControl( )
  {
    final Control control = m_featureComposite.getControl();

    if( m_mainGroup == null || m_mainGroup.isDisposed() || control == null || control.isDisposed() )
      return;

    // As the label of the main group may depend on the values of the feature, we must update it as well.
    final Feature feature = m_featureComposite.getFeature();
    final String groupLabel = feature == null ? _KEIN_FEATURE_SELEKTIERT_ : FeatureHelper.getAnnotationValue( feature, IAnnotation.ANNO_LABEL );
    m_mainGroup.setText( groupLabel );

    m_featureComposite.updateControl();
  }

  public GMLWorkspace getCurrentWorkspace( )
  {
    final Feature feature = m_featureComposite.getFeature();
    return feature == null ? null : feature.getWorkspace();
  }

  public Feature getCurrentFeature( )
  {
    return m_featureComposite.getFeature();
  }

  /** Returns the view template of the current k composite and its children. */
  public FeatureviewType[] getCurrentViewTemplates( )
  {
    final Feature feature = getCurrentFeature();
    if( feature == null )
      return null;

    final Set<FeatureviewType> types = new HashSet<>( 5 );
    m_featureComposite.collectViewTypes( types );

    return types.toArray( new FeatureviewType[types.size()] );
  }

  public FormToolkit getToolkit( )
  {
    return m_toolkit;
  }

  public void setToolkit( final FormToolkit toolkit )
  {
    m_toolkit = toolkit;
  }

  public CachedFeatureviewFactory getCachedFeatureViewFactory( )
  {
    return m_cfvFactory;
  }
}