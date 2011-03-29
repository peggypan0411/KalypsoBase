/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *     @author changed / updated by: Dirk Kuch
 *******************************************************************************/
package org.kalypso.zml.ui.table.cursor;

import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.zml.core.table.binding.BaseColumn;
import org.kalypso.zml.ui.table.model.IZmlTableCell;
import org.kalypso.zml.ui.table.model.IZmlTableColumn;

import com.google.common.base.Strings;

public class ZmlTableCursor extends AbstractZmlCellCursor
{
  private static final Color COLOR_BACKGROUND_SELECTION = new Color( null, new RGB( 0xBA, 0xFF, 0xEC ) );

  private static final Color COLOR_BACKGROUND_SELECTION_DISABLED = new Color( null, new RGB( 0xCC, 0xCC, 0xCC ) );

  public ZmlTableCursor( final TableViewer viewer )
  {
    super( viewer );
  }

  @Override
  protected void paint( final Event event )
  {
    final ViewerCell cell = getFocusCell();
    if( Objects.isNull( cell ) )
      return;

    try
    {
      if( cell.getControl().isDisposed() )
        return;

      final GC gc = event.gc;
      final Display display = getDisplay();
      setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );

      final Color background = gc.getBackground();
      final Color foreground = gc.getForeground();

      gc.setBackground( getBackground() );
      gc.setForeground( getForeground() );

      gc.fillRectangle( event.x, event.y, event.width, event.height );

      final int offset = drawImage( gc, cell, 0 );
      drawText( gc, cell, offset );
      drawFocus( gc, display );

      gc.setBackground( background );
      gc.setForeground( foreground );

      setVisible( true );
    }
    catch( final Throwable t )
    {
      setVisible( false );
      if( t instanceof SWTException )
        return;

      t.printStackTrace();
    }
  }

  /**
   * @see org.eclipse.swt.widgets.Control#getBackground()
   */
  @Override
  public Color getBackground( )
  {
    final IZmlTableCell cell = getFocusTableCell();
    final IZmlTableColumn column = cell.getColumn();
    final BaseColumn type = column.getColumnType();
    if( type.isEditable() )
      return COLOR_BACKGROUND_SELECTION;

    return COLOR_BACKGROUND_SELECTION_DISABLED;
  }

  private void drawFocus( final GC gc, final Display display )
  {
    final Point size = getSize();
    gc.setBackground( display.getSystemColor( SWT.COLOR_BLACK ) );
    gc.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );

    // TODO crap
    gc.drawRectangle( 1, 1, size.x - 2, size.y - 2 );
    gc.drawRectangle( 2, 2, size.x - 4, size.y - 4 );
  }

  private void drawText( final GC gc, final ViewerCell cell, final int x )
  {
    final String text = cell.getText();
    if( Strings.isNullOrEmpty( text ) )
      return;

    int ptrX = x;

    final Rectangle bounds = cell.getBounds();
    final Point extent = gc.stringExtent( text );

    // Temporary code - need a better way to determine table trim
    if( Util.isWin32() )
    {
      if( ((Table) getParent()).getColumnCount() == 0 || cell.getColumnIndex() == 0 )
      {
        ptrX += 2;
      }
      else
      {
        final int alignmnent = ((Table) getParent()).getColumn( cell.getColumnIndex() ).getAlignment();
        if( SWT.LEFT == alignmnent )
          ptrX += 6;
        else if( SWT.RIGHT == alignmnent )
          ptrX = bounds.width - extent.x - 6;
        else if( SWT.CENTER == alignmnent )
          ptrX += (bounds.width - x - extent.x) / 2;
      }
    }
    else
    {
      if( ((Table) getParent()).getColumnCount() == 0 )
      {
        ptrX += 5;
      }
      else
      {
        final int alignmnent = ((Table) getParent()).getColumn( cell.getColumnIndex() ).getAlignment();

        if( SWT.LEFT == alignmnent )
          ptrX += 5;
        else if( SWT.RIGHT == alignmnent )
          ptrX = bounds.width - extent.x - 2;
        else if( SWT.CENTER == alignmnent )
          ptrX += (bounds.width - x - extent.x) / 2 + 2;
      }
    }

    final int textY = (getSize().y - extent.y) / 2;
    gc.drawString( text, ptrX, textY );
  }

  private int drawImage( final GC gc, final ViewerCell cell, final int x )
  {
    final Image image = cell.getImage();
    if( image == null )
      return 0;

    final Rectangle imageSize = image.getBounds();
    final int imageY = (getSize().y - imageSize.height) / 2;
    gc.drawImage( image, x, imageY );

    return imageSize.width;
  }
}
