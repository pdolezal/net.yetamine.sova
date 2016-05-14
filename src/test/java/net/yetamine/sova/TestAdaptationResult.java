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

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link AdaptationResult}.
 */
public final class TestAdaptationResult {

    /**
     * Tests {@link AdaptationResult#argument()}.
     */
    @Test
    public void testArgument() {
        final TestObject o = new TestObject(1024);
        Assert.assertNull(AdaptationResult.of(null, o).argument());
        Assert.assertSame(AdaptationResult.of((Object) o, null).argument(), o);
    }

    /**
     * Tests {@link AdaptationResult#get()}.
     */
    @Test
    public void testGet() {
        final TestObject o = new TestObject(1024);
        Assert.assertNull(AdaptationResult.of(o, null).get());
        Assert.assertSame(AdaptationResult.of(null, o).get(), o);
    }

    /**
     * Tests "presence" methods.
     */
    @Test
    public void testPresence() {
        final TestObject i = new TestObject(1024);
        Assert.assertFalse(AdaptationResult.of(i, null).isPresent());
        AdaptationResult.of(i, null).ifPresent(o -> Assert.fail());

        Assert.assertTrue(AdaptationResult.of(null, i).isPresent());
        final AtomicBoolean done = new AtomicBoolean();
        AdaptationResult.of(null, i).ifPresent(o -> {
            Assert.assertSame(o, i);
            done.set(true);
        });

        Assert.assertTrue(done.get());
    }

    /**
     * Tests {@link AdaptationResult#optional()}.
     */
    @Test
    public void testOptional() {
        final TestObject i = new TestObject(1024);
        Assert.assertFalse(AdaptationResult.of(i, null).optional().isPresent());
        AdaptationResult.of(i, null).optional().ifPresent(o -> Assert.fail());

        Assert.assertTrue(AdaptationResult.of(null, i).optional().isPresent());
        Assert.assertSame(AdaptationResult.of(null, i).optional().get(), i);
    }

    /**
     * Tests {@link AdaptationResult#fallback()}.
     */
    @Test
    public void testFallback() {
        final TestObject i = new TestObject(1024);
        final TestObject f = new TestObject(1025);

        final AdaptationResult<TestObject> iar = AdaptationResult.of(i, f);
        final AdaptationResult<TestObject> iarf = iar.fallback();
        Assert.assertSame(iarf.argument(), i);
        Assert.assertSame(iarf.get(), f);

        final AdaptationResult<TestObject> far = AdaptationResult.of(i, null, () -> f);
        Assert.assertSame(far.argument(), i);
        Assert.assertNull(far.get());

        final AdaptationResult<TestObject> farf = far.fallback();
        Assert.assertSame(farf.argument(), i);
        Assert.assertSame(farf.get(), f);
    }

    /**
     * Tests {@link AdaptationResult#require()}.
     */
    @Test
    public void testRequire() {
        final TestObject i = new TestObject(1024);

        final AdaptationResult<TestObject> ar = AdaptationResult.of(i, i);
        Assert.assertSame(ar.require(o -> new AssertionError("Failed: " + o)), i);
        Assert.assertSame(ar.require(), i);

        Assert.expectThrows(NoSuchElementException.class, () -> {
            AdaptationResult.of(i, null).require(o -> new NoSuchElementException("Failed: " + o));
        });

        Assert.expectThrows(NoSuchElementException.class, () -> {
            AdaptationResult.of(null, null).require(o -> new NoSuchElementException("Failed: " + o));
        });

        Assert.expectThrows(AdaptationException.class, () -> {
            AdaptationResult.of(i, null).require();
        });

        Assert.expectThrows(AdaptationException.class, () -> {
            AdaptationResult.of(null, null).require();
        });
    }

    /**
     * Tests {@link AdaptationResult#request()}.
     */
    @Test
    public void testRequest() {
        final TestObject i = new TestObject(1024);

        final AdaptationResult<TestObject> iar = AdaptationResult.of(i, i);
        Assert.assertSame(iar.request(o -> new AssertionError("Failed: " + o)), i);
        Assert.assertSame(iar.request(), i);

        final AdaptationResult<TestObject> nar = AdaptationResult.of(null, null);
        Assert.assertNull(nar.request(o -> new AssertionError("Failed: " + o)));
        Assert.assertNull(nar.request());

        Assert.expectThrows(NoSuchElementException.class, () -> {
            AdaptationResult.of(i, null).request(o -> new NoSuchElementException("Failed: " + o));
        });

        Assert.expectThrows(AdaptationException.class, () -> {
            AdaptationResult.of(i, null).request();
        });
    }
}
