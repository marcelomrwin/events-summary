package com.masales.main;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.mongodb.MongoClientName;
import io.quarkus.qson.runtime.QuarkusQsonMapper;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.bson.Document;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

@QuarkusMain
public class EmsPodSummaryApp implements QuarkusApplication {

    static final String HTML = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "    <meta name=\"description\" content=\"\">\n" +
            "    <meta name=\"author\" content=\"\">\n" +
            "\n" +
            "    <title>Events Summary</title>\n" +
            "    <link href=\"../html/sbadmin2-1.0.7/bower_components/bootstrap/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
            "    <link href=\"../html/sbadmin2-1.0.7/bower_components/metisMenu/dist/metisMenu.min.css\" rel=\"stylesheet\">\n" +
            "    <link href=\"../html/sbadmin2-1.0.7/dist/css/sb-admin-2.css\" rel=\"stylesheet\">\n" +
            "    <link href=\"../html/content/css/theme.blue.css\" rel=\"stylesheet\">\n" +
            "    <link rel=\"icon\" type=\"image/png\" href=\"../html/content/pages/icon-apache.png\" />\n" +
            "    <link href=\"../html/sbadmin2-1.0.7/bower_components/font-awesome/css/font-awesome.min.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "\n" +
            "    <script type=\"text/javascript\" src=\"events.js\"></script>\n" +
            "\n" +
            "    <style>\n" +
            "      th {\n" +
            "        background-color: #9fbfdf;\n" +
            "        background-repeat: no-repeat;\n" +
            "        background-position: center right;\n" +
            "        padding: 4px 18px 4px 4px;\n" +
            "        white-space: normal;\n" +
            "        cursor: pointer;\n" +
            "      }\n" +
            "    </style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "  <div id=\"wrapper\">\n" +
            "    <div id=\"page-wrapper\" style=\"margin-left: 0px;\">\n" +
            "        <div class=\"row\">\n" +
            "          <!--Table content -->\n" +
            "          <table id=\"eventsTable\" class=\"table table-bordered table-condensed \" >\n" +
            "            <caption>JMeter - Events Summary</caption>\n" +
            "            <thead>\n" +
            "                  <tr role=\"row\">\n" +
            "                      <th scope=\"col\" role=\"columnheader\">Num</th>\n" +
            "                      <th scope=\"col\" role=\"columnheader\">NotificationId</th>\n" +
            "                      <th scope=\"col\" role=\"columnheader\">Type</th>\n" +
            "                      <th scope=\"col\" role=\"columnheader\">Created</th>\n" +
            "                      <th scope=\"col\" role=\"columnheader\">Exist on POD</th>\n" +
            "                      <th scope=\"col\" role=\"columnheader\">Created on POD</th>\n" +
            "                      <th scope=\"col\" role=\"columnheader\">Delivery on POD</th>\n" +
            "                      <th scope=\"col\" role=\"columnheader\">Exist on EMS</th>\n" +
            "                      <th scope=\"col\" role=\"columnheader\">Created on EMS</th>\n" +
            "                      <th scope=\"col\" role=\"columnheader\">Delivery on EMS</th>\n" +
            "                  </tr>\n" +
            "             </thead>\n" +
            "             <tbody>\n" +
            "             </tbody>\n" +
            "          </table>\n" +
            "        </div>\n" +
            "      </div>\n" +
            "\n" +
            "  </div>\n" +
            "\n" +
            "  <script src=\"../html/sbadmin2-1.0.7/bower_components/jquery/dist/jquery.min.js\"></script>\n" +
            "  <script src=\"../html/sbadmin2-1.0.7/bower_components/bootstrap/dist/js/bootstrap.min.js\"></script>\n" +
            "  <script type=\"text/javascript\" src=\"../html/content/js/jquery.tablesorter.min.js\"></script>\n" +
            "\n" +
            "  <script>\n" +
            "\n" +
            "    window.onload = function(){\n" +
            "\n" +
            "      console.log(events.length);\n" +
            "\n" +
            "      for (var i=0;i<events.length;i++){\n" +
            "        var ntf = events[i];\n" +
            "        var row = '<tr role=\"row\"><td>'+(i+1)+'</td><td>'+ntf.id+'</td><td>'+ntf.type+'</td><td>'+ new Date(ntf.created).toUTCString()+'</td><td>'+ntf.inpod+'</td>';\n" +
            "\n" +
            "        if (ntf.createdpod){\n" +
            "          row+='<td>'+ new Date(ntf.createdpod).toUTCString() + '</td><td>' + new Date(ntf.dlvpod).toUTCString()+'</td>';\n" +
            "        }else{\n" +
            "          row+='<td></td><td></td>';\n" +
            "        }\n" +
            "        row+='<td>'+ntf.inems+'</td>';\n" +
            "        if (ntf.inems){\n" +
            "          row+='<td>' + new Date(ntf.createdems).toUTCString()+'</td><td>'+ new Date(ntf.dlvems).toUTCString()+'</td>'\n" +
            "        }else{\n" +
            "          row+='<td></td><td></td>';\n" +
            "        }\n" +
            "        row+='</tr>';\n" +
            "        $(\"#eventsTable > tbody\").append(row);\n" +
            "      }\n" +
            "    }\n" +
            "  </script>\n" +
            "  </body>\n" +
            "  </html>\n";
    @Inject
    QuarkusQsonMapper mapper;
    @Inject
    @DataSource("pod")
    AgroalDataSource podDataSource;
    @Inject
    @MongoClientName("ems")
    MongoClient mongoClient;
    private List<NotificationSummary> notifications = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    @java.lang.Override
    public int run(java.lang.String... args) throws Exception {

        if (args.length < 1 && System.getenv("REPORT_PATH") == null) {
            System.out.println("Usage: events-summary  \"Test Result Folder\"");
            System.out.println("Dont forget to configure environment variables");
            System.out.println("Please configure the variable using\nexport REPORT_PATH=<value>");
            System.out.println("Please configure the variable using\nexport POD_DB_IP=<value>");
            System.out.println("Please configure the variable using\nexport POD_DB_PORT=<value>");
            System.out.println("Please configure the variable using\nexport EMS_DB_IP=<value>");
            System.out.println("Please configure the variable using\nexport EMS_DB_PORT=<value>");
            return 1;
        }

        String podDbIP = System.getenv("POD_DB_IP");
        if (podDbIP == null || "".equals(podDbIP)) {
            System.err.println("Environment variable POD_DB_IP not found. Please configure the variable using\nexport POD_DB_IP=<value>");
            return 1;
        }
        Integer podDbPort = Integer.valueOf(System.getenv("POD_DB_PORT"));
        if (podDbPort == null) {
            System.err.println("Environment variable POD_DB_PORT not found. Please configure the variable using\nexport POD_DB_PORT=<value>");
            return 1;
        }

        String emdDbIp = System.getenv("EMS_DB_IP");
        if (emdDbIp == null || "".equals(emdDbIp)) {
            System.err.println("Environment variable EMS_DB_IP not found. Please configure the variable using\nexport EMS_DB_IP=<value>");
            return 1;
        }
        Integer emsDbPort = Integer.valueOf(System.getenv("EMS_DB_PORT"));
        if (emsDbPort == null) {
            System.err.println("Environment variable EMS_DB_PORT not found. Please configure the variable using\nexport EMS_DB_PORT=<value>");
            return 1;
        }

        String podDbUser = System.getenv("POD_DB_USER");
        if (podDbUser == null || "".equals(podDbUser)) {
            System.err.println("Environment variable POD_DB_USER not found. Please configure the variable using\nexport POD_DB_USER=<value>");
            return 1;
        }

        String podDbPass = System.getenv("POD_DB_PASS");
        if (podDbPass == null || "".equals(podDbPass)) {
            System.err.println("Environment variable POD_DB_PASS not found. Please configure the variable using\nexport POD_DB_PASS=<value>");
            return 1;
        }

        String lastTestFolder = null;

        if (args != null && args.length > 0) lastTestFolder = args[0];
        else
            lastTestFolder = System.getenv("REPORT_PATH");

        File testFolder = new File(lastTestFolder);

        if (!testFolder.exists()) {
            System.err.println("Test folder does not exist!");
            return 1;
        }

        File jmeterLog = new File(testFolder, "jmeter.log");

        if (!jmeterLog.exists()) {
            System.err.println("JMeter Log file does not exist on folder " + testFolder.getAbsolutePath());
            return 1;
        }

        System.out.println(sdf.format(new java.util.Date()) + " - Opening connection with postgres");
        Connection connection = podDataSource.getConnection();

        if (!connection.isValid(10000)) {
            System.err.println("Connection with Postgres fails");
            return 1;
        }

        System.out.println(sdf.format(new java.util.Date()) + " - Starting reading file " + jmeterLog.getAbsolutePath());

        Scanner sc = new Scanner(jmeterLog);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.contains("BeanShellTestElement")) {
                String json = line.substring(line.indexOf("{"), line.length()).trim();
                Notification ntf = mapper.parserFor(Notification.class).read(json);
                NotificationSummary ntfs = new NotificationSummary(ntf);

                //Search on Postgres

                PreparedStatement preparedStatement = connection.prepareStatement("select * from persistent_events where \"notificationId\" = ?");
                preparedStatement.setString(1, ntf.getNotificationId());
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    Date creationTime = rs.getDate("creationTime");
                    Date deliveryTime = rs.getDate("deliveryTime");

                    java.util.Date creationTimeOnPod = new java.util.Date(creationTime.getTime());
                    java.util.Date deliveryTimeOnPod = new java.util.Date(deliveryTime.getTime());

                    LocalDateTime ldtCreation = LocalDateTime.ofInstant(creationTimeOnPod.toInstant(), ZoneOffset.UTC);
                    LocalDateTime ldtDelivery = LocalDateTime.ofInstant(deliveryTimeOnPod.toInstant(), ZoneOffset.UTC);

                    ntfs.setExistOnPod(true);

                    ntfs.setCreationTimeOnPod(java.util.Date.from(ldtCreation.toInstant(ZoneOffset.UTC)));
                    ntfs.setDeliveryTimeOnPod(java.util.Date.from(ldtDelivery.toInstant(ZoneOffset.UTC)));
                }

