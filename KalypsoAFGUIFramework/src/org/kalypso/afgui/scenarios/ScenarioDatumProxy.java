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
package org.kalypso.afgui.scenarios;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.kalypso.afgui.model.IModel;
import org.kalypso.contribs.eclipse.core.runtime.StatusUtilities;
import org.osgi.framework.Bundle;

/**
 * @author kurzbach
 */
public class ScenarioDatumProxy implements IScenarioDatum
{
  private static final String ATTRIBUTE_MODEL_PATH = "modelPath"; //$NON-NLS-1$

  public static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$

  public static final String ATTRIBUTE_CLASS_KEY = "classKey"; //$NON-NLS-1$

  private final IConfigurationElement m_element;

  public ScenarioDatumProxy( final IConfigurationElement element )
  {
    m_element = element;
  }

  /**
   * @see org.kalypso.afgui.scenarios.IScenarioDatum#getID()
   */
  @Override
  public String getID( )
  {
    // REMARK: For backwards compability: we use the clas-name if no id is given.
    final String id = m_element.getAttribute( ATTRIBUTE_ID );
    if( id == null )
      return m_element.getAttribute( ATTRIBUTE_CLASS_KEY );

    return id;
  }

  /**
   * @see org.kalypso.afgui.scenarios.IScenarioDatum#getModelClass()
   */
  @Override
  @SuppressWarnings("unchecked")
  public Class< ? extends IModel> getModelClass( ) throws CoreException
  {
    final String name = m_element.getContributor().getName();
    final Bundle bundle = Platform.getBundle( name );
    try
    {
      final String classKey = m_element.getAttribute( ATTRIBUTE_CLASS_KEY );
      final Class< ? extends IModel> clazz = bundle.loadClass( classKey );
      return clazz;
    }
    catch( final InvalidRegistryObjectException e )
    {
      throw new CoreException( StatusUtilities.statusFromThrowable( e ) );
    }
    catch( final ClassNotFoundException e )
    {
      throw new CoreException( StatusUtilities.statusFromThrowable( e ) );
    }
  }

  /**
   * @see org.kalypso.afgui.scenarios.IScenarioDatum#getModelPath()
   */
  @Override
  public String getModelPath( )
  {
    final String modelPath = m_element.getAttribute( ATTRIBUTE_MODEL_PATH );
    return modelPath;
  }

}
