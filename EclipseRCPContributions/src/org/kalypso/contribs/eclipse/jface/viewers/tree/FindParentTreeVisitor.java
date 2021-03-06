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
package org.kalypso.contribs.eclipse.jface.viewers.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Finds the parent of an element in a tree by brute force.
 * 
 * @author Gernot Belger
 */
public class FindParentTreeVisitor implements ITreeVisitor
{
  private final Object m_element;

  private Object m_parent;

  public FindParentTreeVisitor( final Object element )
  {
    m_element = element;
  }

  /**
   * @see org.kalypso.contribs.eclipse.jface.ITreeVisitor#visit(java.lang.Object,
   *      org.eclipse.jface.viewers.ITreeContentProvider)
   */
  @Override
  public boolean visit( final Object parent, final ITreeContentProvider contentprovider ) throws TreeVisiterAbortException
  {
    final Object[] children = contentprovider.getChildren( parent );
    for( final Object object : children )
    {
      if( object == m_element )
      {
        m_parent = parent;
        // We have found what we searched, so stop visiting
        throw new TreeVisiterAbortException();
      }
    }

    return true;
  }

  public Object getParent( )
  {
    return m_parent;
  }

}
