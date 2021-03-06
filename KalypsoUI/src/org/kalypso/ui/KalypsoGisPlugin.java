/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.ui;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.kalypso.commons.eclipse.core.runtime.PluginImageProvider;
import org.kalypso.contribs.eclipse.core.runtime.TempFileUtilities;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.util.pool.ResourcePool;
import org.kalypso.ogc.gml.dict.DictionaryCatalog;
import org.kalypso.ogc.gml.table.celleditors.DefaultFeatureModifierFactory;
import org.kalypso.ogc.gml.table.celleditors.IFeatureModifierFactory;
import org.kalypso.ogc.sensor.cache.ObservationCache;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class KalypsoGisPlugin extends AbstractUIPlugin implements IPropertyChangeListener
{
  public static final String PLUGIN_ID = "org.kalypso.ui"; //$NON-NLS-1$

  private static KalypsoGisPlugin THE_PLUGIN = null;

  private DefaultFeatureModifierFactory m_defaultFeatureControlFactory;

  private DictionaryCatalog m_dictionaryCatalog;

  private PluginImageProvider m_imgProvider = null;

  /**
   * The constructor. Manages the configuration of the kalypso client.
   */
  public KalypsoGisPlugin( )
  {
    KalypsoGisPlugin.THE_PLUGIN = this;

    try
    {
      // for AWT and Swing stuff used with SWT_AWT so that they look like OS controls
      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
    }
    catch( final Exception e1 )
    {
      e1.printStackTrace();
    }
  }

  private void configureLogger( )
  {
    // TODO:REMOVE THIS: we should always use the eclipse logging mechanisms
    final Logger logger = Logger.getLogger( "org.kalypso" ); //$NON-NLS-1$
    logger.setLevel( Level.INFO );

    final Handler[] handlers = logger.getHandlers();
    for( final Handler handler : handlers )
    {
      handler.setLevel( Level.FINER );
    }
  }

  /**
   * Delete a list of temp dirs found in the properties file 'deletetempdir.properties'. This method is called on
   * plugin-startup to clean the specified directories.
   */
  private void deleteTempDirs( )
  {
    final Properties props = new Properties();
    InputStream ins = null;
    try
    {
      ins = getClass().getResourceAsStream( "resources/deletetempdir.properties" ); //$NON-NLS-1$
      props.load( ins );
      ins.close();

      final String pDirs = props.getProperty( "DELETE_STARTUP", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      final String[] dirNames = pDirs.split( "," ); //$NON-NLS-1$
      for( final String element : dirNames )
      {
        TempFileUtilities.deleteTempDir( this, element );
      }
    }
    catch( final IOException e )
    {
      e.printStackTrace();
    }
    finally
    {
      IOUtils.closeQuietly( ins );
    }
  }

  /**
   * @deprecated use {@link KalypsoCorePlugin#getPool()} instead.
   */
  @Deprecated
  public ResourcePool getPool( )
  {
    return KalypsoCorePlugin.getDefault().getPool();
  }

  /**
   * This method is called upon plug-in activation
   */
  @Override
  public void start( final BundleContext context ) throws Exception
  {
    super.start( context );

    m_imgProvider = new PluginImageProvider( this );
    m_imgProvider.resetTmpFiles();
    configureLogger();

    deleteTempDirs();

    getPreferenceStore().addPropertyChangeListener( this );
  }

  /**
   * This method is called when the plug-in is stopped
   * 
   * @param context
   * @throws Exception
   */
  @Override
  public void stop( final BundleContext context ) throws Exception
  {
    super.stop( context );

    getPreferenceStore().removePropertyChangeListener( this );

    // clear the observation cache
    ObservationCache.clearCache();

    m_imgProvider.resetTmpFiles();
    m_imgProvider = null;

    m_dictionaryCatalog = null;
  }

  public static String getId( )
  {
    return KalypsoGisPlugin.getDefault().getBundle().getSymbolicName();
  }

  /**
   * Returns the shared instance.
   * 
   * @return singleton
   */
  public static KalypsoGisPlugin getDefault( )
  {
    // m_plugin should be set in the constructor
    if( KalypsoGisPlugin.THE_PLUGIN == null )
    {
      throw new NullPointerException( Messages.getString( "org.kalypso.ui.KalypsoGisPlugin.20" ) ); //$NON-NLS-1$
    }

    return KalypsoGisPlugin.THE_PLUGIN;
  }

  /**
   * @return The timeZone as defined in the KALYPSO preferences. If unknown, the system default timezone is returned.
   * @deprecated Use {@link KalypsoCorePlugin#getTimeZone()} instead.
   */
  @Deprecated
  public TimeZone getDisplayTimeZone( )
  {
    return KalypsoCorePlugin.getDefault().getTimeZone();
  }

  /**
   * Returns the global format for displaying dates + times.<br/>
   * The format is preconfigured with the right display timezone (i.e. no need for extra call to {@link #getDisplayTimeZone()}.<br/>
   * <br/>
   * TODO: replace all static date format all around the place <br/>
   * TODO: we should provide several versions: long/short, for String.format etc.<br/>
   * TODO: Let user choose hiw own format in the kalypso preference page <br/>
   */
  public DateFormat getDisplayDateTimeFormat( )
  {
    // We recreate the date format in order to support change of preferences without restart of Kalypso
    final DateFormat df = new SimpleDateFormat( "dd.MM.yyyy HH:mm" ); //$NON-NLS-1$
    df.setTimeZone( getDisplayTimeZone() );
    return df;
  }

  /**
   * @deprecated Use {@link KalypsoCorePlugin#getCoordinatesSystem()} instead.
   */
  @Deprecated
  public String getCoordinatesSystem( )
  {
    return KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
  }

  public int getDefaultMapSelectionID( )
  {
    return 0x1;
  }

  /**
   * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
   */
  @Override
  public void propertyChange( final PropertyChangeEvent event )
  {
  }

  public synchronized IFeatureModifierFactory getFeatureTypeCellEditorFactory( )
  {
    if( m_defaultFeatureControlFactory == null )
    {
      m_defaultFeatureControlFactory = new DefaultFeatureModifierFactory();
    }
    return m_defaultFeatureControlFactory;
  }

  public static PluginImageProvider getImageProvider( )
  {
    return KalypsoGisPlugin.getDefault().m_imgProvider;
  }

  public static DictionaryCatalog getDictionaryCatalog( )
  {
    final KalypsoGisPlugin defaultPlugin = KalypsoGisPlugin.getDefault();
    if( defaultPlugin.m_dictionaryCatalog == null )
    {
      defaultPlugin.m_dictionaryCatalog = new DictionaryCatalog();
    }
    return defaultPlugin.m_dictionaryCatalog;
  }
}