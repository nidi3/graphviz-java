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
import guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT
import guru.nidi.graphviz.engine.Format.PNG
import guru.nidi.graphviz.model.Compass.NORTH
import guru.nidi.graphviz.model.Compass.SOUTH
import org.junit.jupiter.api.Test
import java.io.File

class ReadmeTest {
    @Test
    fun simple() {
        //## kotlin
        graph(directed = true) {
            edge["color" eq "red", Arrow.TEE]
            node[Color.GREEN]
            graph[Rank.dir(LEFT_TO_RIGHT)]

            "a" - "b" - "c"
            ("c"[Color.RED] - "d"[Color.BLUE])[Arrow.VEE]
            "d" / NORTH - "e" / SOUTH
        }.toGraphviz().render(PNG).toFile(File("example/ex1.png"))
        //## end
    }
}
