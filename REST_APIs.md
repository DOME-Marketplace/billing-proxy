# Billing Proxy

**Version:** 2.0.0  
**Description:** Swagger REST APIs for the billing-proxy software  


## REST API Endpoints

### billing-proxy-controller
| Verb | Path | Task |
|------|------|------|
| POST | `/billing/previewPrice` | calculatePricePreview |
| POST | `/billing/instantBill` | calculateInstantBill |
| POST | `/billing/bill` | calculateBill |

### info-proxy-controller
| Verb | Path | Task |
|------|------|------|
| GET | `/proxy/info` | getInfo |
| GET | `/proxy/health` | getHealth |

