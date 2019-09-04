# File Storage Application

This file storage application allows files to be stored with cloud services such as Amazon Web Services's S3 or Google Cloud Platform's storage service. This application is designed to handle the file storage needs of a service/team rather than multiple users.

This application uses Spring's Service Locator Pattern to choose between the AWS S3 and GCP services at runtime based on request arguments.


## Getting Started

These instructions will help you get the project up and running on your local machine for development and testing purposes.


### Prerequisites

Please have Java 8 installed to run this application. Additionally, it is recommended to have maven installed, but a maven wrapper is included (to use the wrapper use `./mvnw` instead of `mvn` for commands)

Additionally, familiarity with AWS and GCP is required. An AWS S3 bucket and a GCP Cloud Storage bucket will need to be set up prior to running this application.

Knowledge of Java and the Spring Framework will help with navigating and understanding the project.


### Setup

Before running the application, please set up the following environment variables:
* AWS_ACCESS_KEY - AWS access key ID, e.g. ABCDEFGHIJKLMNOPQRST
* AWS_SECRET_KEY - AWS secret access key, e.g. 0123456789ABCDEFGHIJ0123456789
* GCP_PROJECT_ID - GCP project id, e.g. sample-project-123456
* GCP_CREDENTIALS_LOCATION - local location of the JSON file, e.g. file:/Users/Chris/Desktop/Sample-Project-123456789123.json

For more information regarding the environment variables, refer to the project's application.properties file.

For setup with the cloud providers, refer to the following links:
* [AWS Access Keys](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html) - Create an access key and store the access key id and secret key
* [GCP Setting Up Authentication](https://cloud.google.com/storage/docs/reference/libraries#setting_up_authentication) - Create a new service account and store the downloaded JSON file.


### Running the application

To run the application, execute the following commands on the command line.

```
# Run the Application
mvn spring-boot:run
```

To access a file, you can go to the following URL.
```
GET: http://localhost:8080/api/fileStorage/storageProvider/GCP/storageLocation/my_test_bucket/fileName/Example.jpg
```
Note: make sure your credentials allow you to access that bucket.

To upload a file, you can make a POST request.
```
POST: http://localhost:8080/api/fileStorage/storageProvider/GCP/storageLocation/sc_test_bucket

Body: form-data
	- key: file, value: Example.jpg
```


## Running the tests

You can use maven to run tests.

```
# If any changes are made, clean the project directory first
mvn clean

# Run tests
mvn test
```


## Deployment

TODO: Add additional notes about how to deploy this on a live system


## Built With

* [Spring](https://spring.io/) - Spring Framework and Spring Boot used to build this web application
* [Maven](https://maven.apache.org/) - Dependency Management


## Authors

* **Chris Luo** - [cl2871](https://github.com/cl2871)


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details


## Acknowledgments

* [ServiceLocatorFactoryBean](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/ServiceLocatorFactoryBean.html) - Reference documentation for the service locator bean
* [Service Locator Pattern in Spring](https://springframework.guru/service-locator-pattern-in-spring/) - Service locator explanation and example from the Spring Framework Guru blog.
* [Using TransferManager for Amazon S3 Operations](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-s3-transfermanager.html) - AWS S3 reference documentation for using the Transfer Manager for file transfer.
* [Using Cloud Storage with Java](https://cloud.google.com/java/getting-started/using-cloud-storage) - GCP Cloud Storage reference documentation for using Java for file transfer.