# Release Notes

**Release Notes** of the *Billing Scheduler* software:

### <code>0.0.4</code> :calendar: 23/01/2025
**Feature**
* Usage of **SWAGGER_PREFIX** to add prefix for **Swagger APIs**. Use `/billing-engine` prefix in Kubernetes if you use Ingress to separate the billing services. 

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
