package org.kalypso.gmlschema.xml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.kalypso.gmlschema.property.IValuePropertyType;

/**
 * mapping between xml-typenames and java-classnames for GML-geometry types and XMLSCHEMA-simple types
 *
 * @author doemming
 */
public class Mapper
{
  private static final SimpleDateFormat XML_DATETIME_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" ); //$NON-NLS-1$

  private static final SimpleDateFormat XML_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd" ); //$NON-NLS-1$

  /**
   * @deprecated Use {@link org.kalypso.gmlschema.types.XsdBaseContentHandler} for that instead
   */
  @SuppressWarnings("dep-ann")
  public static String mapJavaValueToXml( final Object value )
  {
    if( value == null )
      return ""; //$NON-NLS-1$

    if( value instanceof DateWithoutTime )
      return XML_DATE_FORMAT.format( (DateWithoutTime) value );

    if( value instanceof Date )
      return XML_DATETIME_FORMAT.format( (Date) value );

    if( value instanceof Number )
      // TODO: use a special (xml-conform) formatting?
      return value.toString();

    return value.toString();
  }

  /**
   * @deprecated Use {@link org.kalypso.gmlschema.types.XsdBaseContentHandler} instead.
   */
  @Deprecated
  public static Object mapXMLValueToJava( final String value, final Class< ? > clazz ) throws Exception
  {
    if( clazz == String.class )
      return value;
    if( clazz == Float.class )
      return new Float( value );
    if( clazz == Double.class )
      return new Double( value );
    if( clazz == Integer.class )
    {
      // shapefiles give string like "10.0"
      final double doubleValue = Double.parseDouble( value );
      final Integer integer = new Integer( (int) doubleValue );
      if( integer.intValue() != doubleValue )
        throw new Exception( "no valid int value :" + value ); //$NON-NLS-1$
      return integer;
    }
    if( clazz == Long.class )
    {
      // shapefiles give strings like "10.0"
      final double doubleValue = Double.parseDouble( value );
      final Long longValue = new Long( (long) doubleValue );
      if( longValue.longValue() != doubleValue )
        throw new Exception( "no valid long value :" + value ); //$NON-NLS-1$
      return longValue;
    }
    if( clazz == Boolean.class )
    {
      if( "true".equals( value ) || "1".equals( value ) ) //$NON-NLS-1$ //$NON-NLS-2$
        return new Boolean( true );
      return new Boolean( false );
    }
    if( clazz == Date.class )
      return XML_DATETIME_FORMAT.parseObject( value );

    if( clazz == DateWithoutTime.class )
      return XML_DATE_FORMAT.parseObject( value );

    throw new Exception( "unknown XML type: " + clazz + "  for value: " + value ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public static Object defaultValueforJavaType( final Class< ? > type )
  {
    // Why not ask the type handler for a default value?

    if( "java.util.Date".equals( type ) ) //$NON-NLS-1$
      return new Date();
    if( "DateWithoutTime.class.getName()".equals( type ) ) //$NON-NLS-1$
      return new DateWithoutTime();
    if( "java.lang.Boolean".equals( type ) ) //$NON-NLS-1$
      return Boolean.FALSE;
    if( "java.lang.Float".equals( type ) ) //$NON-NLS-1$
      return new Float( 0 );
    if( "java.lang.Integer".equals( type ) ) //$NON-NLS-1$
      return new Integer( 0 );
    if( "java.lang.String".equals( type ) ) //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    if( "java.lang.Double".equals( type ) ) //$NON-NLS-1$
      return new Double( 0.0 );
    if( "java.lang.Long".equals( type ) ) //$NON-NLS-1$
      return new Long( 0 );

    return null;
  }

  public static Object defaultValueforJavaType( final IValuePropertyType ftp )
  {
    if( ftp.isList() )
      return new ArrayList<>();

    return defaultValueforJavaType( ftp.getClass() );
  }

  public static Object mapXMLValueToJava( final String value, final IValuePropertyType ftp ) throws Exception
  {
    return mapXMLValueToJava( value, ftp.getValueClass() );
  }
}