# Storage Service Requests

Examples of HTTP requests you can make to this application to interact with file storage providers.


## AWS S3

### Get File

```
GET: http://localhost:8080/api/fileStorage/storageProvider/AWS_S3/storageLocation/my-test-bucket/fileName/KickoffFB.jpg
```

note: you can go to the link in your browser to download the file

![alt text](./GetFileFromAWS.JPG "Getting a file from an AWS S3 bucket.")

### Upload File

```
POST: http://localhost:8080/api/fileStorage/storageProvider/AWS_S3/storageLocation/my-test-bucket
```

![alt text](./AddFileToAWS.JPG "Adding a file to an AWS S3 bucket.")

### Delete File

```
DELETE: http://localhost:8080/api/fileStorage/storageProvider/AWS_S3/storageLocation/my-test-bucket/fileName/KickoffFB.jpg
```

![alt text](./DeleteFileFromAWS.JPG "Deleting a file from an AWS S3 bucket.")


## GCP Cloud Storage

### Get File

```
GET: http://localhost:8080/api/fileStorage/storageProvider/GCP/storageLocation/my_test_bucket/fileName/KickoffFB.jpg
```

note: you can go to the link in your browser to download the file

![alt text](./GetFileFromGCP.JPG "Getting a file from a GCP Cloud Storage bucket.")

### Upload File

```
POST: http://localhost:8080/api/fileStorage/storageProvider/GCP/storageLocation/my_test_bucket
```

![alt text](./AddFileToGCP.JPG "Adding a file to a GCP Cloud Storage bucket.")

### Delete File

```
DELETE: http://localhost:8080/api/fileStorage/storageProvider/GCP/storageLocation/my_test_bucket/fileName/KickoffFB.jpg
```

![alt text](./DeleteFileFromGCP.JPG "Deleting a file from a GCP Cloud Storage bucket.")
