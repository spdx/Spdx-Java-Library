# Spdx-Java-Library

[![Maven Central Version](https://img.shields.io/maven-central/v/org.spdx/java-spdx-library)](https://central.sonatype.com/artifact/org.spdx/java-spdx-library)
[![javadoc](https://javadoc.io/badge2/org.spdx/java-spdx-library/javadoc.svg)](https://javadoc.io/doc/org.spdx/java-spdx-library)
![Java CI with Maven](https://github.com/spdx/Spdx-Java-Library/workflows/Java%20CI%20with%20Maven/badge.svg)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=bugs)](https://sonarcloud.io/dashboard?id=java-spdx-library)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=security_rating)](https://sonarcloud.io/dashboard?id=java-spdx-library)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=java-spdx-library)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=sqale_index)](https://sonarcloud.io/dashboard?id=java-spdx-library)

Spdx-Java-Library is a Java library which implements the Java object model for SPDX and provides useful helper functions.

The library is available in Maven Central as
[`org.spdx:java-spdx-library`](https://central.sonatype.com/artifact/org.spdx/java-spdx-library)
(note the order of the word "java-spdx").

If you are using Maven, you can add the following dependency in your POM file:

```xml
<dependency>
  <groupId>org.spdx</groupId>
  <artifactId>java-spdx-library</artifactId>
  <version>[2.0,3.0)</version>
</dependency>
```

See the [GETTING-STARTED.md](GETTING-STARTED.md) file for installation
instructions and how to get started in different scenarios.

## Table of Contents

- [Library Version Compatibility](#library-version-compatibility)
- [API Documentation](#api-documentation)
- [Storage Interface](#storage-interface)
  - [Storage Interface Usage](#storage-interface-usage)
- [Multi-Threaded Considerations](#multi-threaded-considerations)
- [Configuration Options](#configuration-options)
- [Initialization](#initialization)
- [Update for New Versions of the Spec](#update-for-new-versions-of-the-spec)
- [Contributing](#contributing)

## Library Version Compatibility

Library version 2.0.0 and higher is not compatible with previous versions of the library due to breaking changes introduced in SPDX 3.0.

However, the library supports both SPDX specification versions 2.X and 3.X.

See [README-V3-UPGRADE.md](README-V3-UPGRADE.md) for library upgrade instructions.

## API Documentation

Here are links to the API documentation for the family of SPDX Java libraries.

"release" points to the API documentation of the latest stable version of the library, while "dev" points to the API documentation generated every time there is an update in the library's GitHub repository.

| Library | Description | Doc (release) | Doc (dev) |
|-|-|-|-|
| [java-spdx-library][lib-gh] | Core library implementing the SPDX Java object model and helper functions. | [release][lib-docr] | [dev][lib-docd] |
| [spdx-java-core][core-gh] | Core utilities and shared functionality for SPDX Java libraries. | [release][core-docr] | [dev][core-docd] |
| Model | | | |
| [spdx-java-model-2_X][model2-gh] | Model implementation for SPDX 2.X specification. | [release][model2-docr] | [dev][model2-docd] |
| [spdx-java-model-3_0][model3-gh] | Model implementation for SPDX 3.0 specification. | [release][model3-docr] | |
| Model store | | | |
| [spdx-jackson-store][jackson-gh] | Jackson Databind-based storage implementation for SPDX objects. Supports JSON, YAML and XML formats. | [release][jackson-docr] | [dev][jackson-docd] |
| [spdx-rdf-store][rdf-gh] | RDF-based storage implementation for SPDX objects. | [release][rdf-docr] | [dev][rdf-docd] |
| [spdx-spreadsheet-store][spreadsheet-gh] | Spreadsheet-based storage implementation for SPDX objects. Supports XLS and XLSX formats. | [release][spreadsheet-docr] | [dev][spreadsheet-docd] |
| [spdx-tagvalue-store][tagvalue-gh] | Tag-value-file-based storage implementation for SPDX objects. | [release][tagvalue-docr] | [dev][tagvalue-docd] |
| [spdx-v3jsonld-store][v3jsonld-gh] | JSON-LD-based storage implementation for SPDX 3 objects. | [release][v3jsonld-docr] | [dev][v3jsonld-docd] |
| Tools | | | |
| [spdx-model-to-java][genjava-gh] | Generates Java source files from SPDX 3 model files. Used for spdx-java-model-3_0. | | [dev][genjava-docd] |
| [spdx-maven-plugin][maven-gh] | Maven plugin producing SPDX documents for artifacts described in the Maven POM file. | [release][maven-docr] | [dev][maven-docd] |
| [tools-java][tools-gh] | Command-line tools for working with SPDX documents. | [release][tools-docr] | |

[lib-gh]: https://github.com/spdx/Spdx-Java-Library
[lib-docr]: https://javadoc.io/doc/org.spdx/java-spdx-library
[lib-docd]: https://spdx.github.io/Spdx-Java-Library/
[core-gh]: https://github.com/spdx/spdx-java-core
[core-docr]: https://javadoc.io/doc/org.spdx/spdx-java-core
[core-docd]: https://spdx.github.io/spdx-java-core/
[model2-gh]: https://github.com/spdx/spdx-java-model-2_X
[model2-docr]: https://javadoc.io/doc/org.spdx/spdx-java-model-2_X
[model2-docd]: https://spdx.github.io/spdx-java-model-2_X/
[model3-gh]: https://github.com/spdx/spdx-java-model-3_0
[model3-docr]: https://javadoc.io/doc/org.spdx/spdx-java-model-3_0
[jackson-gh]: https://github.com/spdx/spdx-java-jackson-store
[jackson-docr]: https://javadoc.io/doc/org.spdx/spdx-jackson-store
[jackson-docd]: https://spdx.github.io/spdx-java-jackson-store/
[rdf-gh]: https://github.com/spdx/spdx-java-rdf-store
[rdf-docr]: https://javadoc.io/doc/org.spdx/spdx-rdf-store
[rdf-docd]: https://spdx.github.io/spdx-java-rdf-store/
[spreadsheet-gh]: https://github.com/spdx/spdx-java-spreadsheet-store
[spreadsheet-docr]: https://javadoc.io/doc/org.spdx/spdx-spreadsheet-store
[spreadsheet-docd]: https://spdx.github.io/spdx-java-spreadsheet-store/
[tagvalue-gh]: https://github.com/spdx/spdx-java-tagvalue-store
[tagvalue-docr]: https://javadoc.io/doc/org.spdx/spdx-tagvalue-store
[tagvalue-docd]: https://spdx.github.io/spdx-java-tagvalue-store/
[v3jsonld-gh]: https://github.com/spdx/spdx-java-v3jsonld-store
[v3jsonld-docr]: https://javadoc.io/doc/org.spdx/spdx-v3jsonld-store
[v3jsonld-docd]: https://spdx.github.io/spdx-java-v3jsonld-store/
[genjava-gh]: https://github.com/spdx/spdx-model-to-java
[genjava-docd]: https://spdx.github.io/spdx-model-to-java/
[maven-gh]: https://github.com/spdx/spdx-maven-plugin
[maven-docr]: https://javadoc.io/doc/org.spdx/spdx-maven-plugin
[maven-docd]: https://spdx.github.io/spdx-maven-plugin/
[tools-gh]: https://github.com/spdx/tools-java
[tools-docr]: https://javadoc.io/doc/org.spdx/tools-java

## Storage Interface

- The Spdx-Java-Library allows for different implementations of SPDX object
  storage.
- The storage facility implements the `org.spdx.storage.IModelStore` interface.
  This is a low level Service Provider Interface (SPI).
- The `ISerializableModelStore` extends the `IModelStore` and supports
  serializing and de-serializing the store to an I/O Stream.
  This interface is currently used to implement JSON, XML, YAML, and RDF/XML
  formats.
- The default storage interface is an in-memory Map which should be sufficient
  for lightweight usage of the library.

### Storage Interface Usage

- Most common use of the library would de-serialize an existing SPDX document
  using one of the supported formats and model stores.
- To create SPDX objects from scratch, simply create the Java objects found in
  the `org.spdx.library.model` package.
- The model follows the [SPDX Object Model][spdx-object-model].
- The model objects themselves are stateless and do not store information.
- All information is retrieved from the model store when properties are access.
- Storage to the classes will store the updates through the use of the storage
  interface.

[spdx-object-model]: https://github.com/spdx/spdx-spec/blob/2a7aff7afa089a774916bd5c64fc2cb83637ea07/model/SPDX-UML-Class-Diagram.jpg

## Multi-Threaded Considerations

The methods `enterCriticalSection` and `leaveCriticalSection` are available to
support multi-threaded applications.

These methods serialize access to the model store for the specific SPDX
document used for the SPDX model object.

## Configuration options

`Spdx-Java-Library` can be configured using either Java system properties or a Java properties file located in the runtime CLASSPATH at `/resources/spdx-java-library.properties`.

The library has these configuration options:

1. `org.spdx.useJARLicenseInfoOnly` - a boolean that controls whether the (potentially out of date) listed license information bundled inside the JAR is used (true), vs the library downloading the latest files from the SPDX website (false). Default is false (always download the latest files from the SPDX website).
2. `org.spdx.downloadCacheEnabled` - a boolean that enables or disables the download cache. Defaults to `false` (the cache is disabled). The cache location is determined as per the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html) (i.e. `${XDG_CACHE_HOME}/Spdx-Java-Library` or `${HOME}/.cache/Spdx-Java-Library`).
3. `org.spdx.downloadCacheCheckIntervalSecs` - a long that controls how often each cache entry is rechecked for staleness, in units of seconds. Defaults to 86,400 seconds (24 hours). Set to 0 (zero) to have each cache entry checked every time (note: this will result in a lot more network I/O and negatively impact performance, albeit there is still a substantial performance saving vs not using the cache at all).

Note that these configuration options can only be modified prior to first use
of Spdx-Java-Library.
*Once the library is initialized, subsequent changes will have no effect.*

## Initialization

Before executing any of the model class methods, the model versions need to be initialized.  This is done by calling:

```java
SpdxModelFactory.init();
```

SPDX data is stored in a "model store" and copying between model stores requires a copy manager.

A simple store is provided in the java library.  To create the simple in-memory model store and a copy manager, execute the following:

```java
InMemSpdxStore modelStore = new InMemSpdxStore();
IModelCopyManager copyManager = new ModelCopyManager();
```

Many factory and helper methods in the library make use of a `DefaultModelStore`
if no model store or copy manager is specified.

The `SpdxModelFactory.init()` will create defaults for this purpose.

If you would like to use a different default model store and/or copy manager, you can call:

```java
DefaultModelStore.initialize(IModelStore newModelStore, String newDefaultDocumentUri,
                            IModelCopyManager newDefaultCopyManager);
```

The `newDefaultDocumentUri` is a default document URI used for SPDX Spec version 2 model objects.

IMPORTANT NOTE: The call to `DefaultModelStore.initialize` must be made prior to or immediately after the call
to `SpdxModelFactory.init()`.  Otherwise, any data stored in the previous default model object will be lost.
The `SpdxModelFactory.init()` will not overwrite an already initialized default model store.

## Update for new versions of the spec

To update Spdx-Java-Library, the following is a very brief checklist:

1. Create a Java .jar file for the new version which contains an implementation of `ISpdxModelInfo` - typically named `SpdxModelInfoVXXX` - where "XXX" is the version of the spec.
2. Update the SpdxModelFactory source file to load the model info by adding the line `ModelRegistry.getModelRegistry().registerModel(new SpdxModelInfoVXXX());` in the static block at the very beginning of the class.
3. If there are any conversions that are needed when copying to or from the new model version, add conversion code to the `ModelCopyConverter` class.
4. Update SpdxModelFactory unit test for the highest version check.

## Development Status

Stable.

## Contributing

Reviews and suggestions are welcome.
Please [submit an issue][issues] with any suggestions.

See [CONTRIBUTING.md][contributing] for contribution guidelines.

[issues]: https://github.com/spdx/Spdx-Java-Library/issues
[contributing]: https://github.com/spdx/Spdx-Java-Library/blob/master/CONTRIBUTING.md
