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

import javax.xml.namespace.QName;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.i18n.Messages;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.map.handlers.MapHandlerUtils;
import org.kalypso.ogc.gml.serialize.Gml2ShapeConverter;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree_impl.io.shpapi.ShapeConst;
import org.kalypsodeegree_impl.io.shpapi.dataprovider.IShapeDataProvider;
import org.kalypsodeegree_impl.io.shpapi.dataprovider.TriangulatedSurfaceSinglePartShapeDataProvider;
import org.kalypsodeegree_impl.model.geometry.GM_TriangulatedSurface_Impl;

/**
 * @author Gernot Belger
 */
public class ExportGml2ShapeThemeHandler extends AbstractHandler implements IHandler
{
  /**
   * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  public Object execute( final ExecutionEvent event ) throws ExecutionException
  {
    final IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();
    final IWorkbenchPart part = (IWorkbenchPart) context.getVariable( ISources.ACTIVE_PART_NAME );
    if( part == null )
      throw new ExecutionException( Messages.getString( "org.kalypso.ogc.gml.outline.handler.ExportGml2ShapeThemeHandler.1" ) ); //$NON-NLS-1$

    final Shell shell = part.getSite().getShell();
    final String title = Messages.getString( "org.kalypso.ogc.gml.outline.handler.ExportGml2ShapeThemeHandler.2" ); //$NON-NLS-1$

    final ISelection sel = HandlerUtil.getCurrentSelectionChecked( event );
    final IKalypsoFeatureTheme theme = MapHandlerUtils.getFirstElement( sel, IKalypsoFeatureTheme.class );
    if( theme == null )
      throw new ExecutionException( "No Feature-Theme in selection." );

    // TODO: let the user choose what to export: visible features, all features, current extent, ...
    final FeatureList featureList = theme == null ? null : theme.getFeatureListVisible( null );
    if( featureList == null || featureList.size() == 0 )
    {
      MessageDialog.openWarning( shell, title, Messages.getString( "org.kalypso.ogc.gml.outline.handler.ExportGml2ShapeThemeHandler.3" ) ); //$NON-NLS-1$
      return Status.CANCEL_STATUS;
    }

    final IFeatureType type = resolveFeatureType( theme );
    final Gml2ShapeConverter converter = Gml2ShapeConverter.createDefault( type );

    // examine what we got and ask user
    // TODO: only use file extension which make sense (dbf OR shp)

    /* ask user for file */
    final String fileName = theme.getLabel();
    final String[] filterExtensions = new String[] { "*.shp", "*.dbf" }; //$NON-NLS-1$ //$NON-NLS-2$
    final String[] filterNames = new String[] {
        Messages.getString( "org.kalypso.ogc.gml.outline.handler.ExportGml2ShapeThemeHandler.9" ), Messages.getString( "org.kalypso.ogc.gml.outline.handler.ExportGml2ShapeThemeHandler.10" ) }; //$NON-NLS-1$ //$NON-NLS-2$

    final File file = MapHandlerUtils.showSaveFileDialog( shell, title, fileName, "gml2shapeExport", filterExtensions, filterNames ); //$NON-NLS-1$
    if( file == null )
      return null;

    final String result = file.getAbsolutePath();
    final String shapeFileBase;
    if( result.toLowerCase().endsWith( ".shp" ) || result.toLowerCase().endsWith( ".dbf" ) ) //$NON-NLS-1$ //$NON-NLS-2$
      shapeFileBase = FileUtilities.nameWithoutExtension( result );
    else
      shapeFileBase = result;

    final Job job = new Job( title + " - " + result ) //$NON-NLS-1$
    {
      @Override
      protected IStatus run( final IProgressMonitor monitor )
      {
        IShapeDataProvider shapeDataProvider = null;

        final Feature feature = (Feature) featureList.get( 0 );
        final GM_Object geometryProperty = feature.getDefaultGeometryProperty();
        if( geometryProperty instanceof GM_TriangulatedSurface_Impl )
        {
          final byte shapeType = ShapeConst.SHAPE_TYPE_POLYGONZ;
          shapeDataProvider = new TriangulatedSurfaceSinglePartShapeDataProvider( (Feature[]) featureList.toArray( new Feature[featureList.size()] ), shapeType );
        }
        try
        {
          converter.writeShape( featureList, shapeFileBase, shapeDataProvider, monitor );
        }
        catch( final CoreException e )
        {
          return e.getStatus();
        }
        return Status.OK_STATUS;
      }
    };
    job.setUser( true );
    job.schedule();

    return Status.OK_STATUS;
  }

  /**
   * @hack special handling for wfs-themes!
   */
  private IFeatureType resolveFeatureType( final IKalypsoFeatureTheme theme )
  {
    final IFeatureType featureType = theme.getFeatureType();
    final QName qname = featureType.getQName();
    if( Feature.QNAME_FEATURE.equals( qname ) )
    {
      final FeatureList list = theme.getFeatureList();
      if( list.size() == 0 )
        return featureType;

      final Object object = list.get( 0 );
      if( object instanceof Feature )
      {
        final Feature feature = (Feature) object;

        return feature.getFeatureType();
      }
    }

    return featureType;
  }

}