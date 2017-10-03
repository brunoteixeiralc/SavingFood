package br.com.savingfood.wizard.model;

import java.util.ArrayList;

/**
 * Created by Ronan Lima on 16/02/2016.
 */
public interface PageTreeNode {
    public Page findByKey(String key);
    public void flattenCurrentPageSequence(ArrayList<Page> dest);
}
