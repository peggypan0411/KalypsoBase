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
package org.kalypso.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.kalypso.contribs.eclipse.jface.preference.ComboStringFieldEditor;
import org.kalypso.contribs.java.util.TimezoneUtilities;
import org.kalypso.core.KalypsoCorePlugin;
import org.kalypso.core.KalypsoCorePreferences;
import org.kalypso.core.preferences.IKalypsoCorePreferences;
import org.kalypso.grid.GeoGridUtilities;
import org.kalypso.grid.GeoGridUtilities.Interpolation;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.KalypsoDeegreePreferences;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing
 * <samp>FieldEditorPreferencePage </samp>, we can use the field support built into JFace that allows us to create a
 * page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main plug-in class. That way, preferences can be accessed directly via the preference
 * store.
 */
public class KalypsoGeneralPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
  private ComboStringFieldEditor m_timeZoneEditor;

  private IntegerFieldEditor m_rasterPixelEditor;

  private ComboFieldEditor m_rasterInterpolationEditor;

  private BooleanFieldEditor m_wheelPositionEditor;

  private BooleanFieldEditor m_wheelInvertEditor;

  public KalypsoGeneralPreferencePage( )
  {
    super( GRID );

    setPreferenceStore( KalypsoCorePreferences.getStore() );
    setDescription( Messages.getString( "org.kalypso.ui.preferences.KalypsoGeneralPreferencePage.0" ) ); //$NON-NLS-1$
  }

  @Override
  public void init( final IWorkbench workbench )
  {
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
   * types of preferences. Each field editor knows how to save and restore itself.
   */
  @Override
  public void createFieldEditors( )
  {
    // fetch list of timezone names and sort it
    final String[] ids = TimezoneUtilities.getSupportedTimezones();

    final String label = Messages.getString( "org.kalypso.ui.preferences.KalypsoGeneralPreferencePage.3" ); //$NON-NLS-1$
    final String tooltipText = Messages.getString( "org.kalypso.ui.preferences.KalypsoGeneralPreferencePage.4" ); //$NON-NLS-1$
    m_timeZoneEditor = new ComboStringFieldEditor( IKalypsoCorePreferences.DISPLAY_TIMEZONE, label, tooltipText, getFieldEditorParent(), false, ids );

    /* raster painting */
    final Group rasterPaintingGroup = new Group( getFieldEditorParent(), SWT.NONE );
    rasterPaintingGroup.setText( Messages.getString( "KalypsoGeneralPreferencePage.0" ) ); //$NON-NLS-1$
    rasterPaintingGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 2, 1 ) );

    final Label rasterPaintingGroupLabel = new Label( rasterPaintingGroup, SWT.WRAP );
    rasterPaintingGroupLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    rasterPaintingGroupLabel.setText( Messages.getString( "KalypsoGeneralPreferencePage.1" ) ); //$NON-NLS-1$

    final String rasterPixelLabel = Messages.getString( "KalypsoGeneralPreferencePage.2" ); //$NON-NLS-1$
    m_rasterPixelEditor = new IntegerFieldEditor( KalypsoDeegreePreferences.SETTING_RASTER_PAINTING_PIXEL_RESOLUTION, rasterPixelLabel, rasterPaintingGroup );
    m_rasterPixelEditor.setValidRange( 1, 10 );

    final String rasterInterpolationLabel = Messages.getString( "KalypsoGeneralPreferencePage.3" ); //$NON-NLS-1$
    final String[][] interpolationNamesAndValues = getInterpolationNamesAndValues();
    m_rasterInterpolationEditor = new ComboFieldEditor( KalypsoDeegreePreferences.SETTING_RASTER_PAINTING_INTERPOLATION_METHOD, rasterInterpolationLabel, interpolationNamesAndValues, rasterPaintingGroup );

    // Gruml... field editors change layout of parent...
    GridLayoutFactory.swtDefaults().numColumns( 2 ).applyTo( rasterPaintingGroup );

    /* other map options */
    final Group generalMapOptionsGroup = new Group( getFieldEditorParent(), SWT.NONE );
    generalMapOptionsGroup.setText( Messages.getString("KalypsoGeneralPreferencePage.4") ); //$NON-NLS-1$

    final String wheelPositionLabel = Messages.getString("KalypsoGeneralPreferencePage.5"); //$NON-NLS-1$
    m_wheelPositionEditor = new BooleanFieldEditor( IKalypsoCorePreferences.PREFERENCE_MAP_KEEP_POSITION_ON_MOUSE_WHEEL, wheelPositionLabel, generalMapOptionsGroup );

    final String wheelInvertLabel = Messages.getString("KalypsoGeneralPreferencePage.6"); //$NON-NLS-1$
    m_wheelInvertEditor = new BooleanFieldEditor( IKalypsoCorePreferences.PREFERENCE_MAP_INVERT_MOUSE_WHEEL_ZOOM, wheelInvertLabel, generalMapOptionsGroup );

    // Gruml... field editors change layout of parent...
    GridLayoutFactory.swtDefaults().numColumns( 1 ).applyTo( generalMapOptionsGroup );

    /* register all fields */
    addField( m_timeZoneEditor );
    addField( m_rasterPixelEditor );
    addField( m_rasterInterpolationEditor );
    addField( m_wheelPositionEditor );
    addField( m_wheelInvertEditor );
  }

  private String[][] getInterpolationNamesAndValues( )
  {
    final Collection<String[]> namesAndValues = new ArrayList<>();

    final Interpolation[] interpolations = GeoGridUtilities.Interpolation.values();
    for( final Interpolation interpolation : interpolations )
    {
      final String name = interpolation.toString();
      final String value = interpolation.name();
      namesAndValues.add( new String[] { name, value } );
    }

    return namesAndValues.toArray( new String[namesAndValues.size()][] );
  }

  @Override
  protected void initialize( )
  {
    super.initialize();

    // Reinitialize editor on stores that are NOT the default store

    m_rasterPixelEditor.setPreferenceStore( KalypsoDeegreePreferences.getStore() );
    m_rasterPixelEditor.load();

    m_rasterInterpolationEditor.setPreferenceStore( KalypsoDeegreePreferences.getStore() );
    m_rasterInterpolationEditor.load();
  }

  @Override
  public boolean performOk( )
  {
    final boolean result = super.performOk();

    // even if on shutdown the preferences are saved, we save them in case of a platfrom crash
    KalypsoCorePlugin.getDefault().savePluginPreferences();
    KalypsoDeegreePlugin.getDefault().savePluginStore();

    return result;
  }
}