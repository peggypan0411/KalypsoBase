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
package de.openali.odysseus.service.ods.operation;

import java.io.FileNotFoundException;

import org.eclipse.swt.graphics.ImageData;

import de.openali.odysseus.service.ods.util.ImageOutput;
import de.openali.odysseus.service.ods.util.ODSUtils;
import de.openali.odysseus.service.ows.exception.OWSException;
import de.openali.odysseus.service.ows.request.RequestBean;

/**
 * @author burtscher1
 */
public class GetSymbol extends AbstractODSOperation
{

	/**
	 * @see de.openali.odysseus.service.ods.operation.AbstractODSOperation#execute(de.openali.ows.service.RequestBean,
	 *      de.openali.ows.service.ResponseBean,
	 *      de.openali.odysseus.service.ods.environment.IODSEnvironment)
	 */
	@Override
	public void execute() throws OWSException
	{

		// TODO: Validate scene, chart and layer parameter
		RequestBean req = getRequest();

		String sceneId = req.getParameterValue("SCENE");
		// use default scene if no parameter value has been assigned
		if ((sceneId == null) || "".equals(sceneId))
			sceneId = getEnv().getDefaultSceneId();
		if (sceneId == null)
			throw new OWSException(
			        OWSException.ExceptionCode.MISSING_PARAMETER_VALUE,
			        "Missing parameter 'SCENE'", req.getUrl());
		else if (!getEnv().validateSceneId(sceneId))
			throw new OWSException(
			        OWSException.ExceptionCode.INVALID_PARAMETER_VALUE,
			        "Scene '" + sceneId + "' is not available", req.getUrl());

		String chartId = req.getParameterValue("CHART");
		if (chartId == null)
			throw new OWSException(
			        OWSException.ExceptionCode.MISSING_PARAMETER_VALUE,
			        "Missing parameter 'CHART'", req.getUrl());
		else if (!getEnv().validateChartId(sceneId, chartId))
			throw new OWSException(
			        OWSException.ExceptionCode.INVALID_PARAMETER_VALUE,
			        "Chart '" + chartId + "' is not available", req.getUrl());

		String layerId = req.getParameterValue("LAYER");
		if (layerId == null)
			throw new OWSException(
			        OWSException.ExceptionCode.MISSING_PARAMETER_VALUE,
			        "Missing parameter 'LAYER'", req.getUrl());
		else if (!getEnv().validateLayerId(sceneId, chartId, layerId))
			throw new OWSException(
			        OWSException.ExceptionCode.INVALID_PARAMETER_VALUE,
			        "Layer '" + layerId + "' is not available", req.getUrl());
		String symbolId = req.getParameterValue("SYMBOL");
		if ((symbolId == null) || "".equals(symbolId.trim()))
			throw new OWSException(
			        OWSException.ExceptionCode.MISSING_PARAMETER_VALUE,
			        "Missing parameter 'SYMBOL'", req.getUrl());

		ImageData id;
		try
		{
			id = ODSUtils.loadSymbol(getEnv().getTmpDir(), sceneId, chartId,
			        layerId, symbolId);
		}
		catch (FileNotFoundException e)
		{
			throw new OWSException(
			        OWSException.ExceptionCode.INVALID_PARAMETER_VALUE,
			        "Symbol '" + symbolId + "' is not available", req.getUrl());
		}
		ImageOutput.imageResponse(req, getResponse(), id);

	}

}
