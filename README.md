# Challenge explanation
This project is a solution based on a challenge, where it is necessary to build an application 
that need to follow certain rules:

The system will have two types of users: common users and merchants users.

- Common users can send money (make transfers) to both merchants and other common users.
- Merchants can only receive transfers; they cannot send money to anyone.

For both types of users, we need the Full Name, CPF, Email, and Password. CPF/CNPJ and emails must be unique in the system. Therefore, the system should allow only one registration per CPF or email address.

- Validate if the user has sufficient balance before making a transfer.
- Before completing the transfer, an external authorization service must be consulted. Use this mock: https://util.devi.tools/api/v2/authorize to simulate the service using the GET method.
- The transfer operation must be a transaction (i.e., it should be rolled back in case of any inconsistency), and the money should be returned to the sender’s wallet.
- Upon receiving a payment, the user or merchant must receive a notification (via email or SMS) sent by a third-party service. This service may occasionally be unavailable or unstable. Use this mock: https://util.devi.tools/api/v1/notify to simulate the notification using the POST method.
- This service must be RESTful.

# Solution

### Introduction 

I believe that in this challenge, it is essential to create a consistent flow that follows best practices and conventions while meeting all the 
requirements. Once these fundamental requirements are met, the challenge presents an interesting aspect that can highlight the developer's ability 
to handle adversities, such as dealing with a service that is frequently unavailable.

Being able to design a valid and efficient architecture under these conditions is one of the key aspects of this challenge-provided that the other 
fundamental requirements have also been properly implemented.

### Chosen solution

There are several possible solutions for the more "open-ended" challenge-handling the unstable notification service.

Some implementations that can help mitigate the impact include:

- Event-driven architecture
- Circuit Breaker
- Messaging

Depending on the context and requirements, one solution may be more suitable than another for a given scenario.

For this project, I chose to split the system into two microservices that communicate via messaging. Additionally, I implemented a 
Circuit Breaker to stop consuming notification messages if the notification service is unavailable.

![architecture diagram](/docs/architecture-diagram.png)

### How to Test My Implementation on Your Machine

1. Run the database and Kafka Docker images using Docker Compose (e.g., `docker-compose up -d`).
2. Start the two microservices: **"payment-api"** and **"notification-queue-consumer"**.
3. **Perform some transfers using the `/transfer` endpoint. [POSTMAN COLLECTION](docs/payment-flow-challenge.postman_collection.json)**
    - The database is already populated with two users using Liquibase for demonstration purposes.
    - You can immediately test transfers using the provided Postman collection.
    - If needed, you can also create new users via API.

# Used Technologies and Strategies

Implementation: 

- WebFlux
- Kafka
- Resilience4J (Circuit Breaker)
- R2DBC
- Optimistic Locking
- HATEOAS

Tests:

- Test containers
- Mockito
- JUnit