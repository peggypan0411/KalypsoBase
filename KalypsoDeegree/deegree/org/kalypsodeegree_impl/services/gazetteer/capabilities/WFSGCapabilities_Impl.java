/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001 by:
EXSE, Department of Geography, University of Bonn
http://www.giub.uni-bonn.de/exse/
lat/lon Fitzke/Fretter/Poth GbR
http://www.lat-lon.de

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

Andreas Poth
lat/lon Fitzke/Fretter/Poth GbR
Meckenheimer Allee 176
53115 Bonn
Germany
E-Mail: poth@lat-lon.de

Jens Fitzke
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: jens.fitzke@uni-bonn.de

                 
 ---------------------------------------------------------------------------*/
package org.deegree_impl.services.gazetteer.capabilities;

import org.deegree.model.geometry.GM_Envelope;
import org.deegree.services.capabilities.HTTP;
import org.deegree.services.capabilities.MetadataURL;
import org.deegree.services.capabilities.Service;
import org.deegree.services.gazetteer.capabilities.Profile;
import org.deegree.services.gazetteer.capabilities.WFSGCapabilities;
import org.deegree.services.wfs.capabilities.Capability;
import org.deegree.services.wfs.capabilities.FeatureType;
import org.deegree.services.wfs.capabilities.FeatureTypeList;
import org.deegree.services.wfs.capabilities.Operation;
import org.deegree.services.wfs.capabilities.Request;
import org.deegree_impl.services.wfs.capabilities.WFSCapabilities_Impl;

