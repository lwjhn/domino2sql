package com.lwjhn.domino2sql;

import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.util.ArcUtils;
import com.lwjhn.util.AutoCloseableBase;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.rjsoft.driver.ExtendedOptions4MongoDb;
import lotus.domino.*;
import lotus.priv.CORBA.iiop.FixIOR;
import lotus.priv.CORBA.iiop.IOR;
import lotus.priv.CORBA.iiop.ORB;
import lotus.priv.CORBA.iiop.Profile;
import org.bson.BsonString;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mongodb:
 * uri: mongodb://192.168.210.134:27218/fjszf
 * chunkSize: 8
 * fileCollection: fs
 * defaultCollection: fjszf_log
 * mongodb:
 * uri: mongodb://192.168.210.186:27017/attachment
 * chunkSize: 8
 * fileCollection: fs
 * defaultCollection:
 * mongodb:
 * uri: mongodb://192.168.210.186:27017/sftoamongo
 * chunkSize: 8
 * fileCollection: fs
 * defaultCollection: sftoamongo
 */
public class Test1 {
    @Test
    public void test3() {
        System.out.println(new ExtendedOptions4MongoDb().sqlFormula("测试文件.txt"));
        System.out.println(new ExtendedOptions4MongoDb().sqlFormula("wenj-txt"));

        String formula = "FileId:= \"\\\"@UUID16\\\"\";\n" +
                "\"INSERT INTO SFTOA.EGOV_ATT (ID, MODULE_ID, DOC_ID, EGOV_FILE_ID, FILE_NAME, FILE_SUFFIX, \\\"TYPE\\\", SORT_TIME, CREATE_TIME) \n" +
                "\tVALUES('\"+ FileId +\"', '\"+@Right(@ReplaceSubstring(@Left(@UpperCase(@Subset(@DbName;-1));\".\");\"\\\\\";\"/\");\"/\")+\"', '\" + @Text(@DocumentUniqueID) + \"', '\"+ FileId +\"', '@FileName', '@FileSuffix', '正常', 0, NOW);\n" +
                "INSERT INTO SFTOA.EGOV_FILE (ID, FILE_PATH, CREATE_TIME) VALUES('\"+ FileId +\"', '\"+ FileId +\"', NOW);\"";

        Pattern PATTERN = Pattern.compile("(@UUID\\d+|@FileName|@FileSuffix)\\b", Pattern.CASE_INSENSITIVE);
        Pattern FILENAME = Pattern.compile("^@FileName$", Pattern.CASE_INSENSITIVE);
        System.out.println(ArcUtils.formula(formula, PATTERN, s ->
                (ArcUtils.PATTERN_UUID.matcher(s).matches()
                        ? ArcUtils.UUID(Integer.parseInt(s.substring(5)))
                        : (FILENAME.matcher(s).matches() ? "文件" : "扩展名")))
        );

        System.out.println(ArcUtils.formula("@UUID24kk"));
        System.out.println(ArcUtils.formula(" @UUID32 "));
    }

    @Test
    public void testFile() {
        String urlSetting = "mongodb://192.168.210.186:27017",
                databaseSetting = "sftoamongo",
                bucketSetting = "fs",
                idSetting = "A92D8BECCE4F9FF44825882F0034DF63";
        //"mongodb+srv://<username>:<password>@<cluster-address>/test?w=majority"
        ConnectionString connString = new ConnectionString(urlSetting);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        for (String name : mongoClient.listDatabaseNames()) {
            System.out.println("db: " + name);
        }
        MongoDatabase database = mongoClient.getDatabase(databaseSetting);
        for (String name : database.listCollectionNames()) {
            System.out.println("collection: " + name);
        }
        MongoCollection<org.bson.Document> collection = database.getCollection("fs.files");
        com.mongodb.client.MongoCursor<org.bson.Document> cursor =
                collection.find(new BasicDBObject("_id", Pattern.compile(idSetting, Pattern.CASE_INSENSITIVE))).iterator();
        while (cursor.hasNext()) {
            org.bson.Document document = cursor.next();
            System.out.println(document.getString("_id"));
            break;
        }

        GridFSBucket bucket = GridFSBuckets.create(database, bucketSetting);
        //GridFSFile gridFSFile = bucket.find(Filters.eq("_id", id)).first();

        //upload(bucket, idSetting, new File("C:\\ftp_xc\\egov_dispatch_nsf\\0b419ee3863ac2fb482582a4002e7ba9\\mss\\关于南平市人民影剧院拟注销事业单位登记0606H的公告.doc"));

        download(bucket, idSetting);
    }

    public void upload(GridFSBucket bucket, String idSetting, File file) {
        InputStream inputStream = null;
        try {
            bucket.uploadFromStream(new BsonString(idSetting), file.getName(), inputStream = new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            AutoCloseableBase.close(inputStream);
        }
    }

    public void download(GridFSBucket bucket, String id) {
        OutputStream outputStream = null;
        GridFSDownloadStream downloadStream = null;
        try {
            downloadStream = bucket.openDownloadStream(new BsonString(id));
            GridFSFile gridFSFile = downloadStream.getGridFSFile();
            System.out.println(gridFSFile.getFilename());
            System.out.println(gridFSFile.getChunkSize());
            System.out.println(gridFSFile.getLength());

            File file = new File("d:/test/mongodb/" + gridFSFile.getFilename());
            outputStream = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int rc;
            while ((rc = downloadStream.read(buff, 0, 1024)) > 0) {
                outputStream.write(buff, 0, rc);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            AutoCloseableBase.close(downloadStream, outputStream);
        }
    }

    @Test
    public void test() throws Exception {
        String username = "Admin";
        String password = "12345678";
        String host = Profile.PROXY_HOST = "192.168.210.153";
        int port = Profile.PROXY_PORT = 9898;
        String origin = host + ":" + port;
        String[] diiopArgs = new String[]{"-ORBClassBootstrapHost", host, "-ORBClassBootstrapPort", String.valueOf(port)};

        Session session = NotesFactory.createSession(origin, diiopArgs, username, password);
        Database db = session.getDatabase("OAAPP/SRV/NANPING", "names.nsf");
        System.out.println(db.isOpen());
        int count = db.getAllDocuments().getCount();
        System.out.println(count);
        Document doc = db.getDocumentByUNID("D8AAFDDDE6066C3B482580F5000CF7A2");
        if (doc != null) {
            for (Object object : doc.getItems()) {
                Item item = (Item) object;
                String name = item.getName();
                String value = item.getText();
                System.out.printf("%s : %s\n", name, value);
            }
        }
    }

    public String fixIOR(String iorString, String host, int port) {
        return FixIOR.stringify(FixIOR.parse(iorString, host, port));
    }

    @Test
    public void testIor() throws Exception {
        String ior = NotesFactory.getIOR("192.168.210.153:9898", "Admin", "Fjsft_123");
        System.out.println(ior);
        String newIor = fixIOR(ior, "192.168.210.153", 9898);
        System.out.println(newIor);

        IOR temp = FixIOR.parse(new ORB(), newIor);
        System.out.println(FixIOR.stringify(temp));
    }
}
