package org.kalypso.ftp.service;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class KalypsoFtpService extends Plugin
{
  public static final String PLUGIN_ID = "org.kalypso.ftp.service"; //$NON-NLS-1$

  private static KalypsoFtpService PLUGIN;

  public KalypsoFtpService( )
  {
  }

  @Override
  public void start( final BundleContext context ) throws Exception
  {
    super.start( context );

    final KalypsoFtpFactory factory = KalypsoFtpFactory.getInstance();
    factory.start();

    PLUGIN = this;
  }

  @Override
  public void stop( final BundleContext context ) throws Exception
  {
    PLUGIN = null;

    final KalypsoFtpFactory factory = KalypsoFtpFactory.getInstance();
    factory.stop();

    super.stop( context );
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static KalypsoFtpService getDefault( )
  {
    return PLUGIN;
  }

}
