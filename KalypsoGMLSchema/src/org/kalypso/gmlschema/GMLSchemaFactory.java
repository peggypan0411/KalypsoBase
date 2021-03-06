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
package org.kalypso.gmlschema;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument.Schema;
import org.kalypso.commons.xml.NS;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.builder.ComplexType2FeatureContentTypeBuilder;
import org.kalypso.gmlschema.builder.ComplexType2PropertyContentFromTypeHandlerTypeBuilder;
import org.kalypso.gmlschema.builder.ComplexType2RelationContentTypeBuilder;
import org.kalypso.gmlschema.builder.ComplexTypeDirectReference2RelationContentTypeBuilder;
import org.kalypso.gmlschema.builder.Element2FeatureTypeBuilder;
import org.kalypso.gmlschema.builder.Element2PropertyTypeBuilder;
import org.kalypso.gmlschema.builder.ElementDirektReference2RelationTypeBuilder;
import org.kalypso.gmlschema.builder.FeatureContentType2ElementBuilder;
import org.kalypso.gmlschema.builder.FeaturePropertyType2RelationTypeBuilder;
import org.kalypso.gmlschema.builder.FeatureType2ComplexTypeBuilder;
import org.kalypso.gmlschema.builder.GMLSchema2SchemaBasicsBuilder;
import org.kalypso.gmlschema.builder.GMLSchemaBuilder;
import org.kalypso.gmlschema.builder.GeometryPropertyBuilder;
import org.kalypso.gmlschema.builder.PropertyType2SimpleTypeBuilder;
import org.kalypso.gmlschema.builder.RelationType2ComplexTypeBuilder;
import org.kalypso.gmlschema.feature.CustomFeatureType;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.i18n.Messages;
import org.kalypso.gmlschema.property.CustomPropertyContentType;
import org.kalypso.gmlschema.property.IPropertyContentType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.gmlschema.property.relation.CustomRelationType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.gmlschema.property.restriction.IRestriction;
import org.kalypso.gmlschema.property.value.CustomValuePropertyType;
import org.kalypso.gmlschema.types.IMarshallingTypeHandler;

/**
 * Factory for various GML-Schema elements.
 *
 * @author doemming
 */
public class GMLSchemaFactory
{
  private static QName QNAME_FEATURE = new QName( NS.GML3, "_Feature" ); //$NON-NLS-1$

  private static Map<String, GMLSchemaBuilder> m_gmlSchemaBuilderRegister = new HashMap<>();

  private static synchronized GMLSchemaBuilder getBuilderForVersion( final String version )
  {
    if( !m_gmlSchemaBuilderRegister.containsKey( version ) )
    {
      final GMLSchemaBuilder gmlSchemaBuilder = new GMLSchemaBuilder( version );
      // schema
      gmlSchemaBuilder.registerBuilder( new GMLSchema2SchemaBasicsBuilder() ); // ok
      // properties
      // relations
      // featuretype
      // test
      gmlSchemaBuilder.registerBuilder( new ComplexType2PropertyContentFromTypeHandlerTypeBuilder( version ) );// ok

      // Geometry-Properties
      gmlSchemaBuilder.registerBuilder( new GeometryPropertyBuilder( version ) );

      // complex type ...
      gmlSchemaBuilder.registerBuilder( new ComplexType2FeatureContentTypeBuilder( version ) );// OK
      // for local elements
      gmlSchemaBuilder.registerBuilder( new FeatureContentType2ElementBuilder() ); // OK

      // element ...
      gmlSchemaBuilder.registerBuilder( new Element2FeatureTypeBuilder( version ) ); // ok
      gmlSchemaBuilder.registerBuilder( new FeatureType2ComplexTypeBuilder() );// ok

      // element ...
      gmlSchemaBuilder.registerBuilder( new Element2PropertyTypeBuilder( version ) );// ok
      gmlSchemaBuilder.registerBuilder( new PropertyType2SimpleTypeBuilder() );// ok

      // relations
      gmlSchemaBuilder.registerBuilder( new FeaturePropertyType2RelationTypeBuilder( version ) ); // ok
      gmlSchemaBuilder.registerBuilder( new ElementDirektReference2RelationTypeBuilder( version ) ); // test

      gmlSchemaBuilder.registerBuilder( new ComplexTypeDirectReference2RelationContentTypeBuilder( version ) ); // test

      gmlSchemaBuilder.registerBuilder( new RelationType2ComplexTypeBuilder() ); // ok
      gmlSchemaBuilder.registerBuilder( new ComplexType2RelationContentTypeBuilder( version ) ); // ok

      m_gmlSchemaBuilderRegister.put( version, gmlSchemaBuilder );
    }
    return m_gmlSchemaBuilderRegister.get( version );
  }

