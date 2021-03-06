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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IAdapterFactory;
import org.kalypso.commons.xml.NS;
import org.kalypso.commons.xml.XmlTypes;
import org.kalypso.core.i18n.Messages;
import org.kalypso.gmlschema.GMLSchemaUtilities;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.relation.IRelationType;
import org.kalypso.gmlschema.property.restriction.IRestriction;
import org.kalypso.gmlschema.swe.RepresentationType;
import org.kalypso.gmlschema.swe.RepresentationType.KIND;
import org.kalypso.gmlschema.types.IMarshallingTypeHandler;
import org.kalypso.gmlschema.types.ITypeRegistry;
import org.kalypso.gmlschema.types.MarshallingTypeRegistrySingleton;
import org.kalypso.observation.IObservation;
import org.kalypso.observation.Observation;
import org.kalypso.observation.phenomenon.IPhenomenon;
import org.kalypso.observation.phenomenon.Phenomenon;
import org.kalypso.observation.phenomenon.PhenomenonUtilities;
import org.kalypso.observation.result.IComponent;
import org.kalypso.observation.result.IRecord;
import org.kalypso.observation.result.IRecordFactory;
import org.kalypso.observation.result.TupleResult;
import org.kalypso.ogc.gml.command.FeatureChange;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.FeatureList;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.feature.IXLinkedFeature;
import org.kalypsodeegree.model.typeHandler.XsdBaseTypeHandler;
import org.kalypsodeegree.model.typeHandler.XsdBaseTypeHandlerString;
import org.kalypsodeegree.model.typeHandler.XsdBaseTypeHandlerXMLGregorianCalendar;
import org.kalypsodeegree_impl.gml.binding.commons.NamedFeatureHelper;
import org.kalypsodeegree_impl.model.feature.FeatureFactory;
import org.kalypsodeegree_impl.model.feature.FeatureHelper;

/**
 * @author schlienger
 */
public class ObservationFeatureFactory implements IAdapterFactory
{
  public static final QName GML_METADATA = new QName( NS.GML3, "metaDataProperty" ); //$NON-NLS-1$

  public static final QName OM_OBSERVATION = IObservation.QNAME_OBSERVATION; //$NON-NLS-1$

  public static final QName OM_OBSERVED_PROP = new QName( NS.OM, "observedProperty" ); //$NON-NLS-1$

  public static final QName OM_RESULT = new QName( NS.OM, "result" ); //$NON-NLS-1$

  public static final QName OM_RESULTDEFINITION = new QName( NS.OM, "resultDefinition" ); //$NON-NLS-1$

  public static final QName SWE_COMPONENT = new QName( NS.SWE, "component" ); //$NON-NLS-1$

  public static final QName SWE_ITEMDEFINITION = new QName( NS.SWE, "ItemDefinition" ); //$NON-NLS-1$

  public static final QName SWE_PROPERTY = new QName( NS.SWE, "property" ); //$NON-NLS-1$

  public static final QName SWE_PHENOMENONTYPE = new QName( NS.SWE, "Phenomenon" ); //$NON-NLS-1$

  public static final QName SWE_RECORDDEFINITIONTYPE = new QName( NS.SWE, "RecordDefinition" ); //$NON-NLS-1$

  public static final QName SWE_REPRESENTATION = new QName( NS.SWE, "representation" ); //$NON-NLS-1$

  private static final QName QNAME_F_SORTED_RECORD_DEFINITION = new QName( NS.SWE_EXTENSIONS, "SortedRecordDefinition" ); //$NON-NLS-1$

  private static final QName QNAME_P_SORTED_COMPONENT = new QName( NS.SWE_EXTENSIONS, "sortedComponent" ); //$NON-NLS-1$

  // FIXME: nonsense!
  private static final QName QNAME_P_ORDINALNUMBER_COMPONENT = new QName( NS.SWE_EXTENSIONS, "ordinalNumberComponent" ); //$NON-NLS-1$

  /**
   * Makes a tuple based observation from a feature. The feature must substitute http://www.opengis.net/om:Observation .
   */
  public static IObservation<TupleResult> toObservation( final Feature f )
  {
    return toObservation( f, null );
  }

