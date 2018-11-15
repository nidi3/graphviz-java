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

package guru.nidi.graphviz

import guru.nidi.graphviz.attribute.*
import guru.nidi.graphviz.attribute.Attributes.attr
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.*
import guru.nidi.graphviz.model.Factory.*

fun graph(name: String = "", strict: Boolean = false, directed: Boolean = false, cluster: Boolean = false, config: () -> Unit = { }) =
        mutGraph(name).apply {
            isStrict = strict
            isDirected = directed
            isCluster = cluster
            use { _, _ ->
                config()
            }
        }

infix fun String.eq(value: Any): Attributes<ForAll> = attr(this, value)

interface AttributeContainer<F : For> {
    operator fun get(vararg attrs: Attributes<out F>)
}

val edge = object : AttributeContainer<ForLink> {
    override fun get(vararg attrs: Attributes<out ForLink>) {
        val linkAttrs = CreationContext.get().linkAttrs()
        attrs.forEach { linkAttrs.add(it) }
    }
}

val node = object : AttributeContainer<ForNode> {
    override fun get(vararg attrs: Attributes<out ForNode>) {
        val nodeAttrs = CreationContext.get().nodeAttrs()
        attrs.forEach { nodeAttrs.add(it) }
    }
}

val graph = object : AttributeContainer<ForGraph> {
    override fun get(vararg attrs: Attributes<out ForGraph>) {
        val graphAttrs = CreationContext.get().graphAttrs()
        attrs.forEach { graphAttrs.add(it) }
    }
}

operator fun MutableNode.minus(target: LinkTarget) = addLink(target).links().last()!!
operator fun MutableNode.minus(node: String) = this - mutNode(node)
operator fun MutableNode.div(record: String) = port(record)
operator fun MutableNode.div(compass: Compass) = port(compass)
operator fun MutableNode.get(vararg attrs: Attributes<out ForNode>) = add(*attrs)

operator fun PortNode.minus(target: LinkTarget) = links().run {
    add(between(port(), target))
    last()!!
}

operator fun PortNode.minus(node: String) = this - mutNode(node)
operator fun PortNode.div(record: String) = port(record)
operator fun PortNode.div(compass: Compass) = port(compass)
operator fun PortNode.get(vararg attrs: Attributes<out ForNode>): PortNode {
    (node() as MutableNode).add(*attrs)
    return this
}

operator fun Link.minus(target: LinkTarget): Link {
    val source = to().asLinkSource()
    return source.links().run {
        add(source.linkTo(target))
        last()!!
    }
}

operator fun Link.minus(node: String) = this - mutNode(node)
operator fun Link.get(vararg attrs: Attributes<out ForLink>) = add(Attributes.attrs(*attrs))

operator fun String.unaryMinus() = mutNode(this, true)
operator fun String.minus(target: LinkTarget) = -this - target
operator fun String.minus(node: String) = -this - node
operator fun String.div(record: String) = -this / record
operator fun String.div(compass: Compass) = -this / compass
operator fun String.get(vararg attrs: Attributes<out ForNode>) = (-this).add(*attrs)

fun MutableGraph.toGraphviz() = Graphviz.fromGraph(this)