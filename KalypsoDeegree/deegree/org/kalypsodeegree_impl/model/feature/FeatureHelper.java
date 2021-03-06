/** This file is part of kalypso/deegree.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * history:
 *
 * Files in this package are originally taken from deegree and modified here
 * to fit in kalypso. As goals of kalypso differ from that one in deegree
 * interface-compatibility to deegree is wanted but not retained always.
 *
 * If you intend to use this software in other ways than in kalypso
 * (e.g. OGC-web services), you should consider the latest version of deegree,
 * see http://www.deegree.org .
 *
 * all modifications are licensed as deegree,
 * original copyright:
 *
 * Copyright (C) 2001 by:
 * EXSE, Department of Geography, University of Bonn
 * http://www.giub.uni-bonn.de/exse/
 * lat/lon GmbH
 * http://www.lat-lon.de
 */
package org.kalypsodeegree_impl.model.feature;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.Assert;
import org.kalypso.commons.tokenreplace.ITokenReplacer;
import org.kalypso.commons.tokenreplace.TokenReplacerEngine;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.contribs.java.lang.MultiException;
import org.kalypso.contribs.java.lang.NumberUtils;
import org.kalypso.contribs.javax.xml.namespace.QNameUnique;
import org.kalypso.gmlschema.GMLSchemaException;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.gmlschema.types.IMarshallingTypeHandler;
import org.kalypso.gmlschema.types.ISimpleMarshallingTypeHandler;
import org.kalypso.gmlschema.types.ITypeRegistry;
import org.kalypso.gmlschema.types.MarshallingTypeRegistrySingleton;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.filterencoding.Filter;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.FeatureVisitor;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.IXLinkedFeature;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_MultiSurface;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPathException;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPathUtilities;
import org.kalypsodeegree_impl.model.feature.tokenreplace.AnnotationTokenReplacer;
import org.kalypsodeegree_impl.model.feature.tokenreplace.FeatureIdTokenReplacer;
import org.kalypsodeegree_impl.model.feature.tokenreplace.ListPropertyTokenReplacer;
import org.kalypsodeegree_impl.model.feature.tokenreplace.PropertyTokenReplacer;
import org.kalypsodeegree_impl.model.feature.visitors.CollectorVisitor;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

/**
 * @author doemming
 */
public final class FeatureHelper
{
  private FeatureHelper( )
  {
    throw new UnsupportedOperationException( "Helper class, do not instantiate" );
  }

  private static ITokenReplacer TR_FEATUREID = new FeatureIdTokenReplacer();

  private static ITokenReplacer TR_PROPERTYVALUE = new PropertyTokenReplacer();

  private static ITokenReplacer TR_LISTPROPERTYVALUE = new ListPropertyTokenReplacer();

  private static ITokenReplacer TR_ANNOTATION_VALUE = new AnnotationTokenReplacer();

  private static TokenReplacerEngine FEATURE_TOKEN_REPLACE = new TokenReplacerEngine( new ITokenReplacer[] { FeatureHelper.TR_FEATUREID, FeatureHelper.TR_PROPERTYVALUE,
      FeatureHelper.TR_LISTPROPERTYVALUE, TR_ANNOTATION_VALUE } );

  /**
   * @deprecated Do not use strings as property names. Use {@link IFeatureType#getProperty(QName)} instead.
   */
  @Deprecated
  public static IPropertyType getPT( final Feature feature, final String propName )
  {
    final IPropertyType[] properties = feature.getFeatureType().getProperties();

    for( final IPropertyType type : properties )
    {
      if( propName.equals( type.getQName().getLocalPart() ) )
        return type;
    }

    return null;
  }

  /**
   * @deprecated use booleanIsTrue( Feature feature, QName propQName, boolean defaultStatus )
   */
  @Deprecated
  public static boolean booleanIsTrue( final Feature feature, final String propName, final boolean defaultStatus )
  {
    final Object property = feature.getProperty( propName );
    if( property != null && property instanceof Boolean )
      return ((Boolean)property).booleanValue();
    return defaultStatus;
  }

  public static boolean booleanIsTrue( final Feature feature, final QName propQName, final boolean defaultStatus )
  {
    final Object property = feature.getProperty( propQName );
    if( property != null && property instanceof Boolean )
      return ((Boolean)property).booleanValue();
    return defaultStatus;
  }

  /**
   * @deprecated Do not use: code that uses this stuff is probably not correct: it is allways defined, which type a
   *             property has, so trying to parse this or that is forbidden!
   */
  @Deprecated
  public static double getAsDouble( final Feature feature, final QName propQName, final double defaultValue )
  {
    final Object value = feature.getProperty( propQName );
    if( value == null )
      return defaultValue;
    if( value instanceof String )
      return Double.valueOf( (String)value ).doubleValue();
    // should be a Double
    if( value instanceof BigDecimal )
      return ((BigDecimal)value).doubleValue();
    return ((Double)value).doubleValue();
  }

  /**
   * @deprecated use instead of propName the QName of the property
   */
  @Deprecated
  public static double getAsDouble( final Feature feature, final String propName, final double defaultValue )
  {
    final Object value = feature.getProperty( propName );
    if( value == null )
      return defaultValue;
    if( value instanceof String )
      return Double.valueOf( (String)value ).doubleValue();
    // should be a Double
    return ((Double)value).doubleValue();
  }

  public static String getAsString( final Feature feature, final String propName )
  {
    final Object value = feature.getProperty( propName );
    // TODO use numberformat
    if( value == null )
      return null;
    if( value instanceof String )
      return (String)value;
    return value.toString();
  }

  /**
   * �bertr�gt die Daten eines Features in die Daten eines anderen.
   * <p>
   * Die Properties werden dabei anhand der �bergebenen {@link Properties} zugeordnet. Es gilt: TODO: die Doku ist quatsch, relation properties werden im Moment gar nicht kopiert!
   * <ul>
   * <li>Es erfolgt ein Deep-Copy, inneliegende Features werden komplett kopiert.</li>
   * <li><Bei Referenzen auf andere Features erfolgt nur ein shallow copy, das Referenzierte Feature bleibt gleich./li>
   * <li>Die Typen der Zurodnung m�ssen passen, sonst gibts ne Exception.</li>
   * </ul>
   * 
   * @throws CloneNotSupportedException
   * @throws IllegalArgumentException
   *           Falls eine Zuordnung zwischen Properties unterschiedlkicher Typen erfolgt.
   * @throws NullPointerException
   *           falls eines der Argumente <codce>null</code> ist.
   * @throws UnsupportedOperationException
   *           Noch sind nicht alle Typen implementiert
   */
  public static void copyProperties( final Feature sourceFeature, final Feature targetFeature, final Properties propertyMap ) throws Exception
  {
    for( final Entry< ? , ? > entry : propertyMap.entrySet() )
    {
      final String sourceProp = (String)entry.getKey();
      final String targetProp = (String)entry.getValue();
      copyProperty( sourceFeature, targetFeature, sourceProp, targetProp );
    }
  }

