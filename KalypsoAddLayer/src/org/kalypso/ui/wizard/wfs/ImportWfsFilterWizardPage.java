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
package org.kalypso.ui.wizard.wfs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.ogc.gml.IKalypsoFeatureTheme;
import org.kalypso.ogc.gml.IKalypsoTheme;
import org.kalypso.ogc.gml.filterdialog.model.FeaturePropertyContentProvider;
import org.kalypso.ogc.gml.filterdialog.model.FeaturePropertyLabelProvider;
import org.kalypso.ogc.gml.map.IMapPanel;
import org.kalypso.ogc.gml.mapmodel.IMapModell;
import org.kalypso.ui.editor.mapeditor.GisMapEditor;
import org.kalypso.ui.i18n.Messages;
import org.kalypsodeegree.filterencoding.Filter;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Envelope;
import org.kalypsodeegree.model.geometry.GM_Exception;
import org.kalypsodeegree.model.geometry.GM_Object;
import org.kalypsodeegree.model.geometry.GM_Polygon;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

/**
 * @author kuepfer
 */
public class ImportWfsFilterWizardPage extends WizardPage
{
  Text m_bufferDistance;

  Button m_bufferButton;

  GM_Object m_selectedGeom;

  Button m_activeSelectionButton;

  IMapModell m_gisMapOutlineViewer;

  // GM_Surface m_BBox;

  private Button m_BBoxButton;

  private Combo m_spatialOpsCombo;

  String OPS_INTERSECTION = "Schneidet das Objekt"; //$NON-NLS-1$

  String OPS_CONTAINS = "Enth�lt das Objekt"; //$NON-NLS-1$

  String OPS_TOUCHES = "Ber�hrt das Objekt"; //$NON-NLS-1$

  String m_themeName;

  boolean m_doFilterMaxFeature = false;

  String m_maxFeaturesAsString = "500"; //$NON-NLS-1$

  int m_maxFeaturesAsInt = 500;

  ComboViewer m_geomComboViewer;

