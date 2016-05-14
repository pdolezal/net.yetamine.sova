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
 * Tests {@link AdaptationStrategy}.
 */
public final class TestAdaptationStrategy {

    /**
     * Tests {@link AdaptationStrategy#nullable(Object)}.
     */
    @Test
    public void testNullable() {
        final TestObject i = new TestObject(1024);
        final TestObject f = new TestObject(0);
        final AdaptationStrategy<TestObject> s = new TestStrategy<>(Downcasting.withFallbackTo(TestObject.class, f));
        Assert.assertNull(s.nullable("hello"));
        Assert.assertSame(s.nullable(i), i);
    }

    /**
     * Tests {@link AdaptationStrategy#optional(Object)}.
     */
    @Test
    public void testOptional() {
        final TestObject i = new TestObject(1024);
        final TestObject f = new TestObject(0);
        final AdaptationStrategy<TestObject> s = new TestStrategy<>(Downcasting.withFallbackTo(TestObject.class, f));
        Assert.assertFalse(s.optional("hello").isPresent());
        Assert.assertSame(s.optional(i).get(), i);
    }

    /**
     * Tests {@link AdaptationStrategy#surrogate(Object)}.
     */
    @Test
    public void testSurrogate() {
        final TestObject i = new TestObject(1024);

        final AdaptationStrategy<TestObject> s1 = new TestStrategy<>(Downcasting.to(TestObject.class));
        Assert.assertNull(s1.surrogate("hello"));
        Assert.assertSame(s1.surrogate(i), i);

        final TestObject v = new TestObject(1);
        final AdaptationStrategy<TestObject> s2 = new TestStrategy<>(Downcasting.withFallbackTo(TestObject.class, v));
        Assert.assertEquals(s2.surrogate("hello"), v);
        Assert.assertEquals(s2.surrogate(i), i);
    }

    /**
     * Tests {@link AdaptationStrategy#function()}.
     */
    @Test
    public void testFunction() {
        final TestObject i = new TestObject(1024);

        final AdaptationStrategy<TestObject> s = new TestStrategy<>(Downcasting.to(TestObject.class));
        final Function<Object, AdaptationResult<TestObject>> f = s.function();

        Assert.assertNull(f.andThen(AdaptationResult::get).apply("hello"));
        Assert.assertSame(f.andThen(AdaptationResult::get).apply(i), i);
    }

    /**
     * A testing strategy.
     *
     * @param <T>
     *            the type of resulting values
     */
    private static final class TestStrategy<T> extends AdaptationDelegate<T> implements AdaptationStrategy<T> {

        /**
         * Creates a new instance.
         *
         * @param provider
         *            the actual provider. It must not be {@code null}.
         */
        public TestStrategy(AdaptationProvider<T> provider) {
            super(provider.rtti(), provider.adaptation(), provider.fallback());
        }
    }
}
