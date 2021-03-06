package org.kalypso.zml.core.table.binding;

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

import java.io.IOException;
import java.net.URL;
import java.util.List;

import jregex.Pattern;
import jregex.RETokenizer;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.kalypso.contribs.eclipse.swt.graphics.RGBUtilities;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.zml.core.table.schema.CellStyleType;
import org.kalypso.zml.core.table.schema.StylePropertyName;
import org.kalypso.zml.core.table.schema.StylePropertyType;

/**
 * @author Dirk Kuch
 */
public class CellStyle implements Cloneable
{
  private static final ColorRegistry COLOR_REGISTRY = new ColorRegistry();

  private static final FontRegistry FONT_REGISTRY = new FontRegistry();

  private static final ImageRegistry IMAGE_REGISTRY = new ImageRegistry();

  private final CellStyleType m_style;

  public CellStyle( final CellStyleType style )
  {
    m_style = init( style );
  }

  public CellStyleType getType( )
  {
    return m_style;
  }

  @Override
  public CellStyle clone( )
  {
    return new CellStyle( TableTypes.cloneStyleType( m_style ) );
  }

  /**
   * Initialize style (cascading style set!)
   */
  private CellStyleType init( final CellStyleType style )
  {
    final CellStyleType base = TableTypes.resolveReference( style.getBaseStyle() );
    if( base == null )
      return style;

    return merge( base, style );
  }

  public static CellStyleType merge( final CellStyleType... styles )
  {
    final CellStyleType base = new CellStyleType();

    for( final CellStyleType style : styles )
    {
      final List<StylePropertyType> properties = style.getProperty();
      for( final StylePropertyType property : properties )
      {
        final StylePropertyType clone = TableTypes.cloneProperty( property );
        if( !hasProperty( base, property ) )
        {
          base.getProperty().add( clone );
        }
        else
        {
          final String name = TableTypes.getPropertyName( property );
          final StylePropertyName targetName = StylePropertyName.fromValue( name );

          final StylePropertyType targetProperty = TableTypes.findPropertyType( base, targetName );
          targetProperty.setValue( clone.getValue() );
        }
      }

      base.setId( mergeId( base.getId(), style.getId() ) );
    }

    return base;
  }

  private static String mergeId( final String id1, final String id2 )
  {
    if( id1 == null )
      return id2;
    else if( id2.startsWith( "urn" ) ) //$NON-NLS-1$
    {
      final RETokenizer tokenizer = new RETokenizer( new Pattern( ".*#" ), id2 ); //$NON-NLS-1$
      final String anchor = tokenizer.nextToken();
      return String.format( "%s#%s", id1, anchor ); //$NON-NLS-1$
    }

    return String.format( "%s#%s", id1, id2 ); //$NON-NLS-1$
  }

  public static boolean hasProperty( final CellStyleType style, final StylePropertyType type )
  {
    final String name = TableTypes.getPropertyName( type );
    final StylePropertyName property = StylePropertyName.fromValue( name );

    final String value = TableTypes.findProperty( style, property );

    return value != null;
  }

  public Color getBackgroundColor( )
  {
    final String htmlColor = TableTypes.findProperty( m_style, StylePropertyName.BACKGROUND_COLOR );
    if( htmlColor == null )
      return null;

    final String id = "background" + m_style.getId(); //$NON-NLS-1$

    try
    {
      final RGB rgb = RGBUtilities.decodeHtmlColor( htmlColor );
      COLOR_REGISTRY.put( id, rgb );
    }
    catch( final RuntimeException ex )
    {
      System.out.println( String.format( "Illegal background cell style: %s", getIdentifier() ) ); //$NON-NLS-1$

      ex.printStackTrace();
    }

    return COLOR_REGISTRY.get( id );
  }

  public Color getForegroundColor( )
  {
    final String htmlColor = TableTypes.findProperty( m_style, StylePropertyName.TEXT_COLOR );
    if( htmlColor == null )
      return null;

    final String id = "foreground" + m_style.getId(); //$NON-NLS-1$
    final RGB rgb = RGBUtilities.decodeHtmlColor( htmlColor );
    COLOR_REGISTRY.put( id, rgb );

    return COLOR_REGISTRY.get( id );
  }

  public Font getFont( )
  {
    final String fontFamily = TableTypes.findProperty( m_style, StylePropertyName.FONT_FAMILY );
    final String fontSize = TableTypes.findProperty( m_style, StylePropertyName.FONT_SIZE );
    final int fontWeight = TableTypes.toSWTFontWeight( TableTypes.findProperty( m_style, StylePropertyName.FONT_WEIGHT ) );

    final FontData data = new FontData( fontFamily == null ? "Arial" : fontFamily, fontSize == null ? 10 : Integer.valueOf( fontSize ), fontWeight ); //$NON-NLS-1$
    FONT_REGISTRY.put( m_style.getId(), new FontData[] { data } );

    return FONT_REGISTRY.get( m_style.getId() );
  }

  public Image getImage( ) throws IOException
  {
    final String urlString = TableTypes.findProperty( m_style, StylePropertyName.ICON );
    if( urlString == null )
      return null;

    final Image cached = IMAGE_REGISTRY.get( m_style.getId() );
    if( cached != null )
      return cached;

    final String uri = KalypsoCorePlugin.getDefault().getCatalogManager().resolve( urlString, urlString );

    final ImageDescriptor descriptor = ImageDescriptor.createFromURL( new URL( uri ) );
    final Image image = descriptor.createImage();

    IMAGE_REGISTRY.put( m_style.getId(), image );

    return image;
  }

  public String getTextFormat( )
  {
    final String format = TableTypes.findProperty( m_style, StylePropertyName.TEXT_FORMAT );

    return format;
  }

  public String getIdentifier( )
  {
    return m_style.getId();
  }

}
