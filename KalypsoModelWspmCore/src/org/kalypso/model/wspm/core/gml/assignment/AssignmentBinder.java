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
package org.kalypso.model.wspm.core.gml.assignment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.IXLinkedFeature;

/**
 * Binding for Assignment-Gml
 *
 * @author Gernot Belger
 */
public class AssignmentBinder
{
  private static final QName QNAME_ASSIGNMENT_MEMBER = new QName( IWspmConstants.NS_WSPMPROF_ASSIGNMENT, "assignmentMember" ); //$NON-NLS-1$

  private static final QName QNAME_SOURCE_ID = new QName( IWspmConstants.NS_WSPMPROF_ASSIGNMENT, "sourceId" ); //$NON-NLS-1$

  private static final QName QNAME_POINT_ASSIGNMENT_MEMBER = new QName( IWspmConstants.NS_WSPMPROF_ASSIGNMENT, "pointAssignmentMember" ); //$NON-NLS-1$

  private static final QName QNAME_POINT_PROPERTY = new QName( IWspmConstants.NS_WSPMPROF_ASSIGNMENT, "pointProperty" ); //$NON-NLS-1$

  private static final QName QNAME_VALUE = new QName( IWspmConstants.NS_WSPMPROF_ASSIGNMENT, "value" ); //$NON-NLS-1$

  private final GMLWorkspace m_assignmentWorkspace;

  private Map<String, Map<String, Object>> m_valueToAssignment = null;

  public AssignmentBinder( final GMLWorkspace assignmentWorkspace )
  {
    m_assignmentWorkspace = assignmentWorkspace;
  }

  public Map<String, Object> getAssignmentsFor( final String value )
  {
    final Map<String, Object> map = getValueToAssignment().get( value );
    if( map == null )
      return Collections.EMPTY_MAP;

    return map;
  }

  private Map<String, Map<String, Object>> getValueToAssignment( )
  {
    if( m_valueToAssignment != null )
      return m_valueToAssignment;

    final Feature rootFeature = m_assignmentWorkspace.getRootFeature();
    final FeatureList assignmentMembers = (FeatureList) rootFeature.getProperty( QNAME_ASSIGNMENT_MEMBER );
    m_valueToAssignment = new HashMap<>();
    for( final Object o : assignmentMembers )
    {
      final Feature assignmentMember = (Feature) o;
      final String sourceId = (String) assignmentMember.getProperty( QNAME_SOURCE_ID );

      final FeatureList pointAssignments = (FeatureList) assignmentMember.getProperty( QNAME_POINT_ASSIGNMENT_MEMBER );
      final Map<String, Object> values = new HashMap<>( pointAssignments.size() );
      for( final Object o2 : pointAssignments )
      {
        final Feature pointFeature = (Feature) o2;
        final IXLinkedFeature pp = (IXLinkedFeature) pointFeature.getProperty( QNAME_POINT_PROPERTY );
        final Double value = (Double) pointFeature.getProperty( QNAME_VALUE );
        values.put( pp.getHref(), value );
      }

      m_valueToAssignment.put( sourceId, values );
    }

    return m_valueToAssignment;
  }

}
