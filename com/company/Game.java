package com.company;

import java.util.*;

public class Game {
    Scanner sc = new Scanner(System.in);
    Random rand = new Random();
    private final ArrayList<Player> players = new ArrayList<>();
    private int n, killed = 0;
    int userType, idUser = 0;
    private int cntMafia = 0;
    TreeSet<Mafia> M = new TreeSet<>(new sortByHP());
    Game() {
        System.out.println("Welcome to Mafia\nEnter Number of players:");
        n = sc.nextInt();
        while(n<6) {
            System.out.println("Number of players should be greater than or equal to 6");
            System.out.println("Enter tha number of players: ");
            n = sc.nextInt();
        }
        System.out.println("Choose a Character\n" +
                "1) Mafia\n" +
                "2) Detective\n" +
                "3) Healer\n" +
                "4) Commoner\n" +
                "5) Assign Randomly");
        userType = sc.nextInt();
        if(userType == 5) userType = rand.nextInt(4)+1;
        for(int i = 0; i<n/5; i++)
            players.add(new Mafia(i == 0 && userType == 1));
        for(int i = 0; i<n/5; i++)
            players.add(new Detective(i == 0 && userType == 2));
        for(int i = 0; i<Math.max(1, n/10); i++)
            players.add(new Healer(i == 0 && userType == 3));
        for(int i = 0; i<n - 2*(n/5) - Math.max(1, n/10); i++)
            players.add(new Commoner(i == 0 && userType == 4));
        cntMafia = n/5;
        Collections.shuffle(players);
        ArrayList<String> tmp = new ArrayList<>();
        for(int i = 0; i<n; i++)
            if(players.get(i).isUser()) {
                System.out.println("You are Player" + (i + 1));
                System.out.println("You are a " + type(players.get(i)));
                idUser = i;
                break;
            }
        for(int i = 0; i<n && userType != 4; i++)
            if(i != idUser && players.get(i).getClass() == players.get(idUser).getClass())
                tmp.add("Player"+(i+1));
        if(tmp.isEmpty() && userType != 4)
            System.out.println("No other " + type(players.get(idUser)));
        else if(userType != 4)
            System.out.println("Other " + type(players.get(idUser)) + " are: "+tmp.toString());
        for(Player player : players)
            if(player instanceof Mafia)
                M.add((Mafia)player);
        for(int id = 1; cntMafia != 0 && n-killed != 2*cntMafia && n-killed != cntMafia; id++)
            Round(id);
        System.out.println(cntMafia == 0 ? "MAFIA lost" : "MAFIA won");
        for(int i = 0; i<n; i++)
            System.out.println("Player" + (i+1) + " is a " + type(players.get(i)));
    }
    String type(Player player) {
        String t = player.getClass().toString();
        t = t.substring(t.length()-3, t.length());
        if(t.equals("ive"))
            return "Detective";
        if(t.equals("fia"))
            return "Mafia";
        if(t.equals("ler"))
            return "Healer";
        return "Commoner";
    }
    int find(Player player) {
        // To find the first alive player of the input class type.
        for(int i = 0; i<n; i++)
            if (!players.get(i).isDead() && players.get(i).getClass() == player.getClass())
                return i;
        return -1;
    }
    double roundOff(double x) {
        int tmp = (int) ((int)x*1e5);
        return (double) tmp/1e5;
    }
    void Round(int idRound) {
        System.out.println("Round "+idRound);
        System.out.println(n-killed+" players are remaining: ");
        //System.out.println(players.toString());
        for(int i = 0; i<n; i++)
            if(!players.get(i).isDead())
                System.out.print("Player"+(i+1)+" ");
        System.out.print("are alive.\n");
        Mafia mafia_test = new Mafia(false);
        Detective detective_test = new Detective(false);
        Healer healer_test = new Healer(false);
        int mafia = (userType == 1 && !players.get(idUser).isDead() ? idUser : find(mafia_test));
        int detective = (userType == 2 && !players.get(idUser).isDead() ? idUser : find(detective_test));
        int healer = (userType == 3 && !players.get(idUser).isDead() ? idUser : find(healer_test));
        int toBeKilled = players.get(mafia).choose(n);
        while(players.get(toBeKilled) instanceof Mafia || players.get(toBeKilled).isDead()) {
            if(players.get(mafia).isUser())
                System.out.println("You cannot kill a "+(players.get(toBeKilled) instanceof Mafia ? "Mafia" : "Dead Player"));
            toBeKilled = players.get(mafia).choose(n);
        }
        int totalHP = 0;
        for(int i = 0; i<n; i++)
            if(players.get(i) instanceof Mafia)
                totalHP += players.get(i).getHP();
        double x = players.get(toBeKilled).getHP();
        players.get(toBeKilled).setHP(Math.max(0, x-totalHP));
        while(roundOff(x)>0 && !M.isEmpty()) {
            double _x = x;
            for(Mafia m : M) {
                double initialHP = m.getHP();
                m.setHP(Math.max(0, initialHP - _x / M.size()));
                x -= Math.min(_x / M.size(), initialHP);
            }
            M.removeIf(m -> m.getHP() == 0);
        }
        if(mafia != idUser) System.out.println("Mafias have chosen their target.");
        boolean flag = false;
        if(detective != -1) {
            int toBeTested = players.get(detective).choose(n);
            while (players.get(toBeTested) instanceof Detective || players.get(toBeTested).isDead()) {
                if(players.get(detective).isUser())
                    System.out.println("You cannot test a "+(players.get(toBeTested) instanceof Detective ? "Detective" : "Dead Player"));
                toBeTested = players.get(detective).choose(n);
            }
            if(players.get(toBeTested) instanceof Mafia) {
                players.get(toBeTested).setDead();
                flag = true;
                killed++;
                cntMafia--;
            }
            if(detective == idUser)
                System.out.println("Player "+(toBeTested+1)+" is " + (flag ? "" : "not ") + "a mafia");
            else
                System.out.println("Detectives have chosen a player to test.");
        }
        else {
            System.out.println("Detectives have chosen a player to test.");
        }
        if(healer != -1) {
            int toBeHealed = players.get(healer).choose(n);
            while (players.get(toBeHealed).isDead()) {
                if(players.get(healer).isUser())
                    System.out.println("You cannot heal a Dead Player");
                toBeHealed = players.get(healer).choose(n);
            }
            players.get(toBeHealed).setHP(players.get(toBeHealed).getHP()+500);
            if(healer == idUser)
                System.out.println("Player"+(toBeHealed+1)+"is healed.");
            else
                System.out.println("Healers have chosen a player to heal.");
            if(players.get(toBeHealed) instanceof Mafia && players.get(toBeHealed).getHP() == 500)
                M.add((Mafia)players.get(toBeHealed));
        } else {
            System.out.println("Healers have chosen a player to heal.");
        }
        int toBeKicked = 0;
        if(!flag) {
            int[] arr = new int[n];
            for(Player player : players) {
                if(player.isDead())
                    continue;
                int vote = player.vote(n);
                while(players.get(vote).isDead) {
                    if (player.isUser())
                        System.out.println("You cannot vote a Dead Player.");
                    vote = player.vote(n);
                }
                arr[vote]++;
                if(arr[toBeKicked]<arr[vote])
                    toBeKicked = vote;
            }
            players.get(toBeKicked).setDead();
            if(players.get(toBeKicked) instanceof Mafia)
                cntMafia--;
            killed++;
        }
        M.removeIf(Mafia::isDead);
        if(players.get(toBeKilled).getHP() == 0 && toBeKicked != toBeKilled) {
            players.get(toBeKilled).setDead();
            killed++;
        }
        System.out.println("-- END OF ACTIONS --");
        if(players.get(toBeKilled).getHP() == 0)
            System.out.println("Player"+(toBeKilled+1)+" is killed");
        else
            System.out.println("No one died.");
        if(!flag)
            System.out.println("Player"+(toBeKicked+1)+" has been voted out.");
        System.out.println("-- END OF ROUND --");
    }
}
