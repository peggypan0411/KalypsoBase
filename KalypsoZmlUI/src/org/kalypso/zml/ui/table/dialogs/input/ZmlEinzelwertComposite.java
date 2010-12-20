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
package org.kalypso.zml.ui.table.dialogs.input;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.progress.UIJob;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.jface.validators.IntegerInputValidator;
import org.kalypso.contribs.eclipse.swt.layout.LayoutHelper;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.zml.ui.KalypsoZmlUI;
import org.kalypso.zml.ui.table.base.widgets.EnhancedComboViewer;
import org.kalypso.zml.ui.table.base.widgets.EnhancedTextBox;
import org.kalypso.zml.ui.table.base.widgets.rules.DateWidgetRule;
import org.kalypso.zml.ui.table.base.widgets.rules.DoubeValueWidgetRule;
import org.kalypso.zml.ui.table.base.widgets.rules.TimeWidgetRule;

/**
 * @author Dirk Kuch
 */
public class ZmlEinzelwertComposite extends Composite implements IZmlEinzelwertModelListener
{
  private static final Image IMG_ADD = new Image( null, ZmlEinzelwertComposite.class.getResourceAsStream( "icons/add.png" ) );

  private static final Image IMG_REMOVE = new Image( null, ZmlEinzelwertComposite.class.getResourceAsStream( "icons/remove.png" ) );

  protected final ZmlEinzelwertModel m_model;

  private Composite m_base;

  private final FormToolkit m_toolkit;

  public ZmlEinzelwertComposite( final Composite parent, final FormToolkit toolkit, final ZmlEinzelwertModel model )
  {
    super( parent, SWT.NULL );
    m_toolkit = toolkit;
    m_model = model;

    setLayout( LayoutHelper.createGridLayout() );

    render();
    toolkit.adapt( this );

    m_model.addListener( this );
  }

  protected void render( )
  {
    if( isDisposed() )
      return;

    if( m_base != null && !m_base.isDisposed() )
      m_base.dispose();

    m_base = m_toolkit.createComposite( this );
    m_base.setLayout( LayoutHelper.createGridLayout( 5 ) );
    m_base.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );

    m_toolkit.createLabel( m_base, "Datum" );
    m_toolkit.createLabel( m_base, "Uhrzeit" );
    m_toolkit.createLabel( m_base, "" );
    m_toolkit.createLabel( m_base, "Wert" );
    m_toolkit.createLabel( m_base, "" );

    final ZmlEinzelwert[] rows = m_model.getRows();
    for( final ZmlEinzelwert row : rows )
    {
      addRow( m_base, m_toolkit, row );
    }

    this.layout();
  }

  private void addRow( final Composite base, final FormToolkit toolkit, final ZmlEinzelwert row )
  {
    try
    {
      final Date date = row.getDate();
      final Date[] existing = m_model.getExistingDateValues();
      final Date[] dayAnchors = getDayAnchors( existing );

      final EnhancedComboViewer<Date> viewerDay = new EnhancedComboViewer<Date>( base, toolkit, new DateWidgetRule() );
      viewerDay.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );
      viewerDay.setInput( dayAnchors );
      viewerDay.setSelection( dayAnchors[0] );

      final EnhancedComboViewer<Date> viewerTime = new EnhancedComboViewer<Date>( base, toolkit, new TimeWidgetRule() );
      viewerTime.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );
      viewerTime.setFilter( new ViewerFilter()
      {
        @Override
        public boolean select( final Viewer viewer, final Object parentElement, final Object element )
        {
          if( element instanceof Date )
          {
            final Date d = (Date) element;

            final Date selection = viewerDay.getSelection();
            if( selection != null )
            {
              return ZmlEinzelwertHelper.compareDayAnchor( d, selection );
            }
          }

          return false;
        }
      } );

      viewerTime.setInput( existing );
      viewerTime.setSelection( date );

// viewerDay.addListener( new ISelectionChangedListener()
// {
// @Override
// public void selectionChanged( final SelectionChangedEvent event )
// {
// viewerTime.refresh();
// }
// } );

      viewerDay.addListener( new ISelectionChangedListener()
      {
        @Override
        public void selectionChanged( final SelectionChangedEvent event )
        {
          final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
          final Date selected = (Date) selection.getFirstElement();

          final Calendar baseCalendar = Calendar.getInstance();
          baseCalendar.setTime( date );

          final Calendar calendar = Calendar.getInstance();
          calendar.setTime( selected );

          calendar.set( Calendar.HOUR_OF_DAY, baseCalendar.get( Calendar.HOUR_OF_DAY ) );
          calendar.set( Calendar.MINUTE, baseCalendar.get( Calendar.MINUTE ) );
          calendar.set( Calendar.SECOND, baseCalendar.get( Calendar.SECOND ) );
          calendar.set( Calendar.MILLISECOND, baseCalendar.get( Calendar.MILLISECOND ) );

          // FIXME doesn't work
          row.setDate( calendar.getTime() );
        }
      } );

      viewerTime.addListener( new ISelectionChangedListener()
      {
        @Override
        public void selectionChanged( final SelectionChangedEvent event )
        {
          final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
          final Date selected = (Date) selection.getFirstElement();
          row.setDate( selected );
        }
      } );

      toolkit.createLabel( base, "" ).setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );

      final EnhancedTextBox<Double> textBox = new EnhancedTextBox<Double>( base, toolkit, new DoubeValueWidgetRule() );
      textBox.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );
      textBox.setText( row.getValue() );

      final ImageHyperlink lnkAdd = toolkit.createImageHyperlink( base, SWT.BEGINNING );
      lnkAdd.setLayoutData( new GridData( GridData.FILL, GridData.FILL, false, false ) );
      lnkAdd.setImage( IMG_ADD );
      lnkAdd.addHyperlinkListener( new HyperlinkAdapter()
      {
        /**
         * @see org.eclipse.ui.forms.events.HyperlinkAdapter#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
         */
        @Override
        public void linkActivated( final HyperlinkEvent e )
        {
          final InputDialog dialog = new InputDialog( lnkAdd.getShell(), "Schrittweite", "Bitte geben Sie den Stunden-Offset (Schrittweite) des nächsten Wertes an", "1", new IntegerInputValidator() );
          final int status = dialog.open();
          if( Window.OK == status )
          {
            try
            {
              final Integer stepping = Integer.valueOf( dialog.getValue() );

              m_model.addRow( date, stepping );
            }
            catch( final Throwable t )
            {
              KalypsoZmlUI.getDefault().getLog().log( StatusUtilities.statusFromThrowable( t ) );
            }
          }

        }
      } );
    }
    catch( final SensorException e )
    {
      KalypsoZmlUI.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
    }
  }

  private Date[] getDayAnchors( final Date[] existing )
  {
    final Set<Date> anchors = new TreeSet<Date>();

    for( final Date date : existing )
    {
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime( date );

      calendar.set( Calendar.HOUR_OF_DAY, 0 );
      calendar.set( Calendar.MINUTE, 0 );
      calendar.set( Calendar.MILLISECOND, 0 );

      anchors.add( calendar.getTime() );
    }

    return anchors.toArray( new Date[] {} );
  }

  /**
   * @see org.kalypso.zml.ui.table.dialogs.input.IZmlEinzelwertModelListener#modelChangedEvent()
   */
  @Override
  public void modelChangedEvent( )
  {
    new UIJob( "" )
    {
      @Override
      public final IStatus runInUIThread( final IProgressMonitor monitor )
      {
        render();

        return Status.OK_STATUS;
      }
    }.schedule();
  }
}
