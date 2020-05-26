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
package guru.nidi.graphviz

import guru.nidi.graphviz.attribute.*
import guru.nidi.graphviz.model.*

class KraphvizContext(private val ctx: CreationContext) {
    interface AttributeContainer<F : For> {
        operator fun get(vararg attrs: Attributes<out F>)
    }

    val edge = object : AttributeContainer<ForLink> {
        override fun get(vararg attrs: Attributes<out ForLink>) {
            attrs.forEach { ctx.linkAttrs().add(it) }
        }
    }

    val node = object : AttributeContainer<ForNode> {
        override fun get(vararg attrs: Attributes<out ForNode>) {
            attrs.forEach { ctx.nodeAttrs().add(it) }
        }
    }

    val graph = object : AttributeContainer<ForGraph> {
        override fun get(vararg attrs: Attributes<out ForGraph>) {
            attrs.forEach { ctx.graphAttrs().add(it) }
        }
    }

    infix fun String.eq(value: Any): Attributes<ForAll> = Attributes.attr(this, value)

    operator fun MutableNode.minus(target: LinkTarget): Link = addLink(target).links().last()!!
    operator fun MutableNode.minus(node: String): Link = this - Factory.mutNode(node)
    operator fun MutableNode.div(record: String): PortNode = port(record)
    operator fun MutableNode.div(compass: Compass): PortNode = port(compass)
    operator fun MutableNode.get(vararg attrs: Attributes<out ForNode>): MutableNode = add(*attrs)

    operator fun PortNode.minus(target: LinkTarget): Link = links().run {
        add(Factory.between(port(), target))
        last()!!
    }

    operator fun PortNode.minus(node: String): Link = this - Factory.mutNode(node)
    operator fun PortNode.div(record: String): PortNode = port(record)
    operator fun PortNode.div(compass: Compass): PortNode = port(compass)
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

    operator fun Link.minus(node: String): Link = this - Factory.mutNode(node)
    operator fun Link.get(vararg attrs: Attributes<out ForLink>): Link = add(Attributes.attrs(*attrs))

    operator fun String.unaryMinus(): MutableNode = Factory.mutNode(this, true)
    operator fun String.minus(target: LinkTarget): Link = -this - target
    operator fun String.minus(node: String): Link = -this - node
    operator fun String.div(record: String): PortNode = -this / record
    operator fun String.div(compass: Compass): PortNode = -this / compass
    operator fun String.get(vararg attrs: Attributes<out ForNode>): MutableNode = (-this).add(*attrs)
}
