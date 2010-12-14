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
package org.kalypso.util.themes.legend.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.kalypso.ogc.gml.AbstractCascadingLayerTheme;
import org.kalypso.ogc.gml.IKalypsoTheme;

/**
 * A content provider for kalypso themes.
 * 
 * @author Holger Albert
 */
public class ThemeTableContentProvider implements ITreeContentProvider
{
  /**
   * The input of the viewer.
   */
  private List<IKalypsoTheme> m_input;

  /**
   * The constructor.
   */
  public ThemeTableContentProvider( )
  {
    m_input = null;
  }

  /**
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
   */
  @Override
  public Object[] getChildren( Object parentElement )
  {
    if( m_input == null )
      return new Object[] {};

    if( !(parentElement instanceof AbstractCascadingLayerTheme) )
      return new Object[] {};

    AbstractCascadingLayerTheme theme = (AbstractCascadingLayerTheme) parentElement;

    return theme.getAllThemes();
  }

  /**
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
   */
  @Override
  public Object getParent( Object element )
  {
    if( m_input == null )
      return null;

    if( !(element instanceof IKalypsoTheme) )
      return null;

    IKalypsoTheme theme = (IKalypsoTheme) element;

    return theme.getMapModell();
  }

  /**
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
   */
  @Override
  public boolean hasChildren( Object element )
  {
    if( m_input == null )
      return false;

    if( !(element instanceof AbstractCascadingLayerTheme) )
      return false;

    AbstractCascadingLayerTheme theme = (AbstractCascadingLayerTheme) element;

    return theme.getAllThemes().length > 0;
  }

  /**
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements( Object inputElement )
  {
    if( m_input == null )
      return new Object[] {};

    return m_input.toArray( new IKalypsoTheme[] {} );
  }

  /**
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose( )
  {
    m_input = null;
  }

  /**
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
   *      java.lang.Object)
   */
  @Override
  public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
  {
    /* Reset the old input. */
    m_input = null;

    /* If the new input is not the right type, ignore it. */
    if( newInput == null || !(newInput instanceof List) )
      return;

    /* Store the new input. */
    m_input = (List<IKalypsoTheme>) newInput;
  }
}