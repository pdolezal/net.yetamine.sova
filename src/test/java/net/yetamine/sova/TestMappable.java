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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Mappable}.
 */
public final class TestMappable {

    /** Value to be present under the key {@code "integer"}. */
    private static final Integer INTEGER_VALUE = Integer.valueOf(1024);
    /** Value to be present under the key {@code "string"}. */
    private static final String STRING_VALUE = "hello";

    /**
     * Testing map with {@link #INTEGER_VALUE} and {@link #STRING_VALUE}.
     */
    private static final Map<Object, Object> MAP;
    static {
        final Map<Object, Object> data = new HashMap<>();
        data.put("integer", INTEGER_VALUE);
        data.put("string", STRING_VALUE);
        MAP = Collections.unmodifiableMap(data);
    }

    /**
     * Tests {@link Mappable#pull(Function)}.
     */
    @Test
    public void testPull_F() {
        final Function<Object, ?> f = MAP::get;
        final AdaptationProvider<Integer> p = Downcasting.to(Integer.class);
        Assert.assertEquals(Mappable.of("integer", p).pull(f), INTEGER_VALUE);
        Assert.assertEquals(Mappable.of("string", p).pull(f), STRING_VALUE);
        Assert.assertNull(Mappable.of("missing", p).pull(f));
    }

    /**
     * Tests {@link Mappable#pull(Map)}.
     */
    @Test
    public void testPull_M() {
        final AdaptationProvider<Integer> p = Downcasting.to(Integer.class);
        Assert.assertEquals(Mappable.of("integer", p).pull(MAP), INTEGER_VALUE);
        Assert.assertEquals(Mappable.of("string", p).pull(MAP), STRING_VALUE);
        Assert.assertNull(Mappable.of("missing", p).pull(MAP));
    }

    /**
     * Tests {@link Mappable#push(Map, Object)} and
     * {@link Mappable#push(BiConsumer, Object)}.
     */
    @Test
    public void testPush() {
        final MockObject i = new MockObject(INTEGER_VALUE);
        final Mappable<?, MockObject> mappable = Mappable.of("test", Downcasting.to(MockObject.class));

        final Map<Object, Object> m = new HashMap<>();
        mappable.push(m, i);
        Assert.assertSame(mappable.pull(m), i);

        final Map<Object, Object> c = new HashMap<>();
        mappable.push(c::put, i);
        Assert.assertSame(mappable.pull(c), i);
    }

    /**
     * Tests {@link Mappable#get(Function)}.
     */
    @Test
    public void testGet_F() {
        final Function<Object, ?> f = MAP::get;
        Assert.assertEquals(Mappable.of("integer", Downcasting.to(Integer.class)).get(f), INTEGER_VALUE);
        Assert.assertNull(Mappable.of("string", Downcasting.to(Integer.class)).get(f));
        Assert.assertNull(Mappable.of("missing", Downcasting.to(Object.class)).get(f));
    }

    /**
     * Tests {@link Mappable#get(Map)}.
     */
    @Test
    public void testGet_M() {
        Assert.assertEquals(Mappable.of("integer", Downcasting.to(Integer.class)).get(MAP), INTEGER_VALUE);
        Assert.assertNull(Mappable.of("string", Downcasting.to(Integer.class)).get(MAP));
        Assert.assertNull(Mappable.of("missing", Downcasting.to(Object.class)).get(MAP));
    }

    /**
     * Tests {@link Mappable#give(Function)}.
     */
    @Test
    public void testGive_F() {
        final Function<Object, ?> f = MAP::get;
        Assert.assertEquals(Mappable.of("integer", Downcasting.to(Integer.class)).give(f), INTEGER_VALUE);
        Assert.assertNull(Mappable.of("string", Downcasting.to(Integer.class)).give(f));
        Assert.assertNull(Mappable.of("missing", Downcasting.to(Object.class)).give(f));

        final Integer i = Integer.valueOf(1);
        final AdaptationProvider<Integer> adaptation = Downcasting.withFallbackTo(Integer.class, i);
        Assert.assertEquals(Mappable.of("integer", adaptation).give(f), INTEGER_VALUE);
        Assert.assertEquals(Mappable.of("string", adaptation).give(f), i);
        Assert.assertEquals(Mappable.of("missing", adaptation).give(f), i);
    }

