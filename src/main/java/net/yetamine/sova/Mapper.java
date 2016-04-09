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

import java.util.Optional;

/**
 * An extension of {@link Mapping} which allows to implement it atop of a
 * generic mapping function.
 */
@FunctionalInterface
public interface Mapper extends Mapping {

    /**
     * Maps a key (usually provided by the other methods as the result of the
     * {@link Mappable#remap()}) to an object that is associated with the key,
     * or to {@code null} if no such object exists.
     *
     * @param key
     *            the key to map
     *
     * @return the value associated with the key, or {@code null} if no mapping
     *         exists
     */
    Object map(Object key);

    /**
     * @see net.yetamine.sova.Mapping#get(net.yetamine.sova.Mappable)
     */
    default <R> R get(Mappable<?, R> symbol) {
        return symbol.derive(map(symbol.remap()));
    }

    /**
     * @see net.yetamine.sova.Mapping#use(net.yetamine.sova.Mappable)
     */
    default <R> R use(Mappable<?, R> symbol) {
        return symbol.recover(map(symbol.remap()));
    }

    /**
     * @see net.yetamine.sova.Mapping#find(net.yetamine.sova.Mappable)
     */
    default <R> Optional<R> find(Mappable<?, R> symbol) {
        return symbol.resolve(map(symbol.remap()));
    }

    /**
     * @see net.yetamine.sova.Mapping#contains(net.yetamine.sova.Mappable)
     */
    default boolean contains(Mappable<?, ?> symbol) {
        return (map(symbol.remap()) != null);
    }

    /**
     * @see net.yetamine.sova.Mapping#yield(net.yetamine.sova.Mappable)
     */
    default <R> AdaptationResult<R> yield(Mappable<?, R> symbol) {
        return symbol.adapt(map(symbol.remap()));
    }
}
