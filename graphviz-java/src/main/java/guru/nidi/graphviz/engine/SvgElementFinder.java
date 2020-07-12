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
package guru.nidi.graphviz.engine;

import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;

public class SvgElementFinder {
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    private static final VariableResolver RESOLVER = new VariableResolver();
    private static final XPath X_PATH = xPath(RESOLVER);
    private static final XPathExpression EXPR_G = pathExpression(X_PATH, "//g");
    private static final XPathExpression EXPR_TITLE = pathExpression(X_PATH, "//title[text()=$var]");
    private static final XPathExpression EXPR_TITLE_OR = pathExpression(X_PATH, "//title[text()=$var or text()=$alt]");
    private final Document doc;

    public SvgElementFinder(String svg) {
        try {
            doc = builder().parse(new InputSource(new StringReader(svg)));
        } catch (SAXException | IOException e) {
            throw new GraphvizException("Could not read SVG", e);
        }
    }

    public String getSvg() {
        final StringWriter sw = new StringWriter();
        try {
            TRANSFORMER_FACTORY.newTransformer().transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (TransformerException e) {
            throw new AssertionError("Could not generate string from DOM", e);
        }
    }

    public Node findGraph() {
        return nodeExpr(EXPR_G, "");
    }

    @Nullable
    public Node findNode(guru.nidi.graphviz.model.Node node) {
        return findNode(node.name().toString());
    }

    @Nullable
    public Node findNode(String name) {
        final Node title = nodeExpr(EXPR_TITLE, name);
        return title == null ? null : title.getParentNode();
    }

    @Nullable
    public Node findLink(Link link) {
        return findLink(link.from().name().toString(), link.to().name().toString());
    }

    @Nullable
    public Node findLink(String from, String to) {
        final Node title = nodeExpr(EXPR_TITLE_OR, from + "--" + to);
        return title == null ? null : title.getParentNode();
    }

    @Nullable
    public Node findCluster(Graph cluster) {
        return findCluster(cluster.name().toString());
    }

    @Nullable
    public Node findCluster(String name) {
        final Node title = nodeExpr(EXPR_TITLE, "cluster_" + name);
        return title == null ? null : title.getParentNode();
    }

    @Nullable
    private Node nodeExpr(XPathExpression expr, String var) {
        RESOLVER.set(var);
        try {
            return (Node) expr.evaluate(doc, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new AssertionError("Could not execute XPath", e);
        }
    }

    private DocumentBuilder builder() {
        try {
            return FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Could not initialize DOM", e);
        }
    }

    private static XPath xPath(XPathVariableResolver resolver) {
        final XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setXPathVariableResolver(resolver);
        return xPath;
    }

    private static XPathExpression pathExpression(XPath xPath, String exp) {
        try {
            return xPath.compile(exp);
        } catch (XPathExpressionException e) {
            throw new AssertionError("Invalid XPath expression", e);
        }
    }

    private static class VariableResolver implements XPathVariableResolver {
        private final static ThreadLocal<String> var = new ThreadLocal<>();

        public void set(String value) {
            var.set(value);
        }

        @Override
        public Object resolveVariable(QName varName) {
            return varName.getLocalPart().equals("var") ? var.get() : var.get().replace("--", "->");
        }
    }
}
