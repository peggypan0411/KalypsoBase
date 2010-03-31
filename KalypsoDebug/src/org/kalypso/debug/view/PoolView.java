/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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

package org.kalypso.debug.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.util.pool.IPoolListener;
import org.kalypso.core.util.pool.IPoolableObjectType;
import org.kalypso.core.util.pool.KeyInfo;
import org.kalypso.core.util.pool.ResourcePool;

/**
 * Zeigt den Inhalt des {@link PoolView}in Form einer Tabelle an. Die Tabelle wird alle 2 Sekunden aktualisiert.
 * 
 * @author belger
 */
public class PoolView extends ViewPart implements ITableLabelProvider
{
  private static abstract class PoolViewColumn
  {
    public final int width;

    public final String text;

    public PoolViewColumn( final String txt, final IMemento memento )
    {
      this.text = txt;

      final Integer mementoWidth = memento == null ? null : memento.getInteger( txt );
      this.width = mementoWidth == null ? 100 : mementoWidth.intValue();
    }

    public abstract String getValue( final KeyInfo info );
  }

  private final List<PoolViewColumn> m_columns = new ArrayList<PoolViewColumn>( 3 );

  private final ResourcePool m_pool = KalypsoCorePlugin.getDefault().getPool();

  private final TimerTask m_task;

  private final Timer m_timer;

  private TableViewer m_viewer;

  public PoolView( )
  {
    m_task = new TimerTask()
    {
      @Override
      public void run( )
      {
        updateView();
      }
    };

    m_timer = new Timer();
    m_timer.schedule( m_task, 1000, 1000 );
  }

  protected void updateView( )
  {
    final TableViewer viewer = m_viewer;
    final ResourcePool pool = m_pool;

    if( (m_pool == null) || (viewer == null) )
      return;

    final Table table = viewer.getTable();
    if( (table != null) && !table.isDisposed() )
      table.getDisplay().asyncExec( new Runnable()
      {
        public void run( )
        {
          // to avoid exception when window is closed test for content provider
          if( viewer.getContentProvider() != null )
            viewer.setInput( pool.getInfos() );
        }
      } );
  }

  /**
   * @see org.eclipse.ui.part.WorkbenchPart#dispose()
   */
  @Override
  public void dispose( )
  {
    m_task.cancel();
    m_timer.cancel();
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl( final Composite parent )
  {
    m_viewer = new TableViewer( parent, SWT.NONE );
    m_viewer.setContentProvider( new ArrayContentProvider() );
    m_viewer.setLabelProvider( this );

    m_viewer.addOpenListener( new IOpenListener()
    {
      public void open( final OpenEvent event )
      {
        final ISelection selection = event.getSelection();
        if( !selection.isEmpty() && (selection instanceof IStructuredSelection) )
        {
          final KeyInfo info = (KeyInfo) ((IStructuredSelection) selection).getFirstElement();

          final IPoolListener[] listeners = info.getPoolListeners();

          final LabelProvider labelProvider = new LabelProvider()
          {
            /**
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             */
            @Override
            public String getText( final Object element )
            {
              if( element != null )
                return super.getText( element ) + " (" + element.getClass().getName() + ")";
              return super.getText( element );
            }
          };
          final ListSelectionDialog dialog = new ListSelectionDialog( event.getViewer().getControl().getShell(), listeners, new ArrayContentProvider(), labelProvider, "Listeners for KeyInfo: " + info );
          if( dialog.open() == dialog.OK )
            printAddTraces( info, dialog.getResult() );
        }
      }
    } );

    final Table table = m_viewer.getTable();
    table.setToolTipText( "Doppelklick f�r Liste der Listener" );
    table.setHeaderVisible( true );

    for( final Object element : m_columns )
    {
      final PoolViewColumn col = (PoolViewColumn) element;

      final TableColumn keytc = new TableColumn( table, SWT.BEGINNING );
      keytc.setText( col.text );
      keytc.setWidth( col.width );
    }
  }

  protected void printAddTraces( final KeyInfo info, final Object[] result )
  {
    for( final Object object : result )
    {
      if( object instanceof IPoolListener )
      {
        final Exception addTrace = info.getAddTrace( (IPoolListener) object );
        System.out.println( "Listener: " + object.toString() );
        addTrace.printStackTrace();
        System.out.println();
      }
    }
  }

  /**
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  @Override
  public void setFocus( )
  {
    // egal
  }

  /**
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  public void addListener( final ILabelProviderListener listener )
  {
    // egal
  }

  /**
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
   */
  public boolean isLabelProperty( final Object element, final String property )
  {
    return true;
  }

  /**
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  public void removeListener( final ILabelProviderListener listener )
  {
    // egal

  }

  /**
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
   */
  public Image getColumnImage( final Object element, final int columnIndex )
  {
    return null;
  }

  /**
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
   */
  public String getColumnText( final Object element, final int columnIndex )
  {
    final KeyInfo info = (KeyInfo) element;

    final PoolViewColumn column = m_columns.get( columnIndex );
    return column.getValue( info );
  }

  /**
   * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
   */
  @Override
  public void init( final IViewSite site, final IMemento memento ) throws PartInitException
  {
    super.init( site, memento );

    m_columns.add( new PoolViewColumn( "Key", memento )
    {
      @Override
      public String getValue( final KeyInfo info )
      {
        final IPoolableObjectType key = info.getKey();
        return "" + key.getLocation();
      }
    } );

    m_columns.add( new PoolViewColumn( "Listener", memento )
    {
      @Override
      public String getValue( final KeyInfo info )
      {
        return "" + info.getPoolListeners().length;
      }
    } );

    m_columns.add( new PoolViewColumn( "Object", memento )
    {
      @Override
      public String getValue( final KeyInfo info )
      {
        return "" + info.getObject();
      }
    } );

    m_columns.add( new PoolViewColumn( "isDirty", memento )
    {
      @Override
      public String getValue( final KeyInfo info )
      {
        return "" + info.isDirty();
      }
    } );
  }

  /**
   * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
   */
  @Override
  public void saveState( final IMemento memento )
  {
    super.saveState( memento );

    final Table table = m_viewer.getTable();
    final TableColumn[] columns = table.getColumns();
    for( final TableColumn column : columns )
      memento.putInteger( column.getText(), column.getWidth() );
  }
}