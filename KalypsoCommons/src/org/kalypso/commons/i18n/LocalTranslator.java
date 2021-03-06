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

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.kalypso.commons.internal.i18n.Messages;
import org.w3c.dom.Element;

/**
 * A translator based on properties files, that are found relative to the given context.
 * 
 * @see org.eclipse.osgi.util.NLS
 * @author Gernot Belger
 */
public class LocalTranslator implements ITranslator, IExecutableExtension
{
  private String m_id;

  private List<Element> m_configuration;

  private ResourceBundle m_bundle;

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
   * @see org.kalypso.commons.i18n.ITranslator#configure(org.kalypso.commons.i18n.ITranslatorContext, java.util.List)
   */
  @Override
  public void configure( final ITranslatorContext context, final List<Element> any )
  {
    m_bundle = ResourceBundleUtils.loadResourceBundle( context.getContext() );
  }

  /**
   * @see org.kalypso.contribs.java.lang.I10nTranslator#getConfiguration()
   */
  @Override
  public List<Element> getConfiguration( )
  {
    return m_configuration;
  }

  /**
   * REMARK: locale is always ignored, as the language is determined when the message class is loaded. It is always the
   * current locale of the eclipse platform.
   * 
   * @see org.kalypso.contribs.java.lang.I10nTranslator#get(java.lang.String, java.util.Locale, java.lang.Object[])
   */
  @Override
  public String get( final String key, final Locale locale, final Object[] context )
  {
    if( m_bundle == null )
      return Messages.getString( "org.kalypso.commons.i18n.LocalTranslator.1", key ); //$NON-NLS-1$

    try
    {
      final String value = m_bundle.getString( key );
      if( value == null || value.isEmpty() )
        return Messages.getString( "org.kalypso.commons.i18n.LocalTranslator.2", key ); //$NON-NLS-1$
      return value;
    }
    catch( final MissingResourceException e )
    {
      return Messages.getString( "org.kalypso.commons.i18n.LocalTranslator.2", key ); //$NON-NLS-1$
    }
  }
}
