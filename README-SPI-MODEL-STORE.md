# Implementing SPDX Model Stores

This is a brief overview on how to create and maintain a Model Store for the SPDX Java Library.

Model stores are used to store and retrieve data used by SPDX objects.

NOTE: This README is currently under development.

## Storing and Retrieving Values

### Converting Object Types

The class org.spdx.library.ModelStorageClassConverter contains static methods to convert object types supported by the SPI.

storedObjectToModelObject will convert a stored object to a Model object.

modelObjectToStoredObject will convert a model object to a stored object.

These methods should be used to avoid common errors when converting between supported object types.

## Using the Serialization interfaces

Note: You can extend the default org.spdx.storage.simple.InMemSpdxStore with a couple of serialization / de-serialization methods to implement a storage interface to a serializable format (such as a JSON or YAML file).

## Handling Collections of Values

## Notes on Concurrency and Multi-threading
