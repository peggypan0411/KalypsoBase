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
package org.kalypso.repository.virtual;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import org.kalypso.ogc.sensor.IObservation;
import org.kalypso.ogc.sensor.filter.FilterFactory;
import org.kalypso.ogc.sensor.filter.IFilterCreator;
import org.kalypso.ogc.sensor.filter.IObservationFilter;
import org.kalypso.repository.IRepository;
import org.kalypso.repository.IRepositoryItem;
import org.kalypso.repository.IRepositoryItemVisitor;
import org.kalypso.repository.RepositoryException;
import org.kalypso.repository.utils.RepositoryVisitors;
import org.kalypso.zml.filters.AbstractFilterType;

/**
 * VirtualRepositoryItem
 *
 * @author schlienger
 */
public class VirtualRepositoryItem implements IRepositoryItem
{
  private final IRepository m_repository;

  private final String m_name;

  private final String m_itemId;

  private IRepositoryItem m_parent = null;

  private IRepositoryItem[] m_children = new IRepositoryItem[] {};

  private AbstractFilterType m_filterType = null;

  public VirtualRepositoryItem( final IRepository rep, final String name, final String itemId, final VirtualRepositoryItem parent )
  {
    m_repository = rep;
    m_name = name;
    m_itemId = itemId;
    m_parent = parent;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#getName()
   */
  @Override
  public String getName( )
  {
    return m_name;
  }

  /**
   * Returns
   *
   * <pre>
   * vrep://&lt;item_id&gt;
   * </pre>
   *
   * .
   *
   * @see org.kalypso.repository.IRepositoryItem#getIdentifier()
   */
  @Override
  public String getIdentifier( )
  {
    return getRepository().getIdentifier() + m_itemId;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#getParent()
   */
  @Override
  public IRepositoryItem getParent( )
  {
    return m_parent;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#hasChildren()
   */
  @Override
  public boolean hasChildren( )
  {
    return m_children != null && m_children.length > 0;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#getChildren()
   */
  @Override
  public IRepositoryItem[] getChildren( )
  {
    return m_children;
  }

  public void setChildren( final List<IRepositoryItem> children )
  {
    m_children = children.toArray( new IRepositoryItem[children.size()] );
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#getRepository()
   */
  @Override
  public IRepository getRepository( )
  {
    return m_repository;
  }

  /**
   * Sets the filter type. If valid, this allows this item to be adapted into an IObservation.
   *
   * @param filterType
   */
  public void setFilterType( final AbstractFilterType filterType )
  {
    m_filterType = filterType;
  }

  @Override
  public Object getAdapter( final Class anotherClass )
  {
    if( m_filterType != null && anotherClass == IObservation.class )
    {
      try
      {
        final IFilterCreator creator = FilterFactory.getCreatorInstance( m_filterType );

        final IObservationFilter filter = creator.createFilter( m_filterType, null, null );

        return filter;
      }
      catch( final Exception e ) // generic exception caught for simplicity
      {
        e.printStackTrace();
        throw new UndeclaredThrowableException( e );
      }
    }

    return null;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#hasAdapter(java.lang.Class)
   */
  @Override
  public boolean hasAdapter( final Class< ? > adapter )
  {
    if( m_filterType != null && adapter == IObservation.class )
    {
      return true;
    }

    return false;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#isMultipleSourceItem()
   */
  @Override
  public boolean isMultipleSourceItem( )
  {
    return false;
  }

  /**
   * @see org.kalypso.repository.IRepositoryItem#accept(org.kalypso.repository.IRepositoryItemVisitor)
   */
  @Override
  public void accept( final IRepositoryItemVisitor visitor ) throws RepositoryException
  {
    RepositoryVisitors.accept( this, visitor );
  }
}
