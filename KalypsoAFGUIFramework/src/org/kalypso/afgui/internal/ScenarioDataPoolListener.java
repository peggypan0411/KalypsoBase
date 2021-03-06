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
package org.kalypso.afgui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.kalypso.afgui.internal.i18n.Messages;
import org.kalypso.core.util.pool.IPoolListener;
import org.kalypso.core.util.pool.IPoolableObjectType;
import org.kalypsodeegree.model.feature.GMLWorkspace;

import de.renew.workflow.connector.cases.IModel;

final class ScenarioDataPoolListener implements IPoolListener
{
  private final IPoolableObjectType m_key;

  private final Class< ? extends IModel> m_modelClass;

  private final SzenarioDataProvider m_dataProvider;

  public ScenarioDataPoolListener( final SzenarioDataProvider dataProvider, final IPoolableObjectType key, final Class< ? extends IModel> modelClass )
  {
    m_dataProvider = dataProvider;
    m_key = key;
    m_modelClass = modelClass;
  }

  public IPoolableObjectType getKey( )
  {
    return m_key;
  }

  @Override
  public void dirtyChanged( final IPoolableObjectType key, final boolean isDirty )
  {
  }

  @Override
  public boolean isDisposed( )
  {
    return false;
  }

  @Override
  public void objectInvalid( final IPoolableObjectType key, final Object oldValue )
  {
    System.out.format( "Object invalid: %s%n", key ); //$NON-NLS-1$
  }

  @Override
  public void objectLoaded( final IPoolableObjectType key, final Object newValue, final IStatus status )
  {
    if( newValue instanceof GMLWorkspace )
    {
      final GMLWorkspace workspace = (GMLWorkspace) newValue;

      // Adapting directly to IModel is dangerous because the mapping is not unique
      // (for example, 1d2d adapter factory as well as risk adapter factory are registered to adapt Feature to IModel)
      // TODO remove mappings to IModel from the factories

      final IModel model = SzenarioDataProvider.adaptModel( m_modelClass, workspace );
      if( model != null )
      {
        m_dataProvider.fireModelLoaded( model, status );
      }

      // notify user once about messages during loading
      final IWorkbench workbench = PlatformUI.getWorkbench();
      if( workbench != null && !workbench.isClosing() && !status.isOK() )
      {
        final Display display = workbench.getDisplay();
        display.asyncExec( new Runnable()
        {
          @Override
          public void run( )
          {
            final Shell activeShell = display.getActiveShell();
            ErrorDialog.openError( activeShell, Messages.getString( "org.kalypso.afgui.scenarios.SzenarioDataProvider.0" ), Messages.getString( "org.kalypso.afgui.scenarios.SzenarioDataProvider.1" ), status ); //$NON-NLS-1$ //$NON-NLS-2$
          }
        } );
      }
    }
  }
}