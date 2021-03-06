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
package org.kalypso.ogc.gml.gui;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.LabelProvider;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.ogc.gml.featureview.IFeatureChangeListener;
import org.kalypso.ogc.gml.featureview.IFeatureModifier;
import org.kalypso.ogc.gml.featureview.dialog.IFeatureDialog;
import org.kalypso.ogc.gml.featureview.dialog.PointFeatureDialog;
import org.kalypso.ogc.gml.featureview.maker.FeatureviewHelper;
import org.kalypso.ogc.gml.featureview.modfier.BooleanModifier;
import org.kalypso.ogc.gml.featureview.modfier.StringModifier;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.template.featureview.Button;
import org.kalypso.template.featureview.CompositeType;
import org.kalypso.template.featureview.ControlType;
import org.kalypso.template.featureview.GridDataType;
import org.kalypso.template.featureview.GridLayout;
import org.kalypso.template.featureview.ObjectFactory;
import org.kalypso.template.featureview.Text;
import org.kalypso.transformation.transformer.GeoTransformerFactory;
import org.kalypso.transformation.transformer.IGeoTransformer;
import org.kalypsodeegree.KalypsoDeegreePlugin;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.geometry.GM_Point;
import org.kalypsodeegree.model.geometry.GM_Position;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;
import org.kalypsodeegree_impl.model.geometry.GeometryFactory;

/**
 * Gui type handler for gml:envelopes's.
 * 
 * @author Holger Albert
 */
public class Gml3PointGuiTypeHandler extends LabelProvider implements IGuiTypeHandler
{
  public Gml3PointGuiTypeHandler( )
  {
  }

  /**
   * @see org.kalypso.ogc.gml.gui.IGuiTypeHandler#createFeatureDialog(org.kalypsodeegree.model.feature.Feature, org.kalypso.gmlschema.property.IPropertyType)
   */
  @Override
  public IFeatureDialog createFeatureDialog( final Feature feature, final IPropertyType ftp )
  {
    return new PointFeatureDialog( feature, (IValuePropertyType)ftp );
  }

  /**
   * @see org.kalypso.ogc.gml.gui.IGuiTypeHandler#createFeatureviewControl(javax.xml.namespace.QName, org.kalypso.template.featureview.ObjectFactory)
   */
  @Override
  public JAXBElement< ? extends ControlType> createFeatureviewControl( final IPropertyType property, final ObjectFactory factory )
  {
    // // if we get a ClassCastException here, something is very wrong
    // IValuePropertyType vpt = (IValuePropertyType) property;
    //
    // // Enumeration will get a Combo-Box
    // Map<String, String> comboEntries = PropertyUtils.createComboEntries( vpt );
    // if( comboEntries.size() > 0 )
    // return super.createFeatureviewControl( property, factory );

    final QName qname = property.getQName();

    final CompositeType composite = factory.createCompositeType();

    final GridLayout layout = factory.createGridLayout();
    layout.setNumColumns( 2 );
    layout.setMakeColumnsEqualWidth( false );
    layout.setMarginWidth( 1 );
    composite.setLayout( factory.createGridLayout( layout ) );
    composite.setStyle( "SWT.NONE" ); //$NON-NLS-1$

    // Text
    final Text text = factory.createText();
    text.setStyle( "SWT.BORDER" ); //$NON-NLS-1$
    text.setEditable( true );
    text.setProperty( qname );

    final GridDataType textData = factory.createGridDataType();
    textData.setHorizontalAlignment( "GridData.FILL" ); //$NON-NLS-1$
    textData.setGrabExcessHorizontalSpace( true );
    textData.setWidthHint( FeatureviewHelper.STANDARD_TEXT_FIELD_WIDTH_HINT );
    text.setLayoutData( factory.createGridData( textData ) );

    // Knopf
    final Button button = factory.createButton();
    final GridDataType buttonData = factory.createGridDataType();
    button.setStyle( "SWT.PUSH" ); //$NON-NLS-1$
    button.setProperty( qname );

    buttonData.setHorizontalAlignment( "GridData.BEGINNING" ); //$NON-NLS-1$
    button.setLayoutData( factory.createGridData( buttonData ) );

    final List<JAXBElement< ? extends ControlType>> control = composite.getControl();
    control.add( factory.createText( text ) );
    control.add( factory.createButton( button ) );

    return factory.createComposite( composite );
  }

