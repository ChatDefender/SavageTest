package Handlers.MongoDBHandler;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBHandler {

    // This is our connection string for mongo db. Note, the password should be placed in [password]
    private static final String url = "mongodb+srv://user_savage:password_T2yp7P0o@savage.zzqm7sw.mongodb.net/?retryWrites=true&w=majority";
    private static final Document document = new Document();

    // Here we instantiate our ServerAPI to connect to the MongoClient
    private static final ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();

    // Here we build the MongoDB Settings and connect to MongoDBAtlas
    private static final MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(url))
            .serverApi(serverApi)
            .build();

    private static String documentIdForRoles = "64e169d4366533366ca82935";
    private static String documentIdForCommands = "64e169d4366533366ca82935";

    // Used when the bot is started up. Usually it should never do this, but it's good practice to verify the collections exist when being used throughout the program
    public static void verifyCollections() {

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // Gather the collection names
                MongoIterable<String> collections = database.listCollectionNames();

                // Verify the two collections exist, if not, we need to create them and add the document
                boolean collectionRolesExist = collections.into(new ArrayList<>()).contains("Roles");
                boolean collectionCommandsExist = collections.into(new ArrayList<>()).contains("Commands");

                if (!collectionCommandsExist) {

                    database.createCollection("Commands");

                    InsertOneResult ior = database.getCollection("Commands").insertOne(document);

                    documentIdForCommands = String.valueOf(ior.getInsertedId());

                }

                if (!collectionRolesExist) {

                    database.createCollection("Roles");

                    InsertOneResult ior = database.getCollection("Roles").insertOne(document);

                    documentIdForRoles = String.valueOf(ior);

                }

                // Close the connection once we are done.
                mongoClient.close();

            } catch (MongoException e) {
                e.printStackTrace();
            }

        }
    }

    public static void insertIntoActiveDirectory(String collectionName, String staffType, String newValue) {

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // get the specified collection name
                MongoCollection<Document> collection = database.getCollection(collectionName);

                // push the new value to the object array
                Document update = new Document("$push", new Document(staffType +".arr", newValue));

                // update the document
                collection.updateOne(eq("_id", new ObjectId(collectionName.equals("Roles") ? documentIdForRoles : documentIdForCommands)), update);

                // Close the connection once we are done.
                mongoClient.close();

            } catch (MongoException e) {
                e.printStackTrace();
            }

        }
    }

    public static void removeFromActiveDirectory(String collectionName, String staffType, String newValue) {

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // get the specified collection name
                MongoCollection<Document> collection = database.getCollection(collectionName);

                // push the new value to the object array
                Document update = new Document("$pull", new Document(staffType + ".arr", newValue));

                // update the document
                collection.updateOne(eq("_id", new ObjectId(collectionName.equals("Roles") ? documentIdForRoles : documentIdForCommands)), update);

                // Close the connection once we are done.
                mongoClient.close();

            } catch (MongoException e) {
                e.printStackTrace();
            }

        }

    }

    public static List<String> getArrValues(String collectionName, String staffType) {

        List<String> valuesReturned = new ArrayList<>();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // get the specified collection name
                MongoCollection<Document> collection = database.getCollection(collectionName);

                // get the values from the document
                Document fields = collection.find(eq("_id", new ObjectId(collectionName.equals("Roles") ? documentIdForRoles : documentIdForCommands))).first();

                if (fields != null && fields.containsKey(staffType)) {

                    Document value = (Document) fields.get(staffType);

                    if (value.containsKey("arr")) {

                        valuesReturned = value.getList("arr", String.class);

                    }

                }

                // Close the connection once we are done.
                mongoClient.close();

                return valuesReturned;

            } catch (MongoException e) {
                e.printStackTrace();
            }

        }

        return new ArrayList<>();
    }

    // if it exists in another group, we want to return the group it is in
    public static String doesExistInOtherGroup(String collectionName, String idOrCommand) {

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // get the specified collection name
                MongoCollection<Document> collection = database.getCollection(collectionName);

                // get the values from the document
                Document fields = collection.find(eq("_id", new ObjectId(collectionName.equals("Roles") ? documentIdForRoles : documentIdForCommands))).first();

                // Verify the fields exist
                if (fields != null ) {

                    // iterate through the keysets
                    for (String s : fields.keySet()) {

                        // if the field is _id, skip it
                        if (s.equals("_id"))
                            continue;

                        // in order to get into the field we really want, we need to parse the fields as another document, but keep in mind
                        // that the result is really an Object
                        Document value = (Document) fields.get(s);

                        // This is the key we want to play around with
                        if (value.containsKey("arr")) {

                            // if the value has the specified item, return the initial field so that we can inform the user.
                            if (value.getList("arr", String.class).contains(idOrCommand)) {

                                return s;

                            }

                        }

                    }

                }

                // Close the connection once we are done.
                mongoClient.close();

            } catch (MongoException e) {
                e.printStackTrace();
            }

            return null;

        }

    }

    public static List<String> getGroups() {

        // Note: Both collections should have the same fields, so returning one or the other will not make a difference

        // Initialize our variable
        List<String> groups = new ArrayList<>();

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {

                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // get the collection
                MongoCollection<Document> collection = database.getCollection("Roles");

                // now find the document to get the groups
                Document document = collection.find(new Document("_id", documentIdForRoles)).first();

                // verify the document exists
                if (document != null) {

                    // Iterate through each key, value pair from the document
                    for (Map.Entry<String, Object> entry : document.entrySet()) {

                        // ignore the ID field
                        if (!entry.getKey().equals("_id")) {

                            // add the group to the list to return
                            groups.add(entry.getKey());

                        }

                    }

                }

                // Close the connection once we are done.
                mongoClient.close();

            } catch (MongoException e) {
                e.printStackTrace();
            }

        }

        return groups;
    }

    public static void  addGroup(String newGroup, int newPermLvl) {

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // iterate through all collections
                for (String collectionName : database.listCollectionNames()) {

                    // get a specific collection to update it
                    MongoCollection<Document> collection = database.getCollection(collectionName);

                    // create the update document
                    Document update = new Document("$set", new Document(newGroup, new Document()
                            .append("permlvl", newPermLvl)
                            .append("arr", new ArrayList<>())));

                    // publish the updated document
                    collection.updateMany(new Document(), update);

                }

                // Close the connection once we are done.
                mongoClient.close();


            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeGroup(String oldGroup) {

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // iterate through all collections
                for (String collectionName : database.listCollectionNames()) {

                    // get a specific collection to update it
                    MongoCollection<Document> collection = database.getCollection(collectionName);

                    // create the update document
                    Document update = new Document("$unset", new Document(oldGroup, new ArrayList<String>()));

                    // publish the updated document
                    collection.updateMany(new Document(), update);

                }

                // Close the connection once we are done.
                mongoClient.close();


            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean doesGroupExist(String collectionName, String group) {

        boolean doesExists = false;

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                MongoCollection<Document> collection = database.getCollection(collectionName);

                Document foundDocument = collection.find(eq("_id", new ObjectId(collectionName.equals("Roles") ? documentIdForRoles : documentIdForCommands))).first();

                if (foundDocument != null) {

                    doesExists = foundDocument.containsKey(group);

                }

                // Close the connection once we are done.
                mongoClient.close();

            } catch (MongoException e) {
                e.printStackTrace();
            }

            return doesExists;
        }
    }

    public static String doesGroupHaveSamePermissionLevel(String collectionName, int permLvl) {

        String matchingField = null;

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // get the specified collection name
                MongoCollection<Document> collection = database.getCollection(collectionName);

                FindIterable<Document> documents = collection.find();

                for (Document document : documents) {

                    for (Map.Entry<String, Object> entry : document.entrySet()) {

                        String field = entry.getKey();

                        if (!field.equals("_id")) {

                            Object fieldValue = entry.getValue();

                            if (fieldValue instanceof Document) {

                                Document fieldDocument = (Document) fieldValue;

                                Integer permlvl = fieldDocument.getInteger("permlvl");

                                if (permlvl != null && permlvl == permLvl) {

                                    matchingField = field;

                                }

                            }

                        }

                    }

                }

                // Close the connection once we are done.
                mongoClient.close();

            } catch (MongoException e) {
                e.printStackTrace();
            }

        }

        return matchingField;

    }

    public static int getPermissionLevel(String collectionName, String group) {

        int permLvl = 0;

        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {
                // Connect to our Active Directory Database
                MongoDatabase database = mongoClient.getDatabase("ActiveDirectory");

                // get the specified collection name
                MongoCollection<Document> collection = database.getCollection(collectionName);

                // get the values from the document
                Document fields = collection.find(eq("_id", new ObjectId(collectionName.equals("Roles") ? documentIdForRoles : documentIdForCommands))).first();

                if (fields != null) {

                    permLvl = fields.getInteger(group + ".permlvl");

                }

                // Close the connection once we are done.
                mongoClient.close();

                return permLvl;

            } catch (MongoException e) {
                e.printStackTrace();
            }

        }

        return permLvl;
    }
}