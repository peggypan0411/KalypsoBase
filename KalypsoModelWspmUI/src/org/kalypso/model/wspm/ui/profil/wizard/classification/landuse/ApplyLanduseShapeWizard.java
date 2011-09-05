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
package org.kalypso.model.wspm.ui.profil.wizard.classification.landuse;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.kalypso.contribs.eclipse.core.resources.ResourceUtilities;
import org.kalypso.model.wspm.ui.profil.wizard.classification.landuse.pages.ApplyLanduseShapePage;
import org.kalypso.model.wspm.ui.profil.wizard.landuse.model.ILanduseModel;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ogc.gml.outline.nodes.IThemeNode;

/**
 * @author Dirk Kuch
 */
public class ApplyLanduseShapeWizard extends Wizard implements IWorkbenchWizard
{
  protected IProject m_project;

  private ApplyLanduseShapePage m_page;

  public ApplyLanduseShapeWizard( )
  {
    setWindowTitle( "Apply landuse shape" );

    setNeedsProgressMonitor( true );
  }

  @Override
  public void addPages( )
  {
    m_page = new ApplyLanduseShapePage( m_project );
    addPage( m_page );
  }

  @Override
  public boolean performFinish( )
  {
    final ILanduseModel model = m_page.getModel();

    /**
     * <pre>
     * how to continue?`
     * 
     * - use existing RoughnessIntersector implementation and
     *    - import selected shape file into a gml workspace
     *    - new pseudo FeatureList implementation
     *    - or refactor existing code -> RoughnessIntersector will run against a new interface
     * </pre>
     */

    throw new UnsupportedOperationException();
  }

  @Override
  public void init( final IWorkbench workbench, final IStructuredSelection selection )
  {
    final Object element = selection.getFirstElement();

    if( element instanceof IThemeNode )
    {
      final IThemeNode node = (IThemeNode) element;
      final Object nodeElement = node.getElement();
      if( !(nodeElement instanceof IKalypsoTheme) )
        throw new UnsupportedOperationException();

      final IKalypsoTheme theme = (IKalypsoTheme) nodeElement;
      final IMapModell model = theme.getMapModell();

      final URL context = model.getContext();
      m_project = ResourceUtilities.findProjectFromURL( context );
    }
    else
      throw new UnsupportedOperationException();
  }

}
