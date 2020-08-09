package org.msprasad.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Azure Functions with HTTP Trigger.
 */
public class ImageUploadFunction {
    /**
     * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpTrigger-Java
     * 2. curl {your host}/api/HttpTrigger-Java?name=HTTP%20Query
     */
    public static final String storageConnectionString = "connection string";

    @FunctionName("upload-image")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, route = "upload-image", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<ImageFile>> request,
            final ExecutionContext context) throws URISyntaxException, StorageException {
        context.getLogger().info("Java HTTP trigger processed a request.");

        CloudStorageAccount storageAccount = null;
        CloudBlobContainer container = null;
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            container = blobClient.getContainerReference("assets");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }

        if (!request.getBody().isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            // Generate document
            final ImageFile imageFile = request.getBody().get();
            String base64 = imageFile.getBase64();
            byte[] imageByte;
            UUID uuid = UUID.randomUUID();
            String filename = uuid.toString();
            CloudBlockBlob blob = container.getBlockBlobReference(filename + ".png");
            try {
                int index = base64.indexOf(",")+1; // Get the index of base64 string in data-uploaded string
                imageByte = Base64.getDecoder().decode(base64.substring(index));
                blob.getProperties().setContentType("Jpeg");
                blob.uploadFromByteArray(imageByte , 0, imageByte.length);
                System.out.println("upload successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
            imageFile.setId(filename);
            imageFile.setUrl(blob.getUri().toString() + "?sv=2019-10-10&ss=bqtf&srt=sco&sp=rwdlacuptfx&se=2020-07-16T02:03:08Z&sig=UwMHN1zHzMMXmM%2BXL4lInNpQd94MCyrSk6sGgFdQSLA%3D&_=1594836191658");
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(imageFile)
                    .build();
        }
    }
}
