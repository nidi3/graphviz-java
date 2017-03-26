/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.graphviz.attribute;

public final class Font {
    private Font() {
    }

    public static Attribute[] def(String name, int size) {
        return new Attribute[]{name(name), size(size)};
    }

    public static Attribute name(String name) {
        return new MapAttribute("fontname", name);
    }

    public static Attribute size(int size) {
        return new MapAttribute("fontsize", size);
    }
}

