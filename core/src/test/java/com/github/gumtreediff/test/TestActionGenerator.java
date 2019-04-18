/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2011-2015 Jean-Rémy Falleri <jr.falleri@gmail.com>
 * Copyright 2011-2015 Floréal Morandat <florealm@gmail.com>
 */

package com.github.gumtreediff.test;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TypeSet;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.utils.Pair;
import com.github.gumtreediff.tree.TreeContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestActionGenerator {

    @Test
    public void testWithActionExample() {
        Pair<TreeContext, TreeContext> trees = TreeLoader.getActionPair();
        ITree src = trees.first.getRoot();
        ITree dst = trees.second.getRoot();
        MappingStore ms = new MappingStore(src, dst);
        ms.addMapping(src, dst);
        ms.addMapping(src.getChild(1), dst.getChild(0));
        ms.addMapping(src.getChild("1.0"), dst.getChild("0.0"));
        ms.addMapping(src.getChild("1.1"), dst.getChild("0.1"));
        ms.addMapping(src.getChild(0), dst.getChild(1).getChild(0));
        ms.addMapping(src.getChild("0.0"), dst.getChild("1.0.0"));
        ms.addMapping(src.getChild(4), dst.getChild(3));
        ms.addMapping(src.getChild("4.0"), dst.getChild("3.0.0.0"));
        ActionGenerator ag = new ActionGenerator(ms);
        ag.generate();
        List<Action> actions = ag.getActions();
        assertEquals(9,  actions.size());

        Action a = actions.get(0);
        assertTrue(a instanceof Insert);
        Insert i = (Insert) a;
        assertEquals("h", i.getNode().getLabel());
        assertEquals("a", i.getParent().getLabel());
        assertEquals(2, i.getPosition());

        a = actions.get(1);
        assertTrue(a instanceof TreeInsert);
        TreeInsert ti = (TreeInsert) a;
        assertEquals("x", ti.getNode().getLabel());
        assertEquals("a", ti.getParent().getLabel());
        assertEquals(3, ti.getPosition());

        a = actions.get(2);
        assertTrue(a instanceof Move);
        Move m = (Move) a;
        assertEquals("e", m.getNode().getLabel());
        assertEquals("h", m.getParent().getLabel());
        assertEquals(0, m.getPosition());

        a = actions.get(3);
        assertTrue(a instanceof Insert);
        Insert i2 = (Insert) a;
        assertEquals("u", i2.getNode().getLabel());
        assertEquals("j", i2.getParent().getLabel());
        assertEquals(0, i2.getPosition());

        a = actions.get(4);
        assertTrue(a instanceof Update);
        Update u = (Update) a;
        assertEquals("f", u.getNode().getLabel());
        assertEquals("y", u.getValue());

        a = actions.get(5);
        assertTrue(a instanceof Insert);
        Insert i3 = (Insert) a;
        assertEquals("v", i3.getNode().getLabel());
        assertEquals("u", i3.getParent().getLabel());
        assertEquals(0, i3.getPosition());

        a = actions.get(6);
        assertTrue(a instanceof Move);
        Move m2 = (Move) a;
        assertEquals("k", m2.getNode().getLabel());
        assertEquals("v", m2.getParent().getLabel());
        assertEquals(0, m.getPosition());

        a = actions.get(7);
        assertTrue(a instanceof TreeDelete);
        TreeDelete td = (TreeDelete) a;
        assertEquals("g", td.getNode().getLabel());

        a = actions.get(8);
        assertTrue(a instanceof Delete);
        Delete d = (Delete) a;
        assertEquals("i", d.getNode().getLabel());
    }

    @Test
    public void testWithUnmappedRoot() {
        ITree src = new Tree(TypeSet.type("foo"), "");
        ITree dst = new Tree(TypeSet.type("bar"), "");
        MappingStore ms = new MappingStore(src, dst);
        ActionGenerator ag = new ActionGenerator(ms);
        ag.generate();
        List<Action> actions = ag.getActions();
        for (Action a : actions)
            System.out.println(a.toString());
    }

    @Test
    public void testWithActionExampleNoMove() {
        ActionGenerator.REMOVE_MOVES_AND_UPDATES = true;
        Pair<TreeContext, TreeContext> trees = TreeLoader.getActionPair();
        ITree src = trees.first.getRoot();
        ITree dst = trees.second.getRoot();
        MappingStore ms = new MappingStore(src, dst);
        ms.addMapping(src, dst);
        ms.addMapping(src.getChild(1), dst.getChild(0));
        ms.addMapping(src.getChild(1).getChild(0), dst.getChild(0).getChild(0));
        ms.addMapping(src.getChild(1).getChild(1), dst.getChild(0).getChild(1));
        ms.addMapping(src.getChild(0), dst.getChild(1).getChild(0));
        ms.addMapping(src.getChild(0).getChild(0), dst.getChild(1).getChild(0).getChild(0));
        ms.addMapping(src.getChild(4), dst.getChild(3));
        ms.addMapping(src.getChild(4).getChild(0), dst.getChild(3).getChild(0).getChild(0).getChild(0));

        ActionGenerator ag = new ActionGenerator(ms);
        ag.generate();

        for (Action a: ag.getActions())
            System.out.println(a.toString());

        List<Action> actions = ag.getActions();
    }

    @Test
    public void testWithZsCustomExample() {
        Pair<TreeContext, TreeContext> trees = TreeLoader.getZsCustomPair();
        ITree src = trees.first.getRoot();
        ITree dst = trees.second.getRoot();
        MappingStore ms = new MappingStore(src, dst);
        ms.addMapping(src, dst.getChild(0));
        ms.addMapping(src.getChild(0), dst.getChild(0).getChild(0));
        ms.addMapping(src.getChild(1), dst.getChild(0).getChild(1));
        ms.addMapping(src.getChild(1).getChild(0), dst.getChild(0).getChild(1).getChild(0));
        ms.addMapping(src.getChild(1).getChild(2), dst.getChild(0).getChild(1).getChild(2));

        ActionGenerator ag = new ActionGenerator(ms);
        ag.generate();
        List<Action> actions = ag.getActions();
    }

}
