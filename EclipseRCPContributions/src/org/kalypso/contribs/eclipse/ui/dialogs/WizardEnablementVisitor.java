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
package org.kalypso.contribs.eclipse.ui.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.kalypso.contribs.eclipse.internal.EclipseRCPContributionsDebug;
import org.kalypso.contribs.eclipse.internal.EclipseRCPContributionsPlugin;
import org.kalypso.contribs.eclipse.jface.wizard.WizardEnablementRegistry;

/**
 * @author Gernot Belger
 */
public class WizardEnablementVisitor extends AbstractWizardRegistryVisitor
{
  private final IEvaluationContext m_context;

  private final Map<String, Boolean> m_enablement = new HashMap<>();

  private boolean m_hasEnabled = false;

  public WizardEnablementVisitor( final IEvaluationContext context )
  {
    m_context = context;
  }

  public Map<String, Boolean> getEnablement( )
  {
    return m_enablement;
  }

  /**
   * returns <code>true</code> if at least one element is enabled.
   */
  public boolean hasEnabled( )
  {
    return m_hasEnabled;
  }

  @Override
  protected void visit( final IWizardDescriptor wizard )
  {
    final String id = wizard.getId();
    addEnablement( id, isEnabled( wizard ) );
  }

  private void addEnablement( final String id, final boolean enabled )
  {
    m_enablement.put( id, enabled );

    if( enabled )
      m_hasEnabled = true;
  }

  private boolean isEnabled( final IWizardDescriptor wizard )
  {
    if( wizard == null )
      return true;

    try
    {
      final String wizardID = wizard.getId();
      final Expression expression = WizardEnablementRegistry.getInstance().getExpression( wizardID );
      final EvaluationResult result = expression.evaluate( m_context );
      return EvaluationResult.TRUE == result;
    }
    catch( final CoreException e )
    {
      // This happen often, as we always get an exception when the tested variable is not
      // in scope, which happens always e.g. if a dialog is opened.
      if( EclipseRCPContributionsDebug.GENERIC_WIZARDS.isEnabled() )
        EclipseRCPContributionsPlugin.getDefault().getLog().log( e.getStatus() );
      return false;
    }
  }
}