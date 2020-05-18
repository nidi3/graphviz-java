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

import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.function.Consumer;

class BatikRasterizer extends SvgRasterizer {
    @Override
    public BufferedImage doRasterize(Graphviz graphviz, Consumer<Graphics2D> graphicsConfigurer, String svg) {
        final BufferedImage[] image = new BufferedImage[1];
        final TranscoderInput in = new TranscoderInput(new StringReader(svg));
        try {
            final TranscoderOutput out = new TranscoderOutput(new OutputStream() {
                @Override
                public void write(int b) {
                }
            });
            final PNGTranscoder t = new PNGTranscoder() {
                @Override
                public BufferedImage createImage(int width, int height) {
                    return image[0] = super.createImage(width, height);
                }
            };
            t.transcode(in, out);
            return image[0];
        } catch (TranscoderException e) {
            throw new GraphvizException("Error during rasterization", e);
        }
    }
}
