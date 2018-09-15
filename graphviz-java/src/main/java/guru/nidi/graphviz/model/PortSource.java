package guru.nidi.graphviz.model;

import java.util.List;

class PortSource implements LinkSource {
    final Port port;

    PortSource(Port port) {
        this.port = port;
    }

    @Override
    public List<Link> links() {
        return null;
    }

    @Override
    public Link linkTo(LinkTarget target) {
        return null;
    }

    @Override
    public void addTo(MutableGraph graph) {
    }

    @Override
    public LinkTarget asLinkTarget() {
        return null;
    }
}
