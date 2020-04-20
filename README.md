# Consumer Driven Contracts using Pact

## Introduction on CDC

> Contract tests assert that inter-application messages conform to a shared understanding that is documented in a contract. 
Without contract testing, the only way to ensure that applications will work correctly together is by using expensive 
and brittle integration tests.

>Do you set your house on fire to test your smoke alarm? No, you test the contract it holds with your ears by using the 
testing button. Pact provides that testing button for your code, allowing you to safely confirm that your applications 
will work together without having to deploy the world first.

- _Taken from Pact.io_

## General information

Application `pact-consumer` is a consumer that would like to use (let's imagine through an HTTP-based web API) 
a specific message from `pact-provider`. This message is the `UserCreated` event, and it is the **contract** between both our applications.
- If the team behind `pact-provider` changes `UserCreated`, the `pact-consumer` application will be impacted, most likely heavily.

By writing Consumer Driver Contract tests, we give the `pact-consumer` application the power to define the contract (what `UserCreated` should look like).
- It is then up to `pact-provider` to verify that they are indeed providing a `UserCreated` event that adheres to this contract.

For writing the CDC tests and verifying the contracts, we use [Pact](https://docs.pact.io/).

## Usage 

Make sure to run `docker-compose up` as it will start-up the [Pact Broker](https://github.com/pact-foundation/pact_broker) (and required PostgreSQL database).

1. `mvn clean install` from inside `pact-consumer`: 
    - Application `pact-consumer` will run its consumer test (`UserCreatedConsumerTest`), out of this, the Pact contract will be generated (`target/pacts/UserConsumer-UserProvider.json`).
2. `mvn pact:publish` from inside `pact-consumer`:
    - Application `pact-consumer` will publish its contract(s) onto the Pact Broker.
3. `mvn clean install` from inside `pact-consumer`:
    - Application `pact-provider` will run its provider test (`UserCreatedProviderTest`), out of this, the verification report will be generated (`target/pact/reports/UserProvider.json`). 
    Furthermore, the results will be published back onto the Pact Broker.
    
So, we have:
1. Created a contract (as the consumer)
2. Shared it with the provider (using the pact broker)
3. Verified that the contract holds (as the provider)
    - And will be able to verify when we as the provider break the contract...



## Resources
- [Pact JVM (GitHub)](https://github.com/DiUS/pact-jvm)
    - [Provider JUnit5](https://github.com/DiUS/pact-jvm/tree/master/provider/pact-jvm-provider-junit5)
        - [Examples](https://github.com/DiUS/pact-jvm/tree/master/provider/pact-jvm-provider-junit5/src/test/java/au/com/dius/pact/provider/junit5)
    - [Consumer JUnit5](https://github.com/DiUS/pact-jvm/tree/master/consumer/pact-jvm-consumer-junit5)
        - [Examples](https://github.com/DiUS/pact-jvm/tree/master/consumer/pact-jvm-consumer-junit5/src/test/java/au/com/dius/pact/consumer/junit5)
    - [Extensive information on Junit Provider](https://github.com/DiUS/pact-jvm/blob/master/provider/pact-jvm-provider-junit/README.md)
    - [Provider Maven](https://github.com/DiUS/pact-jvm/tree/master/provider/pact-jvm-provider-maven)
- [Pact.io - official documentation](https://docs.pact.io/)
    - [Effective Pact Setup Guide](https://docs.pact.io/pact_nirvana)
    - [Versioning in the Pact Broker](https://docs.pact.io/getting_started/versioning_in_the_pact_broker)
    - [Pacticipant Version Numbers](https://docs.pact.io/pact_broker/pacticipant_version_numbers)

## Scenarios

### Provider breaks verified contract

Given:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | VALID         | 

When we change the `UserCreated` code in the `pact-provider` & thus break the contract 
& do not give the provider a new application version & run `mvn clean install` 
from the root. Then:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | INVALID       |

Also, the build will fail (provider test fails!)

### Provider fixes broken contract

Given:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | INVALID       |

When we change the `UserCreated` code in the `pact-provider` & thus again fix the contract 
& do not give the provider a new application version & run `mvn clean install` 
from the root. Then:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | VALID         |

### Provider makes non-breaking change

Due to the change in the code, the provider correctly receives 
a new version (e.g. commit sha). Given:


| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | VALID         | 

When we update the version (`provider-application-version`) & run `mvn clean install` from the root. Then: 

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.1-SNAPSHOT      | VALID         | 
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | VALID         | 

### Provider breaks verified contract

Due to the change in the code, the provider correctly receives 
a new version (e.g.: commit sha). Given:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.1-SNAPSHOT      | VALID         | 
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | VALID         | 

When we change the `UserCreated` code in the `pact-provider` & thus break the contract 
& update the version (`provider-application-version`) & run `mvn clean install` 
from the root. Then: 

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.2-SNAPSHOT      | INVALID       | 
| UserConsumer  | 1.0              | UserProvider   | 1.1-SNAPSHOT      | VALID         | 
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | VALID         | 

Also, the build will fail (provider test fails!)

### Provider fixes broken contract (new release) 

Due to the change in the code, the provider receives a new version (e.g.: commit sha). 
However, the code of 1.2-SNAPSHOT was broken. The contract could also have been fixed by 
using this same version for the provider. Given:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.2-SNAPSHOT      | INVALID       | 
| UserConsumer  | 1.0              | UserProvider   | 1.1-SNAPSHOT      | VALID         | 
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | VALID         | 

When we change the `UserCreated` code in the `pact-provider` & thus fix the contract 
& update the version (`provider-application-version`) & run `mvn clean install` 
from the root. Then: 

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | VALID         | 
| UserConsumer  | 1.0              | UserProvider   | 1.2-SNAPSHOT      | INVALID       | 
| UserConsumer  | 1.0              | UserProvider   | 1.1-SNAPSHOT      | VALID         | 
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | VALID         | 

### Consumer changes contract

Given:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | VALID         | 
| UserConsumer  | 1.0              | UserProvider   | 1.2-SNAPSHOT      | INVALID       | 
| UserConsumer  | 1.0              | UserProvider   | 1.1-SNAPSHOT      | VALID         | 
| UserConsumer  | 1.0              | UserProvider   | 1.0-SNAPSHOT      | VALID         | 

When we change the contract in the `pact-consumer` & run `mvn clean install` from within `pact-consumer`
& publish the contract (`mvn pact:publish`). We did not update the consumer version! 
Thus, we will have a new contract, but for the same consumer version... Then:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   |                   |               | 

and after running the provider test & publishing the results:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | INVALID       |

### And it keeps going...

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | INVALID       |

When the provider fixes the contract & receives a new version. Then:

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.0              | UserProvider   | 1.4-SNAPSHOT      | VALID         |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | INVALID       |

When the consumer changes the contract & receives a new version

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.1              | UserProvider   |                   |               |  
| UserConsumer  | 1.0              | UserProvider   | 1.4-SNAPSHOT      | VALID         |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | INVALID       |

When the provider runs its tests & publishes the results

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.1              | UserProvider   | 1.4-SNAPSHOT      | INVALID       |  
| UserConsumer  | 1.0              | UserProvider   | 1.4-SNAPSHOT      | VALID         |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | INVALID       |

When the consumer changes the contract again & receives a new version (& the provider already verified the contract)

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.2              | UserProvider   | 1.4-SNAPSHOT      | INVALID       |  
| UserConsumer  | 1.1              | UserProvider   | 1.4-SNAPSHOT      | INVALID       |  
| UserConsumer  | 1.0              | UserProvider   | 1.4-SNAPSHOT      | VALID         |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | INVALID       |

When the provider fixes the contract & receives a new version & runs the test & publishes the report

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.2              | UserProvider   | 1.5-SNAPSHOT      | VALID         |  
| UserConsumer  | 1.2              | UserProvider   | 1.4-SNAPSHOT      | INVALID       |  
| UserConsumer  | 1.1              | UserProvider   | 1.4-SNAPSHOT      | INVALID       |  
| UserConsumer  | 1.0              | UserProvider   | 1.4-SNAPSHOT      | VALID         |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | INVALID       |

When the provider hotfixes the contract in 1.4-SNAPSHOT & runs the test & publishes the report

| Consumer      | Consumer Version | Provider       | Provider Version  | Pact Verified |
| ------------- | -------------    | -------------  | -------------     | ------------- |
| UserConsumer  | 1.2              | UserProvider   | 1.4-SNAPSHOT      | VALID         |  
| UserConsumer  | 1.2              | UserProvider   | 1.5-SNAPSHOT      | VALID         |  
| UserConsumer  | 1.1              | UserProvider   | 1.4-SNAPSHOT      | INVALID       |  
| UserConsumer  | 1.0              | UserProvider   | 1.4-SNAPSHOT      | VALID         |
| UserConsumer  | 1.0              | UserProvider   | 1.3-SNAPSHOT      | INVALID       |
