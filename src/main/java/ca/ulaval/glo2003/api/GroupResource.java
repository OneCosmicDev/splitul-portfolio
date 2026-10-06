package ca.ulaval.glo2003.api;

import ca.ulaval.glo2003.data.GroupRepositoryInMemory;
import ca.ulaval.glo2003.domain.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import ca.ulaval.glo2003.domain.Exceptions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Path("/groups")
public class GroupResource {
    private final GroupService groupService;
    private final GroupConverter groupConverter;
    private final ExpenseConverter expenseConverter;

    public static record MemberRequest(String memberName) {}

    public static class CreateGroupRequest {
        private String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class CreateExpenseRequest {
        private String description;
        private double amount;
        private String purchaseDate;
        private String paidBy;
        private Map<String, Double> split;
        private String splitMethod = "equally"; // Valeur par défaut

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getPurchaseDate() {
            return purchaseDate;
        }

        public void setPurchaseDate(String purchaseDate) {
            this.purchaseDate = purchaseDate;
        }

        public String getPaidBy() {
            return paidBy;
        }

        public void setPaidBy(String paidBy) {
            this.paidBy = paidBy;
        }

        public Map<String, Double> getSplit() {
            return split;
        }

        public void setSplit(Map<String, Double> split) {
            this.split = split;
        }
        
        public String getSplitMethod() {
            return splitMethod;
        }
        
        public void setSplitMethod(String splitMethod) {
            this.splitMethod = splitMethod;
        }
    }

    public GroupResource() {
        this.groupConverter = new GroupConverter();
        this.expenseConverter = new ExpenseConverter();
        this.groupService = new GroupService();
    }
    public GroupResource(GroupService groupService) {
        this.groupConverter = new GroupConverter();
        this.expenseConverter = new ExpenseConverter();
        this.groupService = groupService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroup(CreateGroupRequest request) {
        try {
            Group group = groupService.createGroup(request.getName());
            URI groupLocation = URI.create("http://localhost:8080/groups/" + group.getName());
            return Response.created(groupLocation)
                    .entity(groupConverter.toDTO(group))
                    .build();
        } catch (InvalidGroupNameException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDTO("INVALID_PARAMETER", "Le nom du groupe contient un ou des espaces"))
                    .build();
        } catch (GroupAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorDTO("CONFLICTING_PARAMETER", "Le nom du groupe " + request.getName() + " existe déjà"))
                    .build();
        }
    }