    /**
     * Tests {@link Mappable#give(Map)}.
     */
    @Test
    public void testGive_M() {
        Assert.assertEquals(Mappable.of("integer", Downcasting.to(Integer.class)).give(MAP), INTEGER_VALUE);
        Assert.assertNull(Mappable.of("string", Downcasting.to(Integer.class)).give(MAP));
        Assert.assertNull(Mappable.of("missing", Downcasting.to(Object.class)).give(MAP));

        final Integer i = Integer.valueOf(1);
        final AdaptationProvider<Integer> adaptation = Downcasting.withFallbackTo(Integer.class, i);
        Assert.assertEquals(Mappable.of("integer", adaptation).give(MAP), INTEGER_VALUE);
        Assert.assertEquals(Mappable.of("string", adaptation).give(MAP), i);
        Assert.assertEquals(Mappable.of("missing", adaptation).give(MAP), i);
    }

    /**
     * Tests {@link Mappable#find(Function)}.
     */
    @Test
    public void testFind_F() {
        final Function<Object, ?> f = MAP::get;
        Assert.assertEquals(Mappable.of("integer", Downcasting.to(Integer.class)).find(f).get(), INTEGER_VALUE);
        Assert.assertFalse(Mappable.of("string", Downcasting.to(Integer.class)).find(f).isPresent());
        Assert.assertFalse(Mappable.of("missing", Downcasting.to(Object.class)).find(f).isPresent());
    }

    /**
     * Tests {@link Mappable#find(Map)}.
     */
    @Test
    public void testFind_M() {
        Assert.assertEquals(Mappable.of("integer", Downcasting.to(Integer.class)).find(MAP).get(), INTEGER_VALUE);
        Assert.assertFalse(Mappable.of("string", Downcasting.to(Integer.class)).find(MAP).isPresent());
        Assert.assertFalse(Mappable.of("missing", Downcasting.to(Object.class)).find(MAP).isPresent());
    }

    /**
     * Tests {@link Mappable#yield(Function)}.
     */
    @Test
    public void testYield_F() {
        final Integer i = Integer.valueOf(1);
        final Function<Object, ?> f = MAP::get;
        final AdaptationProvider<Integer> adaptation = Downcasting.withFallbackTo(Integer.class, i);

        final AdaptationResult<Integer> r1 = Mappable.of("integer", adaptation).yield(f);
        Assert.assertEquals(r1.argument(), INTEGER_VALUE);
        Assert.assertEquals(r1.get(), INTEGER_VALUE);
        Assert.assertEquals(r1.fallback().get(), i);

        final AdaptationResult<Integer> r2 = Mappable.of("string", adaptation).yield(f);
        Assert.assertEquals(r2.argument(), STRING_VALUE);
        Assert.assertNull(r2.get());
        Assert.assertEquals(r2.fallback().get(), i);

        final AdaptationResult<Integer> r3 = Mappable.of("missing", adaptation).yield(f);
        Assert.assertEquals(r3.argument(), null);
        Assert.assertNull(r3.get());
        Assert.assertEquals(r3.fallback().get(), i);
    }

    /**
     * Tests {@link Mappable#yield(Map)}.
     */
    @Test
    public void testYield_M() {
        final Integer i = Integer.valueOf(1);
        final AdaptationProvider<Integer> adaptation = Downcasting.withFallbackTo(Integer.class, i);

        final AdaptationResult<Integer> r1 = Mappable.of("integer", adaptation).yield(MAP);
        Assert.assertEquals(r1.argument(), INTEGER_VALUE);
        Assert.assertEquals(r1.get(), INTEGER_VALUE);
        Assert.assertEquals(r1.fallback().get(), i);

        final AdaptationResult<Integer> r2 = Mappable.of("string", adaptation).yield(MAP);
        Assert.assertEquals(r2.argument(), STRING_VALUE);
        Assert.assertNull(r2.get());
        Assert.assertEquals(r2.fallback().get(), i);

        final AdaptationResult<Integer> r3 = Mappable.of("missing", adaptation).yield(MAP);
        Assert.assertEquals(r3.argument(), null);
        Assert.assertNull(r3.get());
        Assert.assertEquals(r3.fallback().get(), i);
    }

    /**
     * Tests {@link Mappable#put(BiConsumer, Object)}.
     */
    @Test
    public void testPut_F() {
        final AdaptationProvider<Integer> adaptation = Downcasting.to(Integer.class);
        final Map<Object, Object> m = new HashMap<>(MAP);
        final Integer i = Integer.valueOf(1);

        Mappable.of("integer", adaptation).put(m::put, i);
        Assert.assertEquals(m.get("integer"), i);

        Mappable.of("string", adaptation).put(m::put, i);
        Assert.assertEquals(m.get("string"), i);

        Mappable.of("missing", adaptation).put(m::put, i);
        Assert.assertEquals(m.get("missing"), i);
    }

