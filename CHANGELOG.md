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
