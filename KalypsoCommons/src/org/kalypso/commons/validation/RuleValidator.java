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
package org.kalypso.commons.validation;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.jface.dialogs.IMessageProvider;

/**
 * @author Gernot Belger
 */
public class RuleValidator implements IFormValidator
{
  private final Collection<IFormValidationRule> m_rules = new LinkedHashSet<>();

  public final void addRule( final IFormValidationRule rule )
  {
    m_rules.add( rule );
  }

  public final void removeRule( final IFormValidationRule rule )
  {
    m_rules.remove( rule );
  }

  /**
   * @see org.kalypso.contribs.eclipse.ui.forms.validation.IFormValidator#validate(java.lang.Object)
   */
  @Override
  public IMessageProvider[] validate( final Object value )
  {
    final IFormValidationRule[] rules = m_rules.toArray( new IFormValidationRule[m_rules.size()] );

    final MessageCollector collector = new MessageCollector();
    for( final IFormValidationRule rule : rules )
    {
      rule.validate( value, collector );
      if( collector.isStopped() )
        break;
    }

    return collector.getMessages();
  }
}
