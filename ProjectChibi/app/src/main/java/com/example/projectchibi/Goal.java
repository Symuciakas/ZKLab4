package com.example.projectchibi;


public class Goal {
    private String name;
    private int amount;
    private boolean done;

    public Goal(String name, int amount)
    {
        this.name = name;
        this.amount = amount;
        done = false;
    }

    public void achieveGoal()
    {
        done = true;
    }

    public String getName()
    {
        return name;
    }

    public int getAmount()
    {
        return amount;
    }
}
