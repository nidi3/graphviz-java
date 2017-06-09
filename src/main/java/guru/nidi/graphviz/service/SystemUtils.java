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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SystemUtils.class);

    public static final String OS_NAME;
    public static final boolean IS_OS_WINDOWS;
    public static final boolean IS_OS_MAC;
    public static final boolean IS_OS_LINUX;

    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException e) {
            LOG.error("Caught a SecurityException reading the system property '{}'."
                    + "The SystemUtils property value will default to null.", property);
            return null;
        }
    }

    private static boolean getOsMatchesName(String osNamePrefix) {
        return isOsNameMatch(OS_NAME, osNamePrefix);
    }

    private static boolean isOsNameMatch(String osName, String osNamePrefix) {
        return osName != null && osName.startsWith(osNamePrefix);
    }

    static {
        OS_NAME = getSystemProperty("os.name");
        IS_OS_MAC = getOsMatchesName("Mac");
        IS_OS_WINDOWS = getOsMatchesName("Windows");
        IS_OS_LINUX = getOsMatchesName("Linux") || getOsMatchesName("LINUX");
    }
}
