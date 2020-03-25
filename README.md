# Spdx-Java-Library
![Java CI with Maven](https://github.com/spdx/Spdx-Java-Library/workflows/Java%20CI%20with%20Maven/badge.svg)

Java library which implements the Java object model for SPDX and provides useful helper functions.

## Storage Interface
The Spdx-Java-Library allows for different implementations of SPDX object storage.  The storage facility implements the org.spdx.storage.IModelStore interface.  This is a low level Service Provider Interface (SPI).  The ISerializableModelStore extends the IModelStore and supports serializing and de-serializing the store to an I/O Stream. This interface is currently used to implement JSON, XML, YAML, and RDF/XML formats.  The default storage interface is an in-memory Map which should be sufficient for light weight usage of the library.

Most common use of the library would  de-serialize an existing SPDX document using one of the supported formats and model stores.  To create SPDX objects from scratch, simply create the Java objects found in the org.spdx.library.model package.  The model follows the [SPDX Object Model](https://github.com/spdx/spdx-spec/blob/development/v2.2/model/SPDX-2.1.jpg).  The model objects themselves are stateless and do not store information.  All information is retrieved from the model store when properties are access.  Storage to the classes will store the updates through the use of the storage interface.

## Multi-Threaded Considerations
The methods enterCriticalSection and leaveCritialSection are available to support multi-threaded applications.  These methods serialize access to the model store for the specific SPDX document used for the SPDX model object.

## Getting Started
There are a couple of static classes that help common usage scenarios:

- org.spdx.library.SPDXModelFactory supports the creation of specific model objects
- org.spdx.library.model.license.LicenseInfoFactory supports the parsing of SPDX license expressions, creation, and comparison of SPDX licenses

## Development Status
Note: This library is in development and likely contains defects.  Reviews, suggestions are welcome.  Please enter an issue with any suggestions.
