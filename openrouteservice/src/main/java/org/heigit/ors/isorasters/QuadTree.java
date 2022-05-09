package org.heigit.ors.isorasters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
        traverse(root, (QuadNode node) -> { nodes.add(node); return 0;});
        return nodes;
    }

    private void traverse(QuadNode node, Function<QuadNode,Integer> func)
    {
        if  (node == null)
        {
            return;
        }
        func.apply(node);
        traverse(node.child1, func);
        traverse(node.child2, func);
        traverse(node.child3, func);
        traverse(node.child4, func);
    }

    public void mergeQuadNodes(List<QuadNode> nodes)
    {
        for (QuadNode node : nodes) 
        {
            this.insert(node.x, node.y, node.value);
        }
    }

    /**
     * calculates Bounding-Box of Quadtree entries
     * @return int[] containing four items: [minX, maxX, minY, maxY]
     */
    public int[] getBoundingBox()
    {
        int[] bb = new int[4];
        bb[0] = root.x;
        bb[1] = root.x;
        bb[2] = root.y;
        bb[3] = root.y;
        traverse(root, (QuadNode node) -> { 
            if (node.x < bb[0])
                bb[0] = node.x;
            if (node.x > bb[1])
                bb[1] = node.x;
            if (node.y < bb[2])
                bb[2] = node.y;
            if (node.y > bb[3])
                bb[3] = node.y;
            return 0;
        });
        return bb;
    }
}
