package fr.kyriba.bidgeneration.MCTS;

import fr.kyriba.protocol.Offer;

import java.util.ArrayList;
import java.util.List;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 01/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
/**Nœud de l'arbre MCTS. Contient le mouvement correspondant, mais aussi le score, le nombre de visite etc.*/
public class MCTNode {
    private int visited;
    private List<Double> scores;
    private int player;

    private Offer move;

    public MCTNode(int player, Offer move) {
        this.player = player;
        this.move = move;
        this.visited = 0;
        this.scores = new ArrayList<>();
    }

    public Offer getMove() {
        return move;
    }

    public int getVisited() {
        return visited;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
    }

    public Double getScore(int i){
        if(i>= scores.size()){
            throw new IllegalArgumentException("There are " + scores.size()+1 +"negotiators and you asked the " + i+1 + "th one");
        }
        return scores.get(i);
    }

    public int getPlayer() {
        return player;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("Move: " + move.toString() + "; visited");
        builder.append(visited).append(";  ");
        for(int i = 0; i<scores.size(); i++){
            builder.append("score of player ").append(i).append(": ").append(scores.get(i)).append("; ");
        }
        return builder.toString();
    }
}
