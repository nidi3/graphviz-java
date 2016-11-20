/**
 *  An ANTLRv3 capable DOT tree grammar.
 *  Rule "graph" is entry-point, which parses whole AST
 *  and returns graph object.
 *
 *	This grammar is part of CesTa project, http://cesta.sourceforge.net/
 *
 *	BSD licence
 *  Copyright (c) 2010 Tobias Smolka, BUSLAB FI MUNI
 *
 *	All rights reserved.
 *
 *	http://buslab.org
 *
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions
 *	are met:
 *
 *	 1. Redistributions of source code must retain the above copyright
 *		notice, this list of conditions and the following disclaimer.
 *	 2. Redistributions in binary form must reproduce the above copyright
 *		notice, this list of conditions and the following disclaimer in the
 *		documentation and/or other materials provided with the distribution.
 *	 3. The name of the author may not be used to endorse or promote products
 *		derived from this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY BUSLAB FI MUNI ('BUSLAB') ``AS IS''
 *	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *	ARE DISCLAIMED. IN NO EVENT SHALL 'BUSLAB' BE LIABLE FOR ANY DIRECT, INDIRECT,
 *	INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *	LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 *	OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *	LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *	EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
tree grammar DotTree;

options {
    backtrack = true;
    // memoize = true;
    tokenVocab = Dot;
    ASTLabelType = CommonTree;
    output = AST;
}

// used in statements.. convenient way how to point to the latest graph or subgraph
scope GraphOrSubgraph {
    Graph obj;
}

@treeparser::header {
package org.cesta.parsers.dot;

import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Iterator;
}

@treeparser::members {
    /**
     *  Model of graph. Has own attributes and special graph, edge or node attributes,
     *  nodes (even from subgraphs), edges, subgraphs etc.
     */
    public class Graph {

        public Map<String, String> attributes = new HashMap<String, String>();
        public Map<String, String> graphAttributes = new HashMap<String, String>();
        public Map<String, String> edgeAttributes = new HashMap<String, String>();
        public Map<String, String> nodeAttributes = new HashMap<String, String>();

        public Map<String, Node> nodes = new HashMap<String, Node>();
        public List<Edge> edges = new LinkedList<Edge>();
        public Map<String, SubGraph> subGraphsMap = new HashMap<String, SubGraph>();
        public Set<SubGraph> subGraphs = new HashSet<SubGraph>();

        public boolean isStrict = false;
        public boolean isDirected = false;

        public String id;
        public Graph(){

        }
        public Graph(String id){
            this.id = id;
        }
        @Override
        public String toString(){
            return
                "Graph"
                +(id!=null?" "+id:"")
                +(isStrict?" strict":"")
                +(isDirected?" directed":"")
                +","
                +(!attributes.isEmpty()?" attributes: "+attributes:"")
                +(!nodes.isEmpty()?" nodes: "+nodes:"")
            ;
        }
        public Set<Node> getNodes(){
            Set<Node> retNodes = new HashSet<Node>();
            retNodes.addAll(nodes.values());
            return retNodes;
        }
        public Map<Node, List<Node>> getTransitionMap(){
            Map<Node, List<Node>> map = new HashMap<Node, List<Node>>();
            for (Edge e:edges){
                List<NodePair> pairs = e.getNodePairs();
                for (NodePair np:pairs){
                    if (!map.containsKey(np.x))
                        map.put(np.x, new LinkedList<Node>());
                    map.get(np.x).add(np.y);
                }
            }
            return map;
        }
        public List<NodePair> getNodePairs(){
            List<NodePair> pairs = new LinkedList<NodePair>();
            for (Edge e:edges){
                pairs.addAll(e.getNodePairs());
            }
            return pairs;
        }
        @Override
        public boolean equals(Object obj){
            if (!(obj instanceof Graph)) return false;
            Graph g = (Graph)obj;
            return (g.id!=null && g.id.equals(id));
        }

    };
    /**
     *  Subgraphs - special type of Graph.
     */
    public class SubGraph extends Graph {
        public SubGraph(){
            super();
        }
        public SubGraph(String id){
            super(id);
        }
        @Override
        public String toString(){
            return "Sub"+super.toString();
        }
    }
    /**
     *  Model simple node with id and attributes.
     */
    public class Node {
        public String id;
        public Map<String, String> attributes = new HashMap<String, String>();
        public Node(String id){
            setId(id);
        }
        public Node(String id, Map<String, String> attributes){
            setId(id);
            this.attributes = attributes;
        }
        public void setId(String id){
            // remove quotes
            if (id!=null && id.length()>=2 && id.charAt(0)=='"' && id.charAt(id.length()-1)=='"')
                id = id.substring(1,id.length()-1);
            this.id = id;
        }
        @Override
        public boolean equals(Object obj){
            if (!(obj instanceof Node)) return false;
            Node n = (Node)obj;
            return (n.id!=null && n.id.equals(id));
        }
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
        @Override
        public String toString(){
            return "Node "+id
                +(!attributes.isEmpty()?" "+attributes:"");
        }
    }
    /**
     *  Single edge between two nodes.
     */
    public class NodePair {
        public Node x;
        public Node y;
        public NodePair(Node x, Node y){
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString(){
            return "["+x+", "+y+"]";
        }
    }
    /**
     *  Edge, which has attributes and goes through multiple objects (Nodes or SubGraphs)
     */
    public class Edge {
        public Map<String, String> attributes = new HashMap<String, String>();
        public List<Object> nodes = new LinkedList<Object>();
        /**
         *  Returns list of set of nodes, though which the edge goes.
         */
        public List<Set<Node>> getEdgeNodes(){
            List<Set<Node>> edgeNodes = new LinkedList<Set<Node>>();
            for (Object o:nodes){
                Set<Node> nodeSet = new HashSet<Node>();

                if (o instanceof Node) nodeSet.add((Node)o);
                if (o instanceof SubGraph) {
                    SubGraph subGraph = (SubGraph) o;
                    nodeSet.addAll(subGraph.getNodes());
                }
                edgeNodes.add(nodeSet);
            }
            return edgeNodes;
        }
        /**
         *  Returns list of node pairs (simple edges). It splits full edge to
         *  smaller parts.
         */
        public List<NodePair> getNodePairs(){
            List<NodePair> nodePairs = new LinkedList<NodePair>();
            List<Set<Node>> edgeNodes = getEdgeNodes();
            Set<Node> x = null;
            Set<Node> y = null;
            Iterator<Set<Node>> it = edgeNodes.iterator();
            x = it.next();
            while (it.hasNext()){
                y = it.next();
                // create X * Y node pairs
                for (Node xx:x)
                    for (Node yy:y)
                        nodePairs.add(new NodePair(xx,yy));

                x = y;
            }
            return nodePairs;
        }
        @Override
        public String toString(){
            return "Edge "+attributes+" "+getEdgeNodes();
        }
    }
}

