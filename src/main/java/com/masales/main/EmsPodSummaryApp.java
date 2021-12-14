package com.masales.main;


import static com.mongodb.client.model.Filters.eq;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import org.bson.Document;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.mongodb.MongoClientName;
import io.quarkus.qson.runtime.QuarkusQsonMapper;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

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
            "\n" +
            "    <link rel=\"icon\" type=\"image/png\" href=\"../html/content/pages/icon-apache.png\" />\n" +
            "    <script type=\"text/javascript\" src=\"events.js\"></script>\n" +
            "\n" +
            "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\">\n" +
            "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n" +
            "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\"></script>\n" +
            "    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/r/dt/dt-1.10.22/datatables.min.css\"/>\n" +
            "    <script type=\"text/javascript\" src=\"https://cdn.datatables.net/r/dt/dt-1.10.22/datatables.min.js\"></script>\n" +
            "\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "  <!--Table content -->\n" +
            "  <table id=\"eventsTable\" class=\"display\" cellspacing=\"5\"  >\n" +
            "    <caption>JMeter - Events Summary</caption>\n" +
            "    <thead>\n" +
            "      <tr role=\"row\">\n" +
            "        <th scope=\"col\" role=\"columnheader\">NotificationId</th>\n" +
            "        <th scope=\"col\" role=\"columnheader\">Type</th>\n" +
            "        <th scope=\"col\" role=\"columnheader\">Created</th>\n" +
            "        <th scope=\"col\" role=\"columnheader\">Exist on POD</th>\n" +
            "        <th scope=\"col\" role=\"columnheader\">Created on POD</th>\n" +
            "        <th scope=\"col\" role=\"columnheader\">Delivery on POD</th>\n" +
            "        <th scope=\"col\" role=\"columnheader\">Exist on EMS</th>\n" +
            "        <th scope=\"col\" role=\"columnheader\">Created on EMS</th>\n" +
            "        <th scope=\"col\" role=\"columnheader\">Delivery on EMS</th>\n" +
            "      </tr>\n" +
            "     </thead>\n" +
            "  </table>\n" +
            "  <script>\n" +
            "\n" +
            "    $(document).ready(function(){\n" +
            "      console.log(events.length);\n" +
            "      $('#eventsTable').dataTable({\n" +
            "        pagination: \"bootstrap\",\n" +
            "        filter: true,\n" +
            "        data: events,\n" +
            "        destroy: true,\n" +
            "        pageLength: 50,\n" +
            "        \"columns\":[\n" +
            "          {\"data\":\"id\"},\n" +
            "          {\"data\":\"type\"},\n" +
            "          {\"data\":\"created\",\n" +
            "            render: function (data, type, row, meta){\n" +
            "              return new Date(data);\n" +
            "            }\n" +
            "          },\n" +
            "          {\"data\":\"inpod\"},\n" +
            "          {\"data\":\"createdpod\", \"defaultContent\":\"\",\n" +
            "            render: function (data, type, row, meta){\n" +
            "              return new Date(data);\n" +
            "            }\n" +
            "          },\n" +
            "          {\"data\":\"dlvpod\", \"defaultContent\":\"\",\n" +
            "            render: function (data, type, row, meta){\n" +
            "              dt = new Date(data);\n" +
            "              dt.setHours(dt.getHours()+1);\n" +
            "              return dt;\n" +
            "            }\n" +
            "          },\n" +
            "          {\"data\":\"inems\"},\n" +
            "          {\"data\":\"createdems\", \"defaultContent\":\"\",\n" +
            "            render: function (data, type, row, meta){\n" +
            "              dt = new Date(data);\n" +
            "              dt.setHours(dt.getHours()-1);\n" +
            "              return dt;\n" +
            "            }\n" +
            "          },\n" +
            "          {\"data\":\"dlvems\", \"defaultContent\":\"\",\n" +
            "            render: function (data, type, row, meta){\n" +
            "              dt = new Date(data);\n" +
            "              // dt.setHours(dt.getHours()-1);\n" +
            "              return dt;\n" +
            "            }\n" +
            "          }\n" +
            "        ]\n" +
            "      });\n" +
            "    });\n" +
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
            System.out.println("Don't forget to configure environment variables");
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
                    Timestamp creationTime = rs.getTimestamp("creationTime");
                    Timestamp deliveryTime = rs.getTimestamp("deliveryTime");
                    ntfs.setExistOnPod(true);
                    java.util.Date creationTimeOnPod = new java.util.Date(creationTime.getTime());
                    java.util.Date deliveryTimeOnPod = new java.util.Date(deliveryTime.getTime());
                    ntfs.setCreationTimeOnPod(creationTimeOnPod);
                    ntfs.setDeliveryTimeOnPod(deliveryTimeOnPod);
                    ntfs.setCreationTimeOnPodString(sdf.format(creationTimeOnPod));
                    ntfs.setDeliveryTimeOnPodString(sdf.format(deliveryTimeOnPod));

                }

                //Search on MongoDB
                Document doc = getCollection().find(eq("notificationId", ntf.getNotificationId())).first();
                if (doc != null) {
                    ntfs.setExistOnEms(true);
                    java.util.Date creationTime = doc.getDate("creationTime");
                    java.util.Date deliveryTime = doc.getDate("deliveryTime");

                    ntfs.setCreationTimeOnEms(creationTime);
                    ntfs.setDeliveryTimeOnEms(deliveryTime);

                    ntfs.setCreationTimeOnEmsString(sdf.format(creationTime));
                    ntfs.setDeliveryTimeOnEmsString(sdf.format(deliveryTime));
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


        int totalEvents = notifications.size();
        int completeEvents = 0;
        int inpodEvents = 0;
        int inEMSEvents = 0;
        NotificationSummary lastEvent = notifications.get(0);
        PrintWriter pwSummary = new PrintWriter(new File(eventsFolder,"summary.properties"));
        pwSummary.println("total.events="+totalEvents);

        PrintWriter pw = new PrintWriter(new FileWriter(report));
        pw.println("let events = [");
        String sep = "";
        for (NotificationSummary ntfs : notifications) {
            StringBuilder sb = new StringBuilder(sep);
            sb.append("{\"id\":\"").append(ntfs.getNotificationId()).append("\",");
            sb.append("\"type\":\"").append(ntfs.getType()).append("\",");
            sb.append("\"created\":").append(ntfs.getCreated().getTime()).append(",");
            sb.append("\"createdstring\": \"").append(ntfs.getCreatedString()).append("\",");
            sb.append("\"inpod\":").append(ntfs.getExistOnPod());
            if (ntfs.getExistOnPod()) {
                sb.append(",").append("\"createdpod\":").append(ntfs.getCreationTimeOnPod().getTime()).append(",");
                sb.append("\"createdpodstring\":\"").append(ntfs.getCreationTimeOnPodString()).append("\",");
                sb.append("\"dlvpod\":").append(ntfs.getDeliveryTimeOnPod().getTime()).append(",");
                sb.append("\"dlvpodstring\":\"").append(ntfs.getDeliveryTimeOnPodString()).append("\"");
                inpodEvents++;
            }

            sb.append(",").append("\"inems\":").append(ntfs.getExistOnEms());
            if (ntfs.getExistOnEms()) {
                sb.append(",").append("\"createdems\":").append(ntfs.getCreationTimeOnEms().getTime()).append(",");
                sb.append("\"createdemsstring\":\"").append(ntfs.getCreationTimeOnEmsString()).append("\",");
                sb.append("\"dlvems\":").append(ntfs.getDeliveryTimeOnEms().getTime()).append(",");
                sb.append("\"dlvemsstring\":\"").append(ntfs.getDeliveryTimeOnEmsString()).append("\"");
                inEMSEvents++;
            }

            if (ntfs.getExistOnPod() && ntfs.getExistOnEms())completeEvents++;

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

        //summary report
        pwSummary.println("total.events.complete="+completeEvents);
        pwSummary.println("total.events.in.pod="+inpodEvents);
        pwSummary.println("total.events.not.in.pod="+(totalEvents- inpodEvents));
        pwSummary.println("total.events.in.ems="+inEMSEvents);
        pwSummary.println("total.events.not.in.ems="+(totalEvents- inEMSEvents));

        pwSummary.flush();
        pwSummary.close();

        return 0;
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("ems-inventory").getCollection("persistentEvent", Document.class);
    }

}


