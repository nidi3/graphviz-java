package guru.nidi.graphviz

import guru.nidi.graphviz.attribute.Arrow
import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.engine.Format.PNG
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.*
import guru.nidi.graphviz.model.Compass.SOUTH
import org.junit.jupiter.api.Test
import java.io.File

class KraphvizTest {
    @Test
    fun simple() {
        val g = graph(directed = true) {
            edge["color" to "red"]
            edge[Arrow.TEE]
            node[Color.GREEN]
            graph[Color.GREY.background()]

            ("a"[Color.RED] - "b")[Arrow.VEE]
            "c" / "rec" / SOUTH - "d"
        }
        Graphviz.fromGraph(g).render(PNG).toFile(File("target/kt1.png"))
    }
}
