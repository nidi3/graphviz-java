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
package guru.nidi.graphviz.rough;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FillStyleTest {
    @Test
    void hachure() {
        assertEquals(map("fillStyle", "hachure"), FillStyle.hachure().values);
        assertEquals(map("fillStyle", "hachure", "hachureAngle", 45.0), FillStyle.hachure().angle(45).values);
        assertEquals(map("fillStyle", "hachure", "hachureGap", 5.0), FillStyle.hachure().gap(5).values);
        assertEquals(map("fillStyle", "hachure", "fillWeight", 10.0), FillStyle.hachure().width(10).values);
    }

    @Test
    void crosshatch() {
        assertEquals(map("fillStyle", "cross-hatch"), FillStyle.crossHatch().values);
        assertEquals(map("fillStyle", "cross-hatch", "hachureAngle", 45.0), FillStyle.crossHatch().angle(45).values);
        assertEquals(map("fillStyle", "cross-hatch", "hachureGap", 5.0), FillStyle.crossHatch().gap(5).values);
        assertEquals(map("fillStyle", "cross-hatch", "fillWeight", 10.0), FillStyle.crossHatch().width(10).values);
    }

    @Test
    void solid() {
        assertEquals(map("fillStyle", "solid"), FillStyle.solid().values);
    }

    @Test
    void zigzag() {
        assertEquals(map("fillStyle", "zigzag"), FillStyle.zigzag().values);
        assertEquals(map("fillStyle", "zigzag", "hachureAngle", 45.0), FillStyle.zigzag().angle(45).values);
        assertEquals(map("fillStyle", "zigzag", "hachureGap", 5.0), FillStyle.zigzag().gap(5).values);
        assertEquals(map("fillStyle", "zigzag", "fillWeight", 10.0), FillStyle.zigzag().width(10).values);
    }

    @Test
    void zigzagLine() {
        assertEquals(map("fillStyle", "zigzag-line"), FillStyle.zigzagLine().values);
        assertEquals(map("fillStyle", "zigzag-line", "hachureAngle", 45.0), FillStyle.zigzagLine().angle(45).values);
        assertEquals(map("fillStyle", "zigzag-line", "hachureGap", 5.0), FillStyle.zigzagLine().gap(5).values);
        assertEquals(map("fillStyle", "zigzag-line", "fillWeight", 10.0), FillStyle.zigzagLine().width(10).values);
        assertEquals(map("fillStyle", "zigzag-line", "zigzagOffset", 22.0), FillStyle.zigzagLine().size(22).values);
    }

    @Test
    void dots() {
        assertEquals(map("fillStyle", "dots"), FillStyle.dots().values);
        assertEquals(map("fillStyle", "dots", "fillWeight", 22.0), FillStyle.dots().size(22).values);
    }

    @Test
    void starburst() {
        assertEquals(map("fillStyle", "starburst"), FillStyle.starburst().values);
        assertEquals(map("fillStyle", "starburst", "hachureGap", 5.0), FillStyle.starburst().gap(5).values);
        assertEquals(map("fillStyle", "starburst", "fillWeight", 10.0), FillStyle.starburst().width(10).values);
    }

    @Test
    void dashed() {
        assertEquals(map("fillStyle", "dashed"), FillStyle.dashed().values);
        assertEquals(map("fillStyle", "dashed", "hachureAngle", 45.0), FillStyle.dashed().angle(45).values);
        assertEquals(map("fillStyle", "dashed", "hachureGap", 5.0), FillStyle.dashed().gap(5).values);
        assertEquals(map("fillStyle", "dashed", "fillWeight", 10.0), FillStyle.dashed().width(10).values);
        assertEquals(map("fillStyle", "dashed", "dashOffset", 22.0), FillStyle.dashed().length(22).values);
    }

    private Map<String, Object> map(Object... vs) {
        final Map<String, Object> res = new HashMap<>();
        for (int i = 0; i < vs.length; i += 2) {
            res.put((String) vs[i], vs[i + 1]);
        }
        return res;
    }

}
