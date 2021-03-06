package org.kalypso.commons.java.swing.jtable;

import java.awt.event.ActionEvent;

import javax.swing.JTable;

import org.kalypso.commons.internal.i18n.Messages;

/**
 * SetAllAction
 * 
 * @author schlienger
 */
public class SetAllAction extends AbstractObservationTableAction
{
  public SetAllAction( final JTable table )
  {
    super( table, Messages.getString( "org.kalypso.commons.java.swing.jtable.SetAllAction.0" ), Messages.getString( "org.kalypso.commons.java.swing.jtable.SetAllAction.1" ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public void internalActionPerformed( final ActionEvent e )
  {
    final JTable table = getTable();
    final int col = table.getSelectedColumn();
    final int row = table.getSelectedRow();
    final Object value = table.getValueAt( row, col );

    for( int i = 0; i < row; i++ )
      table.setValueAt( value, i, col );

    for( int i = row + 1; i < table.getRowCount(); i++ )
      table.setValueAt( value, i, col );
  }
}
