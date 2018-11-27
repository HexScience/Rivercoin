/*
 * Copyright (C) 2014 Benny Bobaganoosh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nucleus.math;

import com.maxmind.geoip2.record.Location;

public class Vector2d
{
	private double m_x;
	private double m_y;
	
	public Vector2d(double x, double y)
	{
		this.m_x = x;
		this.m_y = y;
	}

	public Vector2d(Location location)
	{
		this(location.getLongitude(), location.getLatitude());
	}

    public double Length()
	{
		return (double)Math.sqrt(m_x * m_x + m_y * m_y);
	}

	public double Max()
	{
		return Math.max(m_x, m_y);
	}

	public double Dot(Vector2d r)
	{
		return m_x * r.GetX() + m_y * r.GetY();
	}
	
	public Vector2d Normalized()
	{
		double length = Length();
		
		return new Vector2d(m_x / length, m_y / length);
	}

	public double Cross(Vector2d r)
	{
		return m_x * r.GetY() - m_y * r.GetX();
	}

	public Vector2d Lerp(Vector2d dest, double lerpFactor)
	{
		return dest.Sub(this).Mul(lerpFactor).Add(this);
	}

	public Vector2d Rotate(double angle)
	{
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		
		return new Vector2d((double)(m_x * cos - m_y * sin),(double)(m_x * sin + m_y * cos));
	}
	
	public Vector2d Add(Vector2d r)
	{
		return new Vector2d(m_x + r.GetX(), m_y + r.GetY());
	}
	
	public Vector2d Add(double r)
	{
		return new Vector2d(m_x + r, m_y + r);
	}
	
	public Vector2d Sub(Vector2d r)
	{
		return new Vector2d(m_x - r.GetX(), m_y - r.GetY());
	}
	
	public Vector2d Sub(double r)
	{
		return new Vector2d(m_x - r, m_y - r);
	}
	
	public Vector2d Mul(Vector2d r)
	{
		return new Vector2d(m_x * r.GetX(), m_y * r.GetY());
	}
	
	public Vector2d Mul(double r)
	{
		return new Vector2d(m_x * r, m_y * r);
	}
	
	public Vector2d Div(Vector2d r)
	{
		return new Vector2d(m_x / r.GetX(), m_y / r.GetY());
	}
	
	public Vector2d Div(double r)
	{
		return new Vector2d(m_x / r, m_y / r);
	}
	
	public Vector2d Abs()
	{
		return new Vector2d(Math.abs(m_x), Math.abs(m_y));
	}
	
	public String toString()
	{
		return "(" + m_x + " " + m_y + ")";
	}

	public Vector2d Set(double x, double y) { this.m_x = x; this.m_y = y; return this; }
	public Vector2d Set(Vector2d r) { Set(r.GetX(), r.GetY()); return this; }

	public double GetX()
	{
		return m_x;
	}

	public void SetX(double x)
	{
		this.m_x = x;
	}

	public double GetY()
	{
		return m_y;
	}

	public void SetY(double y)
	{
		this.m_y = y;
	}

	public boolean equals(Vector2d r)
	{
		return m_x == r.GetX() && m_y == r.GetY();
	}
}