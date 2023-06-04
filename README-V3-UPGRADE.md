# Upgrading from version 2 to version 3

With the support of SPDX 3.0, several changes have been made to the library codebase that will break previous API's.
Although we tried to keep breaking changes to a minimum, some of the changes were necessary due to breaking changes in the spec. itself.
We also took advantage of the changes to fix some annoying design flaws in the previous implementation of the library.

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
`TypedValue` 