  @Override
  public IFeatureModifier createFeatureModifier( final GMLXPath propertyPath, final IPropertyType ftp, final IFeatureSelectionManager selectionManager, final IFeatureChangeListener fcl, final String format )
  {
    // if we get a ClassCastExxception here, something is very wrong
    final IValuePropertyType vpt = (IValuePropertyType)ftp;

    final Class< ? > valueClass = getValueClass();

    if( Boolean.class == valueClass )
      return new BooleanModifier( propertyPath, vpt );

    return new StringModifier( propertyPath, vpt, format );
  }

  /**
   * @see org.kalypso.gmlschema.types.ITypeHandler#getValueClass()
   */
  @Override
  public Class< ? > getValueClass( )
  {
    return GM_Point.class;
  }

  @Override
  public QName getTypeName( )
  {
    /* This corresponds to the qname, it in defined in GMLConstants. */
    return GM_Point.POINT_ELEMENT;
  }

  /**
   * @see org.kalypso.gmlschema.types.ITypeHandler#isGeometry()
   */
  @Override
  public boolean isGeometry( )
  {
    return false;
  }

  /**
   * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
   */
  @Override
  public String getText( final Object element )
  {
    if( element == null )
      return ""; //$NON-NLS-1$

    final GM_Point point = (GM_Point)element;
    final GM_Position pos = point.getPosition();

    final double[] dbl_values = pos.getAsArray();

    String result = point.getCoordinateSystem();

    for( final double element2 : dbl_values )
      result = result + ";" + new Double( element2 ).toString(); //$NON-NLS-1$

    return result;
  }

  /**
   * @see org.kalypso.ogc.gml.gui.IGuiTypeHandler#fromText(java.lang.String)
   */
  @Override
  public Object parseText( final String text, final String formatHint )
  {
    /* Erstellen des Points. */
    GM_Position pos = null;
    String crs = null;

    /* Werte anhand von ; trennen. */
    final String[] str_values = text.split( ";" ); //$NON-NLS-1$

    double[] dbl_values = null;

    /* Sind mind. zwei Eintr�ge vorhanden (CS und ein double-Wert)? */
    final String kalypsoCrs = KalypsoDeegreePlugin.getDefault().getCoordinateSystem();
    if( str_values.length > 1 )
    {
      /* Der erste Eintrag wird als das CS angenommen. */
      final String cs = str_values[0];

      /* Sollte es das CS nicht geben, stelle das Default CS ein. */
      if( cs != null && cs.length() > 0 )
      {
        crs = cs;

        /* Der erste Eintrag war ein CS. */
        dbl_values = new double[str_values.length - 1];

        for( int i = 1; i < str_values.length; i++ )
          dbl_values[i - 1] = new Double( str_values[i] );
      }
      else
      {
        crs = kalypsoCrs;

        /* Der erste Eintrag war kein CS. */
        dbl_values = new double[str_values.length];

        for( int i = 0; i < str_values.length; i++ )
          dbl_values[i] = new Double( str_values[i] );
      }

      pos = GeometryFactory.createGM_Position( dbl_values );
    }
    else
    {
      /* Falls es nur einen Wert gibt, wird er als CS angenommen. */
      final String cs = str_values[0];

      /* Sollte es das CS nicht geben, stelle das Default CS ein. */
      if( cs != null && cs.length() > 0 )
      {
        crs = cs;
        pos = GeometryFactory.createGM_Position( new double[] { 0.0 } );
      }
      else
      {
        crs = kalypsoCrs;
        pos = GeometryFactory.createGM_Position( new double[] { new Double( str_values[0] ) } );
      }
    }

    final GM_Point point = GeometryFactory.createGM_Point( pos, crs );

    try
    {
      // REMARK: all geometries in memory MUST have the Kalypso CRS, we do transform,
      // regardlesss what the user has entered
      final IGeoTransformer geoTransformer = GeoTransformerFactory.getGeoTransformer( kalypsoCrs );
      return geoTransformer.transform( point );
    }
    catch( final Exception e )
    {
      e.printStackTrace();

      return null;
    }
  }
}