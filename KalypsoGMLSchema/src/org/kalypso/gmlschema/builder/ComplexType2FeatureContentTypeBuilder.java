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
package org.kalypso.gmlschema.builder;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.impl.xb.xsdschema.ComplexContentDocument.ComplexContent;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexRestrictionType;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.ExtensionType;
import org.kalypso.commons.xml.NS;
import org.kalypso.gmlschema.GMLSchema;
import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.feature.FeatureContentType;
import org.kalypso.gmlschema.feature.FeatureContentTypeFromExtension;
import org.kalypso.gmlschema.feature.FeatureContentTypeFromRestriction;
import org.kalypso.gmlschema.feature.FeatureContentTypeFromSequence;
import org.kalypso.gmlschema.xml.ElementWithOccurs;

/**
 * a builder
 * 
 * @author doemming
 */
public class ComplexType2FeatureContentTypeBuilder extends AbstractBuilder
{

  private static final String ABSTRACT_GML_TYPE_STRING = "AbstractGMLType"; //$NON-NLS-1$

  private final String m_version;

  public ComplexType2FeatureContentTypeBuilder( final String version )
  {
    m_version = version;
  }

  /**
   * @see org.kalypso.gmlschema.builder.IBuilder#build(org.kalypso.gmlschema.GMLSchema, java.lang.Object)
   */
  @Override
  public Object[] build( final GMLSchema gmlSchema, final Object complexTypeObject ) throws GMLSchemaException
  {
    final ComplexType complexType = (ComplexType) complexTypeObject;

    final FeatureContentType result = buildResult( gmlSchema, complexType );

    gmlSchema.register( complexTypeObject, result );
    return new Object[] { result };
  }

  private FeatureContentType buildResult( final GMLSchema gmlSchema, final ComplexType complexType ) throws GMLSchemaException
  {
    final ComplexContent complexContent = complexType.getComplexContent();
    if( complexContent != null )
    {
      final ExtensionType extension = complexContent.getExtension();
      final ComplexRestrictionType restriction = complexContent.getRestriction();
      if( extension != null )
        return new FeatureContentTypeFromExtension( gmlSchema, complexType, extension );
      else if( restriction != null )
        return new FeatureContentTypeFromRestriction( gmlSchema, complexType, restriction );
      else
        throw new UnsupportedOperationException();
    }
    else
    {
      final List<ElementWithOccurs> collector = GMLSchemaUtilities.collectElements( gmlSchema, complexType, null, null );
      return new FeatureContentTypeFromSequence( gmlSchema, complexType, collector );
    }
  }

  /**
   * @see org.kalypso.gmlschema.builder.IBuilder#isBuilderFor(org.kalypso.gmlschema.GMLSchema, java.lang.Object,
   *      java.lang.String)
   */
  @Override
  public boolean isBuilderFor( final GMLSchema gmlSchema, final Object object, final String namedPass ) throws GMLSchemaException
  {
    if( !(object instanceof ComplexType) )
      return false;
    final ComplexType complexType = (ComplexType) object;
    if( ABSTRACT_GML_TYPE_STRING.equals( complexType.getName() ) && NS.GML2.equals( gmlSchema.getTargetNamespace() ) ) //$NON-NLS-1$
      return true;
    final QName baseType = GMLSchemaUtilities.findBaseType( gmlSchema, complexType, m_version );
    if( baseType == null )
      return false;
    final String namespaceURI = baseType.getNamespaceURI();
    final String localPart = baseType.getLocalPart();

    // GML
    if( !NS.GML2.equals( namespaceURI ) )
      return false;

    return GMLSchemaUtilities.getBaseOfFeatureType( m_version ).equals( localPart );
  }

  /**
   * @see org.kalypso.gmlschema.builder.IBuilder#replaces(org.kalypso.gmlschema.builder.IBuilder)
   */
  @Override
  public boolean replaces( final IBuilder other )
  {
    return false;
  }
}
