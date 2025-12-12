# Policy Service Usage


## Policy Engines

A policy engine is an external service that implements the Validator-Api interface. One or more policies can be
associated with a policy engine. To validate policies, you must first have a functioning and configured policy engine,
with its `adapterUrl` set to the service's URL.

## Policies

A policy is a set of rules, guidelines, or standards designed to govern the lifecycle of data products.
Policies must be configured to be triggered after one or more event types. 
This events are :

* `DATA_PRODUCT_CREATION`
    * The creation of a new DataProduct (Major version)
* `DATA_PRODUCT_UPDATE`
    * The update of a new DataProduct (Major version)
* `DATA_PRODUCT_VERSION_CREATION`
    * The release of a new DataProductVersion (Minor version)
* `ACTIVITY_STAGE_TRANSITION`
    * A transition from a DevOps activity to the next one
* `TASK_EXECUTION_RESULT`
    * The execution result of a single Activity Task
* `ACTIVITY_EXECUTION_RESULT`
    * The execution result of a whole Activity

Each event has a `currentState` and an `afterState` fields, which contains resource types based of the eventType:
- `DATA_PRODUCT_CREATION`, `DATA_PRODUCT_UPDATED`, `DATA_PRODUCT_VERSION_CREATION`:
  - ```json
    {
      "currentState": {
        "dataProductVersion": {
          //The data product descriptor of the most recent data product version, if present
        }
      }, 
      "afterState": {
          //The data product descriptor of the new data product version
      } 
    }
    ```
    In case of `DATA_PRODUCT_CREATION` and `DATA_PRODUCT_UPDATE`, it is populated only the `info` field of the data 
    product descriptor.
- `ACTIVITY_STAGE_TRANSITION`: 
  - ```json
    {
      "currentState": {
        "lifecycle": { //The field of the descriptor that describes the terminated stage
          "id": "",
          "dataProductId":"",
          "dataProductVersion": "",
          "stage":""       
        },
        "activity": {  //The errors and the results of the terminated stage
          "id": "",
          "dataProductId": "",
          "dataProductVersion": "",
          "stage": "",
          "status": "",
          "results": "",
          "errors": "",
          "createdAt": "",
          "startedAt": "",
          "finishedAt": ""
        },
        "tasks": [  //The errors and the results of each of the stage's tasks                   
          {
            "id": "",   
            "activityId": "", 
            "executorRef": "", 
            "callbackRef": "",
            "template": "",
            "configurations": "",
            "status": "",
            "results": "",
            "errors": "",
            "createdAt": "",
            "startedAt": "",
            "finishedAt": ""
          }
        ]
      }, 
      "afterState": {
          //Analog to the current state structure
      } 
    }
    ```
- `TASK_EXECUTION_RESULT`:
  - ```json
       {
      "currentState": {
        "activity": {  //The errors and the results of the terminated task's activity
          "id": "",
          "dataProductId": "",
          "dataProductVersion": "",
          "stage": "",
          "status": "",
          "results": "",
          "errors": "",
          "createdAt": "",
          "startedAt": "",
          "finishedAt": ""
        },
        "task": {  //The errors and the results of the terminated task
            "id": "",   
            "activityId": "", 
            "executorRef": "", 
            "callbackRef": "",
            "template": "",
            "configurations": "",
            "status": "",
            "results": "",
            "errors": "",
            "createdAt": "",
            "startedAt": "",
            "finishedAt": ""
          }
      }, 
      "afterState": {
          //Analog to the current state structure
      } 
    }
    ```
- `ACTIVITY_EXECUTION_RESULT`:
  - ```json
    {
      "currentState": {
        //Empty
      }, 
      "afterState": {
        "activity": { //The terminated activity with its results
          "id": "",
          "dataProductId": "",
          "dataProductVersion": "",
          "stage": "",
          "status": "",
          "results": "",
          "errors": "",
          "createdAt": "",
          "startedAt": "",
          "finishedAt": ""
        },
        "dataProductVersion": { //The complete data product descriptor
        }
      } 
    }
    ```

It is possible to further filter the events and activate a policy only if the 
event content matches a SpEL expression, which can be defined inside the `filteringExpression` field of a Policy.
If a policy is considered "blocking", if its validation fails, the operation within which it has computed is blocked.

## Policy V2 Adapter

The Policy V2 Adapter is a component that enables the Policy Service to integrate with the Registry V2 and Notification V2 services. It processes Registry V2 events received through the Notification V2 service, validates them against configured policies, and emits approval or rejection events back to the Notification V2 service.

### Overview

The adapter acts as an observer that:
- Subscribes to specific events from the Notification V2 service
- Receives notifications when data products are initialized or versions are published
- Validates the events against configured policies
- Emits approval or rejection events based on policy validation results
- Maintains backward compatibility with existing policies by using the legacy parser

### Events

#### Received Events

The adapter subscribes to and processes the following events from the Notification V2 service:

1. **`DATA_PRODUCT_INITIALIZATION_REQUESTED`**
   - Triggered when a new data product initialization is requested
   - Maps to the `DATA_PRODUCT_CREATION` policy evaluation event
   - Contains the data product information (UUID, FQN, description, domain)

