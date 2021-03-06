package org.kalypso.commons.browser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.internal.resources.PlatformURLResourceConnection;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.IBrowserViewerContainer;
import org.eclipse.ui.part.ViewPart;
import org.kalypso.commons.browser.actions.DefaultHtmlProvider;
import org.kalypso.commons.browser.actions.IHtmlProvider;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;

/**
 * This abstract class is a facade for the eclipse browser view.
 *
 * @see org.eclipse.ui.internal.browser.BrowserViewer Supports the IMemento for persistance and context for relative
 *      references MementoWithUrlResolver.
 * @author kuepfer
 */
@SuppressWarnings("restriction")
public abstract class AbstractBrowserView extends ViewPart implements IBrowserViewerContainer
{
  private IMemento m_memento;

  // Persistance tags.

  private static final String TAG_URL = "url"; //$NON-NLS-1$

  private static final String TAG_SCROLLBARS = "scrolbars"; //$NON-NLS-1$

  private static final String TAG_HORIZONTAL_BAR = "horizontal"; //$NON-NLS-1$

  private static final String TAG_VERTICAL_BAR = "vertical"; //$NON-NLS-1$

  private static final String TAG_SELECTION = "selection"; //$NON-NLS-1$

  public static final int BACKWARD = -1;

  public static final int FORWARD = 1;

  public static final int HOME = 0;

  protected BrowserViewer m_viewer;

  private URL m_browserContext = null;

  private String m_html = null;

  /**
   * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
   */
  @Override
  public void init( final IViewSite site, final IMemento memento ) throws PartInitException
  {
    super.init( site, memento );
    m_memento = memento;
  }

  public void addLocationListener( final LocationListener listener )
  {
    m_viewer.getBrowser().addLocationListener( listener );
  }

  public void removeLocationListener( final LocationListener listener )
  {
    if( m_viewer.getBrowser().isDisposed() )
      return;
    m_viewer.getBrowser().removeLocationListener( listener );
  }

  /**
   * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl( final Composite parent )
  {
    m_viewer = new BrowserViewer( parent, SWT.NONE ); // BrowserViewer.LOCATION_BAR);
    m_viewer.setContainer( this );
    // Delete IE Menu
    final MenuManager menuManager = new MenuManager( "#PopupMenu" ); //$NON-NLS-1$
    menuManager.setRemoveAllWhenShown( true );
    final Menu contextMenu = menuManager.createContextMenu( m_viewer.getBrowser() );
    m_viewer.getBrowser().setMenu( contextMenu );
    getSite().registerContextMenu( menuManager, getSite().getSelectionProvider() );

    // addBrowserListener();
    if( m_memento != null )
      restoreState( m_memento );
    m_memento = null;
  }

  protected void restoreState( final IMemento memento )
  {
    final String urlAsString = memento.getString( TAG_URL );

    try
    {
      handleSetUrl( urlAsString );
    }
    catch( final MalformedURLException e )
    {
      e.printStackTrace();
    }

    final IMemento scrollbars = memento.getChild( TAG_SCROLLBARS );
    if( scrollbars == null )
      return;

    final IMemento horizontal = scrollbars.getChild( TAG_HORIZONTAL_BAR );
    if( horizontal != null )
    {
      final int hSelection = horizontal.getInteger( TAG_SELECTION ).intValue();
      final ScrollBar horizontalBar = m_viewer.getHorizontalBar();
      if( horizontalBar != null )
        horizontalBar.setSelection( hSelection );
    }

    final IMemento vertical = scrollbars.getChild( TAG_VERTICAL_BAR );
    if( vertical != null )
    {
      final int vSelection = vertical.getInteger( TAG_SELECTION ).intValue();
      final ScrollBar verticalBar = m_viewer.getVerticalBar();
      if( verticalBar != null )
        verticalBar.setSelection( vSelection );
    }
  }

  @Override
  public void dispose( )
  {
    if( m_viewer != null )
      m_viewer.dispose();

    super.dispose();
  }

  public void setHtml( final String html )
  {
    if( m_viewer != null )
    {
      m_viewer.getBrowser().setText( html );
      // remember html for the getHtml method
      m_html = html;
    }
  }

  public String getHtml( )
  {
    return m_html;
  }

  public void setURL( final String url )
  {
    if( m_viewer == null )
      return;

    // BUGFIX: see bugfix below, this is also needed
    setHtml( "<html><body></body></html>" ); //$NON-NLS-1$

    m_viewer.getDisplay().asyncExec( new Runnable()
    {
      @Override
      public void run( )
      {
        try
        {
          // BUGFIX: Internet explorer may be not yet initialised
          // when the view was just opened.
          // If we dont wait, he sometimes doesnt show the file but instead
          // asks to download it,
          Thread.sleep( 0, 1 );
        }
        catch( final InterruptedException e )
        {
          e.printStackTrace();
        }

        // set the url for the Browser
        try
        {
          handleSetUrl( url );
        }
        catch( final MalformedURLException e )
        {
          e.printStackTrace();
        }
      }
    } );
  }

  /**
   * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
   */
  @Override
  public void setFocus( )
  {
    m_viewer.setFocus();
  }

