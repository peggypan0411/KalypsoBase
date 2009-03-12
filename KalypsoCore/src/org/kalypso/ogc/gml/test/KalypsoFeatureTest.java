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
package org.kalypso.ogc.gml.test;

import junit.framework.TestCase;

import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureType;
import org.deegree_impl.gml.schema.GMLSchema;
import org.deegree_impl.gml.schema.GMLSchemaCache;
import org.deegree_impl.model.feature.FeatureFactory;

/**
 * @author sbad0205
 */
public class KalypsoFeatureTest extends TestCase
{
  private Feature createFeature()
  {
    try
    {
      final GMLSchema schema = GMLSchemaCache.getSchema( KalypsoFeatureTest.class
          .getResource( "point.xsd" ) );
      final FeatureType featureType = schema.getFeatureTypes()[0];

      return FeatureFactory.createFeature( "x", featureType );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    return null;
  }

  public void testSelect()
  {
    Feature fe = createFeature();
    int s1 = 10;
    fe.select( s1 );
    assertEquals( true, fe.isSelected( s1 ) );
  }

  public void testUnselect()
  {
    Feature fe = createFeature();
    int s1 = 10;
    fe.select( s1 );
    assertEquals( true, fe.isSelected( s1 ) );
    fe.unselect( s1 );
    assertEquals( false, fe.isSelected( s1 ) );
  }

  public void testToggle()
  {
    Feature fe = createFeature();
    int s1 = 10;
    fe.toggle( s1 );
    assertEquals( true, fe.isSelected( s1 ) );
    fe.toggle( s1 );
    assertEquals( false, fe.isSelected( s1 ) );
  }

}