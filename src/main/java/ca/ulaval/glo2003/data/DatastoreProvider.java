package ca.ulaval.glo2003.data;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class DatastoreProvider {
    private static DatastoreProvider instance;
    private Datastore datastore;

    private static final String MONGO_URL = System.getenv("MONGO_CLUSTER_URL");
    private static final String MONGO_NAME = System.getenv("MONGO_DATABASE");

    static {
        if(MONGO_URL == null || MONGO_NAME == null) {
            throw new IllegalArgumentException("Mongo url or database null.");
        }
    }

    private DatastoreProvider() {
        this.datastore = Morphia.createDatastore(MongoClients.create(MONGO_URL), MONGO_NAME);
    }

    public static synchronized DatastoreProvider getInstance() {
        if(instance == null) {
            instance = new DatastoreProvider();
        }
        return instance;
    }

    public Datastore provide(){
        return datastore;
    }
}
