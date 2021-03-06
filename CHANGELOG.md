<a name="0.4.0"></a>
## 0.4.0 (2019-12-26)

* \[breaking change\] domain is now specified in the cookbook
* added DirectDispatchOven for simpler test setup cases where payloads are dispatched directly to a backend oven without a network hop
* added the ability to set default dispatchers on an oven
* now targeting Java 11
* generated java code now uses 2-space indents

<a name="0.3.7"></a>
## 0.3.7 (2019-02-23)

* allow keys to be manually set on generated ingredient data classes
* update jackson dependency to 2.9.8

<a name="0.3.6"></a>
## 0.3.6 (2018-12-14)

### Bug Fixes

* previously the node generator was bumped to 0.3.6 due to a publishing error, but that caused the build to fail; bumping up the other projects to 0.3.6

<a name="0.3.5"></a>
## 0.3.5 (2018-12-05)

### New Features

* can now generate TypeScript and JavaScript hooks
* added a node module to perform generation
* generated code has better whitespace control and formatting

<a name="0.3.4"></a>
## 0.3.4 (2018-11-14)

### New Features

* can now generate JavaScript ingredients using the js-ingredient flavour

### Bug Fixes

* **generator:** sanitize keywords that conflict with language being generated ([#50](https://github.com/kormide/recipe/issues/50)) ([afad9c6](https://github.com/kormide/recipe/commit/afad9c6))

<a name="0.3.3"></a>
## 0.3.3 (2018-11-01)


### Security Updates

* update jackson dependency to 2.9.7 due to exploits (https://nvd.nist.gov/vuln/detail/CVE-2017-15095)


<a name="0.3.2"></a>
## 0.3.2 (2018-05-28)


### Bug Fixes

* **cake:** do not throw ambiguous key error when key is substring of another key ([#51](https://github.com/kormide/recipe/issues/51)) ([e781b54](https://github.com/kormide/recipe/commit/e781b54))
