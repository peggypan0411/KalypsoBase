/*--------------- Kalypso-Header ------------------------------------------

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

 --------------------------------------------------------------------------*/

package org.kalypso.ogc.gml.featureview.control;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.types.IMarshallingTypeHandler;
import org.kalypso.gmlschema.types.MarshallingTypeRegistrySingleton;
import org.kalypso.ogc.gml.command.ChangeFeatureCommand;
import org.kalypsodeegree.model.feature.Feature;

/**
 * This feature control is a radio button, which just sets the feature-value to the goven value when selected. If
 * unselected, nothing happens.
 * <p>
 * Today only properties with String type are supported.
 * </p>
 * 
 * @author belger
 */
public class RadioFeatureControl extends AbstractFeatureControl
{
  private final SelectionListener m_listener = new SelectionListener()
  {
    @Override
    public void widgetSelected( final SelectionEvent e )
    {
      buttonSelected();
    }

    @Override
    public void widgetDefaultSelected( final SelectionEvent e )
    {
      buttonSelected();
    }
  };

  private final List<ModifyListener> m_listeners = new ArrayList<>( 5 );

  private final Object m_valueToSet;

  private Button m_radio = null;

  private final String m_text;

  public RadioFeatureControl( final IPropertyType ftp, final Object valueToSet, final String text )
  {
    this( null, ftp, valueToSet, text );
  }

  public RadioFeatureControl( final Feature feature, final IPropertyType ftp, final Object valueToSet, final String text )
  {
    super( feature, ftp );

    m_valueToSet = convertValueToSet( ftp, valueToSet );
    m_text = text;
  }

  private Object convertValueToSet( final IPropertyType ftp, final Object valueToSet )
  {
    try
    {
      if( valueToSet instanceof String )
      {
        final IMarshallingTypeHandler typeHandler = MarshallingTypeRegistrySingleton.getTypeRegistry().getTypeHandlerFor( ftp );
        return typeHandler.parseType( (String)valueToSet );
      }

      return null;
    }
    catch( final ParseException e )
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.AbstractFeatureControl#dispose()
   */
  @Override
  public void dispose( )
  {
    if( m_radio != null && !m_radio.isDisposed() )
      m_radio.removeSelectionListener( m_listener );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#createControl(org.eclipse.swt.widgets.Composite, int)
   */
  @Override
  public Control createControl( final Composite parent, final int style )
  {
    final IPropertyType pt = getFeatureTypeProperty();
    m_radio = new Button( parent, style | SWT.RADIO );
    final String text;
    if( m_text != null )
      text = m_text;
    else
      text = pt.getAnnotation().getLabel();

    m_radio.setText( text );
    m_radio.addSelectionListener( m_listener );

    updateControl();

    return m_radio;
  }

  protected void buttonSelected( )
  {
    final Feature feature = getFeature();
    final IPropertyType pt = getFeatureTypeProperty();
    final Object currentFeatureValue = feature.getProperty( pt );

    if( !m_valueToSet.equals( currentFeatureValue ) )
      fireFeatureChange( new ChangeFeatureCommand( feature, pt, m_valueToSet ) );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#updateControl()
   */
  @Override
  public void updateControl( )
  {
    final Object currentFeatureValue = getFeature().getProperty( getFeatureTypeProperty() );
    final boolean toBeSelected = currentFeatureValue.equals( m_valueToSet );
    if( toBeSelected != m_radio.getSelection() )
    {
      m_radio.setSelection( toBeSelected );
    }
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#isValid()
   */
  @Override
  public boolean isValid( )
  {
    // a radio button is always valid
    return true;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#addModifyListener(org.eclipse.swt.events.ModifyListener)
   */
  @Override
  public void addModifyListener( final ModifyListener l )
  {
    m_listeners.add( l );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#removeModifyListener(org.eclipse.swt.events.ModifyListener)
   */
  @Override
  public void removeModifyListener( final ModifyListener l )
  {
    m_listeners.remove( l );
  }
}
