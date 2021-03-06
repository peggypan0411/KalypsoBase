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
package org.kalypso.ogc.gml.gui;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.LabelProvider;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.ogc.gml.featureview.IFeatureChangeListener;
import org.kalypso.ogc.gml.featureview.IFeatureModifier;
import org.kalypso.ogc.gml.featureview.dialog.DirectoryFeatureDialog;
import org.kalypso.ogc.gml.featureview.dialog.IFeatureDialog;
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
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.typeHandler.XsdBaseTypeHandler;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;

/**
 * This is a gui type handler for the directory-type in commons.xsd {@link org.kalypsodeegree_impl.gml.schema.schemata.UrlCatalogOGC}.
 * 
 * @author Holger Albert
 */
public class DirectoryGuiTypeHandler extends LabelProvider implements IGuiTypeHandler
{
  private final XsdBaseTypeHandler m_handler;

  public DirectoryGuiTypeHandler( final XsdBaseTypeHandler< ? extends Object> handler )
  {
    m_handler = handler;
  }

  /**
   * @see org.kalypso.ogc.gml.gui.XsdBaseGuiTypeHandler#createFeatureDialog(org.kalypsodeegree.model.feature.Feature, org.kalypso.gmlschema.property.IPropertyType)
   */
  @Override
  public IFeatureDialog createFeatureDialog( final Feature feature, final IPropertyType ftp )
  {
    return new DirectoryFeatureDialog( feature, (IValuePropertyType)ftp );
  }

  /**
   * @see org.kalypso.ogc.gml.gui.XsdBaseGuiTypeHandler#createFeatureviewControl(org.kalypso.gmlschema.property.IPropertyType, org.kalypso.template.featureview.ObjectFactory)
   */
  @Override
  public JAXBElement< ? extends ControlType> createFeatureviewControl( final IPropertyType property, final ObjectFactory factory )
  {
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

  /**
   * @see org.kalypso.ogc.gml.gui.XsdBaseGuiTypeHandler#fromText(java.lang.String)
   */
  @Override
  public Object parseText( final String text, final String formatHint )
  {
    return m_handler.convertToJavaValue( text );
  }

  /**
   * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
   */
  @Override
  public String getText( final Object element )
  {
    return m_handler.convertToXMLString( element );
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
   * @see org.kalypso.gmlschema.types.ITypeHandler#getTypeName()
   */
  @Override
  public QName getTypeName( )
  {
    return m_handler.getTypeName();
  }

  /**
   * @see org.kalypso.gmlschema.types.ITypeHandler#getValueClass()
   */
  @Override
  public Class getValueClass( )
  {
    return m_handler.getValueClass();
  }

  /**
   * @see org.kalypso.gmlschema.types.ITypeHandler#isGeometry()
   */
  @Override
  public boolean isGeometry( )
  {
    return m_handler.isGeometry();
  }
}