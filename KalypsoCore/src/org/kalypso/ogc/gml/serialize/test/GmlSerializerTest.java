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
import java.net.URL;

import org.kalypso.commons.performance.TimeLogger;
import org.kalypso.ogc.gml.serialize.GmlSerializer;
import org.kalypsodeegree.model.feature.GMLWorkspace;

/**
 * @author Gernot Belger
 */
public class GmlSerializerTest
{
  /**
   * Test history<br>
   * <br>
   * Before optimization:<br>
   * - Step 1: 1m10s<br>
   * - Step 2: 1m4s<br>
   * <br>
   * <br>
   * Modification check only after 1 minute:<br>
   * - Step 1: 38s<br>
   * - Step 2: 30s<br>
   * <br>
   * <br>
   * GmlContentHandler uses local cache for Schema-lookup:<br>
   * - Step 1: 33s<br>
   * - Step 2: 25s<br>
   * <br>
   * <br>
   * GmlContentHandler Rechner zuhaus: 83s<br>
   * - Step 1: 49s<br>
   * - Step 2: 34s<br>
   * <br>
   * <br>
   * GmlContentHandler substring nstead of replace all with local xlinks: 75s<br>
   * - Step 1: 44s<br>
   * - Step 2: 30s<br>
   * <br>
   */
// @Test
  public void testLoadPerformance( ) throws Exception
  {
    final URL zipResource = getClass().getResource( "/etc/test/resources/gmlserializer/grandeGml.zip" ); //$NON-NLS-1$
    final String externalForm = zipResource.toExternalForm();
    final URL gmlUrl = new URL( "jar:" + externalForm + "!/discretisation.gml" ); //$NON-NLS-1$ //$NON-NLS-2$

    final TimeLogger timeLogger = new TimeLogger( "GmlSerializer-Test" ); //$NON-NLS-1$
    timeLogger.printCurrentInterim( "%nStep 1: start: " ); //$NON-NLS-1$
    GmlSerializer.createGMLWorkspace( gmlUrl, null );
    timeLogger.takeInterimTime();
    timeLogger.printCurrentInterim( "Step 1: finished: " ); //$NON-NLS-1$
    timeLogger.printCurrentInterim( "Step 2: start: " ); //$NON-NLS-1$
    GmlSerializer.createGMLWorkspace( gmlUrl, null );
    timeLogger.takeInterimTime();
    timeLogger.printCurrentInterim( "Step 2: finished: " ); //$NON-NLS-1$
  }

  /**
   * Test history<br>
   * <br>
   * Before optimization: 17.5s<br>
   * Before optimization: 16.5s<br>
   */
  // Invalid test, depends on KalypsoModel1d2d
// @Test
  public void testSavePerformance( ) throws Exception
  {
// final URL zipResource = getClass().getResource( "resources/dgm2m.zip" );
// final String externalForm = zipResource.toExternalForm();
// final URL gmlUrl = new URL( "jar:" + externalForm + "!/dgm2m.gml" );
    final URL zipResource = getClass().getResource( "/etc/test/resources/gmlserializer/grandeGml.zip" ); //$NON-NLS-1$
    final String externalForm = zipResource.toExternalForm();
    final URL gmlUrl = new URL( "jar:" + externalForm + "!/discretisation.gml" ); //$NON-NLS-1$ //$NON-NLS-2$
// final File outFile = File.createTempFile( "gmlSaveTest", ".gml" );
    final File outFile = new File( "/tmp/gernot.gml" ); //$NON-NLS-1$

    final TimeLogger timeLogger = new TimeLogger( "GmlSerializer-Test" ); //$NON-NLS-1$
    timeLogger.printCurrentInterim( "Loading: " ); //$NON-NLS-1$
    final GMLWorkspace workspace = GmlSerializer.createGMLWorkspace( gmlUrl, null );
    timeLogger.takeInterimTime();
    timeLogger.printCurrentInterim( "Loading finished: " ); //$NON-NLS-1$
    System.out.println( "Saving to file: " + outFile.getAbsolutePath() ); //$NON-NLS-1$
    timeLogger.printCurrentInterim( "Saving 1: " ); //$NON-NLS-1$
    GmlSerializer.serializeWorkspace( outFile, workspace, "UTF-8" ); //$NON-NLS-1$
    timeLogger.takeInterimTime();
    timeLogger.printCurrentInterim( "Saving 1: finished: " ); //$NON-NLS-1$
    timeLogger.printCurrentInterim( "Saving 2: " ); //$NON-NLS-1$
    GmlSerializer.serializeWorkspace( outFile, workspace, "UTF-8" ); //$NON-NLS-1$
    timeLogger.takeInterimTime();
    timeLogger.printCurrentInterim( "Saving 2: finished: " ); //$NON-NLS-1$
  }

}
