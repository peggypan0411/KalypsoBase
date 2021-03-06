/*--------------- Kalypso-Header ----------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 University of Technology Hamburg-Harburg (TUHH)
 Institute of River and Coastal Engineering
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
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact:

 E-Mail:
 g.belger@bjoernsen.de
 m.schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ------------------------------------------------------------------------*/
package org.kalypso.contribs.eclipse.ui.partlistener;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * Empty implementation of {@link org.eclipse.ui.IPartListener2}
 * 
 * @author belger
 */
public class PartAdapter2 implements IPartListener2
{
  /**
   * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
   */
  @Override
  public void partActivated( final IWorkbenchPartReference partRef )
  {
    // does nothing
  }

  /**
   * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
   */
  @Override
  public void partBroughtToTop( final IWorkbenchPartReference partRef )
  {
    // does nothing
  }

  /**
   * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
   */
  @Override
  public void partClosed( final IWorkbenchPartReference partRef )
  {
    // does nothing
  }

  /**
   * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
   */
  @Override
  public void partDeactivated( final IWorkbenchPartReference partRef )
  {
    // does nothing
  }

  /**
   * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
   */
  @Override
  public void partOpened( final IWorkbenchPartReference partRef )
  {
    // does nothing
  }

  /**
   * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
   */
  @Override
  public void partHidden( final IWorkbenchPartReference partRef )
  {
    // does nothing
  }

  /**
   * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
   */
  @Override
  public void partVisible( final IWorkbenchPartReference partRef )
  {
    // does nothing
  }

  /**
   * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
   */
  @Override
  public void partInputChanged( final IWorkbenchPartReference partRef )
  {
    // does nothing
  }
}
