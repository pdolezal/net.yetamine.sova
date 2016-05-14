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

    // Testing values
    private static final Integer INTEGER_VALUE = Integer.valueOf(1024);
    private static final String STRING_VALUE = "hello";

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
     * Tests {@link Mapper#find(Mappable)}.
     */
    @Test
    public void testFind() {
        Assert.assertEquals(MAPPER.find(Mappable.of("integer", Downcasting.to(Integer.class))).get(), INTEGER_VALUE);
        Assert.assertFalse(MAPPER.find(Mappable.of("string", Downcasting.to(Integer.class))).isPresent());
        Assert.assertFalse(MAPPER.find(Mappable.of("missing", Downcasting.to(Object.class))).isPresent());
    }

    /**
     * Tests {@link Mapper#use(Mappable)}.
     */
    @Test
    public void testUse() {
        Assert.assertEquals(MAPPER.use(Mappable.of("integer", Downcasting.to(Integer.class))), INTEGER_VALUE);
        Assert.assertNull(MAPPER.use(Mappable.of("string", Downcasting.to(Integer.class))));
        Assert.assertNull(MAPPER.use(Mappable.of("missing", Downcasting.to(Object.class))));

        final Integer i = Integer.valueOf(1);
        Assert.assertEquals(MAPPER.use(Mappable.of("integer", Downcasting.withFallbackTo(Integer.class, i))), INTEGER_VALUE);
        Assert.assertEquals(MAPPER.use(Mappable.of("string", Downcasting.withFallbackTo(Integer.class, i))), i);
        Assert.assertEquals(MAPPER.use(Mappable.of("missing", Downcasting.withFallbackTo(Integer.class, i))), i);
    }

    /**
     * Tests {@link Mapper#yield(Mappable)}.
     */
    @Test
    public void testYield() {
        final Integer i = Integer.valueOf(1);

        final AdaptationResult<Integer> r1 = MAPPER.yield(Mappable.of("integer", Downcasting.withFallbackTo(Integer.class, i)));
        Assert.assertEquals(r1.argument(), INTEGER_VALUE);
        Assert.assertEquals(r1.get(), INTEGER_VALUE);
        Assert.assertEquals(r1.fallback().get(), i);

        final AdaptationResult<Integer> r2 = MAPPER.yield(Mappable.of("string", Downcasting.withFallbackTo(Integer.class, i)));
        Assert.assertEquals(r2.argument(), STRING_VALUE);
        Assert.assertNull(r2.get());
        Assert.assertEquals(r2.fallback().get(), i);

        final AdaptationResult<Integer> r3 = MAPPER.yield(Mappable.of("missing", Downcasting.withFallbackTo(Integer.class, i)));
        Assert.assertEquals(r3.argument(), null);
        Assert.assertNull(r3.get());
        Assert.assertEquals(r3.fallback().get(), i);
    }
}
