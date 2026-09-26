package com.bugtracker.model;

public class DashboardStats {
    private final int totalBugs;
    private final int openBugs;
    private final int inProgressBugs;
    private final int resolvedBugs;
    private final int closedBugs;
    private final int criticalBugs;

    public DashboardStats(int totalBugs, int openBugs, int inProgressBugs,
                          int resolvedBugs, int closedBugs, int criticalBugs) {
        this.totalBugs = totalBugs;
        this.openBugs = openBugs;
        this.inProgressBugs = inProgressBugs;
        this.resolvedBugs = resolvedBugs;
        this.closedBugs = closedBugs;
        this.criticalBugs = criticalBugs;
    }

    public int getTotalBugs() { return totalBugs; }
    public int getOpenBugs() { return openBugs; }
    public int getInProgressBugs() { return inProgressBugs; }
    public int getResolvedBugs() { return resolvedBugs; }
    public int getClosedBugs() { return closedBugs; }
    public int getCriticalBugs() { return criticalBugs; }
}