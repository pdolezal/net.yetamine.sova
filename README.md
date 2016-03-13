# net.yetamine.sova #

This repository contains a library for typesafe value adaptation which provides the most practical application in the form of typesafe heterogeneous containers. The implementation uses this well-known pattern for typesafe heterogeneous containers which Joshua Bloch published in *Effective Java* and provides it in a more general form, so that it can be used in a broader set of applications. The focus on a more versatile solution is expressed by the name of the library which is an acronym for *Symbol-oriented value adaptation*.


## Examples ##

The major reason for using this library could be probably the adapter that adapts any `Map` instance into a typesafe heterogeneous container, which is especially useful for various contexts and configuration objects. Such a container can be a convenient and extensible replacement for classical beans; unlike a bean, the container is a regular collection and therefore can be used even without heavy use of reflection and leaking many implementation details.

Let's have a context instance that offers the typesafe heterogeneous container interface:

```{java}
// Assuming that the key constants are defined elsewhere, e.g., in UserProfile and Hooks
final String userId = context.get(UserProfile.IDENTIFIER);
final X509Certificate[] userCerts = context.get(UserProfile.CERTIFICATES);
context.get(Hooks.AUTHENTICATION).authenticate(userId, userCerts);
```

The example shows how a single context object, containing values of various types, can return them, properly typed, when using appropriate keys to access the values. However, the keys are not limited just to map-like structures; they can act with other container types when the key is used as a value-access strategy:

```{java}
final X509Certificate[] userCerts = UserProfile.CERTIFICATES.get(servletRequest);

// This is a more convenient replacement for usual constant use + type casting:
// final X509Certificate[] userCerts = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
```


## Prerequisites ##

For building this project is needed:

* JDK 8 or newer.
* Maven 3.3 or newer.

For using the built library is needed:

* JRE 8 or newer.


## Licensing ##

The project is licensed under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0). Contributions to the project are welcome and accepted if they can be incorporated without the need of changing the license or license conditions and terms.


[![Yetamine logo](http://petr.dolezal.matfyz.cz/files/Yetamine_small.svg "Our logo")](http://petr.dolezal.matfyz.cz/files/Yetamine_large.svg)
