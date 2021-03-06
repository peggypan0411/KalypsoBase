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
package org.kalypso.ui.editor.mapeditor;

import java.awt.Component;
import java.awt.Frame;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.internal.ObjectActionContributorManager;
import org.kalypso.commons.command.ICommandTarget;
import org.kalypso.contribs.eclipse.swt.events.SWTAWT_ContextMenuMouseAdapter;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.map.MapPanel;
import org.kalypso.ogc.gml.mapmodel.CommandableWorkspace;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.ui.editor.actions.INewScope;
import org.kalypso.ui.editor.actions.NewScopeFactory;
import org.kalypso.ui.internal.i18n.Messages;

/**
 * Helper class for {@link org.eclipse.ui.IWorkbenchPart}s, which show a map.
 * 
 * @author Gernot Belger
 */
@SuppressWarnings( "restriction" )
public class MapPartHelper
{
  private MapPartHelper( )
  {
    // will not be instantiated
  }

  public static IMapPanel createMapPanel( final Composite parent, final int style, final Object layoutData, final ICommandTarget commandTarget, final IFeatureSelectionManager selectionManager )
  {
    final MapPanel mapPanel = new MapPanel( commandTarget, selectionManager );
    final Composite mapComposite = new Composite( parent, style | SWT.EMBEDDED | SWT.NO_BACKGROUND );
    mapComposite.setLayoutData( layoutData );
    final Frame virtualFrame = SWT_AWT.new_Frame( mapComposite );

    if( mapPanel.isMultitouchEnabled() )
    {
      throw new UnsupportedOperationException();
      // MTMapPanelApp mtApp = new MTMapPanelApp( virtualFrame, mapPanel, mapComposite.handle, commandTarget );
      // mtApp.init();
      //
      // mapPanel.setMTObject( mtApp );
      //
      // virtualFrame.add( mtApp );
    }

    // the order of addition of the frames is very important
    // the virtual "old" mapPanel has to be added after the
    // multitouch renderer, otherwise it will not work
    virtualFrame.add( mapPanel );

    return mapPanel;
  }

  public static MenuManager createMapContextMenu( final Composite parent, final IMapPanel mapPanel, final IWorkbenchPartSite site )
  {
    // create Context Menu
    final MenuManager menuManager = new MenuManager();
    menuManager.setRemoveAllWhenShown( true );
    menuManager.addMenuListener( new IMenuListener()
    {
      @Override
      public void menuAboutToShow( final IMenuManager manager )
      {
        fillMapContextMenu( site.getPart(), manager, mapPanel );
      }
    } );

    final Menu mapMenu = menuManager.createContextMenu( parent );
    parent.setMenu( mapMenu );
    // register it
    if( mapPanel instanceof Component )
      ((Component)mapPanel).addMouseListener( new SWTAWT_ContextMenuMouseAdapter( parent, mapMenu ) );

    return menuManager;
  }

  /**
   * Add some special actions to the menuManager, depending on the current selection.
   */
  public static void fillMapContextMenu( final IWorkbenchPart part, final IMenuManager manager, final IMapPanel mapPanel )
  {
    final IFeatureSelectionManager selectionManager = mapPanel.getSelectionManager();

    /* Menu depending on active theme */
    final IKalypsoTheme activeTheme = mapPanel.getMapModell().getActiveTheme();
    if( activeTheme instanceof IKalypsoFeatureTheme )
    {
      manager.add( new GroupMarker( "themeActions" ) ); //$NON-NLS-1$

      /* Add a 'new' menu corresponding to the theme's feature type. */
      final IKalypsoFeatureTheme theme = (IKalypsoFeatureTheme)activeTheme;
      final CommandableWorkspace workspace = theme.getWorkspace();
      final INewScope scope = NewScopeFactory.createPropertyScope( theme.getFeatureList(), workspace, selectionManager );
      manager.add( scope.createMenu() );

      /* Also add specific theme actions. */
      final StructuredSelection themeSelection = new StructuredSelection( theme );
      final ISelectionProvider selectionProvider = new ISelectionProvider()
      {
        @Override
        public void addSelectionChangedListener( final ISelectionChangedListener listener )
        {
        }

        @Override
        public ISelection getSelection( )
        {
          return themeSelection;
        }

        @Override
        public void removeSelectionChangedListener( final ISelectionChangedListener listener )
        {
        }

        @Override
        public void setSelection( final ISelection selection )
        {
        }
      };
      final IMenuManager themeManager = new MenuManager( Messages.getString( "org.kalypso.ui.editor.mapeditor.MapPartHelper.2" ), "themeActions" ); //$NON-NLS-1$ //$NON-NLS-2$
      ObjectActionContributorManager.getManager().contributeObjectActions( part, themeManager, selectionProvider );
      manager.add( themeManager );
    }

    // add additions separator: if not, eclipse whines
    manager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
  }
}
