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
package org.kalypso.ogc.gml.featureview.control;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.kalypso.commons.command.ICommand;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.gmlschema.property.restriction.IRestriction;
import org.kalypso.gmlschema.property.restriction.MaxExclusiveRestriction;
import org.kalypso.gmlschema.property.restriction.MaxInclusiveRestriction;
import org.kalypso.gmlschema.property.restriction.MinExclusiveRestriction;
import org.kalypso.gmlschema.property.restriction.MinInclusiveRestriction;
import org.kalypso.gmlschema.types.IMarshallingTypeHandler;
import org.kalypso.ogc.gml.command.ChangeFeatureCommand;
import org.kalypsodeegree.model.feature.Feature;

/**
 * @author Gernot Belger
 */
public class SpinnerFeatureControl extends AbstractFeatureControl
{
  private final Collection<ModifyListener> m_modlistener = new HashSet<>();

  private Spinner m_spinner;

  private int m_increment = 1;

  private int m_pageIncrement = 10;

  public SpinnerFeatureControl( final Feature feature, final IValuePropertyType ftp )
  {
    super( feature, ftp );
  }

  @Override
  public void dispose( )
  {
    super.dispose();

    if( m_spinner != null )
      m_spinner.dispose();
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#addModifyListener(org.eclipse.swt.events.ModifyListener)
   */
  @Override
  public void addModifyListener( final ModifyListener l )
  {
    m_modlistener.add( l );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#createControl(org.eclipse.swt.widgets.Composite, int)
   */
  @Override
  public Spinner createControl( final Composite parent, final int style )
  {
    m_spinner = new Spinner( parent, style );

    m_spinner.setIncrement( m_increment );
    m_spinner.setPageIncrement( m_pageIncrement );

    final IValuePropertyType vpt = (IValuePropertyType)getFeatureTypeProperty();

    final int digits = 0;
    final IRestriction[] restrictions = vpt.getRestriction();
// for( final IRestriction restriction : restrictions )
// {
    // TODO: implement fractionDigits restriction!
// }

    int minimum = -1;
    int maximum = -2;
    for( final IRestriction restriction : restrictions )
    {
      if( restriction instanceof MinInclusiveRestriction )
        minimum = (int)Math.round( ((MinInclusiveRestriction)restriction).getMinInclusive() * Math.pow( 10, digits ) );
      if( restriction instanceof MinExclusiveRestriction )
        minimum = (int)Math.round( ((MinExclusiveRestriction)restriction).getMinExclusive() * Math.pow( 10, digits ) );
      if( restriction instanceof MaxInclusiveRestriction )
        maximum = (int)Math.round( ((MaxInclusiveRestriction)restriction).getMaxInclusive() * Math.pow( 10, digits ) );
      if( restriction instanceof MaxExclusiveRestriction )
        maximum = (int)Math.round( ((MaxExclusiveRestriction)restriction).getMaxExclusive() * Math.pow( 10, digits ) );
    }

    m_spinner.setDigits( digits );
    m_spinner.setMinimum( minimum );
    m_spinner.setMaximum( maximum );

    m_spinner.addSelectionListener( new SelectionListener()
    {
      @Override
      public void widgetSelected( final SelectionEvent e )
      {
        fireFeatureChange( getChanges() );
        fireModified();
      }

      @Override
      public void widgetDefaultSelected( final SelectionEvent e )
      {
        fireFeatureChange( getChanges() );
        fireModified();
      }
    } );

    updateControl();

    return m_spinner;
  }

  protected ICommand getChanges( )
  {
    final Feature feature = getFeature();

    final Number newData = getCurrentValue();

    final IPropertyType pt = getFeatureTypeProperty();
    final Object oldData = feature.getProperty( pt );

    // nur �ndern, wenn sich wirklich was ge�ndert hat
    if( newData == null && oldData != null || newData != null && !newData.equals( oldData ) )
      return new ChangeFeatureCommand( feature, pt, newData );

    return null;
  }

  private Number getCurrentValue( )
  {
    final int value = m_spinner.getSelection();

    final IValuePropertyType vpt = (IValuePropertyType)getFeatureTypeProperty();
    final IMarshallingTypeHandler handler = vpt.getTypeHandler();
    try
    {
      return (Number)handler.parseType( Integer.toString( value ) );
    }
    catch( final ParseException e )
    {
      e.printStackTrace();

      return null;
    }
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#isValid()
   */
  @Override
  public boolean isValid( )
  {

    // TODO Auto-generated method stub
    return false;
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#removeModifyListener(org.eclipse.swt.events.ModifyListener)
   */
  @Override
  public void removeModifyListener( final ModifyListener l )
  {
    m_modlistener.remove( l );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.control.IFeatureControl#updateControl()
   */
  @Override
  public void updateControl( )
  {
    if( m_spinner == null || m_spinner.isDisposed() )
      return;

    final Feature feature = getFeature();
    if( feature != null && getFeatureTypeProperty() != null )
    {
      // compare with old to prevent loop
      final Number oldValue = getCurrentValue();

      final Number newValue = (Number)feature.getProperty( getFeatureTypeProperty() );

      if( !ObjectUtils.equals( oldValue, newValue ) )
      {
        final double doubleValue = newValue == null ? 0.0 : newValue.doubleValue();
        // TODO: better handling of null
        final double valueToSet = doubleValue * Math.pow( 10, m_spinner.getDigits() );
        m_spinner.setSelection( (int)valueToSet );
      }
    }
  }

  protected void fireModified( )
  {
    for( final ModifyListener l : m_modlistener )
      l.modifyText( null );
  }

  public void setIncrement( final int increment )
  {
    m_increment = increment;
  }

  public void setPageIncrement( final int pageIncrement )
  {
    m_pageIncrement = pageIncrement;
  }

}
