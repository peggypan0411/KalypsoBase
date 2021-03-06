/** This file is part of Kalypso
 *
 *  Copyright (c) 2012 by
 *
 *  Bj�rnsen Beratende Ingenieure GmbH, Koblenz, Germany (Bjoernsen Consulting Engineers), http://www.bjoernsen.de
 *  Technische Universit�t Hamburg-Harburg, Institut f�r Wasserbau, Hamburg, Germany
 *  (Technical University Hamburg-Harburg, Institute of River and Coastal Engineering), http://www.tu-harburg.de/wb/
 *
 *  Kalypso is free software: you can redistribute it and/or modify it under the terms  
 *  of the GNU Lesser General Public License (LGPL) as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  Kalypso is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with Kalypso.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kalypso.contribs.eclipse.jface.wizard.view;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Gernot Belger
 */
public class NavigationPanel extends Composite
{
  enum NaviCommand
  {
    prev,
    next,
    finish,
    cancel;
  }

  /** Lock for preventing call to changeLocation when url is set internally */
  private boolean m_ignoreNextChangeLocation = false;

  private final Browser m_browser;

  private MenuManager m_menuManager;

  private final WizardView m_container;

  public NavigationPanel( final WizardView container, final Composite parent, final int style )
  {
    super( parent, style );
    m_container = container;

    // Register a context menu on it, so we suppress the ugly explorer menu
    final FillLayout mainLayout = new FillLayout();
    setLayout( mainLayout );

    m_browser = new Browser( this, SWT.NONE );
    m_browser.setJavascriptEnabled( true );

    createContextMenu();
  }

  /**
   * Set an empty context menu in order to overwrite the one of IE
   */
  private void createContextMenu( )
  {
    m_menuManager = new MenuManager( "#PopupMenu" ); //$NON-NLS-1$
    m_menuManager.setRemoveAllWhenShown( true );
    final Menu contextMenu = m_menuManager.createContextMenu( m_browser );
    m_browser.setMenu( contextMenu );
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
  }

  public MenuManager getContextMenu( )
  {
    return m_menuManager;
  }

  protected void changeLocation( final String location )
  {
    if( m_ignoreNextChangeLocation )
    {
      m_ignoreNextChangeLocation = false;
      return;
    }

    final IWizardPage currentPage = m_container.getCurrentPage();

    final String cmdString = getCommandString( location );
    final boolean pageChanged = executeCommand( cmdString );

    if( !pageChanged )
    {
      // we do not need to layout, because the page has not changed
      showUrl( currentPage );
    }
  }

  private boolean executeCommand( final String cmdString )
  {
    final NaviCommand command = parseCommand( cmdString );
    if( command == null )
    {
      final IWizard wizard = m_container.getWizard();
      if( wizard == null )
        return false;

      final IWizardPage page = wizard.getPage( cmdString );
      if( page == null )
        return false;

      return m_container.showPageInternal( page );
    }

    switch( command )
    {
      case prev:
        return m_container.doPrev();

      case next:
        return m_container.doNext();

      case finish:
        return m_container.doFinish();

      case cancel:
        return m_container.doCancel();

      default:
        return false;
    }
  }

  private NaviCommand parseCommand( final String location )
  {
    if( location == null || location.trim().isEmpty() )
      return null;

    try
    {
      final String link = location.toLowerCase();
      return NaviCommand.valueOf( link );
    }
    catch( final IllegalArgumentException e )
    {
      return null;
    }
  }

  private String getCommandString( final String location )
  {
    final int index = location.indexOf( '#' );
    if( index == -1 )
      return location;

    return location.substring( index + 1 );
  }

  public boolean showUrl( final IWizardPage page )
  {
    final URL htmlUrl = getUrlForPage( page );

    m_ignoreNextChangeLocation = true;
    if( htmlUrl != null )
    {
      m_browser.setUrl( htmlUrl.toExternalForm() );

      final String pageNavigationId = getPageNavigationID( page );

      final String script = //
      "elements = document.getElementsByName('%s');" + //
          "for (i = 0; i < elements.length; i++)" + //
          "elements[i].className+=' activ';";
      final String executeScript = String.format( script, pageNavigationId );

      final Browser browser = m_browser;

      final ProgressListener progressListener = new ProgressAdapter()
      {
        @Override
        public void completed( final ProgressEvent event )
        {
          if( !browser.execute( executeScript ) )
            System.out.println( "Failed to execute java script on navigator" );

          browser.removeProgressListener( this );
        }
      };
      browser.addProgressListener( progressListener );

      // REMARK: need to execute this in an extra job, else it does not work for clicks.
      // as the browser seems to still react to the click.
      final UIJob updateBrowserJob = new UIJob( "Aktualisiere Navigation" )
      {
        @Override
        public IStatus runInUIThread( final IProgressMonitor monitor )
        {
          if( !browser.execute( executeScript ) )
            System.out.println( "Failed to execute java script on navigator" );
          return Status.OK_STATUS;
        }
      };
      updateBrowserJob.setSystem( true );
      updateBrowserJob.schedule();

      return true;
    }
    else
      return false;
  }

  private String getPageNavigationID( final IWizardPage page )
  {
    if( page instanceof IHtmlWizardPage )
      return ((IHtmlWizardPage) page).getNavigationID();

    return page.getName();
  }

  private URL getUrlForPage( final IWizardPage page )
  {
    if( page instanceof IHtmlWizardPage )
    {
      final URL htmlURL = ((IHtmlWizardPage) page).getHtmlURL();
      if( htmlURL != null )
        return htmlURL;

      /* Only check for pages that are html-pages, else we get navigation everywhere */
      final IWizard wizard = page.getWizard();
      if( wizard instanceof IHtmlWizard )
        return ((IHtmlWizard) wizard).getHtmlURL();
    }

    return null; //$NON-NLS-1$
  }

}
