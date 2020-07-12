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
package guru.nidi.graphviz.engine;

import org.junit.jupiter.api.Test;

import java.io.File;

import static guru.nidi.graphviz.service.SystemUtils.uriPathOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OptionsTest {
    @Test
    void fromJsonMinimal() {
        final Options options = Options.fromJson("{engine:'DOT',format:'PNG'}");
        assertEquals(Options.create().engine(Engine.DOT).format(Format.PNG), options);
    }

    @Test
    void fromJsonNoImage() {
        final Options options = Options.fromJson("{engine:'DOT',format:'PNG',totalMemory:'42',yInvert:true,basedir:'hula'}");
        final Options expected = Options.create().engine(Engine.DOT).format(Format.PNG).totalMemory(42).yInvert(true).basedir(new File("hula"));
        assertEquals(expected, options);
    }

    @Test
    void fromJsonEmptyImages() {
        final Options options = Options.fromJson("{engine:'DOT',format:'PNG',totalMemory:'42',yInvert:true,basedir:'hula',images:[]}");
        final Options expected = Options.create().engine(Engine.DOT).format(Format.PNG).totalMemory(42).yInvert(true).basedir(new File("hula"));
        assertEquals(expected, options);
    }

    @Test
    void fromJsonOneImage() {
        final File file = new File("example/ex1.png");
        final Options options = Options.fromJson("{engine:'DOT',format:'PNG',images:["
                + "{path:'" + uriPathOf(file) + "',width:'550px',height:'100px'}]}");
        final Options expected = Options.create().engine(Engine.DOT).format(Format.PNG).image(uriPathOf(file));
        assertEquals(expected, options);
    }

    @Test
    void fromJsonTwoImages() {
        final File file1 = new File("example/ex1.png");
        final File file2 = new File("example/ex2.png");
        final Options options = Options.fromJson("{engine:'DOT',format:'PNG',images:["
                + "{path:'" + uriPathOf(file1) + "',width:'550px',height:'100px'},"
                + "{path:'" + uriPathOf(file2) + "',width:'900px',height:'964px']}");
        final Options expected = Options.create().engine(Engine.DOT).format(Format.PNG).image(uriPathOf(file1)).image(uriPathOf(file2));
        assertEquals(expected, options);
    }

    @Test
    void toJsonMinimal() {
        final String s = Options.create().engine(Engine.DOT).format(Format.PNG).toJson(false);
        assertEquals("{format:'svg',engine:'dot',basedir:'" + uriPathOf(new File(".")) + "',images:[]}", s);
    }

    @Test
    void toJsonEmptyImages() {
        final File basedir = new File("/hula");
        final String s = Options.create().engine(Engine.DOT).format(Format.PNG).totalMemory(42).yInvert(true).basedir(basedir).toJson(false);
        assertEquals("{format:'svg',engine:'dot',totalMemory:'42',yInvert:true,basedir:'" + uriPathOf(basedir) + "',images:[]}", s);
    }

    @Test
    void toJsonOneImage() {
        final String s = Options.create().engine(Engine.DOT).format(Format.PNG).basedir(new File("example")).image("ex1.png").toJson(false);
        assertEquals("{format:'svg',engine:'dot',basedir:'" + uriPathOf(new File("example")) + "',images:[" +
                "{path:'" + uriPathOf(new File("example/ex1.png")) + "',width:'550px',height:'100px'}]}", s);
    }

    @Test
    void toJsonTwoImages() {
        final String s = Options.create().engine(Engine.DOT).format(Format.PNG).basedir(new File("example")).image("ex1.png").image("ex2.png").toJson(false);
        assertEquals("{format:'svg',engine:'dot',basedir:'" + uriPathOf(new File("example")) + "',images:["
                + "{path:'" + uriPathOf(new File("example/ex1.png")) + "',width:'550px',height:'100px'},"
                + "{path:'" + uriPathOf(new File("example/ex2.png")) + "',width:'900px',height:'964px'}]}", s);
    }

}