  private static void copyProperty( final Feature sourceFeature, final Feature targetFeature, final String sourceProp, final String targetProp ) throws Exception
  {
    final IPropertyType sourcePT = FeatureHelper.getPT( sourceFeature, sourceProp );
    if( sourcePT == null )
      throw new IllegalArgumentException( "Quell-Property existiert nicht: " + sourceProp );

    final IPropertyType targetPT = FeatureHelper.getPT( targetFeature, targetProp );
    if( targetPT == null )
    {
      // Just this one: can happen, because we are now able to create features of different types at the same time.
      return;
    }

    try
    {
      final Object convertedValue = convertProperty( sourceFeature, targetFeature, sourcePT, targetPT );

      // Hack: Types are same, but ordinality (i.e. list or not) can be different.
      if( !sourcePT.isList() && targetPT.isList() )
        targetFeature.setProperty( targetPT, Arrays.asList( convertedValue ) );
      else if( sourcePT.isList() && !targetPT.isList() )
      {
        final List< ? > newlist = (List< ? >)convertedValue;
        if( newlist.isEmpty() )
          targetFeature.setProperty( targetPT, null );
        else
          targetFeature.setProperty( targetPT, newlist.get( 0 ) );
      }
      else
        targetFeature.setProperty( targetPT, convertedValue );
    }
    catch( final Exception e )
    {
      throw new IllegalArgumentException( "Typen der zugeordneten Properties sind unterschiedlich: '" + sourceProp + "' and '" + targetProp + "'", e );
    }
  }

  private static Object convertProperty( final Feature sourceFeature, final Feature targetFeature, final IPropertyType sourcePT, final IPropertyType targetPT ) throws Exception
  {
    final Object sourceValue = sourceFeature.getProperty( sourcePT );

    if( sourcePT instanceof IValuePropertyType && targetPT instanceof IValuePropertyType )
    {
      /* Shortcut: clone data if the property types are equal */
      final IValuePropertyType sourceFTP = (IValuePropertyType)sourcePT;
      final IValuePropertyType targetFTP = (IValuePropertyType)targetPT;
      if( sourceFTP.getValueQName().equals( targetFTP.getValueQName() ) )
        return cloneData( sourceFeature, targetFeature, sourcePT, sourceValue );
      else if( sourceFTP.isGeometry() && targetFTP.isGeometry() )
      {
        final GM_Object targetGeom = tryConvertGeometry( (GM_Object)sourceValue, targetFTP.getValueQName() );
        if( targetGeom != null )
          return targetGeom;
      }
    }

    final String objectAsString = convertPropertyToString( sourceValue, sourcePT );
    if( objectAsString == null )
      return null;

    if( targetPT instanceof IValuePropertyType )
    {
      final IMarshallingTypeHandler targetTypeHandler = ((IValuePropertyType)targetPT).getTypeHandler();
      return targetTypeHandler.parseType( objectAsString );
    }
    else if( targetPT instanceof IRelationType )
    {
      if( objectAsString.contains( "#" ) ) //$NON-NLS-1$
      {
        final IRelationType targetRT = (IRelationType)targetPT;
        final IFeatureType targetFeatureType = targetRT.getTargetFeatureType();
        return new XLinkedFeature_Impl( targetFeature, targetRT, targetFeatureType, objectAsString );
      }

      // We assume, its internal link only. What to do, if the target references an external link?
      return objectAsString;
    }

    throw new UnsupportedOperationException( String.format( "Unable to handle targetProperty '%s'", targetPT.getQName() ) );
  }

  private static GM_Object tryConvertGeometry( final GM_Object sourceGeom, final QName targetQName )
  {
    if( sourceGeom instanceof GM_MultiSurface )
    {
      final GM_MultiSurface multiSurface = (GM_MultiSurface)sourceGeom;
      if( GM_Polygon.POLYGON_ELEMENT.equals( targetQName ) || GM_Polygon.SURFACE_ELEMENT.equals( targetQName ) )
      {
        if( multiSurface.getSize() > 0 )
          return multiSurface.getObjectAt( 0 );
      }
    }

    return null;
  }

  private static String convertPropertyToString( final Object sourceValue, final IPropertyType sourcePT )
  {
    if( sourceValue == null )
      return null;

    if( sourcePT instanceof IValuePropertyType )
    {
      final IValuePropertyType sourceVPT = (IValuePropertyType)sourcePT;
      final IMarshallingTypeHandler sourceTypeHandler = sourceVPT.getTypeHandler();
      if( sourceTypeHandler instanceof ISimpleMarshallingTypeHandler )
        return ((ISimpleMarshallingTypeHandler)sourceTypeHandler).convertToXMLString( sourceValue );
    }
    else if( sourcePT instanceof IRelationType )
    {
      if( sourceValue instanceof String )
        return (String)sourceValue;
      else if( sourceValue instanceof IXLinkedFeature )
        return ((IXLinkedFeature)sourceValue).getHref();
      else if( sourceValue instanceof Feature )
        return ((Feature)sourceValue).getId();
    }

    return sourceValue.toString();
  }

  /**
   * Clones a feature and puts it into the given parent feature at the given property.
   * 
   * @param newParentFeature
   *          The parent where the cloned feature will be put into. May live in the same or in another workspace.
   * @param relation
   *          Property where to put the new feature. If a list, the new feature is added at the end of the list.
   * @param nullValuedProperties
   *          qname of IPropertiesTypes whos values will not be copied. only the featuretype will exist and the property
   *          of its is null in result feature
   */
  public static Feature cloneFeature( final Feature newParentFeature, final IRelationType relation, final Feature featureToClone, final QName[] nullValuedProperties ) throws Exception
  {
    final IFeatureType featureType = featureToClone.getFeatureType();

    final Feature newFeature = newParentFeature.getWorkspace().createFeature( newParentFeature, relation, featureType );
    // TODO: this is no good to add it here....
    // For example we cannot give a position where to add the new feature...
    if( relation.isList() )
    {
      /*
       * Get relation in the type system from the cloned feature, because the 'old'-relation may not work with the new
       * feature type (due to the fact that some implementation did not implement a fitting equals method).
       */
      final IRelationType newRelation = (IRelationType)newParentFeature.getFeatureType().getProperty( relation.getQName() );
      newParentFeature.getWorkspace().addFeatureAsComposition( newParentFeature, newRelation, -1, newFeature );
    }
    else
    {
      newParentFeature.setProperty( relation, newFeature );
    }

    copyProperties( featureToClone, newFeature, nullValuedProperties );

    return newFeature;
  }

