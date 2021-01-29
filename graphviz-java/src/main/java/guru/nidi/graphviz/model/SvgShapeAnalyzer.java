/*
 * Copyright © 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import java.awt.*;

public class SvgShapeAnalyzer extends SvgElementFinder {
    private static final Logger LOG = LoggerFactory.getLogger(SvgShapeAnalyzer.class);

    private final double xFactor;
    private final double yFactor;
    private final SvgSizeAnalyzer transform;

    public SvgShapeAnalyzer(SvgElementFinder finder, int width, int height) {
        super(finder);
        final String viewBox = finder.doc.getDocumentElement().getAttribute("viewBox");
        final String[] viewBoxParts = viewBox.split(" ");
        xFactor = width / Double.parseDouble(viewBoxParts[2]);
        yFactor = height / Double.parseDouble(viewBoxParts[3]);
        final Element g = (Element) finder.doc.getDocumentElement().getElementsByTagName("g").item(0);
        transform = SvgSizeAnalyzer.transform(g.getAttribute("transform"));
    }

    @Nullable
    public Rectangle getBoundingBox(Element e) {
        if (!e.getAttribute("class").contains("node")) {
            LOG.error("Currently only nodes are supported.");
            return null;
        }
        final NodeList ellipses = e.getElementsByTagName("ellipse");
        if (ellipses.getLength() == 0) {
            LOG.error("Currently only nodes with shape ellipse supported.");
            return null;
        }
        final Element ellipse = (Element) ellipses.item(0);
        final double cx = xFactor * (numAttr(ellipse, "cx") + transform.getTranslateX());
        final double cy = yFactor * (numAttr(ellipse, "cy") + transform.getTranslateY());
        final double rx = xFactor * numAttr(ellipse, "rx");
        final double ry = yFactor * numAttr(ellipse, "ry");
        return new Rectangle((int) Math.round(cx - rx), (int) Math.round(cy - ry),
                (int) Math.round(2 * rx), (int) Math.round(2 * ry));
    }

    private double numAttr(Element e, String attr) {
        return Double.parseDouble(e.getAttribute(attr));
    }
}
