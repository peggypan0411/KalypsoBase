package de.openali.odysseus.chart.framework.logging;

/**
 * interface for logging purposes
 * 
 * @author alibu
 */
public interface ILogger
{
  String TOPIC_LOG_GENERAL = "org.kalypso.chart.framework/debug"; //$NON-NLS-1$

  String TOPIC_LOG_AXIS = "org.kalypso.chart.framework/debug/axis"; //$NON-NLS-1$

  String TOPIC_LOG_LAYER = "org.kalypso.chart.framework/debug/layer"; //$NON-NLS-1$

  String TOPIC_LOG_PLOT = "org.kalypso.chart.framework/debug/plot"; //$NON-NLS-1$

  String TOPIC_LOG_LEGEND = "org.kalypso.chart.framework/debug/legend"; //$NON-NLS-1$

  String TOPIC_LOG_STYLE = "org.kalypso.chart.framework/debug/style"; //$NON-NLS-1$

  String TOPIC_TRACE = "org.kalypso.chart.framework/trace"; //$NON-NLS-1$

  String TOPIC_LOG_CONFIG = "org.kalypso.chart.framework/debug/config"; //$NON-NLS-1$

  String TOPIC_LOG_CHART = "org.kalypso.chart.framework/debug/chart"; //$NON-NLS-1$

  String TOPIC_LOG_DATA = "org.kalypso.chart.framework/debug/data"; //$NON-NLS-1$

  void logInfo( String topic, String msg );

  void logWarning( String topic, String msg );

  void logError( String topic, String msg );

  void logFatal( String topic, String msg );

}
