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
package de.renew.workflow.connector.worklist;

import org.eclipse.core.runtime.IStatus;

import de.renew.workflow.base.ITask;

/**
 * This listener will be notified, if a new task (a context from the workflow.xml) has been activated. <br>
 * It should only be notified, after the last task (last parent) was activated.
 *
 * @author Holger Albert
 */
public interface ITaskExecutionListener
{
  /**
   * This function is called, after a task was activated or the active task was stopped.
   * 
   * @param results
   *          The results of the task, which was activated, as well of all of its associated tasks.
   * @param previouslyActive
   *          The task that was active before The task, which was activated.
   * @param activeTask
   *          The task, which was activated.
   */
  void handleActiveTaskChanged( IStatus result, ITask previouslyActive, ITask activeTask );
}