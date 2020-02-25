package ure.ui;

import ure.math.UColor;

import java.util.HashMap;


/**
 * ULightcell represents a single xy grid cell of a Camera.
 * Tracks all light hitting that cell of the camera.
 *
 * Normal users shouldn't need to mess with this except for unusual camera effects.
 *
 */

public class ULightcell {
    public int x,y;
    public float visibility;
    float sunBrightness;
    float sunBuffer;
    float cloudPattern;

    private UColor lightBuffer;
    private UColor fogBuffer;
    int lightBufferTime, fogBufferTime;

    public HashMap<ULight,Float> sources;
    private UCamera camera;

    public ULightcell(UCamera thecam, int x, int y, float cloud) {
        this.x = x;
        this.y = y;
        camera = thecam;
        visibility = 0f;
        sources = new HashMap<ULight,Float>();
        lightBuffer = new UColor(0f,0f,0f);
        fogBuffer = new UColor(0f,0f,0f);
        cloudPattern = cloud;
    }

    public void wipe() {
        visibility = 0f;
        sources.clear();
    }

    public void receiveLight(ULight source, float intensity) {
        sources.put(source, intensity);
    }

    public void setVisibility(float thevis) {
        visibility = thevis;
    }

    public void setSunBrightness(float thebri) {
        sunBrightness = thebri;
    }

    public void setRenderedSun(float thebri) { sunBuffer = thebri; }

    public float getSunBrightness() { return sunBrightness - cloud(); }

    public float getRenderedSun() { return sunBuffer; }

    public float cloud() {
        int cx = x + camera.commander.frameCounter / 40;
        int cy = y;
        return camera.cloudPatternAt(cx,cy) * camera.area.getClouds();
    }
    public float cloudPattern() {
        return cloudPattern;
    }

    public UColor light() { return light(0); }
    public UColor light(int time) {
        if (time <= lightBufferTime)
            return lightBuffer;
        lightBufferTime = time;
        lightBuffer.set(0f,0f,0f);
        lightBuffer.addLights(camera.area.getSunColor(), getRenderedSun());
        for (ULight source : sources.keySet()) {
            float intensity = sources.get(source) * source.intensityAtTime(time);
            lightBuffer.addLights(source.getColor(), intensity);
        }
        return lightBuffer;
    }
    public UColor fog(int time) {
        if (time <= fogBufferTime) {
            return fogBuffer;
        }
        fogBufferTime = time;
        fogBuffer.set(camera.area.getFogColor());
        fogBuffer.illuminateWith(light(time), 1f);
        return fogBuffer;
    }

}
