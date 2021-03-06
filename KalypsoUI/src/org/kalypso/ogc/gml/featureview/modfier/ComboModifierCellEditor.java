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
package org.kalypso.ogc.gml.featureview.modfier;

import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Gernot Belger
 */
public class ComboModifierCellEditor extends ComboBoxViewerCellEditor
{
  public ComboModifierCellEditor( final Composite parent, final int style )
  {
    super( parent, style );
  }

  @Override
  protected org.eclipse.swt.widgets.Control createControl( final Composite parent )
  {
    final CCombo combo = (CCombo)super.createControl( parent );

    // REMARK: The default behaviour of the ComboBoxViewerCellEditor is to do nothing
    // But we ant the value to be applied as soon as the user clicks at it.
    // Sadly, we get the same event if the user uses up/down arrows, but we are not able to distinguish that.
    combo.addSelectionListener( new SelectionAdapter()
    {
      @Override
      public void widgetSelected( final org.eclipse.swt.events.SelectionEvent e )
      {
        handleComboSelected( combo );
      }
    } );

    return combo;
  }

  protected void handleComboSelected( final CCombo combo )
  {
    /* Apply editor value */
    focusLost();

    /* In order to make the table cursor visible again, we must return the focus to the table */
    combo.getParent().setFocus();
  }

}
