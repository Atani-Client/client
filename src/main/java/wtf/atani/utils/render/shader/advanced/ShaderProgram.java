package wtf.atani.utils.render.shader.advanced;

import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.logging.LogUtil;
import wtf.atani.utils.render.shader.advanced.enums.ShaderType;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class ShaderProgram implements Methods {

    private final String vertexName, fragmentName;

    private final int programID;

    public int getProgramID() {
        return programID;
    }

    private final ShaderType shaderType;

    public ShaderProgram(String vertexName, String fragmentName, ShaderType shaderType) {
        this.vertexName = vertexName;
        this.fragmentName = fragmentName;
        this.shaderType = shaderType;

        String ending = "";

        switch (shaderType) {
            case VERTEX:
                ending += ".vsh";
                break;
            case GLSL:
                ending += ".glsl";
                break;
        }
        final int program = glCreateProgram();

        final String vertexSource = ShaderReader.readShader(vertexName);

        final int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexSource);
        glCompileShader(vertexShaderID);

        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            LogUtil.getInstance().sendMessage(glGetShaderInfoLog(vertexShaderID, 4096), "console");
            System.out.printf("Vertex Shader (%s) failed to compile!%n", GL_VERTEX_SHADER);
            this.programID = 0;
            return;
        }

        final String fragmentSource = ShaderReader.readShader(fragmentName);
        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragmentSource);
        glCompileShader(fragmentShaderID);

        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            LogUtil.getInstance().sendMessage(glGetShaderInfoLog(fragmentShaderID, 4096), "console");
            System.out.printf("Fragment Shader failed to compile!: " + fragmentName, GL_FRAGMENT_SHADER);
            this.programID = 0;
            return;
        }

        glAttachShader(program, vertexShaderID);
        glAttachShader(program, fragmentShaderID);
        glLinkProgram(program);
        this.programID = program;
    }

    public ShaderProgram(String vertexName, String fragment) {
        this.vertexName = vertexName;
        this.fragmentName = "background";
        this.shaderType = ShaderType.GLSL;

        String ending = ".glsl";
        final int program = glCreateProgram();

        final String vertexSource = ShaderReader.readShader(vertexName);

        final int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderID, vertexSource);
        glCompileShader(vertexShaderID);

        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            LogUtil.getInstance().sendMessage(glGetShaderInfoLog(vertexShaderID, 4096), "console");
            System.out.printf("Vertex Shader (%s) failed to compile!%n", GL_VERTEX_SHADER);
            this.programID = 0;
            return;
        }

        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderID, fragment);
        glCompileShader(fragmentShaderID);

        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            LogUtil.getInstance().sendMessage(glGetShaderInfoLog(fragmentShaderID, 4096), "console");
            System.out.printf("Fragment Shader failed to compile!: background shader", GL_FRAGMENT_SHADER);
            this.programID = 0;
            return;
        }

        glAttachShader(program, vertexShaderID);
        glAttachShader(program, fragmentShaderID);
        glLinkProgram(program);
        this.programID = program;
    }

    public void initShader() {
        glUseProgram(this.programID);
    }

    public void deleteShader() {
        glUseProgram(0);
    }

    public int getUniform(String name) {
        return glGetUniformLocation(this.programID, name);
    }

    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) {
            if (args.length > 2) {
                if (args.length > 3) glUniform4f(loc, args[0], args[1], args[2], args[3]);
                else glUniform3f(loc, args[0], args[1], args[2]);
            } else glUniform2f(loc, args[0], args[1]);
        } else glUniform1f(loc, args[0]);
    }


    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    @Override
    public String toString() {
        return "ShaderProgram{" + "programID=" + programID + ", vertexName='" + vertexName + '\'' + ", fragmentName='" + fragmentName + '\'' + '}';
    }

}