  /**
   * Makes a tuple based observation from a feature. The feature must substitute http://www.opengis.net/om:Observation .
   */
  public static IObservation<TupleResult> toObservation( final Feature f, final IRecordFactory recordFactory )
  {
    if( f instanceof IObservationFeature )
      return ((IObservationFeature)f).toObservation();

    return toObservationInternal( f, recordFactory );
  }

  /**
   * Only for implementros of IObservationFeature.
   */
  public static IObservation<TupleResult> toObservationInternal( final Feature f, final IRecordFactory recordFactory )
  {
    final TupleResult tupleResult = new TupleResult( recordFactory );
    final IObservation<TupleResult> observation = new Observation<>( null, null, tupleResult );

    fillObservation( observation, f );

    return observation;
  }

  public static void fillObservation( final IObservation<TupleResult> observation, final Feature f )
  {
    if( f == null )
      return;

    final TupleResult result = observation.getResult();
    fillResult( result, f );

    final IFeatureType featureType = f.getFeatureType();

    if( !GMLSchemaUtilities.substitutes( featureType, ObservationFeatureFactory.OM_OBSERVATION ) )
      throw new IllegalArgumentException( Messages.getString( "org.kalypso.ogc.gml.om.ObservationFeatureFactory.16" ) + f ); //$NON-NLS-1$

    final String name = (String)FeatureHelper.getFirstProperty( f, Feature.QN_NAME );
    final String desc = (String)FeatureHelper.getFirstProperty( f, Feature.QN_DESCRIPTION );

    final Object phenProp = f.getProperty( ObservationFeatureFactory.OM_OBSERVED_PROP );

    final Feature phenFeature = FeatureHelper.getFeature( f.getWorkspace(), phenProp );
    final IPhenomenon phenomenon;
    if( phenFeature != null )
    {
      final String phenId = phenFeature instanceof IXLinkedFeature ? ((IXLinkedFeature)phenFeature).getHref() : phenFeature.getId();
      final String phenName = NamedFeatureHelper.getName( phenFeature );
      final String phenDesc = NamedFeatureHelper.getDescription( phenFeature );
      phenomenon = new Phenomenon( phenId, phenName, phenDesc );
    }
    else
      phenomenon = null;

    observation.setName( name );
    observation.setDescription( desc );
    observation.setPhenomenon( phenomenon );
  }

  /**
   * Helper: builds the tuple result for a tuple based observation according to the record definition encoded as a
   * feature (subtype of ItemDefinition).
   * <p>
   * This method is declared protected, but if the need emanes, it could be made public.
   */
  private static void fillResult( final TupleResult tupleResult, final Feature f )
  {
    /* clear components from result: some TupleResults are created with components (e.g. when created via profile provider in wspm) */
    while( tupleResult.getComponents().length > 0 )
      tupleResult.removeComponent( 0 );

    final Feature recordDefinition = FeatureHelper.resolveLink( f, ObservationFeatureFactory.OM_RESULTDEFINITION );

    final String result = (String)f.getProperty( ObservationFeatureFactory.OM_RESULT );

    final IComponent[] components = ObservationFeatureFactory.buildComponents( recordDefinition );
    final IComponent[] sortComponents = ObservationFeatureFactory.buildSortComponents( recordDefinition );
    final IComponent ordinalNumberComponent = ObservationFeatureFactory.getOrdinalNumberComponent( recordDefinition );
    final XsdBaseTypeHandler< ? >[] typeHandlers = ObservationFeatureFactory.typeHandlersForComponents( components );

    for( final IComponent component : components )
      tupleResult.addComponent( component );

    tupleResult.setSortComponents( sortComponents );
    tupleResult.setOrdinalNumberComponent( ordinalNumberComponent );

    // TODO: move into own method
    // TODO: be more robust against inconsistency between resultDefinition and result
    final StringTokenizer tk = new StringTokenizer( result == null ? "" : result.trim() ); //$NON-NLS-1$
    int nb = 0;
    IRecord record = null;
    while( tk.hasMoreElements() )
    {
      if( nb == 0 )
      {
        record = tupleResult.createRecord();
        tupleResult.add( record );
      }

      final String token = tk.nextToken();
      final XsdBaseTypeHandler< ? > handler = typeHandlers[nb];

      if( handler == null )
        continue;

      final Object value = convertToJavaValue( token, handler );
      record.setValue( nb, value );
      nb++;
      nb = nb % components.length;
    }
  }