2. **`DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED`**
   - Triggered when a data product version publication is requested
   - Maps to the `DATA_PRODUCT_VERSION_CREATION` policy evaluation event
   - Contains the new data product version descriptor and optionally the previous version

#### Emitted Events

The adapter emits the following events to the Notification V2 service based on policy validation results:

1. **`DATA_PRODUCT_INITIALIZATION_APPROVED`**
   - Emitted when all blocking policies pass validation for data product initialization
   - Contains the data product UUID and FQN

2. **`DATA_PRODUCT_INITIALIZATION_REJECTED`**
   - Emitted when at least one blocking policy fails validation for data product initialization
   - Contains the data product UUID and FQN

3. **`DATA_PRODUCT_VERSION_PUBLICATION_APPROVED`**
   - Emitted when all blocking policies pass validation for data product version publication
   - Contains the data product version UUID, tag, and associated data product information

4. **`DATA_PRODUCT_VERSION_PUBLICATION_REJECTED`**
   - Emitted when at least one blocking policy fails validation for data product version publication
   - Contains the data product version UUID, tag, and associated data product information

### Backward Compatibility

The adapter maintains backward compatibility with existing policies by using the **legacy Data Product Descriptor parser (DPDS version 1.0.0)**. This ensures that:

- Existing OPA policies that were built on top of the Registry V1 event format continue to work without modification
- The event structure matches what policies expect from the old Registry V1 implementation
- Field name transformations are applied (e.g., `versionNumber` → `version`) to match the legacy format

When processing `DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED` events, the adapter:
1. Parses the Registry V2 data product version descriptor using the DPDS parser version 1.0.0
2. Transforms the parsed descriptor to match the Registry V1 event structure
3. Applies field name corrections to ensure compatibility
4. Builds the policy evaluation request in the format expected by existing policies

This approach ensures that policies written for Registry V1 continue to function correctly with Registry V2 events.

### Service Configuration

The adapter can be configured through the following properties in your `application.yml` or `application.properties` file:

#### Adapter Activation

```yaml
odm:
  productPlane:
    policy:
      adapter:
        active: true  # Enable or disable the adapter (default: false)
        observer:
          name: policyAdapter  # Observer name registered with Notification V2 service
          displayName: Policy Adapter  # Display name for the observer
```

- **`odm.productPlane.policy.adapter.active`** (boolean, default: `false`)
  - Enables or disables the Policy V2 Adapter
  - When set to `true`, the adapter will be initialized and will subscribe to events
  - When set to `false`, the adapter is not created and events are not processed

- **`odm.productPlane.policy.adapter.observer.name`** (string, default: `"policyAdapter"`)
  - The unique name used to identify this observer in the Notification V2 service
  - This name is used when subscribing to events and when receiving notifications
  - Must be unique across all observers in the system

- **`odm.productPlane.policy.adapter.observer.displayName`** (string, default: `"policy"`)
  - A human-readable display name for the observer
  - Used in logs and administrative interfaces
  - Does not need to be unique

#### Notification Service Configuration

```yaml
odm:
  productPlane:
    notificationService:
      active: true  # Enable or disable notification service integration
      address: http://localhost:8006  # Base URL of the Notification V2 service
```

- **`odm.productPlane.notificationService.active`** (boolean, default: `false`)
  - Enables or disables integration with the Notification V2 service
  - When set to `false`, the adapter will use a dummy client that logs warnings instead of sending events
  - When set to `true`, the adapter will connect to the Notification V2 service and subscribe to events

- **`odm.productPlane.notificationService.address`** (string, required if `active: true`)
  - The base URL of the Notification V2 service
  - Must include the protocol (http:// or https://) and port if not using standard ports
  - Example: `http://localhost:8006` or `https://notifications.example.com`

#### Server Configuration

```yaml
server:
  baseUrl: http://localhost:8005  # Base URL of this Policy Service
```

- **`server.baseUrl`** (string, required)
  - The base URL of the Policy Service instance
  - Used by the adapter to register its observer endpoint with the Notification V2 service
  - The observer endpoint will be available at: `{baseUrl}/api/v2/up/observer/notifications`
  - Must be accessible by the Notification V2 service for event delivery

#### Example Configuration

Here's a complete example configuration:

```yaml
server:
  port: 8005
  baseUrl: http://localhost:8005

odm:
  productPlane:
    notificationService:
      active: true
      address: http://localhost:8006
    policy:
      adapter:
        active: true
        observer:
          name: policyAdapter
          displayName: Policy Adapter
```

### Observer Endpoint

The adapter exposes a REST endpoint to receive notifications from the Notification V2 service:

- **Endpoint**: `POST /api/v2/up/observer/notifications`
- **Content-Type**: `application/json`
- **Request Body**: `NotificationV2Res` (notification event from Notification V2 service)
- **Response**: `200 OK` (event received and processed asynchronously)

The endpoint processes notifications asynchronously, so it returns immediately after receiving the event. Policy validation and event emission happen in the background.