package guru.nidi.graphviz.model;

import java.util.*;

import static java.util.stream.Collectors.toList;

class LinkList extends ArrayList<Link> {
    private final LinkSource owner;

    LinkList(LinkSource owner, List<Link> links) {
        super(links);
        this.owner = owner;
    }

    @Override
    public Link set(int index, Link element) {
        return super.set(index, owner.linkTo(element));
    }

    @Override
    public boolean add(Link link) {
        return super.add(owner.linkTo(link));
    }

    @Override
    public void add(int index, Link element) {
        super.add(index, owner.linkTo(element));
    }

    @Override
    public boolean addAll(Collection<? extends Link> c) {
        return super.addAll(c.stream().map(owner::linkTo).collect(toList()));
    }

    @Override
    public boolean addAll(int index, Collection<? extends Link> c) {
        return super.addAll(index, c.stream().map(owner::linkTo).collect(toList()));
    }
}
