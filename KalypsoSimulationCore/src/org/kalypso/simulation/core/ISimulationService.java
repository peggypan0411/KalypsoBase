/*--------------- Kalypso-Header --------------------------------------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 Technical University Hamburg-Harburg (TUHH)
 Institute of River and coastal engineering
 Denickestr. 22
 21073 Hamburg, Germany
 http://www.tuhh.de/wb

 and

 Bjoernsen Consulting Engineers (BCE)
 Maria Trost 3
 56070 Koblenz, Germany
 http://www.bjoernsen.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 E-Mail:
 belger@bjoernsen.de
 schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ---------------------------------------------------------------------------------------------------*/
package org.kalypso.simulation.core;

import javax.activation.DataHandler;

import org.eclipse.core.resources.IContainer;

/**
 * <p>
 * Der {@link ISimulationService} ist ein Dienst um Berechnungen auszuf�hren.
 * </p>
 * <p>
 * Das ganze funktioniert �hnlich wie eine Druckerwarteschlange. Ein Auftrag wird gesendet und in eine Liste gestellt,
 * welche (normalerweise der Reihe nach) abgearbeitet wird. Jeder Auftrag erh�lt dabei eine eindeutige ID. Ist ein
 * Auftrag fertig kann das Ergebnis abgeholt werden.
 * 
 * @author belger
 */
public interface ISimulationService
{
  /**
   * Gibt die IDs aller unterst�tzten JobTypen zur�ck.
   * 
   * @throws SimulationServiceException
   */
  public String[] getJobTypes( ) throws SimulationException;

  /**
   * Gibt zur�ck, welche Eingaben f�r einen bestimmten Job-Typ ben�tigt werden.
   * 
   * @throws SimulationServiceException
   */
  public SimulationDescription[] getRequiredInput( final String typeID ) throws SimulationException;

  /**
   * Gibt zur�ck, welche Ergebnisse von einem bestimmten Job-Typ geliefert werden.
   * 
   * @throws SimulationServiceException
   */
  public SimulationDescription[] getDeliveringResults( final String typeID ) throws SimulationException;

  /**
   * Gibt den aktuellen Zustand aller vorhandenen {@link ICalcJob}zur�ck
   * 
   * @throws SimulationServiceException
   */
  public SimulationInfo[] getJobs( ) throws SimulationException;

  /** Gibt den aktuellen Zustand eines einzelnen {@link ICalcJob}zur�ck */
  public SimulationInfo getJob( final String jobID ) throws SimulationException;

  /**
   * <p>
   * Erzeugt und startet einen neuen Auftrag (Job).
   * </p>
   * <p>
   * Auftrag wird in eine Warteliste gestellt und (abh�ngig von der Implementation) baldm�glichst abgearbeitet.
   * </p>
   * 
   * @param typeID
   *          Rechentyp des neuen Auftrags.
   * @param description
   *          Menschenlesbare Beschreibung des Auftrags.
   * @param zipHandler
   *          Die eigentlichen Eingangsdaten f�r den Job. Der Inhalt muss ein JAR-Archiv sein.
   * @param input
   *          Die Eingabedateien, die der Client zur Verf�gung stellt. Die relativen Pfade innerhalb der Benas beziehen
   *          sich auf Pfade innerhalb des Archivs.
   */
  public SimulationInfo startJob( final String typeID, final String description, final DataHandler zipHandler, final SimulationDataPath[] input, final SimulationDataPath[] output ) throws SimulationException;

  /**
   * Stoppt einen Auftrag
   */
  public void cancelJob( final String jobID ) throws SimulationException;

  /**
   * Gibt die Ergebnisse eines Jobs zur�ck. Die Funktion kann jederzeit aufgerufen werden, es werden alle im Moment
   * verf�gbaren Ergebnisse zur�ckgeschickt. Der Job muss lediglich daf�r sorgen, dass die Ergebnisse, die er angegeben
   * hat, auch tats�chlich da sind.
   * 
   * @param jobID
   *          Die id des Jobs, dessen Ergebnise geholt werden sollen.
   * @return Die Ergebnisse als ZIP Archiv, sollte in die Rechenvariante als Hauptverzeichnis entpackt werden.
   * @throws SimulationServiceException
   */
  public void transferCurrentResults( final IContainer targetFolder, final String jobID ) throws SimulationException;

  /**
   * Gibt zur�ck, welche Ergebniss (IDs) zur Zeit vorliegen.
   */
  public String[] getCurrentResults( final String jobID ) throws SimulationException;

  /**
   * <p>
   * L�scht einen Auftrag und alle tempor�r angelegten Daten vollst�ndig.
   * <p>
   * <p>
   * Darf nur aufgerufen werden, wenn der Job gecanceled oder fertig ist
   * </p>
   */
  public void disposeJob( final String jobID ) throws SimulationException;
}
