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
package org.kalypso.core.util.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.progress.UIJob;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.kalypso.core.KalypsoCoreDebug;
import org.kalypso.core.catalog.CatalogUtilities;
import org.kalypso.core.i18n.Messages;
import org.kalypso.loader.ILoader;
import org.kalypso.loader.ILoaderFactory;
import org.kalypso.loader.LoaderException;

/**
 * @author Andreas von D�mming
 * @author Gernot Belger
 */
public class ResourcePool
{
  /**
   * Constant for system property intended to be defined in config.ini.
   * <p>
   * if true, the pool asks to save released objects if they are dirty.
   * </p>
   */
  private static final String CONFIG_INI_DO_ASK_FOR_POOL_SAVE = "kalypso.ask_for_pool_save"; //$NON-NLS-1$

  private final ILoaderFactory m_factory;

  /** key -> KeyInfo */
  private final Map<IPoolableObjectType, KeyInfo> m_keyInfos = new TreeMap<>( KeyComparator.getInstance() );

  private final IResourceChangeListener m_resourceChangeListener = new IResourceChangeListener()
  {
    @Override
    public void resourceChanged( final IResourceChangeEvent event )
    {
      handleResourceChanged( event );
    }
  };

  public ResourcePool( final ILoaderFactory factory )
  {
    m_factory = factory;

    ResourcesPlugin.getWorkspace().addResourceChangeListener( m_resourceChangeListener, IResourceChangeEvent.POST_CHANGE );
  }

  public void dispose( )
  {
    ResourcesPlugin.getWorkspace().removeResourceChangeListener( m_resourceChangeListener );

    synchronized( m_keyInfos )
    {
      for( final Entry<IPoolableObjectType, KeyInfo> entry : m_keyInfos.entrySet() )
        entry.getValue().dispose();
      m_keyInfos.clear();
    }
  }

  /**
   * F�gt einen neuen Listener zum Pool f�r eine bestimmten Key hinzu Ist das Objekt f�r den key vorhanden, wird der
   * Listener sofort informiert
   */
  public KeyInfo addPoolListener( final IPoolListener listener, final IPoolableObjectType key )
  {
    // never register a disposed listener to the pool !
    if( listener.isDisposed() )
      return null;

    final KeyInfo info = getOrCreateInfo( key );
    info.addListener( listener );

    return info;
  }

  private KeyInfo getOrCreateInfo( final IPoolableObjectType key )
  {
    synchronized( m_keyInfos )
    {
      final KeyInfo info = m_keyInfos.get( key );
      if( info != null )
        return info;

      try
      {
        final ILoader loader = m_factory.getLoaderInstance( key.getType() );
        final KeyInfo newInfo = new KeyInfo( key, loader );
        m_keyInfos.put( key, newInfo );
        return newInfo;
      }
      catch( final Exception e )
      {
        final String msg = String.format( "No Loader for type: %s", key.getType() );//$NON-NLS-1$
        final RuntimeException iae = new IllegalArgumentException( msg );
        KalypsoCoreDebug.RESOURCE_POOL.printStackTrace( iae );
        throw iae;
      }
    }
  }

  public void removePoolListener( final IPoolListener l )
  {
    final List<KeyInfo> infosToDispose = new ArrayList<>();

    synchronized( m_keyInfos )
    {
      for( final Iterator<Entry<IPoolableObjectType, KeyInfo>> iter = m_keyInfos.entrySet().iterator(); iter.hasNext(); )
      {
        final Entry<IPoolableObjectType, KeyInfo> entry = iter.next();

        final IPoolableObjectType key = entry.getKey();
        final KeyInfo info = entry.getValue();
        final boolean wasRemoved = info.removeListener( l );
        if( wasRemoved && info.isEmpty() )
        {
          KalypsoCoreDebug.RESOURCE_POOL.printf( "Releasing key (no more listeners): %s%n", key ); //$NON-NLS-1$

          iter.remove();

          infosToDispose.add( info );
        }
      }
    }

    final ISchedulingRule mutex = ResourcesPlugin.getWorkspace().getRoot();

    for( final KeyInfo info : infosToDispose )
    {
      final String askForSaveProperty = System.getProperty( CONFIG_INI_DO_ASK_FOR_POOL_SAVE, "false" ); //$NON-NLS-1$
      final boolean askForSave = Boolean.parseBoolean( askForSaveProperty );
      final boolean isSaveable = info.isSaveable();
      final String location = info.getKey().getLocation();
      final boolean isCatalogresource = CatalogUtilities.isCatalogResource( location );

      if( !info.isDirty() )
        info.dispose();
      else if( askForSave && isSaveable && !isCatalogresource )
      {
        final UIJob job = new SaveAndDisposeInfoJob( Messages.getString( "org.kalypso.util.pool.ResourcePool.5" ), info ); //$NON-NLS-1$
        job.setUser( true );
        job.setRule( mutex );
        job.schedule();
      }
      else
      {
        System.out.println( Messages.getString( "org.kalypso.util.pool.ResourcePool.6" ) + info.getObject() ); //$NON-NLS-1$
        info.dispose();
      }
    }
  }

