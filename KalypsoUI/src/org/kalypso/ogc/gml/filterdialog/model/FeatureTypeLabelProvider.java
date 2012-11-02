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
package org.kalypso.ogc.gml.filterdialog.model;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.ui.ImageProvider;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

/**
 * @author kuepfer
 */
public class FeatureTypeLabelProvider extends LabelProvider
{
  private final String m_annotationKey;

  public FeatureTypeLabelProvider( )
  {
    this( IAnnotation.ANNO_LABEL );
  }

  public FeatureTypeLabelProvider( final String annotationKey )
  {
    m_annotationKey = annotationKey;
  }

  /**
   * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
   */
  @Override
  public Image getImage( final Object element )
  {
    ImageDescriptor descriptor = null;
    if( element instanceof IFeatureType )
    {
      return null;
    }
    else if( element instanceof IValuePropertyType )
    {
      final IValuePropertyType vpt = (IValuePropertyType)element;
      if( GeometryUtilities.isPointGeometry( vpt ) )
        descriptor = ImageProvider.IMAGE_GEOM_PROP_POINT;
      if( GeometryUtilities.isMultiPointGeometry( vpt ) )
        descriptor = ImageProvider.IMAGE_GEOM_PROP_MULTIPOINT;
      if( GeometryUtilities.isLineStringGeometry( vpt ) )
        descriptor = ImageProvider.IMAGE_GEOM_PROP_LINE;
      if( GeometryUtilities.isMultiLineStringGeometry( vpt ) )
        descriptor = ImageProvider.IMAGE_GEOM_PROP_MULTILINE;
      if( GeometryUtilities.isPolygonGeometry( vpt ) )
        descriptor = ImageProvider.IMAGE_GEOM_PROP_POLYGON;
      if( GeometryUtilities.isMultiPolygonGeometry( vpt ) )
        descriptor = ImageProvider.IMAGE_GEOM_PROP_MULTIPOLYGON;

      // TODO: dispose those images!
      // use a helper class from your plugin, see for example KalypsInformDss Plugin classes
      if( descriptor != null )
        return descriptor.createImage();
    }
    return null;
  }

  /**
   * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
   */
  @Override
  public String getText( final Object element )
  {
    if( element instanceof IFeatureType )
    {
      final IAnnotation annotation = ((IFeatureType)element).getAnnotation();
      return annotation.getValue( m_annotationKey );
    }
    else if( element instanceof IPropertyType )
    {
      final IAnnotation annotation = ((IPropertyType)element).getAnnotation();
      return annotation.getValue( m_annotationKey );
    }
    return ""; //$NON-NLS-1$
  }

}
