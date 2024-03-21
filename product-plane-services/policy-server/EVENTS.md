# Events in Policy Service

When interacting with other ODM services, Policy Service expects to react to the following events:

* DATA_PRODUCT_CREATION
* DATA_PRODUCT_UPDATE
* ACTIVITY_STAGE_TRANSITION
* TASK_EXECUTION_RESULT
* ACTIVITY_EXECUTION_RESULT

Such kind of events are encapsulated in the body of an evaluation request through the required
`PolicyEvaluationRequestResource`.

When an evaluation request occurs, the Policy Service select all the registered policies with the
`suite` attribute matching the event type and forward them to the right Policy Engine Adapter for the evaluation.
It then collects the results, aggregate them, and forward a response to the original request.

Each event has a default input object, which will be the subject of the policy evaluations request.
The input object is obtained combining two attributes of the original request body 
(i.e., a `PolicyEvaluationRequestResource` resource), that are `currentState` and `afterState`.

This document lists an example of the input object for every event. 

<div class="info" style="background-color: #E1F5FE;
  border-left: 5px solid #0288D1;
  color: #01579B;
  padding: 10px;
  border-radius: 5px;">
Each object shown in the examples has been trimmed as it merely serves as an example of the input object
</div>

## Registry Events
Events representing operations from the Registry Server.

### DATA_PRODUCT_CREATION

This event represents the creation of a Data Product Version object for a Data Product without any existing version.

In this event only the `afterState` attribute is populated. 

#### Current State
An empty object, that is the state before the creation of the Data Product Version.
```json
{}
```

#### After State
The JSON representation of a Data Product Version DPDS object, 
that is the state after the creation of the Data Product Version
```json
{
  "dataProductDescriptor": "1.0.0",
  "info": {
    "fullyQualifiedName": "urn:org.opendatamesh:dataproducts:tripExecution",
    "domain": "sampleDomain",
    "name": "tripExecution",
    "version": "1.0.0",
    "displayName": "Trip Execution",
    "description": "Gestione viaggi trasporti merce",
    ...
  },
  ...
}
```

#### Input Object
The composed input object that will be forwarded to the right Policy Engine Adapter.
```json
{
  "currentState": {},
  "afterState": {
    "dataProductDescriptor": "1.0.0",
    "info": {
      "fullyQualifiedName": "urn:org.opendatamesh:dataproducts:tripExecution",
      "domain": "sampleDomain",
      "name": "tripExecution",
      "version": "1.0.0",
      "displayName": "Trip Execution",
      "description": "Gestione viaggi trasporti merce",
      ...
    },
    ...
  }
}
```

### DATA_PRODUCT_UPDATE

This event represents the update of a Data Product Version object, 
that is the creation of a Data Product Version for a Data Product with at least one already existing version.

In this event, both `currentState` and `afterState` is populated. 
In this way it will be possible to implement policy such as schema retro-compatibility, 
or any other check that involves the previous and the current version.

#### Current State
The previous version of the Data Product updated, represented as a Data Product Version DPDS JSON object.
```json
{
  "dataProductDescriptor": "1.0.0",
  "info": {
    "fullyQualifiedName": "urn:org.opendatamesh:dataproducts:tripExecution",
    "domain": "sampleDomain",
    "name": "tripExecution",
    "version": "1.0.0",
    "displayName": "Trip Execution",
    "description": "Gestione viaggi trasporti merce",
    ...
  },
  ...
}
```

#### After State
The newly created Data Product Version, represented as a Data Product Version DPDS JSON object.
```json
{
  "dataProductDescriptor": "1.0.0",
  "info": {
    "fullyQualifiedName": "urn:org.opendatamesh:dataproducts:tripExecution",
    "domain": "sampleDomain",
    "name": "tripExecution",
    "version": "2.0.0",
    "displayName": "Trip Execution",
    "description": "Gestione trasporti merce da sorgente a destinazione",
    ...
  },
  ...
}
```

#### Input Object
The composed input object that will be forwarded to the right Policy Engine Adapter.
```json
{
  "currentState": {
    "dataProductDescriptor": "1.0.0",
    "info": {
      "fullyQualifiedName": "urn:org.opendatamesh:dataproducts:tripExecution",
      "domain": "sampleDomain",
      "name": "tripExecution",
      "version": "1.0.0",
      "displayName": "Trip Execution",
      "description": "Gestione viaggi trasporti merce",
      ...
    },
    ...
  },
  "afterState": {
    "dataProductDescriptor": "1.0.0",
    "info": {
      "fullyQualifiedName": "urn:org.opendatamesh:dataproducts:tripExecution",
      "domain": "sampleDomain",
      "name": "tripExecution",
      "version": "2.0.0",
      "displayName": "Trip Execution",
      "description": "Gestione trasporti merce da sorgente a destinazione",
      ...
    },
    ...
  }
}
```

## DevOps Events
Events representing operations from the DevOps Server.

### ACTIVITY_STAGE_TRANSITION

