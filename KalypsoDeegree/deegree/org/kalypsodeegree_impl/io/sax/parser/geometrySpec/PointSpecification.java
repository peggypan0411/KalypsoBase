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
package org.kalypsodeegree_impl.io.sax.parser.geometrySpec;

import org.kalypso.gmlschema.types.GeometrySpecificationCatalog;
import org.kalypso.gmlschema.types.IGeometrySpecification;
import org.kalypsodeegree.model.typeHandler.CoordHandler;
import org.kalypsodeegree.model.typeHandler.CoordinatesHandler;
import org.kalypsodeegree.model.typeHandler.PosHandler;

/**
 * @author Felipe Maximino
 */
public class PointSpecification implements IGeometrySpecification
{
  /**
   * @see org.kalypso.gmlschema.types.IGeometrySpecification#fillSpecifications(org.kalypso.gmlschema.types.GeometrySpecificationCatalog)
   */
  @Override
  public void fillSpecifications( GeometrySpecificationCatalog specs )
  {
    /* gml:pos */
    specs.registerSpecificationForGeometry( new PosHandler() );
    /* gml:coordinates - Deprecated with GML version 3.1.0 for coordinates with ordinate values that are numbers. Use "pos" instead */
    specs.registerSpecificationForGeometry( new CoordinatesHandler() );
    /* gml:coord - Deprecated with GML version 3.0. Use "pos" instead. */ 
    specs.registerSpecificationForGeometry( new CoordHandler() );
  }
}
