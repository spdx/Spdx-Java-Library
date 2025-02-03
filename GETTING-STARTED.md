# Getting Started

## Installation

The library is available in Maven Central as
[`org.spdx:java-spdx-library`](https://search.maven.org/artifact/org.spdx/java-spdx-library)
(note the order of the word "java-spdx").

If you are using Maven, you can add the following dependency in your POM file:

```xml
<dependency>
  <groupId>org.spdx</groupId>
  <artifactId>java-spdx-library</artifactId>
  <version>(,2.0]</version>
</dependency>
```

## Using the library for license analysis

The static class `org.spdx.library.model.license.LicenseInfoFactory` supports the parsing of
  SPDX license expressions, creation, and comparison of SPDX licenses.

The `LicenseInfoFactory` will initialize the library and work with both the SPDX spe version 3
and the SPDX spec version 2 license models.  SPDX spec version to methods end in `CompatV2`.

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

Many factory and helper methods in the library make use of a DefaultModelStore 
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

## SPDX Spec Version 3

### Programmatically Creating SPDX Data

See the [Spdx3Example.java](src/examples/java/org/spdx/example/Spdx3Example.java) file for the complete code example.

All SPDX elements are required to have a unique SPDX ID which is an Object URI.  In the SPDX Java libraries, this is commonly referred to as the `objectUri` to avoid confusion with the SPDX 2.X version short SPDX IDs.

A good practice is to create a common prefix to use for your programmatic session.  The prefix should be unique to the session.  There are convenience methods in the library to append identifiers unique to the model store.

In these examples, we'll use:

```java
String prefix = "https://org.spdx.spdxdata/899b1918-f72a-4755-9215-6262b3c346df/";
```

Since SPDX 3.0 requires creation info on every element, the easiest way to start is to use the SPDX 3 model convenience method `SpdxModelFactory.createCreationInfoV3(...)` which will create the `Agent` and `CreationInfo` classes which can be added to all the subsequent elements.

For example:

```java
CreationInfo creationInfo = SpdxModelFactoryV3.createCreationInfo(
			modelStore, prefix + "Agent/Gary01123", "Gary O'Neall",
			copyManager);
```

We're now ready to create our first SPDX element.  You can start anywhere, but let's start with an SBOM element.

Every SPDX object has builder methods for all the SPDX objects.

To build the initial SBOM, we can call the builder method for Sbom.
The only parameter is the Object URI for the element - we'll use the same prefix for consistency.
We might as well add a name while we're at it.

```java
Sbom sbom = creationInfo.createSbom(prefix + "sbom/mysbom")
        .setName("My SBOM")
        .build();
```

Note that the builder add all the creation information from the 
object calling the builder method, so no need to add the creationInfo,
modelStore or copyManager.

From here on out, we can just use the builder methods to create objects and the getter / setter methods.

For example, if we want to create a package to add to the SBOM as a root we can call:

```java
SpdxPackage pkg = sbom.createSpdxPackage(prefix + "package/mypackage")
        .setName("Package Name")
        .build();
        sbom.getElements().add(pkg);
        sbom.getRootElements().add(pkg);
```

The model store, creation info, copy manager, and prefix information will all be copied from the sbom allowing you to focus just on the properties you need to add.

## SPDX Spec Version 2

### Programmatically Creating SPDX Data

SPDX Spec version 2 stores all date within an SPDX document.  SPDX documents have a single document URI which is a 
prefix for all IDs within the document.

The SPDX document MUST be unique.

Below is an example:

```java
		String newDocumentUri = "http://spdx.org/spdxdocs/spdx-example2-444504E0-4F89-41D3-9A0C-0305E82CCCCC";
```

We then need to create the SPDX document:

```java
			SpdxDocument myDoc = new SpdxDocument(
					inMemStore,						// here we'll just use a very simple in memory store which doesn't support serialization
					newDocumentUri,					// the URI of the document - must be globally unique
					null,							// an optional copy manager can be provided if working with more than one store
					true);							// this time, we want to create it
```

We now need to add the required fields to make this a valid SPDX document:

```java
			myDoc.setCreationInfo(				// Set the required creationInfo
					myDoc.createCreationInfo(	// All model objects have a set of convenience methods to create 
												// other model objects using the same model store and document URI
							Arrays.asList(new String[] {"Tool: Sample App"}), // creators
							new SimpleDateFormat(SpdxConstants.SPDX_DATE_FORMAT).format(new Date())));
												// creation date - note that SpdxConstants has several useful constant values
			myDoc.setSpecVersion(Version.CURRENT_SPDX_VERSION); // the Version class has constants defined for all supported SPDX spec versions
			myDoc.setName("My Document");
			// The LicenseInfoFactory contains some convenient static methods to manage licenses including
			// a license parser.  Note that we have to pass in the model store and document URI so that the
			// license is created in the same store.
			myDoc.setDataLicense(LicenseInfoFactory.parseSPDXLicenseString("CC0-1.0", inMemStore, newDocumentUri, null));
			// We need something for the document to describe, we'll create an SPDX file
			AnyLicenseInfo apacheLicense = LicenseInfoFactory.parseSPDXLicenseString("Apache-2.0", inMemStore, newDocumentUri, null);
			SpdxFile file = myDoc.createSpdxFile(
					SpdxConstants.SPDX_ELEMENT_REF_PRENUM + "44",
					"./myfile/name", 
					apacheLicense, 
					Arrays.asList(new AnyLicenseInfo[] {apacheLicense}),
					"Copyright me, 2023",
					myDoc.createChecksum(ChecksumAlgorithm.SHA1, "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12"))
					.build();  // The more complex model objects follows a builder pattern
			myDoc.getDocumentDescribes().add(file);
```

Similar to SPDX spec version 3, the create methods in the SPDX spec version 2 objects will copy the
model store, copy manager, and document uri to the newly created objects.