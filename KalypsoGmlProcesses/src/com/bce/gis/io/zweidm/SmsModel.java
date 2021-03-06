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
package com.bce.gis.io.zweidm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * HINT: It is needed to store all data (nodes and elements), because the format does not specify an order for defining
 * the nodes, elements (i.e. first the nodes, than the elements). The nodes may come last...
 *
 * @author Thomas Jung
 */
public class SmsModel implements ISmsModel
{
  private final GeometryFactory m_geometryFactory = new GeometryFactory();

  private final List<SmsElement> m_elementList = new ArrayList<>();

  final Map<Integer, Coordinate> m_nodeMap = new HashMap<>();

  private final int m_srid;

  public SmsModel( final int srid )
  {
    m_srid = srid;
  }

  @Override
  public void addElement( final String lineString, final int id, final Integer[] nodeIds, final int roughnessClassID )
  {
    final SmsElement data = new SmsElement( this, id, nodeIds );
    m_elementList.add( data );
  }

  @Override
  public void addNode( final String lineString, final int id, final double easting, final double northing, final double elevation )
  {
    final Coordinate position = new Coordinate( easting, northing, elevation );
    m_nodeMap.put( id, position );
  }

  @Override
  public Coordinate getNode( final Integer nodeId )
  {
    return m_nodeMap.get( nodeId );
  }

  @Override
  public List<SmsElement> getElementList( )
  {
    return Collections.unmodifiableList( m_elementList );
  }

  @Override
  public int getSrid( )
  {
    return m_srid;
  }

  @Override
  public GeometryFactory getGeometryFactory( )
  {
    return m_geometryFactory;
  }
}