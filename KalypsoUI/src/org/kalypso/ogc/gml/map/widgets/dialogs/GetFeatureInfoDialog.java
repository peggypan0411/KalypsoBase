/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 * 
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestraße 22
 *  21073 Hamburg, Germany
 *  http://www.tuhh.de/wb
 * 
 *  and
 *  
 *  Bjoernsen Consulting Engineers (BCE)
 *  Maria Trost 3
 *  56070 Koblenz, Germany
 *  http://www.bjoernsen.de
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Contact:
 * 
 *  E-Mail:
 *  belger@bjoernsen.de
 *  schlienger@bjoernsen.de
 *  v.doemming@tuhh.de
 *   
 *  ---------------------------------------------------------------------------*/
package org.kalypso.ogc.gml.map.widgets.dialogs;

import java.net.URL;

import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.commons.net.HttpResponse;
import org.kalypso.commons.net.SendHttpGetRequestJob;
import org.kalypso.contribs.eclipse.jface.dialog.DialogSettingsUtils;
import org.kalypso.i18n.Messages;
import org.kalypso.ui.KalypsoGisPlugin;

/**
 * The get feature info dialog.
 * 
 * @author Holger Albert
 */
public class GetFeatureInfoDialog extends PopupDialog
{
  /**
   * The job change listener.
   */
  private final IJobChangeListener m_listener = new JobChangeAdapter()
  {
    @Override
    public void done( final IJobChangeEvent event )
    {
      handleJobDone( event );
    }
  };

  /**
   * The request URL.
   */
  private final URL m_requestUrl;

  /**
   * The browser.
   */
  private Browser m_browser;

  /**
   * The constructor.
   * 
   * @param parentShell
   *          The parent shell.
   * @param requestUrl
   *          The request Url.
   */
  public GetFeatureInfoDialog( final Shell parentShell, final URL requestUrl )
  {
    super( parentShell, SWT.RESIZE, true, true, true, false, false, Messages.getString( "GetFeatureInfoDialog_0" ), null ); //$NON-NLS-1$

    m_requestUrl = requestUrl;
    m_browser = null;
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea( final Composite parent )
  {
    /* Create the main composite. */
    final Composite main = (Composite) super.createDialogArea( parent );
    main.setLayout( new GridLayout( 1, false ) );
    main.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

    /* Set the title text. */
    setTitleText( "Loading..." );

    /* Create the browser. */
    m_browser = new Browser( main, SWT.BORDER );
    m_browser.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

    /* Initialize. */
    initialize();

    return main;
  }

  /**
   * @see org.eclipse.jface.dialogs.PopupDialog#getDialogSettings()
   */
  @Override
  protected IDialogSettings getDialogSettings( )
  {
    return DialogSettingsUtils.getDialogSettings( KalypsoGisPlugin.getDefault(), getClass().getCanonicalName() );
  }

  /**
   * This function initalizes the dialog.
   */
  private void initialize( )
  {
    /* If there was no url provided show only a notice. */
    if( m_requestUrl == null )
    {
      setTitleText( Messages.getString( "GetFeatureInfoDialog_1" ) ); //$NON-NLS-1$
      return;
    }

    /* Create the send http get request job. */
    final SendHttpGetRequestJob job = new SendHttpGetRequestJob( m_requestUrl );
    job.setSystem( true );
    job.addJobChangeListener( m_listener );

    /* Schedule it. */
    job.schedule();
  }

  protected void handleJobDone( final IJobChangeEvent event )
  {
    if( m_browser == null || m_browser.isDisposed() )
      return;

    final Display display = m_browser.getDisplay();
    display.asyncExec( new Runnable()
    {
      @Override
      public void run( )
      {
        handleJobDoneInternal( event );
      }
    } );
  }

  protected void handleJobDoneInternal( final IJobChangeEvent event )
  {
    setTitleText( "Request successfull" );

    /* Get the result. */
    final IStatus result = event.getResult();
    if( !result.isOK() )
    {
      setText( "text/plain", result.getMessage() );
      return;
    }

    /* Get the job. */
    final Job job = event.getJob();
    if( !(job instanceof SendHttpGetRequestJob) )
      return;

    /* Cast. */
    final SendHttpGetRequestJob task = (SendHttpGetRequestJob) job;

    /* Get the response. */
    final HttpResponse response = task.getResponse();
    final StatusLine statusLine = response.getStatusLine();
    if( statusLine.getStatusCode() != 200 )
    {
      setText( "text/plain", statusLine.toString() );
      return;
    }

    /* Get the response text. */
    final String responseText = response.getResponse();
    if( responseText == null )
    {
      setText( "text/plain", "There was no result returned by the server." );
      return;
    }

    /* Set the response text into the browser. */
    setText( response.getMimeType(), responseText );
  }

  private void setText( final String mimeType, final String text )
  {
    if( m_browser == null || m_browser.isDisposed() )
      return;

    // TODO Parse xml message and create nice html...
    // TODO Wrap html response into get feature info response...
    if( mimeType != null && mimeType.contains( "xml" ) )
      m_browser.setText( StringEscapeUtils.escapeHtml4( text ) );
    else
      m_browser.setText( text );
  }
}