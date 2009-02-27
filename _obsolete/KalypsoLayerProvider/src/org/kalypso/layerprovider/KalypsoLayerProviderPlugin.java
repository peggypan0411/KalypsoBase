package org.kalypso.layerprovider;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class KalypsoLayerProviderPlugin extends Plugin
{

  // The shared instance.
  private static KalypsoLayerProviderPlugin plugin;

  /**
   * The constructor.
   */
  public KalypsoLayerProviderPlugin( )
  {
    plugin = this;
  }

  /**
   * This method is called upon plug-in activation
   */
  @Override
  public void start( BundleContext context ) throws Exception
  {
    super.start( context );
  }

  /**
   * This method is called when the plug-in is stopped
   */
  @Override
  public void stop( BundleContext context ) throws Exception
  {
    super.stop( context );
    plugin = null;
  }

  /**
   * Returns the shared instance.
   */
  public static KalypsoLayerProviderPlugin getDefault( )
  {
    return plugin;
  }

}
