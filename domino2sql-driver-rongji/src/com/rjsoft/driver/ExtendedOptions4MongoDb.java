package com.rjsoft.driver;

import com.lwjhn.util.ArcUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class ExtendedOptions4MongoDb extends ExtendedOptions {
    protected String mongo_url;
    protected String mongo_db;
    protected String mongo_bucket = "fs";
    /*
DocId:= ArcXC_UUID_16;
FileId:= _mongodb_file_id_;
FileType:=@If(FileType="mss";"main_doc";"attach");
"INSERT INTO EGOV_ATT (ID, MODULE_ID, DOC_ID, EGOV_FILE_ID, FILE_NAME, FILE_SUFFIX, \"TYPE\", STATUS, SORT_TIME, CREATE_TIME)
	VALUES('"+ FileId +"', '"+@Right(@ReplaceSubstring(@Left(@UpperCase(@Subset(@DbName;-1));".");"\\";"/");"/")+"', '" + DocId + "', '"+ FileId + "', '@FileName',  '@FileSuffix', '"+FileType+"','正常', 0, NOW);
INSERT INTO EGOV_FILE (ID, FILE_PATH, CREATE_TIME) VALUES('"+ FileId +"', '"+ FileId +"', NOW);"
     */
    protected String mongo_file_id_formula = "@SetField(\"_mongodb_file_id_\";\"@UUID16\");_mongodb_file_id_";
    protected String mongo_map_sql_formula = "DocId:= ArcXC_UUID_16;\n" +
            "FileId:= _mongodb_file_id_;\n" +
            "FileType:=@If(@LowerCase(\"@FileType\")=\"mss\";\"main_doc\";\"attach\");\n" +
            "\"INSERT INTO EGOV_ATT (ID, MODULE_ID, DOC_ID, EGOV_FILE_ID, FILE_NAME, FILE_SUFFIX, \\\"TYPE\\\", STATUS, SORT_TIME, CREATE_TIME) \n" +
            "\tVALUES('\"+ FileId +\"', '\"+@Right(@ReplaceSubstring(@Left(@UpperCase(@Subset(@DbName;-1));\".\");\"\\\\\";\"/\");\"/\")+\"', '\" + DocId + \"', '\"+ FileId + \"', '@FileName',  '@FileSuffix', '\"+FileType+\"','正常', 0, NOW);\n" +
            "INSERT INTO EGOV_FILE (ID, FILE_PATH, CREATE_TIME) VALUES('\"+ FileId +\"', '\"+ FileId +\"', NOW);\"";

    protected static final String FILE_NAME = "@FileName".toLowerCase(Locale.ROOT);
    protected static final String FILE_SUFFIX = "@FileSuffix".toLowerCase(Locale.ROOT);
    protected static final String FILE_TYPE = "@FileType".toLowerCase(Locale.ROOT);
    protected static final Pattern PATTERN = Pattern.compile("(@UUID\\d+|" + FILE_NAME + "|" + FILE_SUFFIX + "|" + FILE_TYPE + ")\\b", Pattern.CASE_INSENSITIVE);
    protected static final Pattern FILENAME = Pattern.compile("^" + FILE_NAME + "$", Pattern.CASE_INSENSITIVE);

    public String sqlFormula(String filename) {
        String alias = filename.replaceAll("\\.[0-9a-z]+$", "");
        return ArcUtils.formula(mongo_map_sql_formula, PATTERN, s ->
                (ArcUtils.PATTERN_UUID.matcher(s).matches()
                        ? ArcUtils.UUID(Integer.parseInt(s.substring(5)))
                        : (FILENAME.matcher(s).matches() ? alias : filename.length() == alias.length() ? "" : filename.substring(alias.length() + 1))));
    }

    public String sqlFormula(String filename, String type) {
        return sqlFormula(new HashMap<String, String>() {{
            String alias = filename.replaceAll("\\.[0-9a-z]+$", "");
            String ext = filename.length() == alias.length() ? "" : filename.substring(alias.length() + 1);
            put(FILE_NAME, alias);
            put(FILE_SUFFIX, ext);
            put(FILE_TYPE, type);
        }});
    }

    public String sqlFormula(Map<String, String> mapping) {
        return ArcUtils.formula(mongo_map_sql_formula, PATTERN, key ->
                ArcUtils.PATTERN_UUID.matcher(key).matches()
                        ? ArcUtils.UUID(Integer.parseInt(key.substring(5)))
                        : mapping.get(key.toLowerCase(Locale.ROOT)));
    }

    public String mongodbFileId() {
        return ArcUtils.formula(mongo_file_id_formula);

    }

    public String getMongo_url() {
        return mongo_url;
    }

    public void setMongo_url(String mongo_url) {
        this.mongo_url = mongo_url;
    }

    public String getMongo_db() {
        return mongo_db;
    }

    public void setMongo_db(String mongo_db) {
        this.mongo_db = mongo_db;
    }

    public String getMongo_bucket() {
        return mongo_bucket;
    }

    public void setMongo_bucket(String mongo_bucket) {
        this.mongo_bucket = mongo_bucket;
    }

    public String getMongo_map_sql_formula() {
        return mongo_map_sql_formula;
    }

    public void setMongo_map_sql_formula(String mongo_map_sql_formula) {
        this.mongo_map_sql_formula = mongo_map_sql_formula;
    }

    public String getMongo_file_id_formula() {
        return mongo_file_id_formula;
    }

    public void setMongo_file_id_formula(String mongo_file_id_formula) {
        this.mongo_file_id_formula = mongo_file_id_formula;
    }
}
