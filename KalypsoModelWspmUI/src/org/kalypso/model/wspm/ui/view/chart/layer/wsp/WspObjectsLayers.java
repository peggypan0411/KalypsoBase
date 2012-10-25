/** This file is part of Kalypso
 *
 *  Copyright (c) 2012 by
 *
 *  Bj�rnsen Beratende Ingenieure GmbH, Koblenz, Germany (Bjoernsen Consulting Engineers), http://www.bjoernsen.de
 *  Technische Universit�t Hamburg-Harburg, Institut f�r Wasserbau, Hamburg, Germany
 *  (Technical University Hamburg-Harburg, Institute of River and Coastal Engineering), http://www.tu-harburg.de/wb/
 *
 *  Kalypso is free software: you can redistribute it and/or modify it under the terms  
 *  of the GNU Lesser General Public License (LGPL) as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  Kalypso is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with Kalypso.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kalypso.model.wspm.ui.view.chart.layer.wsp;

import org.eclipse.swt.graphics.Point;
import org.kalypso.model.wspm.core.profil.IProfile;
import org.kalypso.model.wspm.ui.view.chart.AbstractProfilLayer;

import de.openali.odysseus.chart.framework.model.data.IDataRange;
import de.openali.odysseus.chart.framework.model.layer.EditInfo;

/**
 * @author Gernot Belger
 */
public class WspObjectsLayers extends AbstractProfilLayer
{
  public WspObjectsLayers( final String id, final IProfile profil, final IWspLayerData wspData )
  {
    super( id, profil );
  }

  @Override
  public EditInfo getHover( final Point pos )
  {
    return null;
  }

  @Override
  public IDataRange<Double> getTargetRange( final IDataRange< ? > domainIntervall )
  {
    return null;
  }

  @Override
  public void executeDrop( final Point point, final EditInfo dragStartData )
  {
  }

  @Override
  public void executeClick( final EditInfo dragStartData )
  {
  }

  @Override
  public EditInfo drag( final Point newPos, final EditInfo dragStartData )
  {
    return null;
  }

  @Override
  public EditInfo commitDrag( final Point point, final EditInfo dragStartData )
  {
    return null;
  }

  @Override
  public IDataRange<Double> getDomainRange( )
  {
    return null;
  }
}