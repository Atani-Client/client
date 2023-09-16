package org.lwjgl.openal;

public class AL10 {
	
	public static final int AL_NO_ERROR = AL10.AL_NO_ERROR;
	public static final int AL_BUFFER = AL10.AL_BUFFER;
	public static final int AL_SOURCE_STATE = AL10.AL_SOURCE_STATE;
	public static final int AL_PLAYING = AL10.AL_PLAYING;
	public static final int AL_LOOPING = AL10.AL_LOOPING;
	public static final int AL_TRUE = AL10.AL_TRUE;
	public static final int AL_REFERENCE_DISTANCE = AL10.AL_REFERENCE_DISTANCE;
	public static final int AL_ROLLOFF_FACTOR = AL10.AL_ROLLOFF_FACTOR;
	public static final int AL_POSITION = AL10.AL_POSITION;
	
	public static void alGenBuffers(java.nio.IntBuffer buffers) {
		AL10.alGenBuffers(buffers);
	}
	
	public static void alGenSources(java.nio.IntBuffer sources) {
		AL10.alGenSources(sources);
	}
	
	public static void alBufferData(int buffer, int format, java.nio.ByteBuffer data, int freq) {
		AL10.alBufferData(buffer, format, data, freq);
	}
	
	public static void alSourcei(int source, int pname, int value) {
		AL10.alSourcei(source, pname, value);
	}
	
	public static int alGetSourcei(int source, int pname) {
		return AL10.alGetSourcei(source, pname);
	}
	
	public static String alGetString(int pname) {
		return AL10.alGetString(pname);
	}
	
	public static int alGetError() {
		return AL10.alGetError();
	}
	
	public static void alSourcef(int source, int pname, float value) {
		AL10.alSourcef(source, pname, value);
	}
	
	public static void alSourcePlay(int source) {
		AL10.alSourcePlay(source);
	}
	
	public static void alSourceStop(java.nio.IntBuffer sources) {
		AL10.alSourceStopv(sources);
	}
	
	public static void alDeleteSources(java.nio.IntBuffer sources) {
		AL10.alDeleteSources(sources);
	}
	
	public static void alDeleteBuffers(java.nio.IntBuffer buffers) {
		AL10.alDeleteBuffers(buffers);
	}
	
	public static void alListener3f(int pname, float v1, float v2, float v3) {
		AL10.alListener3f(pname, v1, v2, v3);
	}
	
	public static void alSource3f(int source, int pname, float v1, float v2, float v3) {
		AL10.alSource3f(source, pname, v1, v2, v3);
	}
	
	public static void alSourceStop(int source) {
		AL10.alSourceStop(source);
	}

}
