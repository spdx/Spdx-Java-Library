# Spdx-Java-Library
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.spdx/java-spdx-library/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.spdx/java-spdx-library)
![Java CI with Maven](https://github.com/spdx/Spdx-Java-Library/workflows/Java%20CI%20with%20Maven/badge.svg)

Java library which implements the Java object model for SPDX and provides useful helper functions.

# Code quality badges

|   [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=bugs)](https://sonarcloud.io/dashboard?id=java-spdx-library)    | [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=security_rating)](https://sonarcloud.io/dashboard?id=java-spdx-library) | [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=java-spdx-library) | [![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=java-spdx-library&metric=sqale_index)](https://sonarcloud.io/dashboard?id=java-spdx-library) |

## Library Version Compatibility

Library version 2.0.0 and higher is not compatible with previous versions of the library due to breaking changes introduced in SPDX 3.0.

The library does support the spec versions 2.X and 3.X.

See the [README-V3-UPGRADE.md](README-V3-UPGRADE.md) file for information on how to upgrade from earlier versions of the library.

## Storage Interface
The Spdx-Java-Library allows for different implementations of SPDX object storage.  The storage facility implements the org.spdx.storage.IModelStore interface.  This is a low level Service Provider Interface (SPI).  The ISerializableModelStore extends the IModelStore and supports serializing and de-serializing the store to an I/O Stream. This interface is currently used to implement JSON, XML, YAML, and RDF/XML formats.  The default storage interface is an in-memory Map which should be sufficient for light weight usage of the library.

Most common use of the library would  de-serialize an existing SPDX document using one of the supported formats and model stores.  To create SPDX objects from scratch, simply create the Java objects found in the org.spdx.library.model package.  The model follows the [SPDX Object Model](https://github.com/spdx/spdx-spec/blob/2a7aff7afa089a774916bd5c64fc2cb83637ea07/model/SPDX-UML-Class-Diagram.jpg).  The model objects themselves are stateless and do not store information.  All information is retrieved from the model store when properties are access.  Storage to the classes will store the updates through the use of the storage interface.

## Multi-Threaded Considerations
The methods enterCriticalSection and leaveCritialSection are available to support multi-threaded applications.  These methods serialize access to the model store for the specific SPDX document used for the SPDX model object.

## Getting Started
The library is available in [Maven Central org.spdx:java-spdx-library](https://search.maven.org/artifact/org.spdx/java-spdx-library).

If you are using Maven, you can add the following dependency in your POM file:
```
<dependency>
  <groupId>org.spdx</groupId>
  <artifactId>java-spdx-library</artifactId>
  <version>(,2.0]</version>
</dependency>
```

[API JavaDocs are available here.](https://spdx.github.io/Spdx-Java-Library/)

There are a couple of static classes that help common usage scenarios:

- org.spdx.library.SpdxModelFactory supports the creation of specific model objects
- org.spdx.library.model.license.LicenseInfoFactory supports the parsing of SPDX license expressions, creation, and comparison of SPDX licenses

The first thing that needs to be done in your implementation is call `SpdxModelFactory.init()` - this will load all the supported versions.

## Update for new versions of the spec
To update Spdx-Java-Library, the following is a very brief checklist:

  1. Create a Java .jar file for the new version which contains an implementation of `ISpdxModelInfo` - typically named SpdxModelInfoVXXX - where XXX is the version of the spec.
  2. Update the SpdxModelFactory source file to load the model info by adding the line `ModelRegistry.getModelRegistry().registerModel(new SpdxModelInfoVXXX());` in the static block at the very beginning of the class.
  3. If there are any conversions that are needed when copying to or from the new model version, add conversion code to the `ModelCopyConverter` class.
  4. Update SpdxModelFactory unit test for the highest version check

## Development Status
Note: This library is currently unstable, and under development.  Reviews, suggestions are welcome.  Please enter an issue with any suggestions.
