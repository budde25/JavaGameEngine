package dev.budde.engine.graph;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private final int vaoId;
    private final List<Integer> vboIds;
    private final Texture texture;
    private final int vertexCount;

    public Mesh(float[] positions, int[] indices, float[] textCoords, Texture texture) {
        try {
            // indices will help use from using too much duplicate data
            vertexCount = indices.length;
            vboIds = new ArrayList<>();
            this.texture = texture;

            // VaoId for the Mesh
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            initPosition(positions);
            initIndices(indices);
            initTexture(textCoords);

            // Needs to happen last
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {

        }
    }

    private void initTexture(float[] textCoords) {
        FloatBuffer textCoordsBuffer = null;
        try {
            int vboId = glGenBuffers();
            vboIds.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        } finally {
            if (textCoordsBuffer != null)
                MemoryUtil.memFree(textCoordsBuffer);
        }
    }

    private void initPosition(float[] positions) {
        FloatBuffer posBuffer = null;
        try {
            int vboId = glGenBuffers();
            vboIds.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        } finally {
            if (posBuffer != null)
                MemoryUtil.memFree(posBuffer);
        }
    }

    private void initIndices(int[] indices) {
        IntBuffer indicesBuffer = null;
        try {
            int vboId = glGenBuffers();
            vboId = glGenBuffers();
            vboIds.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        } finally {
            if (indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);
        }
    }

    public void render() {
        // Activate firs texture bank
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, texture.getId());

        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

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
        for (int vboId : vboIds) {
            glDeleteBuffers(vboId);
        }

        // Delete the texture
        texture.cleanup();

        //Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
