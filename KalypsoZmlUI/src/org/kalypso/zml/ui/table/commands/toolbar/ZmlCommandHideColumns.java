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
package org.kalypso.zml.ui.table.commands.toolbar;

import java.util.Map;

import jregex.RETokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.contribs.eclipse.core.commands.HandlerUtils;
import org.kalypso.zml.core.table.binding.DataColumn;
import org.kalypso.zml.core.table.model.IZmlModelColumn;
import org.kalypso.zml.core.table.model.utils.IClonedColumn;
import org.kalypso.zml.core.table.model.view.ZmlModelViewport;
import org.kalypso.zml.core.table.schema.DataColumnType;
import org.kalypso.zml.ui.table.IZmlTable;
import org.kalypso.zml.ui.table.commands.ZmlHandlerUtil;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author Dirk Kuch
 */
public class ZmlCommandHideColumns extends AbstractHandler implements IElementUpdater
{

  @Override
  public Object execute( final ExecutionEvent event )
  {
    final IZmlTable table = ZmlHandlerUtil.getTable( event );
    if( Objects.isNull( table ) )
      return Status.CANCEL_STATUS;

    final Map parameters = event.getParameters();
    final String[] columnTypes = getColumnTypes( parameters );
    final boolean hide = HandlerUtils.isSelected( event );

    final ZmlModelViewport model = table.getModelViewport();
    for( final String type : columnTypes )
    {
      model.setVisible( type, hide );
    }

    return Status.OK_STATUS;
  }

  private String[] getColumnTypes( final Map parameters )
  {
    final String types = (String) parameters.get( "column.type" ); //$NON-NLS-1$
    if( StringUtils.isEmpty( types ) )
      return new String[] {};

    final Iterable<String> columns = Splitter.on( ";" ).trimResults().omitEmptyStrings().split( types ); //$NON-NLS-1$

    return Iterables.toArray( columns, String.class );
  }

  @Override
  public void updateElement( final UIElement element, final Map parameters )
  {
    final IZmlTable table = ZmlHandlerUtil.getTable( element );
    if( table == null )
    {
// element.setChecked( true );
      return;
    }

    // FIXME resolving zmlmodelviewport at this point is impossible
    final String[] types = getColumnTypes( parameters );

    element.setChecked( !isVisible( table.getModelViewport(), types ) );
  }

  private boolean isVisible( final ZmlModelViewport viewport, final String[] types )
  {
    final IZmlModelColumn[] columns = viewport.getColumns();
    for( final IZmlModelColumn column : columns )
    {
      final DataColumn dataColumn = column.getDataColumn();
      final DataColumnType type = dataColumn.getType();

      final RETokenizer tokenizer = new RETokenizer( IClonedColumn.PATTERN_CLONED_COLUMN_TOKENIZER, type.getId() );
      final String parameterType = tokenizer.nextToken();

      if( ArrayUtils.contains( types, parameterType ) )
      {
        return true;
      }
    }

    return false;
  }
}