package ca.ulaval.glo2003.api.auth;

import ca.ulaval.glo2003.api.ErrorDTO;
import ca.ulaval.glo2003.data.GroupRepository;
import ca.ulaval.glo2003.data.GroupRepositoryInMemory;
import ca.ulaval.glo2003.domain.Group;
import ca.ulaval.glo2003.domain.GroupService;
import ca.ulaval.glo2003.domain.Exceptions.GroupNotFoundException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private final GroupService groupService;

    private static final List<Route> EXCLUDED_ROUTES = List.of(
            new Route("POST", "/groups"),          
            new Route("POST", "/groups/{groupName}/members"), 
            new Route("GET", "/groups"),                 
            new Route("GET", "/health")                  
    );

    public AuthenticationFilter(GroupRepository groupRepository) {
        this.groupService = new GroupService(groupRepository);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();

        if (isExcluded(method, path)) {
            return;
        }

        String memberName = requestContext.getHeaderString("Member");
        if (memberName == null || memberName.isBlank()) {
            abort(requestContext, Response.Status.FORBIDDEN, "FORBIDDEN", "Vous n'etes pas membre du groupe");
            return;
        }

        String[] segments = path.split("/");
        if (segments.length >= 2 && segments[0].equals("groups")) {
            String groupName = segments[1];

            try {
                Group group = groupService.getGroup(groupName);
                if (!group.hasMember(memberName)) {
                    abort(requestContext, Response.Status.FORBIDDEN, "FORBIDDEN", "Vous n'etes pas membre du groupe");
                }
            } catch (GroupNotFoundException e) {
                abort(requestContext, Response.Status.NOT_FOUND, "ENTITY_NOT_FOUND",
                        "Le groupe " + groupName + " n'existe pas");
            }
        }
    }

    private boolean isExcluded(String method, String path) {

        String normalizedPath = normalizePath(path);

        for (Route r : EXCLUDED_ROUTES) {
            if (r.method().equalsIgnoreCase(method)
                    && r.path().equalsIgnoreCase(normalizedPath)) {
                return true;
            }
        }
        return false;
    }

    private String normalizePath(String path) {

        String[] segments = path.split("/");
        if (segments.length == 1) {
            return "/" + segments[0];
        } else if (segments.length == 2) {
            return "/groups/{groupName}";
        } else if (segments.length == 3 && segments[2].equals("members")) {
            return "/groups/{groupName}/members";
        }
        return "/" + path;
    }

    private void abort(ContainerRequestContext ctx, Response.Status status, String error, String description) {
        ErrorDTO dto = new ErrorDTO(error, description);
        ctx.abortWith(Response.status(status).entity(dto).build());
    }
    private record Route(String method, String path) {}
}
