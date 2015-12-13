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

package net.yetamine.sova.symbols;

/**
 * An extension of the {@link Symbol} interface that allows attaching a tag that
 * helps manipulations with the symbol for the purpose of presentation, logging,
 * debugging etc.
 *
 * <p>
 * Tags must be of an (effectively) immutable type and must not affect comparing
 * instances on equality as they serve merely for manipulations, but not helping
 * to understand or define the semantics of a particular symbol.
 *
 * @param <T>
 *            the type of the tag
 * @param <V>
 *            the type of resulting values
 */
public interface TaggedSymbol<T, V> extends Symbol<V> {

    /**
     * Returns the tag of this instance.
     *
     * @return the tag of this instance
     */
    T tag();
}
