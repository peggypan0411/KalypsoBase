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
package org.kalypso.ogc.gml.om;

import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.Assert;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.property.restriction.IRestriction;
import org.kalypso.gmlschema.property.restriction.RestrictionUtilities;
import org.kalypso.gmlschema.swe.RepresentationType;
import org.kalypso.observation.phenomenon.FeaturePhenomenon;
import org.kalypso.observation.phenomenon.IPhenomenon;
import org.kalypso.observation.result.AbstractComponent;
import org.kalypso.observation.result.ComponentUtilities;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.IXLinkedFeature;
import org.kalypsodeegree_impl.gml.binding.commons.NamedFeatureHelper;

/**
 * A component which was previously read from a feature.
 * 
 * @author schlienger
 */
public class FeatureComponent extends AbstractComponent
{
  private final Feature m_itemDef;

  private final String m_uri;

  public FeatureComponent( final Feature itemDef, final String uri )
  {
    // TODO: check if this works for all old code;
    // TODO: remove null checks below
    Assert.isNotNull( itemDef );

    m_itemDef = itemDef;
    m_uri = uri;
  }

  public FeatureComponent( final Feature itemDef )
  {
    this( itemDef, null );
  }

  @Override
  public Object getDefaultValue( )
  {
    // HACK: in case of enums, we use the first value. Maybe we could add a annotation for that
    final IRestriction[] restrictions = getRestrictions();
    if( ComponentUtilities.restrictionContainsEnumeration( restrictions ) )
    {
      final Map<Object, IAnnotation> items = RestrictionUtilities.getEnumerationItems( restrictions );
      if( items.size() > 0 )
        return items.keySet().iterator().next();
    }

    // REMARK: The ItemDefinition has no notion of default value, so for now we always return null.
    // TODO: Maybe we can define a meaningful policy what to return as a default value (for example: first value of an
    // enumeration)
    return null;
  }

  @Override
  public String getDescription( )
  {
    if( m_itemDef == null )
      return ""; //$NON-NLS-1$
    return NamedFeatureHelper.getDescription( m_itemDef );
  }

  @Override
  public String getFrame( )
  {
    return getRepresentationType().getFrame();
  }

  @Override
  public String getId( )
  {
    if( m_itemDef instanceof IXLinkedFeature )
      return ((IXLinkedFeature) m_itemDef).getHref();

    if( m_itemDef == null )
      return m_uri;

    if( m_uri == null )
      return m_itemDef.getId();

    return m_uri + "#" + m_itemDef.getId(); //$NON-NLS-1$
  }

  public Feature getItemDefinition( )
  {
    return m_itemDef;
  }

  @Override
  public String getName( )
  {
    if( m_itemDef == null )
      return "<no name>"; //$NON-NLS-1$
    return NamedFeatureHelper.getName( m_itemDef );
  }

  @Override
  public IPhenomenon getPhenomenon( )
  {
    if( m_itemDef == null )
      return null;

    final Object phenomProperty = m_itemDef.getProperty( ObservationFeatureFactory.SWE_PROPERTY );

    if( phenomProperty instanceof String )
    {
      final Feature phenomenonFeature = m_itemDef.getWorkspace().getFeature( (String) phenomProperty );
      return new FeaturePhenomenon( phenomenonFeature );
    }
    else if( phenomProperty instanceof Feature )
      return new FeaturePhenomenon( (Feature) phenomProperty );

    return null;
  }

  public RepresentationType getRepresentationType( )
  {
    return (RepresentationType) m_itemDef.getProperty( ObservationFeatureFactory.SWE_REPRESENTATION );
  }

  @Override
  public String getUnit( )
  {
    return getRepresentationType().getUnit();
  }

  @Override
  public QName getValueTypeName( )
  {
    return getRepresentationType().getValueTypeName();
  }

  @Override
  public IRestriction[] getRestrictions( )
  {
    return getRepresentationType().getRestrictions();
  }

  @Override
  public String toString( )
  {
    final String id = m_itemDef == null ? null : m_itemDef.getId();
    return String.format( "%s#%s", m_uri, id ); //$NON-NLS-1$
  }
}