  public static void copyProperties( final Feature source, final Feature target, final QName[] nullValuedProperties ) throws Exception
  {
    final IFeatureType featureType = source.getFeatureType();

    final IPropertyType[] properties = featureType.getProperties();
    for( final IPropertyType pt : properties )
    {
      if( nullValuedProperties != null && ArrayUtils.contains( nullValuedProperties, pt.getQName() ) )
        continue;

      try
      {
        final Object newValue = FeatureHelper.cloneProperty( source, target, pt );
        target.setProperty( pt, newValue );
      }
      catch( final CloneNotSupportedException e )
      {
        /* Just log, try to copy at least the rest */
        KalypsoDeegreePlugin.getDefault().getLog().log( StatusUtilities.statusFromThrowable( e ) );
      }
    }
  }

  /**
   * Clones a feature and puts it into the given parent feature at the given property.
   */
  public static Feature cloneFeature( final Feature newParentFeature, final IRelationType relation, final Feature featureToClone ) throws Exception
  {
    return FeatureHelper.cloneFeature( newParentFeature, relation, featureToClone, new QName[0] );
  }

  private static Object cloneProperty( final Feature sourceFeature, final Feature targetFeature, final IPropertyType pt ) throws Exception
  {
    final Object property = sourceFeature.getProperty( pt );
    if( pt.isList() )
    {
      final List< ? > list = (List< ? >)property;
      final List targetList = (List)targetFeature.getProperty( pt );

      for( final Object listElement : list )
      {
        final Object cloneData = FeatureHelper.cloneData( sourceFeature, targetFeature, pt, listElement );
        // TODO: this is not nice! Better: d not add feature to list within the cloneFeature Method
        if( cloneData instanceof IXLinkedFeature || !(cloneData instanceof Feature) )
        {
          targetList.add( cloneData );
        }
      }

      return targetList;
    }

    return FeatureHelper.cloneData( sourceFeature, targetFeature, pt, property );
  }

  /**
   * @throws CloneNotSupportedException
   * @throws UnsupportedOperationException
   *           If type of object is not supported for clone <br/>
   *           FIXME: get gml version from source feature type
   */
  public static Object cloneData( final Feature sourceFeature, final Feature targetFeature, final IPropertyType pt, final Object object ) throws Exception
  {
    if( object == null )
      return null;

    if( pt instanceof IRelationType )
    {
      final IRelationType rt = (IRelationType)pt;

      if( object instanceof String )
      {
        // its an internal link: change to external if we change the workspace
        if( sourceFeature.getWorkspace().equals( targetFeature.getWorkspace() ) )
          return object;
        else
          // TODO: not yet supported; internal links will be broken after clone
          return null;
      }
      else if( object instanceof IXLinkedFeature )
      {
        final IXLinkedFeature xlink = (IXLinkedFeature)object;
        // retarget xlink
        return new XLinkedFeature_Impl( targetFeature, rt, xlink.getFeatureType(), xlink.getHref() );
      }
      else if( object instanceof Feature )
        return FeatureHelper.cloneFeature( targetFeature, rt, (Feature)object );

      return null;
    }

    // its an geometry? -> clone geometry
    if( object instanceof GM_Object )
    {
      final GM_Object gmo = (GM_Object)object;

      return gmo.clone();
    }

    // if we have an IMarshallingTypeHandler, it will do the clone for us.
    final ITypeRegistry<IMarshallingTypeHandler> typeRegistry = MarshallingTypeRegistrySingleton.getTypeRegistry();
    final IMarshallingTypeHandler typeHandler = typeRegistry.getTypeHandlerFor( pt );

    if( typeHandler != null )
    {
      try
      {
        final String gmlVersion = sourceFeature.getFeatureType().getGMLSchema().getGMLVersion();
        return typeHandler.cloneObject( object, gmlVersion );
      }
      catch( final Exception e )
      {
        final CloneNotSupportedException cnse = new CloneNotSupportedException( "Kann Datenobjekt vom Typ '" + pt.getQName() + "' nicht kopieren." );
        cnse.initCause( e );
        throw cnse;
      }
    }
    throw new CloneNotSupportedException( "Kann Datenobjekt vom Typ '" + pt.getQName() + "' nicht kopieren." );
  }

  /**
   * Copies all properties from one feature to another by cloning the data. The features must be of same feature type.
   */
  public static void copyData( final Feature source, final Feature target ) throws Exception
  {
    final IFeatureType type = source.getFeatureType();

    Assert.isTrue( type.equals( target.getFeatureType() ) );

    final IPropertyType[] properties = type.getProperties();
    for( final IPropertyType pt : properties )
    {
      final Object value = source.getProperty( pt );
      final Object clonedValue = FeatureHelper.cloneData( source, target, pt, value );
      target.setProperty( pt, clonedValue );
    }
  }

  public static boolean isCompositionLink( final Feature srcFE, final IRelationType linkProp, final Feature destFE )
  {
    final Object property = srcFE.getProperty( linkProp );
    if( property == null )
      return false;
    if( linkProp.isList() )
    {
      // list:
      final List< ? > list = (List< ? >)property;
      return list.contains( destFE );
    }
    // no list:
    return property == destFE;
  }

  /**
   * Checks if one of the feature properties is a collection.
   * 
   * @param f
   * @return It returns true after the first occurrenc of a list
   */
  public static boolean hasCollections( final Feature f )
  {
    final IFeatureType featureType = f.getFeatureType();
    final IPropertyType[] properties = featureType.getProperties();
    for( final IPropertyType property : properties )
      if( property.isList() )
        return true;
    return false;

  }

  public static int[] getPositionOfAllAssociations( final Feature feature )
  {
    final ArrayList<Integer> res = new ArrayList<>();
    final IFeatureType featureType = feature.getFeatureType();
    final IPropertyType[] properties = featureType.getProperties();
    for( int i = 0; i < properties.length; i++ )
    {
      final IPropertyType property = properties[i];
      if( property instanceof IRelationType )
      {
        res.add( new Integer( i ) );
      }
    }
    final Integer[] positions = res.toArray( new Integer[res.size()] );
    return ArrayUtils.toPrimitive( positions );
  }

