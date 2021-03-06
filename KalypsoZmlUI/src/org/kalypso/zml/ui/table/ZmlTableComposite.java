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
package org.kalypso.zml.ui.table;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.contribs.eclipse.jface.action.ContributionUtils;
import org.kalypso.zml.core.table.model.IZmlModel;
import org.kalypso.zml.core.table.model.IZmlModelColumn;
import org.kalypso.zml.core.table.model.event.ZmlModelColumnChangeType;
import org.kalypso.zml.core.table.schema.ZmlTableType;
import org.kalypso.zml.ui.debug.KalypsoZmlUiDebug;
import org.kalypso.zml.ui.table.nat.ZmlTable;

/**
 * @author Dirk Kuch
 */
public class ZmlTableComposite extends Composite implements IZmlTableComposite
{
  private final Set<IZmlTableCompositeListener> m_listeners = new LinkedHashSet<>();

  private final Set<IZmlModelColumn> m_stackColumns = Collections.synchronizedSet( new LinkedHashSet<IZmlModelColumn>() );

  private final FormToolkit m_toolkit;

  protected ZmlTable m_table;

  private IZmlModel m_model;

  public ZmlTableComposite( final Composite parent, final FormToolkit toolkit )
  {
    super( parent, SWT.NULL );

    m_toolkit = toolkit;

    final GridLayout layout = GridLayoutFactory.fillDefaults().create();
    layout.verticalSpacing = 0;
    setLayout( layout );

    toolkit.adapt( this );
  }

  public void doInitialize( final IZmlModel model )
  {
    m_model = model;

    synchronized( this )
    {
      final ZmlTableType tableType = model.getTableType();

      Composite toolbar = null;
      if( hasToolbar( tableType ) )
      {
        toolbar = m_toolkit.createComposite( this );
        toolbar.setLayout( GridLayoutFactory.fillDefaults().create() );
        toolbar.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, false ) );
      }

      m_table = new ZmlTable( this, m_model, m_toolkit );
      m_table.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

      if( hasToolbar( tableType ) )
        initToolbar( tableType, toolbar, m_toolkit );
    }

    this.layout();
  }

  @Override
  public void dispose( )
  {
    m_table.dispose();

    super.dispose();
  }

  private boolean hasToolbar( final ZmlTableType tableType )
  {
    final List<String> toolbar = tableType.getToolbar();
    if( toolbar == null )
      return false;

    return !toolbar.isEmpty();
  }

  private void initToolbar( final ZmlTableType tableType, final Composite composite, final FormToolkit toolkit )
  {
    /** process as job in order to handle tool bar IElementUpdate job actions */
    final String[] references = getToolbarCommandReferences( tableType );
    if( ArrayUtils.isEmpty( references ) && !KalypsoZmlUiDebug.DEBUG_TABLE_DIALOG.isEnabled() )
      return;

    final ToolBarManager toolBarManager = new ToolBarManager( SWT.HORIZONTAL | SWT.FLAT | SWT.RIGHT );
    final ToolBar control = toolBarManager.createControl( composite );
    control.setLayoutData( new GridData( SWT.RIGHT, SWT.FILL, true, false ) );

    for( final String reference : references )
    {
      ContributionUtils.populateContributionManager( PlatformUI.getWorkbench(), toolBarManager, reference );
    }

    if( KalypsoZmlUiDebug.DEBUG_TABLE_DIALOG.isEnabled() )
      ContributionUtils.populateContributionManager( PlatformUI.getWorkbench(), toolBarManager, "toolbar:org.kalypso.zml.ui.table.commands.debug" ); //$NON-NLS-1$

    toolBarManager.update( true );
    toolkit.adapt( control );

    composite.getParent().layout( true, true );
  }

  private String[] getToolbarCommandReferences( final ZmlTableType tableType )
  {
    final List<String> references = tableType.getToolbar();
    if( Objects.isNull( references ) )
      return new String[] {};

    return references.toArray( new String[] {} );
  }

  @Override
  public void refresh( final ZmlModelColumnChangeType type, final IZmlModelColumn... cols )
  {
    synchronized( this )
    {
      // FIXME move to getModel().refresh()?
      // table.getModel().reset();
      // FIXME stack columns in table model
      Collections.addAll( m_stackColumns, cols );

      // final IZmlModelColumn[] missing = ZmlTableColumns.findMissingColumns( getMainTable(), getModel().getColumns()
      // );
      // ZmlTableColumns.buildTableColumns( this, ZmlTableColumns.toBaseColumns( missing ) );

      // FIXME don't refresh all columns!!!!
      m_table.refresh( type );
    }
  }

  @Override
  public void fireTableSourceChanged( final String type )
  {
    final IZmlTableCompositeListener[] listeners = m_listeners.toArray( new IZmlTableCompositeListener[] {} );
    for( final IZmlTableCompositeListener listener : listeners )
    {
      listener.eventTableChanged( type );
    }
  }

  @Override
  public void addListener( final IZmlTableCompositeListener listener )
  {
    m_listeners.add( listener );
  }

  @Override
  public void removeListener( final IZmlTableCompositeListener listener )
  {
    m_listeners.remove( listener );
  }

  @Override
  public IZmlTable getTable( )
  {
    return m_table;
  }
}