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
package org.kalypso.ogc.gml.command;

import java.util.ArrayList;
import java.util.List;

import org.kalypso.core.i18n.Messages;
import org.kalypso.ogc.gml.selection.EasyFeatureWrapper;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Curve;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree.model.sort.JMSpatialIndex;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * Finds features in the map
 * 
 * @author von D�mming
 */
public class JMSelector
{
  /**
   * // selects all features (display elements) that are located within the submitted bounding box. // GMLGeometry
   * gmlGeometry=GMLFactory.createGMLGeometry(bbox); //Operation operation=new
   * SpatialOperation(OperationDefines.WITHIN,myPropertyName,gmlGeometry); //Filter filter=new ComplexFilter(operation);
   */
  public static List<Object> select( final GM_Envelope env, final JMSpatialIndex<Object> list, final boolean selectWithinBoxStatus )
  {
    try
    {
      final List<Object> testFE = new ArrayList<>();

      final List<Object> features = list == null ? new ArrayList<>() : list.query( env, new ArrayList<>() );
      for( final Object fe : features )
      {
        final Feature feature;
        if( fe instanceof Feature )
          feature = (Feature)fe;
        else if( fe instanceof EasyFeatureWrapper )
          feature = ((EasyFeatureWrapper)fe).getFeature();
        else
          continue;

        final GM_Object defaultGeometryProperty = feature.getDefaultGeometryPropertyValue();
        if( defaultGeometryProperty != null )
        {
          final String coordinateSystem = defaultGeometryProperty.getCoordinateSystem();

          final GM_Polygon bbox = GeometryFactory.createGM_Surface( env, coordinateSystem );

          if( selectWithinBoxStatus && bbox.contains( defaultGeometryProperty ) || !selectWithinBoxStatus && bbox.intersects( defaultGeometryProperty ) )
            testFE.add( fe );
        }
      }

      return testFE;
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }

  /**
   * selects all features that intersects the submitted point
   */
  public static List<Object> select( final GM_Position position, final JMSpatialIndex<Object> list )
  {
    final List<Object> resultList = new ArrayList<>();
    final List<Object> testFe = new ArrayList<>();

    final List<Object> features = list.query( position, new ArrayList<>() );
    for( final Object object : features )
    {
      final Feature feature;
      if( object instanceof Feature )
        feature = (Feature)object;
      else if( object instanceof EasyFeatureWrapper )
        feature = ((EasyFeatureWrapper)object).getFeature();
      else
        continue;

      try
      {
        if( feature.getDefaultGeometryPropertyValue().contains( position ) )
          testFe.add( object );
      }
      catch( final Exception err )
      {
        System.out.println( err.getMessage() );
        System.out.println( Messages.getString( "org.kalypso.ogc.gml.command.JMSelector.0" ) ); //$NON-NLS-1$
        System.out.println( Messages.getString( "org.kalypso.ogc.gml.command.JMSelector.1" ) ); //$NON-NLS-1$
        resultList.addAll( select( position, 0.0001d, list, false ) );
      }
    }

    resultList.addAll( testFe );

    return resultList;
  }

  /**
   * selects all features (display elements) that are located within the circle described by the position and the
   * radius.
   */
  public static List<Object> select( final GM_Position pos, final double r, final JMSpatialIndex<Object> list, final boolean withinStatus )
  {
    final GM_Envelope env = GeometryFactory.createGM_Envelope( pos.getX() - r, pos.getY() - r, pos.getX() + r, pos.getY() + r, null );
    final List<Object> resultDE = select( env, list, withinStatus );

    return resultDE;
  }

  public static Object selectNearest( final GM_Point pos, final double r, final JMSpatialIndex<Object> list, final boolean withinStatus )
  {
    Object result = null;
    double dist = 0;
    final List<Object> listFE = select( pos.getPosition(), r, list, withinStatus );
    for( final Object fe : listFE )
    {
      final Feature feature;
      if( fe instanceof Feature )
        feature = (Feature)fe;
      else if( fe instanceof EasyFeatureWrapper )
        feature = ((EasyFeatureWrapper)fe).getFeature();
      else
        continue;

      // TODO: ich bin der Meinung das ist bloedsinn, Gernot
      // TODO: nachtrag: es konnte auch bisher nicht richtig funktionierne,
      // weil deegree die distance nicht implementiert hat!
      final GM_Object defaultGeometryProperty = feature.getDefaultGeometryPropertyValue();
      double distance = defaultGeometryProperty.distance( pos );
      // some geometries must be prefered, otherwise it is not possible to
      // select a point inside a polygon as the polygone is always more near
      if( defaultGeometryProperty instanceof GM_Polygon )
        distance += 2 * r / 5;
      if( defaultGeometryProperty instanceof GM_Curve )
        distance += r / 5;
      if( result == null || distance < dist )
      {
        result = fe;
        dist = distance;
      }
    }
    return result;
  }

  public static GM_Position selectNearestHandel( final GM_Object geom, final GM_Position pos, final double snapRadius )
  {
    GM_Position[] positions = null;
    try
    {
      if( geom instanceof GM_Polygon )
      {
        final GM_Polygon surface = (GM_Polygon)geom;
        positions = surface.getSurfaceBoundary().getExteriorRing().getPositions();
      }
      else if( geom instanceof GM_Curve )
      {
        final GM_Curve curve = (GM_Curve)geom;
        positions = curve.getAsLineString().getPositions();
      }
      else if( geom instanceof GM_Point )
      {
        final GM_Point point = (GM_Point)geom;
        positions = new GM_Position[] { GeometryFactory.createGM_Position( point.getX(), point.getY() ) };
      }
      else
        positions = new GM_Position[] {};
    }
    catch( final GM_Exception e )
    {
      e.printStackTrace();
      // do nothing else
    }
    for( final GM_Position position : positions )
    {
      if( position.getDistance( pos ) <= snapRadius )
        return position;
    }
    return null;
  }
}