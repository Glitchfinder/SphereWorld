/*
 * Copyright (c) 2011-2012 Thomas Bucher <ToasterKTN>,
 * Zhuowei Zhang <zhuowei>, and Sean Porter <glitchkey@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sphereworld;

//* IMPORTS: JDK/JRE
	import java.io.Serializable;
//* IMPORTS: BUKKIT
	import org.bukkit.util.Vector;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class Sphere implements Serializable
{
	private static final long serialVersionUID = 1570149742281426141L;
	private String world;
	private double x;
	private double y;
	private double z;
	private int size;
	private transient Vector vector;
	private boolean oreSphere;
	private int oreId;
	private int oreShellId;
	private boolean protectedStructure;

	public Sphere()
	{
		this.vector = new Vector(x, y, z);
	}

	public Vector getVector()
	{
		this.vector = new Vector(x, y, z);
		return vector;
	}

	public int getOreId()
	{
		return oreId;
	}

	public int getOreShellId()
	{
		return oreShellId;
	}

	public int getSize()
	{
		return size;
	}

	public String getWorld()
	{
		return world;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getZ()
	{
		return z;
	}

	public boolean isOreSphere()
	{
		return oreSphere;
	}

	public boolean isProtectedStructure()
	{
		return protectedStructure;
	}

	public void setOreSphere(boolean value)
	{
		this.oreSphere = value;
	}

	public void setOreId(int id)
	{
		this.oreId = id;
	}

	public void setOreShellId(int id)
	{
		this.oreShellId = id;
	}

	public void setProtectedStructure(boolean value)
	{
		this.protectedStructure = value;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public void setVector(Vector vector)
	{
		this.vector = vector;
	}

	public void setWorld(String world)
	{
		this.world = world;
	}

	public void setX(double x)
	{
		this.x = x;
		this.vector = new Vector(x, y, z);
	}

	public void setY(double y)
	{
		this.y = y;
		this.vector = new Vector(x, y, z);
	}

	public void setZ(double z)
	{
		this.z = z;
		this.vector = new Vector(x, y, z);
	}
}