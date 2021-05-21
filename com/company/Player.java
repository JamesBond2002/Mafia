package com.company;

import java.util.*;

abstract public class Player {
    protected double HP;
    protected final boolean isUser;
    protected boolean isDead = false;
    Scanner sc = new Scanner(System.in);
    Random rand = new Random();
    Player(boolean isUser) {
        this.isUser = isUser;
    }
    public boolean isUser() {return isUser;}
    public boolean isDead() {return isDead;}
    public void setDead() {this.isDead = true;}
    double getHP() {return this.HP;}
    void setHP(double x) {this.HP = x;}
    int vote(int x) {
        if(isUser) {
            System.out.println("Select a person to vote out:");
            return sc.nextInt()-1;
        }
        return rand.nextInt(x);
    }
    abstract int choose(int x);
}

class Mafia extends Player {
    Mafia(boolean isUser) {
        super(isUser);
        super.HP = 2500;
    }

    @Override
    int choose(int x) {
        if(isUser) {
            System.out.println("Select a player to kill:");
            return sc.nextInt()-1;
        }
        return rand.nextInt(x);
    }
}

class sortByHP implements Comparator<Mafia> {
    @Override
    public int compare(Mafia p1, Mafia p2) {
        if(p1.getHP()>p2.getHP())
            return 1;
        return -1;
    }
}

class Detective extends Player {
    Detective(boolean isUser) {
        super(isUser);
        super.HP = 800;
    }

    @Override
    int choose(int x) {
        if(isUser) {
            System.out.println("Select a player to test:");
            return sc.nextInt()-1;
        }
        return rand.nextInt(x);
    }
}

class Healer extends Player {
    Healer(boolean isUser) {
        super(isUser);
        super.HP = 800;
    }

    @Override
    int choose(int x) {
        if(isUser) {
            System.out.println("Select a player to heal:");
            return sc.nextInt()-1;
        }
        return rand.nextInt(x);
    }
}

class Commoner extends Player {
    Commoner(boolean isUser) {
        super(isUser);
        super.HP = 1000;
    }

    @Override
    int choose(int x) {
        return 0;
    }
}