  public static IRelationType[] getAllAssociations( final Feature feature )
  {
    final ArrayList<IRelationType> res = new ArrayList<>();
    final IFeatureType featureType = feature.getFeatureType();
    final IPropertyType[] properties = featureType.getProperties();
    for( final IPropertyType property : properties )
      if( property instanceof IRelationType )
      {
        res.add( (IRelationType)property );
      }
    return res.toArray( new IRelationType[res.size()] );
  }

  public static boolean isCollection( final Feature f )
  {

    final IFeatureType featureType = f.getFeatureType();
    final IPropertyType[] properties = featureType.getProperties();

    if( properties.length > 1 )
      return false;

    for( final IPropertyType property : properties )
      if( property.isList() )
        return true;
    return false;
  }

  public static IFeatureType[] getFeatureTypeFromCollection( final Feature f )
  {
    final IFeatureType featureType = f.getFeatureType();
    final IPropertyType[] properties = featureType.getProperties();
    final IPropertyType property = featureType.getProperty( properties[0].getQName() );

    IFeatureType[] afT = null;
    if( property instanceof IRelationType )
    {

      final IFeatureType ft = ((IRelationType)property).getTargetFeatureType();
      afT = GMLSchemaUtilities.getSubstituts( ft, null, false, true );
    }
    return afT;
  }

  /**
   * Create properties by using the property-value of the given feature for each of the replace-tokens
   * 
   * @param tokens
   *          replace-tokens (tokenKey-featurePropertyName;...)
   */
  public static Properties createReplaceTokens( final Feature f, final String tokens )
  {
    final Properties properties = new Properties();
    final String[] strings = tokens.split( ";" );
    for( final String element : strings )
    {
      final String[] splits = element.split( "-" );

      String value = (String)f.getProperty( splits[1] );
      if( value == null )
      {
        value = splits[1];
      }

      properties.setProperty( splits[0], value );
    }

    return properties;
  }

  /**
   * Returns the property of a feature.<br>
   * If the given qname contains no namespace (), it returns the first property with the same localPart.
   */
  public static Object getPropertyLax( final Feature feature, final QName property )
  {
    final IPropertyType pt = feature.getFeatureType().getProperty( property );
    if( pt != null )
      return feature.getProperty( pt );

    if( XMLConstants.NULL_NS_URI.equals( property.getNamespaceURI() ) )
      return feature.getProperty( property.getLocalPart() );

    // Will throw an IllegalArgumentException as the property will not be found
    return feature.getProperty( property );
  }

  /**
   * Returns the value of the given property. If the property is a java.util.List, it then returns the first element of
   * the list or null if the list is empty.
   */
  public static Object getFirstProperty( final Feature feature, final QName property )
  {
    final Object prop = feature.getProperty( property );

    if( prop == null )
      return null;

    if( prop instanceof List )
    {
      final List< ? > list = (List< ? >)prop;

      if( list.size() > 0 )
        return list.get( 0 );
      else
        return null;
    }

    return prop;
  }

  /**
   * copys all simple type properties from the source feature into the target feature
   * 
   * @param srcFE
   * @param targetFE
   * @throws MultiException
   */
  public static void copySimpleProperties( final Feature srcFE, final Feature targetFE ) throws MultiException
  {
    final MultiException multiException = new MultiException();
    final IPropertyType[] srcFTPs = srcFE.getFeatureType().getProperties();
    for( final IPropertyType element : srcFTPs )
    {
      try
      {
        FeatureHelper.copySimpleProperty( srcFE, targetFE, element );
      }
      catch( final Exception e )
      {
        multiException.addException( e );
      }
    }
    if( !multiException.isEmpty() )
      throw multiException;
  }

  /**
   * @param srcFE
   * @param targetFE
   * @param property
   * @throws CloneNotSupportedException
   */
  public static void copySimpleProperty( final Feature srcFE, final Feature targetFE, final IPropertyType property ) throws Exception
  {
    if( property instanceof IValuePropertyType )
    {
      final IValuePropertyType pt = (IValuePropertyType)property;
      final Object valueOriginal = srcFE.getProperty( property );
      final Object cloneValue = FeatureHelper.cloneData( srcFE, targetFE, pt, valueOriginal );
      targetFE.setProperty( pt, cloneValue );
    }
  }

  /**
   * <ul>
   * <li>If the property is not a list, just set the value</li>
   * <li>If the property ist a list, a the given value to the list. If the given value is a list, add all its values to the list.</li>
   * </ul>
   * 
   * @see org.kalypsodeegree.model.feature.Feature#addProperty(org.kalypsodeegree.model.feature.FeatureProperty)
   */
  public static void addProperty( final Feature feature, final IPropertyType pt, final Object newValue )
  {
    final Object oldValue = feature.getProperty( pt );

    if( oldValue instanceof List )
    {
      if( newValue instanceof List )
      {
        ((List)oldValue).addAll( (List)newValue );
      }
      else
      {
        ((List)oldValue).add( newValue );
      }
    }
    else
    {
      feature.setProperty( pt, newValue );
    }
  }

  /**
   * Same as {@link #addFeature(Feature, QName, QName, 0)}
   */
  public static Feature addFeature( final Feature feature, final QName listProperty, final QName newFeatureName ) throws GMLSchemaException
  {
    return addFeature( feature, listProperty, newFeatureName, 0 );
  }

  /**
   * Adds a new member to a property of the given feature. The property must be a feature list.
   * 
   * @param newFeatureName
   *          The QName of the featureType of the newly generated feature. If null, the target feature-type of the list
   *          is taken.
   * @return The new feature member
   */
  public static Feature addFeature( final Feature feature, final QName listProperty, final QName newFeatureName, final int depth ) throws GMLSchemaException
  {
    final FeatureList list = (FeatureList)feature.getProperty( listProperty );
    final Feature parentFeature = list.getOwner();
    final GMLWorkspace workspace = parentFeature.getWorkspace();

    final IRelationType parentFeatureTypeProperty = list.getPropertyType();
    final IFeatureType targetFeatureType = parentFeatureTypeProperty.getTargetFeatureType();

    final IFeatureType newFeatureType;
    if( newFeatureName == null )
    {
      newFeatureType = targetFeatureType;
    }
    else
    {
      newFeatureType = GMLSchemaUtilities.getFeatureTypeQuiet( newFeatureName );
    }

    if( newFeatureName != null && !GMLSchemaUtilities.substitutes( newFeatureType, targetFeatureType.getQName() ) )
      throw new GMLSchemaException( "Type of new feature (" + newFeatureName + ") does not substitutes target feature type of the list: " + targetFeatureType.getQName() );

    final Feature newFeature = workspace.createFeature( parentFeature, parentFeatureTypeProperty, newFeatureType, depth );

    list.add( newFeature );
    return newFeature;
  }

