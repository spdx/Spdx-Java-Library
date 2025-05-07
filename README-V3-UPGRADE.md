# Upgrading from version 2 to version 3

With the support of SPDX 3.0, several changes have been made to the library codebase that will break previous API's.
Although we tried to keep breaking changes to a minimum, some of the changes were necessary due to breaking changes in the spec. itself.
We also took advantage of the changes to fix some annoying design flaws in the previous implementation of the library.

## New initialisation methods

v2 of the library introduces an explicit initialisation process.  This involves:

1. (optional) Calling `org.spdx.core.DefaultModelStore.initialize(...)` with your own choice of ModelStore and CopyManager.
2. (required) Calling `org.spdx.library.SpdxModelFactory.init()` (this must happen _soon after_ the call to `DefaultModelStore.initialize()` - ideally before any other SPDX classes or methods are used).

Notes:

- Step #1 is optional; if not provided, a default ModelStore and CopyManager will be automatically created and configured by `SpdxModelFactory.init()`.
- Once initialized (via Step #2), further calls to `DefaultModelStore.initialize(...)` will be ignored.

## Deprecated configuration for using online licenses from Jar file removed

The configuration properties `OnlyUseLocalLicenses`  and `SPDXParser.OnlyUseLocalLicenses` will no longer force the 
library to use the licenses distributed with the Jar file.  The supported property `org.spdx.useJARLicenseInfoOnly` should be used.

## Classes and Methods moved to SPDX Java Core library

The SPDX Java Core Library is in a separate repository and jar file.

The following classes and methods are moved from `org.spdx.library` to `org.spdx.core`:

- `DefaultModelStore`
- Most exception classes including `InvalidSPDXAnalysisException`
- `ModelCollection`
- `ModelSet`
- `SimpleUriValue`
- `TypedValue`

The packages in `org.spdx.licenseTemplates` are now in the `java-spdx-core` repository.

A new class `LicenseTextHelper` was added and the method `isLicenseTextEquivalent(String, String)` along with many supporting methods were moved to `LicenseTextHelper` from `org.spdx.utility.compare.LicenseCompareHelper`.

## Other moved classes

- `org.spdx.library.model.license.ListedLicenses` moved to `org.spdx.library.ListedLicenses`

## Removed methods

Some methods that exist in the v2.3 model classes (e.g. `org.spdx.library.model.license.SpdxListedLicense.getLicenseTextHtml()`, `org.spdx.library.model.license.SpdxListedLicense.getCrossRef()`, `org.spdx.library.model.license.ListedLicenseException.getExceptionTextHtml()`) have been removed and do not have an equivalent in the v3.0 model classes.

## Changes to SPDX version 2 package, class, and method names

To support accessing SPDX 2.X model object while updating the library for SPDX 3.0 support, the package names for the SPDX 2.X model objects are now named `org.spdx.library.model.v2.[package]`.

Many of the class and property names have been changed to append `CompatV2` to clearly designate a compatible object is being referenced.

Also note that the model classes are now stored in a separate repository `spdx-java-model-2_X`.

## SPDX version 3.x package

SPDX 3.0.1 model objects can be found in packages underneath `org.spdx.library.model.v3_0_1`.  Note that Spdx-Java-Library will only track the latest minor+patch version for the last 2 major SPDX specification versions (i.e. v2.3.0 and v3.0.1), because minor+patch versions of the SPDX specification are backwards compatible _within_ a major version.

## Changes to ExternalElement and ExternalExtractedLicenseInfo (SPDX Version 2.X classes)

- Constructors changed to take the document URI for the document containing the external element or license.  This is different from the previous constructor which took the document URI of the document containing the reference and an ID of the form `DocumentRef-XX:[ID]`  To accomodate compatibility, the constructors
will check for the old DocumentRef format and attempt a conversion.
- Added a method `referenceElementId(SpdxDocument documentReferencingExternal)` which will convert return the `DocumentRef-XX:[ID]`.  This should be used in place of the getId which previously returned this format.

Note that this incompatibility was introduced due to using a common mode store API which in some cases will not have the documentUri as a required parameter

## Changes to deserialize interface

Since SPDX documents are not generally required in SPDX spec version 3.0, the SPDX namespace was removed from the return value for deserialized and also removed as a parameter for the serialize method.  Serialize will now serialize all objects - which may be multiple SPDX documents.

To find all the SPDX documents in a serialization, you can execute:

```java
List<SpdxDocument> docs = (List<SpdxDocument>)SpdxModelFactory.getSpdxObjects(store, null, SpdxConstantsCompatV2.CLASS_SPDX_DOCUMENT, null, null)
				.collect(Collectors.toList());
```

after deserialization to get a list of all SPDX documents.

For the RDF store, to keep compatible with the SPDX 2.X requirements, it now only supports a single document namespace.

## Changes to the SPI for the Model Store

### Change propertyName to propertyDescriptor

One significant change to the model store which impacts most of the API's.
All `String` `propertyName` properties are replaced by a `propertyDescriptor` of type `PropertyDescriptor`.
The `PropertyDescriptor` has a `name` property and a `nameSpace` property.
The property constants defined in `org.spdx.library.SpdxConstants` have all been changed to use constant `PropertyDescriptor`s.
If you're using the constants, you may not need to change much beyond the method signatures for anything that was passing along the `propertyName`.

### Make DocumentNamespace Optional

In SPDX 3.0, not all elements are contained within an SPDX document and we can't be guaranteed that a namespace is available for all `TypedValue` typed properties.  Methods that are passed a `DocumentNamespace` and an `id` now are passed a URI.

To translate from SPDX 2.X, the `DocumentNamespace` concatenated with the `id` can be used for the URI.

### Change TypedValue structure

`TypedValue` now takes an ObjectURI rather than an ID.
Note that the method signature has not changed, so you may need to manually search for usage in order to change.
There is a convenience helper method `CompatibleModelStoreWrapper.typedValueFromDocUri(String documentUri, String id, boolean anonymous, String type)` that will convert from the SPDX V2 TypedValue to the current version.

### CompatibleModelStoreWrapper

To help with the migration, the `CompatibleModelStoreWrapper` class was introduced supporting the `IModelStore` interface taking a base store as a parameter in the constructor.  This class "wraps" the base store and supports the SPDX 2 methods which take the document namespace parameters.

There is also a convenience static method to convert a namespace and ID to an Object URI.
