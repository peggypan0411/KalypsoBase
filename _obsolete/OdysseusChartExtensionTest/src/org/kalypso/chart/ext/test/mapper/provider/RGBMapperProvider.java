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
package org.kalypso.chart.ext.test.mapper.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.RGB;
import org.kalypso.chart.ext.test.mapper.RGBMapper;

import de.openali.odysseus.chart.factory.config.parameters.impl.RGBParser;
import de.openali.odysseus.chart.factory.provider.AbstractMapperProvider;
import de.openali.odysseus.chart.framework.model.layer.IParameterContainer;

/**
 * @author alibu
 */
public class RGBMapperProvider extends AbstractMapperProvider
{

  /**
   * @see de.openali.odysseus.chart.factory.provider.IMapperProvider#getMapper()
   */
  @Override
  public RGBMapper getMapper( )
  {

    final IParameterContainer pc = getParameterContainer();
    final HashMap<String, String> defaultColorsString = new HashMap<String, String>();
    final int size = 10;
    final int offset = 255 / size;
    for( int i = 0; i < size; i++ )
      defaultColorsString.put( "" + i, "" + offset * i );

    final Map<String, RGB> parsedRGBMap = pc.getParsedParameterMap( "colorMap", defaultColorsString, new RGBParser() );

    final Map<Number, RGB> defaultColors = new HashMap<Number, RGB>();
    final String[] keys = parsedRGBMap.keySet().toArray( new String[] {} );

    for( int i = 0; i < parsedRGBMap.size(); i++ )
      defaultColors.put( Integer.parseInt( keys[i] ), parsedRGBMap.get( keys[i] ) );
    return new RGBMapper( getId(), defaultColors );
  }
}
