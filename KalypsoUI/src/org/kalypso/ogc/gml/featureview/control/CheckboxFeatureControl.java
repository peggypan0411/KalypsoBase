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
package org.kalypso.ogc.gml.featureview.control;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.kalypso.commons.command.ICommand;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.ogc.gml.command.ChangeFeatureCommand;
import org.kalypso.ogc.gml.featureview.IFeatureModifier;
import org.kalypso.ogc.gml.featureview.modfier.BooleanModifier;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.event.ModellEvent;
import org.kalypsodeegree.model.feature.event.ModellEventListener;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;

/**
 * @author belger
 */
public class CheckboxFeatureControl extends AbstractFeatureControl implements ModellEventListener
{
  private Button m_checkbox = null;

  private final IFeatureModifier m_modifier;

  private final Collection<ModifyListener> m_modlistener = new ArrayList<>();

  private final String m_text;

  public CheckboxFeatureControl( final Feature feature, final GMLXPath propertyPath, final IValuePropertyType ftp, final String text )
  {
    super( feature, ftp );

    m_text = text;

    m_modifier = new BooleanModifier( propertyPath, ftp );
  }

  /**
   * @see org.eclipse.swt.widgets.Widget#dispose()
   */
  @Override
  public void dispose( )
  {
    super.dispose();

    if( m_checkbox != null )
      m_checkbox.dispose();
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#createControl(org.eclipse.swt.widgets.Composite, int)
   */
  @Override
  public Control createControl( final Composite parent, final int style )
  {
    m_checkbox = new Button( parent, style | SWT.CHECK );
    m_checkbox.addSelectionListener( new SelectionListener()
    {
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        fireFeatureChange( getChange() );
        fireModified();
      }

      @Override
      public void widgetDefaultSelected( final SelectionEvent e )
      {
        fireFeatureChange( getChange() );
        fireModified();
      }
    } );

    updateControl();

    return m_checkbox;
  }

  /**
   * Checkbox is always valid
   * 
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#isValid()
   */
  @Override
  public boolean isValid( )
  {
    return true;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#updateControl()
   */
  @Override
  public void updateControl( )
  {
    if( m_checkbox == null || m_checkbox.isDisposed() )
      return;

    final Feature feature = getFeature();
    if( feature != null && getFeatureTypeProperty() != null )
    {
      // compare with old to prevent loop
      final boolean oldValue = m_checkbox.getSelection();
      final Boolean newvalue = (Boolean)m_modifier.getProperty( feature );
      if( newvalue.booleanValue() != oldValue )
        m_checkbox.setSelection( newvalue.booleanValue() );
    }

    if( m_text != null )
      m_checkbox.setText( m_text );
  }

  protected ICommand getChange( )
  {
    final Feature feature = getFeature();

    final Boolean value = Boolean.valueOf( m_checkbox.getSelection() );

    final Object newData = m_modifier.parseInput( getFeature(), value );

    final IPropertyType pt = getFeatureTypeProperty();
    final Object oldData = feature.getProperty( pt );

    // nur �ndern, wenn sich wirklich was ge�ndert hat
    if( newData == null && oldData != null || newData != null && !newData.equals( oldData ) )
      return new ChangeFeatureCommand( feature, pt, newData );

    return null;
  }

  /**
   * @see org.kalypsodeegree.model.feature.event.ModellEventListener#onModellChange(org.kalypsodeegree.model.feature.event.ModellEvent)
   */
  @Override
  public void onModellChange( final ModellEvent modellEvent )
  {
    updateControl();
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#addModifyListener(org.eclipse.swt.events.ModifyListener)
   */
  @Override
  public void addModifyListener( final ModifyListener l )
  {
    m_modlistener.add( l );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#removeModifyListener(org.eclipse.swt.events.ModifyListener)
   */
  @Override
  public void removeModifyListener( final ModifyListener l )
  {
    m_modlistener.remove( l );
  }

  protected void fireModified( )
  {
    for( final ModifyListener l : m_modlistener )
    {
      l.modifyText( null );
    }
  }
}