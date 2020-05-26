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

import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.Factory.mutGraph
import guru.nidi.graphviz.model.MutableGraph

fun graph(
    name: String = "",
    strict: Boolean = false,
    directed: Boolean = false,
    cluster: Boolean = false,
    config: KraphvizContext.() -> Unit = { }
): MutableGraph =
        mutGraph(name).apply {
            isStrict = strict
            isDirected = directed
            isCluster = cluster
            use { _, ctx ->
                config(KraphvizContext(ctx))
            }
        }

operator fun MutableGraph.invoke(config: KraphvizContext.() -> Unit): MutableGraph =
        reuse { _, ctx ->
            config(KraphvizContext(ctx))
        }

fun MutableGraph.toGraphviz(): Graphviz = Graphviz.fromGraph(this)
