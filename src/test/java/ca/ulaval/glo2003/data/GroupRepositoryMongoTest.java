package ca.ulaval.glo2003.data;

import com.mongodb.client.MongoClients;
import dev.morphia.Morphia;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class GroupRepositoryMongoTest extends GroupRepositoryInterfaceTest{

    @Container
    final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    @Override
    protected GroupRepository createGroupPersistence(){
        String connectionString = mongoDBContainer.getReplicaSetUrl("mongodb");
        var datastore = Morphia.createDatastore(MongoClients.create(connectionString), "tests");
        return new GroupRepositoryMongo(datastore);
    }

}
