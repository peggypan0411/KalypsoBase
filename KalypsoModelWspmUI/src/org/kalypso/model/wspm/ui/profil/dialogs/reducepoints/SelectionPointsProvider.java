/*----------------    FILE HEADER KALYPSO ------------------------------------------
 *
 *  This file is part of kalypso.
 *  Copyright (C) 2004 by:
 *
 *  Technical University Hamburg-Harburg (TUHH)
 *  Institute of River and coastal engineering
 *  Denickestra�e 22
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
package org.kalypso.model.wspm.ui.profil.dialogs.reducepoints;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.core.profil.wrappers.IProfileRecord;

/**
 * Delivers points from a selection.
 *
 * @author Belger
 */
public class SelectionPointsProvider implements IPointsProvider
{
  private final IProfile m_profil;

  private String m_errorMessage;

  private IProfileRecord[] m_points = new IProfileRecord[] {};

  public SelectionPointsProvider( final IProfile profil, final ISelection selection )
  {
    m_profil = profil;

    if( selection.isEmpty() )
    {
      m_errorMessage = org.kalypso.model.wspm.ui.i18n.Messages.getString( "org.kalypso.model.wspm.ui.profil.dialogs.reducepoints.SelectionPointsProvider.0" ); //$NON-NLS-1$
      return;
    }

    if( selection instanceof IStructuredSelection )
    {
// try
// {
      final IStructuredSelection structSel = (IStructuredSelection) selection;

      if( structSel.size() < 3 )
      {
        m_errorMessage = org.kalypso.model.wspm.ui.i18n.Messages.getString( "org.kalypso.model.wspm.ui.profil.dialogs.reducepoints.SelectionPointsProvider.1" ); //$NON-NLS-1$
        return;
      }

      final Object[] objects = structSel.toArray();

      IProfileRecord lastPoint = null;
      final List<IProfileRecord> points = new ArrayList<>( objects.length );
      for( final Object object : objects )
      {
        if( object instanceof IProfileRecord )
        {
          final IProfileRecord point = (IProfileRecord) object;
          if( lastPoint != null && lastPoint != point.getPreviousPoint() )
          {
            m_errorMessage = org.kalypso.model.wspm.ui.i18n.Messages.getString( "org.kalypso.model.wspm.ui.profil.dialogs.reducepoints.SelectionPointsProvider.2" ); //$NON-NLS-1$
            return;
          }

          points.add( point );
          lastPoint = point;
        }
        else
        {
          m_errorMessage = org.kalypso.model.wspm.ui.i18n.Messages.getString( "org.kalypso.model.wspm.ui.profil.dialogs.reducepoints.SelectionPointsProvider.3" ); //$NON-NLS-1$
          return;
        }
      }

      m_errorMessage = null;
      m_points = points.toArray( new IProfileRecord[points.size()] );
      return;
// }
// catch( final IllegalProfileOperationException e )
// {
// m_errorMessage = "Fehler beim Pr�fen der Selektion: " + e.getLocalizedMessage();
// return;
// }
    }

    m_errorMessage = org.kalypso.model.wspm.ui.i18n.Messages.getString( "org.kalypso.model.wspm.ui.profil.dialogs.reducepoints.SelectionPointsProvider.4" ); //$NON-NLS-1$
  }

  @Override
  public IProfileRecord[] getPoints( )
  {
    return m_points;
  }

  @Override
  public String getErrorMessage( )
  {
    return m_errorMessage;
  }

  @Override
  public String getName( )
  {
    return org.kalypso.model.wspm.ui.i18n.Messages.getString( "org.kalypso.model.wspm.ui.profil.dialogs.reducepoints.SelectionPointsProvider.5" ); //$NON-NLS-1$
  }
}
