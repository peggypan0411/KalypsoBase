/*
 * --------------- Kalypso-Header --------------------------------------------------------------------
 *
 * This file is part of kalypso. Copyright (C) 2004, 2005 by:
 *
 * Technical University Hamburg-Harburg (TUHH) Institute of River and coastal engineering Denickestr. 22 21073 Hamburg,
 * Germany http://www.tuhh.de/wb
 *
 * and
 *
 * Bjoernsen Consulting Engineers (BCE) Maria Trost 3 56070 Koblenz, Germany http://www.bjoernsen.de
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Contact:
 *
 * E-Mail: belger@bjoernsen.de schlienger@bjoernsen.de v.doemming@tuhh.de
 *
 * ---------------------------------------------------------------------------------------------------
 */
package org.kalypso.gmlschema.property.relation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexRestrictionType;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexType;
import org.kalypso.gmlschema.GMLSchema;
import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.builder.IInitialize;
import org.kalypso.gmlschema.xml.ComplexTypeReference;
import org.kalypso.gmlschema.xml.ElementWithOccurs;

/**
 * representation of a feature content definition from xml schema that is defined by restriction.
 *
 * @author doemming
 */
public class RelationContentTypeFromRestriction extends RelationContentType
{
  private final ComplexRestrictionType m_restriction;

  private final List<ElementWithOccurs> m_elements;

  private AttributeGroupRef[] m_attributeGroupArray;

  public RelationContentTypeFromRestriction( final GMLSchema schema, final ComplexType complexType, final ComplexRestrictionType restriction ) throws GMLSchemaException
  {
    super( schema, complexType );
    m_restriction = restriction;
    m_elements = GMLSchemaUtilities.collectElements( schema, restriction, null, null );
  }

  /**
   * @see org.kalypso.gmlschema.feature.FeatureContentType#getSequence()
   */
  @Override
  public List<ElementWithOccurs> getSequence( )
  {
    return m_elements;
  }

  /**
   * @see org.kalypso.gmlschema.basics.IInitialize#init(int)
   */
  @Override
  public void init( final int initializeRun ) throws GMLSchemaException
  {
    switch( initializeRun )
    {
      case IInitialize.INITIALIZE_RUN_FIRST:
        final QName base = m_restriction.getBase();
        final ComplexTypeReference reference = getGMLSchema().resolveComplexTypeReference( base );
        final ComplexType complexType = reference.getComplexType();
        m_attributeGroupArray = complexType.getAttributeGroupArray();
        break;
    }

    super.init( initializeRun );
  }

  @Override
  public AttributeGroupRef[] getAttributeGroups( )
  {
    return m_attributeGroupArray;
  }

  @Override
  public String[] collectReferences( )
  {
    final XmlObject[] xmlObjects = getComplexType().selectPath( RelationContentType.DOCREF_XPATH );
    final XmlObject[] xmlObjectsFromRestriction = m_restriction.selectPath( RelationContentType.DOCREF_XPATH );

    /* Derived refs overwrite ths from the restriction. */
    final Set<String> refs = new HashSet<>( xmlObjects.length );
    for( final XmlObject object : xmlObjectsFromRestriction )
      refs.add( object.newCursor().getTextValue().trim() );
    for( final XmlObject object : xmlObjects )
      refs.add( object.newCursor().getTextValue().trim() );

    return refs.toArray( new String[refs.size()] );
  }

}
