package wtf.atani.utils.render.shader.shaders;

import org.lwjgl.opengl.GL20;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GLSLShader {
    private final int program, time, resolution;

    public GLSLShader(String location) {
        int glProgram = GL20.glCreateProgram();

        GL20.glAttachShader(glProgram, createShader(GLSLShader.class.getResourceAsStream("/assets/minecraft/atani/shaders/passthrough.vsh"), GL20.GL_VERTEX_SHADER));
        GL20.glAttachShader(glProgram, createShader(GLSLShader.class.getResourceAsStream(location), GL20.GL_FRAGMENT_SHADER));
        GL20.glLinkProgram(glProgram);

        this.program = glProgram;

        GL20.glUseProgram(glProgram);

        this.time = GL20.glGetUniformLocation(glProgram, "time");
        this.resolution = GL20.glGetUniformLocation(glProgram, "resolution");

        GL20.glUseProgram(0);
    }

    public void drawShader(int width, int height, float time) {
        GL20.glUseProgram(this.program);
        GL20.glUniform2f(this.resolution, width, height);
        GL20.glUniform1f(this.time, time);
    }

    private int createShader(InputStream stream, int type) {
        int shader = GL20.glCreateShader(type);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[512];

        int read;

        while (true) {
            try {
                if (!((read = stream.read(buffer, 0, buffer.length)) != -1)) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            out.write(buffer, 0, read);
        }

        GL20.glShaderSource(shader, new String(out.toByteArray(), StandardCharsets.UTF_8));
        GL20.glCompileShader(shader);

        return shader;
    }

}
