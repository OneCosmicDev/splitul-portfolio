package ca.ulaval.glo2003.api.auth;

import ca.ulaval.glo2003.data.GroupRepository;
import ca.ulaval.glo2003.data.GroupRepositoryInMemory;
import ca.ulaval.glo2003.domain.Exceptions;
import ca.ulaval.glo2003.domain.Group;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AuthenticationFilterTest {

    private AuthenticationFilter filter;
    private ContainerRequestContext requestContext;
    private UriInfo uriInfo;
    private GroupRepository groupRepository;

    @BeforeEach
    void setUp() {
        groupRepository = mock(GroupRepository.class);
        filter = new AuthenticationFilter(groupRepository);
        requestContext = mock(ContainerRequestContext.class);
        uriInfo = mock(UriInfo.class);
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
    }

    @Test
    void testExcludedRoute() throws Exception {
        when(requestContext.getMethod()).thenReturn("POST");
        when(uriInfo.getPath()).thenReturn("groups");
        filter.filter(requestContext);
        verify(requestContext, never()).abortWith(any(Response.class));
    }

    @Test
    void testMissingMemberHeader() throws Exception {
        when(requestContext.getMethod()).thenReturn("GET");
        when(uriInfo.getPath()).thenReturn("groups/testGroup/expenses");
        when(requestContext.getHeaderString("Member")).thenReturn(null);
        filter.filter(requestContext);
        verify(requestContext, times(1)).abortWith(argThat(response ->
                response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()));
    }

    @Test
    void testGroupNotFound() throws Exception {
        when(requestContext.getMethod()).thenReturn("GET");
        when(uriInfo.getPath()).thenReturn("groups/nonexistent/expenses");
        when(requestContext.getHeaderString("Member")).thenReturn("john");
        when(groupRepository.findByName("nonexistent")).thenThrow(new Exceptions.GroupNotFoundException());

        filter.filter(requestContext);
        verify(requestContext, times(1)).abortWith(argThat(response ->
                response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    void testMemberNotInGroup() throws Exception {
        Group group = new Group("testGroup");
        group.addMember("alice");

        when(groupRepository.findByName("testGroup")).thenReturn(group);
        when(requestContext.getMethod()).thenReturn("GET");
        when(uriInfo.getPath()).thenReturn("groups/testGroup/expenses");
        when(requestContext.getHeaderString("Member")).thenReturn("john");
        filter.filter(requestContext);
        verify(requestContext, times(1)).abortWith(argThat(response ->
                response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()));
    }

    @Test
    void testValidAuthentication() throws Exception {
        Group group = new Group("testGroup");
        group.addMember("john");

        when(groupRepository.findByName("testGroup")).thenReturn(group);
        when(requestContext.getMethod()).thenReturn("GET");
        when(uriInfo.getPath()).thenReturn("groups/testGroup/expenses");
        when(requestContext.getHeaderString("Member")).thenReturn("john");
        filter.filter(requestContext);
        verify(requestContext, never()).abortWith(any(Response.class));
    }
}