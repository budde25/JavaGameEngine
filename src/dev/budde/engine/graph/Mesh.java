package dev.budde.engine.graph;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private final int vaoId;
    private final int posVboId;
    private final int idxVboId;
    private final int colorVboId;
    private final int vertexCount;

    public Mesh(float[] positions, int[] indices, float[] colors) {

        // indices will help use from using too much duplicate data
        vertexCount = indices.length;

        FloatBuffer verticesBuffer = null;
        FloatBuffer colorBuffer = null;
        IntBuffer indicesBuffer = null;

        try {
            // Vertex and indices VBO
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);

            verticesBuffer.put(positions).flip();
            indicesBuffer.put(indices).flip();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            idxVboId = glGenBuffers();
            posVboId = glGenBuffers();

            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);

            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glVertexAttribPointer(0,3,GL_FLOAT, false,0,0);
            glBindBuffer(GL_ARRAY_BUFFER,0);

            // Color VBO
            colorVboId = glGenBuffers();
            colorBuffer = MemoryUtil.memAllocFloat(colors.length);
            colorBuffer.put(colors).flip();
            glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
            glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);

            // index is 1 because pos data is at 0
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

            // Needs to happen last
            glBindVertexArray(0);

        } finally {
            if (verticesBuffer != null)
                MemoryUtil.memFree(verticesBuffer);

            if (indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);

            if (colorBuffer != null)
                MemoryUtil.memFree(colorBuffer);
        }
    }

    public void render() {
        // Bind to the VAO
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the vertices
        //glDrawArrays(GL_TRIANGLES, 0, mesh.getVertexCount());
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        //Delete VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(posVboId);
        glDeleteBuffers(idxVboId);
        glDeleteBuffers(colorVboId);

        //Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
