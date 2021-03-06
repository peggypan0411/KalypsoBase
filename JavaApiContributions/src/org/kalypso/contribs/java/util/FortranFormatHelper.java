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
package org.kalypso.contribs.java.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FortranFormatHelper
{
  public final static Pattern patternBrackets = Pattern.compile( "[\\(|\\)]" );

  final static Pattern pFortranFormat = Pattern.compile( "(a|A|i|I|f|F)([0-9]*)\\.?([0-9]*)" );

  public final static Pattern pSpaceFormat = Pattern.compile( "_*" );

  public final static Pattern pPairFormat = Pattern.compile( "^.+,.+$" );

  final static String textRegExp = "[a-zA-Z0-9_\\., \\(\\)-�������]";

  final static String freeFormat = "\\s*[\\\\a-zA-Z0-9_:\\.-]+\\s*";

  final static String decimalPoint = "[ \\.]";

  final static String decimalValue = "[0-9 -]";

  final static DecimalFormat decimalFormat;
  static
  {
    // TODO remove this dirty hack with java 1.5 and use java printf instead
    final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    dfs.setDecimalSeparator( '.' );
    decimalFormat = new DecimalFormat( "#################################0.0###########################################" );
    decimalFormat.setDecimalSeparatorAlwaysShown( true );
    decimalFormat.setDecimalFormatSymbols( dfs );
  }

  /**
   * @param formatLine
   *          description of line format with fortran-coding <br>
   *          (key,fortran format code) <br>
   *          "_" : space <br>
   *          "*" : free format <br>
   *          "i5" : integer 5 decimals <br>
   *          "f5.2" : float 5 character and 2 decimal places <br>
   *          examples: <br>
   *          "(pns,a1)_(langzeit,a)_(kurzzeit,a)_(faktn,f5.2)"
   *          "(vsg,f5.3)(anzlayy,i5)_____(bimax,f5.1)_____(bianf,f5.1)(izkn_vers,i5)_____(tint,f5.1)_____(rintmx,f5.1)"
   *          "(cinh,*)_(cind,*)_(cex,*)_(bmax,*)_(banf,*)_(fko,*)_(retlay,*)_(evalay,*)"
   *          "(f_eva,f4.2)_(aint,f3.1)__(aigw,f6.2)____(fint,f4.2)____(ftra,f4.2)"
   * @param line
   *          line to scan
   * @return HasMap that contains the values, key as given in formatLine(String),value is scaned value(String)
   */
  public static HashMap<String, String> scanf( final String formatLine, String line ) throws Exception
  {
    final HashMap<String, String> result = new HashMap<>();
    line = line + "                                                ";
    line = line.replaceAll( "\\t", " " );
    final List<String> nameCollector = new ArrayList<>();
    final StringBuffer pattern = new StringBuffer( "^" );
    final String[] formats = patternBrackets.split( formatLine );
    for( final String format : formats )
    {
      final String regExp = getRegExp( format, nameCollector );
      pattern.append( regExp );
    }
    pattern.append( "\\s*$" );
    final Pattern linePattern = Pattern.compile( pattern.toString() );
    final Matcher m = linePattern.matcher( line );
    if( !m.matches() )
    {
      throw new Exception( "parsingexception " + "\n format:" + formatLine + "\nline:" + line + "\nregExp:" + pattern.toString() + "\n  does not match" );
    }

    for( int i = 0; i < m.groupCount(); i++ )
    {
      final String name = nameCollector.get( i );
      final String value = m.group( i + 1 ).trim();
      result.put( name, value );
      // propCollector.put( name, FeatureFactory.createFeatureProperty( name,
      // value ) );
    }
    return result;
  }

  private static String getRegExp( final String string, final List<String> nameCollector )
  {
    Matcher m = pPairFormat.matcher( string );
    if( m.matches() )
      return "(" + getPairRegExp( string, nameCollector ) + ")";
    m = pSpaceFormat.matcher( string );
    if( m.matches() )
    {
      return string.replace( '_', ' ' );
    }
    throw new UnsupportedOperationException();
  }

  private static String getPairRegExp( final String fPair, final List<String> nameCollector )
  {
    if( "".equals( fPair ) )
      return "";
    final String[] s = fPair.split( "," );
    nameCollector.add( s[0] ); // PropertyName

    final String format = s[1];

    if( "*".equals( format ) || "a".equals( format ) || "A".equals( format ) )
      return freeFormat;
    final Matcher m = pFortranFormat.matcher( format );
    if( m.matches() )
    {
      final String type = m.group( 1 );
      String regExpChar = "";
      if( "aA".indexOf( type ) >= 0 )
        regExpChar = textRegExp;
      if( "iIfF".indexOf( type ) >= 0 )
        regExpChar = decimalValue;

      final String charMax = m.group( 2 ); // gesamt anzahl stellen
      final String decimalPlace = m.group( 3 ); // Nachkommastellen

      if( "".equals( decimalPlace ) )
      {
        return regExpChar + "{1," + charMax + "}";
      }
      final int max = Integer.parseInt( charMax );
      final int decimal = Integer.parseInt( decimalPlace );
      return regExpChar + "{1," + (max - decimal - 1) + "}" + decimalPoint + regExpChar + "{" + decimal + "}";
    }
    throw new UnsupportedOperationException();
  }

  public static String createFormatLine( final String name, final String format, final String separator, final int repeats )
  {
    // (ngwzu,*)
    if( repeats < 1 )
      return "";
    final StringBuffer b = new StringBuffer( "(" + name + "" + 0 + "," + format + ")" );
    if( repeats > 1 )
    {
      for( int i = 1; i < repeats; i++ )
      {
        b.append( separator + "(" + name + "" + i + "," + format + ")" );
      }
    }
    return b.toString();
  }

  /**
   * @param format
   *          fortran format
   * @param value
   *          to print
   * @return formated String according to format
   */
  public static String printf( final Double value, final String format )
  {
    // TODO remove this dirty hack with java 1.5 and use java printf instead
    return printf( decimalFormat.format( value.doubleValue() ), format );
  }

  public static String printf( final int value, final String format )
  {
    // TODO remove this dirty hack with java 1.5 and use java printf instead
    return printf( Integer.toString( value ), format );
  }

  /**
   * @param format
   *          fortran format
   * @param value
   *          to print
   * @return formated String according to format
   */
  public static String printf( String value, final String format )
  {
    if( "*".equals( format ) || "a".equals( format ) || "A".equals( format ) )
      return value;
    final Matcher m = FortranFormatHelper.pFortranFormat.matcher( format );
    if( m.matches() )
    {
      final String type = m.group( 1 );
      final String charMax = m.group( 2 ); // gesamt anzahl stellen
      final int max = Integer.parseInt( charMax );
      final String decimalPlace = m.group( 3 ); // Nachkommastellen
      int decimal;
      final StringBuffer result = new StringBuffer( "" );
      for( int i = 0; i < max; i++ )
        result.append( " " );
      if( value == null || "".equals( value ) )
        return result.toString();
      if( !"".equals( decimalPlace ) )
        decimal = Integer.parseInt( decimalPlace );
      else
      {
        if( "aA".indexOf( type ) >= 0 ) // TEXT
        {
          return result.replace( 0, value.length(), value ).toString();
        }
        decimal = 0;
      }

      boolean found = false;
      while( !found )
      {
        final int pointPos = value.indexOf( '.' );
        if( pointPos < 0 && decimal > 0 ) // da fehlt ein Komma
        {
          value = value + ".0";
          continue;
        }
        if( pointPos < 0 && decimal == 0 )
        {
          found = true;
          continue;
        }
        final int points = value.length() - pointPos - 1;
        if( points == decimal )
        {
          found = true;
          continue;
        }

        if( points > decimal ) // zuviele Nachkommastellen
        {
          value = value.substring( 0, value.length() - 1 );
          continue;
        }
        if( points < decimal ) // zuwenig Nachkommastellen
        {
          value = value + "0";
          continue;
        }

      }

      value = value.trim();
      // TODO hier ist ein Problem bez�glich Vorkommastellen, da diese Classe beim Umstieg auf 1.5 eh
      // wegf�llt habe ich nur die variable start eingef�gt und um -1 verkleinert da der dezimalpunkt im
      // String auch als Charakter gez�hlt wird aber in der Formatangabe (z.B. f10.3) nicht!
      int start = max - value.length();
      if( start < 0 )
        start = 0;
      return result.replace( start, max, value ).toString();
    }
    throw new UnsupportedOperationException();

  }

  public static String printf( final double value, final String format )
  {
    return printf( decimalFormat.format( value ), format );
  }
}