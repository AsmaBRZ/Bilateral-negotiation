package fr.kyriba.bidgeneration.MCTS;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/********************************************************************************
 * Copyright 2000 - 2015 Kyriba Corp. All Rights Reserved.                      *
 * The content of this file is copyrighted by Kyriba Corporation and can not be *
 * reproduced, distributed, altered or used in any form, in whole or in part.   *
 *                                                                              *
 * Date        Author  Changes                                                  *
 * 01/08/2016  P-CBU   Initial version.                                         *
 ********************************************************************************/
/**Arbre MCTS. Contient la référence vers le nœud, le père, les fils.*/
public class MCTree {
    private MCTNode root;
    private List<MCTree> subtrees;
    private MCTree parent;

    public MCTNode getRoot() {
        return root;
    }

    public List<MCTree> getSubtrees() {
            return subtrees;
    }

    public MCTree getParent() {
        return parent;
    }

    public MCTree(MCTNode root, MCTree parent){
        this.root = root;
        subtrees = new CopyOnWriteArrayList<>();
        this.parent = parent;
    }

    public void addSubtree(MCTree tree){
            subtrees.add(tree);
    }
}
