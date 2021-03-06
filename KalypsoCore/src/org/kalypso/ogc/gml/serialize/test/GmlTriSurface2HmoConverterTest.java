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
package org.kalypso.ogc.gml.serialize.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.kalypso.commons.java.io.FileUtilities;
import org.kalypso.commons.java.net.UrlUtilities;
import org.kalypso.ogc.gml.serialize.Gml2HmoConverter;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypso.ogc.gml.serialize.GmlTriSurface2HmoConverter;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.GMLWorkspace;
import org.kalypsodeegree.model.geometry.GM_TriangulatedSurface;

/**
 * @author felipe maximino
 */
public class GmlTriSurface2HmoConverterTest extends Assert
{
  @Test
  public void testSerialize( ) throws Exception
  {
    final URL gmlLocation = getClass().getResource( "/etc/test/resources/gmlserializer/tinyTin.gml" ); //$NON-NLS-1$
    assertNotNull( gmlLocation );

    final URL hmoLocation = getClass().getResource( "/etc/test/resources/gmlserializer/tinyTin.hmo" ); //$NON-NLS-1$
    assertNotNull( hmoLocation );

    final GMLWorkspace tinWorkspace = GmlSerializer.createGMLWorkspace( gmlLocation, null );
    final Feature rootFeature = tinWorkspace.getRootFeature();
    final GM_TriangulatedSurface tin = (GM_TriangulatedSurface) rootFeature.getProperty( new QName( "org.kalypso.deegree.gmlparsertest", "triangularSurfaceMember" ) ); //$NON-NLS-1$ //$NON-NLS-2$

    final File result = FileUtilities.createNewUniqueFile( "hmoTest", FileUtilities.TMP_DIR ); //$NON-NLS-1$

    final Gml2HmoConverter conv = new GmlTriSurface2HmoConverter( tin );
    conv.writeHmo( result );

    assertContentEquals( result, hmoLocation );

    result.delete();
  }

  private void assertContentEquals( final File file, final URL location ) throws IOException
  {
    final String fileContent = FileUtils.readFileToString( new File( file.getAbsolutePath() ) );
    final String urlContent = UrlUtilities.toString( location, Charset.defaultCharset().name() );

    final String fileContentClean = StringUtils.replaceChars( fileContent, "\r", null ); //$NON-NLS-1$
    final String urlContentClean = StringUtils.replaceChars( urlContent, "\r", null ); //$NON-NLS-1$

    assertEquals( fileContentClean, urlContentClean );
  }
}
