# net.yetamine.sova #

This repository contains a library for type-safe value adaptation which provides the most practical application in the form of type-safe heterogeneous containers, but which can be useful for various handlers and callbacks as well. The implementation uses a well-known pattern for type-safe heterogeneous containers, published by Joshua Bloch in his book *Effective Java*, and provides it as a set of composable elements that allow to employ the pattern in a broader set of applications. The focus on a more versatile solution is expressed by the name of the library which is an acronym for *Symbol-oriented value adaptation*.

As this library provides just the core for the concrete value adaptation application, see other projects that build on the top of this core, e.g., [net.yetamine.sova.maps](http://github.com/pdolezal/net.yetamine.sova.maps), which turns a regular `Map` into a type-safe heterogeneous container easily.


## Prerequisites ##

For building this project is needed:

* JDK 8 or newer.
* Maven 3.3 or newer.

For using the built library is needed:

* JRE 8 or newer.


## Acknowledgments ##

A special thank belongs to [Atos](http://atos.net/). The development of this library would be much slower without their support which provided a great opportunity to verify the library practically and improve it according to the experience.

Another thank belongs to *davej* from [project77.org](http://project77.org/) for the permission to use his owl picture as the logo for this project. Why an owl? Because it is so cute and because *sova* means *an owl* in Czech.


## Licensing ##

The project is licensed under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0). Contributions to the project are welcome and accepted if they can be incorporated without the need of changing the license or license conditions and terms.


[![Yetamine logo](https://github.com/pdolezal/net.yetamine/raw/master/about/Yetamine_small.png "Our logo")](https://github.com/pdolezal/net.yetamine/blob/master/about/Yetamine_large.png)
[![Sova logo](about/sova_tiny.png "Project logo")](about/sova_large.png)