graph returns [Graph graphObj]
    scope {
        Graph obj;
    }
    scope GraphOrSubgraph;
    @init {
        $GraphOrSubgraph::obj = new Graph();
        $graphObj = $GraphOrSubgraph::obj;
        $graph::obj = $GraphOrSubgraph::obj;
    }
    :
        ^(GRAPH_ROOT
            graphModifier
            {
                $graphObj.isStrict = $graphModifier.isStrict;
                $graphObj.isDirected = $graphModifier.isDirected;
            }
            ID? { if ($ID!=null) $graphObj.id = $ID.text;}
            stmt_list
        )
    ;

graphModifier returns [boolean isStrict, boolean isDirected]
    :
        (STRICT {$isStrict=true;})? (GRAPH | DIGRAPH {$isDirected=true;})
    ;

stmt_list
    :
        ^(STMT_LIST stmt+)
    ;

stmt
    :
        attr_stmt |
    	subgraph |
    	^(ATTR n=ID EQUAL v=ID)
        {
            $GraphOrSubgraph::obj.attributes.put($n.text.toLowerCase(), $v.text);
        }|
    	edge_stmt |
    	node_stmt
    ;

attr_stmt
    :
        ^( GRAPH a=attr_list { $GraphOrSubgraph::obj.graphAttributes.putAll($a.attributes);}) |
        ^( NODE a=attr_list { $GraphOrSubgraph::obj.nodeAttributes.putAll($a.attributes);}) |
        ^( EDGE a=attr_list { $GraphOrSubgraph::obj.edgeAttributes.putAll($a.attributes);})
    ;

