package org.deegree_impl.gml;

import org.deegree.model.feature.FeatureTypeProperty;
import org.w3c.dom.Element;

/**
 * @author belger
 */
public class GMLCustomProperty_Impl extends GMLProperty_Impl
{
  public GMLCustomProperty_Impl( final FeatureTypeProperty ftp, final Element element )
  {
    super( ftp, element );
  }
}