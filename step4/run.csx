#r "Microsoft.Azure.Documents.Client"
#r "SendGrid"

using SendGrid.Helpers.Mail;
using System;
using System.Text;
using Microsoft.Azure.Documents; 
using Microsoft.Azure.Documents.Client;
using System.Collections.Generic;

public static void Run(TimerInfo myTimer, IEnumerable<Document> documents,DocumentClient client,TraceWriter log, out Mail message)
{
    log.Info("Documenti presenti " + documents.Count());
    if (documents.Count()>0) {
        StringBuilder builder = new StringBuilder("");
        foreach (Document document in documents) {
            string compareUrl=document.GetPropertyValue<string>("compareUrl");
            string pusherName=document.GetPropertyValue<string>("pusherName");
            string pusherEmail=document.GetPropertyValue<string>("pusherEmail"); 
            builder.Append("Push by ").Append(pusherName).Append(" - ").Append(pusherEmail).Append("<br/>");
            builder.Append("Compare url : ").Append(compareUrl).Append("<br/><br/>");
            client.DeleteDocumentAsync(document.SelfLink);
        }
        message = new Mail
        {             
        };

        Content content = new Content
        {
            Type = "text/html",
            Value = builder.ToString()
        };
        message.AddContent(content);
    } else {
        throw new Exception();
    }
}