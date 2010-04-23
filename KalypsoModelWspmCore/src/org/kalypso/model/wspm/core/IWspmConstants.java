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
package org.kalypso.model.wspm.core;

/**
 * Contains constants for the wspm models.
 * 
 * @author thuel2
 */
public interface IWspmConstants extends IWspmPhenomenonConstants
{
  public static String NS_WSPM = "org.kalypso.model.wspm"; //$NON-NLS-1$

  public static String NS_WSPMCOMMONS = "org.kalypso.model.wspmcommon"; //$NON-NLS-1$

  public static String NS_WSPMPROF = "org.kalypso.model.wspmprofile"; //$NON-NLS-1$

  public static String NS_WSPMPROF_ASSIGNMENT = "org.kalypso.model.wspmprofile.assignment"; //$NON-NLS-1$

  public static String NS_WSPMRUNOFF = "org.kalypso.model.wspmrunoff"; //$NON-NLS-1$

  public static String NS_WSPMPROJ = "org.kalypso.model.wspmproj"; //$NON-NLS-1$

  public static String NS_NA_WSPM = "org.kalypso.model.wspm.nawspm"; //$NON-NLS-1$

  public static String NS_WSPM_BREAKLINE = "org.kalypso.model.wspmbreakline"; //$NON-NLS-1$

  public static String NS_WSPM_BOUNDARY = "org.kalypso.model.wspmboundary"; //$NON-NLS-1$

  public static final String POINT_PROPERTY = "urn:ogc:gml:dict:kalypso:model:wspm:profilePointComponents#"; //$NON-NLS-1$

  public static final String POINT_PROPERTY_BEWUCHS_AX = POINT_PROPERTY + "BEWUCHS_AX"; //$NON-NLS-1$

  public static final String POINT_PROPERTY_BEWUCHS_AY = POINT_PROPERTY + "BEWUCHS_AY"; //$NON-NLS-1$

  public static final String POINT_PROPERTY_BEWUCHS_DP = POINT_PROPERTY + "BEWUCHS_DP"; //$NON-NLS-1$

  public static final String POINT_PROPERTY_BREITE = POINT_PROPERTY + "BREITE"; //$NON-NLS-1$

  public static final String POINT_PROPERTY_HOCHWERT = POINT_PROPERTY + "HOCHWERT"; //$NON-NLS-1$

  public static final String POINT_PROPERTY_HOEHE = POINT_PROPERTY + "HOEHE"; //$NON-NLS-1$

  public static final String POINT_PROPERTY_COMMENT = POINT_PROPERTY + "COMMENT"; //$NON-NLS-1$

// Die Id im Dictionary bleibt "Rauheit" wird aber im IProfil als Typ "ks" interpretiert
  public static final String POINT_PROPERTY_RAUHEIT_KS = POINT_PROPERTY + "RAUHEIT"; //$NON-NLS-1$

  public static final String POINT_PROPERTY_RAUHEIT_KST = POINT_PROPERTY + "RAUHEIT_KST"; //$NON-NLS-1$

  public static final String POINT_PROPERTY_RECHTSWERT = POINT_PROPERTY + "RECHTSWERT"; //$NON-NLS-1$

  public static final String URN_OGC_GML_DICT_KALYPSO_MODEL_WSPM_COMPONENTS = "urn:ogc:gml:dict:kalypso:model:wspm:components"; //$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY = URN_OGC_GML_DICT_KALYPSO_MODEL_WSPM_COMPONENTS + "#";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_RUNOFF = LENGTH_SECTION_PROPERTY + "LengthSectionRunOff";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_STATION = LENGTH_SECTION_PROPERTY + "LengthSectionStation";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_TYPE = LENGTH_SECTION_PROPERTY + "LengthSectionProfileType";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_GROUND = LENGTH_SECTION_PROPERTY + "LengthSectionGround";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_BOE_LI = LENGTH_SECTION_PROPERTY + "LengthSection_Boe_li";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERT_BOE_RE = LENGTH_SECTION_PROPERTY + "LengthSection_Boe_re";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_WEIR_OK = LENGTH_SECTION_PROPERTY + "LengthSection_WeirOK";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_BRIDGE_OK = LENGTH_SECTION_PROPERTY + "LengthSection_BridgeOK";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_BRIDGE_UK = LENGTH_SECTION_PROPERTY + "LengthSection_BridgeUK";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_TEXT = LENGTH_SECTION_PROPERTY + "LengthSectionText";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_BRIDGE_WIDTH = LENGTH_SECTION_PROPERTY + "LengthSection_BridgeWidth";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_ROHR_DN = LENGTH_SECTION_PROPERTY + "LengthSection_RohrDN";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_WATERLEVEL = LENGTH_SECTION_PROPERTY + "LengthSectionWaterlevel";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_H_BV = LENGTH_SECTION_PROPERTY + "LengthSection_h_BV";//$NON-NLS-1$

  public static final String LENGTH_SECTION_PROPERTY_V_M = LENGTH_SECTION_PROPERTY + "LengthSection_v_m";//$NON-NLS-1$

  /** Constant for coordinate-system of profile rw/hw. For example, it will be set by the GML-Profile converter. */
  public static final String PROFIL_PROPERTY_CRS = "COORDINATE_SYSTEM"; //$NON-NLS-1$

  // needed by IObservation Interface
  public static final String PROFIL_DESCRIPTION = NS_WSPMPROF + "_DESCRIPTION"; //$NON-NLS-1$

  public static final String PROFIL_PROPERTY_COMMENT = NS_WSPMPROF + "_COMMENT"; //$NON-NLS-1$
}
