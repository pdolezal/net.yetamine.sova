/*
 * Copyright 2016 Yetamine
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

package net.yetamine.sova;

import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Adaptation}.
 */
public final class TestAdaptation {

    /**
     * Tests {@link Adaptation#attempt(Object)}.
     */
    @Test
    public void testAttempt() {
        final MockObject o = new MockObject(1024);
        final Adaptation<MockObject> adaptation = adaptation();
        Assert.assertFalse(adaptation.attempt(null).isPresent());
        Assert.assertFalse(adaptation.attempt("no").isPresent());
        Assert.assertSame(adaptation.attempt(o).get(), o);
    }

    /**
     * Tests {@link Adaptation#filter(java.util.function.Predicate)}.
     */
    @Test
    public void testFilter() {
        final MockObject o = new MockObject(1024);

        final Adaptation<MockObject> adaptation = adaptation().filter(n -> {
            final Object value = n.value();
            return (value instanceof Integer) && ((Integer) value > 0);
        });

        Assert.assertFalse(adaptation.attempt(null).isPresent());
        Assert.assertFalse(adaptation.attempt("no").isPresent());
        Assert.assertFalse(adaptation.attempt(new MockObject(0)).isPresent());
        Assert.assertEquals(adaptation.attempt(o).get(), o);
    }

    /**
     * Tests {@link Adaptation#map(java.util.function.Function)}.
     */
    @Test
    public void testMap() {
        final int i = 1024;
        final MockObject o = new MockObject(i);

        final Function<Object, MockObject> map = adaptation().map(n -> {
            if (n == null) {
                return n;
            }

            final Object value = n.value();
            return (value instanceof Integer) ? new MockObject(-((Integer) value)) : n;
        });

        Assert.assertNull(map.apply(null));
        Assert.assertNull(map.apply("no"));
        Assert.assertEquals(map.apply(o), new MockObject(-i));
    }

    /**
     * Returns the testing adaptation that adapts to {@link MockObject}.
     *
     * @return the testing adaptation
     */
    private static Adaptation<MockObject> adaptation() {
        return o -> (o instanceof MockObject) ? (MockObject) o : null;
    }
}
