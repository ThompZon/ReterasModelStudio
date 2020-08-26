package com.hiveworkshop.rms.editor.render3d;

import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vector3;

public interface InternalInstance {
    void setTransformation(Vector3 worldLocation, Quat rotation, Vector3 worldScale);
    void setSequence(int index);
    void show();
    void setPaused(boolean paused);
    void move(Vector3 deltaPosition);

    void hide();
}
