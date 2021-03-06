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
package org.kalypso.ui.editor.styleeditor.rule;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.kalypso.commons.databinding.IDataBinding;
import org.kalypso.commons.databinding.forms.DatabindingForm;
import org.kalypso.commons.databinding.validation.NumberNotNegativeValidator;
import org.kalypso.commons.databinding.validation.StringBlankValidator;
import org.kalypso.contribs.eclipse.jface.action.ActionButton;
import org.kalypso.ui.editor.styleeditor.MessageBundle;
import org.kalypso.ui.editor.styleeditor.binding.IStyleInput;
import org.kalypso.ui.editor.styleeditor.binding.SLDBinding;
import org.kalypso.ui.internal.i18n.Messages;
import org.kalypsodeegree.graphics.sld.Rule;

/**
 * @author Gernot Belger
 */
public class RulePropertiesComposite extends Composite
{
  private final IDataBinding m_binding;

  private final IStyleInput<Rule> m_input;

  private final ScrolledForm m_form;

  public RulePropertiesComposite( final FormToolkit toolkit, final Composite parent, final IStyleInput<Rule> input )
  {
    super( parent, SWT.NONE );

    m_input = input;

    setLayout( new FillLayout() );
    toolkit.adapt( this );

    m_form = toolkit.createScrolledForm( this );

    m_binding = new DatabindingForm( m_form, toolkit );

    final Composite body = m_form.getBody();
    body.setLayout( new GridLayout( 3, false ) );

    /* Text Panel for Rule-Titel */

    if( m_input.getConfig().isRuleEditName() )
      createNameControl( toolkit, body );

    createTitleControl( toolkit, body );
    createAbstractControl( toolkit, body );
    createMinDenomControl( toolkit, body );
    createMaxDenomControl( toolkit, body );
  }

  private void createNameControl( final FormToolkit toolkit, final Composite parent )
  {
    toolkit.createLabel( parent, Messages.getString( "RulePropertiesComposite_0" ) ); //$NON-NLS-1$

    final Text nameField = toolkit.createText( parent, StringUtils.EMPTY, SWT.BORDER );
    nameField.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    nameField.setMessage( MessageBundle.STYLE_EDITOR_FIELD_EMPTY );

    final StringBlankValidator validator = new StringBlankValidator( IStatus.ERROR, StringBlankValidator.DEFAULT_ERROR_MESSAGE );

    final ISWTObservableValue targetValue = SWTObservables.observeText( nameField, SLDBinding.TEXT_DEFAULT_EVENTS );
    m_binding.bindValue( targetValue, new RuleNameValue( m_input ), validator );
  }

  private void createTitleControl( final FormToolkit toolkit, final Composite parent )
  {
    toolkit.createLabel( parent, MessageBundle.STYLE_EDITOR_TITLE );

    final Text titleField = toolkit.createText( parent, StringUtils.EMPTY, SWT.BORDER );
    titleField.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    titleField.setMessage( MessageBundle.STYLE_EDITOR_FIELD_EMPTY );

    final ISWTObservableValue targetValue = SWTObservables.observeText( titleField, SLDBinding.TEXT_DEFAULT_EVENTS );
    m_binding.bindValue( targetValue, new RuleTitleValue( m_input ) );
  }

  private void createAbstractControl( final FormToolkit toolkit, final Composite parent )
  {
    toolkit.createLabel( parent, Messages.getString( "RulePropertiesComposite_1" ) ); //$NON-NLS-1$

    final Text abstractField = toolkit.createText( parent, StringUtils.EMPTY );
    abstractField.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
    abstractField.setMessage( MessageBundle.STYLE_EDITOR_FIELD_EMPTY );

    final ISWTObservableValue targetValue = SWTObservables.observeText( abstractField, SLDBinding.TEXT_DEFAULT_EVENTS );
    m_binding.bindValue( targetValue, new RuleAbstractValue( m_input ) );
  }

  private void createMinDenomControl( final FormToolkit toolkit, final Composite parent )
  {
    toolkit.createLabel( parent, MessageBundle.STYLE_EDITOR_MIN_DENOM );

    final Text minDenomField = toolkit.createText( parent, StringUtils.EMPTY );
    minDenomField.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    minDenomField.setMessage( MessageBundle.STYLE_EDITOR_FIELD_EMPTY );

    ActionButton.createButton( toolkit, parent, new SetCurrentDenomAction( minDenomField ) );

    final IValidator minMaxValidator = new MinMaxValidator( m_input );
    final NumberNotNegativeValidator notNegativeValidator = new NumberNotNegativeValidator( IStatus.WARNING );

    final ISWTObservableValue targetValue = SWTObservables.observeText( minDenomField, SLDBinding.TEXT_DEFAULT_EVENTS );
    m_binding.bindValue( targetValue, new RuleMinDenomValue( m_input ), minMaxValidator, notNegativeValidator );
  }

  private void createMaxDenomControl( final FormToolkit toolkit, final Composite parent )
  {
    toolkit.createLabel( parent, MessageBundle.STYLE_EDITOR_MAX_DENOM );

    final Text maxDenomField = toolkit.createText( parent, StringUtils.EMPTY );
    maxDenomField.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    maxDenomField.setMessage( MessageBundle.STYLE_EDITOR_FIELD_EMPTY );

    ActionButton.createButton( toolkit, parent, new SetCurrentDenomAction( maxDenomField ) );

    final IValidator minMaxValidator = new MaxMinValidator( m_input );
    final NumberNotNegativeValidator notNegativeValidator = new NumberNotNegativeValidator( IStatus.WARNING );

    final ISWTObservableValue targetValue = SWTObservables.observeText( maxDenomField, SLDBinding.TEXT_DEFAULT_EVENTS );
    m_binding.bindValue( targetValue, new RuleMaxDenomValue( m_input ), minMaxValidator, notNegativeValidator );
  }

  /**
   * Call, if style has changed.
   */
  public void updateControl( )
  {
    m_binding.getBindingContext().updateTargets();

    m_form.reflow( true );
  }
}