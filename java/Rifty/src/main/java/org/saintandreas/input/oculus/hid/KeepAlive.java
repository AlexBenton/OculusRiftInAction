package org.saintandreas.input.oculus.hid;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.saintandreas.input.hid.HidFeatureReport;

import com.codeminders.hidapi.HIDDevice;

public class KeepAlive extends HidFeatureReport {
    public static final byte FEATURE_ID = 8;
    public static final int FEATURE_SIZE = 5;

    public short commandId = 0;
    public short intervalMilliSeconds;

    public KeepAlive() {
        super(FEATURE_ID, FEATURE_SIZE);
    }

    public KeepAlive(HIDDevice device) throws IOException {
        super(FEATURE_ID, FEATURE_SIZE, device);
    }

    public KeepAlive(short keepAliveMs) {
        super(FEATURE_ID, FEATURE_SIZE);
        this.intervalMilliSeconds = keepAliveMs;
    }

    protected void parse(ByteBuffer buffer) {
        commandId = buffer.getShort();
        intervalMilliSeconds = buffer.getShort();
    }

    protected void pack(ByteBuffer buffer) {
        buffer.putShort(commandId);
        buffer.putShort(intervalMilliSeconds);
    }
}
