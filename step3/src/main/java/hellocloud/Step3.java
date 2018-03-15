package hellocloud;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;

public class Step3 implements RequestHandler<S3Event, Void> {
	
    public Void handleRequest(S3Event s3event, Context context) {
        try {
            S3EventNotificationRecord record = s3event.getRecords().get(0);
            String bucketName = record.getS3().getBucket().getName();
            String key = record.getS3().getObject().getKey();
            context.getLogger().log("Bucket "+bucketName+" File "+key);
            AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            S3Object object = s3.getObject(
                              new GetObjectRequest(bucketName, key));
            InputStream objectData = object.getObjectContent();
            String json = new BufferedReader(new InputStreamReader(objectData))
            		  .lines().collect(Collectors.joining("\n"));
            //Env variables
            String url=System.getenv("URL");
            String account=System.getenv("ACCOUNT");
            String collectionLink=System.getenv("COLLECTION_LINK");
            
            //Save new document in CosmosDB
            Document document=new Document(json);
            ConnectionPolicy connection = ConnectionPolicy.GetDefault();
            DocumentClient documentClient=new DocumentClient(url+account, account,
        			connection, ConsistencyLevel.Session);
            documentClient.createDocument(collectionLink, document, null, false);
            
            //Close input
            documentClient.close();
            objectData.close();
            context.getLogger().log("Closing...");
       } catch (Exception e) {
           // do something
       }
       return null;
    }
    
}