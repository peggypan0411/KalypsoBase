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
package org.kalypso.ogc.gml.om.table.celleditor;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.kalypso.ui.internal.i18n.Messages;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 * @author Dirk Kuch
 * @deprecated Use {@link org.kalypso.contribs.eclipse.swt.widgets.DateTimeDialog} instead.
 */
@Deprecated
public class DateTimeDialog extends TitleAreaDialog
{
  private final TimeZone m_displayTimeZone;

  private Date m_gregorianCalendar;

  private Date m_preSettedDateTime;

  public DateTimeDialog( final Shell parent, final TimeZone displayTimeZone )
  {
    super( parent );

    m_displayTimeZone = displayTimeZone;

    setBlockOnOpen( true );
  }

  /**
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  @Override
  protected void configureShell( final Shell newShell )
  {
    super.configureShell( newShell );

    newShell.setText( Messages.getString( "org.kalypso.ogc.gml.om.table.celleditor.DateTimeDialog.0" ) ); //$NON-NLS-1$
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createContents( final Composite parent )
  {
    final Control contents = super.createContents( parent );

    setTitle( Messages.getString( "org.kalypso.ogc.gml.om.table.celleditor.DateTimeDialog.1" ) ); //$NON-NLS-1$
    setMessage( null );

    return contents;
  }

  /**
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea( final Composite parent )
  {
    final Composite composite = (Composite)super.createDialogArea( parent );
    composite.setLayout( new GridLayout() );
    final GridData data = new GridData( GridData.FILL, GridData.FILL, true, true );
    data.heightHint = 300;
    data.widthHint = 100;

    composite.setLayoutData( data );

    /* date */
    final SWTCalendar calendar = new SWTCalendar( composite, SWT.FLAT );
    calendar.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );

    final Calendar preSettedTime;

    if( m_preSettedDateTime == null )
      preSettedTime = null;
    else
    {
      preSettedTime = Calendar.getInstance( m_displayTimeZone );
      preSettedTime.setTime( m_preSettedDateTime );
    }

    if( preSettedTime != null )
      calendar.setCalendar( preSettedTime );

    /* time of day */
    final Label lTime = new Label( composite, SWT.NONE );
    lTime.setText( Messages.getString( "org.kalypso.ogc.gml.om.table.celleditor.DateTimeDialog.2" ) ); //$NON-NLS-1$

    final DateTime time = new DateTime( composite, SWT.TIME );

    time.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );
    if( preSettedTime != null )
    {
      time.setDay( preSettedTime.get( Calendar.DAY_OF_MONTH ) );
      time.setMonth( preSettedTime.get( Calendar.MONTH ) );
      time.setYear( preSettedTime.get( Calendar.YEAR ) );
      time.setHours( preSettedTime.get( Calendar.HOUR_OF_DAY ) );
      time.setMinutes( preSettedTime.get( Calendar.MINUTE ) );
      time.setSeconds( preSettedTime.get( Calendar.SECOND ) );
    }

    /* listeners */
    calendar.addSWTCalendarListener( new SWTCalendarListener()
    {
      @Override
      public void dateChanged( final SWTCalendarEvent event )
      {
        setDateTime( calendar, time );
      }
    } );

    time.addSelectionListener( new SelectionAdapter()
    {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        setDateTime( calendar, time );
      }
    } );

    setDateTime( calendar, time );

    return composite;
  }

  protected void setDateTime( final SWTCalendar calendar, final DateTime time )
  {
    final Calendar date = calendar.getCalendar();

    final int day = date.get( Calendar.DAY_OF_MONTH );
    final int month = date.get( Calendar.MONTH );
    final int year = date.get( Calendar.YEAR );

    final int hours = time.getHours();
    final int minutes = time.getMinutes();
    final int seconds = time.getSeconds();

    /* Create calendar with the display time zone */
    // this is necessary, because the SWTCalendar cannot handle any time zone.
    // So we have to convert the date for it by our own.
    final GregorianCalendar gregorianCalendar = new GregorianCalendar( m_displayTimeZone );
    gregorianCalendar.set( year, month, day, hours, minutes, seconds );

    m_gregorianCalendar = gregorianCalendar.getTime();
  }

  public Date getDateTime( )
  {
    return m_gregorianCalendar;
  }

  public void setDateTime( final Date dateTime )
  {
    m_preSettedDateTime = dateTime;
  }

}
