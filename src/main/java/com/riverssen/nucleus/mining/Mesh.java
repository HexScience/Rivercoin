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

package com.riverssen.nucleus.mining;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class Mesh
{
	private static HashMap<String, MeshResource> s_loadedModels = new HashMap<String, MeshResource>();
	private MeshResource m_resource;
	private String       m_fileName;
	
	public Mesh(float vertices[], int[] indices)
	{
		m_fileName = "";
		AddVertices(vertices, indices);
	}

	@Override
	protected void finalize()
	{
		if(m_resource.RemoveReference() && !m_fileName.isEmpty())
			s_loadedModels.remove(m_fileName);
	}
	
	private void AddVertices(float vertices[], int indices[])
	{
		m_resource = new MeshResource(indices.length);

		FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
		buffer.put(vertices);
		buffer.flip();

		IntBuffer intBuffer = MemoryUtil.memAllocInt(indices.length);
		intBuffer.put(indices);
		intBuffer.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, m_resource.GetVbo());
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_resource.GetIbo());
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL_STATIC_DRAW);
	}
	
	public void render()
	{
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		glBindBuffer(GL_ARRAY_BUFFER, m_resource.GetVbo());
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 32, 0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 32, 12);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 32, 24);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_resource.GetIbo());
		glDrawElements(GL_TRIANGLES, m_resource.GetSize(), GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
	}
}