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

/**
 * Provides support for type-safe heterogenous containers and data handlers.
 *
 * <h1>Introduction</h1>
 *
 * Symbol-oriented value adaptation utilities provide means for type-safe and
 * comfortable handling of heterogenous data (i.e., data items of various types)
 * with common map-like collections, although such collections provide no direct
 * support for heterogenous data and can handle them in a type-unsafe way.
 *
 * <h2>A common use case</h2>
 *
 * Many applications and frameworks deal with the need of managing data items,
 * usually configuration entries or various context parameters, that have some
 * well-known name that can be used to access the particular data item, but the
 * type information is no expressed in the programming language directly, it is
 * written in documentation only and a programmer is forced to use typecasting.
 *
 * <p>
 * An example of this usual situation can be met, e.g., in Java Servlet API:
 *
 * <pre>
 * // A code within a Servlet implementation
 * protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
 *    // Some code here
 *    final X509Certificate[] certificates = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
 *    // Some more code
 * }
 * </pre>
 *
 * Of course, the typecasting is uncomfortable at least and often even dangerous
 * in such situations. Could this be done better? What about keys that carry the
 * type information about the associated values?
 *
 * <h2>A solution</h2>
 *
 * The symbol-based approach exactly exploits this idea and uses such keys, so
 * called <em>symbols</em>, that capture the type information, instead of keys
 * based on arbitrary types lacking any type information (e.g., pure strings);
 * btw. maybe better known term is <em>token</em>, but since <i>token</i> seems
 * to be overloaded even more with other meanings than <i>symbol</i> (mostly in
 * the area of security), the less frequent option was chosen.
 *
 * <p>
 * A symbol construction is less straight-forward: besides an identifier, which
 * distinguishes different symbols and plays the role of the former name, the
 * type information must be provided. On the other hand, providing all needed
 * information for the symbol handling at the point of a symbol declaration,
 * spares the effort of the users of the symbol at many other places.
 *
 * <p>
 * Capturing the type information can be handled quite well, although there are
 * some limitations due to non-reified types: type conversions and checks might
 * fail when the compiler is forced to an unchecked cast which can't be avoided
 * sometimes. However, the traditional approach faces the same problem in its
 * worse form: such casts are not limited to a few controlled points, but can
 * appear everywhere.
 *
 * <p>
 * Another challenge is using such a symbol. Existing collections are generally
 * designed for storing homogenous data entries and making symbol-aware friends
 * for each of them looks like a huge and futile task. The only viable approach
 * lets a symbol use collections instead of making all collections use a symbol.
 * A symbol becomes an extraction function for a value on any collection. Let's
 * now assume for a while that {@code ServletRequest} implements a method with
 * the signature {@code getAttribute(Object)} (i.e., accepting anything as the
 * key, not just a {@code String}):
 *
 * <pre>
 * public static Symbol&lt;X509Certificate[]&gt; CERTIFICATES = new AttributeSymbol&lt;&gt;(X509Certificate[].class);
 *
 * // Here comes the use within a Servlet implementation
 * protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
 *    // Some code here
 *    final X509Certificate[] certificates = CERTIFICATES.get(req::getAttribute);
 *    // Some more code
 * }
 * </pre>
 *
 * It's a bit cheating: there is no {@code getAttribute(Object)} method, instead
 * a {@code String} is required, and no web container prepares an attribute with
 * such a key. But where a symbol can be used as the key, it provides a better
 * alternative. The opportunities are in your own code (which does not have to
 * repeat the mistakes that the others do) or everywhere when a collection can
 * deal with any object. Applying an adapter or a bridge that allows to employ
 * symbols is usually possible as well.
 *
 * <p>
 * An example of a bridge: the symbol scheme is extensible and a dedicated class
 * can extend an existing symbol class and add a few convenient methods for the
 * specific use cases. For instance, a new class {@code AttributeSymbol} could
 * be inherited and equipped with methods for accessing {@code ServletRequest}
 * attributes, like {@code T get(ServletRequest)} with a simple implementation.
 * The usage than demonstrates this code snippet:
 *
 * <pre>
 * // This is a symbol declaration similar to the previous one
 * public static AttributeSymbol&lt;X509Certificate[]&gt; CERTIFICATES = new AttributeSymbol&lt;&gt;("javax.servlet.request.X509Certificate", X509Certificate[].class);
 * // Note that the symbol captures the name of the attribute as well as its type
 *
 * // Here comes the use within a Servlet implementation
 * protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
 *    // Some code here
 *    final X509Certificate[] certificates = CERTIFICATES.get(req);
 *    // Some more code
 * }
 * </pre>
 *
 * Another approach might employ an adapter for the {@code ServletRequest} which
 * could provide even better support for more and more generic symbol types.
 */
package net.yetamine.sova;