  public ImportWfsFilterWizardPage( final String pageName, final String title, final ImageDescriptor titleImage, final IMapModell viewer )
  {
    super( pageName, title, titleImage );
    m_gisMapOutlineViewer = viewer;
  }

  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl( final Composite parent )
  {
    final Composite main = new Composite( parent, SWT.NONE );
    main.setLayout( new GridLayout( 2, true ) );
    main.setLayoutData( new GridData() );
    final Group topGroup = new Group( main, SWT.NONE );
    topGroup.setLayout( new GridLayout( 2, false ) );
    topGroup.setLayoutData( new GridData() );
    m_bufferButton = new Button( topGroup, SWT.CHECK );
    m_bufferButton.setText( "Puffer:" ); //$NON-NLS-1$
    m_bufferButton.addSelectionListener( new SelectionAdapter()
    {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        if( m_bufferButton.getSelection() )
        {
          m_bufferDistance.setEditable( true );
          updateMessage();
        }
        else
          m_bufferDistance.setEditable( false );
        setPageComplete( validate() );
      }
    } );
    m_bufferDistance = new Text( topGroup, SWT.SINGLE | SWT.BORDER );
    m_bufferDistance.setText( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.0" ) ); //$NON-NLS-1$
    m_bufferDistance.setEditable( false );
    m_bufferDistance.addFocusListener( new FocusAdapter()
    {
      /**
       * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
       */
      @Override
      public void focusLost( final FocusEvent e )
      {
        setPageComplete( validate() );
      }

    } );
    m_activeSelectionButton = new Button( topGroup, SWT.RADIO );
    m_activeSelectionButton.setText( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.1" ) ); //$NON-NLS-1$
    m_activeSelectionButton.addSelectionListener( new SelectionAdapter()
    {

      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        if( m_activeSelectionButton.getSelection() )
          m_geomComboViewer.getCombo().setEnabled( true );
        else
          m_geomComboViewer.getCombo().setEnabled( false );
        setPageComplete( validate() );
      }
    } );
    final Combo geomCombo = new Combo( topGroup, SWT.FILL | SWT.DROP_DOWN | SWT.READ_ONLY );
    geomCombo.setEnabled( false );
    final GridData data = new GridData( GridData.FILL_HORIZONTAL );
    // data.widthHint = STANDARD_WIDTH_FIELD;
    geomCombo.setLayoutData( data );
    m_geomComboViewer = new ComboViewer( geomCombo );
    m_geomComboViewer.setLabelProvider( new FeaturePropertyLabelProvider() );
    m_geomComboViewer.setContentProvider( new FeaturePropertyContentProvider() );
    m_geomComboViewer.addFilter( new GeometryPropertyFilter() );
    m_geomComboViewer.add( getSelectedFeatureProperties() );
    m_geomComboViewer.addSelectionChangedListener( new ISelectionChangedListener()
    {

      @Override
      public void selectionChanged( final SelectionChangedEvent event )
      {
        final ISelection selection = event.getSelection();
        if( selection instanceof IStructuredSelection )
        {
          final IStructuredSelection ss = (IStructuredSelection)selection;
          m_selectedGeom = (GM_Object)ss.getFirstElement();
        }
        setPageComplete( validate() );
      }
    } );
    m_BBoxButton = new Button( topGroup, SWT.RADIO );
    m_BBoxButton.setText( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.2" ) ); //$NON-NLS-1$
    m_BBoxButton.addSelectionListener( new SelectionAdapter()
    {

      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        getBBoxFromActiveMap();
        setPageComplete( validate() );
      }
    } );
    final GridData data1 = new GridData();
    data1.horizontalSpan = 2;
    m_BBoxButton.setLayoutData( data1 );

    final Button button = new Button( topGroup, SWT.CHECK );
    button.setText( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.3" ) ); //$NON-NLS-1$
    button.setSelection( m_doFilterMaxFeature );

    final Text maxFeatureField = new Text( topGroup, SWT.BORDER );
    maxFeatureField.setText( m_maxFeaturesAsString );
    final GridData data2 = new GridData( GridData.FILL_HORIZONTAL );
    data2.grabExcessHorizontalSpace = true;
    maxFeatureField.setLayoutData( data2 );
    maxFeatureField.addFocusListener( new FocusAdapter()
    {

      @Override
      public void focusLost( final FocusEvent e )
      {
        m_maxFeaturesAsString = maxFeatureField.getText();
        setPageComplete( validate() );
      }

    } );

    button.addSelectionListener( new SelectionAdapter()
    {

      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        m_doFilterMaxFeature = !m_doFilterMaxFeature;
        maxFeatureField.setEnabled( m_doFilterMaxFeature );
      }
    } );

    final Group spatialOpsGroup = new Group( main, SWT.HORIZONTAL );
    spatialOpsGroup.setLayout( new GridLayout( 2, false ) );
    spatialOpsGroup.setLayoutData( new GridData() );
    spatialOpsGroup.setText( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.4" ) ); //$NON-NLS-1$
    final Label opsLabel = new Label( spatialOpsGroup, SWT.NONE );
    opsLabel.setText( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.5" ) ); //$NON-NLS-1$
    m_spatialOpsCombo = new Combo( spatialOpsGroup, SWT.READ_ONLY );
    m_spatialOpsCombo.addSelectionListener( new SelectionAdapter()
    {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        setPageComplete( validate() );
      }
    } );
    m_spatialOpsCombo.setItems( new String[] { OPS_INTERSECTION, OPS_CONTAINS, OPS_TOUCHES } );
    m_spatialOpsCombo.select( 0 );
    setControl( main );
    setPageComplete( validate() );

  }

  protected void updateMessage( )
  {
    String message = ""; //$NON-NLS-1$
    if( m_activeSelectionButton.getSelection() )
      message = message + Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.6", m_themeName ); //$NON-NLS-1$
    if( m_BBoxButton.getSelection() )
      message = message + Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.8" ); //$NON-NLS-1$
    if( m_bufferButton.getSelection() )
      message = message + Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.7", m_bufferDistance.getText() ); //$NON-NLS-1$
    setMessage( message );

  }

  boolean validate( )
  {
    // validate maxFeatures
    if( m_doFilterMaxFeature )
    {
      try
      {
        m_maxFeaturesAsInt = Integer.parseInt( m_maxFeaturesAsString );
        if( m_maxFeaturesAsInt < 1 )
        {
          setErrorMessage( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.9" ) ); //$NON-NLS-1$
          return false;
        }
      }
      catch( final Exception ex )
      {
        setErrorMessage( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.10" ) ); //$NON-NLS-1$
        return false;
      }
    }
    // the text window can only hold numbers
    if( m_bufferDistance.isVisible() && m_bufferButton.getSelection() )
    {
      try
      {
        final String text = m_bufferDistance.getText();
        Double.parseDouble( text );
        // Integer.parseInt( text );
      }
      catch( final NumberFormatException e )
      {
        setErrorMessage( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.11" ) ); //$NON-NLS-1$
        m_bufferDistance.setText( "" ); //$NON-NLS-1$
        return false;
      }
      if( !m_BBoxButton.getSelection() && !m_activeSelectionButton.getSelection() )
      {
        setErrorMessage( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.12" ) ); //$NON-NLS-1$
        return false;
      }
    }
    // the selected Object can not be null and must be of type GM_Object
    if( m_activeSelectionButton.getSelection() )
    {
      if( m_selectedGeom == null )
      {
        setErrorMessage( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.13" ) ); //$NON-NLS-1$
        return false;
      }
    }
    // the bbox can not be null
    if( m_BBoxButton.getSelection() )
    {
      if( getBBoxFromActiveMap() == null )
      {
        setErrorMessage( Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.14" ) ); //$NON-NLS-1$
        return false;
      }
    }

    // // the selection bbox and active selection is not valid, all other combinations are OK.
    // if( m_BBoxButton.getSelection() && m_activeSelectionButton.getSelection() )
    // {
    // // TODO use radio buttons (@christoph)
    // setErrorMessage( "Es kann nur die BBOX-Option ODER eine die active Selektion-Option ausgew�hlt sein und nicht
    // beides gleichzeitig" );
    // return false;
    // }
    updateMessage();
    setErrorMessage( null );
    return true;
  }

  Filter getFilter( )
  {
    // TODO check checkboxes, maybe no filter at all is wanted
// final int selectionIndex = m_spatialOpsCombo.getSelectionIndex();
// final String item = m_spatialOpsCombo.getItem( selectionIndex );
// int ops = -1;
// if( item.equals( OPS_CONTAINS ) )
// ops = OperationDefines.CONTAINS;
// if( item.equals( OPS_INTERSECTION ) )
// ops = OperationDefines.INTERSECTS;
// if( item.equals( OPS_TOUCHES ) )
// ops = OperationDefines.TOUCHES;
// TODO w�hlen des Poperties wenn mehrere gibt, zur Zeit wird nur das defautltGeometryProperty genommen
// dies muss mit einer TreeView gel�st werden -> Label und ContentProvider anpassen
// final IFeatureType ft = null; // layer.getFeatureType();
// if( ft == null )
    return null;
// final IValuePropertyType geom = ft.getDefaultGeometryProperty();
// if( geom == null )
// return null;
// final PropertyName propertyName = new PropertyName( geom.getQName() );
// // check if buffer is selected
// final String val;
// if( !m_bufferButton.getSelection() )
//      val = "0.0"; //$NON-NLS-1$
// else
// val = m_bufferDistance.getText();
// final double distance = Double.parseDouble( val );
//
// if( m_selectedGeom != null )
// {
// final Geometry jtsGeom = JTSAdapter.export( m_selectedGeom );
// final Geometry jtsBufferedGeom = jtsGeom.buffer( distance );
// final SpatialOperation operation = new SpatialOperation( ops, propertyName, JTSAdapter.wrap( jtsBufferedGeom ),
// distance );
// return new ComplexFilter( operation );
// }
// else if( m_BBoxButton.getSelection() )
// {
//
// final Geometry jtsGeom = JTSAdapter.export( getBBoxFromActiveMap() );
// final Geometry jtsBufferedGeom = jtsGeom.buffer( distance );
// final SpatialOperation operation = new SpatialOperation( ops, propertyName, JTSAdapter.wrap( jtsBufferedGeom ),
// distance );
// return new ComplexFilter( operation );
// }
// return null;
  }

  public boolean doFilterMaxFeatures( )
  {
    return m_doFilterMaxFeature;
  }

  public int getMaxFeatures( )
  {
    return m_maxFeaturesAsInt;
  }

  private Object[] getSelectedFeatureProperties( )
  {
    m_themeName = Messages.getString( "org.kalypso.ui.wizard.wfs.ImportWfsFilterWizardPage.15" ); //$NON-NLS-1$
    if( m_gisMapOutlineViewer == null )
      return new Object[0];
    final IMapModell mapModell = m_gisMapOutlineViewer;

    final IKalypsoTheme activeTheme = mapModell.getActiveTheme();
    if( activeTheme instanceof IKalypsoFeatureTheme )
    {
      final Object firstElement = ((IKalypsoFeatureTheme)activeTheme).getSelectionManager().getFirstElement();
      m_themeName = activeTheme.getLabel();
      if( firstElement instanceof Feature )
      {
        final Feature feature = (Feature)firstElement;
        final IFeatureType featureType = feature.getFeatureType();
        final IPropertyType[] properties = featureType.getProperties();
        final List<Object> list = new ArrayList<>();
        for( final IPropertyType pt : properties )
        {
          if( pt instanceof IValuePropertyType )
          {
            final Object object = feature.getProperty( pt );
            if( object != null )
              list.add( object );
          }
        }
        return list.toArray();
      }
    }
    return new Object[0];
  }

  GM_Polygon getBBoxFromActiveMap( )
  {
    final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    // if this Wizard is activated we assume there is always a map (GisMapEditor) open.
    final IEditorPart activeEditor = activePage.getActiveEditor();
    if( activeEditor instanceof GisMapEditor )
    {
      final GisMapEditor gisMapEditor = (GisMapEditor)activeEditor;
      final IMapPanel mapPanel = gisMapEditor.getMapPanel();
      final GM_Envelope boundingBox = mapPanel.getBoundingBox();
      if( boundingBox != null )
      {
        try
        {
          return GeometryFactory.createGM_Surface( boundingBox, mapPanel.getMapModell().getCoordinatesSystem() );
        }
        catch( final GM_Exception ex )
        {
          ex.printStackTrace();
          setPageComplete( validate() );
        }
      }
    }
    return null;
  }

  class GeometryPropertyFilter extends ViewerFilter
  {

    /**
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select( final Viewer viewer, final Object parentElement, final Object element )
    {
      return GeometryUtilities.isGeometry( element );
    }

  }
}
