package org.heigit.ors.isorasters;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    QuadNode root;

    private int calc(int val1, int val2)
    {
        return val1 < val2 ? val1 : val2;
    }

    public void insert(int x, int y, int value)
    {
        if  (root == null)
        {
            root = new QuadNode(x, y, value);
        }
        else
        {
            QuadNode focus = root;
            while (true)
            {
                if  (x == focus.x && y == focus.y)
                {
                    focus.value = calc(focus.value, value);
                    break;
                }
                if  (x >= focus.x && y >= focus.y)
                {
                    if (focus.child1 == null)
                    {
                        focus.child1 = new QuadNode(x, y, value);
                        break;
                    }
                    else
                    {
                        focus = focus.child1;
                        continue;
                    }
                }
                if  (x < focus.x && y >= focus.y)
                {
                    if (focus.child2 == null)
                    {
                        focus.child2 = new QuadNode(x, y, value);
                        break;
                    }
                    else
                    {
                        focus = focus.child2;
                        continue;
                    }
                }
                if  (x < focus.x && y < focus.y)
                {
                    if (focus.child3 == null)
                    {
                        focus.child3 = new QuadNode(x, y, value);
                        break;
                    }
                    else
                    {
                        focus = focus.child3;
                        continue;
                    }
                }
                if  (x >= focus.x && y < focus.y)
                {
                    if (focus.child4 == null)
                    {
                        focus.child4 = new QuadNode(x, y, value);
                        break;
                    }
                    else
                    {
                        focus = focus.child4;
                        continue;
                    }
                }
            }

        }
    }

    public List<QuadNode> toList()
    {
        ArrayList<QuadNode> nodes = new ArrayList<>();
        traverse(root, nodes);
        return nodes;
    }

    private void traverse(QuadNode node, List<QuadNode> nodes)
    {
        if  (node == null)
        {
            return;
        }
        nodes.add(node);
        traverse(node.child1, nodes);
        traverse(node.child2, nodes);
        traverse(node.child3, nodes);
        traverse(node.child4, nodes);
    }

    public void mergeQuadNodes(List<QuadNode> nodes)
    {
        for (QuadNode node : nodes) 
        {
            this.insert(node.x, node.y, node.value);
        }
    }
}
