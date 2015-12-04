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

package net.yetamine.sova.core;

/**
 * Thrown when an argument could not be adapted.
 *
 * <p>
 * This exception can carry the argument of the adaptation that could not be
 * adapted. The argument detail, however, is optional and may be {@code null}.
 * The argument detail it is not serialized either.
 */
public class AdaptationException extends RuntimeException {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Argument of the failed adaptation. */
    private final transient Object argument;

    /**
     * Creates a new instance with no details at all.
     */
    public AdaptationException() {
        argument = null;
    }

    /**
     * Create a new instance with the argument detail.
     *
     * @param arg
     *            the argument detail
     */
    public AdaptationException(Object arg) {
        argument = arg;
    }

    /**
     * Create a new instance with the specified detail message.
     *
     * @param arg
     *            the argument detail
     * @param message
     *            the detail message
     */
    public AdaptationException(Object arg, String message) {
        super(message);
        argument = arg;
    }

    /**
     * Create a new instance with the specified cause and a detail message
     * constructed from the cause (if not {@code null}).
     *
     * @param arg
     *            the argument detail
     * @param cause
     *            the cause
     */
    public AdaptationException(Object arg, Throwable cause) {
        super(cause);
        argument = arg;
    }

    /**
     * Create a new instance with the specified detail message and cause.
     *
     * @param arg
     *            the argument detail
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public AdaptationException(Object arg, String message, Throwable cause) {
        super(message, cause);
        argument = arg;
    }

    /**
     * Returns the argument of the adaptation.
     *
     * @return the argument of the adaptation, or {@code null} if not given or
     *         not known
     */
    public Object getArgument() {
        return argument;
    }
}