  public static GMLSchema createGMLSchema( final String gmlVersion, final URL schemaLocationURL ) throws GMLSchemaException
  {
    Debug.LOADING.printf( "Loading schema: %s%n", schemaLocationURL ); //$NON-NLS-1$

    InputStream inputStream = null;
    try
    {
      inputStream = schemaLocationURL.openStream();
      final GMLSchema createGMLSchema = createGMLSchema( inputStream, gmlVersion, schemaLocationURL );
      inputStream.close();
      return createGMLSchema;
    }
    catch( final Throwable e )
    {
      Debug.LOADING.printf( "Failed to load schema:%n" ); //$NON-NLS-1$
      Debug.LOADING.printStackTrace( e );

      throw new GMLSchemaException( "Unable to load schema: " + e.getMessage(), e ); //$NON-NLS-1$
    }
    finally
    {
      IOUtils.closeQuietly( inputStream );
    }
  }

  public static GMLSchema createGMLSchema( final InputStream inputStream, final String gmlVersion, final URL context ) throws GMLSchemaException
  {
    SchemaDocument schemaDocument;
    try
    {
      Debug.LOADING.printf( "Reading schema with xmlbeans%n" ); //$NON-NLS-1$
      schemaDocument = SchemaDocument.Factory.parse( inputStream );
    }
    catch( final Throwable e )
    {
      Debug.LOADING.printf( "Failed to load schema:%n" ); //$NON-NLS-1$
      Debug.LOADING.printStackTrace( e );

      throw new GMLSchemaException( Messages.getString( "org.kalypso.gmlschema.GMLSchemaFactory.0", e.getMessage() ), e ); //$NON-NLS-1$
    }

    final Schema schema = schemaDocument.getSchema();
    if( schema == null )
    {
      // this is crap, apache catches the exception and keeps it :-(
      // TODO send bugreport/featurerequest to apache
      // documentation of errors:
      // - namespace not set (e.g. xmlns:adv="...")
      // TODO use a schema-validator to report
      // REMARK:
      // DID you have a look at XMLOptions#setErrorListener ?

      Debug.LOADING.printf( "Schema binding failed, xmlbeans returned null.%n" ); //$NON-NLS-1$

      throw new GMLSchemaException( Messages.getString( "org.kalypso.gmlschema.GMLSchemaFactory.1" ) ); //$NON-NLS-1$
    }
    // TODO: read schema version from schemadocument
    // it would be nice to do something like that:
    // schema.selectAttribute( "", "version" ).getDomNode().getNodeValue();
    // but this is the version of the loaded schema, not the version of the gml schema...
    // maybe put a processing instruction there??
    // this could also be used to later load the right GML schema

    // switch GML3 / GML2
    final String version;
    if( gmlVersion == null )
    {
      final String parsedGmlVersion = GMLSchemaUtilities.parseGmlVersion( schemaDocument );

      // we must decide for the first schema, which gml version it is
      // if we have no appinfo, default to gml2?

      version = parsedGmlVersion == null ? IGMLSchema.VERSION_GML3_1_1 : parsedGmlVersion; //$NON-NLS-1$
    }
    else
      version = gmlVersion;

    Debug.LOADING.printf( "Schema (%s) was read. Building GML-Schema using GML-Version %s.%n", schemaDocument.getSchema().getTargetNamespace(), version ); //$NON-NLS-1$

    final GMLSchemaBuilder builder = getBuilderForVersion( version );
    return builder.buildGMLSchema( schemaDocument, context );
  }

  /**
   * Same as {@link CustomFeatureType#CustomFeatureType(IGMLSchema, QName, IPropertyType[], Feature.QNAME_FEATURE )}
   */
  public static IFeatureType createFeatureType( final QName qName, final IPropertyType[] properties )
  {
    return new CustomFeatureType( new EmptyGMLSchema(), qName, properties, QNAME_FEATURE );
  }

  /**
   * Create a feature type that substitutes _Feature. <br/>
   * Same as {@link CustomFeatureType#CustomFeatureType(QName, IPropertyType[], IGMLSchema, Feature.QNAME_FEATURE )}
   */
  public static IFeatureType createFeatureType( final QName qName, final IPropertyType[] properties, final IGMLSchema schema )
  {
    return new CustomFeatureType( schema, qName, properties, QNAME_FEATURE );
  }

  public static IFeatureType createFeatureType( final QName qName, final IPropertyType[] properties, final IGMLSchema schema, final QName subsFTQname )
  {
    return new CustomFeatureType( schema, qName, properties, subsFTQname );
  }

  public static IValuePropertyType createValuePropertyType( final QName name, final IMarshallingTypeHandler typeHandler, final int minOccurs, final int maxOccurs, final boolean isNillable, final IAnnotation annotation )
  {
    final IPropertyContentType pct = new CustomPropertyContentType( typeHandler );
    return new CustomValuePropertyType( name, pct, new IRestriction[] {}, minOccurs, maxOccurs, isNillable, annotation );
  }

  public static IValuePropertyType createValuePropertyType( final QName name, final IMarshallingTypeHandler typeHandler, final int minOccurs, final int maxOccurs, final boolean isNillable )
  {
    final IPropertyContentType pct = new CustomPropertyContentType( typeHandler );
    return new CustomValuePropertyType( name, pct, new IRestriction[] {}, minOccurs, maxOccurs, isNillable );
  }

  public static IRelationType createRelationType( final QName qName, final IFeatureType targetFT, final int minOccurs, final int maxOccurs, final boolean isNillable )
  {
    return new CustomRelationType( qName, targetFT, minOccurs, maxOccurs, isNillable );
  }
}