/**
 * 
 *
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class WFSGCapabilities_Impl extends WFSCapabilities_Impl implements WFSGCapabilities {

	Profile profile = null;

	/** Creates a new instance of WFSGCapabilities_Impl */
	public WFSGCapabilities_Impl(Capability capability, FeatureTypeList featureTypeList, String version, String updateSequence, Service service) {
		super(capability, featureTypeList, version, updateSequence, service);
	}

	/** Creates a new instance of WFSGCapabilities_Impl */
	public WFSGCapabilities_Impl(
		Capability capability,
		FeatureTypeList featureTypeList,
		String version,
		String updateSequence,
		Service service,
		Profile profile) {
		super(capability, featureTypeList, version, updateSequence, service);
		setProfile(profile);
	}

	/**
	*
	*
	* @return 
	*/
	public Profile getProfile() {
		return this.profile;
	}

	private void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * exports the capabilities as OGC WFS conform XML document
	 * @return
	 */
	public String exportAsXML() {
		StringBuffer sb = new StringBuffer(60000);

		try {

			sb.append("<WFS_Capabilities version=\"" + getVersion() + "\" ");
			sb.append("updateSequence=\"" + getUpdateSequence() + "\">");

			// service section
			sb.append("<Service><Name>" + getService().getName() + "</Name>");
			if (getService().getTitle() != null) {
				sb.append("<Title>" + getService().getTitle() + "</Title>");
			}
			if (getService().getAbstract() != null) {
				sb.append("<Abstract>" + getService().getAbstract() + "</Abstract>");
			}
			if (getService().getOnlineResource() != null) {
				String s = getService().getOnlineResource().getProtocol() + "://";
				s += getService().getOnlineResource().getHost();
				s += ":" + getService().getOnlineResource().getPort();
				s += getService().getOnlineResource().getPath();
				sb.append("<OnlineResource>" + s + "</OnlineResource>");
			}
			if (getService().getFees() != null) {
				sb.append("<Fees>" + getService().getFees() + "</Fees>");
			}
			if (getService().getAccessConstraints() != null) {
				sb.append("<AccessConstraints>" + getService().getAccessConstraints() + "</AccessConstraints>");
			}
			sb.append("</Service>");

			// Capability section
			Request req = getCapability().getRequest();
			sb.append("<Capability><Request><GetCapabilities><DCPType><HTTP>");
			HTTP http = (HTTP)req.getGetCapabilities().getDCPType()[0].getProtocol();
			if (http.getGetOnlineResources() != null) {
				for (int i = 0; i < http.getGetOnlineResources().length; i++) {
					sb.append(
						"<Get onlineResource=\""
							+ http.getGetOnlineResources()[i].getProtocol()
							+ "://"
							+ http.getGetOnlineResources()[i].getHost()
							+ ":"
							+ http.getGetOnlineResources()[i].getPort()
							+ http.getGetOnlineResources()[i].getPath()
							+ "\" />");
				}
			}
			if (http.getPostOnlineResources() != null) {
				for (int i = 0; i < http.getPostOnlineResources().length; i++) {
					sb.append(
						"<Post onlineResource=\""
							+ http.getPostOnlineResources()[i].getProtocol()
							+ "://"
							+ http.getPostOnlineResources()[i].getHost()
							+ ":"
							+ http.getPostOnlineResources()[i].getPort()
							+ http.getPostOnlineResources()[i].getPath()
							+ "\" />");
				}
			}
			sb.append("</HTTP></DCPType></GetCapabilities>");

			sb.append("<DescribeFeatureType><SchemaDescriptionLanguage>");
			sb.append("<XMLSCHEMA/></SchemaDescriptionLanguage><DCPType><HTTP>");
			http = (HTTP)req.getDescribeFeatureType().getDCPType()[0].getProtocol();
			if (http.getGetOnlineResources() != null) {
				for (int i = 0; i < http.getGetOnlineResources().length; i++) {
					sb.append(
						"<Get onlineResource=\""
							+ http.getGetOnlineResources()[i].getProtocol()
							+ "://"
							+ http.getGetOnlineResources()[i].getHost()
							+ ":"
							+ http.getGetOnlineResources()[i].getPort()
							+ http.getGetOnlineResources()[i].getPath()
							+ "\" />");
				}
			}
			if (http.getPostOnlineResources() != null) {
				for (int i = 0; i < http.getPostOnlineResources().length; i++) {
					sb.append(
						"<Post onlineResource=\""
							+ http.getPostOnlineResources()[i].getProtocol()
							+ "://"
							+ http.getPostOnlineResources()[i].getHost()
							+ ":"
							+ http.getPostOnlineResources()[i].getPort()
							+ http.getPostOnlineResources()[i].getPath()
							+ "\" />");
				}
			}
			sb.append("</HTTP></DCPType></DescribeFeatureType>");

			if (req.getTransaction() != null) {
				sb.append("<Transaction><DCPType><HTTP>");
				http = (HTTP)req.getTransaction().getDCPType()[0].getProtocol();
				if (http.getGetOnlineResources() != null) {
					for (int i = 0; i < http.getGetOnlineResources().length; i++) {
						sb.append(
							"<Get onlineResource=\""
								+ http.getGetOnlineResources()[i].getProtocol()
								+ "://"
								+ http.getGetOnlineResources()[i].getHost()
								+ ":"
								+ http.getGetOnlineResources()[i].getPort()
								+ http.getGetOnlineResources()[i].getPath()
								+ "\" />");
					}
				}
				if (http.getPostOnlineResources() != null) {
					for (int i = 0; i < http.getPostOnlineResources().length; i++) {
						sb.append(
							"<Post onlineResource=\""
								+ http.getPostOnlineResources()[i].getProtocol()
								+ "://"
								+ http.getPostOnlineResources()[i].getHost()
								+ ":"
								+ http.getPostOnlineResources()[i].getPort()
								+ http.getPostOnlineResources()[i].getPath()
								+ "\" />");
					}
				}
				sb.append("</HTTP></DCPType></Transaction>");
			}

			if (req.getGetFeature() != null) {
				sb.append("<GetFeature><ResultFormat><XML/><GML2/><FEATURECOLLECTION/>");
				sb.append("</ResultFormat><DCPType><HTTP>");
				http = (HTTP)req.getGetFeature().getDCPType()[0].getProtocol();
				if (http.getGetOnlineResources() != null) {
					for (int i = 0; i < http.getGetOnlineResources().length; i++) {
						sb.append(
							"<Get onlineResource=\""
								+ http.getGetOnlineResources()[i].getProtocol()
								+ "://"
								+ http.getGetOnlineResources()[i].getHost()
								+ ":"
								+ http.getGetOnlineResources()[i].getPort()
								+ http.getGetOnlineResources()[i].getPath()
								+ "\" />");
					}
				}
				if (http.getPostOnlineResources() != null) {
					for (int i = 0; i < http.getPostOnlineResources().length; i++) {
						sb.append(
							"<Post onlineResource=\""
								+ http.getPostOnlineResources()[i].getProtocol()
								+ "://"
								+ http.getPostOnlineResources()[i].getHost()
								+ ":"
								+ http.getPostOnlineResources()[i].getPort()
								+ http.getPostOnlineResources()[i].getPath()
								+ "\" />");
					}
				}
				sb.append("</HTTP></DCPType></GetFeature>");
			}

			if (req.getGetFeatureWithLock() != null) {
				sb.append("<GetFeatureWithLock><ResultFormat><XML/><GML2/><FEATURECOLLECTION/>");
				sb.append("</ResultFormat><DCPType><HTTP>");
				http = (HTTP)req.getGetFeatureWithLock().getDCPType()[0].getProtocol();
				if (http.getGetOnlineResources() != null) {
					for (int i = 0; i < http.getGetOnlineResources().length; i++) {
						sb.append(
							"<Get onlineResource=\""
								+ http.getGetOnlineResources()[i].getProtocol()
								+ "://"
								+ http.getGetOnlineResources()[i].getHost()
								+ ":"
								+ http.getGetOnlineResources()[i].getPort()
								+ http.getGetOnlineResources()[i].getPath()
								+ "\" />");
					}
				}
				if (http.getPostOnlineResources() != null) {
					for (int i = 0; i < http.getPostOnlineResources().length; i++) {
						sb.append(
							"<Post onlineResource=\""
								+ http.getPostOnlineResources()[i].getProtocol()
								+ "://"
								+ http.getPostOnlineResources()[i].getHost()
								+ ":"
								+ http.getPostOnlineResources()[i].getPort()
								+ http.getPostOnlineResources()[i].getPath()
								+ "\" />");
					}
				}
				sb.append("</HTTP></DCPType></GetFeatureWithLock>");
			}

			if (req.getLockFeature() != null) {
				sb.append("<LockFeature><DCPType><HTTP>");
				http = (HTTP)req.getLockFeature().getDCPType()[0].getProtocol();
				if (http.getGetOnlineResources() != null) {
					for (int i = 0; i < http.getGetOnlineResources().length; i++) {
						sb.append(
							"<Get onlineResource=\""
								+ http.getGetOnlineResources()[i].getProtocol()
								+ "://"
								+ http.getGetOnlineResources()[i].getHost()
								+ ":"
								+ http.getGetOnlineResources()[i].getPort()
								+ http.getGetOnlineResources()[i].getPath()
								+ "\" />");
					}
				}
				if (http.getPostOnlineResources() != null) {
					for (int i = 0; i < http.getPostOnlineResources().length; i++) {
						sb.append(
							"<Post onlineResource=\""
								+ http.getPostOnlineResources()[i].getProtocol()
								+ "://"
								+ http.getPostOnlineResources()[i].getHost()
								+ ":"
								+ http.getPostOnlineResources()[i].getPort()
								+ http.getPostOnlineResources()[i].getPath()
								+ "\" />");
					}
				}
				sb.append("</HTTP></DCPType></LockFeature>");
			}

			sb.append("</Request></Capability>");

			// FeatureTypeList section
			sb.append("<FeatureTypeList>");
			Operation[] operations = getFeatureTypeList().getOperations();
			if (operations != null) {
				sb.append("<Operations>");
				for (int i = 0; i < operations.length; i++) {
					sb.append("<" + operations[i].getName() + "/>");
				}
				sb.append("</Operations>");
			}

			FeatureType[] types = getFeatureTypeList().getFeatureTypes();

			for (int i = 0; i < types.length; i++) {
				sb.append("<FeatureType>");
				sb.append("<Name>" + types[i].getName() + "</Name>");
				if (types[i].getTitle() != null) {
					sb.append("<Title>" + types[i].getTitle() + "</Title>");
				}
				if (types[i].getAbstract() != null) {
					sb.append("<Abstract>" + types[i].getAbstract() + "</Abstract>");
				}
				if (types[i].getKeywords() != null) {
					sb.append("<Keywords>");
					for (int j = 0; j < types[i].getKeywords().length - 1; j++) {
						if (j != types[i].getKeywords().length - 1) {
							sb.append(types[i].getKeywords()[j] + ",");
						} else {
							sb.append(types[i].getKeywords()[j]);
						}
					}
					sb.append("</Keywords>");
				}
				sb.append("<SRS>" + types[i].getSrs() + "</SRS>");
				GM_Envelope bb = types[i].getLatLonBoundingBox();
				sb.append("<LatLonBoundingBox ");
				sb.append("minx=\"" + bb.getMin().getX() + "\" ");
				sb.append("miny=\"" + bb.getMin().getY() + "\" ");
				sb.append("maxx=\"" + bb.getMax().getX() + "\" ");
				sb.append("maxy=\"" + bb.getMax().getY() + "\" >");
				sb.append("</LatLonBoundingBox>");

				operations = types[i].getOperations();
				if (operations != null) {
					sb.append("<Operations>");
					for (int j = 0; j < operations.length; j++) {
						sb.append("<" + operations[j].getName() + "/>");
					}
					sb.append("</Operations>");
				}

				MetadataURL[] meta = types[i].getMetadataURL();
				if (meta != null) {
					for (int j = 0; j < meta.length; j++) {
						sb.append("<MetadataURL  type=\"" + meta[j].getType() + "\" ");
						sb.append("format=\"" + meta[j].getFormat() + "\">");
						sb.append(
							meta[j].getOnlineResource().getProtocol()
								+ "://"
								+ meta[j].getOnlineResource().getHost()
								+ ":"
								+ meta[j].getOnlineResource().getPort()
								+ meta[j].getOnlineResource().getPath());
						sb.append("</MetadataURL>");
					}
				}

				sb.append("</FeatureType>");

			}

			sb.append("</FeatureTypeList></WFS_Capabilities>");

		} catch (Exception e) {
			System.out.println(e);
		}

		return sb.toString();
	}

	/**
	 * toString representation
	 * @return the class as XML-output
	 */
	public String toString() {
		return exportAsXML();
	}
}
