# Release Notes

**Release Notes** of the *Billing Proxy* software:

### <code>2.0.0</code> :calendar: 29/10/2025
**Improvements**
* Update `/billing/previewPrice`, `/billing/bill` and `/billing/instantBill` REST API with DTOs. 
* Add Exception Management.
* Usage of the new `Brokerage Utils` version: `2.2.0`.
* Add **JacksonModuleConfig** to *serialize* and *deserialize* the **TMForum enum types**.
* Usage of `AbstractHealthService` class from `Brokerage Utils` to manage **getInfo()** and **getHealth()** features.
* Add `TrailingSlashFilter` filter to remove trailing slash from request path.
* Generate automatic `REST_APIs.md` file from **Swagger APIs** using the `generate-rest-apis` profile.

**BugFixing**
* Fixed JSON serialization and deserialization issues.
* Remove unused `TmfApiFactory` class.


### <code>1.2.1</code> :calendar: 15/07/2025
**Improvements**
* Update of the `basePath` for building TMForum API URLs with or without **envoy** usage.
* Display `ENV VARs` in the Listener at beginning.


### <code>1.2.0</code> :calendar: 03/06/2025
**Improvements**
* Set of `[2.1.0, 2.2.0)` version of `Brokerage Utils`.
* Update paths for TMForum internal services.


### <code>0.1.0</code> :calendar: 31/03/2025
**Improvements**
* Usage of `2.0.0` version of `Brokerage Utils`.


### <code>0.0.5</code> :calendar: 05/02/2025
**Improvements**
* Refactoring of `InfoProxyController` and add `DateUtils` dependency.
* Add `StartupListener` listener to log (display) the current version of *Billing Proxy* at startup.
* Include exception handling with `ControllerExceptionHandler.java` class.

**BugFixing**
* Set `org.apache.coyote.http11: INFO` to avoid the `Error parsing HTTP request header`.
* Set pattern console to `%d{yyyy-MM-dd HH:mm:ss} [%-5level] %logger{36} - %msg%n`.


### <code>0.0.4</code> :calendar: 27/01/2025
**Feature**
* Added **proxy** functionalities: **bill** - `billing/bill` - **bill for a specific date** - `billing/billForDate` -  ** instant bill** - `billing/instantBill`.

**Improvements**
* Usage of **BILLING_PREFIX** to add prefix for **Swagger APIs**. Use `/billing-engine` prefix in Kubernetes if you use Ingress to separate the billing services. 

### <code>0.0.3</code> :calendar: 20/01/2025
**Feature**
* Add **proxy** functionalities: **preview mode** - `billingPreviewPrice` and `invoicingPreviewTaxes`, **bill mode** - `bill` and `billApplyTaxes`. 


### <code>0.0.2</code> :calendar: 11/11/2024
**Feature**
* Add swagger UI for REST APIs.


**Improvements**
* Usage of **BuildProperties** to get info from `pom.xml` instead of from the `application.yaml`.


### <code>0.0.1</code> :calendar: 25/10/2024
**Feature**
* Init project.
