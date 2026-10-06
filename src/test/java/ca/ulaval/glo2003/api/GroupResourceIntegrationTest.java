package ca.ulaval.glo2003.api;

import ca.ulaval.glo2003.data.GroupRepository;
import ca.ulaval.glo2003.data.GroupRepositoryInMemory;
import ca.ulaval.glo2003.domain.Exceptions;
import ca.ulaval.glo2003.domain.GroupService;
import ca.ulaval.glo2003.domain.Group;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class GroupResourceIntegrationTest extends JerseyTest {

    private static final String VALID_GROUP_NAME = "testGroup";
    private static final String INVALID_GROUP_NAME = "invalid group";
    private static final String EXISTING_GROUP_NAME = "existingGroup";
    private static final String NON_EXISTENT_GROUP_NAME = "unknownGroup";
    private static final String GROUP_NAME = "myGroup";
    private static final String MEMBER_NAME = "john";
    private static final String GROUP_ONE_NAME = "groupOne";
    private static final String GROUP_TWO_NAME = "groupTwo";
    private static final String MEMBER_ONE_NAME = "alice";
    private static final String MEMBER_TWO_NAME = "bob";
    private static final String LOCATION_HEADER = "Location";
    private static final String GROUPS_ENDPOINT = "/groups";

    private static final String BASE_URL = "http://localhost:8080/groups/";
    private static final int STATUS_CREATED = 201;
    private static final int STATUS_OK = 200;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_CONFLICT = 409;
    private static final int STATUS_NOT_FOUND = 404;

    private GroupService groupServiceMock;

    @Override
    protected Application configure() {
        groupServiceMock = Mockito.mock(GroupService.class);

        when(groupServiceMock.createGroup(VALID_GROUP_NAME)).thenReturn(new Group(VALID_GROUP_NAME));
        doThrow(new Exceptions.GroupAlreadyExistsException()).when(groupServiceMock).createGroup(EXISTING_GROUP_NAME);
        doThrow(new Exceptions.InvalidGroupNameException()).when(groupServiceMock).createGroup(INVALID_GROUP_NAME);

        return new ResourceConfig().register(new GroupResource(groupServiceMock));
    }

    private GroupResource.CreateGroupRequest createGroupRequest(String name) {
        GroupResource.CreateGroupRequest request = new GroupResource.CreateGroupRequest();
        request.setName(name);
        return request;
    }

    @Test
    public void givenValidGroupName_whenCreateGroup_thenReturns201() {
        Response response = target(GROUPS_ENDPOINT)
                .request()
                .post(Entity.json(createGroupRequest(VALID_GROUP_NAME)));

        assertEquals(STATUS_CREATED, response.getStatus());
    }

    @Test
    public void givenValidGroupName_whenCreateGroup_thenServiceIsCalled() {
        target(GROUPS_ENDPOINT)
                .request()
                .post(Entity.json(createGroupRequest(VALID_GROUP_NAME)));

        verify(groupServiceMock).createGroup(VALID_GROUP_NAME);
    }

    @Test
    public void givenValidGroupName_whenCreateGroup_thenLocationHeaderIsSet() {
        Response response = target(GROUPS_ENDPOINT)
                .request()
                .post(Entity.json(createGroupRequest(VALID_GROUP_NAME)));

        String expectedLocation = BASE_URL + VALID_GROUP_NAME;
        assertEquals(expectedLocation, response.getHeaderString(LOCATION_HEADER));
    }

    @Test
    public void givenInvalidGroupName_whenCreateGroup_thenReturns400() {
        Response response = target(GROUPS_ENDPOINT)
                .request()
                .post(Entity.json(createGroupRequest(INVALID_GROUP_NAME)));

        assertEquals(STATUS_BAD_REQUEST, response.getStatus());
    }
    @Test
    public void givenInvalidGroupName_whenCreateGroup_thenServiceIsCalled() {
        try {
            target(GROUPS_ENDPOINT)
                    .request()
                    .post(Entity.json(createGroupRequest(INVALID_GROUP_NAME)));
        } catch (Exception ignored) {

        }

        verify(groupServiceMock).createGroup(INVALID_GROUP_NAME);
    }


    @Test
    public void givenExistingGroupName_whenCreateGroup_thenReturns409() {
        Response response = target(GROUPS_ENDPOINT)
                .request()
                .post(Entity.json(createGroupRequest(EXISTING_GROUP_NAME)));

        assertEquals(STATUS_CONFLICT, response.getStatus());
    }
    @Test
    public void givenExistingGroupName_whenCreateGroup_thenServiceIsCalled() {
        try {
            target(GROUPS_ENDPOINT)
                    .request()
                    .post(Entity.json(createGroupRequest(EXISTING_GROUP_NAME)));
        } catch (Exception ignored) {

        }

        verify(groupServiceMock).createGroup(EXISTING_GROUP_NAME);
    }



    @Test
    public void givenExistingGroup_whenGetGroup_thenReturns200() {
        Group group = new Group(GROUP_NAME);
        group.addMember(MEMBER_NAME);
        when(groupServiceMock.getGroup(GROUP_NAME)).thenReturn(group);

        Response response = target(GROUPS_ENDPOINT + "/" + GROUP_NAME)
                .request()
                .get();

        assertEquals(STATUS_OK, response.getStatus());
    }

    @Test
    public void givenExistingGroup_whenGetGroup_thenResponseContainsGroupName() {
        Group group = new Group(GROUP_NAME);
        group.addMember(MEMBER_NAME);
        when(groupServiceMock.getGroup(GROUP_NAME)).thenReturn(group);

        String responseBody = target(GROUPS_ENDPOINT + "/" + GROUP_NAME)
                .request()
                .get()
                .readEntity(String.class);

        assertTrue(responseBody.contains(GROUP_NAME));
    }

    @Test
    public void givenExistingGroup_whenGetGroup_thenServiceIsCalled() {
        Group group = new Group(GROUP_NAME);
        group.addMember(MEMBER_NAME);
        when(groupServiceMock.getGroup(GROUP_NAME)).thenReturn(group);

        target(GROUPS_ENDPOINT + "/" + GROUP_NAME)
                .request()
                .get();

        verify(groupServiceMock).getGroup(GROUP_NAME);
    }

    @Test
    public void givenMultipleGroups_whenListGroups_thenReturns200() {
        Group group1 = new Group(GROUP_ONE_NAME);
        group1.addMember(MEMBER_ONE_NAME);

        Group group2 = new Group(GROUP_TWO_NAME);
        group2.addMember(MEMBER_TWO_NAME);

        when(groupServiceMock.getAllGroups()).thenReturn(List.of(group1, group2));

        Response response = target(GROUPS_ENDPOINT)
                .request()
                .get();

        assertEquals(STATUS_OK, response.getStatus());
    }

    @Test
    public void givenMultipleGroups_whenListGroups_thenResponseContainsGroupNames() {
        Group group1 = new Group(GROUP_ONE_NAME);
        group1.addMember(MEMBER_ONE_NAME);

        Group group2 = new Group(GROUP_TWO_NAME);
        group2.addMember(MEMBER_TWO_NAME);

        when(groupServiceMock.getAllGroups()).thenReturn(List.of(group1, group2));

        String responseBody = target(GROUPS_ENDPOINT)
                .request()
                .get()
                .readEntity(String.class);

        assertTrue(responseBody.contains(GROUP_ONE_NAME));
        assertTrue(responseBody.contains(GROUP_TWO_NAME));
    }

    @Test
    public void whenListGroups_thenServiceIsCalled() {
        when(groupServiceMock.getAllGroups()).thenReturn(List.of());

        target(GROUPS_ENDPOINT)
                .request()
                .get();

        verify(groupServiceMock).getAllGroups();
    }

    @Test
    public void givenNonExistentGroup_whenGetGroup_thenReturns404() {
        when(groupServiceMock.getGroup(NON_EXISTENT_GROUP_NAME))
                .thenThrow(new Exceptions.GroupNotFoundException());

        Response response = target(GROUPS_ENDPOINT + "/" + NON_EXISTENT_GROUP_NAME)
                .request()
                .get();

        assertEquals(STATUS_NOT_FOUND, response.getStatus());
    }

    @Test
    public void givenNonExistentGroup_whenGetGroup_thenServiceIsCalled() {
        when(groupServiceMock.getGroup(NON_EXISTENT_GROUP_NAME))
                .thenThrow(new Exceptions.GroupNotFoundException());

        target(GROUPS_ENDPOINT + "/" + NON_EXISTENT_GROUP_NAME)
                .request()
                .get();

        verify(groupServiceMock).getGroup(NON_EXISTENT_GROUP_NAME);
    }
}
