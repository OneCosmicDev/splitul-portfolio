package ca.ulaval.glo2003.api;

import java.util.Date;
import java.util.Map;

public record DashboardDTO (
        Map<String,Double> expenseByTimeslice,
        String biggestSpender,
        Double biggestSpenderAmount,
        Map<String,Double> expensesByCategory){ }
