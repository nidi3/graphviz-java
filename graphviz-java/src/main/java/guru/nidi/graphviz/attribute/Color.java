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
package guru.nidi.graphviz.attribute;

import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.Attributes.attrs;
import static java.util.stream.Collectors.joining;

public class Color extends SingleAttributes<String, ForAll> {
    private Color(String key, String value) {
        super(key, value);
    }

    protected Color(String value) {
        super("color", value);
    }

    public Color fill() {
        return key("fillcolor");
    }

    public Color background() {
        return key("bgcolor");
    }

    public Color font() {
        return key("fontcolor");
    }

    public Color labelFont() {
        return key("labelfontcolor");
    }

    public Color gradient(Color c) {
        return and(c);
    }

    public Color gradient(Color c, double at) {
        return and(c, at);
    }

    public Color and(Color c) {
        return new Color(value + ":" + c.value);
    }

    public Color and(Color... cs) {
        return new Color(value + ":" + Stream.of(cs).map(c -> c.value).collect(joining(":")));
    }

    public Color and(Color c, double at) {
        return new Color(value + ":" + c.value + ";" + at);
    }

    public Attributes<ForAll> angle(int angle) {
        return attrs(this, new SingleAttributes<>("gradientangle", angle));
    }

    public Attributes<ForGraphNode> radial() {
        return radial(0);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Attributes<ForGraphNode> radial(int angle) {
        return attrs((Attributes) this, new SingleAttributes<>("gradientangle", angle), Style.RADIAL);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Attributes<ForGraphNode> striped() {
        return attrs((Attributes) this, Style.STRIPED);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Attributes<ForGraphNode> wedged() {
        return attrs((Attributes) this, Style.WEDGED);
    }

    public static Color rgb(String rgb) {
        final String val = rgb.startsWith("#") ? rgb.substring(1) : rgb;
        if (val.length() != 6) {
            throw new IllegalArgumentException("Must have length 6");
        }
        return new Color("#" + val);
    }

    public static Color rgb(int rgb) {
        return rgb(hex(rgb >> 16) + hex(rgb >> 8) + hex(rgb));
    }

    public static Color rgb(int r, int g, int b) {
        return rgb(hex(r) + hex(g) + hex(b));
    }

    public static Color rgba(String rgba) {
        final String val = rgba.startsWith("#") ? rgba.substring(1) : rgba;
        if (val.length() != 8) {
            throw new IllegalArgumentException("Must have length 8");
        }
        return new Color("#" + val);
    }

    public static Color rgba(int rgba) {
        return rgba(hex(rgba >> 16) + hex(rgba >> 8) + hex(rgba) + hex(rgba >> 24));
    }

    public static Color rgba(int r, int g, int b, int a) {
        return rgb(hex(r) + hex(g) + hex(b) + hex(a));
    }

    private static String hex(int value) {
        final String s = Integer.toHexString(value & 0xff);
        return s.length() == 1 ? "0" + s : s;
    }

    public static Color hsv(double h, double s, double v) {
        if (h < 0 || h > 1 || s < 0 || s > 1 || v < 0 || v > 1) {
            throw new IllegalArgumentException("Values must be 0<=value<=1");
        }
        return new Color(h + " " + s + " " + v);
    }

    public static Color named(String name) {
        return new Color(name);
    }

    public static final Color
            ALICEBLUE = named("aliceblue"), ANTIQUEWHITE = named("antiquewhite"),
            ANTIQUEWHITE1 = named("antiquewhite1"), ANTIQUEWHITE2 = named("antiquewhite2"),
            ANTIQUEWHITE3 = named("antiquewhite3"), ANTIQUEWHITE4 = named("antiquewhite4"),
            AQUAMARINE = named("aquamarine"), AQUAMARINE1 = named("aquamarine1"), AQUAMARINE2 = named("aquamarine2"),
            AQUAMARINE3 = named("aquamarine3"), AQUAMARINE4 = named("aquamarine4"), AZURE = named("azure"),
            AZURE1 = named("azure1"), AZURE2 = named("azure2"), AZURE3 = named("azure3"), AZURE4 = named("azure4"),
            BEIGE = named("beige"), BISQUE = named("bisque"), BISQUE1 = named("bisque1"), BISQUE2 = named("bisque2"),
            BISQUE3 = named("bisque3"), BISQUE4 = named("bisque4"), BLACK = named("black"),
            BLANCHEDALMOND = named("blanchedalmond"), BLUE = named("blue"), BLUE1 = named("blue1"),
            BLUE2 = named("blue2"), BLUE3 = named("blue3"), BLUE4 = named("blue4"), BLUEVIOLET = named("blueviolet"),
            BROWN = named("brown"), BROWN1 = named("brown1"), BROWN2 = named("brown2"), BROWN3 = named("brown3"),
            BROWN4 = named("brown4"), BURLYWOOD = named("burlywood"), BURLYWOOD1 = named("burlywood1"),
            BURLYWOOD2 = named("burlywood2"), BURLYWOOD3 = named("burlywood3"), BURLYWOOD4 = named("burlywood4"),
            CADETBLUE = named("cadetblue"), CADETBLUE1 = named("cadetblue1"), CADETBLUE2 = named("cadetblue2"),
            CADETBLUE3 = named("cadetblue3"), CADETBLUE4 = named("cadetblue4"), CHARTREUSE = named("chartreuse"),
            CHARTREUSE1 = named("chartreuse1"), CHARTREUSE2 = named("chartreuse2"), CHARTREUSE3 = named("chartreuse3"),
            CHARTREUSE4 = named("chartreuse4"), CHOCOLATE = named("chocolate"), CHOCOLATE1 = named("chocolate1"),
            CHOCOLATE2 = named("chocolate2"), CHOCOLATE3 = named("chocolate3"), CHOCOLATE4 = named("chocolate4"),
            CORAL = named("coral"), CORAL1 = named("coral1"), CORAL2 = named("coral2"), CORAL3 = named("coral3"),
            CORAL4 = named("coral4"), CORNFLOWERBLUE = named("cornflowerblue"), CORNSILK = named("cornsilk"),
            CORNSILK1 = named("cornsilk1"), CORNSILK2 = named("cornsilk2"), CORNSILK3 = named("cornsilk3"),
            CORNSILK4 = named("cornsilk4"), CRIMSON = named("crimson"), CYAN = named("cyan"), CYAN1 = named("cyan1"),
            CYAN2 = named("cyan2"), CYAN3 = named("cyan3"), CYAN4 = named("cyan4"),
            DARKGOLDENROD = named("darkgoldenrod"), DARKGOLDENROD1 = named("darkgoldenrod1"),
            DARKGOLDENROD2 = named("darkgoldenrod2"), DARKGOLDENROD3 = named("darkgoldenrod3"),
            DARKGOLDENROD4 = named("darkgoldenrod4"), DARKGREEN = named("darkgreen"), DARKKHAKI = named("darkkhaki"),
            DARKOLIVEGREEN = named("darkolivegreen"),
            DARKOLIVEGREEN1 = named("darkolivegreen1"), DARKOLIVEGREEN2 = named("darkolivegreen2"),
            DARKOLIVEGREEN3 = named("darkolivegreen3"), DARKOLIVEGREEN4 = named("darkolivegreen4"),
            DARKORANGE = named("darkorange"), DARKORANGE1 = named("darkorange1"), DARKORANGE2 = named("darkorange2"),
            DARKORANGE3 = named("darkorange3"), DARKORANGE4 = named("darkorange4"), DARKORCHID = named("darkorchid"),
            DARKORCHID1 = named("darkorchid1"), DARKORCHID2 = named("darkorchid2"), DARKORCHID3 = named("darkorchid3"),
            DARKORCHID4 = named("darkorchid4"), DARKSALMON = named("darksalmon"), DARKSEAGREEN = named("darkseagreen"),
            DARKSEAGREEN1 = named("darkseagreen1"), DARKSEAGREEN2 = named("darkseagreen2"),
            DARKSEAGREEN3 = named("darkseagreen3"), DARKSEAGREEN4 = named("darkseagreen4"),
            DARKSLATEBLUE = named("darkslateblue"), DARKSLATEGRAY = named("darkslategray"),
            DARKSLATEGRAY1 = named("darkslategray1"), DARKSLATEGRAY2 = named("darkslategray2"),
            DARKSLATEGRAY3 = named("darkslategray3"), DARKSLATEGRAY4 = named("darkslategray4"),
            DARKSLATEGREY = named("darkslategrey"), DARKTURQUOISE = named("darkturquoise"),
            DARKVIOLET = named("darkviolet"), DEEPPINK = named("deeppink"), DEEPPINK1 = named("deeppink1"),
            DEEPPINK2 = named("deeppink2"), DEEPPINK3 = named("deeppink3"), DEEPPINK4 = named("deeppink4"),
            DEEPSKYBLUE = named("deepskyblue"), DEEPSKYBLUE1 = named("deepskyblue1"),
            DEEPSKYBLUE2 = named("deepskyblue2"), DEEPSKYBLUE3 = named("deepskyblue3"),
            DEEPSKYBLUE4 = named("deepskyblue4"), DIMGRAY = named("dimgray"), DIMGREY = named("dimgrey"),
            DODGERBLUE = named("dodgerblue"), DODGERBLUE1 = named("dodgerblue1"), DODGERBLUE2 = named("dodgerblue2"),
            DODGERBLUE3 = named("dodgerblue3"), DODGERBLUE4 = named("dodgerblue4"), FIREBRICK = named("firebrick"),
            FIREBRICK1 = named("firebrick1"), FIREBRICK2 = named("firebrick2"), FIREBRICK3 = named("firebrick3"),
            FIREBRICK4 = named("firebrick4"), FLORALWHITE = named("floralwhite"), FORESTGREEN = named("forestgreen"),
            GAINSBORO = named("gainsboro"), GHOSTWHITE = named("ghostwhite"), GOLD = named("gold"),
            GOLD1 = named("gold1"), GOLD2 = named("gold2"), GOLD3 = named("gold3"), GOLD4 = named("gold4"),
            GOLDENROD = named("goldenrod"), GOLDENROD1 = named("goldenrod1"), GOLDENROD2 = named("goldenrod2"),
            GOLDENROD3 = named("goldenrod3"), GOLDENROD4 = named("goldenrod4"), GRAY = named("gray"),
            GRAY0 = named("gray0"), GRAY1 = named("gray1"), GRAY10 = named("gray10"), GRAY100 = named("gray100"),
            GRAY11 = named("gray11"), GRAY12 = named("gray12"), GRAY13 = named("gray13"), GRAY14 = named("gray14"),
            GRAY15 = named("gray15"), GRAY16 = named("gray16"), GRAY17 = named("gray17"), GRAY18 = named("gray18"),
            GRAY19 = named("gray19"), GRAY2 = named("gray2"), GRAY20 = named("gray20"), GRAY21 = named("gray21"),
            GRAY22 = named("gray22"), GRAY23 = named("gray23"), GRAY24 = named("gray24"), GRAY25 = named("gray25"),
            GRAY26 = named("gray26"), GRAY27 = named("gray27"), GRAY28 = named("gray28"), GRAY29 = named("gray29"),
            GRAY3 = named("gray3"), GRAY30 = named("gray30"), GRAY31 = named("gray31"), GRAY32 = named("gray32"),
            GRAY33 = named("gray33"), GRAY34 = named("gray34"), GRAY35 = named("gray35"), GRAY36 = named("gray36"),
            GRAY37 = named("gray37"), GRAY38 = named("gray38"), GRAY39 = named("gray39"), GRAY4 = named("gray4"),
            GRAY40 = named("gray40"), GRAY41 = named("gray41"), GRAY42 = named("gray42"), GRAY43 = named("gray43"),
            GRAY44 = named("gray44"), GRAY45 = named("gray45"), GRAY46 = named("gray46"), GRAY47 = named("gray47"),
            GRAY48 = named("gray48"), GRAY49 = named("gray49"), GRAY5 = named("gray5"), GRAY50 = named("gray50"),
            GRAY51 = named("gray51"), GRAY52 = named("gray52"), GRAY53 = named("gray53"), GRAY54 = named("gray54"),
            GRAY55 = named("gray55"), GRAY56 = named("gray56"), GRAY57 = named("gray57"), GRAY58 = named("gray58"),
            GRAY59 = named("gray59"), GRAY6 = named("gray6"), GRAY60 = named("gray60"), GRAY61 = named("gray61"),
            GRAY62 = named("gray62"), GRAY63 = named("gray63"), GRAY64 = named("gray64"), GRAY65 = named("gray65"),
            GRAY66 = named("gray66"), GRAY67 = named("gray67"), GRAY68 = named("gray68"), GRAY69 = named("gray69"),
            GRAY7 = named("gray7"), GRAY70 = named("gray70"), GRAY71 = named("gray71"), GRAY72 = named("gray72"),
            GRAY73 = named("gray73"), GRAY74 = named("gray74"), GRAY75 = named("gray75"), GRAY76 = named("gray76"),
            GRAY77 = named("gray77"), GRAY78 = named("gray78"), GRAY79 = named("gray79"), GRAY8 = named("gray8"),
            GRAY80 = named("gray80"), GRAY81 = named("gray81"), GRAY82 = named("gray82"), GRAY83 = named("gray83"),
            GRAY84 = named("gray84"), GRAY85 = named("gray85"), GRAY86 = named("gray86"), GRAY87 = named("gray87"),
            GRAY88 = named("gray88"), GRAY89 = named("gray89"), GRAY9 = named("gray9"), GRAY90 = named("gray90"),
            GRAY91 = named("gray91"), GRAY92 = named("gray92"), GRAY93 = named("gray93"), GRAY94 = named("gray94"),
            GRAY95 = named("gray95"), GRAY96 = named("gray96"), GRAY97 = named("gray97"), GRAY98 = named("gray98"),
            GRAY99 = named("gray99"), GREEN = named("green"), GREEN1 = named("green1"), GREEN2 = named("green2"),
            GREEN3 = named("green3"), GREEN4 = named("green4"), GREENYELLOW = named("greenyellow"),
            GREY = named("grey"), GREY0 = named("grey0"), GREY1 = named("grey1"), GREY10 = named("grey10"),
            GREY100 = named("grey100"), GREY11 = named("grey11"), GREY12 = named("grey12"), GREY13 = named("grey13"),
            GREY14 = named("grey14"), GREY15 = named("grey15"), GREY16 = named("grey16"), GREY17 = named("grey17"),
            GREY18 = named("grey18"), GREY19 = named("grey19"), GREY2 = named("grey2"), GREY20 = named("grey20"),
            GREY21 = named("grey21"), GREY22 = named("grey22"), GREY23 = named("grey23"), GREY24 = named("grey24"),
            GREY25 = named("grey25"), GREY26 = named("grey26"), GREY27 = named("grey27"), GREY28 = named("grey28"),
            GREY29 = named("grey29"), GREY3 = named("grey3"), GREY30 = named("grey30"), GREY31 = named("grey31"),
            GREY32 = named("grey32"), GREY33 = named("grey33"), GREY34 = named("grey34"), GREY35 = named("grey35"),
            GREY36 = named("grey36"), GREY37 = named("grey37"), GREY38 = named("grey38"), GREY39 = named("grey39"),
            GREY4 = named("grey4"), GREY40 = named("grey40"), GREY41 = named("grey41"), GREY42 = named("grey42"),
            GREY43 = named("grey43"), GREY44 = named("grey44"), GREY45 = named("grey45"), GREY46 = named("grey46"),
            GREY47 = named("grey47"), GREY48 = named("grey48"), GREY49 = named("grey49"), GREY5 = named("grey5"),
            GREY50 = named("grey50"), GREY51 = named("grey51"), GREY52 = named("grey52"), GREY53 = named("grey53"),
            GREY54 = named("grey54"), GREY55 = named("grey55"), GREY56 = named("grey56"), GREY57 = named("grey57"),
            GREY58 = named("grey58"), GREY59 = named("grey59"), GREY6 = named("grey6"), GREY60 = named("grey60"),
            GREY61 = named("grey61"), GREY62 = named("grey62"), GREY63 = named("grey63"), GREY64 = named("grey64"),
            GREY65 = named("grey65"), GREY66 = named("grey66"), GREY67 = named("grey67"), GREY68 = named("grey68"),
            GREY69 = named("grey69"), GREY7 = named("grey7"), GREY70 = named("grey70"), GREY71 = named("grey71"),
            GREY72 = named("grey72"), GREY73 = named("grey73"), GREY74 = named("grey74"), GREY75 = named("grey75"),
            GREY76 = named("grey76"), GREY77 = named("grey77"), GREY78 = named("grey78"), GREY79 = named("grey79"),
            GREY8 = named("grey8"), GREY80 = named("grey80"), GREY81 = named("grey81"), GREY82 = named("grey82"),
            GREY83 = named("grey83"), GREY84 = named("grey84"), GREY85 = named("grey85"), GREY86 = named("grey86"),
            GREY87 = named("grey87"), GREY88 = named("grey88"), GREY89 = named("grey89"), GREY9 = named("grey9"),
            GREY90 = named("grey90"), GREY91 = named("grey91"), GREY92 = named("grey92"), GREY93 = named("grey93"),
            GREY94 = named("grey94"), GREY95 = named("grey95"), GREY96 = named("grey96"), GREY97 = named("grey97"),
            GREY98 = named("grey98"), GREY99 = named("grey99"), HONEYDEW = named("honeydew"),
            HONEYDEW1 = named("honeydew1"), HONEYDEW2 = named("honeydew2"), HONEYDEW3 = named("honeydew3"),
            HONEYDEW4 = named("honeydew4"), HOTPINK = named("hotpink"), HOTPINK1 = named("hotpink1"),
            HOTPINK2 = named("hotpink2"), HOTPINK3 = named("hotpink3"), HOTPINK4 = named("hotpink4"),
            INDIANRED = named("indianred"), INDIANRED1 = named("indianred1"), INDIANRED2 = named("indianred2"),
            INDIANRED3 = named("indianred3"), INDIANRED4 = named("indianred4"), INDIGO = named("indigo"),
            IVORY = named("ivory"), IVORY1 = named("ivory1"), IVORY2 = named("ivory2"), IVORY3 = named("ivory3"),
            IVORY4 = named("ivory4"), KHAKI = named("khaki"), KHAKI1 = named("khaki1"), KHAKI2 = named("khaki2"),
            KHAKI3 = named("khaki3"), KHAKI4 = named("khaki4"), LAVENDER = named("lavender"),
            LAVENDERBLUSH = named("lavenderblush"), LAVENDERBLUSH1 = named("lavenderblush1"),
            LAVENDERBLUSH2 = named("lavenderblush2"), LAVENDERBLUSH3 = named("lavenderblush3"),
            LAVENDERBLUSH4 = named("lavenderblush4"), LAWNGREEN = named("lawngreen"),
            LEMONCHIFFON = named("lemonchiffon"), LEMONCHIFFON1 = named("lemonchiffon1"),
            LEMONCHIFFON2 = named("lemonchiffon2"), LEMONCHIFFON3 = named("lemonchiffon3"),
            LEMONCHIFFON4 = named("lemonchiffon4"), LIGHTBLUE = named("lightblue"), LIGHTBLUE1 = named("lightblue1"),
            LIGHTBLUE2 = named("lightblue2"), LIGHTBLUE3 = named("lightblue3"), LIGHTBLUE4 = named("lightblue4"),
            LIGHTCORAL = named("lightcoral"), LIGHTCYAN = named("lightcyan"), LIGHTCYAN1 = named("lightcyan1"),
            LIGHTCYAN2 = named("lightcyan2"), LIGHTCYAN3 = named("lightcyan3"), LIGHTCYAN4 = named("lightcyan4"),
            LIGHTGOLDENROD = named("lightgoldenrod"), LIGHTGOLDENROD1 = named("lightgoldenrod1"),
            LIGHTGOLDENROD2 = named("lightgoldenrod2"), LIGHTGOLDENROD3 = named("lightgoldenrod3"),
            LIGHTGOLDENROD4 = named("lightgoldenrod4"), LIGHTGOLDENRODYELLOW = named("lightgoldenrodyellow"),
            LIGHTGRAY = named("lightgray"), LIGHTGREY = named("lightgrey"), LIGHTPINK = named("lightpink"),
            LIGHTPINK1 = named("lightpink1"), LIGHTPINK2 = named("lightpink2"), LIGHTPINK3 = named("lightpink3"),
            LIGHTPINK4 = named("lightpink4"), LIGHTSALMON = named("lightsalmon"), LIGHTSALMON1 = named("lightsalmon1"),
            LIGHTSALMON2 = named("lightsalmon2"), LIGHTSALMON3 = named("lightsalmon3"),
            LIGHTSALMON4 = named("lightsalmon4"), LIGHTSEAGREEN = named("lightseagreen"),
            LIGHTSKYBLUE = named("lightskyblue"), LIGHTSKYBLUE1 = named("lightskyblue1"),
            LIGHTSKYBLUE2 = named("lightskyblue2"), LIGHTSKYBLUE3 = named("lightskyblue3"),
            LIGHTSKYBLUE4 = named("lightskyblue4"), LIGHTSLATEBLUE = named("lightslateblue"),
            LIGHTSLATEGRAY = named("lightslategray"), LIGHTSLATEGREY = named("lightslategrey"),
            LIGHTSTEELBLUE = named("lightsteelblue"), LIGHTSTEELBLUE1 = named("lightsteelblue1"),
            LIGHTSTEELBLUE2 = named("lightsteelblue2"), LIGHTSTEELBLUE3 = named("lightsteelblue3"),
            LIGHTSTEELBLUE4 = named("lightsteelblue4"), LIGHTYELLOW = named("lightyellow"),
            LIGHTYELLOW1 = named("lightyellow1"), LIGHTYELLOW2 = named("lightyellow2"),
            LIGHTYELLOW3 = named("lightyellow3"), LIGHTYELLOW4 = named("lightyellow4"), LIMEGREEN = named("limegreen"),
            LINEN = named("linen"), MAGENTA = named("magenta"), MAGENTA1 = named("magenta1"),
            MAGENTA2 = named("magenta2"), MAGENTA3 = named("magenta3"), MAGENTA4 = named("magenta4"),
            MAROON = named("maroon"), MAROON1 = named("maroon1"), MAROON2 = named("maroon2"),
            MAROON3 = named("maroon3"), MAROON4 = named("maroon4"), MEDIUMAQUAMARINE = named("mediumaquamarine"),
            MEDIUMBLUE = named("mediumblue"), MEDIUMORCHID = named("mediumorchid"),
            MEDIUMORCHID1 = named("mediumorchid1"), MEDIUMORCHID2 = named("mediumorchid2"),
            MEDIUMORCHID3 = named("mediumorchid3"), MEDIUMORCHID4 = named("mediumorchid4"),
            MEDIUMPURPLE = named("mediumpurple"), MEDIUMPURPLE1 = named("mediumpurple1"),
            MEDIUMPURPLE2 = named("mediumpurple2"), MEDIUMPURPLE3 = named("mediumpurple3"),
            MEDIUMPURPLE4 = named("mediumpurple4"), MEDIUMSEAGREEN = named("mediumseagreen"),
            MEDIUMSLATEBLUE = named("mediumslateblue"), MEDIUMSPRINGGREEN = named("mediumspringgreen"),
            MEDIUMTURQUOISE = named("mediumturquoise"), MEDIUMVIOLETRED = named("mediumvioletred"),
            MIDNIGHTBLUE = named("midnightblue"), MINTCREAM = named("mintcream"), MISTYROSE = named("mistyrose"),
            MISTYROSE1 = named("mistyrose1"), MISTYROSE2 = named("mistyrose2"), MISTYROSE3 = named("mistyrose3"),
            MISTYROSE4 = named("mistyrose4"), MOCCASIN = named("moccasin"), NAVAJOWHITE = named("navajowhite"),
            NAVAJOWHITE1 = named("navajowhite1"), NAVAJOWHITE2 = named("navajowhite2"),
            NAVAJOWHITE3 = named("navajowhite3"), NAVAJOWHITE4 = named("navajowhite4"), NAVY = named("navy"),
            NAVYBLUE = named("navyblue"), OLDLACE = named("oldlace"), OLIVEDRAB = named("olivedrab"),
            OLIVEDRAB1 = named("olivedrab1"), OLIVEDRAB2 = named("olivedrab2"), OLIVEDRAB3 = named("olivedrab3"),
            OLIVEDRAB4 = named("olivedrab4"), ORANGE = named("orange"), ORANGE1 = named("orange1"),
            ORANGE2 = named("orange2"), ORANGE3 = named("orange3"), ORANGE4 = named("orange4"),
            ORANGERED = named("orangered"), ORANGERED1 = named("orangered1"), ORANGERED2 = named("orangered2"),
            ORANGERED3 = named("orangered3"), ORANGERED4 = named("orangered4"), ORCHID = named("orchid"),
            ORCHID1 = named("orchid1"), ORCHID2 = named("orchid2"), ORCHID3 = named("orchid3"),
            ORCHID4 = named("orchid4"), PALEGOLDENROD = named("palegoldenrod"), PALEGREEN = named("palegreen"),
            PALEGREEN1 = named("palegreen1"), PALEGREEN2 = named("palegreen2"), PALEGREEN3 = named("palegreen3"),
            PALEGREEN4 = named("palegreen4"), PALETURQUOISE = named("paleturquoise"),
            PALETURQUOISE1 = named("paleturquoise1"), PALETURQUOISE2 = named("paleturquoise2"),
            PALETURQUOISE3 = named("paleturquoise3"), PALETURQUOISE4 = named("paleturquoise4"),
            PALEVIOLETRED = named("palevioletred"), PALEVIOLETRED1 = named("palevioletred1"),
            PALEVIOLETRED2 = named("palevioletred2"), PALEVIOLETRED3 = named("palevioletred3"),
            PALEVIOLETRED4 = named("palevioletred4"), PAPAYAWHIP = named("papayawhip"), PEACHPUFF = named("peachpuff"),
            PEACHPUFF1 = named("peachpuff1"), PEACHPUFF2 = named("peachpuff2"), PEACHPUFF3 = named("peachpuff3"),
            PEACHPUFF4 = named("peachpuff4"), PERU = named("peru"), PINK = named("pink"), PINK1 = named("pink1"),
            PINK2 = named("pink2"), PINK3 = named("pink3"), PINK4 = named("pink4"), PLUM = named("plum"),
            PLUM1 = named("plum1"), PLUM2 = named("plum2"), PLUM3 = named("plum3"), PLUM4 = named("plum4"),
            POWDERBLUE = named("powderblue"), PURPLE = named("purple"), PURPLE1 = named("purple1"),
            PURPLE2 = named("purple2"), PURPLE3 = named("purple3"), PURPLE4 = named("purple4"), RED = named("red"),
            RED1 = named("red1"), RED2 = named("red2"), RED3 = named("red3"), RED4 = named("red4"),
            ROSYBROWN = named("rosybrown"), ROSYBROWN1 = named("rosybrown1"), ROSYBROWN2 = named("rosybrown2"),
            ROSYBROWN3 = named("rosybrown3"), ROSYBROWN4 = named("rosybrown4"), ROYALBLUE = named("royalblue"),
            ROYALBLUE1 = named("royalblue1"), ROYALBLUE2 = named("royalblue2"), ROYALBLUE3 = named("royalblue3"),
            ROYALBLUE4 = named("royalblue4"), SADDLEBROWN = named("saddlebrown"), SALMON = named("salmon"),
            SALMON1 = named("salmon1"), SALMON2 = named("salmon2"), SALMON3 = named("salmon3"),
            SALMON4 = named("salmon4"), SANDYBROWN = named("sandybrown"), SEAGREEN = named("seagreen"),
            SEAGREEN1 = named("seagreen1"), SEAGREEN2 = named("seagreen2"), SEAGREEN3 = named("seagreen3"),
            SEAGREEN4 = named("seagreen4"), SEASHELL = named("seashell"), SEASHELL1 = named("seashell1"),
            SEASHELL2 = named("seashell2"), SEASHELL3 = named("seashell3"), SEASHELL4 = named("seashell4"),
            SIENNA = named("sienna"), SIENNA1 = named("sienna1"), SIENNA2 = named("sienna2"),
            SIENNA3 = named("sienna3"), SIENNA4 = named("sienna4"), SKYBLUE = named("skyblue"),
            SKYBLUE1 = named("skyblue1"), SKYBLUE2 = named("skyblue2"), SKYBLUE3 = named("skyblue3"),
            SKYBLUE4 = named("skyblue4"), SLATEBLUE = named("slateblue"), SLATEBLUE1 = named("slateblue1"),
            SLATEBLUE2 = named("slateblue2"), SLATEBLUE3 = named("slateblue3"), SLATEBLUE4 = named("slateblue4"),
            SLATEGRAY = named("slategray"), SLATEGRAY1 = named("slategray1"), SLATEGRAY2 = named("slategray2"),
            SLATEGRAY3 = named("slategray3"), SLATEGRAY4 = named("slategray4"), SLATEGREY = named("slategrey"),
            SNOW = named("snow"), SNOW1 = named("snow1"), SNOW2 = named("snow2"), SNOW3 = named("snow3"),
            SNOW4 = named("snow4"), SPRINGGREEN = named("springgreen"), SPRINGGREEN1 = named("springgreen1"),
            SPRINGGREEN2 = named("springgreen2"), SPRINGGREEN3 = named("springgreen3"),
            SPRINGGREEN4 = named("springgreen4"), STEELBLUE = named("steelblue"), STEELBLUE1 = named("steelblue1"),
            STEELBLUE2 = named("steelblue2"), STEELBLUE3 = named("steelblue3"), STEELBLUE4 = named("steelblue4"),
            TAN = named("tan"), TAN1 = named("tan1"), TAN2 = named("tan2"), TAN3 = named("tan3"), TAN4 = named("tan4"),
            THISTLE = named("thistle"), THISTLE1 = named("thistle1"), THISTLE2 = named("thistle2"),
            THISTLE3 = named("thistle3"), THISTLE4 = named("thistle4"), TOMATO = named("tomato"),
            TOMATO1 = named("tomato1"), TOMATO2 = named("tomato2"), TOMATO3 = named("tomato3"),
            TOMATO4 = named("tomato4"), TRANSPARENT = named("transparent"), TURQUOISE = named("turquoise"),
            TURQUOISE1 = named("turquoise1"), TURQUOISE2 = named("turquoise2"), TURQUOISE3 = named("turquoise3"),
            TURQUOISE4 = named("turquoise4"), VIOLET = named("violet"), VIOLETRED = named("violetred"),
            VIOLETRED1 = named("violetred1"), VIOLETRED2 = named("violetred2"), VIOLETRED3 = named("violetred3"),
            VIOLETRED4 = named("violetred4"), WHEAT = named("wheat"), WHEAT1 = named("wheat1"),
            WHEAT2 = named("wheat2"), WHEAT3 = named("wheat3"), WHEAT4 = named("wheat4"), WHITE = named("white"),
            WHITESMOKE = named("whitesmoke"), YELLOW = named("yellow"), YELLOW1 = named("yellow1"),
            YELLOW2 = named("yellow2"), YELLOW3 = named("yellow3"), YELLOW4 = named("yellow4"),
            YELLOWGREEN = named("yellowgreen");
}
