/*--------------- Kalypso-Header --------------------------------------

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

 -----------------------------------------------------------------------*/
package org.kalypso.ogc.sensor.tableview.swing.tablemodel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.ObjectUtils;
import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.ObservationUtilities;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.status.KalypsoStati;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.ogc.sensor.tableview.TableViewColumn;
import org.kalypso.ogc.sensor.tableview.rules.ITableViewRules;
import org.kalypso.ogc.sensor.tableview.rules.RenderingRule;
import org.kalypso.ogc.sensor.tableview.rules.RulesFactory;
import org.kalypso.ogc.sensor.timeseries.AxisUtils;
import org.kalypso.ogc.sensor.timeseries.TimeseriesUtils;
import org.kalypso.ogc.sensor.timeseries.datasource.DataSourceHandler;
import org.kalypso.repository.IDataSourceItem;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * table model which handles an IObservation. Can be used by JTable
 * <p>
 * Changes made in the table are directly reflected into the observations models.
 * 
 * @author schlienger
 */
public class ObservationTableModel extends AbstractTableModel implements IObservationTableModel
{
  private static final RenderingRule[] EMPTY_RENDERING_RULES = new RenderingRule[0];

  /** rendering rules, defaults to some standard rules */
  private ITableViewRules m_rules = RulesFactory.getDefaultRules();

  /** the shared model is sorted, it contains all key values */
  private final SortedSet<Object> m_sharedModel = new TreeSet<>();

  /** it contains all the "value" columns */
  private final List<TableViewColumn> m_columns = new ArrayList<>();

  /** the shared axis is the axis which is common to all "value" columns */
  private IAxis m_sharedAxis = null;

  /** alphabetical sort order is activated by default */
  private boolean m_alphaSort = true;

  private final static Logger m_logger = Logger.getLogger( ObservationTableModel.class.getName() );

  /**
   * Adds a column at the end of the table.
   */
  public void addColumn( final TableViewColumn col ) throws SensorException
  {
    synchronized( m_columns )
    {
      final TableViewColumn[] cols = m_columns.toArray( new TableViewColumn[m_columns.size()] );

      int i = 0;

      if( m_alphaSort )
      {
        // find good position for column (alphabetically sorted)
        i = Arrays.binarySearch( cols, col, ColOrderNameComparator.INSTANCE );
        if( i < 0 )
          i = -i - 1;
      }
      else
      {
        if( col.isDefaultPosition() )
        {
          // add column at last position
          i = cols.length;
        }
        else
        {
          // find good position for column according to its position
          i = Arrays.binarySearch( cols, col, ColOrderIndexComparator.INSTANCE );
          if( i < 0 )
            i = -i - 1;
        }
      }

      addColumn( col, i );
    }
  }

