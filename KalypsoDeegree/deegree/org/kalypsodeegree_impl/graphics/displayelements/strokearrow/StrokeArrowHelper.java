/** This file is part of kalypso/deegree.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * history:
 * 
 * Files in this package are originally taken from deegree and modified here
 * to fit in kalypso. As goals of kalypso differ from that one in deegree
 * interface-compatibility to deegree is wanted but not retained always. 
 * 
 * If you intend to use this software in other ways than in kalypso 
 * (e.g. OGC-web services), you should consider the latest version of deegree,
 * see http://www.deegree.org .
 *
 * all modifications are licensed as deegree, 
 * original copyright:
 *
 * Copyright (C) 2001 by:
 * EXSE, Department of Geography, University of Bonn
 * http://www.giub.uni-bonn.de/exse/
 * lat/lon GmbH
 * http://www.lat-lon.de
 */
package org.kalypsodeegree_impl.graphics.displayelements.strokearrow;

import java.util.Map;

import org.kalypsodeegree.graphics.sld.CssParameter;
import org.kalypsodeegree.graphics.sld.ParameterValueType;
import org.kalypsodeegree.graphics.sld.Stroke;

/**
 * @author Dirk Kuch
 */
public final class StrokeArrowHelper
{
  public StrokeArrowHelper( )
  {
    throw new UnsupportedOperationException();
  }

  public static enum ARROW_TYPE
  {
    line,
    segment;

    public static ARROW_TYPE getType( final CssParameter param )
    {
      final ParameterValueType value = param.getValue();
      final Object[] components = value.getComponents();
      if( components.length != 1 )
        throw new IllegalStateException( "StrokeArrowType consists only of one parameter." );

      final String type = ((String) components[0]).toLowerCase();
      return ARROW_TYPE.valueOf( type );
    }
  }

  public static enum ARROW_WIDGET
  {
    open,
    fill;

    public static ARROW_WIDGET getType( final CssParameter param )
    {
      final ParameterValueType value = param.getValue();
      final Object[] components = value.getComponents();
      if( components.length != 1 )
        return ARROW_WIDGET.open; // default value!

      final String type = ((String) components[0]).toLowerCase();
      return ARROW_WIDGET.valueOf( type );
    }
  }

  public static enum ARROW_ALIGNMENT
  {
    start,
    middle,
    end;

    public static ARROW_ALIGNMENT getType( final CssParameter param )
    {
      final ParameterValueType value = param.getValue();
      final Object[] components = value.getComponents();
      if( components.length != 1 )
        throw new IllegalStateException( "StrokeArrowAligment can only consists about one parameter." );

      final String type = ((String) components[0]).toLowerCase();

      return ARROW_ALIGNMENT.valueOf( type );
    }
  }

  public static final String STROKE_WIDTH = "stroke-width";

  public static final String STROKE_ARROW_TYPE = "stroke-arrow-type";

  public static final String STROKE_ARROW_WIDGET = "stroke-arrow-widget";

  public static final String STROKE_ARROW_ALIGNMENT = "stroke-arrow-alignment";

  public static final String STROKE_ARROW_SIZE = "stroke-arrow-size";

  public static boolean isArrowDefined( final Stroke stroke )
  {
    if( stroke == null )
      return false;

    final Object object = stroke.getParameter( STROKE_ARROW_TYPE );
    if( object != null )
      return true;

    return false;
  }

  public static ARROW_TYPE getArrowType( final Map< ? , ? > cssParameters )
  {
    final CssParameter csType = (CssParameter) cssParameters.get( STROKE_ARROW_TYPE );

    return ARROW_TYPE.getType( csType );
  }

  public static ARROW_ALIGNMENT getArrowAlignment( final Map< ? , ? > cssParameters )
  {
    final CssParameter csType = (CssParameter) cssParameters.get( STROKE_ARROW_ALIGNMENT );

    return ARROW_ALIGNMENT.getType( csType );
  }

  public static Double getArrowSize( final Map< ? , ? > cssParameters )
  {
    final CssParameter csType = (CssParameter) cssParameters.get( STROKE_ARROW_SIZE );

    final ParameterValueType value = csType.getValue();
    final Object[] components = value.getComponents();
    if( components.length != 1 )
      throw new IllegalStateException( "StrokeArrowAligment can only consists about one parameter." );

    return Double.valueOf( (String) components[0] );
  }

  public static Double getStrokeWidth( final Map< ? , ? > cssParameters )
  {
    final CssParameter csType = (CssParameter) cssParameters.get( STROKE_WIDTH );
    if( csType == null )
      return 1.0;

    final ParameterValueType value = csType.getValue();
    final Object[] components = value.getComponents();
    if( components.length != 1 )
      throw new IllegalStateException( "StrokeWidth can only consists about one parameter." );

    return Double.valueOf( (String) components[0] );
  }

  public static ARROW_WIDGET getArrowWidget( final Map< ? , ? > cssParameters )
  {
    final CssParameter csType = (CssParameter) cssParameters.get( STROKE_ARROW_WIDGET );

    return ARROW_WIDGET.getType( csType );
  }
}