    @DELETE
    @Path("/{groupName}")
    public Response deleteGroup(@PathParam("groupName") String groupName, @HeaderParam("Member") String memberName) {
        try {
            groupService.deleteGroup(groupName, memberName);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exceptions.GroupNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO("ENTITY_NOT_FOUND", "Le groupe " + groupName + " n'existe pas"))
                    .build();
        } catch (Exceptions.MemberNotFoundException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorDTO("FORBIDDEN", "Vous n'êtes pas membre du groupe"))
                    .build();
        } catch (Exceptions.GroupHasUnresolvedDebtsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorDTO("INVALID_ACTION", "Le groupe ne peut pas être supprimé, certaines dettes ne sont pas réglées"))
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listGroups() {
        return Response.ok(groupConverter.toDTOs(groupService.getAllGroups())).build();
    }

    @GET
    @Path("/{groupName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroup(@PathParam("groupName") String groupName) {
        try {
            Group group = groupService.getGroup(groupName);
            return Response.ok(groupConverter.toDTO(group)).build();
        } catch (GroupNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO("ENTITY_NOT_FOUND", "Le groupe " + groupName + " n'existe pas"))
                    .build();
        }
    }

    @POST
    @Path("/{groupName}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMember(@PathParam("groupName") String groupName, MemberRequest request) {
        try {
            Group group = groupService.addMember(groupName, request.memberName());
            URI memberLocation = URI.create(String.format("http://localhost:8080/groups/%s/members/%s",
                    groupName, request.memberName()));
            return Response.created(memberLocation)
                    .entity(groupConverter.toDTO(group))
                    .build();
        } catch (GroupNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO("ENTITY_NOT_FOUND", "Le groupe " + groupName + " n'existe pas"))
                    .build();
        } catch (InvalidMemberNameException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorDTO("INVALID_PARAMETER", "Le nom du membre contient un ou des espaces"))
                    .build();
        } catch (MemberAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorDTO("CONFLICTING_PARAMETER", "Le membre " + request.memberName() + " est deja dans le groupe"))
                    .build();
        }
    }

    @GET
    @Path("/{groupName}/members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listGroupMembers(
            @PathParam("groupName") String groupName,
            @HeaderParam("Member") String requestingMember) {
        try {
            Group group = groupService.getGroup(groupName);

            if (!group.hasMember(requestingMember)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorDTO("FORBIDDEN", "Vous n'etes pas membre du groupe"))
                        .build();
            }

            List<MemberDTO> memberWithDebtsList = groupConverter.toMembersDto(group);

            return Response.ok(memberWithDebtsList).build();
        }
        catch (GroupNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO("ENTITY_NOT_FOUND", "Le groupe " + groupName + " n'existe pas"))
                    .build();
        }
    }

    @POST
    @Path("/{groupName}/expenses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addExpense(@PathParam("groupName") String groupName, CreateExpenseRequest request) {
        try {
            Group group = groupService.getGroup(groupName);
            if (!group.hasMember(request.getPaidBy())) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorDTO("ENTITY_NOT_FOUND", "Le membre " + request.getPaidBy() + " n'existe pas dans ce groupe"))
                        .build();
            }

            try {
                ExpenseSplit split = group.calculateExpenseSplit(request.getSplitMethod(), request.getSplit());
                Expense expense = groupService.addExpense(
                        groupName,
                        request.getDescription(),
                        request.getAmount(),
                        request.getPurchaseDate(),
                        request.getPaidBy(),
                        split
                );

                URI expenseLocation = URI.create(String.format("http://localhost:8080/groups/%s/expenses/%s",
                        groupName, expense.getId()));
                return Response.created(expenseLocation)
                        .entity(expenseConverter.toDTO(expense))
                        .build();
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDTO("INVALID_SPLIT", e.getMessage()))
                        .build();
            } catch (InvalidAmountException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDTO("INVALID_AMOUNT", "Le montant doit être positif"))
                        .build();
            } catch (InvalidDateException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDTO("INVALID_DATE", "La date d'achat ne peut pas être dans le futur"))
                        .build();
            } catch (InvalidDateFormatException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDTO("INVALID_DATE_FORMAT", "Le format de la date est invalide. Utilisez ISO_DATE (yyyy-MM-dd)"))
                        .build();
            }
        } catch (GroupNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO("ENTITY_NOT_FOUND", e.getMessage()))
                    .build();
        }
    }
    @PUT
    @Path("/{groupName}/members/{memberName}/settle")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response settleDebts(@PathParam("groupName") String groupName, @PathParam("memberName") String memberName, @HeaderParam("Member") String requestingMember) {
        try {
            Group group = groupService.getGroup(groupName);
            if (!group.hasMember(requestingMember)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorDTO("FORBIDDEN", "Vous n'etes pas membre du groupe"))
                        .build();
            }
            groupService.settleDebts(groupName,memberName, requestingMember);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GroupNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO("ENTITY_NOT_FOUND", "Le groupe " + groupName + " n'existe pas"))
                    .build();
        } catch (MemberNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO("ENTITY_NOT_FOUND", "Le membre " + e.getMemberName() + " n'existe pas dans ce groupe"))
                    .build();
        }

    }

    @GET
    @Path("/{groupName}/expenses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpenseHistory(@PathParam("groupName") String groupName, @HeaderParam("Member") String requestingMember) {
        try {
            var history = groupService.getExpenseHistory(groupName, requestingMember);
            var expenseDTOs = expenseConverter.toDTOs(history.expenses());
            ExpenseHistoryDTO expenseHistory = new ExpenseHistoryDTO(history.total(), expenseDTOs);
            return Response.ok(expenseHistory).build();
        } catch (Exceptions.GroupNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO("ENTITY_NOT_FOUND", "Le groupe " + groupName + " n'existe pas"))
                    .build();
        } catch (Exceptions.MemberNotFoundException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorDTO("FORBIDDEN", "Vous n'êtes pas membre du groupe"))
                    .build();
        }
    }
    @GET
    @Path("/{groupName}/dashboard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboard(@PathParam("groupName") String groupName,
                                 @HeaderParam("Member") String requestingMember,
                                 @DefaultValue("M") @QueryParam("time") String time,
                                 @DefaultValue("6") @QueryParam("timeslices") Integer timeslices) {
        try{

            var dashboard = groupService.getDashboardAsDTO(groupName,requestingMember,time,timeslices);

            return Response.ok(dashboard).build();

        } catch (Exceptions.GroupNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDTO("ENTITY_NOT_FOUND", "Le groupe " + groupName + " n'existe pas"))
                    .build();
        } catch (Exceptions.MemberNotFoundException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorDTO("FORBIDDEN", "Vous n'êtes pas membre du groupe"))
                    .build();
        }
    }
}