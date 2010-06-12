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
package org.kalypso.contribs.eclipse.jface.wizard.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer2;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.kalypso.contribs.eclipse.i18n.Messages;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.contribs.eclipse.jface.operation.RunnableContextHelper;
import org.kalypso.contribs.eclipse.jface.wizard.IResetableWizard;
import org.kalypso.contribs.java.lang.CatchRunnable;
import org.kalypso.contribs.java.lang.DisposeHelper;

/**
 * A {@link org.eclipse.ui.IViewPart}which is a wizard container.
 * <p>
 * The {@link org.eclipse.jface.wizard.IWizard}must be set from the outside.
 * </p>
 * <p>
 * Lots of the code was taken from {@link org.eclipse.jface.wizard.WizardDialog}.
 * </p>
 * 
 * @author belger
 */
public class WizardView extends ViewPart implements IWizardContainer2, IWizardChangeProvider, IPageChangeProvider
{
  public static final int SAVE_ID = IDialogConstants.CLIENT_ID + 1;

  public static final int RESET_ID = IDialogConstants.CLIENT_ID + 2;

  private RGB m_defaultTitleBackground;

  private RGB m_defaultTitleForeground;

  private final String m_foregroundRGB_ID = "" + this + ".title.foreground"; //$NON-NLS-1$ //$NON-NLS-2$

  private final String m_backgroundRGB_ID = "" + this + ".title.background"; //$NON-NLS-1$ //$NON-NLS-2$

  private final List<IWizardContainerListener> m_listeners = new ArrayList<IWizardContainerListener>( 5 );

  private IWizard m_wizard;

  private IWizardPage m_currentPage;

  private Composite m_pageContainer;

  private static final StackLayout m_stackLayout = new StackLayout();

  /** Alles ausser title */
  private Composite m_workArea;

  /** Right side of sash form, will be recreated on every {@link #setWizard(IWizard, int)} */
  private Composite m_pageAndButtonArea;

  private final Map<Integer, Button> m_buttons = new HashMap<Integer, Button>( 10 );

  private FontMetrics m_fontMetrics;

  private Browser m_browser;

  private SashForm m_mainSash;

  private boolean m_isMovingToPreviousPage = false;

  private boolean m_useNormalBackground = false;

  private boolean m_backJumpsToLastVisited = true;

  private final Map<Integer, String> m_buttonLabels = new HashMap<Integer, String>();

  /** Lock for preventing call to changeLocation when url is set internally */
  private boolean m_ignoreNextCangeLocation = false;

  private final ListenerList pageChangedListeners = new ListenerList();

  private final ListenerList pageChangingListeners = new ListenerList();

  private boolean m_useDefaultButton = true;

  /** If set to true, the background color of error messages is the same as normal messages. */
  public void setErrorBackgroundBehaviour( final boolean useNormalBackground )
  {
    m_useNormalBackground = useNormalBackground;
  }

  /**
   * Determines the behaviour of the back button.
   * <ul>
   * <li>if true, back jumps to the last visited page (default behaviour)</li>
   * <li>if false, back jumps to the previous page given by the {@link IWizard}</li>
   * </ul>
   */
  public void setBackJumpsToLastVisited( final boolean backJumpsToLastVisited )
  {
    m_backJumpsToLastVisited = backJumpsToLastVisited;
  }

  /**
   * Overwrites the default label for the given button (-id).
   * <p>
   * Must be called before the buttons are created
   * </p>
   */
  public void setButtonLabel( final int buttonID, final String label )
  {
    m_buttonLabels.put( new Integer( buttonID ), label );
  }

  /** Sets an new wizard and immediately displays its first page */
  public void setWizard( final IWizard wizard )
  {
    setWizard( wizard, IWizardContainerListener.REASON_NONE );
  }

