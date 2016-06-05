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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Mapper}.
 */
public final class TestMapper {

    /** Value to be present under the key {@code "integer"}. */
    private static final Integer INTEGER_VALUE = Integer.valueOf(1024);
    /** Value to be present under the key {@code "string"}. */
    private static final String STRING_VALUE = "hello";

    /**
     * Testing {@link Mapper} returning both {@link #INTEGER_VALUE} and
     * {@link #STRING_VALUE}.
     */
    private static final Mapper MAPPER;
    static {
        final Map<Object, Object> data = new HashMap<>();
        data.put("integer", INTEGER_VALUE);
        data.put("string", STRING_VALUE);
        MAPPER = Collections.unmodifiableMap(data)::get;
    }

    /**
     * Tests {@link Mapper#map(Object)}.
     */
    @Test
    public void testMap() {
        Assert.assertEquals(MAPPER.map("integer"), INTEGER_VALUE);
        Assert.assertEquals(MAPPER.map("string"), "hello");
        Assert.assertNull(MAPPER.map("missing"));
    }

    /**
     * Tests {@link Mapper#contains(Mappable)}.
     */
    @Test
    public void testContains() {
        Assert.assertTrue(MAPPER.contains(Mappable.of("integer", Downcasting.to(Integer.class))));
        Assert.assertFalse(MAPPER.contains(Mappable.of("string", Downcasting.to(Integer.class))));
        Assert.assertFalse(MAPPER.contains(Mappable.of("missing", Downcasting.to(Object.class))));
    }

    /**
     * Tests {@link Mapper#get(Mappable)}.
     */
    @Test
    public void testGet() {
        Assert.assertEquals(MAPPER.get(Mappable.of("integer", Downcasting.to(Integer.class))), INTEGER_VALUE);
        Assert.assertNull(MAPPER.get(Mappable.of("string", Downcasting.to(Integer.class))));
        Assert.assertNull(MAPPER.get(Mappable.of("missing", Downcasting.to(Object.class))));
    }

    /**
     * Tests {@link Mapper#give(Mappable)}.
     */
    @Test
    public void testGive() {
        Assert.assertEquals(MAPPER.give(Mappable.of("integer", Downcasting.to(Integer.class))), INTEGER_VALUE);
        Assert.assertNull(MAPPER.give(Mappable.of("string", Downcasting.to(Integer.class))));
        Assert.assertNull(MAPPER.give(Mappable.of("missing", Downcasting.to(Object.class))));

        final Integer i = Integer.valueOf(1);
        final AdaptationProvider<Integer> p = Downcasting.withFallbackTo(Integer.class, i);
        Assert.assertEquals(MAPPER.give(Mappable.of("integer", p)), INTEGER_VALUE);
        Assert.assertEquals(MAPPER.give(Mappable.of("string", p)), i);
        Assert.assertEquals(MAPPER.give(Mappable.of("missing", p)), i);
    }

    /**
     * Tests {@link Mapper#find(Mappable)}.
     */
    @Test
    public void testFind() {
        final AdaptationProvider<Integer> p = Downcasting.to(Integer.class);
        Assert.assertEquals(MAPPER.find(Mappable.of("integer", p)).get(), INTEGER_VALUE);
        Assert.assertFalse(MAPPER.find(Mappable.of("string", p)).isPresent());
        Assert.assertFalse(MAPPER.find(Mappable.of("missing", p)).isPresent());
    }

    /**
     * Tests {@link Mapper#yield(Mappable)}.
     */
    @Test
    public void testYield() {
        final Integer i = Integer.valueOf(1);
        final AdaptationProvider<Integer> p = Downcasting.withFallbackTo(Integer.class, i);

        final AdaptationResult<Integer> r1 = MAPPER.yield(Mappable.of("integer", p));
        Assert.assertEquals(r1.argument(), INTEGER_VALUE);
        Assert.assertEquals(r1.get(), INTEGER_VALUE);
        Assert.assertEquals(r1.fallback().get(), i);

        final AdaptationResult<Integer> r2 = MAPPER.yield(Mappable.of("string", p));
        Assert.assertEquals(r2.argument(), STRING_VALUE);
        Assert.assertNull(r2.get());
        Assert.assertEquals(r2.fallback().get(), i);

        final AdaptationResult<Integer> r3 = MAPPER.yield(Mappable.of("missing", p));
        Assert.assertEquals(r3.argument(), null);
        Assert.assertNull(r3.get());
        Assert.assertEquals(r3.fallback().get(), i);
    }
}
