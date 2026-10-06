package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.api.DashboardDTO;
import ca.ulaval.glo2003.api.ExpenseDTO;

import javax.management.InvalidAttributeValueException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.*;

public class DashboardFactory {

    private final static int MONTHSINAYEAR = 12;
    private final static int YEARINAYEAR = 1;
    private final static int QUARTERINAYEAR = 4;
    private final static String YEARPREFIX = "Year";
    private final static String MONTHSPREFIX = "Month";
    private final static String QUARTERPREFIX = "Quarter";
    private final static String EXPENSEBYTIMESLICESUFFIX = "ago";

    public static DashboardDTO computeDTOfromGroup(Group group, String time, Integer timeslices) {
        HashMap<String, Double> expensesByTimeslice = getExpensesByTimeslice(group.getExpenses(), time, timeslices);

        HashMap<String, Double> expenseByCategory = getExpensesByCategory(group.getExpenses());

        GroupMember biggestSpender = getBiggestSpender(group);
        String biggestSpenderName = biggestSpender == null ? null : biggestSpender.getName();

        double biggestSpenderAmount = getBiggestSpenderAmount(group);

        DashboardDTO dashboardDTO = new DashboardDTO(expensesByTimeslice,
                biggestSpenderName,
                biggestSpenderAmount,
                expenseByCategory);

        return dashboardDTO;
    }

    private static HashMap<String, Double> getExpensesByCategory(List<Expense> expenses) {
        HashMap<String, Double> expenseByCategory = new HashMap();
        for (Expense expense : expenses) {
            double amount = expenseByCategory.getOrDefault(expense.getDescription(), 0.0);
            expenseByCategory.put(expense.getDescription(), amount + expense.getAmount());
        }
        return expenseByCategory;
    }

    private static HashMap<String, Double> getExpensesByTimeslice(List<Expense> expenses, String timeFormat, int timeslices) {
        HashMap<String, Double> expensesByTimeslice;
        if (timeslices < 0) {
            throw new IllegalArgumentException("Timeslices cannot be negative");
        }
        switch (timeFormat.toUpperCase()) {
            case "M":
                expensesByTimeslice = expensesByMonth(expenses, timeslices);
                break;
            case "Q":
                expensesByTimeslice = expensesByQuarter(expenses, timeslices);
                break;
            case "A":
                expensesByTimeslice = expensesByYear(expenses, timeslices);
                break;
            default:
                throw new IllegalArgumentException("timeFormat should be M, Q or A");
        }
        return expensesByTimeslice;
    }

    private static Double getBiggestSpenderAmount(Group group) {
        GroupMember biggestSpender = getBiggestSpender(group);

        double sum = 0;
        if (biggestSpender == null) {
            return sum;
        }

        for (Expense expense : group.getExpenses()) {
            if (Objects.equals(expense.getPaidBy(), biggestSpender.getName())) {
                sum += expense.getAmount();
            }
        }
        return sum;
    }

    private static GroupMember getBiggestSpender(Group group) {
        Map<GroupMember, Double> totalByMember = new HashMap<>();

        for (Expense expense : group.getExpenses()) {
            GroupMember spender = group.getMemberByName(expense.getPaidBy());
            double currentTotal = totalByMember.getOrDefault(spender, 0.0);
            totalByMember.put(spender, currentTotal + expense.getAmount());
        }

        return totalByMember.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null); // or throw if you prefer
    }

    private static HashMap<String, Double> expensesByMonth(List<Expense> expenses, Integer timeslices) {

        HashMap<String, Double> expensesByTimeslice = createExpenseMap(expenses, timeslices, MONTHSINAYEAR);
        addPrefixesToMap(expensesByTimeslice, MONTHSPREFIX);
        return expensesByTimeslice;
    }

    private static HashMap<String, Double> expensesByQuarter(List<Expense> expenses, Integer timeslices) {

        HashMap<String, Double> expensesByTimeslice = createExpenseMap(expenses, timeslices, QUARTERINAYEAR);
        addPrefixesToMap(expensesByTimeslice, QUARTERPREFIX);
        return expensesByTimeslice;
    }

    private static HashMap<String, Double> expensesByYear(List<Expense> expenses, Integer timeslices) {

        HashMap<String, Double> expensesByTimeslice = createExpenseMap(expenses, timeslices, YEARINAYEAR);
        addPrefixesToMap(expensesByTimeslice, YEARPREFIX);
        return expensesByTimeslice;
    }

    private static void addPrefixesToMap(HashMap<String, Double> expensesByTimeslice, String prefix) {
        HashMap<String, Double> updatedMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : expensesByTimeslice.entrySet()) {
            updatedMap.put(entry.getKey() + " " + prefix + " " + EXPENSEBYTIMESLICESUFFIX, entry.getValue());
        }
        expensesByTimeslice.clear();
        expensesByTimeslice.putAll(updatedMap);
    }

    private static HashMap<String, Double> createExpenseMap(List<Expense> expenses, Integer slices, Integer slicesInAYear) {
        HashMap<String, Double> expensesByTimeslice = new HashMap<>();
        if (expenses == null || expenses.isEmpty()) {
            return expensesByTimeslice;
        }

        expenses.sort(Comparator.comparing(Expense::getPurchaseDate));

        for (Expense expense : expenses) {
            long slicesBetween = getDateSliceIndex(expense.getPurchaseDate(), slicesInAYear);

            if (slicesBetween < slices) {

                String key = String.valueOf(slicesBetween);
                expensesByTimeslice.merge(key, expense.getAmount(), Double::sum);
            }

        }
        return expensesByTimeslice;
    }

    private static int getDateSliceIndex(LocalDate date, double slicesInAYear) {
        long monthsBetween = (ChronoUnit.MONTHS.between(date, LocalDate.now()));
        int slicesBetween = (int) Math.floor((double) monthsBetween * ((double) slicesInAYear / (double) MONTHSINAYEAR));
        return slicesBetween;
    }
}