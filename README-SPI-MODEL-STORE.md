# Implementing SPDX Model Stores

This is a brief overview on how to create and maintain a Model Store for the SPDX Java Library.

Model stores are used to store and retrieve data used by SPDX objects.

NOTE: This README is currently under development.

## Storing and Retrieving Values

## Using the Serialization interfaces

Note: You can extend the default org.spdx.storage.simple.InMemSpdxStore with a couple of serialization / de-serialization methods to implement a storage interface to a serializeable format (such as a JSON or YAML file).

## Handling Collections of Values

## Notes on Concurrency and Multi-threading