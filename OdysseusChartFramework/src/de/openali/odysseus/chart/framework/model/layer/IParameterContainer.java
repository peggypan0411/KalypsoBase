package de.openali.odysseus.chart.framework.model.layer;

import java.util.List;
import java.util.Map;

import de.openali.odysseus.chart.framework.model.data.IStringParser;

/**
 * The interface provides access to a collection of parameters, grouped by name spaces
 * 
 * @author alibu
 */
public interface IParameterContainer
{
  String[] findAllKeys( String string );

  /**
   * returns a value a parameter as String or null of no parameter by the given name is present
   */
  String getParameterValue( String paramName, String defaultValue );

  /**
   * returns a sorted list of parameters or null if no parameter list by the given name is present
   */
  List<String> getParameterList( String paramName );

  /**
   * returns an unsorted map of parameters or null if no parameter map by the given name is present
   */
  Map<String, String> getParameterMap( String paramName );

  /**
   * returns a value without checking for the name space; should only be used if there's only one name space or if all
   * parameters have unique names
   * <p>
   * TODO: should it be 'T defaultValue' ?
   */
  <T> T getParsedParameterValue( String paramName, String defaultValue, IStringParser<T> parser );

  <T> List<T> getParsedParameterList( String paramName, List<String> defaultValues, IStringParser<T> parser );

  <T> Map<String, T> getParsedParameterMap( String paramName, Map<String, String> defaultValues, IStringParser<T> parser );

  String getOwnerId( );

  String[] keys( );
}
