package org.saintandreas.input.oculus;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.saintandreas.input.oculus.RiftTracker;
import org.saintandreas.input.oculus.hid.DisplayInfo;
import org.saintandreas.input.oculus.hid.SensorConfig;
import org.saintandreas.input.oculus.hid.SensorRange;
import org.saintandreas.input.oculus.tracker.TrackerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeminders.hidapi.HIDDevice;
import com.google.common.base.Predicate;

public class RiftTest {
    private static final Logger LOG = LoggerFactory.getLogger(RiftTest.class);

    public static void main(String... args) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        RiftTracker tracker = RiftTracker.getInstance();
        DisplayInfo di = tracker.getDisplayInfo();
        LOG.info(writer.writeValueAsString(di));
        SensorConfig sc = tracker.getSensorConfig();
        LOG.info(writer.writeValueAsString(sc));
        SensorRange sr = tracker.getSensorRange();
        LOG.info(writer.writeValueAsString(sr));
        RiftTracker.startListening(new Predicate<TrackerMessage>() {
            @Override
            public boolean apply(TrackerMessage message) {
                try {
                    // Just enough data to see we're getting stuff
                    LOG.info("Sample count: " + message.sampleCount);
                    LOG.info("Sample 0: " + mapper.writeValueAsString(message.samples.get(0)));
                } catch (Exception e) {
                    LOG.error("Failed to serialize sample data", e);
                }
                return true;
            }
        });
    }

    /**
     * This is a tool I used to fix my display info report after I accidentally
     * trashed it by running example code here:
     * http://lxr.free-electrons.com/source/samples/hidraw/hid-example.c -- note
     * the sample code writes to feature report 9 without actually doing any
     * verification that that's a good idea.
     * 
     * No guarantee that this will not destroy your device. No warranty is
     * implied or provided.
     * 
     * @param device
     * @throws IOException
     */
    public static void fixDisplayInfo(HIDDevice device) throws IOException {
        DisplayInfo di = new DisplayInfo();
        // These values are from an earlier dump of my display Info. I have no
        // idea if they're valid.
        di.distortion = 1;
        di.xres = 1280;
        di.yres = 800;
        di.xsize = 149760;
        di.ysize = 93600;
        di.center = 46800;
        di.sep = 63500;
        di.zeye = new int[] { 49800, 49800 };
        di.distortionCoefficients = new float[] {
            1.0f,
            0.22f,
            0.24f,
            3.4119901E16f,
            4.6665167E-38f,
            -9.6494493E15f
        };
        di.write(device);
    }
}