  /**
   * Only works for non list feature property
   * 
   * @param feature
   *          feature which list property receive the new feature
   * @param listProperty
   *          the {@link QName} of the list property
   * @param featureProperties
   *          the property of the feature to be set before adding the feature to the list
   * @param featurePropQNames
   *          the {@link QName} of the feature property to be set before adding it the the
   * @param throws {@link IllegalArgumentException} if featureProperties and featurePropQNames are not all null or are
   *        not all non null with differen lengths
   */
  public static Feature addFeature( final Feature feature, final QName listProperty, final QName newFeatureName, final Object[] featureProperties, final QName[] featurePropQNames ) throws GMLSchemaException, IllegalArgumentException
  {
    if( featureProperties == null & featurePropQNames == null )
    {
      // okay
    }
    else if( featureProperties != null && featurePropQNames != null )
    {
      if( featureProperties.length != featurePropQNames.length )
        throw new IllegalArgumentException( "featurePropQName and featureProperties must have the same length" );

    }
    else
      throw new IllegalArgumentException( "featureProperties and FeaturePropQnames must be all null or all non null with" + "the same length" );
    final FeatureList list = (FeatureList)feature.getProperty( listProperty );
    // TODO Patrice to check can the feature(param) be different from the list property parent
    final Feature parentFeature = list.getOwner();
    final GMLWorkspace workspace = parentFeature.getWorkspace();

    final IRelationType parentFeatureTypeProperty = list.getPropertyType();
    final IFeatureType targetFeatureType = parentFeatureTypeProperty.getTargetFeatureType();

    final IFeatureType newFeatureType;
    if( newFeatureName == null )
    {
      newFeatureType = targetFeatureType;
    }
    else
    {
      newFeatureType = GMLSchemaUtilities.getFeatureTypeQuiet( newFeatureName );
    }

    if( newFeatureName != null && !GMLSchemaUtilities.substitutes( newFeatureType, targetFeatureType.getQName() ) )
      throw new GMLSchemaException( "Type of new feature (" + newFeatureName + ") does not substitutes target feature type of the list: " + targetFeatureType.getQName() );

    final Feature newFeature = workspace.createFeature( parentFeature, parentFeatureTypeProperty, newFeatureType );
    for( int i = featureProperties.length - 1; i >= 0; i-- )
    {
      newFeature.setProperty( featurePropQNames[i], featureProperties[i] );
    }

    list.add( newFeature );

    return newFeature;
  }

  public static void setProperties( final Feature result, final Map<IPropertyType, Object> props )
  {
    for( final Map.Entry<IPropertyType, Object> entry : props.entrySet() )
    {
      result.setProperty( entry.getKey(), entry.getValue() );
    }
  }

  // FIXME: check, if this can be replaced with Feature#getMember
  public static Feature resolveLink( final Feature feature, final QName qname )
  {
    return FeatureHelper.resolveLink( feature, qname, false );
  }

  /**
   * Returns a value of the given feature as feature. If it is a link, it will be resolved.
   * 
   * @param qname
   *          Must denote a property of type IRelationType of maxoccurs 1.
   * @param followXLinks
   *          If true and the property is an xlinked Feature, return the Feature where the xlink points to. Else the
   *          xlink itself is returned as feature.
   */
  // FIXME: check, if this can be replaced with Feature#getMember
  public static Feature resolveLink( final Feature feature, final QName qname, final boolean followXLinks )
  {
    final IRelationType property = (IRelationType)feature.getFeatureType().getProperty( qname );
    final Object value = feature.getProperty( property );

    if( value == null )
      return null;

    if( value instanceof IXLinkedFeature && followXLinks )
      return ((IXLinkedFeature)value).getFeature();

    if( value instanceof Feature )
      return (Feature)value;

    // FIXME: code never reached?!
    if( feature instanceof IXLinkedFeature )
    {
      /* Its a local link inside a xlinked-feature */
      final IXLinkedFeature xlinkedFeature = (IXLinkedFeature)feature;
      final String href = xlinkedFeature.getUri() + "#" + value; //$NON-NLS-1$
      return new XLinkedFeature_Impl( feature, property, property.getTargetFeatureType(), href );
    }

    /* A normal local link inside the same workspace */
    return feature.getWorkspace().getFeature( (String)value );
  }

  /**
   * Resolves and adapts the linked feature. Note that the real feature is wrapped and return not the xlinked feature.
   * 
   * @param feature
   *          the link property holder
   * @param propertyQName
   *          the q-name of the link property
   * @param adapterTargetClass
   *          the class the link feature is to be adapted to
   * @throws IllegalArgumentException
   *           if any of the parameter is null
   * @throws IllegalStateException
   *           if xlink is broken (i.e. xlinked feature points to non existing real feature)
   * @return an adapter if the link feature or null if no linked feature is found or if the linked feature is not
   *         adaptable to the specified class
   */
  public static <T> T resolveLink( final Feature feature, final QName propertyQName, final Class<T> adapterTargetClass )
  {
    if( feature == null || propertyQName == null || adapterTargetClass == null )
    {
      final String message = String.format( "Arguments must not be null : \n\tfeature = %s " + "\n\tpropertyQName = %s \n\tadapterTargetClass = %s\n\t", feature, propertyQName, adapterTargetClass );
      throw new IllegalArgumentException( message );
    }
    Feature propFeature = FeatureHelper.resolveLink( feature, propertyQName );
    if( propFeature == null )
      return null;
    else
    {
      if( propFeature instanceof IXLinkedFeature )
      {
        // here is also possible to get IllegalArgumentException, if (phantom) xlinked feature points to nothing
        propFeature = ((IXLinkedFeature)propFeature).getFeature();
      }
      final T adaptedFeature = (T)propFeature.getAdapter( adapterTargetClass );
      return adaptedFeature;
    }
  }

  public static void addChild( final Feature parentFE, final IRelationType rt, final String featureID )
  {
    if( rt.isList() )
    {
      final FeatureList list = (FeatureList)parentFE.getProperty( rt );
      list.add( featureID );
    }
    else
    {
      parentFE.setProperty( rt, featureID );
    }
  }

