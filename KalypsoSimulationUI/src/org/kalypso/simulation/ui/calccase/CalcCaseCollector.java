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
package org.kalypso.simulation.ui.calccase;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

/**
 * Collects CalcCases in the Workspace
 *
 * @author tgu
 */
public class CalcCaseCollector implements IResourceVisitor
{
  private final Collection<IResource> m_calcCases = new ArrayList<>();

  private final String m_controlPath;

  public CalcCaseCollector( final String controlPath )
  {
    m_controlPath = controlPath;
  }

  @Override
  public boolean visit( final IResource resource )
  {
    if( resource.getType() == IResource.FOLDER && ModelNature.isCalcCalseFolder( (IFolder) resource, m_controlPath ) )
    {
      m_calcCases.add( resource );
      return false;
    }

    return true;
  }

  /** Clears the collector, so it can be reused. */
  public void clear( )
  {
    m_calcCases.clear();
  }

  /** Returns the visited calc cases since the last {@link #clear()}. */
  public IFolder[] getCalcCases( )
  {
    return m_calcCases.toArray( new IFolder[m_calcCases.size()] );
  }
}