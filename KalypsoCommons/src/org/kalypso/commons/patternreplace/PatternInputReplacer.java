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
package org.kalypso.commons.patternreplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.kalypso.commons.internal.i18n.Messages;
import org.kalypso.contribs.eclipse.swt.widgets.MenuButton;

/**
 * @author Gernot Belger
 */
public class PatternInputReplacer<T>
{
  public static final String STR_INSERT_A_TOKEN_FROM_THE_LIST_OF_AVAILABLE_PATTERNS = Messages.getString( "PatternInputReplacer_0" ); //$NON-NLS-1$

  private final Map<String, IPatternInput<T>> m_replacers = new LinkedHashMap<>();

  private final String m_patternStart;

  private final String m_patternStop;

  private Pattern m_tokenPattern;

  public PatternInputReplacer( )
  {
    this( "<", ">" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public PatternInputReplacer( final String patternStart, final String patternStop )
  {
    m_patternStart = patternStart;
    m_patternStop = patternStop;

    final String pattern = String.format( "%s(.*?)(:(.*?))?%s", Pattern.quote( m_patternStart ), Pattern.quote( m_patternStop ) ); //$NON-NLS-1$

    m_tokenPattern = Pattern.compile( pattern );
  }

  public void addReplacer( final IPatternInput<T> replacer )
  {
    final String token = replacer.getToken();
    m_replacers.put( token, replacer );
  }

  public IPatternInput<T>[] getReplacer( )
  {
    return (IPatternInput<T>[])m_replacers.values().toArray( new IPatternInput< ? >[m_replacers.size()] );
  }

  public String getMessage( )
  {
    try( final Formatter formatter = new Formatter() )
    {
      // FIXME: how to show params?
      for( final IPatternInput< ? extends T> token : m_replacers.values() )
      {
        if( token.getShowInMenu() )
          formatter.format( "%s%s%s: %s%n", m_patternStart, token.getToken(), m_patternStop, token.getLabel() ); //$NON-NLS-1$
      }

      return formatter.toString();
    }
  }

  public String replaceTokens( final String pattern, final T context )
  {
    // FIXME: use StrSubstituor (commons-lang) instead.

    if( pattern == null )
      return null;

    final StringBuffer result = new StringBuffer();

    final Matcher matcher = createPatternMatcher( pattern );
    while( matcher.find() )
    {
      final IPatternInput<T> tokenReplacer = getMatchedTokenReplacer( matcher );
      final String params = getMatchedParameters( matcher );

      if( tokenReplacer == null )
      {
        // Append the (non-replaced) pattern itself, so we do not change the content
        final String group = matcher.group();
        matcher.appendReplacement( result, Matcher.quoteReplacement( group ) );
      }
      else
      {
        final String replacement = tokenReplacer.getReplacement( context, params );
        matcher.appendReplacement( result, Matcher.quoteReplacement( replacement ) );
      }
    }

    matcher.appendTail( result );

    return result.toString();
  }

  protected String getMatchedParameters( final Matcher matcher )
  {
    return matcher.group( 3 );
  }

  protected IPatternInput<T> getMatchedTokenReplacer( final Matcher matcher )
  {
    final String token = matcher.group( 1 );
    return getReplacer( token );
  }

  protected Matcher createPatternMatcher( final String pattern )
  {
    return m_tokenPattern.matcher( pattern );
  }

  protected IPatternInput<T> getReplacer( final String token )
  {
    return m_replacers.get( token );
  }

  public IContributionItem[] asContributionItems( final Text text )
  {
    final Collection<IContributionItem> items = new ArrayList<>();

    for( final IPatternInput<T> pattern : m_replacers.values() )
    {
      if( pattern.getShowInMenu() )
      {
        final String label = pattern.getLabel();
        final String token = pattern.getToken();
        final String replacement = formatPattern( token );
        items.add( new ActionContributionItem( new PatternAction( label, replacement, text ) ) );
      }
    }

    return items.toArray( new IContributionItem[items.size()] );
  }

  public String formatPattern( final String token )
  {
    return String.format( "%s%s%s", m_patternStart, token, m_patternStop ); //$NON-NLS-1$
  }

  /**
   * Creates a menu that insert pattern into the given text field.
   */
  public MenuManager createPatternMenu( final Text text )
  {
    final MenuManager menuManager = new MenuManager();

    final IContributionItem[] items = asContributionItems( text );
    for( final IContributionItem item : items )
      menuManager.add( item );

    return menuManager;
  }

  /**
   * Creates a menu-button that insert pattern into the given text field.
   */
  public Button createPatternButton( final Composite parent, final Text text )
  {
    final MenuManager menuManager = createPatternMenu( text );
    menuManager.setRemoveAllWhenShown( false );

    final Button button = new Button( parent, SWT.ARROW | SWT.LEFT );
    button.setToolTipText( STR_INSERT_A_TOKEN_FROM_THE_LIST_OF_AVAILABLE_PATTERNS );

    new MenuButton( button, menuManager );

    return button;
  }
}