                //Search on MongoDB
                Document doc = getCollection().find(eq("notificationId", ntf.getNotificationId())).first();
                if (doc != null) {
                    ntfs.setExistOnEms(true);
                    java.util.Date creationTime = doc.getDate("creationTime");
                    java.util.Date deliveryTime = doc.getDate("deliveryTime");

                    LocalDateTime ldtCreation = LocalDateTime.ofInstant(creationTime.toInstant(), ZoneOffset.UTC);
                    LocalDateTime ldtDelivery = LocalDateTime.ofInstant(deliveryTime.toInstant(), ZoneOffset.UTC);

                    ntfs.setCreationTimeOnEms(java.util.Date.from(ldtCreation.toInstant(ZoneOffset.UTC)));
                    ntfs.setDeliveryTimeOnEms(java.util.Date.from(ldtDelivery.toInstant(ZoneOffset.UTC)));
                }
                notifications.add(ntfs);
            }
        }
        System.out.println(sdf.format(new java.util.Date()) + " - File read complete");

        System.out.println(sdf.format(new java.util.Date()) + " - Starting generating the JS report");

        File eventsFolder = new File(testFolder, "/events");
        if (!eventsFolder.exists()) eventsFolder.mkdir();

        File report = new File(eventsFolder, "events.js");
        if (report.exists()) report.delete();

        PrintWriter pw = new PrintWriter(new FileWriter(report));
        pw.println("let events = [");
        String sep = "";
        for (NotificationSummary ntfs : notifications) {
            StringBuilder sb = new StringBuilder(sep);
            sb.append("{\"id\":\"").append(ntfs.getNotificationId()).append("\",");
            sb.append("\"type\":\"").append(ntfs.getType()).append("\",");
            sb.append("\"created\":").append(ntfs.getCreated().getTime()).append(",");
            sb.append("\"inpod\":").append(ntfs.getExistOnPod());
            if (ntfs.getExistOnPod()) {
                sb.append(",").append("\"createdpod\":").append(ntfs.getCreationTimeOnPod().getTime()).append(",");
                sb.append("\"dlvpod\":").append(ntfs.getDeliveryTimeOnPod().getTime());
            }

            sb.append(",").append("\"inems\":").append(ntfs.getExistOnEms());
            if (ntfs.getExistOnEms()) {
                sb.append(",").append("\"createdems\":").append(ntfs.getCreationTimeOnEms().getTime()).append(",");
                sb.append("\"dlvems\":").append(ntfs.getDeliveryTimeOnEms().getTime());
            }
            sb.append("}");
            sep = ",";
            pw.println(sb.toString());
            pw.flush();

        }
        pw.println("]");
        pw.flush();
        pw.close();

        System.out.println(sdf.format(new java.util.Date()) + " - JS generation complete " + report.getAbsolutePath());

        File indexHtml = new File(eventsFolder, "index.html");
        if (indexHtml.exists()) indexHtml.delete();

        try (PrintWriter htmlWriter = new PrintWriter(new FileWriter(indexHtml))) {
            htmlWriter.print(HTML);
            htmlWriter.flush();
        }

        return 0;
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("ems-inventory").getCollection("persistentEvent", Document.class);
    }

}


