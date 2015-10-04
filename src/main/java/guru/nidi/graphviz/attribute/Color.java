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

/**
 *
 */
public class Color extends SimpleAttribute<String> {

    protected Color(String value) {
        super("color", value);
    }

    public Attribute fill() {
        return key("fillcolor");
    }

    public Attribute background() {
        return key("bgcolor");
    }

    public Attribute font() {
        return key("fontcolor");
    }

    public Attribute labelFont() {
        return key("labelfontcolor");
    }

    public static Color rgb(String rgb) {
        if (rgb.length() != 6) {
            throw new IllegalArgumentException("Must have length 6");
        }
        return new Color("#" + rgb);
    }

    public static Color rgba(String rgba) {
        if (rgba.length() != 8) {
            throw new IllegalArgumentException("Must have length 8");
        }
        return new Color("#" + rgba);
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
            ALICEBLUE = Color.named("aliceblue"), ANTIQUEWHITE = Color.named("antiquewhite"), ANTIQUEWHITE1 = Color.named("antiquewhite1"), ANTIQUEWHITE2 = Color.named("antiquewhite2"), ANTIQUEWHITE3 = Color.named("antiquewhite3"),
            ANTIQUEWHITE4 = Color.named("antiquewhite4"), AQUAMARINE = Color.named("aquamarine"), AQUAMARINE1 = Color.named("aquamarine1"), AQUAMARINE2 = Color.named("aquamarine2"), AQUAMARINE3 = Color.named("aquamarine3"),
            AQUAMARINE4 = Color.named("aquamarine4"), AZURE = Color.named("azure"), AZURE1 = Color.named("azure1"), AZURE2 = Color.named("azure2"), AZURE3 = Color.named("azure3"),
            AZURE4 = Color.named("azure4"), BEIGE = Color.named("beige"), BISQUE = Color.named("bisque"), BISQUE1 = Color.named("bisque1"), BISQUE2 = Color.named("bisque2"),
            BISQUE3 = Color.named("bisque3"), BISQUE4 = Color.named("bisque4"), BLACK = Color.named("black"), BLANCHEDALMOND = Color.named("blanchedalmond"), BLUE = Color.named("blue"),
            BLUE1 = Color.named("blue1"), BLUE2 = Color.named("blue2"), BLUE3 = Color.named("blue3"), BLUE4 = Color.named("blue4"), BLUEVIOLET = Color.named("blueviolet"),
            BROWN = Color.named("brown"), BROWN1 = Color.named("brown1"), BROWN2 = Color.named("brown2"), BROWN3 = Color.named("brown3"), BROWN4 = Color.named("brown4"),
            BURLYWOOD = Color.named("burlywood"), BURLYWOOD1 = Color.named("burlywood1"), BURLYWOOD2 = Color.named("burlywood2"), BURLYWOOD3 = Color.named("burlywood3"), BURLYWOOD4 = Color.named("burlywood4"),
            CADETBLUE = Color.named("cadetblue"), CADETBLUE1 = Color.named("cadetblue1"), CADETBLUE2 = Color.named("cadetblue2"), CADETBLUE3 = Color.named("cadetblue3"), CADETBLUE4 = Color.named("cadetblue4"),
            CHARTREUSE = Color.named("chartreuse"), CHARTREUSE1 = Color.named("chartreuse1"), CHARTREUSE2 = Color.named("chartreuse2"), CHARTREUSE3 = Color.named("chartreuse3"), CHARTREUSE4 = Color.named("chartreuse4"),
            CHOCOLATE = Color.named("chocolate"), CHOCOLATE1 = Color.named("chocolate1"), CHOCOLATE2 = Color.named("chocolate2"), CHOCOLATE3 = Color.named("chocolate3"), CHOCOLATE4 = Color.named("chocolate4"),
            CORAL = Color.named("coral"), CORAL1 = Color.named("coral1"), CORAL2 = Color.named("coral2"), CORAL3 = Color.named("coral3"), CORAL4 = Color.named("coral4"),
            CORNFLOWERBLUE = Color.named("cornflowerblue"), CORNSILK = Color.named("cornsilk"), CORNSILK1 = Color.named("cornsilk1"), CORNSILK2 = Color.named("cornsilk2"), CORNSILK3 = Color.named("cornsilk3"),
            CORNSILK4 = Color.named("cornsilk4"), CRIMSON = Color.named("crimson"), CYAN = Color.named("cyan"), CYAN1 = Color.named("cyan1"), CYAN2 = Color.named("cyan2"),
            CYAN3 = Color.named("cyan3"), CYAN4 = Color.named("cyan4"), DARKGOLDENROD = Color.named("darkgoldenrod"), DARKGOLDENROD1 = Color.named("darkgoldenrod1"), DARKGOLDENROD2 = Color.named("darkgoldenrod2"),
            DARKGOLDENROD3 = Color.named("darkgoldenrod3"), DARKGOLDENROD4 = Color.named("darkgoldenrod4"), DARKGREEN = Color.named("darkgreen"), DARKKHAKI = Color.named("darkkhaki"), DARKOLIVEGREEN = Color.named("darkolivegreen"),
            DARKOLIVEGREEN1 = Color.named("darkolivegreen1"), DARKOLIVEGREEN2 = Color.named("darkolivegreen2"), DARKOLIVEGREEN3 = Color.named("darkolivegreen3"), DARKOLIVEGREEN4 = Color.named("darkolivegreen4"), DARKORANGE = Color.named("darkorange"),
            DARKORANGE1 = Color.named("darkorange1"), DARKORANGE2 = Color.named("darkorange2"), DARKORANGE3 = Color.named("darkorange3"), DARKORANGE4 = Color.named("darkorange4"), DARKORCHID = Color.named("darkorchid"),
            DARKORCHID1 = Color.named("darkorchid1"), DARKORCHID2 = Color.named("darkorchid2"), DARKORCHID3 = Color.named("darkorchid3"), DARKORCHID4 = Color.named("darkorchid4"), DARKSALMON = Color.named("darksalmon"),
            DARKSEAGREEN = Color.named("darkseagreen"), DARKSEAGREEN1 = Color.named("darkseagreen1"), DARKSEAGREEN2 = Color.named("darkseagreen2"), DARKSEAGREEN3 = Color.named("darkseagreen3"), DARKSEAGREEN4 = Color.named("darkseagreen4"),
            DARKSLATEBLUE = Color.named("darkslateblue"), DARKSLATEGRAY = Color.named("darkslategray"), DARKSLATEGRAY1 = Color.named("darkslategray1"), DARKSLATEGRAY2 = Color.named("darkslategray2"), DARKSLATEGRAY3 = Color.named("darkslategray3"),
            DARKSLATEGRAY4 = Color.named("darkslategray4"), DARKSLATEGREY = Color.named("darkslategrey"), DARKTURQUOISE = Color.named("darkturquoise"), DARKVIOLET = Color.named("darkviolet"), DEEPPINK = Color.named("deeppink"),
            DEEPPINK1 = Color.named("deeppink1"), DEEPPINK2 = Color.named("deeppink2"), DEEPPINK3 = Color.named("deeppink3"), DEEPPINK4 = Color.named("deeppink4"), DEEPSKYBLUE = Color.named("deepskyblue"),
            DEEPSKYBLUE1 = Color.named("deepskyblue1"), DEEPSKYBLUE2 = Color.named("deepskyblue2"), DEEPSKYBLUE3 = Color.named("deepskyblue3"), DEEPSKYBLUE4 = Color.named("deepskyblue4"), DIMGRAY = Color.named("dimgray"),
            DIMGREY = Color.named("dimgrey"), DODGERBLUE = Color.named("dodgerblue"), DODGERBLUE1 = Color.named("dodgerblue1"), DODGERBLUE2 = Color.named("dodgerblue2"), DODGERBLUE3 = Color.named("dodgerblue3"),
            DODGERBLUE4 = Color.named("dodgerblue4"), FIREBRICK = Color.named("firebrick"), FIREBRICK1 = Color.named("firebrick1"), FIREBRICK2 = Color.named("firebrick2"), FIREBRICK3 = Color.named("firebrick3"),
            FIREBRICK4 = Color.named("firebrick4"), FLORALWHITE = Color.named("floralwhite"), FORESTGREEN = Color.named("forestgreen"), GAINSBORO = Color.named("gainsboro"), GHOSTWHITE = Color.named("ghostwhite"),
            GOLD = Color.named("gold"), GOLD1 = Color.named("gold1"), GOLD2 = Color.named("gold2"), GOLD3 = Color.named("gold3"), GOLD4 = Color.named("gold4"),
            GOLDENROD = Color.named("goldenrod"), GOLDENROD1 = Color.named("goldenrod1"), GOLDENROD2 = Color.named("goldenrod2"), GOLDENROD3 = Color.named("goldenrod3"), GOLDENROD4 = Color.named("goldenrod4"),
            GRAY = Color.named("gray"), GRAY0 = Color.named("gray0"), GRAY1 = Color.named("gray1"), GRAY10 = Color.named("gray10"), GRAY100 = Color.named("gray100"),
            GRAY11 = Color.named("gray11"), GRAY12 = Color.named("gray12"), GRAY13 = Color.named("gray13"), GRAY14 = Color.named("gray14"), GRAY15 = Color.named("gray15"),
            GRAY16 = Color.named("gray16"), GRAY17 = Color.named("gray17"), GRAY18 = Color.named("gray18"), GRAY19 = Color.named("gray19"), GRAY2 = Color.named("gray2"),
            GRAY20 = Color.named("gray20"), GRAY21 = Color.named("gray21"), GRAY22 = Color.named("gray22"), GRAY23 = Color.named("gray23"), GRAY24 = Color.named("gray24"),
            GRAY25 = Color.named("gray25"), GRAY26 = Color.named("gray26"), GRAY27 = Color.named("gray27"), GRAY28 = Color.named("gray28"), GRAY29 = Color.named("gray29"),
            GRAY3 = Color.named("gray3"), GRAY30 = Color.named("gray30"), GRAY31 = Color.named("gray31"), GRAY32 = Color.named("gray32"), GRAY33 = Color.named("gray33"),
            GRAY34 = Color.named("gray34"), GRAY35 = Color.named("gray35"), GRAY36 = Color.named("gray36"), GRAY37 = Color.named("gray37"), GRAY38 = Color.named("gray38"),
            GRAY39 = Color.named("gray39"), GRAY4 = Color.named("gray4"), GRAY40 = Color.named("gray40"), GRAY41 = Color.named("gray41"), GRAY42 = Color.named("gray42"),
            GRAY43 = Color.named("gray43"), GRAY44 = Color.named("gray44"), GRAY45 = Color.named("gray45"), GRAY46 = Color.named("gray46"), GRAY47 = Color.named("gray47"),
            GRAY48 = Color.named("gray48"), GRAY49 = Color.named("gray49"), GRAY5 = Color.named("gray5"), GRAY50 = Color.named("gray50"), GRAY51 = Color.named("gray51"),
            GRAY52 = Color.named("gray52"), GRAY53 = Color.named("gray53"), GRAY54 = Color.named("gray54"), GRAY55 = Color.named("gray55"), GRAY56 = Color.named("gray56"),
            GRAY57 = Color.named("gray57"), GRAY58 = Color.named("gray58"), GRAY59 = Color.named("gray59"), GRAY6 = Color.named("gray6"), GRAY60 = Color.named("gray60"),
            GRAY61 = Color.named("gray61"), GRAY62 = Color.named("gray62"), GRAY63 = Color.named("gray63"), GRAY64 = Color.named("gray64"), GRAY65 = Color.named("gray65"),
            GRAY66 = Color.named("gray66"), GRAY67 = Color.named("gray67"), GRAY68 = Color.named("gray68"), GRAY69 = Color.named("gray69"), GRAY7 = Color.named("gray7"),
            GRAY70 = Color.named("gray70"), GRAY71 = Color.named("gray71"), GRAY72 = Color.named("gray72"), GRAY73 = Color.named("gray73"), GRAY74 = Color.named("gray74"),
            GRAY75 = Color.named("gray75"), GRAY76 = Color.named("gray76"), GRAY77 = Color.named("gray77"), GRAY78 = Color.named("gray78"), GRAY79 = Color.named("gray79"),
            GRAY8 = Color.named("gray8"), GRAY80 = Color.named("gray80"), GRAY81 = Color.named("gray81"), GRAY82 = Color.named("gray82"), GRAY83 = Color.named("gray83"),
            GRAY84 = Color.named("gray84"), GRAY85 = Color.named("gray85"), GRAY86 = Color.named("gray86"), GRAY87 = Color.named("gray87"), GRAY88 = Color.named("gray88"),
            GRAY89 = Color.named("gray89"), GRAY9 = Color.named("gray9"), GRAY90 = Color.named("gray90"), GRAY91 = Color.named("gray91"), GRAY92 = Color.named("gray92"),
            GRAY93 = Color.named("gray93"), GRAY94 = Color.named("gray94"), GRAY95 = Color.named("gray95"), GRAY96 = Color.named("gray96"), GRAY97 = Color.named("gray97"),
            GRAY98 = Color.named("gray98"), GRAY99 = Color.named("gray99"), GREEN = Color.named("green"), GREEN1 = Color.named("green1"), GREEN2 = Color.named("green2"),
            GREEN3 = Color.named("green3"), GREEN4 = Color.named("green4"), GREENYELLOW = Color.named("greenyellow"), GREY = Color.named("grey"), GREY0 = Color.named("grey0"),
            GREY1 = Color.named("grey1"), GREY10 = Color.named("grey10"), GREY100 = Color.named("grey100"), GREY11 = Color.named("grey11"), GREY12 = Color.named("grey12"),
            GREY13 = Color.named("grey13"), GREY14 = Color.named("grey14"), GREY15 = Color.named("grey15"), GREY16 = Color.named("grey16"), GREY17 = Color.named("grey17"),
            GREY18 = Color.named("grey18"), GREY19 = Color.named("grey19"), GREY2 = Color.named("grey2"), GREY20 = Color.named("grey20"), GREY21 = Color.named("grey21"),
            GREY22 = Color.named("grey22"), GREY23 = Color.named("grey23"), GREY24 = Color.named("grey24"), GREY25 = Color.named("grey25"), GREY26 = Color.named("grey26"),
            GREY27 = Color.named("grey27"), GREY28 = Color.named("grey28"), GREY29 = Color.named("grey29"), GREY3 = Color.named("grey3"), GREY30 = Color.named("grey30"),
            GREY31 = Color.named("grey31"), GREY32 = Color.named("grey32"), GREY33 = Color.named("grey33"), GREY34 = Color.named("grey34"), GREY35 = Color.named("grey35"),
            GREY36 = Color.named("grey36"), GREY37 = Color.named("grey37"), GREY38 = Color.named("grey38"), GREY39 = Color.named("grey39"), GREY4 = Color.named("grey4"),
            GREY40 = Color.named("grey40"), GREY41 = Color.named("grey41"), GREY42 = Color.named("grey42"), GREY43 = Color.named("grey43"), GREY44 = Color.named("grey44"),
            GREY45 = Color.named("grey45"), GREY46 = Color.named("grey46"), GREY47 = Color.named("grey47"), GREY48 = Color.named("grey48"), GREY49 = Color.named("grey49"),
            GREY5 = Color.named("grey5"), GREY50 = Color.named("grey50"), GREY51 = Color.named("grey51"), GREY52 = Color.named("grey52"), GREY53 = Color.named("grey53"),
            GREY54 = Color.named("grey54"), GREY55 = Color.named("grey55"), GREY56 = Color.named("grey56"), GREY57 = Color.named("grey57"), GREY58 = Color.named("grey58"),
            GREY59 = Color.named("grey59"), GREY6 = Color.named("grey6"), GREY60 = Color.named("grey60"), GREY61 = Color.named("grey61"), GREY62 = Color.named("grey62"),
            GREY63 = Color.named("grey63"), GREY64 = Color.named("grey64"), GREY65 = Color.named("grey65"), GREY66 = Color.named("grey66"), GREY67 = Color.named("grey67"),
            GREY68 = Color.named("grey68"), GREY69 = Color.named("grey69"), GREY7 = Color.named("grey7"), GREY70 = Color.named("grey70"), GREY71 = Color.named("grey71"),
            GREY72 = Color.named("grey72"), GREY73 = Color.named("grey73"), GREY74 = Color.named("grey74"), GREY75 = Color.named("grey75"), GREY76 = Color.named("grey76"),
            GREY77 = Color.named("grey77"), GREY78 = Color.named("grey78"), GREY79 = Color.named("grey79"), GREY8 = Color.named("grey8"), GREY80 = Color.named("grey80"),
            GREY81 = Color.named("grey81"), GREY82 = Color.named("grey82"), GREY83 = Color.named("grey83"), GREY84 = Color.named("grey84"), GREY85 = Color.named("grey85"),
            GREY86 = Color.named("grey86"), GREY87 = Color.named("grey87"), GREY88 = Color.named("grey88"), GREY89 = Color.named("grey89"), GREY9 = Color.named("grey9"),
            GREY90 = Color.named("grey90"), GREY91 = Color.named("grey91"), GREY92 = Color.named("grey92"), GREY93 = Color.named("grey93"), GREY94 = Color.named("grey94"),
            GREY95 = Color.named("grey95"), GREY96 = Color.named("grey96"), GREY97 = Color.named("grey97"), GREY98 = Color.named("grey98"), GREY99 = Color.named("grey99"),
            HONEYDEW = Color.named("honeydew"), HONEYDEW1 = Color.named("honeydew1"), HONEYDEW2 = Color.named("honeydew2"), HONEYDEW3 = Color.named("honeydew3"), HONEYDEW4 = Color.named("honeydew4"),
            HOTPINK = Color.named("hotpink"), HOTPINK1 = Color.named("hotpink1"), HOTPINK2 = Color.named("hotpink2"), HOTPINK3 = Color.named("hotpink3"), HOTPINK4 = Color.named("hotpink4"),
            INDIANRED = Color.named("indianred"), INDIANRED1 = Color.named("indianred1"), INDIANRED2 = Color.named("indianred2"), INDIANRED3 = Color.named("indianred3"), INDIANRED4 = Color.named("indianred4"),
            INDIGO = Color.named("indigo"), IVORY = Color.named("ivory"), IVORY1 = Color.named("ivory1"), IVORY2 = Color.named("ivory2"), IVORY3 = Color.named("ivory3"),
            IVORY4 = Color.named("ivory4"), KHAKI = Color.named("khaki"), KHAKI1 = Color.named("khaki1"), KHAKI2 = Color.named("khaki2"), KHAKI3 = Color.named("khaki3"),
            KHAKI4 = Color.named("khaki4"), LAVENDER = Color.named("lavender"), LAVENDERBLUSH = Color.named("lavenderblush"), LAVENDERBLUSH1 = Color.named("lavenderblush1"), LAVENDERBLUSH2 = Color.named("lavenderblush2"),
            LAVENDERBLUSH3 = Color.named("lavenderblush3"), LAVENDERBLUSH4 = Color.named("lavenderblush4"), LAWNGREEN = Color.named("lawngreen"), LEMONCHIFFON = Color.named("lemonchiffon"), LEMONCHIFFON1 = Color.named("lemonchiffon1"),
            LEMONCHIFFON2 = Color.named("lemonchiffon2"), LEMONCHIFFON3 = Color.named("lemonchiffon3"), LEMONCHIFFON4 = Color.named("lemonchiffon4"), LIGHTBLUE = Color.named("lightblue"), LIGHTBLUE1 = Color.named("lightblue1"),
            LIGHTBLUE2 = Color.named("lightblue2"), LIGHTBLUE3 = Color.named("lightblue3"), LIGHTBLUE4 = Color.named("lightblue4"), LIGHTCORAL = Color.named("lightcoral"), LIGHTCYAN = Color.named("lightcyan"),
            LIGHTCYAN1 = Color.named("lightcyan1"), LIGHTCYAN2 = Color.named("lightcyan2"), LIGHTCYAN3 = Color.named("lightcyan3"), LIGHTCYAN4 = Color.named("lightcyan4"), LIGHTGOLDENROD = Color.named("lightgoldenrod"),
            LIGHTGOLDENROD1 = Color.named("lightgoldenrod1"), LIGHTGOLDENROD2 = Color.named("lightgoldenrod2"), LIGHTGOLDENROD3 = Color.named("lightgoldenrod3"), LIGHTGOLDENROD4 = Color.named("lightgoldenrod4"), LIGHTGOLDENRODYELLOW = Color.named("lightgoldenrodyellow"),
            LIGHTGRAY = Color.named("lightgray"), LIGHTGREY = Color.named("lightgrey"), LIGHTPINK = Color.named("lightpink"), LIGHTPINK1 = Color.named("lightpink1"), LIGHTPINK2 = Color.named("lightpink2"),
            LIGHTPINK3 = Color.named("lightpink3"), LIGHTPINK4 = Color.named("lightpink4"), LIGHTSALMON = Color.named("lightsalmon"), LIGHTSALMON1 = Color.named("lightsalmon1"), LIGHTSALMON2 = Color.named("lightsalmon2"),
            LIGHTSALMON3 = Color.named("lightsalmon3"), LIGHTSALMON4 = Color.named("lightsalmon4"), LIGHTSEAGREEN = Color.named("lightseagreen"), LIGHTSKYBLUE = Color.named("lightskyblue"), LIGHTSKYBLUE1 = Color.named("lightskyblue1"),
            LIGHTSKYBLUE2 = Color.named("lightskyblue2"), LIGHTSKYBLUE3 = Color.named("lightskyblue3"), LIGHTSKYBLUE4 = Color.named("lightskyblue4"), LIGHTSLATEBLUE = Color.named("lightslateblue"), LIGHTSLATEGRAY = Color.named("lightslategray"),
            LIGHTSLATEGREY = Color.named("lightslategrey"), LIGHTSTEELBLUE = Color.named("lightsteelblue"), LIGHTSTEELBLUE1 = Color.named("lightsteelblue1"), LIGHTSTEELBLUE2 = Color.named("lightsteelblue2"), LIGHTSTEELBLUE3 = Color.named("lightsteelblue3"),
            LIGHTSTEELBLUE4 = Color.named("lightsteelblue4"), LIGHTYELLOW = Color.named("lightyellow"), LIGHTYELLOW1 = Color.named("lightyellow1"), LIGHTYELLOW2 = Color.named("lightyellow2"), LIGHTYELLOW3 = Color.named("lightyellow3"),
            LIGHTYELLOW4 = Color.named("lightyellow4"), LIMEGREEN = Color.named("limegreen"), LINEN = Color.named("linen"), MAGENTA = Color.named("magenta"), MAGENTA1 = Color.named("magenta1"),
            MAGENTA2 = Color.named("magenta2"), MAGENTA3 = Color.named("magenta3"), MAGENTA4 = Color.named("magenta4"), MAROON = Color.named("maroon"), MAROON1 = Color.named("maroon1"),
            MAROON2 = Color.named("maroon2"), MAROON3 = Color.named("maroon3"), MAROON4 = Color.named("maroon4"), MEDIUMAQUAMARINE = Color.named("mediumaquamarine"), MEDIUMBLUE = Color.named("mediumblue"),
            MEDIUMORCHID = Color.named("mediumorchid"), MEDIUMORCHID1 = Color.named("mediumorchid1"), MEDIUMORCHID2 = Color.named("mediumorchid2"), MEDIUMORCHID3 = Color.named("mediumorchid3"), MEDIUMORCHID4 = Color.named("mediumorchid4"),
            MEDIUMPURPLE = Color.named("mediumpurple"), MEDIUMPURPLE1 = Color.named("mediumpurple1"), MEDIUMPURPLE2 = Color.named("mediumpurple2"), MEDIUMPURPLE3 = Color.named("mediumpurple3"), MEDIUMPURPLE4 = Color.named("mediumpurple4"),
            MEDIUMSEAGREEN = Color.named("mediumseagreen"), MEDIUMSLATEBLUE = Color.named("mediumslateblue"), MEDIUMSPRINGGREEN = Color.named("mediumspringgreen"), MEDIUMTURQUOISE = Color.named("mediumturquoise"), MEDIUMVIOLETRED = Color.named("mediumvioletred"),
            MIDNIGHTBLUE = Color.named("midnightblue"), MINTCREAM = Color.named("mintcream"), MISTYROSE = Color.named("mistyrose"), MISTYROSE1 = Color.named("mistyrose1"), MISTYROSE2 = Color.named("mistyrose2"),
            MISTYROSE3 = Color.named("mistyrose3"), MISTYROSE4 = Color.named("mistyrose4"), MOCCASIN = Color.named("moccasin"), NAVAJOWHITE = Color.named("navajowhite"), NAVAJOWHITE1 = Color.named("navajowhite1"),
            NAVAJOWHITE2 = Color.named("navajowhite2"), NAVAJOWHITE3 = Color.named("navajowhite3"), NAVAJOWHITE4 = Color.named("navajowhite4"), NAVY = Color.named("navy"), NAVYBLUE = Color.named("navyblue"),
            OLDLACE = Color.named("oldlace"), OLIVEDRAB = Color.named("olivedrab"), OLIVEDRAB1 = Color.named("olivedrab1"), OLIVEDRAB2 = Color.named("olivedrab2"), OLIVEDRAB3 = Color.named("olivedrab3"),
            OLIVEDRAB4 = Color.named("olivedrab4"), ORANGE = Color.named("orange"), ORANGE1 = Color.named("orange1"), ORANGE2 = Color.named("orange2"), ORANGE3 = Color.named("orange3"),
            ORANGE4 = Color.named("orange4"), ORANGERED = Color.named("orangered"), ORANGERED1 = Color.named("orangered1"), ORANGERED2 = Color.named("orangered2"), ORANGERED3 = Color.named("orangered3"),
            ORANGERED4 = Color.named("orangered4"), ORCHID = Color.named("orchid"), ORCHID1 = Color.named("orchid1"), ORCHID2 = Color.named("orchid2"), ORCHID3 = Color.named("orchid3"),
            ORCHID4 = Color.named("orchid4"), PALEGOLDENROD = Color.named("palegoldenrod"), PALEGREEN = Color.named("palegreen"), PALEGREEN1 = Color.named("palegreen1"), PALEGREEN2 = Color.named("palegreen2"),
            PALEGREEN3 = Color.named("palegreen3"), PALEGREEN4 = Color.named("palegreen4"), PALETURQUOISE = Color.named("paleturquoise"), PALETURQUOISE1 = Color.named("paleturquoise1"), PALETURQUOISE2 = Color.named("paleturquoise2"),
            PALETURQUOISE3 = Color.named("paleturquoise3"), PALETURQUOISE4 = Color.named("paleturquoise4"), PALEVIOLETRED = Color.named("palevioletred"), PALEVIOLETRED1 = Color.named("palevioletred1"), PALEVIOLETRED2 = Color.named("palevioletred2"),
            PALEVIOLETRED3 = Color.named("palevioletred3"), PALEVIOLETRED4 = Color.named("palevioletred4"), PAPAYAWHIP = Color.named("papayawhip"), PEACHPUFF = Color.named("peachpuff"), PEACHPUFF1 = Color.named("peachpuff1"),
            PEACHPUFF2 = Color.named("peachpuff2"), PEACHPUFF3 = Color.named("peachpuff3"), PEACHPUFF4 = Color.named("peachpuff4"), PERU = Color.named("peru"), PINK = Color.named("pink"),
            PINK1 = Color.named("pink1"), PINK2 = Color.named("pink2"), PINK3 = Color.named("pink3"), PINK4 = Color.named("pink4"), PLUM = Color.named("plum"),
            PLUM1 = Color.named("plum1"), PLUM2 = Color.named("plum2"), PLUM3 = Color.named("plum3"), PLUM4 = Color.named("plum4"), POWDERBLUE = Color.named("powderblue"),
            PURPLE = Color.named("purple"), PURPLE1 = Color.named("purple1"), PURPLE2 = Color.named("purple2"), PURPLE3 = Color.named("purple3"), PURPLE4 = Color.named("purple4"),
            RED = Color.named("red"), RED1 = Color.named("red1"), RED2 = Color.named("red2"), RED3 = Color.named("red3"), RED4 = Color.named("red4"),
            ROSYBROWN = Color.named("rosybrown"), ROSYBROWN1 = Color.named("rosybrown1"), ROSYBROWN2 = Color.named("rosybrown2"), ROSYBROWN3 = Color.named("rosybrown3"), ROSYBROWN4 = Color.named("rosybrown4"),
            ROYALBLUE = Color.named("royalblue"), ROYALBLUE1 = Color.named("royalblue1"), ROYALBLUE2 = Color.named("royalblue2"), ROYALBLUE3 = Color.named("royalblue3"), ROYALBLUE4 = Color.named("royalblue4"),
            SADDLEBROWN = Color.named("saddlebrown"), SALMON = Color.named("salmon"), SALMON1 = Color.named("salmon1"), SALMON2 = Color.named("salmon2"), SALMON3 = Color.named("salmon3"),
            SALMON4 = Color.named("salmon4"), SANDYBROWN = Color.named("sandybrown"), SEAGREEN = Color.named("seagreen"), SEAGREEN1 = Color.named("seagreen1"), SEAGREEN2 = Color.named("seagreen2"),
            SEAGREEN3 = Color.named("seagreen3"), SEAGREEN4 = Color.named("seagreen4"), SEASHELL = Color.named("seashell"), SEASHELL1 = Color.named("seashell1"), SEASHELL2 = Color.named("seashell2"),
            SEASHELL3 = Color.named("seashell3"), SEASHELL4 = Color.named("seashell4"), SIENNA = Color.named("sienna"), SIENNA1 = Color.named("sienna1"), SIENNA2 = Color.named("sienna2"),
            SIENNA3 = Color.named("sienna3"), SIENNA4 = Color.named("sienna4"), SKYBLUE = Color.named("skyblue"), SKYBLUE1 = Color.named("skyblue1"), SKYBLUE2 = Color.named("skyblue2"),
            SKYBLUE3 = Color.named("skyblue3"), SKYBLUE4 = Color.named("skyblue4"), SLATEBLUE = Color.named("slateblue"), SLATEBLUE1 = Color.named("slateblue1"), SLATEBLUE2 = Color.named("slateblue2"),
            SLATEBLUE3 = Color.named("slateblue3"), SLATEBLUE4 = Color.named("slateblue4"), SLATEGRAY = Color.named("slategray"), SLATEGRAY1 = Color.named("slategray1"), SLATEGRAY2 = Color.named("slategray2"),
            SLATEGRAY3 = Color.named("slategray3"), SLATEGRAY4 = Color.named("slategray4"), SLATEGREY = Color.named("slategrey"), SNOW = Color.named("snow"), SNOW1 = Color.named("snow1"),
            SNOW2 = Color.named("snow2"), SNOW3 = Color.named("snow3"), SNOW4 = Color.named("snow4"), SPRINGGREEN = Color.named("springgreen"), SPRINGGREEN1 = Color.named("springgreen1"),
            SPRINGGREEN2 = Color.named("springgreen2"), SPRINGGREEN3 = Color.named("springgreen3"), SPRINGGREEN4 = Color.named("springgreen4"), STEELBLUE = Color.named("steelblue"), STEELBLUE1 = Color.named("steelblue1"),
            STEELBLUE2 = Color.named("steelblue2"), STEELBLUE3 = Color.named("steelblue3"), STEELBLUE4 = Color.named("steelblue4"), TAN = Color.named("tan"), TAN1 = Color.named("tan1"),
            TAN2 = Color.named("tan2"), TAN3 = Color.named("tan3"), TAN4 = Color.named("tan4"), THISTLE = Color.named("thistle"), THISTLE1 = Color.named("thistle1"),
            THISTLE2 = Color.named("thistle2"), THISTLE3 = Color.named("thistle3"), THISTLE4 = Color.named("thistle4"), TOMATO = Color.named("tomato"), TOMATO1 = Color.named("tomato1"),
            TOMATO2 = Color.named("tomato2"), TOMATO3 = Color.named("tomato3"), TOMATO4 = Color.named("tomato4"), TRANSPARENT = Color.named("transparent"), TURQUOISE = Color.named("turquoise"),
            TURQUOISE1 = Color.named("turquoise1"), TURQUOISE2 = Color.named("turquoise2"), TURQUOISE3 = Color.named("turquoise3"), TURQUOISE4 = Color.named("turquoise4"), VIOLET = Color.named("violet"),
            VIOLETRED = Color.named("violetred"), VIOLETRED1 = Color.named("violetred1"), VIOLETRED2 = Color.named("violetred2"), VIOLETRED3 = Color.named("violetred3"), VIOLETRED4 = Color.named("violetred4"),
            WHEAT = Color.named("wheat"), WHEAT1 = Color.named("wheat1"), WHEAT2 = Color.named("wheat2"), WHEAT3 = Color.named("wheat3"), WHEAT4 = Color.named("wheat4"),
            WHITE = Color.named("white"), WHITESMOKE = Color.named("whitesmoke"), YELLOW = Color.named("yellow"), YELLOW1 = Color.named("yellow1"), YELLOW2 = Color.named("yellow2"),
            YELLOW3 = Color.named("yellow3"), YELLOW4 = Color.named("yellow4"), YELLOWGREEN = Color.named("yellowgreen");
}
