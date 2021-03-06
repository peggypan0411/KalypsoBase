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
package org.kalypso.core.gml.provider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Gernot Belger
 */
public class GmlSourceContentProvider implements ITreeContentProvider
{
  private final Map<IGmlSourceProvider, ITreeContentProvider> m_contentProvider = new LinkedHashMap<>();

  private final Map<Object, IGmlSourceProvider> m_providerHash = new LinkedHashMap<>();

  private IGmlSourceProvider[] m_provider;

  @Override
  public void dispose( )
  {
    for( final ITreeContentProvider cp : m_contentProvider.values() )
      cp.dispose();
  }

  @Override
  public Object[] getElements( final Object inputElement )
  {
    Assert.isTrue( inputElement == m_provider );

    // add all elements
    final List<Object> result = new ArrayList<>();
    for( final Entry<IGmlSourceProvider, ITreeContentProvider> entry : m_contentProvider.entrySet() )
    {
      final IGmlSourceProvider provider = entry.getKey();
      final ITreeContentProvider cp = entry.getValue();
      final Object[] elements = cp.getElements( provider );
      for( final Object object : elements )
      {
        result.add( object );
        m_providerHash.put( object, provider );
      }
    }

    return result.toArray( new Object[result.size()] );
  }

  @Override
  public Object[] getChildren( final Object parentElement )
  {
    final IGmlSourceProvider provider = getProvider( parentElement );
    final ITreeContentProvider cp = m_contentProvider.get( provider );
    final Object[] children = cp.getChildren( parentElement );
    for( final Object object : children )
      m_providerHash.put( object, provider );

    return children;
  }

  /**
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
   */
  @Override
  public Object getParent( final Object element )
  {
    final ITreeContentProvider cp = getContentProvider( element );
    return cp.getParent( element );
  }

  /**
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
   */
  @Override
  public boolean hasChildren( final Object element )
  {
    final ITreeContentProvider cp = getContentProvider( element );
    return cp.hasChildren( element );
  }

  /**
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
   *      java.lang.Object)
   */
  @Override
  public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput )
  {
    m_provider = (IGmlSourceProvider[]) newInput;

    m_contentProvider.clear();

    if( m_provider == null )
      return;

    // create content provider for each provider
    for( final IGmlSourceProvider provider : m_provider )
    {
      final ITreeContentProvider cp = provider.createContentProvider();
      m_contentProvider.put( provider, cp );
      cp.inputChanged( viewer, null, provider ); // provider is input; what else could it be?
    }

  }

  public IGmlSource getSource( final Object element )
  {
    final IGmlSourceProvider provider = getProvider( element );
    return provider.createSource( element );
  }

  public IGmlSourceProvider getProvider( final Object element )
  {
    if( !m_providerHash.containsKey( element ) )
      throw new IllegalStateException( String.format( "Unknown element: %s", element ) ); //$NON-NLS-1$

    return m_providerHash.get( element );
  }

  private ITreeContentProvider getContentProvider( final Object parentElement )
  {
    final IGmlSourceProvider provider = getProvider( parentElement );
    return m_contentProvider.get( provider );
  }

}
