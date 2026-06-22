ala-bootstrap5   [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/ala-bootstrap5.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/ala-bootstrap5)
=========
## Grails 7
The latest version is: `2.0.0-SNAPSHOT`, which supports Grails 7.1.1.
NOTES:
Grails 7.1.1 is not compatible with Grails 6


## Grails 6
The Grails 6 version of this plugin can be found on the `main` branch of this repo.

## Usage
```
compile ":ala-bootstrap5:1.0.0-SNAPSHOT"
```

## Description
This is a Grails Plugin to provide the basic set of web assets to correctly apply the **new 2019** ala web theme based on [bootstrap 5.3](http://getbootstrap.com)

Note: templates, some CSS & JS files are located in the `commonui-bs5` git repository.

## Integration with ALA-Auth plugin

This plugin assumes but does not directly depend on the ala-auth plugin.  It switches login URL behaviour based on the `security.oidc.enabled` config property, using the ala-auth provided login controller instead of a redirect straight to the configured CAS server.

### Use without the ALA-Auth plugin

To use custom login behaviour with this plugin without the ALA-Auth plugin, set `security.oidc.enabled` to true then define a URL mapping named 'login', which can accept a single parameter `url` which is the URL to redirect to after logging in.

## Grails taglib integration with Bootstrap
This plugin borrows the taglib used in the [Grails Twitter Bootstrap plugin](https://grails.org/plugin/twitter-bootstrap) to modify the way some Core Grails tags are rendered (eg: pagination)

To enable the boostrap compatible rendering add the following line to your `Config.groovy`:

```
grails.plugins.twitterbootstrap.fixtaglib = true
```
