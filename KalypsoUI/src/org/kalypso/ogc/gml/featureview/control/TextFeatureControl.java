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

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.kalypso.commons.command.ICommand;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.ogc.gml.command.ChangeFeatureCommand;
import org.kalypso.ogc.gml.featureview.IFeatureModifier;
import org.kalypso.ogc.gml.featureview.modfier.StringModifier;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.model.feature.Feature;
import org.kalypsodeegree.model.feature.event.ModellEvent;
import org.kalypsodeegree.model.feature.event.ModellEventListener;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;

/**
 * @author Gernot Belger
 */
public class TextFeatureControl extends AbstractFeatureControl implements ModellEventListener
{
  private final Color m_errorColor = Display.getCurrent().getSystemColor( SWT.COLOR_RED );

  private Text m_text = null;

  private String m_currentValue;

  private boolean m_isValid = false;

  private final IFeatureModifier m_modifier;

  private boolean m_editable = true;

  public TextFeatureControl( final Feature feature, final GMLXPath propertyPath, final IValuePropertyType ftp, final String format )
  {
    super( feature, ftp );

    Assert.isNotNull( ftp, "Unknown Feature-Property" ); //$NON-NLS-1$

    m_modifier = new StringModifier( propertyPath, ftp, format );
  }

  @Override
  public Control createControl( final Composite parent, final int style )
  {
    m_text = new Text( parent, style );
    m_text.setEditable( m_editable );

    if( m_editable )
    {
      m_text.addModifyListener( new ModifyListener()
      {
        @Override
        public void modifyText( final ModifyEvent e )
        {
          onTextModified();
        }
      } );

      m_text.addFocusListener( new FocusAdapter()
      {
        @Override
        public void focusLost( final FocusEvent e )
        {
          fireFeatureChange( getChanges() );
        }
      } );

      m_text.addSelectionListener( new SelectionAdapter()
      {
        @Override
        public void widgetDefaultSelected( final SelectionEvent e )
        {
          fireFeatureChange( getChanges() );
        }
      } );
    }

    updateControl();

    return m_text;
  }

  protected void onTextModified( )
  {
    m_currentValue = m_text.getText();
    updateValid();
  }

  protected void setValid( final boolean valid )
  {
    if( m_isValid != valid )
    {
      m_isValid = valid;

      if( m_text != null && !m_text.isDisposed() )
        m_text.setForeground( m_isValid ? null : m_errorColor );
    }
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#isValid()
   */
  @Override
  public boolean isValid( )
  {
    return m_isValid;
  }

  public void setEditable( final boolean editable )
  {
    m_editable = editable;
    if( m_text != null && !m_text.isDisposed() )
      m_text.setEditable( editable );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#updateControl()
   */
  @Override
  public void updateControl( )
  {
    if( m_text == null || m_text.isDisposed() )
      return;

    final Feature feature = getFeature();

    if( feature == null || getFeatureTypeProperty() == null )
      m_text.setText( Messages.getString( "org.kalypso.ogc.gml.featureview.control.TextFeatureControl.nodata" ) ); //$NON-NLS-1$
    else
    {
      // compare with old to prevent loop
      final String newText = toString();
      final String oldText = m_text.getText();

      // what if the value is null???
      if( newText != null && oldText == null )
        m_text.setText( newText );
      else if( newText != null && oldText != null && newText.compareTo( oldText ) != 0 )
        m_text.setText( newText );
    }

    setValid( true );
  }

  @Override
  public String toString( )
  {
    return m_modifier.getLabel( getFeature() );
  }

  protected ICommand getChanges( )
  {
    final Feature feature = getFeature();
    final IPropertyType pt = getFeatureTypeProperty();
    final Object oldData = feature.getProperty( pt );

    updateValid();

    final Object newData;
    if( !isValid() )
      newData = null;
    else
    {
      final String text = m_currentValue;

      newData = m_modifier.parseInput( getFeature(), text );
    }

    // nur �ndern, wenn sich wirklich was ge�ndert hat
    if( !ObjectUtils.equals( newData, oldData ) )
// if( (newData == null && oldData != null) || (newData != null && !m_modifier.equals( newData, oldData )) )
      return new ChangeFeatureCommand( feature, pt, newData );

    return null;
  }

  protected void updateValid( )
  {
    final String text = m_currentValue;
    setValid( m_modifier.isValid( text ) == null );
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
    m_text.addModifyListener( l );
  }

  /**
   * @see org.kalypso.ogc.gml.featureview.IFeatureControl#removeModifyListener(org.eclipse.swt.events.ModifyListener)
   */
  @Override
  public void removeModifyListener( final ModifyListener l )
  {
    m_text.removeModifyListener( l );
  }
}