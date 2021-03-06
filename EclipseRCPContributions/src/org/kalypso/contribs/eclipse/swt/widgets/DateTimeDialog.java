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
package org.kalypso.contribs.eclipse.swt.widgets;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Dirk Kuch
 * @author Gernot Belger
 */
public class DateTimeDialog extends Dialog
{
  private TimeZone m_displayTimeZone;

  private Date m_gregorianCalendar;

  private Date m_preSettedDateTime;

  private String m_windowTitle;

  private final int m_timeStyle;

  /**
   * @param timeStyle
   *          Style bits for the time control. One of {@link SWT#SHORT}, {@link SWT#MEDIUM} or {@link SWT#LONG}, .
   */
  public DateTimeDialog( final Shell parent, final int timeStyle )
  {
    super( parent );

    m_windowTitle = "DateTime";
    m_timeStyle = timeStyle;

    setBlockOnOpen( true );
  }

  public void setDisplayTimeZone( final TimeZone displayTimeZone )
  {
    m_displayTimeZone = displayTimeZone;
  }

  /**
   * Sets the title of the dialog window. Must be called before {@link #create()}.
   */
  public void setWindowTitle( final String title )
  {
    m_windowTitle = title;
  }

  @Override
  protected void configureShell( final Shell newShell )
  {
    super.configureShell( newShell );

    newShell.setText( m_windowTitle );
  }

  @Override
  protected Control createDialogArea( final Composite parent )
  {
    final Composite composite = (Composite) super.createDialogArea( parent );
    GridLayoutFactory.fillDefaults().applyTo( composite );

    final Calendar preSettedTime = fetchPreSettedTime();

    /* date */
    final DateTime calendar = createDateTime( composite, SWT.CALENDAR, preSettedTime );
    calendar.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

    /* time of day */
    final DateTime time = createDateTime( composite, SWT.TIME | m_timeStyle, preSettedTime );
    time.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );

    /* listeners */
    calendar.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( final SelectionEvent event )
      {
        setDateTime( calendar, time );
      }
    } );

    time.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        setDateTime( calendar, time );
      }
    } );

    setDateTime( calendar, time );

    return composite;
  }

  private Calendar fetchPreSettedTime( )
  {
    if( m_preSettedDateTime == null )
      return null;

    final Calendar preSettedTime = Calendar.getInstance( m_displayTimeZone );
    preSettedTime.setTime( m_preSettedDateTime );
    return preSettedTime;
  }

  private DateTime createDateTime( final Composite composite, final int style, final Calendar cal )
  {
    final DateTime dateTime = new DateTime( composite, style );

    if( cal == null )
      return dateTime;

    dateTime.setDay( cal.get( Calendar.DAY_OF_MONTH ) );
    dateTime.setMonth( cal.get( Calendar.MONTH ) );
    dateTime.setYear( cal.get( Calendar.YEAR ) );
    dateTime.setHours( cal.get( Calendar.HOUR_OF_DAY ) );
    dateTime.setMinutes( cal.get( Calendar.MINUTE ) );
    dateTime.setSeconds( cal.get( Calendar.SECOND ) );

    return dateTime;
  }

  protected void setDateTime( final DateTime calendar, final DateTime time )
  {
    final int day = calendar.getDay();
    final int month = calendar.getMonth();
    final int year = calendar.getYear();

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
