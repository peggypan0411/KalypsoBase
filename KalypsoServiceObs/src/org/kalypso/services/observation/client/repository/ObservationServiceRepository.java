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
package org.kalypso.services.observation.client.repository;

import java.util.HashMap;
import java.util.Map;

import org.kalypso.repository.AbstractRepository;
import org.kalypso.repository.IModifyableRepository;
import org.kalypso.repository.IRepositoryItem;
import org.kalypso.repository.RepositoryException;
import org.kalypso.repository.RepositoryUtils;
import org.kalypso.services.observation.KalypsoServiceObsActivator;
import org.kalypso.services.observation.sei.IObservationService;
import org.kalypso.services.observation.sei.ItemBean;

/**
 * Repository of the Observation Service.
 * 
 * @author Schlienger
 */
public class ObservationServiceRepository extends AbstractRepository implements IModifyableRepository
{
  /** root item is identified by the null bean */
  private final static ItemBean ROOT_ITEM = null;

  private final Map<String, IRepositoryItem> m_foundItems = new HashMap<String, IRepositoryItem>();

  /**
   * @throws ServiceException
   *           when the underlying service is not available
   */
  public ObservationServiceRepository( final String name, final String factory, final boolean readOnly ) throws RepositoryException 
  {
    super( name, factory, "", readOnly, "observation-service-repository" ); //$NON-NLS-1$ //$NON-NLS-2$

    final IObservationService srv = KalypsoServiceObsActivator.getDefault().getObservationServiceProxy();
    if( srv == null )
      throw new RepositoryException( "Could not find Repository Service" ); //$NON-NLS-1$
  }

  @Override
  public String getDescription()
  {
    return ""; //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#hasChildren()
   */
  public boolean hasChildren() throws RepositoryException
  {
    try
    {
      return KalypsoServiceObsActivator.getDefault().getObservationServiceProxy().hasChildren( ROOT_ITEM );
    }
    catch( final RepositoryException e )
    {
      throw new RepositoryException( e );
    }
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#getChildren()
   */
  public IRepositoryItem[] getChildren() throws RepositoryException
  {
    try
    {
      final IObservationService srv = KalypsoServiceObsActivator.getDefault().getObservationServiceProxy();
      final ItemBean[] beans = srv.getChildren( ROOT_ITEM );

      final IRepositoryItem[] items = new IRepositoryItem[beans.length];

      for( int i = 0; i < items.length; i++ )
        items[i] = new ServiceRepositoryItem( srv, beans[i], null, this );

      /** @hack single repository? skip one hierarchy level and return children of repository item */
      if( items.length == 1 )
        return items[0].getChildren();

      return items;
    }
    catch( final RepositoryException e )
    {
      throw new RepositoryException( e );
    }
  }


  /**
   * @see org.kalypso.repository.IRepository#reload()
   */
  public void reload() throws RepositoryException
  {
    m_foundItems.clear();
    
    try
    {
      KalypsoServiceObsActivator.getDefault().getObservationServiceProxy().reload();
    }
    catch( final RepositoryException e )
    {
      throw new RepositoryException( e );
    }
  }

  /**
   * @see org.kalypso.repository.IRepository#findItem(java.lang.String)
   */
  public IRepositoryItem findItem( final String id ) throws RepositoryException
  {
    if( this.getIdentifier() == id )
      return this;

    final IRepositoryItem[] children = this.getChildren();
    for( final IRepositoryItem child : children )
    {
      final IRepositoryItem item = findItemRecursive( id, child );
      if( item != null )
        return item;
    }

    return null;
  }

  /**
   * Helper: finds item using recursion
   * 
   * @return item if found, otherwise null.
   */
  private IRepositoryItem findItemRecursive( final String id, final IRepositoryItem item ) throws RepositoryException
  {
    IRepositoryItem foundItem = null;

    // first lookup in the cache
    foundItem = m_foundItems.get( id );
    if( foundItem != null )
      return foundItem;

    // either this is the item, or find recursive
    final String identifier = RepositoryUtils.replaceIdentifier( item.getIdentifier(), this.getIdentifier() );
    if( identifier.equals( id ) )
    {
      foundItem = item;
    }
    else
    {
      final IRepositoryItem[] items = item.getChildren();
      for( final IRepositoryItem item2 : items )
      {
        foundItem = findItemRecursive( id, item2 );

        if( foundItem != null )
          break;
      }
    }

    if( foundItem != null )
      m_foundItems.put( id, foundItem );
    
    return foundItem;
  }

  /**
   * @see org.kalypso.repository.IModifyableRepository#createItem(java.lang.String)
   */
  @Override
  public void makeItem( final String identifier ) throws RepositoryException
  {
    final IObservationService srv = KalypsoServiceObsActivator.getDefault().getObservationServiceProxy();
    srv.makeItem( identifier );
  }
}