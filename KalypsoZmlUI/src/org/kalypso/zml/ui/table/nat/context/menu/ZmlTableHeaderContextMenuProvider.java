/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  DenickestraÃŸe 22
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
package org.kalypso.zml.ui.table.nat.context.menu;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.kalypso.commons.java.lang.Objects;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.jface.action.ContributionUtils;
import org.kalypso.zml.core.table.binding.BaseColumn;
import org.kalypso.zml.core.table.binding.ColumnHeader;
import org.kalypso.zml.core.table.model.IZmlModelColumn;
import org.kalypso.zml.core.table.model.utils.ZmlModelColumns;
import org.kalypso.zml.core.table.model.view.ZmlModelViewport;
import org.kalypso.zml.core.table.rules.AppliedRule;
import org.kalypso.zml.ui.KalypsoZmlUI;
import org.kalypso.zml.ui.i18n.Messages;

/**
 * @author Dirk Kuch
 */
public class ZmlTableHeaderContextMenuProvider
{
  public void fillMenu( final ZmlModelViewport viewport, final IZmlModelColumn column, final MenuManager menuManager )
  {
    if( column == null )
      return;

    final BaseColumn columnType = column.getDataColumn();
    final String uri = columnType.getUriHeaderContextMenu();
    if( uri != null )
    {
      // add basic menu entries which are defined in the plugin.xml
      ContributionUtils.populateContributionManager( PlatformUI.getWorkbench(), menuManager, uri );
    }

    // add additional info items
    addAdditionalItems( viewport, column, menuManager );
  }

  private void addAdditionalItems( final ZmlModelViewport viewport, final IZmlModelColumn column, final MenuManager menuManager )
  {
    menuManager.add( new Separator() );

    menuManager.add( new Action()
    {
      @Override
      public String getText( )
      {
        return Messages.ZmlTableHeaderContextMenuProvider_0;
      }

      @Override
      public boolean isEnabled( )
      {
        return false;
      }
    } );

    final ColumnHeader[] headers = column.getDataColumn().getHeaders();
    for( final ColumnHeader header : headers )
    {
      addAdditionalItem( header, menuManager );
    }

    final AppliedRule[] rules = ZmlModelColumns.findRules( viewport, column );
    for( final AppliedRule rule : rules )
    {
      addAditionalItem( rule, menuManager );
    }

  }

  private void addAdditionalItem( final ColumnHeader header, final MenuManager menuManager )
  {
    menuManager.add( new Action()
    {
      @Override
      public org.eclipse.jface.resource.ImageDescriptor getImageDescriptor( )
      {
        try
        {
          final Image icon = header.getIcon();
          if( icon != null )
            return ImageDescriptor.createFromImage( icon );
        }
        catch( final Throwable t )
        {
          KalypsoZmlUI.getDefault().getLog().log( StatusUtilities.statusFromThrowable( t ) );
        }

        return null;
      }

      @Override
      public String getText( )
      {
        return String.format( "   %s", header.getLabel() ); //$NON-NLS-1$
      }

      @Override
      public boolean isEnabled( )
      {
        return false;
      }

    } );

  }

  private void addAditionalItem( final AppliedRule rule, final MenuManager menuManager )
  {
    menuManager.add( new Action()
    {
      @Override
      public org.eclipse.jface.resource.ImageDescriptor getImageDescriptor( )
      {
        try
        {
          final Image image = rule.getCellStyle().getImage();
          if( Objects.isNotNull( image ) )
            return ImageDescriptor.createFromImage( image );
        }
        catch( final Throwable t )
        {
          KalypsoZmlUI.getDefault().getLog().log( StatusUtilities.statusFromThrowable( t ) );
        }

        return null;
      }

      @Override
      public String getText( )
      {
        return String.format( Messages.ZmlTableHeaderContextMenuProvider_2, rule.getLabel() );
      }

      @Override
      public boolean isEnabled( )
      {
        return false;
      }
    } );
  }
}
