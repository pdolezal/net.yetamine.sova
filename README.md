# net.yetamine.sova #

This repository contains a library for typesafe value adaptation which provides the most practical application in the form of typesafe heterogeneous containers, but can be useful for various handlers and callbacks. The implementation uses a well-known pattern for typesafe heterogeneous containers, which Joshua Bloch published in *Effective Java*, and provides it as a set of composable elements that allow to employ the pattern in a broader set of applications. The focus on a more versatile solution is expressed by the name of the library which is an acronym for *Symbol-oriented value adaptation*.

As this library provides just the core for the concrete value adaptation application, see the other projects like [net.yetamine.sova.maps](http://github.com/pdolezal/net.yetamine.sova.maps), which uses it to turn `Map` instances into typesafe heterogeneous containers easily.


## Prerequisites ##

For building this project is needed:

* JDK 8 or newer.
* Maven 3.3 or newer.

For using the built library is needed:

* JRE 8 or newer.


## Acknowledgments ##

A special thanks belongs to [Atos](http://atos.net/). The development of this library would be much slower without their support which provided a great opportunity to verify the library practically and improve it according to the experience.


## Licensing ##

The project is licensed under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0). Contributions to the project are welcome and accepted if they can be incorporated without the need of changing the license or license conditions and terms.


[![Yetamine logo](http://petr.dolezal.matfyz.cz/files/Yetamine_small.svg "Our logo")](http://petr.dolezal.matfyz.cz/files/Yetamine_large.svg)