    /**
     * Tests {@link Mappable#put(Map, Object)}.
     */
    @Test
    public void testPut_M() {
        final AdaptationProvider<Integer> adaptation = Downcasting.to(Integer.class);
        final Map<Object, Object> m = new HashMap<>(MAP);
        final Integer i = Integer.valueOf(1);

        Assert.assertEquals(Mappable.of("integer", adaptation).put(m, i), INTEGER_VALUE);
        Assert.assertEquals(m.get("integer"), i);

        Assert.assertNull(Mappable.of("string", adaptation).put(m, i));
        Assert.assertEquals(m.get("string"), i);

        Assert.assertNull(Mappable.of("missing", adaptation).put(m, i));
        Assert.assertEquals(m.get("missing"), i);
    }

    /**
     * Tests {@link Mappable#have(BiConsumer, Object)}.
     */
    @Test
    public void testHave_F() {
        final AdaptationProvider<Integer> adaptation = Downcasting.to(Integer.class);
        final Map<Object, Object> m = new HashMap<>(MAP);
        final Integer i = Integer.valueOf(1);

        Assert.assertEquals(Mappable.of("integer", adaptation).have(m::put, i).get(), i);
        Assert.assertEquals(m.get("integer"), i);

        Assert.assertEquals(Mappable.of("string", adaptation).have(m::put, i).get(), i);
        Assert.assertEquals(m.get("string"), i);

        Assert.assertEquals(Mappable.of("missing", adaptation).have(m::put, i).get(), i);
        Assert.assertEquals(m.get("missing"), i);

        Assert.assertFalse(Mappable.of("integer", adaptation).have(m::put, "hello").isPresent());
        Assert.assertEquals(m.get("integer"), i);

        Assert.assertFalse(Mappable.of("none", adaptation).have(m::put, "hello").isPresent());
        Assert.assertFalse(m.containsKey("none"));
    }

    /**
     * Tests {@link Mappable#have(Map, Object)}.
     */
    @Test
    public void testHave_M() {
        final AdaptationProvider<Integer> adaptation = Downcasting.to(Integer.class);
        final Map<Object, Object> m = new HashMap<>(MAP);
        final Integer i = Integer.valueOf(1);

        Assert.assertEquals(Mappable.of("integer", adaptation).have(m, i).get(), i);
        Assert.assertEquals(m.get("integer"), i);

        Assert.assertEquals(Mappable.of("string", adaptation).have(m, i).get(), i);
        Assert.assertEquals(m.get("string"), i);

        Assert.assertEquals(Mappable.of("missing", adaptation).have(m, i).get(), i);
        Assert.assertEquals(m.get("missing"), i);

        Assert.assertFalse(Mappable.of("integer", adaptation).have(m, "hello").isPresent());
        Assert.assertEquals(m.get("integer"), i);

        Assert.assertFalse(Mappable.of("none", adaptation).have(m, "hello").isPresent());
        Assert.assertFalse(m.containsKey("none"));
    }

    /**
     * Tests {@link Mappable#let(Map, Object)}.
     */
    @Test
    public void testLet() {
        final AdaptationProvider<Integer> adaptation = Downcasting.to(Integer.class);
        final Map<Object, Object> m = new HashMap<>(MAP);
        final Integer i = Integer.valueOf(1);

        Assert.assertEquals(Mappable.of("integer", adaptation).let(m, i), INTEGER_VALUE);
        Assert.assertEquals(m.get("integer"), i);

        Assert.assertNull(Mappable.of("string", adaptation).let(m, i));
        Assert.assertEquals(m.get("string"), i);

        Assert.assertNull(Mappable.of("missing", adaptation).let(m, i));
        Assert.assertEquals(m.get("missing"), i);

        Assert.assertEquals(Mappable.of("integer", adaptation).let(m, null), i);
        Assert.assertFalse(m.containsKey("integer"));

        Assert.assertEquals(Mappable.of("string", adaptation).let(m, "hello"), i);
        Assert.assertFalse(m.containsKey("string"));
    }

    /**
     * Tests {@link Mappable#supply(Map, Supplier)}.
     */
    @Test
    public void testSupply() {
        final AdaptationProvider<Integer> adaptation = Downcasting.to(Integer.class);
        final Map<Object, Object> m = new HashMap<>(MAP);
        final Integer i = Integer.valueOf(1);

        Assert.assertEquals(Mappable.of("integer", adaptation).supply(m, () -> i), INTEGER_VALUE);
        Assert.assertEquals(m.get("integer"), INTEGER_VALUE);

        Assert.assertEquals(Mappable.of("string", adaptation).supply(m, () -> i), i);
        Assert.assertEquals(m.get("string"), i);

        Assert.assertEquals(Mappable.of("missing", adaptation).supply(m, () -> i), i);
        Assert.assertEquals(m.get("missing"), i);
    }
}
