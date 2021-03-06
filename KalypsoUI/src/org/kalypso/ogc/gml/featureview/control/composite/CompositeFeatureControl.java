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
package org.kalypso.ogc.gml.featureview.control.composite;

import javax.xml.bind.JAXBElement;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.kalypso.commons.i18n.ITranslator;
import org.kalypso.contribs.eclipse.swt.SWTUtilities;
import org.kalypso.gmlschema.annotation.IAnnotation;
import org.kalypso.ogc.gml.featureview.control.FeatureComposite;
import org.kalypso.template.featureview.CompositeType;
import org.kalypso.template.featureview.ControlType;
import org.kalypso.template.featureview.LayoutType;

/**
 * @author Gernot Belger
 */
public class CompositeFeatureControl extends AbstractFeatureCompositionControl
{
  private final CompositeType m_compositeType;

  public CompositeFeatureControl( final FeatureComposite featureComposite, final CompositeType compositeType, final IAnnotation annotation, final ITranslator translator )
  {
    super( featureComposite, annotation, translator );

    m_compositeType = compositeType;
  }

  @Override
  public Control createControl( final FormToolkit toolkit, final Composite parent, final int style )
  {
    final Composite composite = createCompositeFromCompositeType( parent, style );
    // composite.setBackground( parent.getDisplay().getSystemColor( (int) (Math.random() * 16) ) );

    if( toolkit != null )
      toolkit.adapt( composite );

    // Layout setzen
    final LayoutType layoutType = m_compositeType.getLayout().getValue();
    if( layoutType != null )
      composite.setLayout( createLayout( layoutType ) );

    for( final JAXBElement< ? extends ControlType> element : m_compositeType.getControl() )
    {
      final ControlType value = element.getValue();
      final int elementStyle = SWTUtilities.createStyleFromString( value.getStyle() );
      createControl( composite, elementStyle, value );
    }

    return composite;
  }

  private Composite createCompositeFromCompositeType( final Composite parent, final int style )
  {
    if( m_compositeType instanceof org.kalypso.template.featureview.Group )
    {
      final Group group = new org.eclipse.swt.widgets.Group( parent, style );

      final String groupControlText = ((org.kalypso.template.featureview.Group)m_compositeType).getText();

      final String groupText = getAnnotation( IAnnotation.ANNO_LABEL, groupControlText );
      group.setText( translate( groupText ) );

      return group;
    }

    return new Composite( parent, style );
  }
}
