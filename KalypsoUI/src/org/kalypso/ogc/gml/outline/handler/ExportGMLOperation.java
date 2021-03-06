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
package org.kalypso.ogc.gml.outline.handler;

import java.io.File;
import java.net.URLEncoder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.eclipse.jface.operation.ICoreRunnableWithProgress;
import org.kalypso.gmlschema.GMLSchema;
import org.kalypso.gmlschema.IGMLSchema;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypsodeegree.model.feature.GMLWorkspace;

/**
 * @author Gernot Belger
 */
public class ExportGMLOperation implements ICoreRunnableWithProgress
{
  private final ExportGMLData m_data;

  public ExportGMLOperation( final ExportGMLData data )
  {
    m_data = data;
  }

  @Override
  public IStatus execute( final IProgressMonitor monitor )
  {
    final File gmlFile = m_data.getExportFile();
    final String gmlPath = gmlFile.getAbsolutePath();
    final File xsdFile = new File( FileUtilities.setSuffix( gmlPath, ".xsd" ) ); //$NON-NLS-1$

    final GMLWorkspace workspace = m_data.getWorkspace();

    try
    {
      final IGMLSchema schema = workspace.getGMLSchema();
      final String encode = URLEncoder.encode( xsdFile.getName(), "UTF-8" ); //$NON-NLS-1$
      workspace.setSchemaLocation( schema.getTargetNamespace() + " " + encode ); //$NON-NLS-1$
      GmlSerializer.serializeWorkspace( gmlFile, workspace, "UTF-8" ); //$NON-NLS-1$
      if( schema instanceof GMLSchema )
      {
        // can only save real GMLSchema, EmptyGMLSchema is not saved
        ((GMLSchema)schema).getSchema().save( xsdFile );
      }
    }
    catch( final Exception e )
    {
      return StatusUtilities.statusFromThrowable( e );
    }

    return Status.OK_STATUS;
  }
}