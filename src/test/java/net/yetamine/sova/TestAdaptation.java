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
        final TestObject o = new TestObject(1024);
        final Adaptation<TestObject> adaptation = adaptation();
        Assert.assertFalse(adaptation.attempt(null).isPresent());
        Assert.assertFalse(adaptation.attempt("no").isPresent());
        Assert.assertSame(adaptation.attempt(o).get(), o);
    }

    /**
     * Tests {@link Adaptation#filter(java.util.function.Predicate)}.
     */
    @Test
    public void testFilter() {
        final TestObject o = new TestObject(1024);

        final Adaptation<TestObject> adaptation = adaptation().filter(n -> {
            final Object value = n.value();
            return (value instanceof Integer) && ((Integer) value > 0);
        });

        Assert.assertFalse(adaptation.attempt(null).isPresent());
        Assert.assertFalse(adaptation.attempt("no").isPresent());
        Assert.assertFalse(adaptation.attempt(new TestObject(0)).isPresent());
        Assert.assertEquals(adaptation.attempt(o).get(), o);
    }

    /**
     * Tests {@link Adaptation#map(java.util.function.Function)}.
     */
    @Test
    public void testMap() {
        final int i = 1024;
        final TestObject o = new TestObject(i);

        final Function<Object, TestObject> map = adaptation().map(n -> {
            if (n == null) {
                return n;
            }

            final Object value = n.value();
            return (value instanceof Integer) ? new TestObject(-((Integer) value)) : n;
        });

        Assert.assertNull(map.apply(null));
        Assert.assertNull(map.apply("no"));
        Assert.assertEquals(map.apply(o), new TestObject(-i));
    }

    /**
     * Returns the testing adaptation that adapts to {@link TestObject}.
     *
     * @return the testing adaptation
     */
    private static Adaptation<TestObject> adaptation() {
        return o -> (o instanceof TestObject) ? (TestObject) o : null;
    }
}