  @Override
  public boolean close( )
  {
    try
    {
      getSite().getPage().hideView( this );
      return true;
    }
    catch( final Exception e )
    {
      return false;
    }
  }

  @Override
  public IActionBars getActionBars( )
  {
    return getViewSite().getActionBars();
  }

  @Override
  public void openInExternalBrowser( final String url )
  {
    try
    {
      final URL theURL = new URL( url );
      final IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
      support.getExternalBrowser().openURL( theURL );
    }
    catch( final MalformedURLException e )
    {
      // skip
    }
    catch( final PartInitException e )
    {
      // skip
    }
  }

  /**
   * Return true if the filename has a "web" extension.
   *
   * @param name
   *          The filename.
   * @return True if the filename has a "web" extension.
   */
  public static boolean isWebFile( final String name )
  {
    final String lowerCase = name.toLowerCase();

    return lowerCase.endsWith( "html" ) || lowerCase.endsWith( "htm" ) || lowerCase.endsWith( "gif" ) || lowerCase.endsWith( "png" ) || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        lowerCase.endsWith( "jpg" ) || lowerCase.endsWith( "pdf" ) || lowerCase.endsWith( "txt" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  public void saveState( final IMemento memento )
  {
    if( m_viewer == null )
    {
      if( m_memento != null ) // Keep the old state;
        memento.putMemento( m_memento );

      return;
    }

    // BUGFIX + HACK: do not store memento because restoring
    // it lead sometimes to a bug (IE asks to save the url instead of
    // displaying it)
    // memento.putString( TAG_URL, m_viewer.getURL() );
    memento.putString( TAG_URL, null );

    final IMemento scrollbarMemento = memento.createChild( TAG_SCROLLBARS );
    final ScrollBar horizontalBar = m_viewer.getHorizontalBar();
    if( horizontalBar != null )
    {
      final IMemento horizontal = scrollbarMemento.createChild( TAG_HORIZONTAL_BAR );
      horizontal.putInteger( TAG_SELECTION, horizontalBar.getSelection() );
    }
    final ScrollBar verticalBar = m_viewer.getVerticalBar();
    if( verticalBar != null )
    {
      final IMemento vertical = scrollbarMemento.createChild( TAG_VERTICAL_BAR );
      vertical.putInteger( TAG_SELECTION, verticalBar.getSelection() );
    }
  }

  protected void handleSetUrl( final String url ) throws MalformedURLException
  {
    Runnable runnable = null;
    if( url == null )
      return;

    if( url.startsWith( PlatformURLResourceConnection.RESOURCE_URL_STRING ) )
    {
      final URL realUrl = new URL( url );
      final IPath path = ResourceUtilities.findPathFromURL( realUrl );
      final File file = ResourceUtilities.makeFileFromPath( path );
      final String fileAsString = file.toString();
      changeContext( file.toURI().toURL() );
      runnable = new Runnable()
      {
        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run( )
        {
          m_viewer.setURL( fileAsString );
          if( m_viewer.combo != null )
            m_viewer.combo.setText( fileAsString );
          m_viewer.forward();
        }
      };
    }

    if( runnable == null )
    {
      try
      {
        changeContext( new URL( url ) );

        runnable = new Runnable()
        {

          @Override
          public void run( )
          {
            m_viewer.setURL( url );
            if( m_viewer.combo != null )
              m_viewer.combo.setText( url );
            m_viewer.forward();
          }
        };
      }
      catch( final MalformedURLException e )
      {
        // nothing
      }
    }

    if( runnable == null )
      return;

    final IWorkbenchPartSite site = getSite();
    if( site == null )
      return;

    final Shell shell = site.getShell();
    if( shell == null )
      return;

    final Display display = shell.getDisplay();
    if( display == null )
      return;

    display.asyncExec( runnable );
  }

  public void changeContext( final URL context )
  {
    m_browserContext = context;
  }

  public URL getContext( )
  {
    return m_browserContext;
  }

  @Override
  public Object getAdapter( final Class adapter )
  {
    if( adapter == IHtmlProvider.class )
      return new DefaultHtmlProvider( getHtml() );

    if( adapter == Browser.class )
      return m_viewer.getBrowser();

    if( adapter == BrowserViewer.class )
      return m_viewer;

    return super.getAdapter( adapter );
  }
}