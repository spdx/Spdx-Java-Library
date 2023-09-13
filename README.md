# Spdx-Java-Library
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.spdx/java-spdx-library/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.spdx/java-spdx-library)
![Java CI with Maven](https://github.com/spdx/Spdx-Java-Library/workflows/Java%20CI%20with%20Maven/badge.svg)

Java library which implements the Java object model for SPDX and provides useful helper functions.

# Code quality badges

|   [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=bugs)](https://sonarcloud.io/dashboard?id=java-spdx-library)    | [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=security_rating)](https://sonarcloud.io/dashboard?id=java-spdx-library) | [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=java-spdx-library) | [![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=sqale_index)](https://sonarcloud.io/dashboard?id=java-spdx-library) |

## Storage Interface
The Spdx-Java-Library allows for different implementations of SPDX object storage.  The storage facility implements the org.spdx.storage.IModelStore interface.  This is a low level Service Provider Interface (SPI).  The ISerializableModelStore extends the IModelStore and supports serializing and de-serializing the store to an I/O Stream. This interface is currently used to implement JSON, XML, YAML, and RDF/XML formats.  The default storage interface is an in-memory Map which should be sufficient for light weight usage of the library.

Most common use of the library would  de-serialize an existing SPDX document using one of the supported formats and model stores.  To create SPDX objects from scratch, simply create the Java objects found in the org.spdx.library.model package.  The model follows the [SPDX Object Model](https://github.com/spdx/spdx-spec/blob/2a7aff7afa089a774916bd5c64fc2cb83637ea07/model/SPDX-UML-Class-Diagram.jpg).  The model objects themselves are stateless and do not store information.  All information is retrieved from the model store when properties are access.  Storage to the classes will store the updates through the use of the storage interface.

## Multi-Threaded Considerations
The methods enterCriticalSection and leaveCritialSection are available to support multi-threaded applications.  These methods serialize access to the model store for the specific SPDX document used for the SPDX model object.

## Getting Started
The library is available in [Maven Central org.spdx:java-spdx-library](https://search.maven.org/artifact/org.spdx/java-spdx-library).

If you are using Maven, you can add the following dependency in your POM file:
```xml
<dependency>
  <groupId>org.spdx</groupId>
  <artifactId>java-spdx-library</artifactId>
  <version>(,1.0]</version>
</dependency>
```

[API JavaDocs are available here](https://spdx.github.io/Spdx-Java-Library/).

There are a couple of static classes that help common usage scenarios:

- `org.spdx.library.SPDXModelFactory` supports the creation of specific model objects
- `org.spdx.library.model.license.LicenseInfoFactory` supports the parsing of SPDX license expressions, creation, and comparison of SPDX licenses

## Configuration options

`Spdx-Java-Library` is configured using Java system properties, as those allow both human operator configuration (via JVM `-D` command line options) as well as programmatic configuration by downstream code that needs automated control.

Currently the library offers these configuration options:
1. `org.spdx.storage.listedlicense.SpdxListedLicenseWebStore.enableCache` - a boolean that enables or disables the WebStore's local cache. Defaults to `false` (the cache is disabled). The cache location is determined as per the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html) i.e. `${XDG_CACHE_HOME}/Spdx-Java-Library` or `${HOME}/.cache/Spdx-Java-Library`).
2. `org.spdx.storage.listedlicense.SpdxListedLicenseWebStore.cacheCheckIntervalSecs` - a long that controls how often each cache entry is rechecked for staleness, in units of seconds. Defaults to 86,400 seconds (24 hours). Set to 0 (zero) to have each cache entry checked every time (note: this will result in a lot more network I/O and negatively impact performance, albeit there is still a substantial performance saving vs not using the cache at all).

Note that both of these configuration options can only be modified prior to initialization of Spdx-Java-Library. Once the library is initialized, subsequent changes to either or both of these Java properties will have no effect.

## Update for new properties or classes
To update Spdx-Java-Library, the following is a very brief checklist:

  1. Update the SpdxContants with any new or changed properties and classes
  2. Update the Java code representing the model
  3. Update the SpdxComparer/SpdxFileComparer in the org.spdx.compare package
  4. Update unit tests

## Development Status
Note: This library is mostly stable, but and contains some defects.  Reviews, suggestions are welcome.  Please enter an issue with any suggestions.
