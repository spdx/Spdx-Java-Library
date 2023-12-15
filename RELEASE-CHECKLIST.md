# Release Checklist for the SPDX Java Tools

- [ ] Check for any warnings from the compiler and findbugs
- [ ] Run unit tests for all packages that depend on the library
- [ ] Run unit tests with `export SPDX_JAVA_LIB_RUN_SLOW_TESTS=true` to ensure the extended (slow) test suite passes
- [ ] Run dependency check to find any potential vulnerabilities `mvn dependency-check:check`
- [ ] Test the release `mvn release:prepare -DdryRun`
- [ ] Run `mvn release:prepare` - you will be prompted for the release - typically take the defaults
- [ ] Run `mvn release:perform`
- [ ] Release artifacts to Maven Central
- [ ] Create a Git release including release notes
- [ ] Zip up the files from the Maven archive and add them to the release
