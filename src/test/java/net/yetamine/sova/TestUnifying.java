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
 * Tests {@link Unifying}.
 */
public final class TestUnifying {

    /**
     * Tests a simple unification.
     */
    @Test
    public void testUnifyingTo() {
        final Function<Object, Integer> u = o -> (o instanceof Number) ? ((Number) o).intValue() : null;
        final AdaptationProvider<Integer> a = Unifying.to(Integer.class, u);
        final AdaptationStrategy<Integer> s = AdaptationStrategy.using(a);

        Assert.assertEquals(s.nullable(Integer.valueOf(1)), Integer.valueOf(1));
        Assert.assertEquals(s.nullable(Double.valueOf(2)), Integer.valueOf(2));
        Assert.assertEquals(s.nullable(Long.valueOf(3)), Integer.valueOf(3));
        Assert.assertNull(s.nullable("4"));
    }

    /**
     * Tests a fallback unification.
     */
    @Test
    public void testUnifyingFallback() {
        final Function<Object, Integer> u = o -> (o instanceof Number) ? ((Number) o).intValue() : null;
        final AdaptationProvider<Integer> a = Unifying.withFallbackTo(Integer.class, u, Integer.valueOf(0));
        final AdaptationStrategy<Integer> s = AdaptationStrategy.using(a);

        Assert.assertEquals(s.nullable(Integer.valueOf(1)), Integer.valueOf(1));
        Assert.assertEquals(s.nullable(Double.valueOf(2)), Integer.valueOf(2));
        Assert.assertEquals(s.nullable(Long.valueOf(3)), Integer.valueOf(3));
        Assert.assertNull(s.nullable("4"));
        Assert.assertEquals(s.surrogate("4"), Integer.valueOf(0));
    }

    /**
     * Tests a conditional unification.
     */
    @Test
    public void testUnifyingFilter() {
        final Function<Object, Integer> u = o -> (o instanceof Number) ? ((Number) o).intValue() : null;
        final AdaptationProvider<Integer> a = Unifying.withFilter(Integer.class, u, i -> 0 <= i);
        final AdaptationStrategy<Integer> s = AdaptationStrategy.using(a);

        Assert.assertEquals(s.nullable(Integer.valueOf(1)), Integer.valueOf(1));
        Assert.assertEquals(s.nullable(Double.valueOf(2)), Integer.valueOf(2));
        Assert.assertEquals(s.nullable(Long.valueOf(3)), Integer.valueOf(3));
        Assert.assertNull(s.nullable("4"));
        Assert.assertNull(s.nullable(Integer.valueOf(-1)));
    }
}
