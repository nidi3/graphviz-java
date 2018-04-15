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
@file:JvmName("Kraphviz")

package guru.nidi.graphviz.model

import guru.nidi.graphviz.attribute.Attributes
import guru.nidi.graphviz.attribute.Attributes.attr

fun graph(name: String = "", strict: Boolean = false, directed: Boolean = false, cluster: Boolean = false, config: () -> Unit = { }): Graph {
    val graph = (Factory.graph(name) as MutableGraph).apply {
        isStrict = strict
        isDirected = directed
        isCluster = cluster
    }
    try {
        val ctx = CreationContext.begin()
        config()
//        ctx.applyTo(graph)
    } finally {
        CreationContext.end()
    }
    return graph as Graph
}

infix fun String.to(value: Any) = attr(this, value)

interface AttributeContainer {
    operator fun get(vararg attrs: Attributes)
}

val edge = object : AttributeContainer {
    override fun get(vararg attrs: Attributes) {
        attrs.forEach { attr ->
            CreationContext.current().get().links().add(attr)
        }
    }
}

val node = object : AttributeContainer {
    override fun get(vararg attrs: Attributes) {
        attrs.forEach { attr ->
            CreationContext.current().get().nodes().add(attr)
        }
    }
}

val graph = object : AttributeContainer {
    override fun get(vararg attrs: Attributes) {
        attrs.forEach { attr ->
            CreationContext.current().get().graphs().add(attr)
        }
    }
}

infix fun String.link(node: String) = Factory.node(this).link(node)!!
operator fun String.minus(node: String) = Factory.node(this).link(node)!!
operator fun String.div(record: String) = Factory.node(this).port(record)!!
operator fun String.div(compass: Compass) = Factory.node(this).port(compass)!!

operator fun PortNode.div(compass: Compass) = this.port(compass)!!

infix fun Node.link(node: String) = this.link(node)!!
operator fun Node.minus(node: String) = this.link(node)!!
operator fun PortNode.minus(node: String) = Factory.between(this, Factory.node(node))

infix fun String.link(target: LinkTarget) = Factory.node(this).link(target)!!
operator fun String.get(vararg attrs: Attributes) = Factory.node(this).with(*attrs)!!

operator fun Node.get(vararg attrs: Attributes): Node {
    val n = this.with(*attrs)
//    CreationContext.current().map { it.setNode(n as ImmutableNode) }
    return n
}
