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
package org.kalypso.zml.core.table.binding;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import jregex.Pattern;
import jregex.RETokenizer;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.catalog.ICatalog;
import org.kalypso.zml.core.KalypsoZmlCore;
import org.kalypso.zml.core.i18n.Messages;
import org.kalypso.zml.core.table.ZmlTableConfigurationLoader;
import org.kalypso.zml.core.table.binding.rule.AbstractZmlRule;
import org.kalypso.zml.core.table.binding.rule.ZmlCellRule;
import org.kalypso.zml.core.table.schema.ColumnRuleType;
import org.kalypso.zml.core.table.schema.RuleRefernceType;
import org.kalypso.zml.core.table.schema.RuleSetType;
import org.kalypso.zml.core.table.schema.RuleType;
import org.kalypso.zml.core.table.schema.ZmlTableType;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author Dirk Kuch
 */
public final class ZmlRuleResolver
{
  private final Cache<String, List<ZmlRuleSet>> m_ruleSetCache;

  private final Cache<String, AbstractZmlRule> m_ruleCache;

  private static ZmlRuleResolver INSTANCE;

  private ZmlRuleResolver( )
  {
    final CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder().expireAfterAccess( 30, TimeUnit.MINUTES );
    m_ruleSetCache = builder.build();
    m_ruleCache = builder.build();
  }

  public static ZmlRuleResolver getInstance( )
  {
    if( INSTANCE == null )
      INSTANCE = new ZmlRuleResolver();

    return INSTANCE;
  }

  public AbstractZmlRule findRule( final URL context, final RuleRefernceType reference ) throws CoreException
  {
    try
    {
      final Object ref = reference.getReference();
      if( ref instanceof RuleType )
        return new ZmlCellRule( (RuleType) ref );
      else if( ref instanceof ColumnRuleType )
        throw new UnsupportedOperationException();

      final String url = reference.getUrl();
      if( url != null )
      {
        final AbstractZmlRule cached = getCachedRule( url );
        if( cached != null )
          return cached;

        final String plainUrl = getUrl( url );
        final String identifier = getAnchor( url );

        AbstractZmlRule rule;
        if( plainUrl.startsWith( "urn:" ) ) //$NON-NLS-1$
          rule = findUrnRule( context, plainUrl, identifier );
        else
          rule = findUrlRule( context, plainUrl, identifier );

        // FIXME: what to do if rule null?
        if( rule != null )
          m_ruleCache.put( url, rule );

        return rule;
      }
    }
    catch( final Throwable t )
    {
      throw new CoreException( new Status(IStatus.ERROR, KalypsoZmlCore.PLUGIN_ID, Messages.ZmlRuleResolver_1, t ) );
    }
    throw new IllegalStateException();
  }

  private AbstractZmlRule getCachedRule( final String url )
  {
    // FIXME: we should consider a timeout based on the modification timestamp of the underlying resource here
    // Else, the referenced resource will never be loaded again, even if it has changed meanwhile
    return m_ruleCache.asMap().get( url );
  }

  private AbstractZmlRule findUrlRule( final URL context, final String uri, final String identifier ) throws MalformedURLException, JAXBException
  {
    final URL absoluteUri = new URL( context, uri );

    List<ZmlRuleSet> ruleSets = m_ruleSetCache.asMap().get( uri );
    if( ruleSets == null )
    {
      final ZmlTableConfigurationLoader loader = new ZmlTableConfigurationLoader( absoluteUri );
      final ZmlTableType tableType = loader.getTableType();

      ruleSets = new ArrayList<>();

      for( final RuleSetType ruleSet : tableType.getRuleSet() )
      {
        ruleSets.add( new ZmlRuleSet( ruleSet ) );
      }

      m_ruleSetCache.put( uri, ruleSets );
    }

    for( final ZmlRuleSet ruleSet : ruleSets )
    {
      final AbstractZmlRule rule = ruleSet.find( identifier );
      if( rule != null )
        return rule;
    }

    return null;
  }

  private AbstractZmlRule findUrnRule( final URL context, final String urn, final String identifier ) throws MalformedURLException, JAXBException
  {
    final ICatalog baseCatalog = KalypsoCorePlugin.getDefault().getCatalogManager().getBaseCatalog();
    final String uri = baseCatalog.resolve( urn, urn );

    return findUrlRule( context, uri, identifier );
  }

  private String getUrl( final String url )
  {
    final RETokenizer tokenizer = new RETokenizer( new Pattern( "#.*" ), url ); //$NON-NLS-1$

    return StringUtils.chomp( tokenizer.nextToken() );
  }

  private String getAnchor( final String url )
  {
    final RETokenizer tokenizer = new RETokenizer( new Pattern( ".*#" ), url ); //$NON-NLS-1$

    return StringUtils.chomp( tokenizer.nextToken() );
  }

}
