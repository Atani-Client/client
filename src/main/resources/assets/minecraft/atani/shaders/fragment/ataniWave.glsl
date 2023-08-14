#ifdef GL_ES
precision highp float;
#endif

uniform vec2 resolution;
uniform vec2 mouse;
uniform float time;

float iTime = time;
vec2 iResolution = resolution;
vec2 iMouse = mouse;


#define A .1 // Amplitude
#define V 3. // Velocity
#define W 3. // Wavelength
#define T 1.3 // Thickness
#define S 6. // Sharpness

float sine(vec2 p, float o)
{
    // Offset the sine function by subtracting 1.0 from p.y to keep the line at the bottom
    return pow(T / abs((p.y + sin((p.x * W + o)) * A)), S);
}

// Gaussian function to create a glow effect
float gaussian(float x, float sigma)
{
    return exp(-0.5 * (x * x) / (sigma * sigma));
}

void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    vec2 p = fragCoord.xy / iResolution.xy * 5. - 1.;

    // Calculate the sine value for the current pixel
    float sineValue = sine(p, iTime * V);

    // Set the RGB channels to color the sine line (red: 10, green: 60, blue: 112)
    vec3 lineColor = vec3(10.0 / 255.0, 60.0 / 255.0, 112.0 / 255.0);

    // Glow effect using Gaussian function
    float glow = gaussian(sineValue, 0.3); // Adjust the second parameter for glow intensity

    // Blend the background color (black) with the line color using the glow effect
    vec3 finalColor = mix(lineColor, vec3(0.0), glow);

    // Set the alpha value to 1 for the sine line, and 0 for the background
    fragColor = vec4(finalColor, sineValue);
}

// original shadertoy code ends here
void main(){
    mainImage(gl_FragColor, gl_FragCoord.xy);
}