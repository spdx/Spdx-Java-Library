/*
 * SPDX-FileCopyrightText: Copyright (c) 2025 Source Auditor Inc.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spdx.example;

import org.spdx.core.IModelCopyManager;
import org.spdx.core.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxModelFactory;
import org.spdx.library.model.v3_0_1.SpdxModelClassFactoryV3;
import org.spdx.library.model.v3_0_1.core.CreationInfo;
import org.spdx.library.model.v3_0_1.software.Sbom;
import org.spdx.library.model.v3_0_1.software.SpdxPackage;
import org.spdx.storage.simple.InMemSpdxStore;

/**
 * Example which creates an SBOM from scratch
 * <p>
 * NOTE: Snippets from this file are used in the GETTING_STARTED.md file - any changes to this file
 * should update the GETTING_STARTED.md file
 */
public class Spdx3Example {

    public static Sbom createSbomFromScratch() throws InvalidSPDXAnalysisException {
        // Before executing any of the model class methods, the model versions need to be initialized
        SpdxModelFactory.init();
        // SPDX data is stored in a "model store" and copying between model stores requires a copy manager.
        // A simple store is provided in the java library
        InMemSpdxStore modelStore = new InMemSpdxStore();
        IModelCopyManager copyManager = new ModelCopyManager();

        // All SPDX elements are required to have a unique SPDX ID which is an Object URI.
        // In the SPDX Java libraries, this is commonly referred to as the objectUri to avoid confusion with the SPDX 2.X version short SPDX IDs.
        // A good practice is to create a common prefix to use for your programmatic session.
        // The prefix should be unique to the session.
        // There are convenience methods in the library to append identifiers unique to the model store.
        String prefix = "https://org.spdx.spdxdata/899b1918-f72a-4755-9215-6262b3c346df/";

        // Since SPDX 3.0 requires creation info on every element,
        // the easiest way to start is to use the SPDX 3 model convenience method SpdxModelClassFactoryV3.createCreationInfo(...)
        // which will create the Agent and CreationInfo classes which can be added to all the subsequent elements.
        CreationInfo creationInfo = SpdxModelClassFactoryV3.createCreationInfo(
                modelStore, prefix + "Agent/Gary01123", "Gary O'Neall",
                copyManager);

        // Every SPDX object has builder methods for all the SPDX objects.
        // To build the initial SBOM, we can call the builder method for Sbom.
        // The only parameter is the Object URI for the element - we'll use the same prefix for consistency.
        Sbom sbom = creationInfo.createSbom(prefix + "sbom/mysbom")
                // we might as well add a name while we're at it. - you can add any of the property during the build
                // Don't worry if you don't have all the properties, they can be added later
                .setName("My SBOM")
                .build();

        // From here on out, we can just use the builder methods to create objects and the getter / setter methods
        // on the objects to update any properties.
        // Let's create an SPDX Package and add it to the SBOM and make it the root
        SpdxPackage pkg = sbom.createSpdxPackage(prefix + "package/mypackage")
                .setName("Package Name")
                .build();
        sbom.getElements().add(pkg);
        sbom.getRootElements().add(pkg);

        return sbom;
    }

}
