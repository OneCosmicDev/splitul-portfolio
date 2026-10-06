package ca.ulaval.glo2003;

import ca.ulaval.glo2003.api.GroupResource;
import ca.ulaval.glo2003.api.SentryExceptionMapper;
import ca.ulaval.glo2003.api.auth.AuthenticationFilter;
import ca.ulaval.glo2003.api.HealthResource;
import ca.ulaval.glo2003.data.DatastoreProvider;
import ca.ulaval.glo2003.data.GroupRepositoryInMemory;
import ca.ulaval.glo2003.data.GroupRepositoryMongo;
import ca.ulaval.glo2003.domain.GroupService;
import ca.ulaval.glo2003.data.GroupRepository;
import io.sentry.Sentry;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {
    public static final String DEFAULT_PORT = "8080";
    public static final String BASE_URI = String.format("http://0.0.0.0:%s/", System.getenv("PORT") != null ? System.getenv("PORT") : DEFAULT_PORT);
    public static HttpServer startServer() {
        String persistence = System.getProperty("persistence", "inmemory");
        GroupRepository groupRepository;

        if (persistence.equals("mongo")) {
            groupRepository = new GroupRepositoryMongo();
        } else {
            groupRepository = GroupRepositoryInMemory.getInstance();
        }

        GroupService groupService = new GroupService(groupRepository);
        GroupResource groupApi = new GroupResource(groupService);
        AuthenticationFilter authFilter = new AuthenticationFilter(groupRepository);

        final ResourceConfig rc = new ResourceConfig()
                .register(groupApi)
                .register(authFilter)
                .register(new HealthResource())
                .register(SentryExceptionMapper.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        String dsn = System.getenv("SENTRY_DSN");

        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setTracesSampleRate(1.0);
        });

        startServer();
        System.out.printf("Jersey app started with endpoints available at %s%n", BASE_URI);
    }
}