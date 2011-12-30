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
package org.kalypsodeegree.model.feature;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IAdaptable;
import org.kalypso.commons.xml.NS;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypsodeegree.model.geometry.GM_Object;

/**
 * @author Dirk Kuch
 */
public interface Feature extends BaseFeature, Deegree2Feature, IAdaptable
{
  QName QN_NAME = new QName( NS.GML3, "name" ); //$NON-NLS-1$

  QName QN_DESCRIPTION = new QName( NS.GML3, "description" ); //$NON-NLS-1$

  QName QN_BOUNDED_BY = new QName( NS.GML3, "boundedBy" ); //$NON-NLS-1$

  QName QN_LOCATION = new QName( NS.GML3, "location" ); //$NON-NLS-1$

  /** QName of gml's gml:_Feature */
  QName QNAME_FEATURE = new QName( NS.GML3, "_Feature" ); //$NON-NLS-1$

  /** Returns the gml:name property of the bound feature. */
  String getName( );

  /** Sets the gml:name property */
  void setName( String name );

// Defined in Deegree2Feature
// /** Returns the gml:description property of the bound feature. */
// public String getDescription( );

  /** Sets the gml_description property */
  void setDescription( String desc );

  /**
   * Return the gml:location property of the bound feature.<br>
   * REMARK: gml:location is deprecated in the GML3-Schema.
   */
  GM_Object getLocation( );

  /**
   * Sets the gml:location property to the bound feature.<br>
   * REMARK: gml:location is deprecated in the GML3-Schema.
   */
  void setLocation( final GM_Object location );

  /**
   * Resolves a related member feature.<br/>
   * Inline feature gets returned, feature links are returned a xlinked-features.
   */
  Feature getMember( IRelationType relation );

  /**
   * Resolves a related member feature.<br/>
   * Inline feature gets returned, feature links are returned a xlinked-features.
   * 
   * @param relation
   *          Name of a property of this feature (must be a relation).
   * @throws IllegalArgumentException
   *           If <code>relation</code> is not the name of a relation type.
   */
  Feature getMember( QName relation );
}
