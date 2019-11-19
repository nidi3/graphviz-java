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
package guru.nidi.graphviz.model;

import javax.annotation.Nullable;
import java.util.Objects;

public class Port {
    @Nullable
    private final String record;
    @Nullable
    private final Compass compass;

    Port(@Nullable String record, @Nullable Compass compass) {
        this.record = record;
        this.compass = compass;
    }

    @Nullable
    public String record() {
        return record;
    }

    @Nullable
    public Compass compass() {
        return compass;
    }

    public boolean isEmpty() {
        return record == null && compass == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Port port = (Port) o;
        return Objects.equals(record, port.record)
                && compass == port.compass;
    }

    @Override
    public int hashCode() {
        return Objects.hash(record, compass);
    }

    @Override
    public String toString() {
        return ":" + (record == null ? "" : record) + ":" + (compass == null ? "" : compass);
    }
}
