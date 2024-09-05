# Getting Started

## SPDX Version 3

### Programmatically Creating SPDX Data

Before executing any of the model class methods, the model versions need to be intialized.  This is done by calling:

```
SpdxModelFactory.init();
```

SPDX data is stored in a "model store" and copying between model stores requires a copy manager.

A simple store is provided in the java library.  To create the simple in-memory model store and a copy manager, execute the following:

```
InMemSpdxStore modelStore = new InMemSpdxStore();
IModelCopyManager copyManager = new ModelCopyManager();
```

All SPDX elements are required to have a unique SPDX ID which is an Object URI.  In the SPDX Java libraries, this is commonly referred to as the `objectUri` to avoid confusion with the SPDX 2.X version short SPDX IDs.

A good practice is to create a common prefix to use for your programatic session.  The prefix should be unique to the session.  There are convenience methods in the library to append identifiers uniques to the model store.

In these examples, we'll use:

```
String prefix = "https://org.spdx.spdxdata/899b1918-f72a-4755-9215-6262b3c346df/";
```

Since SPDX 3.0 requires creation info on every element, the easiest way to start is to use the SPDX 3 model convenience method `SpdxModelClassFactory.createCreationInfo(...)` which will create the `Agent` and `CreationInfo` classes which can be added to all of the subsequent elements.

For example:

```
CreationInfo creationInfo = SpdxModelClassFactory.createCreationInfo(
			modelStore, prefix + "Agent/Gary01123", "Gary O'Neall",
			copyManager);
```

We're now ready to create our first SPDX element.  You can start anywhere, but let's start with an SBOM element.

There is a factory method you can use to get started:

```
Sbom sbom = SpdxModelClassFactory.getModelObject(modelStore, 
			prefix + "sbom/mysbom", SpdxConstantsV3.SOFTWARE_SBOM, 
			copyManager, true, prefix);
```

Let's not forget to add the creation info:

```
sbom.setCreationInfo(creationInfo);
```

From here on, things get easier.  We can get and set properties to the sbom we just created.

If we want to create another SPDX object or element, we can use the builder convenience methods available to all SPDX objects.  For example, if we want to create a package to add to the SBOM we can call:

```
sbom.getElements().add(
			sbom.createSpdxPackage(prefix + "package/mypackage")
			.setName("Package Name")
			.build()
			);
```

The model store, creation info, copy manager, and prefix information will all be copied from the sbom allowing you to focus just on the properties you need to add.