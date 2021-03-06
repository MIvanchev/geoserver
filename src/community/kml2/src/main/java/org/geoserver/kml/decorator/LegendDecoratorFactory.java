/* Copyright (c) 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.kml.decorator;

import java.util.List;

import org.geoserver.wms.GetMapRequest;
import org.geoserver.wms.WMSRequests;
import org.geotools.map.Layer;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Icon;
import de.micromata.opengis.kml.v_2_2_0.ScreenOverlay;
import de.micromata.opengis.kml.v_2_2_0.Units;
import de.micromata.opengis.kml.v_2_2_0.Vec2;

/**
 * Encodes previous/next network links when paging is used
 * 
 * @author Andrea Aime - GeoSolutions
 */
public class LegendDecoratorFactory implements KmlDecoratorFactory {

    @Override
    public KmlDecorator getDecorator(Class<? extends Feature> featureClass,
            KmlEncodingContext context) {
        // see if we have to encode a legend
        GetMapRequest request = context.getRequest();
        Boolean legend = (Boolean) request.getFormatOptions().get("legend");
        if (legend != null && legend && Document.class.isAssignableFrom(featureClass)) {
            return new LegendDecorator();
        } else {
            return null;
        }
    }

    static class LegendDecorator implements KmlDecorator {

        @Override
        public Feature decorate(Feature feature, KmlEncodingContext context) {
            Document doc = (Document) feature;
            
            // create the screen overlay
            ScreenOverlay go = doc.createAndAddScreenOverlay();
            go.setName("Legend");
            go.setOverlayXY(createPixelsVec(0, 0));
            go.setScreenXY(createPixelsVec(10, 20));

            // build the href
            Icon icon = go.createAndSetIcon();
            String legendOptions = (String) context.getRequest().getRawKvp().get("LEGEND_OPTIONS");
            String[] kvpArray = null;
            if (legendOptions != null) {
                kvpArray = new String[] { "LEGEND_OPTIONS", legendOptions };
            }
            List<Layer> layerList = context.getMapContent().layers();
            Layer[] layers = (Layer[]) layerList.toArray(new Layer[layerList.size()]);
            icon.setHref(WMSRequests.getGetLegendGraphicUrl(context.getRequest(),
                    layers, kvpArray));
            
            return feature;
        }

        private Vec2 createPixelsVec(int x, int y) {
            Vec2 vec = new Vec2();
            vec.setX(x);
            vec.setY(y);
            vec.setXunits(Units.PIXELS);
            vec.setYunits(Units.PIXELS);
            return vec;
        }

    }
}
