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
package guru.nidi.graphviz.service;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;

/**
 * Execute a CommandLine.
 *
 * @author toon
 */
public class CommandLineExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(CommandLineExecutor.class);

    public void execute(CommandLine cmd, @Nullable File workingDirectory, int timeout)
            throws InterruptedException, IOException {
        LOG.info("executing command {}", cmd.toString());

        final ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
        final Executor executor = new DefaultExecutor();

        executor.setWatchdog(watchdog);
        if (workingDirectory != null) {
            executor.setWorkingDirectory(workingDirectory);
        }
        LOG.debug("workdir: {}", executor.getWorkingDirectory());

        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(out, err));
        executor.execute(cmd, resultHandler);
        resultHandler.waitFor();

        final int exitCode = resultHandler.getExitValue();
        if (out.size() > 0) {
            LOG.info(out.toString());
        }
        if (exitCode != 0) {
            final String msg = executor.getWatchdog().killedProcess()
                    ? "Command took too long to execute, try setting a higher timout: '" + cmd + "'"
                    : err.size() == 0 ? "Command didn't succeed: '" + cmd + "'" : err.toString();
            throw new IOException(msg);
        }
    }
}