  private static Object convertToJavaValue( final String token, final XsdBaseTypeHandler< ? > handler )
  {
    try
    {
      if( "null".equals( token ) ) //$NON-NLS-1$
        return null;

      if( handler instanceof XsdBaseTypeHandlerString )
      {
        final XsdBaseTypeHandlerString myHandler = (XsdBaseTypeHandlerString)handler;
        return myHandler.convertToJavaValue( URLDecoder.decode( token, "UTF-8" ) ); //$NON-NLS-1$
      }

      if( handler instanceof XsdBaseTypeHandlerXMLGregorianCalendar )
      {
        final XsdBaseTypeHandlerXMLGregorianCalendar myHandler = (XsdBaseTypeHandlerXMLGregorianCalendar)handler;
        return myHandler.convertToJavaValue( URLDecoder.decode( token, "UTF-8" ) ); //$NON-NLS-1$
      }

      return handler.convertToJavaValue( token );
    }
    catch( final NumberFormatException e )
    {
      e.printStackTrace();
      // TODO: set null here: Problem: the other components can't handle null now, they should
      return null;
    }
    catch( final UnsupportedEncodingException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Creates the list of components the observation should be sorted by. Returns always null, excpet redordDefinition is
   * of type sweExt:SortedRecordDefinition.
   */
  private static IComponent[] buildSortComponents( final Feature recordDefinition )
  {
    if( recordDefinition == null || !GMLSchemaUtilities.substitutes( recordDefinition.getFeatureType(), ObservationFeatureFactory.QNAME_F_SORTED_RECORD_DEFINITION ) )
      return new IComponent[0];

    final List<IComponent> components = new ArrayList<>();

    final FeatureList comps = (FeatureList)recordDefinition.getProperty( ObservationFeatureFactory.QNAME_P_SORTED_COMPONENT );
    for( final Object object : comps )
    {
      final Feature itemDef = FeatureHelper.getFeature( recordDefinition.getWorkspace(), object );

      components.add( new FeatureComponent( itemDef ) );
    }

    return components.toArray( new IComponent[components.size()] );
  }

  private static IComponent getOrdinalNumberComponent( final Feature recordDefinition )
  {
    if( recordDefinition == null || !GMLSchemaUtilities.substitutes( recordDefinition.getFeatureType(), ObservationFeatureFactory.QNAME_F_SORTED_RECORD_DEFINITION ) )
      return null;
    final Feature component = (Feature)recordDefinition.getProperty( ObservationFeatureFactory.QNAME_P_ORDINALNUMBER_COMPONENT );
    if( component != null )
      return new FeatureComponent( FeatureHelper.getFeature( recordDefinition.getWorkspace(), component ) );
    return null;
  }

  public static XsdBaseTypeHandler< ? >[] typeHandlersForComponents( final IComponent[] components )
  {
    final XsdBaseTypeHandler< ? >[] typeHandlers = new XsdBaseTypeHandler[components.length];

    for( int i = 0; i < components.length; i++ )
    {
      typeHandlers[i] = ObservationFeatureFactory.typeHanderForComponent( components[i] );
    }

    return typeHandlers;
  }

  public static final XsdBaseTypeHandler< ? > typeHanderForComponent( final IComponent component )
  {
    final ITypeRegistry<IMarshallingTypeHandler> typeRegistry = MarshallingTypeRegistrySingleton.getTypeRegistry();

    final QName valueTypeName = component.getValueTypeName();

    // REMARK: special case for boolean values, as their string represantation is not always
    // 'true' or 'false'
    if( valueTypeName.equals( new QName( NS.XSD_SCHEMA, "boolean" ) ) ) //$NON-NLS-1$
    {
      // TODO: we need to return a special type handler, that serializes according to the enumeration restriction
      // of the component
      // At the moment we still have the old behaviour: we read/write only 'false' and 'true'
    }

    final IMarshallingTypeHandler handler = typeRegistry.getTypeHandlerForTypeName( valueTypeName );
    if( handler instanceof XsdBaseTypeHandler< ? > )
      return (XsdBaseTypeHandler< ? >)handler;

    return null;
  }

  public static IComponent[] componentsFromFeature( final Feature f )
  {
    final IFeatureType featureType = f.getFeatureType();

    if( !GMLSchemaUtilities.substitutes( featureType, ObservationFeatureFactory.OM_OBSERVATION ) )
      throw new IllegalArgumentException( Messages.getString( "org.kalypso.ogc.gml.om.ObservationFeatureFactory.22" ) + f ); //$NON-NLS-1$

    final Feature recordDefinition = ObservationFeatureFactory.getOrCreateRecordDefinition( f );
    return ObservationFeatureFactory.buildComponents( recordDefinition );
  }

  /**
   * Helper: builds the list of component definitions defined as ItemDefinitions within the recordDefinition element.
   * <p>
   * This method is declared protected, but if the need emanes, it could be made public.
   */
  protected static IComponent[] buildComponents( final Feature recordDefinition )
  {
    if( recordDefinition == null )
      return new IComponent[0];

    final List<IComponent> components = new ArrayList<>();

    final FeatureList comps = (FeatureList)recordDefinition.getProperty( ObservationFeatureFactory.SWE_COMPONENT );
    for( final Object object : comps )
    {
      final Feature itemDef = FeatureHelper.getFeature( recordDefinition.getWorkspace(), object );

      components.add( new FeatureComponent( itemDef ) );
    }

    return components.toArray( new IComponent[components.size()] );
  }

  /**
   * Writes the contents of a tuple based observation into a feature. The feature must substitute
   * http://www.opengis.net/om:Observation.
   */
  public static void toFeature( final IObservation<TupleResult> source, final Feature targetObsFeature )
  {
    final FeatureChange[] changes = ObservationFeatureFactory.toFeatureAsChanges( source, targetObsFeature );
    for( final FeatureChange change : changes )
    {
      change.getFeature().setProperty( change.getProperty(), change.getNewValue() );
    }
  }

  public static FeatureChange[] toFeatureAsChanges( final IObservation<TupleResult> source, final Feature targetObsFeature )
  {
    final IFeatureType featureType = targetObsFeature.getFeatureType();

    if( !GMLSchemaUtilities.substitutes( featureType, ObservationFeatureFactory.OM_OBSERVATION ) )
      throw new IllegalArgumentException( Messages.getString( "org.kalypso.ogc.gml.om.ObservationFeatureFactory.23" ) + targetObsFeature ); //$NON-NLS-1$

    final List<FeatureChange> changes = new ArrayList<>();

    changes.add( new FeatureChange( targetObsFeature, featureType.getProperty( Feature.QN_NAME ), Collections.singletonList( source.getName() ) ) );
    changes.add( new FeatureChange( targetObsFeature, featureType.getProperty( Feature.QN_DESCRIPTION ), source.getDescription() ) );

    final IRelationType phenPt = (IRelationType)featureType.getProperty( ObservationFeatureFactory.OM_OBSERVED_PROP );
    final IPhenomenon phenomenon = source.getPhenomenon();
    final Object phenonemonFeature = toFeaturePhenonemon( phenomenon, targetObsFeature, phenPt );
    changes.add( new FeatureChange( targetObsFeature, phenPt, phenonemonFeature ) );

    final TupleResult result = source.getResult();

    final IComponent[] components = result.getComponents();
    final IComponent[] sortComponents = result.getSortComponents();

    // FIXME: nonsense!
    final IComponent ordinalNumberComponent = result.getOrdinalNumberComponent();

    final IRelationType targetObsFeatureRelation = (IRelationType)featureType.getProperty( ObservationFeatureFactory.OM_RESULTDEFINITION );
    final Feature rd = ObservationFeatureFactory.buildRecordDefinition( targetObsFeature, targetObsFeatureRelation, components, sortComponents, ordinalNumberComponent );
    changes.add( new FeatureChange( targetObsFeature, targetObsFeatureRelation, rd ) );

    final String strResult = ObservationFeatureFactory.serializeResultAsString( result );
    changes.add( new FeatureChange( targetObsFeature, featureType.getProperty( ObservationFeatureFactory.OM_RESULT ), strResult ) );

    return changes.toArray( new FeatureChange[changes.size()] );
  }

  private static Object toFeaturePhenonemon( final IPhenomenon phenomenon, final Feature targetObsFeature, final IRelationType phenPt )
  {
    if( phenomenon == null )
      return null;

    final String id = phenomenon.getID();

    final Object phenomenonRef = FeatureHelper.createLinkToID( id, targetObsFeature, phenPt, phenPt.getTargetFeatureType() );
    if( phenomenonRef == null || phenomenonRef instanceof String )
      return phenomenonRef;

    if( phenomenonRef instanceof IXLinkedFeature )
    {
      // check if link exists, if not, we create an inline phenonemon
      if( ((IXLinkedFeature)phenomenonRef).getFeatureId() != null )
      {
        return phenomenonRef;
      }

      final org.kalypso.deegree.binding.swe.Phenomenon phenonemonFeature = (org.kalypso.deegree.binding.swe.Phenomenon)FeatureFactory.createFeature( targetObsFeature, phenPt, id, phenPt.getTargetFeatureType(), true );
      phenonemonFeature.setName( phenomenon.getName() );
      phenonemonFeature.setDescription( phenomenon.getDescription() );

      return phenonemonFeature;
    }

    return null;
  }

  /**
   * Helper: builds the record definition according to the components of the tuple result.
   * 
   * @param map
   *          ATTENTION: the recordset is written in the same order as this map
   */
  public static Feature buildRecordDefinition( final Feature targetObsFeature, final IRelationType targetObsFeatureRelation, final IComponent[] components, final IComponent[] sortComponents, final IComponent ordinalNumberComponent )
  {
// final IGMLSchema schema = targetObsFeature.getWorkspace().getGMLSchema();

    final IFeatureType recordDefinitionFT;
    if( sortComponents == null || sortComponents.length == 0 )
      recordDefinitionFT = GMLSchemaUtilities.getFeatureTypeQuiet( ObservationFeatureFactory.SWE_RECORDDEFINITIONTYPE );
    else
      recordDefinitionFT = GMLSchemaUtilities.getFeatureTypeQuiet( ObservationFeatureFactory.QNAME_F_SORTED_RECORD_DEFINITION );

    // set resultDefinition property, create RecordDefinition feature
    final Feature featureRD = createSafeFeature( targetObsFeature, targetObsFeatureRelation.getQName(), recordDefinitionFT.getQName() );

    final IRelationType itemDefRelation = (IRelationType)featureRD.getFeatureType().getProperty( ObservationFeatureFactory.SWE_COMPONENT );

    // for each component, set a component property, create a feature: ItemDefinition
    for( final IComponent comp : components )
    {
      final Feature featureItemDef = ObservationFeatureFactory.itemDefinitionFromComponent( featureRD, itemDefRelation, comp );

      NamedFeatureHelper.setName( featureItemDef, comp.getName() );
      NamedFeatureHelper.setDescription( featureItemDef, comp.getDescription() );

      FeatureHelper.addProperty( featureRD, ObservationFeatureFactory.SWE_COMPONENT, featureItemDef );
    }

    if( sortComponents != null && sortComponents.length > 0 )
    {
      final IRelationType sortedItemDefRelation = (IRelationType)featureRD.getFeatureType().getProperty( ObservationFeatureFactory.QNAME_P_SORTED_COMPONENT );
      for( final IComponent comp : sortComponents )
      {
        final Feature featureItemDef = ObservationFeatureFactory.itemDefinitionFromComponent( featureRD, sortedItemDefRelation, comp );
        FeatureHelper.addProperty( featureRD, ObservationFeatureFactory.QNAME_P_SORTED_COMPONENT, featureItemDef );
      }
    }

    // FIXME: nonsense!
    if( ordinalNumberComponent != null )
    {
      final IRelationType ordinalNumberItemDefRelation = (IRelationType)featureRD.getFeatureType().getProperty( ObservationFeatureFactory.QNAME_P_ORDINALNUMBER_COMPONENT );
      final Feature featureItemDef = ObservationFeatureFactory.itemDefinitionFromComponent( featureRD, ordinalNumberItemDefRelation, ordinalNumberComponent );
      FeatureHelper.addProperty( featureRD, ObservationFeatureFactory.QNAME_P_ORDINALNUMBER_COMPONENT, featureItemDef );
    }

    return featureRD;
  }

  private static Feature itemDefinitionFromComponent( final Feature recordDefinition, final IRelationType itemDefinitionRelation, final IComponent comp )
  {
    final String id = comp.getId();
    // try to find a dictionary entry for this component, if it exists, create xlinked-feature to it
    final IFeatureType itemDefType = GMLSchemaUtilities.getFeatureTypeQuiet( ObservationFeatureFactory.SWE_ITEMDEFINITION );
    final IXLinkedFeature xlink = FeatureFactory.createXLink( recordDefinition, itemDefinitionRelation, itemDefType, id );
    if( xlink.getFeature() != null )
      return xlink;

    if( comp instanceof FeatureComponent )
    {
      // TODO: dangerous, we should always create a new feature here, don't we?

      final FeatureComponent fc = (FeatureComponent)comp;
      return fc.getItemDefinition();
    }

    final Feature itemDefinition = recordDefinition.getWorkspace().createFeature( recordDefinition, itemDefinitionRelation, itemDefType );
    itemDefinition.setName( comp.getName() );
    itemDefinition.setDescription( comp.getDescription() );

    /* Phenomenon */
    final IRelationType phenomenonRelation = (IRelationType)itemDefinition.getFeatureType().getProperty( ObservationFeatureFactory.SWE_PROPERTY );
    final IPhenomenon phenomenon = comp.getPhenomenon();

    final Feature featurePhenomenon = PhenomenonUtilities.createPhenomenonFeature( phenomenon, itemDefinition, phenomenonRelation );
    itemDefinition.setProperty( phenomenonRelation, featurePhenomenon );

    /* Representation type */
    final RepresentationType rt = ObservationFeatureFactory.createRepresentationType( comp );
    itemDefinition.setProperty( ObservationFeatureFactory.SWE_REPRESENTATION, rt );

    return itemDefinition;
  }

  /**
   * Creates an instance of ComponentDefinition that best fits the given component
   */
  public static RepresentationType createRepresentationType( final IComponent component )
  {
    if( component == null )
      throw new IllegalArgumentException( Messages.getString( "org.kalypso.ogc.gml.om.ObservationFeatureFactory.29" ) ); //$NON-NLS-1$

    final QName valueTypeName = component.getValueTypeName();

    final String classification = ""; //$NON-NLS-1$

    final String unit = component.getUnit();

    final String frame = component.getFrame();

    final IRestriction[] restrictions = component.getRestrictions();

    return new RepresentationType( ObservationFeatureFactory.toKind( valueTypeName ), valueTypeName, unit, frame, restrictions, classification );
  }

  /**
   * Finds the best KIND that suits the given QName
   */
  private static KIND toKind( final QName valueTypeName )
  {
    if( XmlTypes.XS_BOOLEAN.equals( valueTypeName ) )
      return KIND.Boolean;

    if( valueTypeName.equals( XmlTypes.XS_DOUBLE ) )
    {
      // TODO: also check, if comp only contains min/maxXXclusive restrictions, else it is no number
// if( XmlTypes.isNumber( valueTypeName ) )
      return KIND.Number;
    }

    if( XmlTypes.isNumber( valueTypeName ) || XmlTypes.isDate( valueTypeName ) )
      return KIND.SimpleType;

    return KIND.Word;
  }

  public static String serializeResultAsString( final TupleResult result )
  {
    final StringBuffer buffer = new StringBuffer();

    final IComponent[] components = result.getComponents();

    final XsdBaseTypeHandler< ? >[] handlers = ObservationFeatureFactory.typeHandlersForComponents( components );

    for( final IRecord record : result )
    {
      for( int i = 0; i < components.length; i++ )
      {
        final XsdBaseTypeHandler< ? > handler = handlers[i];
        final IComponent comp = components[i];

        if( comp != components[0] )
          buffer.append( ' ' ); //$NON-NLS-1$

        final Object value = record.getValue( i );
        final String bufferValue = ObservationFeatureFactory.recordValueToString( value, handler );

        if( bufferValue != null && bufferValue.trim().isEmpty() )
        {
          // NEVER write an empty string! It will break the format....
          buffer.append( "null" ); //$NON-NLS-1$
        }
        else
          buffer.append( bufferValue );
      }

      buffer.append( "\n" ); //$NON-NLS-1$
    }

    return buffer.toString();
  }

  private static String recordValueToString( final Object value, @SuppressWarnings( "rawtypes" ) final XsdBaseTypeHandler handler )
  {
    if( value == null )
      return "null"; //$NON-NLS-1$

    // REMARK URLEncoder: encoding of xml file is not known - at this point we assume target encoding as "UTF-8".
    // Windows Eclipse often creates Cp-1252 encoded files
    try
    {
      // FIXME implement other type handlers over an fabrication method
      if( handler instanceof XsdBaseTypeHandlerString )
      {
        final XsdBaseTypeHandlerString myHandler = (XsdBaseTypeHandlerString)handler;
        return myHandler.convertToXMLString( URLEncoder.encode( (String)value, "UTF-8" ) ); //$NON-NLS-1$
      }

      if( handler instanceof XsdBaseTypeHandlerXMLGregorianCalendar )
      {
        final XsdBaseTypeHandlerXMLGregorianCalendar myHandler = (XsdBaseTypeHandlerXMLGregorianCalendar)handler;
        final XMLGregorianCalendar cal = (XMLGregorianCalendar)value;
        return URLEncoder.encode( myHandler.convertToXMLString( cal ), "UTF-8" ); //$NON-NLS-1$
      }

      @SuppressWarnings( "unchecked" ) final String xmlString = handler.convertToXMLString( value );
      return xmlString;
    }
    catch( final UnsupportedEncodingException e )
    {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * TODO do not directly return an observation, but rather an observation provider
   * <p>
   * TODO do not create an observation twice for the same feature, pooling?
   */
  @Override
  public Object getAdapter( final Object adaptableObject, final Class adapterType )
  {
    if( adapterType == IObservation.class && adaptableObject instanceof Feature )
      return ObservationFeatureFactory.toObservation( (Feature)adaptableObject );

    return null;
  }

  @Override
  public Class< ? >[] getAdapterList( )
  {
    return new Class[] { IObservation.class };
  }

  public static FeatureComponent createDictionaryComponent( final Feature obsFeature, final String dictUrn )
  {
    final Feature recordDefinition = ObservationFeatureFactory.getOrCreateRecordDefinition( obsFeature );

    final IFeatureType featureType = GMLSchemaUtilities.getFeatureTypeQuiet( ObservationFeatureFactory.SWE_ITEMDEFINITION );

    final FeatureList componentList = (FeatureList)recordDefinition.getProperty( ObservationFeatureFactory.SWE_COMPONENT );

    final IXLinkedFeature itemDef = componentList.addLink( dictUrn, featureType );

    return new FeatureComponent( itemDef );
  }

  private static Feature getOrCreateRecordDefinition( final Feature obsFeature )
  {
    final IRelationType resultRelation = (IRelationType)obsFeature.getFeatureType().getProperty( ObservationFeatureFactory.OM_RESULTDEFINITION );
    final Feature recordDefinition = FeatureHelper.resolveLink( obsFeature, ObservationFeatureFactory.OM_RESULTDEFINITION );
    /* Make sure there is always a record definition */
    if( recordDefinition == null )
    {
      final Feature rd = ObservationFeatureFactory.buildRecordDefinition( obsFeature, resultRelation, new IComponent[] {}, null, null );
      obsFeature.setProperty( ObservationFeatureFactory.OM_RESULTDEFINITION, rd );
      return rd;
    }
    return recordDefinition;
  }

  public static Feature createSafeFeature( final Feature feature, final QName parentRelation, final QName featureName )
  {
    final GMLWorkspace workspace = feature.getWorkspace();
    final IRelationType rdParentRelation = (IRelationType)feature.getFeatureType().getProperty( parentRelation );
    final IFeatureType featureType = GMLSchemaUtilities.getFeatureTypeQuiet( featureName );

    if( workspace == null )
    {
      final String id = featureName.getLocalPart() + System.currentTimeMillis() + Math.random();
      return FeatureFactory.createFeature( feature, rdParentRelation, id, featureType, true );
    }

    return workspace.createFeature( feature, rdParentRelation, featureType );
  }
}