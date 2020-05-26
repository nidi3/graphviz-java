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
import guru.nidi.graphviz.attribute.Color.*
import guru.nidi.graphviz.engine.Format.PNG
import guru.nidi.graphviz.model.Compass.SOUTH
import guru.nidi.graphviz.model.Compass.WEST
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.Link
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class KraphvizTest {
    @Test
    fun simple() {
        val g = graph(directed = true) {
            edge["color" eq "red"]
            edge[Arrow.TEE]
            node[GREEN]
            graph[GREY.background()]
        }

        g {
            "e" - "f"

            ("a"[RED] - "b")[Arrow.VEE]
            ("c" / "rec" / SOUTH)[BLUE] - "d" / WEST
        }

        val h = mutGraph().setDirected(true)
                .graphAttrs().add(GREY.background())
                .add(node("e").with(GREEN).link(Link.to(node("f").with(GREEN)).with(RED, Arrow.TEE)))
                .add(node("a").with(RED).link(Link.to(node("b").with(GREEN)).with(RED, Arrow.VEE)))
                .add(node("c").with(BLUE).link(Link.between(port("rec", SOUTH), node("d").with(GREEN).port(WEST)).with(RED, Arrow.TEE)))
        assertEquals(h.toString(), g.toString())
        g.toGraphviz().render(PNG).toFile(File("target/kt1.png"))
    }

    @Test
    fun complex() {
        val g = graph("example2", directed = true) {
            val main = "<<b>main</b>>"[rgb("1020d0").font()]
            main - ("parse" - "execute")["weight" eq 8]
            (main - "init")[Style.DOTTED]
            main - "cleanup"
            (main - "printf")[Style.BOLD, Label.of("100 times"), RED]
            "execute" - graph {
                -"make a\nstring"
                -"printf"
            }
            ("execute" - "compare"[Shape.RECTANGLE, Style.FILLED, hsv(.7, .3, 1.0)])[RED]
            "init" - "make a\nstring"
        }
        g.toGraphviz().render(PNG).toFile(File("target/kt2.png"))
    }
}