  public void saveObject( final Object object, final IProgressMonitor monitor ) throws LoaderException
  {
    final List<KeyInfo> infosToSave = new ArrayList<>();

    synchronized( m_keyInfos )
    {
      if( object == null )
        return;

      final Collection<KeyInfo> values = m_keyInfos.values();
      for( final KeyInfo info : values )
      {
        if( info.getObject() == object )
          infosToSave.add( info );
      }
    }

    // REMARK: we do not save inside the sync-block, because saving may cause access to
    // the pool (Example: saving a GML might cause access to Xlinked properties)
    // TODO: monitor handling wrong!
    for( final KeyInfo keyInfo : infosToSave )
      keyInfo.saveObject( monitor );
  }

  /** Get the key info which is responsible for a given object. */
  public KeyInfo getInfo( final Object object )
  {
    synchronized( m_keyInfos )
    {
      if( object == null )
        return null;

      final Collection<KeyInfo> values = m_keyInfos.values();
      for( final KeyInfo info : values )
      {
        if( info.getObject() == object )
          return info;
      }

      return null;
    }
  }

  public KeyInfo[] getInfos( )
  {
    return m_keyInfos.values().toArray( new KeyInfo[0] );
  }

  /**
   * Registers a pool listener ANd directly loads the associated object. If the object was already loaded, only the
   * listener is registered and the object is returned.
   */
  public Object loadObject( final IPoolListener listener, final IPoolableObjectType key ) throws CoreException
  {
    return getObject( key, listener );
  }

  /**
   * Specific method for synchron-loading. If the given key is already present, the associated object is returned. Else
   * a new key is temporary created for the purpose of loading. Once done, the key is disposed.
   * <p>
   * Use this method if you want direct-loading (synchronuous).
   * <p>
   * Bear in mind that the pool-listener mechanism is bypassed here.
   */
  public Object getObject( final IPoolableObjectType key ) throws CoreException
  {
    return getObject( key, null );
  }

  // TODO: synchronize
  /**
   * @param listener
   *          If non-<code>null</code>, this listener will be registered and the freshly create info will not be
   *          disposed.
   */
  private Object getObject( final IPoolableObjectType key, final IPoolListener listener ) throws CoreException
  {
    final KeyInfo existingInfo;

    synchronized( m_keyInfos )
    {
      existingInfo = m_keyInfos.get( key );
    }

    if( existingInfo != null )
    {
      try
      {
        // wait for info
        existingInfo.join();

        final IStatus result = existingInfo.getJobResult();

        if( listener != null )
          existingInfo.addListener( listener );

        if( !result.isOK() )
          throw new CoreException( result );

        return existingInfo.getObject();
      }
      catch( final InterruptedException e )
      {
        e.printStackTrace();

        throw new CoreException( StatusUtilities.statusFromThrowable( e, Messages.getString( "org.kalypso.util.pool.ResourcePool.7" ) ) ); //$NON-NLS-1$
      }
    }

    /**
     * Create new key and load object. If listener is non-<code>null</code>, keep the info and object, else dispose
     * everything.
     */
    KeyInfo newInfo = null;
    try
    {
      final ILoader loader = m_factory.getLoaderInstance( key.getType() );
      newInfo = new KeyInfo( key, loader );
      final IStatus result = newInfo.loadObject( new NullProgressMonitor() );
      if( (result.getSeverity() & IStatus.ERROR) != 0 )
        throw new CoreException( result );

      if( listener != null )
      {
        newInfo.addListener( listener );

        synchronized( m_keyInfos )
        {
          m_keyInfos.put( key, newInfo );
        }
      }

      return newInfo.getObject();
    }
    catch( final Exception e )
    {
      throw new CoreException( StatusUtilities.statusFromThrowable( e, Messages.getString( "org.kalypso.util.pool.ResourcePool.8" ) ) ); //$NON-NLS-1$
    }
    finally
    {
      if( newInfo != null && listener == null )
        newInfo.dispose();
    }
  }

  public KeyInfo getInfoForKey( final IPoolableObjectType poolKey )// TODO: synchronize
  {
    return m_keyInfos.get( poolKey );
  }

  protected void handleResourceChanged( final IResourceChangeEvent event )
  {
    // allways true, because of the bitmask set on adding this listener
    if( event.getType() == IResourceChangeEvent.POST_CHANGE )
    {
      final IResourceDelta delta = event.getDelta();

      KeyInfo[] array;
      synchronized( m_keyInfos )
      {
        final Collection<KeyInfo> values = m_keyInfos.values();
        array = values.toArray( new KeyInfo[values.size()] );
      }

      for( final KeyInfo keyInfo : array )
        keyInfo.handleResourceChanged( delta );
    }
  }
}