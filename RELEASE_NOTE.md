# Release Notes

**Release Notes** of the *Billing Proxy* software:

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
