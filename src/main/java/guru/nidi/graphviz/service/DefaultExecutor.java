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

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Execute a CommandLine.
 *
 * @author toon
 */
public class DefaultExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutor.class);

    public int execute(CommandLine cmd, File workingDirectory) throws InterruptedException, IOException {
        LOG.info("STARTING COMMAND: " + cmd.toString());

        final ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
        final Executor executor = new org.apache.commons.exec.DefaultExecutor();

        executor.setWatchdog(watchdog);
        if (workingDirectory != null) {
            executor.setWorkingDirectory(workingDirectory);
        }
        LOG.debug("WORKING: " + executor.getWorkingDirectory());

        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        executor.execute(cmd, resultHandler);
        resultHandler.waitFor();

        final int exitCode = resultHandler.getExitValue();
        LOG.info("END COMMAND: " + cmd.toString() + " - EXIT CODE " + exitCode);
        return exitCode;
    }
}