  /**
   * Creates a data object suitable for a feature property out of string.
   * 
   * @return null, if the data-type is unknown
   * @throws NumberFormatException
   */
  public static final Object createFeaturePropertyFromStrings( final IValuePropertyType type, final String format, final String[] input, final boolean handleEmptyAsNull )
  {
    if( GeometryUtilities.getPointClass() == type.getValueClass() || GM_Object.class == type.getValueClass() )
    {
      final String rwString = input[0].trim();
      final String hwString = input[1].trim();

      final String crsString;
      if( input.length > 2 )
      {
        crsString = input[2];
      }
      else
      {
        crsString = format;
      }

      if( rwString == null || rwString.isEmpty() || hwString == null || hwString.isEmpty() )
        return null; // GeometryFactory.createGM_Point( 0, 0, crsString );

      final double rw = NumberUtils.parseDouble( rwString );
      final double hw = NumberUtils.parseDouble( hwString );

      return GeometryFactory.createGM_Point( rw, hw, crsString );
    }

    final IMarshallingTypeHandler typeHandler = MarshallingTypeRegistrySingleton.getTypeRegistry().getTypeHandlerFor( type );
    if( typeHandler == null )
      return null;

    try
    {
      final String inputString = input[0];
      if( handleEmptyAsNull && (inputString == null || inputString.trim().length() == 0) )
        return null;

      return typeHandler.parseType( inputString );
    }
    catch( final ParseException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Retrieves a property as a feature.
   * <p>
   * If the property is not yet set, the feature is generated and set.
   * </p>
   * <p>
   * This method creates directly a feature of the target feature type of the given property.
   * </p>
   * 
   * @throws IllegalArgumentException
   *           If the target feature type of the given property is abstract.
   */
  // FIXME: check, if this can be replaced with Feature#getMember
  public static Feature getSubFeature( final Feature parent, final QName propertyName )
  {
    final Object value = parent.getProperty( propertyName );
    if( value instanceof Feature )
      return (Feature)value;

    if( value instanceof String )
      return parent.getWorkspace().getFeature( (String)value );

    final IFeatureType parentType = parent.getFeatureType();
    final IPropertyType property = parentType.getProperty( propertyName );
    if( !(property instanceof IRelationType) )
      throw new IllegalArgumentException( "Property is no relation: " + propertyName );

    final IRelationType rt = (IRelationType)property;
    final IFeatureType targetFeatureType = rt.getTargetFeatureType();

    if( targetFeatureType.isAbstract() )
      throw new IllegalArgumentException( "Cannot instantiate an abstract feature" );

    // neues machen
    final GMLWorkspace workspace = parent.getWorkspace();
    final Feature newSubFeature = workspace.createFeature( parent, rt, targetFeatureType );
    parent.setProperty( propertyName, newSubFeature );
    return newSubFeature;
  }

  /**
   * @see #addProperty(Feature, IPropertyType, Object)
   */
  public static void addProperty( final Feature feature, final QName propertyName, final Object value )
  {
    final IPropertyType property = feature.getFeatureType().getProperty( propertyName );
    FeatureHelper.addProperty( feature, property, value );
  }

  /**
   * If the given object is a feature, return it, else return the feature with id (String)linkOrFeature.
   */
  public static Feature getFeature( final GMLWorkspace wrk, final Object linkOrFeature )
  {
    if( linkOrFeature instanceof Feature )
      return (Feature)linkOrFeature;

    if( linkOrFeature instanceof String )
      return wrk.getFeature( (String)linkOrFeature );

    return null;
  }

  public static <T> T getFeature( final GMLWorkspace workspace, final Object linkOrFeature, final Class<T> targetAdapterClass )
  {
    if( workspace == null || linkOrFeature == null || targetAdapterClass == null )
    {
      final String message = null;
      throw new IllegalArgumentException( message );
    }

    final Feature feature = FeatureHelper.getFeature( workspace, linkOrFeature );
    if( feature == null )
      return null;

    final Object adapter = feature.getAdapter( targetAdapterClass );
    return targetAdapterClass.cast( adapter );
  }

  /**
   * Returns the label of a feature.
   * <p>
   * The label is taken from the annotation on the feature type
   * </p>
   * <p>
   * Additionally, token replace will be performed on the annotation string.
   * </p>
   * <p>
   * The following tokens are supported:
   * <ul>
   * <li>${id}: the gml:id of the feature</li>
   * <li>${property:_qname_}: the value of the property _qname_ parsed as string (via its marshalling handler). _qname_
   * <li>${listProperty:_qname_;listindex}: Similar to ${property}, but the value is interpretated as List, and then the list item with index listindex is returned. Syntax: namespace#localPart</li>
   * </ul>
   * </p>
   */
  public static String getAnnotationValue( final Feature feature, final String annotationKey )
  {
    if( feature instanceof IXLinkedFeature )
    {
      // BUGFIX: access the feature here once, before the annotation is fetched.
      // This is necessary in order to force the featureType to be known.
      ((IXLinkedFeature)feature).getFeature();
    }

    final IFeatureType featureType = feature.getFeatureType();
    final IAnnotation annotation = featureType.getAnnotation();

    final String value = annotation.getValue( annotationKey );
    return FeatureHelper.tokenReplace( feature, value );
  }

  public static boolean hasReplaceTokens( final IFeatureType featureType, final String annotationKey )
  {
    final IAnnotation annotation = featureType.getAnnotation();
    final String label = annotation.getValue( annotationKey );
    return label.contains( TokenReplacerEngine.TOKEN_START );
  }

  /** Performs the token replace for the methods {@link #getLabel(Feature)}, ... */
  public static String tokenReplace( final Feature feature, final String tokenString )
  {
    return FeatureHelper.FEATURE_TOKEN_REPLACE.replaceTokens( feature, tokenString );
  }

  /**
   * This function creates separate lists of features by qname and collects them in a hash map.
   * 
   * @param parent
   *          The parent feature, containing the original feature list.
   * @param propertyQName
   *          The qname of the original feature list, which contains all features.
   */
  public static HashMap<QName, ArrayList<Feature>> sortType( final Feature parent, final QName propertyQName )
  {
    /* Get a list of all features in the given property. */
    final FeatureList list = (FeatureList)parent.getProperty( propertyQName );

    /* Create a map QName->Features. */
    final HashMap<QName, ArrayList<Feature>> featureMap = new HashMap<>();

    for( final Object o : list )
    {
      /* Get the feature. */
      final Feature feature = (Feature)o;

      /* Get the qname of the feature. */
      final QName qname = feature.getFeatureType().getQName();

      /* Wenn der QName bereits in der Liste existiert, h�nge das Feature an dessen Liste. */
      if( featureMap.containsKey( qname ) )
      {
        final ArrayList<Feature> sub_list = featureMap.get( qname );
        sub_list.add( feature );
        featureMap.put( qname, sub_list );
      }
      else
      {
        /* Add the qname as a new key, with a new List. */
        final ArrayList<Feature> sub_list = new ArrayList<>();
        sub_list.add( feature );
        featureMap.put( qname, sub_list );
      }
    }

    return featureMap;
  }

  /**
   * Sets a link to a feature (<code>targetFeature</code>) inside another feature (<code>sourceFeature</code>) as a
   * property.<br/>
   * If the parent workspaces of the two features are the same, an internal link (#&lt;id&gt;) is created.<br/>
   * Else, an external xlink is created. In this case, the context of the target workspace must be non <code>null</code>
   * 
   * @throws IllegalArgumentException
   *           If the property argument is not suitable for a link (not an {@link IRelationType}). If the
   *           targetWorksapce has not a suitable context for the link.
   */
  // TODO: check: move to Feature? Similar to Feature#setLink
  public static void setAsLink( final Feature sourceFeature, final QName property, final Feature targetFeature )
  {
    Assert.isNotNull( sourceFeature );

    final IPropertyType propertyType = sourceFeature.getFeatureType().getProperty( property );
    if( !(propertyType instanceof IRelationType) )
      throw new IllegalArgumentException( String.format( "Unable to set a link to property %s. It is not a relation.", property ) );

    if( targetFeature == null )
      sourceFeature.setProperty( propertyType, null );

    final GMLWorkspace sourceWorkspace = sourceFeature.getWorkspace();
    final GMLWorkspace targetWorkspace = targetFeature.getWorkspace();

    final IRelationType sourceRelation = (IRelationType)propertyType;
    final String targetID = targetFeature.getId();
    final IFeatureType targetFeatureType = targetFeature.getFeatureType();

    if( sourceWorkspace == targetWorkspace )
      sourceFeature.setProperty( sourceRelation, targetID );
    else
    {
      final URL targetContext = targetWorkspace.getContext();
      if( targetContext == null )
        throw new IllegalArgumentException( String.format( "Unable to set a link to property %s. Workspace of target feature has no context.", property ) );

      final String uri = targetContext.toExternalForm();
      final String href = String.format( "%s#%s", uri, targetID ); //$NON-NLS-1$
      final Feature link = new XLinkedFeature_Impl( sourceFeature, sourceRelation, targetFeatureType, href );
      sourceFeature.setProperty( propertyType, link );
    }
  }

  /**
   * Use {@link Feature#createLink(IRelationType, String)} instead.
   */
  @Deprecated
  public static Object createLinkToID( final String id, final Feature parentFeature, final IRelationType parentRelation, final IFeatureType ft )
  {
    if( id == null )
      return null;

    if( id.startsWith( "#" ) ) //$NON-NLS-1$
      return id;

    return new XLinkedFeature_Impl( parentFeature, parentRelation, ft, id );
  }

  /**
   * @author thuel2
   * @return <code>true</code> if <code>parent</code> is one of the ancestors of or equals <em>ALL</em> <code>children</code>
   */
  public static boolean isParentOfAllOrEquals( final Feature parent, final Feature[] children )
  {
    if( children.length < 1 )
      return false;
    boolean isParentOfAllOrEquals = true;
    for( final Feature child : children )
    {
      isParentOfAllOrEquals = isParentOfAllOrEquals && FeatureHelper.isParentOrEquals( parent, child );
    }
    return isParentOfAllOrEquals;
  }

  /**
   * @author thuel2
   * @return <code>true</code> if <code>parent</code> is one of the ancestors of <code>child</code> or equals <code>child</code>
   */
  public static boolean isParentOrEquals( final Feature parent, final Feature child )
  {
    if( parent == null || child == null )
      return false;
    if( parent.equals( child ) )
      return true;
    else
      return FeatureHelper.isParent( parent, child );
  }

  /**
   * @author thuel2
   * @return <code>true</code> if <code>parent</code> is one of the ancestors of <em>ALL</em> <code>children</code>
   */
  public static boolean isParentOfAll( final Feature parent, final Feature[] children )
  {
    if( children.length < 1 )
      return false;
    boolean isParentOffAll = true;
    for( final Feature child : children )
    {
      isParentOffAll = isParentOffAll && FeatureHelper.isParent( parent, child );
    }
    return isParentOffAll;
  }

  /**
   * @author thuel2
   * @return <code>true</code> if <code>parent</code> is one of the ancestors of <code>child</code> (in relation to <code>workspace</code>)
   */
  public static boolean isParent( final GMLWorkspace workspace, final Object parent, final Object child )
  {
    Feature parentFeat = null;

    if( parent instanceof Feature )
    {
      parentFeat = (Feature)parent;
    }
    else
    {
      parentFeat = workspace.getFeature( (String)parent );
    }

    Feature childFeat = null;
    if( child instanceof Feature )
    {
      childFeat = (Feature)child;
    }
    else
    {
      childFeat = workspace.getFeature( (String)child );
    }

    return FeatureHelper.isParent( parentFeat, childFeat );
  }

  /**
   * @author thuel2
   * @return <code>true</code> if <code>parent</code> is one of the ancestors of <code>child</code>
   */
  public static boolean isParent( final Feature parent, final Feature child )
  {
    if( parent == null || child == null )
      return false;
    else if( child.getParentRelation() != null )
    {
      final Feature childParent = child.getOwner();
      final Feature childRoot = child.getWorkspace().getRootFeature();
      if( parent.equals( childParent ) )
        return true;
      else if( childParent.equals( childRoot ) )
        return false;
      else
        return FeatureHelper.isParent( parent, childParent );
    }
    else
      return false;
  }

  /**
   * Calculates the minimal envelope containing all envelopes of the given features.
   * 
   * @return <code>null</code> if none of the given features contains a valid envelope.
   */
  public static GM_Envelope getEnvelope( final Feature[] features )
  {
    GM_Envelope result = null;

    for( final Feature feature : features )
    {
      final GM_Envelope envelope = feature.getEnvelope();
      if( envelope != null )
      {
        if( result == null )
          result = envelope;
        else
          result = result.getMerged( envelope );
      }
    }

    return result;
  }

  public static Feature createFeatureForListProp( final FeatureList list, final QName newFeatureName, final int index ) throws GMLSchemaException
  {
    final Feature parentFeature = list.getOwner();
    final GMLWorkspace workspace = parentFeature.getWorkspace();

    final IRelationType parentRelation = list.getPropertyType();
    final IFeatureType targetFeatureType = parentRelation.getTargetFeatureType();

    final IFeatureType newFeatureType;
    if( newFeatureName == null )
    {
      newFeatureType = targetFeatureType;
    }
    else
    {
      newFeatureType = GMLSchemaUtilities.getFeatureTypeQuiet( newFeatureName );
    }

    if( newFeatureName != null && !GMLSchemaUtilities.substitutes( newFeatureType, targetFeatureType.getQName() ) )
      throw new GMLSchemaException( "Type of new feature (" + newFeatureName + ") does not substitutes target feature type of the list: " + targetFeatureType.getQName() );

    final Feature newFeature = workspace.createFeature( parentFeature, parentRelation, newFeatureType );
    try
    {
      workspace.addFeatureAsComposition( parentFeature, parentRelation, index, newFeature );
    }
    catch( final Exception e )
    {
      e.printStackTrace();
    }
    return newFeature;
  }

  public static Feature createFeatureWithId( final QName newFeatureQName, final Feature parentFeature, final QName propQName, final String gmlID ) throws IllegalArgumentException
  {
    Assert.isNotNull( parentFeature, "parentFeature" ); //$NON-NLS-1$
    Assert.isNotNull( propQName, "propQName" ); //$NON-NLS-1$
    Assert.isNotNull( newFeatureQName, "newFeatureQName" ); //$NON-NLS-1$
    Assert.isNotNull( gmlID );

    final GMLWorkspace workspace = parentFeature.getWorkspace();
    final IFeatureType featureType = GMLSchemaUtilities.getFeatureTypeQuiet( newFeatureQName );
    final IPropertyType parentPT = parentFeature.getFeatureType().getProperty( propQName );
    if( !(parentPT instanceof IRelationType) )
      throw new IllegalArgumentException( "Property not a IRelationType=" + parentPT + " propQname=" + propQName );

    // TOASK does not include the feature into any workspace

    final Feature created = FeatureFactory.createFeature( parentFeature, (IRelationType)parentPT, gmlID, featureType, true );

    try
    {
      if( parentPT.isList() )
      {
        // workspace.addFeatureAsAggregation(
        // parentFeature,//srcFE,
        // (IRelationType)parentPT,//linkProperty,
        // -1,//pos,
        // gmlID//featureID
        // );

        // FeatureList propList=
        // (FeatureList)parentFeature.getProperty( parentPT );
        // propList.add( created );

        workspace.addFeatureAsComposition( parentFeature, (IRelationType)parentPT, -1, created );
      }
      else
      {
        // TODO test this case
        parentFeature.setProperty( parentPT, created );
      }
    }
    catch( final Exception e )
    {
      throw new RuntimeException( "Could not add to the workspace", e );
    }

    return created;
  }

  /**
   * Converts a feature list into an array, and resolves all links while dooing this.<br>
   * The size of the resulting array may be smaller than the given list, if contained links cannot be resolved.
   */
  public static Feature[] toArray( final FeatureList featureList )
  {
    final GMLWorkspace workspace = featureList.getOwner().getWorkspace();

    final List<Feature> features = new ArrayList<>( featureList.size() );
    for( final Object object : featureList )
    {
      final Feature feature = FeatureHelper.getFeature( workspace, object );
      if( feature != null )
      {
        features.add( feature );
      }
    }

    return features.toArray( new Feature[features.size()] );
  }

  /**
   * Reads a property for every feature of an array of features and puts them into a new array.
   */
  public static <T> T[] getProperties( final Feature[] features, final GMLXPath xPath, final T[] a ) throws GMLXPathException
  {
    final T[] properties = a == null ? a : (T[])Array.newInstance( a.getClass().getComponentType(), features.length );

    for( int i = 0; i < features.length; i++ )
    {
      final Feature feature = features[i];
      if( feature == null )
        continue;

      properties[i] = (T)GMLXPathUtilities.query( xPath, feature );
    }

    return properties;
  }

  /**
   * Resolves linked features. Handles XLinks.
   * 
   * @deprecated Use {@link Feature#getMember(QName)} instead.
   */
  @Deprecated
  public static Feature resolveLinkedFeature( final GMLWorkspace targetWorkspace, final Object property )
  {
    if( property == null )
      return null;

    if( property instanceof IXLinkedFeature )
    {
      try
      {
        final IXLinkedFeature xLnk = (IXLinkedFeature)property;
        return xLnk.getFeature();
      }
      catch( final IllegalStateException e )
      {
        e.printStackTrace();

        return null;
      }
    }

    final Feature result = getFeature( targetWorkspace, property );
    if( result == null )
      return null;
    // throw new IllegalStateException( String.format( "Feature with id %s not found", property.toString() ) );

    return result;
  }

  public static Feature[] getFeaturess( final Object object )
  {
    if( object == null )
      return new Feature[] {};
    if( object instanceof Feature )
      return new Feature[] { (Feature)object };
    else if( object instanceof FeatureList )
      return ((FeatureList)object).toFeatures();
    else
      throw new UnsupportedOperationException( "unexcepted object, can not convert to Feature[]" );
  }

  /**
   * Returns all features of a certain feature type contained in this workspace.<br>
   * Comparison with feature type is exact, substitution is not considered.
   */
  public static Feature[] getFeaturesWithType( final GMLWorkspace workspace, final IFeatureType type )
  {
    final FeatureTypeFilter predicate = new FeatureTypeFilter( type, false );
    return getFeatures( workspace, predicate );
  }

  public static Feature[] getFeaturesWithName( final GMLWorkspace workspace, final QName name )
  {
    final QNameUnique uniqueName = QNameUnique.create( name );

    final FeatureTypeFilter predicate = new FeatureTypeFilter( uniqueName, null, false );

    return getFeatures( workspace, predicate );
  }

  /**
   * Returns all feature with the given local typename.
   */
  public static Feature[] getFeaturesWithLocalName( final GMLWorkspace workspace, final String typenameLocalPart )
  {
    final QNameUnique localTypename = QNameUnique.create( XMLConstants.NULL_NS_URI, typenameLocalPart );

    final FeatureTypeFilter predicate = new FeatureTypeFilter( null, localTypename, false );

    return getFeatures( workspace, predicate );
  }

  /**
   * Returns all feature of a workspace filtered by a given predicate.
   */
  public static Feature[] getFeatures( final GMLWorkspace workspace, final Filter predicate )
  {
    Assert.isNotNull( workspace );

    final CollectorVisitor collector = new CollectorVisitor( predicate );
    try
    {
      final Feature rootFeature = workspace.getRootFeature();
      workspace.accept( collector, rootFeature, FeatureVisitor.DEPTH_INFINITE );
    }
    catch( final Throwable e )
    {
      e.printStackTrace();
    }

    return collector.getResults( true );
  }
}