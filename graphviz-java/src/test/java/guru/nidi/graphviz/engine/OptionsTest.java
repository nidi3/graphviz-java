package guru.nidi.graphviz.engine;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OptionsTest {
    @Test
    void fromJsonMinimal() {
        final Options options = Options.fromJson("{engine:'DOT',format:'PNG'}");
        assertEquals(Options.create().engine(Engine.DOT).format(Format.PNG), options);
    }

    @Test
    void fromJsonEmptyImages() {
        final Options options = Options.fromJson("{engine:'DOT',format:'PNG',totalMemory:'42',yInvert:true,basedir:'hula'}");
        final Options expected = Options.create().engine(Engine.DOT).format(Format.PNG).totalMemory(42).yInvert(true).basedir(new File("hula"));
        assertEquals(expected, options);
    }

    @Test
    void fromJsonOneImage() throws IOException {
        final Options options = Options.fromJson("{engine:'DOT',format:'PNG',images:[{path:'example/ex1.png',width:'550px',height:'100px'}]}");
        final Options expected = Options.create().engine(Engine.DOT).format(Format.PNG).image("example/ex1.png");
        assertEquals(expected, options);
    }

    @Test
    void fromJsonTwoImages() throws IOException {
        final Options options = Options.fromJson("{engine:'DOT',format:'PNG',images:[{path:'example/ex1.png',width:'550px',height:'100px'},{path:'example/ex2.png',width:'900px',height:'964px']}");
        final Options expected = Options.create().engine(Engine.DOT).format(Format.PNG).image("example/ex1.png").image("example/ex2.png");
        assertEquals(expected, options);
    }

    @Test
    void toJsonMinimal() {
        final String s = Options.create().engine(Engine.DOT).format(Format.PNG).toJson(false);
        assertEquals("{format:'svg',engine:'dot',basedir:'" + new File(".").getAbsolutePath() + "',images:[]}", s);
    }

    @Test
    void toJsonEmptyImages() {
        final String s = Options.create().engine(Engine.DOT).format(Format.PNG).totalMemory(42).yInvert(true).basedir(new File("/hula")).toJson(false);
        assertEquals("{format:'svg',engine:'dot',totalMemory:'42',yInvert:true,basedir:'/hula',images:[]}", s);
    }

    @Test
    void toJsonOneImage() {
        final String s = Options.create().engine(Engine.DOT).format(Format.PNG).basedir(new File("example")).image("ex1.png").toJson(false);
        assertEquals("{format:'svg',engine:'dot',basedir:'" + new File("example").getAbsolutePath() + "',images:[{path:'" + new File("example/ex1.png").getAbsolutePath() + "',width:'550px',height:'100px'}]}", s);
    }

    @Test
    void toJsonTwoImages() {
        final String s = Options.create().engine(Engine.DOT).format(Format.PNG).basedir(new File("example")).image("ex1.png").image("ex2.png").toJson(false);
        assertEquals("{format:'svg',engine:'dot',basedir:'" + new File("example").getAbsolutePath() + "',images:[{path:'" + new File("example/ex1.png").getAbsolutePath() + "',width:'550px',height:'100px'},{path:'" + new File("example/ex2.png").getAbsolutePath() + "',width:'900px',height:'964px'}]}", s);
    }

}
