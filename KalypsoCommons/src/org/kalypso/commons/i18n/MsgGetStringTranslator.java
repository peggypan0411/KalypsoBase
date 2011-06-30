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
package org.kalypso.commons.i18n;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.kalypso.commons.internal.i18n.Messages;
import org.kalypso.contribs.java.JavaApiContributionsPlugin;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

/**
 * A translator based on the eclipse messages translation handling.<br>
 * Useful for Message-classes that allow acdces to their translations via the 'getString' method.<br>
 * TODO: we should introduce a separate interface for all message classes.
 * 
 * @see org.eclipse.osgi.util.NLS
 * @author Gernot Belger
 */
public class MsgGetStringTranslator implements ITranslator, IExecutableExtension
{
  private Class< ? extends NLS> m_nls;

  private String m_id;

  private List<Element> m_configuration;

  /**
   * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
   *      java.lang.String, java.lang.Object)
   */
  @Override
  public void setInitializationData( final IConfigurationElement config, final String propertyName, final Object data )
  {
    m_id = config.getAttribute( "id" ); //$NON-NLS-1$
  }

  /**
   * @see org.kalypso.contribs.java.lang.I10nTranslator#getId()
   */
  @Override
  public String getId( )
  {
    return m_id;
  }

  /**
   * @see org.kalypso.contribs.java.lang.I10nTranslator#configure(java.util.List)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void configure( final URL context, final List<Element> configuration )
  {
    m_configuration = configuration;

    for( final Element element : configuration )
    {
      final String msgClass = element.getTextContent();
      try
      {
        final String[] split = msgClass.split( ":" ); //$NON-NLS-1$
        Assert.isTrue( split.length == 2, Messages.getString( "org.kalypso.commons.i18n.MsgGetStringTranslator.2", msgClass ) ); //$NON-NLS-1$
        final Bundle bundle = Platform.getBundle( split[0] );
        m_nls = (Class< ? extends NLS>) bundle.loadClass( split[1] );
        return;
      }
      catch( final ClassNotFoundException e )
      {
        final Status status = new Status( IStatus.ERROR, JavaApiContributionsPlugin.getDefault().getBundle().getSymbolicName(), -1, Messages.getString( "org.kalypso.commons.i18n.MsgGetStringTranslator.3", msgClass ), e ); //$NON-NLS-1$
        JavaApiContributionsPlugin.getDefault().getLog().log( status );
        return;
      }
    }

  }

  /**
   * @see org.kalypso.contribs.java.lang.I10nTranslator#getConfiguration()
   */
  @Override
  public List<Element> getConfiguration( )
  {
    return m_configuration;
  }

  @Override
  public String get( final String key )
  {
    if( m_nls == null )
      return Messages.getString( "org.kalypso.commons.i18n.MsgGetStringTranslator.4", key ); //$NON-NLS-1$

    try
    {
      final Method method = m_nls.getMethod( "getString", new Class< ? >[] { String.class } ); //$NON-NLS-1$
      return (String) method.invoke( m_nls, new Object[] { key } );
    }
    catch( final SecurityException e )
    {
      e.printStackTrace();
      return Messages.getString( "org.kalypso.commons.i18n.MsgGetStringTranslator.6", key, e.toString() ); //$NON-NLS-1$ 
    }
    catch( final NoSuchMethodException e )
    {
      e.printStackTrace();
      return Messages.getString( "org.kalypso.commons.i18n.MsgGetStringTranslator.6", key, e.toString() ); //$NON-NLS-1$ 
    }
    catch( final IllegalArgumentException e )
    {
      e.printStackTrace();
      return Messages.getString( "org.kalypso.commons.i18n.MsgGetStringTranslator.6", key, e.toString() ); //$NON-NLS-1$ 
    }
    catch( final IllegalAccessException e )
    {
      e.printStackTrace();
      return Messages.getString( "org.kalypso.commons.i18n.MsgGetStringTranslator.6", key, e.toString() ); //$NON-NLS-1$ 
    }
    catch( final InvocationTargetException e )
    {
      e.printStackTrace();
      return Messages.getString( "org.kalypso.commons.i18n.MsgGetStringTranslator.6", key, e.toString() ); //$NON-NLS-1$ 
    }
  }
}