This event represents the execution request of an Activity for a specific Data Product Version.

In this event, the `currentState` could be empty, 
in the scenario of a first Activity for a specific Data Product Version,
or it could be populated with the current lifecycle stage of the Data Product Version.
The `afterState` is always populated with information about the Activity to execute.

In this way it will be possible to evaluate conditions such the possibility of execute the given Activity
given the current lifecycle of the Data Product Version subject of the Activity.

#### Current State
An empty object, if it's the first Activity for the specific Data Product Version.
```json
{}
```
A LifecycleResource JSON object in case of a Data Product Version with at least one previous Activity executed.
```json
{
  "dataProductId": "ca8802b6-bc59-3ad8-8436-fdfe79c9c512",
  "dataProductVersion": "1.0.0",
  "stage": "dev",
  "results": {
    "task1": {
      "customResult": "value1",
      "result2": {
        "subresult2_1": "value2_1",
        "subresult2_2": "value2_2"
      }
    },
    "task2": {
      ...
    },
    ...
  },
  "startedAt": "2024-03-21T12:04:11.000+00:00",
  "finishedAt": "2024-03-21T12:17:11.000+00:00"
}
```

#### After State
A composite object putting together the JSON object of the ActivityResource to execute 
and the JSON representation of the list of TaskResource included in the Activity.
```json
{
  "activity": {
    "id": 2,
    "dataProductId": "ca8802b6-bc59-3ad8-8436-fdfe79c9c512",
    "dataProductVersion": "1.0.0",
    "stage": "prod",
    "status": "PLANNED",
    "results": null,
    "errors": null,
    "createdAt": "2024-03-21T12:04:11.000+00:00",
    "startedAt": null,
    "finishedAt": null
  },
  "tasks": [
    {
      "id": 6,
      "activityId": "2",
      "executorRef": "azure-devops",
      "callbackRef": "http://localhost:8002/api/v1/pp/devops/tasks/6/status?action=STOP",
      "template": "{\"organization\":\"customOrg\",\"project\":\"customProject\",\"pipelineId\":3,\"branch\":\"main\"}",
      "configurations": "{\"stagesToSkip\":[\"Test\"],\"params\":{\"paramOne\":\"value1.1\",\"paramTwo\":\"${dev.results.task1.customResult}\"}}",
      "status": "PLANNED",
      "results": null,
      "errors": null,
      "createdAt": "2024-03-21T12:04:11.000+00:00",
      "startedAt": null,
      "finishedAt": null
    },
    ...
  ]
}
```

#### Input Object
The composed input object that will be forwarded to the right Policy Engine Adapter.

In the scenario of a first Activity for a specific Data Product Version:
```json
{
  "currentState": {},
  "afterState": {
    "activity": {
      "id": 2,
      "dataProductId": "ca8802b6-bc59-3ad8-8436-fdfe79c9c512",
      "dataProductVersion": "1.0.0",
      "stage": "prod",
      "status": "PLANNED",
      "results": null,
      "errors": null,
      "createdAt": "2024-03-21T12:04:11.000+00:00",
      "startedAt": null,
      "finishedAt": null
    },
    "tasks": [
      {
        "id": 6,
        "activityId": "2",
        "executorRef": "azure-devops",
        "callbackRef": "http://localhost:8002/api/v1/pp/devops/tasks/6/status?action=STOP",
        "template": "{\"organization\":\"customOrg\",\"project\":\"customProject\",\"pipelineId\":3,\"branch\":\"main\"}",
        "configurations": "{\"stagesToSkip\":[\"Test\"],\"params\":{\"paramOne\":\"value1.1\",\"paramTwo\":\"${dev.results.task1.customResult}\"}}",
        "status": "PLANNED",
        "results": null,
        "errors": null,
        "createdAt": "2024-03-21T12:04:11.000+00:00",
        "startedAt": null,
        "finishedAt": null
      },
      ...
    ]
  }
}
```

In the scenario of a Data Product Version with at least one previous Activity executed:
```json
{
  "currentState": {
    "dataProductId": "ca8802b6-bc59-3ad8-8436-fdfe79c9c512",
    "dataProductVersion": "1.0.0",
    "stage": "dev",
    "results": {
      "task1": {
        "customResult": "value1",
        "result2": {
          "subresult2_1": "value2_1",
          "subresult2_2": "value2_2"
        }
      },
      "task2": {
        ...
      },
      ...
    },
    "startedAt": "2024-03-21T12:04:11.000+00:00",
    "finishedAt": "2024-03-21T12:17:11.000+00:00"
  },
  "afterState": {
    "activity": {
      "id": 2,
      "dataProductId": "ca8802b6-bc59-3ad8-8436-fdfe79c9c512",
      "dataProductVersion": "1.0.0",
      "stage": "prod",
      "status": "PLANNED",
      "results": null,
      "errors": null,
      "createdAt": "2024-03-21T12:04:11.000+00:00",
      "startedAt": null,
      "finishedAt": null
    },
    "tasks": [
      {
        "id": 6,
        "activityId": "2",
        "executorRef": "azure-devops",
        "callbackRef": "http://localhost:8002/api/v1/pp/devops/tasks/6/status?action=STOP",
        "template": "{\"organization\":\"customOrg\",\"project\":\"customProject\",\"pipelineId\":3,\"branch\":\"main\"}",
        "configurations": "{\"stagesToSkip\":[\"Test\"],\"params\":{\"paramOne\":\"value1.1\",\"paramTwo\":\"${dev.results.task1.customResult}\"}}",
        "status": "PLANNED",
        "results": null,
        "errors": null,
        "createdAt": "2024-03-21T12:04:11.000+00:00",
        "startedAt": null,
        "finishedAt": null
      },
      ...
    ]
  }
}
```

