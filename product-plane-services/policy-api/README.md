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

It is possible to furter filter the events and activate a policy only if the 
event content matches a SpeL expression, which can be defined inside the `filteringExpression` field of a Policy.
If a policy is considered "blocking", if its validation fails, the operation within which it has computed is blocked.
Finally, the `rawContent`
rawContent;