  /** Sets an new wizard and immediately displays its first page */
  private void setWizard( final IWizard wizard, final int reason )
  {
    // clean wizard
    if( m_wizard != null )
    {
      // IMPORTANT: set the image to null, because it probably gets disposed
      // by m_wizard.dispose();
      setWizardTitleImage( null );

      m_wizard.setContainer( null );
      m_wizard.dispose();
      m_wizard = null;
    }

    // clean components
    if( m_pageAndButtonArea != null && !m_pageAndButtonArea.isDisposed() )
    {
      final Control[] children = m_pageAndButtonArea.getChildren();
      for( final Control element : children )
        element.dispose();
    }

    // first time set this wizard
    boolean doLayout = false;
    if( wizard != null && wizard != m_wizard )
    {
      wizard.setContainer( this );
      wizard.addPages();
      doLayout = true;
    }

    m_wizard = wizard;

    if( m_wizard == null )
    {
      // HACK: only set the message, if at least one message was set. Else we may destroy the background color
      // of the message label
      if( normalMsgAreaBackground != null )
        setErrorMessage( Messages.getString( "org.kalypso.contribs.eclipse.jface.wizard.view.WizardView0" ) ); //$NON-NLS-1$
    }
    else
    {
      createRightPanel( m_pageAndButtonArea );

      int initialBrowserSize = 25;
      if( m_wizard instanceof IWizard2 )
      {
        final int wizardInitialBrowserSize = ((IWizard2) m_wizard).getInitialBrowserSize();
        // force into [5, 95]
        initialBrowserSize = Math.min( 95, Math.max( 5, wizardInitialBrowserSize ) );

        m_useDefaultButton = ((IWizard2) m_wizard).useDefaultButton();
        if( !m_useDefaultButton )
          getShell().setDefaultButton( null );
      }
      else
        m_useDefaultButton = true;

      m_mainSash.setWeights( new int[] { initialBrowserSize, 100 - initialBrowserSize } );
    }

    fireWizardChanged( wizard, reason );

    if( m_wizard != null )
      showStartingPage();

    if( m_workArea != null && !m_workArea.isDisposed() && doLayout )
    {
      // WORKAROUND: no layout() or redraw() on any of the involved composites
      // causes a real repaint -> panel stays empty after wizard is newly set.
      // So we minimize maximize the sashform (backdraw, it flickers!)
      final Control maximizedControl = m_mainSash.getMaximizedControl();
      m_mainSash.setMaximizedControl( null );
      m_mainSash.setMaximizedControl( maximizedControl );
    }
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#dispose()
   */
  @Override
  public void dispose( )
  {
    setWizard( null );

    m_listeners.clear();
    pageChangedListeners.clear();
    pageChangingListeners.clear();

    m_disposeHelper.dispose();
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl( final Composite parent )
  {
    // initialize the dialog units
    initializeDialogUnits( parent );

    final FormLayout layout = new FormLayout();
    parent.setLayout( layout );
    final FormData data = new FormData();
    data.top = new FormAttachment( 0, 0 );
    data.bottom = new FormAttachment( 100, 0 );
    parent.setLayoutData( data );

    // Now create a work area for the rest of the dialog
    m_workArea = new Composite( parent, SWT.NULL );
    final GridLayout workLayout = new GridLayout();
    workLayout.marginHeight = 0;
    workLayout.marginWidth = 0;
    workLayout.verticalSpacing = 0;
    m_workArea.setLayout( workLayout );

    m_workArea.setBackground( parent.getDisplay().getSystemColor( SWT.COLOR_RED ) );

    final Control top = createTitleArea( parent );
    resetWorkAreaAttachments( top );

    m_workArea.setFont( JFaceResources.getDialogFont() );

    // initialize the dialog units
    initializeDialogUnits( m_workArea );

    final Label titleBarSeparator = new Label( m_workArea, SWT.HORIZONTAL | SWT.SEPARATOR );
    titleBarSeparator.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

    m_mainSash = new SashForm( m_workArea, SWT.HORIZONTAL );
    m_mainSash.setFont( m_workArea.getFont() );
    m_mainSash.setLayoutData( new GridData( GridData.FILL_BOTH ) );

    // Browser: to the left
    // Register a context menu on it, so we suppress the ugly explorer menu
    final Composite browserPanel = new Composite( m_mainSash, SWT.BORDER );
    final GridLayout browserPanelLayout = new GridLayout();
    browserPanelLayout.marginHeight = 0;
    browserPanelLayout.marginWidth = 0;
    browserPanel.setLayout( browserPanelLayout );

    m_browser = new Browser( browserPanel, SWT.NONE );
    m_browser.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    final MenuManager menuManager = new MenuManager( "#PopupMenu" ); //$NON-NLS-1$
    menuManager.setRemoveAllWhenShown( true );
    final Menu contextMenu = menuManager.createContextMenu( m_browser );
    m_browser.setMenu( contextMenu );
    getSite().registerContextMenu( menuManager, getSite().getSelectionProvider() );
    m_browser.addLocationListener( new LocationAdapter()
    {
      /**
       * @see org.eclipse.swt.browser.LocationAdapter#changed(org.eclipse.swt.browser.LocationEvent)
       */
      @Override
      public void changed( final LocationEvent event )
      {
        changeLocation( event.location );
      }
    } );

    m_pageAndButtonArea = new Composite( m_mainSash, SWT.NONE );
    final GridLayout pageAndButtonLayout = new GridLayout();
    pageAndButtonLayout.marginHeight = 0;
    pageAndButtonLayout.marginWidth = 0;
    pageAndButtonLayout.horizontalSpacing = 0;
    pageAndButtonLayout.verticalSpacing = 0;
    m_pageAndButtonArea.setLayout( pageAndButtonLayout );
    m_pageAndButtonArea.setFont( m_mainSash.getFont() );

    setWizard( m_wizard );
  }

  private void createRightPanel( final Composite parent )
  {
    m_pageContainer = new Composite( parent, SWT.NONE );
    m_pageContainer.setLayout( m_stackLayout );
    m_pageContainer.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    m_pageContainer.setFont( parent.getFont() );

    // // Allow the wizard pages to precreate their page controls
    // createPageControls();

    final Label label = new Label( parent, SWT.HORIZONTAL | SWT.SEPARATOR );
    label.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

    createButtonBar( parent );

    m_workArea.layout();
  }

  /**
   * Creates and returns the contents of this dialog's button bar.
   * <p>
   * The <code>Dialog</code> implementation of this framework method lays out a button bar and calls the
   * <code>createButtonsForButtonBar</code> framework method to populate it. Subclasses may override.
   * </p>
   * <p>
   * The returned control's layout data must be an instance of <code>GridData</code>.
   * </p>
   * 
   * @param parent
   *          the parent composite to contain the button bar
   * @return the button bar control
   */
  protected Control createButtonBar( final Composite parent )
  {
    final Composite composite = new Composite( parent, SWT.NONE );

    // create a layout with spacing and margins appropriate for the font
    // size.
    final GridLayout layout = new GridLayout();
    layout.numColumns = 0; // this is incremented by createButton
    layout.makeColumnsEqualWidth = false;
    layout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
    layout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
    layout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
    layout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
    composite.setLayout( layout );

    final GridData data = new GridData( GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER );
    composite.setLayoutData( data );
    composite.setFont( parent.getFont() );

    // Add the buttons to the button bar.
    createButtonsForButtonBar( composite );

    return composite;
  }

  protected void createButtonsForButtonBar( final Composite parent )
  {
    // Reset button; will be invisible if current page is not resetable (see IResetableWizard).
    createButton( parent, RESET_ID, Messages.getString( "org.kalypso.contribs.eclipse.jface.wizard.view.WizardView1" ), "doReset", false ); //$NON-NLS-1$ //$NON-NLS-2$

    if( m_wizard instanceof IWizard2 && ((IWizard2) m_wizard).hasSaveButton() )
      createButton( parent, SAVE_ID, Messages.getString( "org.kalypso.contribs.eclipse.jface.wizard.view.WizardView2" ), "doSave", false ); //$NON-NLS-1$ //$NON-NLS-2$

    if( m_wizard.isHelpAvailable() )
      createButton( parent, IDialogConstants.HELP_ID, IDialogConstants.HELP_LABEL, "doHelp", false ); //$NON-NLS-1$

    if( m_wizard.needsPreviousAndNextButtons() )
      createPreviousAndNextButtons( parent );

    createButton( parent, IDialogConstants.FINISH_ID, IDialogConstants.FINISH_LABEL, "doFinish", true ); //$NON-NLS-1$

    if( !(m_wizard instanceof IWizard2) || ((IWizard2) m_wizard).hasCancelButton() )
      createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, "doCancel", false ); //$NON-NLS-1$
  }

  /**
   * Creates the Previous and Next buttons for this wizard dialog. Creates standard (<code>SWT.PUSH</code>) buttons and
   * registers for their selection events. Note that the number of columns in the button bar composite is incremented.
   * These buttons are created specially to prevent any space between them.
   * 
   * @param parent
   *          the parent button bar
   * @return a composite containing the new buttons
   */
  private Composite createPreviousAndNextButtons( final Composite parent )
  {
    // increment the number of columns in the button bar
    ((GridLayout) parent.getLayout()).numColumns++;
    final Composite composite = new Composite( parent, SWT.NONE );
    // create a layout with spacing and margins appropriate for the font size.
    final GridLayout layout = new GridLayout();
    layout.numColumns = 0; // will be incremented by createButton
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    layout.horizontalSpacing = 0;
    layout.verticalSpacing = 0;
    composite.setLayout( layout );
    final GridData data = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
    composite.setLayoutData( data );
    composite.setFont( parent.getFont() );
    createButton( composite, IDialogConstants.BACK_ID, IDialogConstants.BACK_LABEL, "doPrev", false ); //$NON-NLS-1$
    createButton( composite, IDialogConstants.NEXT_ID, IDialogConstants.NEXT_LABEL, "doNext", false ); //$NON-NLS-1$
    return composite;
  }

  /**
   * Creates a new button with the given id.
   * <p>
   * The <code>Dialog</code> implementation of this framework method creates a standard push button, registers it for
   * selection events including button presses, and registers default buttons with its shell. The button id is stored as
   * the button's client data. If the button id is <code>IDialogConstants.CANCEL_ID</code>, the new button will be
   * accessible from <code>getCancelButton()</code>. If the button id is <code>IDialogConstants.OK_ID</code>, the new
   * button will be accesible from <code>getOKButton()</code>. Note that the parent's layout is assumed to be a
   * <code>GridLayout</code> and the number of columns in this layout is incremented. Subclasses may override.
   * </p>
   * 
   * @param parent
   *          the parent composite
   * @param id
   *          the id of the button (see <code>IDialogConstants.*_ID</code> constants for standard dialog button ids)
   * @param defaultLabel
   *          the label from the button
   * @param defaultButton
   *          <code>true</code> if the button is to be the default button, and <code>false</code> otherwise
   * @param handlerMethod
   *          (java) name of the method which handles this button. Must be of kind 'public void xxx()'
   * @return the new button
   */
  protected Button createButton( final Composite parent, final int id, final String defaultLabel, final String handlerMethod, final boolean defaultButton )
  {
    final Integer buttonID = new Integer( id );
    final String label = m_buttonLabels.containsKey( buttonID ) ? (String) m_buttonLabels.get( buttonID ) : defaultLabel;

    // increment the number of columns in the button bar
    ((GridLayout) parent.getLayout()).numColumns++;
    final Button button = new Button( parent, SWT.PUSH );
    button.setText( label );
    button.setFont( JFaceResources.getDialogFont() );

    button.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( final SelectionEvent event )
      {
        buttonPressed( handlerMethod );
      }
    } );
    if( m_useDefaultButton && defaultButton )
    {
      final Shell shell = parent.getShell();
      if( shell != null )
        shell.setDefaultButton( button );
    }

