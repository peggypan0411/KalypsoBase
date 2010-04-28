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
package org.kalypso.model.wspm.ui.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IProgressMonitor;
import org.kalypso.commons.xml.NS;
import org.kalypso.contribs.eclipse.ui.progress.ProgressUtilities;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.model.wspm.core.IWspmConstants;
import org.kalypso.model.wspm.core.KalypsoModelWspmCoreExtensions;
import org.kalypso.model.wspm.core.gml.IProfileFeature;
import org.kalypso.model.wspm.core.gml.ProfileFeatureFactory;
import org.kalypso.model.wspm.core.gml.assignment.AssignmentBinder;
import org.kalypso.model.wspm.core.profil.IProfil;
import org.kalypso.model.wspm.core.profil.IProfilPointPropertyProvider;
import org.kalypso.model.wspm.core.profil.filter.IProfilePointFilter;
import org.kalypso.model.wspm.core.util.WspmGeometryUtilities;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.observation.result.TupleResultUtilities;
import org.kalypso.ogc.gml.command.FeatureChange;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;
import org.kalypsodeegree_impl.model.geometry.JTSAdapter;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Gernot Belger
 */
public class RoughnessIntersector
{
  private final Object[] m_profileFeatures;

  private final FeatureList m_polygoneFeatures;

  private final IPropertyType m_polygoneGeomType;

  private final IPropertyType m_polygoneValueType;

  private final AssignmentBinder m_assignment;

  private final IProfilePointFilter m_pointFilter;

  public RoughnessIntersector( final Object[] profileFeatures, final FeatureList polygoneFeatures, final IPropertyType polygoneGeomType, final IPropertyType polygoneValueType, final AssignmentBinder assignment, final IProfilePointFilter pointFilter )
  {
    m_profileFeatures = profileFeatures;
    m_polygoneFeatures = polygoneFeatures;
    m_polygoneGeomType = polygoneGeomType;
    m_polygoneValueType = polygoneValueType;
    m_assignment = assignment;
    m_pointFilter = pointFilter;
  }

  public FeatureChange[] intersect( final IProgressMonitor monitor ) throws Exception
  {
    monitor.beginTask( org.kalypso.model.wspm.ui.i18n.Messages.getString( "org.kalypso.model.wspm.ui.wizard.RoughnessIntersector.0" ), m_profileFeatures.length ); //$NON-NLS-1$

    final List<FeatureChange> changes = new ArrayList<FeatureChange>();

    /* apply polygone data to profile data */
    for( final Object object : m_profileFeatures )
    {
      final IProfileFeature profile = (IProfileFeature) object;
      final String crs = profile.getSrsName();
      final IProfil profil = profile.getProfil();
      // TODO: check if the profile has all components already.
      // but how to do, we don't know here what components are necessary for the current profile...
      final String label = FeatureHelper.getAnnotationValue( profile, IAnnotation.ANNO_LABEL );

      final IRecord[] points = profil.getPoints();

      final int widthIndex = profil.indexOfProperty( IWspmConstants.POINT_PROPERTY_BREITE );
      final int rwIndex = profil.indexOfProperty( IWspmConstants.POINT_PROPERTY_RECHTSWERT );
      final int hwIndex = profil.indexOfProperty( IWspmConstants.POINT_PROPERTY_HOCHWERT );

      final double[] rws = TupleResultUtilities.getInterpolatedValues( profil, rwIndex, widthIndex );
      final double[] hws = TupleResultUtilities.getInterpolatedValues( profil, hwIndex, widthIndex );

      int count = 1;

      for( int i = 0; i < points.length; i++ )
      {
        final IRecord point = points[i];

        if( count % 10 == 0 )
        {
          final String subTaskMsg = String.format( "%s (%d/%d)", label, count, points.length ); //$NON-NLS-1$
          monitor.subTask( subTaskMsg );
        }

        if( !m_pointFilter.accept( profil, point ) )
          continue;

        final double rw = rws[i];
        final double hw = hws[i];
        if( !Double.isNaN( rw ) && !Double.isNaN( hw ) )
        {
          final GM_Point geoPoint = WspmGeometryUtilities.pointFromRwHw( rw, hw, Double.NaN, crs, WspmGeometryUtilities.GEO_TRANSFORMER );
          if( geoPoint != null )
          {
            final Geometry jtsPoint = JTSAdapter.export( geoPoint );
            assignValueToPoint( profil, point, geoPoint, jtsPoint );
          }
        }

        count++;
      }

      final FeatureChange[] fcs = ProfileFeatureFactory.toFeatureAsChanges( profil, profile );
      Collections.addAll( changes, fcs );

      ProgressUtilities.worked( monitor, 1 );
    }

    return changes.toArray( new FeatureChange[changes.size()] );
  }

  @SuppressWarnings("unchecked")
  private List<Object> assignValueToPoint( final IProfil profil, final IRecord point, final GM_Point geoPoint, final Geometry jtsPoint ) throws GM_Exception
  {
    final TupleResult owner = point.getOwner();

    /* find polygon for location */
    final List<Object> foundPolygones = m_polygoneFeatures.query( geoPoint.getPosition(), null );
    for( final Object polyObject : foundPolygones )
    {
      final Feature polygoneFeature = (Feature) polyObject;

      // BUGFIX: use any gm_object here, because we do not know what it is (surface, multi surface, ...)
      final GM_Object gmObject = (GM_Object) polygoneFeature.getProperty( m_polygoneGeomType );

      final Geometry jtsGeom = JTSAdapter.export( gmObject );
      if( jtsGeom.contains( jtsPoint ) )
      {
        final Object polygoneValue = polygoneFeature.getProperty( m_polygoneValueType );
        if( polygoneValue != null )
        {
          // find assignment for polygon
          final Map<String, Double> assignments = m_assignment.getAssignmentsFor( polygoneValue.toString() );
          // apply assignment to point properties
          for( final Map.Entry<String, Double> entry : assignments.entrySet() )
          {
            final String componentId = entry.getKey();
            final Double newValue = entry.getValue();

            if( newValue != null )
            {
              if( componentId != null )
              {
                final IProfilPointPropertyProvider provider = KalypsoModelWspmCoreExtensions.getPointPropertyProviders( profil.getType() );

                final IComponent component = provider.getPointProperty( componentId );

                Object defaultValue = component.getDefaultValue();
                if( defaultValue == null && component.getValueTypeName().equals( new QName( NS.XSD_SCHEMA, "double" ) ) ) //$NON-NLS-1$
                  defaultValue = 0.0;

                profil.addPointProperty( component, defaultValue );

                point.setValue( owner.indexOfComponent( component ), newValue );
              }
            }
          }
        }
        // DONT break, because we may have several polygone covering the point, but only one has an assigned value
        // break;
      }
    }
    return foundPolygones;
  }
}
