package org.heigit.ors.shortestpathtree;

public class QuadNode
{
    public int x;
    public int y;
    public int value;
    public QuadNode child1;
    public QuadNode child2;
    public QuadNode child3;
    public QuadNode child4;

    public QuadNode(int x, int y, int value)
    {
        this.x = x;
        this.y = y;
        this.value = value;
    }
}