    m_buttons.put( buttonID, button );
    setButtonLayoutData( button );
    return button;
  }

  protected void buttonPressed( final String handlerMethod )
  {
    try
    {
      final Method method = getClass().getMethod( handlerMethod, (Class< ? >[]) null );
      method.invoke( this, (Object[]) null );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Set the layout data of the button to a GridData with appropriate heights and widths.
   * 
   * @param button
   */
  protected void setButtonLayoutData( final Button button )
  {
    final GridData data = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
    final int widthHint = convertHorizontalDLUsToPixels( IDialogConstants.BUTTON_WIDTH );
    final Point minSize = button.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
    data.widthHint = Math.max( widthHint, minSize.x );
    button.setLayoutData( data );
  }

  /**
   * Initializes the computation of horizontal and vertical dialog units based on the size of current font.
   * <p>
   * This method must be called before any of the dialog unit based conversion methods are called.
   * </p>
   * 
   * @param control
   *          a control from which to obtain the current font
   */
  protected void initializeDialogUnits( final Control control )
  {
    // Compute and store a font metric
    final GC gc = new GC( control );
    gc.setFont( JFaceResources.getDialogFont() );
    m_fontMetrics = gc.getFontMetrics();
    gc.dispose();
  }

  /**
   * Returns the number of pixels corresponding to the given number of vertical dialog units.
   * <p>
   * This method may only be called after <code>initializeDialogUnits</code> has been called.
   * </p>
   * <p>
   * Clients may call this framework method, but should not override it.
   * </p>
   * 
   * @param dlus
   *          the number of vertical dialog units
   * @return the number of pixels
   */
  protected int convertVerticalDLUsToPixels( final int dlus )
  {
    // test for failure to initialize for backward compatibility
    if( m_fontMetrics == null )
      return 0;
    return Dialog.convertVerticalDLUsToPixels( m_fontMetrics, dlus );
  }

  /**
   * Returns the number of pixels corresponding to the given number of horizontal dialog units.
   * <p>
   * This method may only be called after <code>initializeDialogUnits</code> has been called.
   * </p>
   * <p>
   * Clients may call this framework method, but should not override it.
   * </p>
   * 
   * @param dlus
   *          the number of horizontal dialog units
   * @return the number of pixels
   */
  protected int convertHorizontalDLUsToPixels( final int dlus )
  {
    // test for failure to initialize for backward compatibility
    if( m_fontMetrics == null )
      return 0;
    return Dialog.convertHorizontalDLUsToPixels( m_fontMetrics, dlus );
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  @Override
  public void setFocus( )
  {
    if( m_pageContainer == null && !m_workArea.isDisposed() )
      m_workArea.setFocus();
    else if( !m_pageContainer.isDisposed() )
      m_pageContainer.setFocus();
  }

  /**
   * Not imlemented, as we are inside a ViewPart, which should'nt change its own size.
   * 
   * @see org.eclipse.jface.wizard.IWizardContainer2#updateSize()
   */
  @Override
  public void updateSize( )
  {
    //
  }

  /**
   * @see org.eclipse.jface.wizard.IWizardContainer#getCurrentPage()
   */
  @Override
  public IWizardPage getCurrentPage( )
  {
    return m_currentPage;
  }

  /**
   * @see org.eclipse.jface.wizard.IWizardContainer#getShell()
   */
  @Override
  public Shell getShell( )
  {
    return getViewSite().getShell();
  }

  /**
   * @see org.eclipse.jface.wizard.IWizardContainer#showPage(org.eclipse.jface.wizard.IWizardPage)
   */
  @Override
  public void showPage( final IWizardPage page )
  {
    showPageInternal( page );
  }

  private boolean showPageInternal( final IWizardPage page )
  {
    if( page == null || page == m_currentPage )
      return false;

    if( !m_isMovingToPreviousPage )
    // remember my previous page.
    {
      if( m_backJumpsToLastVisited )
        page.setPreviousPage( m_currentPage );
    }
    else
      m_isMovingToPreviousPage = false;
    // Update for the new page in a busy cursor if possible

    // If page changing evaluation unsuccessful, do not change the page
    if( !doPageChanging( page ) )
      return false;

    final CatchRunnable runnable = new CatchRunnable()
    {
      @Override
      protected void runIntern( ) throws Throwable
      {
        updateForPage( page );
      }
    };

    BusyIndicator.showWhile( m_pageContainer.getDisplay(), runnable );

    // only if no exception was thrown, it is marked as successful
    return runnable.getThrown() == null;
  }

  /**
   * @see org.eclipse.jface.wizard.IWizardContainer#updateButtons()
   */
  @Override
  public void updateButtons( )
  {
    if( m_wizard == null || m_currentPage == null )
      return;

    boolean canFlipToNextPage = false;
    final boolean canFinish = m_wizard.canFinish();

    final boolean canReset = m_wizard instanceof IResetableWizard ? ((IResetableWizard) m_wizard).canReset() : false;
    final boolean showReset = m_wizard instanceof IResetableWizard ? ((IResetableWizard) m_wizard).showResetButton() : false;

    final Button backButton = getButton( IDialogConstants.BACK_ID );
    final Button nextButton = getButton( IDialogConstants.NEXT_ID );
    final Button finishButton = getButton( IDialogConstants.FINISH_ID );
    final Button resetButton = getButton( RESET_ID );

    if( backButton != null )
      backButton.setEnabled( m_currentPage.getPreviousPage() != null );

    if( nextButton != null )
    {
      canFlipToNextPage = m_currentPage.canFlipToNextPage();
      nextButton.setEnabled( canFlipToNextPage );
    }

    if( finishButton != null )
      finishButton.setEnabled( canFinish );

    if( resetButton != null )
    {
      resetButton.setVisible( showReset );
      resetButton.setEnabled( canReset );
    }

    // finish is default unless it is disabled and next is enabled
    // if( canFlipToNextPage && !canFinish )
    // cancel is default unless it is disabled or non-existent
    if( m_useDefaultButton )
    {
      if( canFlipToNextPage && nextButton != null )
        getShell().setDefaultButton( nextButton );
      else
        getShell().setDefaultButton( finishButton );
    }
  }

  /**
   * Returns the button created by the method <code>createButton</code> for the specified ID as defined on
   * <code>IDialogConstants</code>. If <code>createButton</code> was never called with this ID, or if
   * <code>createButton</code> is overridden, this method will return <code>null</code>.
   * 
   * @param id
   *          the id of the button to look for
   * @return the button for the ID or <code>null</code>
   * @see #createButton(Composite, int, String, String, boolean)
   * @since 2.0
   */
  protected Button getButton( final int id )
  {
    return m_buttons.get( new Integer( id ) );
  }

  protected void changeLocation( final String location )
  {
    if( m_ignoreNextCangeLocation || m_wizard == null )
    {
      m_ignoreNextCangeLocation = false;
      return;
    }

    final int index = location.indexOf( '#' );
    final String link;
    if( index == -1 )
      link = location;
    else
      link = location.substring( index + 1 );

    boolean pageChanged = false;
    if( "prev".compareToIgnoreCase( link ) == 0 ) //$NON-NLS-1$
      pageChanged = doPrev();
    else if( "next".compareToIgnoreCase( link ) == 0 ) //$NON-NLS-1$
      pageChanged = doNext();
    else if( "finish".compareToIgnoreCase( link ) == 0 ) //$NON-NLS-1$
      pageChanged = doFinish();
    else if( "cancel".compareToIgnoreCase( link ) == 0 ) //$NON-NLS-1$
      pageChanged = doCancel();
    else
    {
      final IWizardPage page = m_wizard.getPage( link );
      if( page != null )
        pageChanged = showPageInternal( page );
    }

    if( !pageChanged )
    {
      // we do not need to layout, because the page has not changed
      showUrl( getCurrentPage() );
    }
  }

  /**
   * @see org.eclipse.jface.operation.IRunnableContext#run(boolean, boolean,
   *      org.eclipse.jface.operation.IRunnableWithProgress)
   */
  @Override
  public void run( final boolean fork, final boolean cancelable, final IRunnableWithProgress runnable ) throws InvocationTargetException, InterruptedException
  {
    final IProgressService progressService = (IProgressService) getSite().getService( IProgressService.class );
    progressService.run( fork, cancelable, runnable );
  }

  /**
   * Shows the starting page of the wizard.
   */
  private void showStartingPage( )
  {
    final IWizardPage startingPage = m_wizard.getStartingPage();
    if( startingPage == null )
    {
      // something must have happend getting the page
      return;
    }

    updateForPage( startingPage );
  }

  /**
   * Updates this dialog's controls to reflect the current page.
   */
  protected void update( )
  {
    // Update the window title
    updateWindowTitle();
    // Update the title bar
    updateTitleBar();
    // Update the buttons
    updateButtons();

    // Fires the page change event
    firePageChanged( new PageChangedEvent( this, getCurrentPage() ) );
  }

  /**
   * Update the receiver for the new page.
   * 
   * @param page
   */
  protected void updateForPage( final IWizardPage page )
  {
    // ensure this page belongs to the current wizard
    if( m_wizard != page.getWizard() )
      throw new IllegalArgumentException();

    // ensure that page control has been created
    // (this allows lazy page control creation)
    if( page.getControl() == null )
    {
      page.createControl( m_pageContainer );
      // the page is responsible for ensuring the created control is accessable
      // via getControl.
      final Control control = page.getControl();
      Assert.isNotNull( control );

      // ensure the dialog is large enough for this page
      // updateSize( page );
    }

    // make the new page visible
    // final IWizardPage oldPage = m_currentPage;
    m_currentPage = page;
    m_stackLayout.topControl = m_currentPage.getControl();

    final String html = showUrl( page );
    if( html.length() != 0 )
      m_mainSash.setMaximizedControl( null );
    else
      m_mainSash.setMaximizedControl( m_pageAndButtonArea );

    // update the dialog controls
    m_pageContainer.layout();
    update();
  }

  private String showUrl( final IWizardPage page )
  {
    final String html = getHtmlForPage( page );
// TODO implement some search/replace, so we only need one html template for all pages
    m_ignoreNextCangeLocation = true;
    if( html.length() > 0 )
      m_browser.setUrl( html );

    return html;
  }

  private String getHtmlForPage( final IWizardPage page )
  {
    if( page instanceof IHtmlWizardPage )
    {
      final URL htmlURL = ((IHtmlWizardPage) page).getHtmlURL();
      if( htmlURL != null )
        return htmlURL.toString();
    }

    return ""; //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.contribs.eclipse.jface.wizard.view.IWizardContainer3#addWizardContainerListener(org.kalypso.contribs.eclipse.jface.wizard.view.IWizardContainerListener)
   */
  @Override
  public void addWizardContainerListener( final IWizardContainerListener l )
  {
    m_listeners.add( l );
  }

  /**
   * @see org.kalypso.contribs.eclipse.jface.wizard.view.IWizardContainer3#removeWizardContainerListener(org.kalypso.contribs.eclipse.jface.wizard.view.IWizardContainerListener)
   */
  @Override
  public void removeWizardContainerListener( final IWizardContainerListener l )
  {
    m_listeners.remove( l );
  }

  protected final void fireWizardChanged( final IWizard newwizard, final int reason )
  {
    final IWizardContainerListener[] listeners = m_listeners.toArray( new IWizardContainerListener[m_listeners.size()] );
    for( final IWizardContainerListener listener : listeners )
    {
      SafeRunnable.run( new SafeRunnable()
      {
        @Override
        public void run( ) throws Exception
        {
          listener.onWizardChanged( newwizard, reason );
        }
      } );
    }
  }

  public IWizard getWizard( )
  {
    return m_wizard;
  }

  public boolean doNext( )
  {
    final IWizardPage currentPage = getCurrentPage();
    final IWizard wizard = getWizard();

    if( wizard == null || currentPage == null )
      return false;

    if( wizard instanceof IWizard2 )
    {
      if( !((IWizard2) wizard).finishPage( currentPage ) )
        return false;
    }

    final IWizardPage nextPage = currentPage.getNextPage();
    if( nextPage == null )
      return false;

    return showPageInternal( nextPage );
  }

  public boolean doPrev( )
  {
    final IWizardPage currentPage = getCurrentPage();
    if( currentPage == null )
      return false;

    final IWizardPage previousPage = currentPage.getPreviousPage();
    if( previousPage == null )
      return false;

    // set flag to indicate that we are moving back
    m_isMovingToPreviousPage = true;

    return showPageInternal( previousPage );
  }

  public boolean doFinish( )
  {
    final IWizard wizard = getWizard();

    if( wizard == null )
    {
      // even if wizard is null, fire event, so listeners will know that the finish button was pressed (and can close
      // this view)
      fireWizardChanged( null, IWizardContainerListener.REASON_FINISHED );
      return true;
    }

    if( wizard.performFinish() )
    {
      setWizard( null, IWizardContainerListener.REASON_FINISHED );
      return true;
    }

    return false;
  }

  public boolean doCancel( )
  {
    final IWizard wizard = getWizard();

    if( wizard == null )
      return false;

    if( wizard.performCancel() )
    {
      setWizard( null, IWizardContainerListener.REASON_CANCELED );
      return true;
    }

    return false;
  }

  public boolean doHelp( )
  {
    final IWizard wizard = getWizard();
    final String helpId;
    if( wizard instanceof IWizard2 )
      helpId = ((IWizard2) wizard).getHelpId();
    else
      helpId = null;

    BusyIndicator.showWhile( null, new Runnable()
    {
      @Override
      public void run( )
      {
        final IContext context = HelpSystem.getContext( helpId );

        // take the first topic found and directly display it
        if( context != null && context.getRelatedTopics().length > 0 )
        {
          final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
          helpSystem.displayHelpResource( context.getRelatedTopics()[0].getHref() );
        }
        else
          Logger.getLogger( WizardView.class.getName() ).warning( Messages.getString( "org.kalypso.contribs.eclipse.jface.wizard.view.WizardView3" ) + helpId ); //$NON-NLS-1$
      }
    } );

    // the help button never changes the page, so always return false
    return false;
  }

  public boolean doReset( )
  {
    Assert.isTrue( m_wizard instanceof IResetableWizard );

    final IResetableWizard resetableWizard = (IResetableWizard) m_wizard;

    Assert.isTrue( resetableWizard.canReset() );

    resetableWizard.performReset();

    return true;
  }

  public boolean doSave( )
  {
    final IWizard wizard = getWizard();

    if( wizard instanceof IWizard2 )
    {
      final IWizard2 wizard2 = (IWizard2) wizard;

      if( wizard2.doAskForSave() )
      {
        if( !MessageDialog.openQuestion( getShell(), Messages.getString( "org.kalypso.contribs.eclipse.jface.wizard.view.WizardView4" ), Messages.getString( "org.kalypso.contribs.eclipse.jface.wizard.view.WizardView5" ) ) ) //$NON-NLS-1$ //$NON-NLS-2$
          return false;
      }

      final ICoreRunnableWithProgress saveOperation = new ICoreRunnableWithProgress()
      {
        @Override
        public IStatus execute( final IProgressMonitor monitor ) throws CoreException
        {
          return wizard2.saveAllPages( monitor );
        }
      };
      final IStatus status = RunnableContextHelper.execute( this, true, false, saveOperation );
      ErrorDialog.openError( getShell(), Messages.getString( "org.kalypso.contribs.eclipse.jface.wizard.view.WizardView6" ), Messages.getString( "org.kalypso.contribs.eclipse.jface.wizard.view.WizardView7" ), status ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    return true;
  }

  // /////////////////////
  // TITLE AREA DIALOG //
  // /////////////////////

  // Space between an image and a label
  private static final int H_GAP_IMAGE = 5;

  private Label titleLabel;

  private Label titleImage;

  private Label bottomFillerLabel;

  private Label leftFillerLabel;

  // private RGB titleAreaRGB;
  private String message = ""; //$NON-NLS-1$

  private String errorMessage;

  private Text messageLabel;

  private Label messageImageLabel;

  private Image messageImage;

  private Color normalMsgAreaBackground;

  private Color errorMsgAreaBackground;

  private Image errorMsgImage;

  private boolean showingError = false;

  private boolean titleImageLargest = true;

  private Composite m_parent;

  /**
   * Creates the dialog's title area.
   * 
   * @param parent
   *          the SWT parent for the title area widgets
   * @return Control with the highest x axis value.
   */
  private Control createTitleArea( final Composite parent )
  {
    // remeber parent in order to change its colors
    m_parent = parent;

    // Determine the background color of the title bar
    final Display display = parent.getDisplay();
    m_defaultTitleBackground = JFaceColors.getBannerBackground( display ).getRGB();
    m_defaultTitleForeground = JFaceColors.getBannerForeground( display ).getRGB();

    final int verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
    final int horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );

    // Dialog image @ right
    titleImage = new Label( parent, SWT.CENTER );
    titleImage.setImage( JFaceResources.getImage( TitleAreaDialog.DLG_IMG_TITLE_BANNER ) );
    final FormData imageData = new FormData();
    imageData.top = new FormAttachment( 0, verticalSpacing );
    // Note: do not use horizontalSpacing on the right as that would be a
    // regression from
    // the R2.x style where there was no margin on the right and images are
    // flush to the right
    // hand side. see reopened comments in 41172
    imageData.right = new FormAttachment( 100, 0 ); // horizontalSpacing
    titleImage.setLayoutData( imageData );
    // Title label @ top, left
    titleLabel = new Label( parent, SWT.LEFT );
    titleLabel.setFont( JFaceResources.getBannerFont() );
    titleLabel.setText( " " );//$NON-NLS-1$
    final FormData titleData = new FormData();
    titleData.top = new FormAttachment( 0, verticalSpacing );
    titleData.right = new FormAttachment( titleImage );
    titleData.left = new FormAttachment( 0, horizontalSpacing );
    titleLabel.setLayoutData( titleData );
    // Message image @ bottom, left
    messageImageLabel = new Label( parent, SWT.CENTER );
    // Message label @ bottom, center
    messageLabel = new Text( parent, SWT.WRAP | SWT.READ_ONLY );
    messageLabel.setText( " \n " ); // two lines//$NON-NLS-1$
    messageLabel.setFont( JFaceResources.getDialogFont() );

    // Filler labels
    leftFillerLabel = new Label( parent, SWT.CENTER );
    bottomFillerLabel = new Label( parent, SWT.CENTER );
    setLayoutsForNormalMessage( verticalSpacing, horizontalSpacing );
    determineTitleImageLargest();
    if( titleImageLargest )
      return titleImage;
    return messageLabel;
  }

  /**
   * Determine if the title image is larger than the title message and message area. This is used for layout decisions.
   */
  private void determineTitleImageLargest( )
  {
    final int titleY = titleImage.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
    int labelY = titleLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
    labelY += messageLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
    final FontData[] data = messageLabel.getFont().getFontData();
    labelY += data[0].getHeight();
    titleImageLargest = titleY > labelY;
  }

  /**
   * Set the layout values for the messageLabel, messageImageLabel and fillerLabel for the case where there is a normal
   * message.
   * 
   * @param verticalSpacing
   *          int The spacing between widgets on the vertical axis.
   * @param horizontalSpacing
   *          int The spacing between widgets on the horizontal axis.
   */
  private void setLayoutsForNormalMessage( final int verticalSpacing, final int horizontalSpacing )
  {
    final FormData messageImageData = new FormData();
    messageImageData.top = new FormAttachment( titleLabel, verticalSpacing );
    messageImageData.left = new FormAttachment( 0, H_GAP_IMAGE );
    messageImageLabel.setLayoutData( messageImageData );
    final FormData messageLabelData = new FormData();
    messageLabelData.top = new FormAttachment( titleLabel, verticalSpacing );
    messageLabelData.right = new FormAttachment( titleImage );
    messageLabelData.left = new FormAttachment( messageImageLabel, horizontalSpacing );
    if( titleImageLargest )
      messageLabelData.bottom = new FormAttachment( titleImage, 0, SWT.BOTTOM );
    messageLabel.setLayoutData( messageLabelData );
    final FormData fillerData = new FormData();
    fillerData.left = new FormAttachment( 0, horizontalSpacing );
    fillerData.top = new FormAttachment( messageImageLabel, 0 );
    fillerData.bottom = new FormAttachment( messageLabel, 0, SWT.BOTTOM );
    bottomFillerLabel.setLayoutData( fillerData );
    final FormData data = new FormData();
    data.top = new FormAttachment( messageImageLabel, 0, SWT.TOP );
    data.left = new FormAttachment( 0, 0 );
    data.bottom = new FormAttachment( messageImageLabel, 0, SWT.BOTTOM );
    data.right = new FormAttachment( messageImageLabel, 0 );
    leftFillerLabel.setLayoutData( data );
  }

  // /**
  // * The <code>TitleAreaDialog</code> implementation of this
  // * <code>Window</code> methods returns an initial size which is at least
  // * some reasonable minimum.
  // *
  // * @return the initial size of the dialog
  // */
  // protected Point getInitialSize() {
  // Point shellSize = super.getInitialSize();
  // return new Point(Math.max(
  // convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
  // Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
  // shellSize.y));
  // }
  /**
   * Retained for backward compatibility. Returns the title area composite. There is no composite in this implementation
   * so the shell is returned.
   * 
   * @return Composite
   * @deprecated
   */
  @Deprecated
  protected Composite getTitleArea( )
  {
    return getShell();
  }

  /**
   * Returns the title image label.
   * 
   * @return the title image label
   */
  protected Label getTitleImageLabel( )
  {
    return titleImage;
  }

  /**
   * Display the given error message. The currently displayed message is saved and will be redisplayed when the error
   * message is set to <code>null</code>.
   * 
   * @param newErrorMessage
   *          the newErrorMessage to display or <code>null</code>
   */
  public void setErrorMessage( final String newErrorMessage )
  {
    // Any change?
    if( errorMessage == null ? newErrorMessage == null : errorMessage.equals( newErrorMessage ) )
      return;
    errorMessage = newErrorMessage;

    if( messageLabel.isDisposed() )
      return;

    if( errorMessage == null )
    {
      if( showingError )
      {
        // we were previously showing an error
        showingError = false;
        setMessageBackgrounds( false );
      }
      // show the message
      // avoid calling setMessage in case it is overridden to call
      // setErrorMessage,
      // which would result in a recursive infinite loop
      if( message == null ) // this should probably never happen since
        // setMessage does this conversion....
        message = ""; //$NON-NLS-1$

      updateMessage( message );
      messageImageLabel.setImage( messageImage );
      setImageLabelVisible( messageImage != null );
      messageLabel.setToolTipText( message );
    }
    else
    {
      // Add in a space for layout purposes but do not
      // change the instance variable
      final String displayedErrorMessage = " " + errorMessage; //$NON-NLS-1$
      updateMessage( displayedErrorMessage );
      messageLabel.setToolTipText( errorMessage );
      if( !showingError )
      {
        // we were not previously showing an error
        showingError = true;
        // lazy initialize the error background color and image
        if( errorMsgAreaBackground == null )
        {
          errorMsgAreaBackground = JFaceColors.getErrorBackground( messageLabel.getDisplay() );
          errorMsgImage = JFaceResources.getImage( TitleAreaDialog.DLG_IMG_TITLE_ERROR );
        }
        // show the error
        normalMsgAreaBackground = messageLabel.getBackground();

        setMessageBackgrounds( !m_useNormalBackground );

        messageImageLabel.setImage( errorMsgImage );
        setImageLabelVisible( true );
      }
    }
    layoutForNewMessage();
  }

  /**
   * Re-layout the labels for the new message.
   */
  private void layoutForNewMessage( )
  {
    final int verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
    final int horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
    // If there are no images then layout as normal
    if( errorMessage == null && messageImage == null )
    {
      setImageLabelVisible( false );
      setLayoutsForNormalMessage( verticalSpacing, horizontalSpacing );
    }
    else
    {
      messageImageLabel.setVisible( true );
      bottomFillerLabel.setVisible( true );
      leftFillerLabel.setVisible( true );
      /**
       * Note that we do not use horizontalSpacing here as when the background of the messages changes there will be
       * gaps between the icon label and the message that are the background color of the shell. We add a leading space
       * elsewhere to compendate for this.
       */
      FormData data = new FormData();
      data.left = new FormAttachment( 0, H_GAP_IMAGE );
      data.top = new FormAttachment( titleLabel, verticalSpacing );
      messageImageLabel.setLayoutData( data );
      data = new FormData();
      data.top = new FormAttachment( messageImageLabel, 0 );
      data.left = new FormAttachment( 0, 0 );
      data.bottom = new FormAttachment( messageLabel, 0, SWT.BOTTOM );
      data.right = new FormAttachment( messageImageLabel, 0, SWT.RIGHT );
      bottomFillerLabel.setLayoutData( data );
      data = new FormData();
      data.top = new FormAttachment( messageImageLabel, 0, SWT.TOP );
      data.left = new FormAttachment( 0, 0 );
      data.bottom = new FormAttachment( messageImageLabel, 0, SWT.BOTTOM );
      data.right = new FormAttachment( messageImageLabel, 0 );
      leftFillerLabel.setLayoutData( data );
      final FormData messageLabelData = new FormData();
      messageLabelData.top = new FormAttachment( titleLabel, verticalSpacing );
      messageLabelData.right = new FormAttachment( titleImage );
      messageLabelData.left = new FormAttachment( messageImageLabel, 0 );
      if( titleImageLargest )
        messageLabelData.bottom = new FormAttachment( titleImage, 0, SWT.BOTTOM );
      messageLabel.setLayoutData( messageLabelData );
    }
    // Do not layout before the dialog area has been created
    // to avoid incomplete calculations.
    if( m_pageContainer != null && !m_pageContainer.isDisposed() )
      getShell().layout( true );
  }

  /**
   * Set the message text. If the message line currently displays an error, the message is saved and will be redisplayed
   * when the error message is set to <code>null</code>.
   * <p>
   * Shortcut for <code>setMessage(newMessage, IMessageProvider.NONE)</code>
   * </p>
   * This method should be called after the dialog has been opened as it updates the message label immediately.
   * 
   * @param newMessage
   *          the message, or <code>null</code> to clear the message
   */
  public void setMessage( final String newMessage )
  {
    setMessage( newMessage, IMessageProvider.NONE );
  }

  /**
   * Sets the message for this dialog with an indication of what type of message it is.
   * <p>
   * The valid message types are one of <code>NONE</code>,<code>INFORMATION</code>,<code>WARNING</code>, or
   * <code>ERROR</code>.
   * </p>
   * <p>
   * Note that for backward compatibility, a message of type <code>ERROR</code> is different than an error message (set
   * using <code>setErrorMessage</code>). An error message overrides the current message until the error message is
   * cleared. This method replaces the current message and does not affect the error message.
   * </p>
   * 
   * @param newMessage
   *          the message, or <code>null</code> to clear the message
   * @param newType
   *          the message type
   * @since 2.0
   */
  public void setMessage( final String newMessage, final int newType )
  {
    Image newImage = null;
    if( newMessage != null )
    {
      switch( newType )
      {
        case IMessageProvider.NONE:
          break;
        case IMessageProvider.INFORMATION:
          newImage = JFaceResources.getImage( Dialog.DLG_IMG_MESSAGE_INFO );
          break;
        case IMessageProvider.WARNING:
          newImage = JFaceResources.getImage( Dialog.DLG_IMG_MESSAGE_WARNING );
          break;
        case IMessageProvider.ERROR:
          newImage = JFaceResources.getImage( Dialog.DLG_IMG_MESSAGE_ERROR );
          break;
      }
    }
    showMessage( newMessage, newImage );
  }

  /**
   * Show the new message and image.
   * 
   * @param newMessage
   * @param newImage
   */
  private void showMessage( final String newMessage, final Image newImage )
  {
    // Any change?
    if( message.equals( newMessage ) && messageImage == newImage )
      return;
    message = newMessage;
    if( message == null )
      message = "";//$NON-NLS-1$
    // Message string to be shown - if there is an image then add in
    // a space to the message for layout purposes
    final String shownMessage = (newImage == null) ? message : " " + message; //$NON-NLS-1$
    messageImage = newImage;
    if( !showingError )
    {
      // we are not showing an error
      updateMessage( shownMessage );
      messageImageLabel.setImage( messageImage );
      setImageLabelVisible( messageImage != null );
      messageLabel.setToolTipText( message );
      layoutForNewMessage();
    }
  }

  /**
   * Update the contents of the messageLabel.
   * 
   * @param newMessage
   *          the message to use
   */
  private void updateMessage( String newMessage )
  {
    // Be sure there are always 2 lines for layout purposes
    if( newMessage != null && newMessage.indexOf( '\n' ) == -1 )
      newMessage = newMessage + "\n "; //$NON-NLS-1$

    if( !messageLabel.isDisposed() )
      messageLabel.setText( newMessage );
  }

  /**
   * Sets the title to be shown in the title area of this dialog.
   * 
   * @param newTitle
   *          the title show
   */
  public void setWizardTitle( final String newTitle )
  {
    if( titleLabel == null )
      return;
    String title = newTitle;
    if( title == null )
      title = "";//$NON-NLS-1$
// if( titleLabel.isDisposed() )
    titleLabel.setText( title );
  }

  /**
   * Sets the title image to be shown in the title area of this dialog.
   * 
   * @param newTitleImage
   *          the title image show
   */
  public void setWizardTitleImage( final Image newTitleImage )
  {
    if( titleImage == null || titleImage.isDisposed() )
      return;

    titleImage.setImage( newTitleImage );
    titleImage.setVisible( newTitleImage != null );
    if( newTitleImage != null )
    {
      determineTitleImageLargest();
      Control top;
      if( titleImageLargest )
        top = titleImage;
      else
        top = messageLabel;
      resetWorkAreaAttachments( top );
    }
  }

  /**
   * Make the label used for displaying error images visible depending on boolean.
   * 
   * @param visible
   *          . If <code>true</code> make the image visible, if not then make it not visible.
   */
  private void setImageLabelVisible( final boolean visible )
  {
    messageImageLabel.setVisible( visible );
    bottomFillerLabel.setVisible( visible );
    leftFillerLabel.setVisible( visible );
  }

  /**
   * Set the message backgrounds to be the error or normal color depending on whether or not showingError is true.
   * 
   * @param showingErr
   *          If <code>true</code> use a different Color to indicate the error.
   */
  private void setMessageBackgrounds( final boolean showingErr )
  {
    Color color;
    if( showingErr )
      color = errorMsgAreaBackground;
    else
      color = normalMsgAreaBackground;
    messageLabel.setBackground( color );
    messageImageLabel.setBackground( color );
    bottomFillerLabel.setBackground( color );
    leftFillerLabel.setBackground( color );
  }

  /**
   * Reset the attachment of the workArea to now attach to top as the top control.
   * 
   * @param top
   */
  private void resetWorkAreaAttachments( final Control top )
  {
    final FormData childData = new FormData();
    childData.top = new FormAttachment( top );
    childData.right = new FormAttachment( 100, 0 );
    childData.left = new FormAttachment( 0, 0 );
    childData.bottom = new FormAttachment( 100, 0 );
    m_workArea.setLayoutData( childData );
  }

  // ////////////////
  // WizardDialog //
  // ////////////////

  // The current page message and description
  private String pageMessage;

  private int pageMessageType = IMessageProvider.NONE;

  private String pageDescription;

  private final DisposeHelper m_disposeHelper = new DisposeHelper();

  /**
   * @see org.eclipse.jface.wizard.IWizardContainer#updateMessage()
   */
  @Override
  public void updateMessage( )
  {
    if( m_currentPage == null )
      return;

    pageMessage = m_currentPage.getMessage();
    if( pageMessage != null && m_currentPage instanceof IMessageProvider )
      pageMessageType = ((IMessageProvider) m_currentPage).getMessageType();
    else
      pageMessageType = IMessageProvider.NONE;
    if( pageMessage == null )
      setMessage( pageDescription );
    else
      setMessage( pageMessage, pageMessageType );
    setErrorMessage( m_currentPage.getErrorMessage() );
  }

  /**
   * @see org.eclipse.jface.wizard.IWizardContainer#updateTitleBar()
   */
  @Override
  public void updateTitleBar( )
  {
    // update title-bar colors
    final RGB backgroundRGB;
    final RGB foregroundRGB = m_defaultTitleForeground;
    if( m_wizard != null )
      backgroundRGB = m_wizard.getTitleBarColor();
    else
      backgroundRGB = m_defaultTitleBackground;

    final ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
    colorRegistry.put( m_backgroundRGB_ID, backgroundRGB );
    colorRegistry.put( m_foregroundRGB_ID, foregroundRGB );

    final Color background = colorRegistry.get( m_backgroundRGB_ID );
    final Color foreground = colorRegistry.get( m_foregroundRGB_ID );

    if( m_parent != null && !m_parent.isDisposed() )
      m_parent.setBackground( background );
    if( titleImage != null && !titleImage.isDisposed() )
      titleImage.setBackground( background );
    if( titleLabel != null && !titleLabel.isDisposed() )
      JFaceColors.setColors( titleLabel, foreground, background );
    if( messageImageLabel != null && !messageImageLabel.isDisposed() )
      messageImageLabel.setBackground( background );
    if( messageLabel != null && !messageLabel.isDisposed() )
      JFaceColors.setColors( messageLabel, foreground, background );
    if( leftFillerLabel != null && !leftFillerLabel.isDisposed() )
      leftFillerLabel.setBackground( background );
    if( bottomFillerLabel != null && !bottomFillerLabel.isDisposed() )
      bottomFillerLabel.setBackground( background );

    String s = null;
    if( m_currentPage != null )
      s = m_currentPage.getTitle();
    if( s == null )
      s = ""; //$NON-NLS-1$
    setWizardTitle( s );
    if( m_currentPage != null )
      setWizardTitleImage( m_currentPage.getImage() );

    updateDescriptionMessage();
    updateMessage();
  }

  /**
   * @see org.eclipse.jface.wizard.IWizardContainer#updateWindowTitle()
   */
  @Override
  public void updateWindowTitle( )
  {
    if( m_wizard == null )
      return;

    String title = m_wizard.getWindowTitle();
    if( title == null )
      title = ""; //$NON-NLS-1$
    setPartName( title );
  }

  /**
   * Update the message line with the page's description.
   * <p>
   * A discription is shown only if there is no message or error message.
   * </p>
   */
  private void updateDescriptionMessage( )
  {
    if( m_currentPage == null )
      return;

    pageDescription = m_currentPage.getDescription();
    if( pageMessage == null )
      setMessage( m_currentPage.getDescription() );
  }

  /**
   * @param imageDescriptor
   */
  public void setTitleImage( final ImageDescriptor imageDescriptor )
  {
    final Image image = imageDescriptor.createImage();
    m_disposeHelper.addDisposeCandidate( image );

    setTitleImage( image );
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.IPageChangeProvider#getSelectedPage()
   */
  @Override
  public Object getSelectedPage( )
  {
    return getCurrentPage();
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialog.IPageChangeProvider#addPageChangedListener()
   */
  @Override
  public void addPageChangedListener( final IPageChangedListener listener )
  {
    pageChangedListeners.add( listener );
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialog.IPageChangeProvider#removePageChangedListener()
   */
  @Override
  public void removePageChangedListener( final IPageChangedListener listener )
  {
    pageChangedListeners.remove( listener );
  }

  /**
   * Notifies any selection changed listeners that the selected page has changed. Only listeners registered at the time
   * this method is called are notified.
   * 
   * @param event
   *          a selection changed event
   * @see IPageChangedListener#pageChanged
   * @since 3.1
   */
  protected void firePageChanged( final PageChangedEvent event )
  {
    final Object[] listeners = pageChangedListeners.getListeners();
    for( int i = 0; i < listeners.length; ++i )
    {
      final IPageChangedListener l = (IPageChangedListener) listeners[i];
      SafeRunnable.run( new SafeRunnable()
      {
        @Override
        public void run( )
        {
          l.pageChanged( event );
        }
      } );
    }
  }

  /**
   * Adds a listener for page changes to the list of page changing listeners registered for this dialog. Has no effect
   * if an identical listener is already registered.
   * 
   * @param listener
   *          a page changing listener
   * @since 3.3
   */
  public void addPageChangingListener( final IPageChangingListener listener )
  {
    pageChangingListeners.add( listener );
  }

  /**
   * Removes the provided page changing listener from the list of page changing listeners registered for the dialog.
   * 
   * @param listener
   *          a page changing listener
   * @since 3.3
   */
  public void removePageChangingListener( final IPageChangingListener listener )
  {
    pageChangingListeners.remove( listener );
  }

  /**
   * Notifies page changing listeners and returns result of page changing processing to the sender.
   * 
   * @param eventType
   * @return <code>true</code> if page changing listener completes successfully, <code>false</code> otherwise
   */
  private boolean doPageChanging( final IWizardPage targetPage )
  {
    final PageChangingEvent e = new PageChangingEvent( this, getCurrentPage(), targetPage );
    firePageChanging( e );
    // Prevent navigation if necessary
    return e.doit;
  }

  /**
   * Notifies any page changing listeners that the currently selected dialog page is changing. Only listeners registered
   * at the time this method is called are notified.
   * 
   * @param event
   *          a selection changing event
   * @see IPageChangingListener#handlePageChanging(PageChangingEvent)
   * @since 3.3
   */
  protected void firePageChanging( final PageChangingEvent event )
  {
    final Object[] listeners = pageChangingListeners.getListeners();
    for( int i = 0; i < listeners.length; ++i )
    {
      final IPageChangingListener l = (IPageChangingListener) listeners[i];
      SafeRunnable.run( new SafeRunnable()
      {
        @Override
        public void run( )
        {
          l.handlePageChanging( event );
        }
      } );
    }
  }

}