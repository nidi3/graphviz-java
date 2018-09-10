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
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.Factory.mutGraph
import guru.nidi.graphviz.model.Factory.mutNode

fun graph(name: String = "", strict: Boolean = false, directed: Boolean = false, cluster: Boolean = false, config: () -> Unit = { }) =
        mutGraph(name).apply {
            isStrict = strict
            isDirected = directed
            isCluster = cluster
            use { _, _ ->
                config()
            }
        }

infix fun String.eq(value: Any) = attr(this, value)

interface AttributeContainer {
    operator fun get(vararg attrs: Attributes)
}

val edge = object : AttributeContainer {
    override fun get(vararg attrs: Attributes) {
        val linkAttrs = CreationContext.get().linkAttrs()
        attrs.forEach { linkAttrs.add(it) }
    }
}

val node = object : AttributeContainer {
    override fun get(vararg attrs: Attributes) {
        val nodeAttrs = CreationContext.get().nodeAttrs()
        attrs.forEach { nodeAttrs.add(it) }
    }
}

val graph = object : AttributeContainer {
    override fun get(vararg attrs: Attributes) {
        val graphAttrs = CreationContext.get().graphAttrs()
        attrs.forEach { graphAttrs.add(it) }
    }
}

operator fun MutableNode.minus(target: LinkTarget) = addLink(target).links.last()!!
operator fun MutableNode.minus(node: String) = this - mutNode(node)
operator fun MutableNode.div(record: String) = withRecord(record)
operator fun MutableNode.div(compass: Compass) = withCompass(compass)
operator fun MutableNode.get(vararg attrs: Attributes) = add(*attrs)

operator fun MutablePortNode.minus(target: LinkTarget) = addLink(target).links.last()!!
operator fun MutablePortNode.minus(node: String) = this - mutNode(node)
operator fun MutablePortNode.div(record: String) = setRecord(record)
operator fun MutablePortNode.div(compass: Compass) = setCompass(compass)
operator fun MutablePortNode.get(vararg attrs: Attributes) = node!!.add(*attrs)

operator fun Link.minus(target: LinkTarget): Link {
    val source = to.asLinkSource()
    source.links().add(source.linkTo(target))
    return source.links().last()
}

operator fun Link.minus(node: String) = this - mutNode(node)
operator fun Link.get(vararg attrs: Attributes) = add(Attributes.attrs(*attrs))

operator fun String.unaryMinus() = mutNode(this, true)
operator fun String.minus(target: LinkTarget) = -this - target
operator fun String.minus(node: String) = -this - node
operator fun String.div(record: String) = -this / record
operator fun String.div(compass: Compass) = -this / compass
operator fun String.get(vararg attrs: Attributes) = (-this).add(*attrs)

fun MutableGraph.toGraphviz() = Graphviz.fromGraph(this)