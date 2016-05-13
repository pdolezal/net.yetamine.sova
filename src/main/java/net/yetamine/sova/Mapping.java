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
 * An adaptive function-like mapping from {@link Mappable} references to values
 * that the references are associated with.
 *
 * <p>
 * This structure does not support {@code null} values and {@code null} result
 * means that the mapping to a valid adaptable result does not exist.
 *
 * <p>
 * Using {@code null} for the reference arguments is prohibited (consistently
 * with prohibiting {@code null} values). When suitable or necessary, it is
 * possible to use {@link Mappable#nil()} as a surrogate for a {@code null}
 * association reference.
 *
 * <p>
 * The interface is designed as read-only; however, changing the content might
 * be possible anyway: an implementation may offer yet a mutable interface for
 * the content, or the underlying data source may be changed in other ways.
 */
public interface Mapping {

    /**
     * Returns {@code true} if a value associated with the given reference
     * exists.
     *
     * @param ref
     *            the reference to test. It must not be {@code null}.
     *
     * @return {@code true} if a value associated with the given reference
     *         exists
     */
    boolean contains(Mappable<?, ?> ref);

    /**
     * Returns an {@link Optional} with the value associated with the given
     * reference, or an empty container if no mapping for the reference exists.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference whose associated value is to be returned. It
     *            must not be {@code null}.
     *
     * @return an {@link Optional} containing the value associated with the
     *         given reference, or an empty container if no mapping for the
     *         reference exists
     */
    <R> Optional<R> find(Mappable<?, R> ref);

    /**
     * Returns the value associated with the given reference, or {@code null} if
     * no mapping for the reference exists.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference whose associated value is to be returned. It
     *            must not be {@code null}.
     *
     * @return the value associated with the given reference, or {@code null} if
     *         no mapping for the reference exists
     */
    <R> R get(Mappable<?, R> ref);

    /**
     * Returns the value associated with the given reference, or the default
     * value for the reference if no mapping for the reference exists.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference whose associated value is to be returned. It
     *            must not be {@code null}.
     *
     * @return the value associated with the given reference, or the default
     *         value for the reference if no mapping for the reference exists
     */
    <R> R use(Mappable<?, R> ref);

    /**
     * Returns an {@link AdaptationResult} describing the attempt to adapt the
     * value associated to the given reference with the reference; the result
     * allows querying the value or the fallback as well as other details of the
     * operation.
     *
     * @param <R>
     *            the type of the result
     * @param ref
     *            the reference whose associated value is to be returned. It
     *            must not be {@code null}.
     *
     * @return an {@link AdaptationResult} describing the attempt to adapt the
     *         value associated to the given reference with the reference
     */
    <R> AdaptationResult<R> yield(Mappable<?, R> ref);
}
