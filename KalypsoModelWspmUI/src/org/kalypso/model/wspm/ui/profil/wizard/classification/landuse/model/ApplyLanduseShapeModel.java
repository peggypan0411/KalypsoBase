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
package org.kalypso.model.wspm.ui.profil.wizard.classification.landuse.model;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.kalypso.contribs.eclipse.core.resources.CollectFilesWithExtensionVisitor;
import org.kalypso.model.wspm.core.IWspmPointProperties;
import org.kalypso.model.wspm.ui.profil.wizard.landuse.model.ILanduseModel;
import org.kalypso.model.wspm.ui.profil.wizard.landuse.model.LanduseModel;

/**
 * @author Dirk Kuch
 */
public class ApplyLanduseShapeModel extends LanduseModel
{
  private IFile[] m_landuseShapeFiles;

  Properties m_mapping = new Properties();

  public ApplyLanduseShapeModel( final IProject project )
  {
    super( project, IWspmPointProperties.POINT_PROPERTY_ROUGHNESS_CLASS );

    try
    {
      init();
    }
    catch( final CoreException e )
    {
      e.printStackTrace();
    }

    addPropertyChangeListener( ILanduseModel.PROPERTY_LANDUSE_SHAPE, new ALSSelectedShapeFileChangedListener() );
    addPropertyChangeListener( ILanduseModel.PROPERTY_SHAPE_COLUMN, new ALSSelectedColumnChanged() );
  }

  private void init( ) throws CoreException
  {
    final IFolder landuses = getProject().getFolder( "data/landuse" ); //$NON-NLS-1$
    if( !landuses.exists() )
      return;

    final CollectFilesWithExtensionVisitor visitor = new CollectFilesWithExtensionVisitor();
    visitor.setExtension( "shp" ); //$NON-NLS-1$
    landuses.accept( visitor );

    m_landuseShapeFiles = visitor.getFiles();
  }

  public IFile[] getLanduseShapeFiles( )
  {
    return m_landuseShapeFiles;
  }
}