  /**
   * Adds a column to this table model at the given position
   */
  private void addColumn( final TableViewColumn col, final int pos ) throws SensorException
  {
    synchronized( m_columns )
    {
      // test if column index is -1, in that case do nothing. This might happen when
      // concurrent updates are done on the table: a column is being removed despite
      // the fact it's not there (anymore).
      if( col == null || !col.isShown() || pos == -1 )
        return;

      final IAxis keyAxis = col.getKeyAxis();

      if( m_sharedAxis == null )
        m_sharedAxis = keyAxis;
      else
      {
        // verify compatibility of the axes. We do not use IAxis.equals()
        // since the position is not relevant here
        if( m_sharedAxis.getDataClass() != keyAxis.getDataClass() || !m_sharedAxis.getUnit().equals( keyAxis.getUnit() ) || !m_sharedAxis.getType().equals( keyAxis.getType() ) )
        {
          throw new SensorException( m_sharedAxis
              + Messages.getString( "org.kalypso.ogc.sensor.tableview.swing.ObservationTableModel.0" ) + keyAxis + Messages.getString( "org.kalypso.ogc.sensor.tableview.swing.ObservationTableModel.1" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }

      // values of observation of the column
      final IObservation obs = col.getObservation();
      final IAxis axis = col.getValueAxis();
      if( obs == null || axis == null )
        return;

      if( !m_columns.contains( col ) )
      {
        m_columns.add( pos, col );

        // adapt width of column
        // TODO: listen for column width changes (initiated by the user) and
        // store it in the template when saving it
        // final int colIx = m_valuesModel.findColumn( col.getName() );
        // final int colIx = findColumn( col.getName() );
        // final TableColumn tableColumn = m_table.getColumnModel().getColumn(
        // colIx );
        // tableColumn.setPreferredWidth( col.getWidth() );
      }

      final ITupleModel tupModel = col.getValues();

      // fill shared column values
      for( int r = 0; r < tupModel.size(); r++ )
      {
        final Object elt = tupModel.get( r, keyAxis );
        m_sharedModel.add( elt );
      }
    }

    // !WARNING! never fire within synchronized block
    // -> dead-lock!
    fireTableStructureChanged();
  }

  public void refreshColumn( final TableViewColumn column, final Object sourceObject ) throws SensorException
  {
    synchronized( m_columns )
    {
      if( sourceObject == this )
        return;

      final int pos = removeColumn( column );
      addColumn( column, pos );
    }
  }

  /**
   * Returns the status axis for the 'normal' axis found in the given column.
   * 
   * @return status axis or null if not found
   */
  private IAxis getStatusAxis( final IObservation obs, final IAxis axis )
  {
    final IAxis[] obsAxes = obs.getAxes();
    final String statusAxisLabel = KalypsoStatusUtils.getStatusAxisNameFor( axis );
    final IAxis statusAxis = ObservationUtilities.findAxisByNameNoEx( obsAxes, statusAxisLabel );

    return statusAxis;
  }

  /**
   * Return the TableViewColumn for the given index
   */
  public Object getColumnValue( final int columnIndex )
  {
    synchronized( m_columns )
    {
      if( m_columns.size() == 0 )
        return null;

      if( isDateColumn( columnIndex ) )
        return m_sharedAxis;

      return m_columns.get( columnIndex - 1 );
    }
  }

  @Override
  public Class< ? > getColumnClass( final int columnIndex )
  {
    synchronized( m_columns )
    {
      if( m_columns.size() == 0 )
        return String.class;

      if( isDateColumn( columnIndex ) )
        return m_sharedAxis.getDataClass();

      return m_columns.get( columnIndex - 1 ).getColumnClass();
    }
  }

  @Override
  public String getColumnName( final int columnIndex )
  {
    synchronized( m_columns )
    {
      if( m_columns.size() == 0 )
        return Messages.getString( "org.kalypso.ogc.sensor.tableview.swing.ObservationTableModel.2" ); //$NON-NLS-1$

      if( isDateColumn( columnIndex ) )
        return m_sharedAxis.getName();

      return m_columns.get( columnIndex - 1 ).getName();
    }
  }

  @Override
  public int getColumnCount( )
  {
    synchronized( m_columns )
    {
      if( m_columns.size() == 0 )
        return 1;

      return m_columns.size() + 1;
    }
  }

  @Override
  public int getRowCount( )
  {
    return m_sharedModel.size();
  }

  @Override
  public Object getValueAt( final int rowIndex, final int columnIndex )
  {
    synchronized( m_columns )
    {
      try
      {
        final Object key = m_sharedModel.toArray()[rowIndex];

        if( isDateColumn( columnIndex ) )
          return key;

        try
        {
          final TableViewColumn col = m_columns.get( columnIndex - 1 );
          final ITupleModel values = col.getValues();
          final int ix = values.indexOf( key, col.getKeyAxis() );
          if( ix != -1 )
            return values.get( ix, col.getValueAxis() );

          return null;
        }
        catch( final SensorException e )
        {
          e.printStackTrace();
          throw new IllegalStateException( e.getLocalizedMessage() );
        }
      }
      catch( final ArrayIndexOutOfBoundsException e )
      {
        // TRICKY: Ich weiss nicht warum, diese Exception taucht auf wenn die
        // Berechnung im Wizard gestartet wird. Ich denke, es liegt daran, dass
        // im Hintergrund die Zeitreihen aktualisiert werden, der Pool schickt
        // das Event, und die Tabelle versucht sich zu aktualisieren: dabei
        // ist der Model nicht mehr genau in Sync mit die Daten
        // WORKAROUND: wir ignorieren die Exception ganz leise und geben
        // einfach null zur�ck.
        return null;
      }
    }
  }

  @Override
  public boolean isCellEditable( final int rowIndex, final int columnIndex )
  {
    synchronized( m_columns )
    {
      if( isDateColumn( columnIndex ) )
        return false;

      return m_columns.get( columnIndex - 1 ).isEditable();
    }
  }

  /**
   * TODO: real real ugly: the column inedx here and in {@link #isCellEditable(int, int)} are interpreted differently,
   * obviously they are used from different contexts (with/without first key-column)
   * 
   * @see org.kalypso.ogc.sensor.tableview.swing.tablemodel.IObservationTableModel#isEditable(int)
   */
  @Override
  public boolean isEditable( final int column )
  {
    synchronized( m_columns )
    {
      return m_columns.get( column ).isEditable();
    }
  }

  /**
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
   */
  @Override
  public void setValueAt( final Object changedValue, final int rowIndex, final int columnIndex )
  {
    synchronized( m_columns )
    {
      /*
       * TRICKY: if value is null, do nothing (NOTE: the DoubleCellEditor used within our ObservationTable returns null
       * when editing has been canceled and in our case this means: don't modify the status)
       */
      if( changedValue == null )
        return;
      /* date column is not editable! */
      else if( isDateColumn( columnIndex ) )
        return;
      else if( !isEditable( columnIndex - 1 ) )
        throw new IllegalStateException( Messages.getString( "ObservationTableModel.0" ) ); //$NON-NLS-1$

      final TableViewColumn col = m_columns.get( columnIndex - 1 );

      try
      {
        final IObservation observation = col.getObservation();

        // request argument must be null - otherwise we are editing an different model and so the changes will get lost
        final ITupleModel model = observation.getValues( null );
        final Object key = m_sharedModel.toArray()[rowIndex];
        final int ix = model.indexOf( key, col.getKeyAxis() );
        if( ix != -1 )
        {
          final IAxis valueAxis = col.getValueAxis();
          final IAxis statusAxis = getStatusAxis( observation, valueAxis );

          final Object oldValue = model.get( ix, valueAxis );
          if( !checkValuesEqual( changedValue, oldValue ) )
          {
            /* Only change value if really something has happened */

            // first set status (may be overwritten)
            if( statusAxis != null )
              model.set( ix, statusAxis, KalypsoStati.STATUS_USERMOD );

            changeDataSource( observation, model, ix, IDataSourceItem.SOURCE_MANUAL_CHANGED, valueAxis );

            // then set value
            model.set( ix, valueAxis, changedValue );
          }
        }
        else
          m_logger.info( Messages.getString( "org.kalypso.ogc.sensor.tableview.swing.ObservationTableModel.3" ) ); //$NON-NLS-1$
      }
      catch( final SensorException e )
      {
        e.printStackTrace();
        throw new IllegalStateException( e.getLocalizedMessage() );
      }
    }
  }

  private void changeDataSource( final IObservation observation, final ITupleModel model, final int index, final String source, final IAxis valueAxis ) throws SensorException
  {
    final IAxis dataSourceAxis = AxisUtils.findDataSourceAxis( model.getAxes(), valueAxis );
    if( dataSourceAxis == null )
      return; // we didn't want to change the model

    final DataSourceHandler handler = new DataSourceHandler( observation.getMetadataList() );
    final int dataSourceIndex = handler.addDataSource( source, source );

    model.set( index, dataSourceAxis, Integer.valueOf( dataSourceIndex ) );
  }

  private boolean isDateColumn( final int columnIndex )
  {
    return columnIndex == 0;
  }

  /**
   * Checks if two value are equal in the sense of the table model.
   */
  private boolean checkValuesEqual( final Object oldValue, final Object newValue )
  {
    /* Special case: if they are numbers, compare by a (very small) margin */
    if( newValue instanceof Number && oldValue instanceof Number )
    {
      final double oldDouble = ((Number)oldValue).doubleValue();
      final double newDouble = ((Number)newValue).doubleValue();
      // REAMRK: we are using a fixed epsilon here, but this should be small enough for all cases...
      return Math.abs( oldDouble - newDouble ) < 0.0000001;
    }

    return ObjectUtils.equals( oldValue, newValue );
  }

  /**
   * Sets the rules. Important: you should call this method if you want the rules rendering to be enabled.
   * 
   * @param rules
   */
  public void setRules( final ITableViewRules rules )
  {
    m_rules = rules;
  }

  /**
   * Set the flag for alphabetical sorting order. If true, columns are sorted according to their name in alphabetical
   * order.
   */
  public void setAlphaSort( final boolean alphaSort )
  {
    m_alphaSort = alphaSort;
  }

  /**
   * Finds the renderingrules for the given element.
   * 
   * @return rendering rules or empty array if no rules found.
   */
  @Override
  public RenderingRule[] findRules( final int row, final int column )
  {
    synchronized( m_columns )
    {
      // if( column == 0 )
      // return EMPTY_RENDERING_RULES;

      // REMARK: in spite of synchronizing we still get ArrayOutOfBoundsException's here....
      // So range check and return default value
      if( column > m_columns.size() - 1 )
        return EMPTY_RENDERING_RULES;

      final TableViewColumn col = m_columns.get( column );
      try
      {
        final ITupleModel values = col.getValues();
        final Object key = m_sharedModel.toArray()[row];
        final int ix = values.indexOf( key, col.getKeyAxis() );
        if( ix != -1 )
        {
          final IAxis statusAxis = getStatusAxis( col.getObservation(), col.getValueAxis() );
          if( statusAxis != null )
          {
            final Number status = (Number)values.get( ix, statusAxis );

            if( status != null )
              return m_rules.findRules( status );
          }
        }

        return EMPTY_RENDERING_RULES;
      }
      catch( final SensorException e )
      {
        e.printStackTrace();
        throw new IllegalStateException( e.getLocalizedMessage() );
      }
    }
  }

  /**
   * Clears the columns of the model.
   */
  public void clearColumns( )
  {
    synchronized( m_columns )
    {
      m_columns.clear();
      m_sharedModel.clear();
      m_sharedAxis = null;
    }

    fireTableStructureChanged();
  }

  /**
   * Removes one column
   * 
   * @return position of the column that has just been removed
   */
  public int removeColumn( final TableViewColumn col ) throws SensorException
  {
    synchronized( m_columns )
    {
      final int pos = m_columns.indexOf( col );

      m_columns.remove( col );

      final List<TableViewColumn> cols = new ArrayList<>( m_columns );
      clearColumns();

      for( final TableViewColumn tableViewColumn : cols )
        addColumn( tableViewColumn );

      return pos;
    }
  }

  public boolean containsColumn( final TableViewColumn col )
  {
    synchronized( m_columns )
    {
      return m_columns.contains( col );
    }
  }

  /**
   * Returns the corresponding NumberFormat for the given column. The NumberFormat is type dependent. For instance,
   * Water-level is displayed differently than Rainfall.
   * 
   * @return adequate instance of NumberFormat
   * @see org.kalypso.ogc.sensor.timeseries.TimeserieUtils#getNumberFormatFor(String)
   */
  @Override
  public NumberFormat getNumberFormat( final int column )
  {
    synchronized( m_columns )
    {
      final TableViewColumn col = m_columns.get( column );
      return TimeseriesUtils.getNumberFormat( col.getFormat() );
    }
  }

  /**
   * Compares TableViewColumns according to their names
   * 
   * @author schlienger
   */
  private static class ColOrderNameComparator implements Comparator<TableViewColumn>
  {
    public final static ColOrderNameComparator INSTANCE = new ColOrderNameComparator();

    @Override
    public int compare( final TableViewColumn col1, final TableViewColumn col2 )
    {
      return col1.getName().compareTo( col2.getName() );
    }
  }

  /**
   * Compares TableViewColumns according to their position
   * 
   * @author schlienger
   */
  private static class ColOrderIndexComparator implements Comparator<TableViewColumn>
  {
    public final static ColOrderIndexComparator INSTANCE = new ColOrderIndexComparator();

    @Override
    public int compare( final TableViewColumn col1, final TableViewColumn col2 )
    {
      return col1.getPosition() - col2.getPosition();
    }
  }

  /**
   * Exports the contents of the model
   */
  public void dump( final String separator, final BufferedWriter writer ) throws IOException
  {
    if( m_sharedModel.isEmpty() )
      return;

    final Object checkObject = m_sharedModel.first();

    // will be used for formating the various columns
    final Format[] nf = new Format[m_columns.size() + 1];

    // find appropriate format for shared column (Attention: can still be null)
    if( checkObject instanceof Date )
      nf[0] = TimeseriesUtils.getDateFormat();
    else if( checkObject instanceof Integer )
      nf[0] = NumberFormat.getIntegerInstance();
    else if( checkObject instanceof Number )
      nf[0] = NumberFormat.getNumberInstance();

    // dump header and fetch numberformats
    writer.write( m_sharedAxis.getName() );
    if( nf[0] instanceof DateFormat )
    {
      final DateFormat df = (DateFormat)nf[0];
      final TimeZone timeZone = df.getTimeZone();
      if( timeZone != null )
      {
        writer.write( " (" ); //$NON-NLS-1$
        writer.write( timeZone.getID() );
        writer.write( ")" ); //$NON-NLS-1$
      }
    }

    int col = 1;
    for( final Object element : m_columns )
    {
      final TableViewColumn tvc = (TableViewColumn)element;

      nf[col] = TimeseriesUtils.getNumberFormat( tvc.getFormat() );

      writer.write( separator );
      writer.write( tvc.getName() );

      col++;
    }

    writer.newLine();

    // dump values
    int row = 0;
    for( final Object key : m_sharedModel )
    {
      writer.write( nf[0] == null ? key.toString() : nf[0].format( key ) );

      col = 1;
      for( ; col < getColumnCount(); col++ )
      {
        final Object value = getValueAt( row, col );

        writer.write( separator );

        if( nf[col] != null && value != null )
          writer.write( nf[col].format( value ) );
        else
          writer.write( "" ); //$NON-NLS-1$
      }

      writer.newLine();
      row++;
    }
  }

  /**
   * Called to update the displayed columns by removing the one that should be ignored. This method should only be
   * called by ObservationTable.
   */
  protected void hideIgnoredTypes( )
  {
    synchronized( m_columns )
    {

    }
  }

  public IAxis getSharedAxis( )
  {
    return m_sharedAxis;
  }

  /**
   * Returns the row index of the given shared object.<br>
   * Careful, this is a linear list search, so it is quite slow.
   * 
   * @return -1, if the given key was not found.
   */
  public int indexOfKey( final Object sharedKey )
  {
    int index = 0;
    for( final Object key : m_sharedModel )
    {
      if( ObjectUtils.equals( sharedKey, key ) )
        return index;

      index++;
    }

    return -1;
  }
}