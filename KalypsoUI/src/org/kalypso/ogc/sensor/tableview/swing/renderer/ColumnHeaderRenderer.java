/*--------------- Kalypso-Header ------------------------------------------

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

 --------------------------------------------------------------------------*/

package org.kalypso.ogc.sensor.tableview.swing.renderer;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.kalypso.ogc.sensor.IAxis;
import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.ITupleModel;
import org.kalypso.ogc.sensor.SensorException;
import org.kalypso.ogc.sensor.status.KalypsoStatusUtils;
import org.kalypso.ogc.sensor.tableview.TableViewColumn;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * A table cell renderer for table headings.
 * <P>
 * This class (and also BevelArrowIcon) is adapted from original code by jcommons (version 0.9.6) posted on http://www.jfree.org/jcommon/.
 */
public class ColumnHeaderRenderer implements TableCellRenderer
{
  /** The current pressed column (-1 for no column). */
  private int m_pressedColumn = -1;

  /** The button that is used to render the table header cells. */
  private final JButton m_button;

  /**
   * Creates a new button renderer.
   */
  public ColumnHeaderRenderer( )
  {
    m_pressedColumn = -1;

    final Border border = UIManager.getBorder( "TableHeader.cellBorder" ); //$NON-NLS-1$

    m_button = new JButton();
    m_button.setMargin( new Insets( 0, 0, 0, 0 ) );
    m_button.setHorizontalAlignment( SwingConstants.CENTER );
    m_button.setHorizontalTextPosition( SwingConstants.RIGHT );

    m_button.setBorder( border );
  }

  /**
   * Sets the pressed column.
   * 
   * @param column
   *          the column.
   */
  public void setPressedColumn( final int column )
  {
    m_pressedColumn = column;
  }

  @Override
  public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column )
  {
    final int cc = table == null ? -1 : table.convertColumnIndexToModel( column );
    final JTableHeader header = table == null ? null : table.getTableHeader();
    final boolean isPressed = cc == m_pressedColumn;

    if( header != null )
    {
      m_button.setForeground( header.getForeground() );
      m_button.setBackground( header.getBackground() );
      m_button.setFont( header.getFont() );
    }

    m_button.getModel().setPressed( isPressed );
    m_button.getModel().setArmed( isPressed );

    final String text = getText( value );
    final Icon icon = getIcon( value );

    m_button.setText( text == null ? "" : text.toString() ); //$NON-NLS-1$
    m_button.setIcon( icon );
    m_button.setPressedIcon( icon );

    return m_button;
  }

  private String getText( final Object tvc )
  {
    if( tvc == null )
      return Messages.getString( "org.kalypso.ogc.sensor.tableview.swing.renderer.ColumnHeaderRenderer.2" ); //$NON-NLS-1$

    return tvc.toString();
  }

  public String getTooltip( final Object element )
  {
    final String text = getText( element );

    if( !(element instanceof TableViewColumn) )
      return text;

    try
    {
      final TableViewColumn tvc = (TableViewColumn)element;
      final IAxis valueAxis = tvc.getValueAxis();
      final ITupleModel values = tvc.getValues();

      final int mergedStatus = KalypsoStatusUtils.getStatus( values, valueAxis );
      final String tooltip = KalypsoStatusUtils.getTooltipFor( mergedStatus );
      if( tooltip != null )
        return text + " - " + tooltip; //$NON-NLS-1$
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
    }

    return text;
  }

  private Icon getIcon( final Object element )
  {
    if( !(element instanceof TableViewColumn) )
      return null;

    try
    {
      final TableViewColumn tvc = (TableViewColumn)element;
      final IObservation observation = tvc.getObservation();
      if( observation == null )
        return null;

      final IAxis valueAxis = tvc.getValueAxis();
      final ITupleModel values = tvc.getValues();

      final int mergedStatus = KalypsoStatusUtils.getStatus( values, valueAxis );
      return KalypsoStatusUtils.getIconFor( mergedStatus );
    }
    catch( final SensorException e )
    {
      e.printStackTrace();
    }

    return null;
  }

  public void openDialog( final Object element )
  {
    if( !(element instanceof TableViewColumn) )
      return;

    final TableViewColumn tvc = (TableViewColumn)element;

    final IObservation observation = tvc.getObservation();
    final IAxis valueAxis = tvc.getValueAxis();

    final StringBuffer msg = new StringBuffer();
    msg.append( Messages.getString( "org.kalypso.ogc.sensor.tableview.swing.renderer.ColumnHeaderRenderer.4" ) ); //$NON-NLS-1$
    msg.append( observation.getName() );
    msg.append( '\n' );

    msg.append( Messages.getString( "org.kalypso.ogc.sensor.tableview.swing.renderer.ColumnHeaderRenderer.5" ) ); //$NON-NLS-1$
    msg.append( valueAxis.getName() );
    msg.append( '\n' );

    // TODO: show dialog with informations about:
    // - observation
    // - the particular axis
    // - the data range
    // - stati of values
    // - stati of observation (not yet implemented)

    // JOptionPane.showMessageDialog( e.getComponent(), msg.toString() );
  }
}
