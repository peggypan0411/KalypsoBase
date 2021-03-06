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
package org.kalypso.ogc.gml.mapmodel.visitor;

import java.util.Arrays;

import javax.xml.namespace.QName;

import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypsodeegree.model.geometry.GM_MultiPoint;
import org.kalypsodeegree.model.geometry.GM_Point;

/**
 * @author Monika Thuel
 */
public class PointThemePredicate extends LineThemePredicater
{
  private static final QName[] ACCEPTED_GEOMETRIES = new QName[] { GM_MultiPoint.MULTI_POINT_ELEMENT, GM_Point.POINT_ELEMENT };

  @Override
  public boolean decide( final IKalypsoTheme theme )
  {
    if( theme instanceof IKalypsoFeatureTheme )
    {
      final IKalypsoFeatureTheme fTheme = (IKalypsoFeatureTheme) theme;
      final IFeatureType featureType = fTheme.getFeatureType();
      if( featureType == null )
        return false;

      final IValuePropertyType[] allGeomtryProperties = featureType.getAllGeometryProperties();
      if( allGeomtryProperties.length > 0 && Arrays.asList( ACCEPTED_GEOMETRIES ).contains( allGeomtryProperties[0].getValueQName() ) )
        return true;
    }
    return false;
  }

}