attr_list returns [Map attributes]
    @init {
        $attributes = new HashMap<String, String>();
    }
    :
    	^(ATTR_LIST (attr {$attributes.put($attr.name.toLowerCase(), $attr.value);})*)
    ;

attr returns [String name, String value]
    :
        ^(ATTR n=ID (EQUAL v=ID)?) {$name = $n.text; $value=$v.text;}
    ;

edge_stmt returns [Edge edge]
    scope {
        Edge edgeObj;
    }
    @init {
        $edge = new Edge();
        $edge_stmt::edgeObj = $edge;
        $graph::obj.edges.add($edge);
    }
    :
       ^(EDGE_STMT
           n=node_id
           {
                Node node = null;
                if ($graph::obj.nodes.containsKey($n.id)) {
                    node = $graph::obj.nodes.get($n.id);
                } else {
                    node=new Node($n.id);
                    $graph::obj.nodes.put(node.id, node);
                }
                $GraphOrSubgraph::obj.nodes.put(node.id, node);
                $edge.nodes.add(node);
                // NOTE: the port information should be somehow saved in edge too
                // however, it is ignored right now
           }
           edgeRHS
           attr_list?
           {
                $edge.attributes.putAll($attr_list.attributes);
           }
       )
    |
        ^(EDGE_STMT
            s=subgraph
            {
                $edge.nodes.add($s.subGraphObj);
            }
            edgeRHS
            attr_list?
            {
                $edge.attributes.putAll($attr_list.attributes);
            }
        )

    ;

edgeRHS
    :
        ^(EDGEOP n=node_id
            {
                Node node = null;
                if ($graph::obj.nodes.containsKey($n.id)) {
                    node = $graph::obj.nodes.get($n.id);
                } else {
                    node=new Node($n.id);
                    $graph::obj.nodes.put(node.id, node);
                }
                $GraphOrSubgraph::obj.nodes.put(node.id, node);
                $edge_stmt::edgeObj.nodes.add(node);
            } (edgeRHS)?) |
        ^(EDGEOP subgraph
            {
                $edge_stmt::edgeObj.nodes.add($subgraph.subGraphObj);
            }
            (edgeRHS)?
        )
    ;

node_stmt returns [Node node]
    :
        ^(NODE_STMT n=node_id a=attr_list? {
            if ($graph::obj.nodes.containsKey($n.id)) {
                $node = $graph::obj.nodes.get($n.id);
                $node.attributes.putAll($a.attributes);
            } else {
                $node=new Node($n.id, $a.attributes);
                $graph::obj.nodes.put($node.id, $node);
            }
            // set reference also in subgraph
            $GraphOrSubgraph::obj.nodes.put($node.id, $node);
        })
    ;

node_id returns [String id, String port]
    :
        ^(ident=ID p=port?)
            {$id=$ident.text; $port=$p.text;}
    ;

port
    :
    	ID (VALIDSTR)? |
    	VALIDSTR
    ;

subgraph returns [SubGraph subGraphObj]
    scope GraphOrSubgraph;
    :
    	^(SUBGRAPH_ROOT
            ID?
            {
                if ($ID!=null) {
                    if ($graph::obj.subGraphsMap.containsKey($ID.text)) {
                        $subGraphObj = $graph::obj.subGraphsMap.get($ID.text);
                    } else {
                        $subGraphObj = new SubGraph($ID.text);
                    }
                    $graph::obj.subGraphsMap.put($subGraphObj.id, $subGraphObj);
                } else {
                    $subGraphObj = new SubGraph();
                }
                $graph::obj.subGraphs.add($subGraphObj);
                $GraphOrSubgraph::obj = (Graph)$subGraphObj;
            }
            stmt_list
          )
      |
          ^(SUBGRAPH_ROOT ID {
            if ($graph::obj.subGraphsMap.containsKey($ID.text))
                $subGraphObj = $graph::obj.subGraphsMap.get($ID.text);
            else
                $subGraphObj = new SubGraph($ID.text);

            $graph::obj.subGraphsMap.put($subGraphObj.id, $subGraphObj);
            $graph::obj.subGraphs.add($subGraphObj);
            // not usefull, since this subgraph doesn't define any statements..
            // $GraphOrSubgraph::obj = (Graph)$subGraphObj;
          })
    ;
