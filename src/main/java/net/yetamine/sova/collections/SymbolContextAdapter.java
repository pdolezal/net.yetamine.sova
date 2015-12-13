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

package net.yetamine.sova.collections;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.yetamine.sova.adaptation.Adaptation;
import net.yetamine.sova.adaptation.Mappable;

/**
 * A skeletal implementation of an adapter for {@link Map}.
 *
 * <p>
 * Inherited classes must override {@link #storage()} to supply the underlying
 * {@link Map} instance which the implementation encapsulates and adapts; this
 * instance must be modifiable and it may tolerate {@code null} values as well
 * as it may prohibit them.
 */
public abstract class SymbolContextAdapter extends SymbolMappingAdapter implements SymbolContext {

    /**
     * Prepares a new instance.
     */
    protected SymbolContextAdapter() {
        // Default constructor
    }

    /**
     * @see net.yetamine.sova.collections.SymbolMapping#map()
     */
    public Map<?, ?> map() {
        return storage();
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#unmodifiable()
     */
    public SymbolMapping unmodifiable() {
        return new DefaultSymbolMapping(Collections.unmodifiableMap(storage()));
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#clear()
     */
    public SymbolContext clear() {
        storage().clear();
        return this;
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#discard(net.yetamine.sova.adaptation.Mappable)
     */
    public SymbolContext discard(Mappable<?, ?> symbol) {
        storage().remove(symbol.remap());
        return this;
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#set(net.yetamine.sova.adaptation.Mappable,
     *      java.lang.Object)
     */
    public <T> SymbolContext set(Mappable<?, T> symbol, T value) {
        final T item = symbol.adapt(value).request();
        storage().put(symbol.remap(), item);
        return this;
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#remove(net.yetamine.sova.adaptation.Mappable)
     */
    public <T> T remove(Mappable<?, T> symbol) {
        return symbol.apply(storage().remove(symbol.remap()));
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#remove(net.yetamine.sova.adaptation.Mappable,
     *      java.lang.Object)
     */
    public boolean remove(Mappable<?, ?> symbol, Object value) {
        return storage().remove(symbol.remap(), value);
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#put(net.yetamine.sova.adaptation.Mappable,
     *      java.lang.Object)
     */
    public <T> T put(Mappable<?, T> symbol, T value) {
        final T item = symbol.adapt(value).request();
        return symbol.apply(storage().put(symbol.remap(), item));
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#putIfAbsent(net.yetamine.sova.adaptation.Mappable,
     *      java.lang.Object)
     */
    public <T> T putIfAbsent(Mappable<?, T> symbol, T value) {
        final T item = symbol.adapt(value).request();

        @SuppressWarnings("unchecked")
        final T result = (T) storage().merge(symbol.remap(), item, (u, v) -> {
            return symbol.resolve(v).orElse(item);
        });

        // Verify that the result is either null, or is equal to what the adaptation would return
        assert((result == null) || result.equals(symbol.apply(result)));
        return result;
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#replace(net.yetamine.sova.adaptation.Mappable,
     *      java.lang.Object, java.lang.Object)
     */
    public <T> boolean replace(Mappable<?, T> symbol, Object oldValue, T newValue) {
        return storage().replace(symbol.remap(), oldValue, symbol.adapt(newValue).request());
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#replace(net.yetamine.sova.adaptation.Mappable,
     *      java.lang.Object)
     */
    public <T> T replace(Mappable<?, T> symbol, T value) {
        final T item = symbol.adapt(value).request();
        return symbol.apply(storage().replace(symbol.remap(), item));
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#merge(net.yetamine.sova.adaptation.Mappable,
     *      java.lang.Object, java.util.function.BiFunction)
     */
    public <T> T merge(Mappable<?, T> symbol, T value, BiFunction<? super T, ? super T, ? extends T> remapping) {
        final T item = symbol.adapt(value).request();

        final Object result = storage().merge(symbol.remap(), item, (u, v) -> {
            final T current = symbol.apply(v);
            final T replace = remapping.apply(item, current);
            return symbol.adapt(replace).request();
        });

        return symbol.apply(result);
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#compute(net.yetamine.sova.adaptation.Mappable,
     *      java.util.function.BiFunction)
     */
    public <K, T> T compute(Mappable<K, T> symbol, BiFunction<? super K, ? super T, ? extends T> remappingFunction) {
        final Adaptation<T> adaptation = symbol.adaptation();
        final K key = symbol.remap(); // Need to use it later due to correct static type
        final Object result = storage().compute(key, (k, v) -> {
            final T current = adaptation.apply(v);
            final T replace = remappingFunction.apply(key, current);
            return symbol.adapt(replace).request();
        });

        return adaptation.apply(result);
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#computeIfAbsent(net.yetamine.sova.adaptation.Mappable,
     *      java.util.function.Function)
     */
    public <K, T> T computeIfAbsent(Mappable<K, T> symbol, Function<? super K, ? extends T> mappingFunction) {
        return compute(symbol, (t, v) -> (v == null) ? symbol.adapt(mappingFunction.apply(t)).request() : v);
    }

    /**
     * @see net.yetamine.sova.collections.SymbolContext#computeIfPresent(net.yetamine.sova.adaptation.Mappable,
     *      java.util.function.BiFunction)
     */
    public <K, T> T computeIfPresent(Mappable<K, T> symbol, BiFunction<? super K, ? super T, ? extends T> remapping) {
        return compute(symbol, (t, v) -> (v != null) ? symbol.adapt(remapping.apply(t, v)).request() : null);
    }

    /**
     * Returns the instance of the underlying {@link Map} instance. The returned
     * instance must be mutable to enable other methods, but it may restrict its
     * usage and prohibit some operations or some parameters.
     *
     * @return the instance of the underlying {@link Map} instance
     */
    protected abstract Map<Object, Object> storage();
}
