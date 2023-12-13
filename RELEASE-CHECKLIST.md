# Release Checklist for the SPDX Java Tools

- [ ] Check for any warnings from the compiler and findbugs
- [ ] Run dependency check to find any potential vulnerabilities `mvn dependency-check:check`
- [ ] Test the release `mvn release:prepare -DdryRun`
- [ ] Run `mvn release:prepare` - you will be prompted for the release - typically take the defaults
- [ ] Run `mvn release:perform`
- [ ] Release artifacts to Maven Central
- [ ] Create a Git release including release notes
- [ ] Zip up the files from the Maven archive and add them to the release
