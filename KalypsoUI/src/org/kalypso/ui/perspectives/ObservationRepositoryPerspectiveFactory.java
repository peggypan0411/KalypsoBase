/*--------------- Kalypso-Header --------------------------------------------------------------------

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

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;
import org.kalypso.ui.repository.view.RepositoryExplorerPart;

/**
 * The PerspectiveFactory for browsing the ObservationRepository.
 * 
 * @author schlienger
 */
public class ObservationRepositoryPerspectiveFactory implements IPerspectiveFactory
{
  /**
   * The perspective id.
   */
  public static String ID = "org.kalypso.ui.perspectives.ObservationRepositoryPerspectiveFactory"; //$NON-NLS-1$

  /**
   * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
   */
  @Override
  public void createInitialLayout( final IPageLayout layout )
  {
    final IFolderLayout topLeft = layout.createFolder( "topLeft", IPageLayout.LEFT, (float)0.26, layout.getEditorArea() ); //$NON-NLS-1$
    topLeft.addView( RepositoryExplorerPart.ID );

    final IFolderLayout botLeft = layout.createFolder( "bottom", IPageLayout.BOTTOM, (float)0.70, "topLeft" ); //$NON-NLS-1$ //$NON-NLS-2$
    botLeft.addView( IPageLayout.ID_PROP_SHEET );

    final IFolderLayout leftBottom = layout.createFolder( "leftBottom", IPageLayout.BOTTOM, (float)0.0, layout.getEditorArea() ); //$NON-NLS-1$
    leftBottom.addView( "org.kalypso.zml.ui.chart.view.DiagramViewPart" ); //$NON-NLS-1$ //FIXME move complete perspective to zml ui plugin!!!

    final IFolderLayout rightBottom = layout.createFolder( "rightBottom", IPageLayout.RIGHT, (float)0.50, "leftBottom" ); //$NON-NLS-1$ //$NON-NLS-2$
    rightBottom.addView( "org.kalypso.zml.ui.table.view.TableViewPart" ); //$NON-NLS-1$ //FIXME move complete perspective to zml ui plugin!!!

    setContentsOfShowViewMenu( layout );
    layout.setEditorAreaVisible( false );

    /* Configure behavior of views */
    final IViewLayout repositoryViewLayout = layout.getViewLayout( RepositoryExplorerPart.ID );
    repositoryViewLayout.setCloseable( false );
    repositoryViewLayout.setMoveable( true );

// final IViewLayout propertyViewLayout = layout.getViewLayout( IPageLayout.ID_PROP_SHEET );
// propertyViewLayout.setCloseable( true );
// propertyViewLayout.setMoveable( true );
//
    final IViewLayout diagramViewLayout = layout.getViewLayout( "org.kalypso.zml.ui.chart.view.DiagramViewPart" ); //$NON-NLS-1$
    diagramViewLayout.setCloseable( false );
    diagramViewLayout.setMoveable( true );

    final IViewLayout tableViewLayout = layout.getViewLayout( "org.kalypso.zml.ui.table.view.TableViewPart" ); //$NON-NLS-1$
    tableViewLayout.setCloseable( false );
    tableViewLayout.setMoveable( true );

    /* Add to "Open perspective"... */
    layout.addPerspectiveShortcut( "org.kalypso.hwv.product.sachsen.ui.views.admin.LhwzAdminPerspective" ); //$NON-NLS-1$
    layout.addPerspectiveShortcut( "org.kalypso.simulation.ui.startscreen.PrognosePerspective" ); //$NON-NLS-1$
    layout.addPerspectiveShortcut( ObservationRepositoryPerspectiveFactory.ID );
    layout.addPerspectiveShortcut( ModelerPerspectiveFactory.ID );
    layout.addPerspectiveShortcut( "org.kalypso.hwv.product.sachsen.ui.views.LhwzStartPagePerspective" ); //$NON-NLS-1$
    layout.addPerspectiveShortcut( "org.kalypso.dwd.visualization.perspectives.DwdVisualizePerspective" ); //$NON-NLS-1$
  }

  /**
   * Sets the intial contents of the "Show View" menu
   * 
   * @param layout
   */
  protected void setContentsOfShowViewMenu( final IPageLayout layout )
  {
    // layout.addShowViewShortcut( IKalypsoUIConstants.ID_REPOSITORY_VIEW );
    layout.addShowViewShortcut( IPageLayout.ID_PROP_SHEET );
  }
}