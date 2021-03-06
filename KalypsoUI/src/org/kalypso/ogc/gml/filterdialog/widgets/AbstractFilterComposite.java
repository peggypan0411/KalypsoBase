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
package org.kalypso.ogc.gml.filterdialog.widgets;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.kalypso.gmlschema.feature.IFeatureType;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.property.IValuePropertyType;
import org.kalypso.ogc.gml.filterdialog.dialog.IErrorMessageReciever;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.filterencoding.Expression;
import org.kalypsodeegree.model.feature.event.ModellEventProviderAdapter;
import org.kalypsodeegree_impl.filterencoding.Literal;
import org.kalypsodeegree_impl.filterencoding.PropertyName;
import org.kalypsodeegree_impl.tools.GeometryUtilities;

/**
 * @author kuepfer
 */
public abstract class AbstractFilterComposite extends Composite implements IErrorMessageReciever
{
  final protected IErrorMessageReciever m_errorMessageReciever;

  final protected IFeatureType m_ft;

  final private TextFieldToPropertyTypeValidator m_validator;

  final protected String EMPTY_VALUE = "-NULL-"; //$NON-NLS-1$

  static final int STANDARD_WIDTH_FIELD = 150;

  static final int STANDARD_WIDTH_HEIGHT = 200;

  public AbstractFilterComposite( final Composite parent, final int style, final IErrorMessageReciever errorMessageReciever, final IFeatureType ft )
  {
    super( parent, style );
    m_errorMessageReciever = errorMessageReciever;
    m_ft = ft;
    m_validator = new TextFieldToPropertyTypeValidator();
    setLayout( new GridLayout( 2, false ) );
    final GridData data = new GridData( GridData.FILL_HORIZONTAL );
    data.horizontalIndent = 10;
    data.verticalIndent = 10;
    data.widthHint = STANDARD_WIDTH_FIELD;
    data.grabExcessHorizontalSpace = true;
    setLayoutData( data );
  }

  protected ModellEventProviderAdapter m_ModelEventProvider = new ModellEventProviderAdapter();

  private org.kalypso.ogc.gml.filterdialog.dialog.FilterDialog m_dialog;

  /**
   * @see org.kalypso.ogc.gml.filterdialog.dialog.IErrorMessageReciever#setErrorMessage(java.lang.String)
   */
  @Override
  public void setErrorMessage( final String message )
  {
    m_errorMessageReciever.setErrorMessage( message );

  }

  /**
   * @see org.kalypso.ogc.gml.filterdialog.dialog.IErrorMessageReciever#getErrorMessageReciever()
   */
  @Override
  public IErrorMessageReciever getErrorMessageReciever( )
  {
    return m_errorMessageReciever;
  }

  public boolean validate( final IValuePropertyType vpt, final String toValidate )
  {
    m_validator.setValueProptery( vpt );
    final String errorMessage = m_validator.isValid( toValidate );
    if( m_errorMessageReciever != null )
      m_errorMessageReciever.setErrorMessage( errorMessage );
    if( errorMessage == null )
      return true;
    return false;
  }

  protected class TextFieldToPropertyTypeValidator implements IInputValidator
  {
    private IValuePropertyType i_ftp = null;

    TextFieldToPropertyTypeValidator( )
    {
    }

    public void setValueProptery( final IValuePropertyType vpt )
    {
      i_ftp = vpt;
    }

    TextFieldToPropertyTypeValidator( final IValuePropertyType featureTypeProperty )
    {
      i_ftp = featureTypeProperty;
    }

    /**
     * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
     */
    @Override
    public String isValid( final String newText )
    {
      final Class clazz = i_ftp.getValueClass();
      if( clazz != null )
      {
        try
        {
          if( clazz == Double.class )
            Double.parseDouble( newText );
          if( clazz == Float.class )
            Float.parseFloat( newText );
          if( clazz == Integer.class )
            Integer.parseInt( newText );
          if( clazz == Long.class )
            Long.parseLong( newText );
          if( GeometryUtilities.isGeometry( i_ftp ) )
          {
            // TODO Christoph was macht das ?
            final String geomString = clazz.getName().replaceAll( ".+\\.", "" ); //$NON-NLS-1$ //$NON-NLS-2$
            if( newText.equals( geomString ) )
            {
              return Messages.getString( "org.kalypso.ogc.gml.filterdialog.widgets.AbstractFilterComposite.3" ); //$NON-NLS-1$
            }
          }
          if( clazz == String.class )
          {
            if( newText == null || newText.length() == 0 )
              return Messages.getString( "org.kalypso.ogc.gml.filterdialog.widgets.AbstractFilterComposite.4" ); //$NON-NLS-1$
          }
        }
        catch( final NumberFormatException e )
        {
          return Messages.getString( "org.kalypso.ogc.gml.filterdialog.widgets.AbstractFilterComposite.5" ) + clazz.getName().replaceAll( ".+\\.", "" ) + Messages.getString( "org.kalypso.ogc.gml.filterdialog.widgets.AbstractFilterComposite.8" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        return null;
      }

      return null;
    }
  }

  protected Object[] setPropertySelection( final Expression expression )
  {
    String value = null;
    if( expression != null )
    {
      if( expression instanceof Literal )
        value = ((Literal)expression).getValue();
      else if( expression instanceof PropertyName )
        value = ((PropertyName)expression).getValue();

      final IPropertyType[] properties = m_ft.getProperties();
      for( final IPropertyType type : properties )
      {
        if( type.getQName().getLocalPart().equals( value ) )
          return new Object[] { type };
      }
    }
    return new Object[] { m_ft.getProperties( 0 ) };
  }

  public void setFilterDialog( final org.kalypso.ogc.gml.filterdialog.dialog.FilterDialog dialog )
  {
    m_dialog = dialog;
  }

  protected void refresh( )
  {
    m_dialog.refresh();
  }
}
