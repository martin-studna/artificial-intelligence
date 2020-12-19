package csp;

public class Solver {
    // Create a Solver for a problem with _n_ variables.
    public Solver(int n) {
    }
    
    // Add a constraint to the problem.
    public void add(Constraint c) {
    }
    
    // Assign a fixed value to a variable.
    public void setVar(int i, boolean v) {
    }
    
    // Deduce that some variable must have a certain value, and assign that value
    // to the variable.  Return the assignment that was made, or null if
    // no deduction was possible.
    public Assignment solve() {
        return null;
    }
}
