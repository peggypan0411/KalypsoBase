/*--------------- Kalypso-Header ----------------------------------------

 This file is part of kalypso.
 Copyright (C) 2004, 2005 by:

 University of Technology Hamburg-Harburg (TUHH)
 Institute of River and Coastal Engineering
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
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact:

 E-Mail:
 g.belger@bjoernsen.de
 m.schlienger@bjoernsen.de
 v.doemming@tuhh.de

 ------------------------------------------------------------------------*/
package org.kalypso.ogc.gml.gui;

import java.text.ParseException;

import javax.xml.bind.JAXBElement;

import org.eclipse.jface.viewers.ILabelProvider;
import org.kalypso.gmlschema.property.IPropertyType;
import org.kalypso.gmlschema.types.ITypeHandler;
import org.kalypso.ogc.gml.featureview.IFeatureChangeListener;
import org.kalypso.ogc.gml.featureview.IFeatureModifier;
import org.kalypso.ogc.gml.selection.IFeatureSelectionManager;
import org.kalypso.template.featureview.ControlType;
import org.kalypso.template.featureview.ObjectFactory;
import org.kalypsodeegree_impl.model.feature.gmlxpath.GMLXPath;

/**
 * Provides editors and visualisation for {@link org.kalypsodeegree.model.feature.Feature}s.
 * 
 * @author Gernot Belger
 */
public interface IGuiTypeHandler extends ILabelProvider, ITypeHandler, IFeatureDialogFactory
{
  JAXBElement< ? extends ControlType> createFeatureviewControl( IPropertyType property, ObjectFactory factory );

  IFeatureModifier createFeatureModifier( GMLXPath propertyPath, IPropertyType ftp, IFeatureSelectionManager selectionManager, IFeatureChangeListener fcl, String format );

  /**
   * Inverse operation to {@link ILabelProvider#getText(java.lang.Object)}. Must return an object of the type for which
   * this handler is registered for.
   * <p>
   * Parses a (human edited) text into an object of the type this handler is responsible for.
   * 
   * @param text
   *          The text which gets parsed.
   * @param formatHint
   *          Potentially a hint how to parse the text. It depends on the type handler what format hints are supported.
   *          For example for the date-handler, DateFormat format string can be used. May be <code>null</code>.
   */
  Object parseText( String text, String formatHint ) throws ParseException;
}
