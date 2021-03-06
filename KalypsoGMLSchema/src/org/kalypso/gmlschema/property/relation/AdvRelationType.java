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

import javax.xml.namespace.QName;

import org.apache.xmlbeans.impl.xb.xsdschema.Element;
import org.kalypso.gmlschema.GMLSchema;
import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.gmlschema.IGMLSchema;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.builder.IInitialize;
import org.kalypso.gmlschema.feature.FeatureType;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.AbstractPropertyTypeFromElement;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.xml.ElementReference;
import org.kalypso.gmlschema.xml.Occurs;

/**
 * @author doemming
 */
public class AdvRelationType extends AbstractPropertyTypeFromElement implements IRelationType
{
  private final static IDocumentReference[] DOCUMENT_REFERENCES = new IDocumentReference[] { IDocumentReference.SELF_REFERENCE };

  private final QName m_advReferenziertesElement;

  private IFeatureType m_ftRelationTarget = null;

  private IAnnotation m_annotation;

  public AdvRelationType( final IGMLSchema gmlSchema, final Element element, final Occurs occurs, final QName advReferenziertesElement, final IFeatureType featureType )
  {
    super( gmlSchema, featureType, element, occurs, null );
    m_advReferenziertesElement = advReferenziertesElement;
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
        final GMLSchema gmlSchema = (GMLSchema) getGMLSchema();
        final ElementReference reference = gmlSchema.resolveElementReference( m_advReferenziertesElement );
        final GMLSchema referencedSchema = (GMLSchema) reference.getGMLSchema();
        final Element element = reference.getElement();
        final Object buildedObject = referencedSchema.getBuildedObjectFor( element );
        if( buildedObject instanceof FeatureType )
          m_ftRelationTarget = (IFeatureType) buildedObject;
        else
          throw new UnsupportedOperationException();
        break;
    }
  }

  /**
   * @see org.kalypso.gmlschema.property.relation.IRelationType#isInlineAble()
   */
  @Override
  public boolean isInlineAble( )
  {
    return false;
  }

  /**
   * @see org.kalypso.gmlschema.property.relation.IRelationType#isLinkAble()
   */
  @Override
  public boolean isLinkAble( )
  {
    return true;
  }

  /**
   * @see org.kalypso.gmlschema.property.relation.IRelationContentType#getTargetFeatureTypes(org.kalypso.gmlschema.GMLSchema,
   *      boolean)
   */
  @Override
  public IFeatureType getTargetFeatureType( )
  {
    return m_ftRelationTarget;
  }

  /**
   * @see org.kalypso.gmlschema.property.relation.IRelationType#getDocumentReferences()
   */
  @Override
  public IDocumentReference[] getDocumentReferences( )
  {
    return DOCUMENT_REFERENCES;
  }

  /**
   * @see org.kalypso.gmlschema.property.IPropertyType#getAnnotation()
   */
  @Override
  public IAnnotation getAnnotation( )
  {
    return m_annotation;
  }

  /**
   * @see org.kalypso.gmlschema.property.IPropertyType#cloneForFeatureType(org.kalypso.gmlschema.feature.IFeatureType)
   */
  @Override
  public IPropertyType cloneForFeatureType( final IFeatureType featureType )
  {
    return new AdvRelationType( getGMLSchema(), getElement(), getOccurs(), m_advReferenziertesElement, featureType );
  }
}
