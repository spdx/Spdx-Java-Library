# Upgrading from version 2 to version 3

With the support of SPDX 3.0, several changes have been made to the library codebase that will break previous API's.
Although we tried to keep breaking changes to a minimum, some of the changes were necessary due to breaking changes in the spec. itself.
We also took advantage of the changes to fix some annoying design flaws in the previous implementation of the library.

## Changes to ExternalElement and ExternalExtractedLicenseInfo (SPDX Version 2.X classes)

- Constructors changed to take the document URI for the document containing the external element or license.  This is different from the previous constructor which took the document URI of the document containing the reference and an ID of the form `DocumentRef-XX:[ID]`  To accomodate compatibility, the constructors
will check for the old DocumentRef format and attempt a conversion.
- Added a method `referenceElementId(SpdxDocument documentReferencingExternal)` which will convert return the `DocumentRef-XX:[ID]`.  This should be used in place of the getId which previously returned this format.

Note that this incompatibility was introduced due to using a common mode store API which in some cases will not have the documentUri as a required parameter

## Changes to the SPI for the Model Store

### Change propertyName to propertyDescriptor

One significant change to the model store which impacts most of the API's.
All `String` `propertyName` properties are replaced by a `propertyDescriptor` of type `ProperyDescriptor`.
The `PropertyDescriptor` has a `name` property and a `nameSpace` property.
The property constants defined in  `org.spdx.library.SpdxConstants` have all been changed to use constant `PropertyDescriptor`s.
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