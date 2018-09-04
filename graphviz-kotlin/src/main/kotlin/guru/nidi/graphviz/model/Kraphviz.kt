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
import guru.nidi.graphviz.model.Factory.*

fun graph(name: String = "", strict: Boolean = false, directed: Boolean = false, cluster: Boolean = false, config: () -> Unit = { }) =
        mutGraph(name).apply {
            isStrict = strict
            isDirected = directed
            isCluster = cluster
            CreationContext.use {
                config()
            }
        }

infix fun String.eq(value: Any) = attr(this, value)!!

interface AttributeContainer {
    operator fun get(vararg attrs: Attributes)
}

val edge = object : AttributeContainer {
    override fun get(vararg attrs: Attributes) {
        attrs.forEach { attr ->
            CreationContext.get().linkAttrs().add(attr)
        }
    }
}

val node = object : AttributeContainer {
    override fun get(vararg attrs: Attributes) {
        attrs.forEach { attr ->
            CreationContext.get().nodeAttrs().add(attr)
        }
    }
}

val graph = object : AttributeContainer {
    override fun get(vararg attrs: Attributes) {
        attrs.forEach { attr ->
            CreationContext.get().graphAttrs().add(attr)
        }
    }
}

infix fun String.link(node: String) = mutNode(this).addLink(node).links.last()!!
infix fun String.link(target: LinkTarget) = mutNode(this).addLink(target)!!
operator fun String.minus(node: String) = mutNode(this).addLink(mutNode(node))!!
operator fun String.div(record: String) = mutNode(this).withRecord(record)!!
operator fun String.div(compass: Compass) = mutNode(this).withCompass(compass)!!
operator fun String.get(vararg attrs: Attributes) = mutNode(this).add(*attrs)!!

operator fun MutablePortNode.div(compass: Compass) = this.setCompass(compass)!!

infix fun MutableNode.link(node: String) = this.addLink(node)!!
operator fun MutableNode.minus(node: String) = this.addLink(node)!!
operator fun MutablePortNode.minus(node: String) = between(this, mutNode(node))!!

operator fun MutableNode.get(vararg attrs: Attributes): MutableNode {
    val n = this.add(*attrs)
//    CreationContext.current().map { it.setNode(n as ImmutableNode) }
    return n
}