### TASK_EXECUTION_RESULT

This event represents the reception of a callback from the execution of a single Task of an Activity for a specific Data Product Version.

In this event, only the `currentState` attribute will be populated with the representation of the completed Task and its results.

#### Current State
A TaskResource JSON object updated with the results from the TaskResultResource representing the body received from the callback.

Considering the following TaskResultResource:
```json
{
  "status": "PROCESSED",
  "results": {
    "customResult": "custom",
    "prova": "2"
  }
}
```
the currentState object will be:
```json
{
  "id": 2,
  "activityId": "1",
  "executorRef": "azure-devops",
  "callbackRef": "http://localhost:8002/api/v1/pp/devops/tasks/2/status?action=STOP",
  "template": "{\"organization\":\"customOrg\",\"project\":\"customProject\",\"pipelineId\":3,\"branch\":\"main\"}",
  "configurations": "{\"params\":{\"paramOne\":\"value1\",\"paramTwo\":\"value2\"}}",
  "status": "PROCESSED",
  "results": {
    "customResult": "customValue",
    "customResultTwo": "customValueTwo"
  },
  "errors": null,
  "createdAt": "2024-03-21T12:04:00.000+00:00",
  "startedAt": "2024-03-21T12:08:55.000+00:00",
  "finishedAt": "2024-03-21T12:08:55.000+00:00"
}
```

<div class="info" style="background-color: #E1F5FE;
  border-left: 5px solid #0288D1;
  color: #01579B;
  padding: 10px;
  border-radius: 5px;">
A specific example of policy evaluation could be the evaluation of a set of constraints on a terraform plan.
In this scenario, the TaskResultResource will be the JSON representation of the terraform plan.
</div>

#### After State
An empty object, given that there isn't any future state for the reception of a Task Result.
```json
{}
```

#### Input Object
The composed input object that will be forwarded to the right Policy Engine Adapter.
```json
{
  "currentState": {
    "id": 2,
    "activityId": "1",
    "executorRef": "azure-devops",
    "callbackRef": "http://localhost:8002/api/v1/pp/devops/tasks/2/status?action=STOP",
    "template": "{\"organization\":\"customOrg\",\"project\":\"customProject\",\"pipelineId\":3,\"branch\":\"main\"}",
    "configurations": "{\"params\":{\"paramOne\":\"value1\",\"paramTwo\":\"value2\"}}",
    "status": "PROCESSED",
    "results": {
      "customResult": "customValue",
      "customResultTwo": "customValueTwo"
    },
    "errors": null,
    "createdAt": "2024-03-21T12:04:00.000+00:00",
    "startedAt": "2024-03-21T12:08:55.000+00:00",
    "finishedAt": "2024-03-21T12:08:55.000+00:00"
  },
  "afterState": {}
}
```

### ACTIVITY_EXECUTION_RESULT

This event represents the execution of the last Task of an Activity for a specific Data Product Version.

This event has only the `currentState` attribute populated.

Once the activity is completed, it's possible to check conditions such the coherence of the deployed services with the initial contract.

#### Current State
The JSON representation of a Data Product Version DPDS object
```json
{
  "dataProductDescriptor": "1.0.0",
  "info": {
    "fullyQualifiedName": "urn:org.opendatamesh:dataproducts:tripExecution",
    "domain": "sampleDomain",
    "name": "tripExecution",
    "version": "1.0.0",
    "displayName": "Trip Execution",
    "description": "Gestione viaggi trasporti merce",
    ...
  },
  ...
}
```

#### After State
An empty object, given that there isn't any future state for the end of the execution of an Activity.
```json
{}
```

#### Input Object
The composed input object that will be forwarded to the right Policy Engine Adapter.
```json
{
  "currentState": {
    "dataProductDescriptor": "1.0.0",
    "info": {
      "fullyQualifiedName": "urn:org.opendatamesh:dataproducts:tripExecution",
      "domain": "sampleDomain",
      "name": "tripExecution",
      "version": "1.0.0",
      "displayName": "Trip Execution",
      "description": "Gestione viaggi trasporti merce",
      ...
    },
    ...
  },
  "afterState": {}
}
```