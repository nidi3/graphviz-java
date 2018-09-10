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
import guru.nidi.graphviz.engine.Format.PNG
import guru.nidi.graphviz.model.*
import guru.nidi.graphviz.model.Compass.SOUTH
import org.junit.jupiter.api.Test
import java.io.File

class KraphvizTest {
    @Test
    fun simple() {
        val g = graph(directed = true) {
            edge["color" eq "red"]
            edge[Arrow.TEE]
            node[Color.GREEN]
            graph[Color.GREY.background()]

            "e" - "f"

            ("a"[Color.RED] - "b")[Arrow.VEE]
            "c" / "rec" / SOUTH - "d" / Compass.WEST
        }
        println(g)
        g.toGraphviz().render(PNG).toFile(File("target/kt1.png"))
    }

    @Test
    fun complex() {
        val g = graph("example2", directed = true) {
            val main = "<<b>main</b>>"[Color.rgb("1020d0").font()]
            main - ("parse" - "execute")["weight" eq 8]
            (main - "init")[Style.DOTTED]
            main - "cleanup"
            (main - "printf")[Style.BOLD, Label.of("100 times"), Color.RED]
            "execute" - graph() {
                -"make a\nstring"
                -"printf"
            }
            ("execute" - "compare"[Shape.RECTANGLE, Style.FILLED, Color.hsv(.7, .3, 1.0)])[Color.RED]
            "init" - "make a\nstring"
        }
        g.toGraphviz().render(PNG).toFile(File("target/kt2.png"))
    }
}
