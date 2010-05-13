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
package org.kalypso.gml.ui.commands.exportshape;

import java.nio.charset.Charset;

import org.kalypso.ogc.gml.selection.IFeatureSelection;
import org.kalypso.shape.deegree.IShapeDataFactory;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Gernot Belger
 */
public class ExportTin2ShapeWizard extends ExportShapeWizard
{
  public ExportTin2ShapeWizard( final IFeatureSelection featureSelection, final String fileName )
  {
    super( featureSelection, fileName );
  }

  /**
   * @see org.kalypso.gml.ui.commands.exportshape.ExportShapeWizard#createDataFactory(org.kalypsodeegree.model.feature.Feature[],
   *      java.nio.charset.Charset, java.lang.String)
   */
  @Override
  protected IShapeDataFactory createDataFactory( final Feature[] chosenFeatures, final Charset shapeCharset, final String coordinateSystem )
  {
    return new Tin2ShapeDataFactory( chosenFeatures, shapeCharset, coordinateSystem );
  }
}
